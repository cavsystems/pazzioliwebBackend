package com.pazzioliweb.facturacionmodule.service;

import com.pazzioliweb.commonbacken.events.DevolucionRegistradaEvent;
import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.facturacionmodule.config.DianConfig;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoRequestDTO;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoResponseDTO;
import com.pazzioliweb.facturacionmodule.entity.Facturas;
import com.pazzioliweb.facturacionmodule.repositori.FacturasRepository;
import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresasback.repositori.EmpresaRepositori;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.ventasmodule.entity.DetalleDevolucion;
import com.pazzioliweb.ventasmodule.entity.Devolucion;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.repository.DevolucionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener que se dispara al registrarse una devolución de venta.
 * Genera automáticamente la Nota Crédito Electrónica (NC) para DIAN
 * referenciando la factura original (CUFE).
 *
 * El listener corre AFTER_COMMIT en una nueva transacción para que si falla
 * no afecte la devolución que ya fue persistida.
 */
@Component
public class DevolucionRegistradaListener {

    private static final Logger log = LoggerFactory.getLogger(DevolucionRegistradaListener.class);

    private final DevolucionRepository devolucionRepository;
    private final FacturasRepository facturasRepository;
    private final ComprobanteContableRepository comprobantesRepository;
    private final EmpresaRepositori empresaRepositori;
    private final ProveedorFacturacionElectronica proveedorDian;
    private final DianConfig dianConfig;
    private final com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService asignacionComprobante;

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    /** TX corta REQUIRES_NEW: commits inmediatos que sobreviven a un rollback del listener. */
    private org.springframework.transaction.support.TransactionTemplate txCorta() {
        org.springframework.transaction.support.TransactionTemplate t =
                new org.springframework.transaction.support.TransactionTemplate(transactionManager);
        t.setPropagationBehavior(org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return t;
    }

    /** devoluciones.mensaje_dian_nc es varchar(500): un mensaje SOAP largo tumbaría el
     *  save con DataTruncation y la NC quedaría emitida pero sin registrar (doble emisión). */
    private static String truncar(String m) {
        return (m == null || m.length() <= 500) ? m : m.substring(0, 497) + "...";
    }

    public DevolucionRegistradaListener(DevolucionRepository devolucionRepository,
                                         FacturasRepository facturasRepository,
                                         ComprobanteContableRepository comprobantesRepository,
                                         EmpresaRepositori empresaRepositori,
                                         ProveedorFacturacionElectronica proveedorDian,
                                         DianConfig dianConfig,
                                         com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService asignacionComprobante) {
        this.devolucionRepository = devolucionRepository;
        this.facturasRepository = facturasRepository;
        this.comprobantesRepository = comprobantesRepository;
        this.empresaRepositori = empresaRepositori;
        this.proveedorDian = proveedorDian;
        this.dianConfig = dianConfig;
        this.asignacionComprobante = asignacionComprobante;
    }

    // fallbackExecution=true: el endpoint de REENVÍO publica el evento SIN transacción
    // activa; sin esto Spring descartaba el evento en silencio y el "reenvío" era un no-op.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onDevolucionRegistrada(DevolucionRegistradaEvent event) {
        log.info("══════ Evento DevolucionRegistrada → Generando Nota Crédito Electrónica ══════");
        log.info("Devolución ID: {}, Venta ID: {}, Concepto: {}",
                event.getDevolucionId(), event.getVentaId(), event.getCodigoConcepto());

        try {
            Devolucion devolucion = devolucionRepository.findById(event.getDevolucionId()).orElse(null);
            if (devolucion == null) {
                log.warn("Devolución {} no encontrada — se omite NC", event.getDevolucionId());
                return;
            }
            Venta venta = devolucion.getVenta();
            if (venta == null) {
                log.warn("Devolución {} sin venta asociada — se omite NC", event.getDevolucionId());
                return;
            }

            // Buscar la factura electrónica original de la venta (para referenciar el CUFE)
            Optional<Facturas> facturaOpt = facturasRepository.findByVentaId(venta.getId());
            if (facturaOpt.isEmpty()) {
                log.warn("La venta {} no tiene factura electrónica previa — NC se omite (no se puede referenciar)",
                        venta.getNumeroVenta());
                return;
            }
            Facturas facturaOriginal = facturaOpt.get();

            // Idempotencia: si esta devolución YA tiene una NC autorizada, un reenvío
            // no debe emitir una SEGUNDA nota fiscal (folio nuevo pisando numeroNc/cufeNc).
            if ("AUTORIZADA".equals(devolucion.getEstadoDianNc())
                    && devolucion.getCufeNc() != null && !devolucion.getCufeNc().isBlank()
                    && !devolucion.getCufeNc().startsWith("SIMULADO-")) {
                log.info("La devolución {} ya tiene NC autorizada ({}) — no se re-emite",
                        event.getDevolucionId(), devolucion.getNumeroNc());
                return;
            }

            // Si la NC anterior quedó EN PROCESO (Facturatech firmando cuando se agotó
            // el polling), NO se re-emite con folio nuevo: se consulta por prefijo+folio.
            // Si ya fue firmada, se cierra aquí; si sigue en proceso, se espera.
            String estadoNcPrevio = devolucion.getEstadoDianNc();
            if (devolucion.getNumeroNc() != null && !devolucion.getNumeroNc().isBlank()
                    && ("EN_PROCESO".equals(estadoNcPrevio) || "ENVIADA".equals(estadoNcPrevio))) {
                String numNc = devolucion.getNumeroNc();
                String folioStr = numNc.replaceAll("^\\D+", "");
                String prefStr = numNc.substring(0, numNc.length() - folioStr.length());
                if (!folioStr.isEmpty()) {
                    try {
                        DianDocumentoResponseDTO estadoResp =
                                proveedorDian.consultarEstadoDocumento(prefStr, Integer.parseInt(folioStr));
                        if (estadoResp.isExitoso()) {
                            log.info("La NC {} ya fue firmada en Facturatech: se actualiza sin re-emitir", numNc);
                            devolucion.setCufeNc(estadoResp.getCufe());
                            devolucion.setEstadoDianNc("AUTORIZADA");
                            devolucion.setMensajeDianNc(truncar(estadoResp.getMensajeDian()));
                            if (estadoResp.getQrData() != null) devolucion.setQrDataNc(estadoResp.getQrData());
                            devolucionRepository.save(devolucion);
                        } else {
                            log.info("La NC {} sigue en proceso en Facturatech: no se re-emite (reintente luego)", numNc);
                        }
                    } catch (Exception ex) {
                        log.warn("No se pudo consultar el estado de la NC {} ({}): no se re-emite para evitar duplicado",
                                numNc, ex.getMessage());
                    }
                    return;
                }
            }

            // La NC referencia el CUFE de la factura (REF_4): si la factura no está
            // AUTORIZADA (o su CUFE es simulado), la DIAN rechazaría la nota y el folio
            // NC quedaría consumido. Se deja PENDIENTE para reintentar con /reenviar
            // cuando la factura esté autorizada.
            if (facturaOriginal.getEstadoDian() != Facturas.EstadoDian.AUTORIZADA
                    || facturaOriginal.getCufe() == null || facturaOriginal.getCufe().isBlank()
                    || facturaOriginal.getCufe().startsWith("SIMULADO-")) {
                log.warn("La factura {} de la venta {} no está AUTORIZADA (estado: {}) — la NC queda " +
                        "PENDIENTE sin consumir folio; reintente con el reenvío tras autorizar la factura.",
                        facturaOriginal.getNumeroFactura(), venta.getNumeroVenta(), facturaOriginal.getEstadoDian());
                devolucion.setEstadoDianNc("PENDIENTE");
                devolucion.setMensajeDianNc("Factura original " + facturaOriginal.getNumeroFactura() +
                        " sin autorizar por la DIAN (estado " + facturaOriginal.getEstadoDian() +
                        "): la NC se emitirá al reenviar cuando esté autorizada.");
                devolucionRepository.save(devolucion);
                return;
            }

            // PRE-validación de la resolución del comprobante NC ANTES de consumir folio:
            // validarResolucionDian deja pasar resoluciones con fechas/rango en null y el
            // guard posterior quemaba un folio por cada intento fallido.
            ComprobanteContable compNcPre = comprobantesRepository.findByTipo(TipoMovimientoComprobante.NC).stream()
                    .filter(c -> Boolean.TRUE.equals(c.getActivo()) && !Boolean.TRUE.equals(c.getEsLegacy()))
                    .sorted(java.util.Comparator.comparing(ComprobanteContable::getId))
                    .findFirst().orElse(null);
            if (compNcPre != null && (compNcPre.getResolucionDian() == null || compNcPre.getResolucionDian().isBlank()
                    || compNcPre.getFechaInicioResolucion() == null || compNcPre.getFechaFinResolucion() == null
                    || compNcPre.getConsecutivoDesde() == null || compNcPre.getConsecutivoHasta() == null)) {
                log.error("El comprobante NC '{}' no tiene la resolución DIAN completa — la NC queda " +
                        "PENDIENTE sin consumir folio. Configure resolución/vigencia/rango.", compNcPre.getPrefijo());
                devolucion.setEstadoDianNc("PENDIENTE");
                devolucion.setMensajeDianNc(truncar("Comprobante NC " + compNcPre.getPrefijo() +
                        " sin resolución DIAN completa: configure la resolución y reenvíe la NC."));
                devolucionRepository.save(devolucion);
                return;
            }

            // Numeración de la NC con AsignacionComprobanteService: lock pesimista sobre el
            // comprobante (dos devoluciones concurrentes no duplican folio) y validación de
            // resolución vigente/rango. En TX CORTA propia: (a) el lock no se retiene
            // durante el round-trip SOAP; (b) si asignar lanza, la excepción no marca
            // rollback-only la TX del listener (evita UnexpectedRollbackException hacia
            // el commit de la devolución).
            com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService.Resultado rNC;
            try {
                rNC = txCorta().execute(status ->
                        asignacionComprobante.asignarSinCajero(TipoMovimientoComprobante.NC));
            } catch (RuntimeException ex) {
                log.warn("No se pudo asignar numeración NC ({}). Se omite la Nota Crédito electrónica.", ex.getMessage());
                devolucion.setEstadoDianNc("PENDIENTE");
                devolucion.setMensajeDianNc(truncar("No se pudo asignar numeración NC: " + ex.getMessage()));
                devolucionRepository.save(devolucion);
                return;
            }
            ComprobanteContable compNC = rNC.getComprobante();
            int siguiente = rNC.getConsecutivo();

            // Armar request DIAN para NC
            DianDocumentoRequestDTO req = armarRequestNC(devolucion, venta, facturaOriginal,
                    compNC.getPrefijo(), siguiente, event.getCodigoConcepto());

            // Propagar resolución/numeración del comprobante NC (nodo DRF del documento).
            // Sin resolución completa NO se envía: el DRF con datos de otra resolución
            // (fallback global) provoca rechazo 409 en Facturatech.
            if (compNC.getResolucionDian() != null && !compNC.getResolucionDian().isBlank()
                    && compNC.getFechaInicioResolucion() != null && compNC.getFechaFinResolucion() != null
                    && compNC.getConsecutivoDesde() != null && compNC.getConsecutivoHasta() != null) {
                req.setResolucionDian(compNC.getResolucionDian());
                req.setClaveTecnicaDian(compNC.getClaveTecnicaDian());
                req.setFechaInicioResolucion(compNC.getFechaInicioResolucion());
                req.setFechaFinResolucion(compNC.getFechaFinResolucion());
                req.setConsecutivoDesde(compNC.getConsecutivoDesde());
                req.setConsecutivoHasta(compNC.getConsecutivoHasta());
            } else {
                log.error("El comprobante NC '{}' no tiene la resolución DIAN completa — la NC queda " +
                        "PENDIENTE. Configure resolución/vigencia/rango en Contabilidad → Comprobantes.",
                        compNC.getPrefijo());
                devolucion.setEstadoDianNc("PENDIENTE");
                devolucion.setMensajeDianNc("Comprobante NC " + compNC.getPrefijo() +
                        " sin resolución DIAN completa: configure la resolución y reenvíe la NC.");
                devolucionRepository.save(devolucion);
                return;
            }

            log.info("Enviando NC {} {} (referencia CUFE: {})",
                    req.getPrefijo(), req.getConsecutivo(),
                    facturaOriginal.getCufe() != null ? facturaOriginal.getCufe().substring(0, Math.min(20, facturaOriginal.getCufe().length())) + "..." : "N/A");

            // El folio ya quedó commiteado en la TX corta: pase lo que pase de aquí en
            // adelante hay que dejar RASTRO en la devolución (numeroNc + estado).
            devolucion.setNumeroNc(compNC.getPrefijo() + siguiente);

            DianDocumentoResponseDTO resp;
            try {
                resp = proveedorDian.enviarFactura(req);
            } catch (Exception envEx) {
                log.error("Error enviando la NC {} a Facturatech: {}", devolucion.getNumeroNc(), envEx.getMessage());
                devolucion.setEstadoDianNc("RECHAZADA");
                devolucion.setMensajeDianNc(truncar("Error al enviar la NC: " + envEx.getMessage()));
                final Devolucion dErr = devolucion;
                txCorta().execute(status -> devolucionRepository.save(dErr));
                return;
            }

            // Persistir resultado en la devolución. El número guardado es el LEGAL
            // (prefijo+folio sin guion, igual al ENC_6 enviado a la DIAN) para que las
            // re-consultas por prefijo+folio y la conciliación coincidan con el documento.
            // TX corta: si el listener llegara a hacer rollback después, el registro de
            // la NC (que YA existe en Facturatech) no se pierde.
            devolucion.setCufeNc(resp.getCufe());
            devolucion.setEstadoDianNc(resp.getEstadoDian());
            devolucion.setMensajeDianNc(truncar(resp.getMensajeDian()));
            devolucion.setQrDataNc(resp.getQrData());
            final Devolucion dOk = devolucion;
            txCorta().execute(status -> devolucionRepository.save(dOk));

            log.info("✅ Nota Crédito generada: {} - Estado DIAN: {} - CUDE: {}",
                    devolucion.getNumeroNc(), resp.getEstadoDian(),
                    resp.getCufe() != null ? resp.getCufe().substring(0, Math.min(30, resp.getCufe().length())) + "..." : "N/A");

        } catch (Exception e) {
            log.error("❌ Error generando NC para devolución {}: {}",
                    event.getDevolucionId(), e.getMessage(), e);
            // Best-effort: dejar rastro del fallo en la devolución (en TX corta, porque
            // la TX del listener puede estar rollback-only en este punto).
            try {
                Devolucion d = devolucionRepository.findById(event.getDevolucionId()).orElse(null);
                if (d != null && (d.getEstadoDianNc() == null || d.getEstadoDianNc().isBlank()
                        || "PENDIENTE".equals(d.getEstadoDianNc()))) {
                    d.setEstadoDianNc("RECHAZADA");
                    d.setMensajeDianNc(truncar("Error generando NC: " + e.getMessage()));
                    txCorta().execute(status -> devolucionRepository.save(d));
                }
            } catch (Exception ignored) {
                log.warn("No se pudo registrar el error de NC en la devolución {}", event.getDevolucionId());
            }
        }
    }

