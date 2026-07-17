package com.pazzioliweb.ventasmodule.service;

import com.pazzioliweb.commonbacken.util.HtmlPdfUtil;
import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import com.pazzioliweb.ventasmodule.dtos.CotizacionDTO;
import com.pazzioliweb.ventasmodule.dtos.DetalleCotizacionDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailVentaService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@pazzioli.com}")
    private String remitente;

    /**
     * Envía el recibo/factura de venta por email al cliente, con la factura en PDF adjunta.
     * El PDF es best-effort: si su generación falla, el correo se envía igual con el HTML en el
     * cuerpo (nunca se bloquea el envío por un problema de render de PDF).
     * @return true si fue enviado, false si SMTP no está configurado.
     */
    public boolean enviarRecibo(VentaDTO venta, String correo) {
        if (mailSender == null) {
            System.out.println("[EmailVenta] JavaMailSender no configurado. Configure spring.mail.* en application.properties.");
            return false;
        }
        if (correo == null || correo.isBlank()) {
            System.out.println("[EmailVenta] No se proporcionó correo para la venta " + venta.getNumeroVenta());
            return false;
        }
        try {
            String html = construirHtml(venta);
            MimeMessage message = mailSender.createMimeMessage();
            // multipart=true para poder adjuntar el PDF.
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(correo);
            helper.setSubject("Factura de venta " + venta.getNumeroVenta());
            helper.setText(html, true);

            // Adjuntar la factura en PDF (best-effort).
            try {
                byte[] pdf = HtmlPdfUtil.htmlToPdf(html);
                helper.addAttachment("Factura-" + venta.getNumeroVenta() + ".pdf",
                        new ByteArrayResource(pdf), "application/pdf");
            } catch (Throwable pex) {
                System.out.println("[EmailVenta] No se pudo generar el PDF (se envía solo HTML): " + pex.getMessage());
            }

            mailSender.send(message);
            System.out.println("[EmailVenta] Recibo enviado a " + correo + " para venta " + venta.getNumeroVenta());
            return true;
        } catch (Exception ex) {
            System.out.println("[EmailVenta] Error enviando email: " + ex.getMessage());
            throw new RuntimeException("Error enviando recibo: " + ex.getMessage(), ex);
        }
    }

    /**
     * Envía una COTIZACIÓN por email, con PDF adjunto (best-effort). Mismo patrón que la venta.
     * @return true si fue enviada, false si SMTP no está configurado.
     */
    public boolean enviarCotizacion(CotizacionDTO cot, String correo) {
        if (mailSender == null) {
            System.out.println("[EmailVenta] JavaMailSender no configurado. Configure spring.mail.* en application.properties.");
            return false;
        }
        if (correo == null || correo.isBlank()) return false;
        try {
            String html = construirHtmlCotizacion(cot);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(correo);
            helper.setSubject("Cotización " + nvl(cot.getNumeroCotizacion()));
            helper.setText(html, true);
            try {
                byte[] pdf = HtmlPdfUtil.htmlToPdf(html);
                helper.addAttachment("Cotizacion-" + nvl(cot.getNumeroCotizacion()) + ".pdf",
                        new ByteArrayResource(pdf), "application/pdf");
            } catch (Throwable pex) {
                System.out.println("[EmailVenta] No se pudo generar el PDF de cotización (se envía solo HTML): " + pex.getMessage());
            }
            mailSender.send(message);
            System.out.println("[EmailVenta] Cotización enviada a " + correo + ": " + cot.getNumeroCotizacion());
            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error enviando cotización: " + ex.getMessage(), ex);
        }
    }

    private String construirHtmlCotizacion(CotizacionDTO cot) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\" /><style>")
          .append("body{font-family:Arial,sans-serif;color:#333;margin:0;padding:16px;background:#f8f9fa}")
          .append(".container{max-width:640px;margin:0 auto;background:#fff;border:1px solid #e9ecef;border-radius:8px}")
          .append(".header{background:#f09700;color:#fff;padding:20px 24px}.header h2{margin:0;font-size:20px}.header p{margin:4px 0 0;font-size:13px}")
          .append(".body{padding:20px 24px}")
          .append(".info{width:100%;border-collapse:collapse;margin-bottom:16px}.info td{padding:6px 8px;font-size:13px;width:50%;vertical-align:top}")
          .append(".info .label{color:#6c757d;font-weight:bold;font-size:11px;text-transform:uppercase}")
          .append(".items{width:100%;border-collapse:collapse;margin:12px 0;font-size:13px}")
          .append(".items thead th{background:#f09700;color:#fff;padding:8px 10px;text-align:left}.items td{padding:7px 10px;border-bottom:1px solid #e9ecef}")
          .append(".tot{width:100%;border-collapse:collapse;margin-top:8px;border-top:2px solid #f09700;font-size:13px}.tot td{padding:5px 8px}.tot .fin td{font-size:17px;font-weight:bold;color:#f09700}")
          .append(".footer{background:#f8f9fa;padding:14px 24px;font-size:11px;color:#868e96;text-align:center;border-top:1px solid #e9ecef}")
          .append("</style></head><body><div class=\"container\">");
        sb.append("<div class=\"header\"><h2>Cotización</h2><p>").append(nvl(cot.getNumeroCotizacion())).append("</p></div>");
        sb.append("<div class=\"body\"><table class=\"info\">")
          .append("<tr>").append(infoTd("Cliente", nvl(cot.getClienteNombre(), "-"))).append(infoTd("Fecha", String.valueOf(cot.getFechaEmision()))).append("</tr>")
          .append("<tr>").append(infoTd("Vence", String.valueOf(cot.getFechaVencimiento()))).append(infoTd("Vendedor", nvl(cot.getVendedorNombre(), "-"))).append("</tr>")
          .append("</table>");
        if (cot.getItems() != null && !cot.getItems().isEmpty()) {
            sb.append("<table class=\"items\"><thead><tr><th>Descripci&#243;n</th><th style=\"text-align:center\">Cant.</th><th style=\"text-align:right\">Precio</th><th style=\"text-align:right\">Total</th></tr></thead><tbody>");
            for (DetalleCotizacionDTO it : cot.getItems()) {
                sb.append("<tr><td>").append(nvl(it.getDescripcionProducto())).append("</td>")
                  .append("<td style=\"text-align:center\">").append(it.getCantidad()).append("</td>")
                  .append("<td style=\"text-align:right\">").append(fmt(it.getPrecioUnitario())).append("</td>")
                  .append("<td style=\"text-align:right;font-weight:bold\">").append(fmt(it.getTotal())).append("</td></tr>");
            }
            sb.append("</tbody></table>");
        }
        sb.append("<table class=\"tot\">")
          .append(totRow("Subtotal", cot.getSubtotal(), false))
          .append(totRow("IVA", cot.getIva(), false))
          .append(totRow("TOTAL", cot.getTotal(), true)).append("</table>");
        sb.append("</div><div class=\"footer\">Cotización generada por Pazzioli WEB. Válida hasta la fecha de vencimiento.</div></div></body></html>");
        return sb.toString();
    }

    // ── Plantilla HTML (table-based, XHTML válido → sirve para el correo Y para el PDF) ──
    private String construirHtml(VentaDTO venta) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\" />")
          .append("<style>")
          .append("body{font-family:Arial,sans-serif;color:#333;margin:0;padding:16px;background:#f8f9fa}")
          .append(".container{max-width:640px;margin:0 auto;background:#fff;border:1px solid #e9ecef;border-radius:8px}")
          .append(".header{background:#f09700;color:#fff;padding:20px 24px}")
          .append(".header h2{margin:0;font-size:20px}")
          .append(".header p{margin:4px 0 0;font-size:13px}")
          .append(".body{padding:20px 24px}")
          .append(".info{width:100%;border-collapse:collapse;margin-bottom:16px}")
          .append(".info td{padding:6px 8px;font-size:13px;vertical-align:top;width:50%}")
          .append(".info .label{color:#6c757d;font-weight:bold;font-size:11px;text-transform:uppercase}")
          .append(".items{width:100%;border-collapse:collapse;margin:12px 0;font-size:13px}")
          .append(".items thead th{background:#f09700;color:#fff;padding:8px 10px;text-align:left}")
          .append(".items td{padding:7px 10px;border-bottom:1px solid #e9ecef}")
          .append(".tot{width:100%;border-collapse:collapse;margin-top:8px;border-top:2px solid #f09700;font-size:13px}")
          .append(".tot td{padding:5px 8px}")
          .append(".tot .fin td{font-size:17px;font-weight:bold;color:#f09700}")
          .append(".footer{background:#f8f9fa;padding:14px 24px;font-size:11px;color:#868e96;text-align:center;border-top:1px solid #e9ecef}")
          .append("</style></head><body><div class=\"container\">");

        sb.append("<div class=\"header\"><h2>Factura de venta</h2>")
          .append("<p>").append(nvl(venta.getNumeroVenta())).append(" &#183; COMPLETADA</p></div>");

        sb.append("<div class=\"body\">");
        // Info en tabla 2x2 (no grid, para que el PDF lo renderice bien)
        sb.append("<table class=\"info\">")
          .append("<tr>").append(infoTd("Cliente", nvl(venta.getClienteNombre(), "Consumidor Final")))
          .append(infoTd("Fecha", String.valueOf(venta.getFechaEmision()))).append("</tr>")
          .append("<tr>").append(infoTd("Cajero", nvl(venta.getCajeroNombre(), "-")))
          .append(infoTd("Vendedor", nvl(venta.getVendedorNombre(), "-"))).append("</tr>")
          .append("</table>");

        if (venta.getItems() != null && !venta.getItems().isEmpty()) {
            sb.append("<table class=\"items\"><thead><tr>")
              .append("<th>Descripci&#243;n</th><th style=\"text-align:center\">Cant.</th>")
              .append("<th style=\"text-align:right\">Precio</th><th style=\"text-align:right\">Total</th>")
              .append("</tr></thead><tbody>");
            for (DetalleVentaDTO item : venta.getItems()) {
                sb.append("<tr>")
                  .append("<td>").append(nvl(item.getDescripcionProducto())).append("</td>")
                  .append("<td style=\"text-align:center\">").append(item.getCantidad()).append("</td>")
                  .append("<td style=\"text-align:right\">").append(fmt(item.getPrecioUnitario())).append("</td>")
                  .append("<td style=\"text-align:right;font-weight:bold\">").append(fmt(item.getTotal())).append("</td>")
                  .append("</tr>");
            }
            sb.append("</tbody></table>");
        }

        if (venta.getMetodosPago() != null && !venta.getMetodosPago().isEmpty()) {
            sb.append("<div style=\"font-size:12px;color:#6c757d;margin:8px 0\"><strong>Formas de pago:</strong> ");
            StringBuilder mp = new StringBuilder();
            for (VentaMetodoPagoDTO m : venta.getMetodosPago()) {
                mp.append(nvl(m.getMetodoPagoNombre())).append(" ").append(fmt(m.getMonto())).append(" &#183; ");
            }
            if (mp.length() >= 8) mp.setLength(mp.length() - 8);
            sb.append(mp).append("</div>");
        }

        sb.append("<table class=\"tot\">")
          .append(totRow("Subtotal", venta.getSubtotal(), false))
          .append(totRow("IVA", venta.getIva(), false));
        if (bd(venta.getDescuentos()).compareTo(BigDecimal.ZERO) > 0) sb.append(totRow("Descuentos", venta.getDescuentos().negate(), false));
        if (bd(venta.getRetefuente()).compareTo(BigDecimal.ZERO) > 0) sb.append(totRow("Retefuente", venta.getRetefuente().negate(), false));
        if (bd(venta.getReteiva()).compareTo(BigDecimal.ZERO) > 0) sb.append(totRow("Rete IVA", venta.getReteiva().negate(), false));
        if (bd(venta.getReteica()).compareTo(BigDecimal.ZERO) > 0) sb.append(totRow("Rete ICA", venta.getReteica().negate(), false));
        sb.append(totRow("TOTAL", venta.getTotal(), true)).append("</table>");

        sb.append("</div>"); // /body
        sb.append("<div class=\"footer\">Comprobante generado por Pazzioli WEB. Gracias por su preferencia.</div>");
        sb.append("</div></body></html>");
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

    private String fmt(BigDecimal v) {
        if (v == null) return "$0";
        return "$" + String.format("%,.0f", v);
    }

    private BigDecimal bd(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }
}
