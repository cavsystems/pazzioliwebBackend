package com.pazzioliweb.commonbacken.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;

/**
 * Convierte HTML (XHTML bien formado) a PDF. Se usa para adjuntar la factura/recibo al correo.
 *
 * IMPORTANTE: openhtmltopdf exige XHTML válido (etiquetas cerradas) y soporta CSS básico
 * (tablas sí; grid/flex NO). Las plantillas de correo que se pasen aquí deben usar tablas y
 * cerrar todas las etiquetas (&lt;br/&gt;, &lt;meta .../&gt;). Si el render falla, el llamador debe
 * degradar a enviar solo el HTML en el cuerpo (nunca bloquear el envío del correo).
 */
public final class HtmlPdfUtil {

    private HtmlPdfUtil() {}

    /** Renderiza el HTML a un PDF en memoria. Lanza excepción si el HTML no es válido para PDF. */
    public static byte[] htmlToPdf(String html) throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }
}