    private DianDocumentoRequestDTO armarRequestNC(Devolucion devolucion, Venta venta,
                                                    Facturas facturaOriginal,
                                                    String prefijoNC, int consecutivoNC,
                                                    Integer concepto) {
        DianDocumentoRequestDTO req = new DianDocumentoRequestDTO();
        req.setTipoDocumento("91"); // 91 = Nota Crédito
        req.setPrefijo(prefijoNC);
        req.setConsecutivo(consecutivoNC);
        // La NC se emite HOY (ENC_7): con el reenvío diferido, heredar la fecha de la
        // devolución produciría una nota con fecha de emisión pasada (rechazo DIAN).
        req.setFechaEmision(java.time.LocalDate.now());
        req.setCodigoConcepto(concepto != null ? concepto : 1);
        req.setRazonConcepto(devolucion.getMotivo());

        // MEP de la nota = MEP de la FACTURA que referencia (el manual exige coherencia):
        // forma de pago según el comprobante de la venta (VC = crédito) y los medios
        // de pago reales — antes viajaba siempre contado/efectivo por defecto.
        boolean esCredito = venta.getComprobante() != null
                && venta.getComprobante().getTipoMovimiento() == TipoMovimientoComprobante.VC;
        req.setFormaPago(esCredito ? "2" : "1");
        if (esCredito && facturaOriginal.getFechaVencimiento() != null) {
            req.setFechaVencimiento(facturaOriginal.getFechaVencimiento());
        }
        List<DianDocumentoRequestDTO.MetodoPagoDTO> mps = new ArrayList<>();
        if (venta.getMetodosPago() != null) {
            for (com.pazzioliweb.ventasmodule.entity.VentaMetodoPago vmp : venta.getMetodosPago()) {
                DianDocumentoRequestDTO.MetodoPagoDTO mp = new DianDocumentoRequestDTO.MetodoPagoDTO();
                mp.setMedioPago(mapearMedioPagoDian(vmp.getMetodoPago() != null ? vmp.getMetodoPago().getSigla() : null));
                mp.setMonto(vmp.getMonto());
                mp.setReferencia(vmp.getReferencia());
                mps.add(mp);
            }
        }
        req.setMetodosPago(mps);
        req.setResolucionDian(dianConfig.getResolucion().getNumero());

        // Referencia al documento original (factura electrónica)
        DianDocumentoRequestDTO.DocumentoReferenciaDTO ref = new DianDocumentoRequestDTO.DocumentoReferenciaDTO();
        ref.setNumeroDocumento(facturaOriginal.getNumeroFactura());
        ref.setCufeOriginal(facturaOriginal.getCufe());
        // REF_3 = fecha de FIRMA del documento referenciado (validación DIAN), no la de emisión
        ref.setFechaEmisionOriginal(facturaOriginal.getFechaValidacionDian() != null
                ? facturaOriginal.getFechaValidacionDian().toLocalDate()
                : facturaOriginal.getFechaEmision());
        ref.setTipoDocumentoOriginal("01");
        req.setDocumentoReferencia(ref);

        // Emisor
        DianDocumentoRequestDTO.EmisorDTO emisor = new DianDocumentoRequestDTO.EmisorDTO();
        try {
            Empresa empresa = empresaRepositori.findById((long) dianConfig.getEmpresaId()).orElse(null);
            if (empresa != null) {
                emisor.setTipoIdentificacion(empresa.getCodigotipoidentificacion() != null
                        ? String.valueOf(empresa.getCodigotipoidentificacion().getCodigoTipoIdentificacion())
                        : "31");
                emisor.setNumeroIdentificacion(empresa.getNumeroidentificacion());
                emisor.setDigitoVerificacion(empresa.getDigitoverificacion());
                emisor.setRazonSocial(empresa.getRazonsocial() != null ? empresa.getRazonsocial() : empresa.getNombrecomercial());
                emisor.setNombreComercial(empresa.getNombrecomercial());
                emisor.setDireccion(empresa.getDireccion());
                emisor.setTelefono(empresa.getCelularempresa());
                emisor.setCorreo(empresa.getCorreoempresa());
                emisor.setPais("CO");
                emisor.setCodigoPostal(empresa.getCodigopostal());
                // Ubicación con códigos DANE (Facturatech tablas 34/35)
                if (empresa.getCodigomunicipio() != null) {
                    emisor.setMunicipio(empresa.getCodigomunicipio().getMunicipio());
                    int codDep = empresa.getCodigomunicipio().getCodigoDepartamento();
                    int codMun = empresa.getCodigomunicipio().getCodigoMunicipio();
                    emisor.setCodigoMunicipio(codMun >= 1000
                            ? String.format("%05d", codMun)
                            : String.format("%02d%03d", codDep, codMun));
                }
                if (empresa.getCodigodepartamento() != null) {
                    emisor.setDepartamento(empresa.getCodigodepartamento().getDepartamento());
                    emisor.setCodigoDepartamento(String.format("%02d",
                            empresa.getCodigodepartamento().getCodigoDepartamento()));
                }
                // Datos fiscales
                emisor.setResponsabilidadFiscal(empresa.getResponsabilidadFiscal());
                emisor.setTipoContribuyente(empresa.getTipoContribuyente());
                emisor.setGranContribuyente(empresa.getGranContribuyente());
                emisor.setAutorretenedor(empresa.getAutorretenedor());
                emisor.setResponsableIva(empresa.getResponsableIva());
            }
        } catch (Exception ex) {
            log.warn("[NC] Error cargando empresa: {}", ex.getMessage());
        }
        req.setEmisor(emisor);

        // Receptor (cliente de la venta)
        Terceros cliente = venta.getCliente();
        DianDocumentoRequestDTO.ReceptorDTO receptor = new DianDocumentoRequestDTO.ReceptorDTO();
        if (cliente != null) {
            if (cliente.getTipoIdentificacion() != null) {
                // Código DIAN (13=CC, 31=NIT...), NO el PK interno de la tabla tipoidentificacion
                receptor.setTipoIdentificacion(String.valueOf(cliente.getTipoIdentificacion().getCodigoTipoIdentificacion()));
            }
            receptor.setNumeroIdentificacion(cliente.getIdentificacion());
            receptor.setDigitoVerificacion(cliente.getDv());
            receptor.setNombre(cliente.getRazonSocial() != null && !cliente.getRazonSocial().isBlank()
                    ? cliente.getRazonSocial()
                    : (cliente.getNombre1() != null ? cliente.getNombre1() : ""));
            // Nombres/apellidos separados (Facturatech los exige en ADQ_4/ADQ_5 para persona natural)
            receptor.setNombres(((cliente.getNombre1() != null ? cliente.getNombre1() : "") + " "
                    + (cliente.getNombre2() != null ? cliente.getNombre2() : "")).trim());
            receptor.setApellidos(((cliente.getApellido1() != null ? cliente.getApellido1() : "") + " "
                    + (cliente.getApellido2() != null ? cliente.getApellido2() : "")).trim());
            receptor.setDireccion(cliente.getDireccion());
            receptor.setCorreo(cliente.getCorreo());
            if (cliente.getCiudad() != null) {
                receptor.setMunicipio(cliente.getCiudad().getMunicipio());
                int codDep = cliente.getCiudad().getCodigoDepartamento();
                int codMun = cliente.getCiudad().getCodigoMunicipio();
                receptor.setCodigoMunicipio(codMun >= 1000
                        ? String.format("%05d", codMun)
                        : String.format("%02d%03d", codDep, codMun));
            }
            if (cliente.getDepartamento() != null) {
                receptor.setDepartamento(cliente.getDepartamento().getDepartamento());
                receptor.setCodigoDepartamento(String.format("%02d",
                        cliente.getDepartamento().getCodigoDepartamento()));
            }
        }
        req.setReceptor(receptor);

        // Líneas y totales: para ANULACIÓN (concepto 2) la NC debe calcar la factura
        // COMPLETA (líneas de la venta con sus descuentos reales y los mismos totales);
        // para devolución/rebaja (1/3...) se usan los items devueltos.
        int conceptoNc = concepto != null ? concepto : 1;
        List<DianDocumentoRequestDTO.LineaDTO> lineas = new ArrayList<>();
        AtomicInteger num = new AtomicInteger(1);
        if (conceptoNc == 2 && venta.getItems() != null && !venta.getItems().isEmpty()) {
            for (com.pazzioliweb.ventasmodule.entity.DetalleVenta d : venta.getItems()) {
                DianDocumentoRequestDTO.LineaDTO l = new DianDocumentoRequestDTO.LineaDTO();
                l.setNumero(num.getAndIncrement());
                l.setCodigoProducto(d.getCodigoProducto());
                l.setDescripcion(d.getDescripcionProducto());
                l.setCantidad(d.getCantidad() != null ? d.getCantidad() : 1);
                l.setPrecioUnitario(d.getPrecioUnitario() != null ? d.getPrecioUnitario() : BigDecimal.ZERO);
                l.setDescuento(d.getDescuento() != null ? d.getDescuento() : BigDecimal.ZERO);
                l.setValorIva(d.getIva() != null ? d.getIva() : BigDecimal.ZERO);
                l.setTotalLinea(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
                lineas.add(l);
            }
            req.setLineas(lineas);
            // Totales idénticos a los de la factura anulada
            req.setBaseGravable(venta.getGravada() != null ? venta.getGravada() : BigDecimal.ZERO);
            req.setTotalIva(venta.getIva() != null ? venta.getIva() : BigDecimal.ZERO);
            req.setTotalDescuento(venta.getDescuentos() != null ? venta.getDescuentos() : BigDecimal.ZERO);
            req.setTotalFactura(venta.getTotalVenta() != null ? venta.getTotalVenta() : BigDecimal.ZERO);
        } else {
            for (DetalleDevolucion d : devolucion.getItems()) {
                DianDocumentoRequestDTO.LineaDTO l = new DianDocumentoRequestDTO.LineaDTO();
                l.setNumero(num.getAndIncrement());
                if (d.getDetalleVenta() != null) {
                    l.setCodigoProducto(d.getDetalleVenta().getCodigoProducto());
                    l.setDescripcion(d.getDetalleVenta().getDescripcionProducto());
                }
                l.setCantidad(d.getCantidadDevuelta() != null ? d.getCantidadDevuelta() : 1);
                l.setPrecioUnitario(d.getPrecioUnitario() != null ? d.getPrecioUnitario() : BigDecimal.ZERO);
                l.setDescuento(BigDecimal.ZERO);
                l.setValorIva(d.getIvaLinea() != null ? d.getIvaLinea() : BigDecimal.ZERO);
                l.setTotalLinea(d.getTotalLinea() != null ? d.getTotalLinea() : BigDecimal.ZERO);
                lineas.add(l);
            }
            req.setLineas(lineas);

            // Totales
            BigDecimal totalNeto = devolucion.getTotalNeto() != null ? devolucion.getTotalNeto() : BigDecimal.ZERO;
            BigDecimal iva       = devolucion.getIvaDevuelto() != null ? devolucion.getIvaDevuelto() : BigDecimal.ZERO;
            BigDecimal base      = devolucion.getTotalDevuelto() != null ? devolucion.getTotalDevuelto() : totalNeto.subtract(iva);
            req.setBaseGravable(base);
            req.setTotalIva(iva);
            req.setTotalDescuento(BigDecimal.ZERO);
            req.setTotalFactura(totalNeto);
        }

        return req;
    }

    /** Sigla del método de pago interno → código DIAN Tabla 5 (mismo mapeo del servicio). */
    private String mapearMedioPagoDian(String sigla) {
        if (sigla == null) return "ZZZ";
        return switch (sigla.toUpperCase()) {
            case "EF", "EFECTIVO" -> "10";
            case "TC", "TARJETA_CREDITO" -> "48";
            case "TD", "TARJETA_DEBITO" -> "49";
            case "TR", "TRANSFERENCIA" -> "47";
            default -> "ZZZ";
        };
    }
}
