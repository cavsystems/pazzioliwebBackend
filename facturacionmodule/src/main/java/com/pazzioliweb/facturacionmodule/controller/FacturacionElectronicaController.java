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
}

