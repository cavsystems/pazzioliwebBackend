package com.pazzioliweb.facturacionmodule.service.dian;

import com.pazzioliweb.facturacionmodule.config.DianConfig;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Cliente SOAP para enviar documentos electrónicos al Web Service de la DIAN.
 * Soporta:
 * - SendBillSync (envío sincrónico en producción)
 * - SendTestSetAsync (envío en ambiente de habilitación/pruebas)
 * - GetStatus (consulta de estado por CUFE)
 */
@Component
public class DianSoapClient {

    private final DianConfig dianConfig;

    public DianSoapClient(DianConfig dianConfig) {
        this.dianConfig = dianConfig;
    }

    /**
     * Envía una factura electrónica a la DIAN.
     *
     * @param xmlFirmado XML UBL 2.1 firmado digitalmente
     * @param fileName   nombre del archivo (ej: "fv09000001.xml")
     * @return respuesta SOAP de la DIAN como String
     */
    public DianSoapResponse enviarDocumento(String xmlFirmado, String fileName) {
        try {
            // 1. Comprimir XML en ZIP y codificar en Base64
            String zipBase64 = comprimirYCodificar(xmlFirmado, fileName);

            // 2. Construir envelope SOAP según ambiente
            String soapAction;
            String soapBody;

            if (dianConfig.getAmbiente() == 2) {
                // Ambiente de pruebas - SendTestSetAsync
                soapAction = "http://wcf.dian.colombia/IWcfDianCustomerServices/SendTestSetAsync";
                soapBody = buildSendTestSetAsyncEnvelope(fileName, zipBase64);
            } else {
                // Producción - SendBillSync
                soapAction = "http://wcf.dian.colombia/IWcfDianCustomerServices/SendBillSync";
                soapBody = buildSendBillSyncEnvelope(fileName, zipBase64);
            }

            // 3. Enviar por HTTP
            String responseXml = enviarSoap(dianConfig.getUrlActiva(), soapAction, soapBody);

            // 4. Parsear respuesta
            return parsearRespuesta(responseXml);

        } catch (Exception e) {
            DianSoapResponse errorResponse = new DianSoapResponse();
            errorResponse.setExitoso(false);
            errorResponse.setMensaje("Error enviando a DIAN: " + e.getMessage());
            errorResponse.setCodigoEstado("ERROR");
            return errorResponse;
        }
    }

