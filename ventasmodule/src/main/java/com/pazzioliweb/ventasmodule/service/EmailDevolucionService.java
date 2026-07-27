package com.pazzioliweb.ventasmodule.service;

import com.pazzioliweb.commonbacken.dtos.DocumentoAdjuntoDTO;
import com.pazzioliweb.commonbacken.util.HtmlPdfUtil;
import com.pazzioliweb.ventasmodule.dtos.DetalleDevolucionDTO;
import com.pazzioliweb.ventasmodule.dtos.DevolucionDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Envío de la DEVOLUCIÓN DE VENTA (nota crédito) por correo, con el documento en PDF adjunto.
 * Mismo patrón que EmailVentaService / EmailTesoreriaService: JavaMailSender opcional, si SMTP no
 * está configurado devuelve false sin romper nada.
 *
 * La plantilla es table-based y XHTML válido a propósito, porque el mismo HTML alimenta el PDF que
 * se manda por correo y por WhatsApp (ver HtmlPdfUtil).
 */
@Service
public class EmailDevolucionService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@pazzioli.com}")
    private String remitente;

    public boolean enviarDevolucion(DevolucionDTO dev, String correo) {
        if (mailSender == null) {
            System.out.println("[EmailDevolucion] JavaMailSender no configurado. Configure spring.mail.* en application.properties.");
            return false;
        }
        if (correo == null || correo.isBlank()) {
            System.out.println("[EmailDevolucion] No se proporcionó correo para la devolución " + dev.getNumeroDevolucion());
            return false;
        }
        try {
            String html = construirHtml(dev);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(correo);
            helper.setSubject("Devolución " + nvl(dev.getNumeroDevolucion()));
            helper.setText(html, true);
            try {
                byte[] pdf = HtmlPdfUtil.htmlToPdf(html);
                helper.addAttachment(nombreArchivo(dev) + ".pdf", new ByteArrayResource(pdf), "application/pdf");
            } catch (Throwable pex) {
                System.out.println("[EmailDevolucion] No se pudo generar el PDF (se envía solo HTML): " + pex.getMessage());
            }
            mailSender.send(message);
            System.out.println("[EmailDevolucion] Devolución enviada a " + correo + ": " + dev.getNumeroDevolucion());
            return true;
        } catch (Exception ex) {
            System.out.println("[EmailDevolucion] Error enviando email: " + ex.getMessage());
            throw new RuntimeException("Error enviando devolución: " + ex.getMessage(), ex);
        }
    }

    /** Documento listo para otros canales (WhatsApp). Mismo HTML/PDF que el correo. */
    public DocumentoAdjuntoDTO documentoDevolucion(DevolucionDTO dev) {
        String numero = nvl(dev.getNumeroDevolucion());
        StringBuilder caption = new StringBuilder("*DEVOLUCIÓN " + numero + "*")
                .append("\nCliente: ").append(nvl(dev.getNombreCliente(), "-"))
                .append("\nFactura: ").append(nvl(dev.getNumeroVenta(), "-"))
                .append("\nFecha: ").append(dev.getFechaCreacion())
                .append("\nTotal devuelto: ").append(fmt(dev.getTotalNeto()));
        if (dev.getNumeroNc() != null && !dev.getNumeroNc().isBlank()) {
            caption.append("\nNota crédito: ").append(dev.getNumeroNc());
        }
        return new DocumentoAdjuntoDTO(nombreArchivo(dev), caption.toString(), pdfSeguro(construirHtml(dev)));
    }

    private String nombreArchivo(DevolucionDTO dev) {
        return "Devolucion-" + nvl(dev.getNumeroDevolucion(), String.valueOf(dev.getId()));
    }

    private byte[] pdfSeguro(String html) {
        try {
            return HtmlPdfUtil.htmlToPdf(html);
        } catch (Throwable ex) {
            System.out.println("[EmailDevolucion] No se pudo generar el PDF: " + ex.getMessage());
            return null;
        }
    }

    // ── Plantilla HTML (table-based, XHTML válido → sirve para correo y para PDF) ──
    private String construirHtml(DevolucionDTO dev) {
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
          .append(".items{width:100%;border-collapse:collapse;margin:12px 0;font-size:13px}")
          .append(".items thead th{background:#f09700;color:#fff;padding:8px 10px;text-align:left}")
          .append(".items td{padding:7px 10px;border-bottom:1px solid #e9ecef}")
          .append(".tot{width:100%;border-collapse:collapse;margin-top:8px;border-top:2px solid #f09700;font-size:13px}")
          .append(".tot td{padding:5px 8px}.tot .fin td{font-size:17px;font-weight:bold;color:#f09700}")
          .append(".footer{background:#f8f9fa;padding:14px 24px;font-size:11px;color:#868e96;text-align:center;border-top:1px solid #e9ecef}")
          .append("</style></head><body><div class=\"container\">");

        sb.append("<div class=\"header\"><h2>Devoluci&#243;n de venta</h2><p>")
          .append(nvl(dev.getNumeroDevolucion())).append("</p></div>");

        sb.append("<div class=\"body\"><table class=\"info\">")
          .append("<tr>").append(infoTd("Cliente", nvl(dev.getNombreCliente(), "-")))
          .append(infoTd("Factura origen", nvl(dev.getNumeroVenta(), "-"))).append("</tr>")
          .append("<tr>").append(infoTd("Fecha", String.valueOf(dev.getFechaCreacion())))
          .append(infoTd("Estado", nvl(dev.getEstado(), "-"))).append("</tr>");
        if (dev.getNumeroNc() != null && !dev.getNumeroNc().isBlank()) {
            sb.append("<tr>").append(infoTd("Nota cr&#233;dito", dev.getNumeroNc()))
              .append(infoTd("Estado DIAN", nvl(dev.getEstadoDianNc(), "-"))).append("</tr>");
        }
        if (dev.getMotivo() != null && !dev.getMotivo().isBlank()) {
            sb.append("<tr><td colspan=\"2\"><div class=\"label\">Motivo</div><div>")
              .append(dev.getMotivo()).append("</div></td></tr>");
        }
        sb.append("</table>");

        if (dev.getItems() != null && !dev.getItems().isEmpty()) {
            sb.append("<table class=\"items\"><thead><tr>")
              .append("<th>Descripci&#243;n</th>")
              .append("<th style=\"text-align:center\">Cant.</th>")
              .append("<th style=\"text-align:right\">Precio</th>")
              .append("<th style=\"text-align:right\">Total</th>")
              .append("</tr></thead><tbody>");
            for (DetalleDevolucionDTO it : dev.getItems()) {
                sb.append("<tr><td>").append(nvl(it.getDescripcionProducto())).append("</td>")
                  .append("<td style=\"text-align:center\">").append(it.getCantidadDevuelta()).append("</td>")
                  .append("<td style=\"text-align:right\">").append(fmt(it.getPrecioUnitario())).append("</td>")
                  .append("<td style=\"text-align:right;font-weight:bold\">").append(fmt(it.getTotalLinea())).append("</td></tr>");
            }
            sb.append("</tbody></table>");
        }

        sb.append("<table class=\"tot\">")
          .append(totRow("Subtotal devuelto", dev.getTotalDevuelto(), false))
          .append(totRow("IVA devuelto", dev.getIvaDevuelto(), false))
          .append(totRow("TOTAL DEVUELTO", dev.getTotalNeto(), true))
          .append("</table>");

        sb.append("</div><div class=\"footer\">Documento generado por Pazzioli WEB.</div></div></body></html>");
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
}
