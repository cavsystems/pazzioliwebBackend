package com.pazzioliweb.facturacionmodule.service.facturatech;

import com.pazzioliweb.facturacionmodule.config.FacturatechConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Cliente SOAP del Web Service de FACTURATECH (Factura Electrónica UBL 2.1).
 *
 * Métodos según "Manual de usuario - Documentación Web Service UBL 2.1 Facturatech":
 *  - FtechAction.uploadInvoiceFile   → sube el XML_SIMPLIFICADO (base64), devuelve transaccionID
 *  - FtechAction.documentStatusFile  → estado por transaccionID (SIGNED_XML / PROCESSING)
 *  - FtechAction.downloadXMLFile     → XML UBL firmado (base64) por prefijo+folio
 *  - FtechAction.downloadPDFFile     → representación gráfica PDF (base64) por prefijo+folio
 *  - FtechAction.getCUFEFile         → CUFE del documento firmado por prefijo+folio
 *  - FtechAction.getQRFile           → datos del QR por prefijo+folio
 *
 * Respuestas: code (200/201 éxito, 404/405/409 error), success, error y
 * según el método: transaccionID, status o resourceData.
 */
@Component
public class FacturatechSoapClient {

    private static final Logger log = LoggerFactory.getLogger(FacturatechSoapClient.class);

    private final FacturatechConfig config;

    public FacturatechSoapClient(FacturatechConfig config) {
        this.config = config;
    }

    // ══════════════════════════════════════════════════════════
    //  Métodos públicos
    // ══════════════════════════════════════════════════════════

    /** Sube el XML_SIMPLIFICADO en base64. Devuelve transaccionID si fue aceptado. */
    public FacturatechResponse uploadInvoiceFile(String xmlBase64) {
        String body = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <FtechAction.uploadInvoiceFileRequest>
                      <username>%s</username>
                      <password>%s</password>
                      <xmlBase64>%s</xmlBase64>
                    </FtechAction.uploadInvoiceFileRequest>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(esc(config.getUsuario()), esc(config.getPasswordHash()), xmlBase64);
        return invocar("FtechAction.uploadInvoiceFile", body);
    }

    /** Consulta el estado del documento por transaccionID (SIGNED_XML / PROCESSING). */
    public FacturatechResponse documentStatusFile(String transaccionId) {
        String body = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <FtechAction.documentStatusFileRequest>
                      <username>%s</username>
                      <password>%s</password>
                      <transaccionID>%s</transaccionID>
                    </FtechAction.documentStatusFileRequest>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(esc(config.getUsuario()), esc(config.getPasswordHash()), esc(transaccionId));
        return invocar("FtechAction.documentStatusFile", body);
    }

    /** Descarga el XML UBL 2.1 firmado (base64) de un documento por prefijo + folio. */
    public FacturatechResponse downloadXMLFile(String prefijo, String folio) {
        return invocarPorFolio("FtechAction.downloadXMLFile", prefijo, folio);
    }

    /** Descarga la representación gráfica PDF (base64) de un documento por prefijo + folio. */
    public FacturatechResponse downloadPDFFile(String prefijo, String folio) {
        return invocarPorFolio("FtechAction.downloadPDFFile", prefijo, folio);
    }

    /** Obtiene el CUFE/CUDE de un documento firmado por prefijo + folio. */
    public FacturatechResponse getCUFEFile(String prefijo, String folio) {
        return invocarPorFolio("FtechAction.getCUFEFile", prefijo, folio);
    }

    /** Obtiene los datos que componen el QR de un documento firmado por prefijo + folio. */
    public FacturatechResponse getQRFile(String prefijo, String folio) {
        return invocarPorFolio("FtechAction.getQRFile", prefijo, folio);
    }

    // ══════════════════════════════════════════════════════════
    //  Internos
    // ══════════════════════════════════════════════════════════

