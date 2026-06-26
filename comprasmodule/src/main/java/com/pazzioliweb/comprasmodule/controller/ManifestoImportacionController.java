package com.pazzioliweb.comprasmodule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pazzioliweb.comprasmodule.dtos.ManifestoImportacionDTO;
import com.pazzioliweb.comprasmodule.service.ManifestoImportacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compras/manifiestos")
public class ManifestoImportacionController {

    @Autowired
    private ManifestoImportacionService service;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ManifestoImportacionDTO> crear(
            @RequestPart("datos") String datosJson,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf,
            @RequestHeader(value = "X-TenantID", required = false) String tenant) {
        try {
            ManifestoImportacionDTO dto = objectMapper.readValue(datosJson, ManifestoImportacionDTO.class);
            ManifestoImportacionDTO created = service.crear(dto, pdf, tenant);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/orden-compra/{ordenCompraId}")
    public ResponseEntity<List<ManifestoImportacionDTO>> porOrdenCompra(@PathVariable Long ordenCompraId) {
        return ResponseEntity.ok(service.porOrdenCompra(ordenCompraId));
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<ManifestoImportacionDTO>> porVenta(@PathVariable Long ventaId) {
        return ResponseEntity.ok(service.porVenta(ventaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManifestoImportacionDTO> porId(@PathVariable Long id) {
        return ResponseEntity.ok(service.porId(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(
            @PathVariable Long id,
            @RequestHeader(value = "X-TenantID", required = false) String tenant) {
        try {
            ManifestoImportacionDTO meta = service.porId(id);
            byte[] bytes = service.obtenerPdf(id, tenant);
            String filename = meta.getNombreArchivoOriginal() != null
                ? meta.getNombreArchivoOriginal() : "manifiesto_" + id + ".pdf";
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(bytes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<Map<String, String>> enviarEmail(
            @PathVariable Long id,
            @RequestParam String correo,
            @RequestParam(required = false) Long ventaId) {
        try {
            service.enviarEmail(id, correo, ventaId);
            return ResponseEntity.ok(Map.of("mensaje", "Email enviado correctamente a " + correo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", e.getMessage() != null ? e.getMessage() : "Error enviando email"));
        }
    }
}
