package com.pazzioliweb.facturacionmodule.controller;

import com.pazzioliweb.facturacionmodule.dtos.FacturaElectronicaResponseDTO;
import com.pazzioliweb.facturacionmodule.dtos.GenerarFacturaRequestDTO;
import com.pazzioliweb.facturacionmodule.service.FacturacionElectronicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/facturacion-electronica")
public class FacturacionElectronicaController {

    private final FacturacionElectronicaService facturacionService;

    @Autowired
    public FacturacionElectronicaController(FacturacionElectronicaService facturacionService) {
        this.facturacionService = facturacionService;
    }

    /**
     * Genera una factura electrónica a partir de una venta COMPLETADA.
     * POST /api/facturacion-electronica/generar
     */
    @PostMapping("/generar")
    public ResponseEntity<FacturaElectronicaResponseDTO> generarFactura(@RequestBody GenerarFacturaRequestDTO request) {
        FacturaElectronicaResponseDTO response = facturacionService.generarDesdeVenta(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta el estado de una factura en la DIAN.
     * GET /api/facturacion-electronica/{facturaId}/estado-dian
     */
    @GetMapping("/{facturaId}/estado-dian")
    public ResponseEntity<FacturaElectronicaResponseDTO> consultarEstado(@PathVariable Integer facturaId) {
        FacturaElectronicaResponseDTO response = facturacionService.consultarEstadoDian(facturaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Descarga el PDF de la representación gráfica de la factura.
     * GET /api/facturacion-electronica/{facturaId}/pdf
     */
    @GetMapping("/{facturaId}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Integer facturaId) {
        String pdfBase64 = facturacionService.obtenerPdfBase64(facturaId);
        if (pdfBase64 == null || pdfBase64.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdfBytes = Base64.getDecoder().decode(pdfBase64);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "inline; filename=factura-" + facturaId + ".pdf")
                .body(pdfBytes);
    }

    /**
     * Reenvía una factura RECHAZADA o PENDIENTE a la DIAN.
     * POST /api/facturacion-electronica/{facturaId}/reenviar
     */
    @PostMapping("/{facturaId}/reenviar")
    public ResponseEntity<FacturaElectronicaResponseDTO> reenviarFactura(@PathVariable Integer facturaId) {
        FacturaElectronicaResponseDTO response = facturacionService.reenviarFacturaDian(facturaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Descarga el XML firmado de la factura.
     * GET /api/facturacion-electronica/{facturaId}/xml
     */
    @GetMapping("/{facturaId}/xml")
    public ResponseEntity<byte[]> descargarXml(@PathVariable Integer facturaId) {
        FacturaElectronicaResponseDTO factura = facturacionService.consultarEstadoDian(facturaId);
        // Obtener XML desde BD
        return ResponseEntity.ok()
                .header("Content-Type", "application/xml")
                .header("Content-Disposition", "attachment; filename=factura-" + facturaId + ".xml")
                .body(new byte[0]); // Se completará cuando se tenga el XML
    }

    // ─────────────────────────────────────────────────────────────────
    //  Documentos equivalentes DIAN
    // ─────────────────────────────────────────────────────────────────

    /**
     * Genera un Tiquete POS Electrónico (TPOS) para una venta.
     * POST /api/facturacion-electronica/tiquete-pos/{ventaId}
     */
    @PostMapping("/tiquete-pos/{ventaId}")
    public ResponseEntity<com.pazzioliweb.facturacionmodule.dtos.DianDocumentoResponseDTO> generarTPOS(@PathVariable Long ventaId) {
        return ResponseEntity.ok(facturacionService.generarTiquetePOS(ventaId));
    }

    /**
     * Genera una Nota Débito Electrónica (ND) para una factura existente.
     * POST /api/facturacion-electronica/nota-debito
     * Body: { facturaId, codigoConcepto, razon, monto, ivaMonto }
     */
    @PostMapping("/nota-debito")
    public ResponseEntity<com.pazzioliweb.facturacionmodule.dtos.DianDocumentoResponseDTO> generarND(@RequestBody Map<String, Object> body) {
        Integer facturaId = ((Number) body.get("facturaId")).intValue();
        Integer concepto = body.get("codigoConcepto") != null ? ((Number) body.get("codigoConcepto")).intValue() : 4;
        String razon = body.get("razon") != null ? body.get("razon").toString() : "Cargo adicional";
        java.math.BigDecimal monto = new java.math.BigDecimal(body.get("monto").toString());
        java.math.BigDecimal iva = body.get("ivaMonto") != null
                ? new java.math.BigDecimal(body.get("ivaMonto").toString())
                : java.math.BigDecimal.ZERO;
        return ResponseEntity.ok(facturacionService.generarNotaDebito(facturaId, concepto, razon, monto, iva));
    }

    /**
     * Genera un Documento Soporte (DS) para una compra a no facturador.
     * POST /api/facturacion-electronica/documento-soporte
     * Body: { proveedorIdentificacion, proveedorNombre, base, iva, total, concepto }
     */
    @PostMapping("/documento-soporte")
    public ResponseEntity<com.pazzioliweb.facturacionmodule.dtos.DianDocumentoResponseDTO> generarDS(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("proveedorIdentificacion");
        String nom = (String) body.get("proveedorNombre");
        java.math.BigDecimal base = new java.math.BigDecimal(body.getOrDefault("base", "0").toString());
        java.math.BigDecimal iva = new java.math.BigDecimal(body.getOrDefault("iva", "0").toString());
        java.math.BigDecimal total = new java.math.BigDecimal(body.getOrDefault("total", "0").toString());
        String concepto = (String) body.getOrDefault("concepto", "Compra a no facturador");
        String tipoCompra = (String) body.getOrDefault("tipoCompra", "SERVICIOS");
        java.math.BigDecimal retFte = new java.math.BigDecimal(body.getOrDefault("retencionFuente", "0").toString());
        java.math.BigDecimal retIva = new java.math.BigDecimal(body.getOrDefault("retencionIva", "0").toString());
        java.math.BigDecimal retIca = new java.math.BigDecimal(body.getOrDefault("retencionIca", "0").toString());
        return ResponseEntity.ok(facturacionService.generarDocumentoSoporte(
                id, nom, base, iva, total, concepto, tipoCompra, retFte, retIva, retIca));
    }
}

