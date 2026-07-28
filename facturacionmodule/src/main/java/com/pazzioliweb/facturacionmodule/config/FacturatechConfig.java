package com.pazzioliweb.facturacionmodule.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Configuración del proveedor tecnológico FACTURATECH.
 *
 * El Web Service de Facturatech recibe el XML_SIMPLIFICADO en base64,
 * genera el UBL 2.1, lo firma y lo envía a la DIAN. La autenticación
 * usa el NIT (usuario) y la contraseña Web Service encriptada en SHA-256.
 *
 * IMPORTANTE: la contraseña Web Service NO es la del portal Facturatech;
 * la entrega soportews@facturatech.co por solicitud.
 */
@Configuration
@ConfigurationProperties(prefix = "facturatech")
@Data
public class FacturatechConfig {

    /** 1 = Producción (pro), 2 = Pruebas/habilitación (demo). También se envía en ENC_20. */
    private int ambiente = 2;

    /** Usuario del Web Service (NIT del emisor, sin DV). */
    private String usuario;

    /** Contraseña Web Service en texto plano (se hashea con SHA-256 antes de enviar). */
    private String password;

    /** Contraseña ya encriptada en SHA-256 (alternativa a `password`; tiene prioridad si se define). */
    private String passwordSha256;

    private Url url = new Url();

    /** Intentos de consulta de estado tras subir el documento (espera de firma). */
    private int intentosEstado = 10;

    /** Milisegundos de espera entre consultas de estado. */
    private long esperaEntreIntentosMs = 2000;

    /** Timeout de conexión en ms. */
    private int connectTimeoutMs = 30000;

    /** Timeout de lectura en ms. */
    private int readTimeoutMs = 90000;

    /** Descargar la representación gráfica (PDF) de Facturatech al autorizar. */
    private boolean descargarPdf = true;

    @Data
    public static class Url {
        private String produccion = "https://ws.facturatech.co/v2/pro/index.php";
        private String demo = "https://ws.facturatech.co/v2/demo/index.php";
    }

    public String getUrlActiva() {
        return ambiente == 1 ? url.getProduccion() : url.getDemo();
    }

    /** Namespace SOAP según ambiente (urn:https://ws.facturatech.co/v2/demo/ ó /pro/). */
    public String getNamespaceActivo() {
        return ambiente == 1
                ? "urn:https://ws.facturatech.co/v2/pro/"
                : "urn:https://ws.facturatech.co/v2/demo/";
    }

    /** Devuelve la contraseña lista para enviar (SHA-256 en hexadecimal minúscula). */
    public String getPasswordHash() {
        if (passwordSha256 != null && !passwordSha256.isBlank()) {
            return passwordSha256.trim();
        }
        if (password == null || password.isBlank()) {
            return "";
        }
        return sha256Hex(password);
    }

    public boolean credencialesConfiguradas() {
        return usuario != null && !usuario.isBlank() && !getPasswordHash().isBlank();
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular SHA-256 de la contraseña Facturatech", e);
        }
    }
}
