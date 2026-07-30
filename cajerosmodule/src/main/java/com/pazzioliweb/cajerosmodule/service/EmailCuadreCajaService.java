package com.pazzioliweb.cajerosmodule.service;

import com.pazzioliweb.cajerosmodule.dtos.CuadreCajaDTO;
import com.pazzioliweb.commonbacken.util.HtmlPdfUtil;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Envío por correo del CUADRE DE CAJA al cierre de la sesión del cajero, con el
 * resumen en PDF adjunto (best-effort: si el PDF falla se envía igual el HTML).
 * Mismo patrón que EmailTesoreriaService: JavaMailSender opcional — si SMTP no
 * está configurado devuelve false sin romper el cierre de caja.
 */
@Service
public class EmailCuadreCajaService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@pazzioli.com}")
    private String remitente;

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public boolean enviarCuadre(CuadreCajaDTO cuadre, String correo) {
        if (mailSender == null) {
            System.out.println("[EmailCuadre] JavaMailSender no configurado. Configure spring.mail.* en application.properties.");
            return false;
        }
        if (correo == null || correo.isBlank()) {
            System.out.println("[EmailCuadre] No se proporcionó correo.");
            return false;
        }
        String asunto = "Cuadre de caja — " + nvl(cuadre.getCajeroNombre(), "Cajero " + cuadre.getCajeroId())
                + (cuadre.getFechaCierre() != null ? " (" + cuadre.getFechaCierre().format(FECHA_HORA) + ")" : "");
        String html = buildHtml(cuadre);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(correo.trim());
            helper.setSubject(asunto);
            helper.setText(html, true);
            try {
                byte[] pdf = HtmlPdfUtil.htmlToPdf(html);
                helper.addAttachment("Cuadre-caja-" + cuadre.getDetalleCajeroId() + ".pdf",
                        new ByteArrayResource(pdf), "application/pdf");
            } catch (Throwable pex) {
                System.out.println("[EmailCuadre] No se pudo generar el PDF (se envía solo HTML): " + pex.getMessage());
            }
            mailSender.send(message);
            System.out.println("[EmailCuadre] Cuadre enviado a " + correo + ": " + asunto);
            return true;
        } catch (Exception ex) {
            System.out.println("[EmailCuadre] Error enviando email: " + ex.getMessage());
            throw new RuntimeException("Error enviando el cuadre por correo: " + ex.getMessage(), ex);
        }
    }

    // ── Plantilla HTML (table-based, XHTML válido → sirve para correo y PDF) ──
    private String buildHtml(CuadreCajaDTO c) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\" /><style>")
          .append("body{font-family:Arial,sans-serif;color:#333;margin:0;padding:16px;background:#f8f9fa}")
          .append(".container{max-width:680px;margin:0 auto;background:#fff;border:1px solid #e9ecef;border-radius:8px}")
          .append(".header{background:#f09700;color:#fff;padding:20px 24px}")
          .append(".header h2{margin:0;font-size:20px}.header p{margin:4px 0 0;font-size:13px}")
          .append(".body{padding:20px 24px}")
          .append(".info{width:100%;border-collapse:collapse;margin-bottom:14px}")
          .append(".info td{padding:5px 8px;font-size:13px;vertical-align:top;width:50%}")
          .append(".info .label{color:#6c757d;font-weight:bold;font-size:11px;text-transform:uppercase}")
          .append("h3{font-size:14px;color:#f09700;border-bottom:2px solid #f09700;padding-bottom:4px;margin:16px 0 8px}")
          .append(".t{width:100%;border-collapse:collapse;font-size:13px}")
          .append(".t td,.t th{padding:5px 8px;border-bottom:1px solid #f1f3f5}")
          .append(".t th{text-align:left;color:#6c757d;font-size:11px;text-transform:uppercase}")
          .append(".num{text-align:right}")
          .append(".pos{color:#198754;font-weight:bold}.neg{color:#dc3545;font-weight:bold}")
          .append(".fin td{font-size:15px;font-weight:bold;color:#f09700;border-top:2px solid #f09700}")
          .append(".footer{background:#f8f9fa;padding:14px 24px;font-size:11px;color:#868e96;text-align:center;border-top:1px solid #e9ecef}")
          .append("</style></head><body><div class=\"container\">");

        sb.append("<div class=\"header\"><h2>Cuadre de caja</h2><p>")
          .append(nvl(c.getCajeroNombre(), "Cajero " + c.getCajeroId()))
          .append(" · Sesión #").append(c.getDetalleCajeroId()).append("</p></div>");

        sb.append("<div class=\"body\">");

        // Datos de la sesión
        sb.append("<table class=\"info\"><tr>")
          .append(infoTd("Apertura", c.getFechaApertura() != null ? c.getFechaApertura().format(FECHA_HORA) : "-"))
          .append(infoTd("Cierre", c.getFechaCierre() != null ? c.getFechaCierre().format(FECHA_HORA) : "-"))
          .append("</tr><tr>")
          .append(infoTd("Estado", nvl(c.getEstado(), "-")))
          .append(infoTd("Transacciones", c.getTotalTransacciones() != null ? String.valueOf(c.getTotalTransacciones()) : "0"))
          .append("</tr></table>");

        // Componente 1: efectivo
        sb.append("<h3>1. Efectivo</h3><table class=\"t\">")
          .append(row("Base de caja", c.getBaseCaja()))
          .append(row("Ingresos en efectivo", c.getTotalEfectivo()))
          .append(row("Efectivo esperado", c.getEfectivoEsperado()))
          .append(row("Efectivo declarado", c.getEfectivoDeclarado()))
          .append(rowDif("Diferencia efectivo", c.getDiferenciaEfectivo()))
          .append("</table>");

        // Componente 2: medios electrónicos
        sb.append("<h3>2. Medios electrónicos</h3><table class=\"t\">")
          .append(row("Esperado por sistema", c.getTotalMediosElectronicos()))
          .append(row("Declarado por el cajero", c.getMediosElectronicosDeclarado()))
          .append(rowDif("Diferencia electrónico", c.getDiferenciaMediosElectronicos()))
          .append("</table>");

        // Desglose por tipo de documento
        if (c.getDesglosePorTipo() != null && !c.getDesglosePorTipo().isEmpty()) {
            sb.append("<h3>Desglose por tipo de documento</h3><table class=\"t\">")
              .append("<tr><th>Documento</th><th class=\"num\">Cant.</th><th class=\"num\">Efectivo</th>")
              .append("<th class=\"num\">Electrónico</th><th class=\"num\">Total</th></tr>");
            for (CuadreCajaDTO.ResumenTipoDocumento d : c.getDesglosePorTipo()) {
                sb.append("<tr><td>").append(nvl(d.getDescripcion(), d.getTipoMovimiento())).append("</td>")
                  .append("<td class=\"num\">").append(d.getCantidad() != null ? d.getCantidad() : 0).append("</td>")
                  .append("<td class=\"num\">").append(fmt(d.getTotalEfectivo())).append("</td>")
                  .append("<td class=\"num\">").append(fmt(d.getTotalElectronico())).append("</td>")
                  .append("<td class=\"num\">").append(fmt(d.getTotalMonto())).append("</td></tr>");
            }
            sb.append("</table>");
        }

        // Totales generales
        sb.append("<h3>3. Totales</h3><table class=\"t\">")
          .append(row("Total recaudo", c.getTotalRecaudo()))
          .append("<tr class=\"fin\"><td>TOTAL ESPERADO EN CAJA</td><td class=\"num\">")
          .append(fmt(c.getMontoFinal())).append("</td></tr></table>");

        sb.append("</div><div class=\"footer\">Cuadre de caja generado por Pazzioli WEB.</div></div></body></html>");
        return sb.toString();
    }

    private String infoTd(String label, String value) {
        return "<td><div class=\"label\">" + label + "</div><div>" + nvl(value, "-") + "</div></td>";
    }
    private String row(String label, BigDecimal value) {
        return "<tr><td>" + label + "</td><td class=\"num\">" + fmt(value) + "</td></tr>";
    }
    /** Fila de diferencia con color: verde sobrante, rojo faltante. */
    private String rowDif(String label, BigDecimal value) {
        BigDecimal v = value != null ? value : BigDecimal.ZERO;
        String cls = v.signum() > 0 ? "pos" : v.signum() < 0 ? "neg" : "";
        String texto = fmt(v) + (v.signum() > 0 ? " (sobrante)" : v.signum() < 0 ? " (faltante)" : " (cuadrado)");
        return "<tr><td>" + label + "</td><td class=\"num " + cls + "\">" + texto + "</td></tr>";
    }
    private String nvl(String s, String def) { return (s != null && !s.isBlank()) ? s : def; }
    private String fmt(BigDecimal v) { return v == null ? "$0" : "$" + String.format("%,.0f", v); }
}
