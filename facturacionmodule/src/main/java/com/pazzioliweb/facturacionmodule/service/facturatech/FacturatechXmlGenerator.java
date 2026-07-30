package com.pazzioliweb.facturacionmodule.service.facturatech;

import com.pazzioliweb.facturacionmodule.config.FacturatechConfig;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generador del insumo XML_SIMPLIFICADO de FACTURATECH (Anexo técnico 1.9).
 *
 * Estructura factura de venta:  FACTURA → ENC, EMI(TAC,DFE,ICC,CDE,GTE),
 *   ADQ(TCR,ILA,CDA,GTA), TOT, [TIM(IMP)], DRF, MEP, ITE(IAE,[IDE],[TII(IIM)])*
 *
 * Estructura notas NC/ND:       NOTA → ENC, EMI(...), ADQ(...), TOT, [TIM(IMP)],
 *   DRF, REF, MEP, CDN, ITE(...)*
 *
 * Estructura documento soporte: DOCUMENTO_SOPORTE → ENC, PRO(TAC,GTE), ADQ(TCR,GTA),
 *   TOT, [TIM(IMP)], DRF, [CDN(DCN)], [REF], MEP, ITE([MYM],IAE,IBS,[IDE],[TII(IIM)])*
 *   (el orden es el del "Estándar simplificado_DS FTECH.xlsx")
 *
 * Reglas clave del material de certificación:
 *  - ITE_5 = ITE_19 = (ITE_27 × ITE_7) − descuentos IDE + cargos IDE
 *  - ITE_21 = ITE_19 + Σ impuestos del ítem (IIM_2)
 *  - TOT_1 = Σ ITE_5 · TOT_3 = Σ IIM_4 (primer impuesto por ítem)
 *  - TOT_7 = TOT_1 + Σ impuestos · TOT_5 = TOT_7 − DSC descuentos + DSC cargos
 *  - Impuestos con misma tarifa se agrupan en un IMP dentro del TIM global
 *  - En NC/ND: si ENC_21=20 entonces CDN_1=2 (anulación) y viceversa
 */
@Component
public class FacturatechXmlGenerator {

    private static final Logger log = LoggerFactory.getLogger(FacturatechXmlGenerator.class);
    private static final String COP = "COP";
    /** ENC_8 del documento soporte debe informarse en la zona horaria de Colombia (-05:00). */
    private static final ZoneId ZONA_CO = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter HORA_DS = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final FacturatechConfig config;

    public FacturatechXmlGenerator(FacturatechConfig config) {
        this.config = config;
    }

    /**
     * Genera el XML_SIMPLIFICADO según el tipo de documento del request:
     * "01" factura de venta, "91" nota crédito, "92" nota débito,
     * "05" documento soporte, "95" nota de ajuste al documento soporte.
     */
    public String generarXml(DianDocumentoRequestDTO req) {
        String tipo = req.getTipoDocumento() != null ? req.getTipoDocumento() : "01";
        return switch (tipo) {
            case "05" -> generarDocumentoSoporte(req, false);
            case "95" -> generarDocumentoSoporte(req, true);
            case "91" -> generarNota(req, true);
            case "92" -> generarNota(req, false);
            default -> generarFactura(req);
        };
    }

    // ══════════════════════════════════════════════════════════
    //  FACTURA de venta (INVOIC)
    // ══════════════════════════════════════════════════════════

    private String generarFactura(DianDocumentoRequestDTO req) {
        Totales tot = calcularTotales(req.getLineas());
        StringBuilder xml = new StringBuilder(8192);
        xml.append("<FACTURA>\n");
        appendEnc(xml, req, "INVOIC", "01", "10", tot);
        appendEmi(xml, req);
        appendAdq(xml, req);
        appendTot(xml, tot);
        appendTim(xml, tot);
        appendDrf(xml, req);
        appendMep(xml, req);
        appendItems(xml, req.getLineas());
        xml.append("</FACTURA>");
        return xml.toString();
    }

    // ══════════════════════════════════════════════════════════
    //  NOTA crédito / débito
    // ══════════════════════════════════════════════════════════

    private String generarNota(DianDocumentoRequestDTO req, boolean esNotaCredito) {
        Totales tot = calcularTotales(req.getLineas());
        int concepto = req.getCodigoConcepto() != null ? req.getCodigoConcepto() : (esNotaCredito ? 2 : 4);

        // Tipo de operación (Tabla 38): NC → 20 si anula factura (CDN_1=2), 22 en otro caso.
        // ND → 30 (referencia factura electrónica).
        String tipoOperacion;
        if (esNotaCredito) {
            tipoOperacion = concepto == 2 ? "20" : "22";
        } else {
            tipoOperacion = "30";
        }

        StringBuilder xml = new StringBuilder(8192);
        xml.append("<NOTA>\n");
        appendEnc(xml, req, esNotaCredito ? "NC" : "ND", esNotaCredito ? "91" : "92", tipoOperacion, tot);
        appendEmi(xml, req);
        appendAdq(xml, req);
        appendTot(xml, tot);
        appendTim(xml, tot);
        appendDrf(xml, req);
        appendRef(xml, req);
        appendMep(xml, req);
        appendCdn(xml, concepto, req.getRazonConcepto(), esNotaCredito);
        appendItems(xml, req.getLineas());
        xml.append("</NOTA>");
        return xml.toString();
    }

    // ══════════════════════════════════════════════════════════
    //  DOCUMENTO SOPORTE (05) y NOTA DE AJUSTE al DS (95)
    //
    //  Ojo: las partes van INVERTIDAS respecto a una factura de venta.
    //   · PRO = proveedor no obligado a facturar  → req.receptor
    //   · ADQ = nuestra empresa (quien lo expide) → req.emisor
    //  El nodo IBS (fecha de compra + forma de generación) es exclusivo del DS
    //  y NO se envía en la nota de ajuste; las retenciones tampoco aplican en la nota.
    // ══════════════════════════════════════════════════════════

