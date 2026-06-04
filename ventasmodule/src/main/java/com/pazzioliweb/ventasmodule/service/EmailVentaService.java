package com.pazzioliweb.ventasmodule.service;

import com.pazzioliweb.ventasmodule.dtos.VentaDTO;
import com.pazzioliweb.ventasmodule.dtos.DetalleVentaDTO;
import com.pazzioliweb.ventasmodule.dtos.VentaMetodoPagoDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
     * Envía el recibo/factura de venta por email al cliente.
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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(correo);
            helper.setSubject("Recibo de compra " + venta.getNumeroVenta());
            helper.setText(construirHtml(venta), true);
            mailSender.send(message);
            System.out.println("[EmailVenta] Recibo enviado a " + correo + " para venta " + venta.getNumeroVenta());
            return true;
        } catch (Exception ex) {
            System.out.println("[EmailVenta] Error enviando email: " + ex.getMessage());
            throw new RuntimeException("Error enviando recibo: " + ex.getMessage(), ex);
        }
    }

    private String construirHtml(VentaDTO venta) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
          .append("<style>")
          .append("body{font-family:Arial,sans-serif;color:#333;margin:0;padding:20px;background:#f8f9fa}")
          .append(".container{max-width:600px;margin:0 auto;background:#fff;border-radius:10px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,.12)}")
          .append(".header{background:#f09700;color:#fff;padding:24px 28px;}")
          .append(".header h2{margin:0;font-size:20px}")
          .append(".header p{margin:4px 0 0;font-size:13px;opacity:.9}")
          .append(".body{padding:24px 28px}")
          .append(".info-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:20px}")
          .append(".info-box{background:#f8f9fa;border-radius:6px;padding:12px 14px}")
          .append(".info-box .label{font-size:11px;color:#6c757d;font-weight:600;text-transform:uppercase;margin-bottom:2px}")
          .append(".info-box .value{font-size:14px;font-weight:600}")
          .append("table{width:100%;border-collapse:collapse;margin:16px 0;font-size:13px}")
          .append("thead tr{background:#f09700;color:#fff}")
          .append("th{padding:9px 10px;text-align:left;font-weight:600}")
          .append("td{padding:8px 10px;border-bottom:1px solid #e9ecef}")
          .append("tr:nth-child(even) td{background:#f8f9fa}")
          .append(".totales{margin-top:8px;border-top:2px solid #f09700;padding-top:12px}")
          .append(".total-row{display:flex;justify-content:space-between;padding:5px 0;font-size:13px}")
          .append(".total-final{font-size:17px;font-weight:700;color:#f09700}")
          .append(".footer{background:#f8f9fa;padding:16px 28px;font-size:11px;color:#868e96;text-align:center;border-top:1px solid #e9ecef}")
          .append(".badge{display:inline-block;padding:3px 10px;border-radius:20px;font-size:11px;font-weight:700;background:#d4edda;color:#155724}")
          .append("</style></head><body><div class='container'>");

        // Header
        sb.append("<div class='header'>")
          .append("<h2>Recibo de compra</h2>")
          .append("<p>").append(venta.getNumeroVenta()).append(" · <span class='badge'>COMPLETADA</span></p>")
          .append("</div>");

        // Info grid
        sb.append("<div class='body'>")
          .append("<div class='info-grid'>")
          .append(infoBox("Cliente", nvl(venta.getClienteNombre(), "Consumidor Final")))
          .append(infoBox("Fecha", String.valueOf(venta.getFechaEmision())))
          .append(infoBox("Cajero", nvl(venta.getCajeroNombre(), "—")))
          .append(infoBox("Vendedor", nvl(venta.getVendedorNombre(), "—")))
          .append("</div>");

        // Tabla de productos
        if (venta.getItems() != null && !venta.getItems().isEmpty()) {
            sb.append("<table><thead><tr>")
              .append("<th>Descripción</th><th style='text-align:center'>Cant.</th>")
              .append("<th style='text-align:right'>Precio</th><th style='text-align:right'>Total</th>")
              .append("</tr></thead><tbody>");
            for (DetalleVentaDTO item : venta.getItems()) {
                sb.append("<tr>")
                  .append("<td>").append(nvl(item.getDescripcionProducto())).append("</td>")
                  .append("<td style='text-align:center'>").append(item.getCantidad()).append("</td>")
                  .append("<td style='text-align:right'>").append(fmt(item.getPrecioUnitario())).append("</td>")
                  .append("<td style='text-align:right;font-weight:600'>").append(fmt(item.getTotal())).append("</td>")
                  .append("</tr>");
            }
            sb.append("</tbody></table>");
        }

        // Métodos de pago
        if (venta.getMetodosPago() != null && !venta.getMetodosPago().isEmpty()) {
            sb.append("<div style='font-size:12px;color:#6c757d;margin-bottom:12px'><strong>Formas de pago:</strong> ");
            for (VentaMetodoPagoDTO mp : venta.getMetodosPago()) {
                sb.append(nvl(mp.getMetodoPagoNombre())).append(" ").append(fmt(mp.getMonto())).append(" · ");
            }
            sb.setLength(sb.length() - 3);
            sb.append("</div>");
        }

        // Totales
        sb.append("<div class='totales'>")
          .append(totalRow("Subtotal", venta.getSubtotal(), false))
          .append(totalRow("IVA", venta.getIva(), false));
        if (bd(venta.getDescuentos()).compareTo(BigDecimal.ZERO) > 0)
            sb.append(totalRow("Descuentos", venta.getDescuentos().negate(), false));
        if (bd(venta.getRetefuente()).compareTo(BigDecimal.ZERO) > 0)
            sb.append(totalRow("Retefuente", venta.getRetefuente().negate(), false));
        if (bd(venta.getReteiva()).compareTo(BigDecimal.ZERO) > 0)
            sb.append(totalRow("Rete IVA", venta.getReteiva().negate(), false));
        if (bd(venta.getReteica()).compareTo(BigDecimal.ZERO) > 0)
            sb.append(totalRow("Rete ICA", venta.getReteica().negate(), false));
        sb.append(totalRow("TOTAL", venta.getTotal(), true))
          .append("</div>");

        sb.append("</div>"); // /body

        // Footer
        sb.append("<div class='footer'>")
          .append("Este es un comprobante de compra generado por Pazzioli WEB.<br>")
          .append("Gracias por su preferencia.")
          .append("</div></div></body></html>");

        return sb.toString();
    }

    private String infoBox(String label, String value) {
        return "<div class='info-box'><div class='label'>" + label + "</div><div class='value'>" + value + "</div></div>";
    }

    private String totalRow(String label, BigDecimal value, boolean bold) {
        String style = bold ? " class='total-row total-final'" : " class='total-row'";
        return "<div" + style + "><span>" + label + "</span><span>" + fmt(value) + "</span></div>";
    }

    private String nvl(String s) { return s != null ? s : ""; }
    private String nvl(String s, String def) { return (s != null && !s.isBlank()) ? s : def; }

    private String fmt(BigDecimal v) {
        if (v == null) return "$0";
        return "$" + String.format("%,.0f", v);
    }

    private BigDecimal bd(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }
}