    private FacturatechResponse invocarPorFolio(String metodo, String prefijo, String folio) {
        String body = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <%sRequest>
                      <username>%s</username>
                      <password>%s</password>
                      <prefijo>%s</prefijo>
                      <folio>%s</folio>
                    </%sRequest>
                  </soap:Body>
                </soap:Envelope>
                """.formatted(metodo, esc(config.getUsuario()), esc(config.getPasswordHash()),
                        esc(prefijo), esc(folio), metodo);
        return invocar(metodo, body);
    }

    private FacturatechResponse invocar(String metodo, String soapBody) {
        FacturatechResponse resp = new FacturatechResponse();
        resp.setMetodo(metodo);
        try {
            String soapAction = config.getNamespaceActivo() + "#" + metodo;
            String responseXml = enviarSoap(config.getUrlActiva(), soapAction, soapBody);
            parsear(responseXml, resp);
        } catch (Exception e) {
            log.error("[Facturatech] Error invocando {}: {}", metodo, e.getMessage());
            resp.setCode("ERROR");
            resp.setError("Error de comunicación con Facturatech: " + e.getMessage());
        }
        return resp;
    }

    private String enviarSoap(String urlStr, String soapAction, String soapBody) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        // SOAP 1.1: Content-Type text/xml + header SOAPAction
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"" + soapAction + "\"");
        conn.setDoOutput(true);
        conn.setConnectTimeout(config.getConnectTimeoutMs());
        conn.setReadTimeout(config.getReadTimeoutMs());

        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode >= 200 && responseCode < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        if (is != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line).append('\n');
                }
            }
        }
        conn.disconnect();
        return response.toString();
    }

    private void parsear(String responseXml, FacturatechResponse resp) {
        resp.setResponseXml(responseXml);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            // Protección XXE
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document doc = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(responseXml)));

            resp.setCode(getTagValue(doc, "code"));
            resp.setSuccess(getTagValue(doc, "success"));
            resp.setError(getTagValue(doc, "error"));
            resp.setTransaccionId(getTagValue(doc, "transaccionID"));
            resp.setStatus(getTagValue(doc, "status"));
            resp.setResourceData(getTagValue(doc, "resourceData"));

            // Fallo SOAP genérico (faultstring) sin estructura de respuesta
            if (resp.getCode() == null) {
                String fault = getTagValue(doc, "faultstring");
                if (fault != null) {
                    resp.setCode("FAULT");
                    resp.setError(fault);
                }
            }
        } catch (Exception e) {
            resp.setCode("ERROR");
            resp.setError("Error parseando respuesta Facturatech: " + e.getMessage());
        }
    }

    private String getTagValue(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            nodes = doc.getElementsByTagNameNS("*", tagName);
        }
        if (nodes.getLength() > 0 && nodes.item(0).getTextContent() != null) {
            String value = nodes.item(0).getTextContent().trim();
            return value.isEmpty() ? null : value;
        }
        return null;
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ── DTO de respuesta ──
    public static class FacturatechResponse {
        private String metodo;
        private String code;          // 200/201 éxito · 404/405/409 error · ERROR/FAULT local
        private String success;       // mensaje descriptivo
        private String error;         // detalle del problema
        private String transaccionId; // uploadInvoiceFile
        private String status;        // documentStatusFile: SIGNED_XML | PROCESSING
        private String resourceData;  // downloadXML/PDF (base64), getCUFE (cufe), getQR (texto)
        private String responseXml;

        /** true si el WS respondió 200 ó 201. */
        public boolean isExitoso() {
            return "200".equals(code) || "201".equals(code);
        }

        /** Mensaje más significativo disponible (error > success > code). */
        public String getMensaje() {
            if (error != null && !error.isBlank()) return error;
            if (success != null && !success.isBlank()) return success;
            return "Código de respuesta: " + code;
        }

        public String getMetodo() { return metodo; }
        public void setMetodo(String metodo) { this.metodo = metodo; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getSuccess() { return success; }
        public void setSuccess(String success) { this.success = success; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getTransaccionId() { return transaccionId; }
        public void setTransaccionId(String transaccionId) { this.transaccionId = transaccionId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getResourceData() { return resourceData; }
        public void setResourceData(String resourceData) { this.resourceData = resourceData; }
        public String getResponseXml() { return responseXml; }
        public void setResponseXml(String responseXml) { this.responseXml = responseXml; }
    }
}