    private String generarDocumentoSoporte(DianDocumentoRequestDTO req, boolean esNotaAjuste) {
        Totales tot = calcularTotales(req.getLineas());
        StringBuilder xml = new StringBuilder(8192);
        xml.append("<DOCUMENTO_SOPORTE>\n");
        appendEncDs(xml, req, esNotaAjuste, tot);
        appendPro(xml, req);
        appendAdqDs(xml, req);
        appendTot(xml, tot);
        appendTim(xml, tot);
        if (!esNotaAjuste) {
            // Retenciones: sólo en el DS (en la nota de ajuste no aplican).
            appendTimRetencion(xml, "06", req.getRetencionFuente(), tot.brutoAntesTributos);
            appendTimRetencion(xml, "05", req.getRetencionIva(), tot.totalIva);
        }
        appendDrf(xml, req);
        if (esNotaAjuste) {
            appendCdnDs(xml, req);
            appendRefDs(xml, req);
        }
        appendMep(xml, req);
        appendItemsDs(xml, req, esNotaAjuste);
        xml.append("</DOCUMENTO_SOPORTE>");
        return xml.toString();
    }

    /** ENC del documento soporte. ENC_2 es el NIT del adquiriente (nuestra empresa). */
    private void appendEncDs(StringBuilder xml, DianDocumentoRequestDTO req,
                             boolean esNotaAjuste, Totales tot) {
        DianDocumentoRequestDTO.EmisorDTO adq = req.getEmisor() != null
                ? req.getEmisor() : new DianDocumentoRequestDTO.EmisorDTO();
        int numItems = req.getLineas() != null ? req.getLineas().size() : 0;
        LocalDate fecha = req.getFechaEmision() != null ? req.getFechaEmision() : LocalDate.now(ZONA_CO);

        xml.append("  <ENC>\n");
        tag(xml, "ENC_1", esNotaAjuste ? "NC" : "DS");
        tag(xml, "ENC_2", nvl(adq.getNumeroIdentificacion()));
        tag(xml, "ENC_4", "UBL 2.1");
        tag(xml, "ENC_5", "DIAN 2.1");
        tag(xml, "ENC_6", nvl(req.getPrefijo()) + (req.getConsecutivo() != null ? req.getConsecutivo() : ""));
        tag(xml, "ENC_7", fecha.toString());
        // ENC_8 debe ir en la zona horaria de Colombia (-05:00), no en la del servidor.
        tag(xml, "ENC_8", LocalTime.now(ZONA_CO).format(HORA_DS) + "-05:00");
        tag(xml, "ENC_9", esNotaAjuste ? "95" : "05");
        tag(xml, "ENC_10", COP);
        tag(xml, "ENC_15", String.valueOf(numItems));
        if (req.getFechaVencimiento() != null) {
            tag(xml, "ENC_16", req.getFechaVencimiento().toString());
        }
        tag(xml, "ENC_20", String.valueOf(config.getAmbiente()));
        tag(xml, "ENC_21", tipoOperacionDs(req));
        xml.append("  </ENC>\n");
    }

    /** PRO — proveedor NO obligado a facturar (viene en req.receptor). */
    private void appendPro(StringBuilder xml, DianDocumentoRequestDTO req) {
        DianDocumentoRequestDTO.ReceptorDTO p = req.getReceptor() != null
                ? req.getReceptor() : new DianDocumentoRequestDTO.ReceptorDTO();

        String tipoDoc = notBlank(p.getTipoIdentificacion()) ? p.getTipoIdentificacion() : "13";
        boolean residente = "10".equals(tipoOperacionDs(req));
        String codPais = notBlank(p.getCodigoPais()) ? p.getCodigoPais() : "CO";
        String nombrePais = notBlank(p.getNombrePais()) ? p.getNombrePais() : "COLOMBIA";

        if (residente && (!notBlank(p.getDireccion()) || !notBlank(p.getMunicipio())
                || !notBlank(p.getCodigoMunicipio()) || !notBlank(p.getCodigoDepartamento()))) {
            log.warn("[Facturatech][DS] Proveedor {} sin dirección/municipio/departamento completos: " +
                    "los nodos PRO_10/11/13/19/23 son obligatorios y Facturatech rechazará el documento",
                    p.getNumeroIdentificacion());
        }
        if (residente && !notBlank(p.getCodigoPostal())) {
            log.warn("[Facturatech][DS] Proveedor {} sin código postal: se envía 000000 en PRO_14 " +
                    "(la DIAN puede generar notificación; registre el código postal del tercero)",
                    p.getNumeroIdentificacion());
        }

        xml.append("  <PRO>\n");
        tag(xml, "PRO_1", tipoPersona(p.getTipoContribuyente(), tipoDoc));
        tag(xml, "PRO_2", nvl(p.getNumeroIdentificacion()));
        tag(xml, "PRO_3", tipoDoc);
        tag(xml, "PRO_6", nvl(p.getNombre()));
        tag(xml, "PRO_10", nvl(p.getDireccion()));
        tag(xml, "PRO_11", nvl(p.getCodigoDepartamento()));
        tag(xml, "PRO_13", nvl(p.getMunicipio()));
        tag(xml, "PRO_14", notBlank(p.getCodigoPostal()) ? p.getCodigoPostal() : "000000");
        tag(xml, "PRO_15", codPais);
        tag(xml, "PRO_19", nvl(p.getDepartamento()));
        tag(xml, "PRO_21", nombrePais);
        // PRO_22 (DV) es mandatorio únicamente cuando PRO_3 = 31 (NIT).
        if ("31".equals(tipoDoc)) {
            tag(xml, "PRO_22", notBlank(p.getDigitoVerificacion())
                    ? p.getDigitoVerificacion() : digitoVerificacion(p.getNumeroIdentificacion()));
        }
        tag(xml, "PRO_23", nvl(p.getCodigoMunicipio()));

        // TAC: responsabilidades. Un no obligado a facturar es típicamente R-99-PN.
        xml.append("    <TAC>\n");
        tag2(xml, "TAC_1", obligacionFiscal(p.getResponsabilidadFiscal(), null, null));
        xml.append("    </TAC>\n");

        // GTE: tributo del proveedor. La Tabla 11 del anexo DS sólo define 01 (IVA),
        // 05 (ReteIVA) y 06 (ReteRenta); el ejemplo de certificación usa 01/IVA
        // incluso para proveedores R-99-PN.
        xml.append("    <GTE>\n");
        tag2(xml, "GTE_1", "01");
        tag2(xml, "GTE_2", "IVA");
        xml.append("    </GTE>\n");

        xml.append("  </PRO>\n");
    }

