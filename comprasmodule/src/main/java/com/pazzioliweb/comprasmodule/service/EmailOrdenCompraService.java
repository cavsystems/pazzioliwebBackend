package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class EmailOrdenCompraService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@pazzioli.com}")
    private String remitente;

    /**
     * Envía la orden de compra por email al proveedor.
     * @param orden   Orden a enviar
     * @param correo  Correo destino (proveedor); si es null se usa el correo de la orden
     * @return true si se envió, false si el servicio de email no está configurado
     */
    public boolean enviarOrden(OrdenCompraDTO orden, String correo) {
        if (mailSender == null) {
            System.out.println("[EmailOC] JavaMailSender no configurado. Configure spring.mail.* en application.properties.");
            return false;
        }
        if (correo == null || correo.isBlank()) {
            System.out.println("[EmailOC] No se proporcionó correo destino para la orden " + orden.getNumeroOrden());
            return false;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(correo);
            helper.setSubject("Orden de Compra " + orden.getNumeroOrden());
            helper.setText(construirHtml(orden), true);
            mailSender.send(message);
            System.out.println("[EmailOC] Email enviado a " + correo + " para orden " + orden.getNumeroOrden());
            return true;
        } catch (Exception ex) {
            System.out.println("[EmailOC] Error enviando email: " + ex.getMessage());
            throw new RuntimeException("Error enviando email: " + ex.getMessage(), ex);
        }
    }

    private String construirHtml(OrdenCompraDTO orden) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head>")
          .append("<meta charset='UTF-8'>")
          .append("<style>")
          .append("body{font-family:Arial,sans-serif;color:#333;margin:0;padding:20px}")
          .append("h2{color:#2c5282;border-bottom:2px solid #2c5282;padding-bottom:8px}")
          .append("table{width:100%;border-collapse:collapse;margin-top:16px}")
          .append("th{background:#2c5282;color:#fff;padding:10px;text-align:left}")
          .append("td{padding:8px 10px;border-bottom:1px solid #e2e8f0}")
          .append("tr:nth-child(even) td{background:#f7fafc}")
          .append(".totales{margin-top:16px;text-align:right}")
          .append(".totales td{font-size:14px;padding:4px 10px}")
          .append(".totales .label{font-weight:bold}")
          .append(".footer{margin-top:24px;font-size:12px;color:#718096;border-top:1px solid #e2e8f0;padding-top:12px}")
          .append("</style></head><body>");

        sb.append("<h2>Orden de Compra ").append(orden.getNumeroOrden()).append("</h2>");
        sb.append("<p><strong>Fecha:</strong> ").append(orden.getFechaEmision()).append("</p>");
        sb.append("<p><strong>Fecha de entrega esperada:</strong> ").append(orden.getFechaEntregaEsperada()).append("</p>");
        if (orden.getObservaciones() != null && !orden.getObservaciones().isBlank()) {
            sb.append("<p><strong>Observaciones:</strong> ").append(orden.getObservaciones()).append("</p>");
        }

        // Tabla de productos
        sb.append("<table>")
          .append("<tr><th>#</th><th>Descripción</th><th>Referencia</th>")
          .append("<th>Cantidad</th><th>Precio Unit.</th><th>Desc.</th><th>IVA %</th><th>Total</th></tr>");

        if (orden.getItems() != null) {
            int idx = 1;
            for (var item : orden.getItems()) {
                sb.append("<tr>")
                  .append("<td>").append(idx++).append("</td>")
                  .append("<td>").append(nvl(item.getDescripcionProducto())).append("</td>")
                  .append("<td>").append(nvl(item.getReferenciaVariantes())).append("</td>")
                  .append("<td style='text-align:right'>").append(item.getCantidad()).append("</td>")
                  .append("<td style='text-align:right'>").append(fmt(item.getPrecioUnitario())).append("</td>")
                  .append("<td style='text-align:right'>").append(fmt(item.getDescuento())).append("</td>")
                  .append("<td style='text-align:right'>").append(item.getIvaPorcentaje()).append("%</td>")
                  .append("<td style='text-align:right'>").append(fmt(item.getTotal())).append("</td>")
                  .append("</tr>");
            }
        }
        sb.append("</table>");

        // Totales
        sb.append("<table class='totales' style='width:350px;margin-left:auto'>")
          .append("<tr><td class='label'>Subtotal:</td><td style='text-align:right'>").append(fmt(orden.getSubtotal())).append("</td></tr>")
          .append("<tr><td class='label'>IVA:</td><td style='text-align:right'>").append(fmt(orden.getIva())).append("</td></tr>");
        // descuentos no está en OrdenCompraDTO — se omite en el email
        sb.append("<tr style='font-size:16px;font-weight:bold'><td class='label'>TOTAL:</td><td style='text-align:right'>")
          .append(fmt(orden.getTotal())).append("</td></tr>")
          .append("</table>");

        sb.append("<div class='footer'>Este documento es una orden de compra generada por el sistema Pazzioli WEB.</div>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String nvl(String s) { return s != null ? s : ""; }
    private String fmt(BigDecimal v) {
        if (v == null) return "$ 0";
        return "$ " + String.format("%,.2f", v);
    }
    private BigDecimal bd(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }
}
