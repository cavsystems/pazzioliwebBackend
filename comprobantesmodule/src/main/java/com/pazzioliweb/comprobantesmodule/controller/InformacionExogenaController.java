package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.dtos.RegistroExogenoDTO;
import com.pazzioliweb.comprobantesmodule.service.InformacionExogenaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints para generar / consultar los formatos de Información Exógena DIAN.
 * Devuelve los datos como JSON (para preview) o como CSV/XML (para descargar).
 */
@RestController
@RequestMapping("/api/exogena")
public class InformacionExogenaController {

    @Autowired
    private InformacionExogenaService exogenaService;

    /** Vista consolidada de todos los formatos del año. */
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> resumen(
            @RequestParam(required = false) Integer anio) {
        int a = anio != null ? anio : LocalDate.now().getYear() - 1;
        return ResponseEntity.ok(exogenaService.resumen(a));
    }

    /** Datos de un formato específico en JSON (para preview en pantalla). */
    @GetMapping("/{formato}")
    public ResponseEntity<List<RegistroExogenoDTO>> obtenerFormato(
            @PathVariable String formato,
            @RequestParam(required = false) Integer anio) {
        int a = anio != null ? anio : LocalDate.now().getYear() - 1;
        return ResponseEntity.ok(generar(formato, a));
    }

