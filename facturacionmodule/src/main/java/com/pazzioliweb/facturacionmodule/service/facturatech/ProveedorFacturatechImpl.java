package com.pazzioliweb.facturacionmodule.service.facturatech;

import com.pazzioliweb.facturacionmodule.config.FacturatechConfig;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoRequestDTO;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoResponseDTO;
import com.pazzioliweb.facturacionmodule.service.ProveedorFacturacionElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Proveedor de facturación electrónica vía FACTURATECH (proveedor tecnológico).
 *
 * Flujo:
 *  1. Genera el XML_SIMPLIFICADO (insumo Facturatech, no UBL) y lo codifica en base64
 *  2. FtechAction.uploadInvoiceFile → transaccionID
 *  3. FtechAction.documentStatusFile hasta SIGNED_XML (Facturatech firma y envía a DIAN)
 *  4. FtechAction.getCUFEFile / downloadXMLFile / downloadPDFFile / getQRFile
 *
 * A diferencia del envío directo a la DIAN, aquí NO se necesita certificado
 * digital propio ni cálculo local de CUFE: Facturatech firma y calcula todo.
 *
 * Se activa por defecto; para volver al envío directo DIAN configurar
 * `facturacion.proveedor=dian` en application.properties.
 */
@Service
@Primary
@ConditionalOnProperty(name = "facturacion.proveedor", havingValue = "facturatech", matchIfMissing = true)
public class ProveedorFacturatechImpl implements ProveedorFacturacionElectronica {

    private static final Logger log = LoggerFactory.getLogger(ProveedorFacturatechImpl.class);

    public static final String ESTADO_AUTORIZADA = "AUTORIZADA";
    public static final String ESTADO_RECHAZADA = "RECHAZADA";
    public static final String ESTADO_EN_PROCESO = "EN_PROCESO";
    public static final String ESTADO_SIMULADA = "SIMULADA";

    private final FacturatechConfig config;
    private final FacturatechXmlGenerator xmlGenerator;
    private final FacturatechSoapClient soapClient;

    public ProveedorFacturatechImpl(FacturatechConfig config,
                                     FacturatechXmlGenerator xmlGenerator,
                                     FacturatechSoapClient soapClient) {
        this.config = config;
        this.xmlGenerator = xmlGenerator;
        this.soapClient = soapClient;
    }