    /**
     * Consulta el estado de un documento por CUFE.
     */
    public DianSoapResponse consultarEstado(String cufe) {
        try {
            String soapAction = "http://wcf.dian.colombia/IWcfDianCustomerServices/GetStatus";
            String soapBody = buildGetStatusEnvelope(cufe);
            String responseXml = enviarSoap(dianConfig.getUrlActiva(), soapAction, soapBody);
            return parsearRespuesta(responseXml);
        } catch (Exception e) {
            DianSoapResponse errorResponse = new DianSoapResponse();
            errorResponse.setExitoso(false);
            errorResponse.setMensaje("Error consultando estado DIAN: " + e.getMessage());
            return errorResponse;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  Métodos privados
    // ══════════════════════════════════════════════════════════

    private String comprimirYCodificar(String xml, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry(fileName);
            zos.putNextEntry(entry);
            byte[] xmlBytes = xml.getBytes(StandardCharsets.UTF_8);
            zos.write(xmlBytes, 0, xmlBytes.length);
            zos.closeEntry();
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private String buildSendBillSyncEnvelope(String fileName, String zipBase64) {
        return """
                <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
                               xmlns:wcf="http://wcf.dian.colombia">
                    <soap:Header/>
                    <soap:Body>
                        <wcf:SendBillSync>
                            <wcf:fileName>%s.zip</wcf:fileName>
                            <wcf:contentFile>%s</wcf:contentFile>
                        </wcf:SendBillSync>
                    </soap:Body>
                </soap:Envelope>
                """.formatted(fileName.replace(".xml", ""), zipBase64);
    }

    private String buildSendTestSetAsyncEnvelope(String fileName, String zipBase64) {
        return """
                <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
                               xmlns:wcf="http://wcf.dian.colombia">
                    <soap:Header/>
                    <soap:Body>
                        <wcf:SendTestSetAsync>
                            <wcf:fileName>%s.zip</wcf:fileName>
                            <wcf:contentFile>%s</wcf:contentFile>
                            <wcf:testSetId>%s</wcf:testSetId>
                        </wcf:SendTestSetAsync>
                    </soap:Body>
                </soap:Envelope>
                """.formatted(
                fileName.replace(".xml", ""),
                zipBase64,
                dianConfig.getTestSetId() != null ? dianConfig.getTestSetId() : ""
        );
    }

    private String buildGetStatusEnvelope(String cufe) {
        return """
                <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
                               xmlns:wcf="http://wcf.dian.colombia">
                    <soap:Header/>
                    <soap:Body>
                        <wcf:GetStatus>
                            <wcf:trackId>%s</wcf:trackId>
                        </wcf:GetStatus>
                    </soap:Body>
                </soap:Envelope>
                """.formatted(cufe);
    }

    private String enviarSoap(String urlStr, String soapAction, String soapBody) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8;action=\"" + soapAction + "\"");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode >= 200 && responseCode < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        conn.disconnect();

        return response.toString();
    }

    private DianSoapResponse parsearRespuesta(String responseXml) {
        DianSoapResponse response = new DianSoapResponse();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(responseXml)));

            // Buscar StatusCode
            String statusCode = getTagValue(doc, "StatusCode");
            String statusDescription = getTagValue(doc, "StatusDescription");
            String statusMessage = getTagValue(doc, "StatusMessage");
            String isValid = getTagValue(doc, "IsValid");
            String xmlBase64 = getTagValue(doc, "XmlBase64Bytes");
            String xmlFileName = getTagValue(doc, "XmlFileName");

            response.setCodigoEstado(statusCode);
            response.setDescripcionEstado(statusDescription);
            response.setMensaje(statusMessage != null ? statusMessage : statusDescription);
            response.setXmlRespuestaBase64(xmlBase64);
            response.setResponseXml(responseXml);

            // IsValid o StatusCode "00" = éxito
            boolean exitoso = "true".equalsIgnoreCase(isValid) || "00".equals(statusCode);
            response.setExitoso(exitoso);

        } catch (Exception e) {
            response.setExitoso(false);
            response.setMensaje("Error parseando respuesta DIAN: " + e.getMessage());
            response.setResponseXml(responseXml);
        }
        return response;
    }

    private String getTagValue(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            // Intentar con namespace
            nodes = doc.getElementsByTagNameNS("*", tagName);
        }
        if (nodes.getLength() > 0 && nodes.item(0).getTextContent() != null) {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }

    // ── DTO de respuesta SOAP ──
    public static class DianSoapResponse {
        private boolean exitoso;
        private String codigoEstado;
        private String descripcionEstado;
        private String mensaje;
        private String xmlRespuestaBase64;
        private String responseXml;

        public boolean isExitoso() { return exitoso; }
        public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
        public String getCodigoEstado() { return codigoEstado; }
        public void setCodigoEstado(String codigoEstado) { this.codigoEstado = codigoEstado; }
        public String getDescripcionEstado() { return descripcionEstado; }
        public void setDescripcionEstado(String descripcionEstado) { this.descripcionEstado = descripcionEstado; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public String getXmlRespuestaBase64() { return xmlRespuestaBase64; }
        public void setXmlRespuestaBase64(String xmlRespuestaBase64) { this.xmlRespuestaBase64 = xmlRespuestaBase64; }
        public String getResponseXml() { return responseXml; }
        public void setResponseXml(String responseXml) { this.responseXml = responseXml; }
    }
}