    /** Descarga el formato como CSV (más fácil de revisar en Excel). */
    @GetMapping("/{formato}/csv")
    public ResponseEntity<byte[]> descargarCsv(
            @PathVariable String formato,
            @RequestParam(required = false) Integer anio) {
        int a = anio != null ? anio : LocalDate.now().getYear() - 1;
        List<RegistroExogenoDTO> registros = generar(formato, a);
        String csv = toCsv(formato, registros);
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition",
                        "attachment; filename=formato_" + formato + "_" + a + ".csv")
                .body(("﻿" + csv).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /** Descarga el formato como XML conforme a DIAN. */
    @GetMapping(value = "/{formato}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> descargarXml(
            @PathVariable String formato,
            @RequestParam(required = false) Integer anio) {
        int a = anio != null ? anio : LocalDate.now().getYear() - 1;
        List<RegistroExogenoDTO> registros = generar(formato, a);
        String xml = toXmlDian(formato, registros, a);
        return ResponseEntity.ok()
                .header("Content-Type", "application/xml")
                .header("Content-Disposition",
                        "attachment; filename=formato_" + formato + "_" + a + ".xml")
                .body(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    // ─── Helpers ───────────────────────────────────────────────────────────

    private List<RegistroExogenoDTO> generar(String formato, int anio) {
        return switch (formato) {
            case "1001" -> exogenaService.generar1001(anio);
            case "1005" -> exogenaService.generar1005(anio);
            case "1006" -> exogenaService.generar1006(anio);
            case "1007" -> exogenaService.generar1007(anio);
            case "1008" -> exogenaService.generar1008(anio);
            case "1009" -> exogenaService.generar1009(anio);
            default -> throw new IllegalArgumentException("Formato no soportado: " + formato);
        };
    }

    private String toCsv(String formato, List<RegistroExogenoDTO> registros) {
        StringBuilder sb = new StringBuilder();
        // Cabecera según formato
        sb.append("tipo_documento,numero_identificacion,dv,razon_social,concepto,");
        switch (formato) {
            case "1001" -> sb.append("pagos_sujetos_retencion,iva_mayor_valor_costo,retenciones_practicadas\n");
            case "1005" -> sb.append("iva_descontable\n");
            case "1006" -> sb.append("iva_generado\n");
            case "1007" -> sb.append("ingresos_brutos,devoluciones_rebajas,iva_generado\n");
            case "1008", "1009" -> sb.append("saldo_31_dic\n");
            default -> sb.append("\n");
        }
        for (RegistroExogenoDTO r : registros) {
            sb.append(quote(r.getTipoDocumento())).append(",")
              .append(quote(r.getNumeroIdentificacion())).append(",")
              .append(quote(r.getDv())).append(",")
              .append(quote(r.getRazonSocial())).append(",")
              .append(quote(r.getConcepto())).append(",");
            switch (formato) {
                case "1001" -> sb.append(num(r.getPagosSujetosARetencion())).append(",")
                                 .append(num(r.getIvaMayorValorCosto())).append(",")
                                 .append(num(r.getRetencionesPracticadas())).append("\n");
                case "1005" -> sb.append(num(r.getIvaDescontable())).append("\n");
                case "1006" -> sb.append(num(r.getIvaGenerado())).append("\n");
                case "1007" -> sb.append(num(r.getIngresosBrutos())).append(",")
                                 .append(num(r.getDevolucionesRebajasDescuentos())).append(",")
                                 .append(num(r.getIvaGenerado())).append("\n");
                case "1008", "1009" -> sb.append(num(r.getSaldo())).append("\n");
            }
        }
        return sb.toString();
    }

    private String toXmlDian(String formato, List<RegistroExogenoDTO> registros, int anio) {
        StringWriter sw = new StringWriter();
        sw.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sw.append("<mas:Documento xmlns:mas=\"http://www.dian.gov.co\">\n");
        sw.append("  <mas:Cabecera>\n");
        sw.append("    <mas:Ano>").append(String.valueOf(anio)).append("</mas:Ano>\n");
        sw.append("    <mas:Formato>FORMATO_").append(formato).append("</mas:Formato>\n");
        sw.append("    <mas:CantidadRegistros>").append(String.valueOf(registros.size())).append("</mas:CantidadRegistros>\n");
        sw.append("  </mas:Cabecera>\n");
        sw.append("  <mas:Contenido>\n");
        for (RegistroExogenoDTO r : registros) {
            sw.append("    <mas:Registro>\n");
            sw.append("      <mas:TipoDocumento>").append(safe(r.getTipoDocumento())).append("</mas:TipoDocumento>\n");
            sw.append("      <mas:NumeroIdentificacion>").append(safe(r.getNumeroIdentificacion())).append("</mas:NumeroIdentificacion>\n");
            sw.append("      <mas:DV>").append(safe(r.getDv())).append("</mas:DV>\n");
            sw.append("      <mas:RazonSocial>").append(escape(r.getRazonSocial())).append("</mas:RazonSocial>\n");
            sw.append("      <mas:Concepto>").append(safe(r.getConcepto())).append("</mas:Concepto>\n");
            switch (formato) {
                case "1001" -> {
                    sw.append("      <mas:PagosSujetosARetencion>").append(num(r.getPagosSujetosARetencion())).append("</mas:PagosSujetosARetencion>\n");
                    sw.append("      <mas:IvaMayorValorCosto>").append(num(r.getIvaMayorValorCosto())).append("</mas:IvaMayorValorCosto>\n");
                    sw.append("      <mas:RetencionesPracticadas>").append(num(r.getRetencionesPracticadas())).append("</mas:RetencionesPracticadas>\n");
                }
                case "1005" -> sw.append("      <mas:IvaDescontable>").append(num(r.getIvaDescontable())).append("</mas:IvaDescontable>\n");
                case "1006" -> sw.append("      <mas:IvaGenerado>").append(num(r.getIvaGenerado())).append("</mas:IvaGenerado>\n");
                case "1007" -> {
                    sw.append("      <mas:IngresosBrutos>").append(num(r.getIngresosBrutos())).append("</mas:IngresosBrutos>\n");
                    sw.append("      <mas:DevolucionesRebajas>").append(num(r.getDevolucionesRebajasDescuentos())).append("</mas:DevolucionesRebajas>\n");
                    sw.append("      <mas:IvaGenerado>").append(num(r.getIvaGenerado())).append("</mas:IvaGenerado>\n");
                }
                case "1008", "1009" -> sw.append("      <mas:Saldo>").append(num(r.getSaldo())).append("</mas:Saldo>\n");
            }
            sw.append("    </mas:Registro>\n");
        }
        sw.append("  </mas:Contenido>\n");
        sw.append("</mas:Documento>\n");
        return sw.toString();
    }

    private String quote(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    private String safe(String s) { return s == null ? "" : s; }
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
    private String num(BigDecimal v) { return v == null ? "0" : v.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString(); }
}