    /** ADQ del documento soporte — nuestra empresa (viene en req.emisor). */
    private void appendAdqDs(StringBuilder xml, DianDocumentoRequestDTO req) {
        DianDocumentoRequestDTO.EmisorDTO a = req.getEmisor() != null
                ? req.getEmisor() : new DianDocumentoRequestDTO.EmisorDTO();

        xml.append("  <ADQ>\n");
        tag(xml, "ADQ_1", tipoPersona(a.getTipoContribuyente(), "31"));
        tag(xml, "ADQ_2", nvl(a.getNumeroIdentificacion()));
        tag(xml, "ADQ_3", "31"); // El adquiriente siempre se identifica con NIT
        tag(xml, "ADQ_6", nvl(a.getRazonSocial()));
        tag(xml, "ADQ_22", notBlank(a.getDigitoVerificacion())
                ? a.getDigitoVerificacion() : digitoVerificacion(a.getNumeroIdentificacion()));

        xml.append("    <TCR>\n");
        tag2(xml, "TCR_1", obligacionFiscal(a.getResponsabilidadFiscal(),
                a.getGranContribuyente(), a.getAutorretenedor()));
        xml.append("    </TCR>\n");

        xml.append("    <GTA>\n");
        tag2(xml, "GTA_1", "01");
        tag2(xml, "GTA_2", "IVA");
        xml.append("    </GTA>\n");

        xml.append("  </ADQ>\n");
    }

