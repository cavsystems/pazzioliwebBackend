package com.pazzioliweb.commonbacken.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Envío de documentos (PDF) por WhatsApp usando la Cloud API de Meta.
 *
 * MISMO PATRÓN QUE LOS EmailXService: si no hay credenciales configuradas NO rompe, devuelve
 * {@code configurado=false} y el frontend degrada al link wa.me (abre WhatsApp con el mensaje ya
 * escrito para que el usuario adjunte el PDF a mano).
 *
 * Configuración (application.properties). Mientras estén vacías, el envío automático queda apagado:
 * <pre>
 * whatsapp.cloud.token=EAAG...            # token permanente del System User de Meta
 * whatsapp.cloud.phone-number-id=123456   # Phone Number ID del número WABA (NO el número)
 * whatsapp.cloud.api-version=v21.0
 * whatsapp.cloud.default-country-code=57
 * </pre>
 *
 * LIMITACIÓN DE META, no del código: un mensaje libre (como este documento) solo se entrega si el
 * cliente escribió al número en las últimas 24 h. Fuera de esa ventana Meta exige una PLANTILLA
 * aprobada y responde error; ese error se devuelve tal cual en {@code mensaje} para que se vea en
 * la UI en vez de fallar en silencio.
 */
@Service
public class WhatsappService {

    @Value("${whatsapp.cloud.token:}")
    private String token;

    @Value("${whatsapp.cloud.phone-number-id:}")
    private String phoneNumberId;

    @Value("${whatsapp.cloud.api-version:v21.0}")
    private String apiVersion;

    /** Indicativo que se asume cuando el número llega sin él (57 = Colombia). */
    @Value("${whatsapp.cloud.default-country-code:57}")
    private String defaultCountryCode;

    private final RestTemplate rest = new RestTemplate();

    /** Resultado del envío. {@code configurado=false} → el front debe usar el link wa.me. */
    public record Resultado(boolean enviado, boolean configurado, String mensaje) {
        public static Resultado noConfigurado() {
            return new Resultado(false, false,
                    "WhatsApp automático no configurado. Se abrirá WhatsApp para enviarlo manualmente.");
        }
        public static Resultado ok(String telefono) {
            return new Resultado(true, true, "Documento enviado por WhatsApp a " + telefono);
        }
        public static Resultado error(String detalle) {
            return new Resultado(false, true, detalle);
        }
    }

    public boolean isConfigurado() {
        return token != null && !token.isBlank()
                && phoneNumberId != null && !phoneNumberId.isBlank();
    }

    /**
     * Sube el PDF a Meta y lo envía como mensaje de tipo documento.
     *
     * @param telefono      número del destinatario; se le agrega el indicativo si no lo trae
     * @param nombreArchivo nombre con el que el cliente ve el archivo (sin extensión)
     * @param caption       texto que acompaña al documento
     * @param pdf           bytes del PDF ya generado (ver HtmlPdfUtil)
     */
    public Resultado enviarDocumento(String telefono, String nombreArchivo, String caption, byte[] pdf) {
        if (!isConfigurado()) return Resultado.noConfigurado();

        String destino = normalizarTelefono(telefono);
        if (destino == null) return Resultado.error("El número de WhatsApp no es válido.");
        if (pdf == null || pdf.length == 0) return Resultado.error("No se pudo generar el PDF del documento.");

        try {
            String mediaId = subirMedia(nombreArchivo, pdf);
            if (mediaId == null) return Resultado.error("Meta no devolvió el id del archivo subido.");
            enviarMensajeDocumento(destino, mediaId, nombreArchivo, caption);
            System.out.println("[Whatsapp] Documento enviado a " + destino + ": " + nombreArchivo);
            return Resultado.ok(destino);
        } catch (Exception ex) {
            String detalle = extraerMensajeMeta(ex);
            System.out.println("[Whatsapp] Error enviando a " + destino + ": " + detalle);
            return Resultado.error(detalle);
        }
    }

    // ── Paso 1: subir el PDF a /media ──
    private String subirMedia(String nombreArchivo, byte[] pdf) {
        String url = "https://graph.facebook.com/" + apiVersion + "/" + phoneNumberId + "/media";

        ByteArrayResource archivo = new ByteArrayResource(pdf) {
            @Override
            public String getFilename() {
                return nombreArchivo + ".pdf";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("messaging_product", "whatsapp");
        body.add("type", "application/pdf");
        body.add("file", archivo);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        @SuppressWarnings("unchecked")
        Map<String, Object> resp = rest.postForObject(url, new HttpEntity<>(body, headers), Map.class);
        return resp != null && resp.get("id") != null ? String.valueOf(resp.get("id")) : null;
    }

    // ── Paso 2: enviar el mensaje de tipo documento ──
    private void enviarMensajeDocumento(String destino, String mediaId, String nombreArchivo, String caption) {
        String url = "https://graph.facebook.com/" + apiVersion + "/" + phoneNumberId + "/messages";

        Map<String, Object> documento = new LinkedHashMap<>();
        documento.put("id", mediaId);
        documento.put("filename", nombreArchivo + ".pdf");
        if (caption != null && !caption.isBlank()) documento.put("caption", caption);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", destino);
        payload.put("type", "document");
        payload.put("document", documento);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        rest.postForObject(url, new HttpEntity<>(payload, headers), Map.class);
    }

    /**
     * Deja el número como lo pide Meta: solo dígitos, con indicativo y sin el 0/+ inicial.
     * Un celular colombiano de 10 dígitos queda como 57XXXXXXXXXX.
     */
    private String normalizarTelefono(String telefono) {
        if (telefono == null) return null;
        String d = telefono.replaceAll("\\D", "");
        if (d.isEmpty()) return null;
        if (d.length() == 10) d = defaultCountryCode + d;      // celular local sin indicativo
        return d.length() >= 11 ? d : null;
    }

    /** Saca el mensaje de error real que devuelve Meta en el body, que es el que le sirve al usuario. */
    private String extraerMensajeMeta(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null) return "Error enviando por WhatsApp.";
        // Los errores de Meta llegan como {"error":{"message":"...","code":...}} dentro del mensaje.
        int i = msg.indexOf("\"message\":\"");
        if (i >= 0) {
            int ini = i + 11;
            int fin = msg.indexOf('"', ini);
            if (fin > ini) return "WhatsApp: " + msg.substring(ini, fin);
        }
        return "Error enviando por WhatsApp: " + msg;
    }
}
