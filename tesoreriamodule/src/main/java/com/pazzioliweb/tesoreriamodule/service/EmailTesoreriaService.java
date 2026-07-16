package com.pazzioliweb.tesoreriamodule.service;

import com.pazzioliweb.commonbacken.util.HtmlPdfUtil;
import com.pazzioliweb.tesoreriamodule.dtos.ReciboCajaResponseDTO;
import com.pazzioliweb.tesoreriamodule.dtos.ComprobanteEgresoResponseDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Envío por correo del Recibo de Caja (RC) y del Comprobante de Egreso (CE), con el documento en
 * PDF adjunto (best-effort: si el PDF falla, se envía igual el HTML en el cuerpo). Mismo patrón que
 * EmailVentaService: JavaMailSender opcional (si SMTP no está configurado, devuelve false sin romper).
 */
@Service
public class EmailTesoreriaService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@pazzioli.com}")
    private String remitente;

    public boolean enviarRecibo(ReciboCajaResponseDTO rc, String correo) {
        String html = buildHtml("Recibo de caja", nvl(rc.getNumeroDocumento(), "RC-" + rc.getConsecutivo()),
                rc.getTerceroNombre(), rc.getTerceroNit(), String.valueOf(rc.getFecha()),
                Boolean.TRUE.equals(rc.getConceptoAbierto()) ? rc.getConcepto() : null, rc.getMetodoPago(),
                rc.getSubtotal(), rc.getRetefuente(), rc.getReteiva(), rc.getReteica(), rc.getDescuento(), rc.getTotal());
        return enviar(correo, "Recibo de caja " + nvl(rc.getNumeroDocumento(), ""),
                "Recibo-" + nvl(rc.getNumeroDocumento(), String.valueOf(rc.getConsecutivo())), html);
    }

    public boolean enviarEgreso(ComprobanteEgresoResponseDTO ce, String correo) {
        String html = buildHtml("Comprobante de egreso", nvl(ce.getNumeroDocumento(), "CE-" + ce.getConsecutivo()),
                ce.getTerceroNombre(), ce.getTerceroNit(), String.valueOf(ce.getFecha()),
                Boolean.TRUE.equals(ce.getConceptoAbierto()) ? ce.getConcepto() : null, ce.getMetodoPago(),
                ce.getSubtotal(), ce.getRetefuente(), ce.getReteiva(), ce.getReteica(), ce.getDescuento(), ce.getTotal());
        return enviar(correo, "Comprobante de egreso " + nvl(ce.getNumeroDocumento(), ""),
                "Egreso-" + nvl(ce.getNumeroDocumento(), String.valueOf(ce.getConsecutivo())), html);
    }

    // ── Envío común ──
    private boolean enviar(String correo, String asunto, String nombreArchivo, String html) {
        if (mailSender == null) {
            System.out.println("[EmailTesoreria] JavaMailSender no configurado. Configure spring.mail.* en application.properties.");
            return false;
        }
        if (correo == null || correo.isBlank()) {
            System.out.println("[EmailTesoreria] No se proporcionó correo.");
            return false;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(correo);
            helper.setSubject(asunto);
            helper.setText(html, true);
            try {
                byte[] pdf = HtmlPdfUtil.htmlToPdf(html);
                helper.addAttachment(nombreArchivo + ".pdf", new ByteArrayResource(pdf), "application/pdf");
            } catch (Throwable pex) {
                System.out.println("[EmailTesoreria] No se pudo generar el PDF (se envía solo HTML): " + pex.getMessage());
            }
            mailSender.send(message);
            System.out.println("[EmailTesoreria] Documento enviado a " + correo + ": " + asunto);
            return true;
        } catch (Exception ex) {
            System.out.println("[EmailTesoreria] Error enviando email: " + ex.getMessage());
            throw new RuntimeException("Error enviando documento: " + ex.getMessage(), ex);
        }
    }

    // ── Plantilla HTML (table-based, XHTML válido → sirve para correo y PDF) ──
    private String buildHtml(String titulo, String numero, String terceroNombre, String terceroNit,
                             String fecha, String concepto, String metodoPago,
                             BigDecimal subtotal, BigDecimal rf, BigDecimal rv, BigDecimal rc,
                             BigDecimal descuento, BigDecimal total) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\" /><style>")
          .append("body{font-family:Arial,sans-serif;color:#333;margin:0;padding:16px;background:#f8f9fa}")
          .append(".container{max-width:640px;margin:0 auto;background:#fff;border:1px solid #e9ecef;border-radius:8px}")
          .append(".header{background:#f09700;color:#fff;padding:20px 24px}")
          .append(".header h2{margin:0;font-size:20px}.header p{margin:4px 0 0;font-size:13px}")
          .append(".body{padding:20px 24px}")
          .append(".info{width:100%;border-collapse:collapse;margin-bottom:16px}")
          .append(".info td{padding:6px 8px;font-size:13px;vertical-align:top;width:50%}")
          .append(".info .label{color:#6c757d;font-weight:bold;font-size:11px;text-transform:uppercase}")
          .append(".tot{width:100%;border-collapse:collapse;margin-top:8px;border-top:2px solid #f09700;font-size:13px}")
          .append(".tot td{padding:5px 8px}.tot .fin td{font-size:17px;font-weight:bold;color:#f09700}")
          .append(".footer{background:#f8f9fa;padding:14px 24px;font-size:11px;color:#868e96;text-align:center;border-top:1px solid #e9ecef}")
          .append("</style></head><body><div class=\"container\">");

        sb.append("<div class=\"header\"><h2>").append(titulo).append("</h2><p>").append(nvl(numero)).append("</p></div>");
        sb.append("<div class=\"body\"><table class=\"info\">")
          .append("<tr>").append(infoTd("Tercero", nvl(terceroNombre, "-"))).append(infoTd("NIT/CC", nvl(terceroNit, "-"))).append("</tr>")
          .append("<tr>").append(infoTd("Fecha", nvl(fecha, "-"))).append(infoTd("Forma de pago", nvl(metodoPago, "-"))).append("</tr>");
        if (concepto != null && !concepto.isBlank()) {
            sb.append("<tr>").append("<td colspan=\"2\"><div class=\"label\">Concepto</div><div>").append(concepto).append("</div></td></tr>");
        }
        sb.append("</table>");

        sb.append("<table class=\"tot\">")
          .append(totRow("Subtotal", subtotal, false));
        if (bd(descuento).compareTo(BigDecimal.ZERO) > 0) sb.append(totRow("Descuento", descuento.negate(), false));
        if (bd(rf).compareTo(BigDecimal.ZERO) > 0) sb.append(totRow("Retefuente", rf.negate(), false));
        if (bd(rv).compareTo(BigDecimal.ZERO) > 0) sb.append(totRow("Rete IVA", rv.negate(), false));
        if (bd(rc).compareTo(BigDecimal.ZERO) > 0) sb.append(totRow("Rete ICA", rc.negate(), false));
        sb.append(totRow("TOTAL", total, true)).append("</table>");

        sb.append("</div><div class=\"footer\">Comprobante generado por Pazzioli WEB.</div></div></body></html>");
        return sb.toString();
    }

    private String infoTd(String label, String value) {
        return "<td><div class=\"label\">" + label + "</div><div>" + nvl(value) + "</div></td>";
    }
    private String totRow(String label, BigDecimal value, boolean fin) {
        String cls = fin ? " class=\"fin\"" : "";
        return "<tr" + cls + "><td>" + label + "</td><td style=\"text-align:right\">" + fmt(value) + "</td></tr>";
    }
    private String nvl(String s) { return s != null ? s : ""; }
    private String nvl(String s, String def) { return (s != null && !s.isBlank()) ? s : def; }
    private String fmt(BigDecimal v) { return v == null ? "$0" : "$" + String.format("%,.0f", v); }
    private BigDecimal bd(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }
}