    /**
     * TIM/IMP de una retención (TIM_1 = true). Va un TIM por cada código de tributo:
     * 06 = ReteRenta (base: valor bruto), 05 = ReteIVA (base: IVA facturado).
     */
    private void appendTimRetencion(StringBuilder xml, String codigoTributo,
                                    BigDecimal valor, BigDecimal base) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal b = base != null ? base : BigDecimal.ZERO;
        if (b.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("[Facturatech][DS] Retención {} de {} sin base gravable: se omite del XML",
                    codigoTributo, valor);
            return;
        }
        BigDecimal tarifa = valor.multiply(BigDecimal.valueOf(100)).divide(b, 2, RoundingMode.HALF_UP);
        xml.append("  <TIM>\n");
        tag(xml, "TIM_1", "true"); // true = retención
        tag(xml, "TIM_2", fmt(valor));
        tag(xml, "TIM_3", COP);
        xml.append("    <IMP>\n");
        tag2(xml, "IMP_1", codigoTributo);
        tag2(xml, "IMP_2", fmt(b));
        tag2(xml, "IMP_3", COP);
        tag2(xml, "IMP_4", fmt(valor));
        tag2(xml, "IMP_5", COP);
        tag2(xml, "IMP_6", fmt(tarifa));
        xml.append("    </IMP>\n");
        xml.append("  </TIM>\n");
    }

    /** CDN/DCN: naturaleza de la corrección de la nota de ajuste (Tabla 42). */
    private void appendCdnDs(StringBuilder xml, DianDocumentoRequestDTO req) {
        int concepto = req.getCodigoConcepto() != null ? req.getCodigoConcepto() : 2;
        int seccion = req.getSeccionCorregida() != null ? req.getSeccionCorregida() : 1;
        String descripcion = notBlank(req.getRazonConcepto())
                ? req.getRazonConcepto() : conceptoCorreccionDs(concepto);
        xml.append("  <CDN>\n");
        tag(xml, "CDN_1", String.valueOf(seccion));
        tag(xml, "CDN_2", String.valueOf(concepto));
        xml.append("    <DCN>\n");
        tag2(xml, "DCN_1", descripcion);
        xml.append("    </DCN>\n");
        xml.append("  </CDN>\n");
    }

    /** REF: documento soporte referenciado por la nota de ajuste (CUDS, no CUFE). */
    private void appendRefDs(StringBuilder xml, DianDocumentoRequestDTO req) {
        DianDocumentoRequestDTO.DocumentoReferenciaDTO ref = req.getDocumentoReferencia();
        if (ref == null) {
            log.warn("[Facturatech][DS] Nota de ajuste sin documento de referencia: será rechazada");
            return;
        }
        xml.append("  <REF>\n");
        tag(xml, "REF_1", "IV");
        tag(xml, "REF_2", nvl(ref.getNumeroDocumento()));
        if (ref.getFechaEmisionOriginal() != null) {
            tag(xml, "REF_3", ref.getFechaEmisionOriginal().toString());
        }
        if (notBlank(ref.getCufeOriginal())) {
            tag(xml, "REF_4", ref.getCufeOriginal()); // CUDS del DS referenciado
            tag(xml, "REF_5", "CUDS-SHA384");
        } else {
            log.warn("[Facturatech][DS] Nota de ajuste sin CUDS del documento soporte {}",
                    ref.getNumeroDocumento());
        }
        xml.append("  </REF>\n");
    }

    /**
     * ITE del documento soporte. Difiere de la factura: no lleva ITE_19..22 y
     * agrega IBS (obligatorio, sólo en el DS) después de IAE.
     */
    private void appendItemsDs(StringBuilder xml, DianDocumentoRequestDTO req, boolean esNotaAjuste) {
        if (req.getLineas() == null) return;
        LocalDate fechaCompra = req.getFechaCompra() != null ? req.getFechaCompra()
                : (req.getFechaEmision() != null ? req.getFechaEmision() : LocalDate.now(ZONA_CO));
        int forma = req.getFormaGeneracionTransmision() != null
                ? req.getFormaGeneracionTransmision()
                : config.getDocumentoSoporte().getFormaGeneracion();

        for (DianDocumentoRequestDTO.LineaDTO l : req.getLineas()) {
            LineaCalc c = calcularLinea(l);

            xml.append("  <ITE>\n");
            tag(xml, "ITE_1", String.valueOf(l.getNumero() != null ? l.getNumero() : 1));
            tag(xml, "ITE_3", fmt(c.cantidad));
            tag(xml, "ITE_4", "94");
            tag(xml, "ITE_5", fmt(c.base));
            tag(xml, "ITE_6", COP);
            tag(xml, "ITE_7", fmt(c.precioUnitario));
            tag(xml, "ITE_8", COP);
            tag(xml, "ITE_11", nvl(l.getDescripcion()));
            tag(xml, "ITE_27", fmt(c.cantidad));
            tag(xml, "ITE_28", "94");

            xml.append("    <IAE>\n");
            tag2(xml, "IAE_1", notBlank(l.getCodigoProducto()) ? l.getCodigoProducto() : "999");
            tag2(xml, "IAE_2", "999");
            xml.append("    </IAE>\n");

            // IBS: exclusivo del documento soporte (no se envía en la nota de ajuste).
            if (!esNotaAjuste) {
                xml.append("    <IBS>\n");
                tag2(xml, "IBS_1", fechaCompra.toString());
                tag2(xml, "IBS_2", String.valueOf(forma));
                tag2(xml, "IBS_3", forma == 2 ? "Acumulado semanal" : "Por operación");
                xml.append("    </IBS>\n");
            }

            if (c.descuento.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal baseDescuento = c.precioUnitario.multiply(c.cantidad);
                BigDecimal porcentaje = baseDescuento.compareTo(BigDecimal.ZERO) > 0
                        ? c.descuento.multiply(BigDecimal.valueOf(100)).divide(baseDescuento, 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                xml.append("    <IDE>\n");
                tag2(xml, "IDE_1", "false");
                tag2(xml, "IDE_2", fmt(c.descuento));
                tag2(xml, "IDE_3", COP);
                tag2(xml, "IDE_6", fmt(porcentaje));
                tag2(xml, "IDE_7", fmt(baseDescuento));
                tag2(xml, "IDE_8", COP);
                xml.append("    </IDE>\n");
            }

            if (c.iva.compareTo(BigDecimal.ZERO) > 0) {
                xml.append("    <TII>\n");
                tag2(xml, "TII_1", fmt(c.iva));
                tag2(xml, "TII_2", COP);
                tag2(xml, "TII_3", "false"); // false = impuesto
                xml.append("      <IIM>\n");
                tag3(xml, "IIM_1", "01");
                tag3(xml, "IIM_2", fmt(c.iva));
                tag3(xml, "IIM_3", COP);
                tag3(xml, "IIM_4", fmt(c.base));
                tag3(xml, "IIM_5", COP);
                tag3(xml, "IIM_6", c.tarifa);
                xml.append("      </IIM>\n");
                xml.append("    </TII>\n");
            }

            xml.append("  </ITE>\n");
        }
    }

    /** ENC_21 (Tabla 38): 10 = Residente (default), 11 = No Residente. */
    private String tipoOperacionDs(DianDocumentoRequestDTO req) {
        if (notBlank(req.getTipoOperacion())) {
            return req.getTipoOperacion().trim();
        }
        String porConfig = config.getDocumentoSoporte().getTipoOperacion();
        return notBlank(porConfig) ? porConfig.trim() : "10";
    }

    /** Tabla 42 — concepto de corrección de la nota de ajuste al documento soporte. */
    private String conceptoCorreccionDs(int codigo) {
        return switch (codigo) {
            case 1 -> "Devolución parcial de los bienes y/o no aceptación parcial del servicio";
            case 2 -> "Anulación del documento soporte en adquisiciones efectuadas a sujetos no obligados a expedir factura";
            case 3 -> "Rebaja o descuento parcial o total";
            case 4 -> "Ajuste de precio";
            default -> "Otros";
        };
    }

    /**
     * Dígito de verificación DIAN calculado sobre el número de identificación.
     * Se usa cuando el tercero/empresa no lo tiene registrado, porque la DIAN
     * rechaza el documento si el DV no corresponde al NIT informado.
     */
    static String digitoVerificacion(String identificacion) {
        if (identificacion == null) return "";
        String digitos = identificacion.replaceAll("\\D", "");
        if (digitos.isEmpty()) return "";
        int[] pesos = {3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71};
        long suma = 0;
        for (int i = 0; i < digitos.length() && i < pesos.length; i++) {
            int digito = digitos.charAt(digitos.length() - 1 - i) - '0';
            suma += (long) digito * pesos[i];
        }
        int resto = (int) (suma % 11);
        return String.valueOf(resto < 2 ? resto : 11 - resto);
    }

    // ══════════════════════════════════════════════════════════
    //  ENC — Encabezado
    // ══════════════════════════════════════════════════════════

    private void appendEnc(StringBuilder xml, DianDocumentoRequestDTO req,
                           String identificador, String tipoFactura, String tipoOperacion, Totales tot) {
        String nitEmisor = req.getEmisor() != null ? nvl(req.getEmisor().getNumeroIdentificacion()) : "";
        String nitAdq = req.getReceptor() != null ? nvl(req.getReceptor().getNumeroIdentificacion()) : "";
        int numItems = req.getLineas() != null ? req.getLineas().size() : 0;

        xml.append("  <ENC>\n");
        tag(xml, "ENC_1", identificador);
        tag(xml, "ENC_2", nitEmisor);
        tag(xml, "ENC_3", nitAdq);
        tag(xml, "ENC_4", "UBL 2.1");
        tag(xml, "ENC_5", "DIAN 2.1");
        tag(xml, "ENC_6", nvl(req.getPrefijo()) + (req.getConsecutivo() != null ? req.getConsecutivo() : ""));
        // ENC_7/ENC_8: fecha y hora de emisión en zona horaria de Colombia (-05:00).
        // El insumo base de certificación C1 los incluye; sin ellos Facturatech puede
        // tomar la fecha de firma y descuadrar contra la fecha del documento.
        LocalDate fechaEmi = req.getFechaEmision() != null ? req.getFechaEmision() : LocalDate.now(ZONA_CO);
        tag(xml, "ENC_7", fechaEmi.toString());
        tag(xml, "ENC_8", LocalTime.now(ZONA_CO).format(HORA_DS) + "-05:00");
        tag(xml, "ENC_9", tipoFactura);
        tag(xml, "ENC_10", COP);
        tag(xml, "ENC_15", String.valueOf(numItems));
        if (req.getFechaVencimiento() != null) {
            tag(xml, "ENC_16", req.getFechaVencimiento().toString());
        }
        tag(xml, "ENC_20", String.valueOf(config.getAmbiente()));
        tag(xml, "ENC_21", tipoOperacion);
        xml.append("  </ENC>\n");
    }

    // ══════════════════════════════════════════════════════════
    //  EMI — Emisor + nodos complementarios TAC, DFE, ICC, CDE, GTE
    // ══════════════════════════════════════════════════════════

    private void appendEmi(StringBuilder xml, DianDocumentoRequestDTO req) {
        DianDocumentoRequestDTO.EmisorDTO e = req.getEmisor() != null
                ? req.getEmisor() : new DianDocumentoRequestDTO.EmisorDTO();

        String razonSocial = nvl(e.getRazonSocial());
        String nombreComercial = e.getNombreComercial() != null && !e.getNombreComercial().isBlank()
                ? e.getNombreComercial() : razonSocial;
        String codDepto = nvl(e.getCodigoDepartamento());
        String codMuni = nvl(e.getCodigoMunicipio());
        String postal = e.getCodigoPostal() != null && !e.getCodigoPostal().isBlank()
                ? e.getCodigoPostal() : "000000";
        boolean responsableIva = !Boolean.FALSE.equals(e.getResponsableIva());

        if (codDepto.isEmpty() || codMuni.isEmpty()) {
            log.warn("[Facturatech] Emisor sin códigos DANE de departamento/municipio: " +
                    "configure la ubicación de la empresa para evitar rechazo del documento");
        }

        xml.append("  <EMI>\n");
        tag(xml, "EMI_1", tipoPersona(e.getTipoContribuyente(), e.getTipoIdentificacion()));
        tag(xml, "EMI_2", nvl(e.getNumeroIdentificacion()));
        tag(xml, "EMI_3", "31"); // Para emisor es obligatorio código 31 (NIT)
        tag(xml, "EMI_6", razonSocial);
        tag(xml, "EMI_7", nombreComercial);
        tag(xml, "EMI_10", nvl(e.getDireccion()));
        tag(xml, "EMI_11", codDepto);
        tag(xml, "EMI_13", nvl(e.getMunicipio()));
        tag(xml, "EMI_15", "CO");
        tag(xml, "EMI_19", nvl(e.getDepartamento()));
        tag(xml, "EMI_22", nvl(e.getDigitoVerificacion()));
        tag(xml, "EMI_23", codMuni);
        tag(xml, "EMI_24", razonSocial);

        // TAC: Obligaciones del contribuyente (O-13, O-15, O-23, O-47, R-99-PN)
        xml.append("    <TAC>\n");
        tag2(xml, "TAC_1", obligacionFiscal(e.getResponsabilidadFiscal(),
                e.getGranContribuyente(), e.getAutorretenedor()));
        xml.append("    </TAC>\n");

        // DFE: Dirección física del emisor
        xml.append("    <DFE>\n");
        tag2(xml, "DFE_1", codMuni);
        tag2(xml, "DFE_2", codDepto);
        tag2(xml, "DFE_3", "CO");
        tag2(xml, "DFE_4", postal);
        tag2(xml, "DFE_5", "COLOMBIA");
        tag2(xml, "DFE_6", nvl(e.getDepartamento()));
        tag2(xml, "DFE_7", nvl(e.getMunicipio()));
        tag2(xml, "DFE_8", nvl(e.getDireccion()));
        xml.append("    </DFE>\n");

        // ICC: Matrícula mercantil + prefijo
        xml.append("    <ICC>\n");
        tag2(xml, "ICC_1", e.getMatriculaMercantil() != null && !e.getMatriculaMercantil().isBlank()
                ? e.getMatriculaMercantil() : "0");
        tag2(xml, "ICC_9", nvl(req.getPrefijo()));
        xml.append("    </ICC>\n");

        // CDE: Contacto del emisor
        xml.append("    <CDE>\n");
        tag2(xml, "CDE_2", razonSocial);
        tag2(xml, "CDE_3", nvl(e.getTelefono()));
        tag2(xml, "CDE_4", nvl(e.getCorreo()));
        xml.append("    </CDE>\n");

        // GTE: Tributo del emisor (Tabla 11: 01=IVA, ZZ=No aplica)
        xml.append("    <GTE>\n");
        tag2(xml, "GTE_1", responsableIva ? "01" : "ZZ");
        tag2(xml, "GTE_2", responsableIva ? "IVA" : "No aplica");
        xml.append("    </GTE>\n");

        xml.append("  </EMI>\n");
    }

    // ══════════════════════════════════════════════════════════
    //  ADQ — Adquiriente + nodos complementarios TCR, ILA, CDA, GTA
    // ══════════════════════════════════════════════════════════

    private void appendAdq(StringBuilder xml, DianDocumentoRequestDTO req) {
        DianDocumentoRequestDTO.ReceptorDTO r = req.getReceptor() != null
                ? req.getReceptor() : new DianDocumentoRequestDTO.ReceptorDTO();

        String tipoDoc = r.getTipoIdentificacion() != null && !r.getTipoIdentificacion().isBlank()
                ? r.getTipoIdentificacion() : "13";
        String nombre = nvl(r.getNombre());
        boolean responsableIvaAdq = Boolean.TRUE.equals(r.getResponsableIva());

        xml.append("  <ADQ>\n");
        tag(xml, "ADQ_1", tipoPersona(r.getTipoContribuyente(), tipoDoc));
        tag(xml, "ADQ_2", nvl(r.getNumeroIdentificacion()));
        tag(xml, "ADQ_3", tipoDoc);
        tag(xml, "ADQ_6", nombre);
        tag(xml, "ADQ_7", nombre);

        // ADQ_10 condiciona los nodos 11,13,14,15,19,21,23 → solo si tenemos la información completa
        boolean direccionCompleta = notBlank(r.getDireccion())
                && notBlank(r.getCodigoDepartamento()) && notBlank(r.getCodigoMunicipio())
                && notBlank(r.getMunicipio()) && notBlank(r.getDepartamento());
        if (direccionCompleta) {
            tag(xml, "ADQ_10", r.getDireccion());
            tag(xml, "ADQ_11", r.getCodigoDepartamento());
            tag(xml, "ADQ_13", r.getMunicipio());
            tag(xml, "ADQ_14", notBlank(r.getCodigoPostal()) ? r.getCodigoPostal() : "000000");
            tag(xml, "ADQ_15", "CO");
            tag(xml, "ADQ_19", r.getDepartamento());
            tag(xml, "ADQ_21", "COLOMBIA");
        }
        if (notBlank(r.getDigitoVerificacion())) {
            tag(xml, "ADQ_22", r.getDigitoVerificacion());
        }
        if (direccionCompleta) {
            tag(xml, "ADQ_23", r.getCodigoMunicipio());
        }

        // TCR: Información tributaria del adquiriente
        xml.append("    <TCR>\n");
        tag2(xml, "TCR_1", obligacionFiscal(r.getResponsabilidadFiscal(), null, null));
        xml.append("    </TCR>\n");

        // ILA: Información legal del adquiriente
        xml.append("    <ILA>\n");
        tag2(xml, "ILA_1", nombre);
        tag2(xml, "ILA_2", nvl(r.getNumeroIdentificacion()));
        tag2(xml, "ILA_3", tipoDoc);
        xml.append("    </ILA>\n");

        // CDA: Contacto del adquiriente — sin CDA_4 Facturatech no envía el comprobante al cliente
        if (notBlank(r.getCorreo())) {
            xml.append("    <CDA>\n");
            if (notBlank(r.getTelefono())) {
                tag2(xml, "CDA_2", nombre);
                tag2(xml, "CDA_3", r.getTelefono());
            }
            tag2(xml, "CDA_4", r.getCorreo());
            xml.append("    </CDA>\n");
        } else {
            log.warn("[Facturatech] Adquiriente {} sin correo: el comprobante no será enviado por email",
                    r.getNumeroIdentificacion());
        }

        // GTA: Detalles tributarios del adquiriente (Tabla 11)
        xml.append("    <GTA>\n");
        tag2(xml, "GTA_1", responsableIvaAdq ? "01" : "ZZ");
        tag2(xml, "GTA_2", responsableIvaAdq ? "IVA" : "No aplica");
        xml.append("    </GTA>\n");

        xml.append("  </ADQ>\n");
    }

    // ══════════════════════════════════════════════════════════
    //  TOT / TIM — Totales e impuestos globales
    // ══════════════════════════════════════════════════════════

    private void appendTot(StringBuilder xml, Totales tot) {
        xml.append("  <TOT>\n");
        tag(xml, "TOT_1", fmt(tot.brutoAntesTributos)); // Σ ITE_5
        tag(xml, "TOT_2", COP);
        tag(xml, "TOT_3", fmt(tot.baseImponible));      // Σ IIM_4 (primer impuesto por ítem)
        tag(xml, "TOT_4", COP);
        tag(xml, "TOT_5", fmt(tot.totalFactura));       // TOT_7 (sin descuentos/cargos globales)
        tag(xml, "TOT_6", COP);
        tag(xml, "TOT_7", fmt(tot.totalFactura));       // TOT_1 + Σ impuestos
        tag(xml, "TOT_8", COP);
        xml.append("  </TOT>\n");
    }

    private void appendTim(StringBuilder xml, Totales tot) {
        if (tot.ivaPorTarifa.isEmpty()) {
            return; // Factura simple sin impuestos: no se definen TIM/IMP
        }
        xml.append("  <TIM>\n");
        tag(xml, "TIM_1", "false"); // false = impuesto
        tag(xml, "TIM_2", fmt(tot.totalIva));
        tag(xml, "TIM_3", COP);
        for (Map.Entry<String, BigDecimal[]> e : tot.ivaPorTarifa.entrySet()) {
            xml.append("    <IMP>\n");
            tag2(xml, "IMP_1", "01"); // 01 = IVA (Tabla 44)
            tag2(xml, "IMP_2", fmt(e.getValue()[0])); // base
            tag2(xml, "IMP_3", COP);
            tag2(xml, "IMP_4", fmt(e.getValue()[1])); // impuesto
            tag2(xml, "IMP_5", COP);
            tag2(xml, "IMP_6", e.getKey());           // tarifa
            xml.append("    </IMP>\n");
        }
        xml.append("  </TIM>\n");
    }

    // ══════════════════════════════════════════════════════════
    //  DRF / REF / MEP / CDN
    // ══════════════════════════════════════════════════════════

    private void appendDrf(StringBuilder xml, DianDocumentoRequestDTO req) {
        xml.append("  <DRF>\n");
        tag(xml, "DRF_1", nvl(req.getResolucionDian()));
        tag(xml, "DRF_2", req.getFechaInicioResolucion() != null ? req.getFechaInicioResolucion().toString() : "");
        tag(xml, "DRF_3", req.getFechaFinResolucion() != null ? req.getFechaFinResolucion().toString() : "");
        tag(xml, "DRF_4", nvl(req.getPrefijo()));
        tag(xml, "DRF_5", req.getConsecutivoDesde() != null ? String.valueOf(req.getConsecutivoDesde()) : "1");
        tag(xml, "DRF_6", req.getConsecutivoHasta() != null ? String.valueOf(req.getConsecutivoHasta()) : "5000000");
        xml.append("  </DRF>\n");
    }

    /** REF: referencia a la factura original (obligatorio en NC/ND). */
    private void appendRef(StringBuilder xml, DianDocumentoRequestDTO req) {
        DianDocumentoRequestDTO.DocumentoReferenciaDTO ref = req.getDocumentoReferencia();
        if (ref == null) {
            log.warn("[Facturatech] Nota sin documento de referencia: la DIAN puede rechazarla");
            return;
        }
        xml.append("  <REF>\n");
        tag(xml, "REF_1", "IV"); // IV = referencia a factura
        tag(xml, "REF_2", nvl(ref.getNumeroDocumento()));
        if (ref.getFechaEmisionOriginal() != null) {
            tag(xml, "REF_3", ref.getFechaEmisionOriginal().toString());
        }
        if (notBlank(ref.getCufeOriginal())) {
            tag(xml, "REF_4", ref.getCufeOriginal());
            tag(xml, "REF_5", "CUFE-SHA384");
        }
        xml.append("  </REF>\n");
    }

    private void appendMep(StringBuilder xml, DianDocumentoRequestDTO req) {
        // MEP_1: medio de pago (Tabla 5: 10=Efectivo, 47=Transferencia, 48=TC, 49=TD, ZZZ=Otro)
        // MEP_2: método de pago (Tabla 26: 1=Contado, 2=Crédito)
        String medio = "10";
        List<DianDocumentoRequestDTO.MetodoPagoDTO> mps = req.getMetodosPago();
        if (mps != null && !mps.isEmpty() && notBlank(mps.get(0).getMedioPago())) {
            medio = mps.get(0).getMedioPago();
        }
        boolean credito = "2".equals(req.getFormaPago());
        xml.append("  <MEP>\n");
        tag(xml, "MEP_1", medio);
        tag(xml, "MEP_2", credito ? "2" : "1");
        if (credito && req.getFechaVencimiento() != null) {
            tag(xml, "MEP_3", req.getFechaVencimiento().toString());
        }
        xml.append("  </MEP>\n");
    }

    /** CDN: concepto de la nota (Tabla 18 NC / Tabla 19 ND). */
    private void appendCdn(StringBuilder xml, int concepto, String razon, boolean esNotaCredito) {
        String descripcion = notBlank(razon) ? razon : descripcionConcepto(concepto, esNotaCredito);
        xml.append("  <CDN>\n");
        tag(xml, "CDN_1", String.valueOf(concepto));
        tag(xml, "CDN_2", descripcion);
        xml.append("  </CDN>\n");
    }

    // ══════════════════════════════════════════════════════════
    //  ITE — Ítems (con IAE, IDE de descuento y TII/IIM de IVA)
    // ══════════════════════════════════════════════════════════

    private void appendItems(StringBuilder xml, List<DianDocumentoRequestDTO.LineaDTO> lineas) {
        if (lineas == null) return;
        for (DianDocumentoRequestDTO.LineaDTO l : lineas) {
            LineaCalc c = calcularLinea(l);

            xml.append("  <ITE>\n");
            tag(xml, "ITE_1", String.valueOf(l.getNumero() != null ? l.getNumero() : 1));
            tag(xml, "ITE_3", fmt(c.cantidad));
            tag(xml, "ITE_4", "94"); // 94 = unidad (Tabla 12)
            tag(xml, "ITE_5", fmt(c.base));
            tag(xml, "ITE_6", COP);
            tag(xml, "ITE_7", fmt(c.precioUnitario));
            tag(xml, "ITE_8", COP);
            tag(xml, "ITE_11", nvl(l.getDescripcion()));
            tag(xml, "ITE_19", fmt(c.base));
            tag(xml, "ITE_20", COP);
            tag(xml, "ITE_21", fmt(c.base.add(c.iva)));
            tag(xml, "ITE_22", COP);
            tag(xml, "ITE_27", fmt(c.cantidad));
            tag(xml, "ITE_28", "94");

            // IAE: identificación del artículo (999 = estándar del contribuyente)
            xml.append("    <IAE>\n");
            tag2(xml, "IAE_1", notBlank(l.getCodigoProducto()) ? l.getCodigoProducto() : "999");
            tag2(xml, "IAE_2", "999");
            xml.append("    </IAE>\n");

            // IDE: descuento a nivel ítem (ya restado en ITE_5/19/21)
            if (c.descuento.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal baseDescuento = c.precioUnitario.multiply(c.cantidad);
                BigDecimal porcentaje = baseDescuento.compareTo(BigDecimal.ZERO) > 0
                        ? c.descuento.multiply(BigDecimal.valueOf(100)).divide(baseDescuento, 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                xml.append("    <IDE>\n");
                tag2(xml, "IDE_1", "false"); // false = descuento
                tag2(xml, "IDE_2", fmt(c.descuento));
                tag2(xml, "IDE_3", COP);
                tag2(xml, "IDE_6", fmt(porcentaje));
                tag2(xml, "IDE_7", fmt(baseDescuento));
                tag2(xml, "IDE_8", COP);
                tag2(xml, "IDE_10", "1");
                xml.append("    </IDE>\n");
            }

            // TII/IIM: IVA del ítem (solo si tiene impuesto)
            if (c.iva.compareTo(BigDecimal.ZERO) > 0) {
                xml.append("    <TII>\n");
                tag2(xml, "TII_1", fmt(c.iva));
                tag2(xml, "TII_2", COP);
                tag2(xml, "TII_3", "false"); // false = impuesto
                xml.append("      <IIM>\n");
                tag3(xml, "IIM_1", "01"); // 01 = IVA (Tabla 44)
                tag3(xml, "IIM_2", fmt(c.iva));
                tag3(xml, "IIM_3", COP);
                tag3(xml, "IIM_4", fmt(c.base));
                tag3(xml, "IIM_5", COP);
                tag3(xml, "IIM_6", c.tarifa);
                xml.append("      </IIM>\n");
                xml.append("    </TII>\n");
            }

            xml.append("  </ITE>\n");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  Cálculos
    // ══════════════════════════════════════════════════════════

    private static class LineaCalc {
        BigDecimal cantidad;
        BigDecimal precioUnitario;
        BigDecimal descuento;
        BigDecimal base;   // (cantidad × precio) − descuento
        BigDecimal iva;
        String tarifa;     // "19.00"
    }

    private static class Totales {
        BigDecimal brutoAntesTributos = BigDecimal.ZERO; // Σ bases
        BigDecimal baseImponible = BigDecimal.ZERO;      // Σ bases con impuesto definido
        BigDecimal totalIva = BigDecimal.ZERO;
        BigDecimal totalFactura = BigDecimal.ZERO;       // bruto + IVA
        // tarifa → [Σ base, Σ iva]
        Map<String, BigDecimal[]> ivaPorTarifa = new LinkedHashMap<>();
    }

    private LineaCalc calcularLinea(DianDocumentoRequestDTO.LineaDTO l) {
        LineaCalc c = new LineaCalc();
        c.cantidad = l.getCantidad() != null ? BigDecimal.valueOf(l.getCantidad()) : BigDecimal.ONE;
        c.precioUnitario = l.getPrecioUnitario() != null ? l.getPrecioUnitario() : BigDecimal.ZERO;
        c.descuento = l.getDescuento() != null ? l.getDescuento() : BigDecimal.ZERO;
        c.base = c.precioUnitario.multiply(c.cantidad).subtract(c.descuento).max(BigDecimal.ZERO);
        c.iva = l.getValorIva() != null ? l.getValorIva() : BigDecimal.ZERO;

        BigDecimal tarifa = l.getPorcentajeIva();
        if ((tarifa == null || tarifa.compareTo(BigDecimal.ZERO) == 0)
                && c.iva.compareTo(BigDecimal.ZERO) > 0
                && c.base.compareTo(BigDecimal.ZERO) > 0) {
            tarifa = c.iva.multiply(BigDecimal.valueOf(100)).divide(c.base, 2, RoundingMode.HALF_UP);
        }
        c.tarifa = fmt(tarifa != null ? tarifa : BigDecimal.ZERO);
        return c;
    }

    private Totales calcularTotales(List<DianDocumentoRequestDTO.LineaDTO> lineas) {
        Totales t = new Totales();
        if (lineas == null) return t;
        for (DianDocumentoRequestDTO.LineaDTO l : lineas) {
            LineaCalc c = calcularLinea(l);
            t.brutoAntesTributos = t.brutoAntesTributos.add(c.base);
            t.totalIva = t.totalIva.add(c.iva);
            if (c.iva.compareTo(BigDecimal.ZERO) > 0) {
                t.baseImponible = t.baseImponible.add(c.base);
                BigDecimal[] acc = t.ivaPorTarifa.computeIfAbsent(c.tarifa,
                        k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
                acc[0] = acc[0].add(c.base);
                acc[1] = acc[1].add(c.iva);
            }
        }
        t.totalFactura = t.brutoAntesTributos.add(t.totalIva);
        return t;
    }

    // ══════════════════════════════════════════════════════════
    //  Utilidades
    // ══════════════════════════════════════════════════════════

    /** Tipo de persona (Tabla 20): 1 = Jurídica, 2 = Natural. */
    private String tipoPersona(String tipoContribuyente, String tipoIdentificacion) {
        if (tipoContribuyente != null) {
            if (tipoContribuyente.toUpperCase().contains("JURIDICA")) return "1";
            if (tipoContribuyente.toUpperCase().contains("NATURAL")) return "2";
        }
        return "31".equals(tipoIdentificacion) ? "1" : "2";
    }

    /**
     * Devuelve la obligación fiscal válida para Facturatech
     * (O-13, O-15, O-23, O-47, R-99-PN). Toma el primer código válido de la
     * responsabilidad fiscal registrada, o la deriva de los indicadores.
     */
    private String obligacionFiscal(String responsabilidadFiscal, Boolean granContribuyente, Boolean autorretenedor) {
        if (responsabilidadFiscal != null && !responsabilidadFiscal.isBlank()) {
            for (String codigo : responsabilidadFiscal.split("[;,\\s]+")) {
                String c = codigo.trim().toUpperCase();
                if (c.equals("O-13") || c.equals("O-15") || c.equals("O-23")
                        || c.equals("O-47") || c.equals("R-99-PN")) {
                    return c;
                }
            }
        }
        if (Boolean.TRUE.equals(granContribuyente)) return "O-13";
        if (Boolean.TRUE.equals(autorretenedor)) return "O-15";
        return "R-99-PN";
    }

    private String descripcionConcepto(int codigo, boolean esNotaCredito) {
        if (esNotaCredito) {
            return switch (codigo) { // Tabla 18
                case 1 -> "Devolución parcial de los bienes y/o no aceptación parcial del servicio";
                case 2 -> "Anulación de factura electrónica";
                case 3 -> "Rebaja o descuento parcial o total";
                case 4 -> "Ajuste de precio";
                default -> "Otros";
            };
        }
        return switch (codigo) { // Tabla 19
            case 1 -> "Intereses";
            case 2 -> "Gastos por cobrar";
            case 3 -> "Cambio del valor";
            default -> "Otros";
        };
    }

    private static void tag(StringBuilder xml, String nombre, String valor) {
        xml.append("    <").append(nombre).append('>').append(esc(valor)).append("</").append(nombre).append(">\n");
    }

    private static void tag2(StringBuilder xml, String nombre, String valor) {
        xml.append("      <").append(nombre).append('>').append(esc(valor)).append("</").append(nombre).append(">\n");
    }

    private static void tag3(StringBuilder xml, String nombre, String valor) {
        xml.append("        <").append(nombre).append('>').append(esc(valor)).append("</").append(nombre).append(">\n");
    }

    private static String fmt(BigDecimal v) {
        return (v != null ? v : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String nvl(String s) {
        return s != null ? s.trim() : "";
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