    @Override
    public DianDocumentoResponseDTO enviarFactura(DianDocumentoRequestDTO request) {
        String tipoDoc = request.getTipoDocumento() != null ? request.getTipoDocumento() : "01";
        String nombreDoc = switch (tipoDoc) {
            case "91" -> "Nota Crédito";
            case "92" -> "Nota Débito";
            case "20" -> "Tiquete POS";
            case "05" -> "Documento Soporte";
            case "95" -> "Nota de Ajuste al Documento Soporte";
            default -> "Factura Electrónica";
        };
        String prefijo = request.getPrefijo() != null ? request.getPrefijo() : "";
        String folio = request.getConsecutivo() != null ? String.valueOf(request.getConsecutivo()) : "";
        String numeroDoc = prefijo + folio;

        log.info("══════ INICIO {} vía FACTURATECH ══════", nombreDoc);
        log.info("Documento: {} - {} | Ambiente: {} ({})", tipoDoc, numeroDoc,
                config.getAmbiente(), config.getAmbiente() == 1 ? "PRODUCCIÓN" : "DEMO");

        DianDocumentoResponseDTO response = new DianDocumentoResponseDTO();
        response.setNumero(numeroDoc);
        response.setFechaValidacion(LocalDateTime.now());

        // El Tiquete POS usa un Web Service aparte de Facturatech (ws-pos) cuyo insumo
        // no está en el material entregado. El Documento Soporte (05) y su nota de
        // ajuste (95) sí van por este mismo WS con el insumo DOCUMENTO_SOPORTE.
        if ("20".equals(tipoDoc)) {
            response.setExitoso(false);
            response.setEstadoDian(ESTADO_RECHAZADA);
            response.setMensajeDian(nombreDoc + " requiere el Web Service POS de Facturatech, " +
                    "aún no habilitado en el sistema. Contacte a soporte.");
            log.warn("[Facturatech] {}", response.getMensajeDian());
            return response;
        }

        // ── SET de pruebas C1: el WS demo solo acepta documentos del NIT demo de
        // Facturatech (ENC_2 / EMI_2 / EMI_22). Se sobreescribe la identificación del
        // emisor SOLO en el XML; la factura guardada en BD conserva los datos reales.
        if (config.getSetPruebas().isHabilitado()) {
            if (request.getEmisor() == null) {
                request.setEmisor(new DianDocumentoRequestDTO.EmisorDTO());
            }
            DianDocumentoRequestDTO.EmisorDTO e = request.getEmisor();
            e.setNumeroIdentificacion(config.getSetPruebas().getNit());
            e.setDigitoVerificacion(config.getSetPruebas().getDv());
            e.setTipoIdentificacion("31");
            if (config.getSetPruebas().getRazonSocial() != null && !config.getSetPruebas().getRazonSocial().isBlank()) {
                e.setRazonSocial(config.getSetPruebas().getRazonSocial());
                e.setNombreComercial(config.getSetPruebas().getRazonSocial());
            }
            log.warn("⚠️ SET DE PRUEBAS C1 ACTIVO: el documento se emite con el NIT demo {} ({}). " +
                    "Desactive facturatech.set-pruebas.habilitado en producción.",
                    config.getSetPruebas().getNit(), config.getSetPruebas().getRazonSocial());
        }

        // Modo simulación si no hay credenciales configuradas (equivalente al modo sin certificado)
        if (!config.credencialesConfiguradas()) {
            log.warn("⚠️ MODO SIMULACIÓN: faltan credenciales Facturatech (facturatech.usuario / facturatech.password)");
            String xml = xmlGenerator.generarXml(request);
            response.setExitoso(true);
            response.setEstadoDian(ESTADO_SIMULADA);
            response.setMensajeDian(nombreDoc + " generada en modo simulación (sin credenciales Facturatech)");
            response.setCufe("SIMULADO-" + numeroDoc + "-" + System.currentTimeMillis());
            response.setXmlFirmado(Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8)));
            response.setQrData("https://catalogo-vpfe.dian.gov.co/document/searchqr?documentkey=" + response.getCufe());
            return response;
        }

        try {
            // 1. Generar XML_SIMPLIFICADO y codificar en base64
            log.info("Paso 1: Generando XML_SIMPLIFICADO Facturatech...");
            String xml = xmlGenerator.generarXml(request);
            String xmlBase64 = Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8));
            log.info("XML generado ({} caracteres)", xml.length());
            log.debug("[Facturatech] XML insumo:\n{}", xml);

            // 2. Subir a Facturatech
            log.info("Paso 2: Enviando a Facturatech (uploadInvoiceFile)...");
            FacturatechSoapClient.FacturatechResponse upload = soapClient.uploadInvoiceFile(xmlBase64);
            log.info("Respuesta upload → code: {}, success: {}, transaccionID: {}, error: {}",
                    upload.getCode(), upload.getSuccess(), upload.getTransaccionId(), upload.getError());

            response.setTransaccionId(upload.getTransaccionId());

            if (!upload.isExitoso()) {
                response.setExitoso(false);
                response.setEstadoDian(ESTADO_RECHAZADA);
                response.setMensajeDian("Facturatech rechazó el documento: " + upload.getMensaje());
                log.error("══════ RESULTADO: RECHAZADA - {} ══════", upload.getMensaje());
                return response;
            }

            // 3. Esperar la firma (documentStatusFile con el transaccionID)
            String estadoFinal = esperarFirma(upload.getTransaccionId());

            if ("SIGNED_XML".equals(estadoFinal)) {
                completarDocumentoFirmado(response, prefijo, folio);
                response.setExitoso(true);
                response.setEstadoDian(ESTADO_AUTORIZADA);
                response.setMensajeDian(nombreDoc + " " + numeroDoc + " firmada y autorizada"
                        + (upload.getSuccess() != null ? " — " + upload.getSuccess() : ""));
            } else if ("PROCESSING".equals(estadoFinal)) {
                // Sigue en proceso: NO es rechazo. Se puede re-consultar luego por prefijo+folio.
                response.setExitoso(false);
                response.setEstadoDian(ESTADO_EN_PROCESO);
                response.setMensajeDian(nombreDoc + " " + numeroDoc +
                        " en proceso de validación (transacción " + upload.getTransaccionId() +
                        "). Use 'consultar estado' para actualizar.");
            } else {
                response.setExitoso(false);
                response.setEstadoDian(ESTADO_RECHAZADA);
                response.setMensajeDian("Estado inesperado de Facturatech: " + estadoFinal);
            }

            log.info("══════ RESULTADO: {} - {} ══════", response.getEstadoDian(), response.getMensajeDian());

        } catch (Exception e) {
            log.error("Error en facturación electrónica Facturatech: {}", e.getMessage(), e);
            response.setExitoso(false);
            response.setEstadoDian(ESTADO_RECHAZADA);
            response.setMensajeDian("Error: " + e.getMessage());
        }

        return response;
    }

    /**
     * Facturatech no soporta consulta por CUFE; la trazabilidad es por
     * transacción o por prefijo+folio. Ver {@link #consultarEstadoDocumento}.
     */
    @Override
    public DianDocumentoResponseDTO consultarEstado(String cufe) {
        DianDocumentoResponseDTO response = new DianDocumentoResponseDTO();
        response.setExitoso(false);
        response.setCufe(cufe);
        response.setMensajeDian("Facturatech no permite consultar por CUFE; " +
                "use la consulta por prefijo y folio del documento.");
        return response;
    }

    /**
     * Consulta el estado real de un documento por prefijo + folio:
     * si Facturatech ya tiene el CUFE, el documento está firmado/autorizado.
     * Descarga también XML, PDF y QR para completar la factura local.
     */
    @Override
    public DianDocumentoResponseDTO consultarEstadoDocumento(String prefijo, Integer folio) {
        DianDocumentoResponseDTO response = new DianDocumentoResponseDTO();
        response.setNumero((prefijo != null ? prefijo : "") + (folio != null ? folio : ""));
        response.setFechaValidacion(LocalDateTime.now());

        if (!config.credencialesConfiguradas()) {
            response.setExitoso(false);
            response.setMensajeDian("Credenciales Facturatech no configuradas");
            return response;
        }

        String folioStr = folio != null ? String.valueOf(folio) : "";
        log.info("[Facturatech] Consultando estado de {}{}...", prefijo, folioStr);
        try {
            FacturatechSoapClient.FacturatechResponse cufeResp = soapClient.getCUFEFile(prefijo, folioStr);
            if (cufeResp.isExitoso() && cufeResp.getResourceData() != null) {
                completarDocumentoFirmado(response, prefijo, folioStr);
                response.setExitoso(true);
                response.setEstadoDian(ESTADO_AUTORIZADA);
                response.setMensajeDian("Documento firmado y autorizado");
            } else {
                response.setExitoso(false);
                response.setEstadoDian(ESTADO_EN_PROCESO);
                response.setMensajeDian("Documento aún no firmado en Facturatech: " + cufeResp.getMensaje());
            }
        } catch (Exception e) {
            log.error("[Facturatech] Error consultando estado: {}", e.getMessage(), e);
            response.setExitoso(false);
            response.setMensajeDian("Error consultando: " + e.getMessage());
        }
        return response;
    }

    // ══════════════════════════════════════════════════════════
    //  Internos
    // ══════════════════════════════════════════════════════════

    /** Consulta documentStatusFile hasta SIGNED_XML o agotar los intentos configurados. */
    private String esperarFirma(String transaccionId) {
        if (transaccionId == null || transaccionId.isBlank()) {
            return "PROCESSING";
        }
        String ultimo = "PROCESSING";
        for (int intento = 1; intento <= config.getIntentosEstado(); intento++) {
            FacturatechSoapClient.FacturatechResponse status = soapClient.documentStatusFile(transaccionId);
            log.info("Paso 3: Estado intento {}/{} → code: {}, status: {}",
                    intento, config.getIntentosEstado(), status.getCode(), status.getStatus());

            if (status.getStatus() != null) {
                ultimo = status.getStatus();
                if ("SIGNED_XML".equalsIgnoreCase(ultimo)) {
                    return "SIGNED_XML";
                }
            } else if (!status.isExitoso()) {
                // 404 = transacción/consulta inválida: no seguir insistiendo
                log.warn("[Facturatech] documentStatusFile falló: {}", status.getMensaje());
                return "ERROR: " + status.getMensaje();
            }

            try {
                Thread.sleep(config.getEsperaEntreIntentosMs());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return ultimo;
            }
        }
        return ultimo;
    }

    /** Descarga CUFE, XML firmado, PDF y QR del documento ya firmado. */
    private void completarDocumentoFirmado(DianDocumentoResponseDTO response, String prefijo, String folio) {
        log.info("Paso 4: Descargando CUFE/XML/PDF/QR de {}{}...", prefijo, folio);

        FacturatechSoapClient.FacturatechResponse cufeResp = soapClient.getCUFEFile(prefijo, folio);
        if (cufeResp.isExitoso()) {
            response.setCufe(cufeResp.getResourceData());
        } else {
            log.warn("[Facturatech] No se pudo obtener el CUFE: {}", cufeResp.getMensaje());
        }

        FacturatechSoapClient.FacturatechResponse xmlResp = soapClient.downloadXMLFile(prefijo, folio);
        if (xmlResp.isExitoso()) {
            response.setXmlFirmado(xmlResp.getResourceData()); // base64
        } else {
            log.warn("[Facturatech] No se pudo descargar el XML firmado: {}", xmlResp.getMensaje());
        }

        if (config.isDescargarPdf()) {
            FacturatechSoapClient.FacturatechResponse pdfResp = soapClient.downloadPDFFile(prefijo, folio);
            if (pdfResp.isExitoso()) {
                response.setPdfBase64(pdfResp.getResourceData()); // base64
            } else {
                log.warn("[Facturatech] No se pudo descargar el PDF: {}", pdfResp.getMensaje());
            }
        }

        FacturatechSoapClient.FacturatechResponse qrResp = soapClient.getQRFile(prefijo, folio);
        if (qrResp.isExitoso() && qrResp.getResourceData() != null) {
            response.setQrData(qrResp.getResourceData());
        } else if (response.getCufe() != null) {
            response.setQrData("https://catalogo-vpfe.dian.gov.co/document/searchqr?documentkey=" + response.getCufe());
        }

        response.setFechaValidacion(LocalDateTime.now());
    }
}
