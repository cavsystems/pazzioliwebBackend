package com.pazzioliweb.facturacionmodule.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.facturacionmodule.dtos.FacturaResumenDTO;
import com.pazzioliweb.facturacionmodule.entity.Facturas;
import com.pazzioliweb.facturacionmodule.service.FacturasService;

@RestController
@RequestMapping("/api/facturas")
public class FacturasController {
	private final FacturasService facturaService;
	
	@Autowired
	public FacturasController(FacturasService facturaService) {
		this.facturaService = facturaService;
	}
	
	@GetMapping("/listarFacturasResumen")
    public ResponseEntity<Map<String, Object>> listarFacturasResumen(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "facturaId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<FacturaResumenDTO> facturasPage = facturaService.listarFacturasResumen(page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", facturasPage.getContent());
        response.put("currentPage", facturasPage.getNumber());
        response.put("totalItems", facturasPage.getTotalElements());
        response.put("totalPages", facturasPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/listarFacturasResumenPorFecha")
	public ResponseEntity<Map<String, Object>> listarFacturasResumenPorFecha(
	        @RequestParam String fechaInicio,
	        @RequestParam String fechaFin,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "facturaId") String sortField,
	        @RequestParam(defaultValue = "asc") String sortDirection) {

	    LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
	    LocalDateTime fin = LocalDateTime.parse(fechaFin);

	    Page<FacturaResumenDTO> facturasPage = facturaService.listarFacturasResumenPorFecha(
	            inicio, fin, page, size, sortField, sortDirection);

	    Map<String, Object> response = new HashMap<>();
	    response.put("content", facturasPage.getContent());
	    response.put("currentPage", facturasPage.getNumber());
	    response.put("totalItems", facturasPage.getTotalElements());
	    response.put("totalPages", facturasPage.getTotalPages());

	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/listarFacturasResumenPorFechaTodas")
	public ResponseEntity<List<FacturaResumenDTO>> listarFacturasResumenPorFechaTodas(
	        @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
	        @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

	    List<FacturaResumenDTO> facturas = facturaService.listarFacturasResumenPorFechaTodas(fechaInicio, fechaFin);
	    return ResponseEntity.ok(facturas);
	}
	
	@GetMapping("/listarFacturasResumenPorFechaConDetalles")
	public ResponseEntity<List<Facturas>> listarFacturasResumenPorFechaMetodoPagoTipoTotal(
	        @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
	        @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

	    List<Facturas> facturas = facturaService.listarfacturasResumenPorFechasConDetalles(fechaInicio, fechaFin);
	    return ResponseEntity.ok(facturas);
	}
	
	/**
	 * Resumen de la factura electrónica de una venta — lo que necesita el POS para
	 * imprimir la tirilla con el bloque DIAN (número, CUFE, QR, estado y resolución).
	 * 404 si la venta aún no tiene factura.
	 */
	@GetMapping("/por-venta/{ventaId}")
	public ResponseEntity<java.util.Map<String, Object>> porVenta(@PathVariable Long ventaId) {
		return facturaService.buscarPorVentaId(ventaId)
				.<ResponseEntity<java.util.Map<String, Object>>>map(f -> {
					java.util.Map<String, Object> out = new java.util.HashMap<>();
					out.put("facturaId", f.getFacturaId());
					out.put("numeroFactura", f.getNumeroFactura());
					out.put("prefijo", f.getPrefijo());
					out.put("consecutivo", f.getConsecutivo());
					out.put("cufe", f.getCufe());
					out.put("qrData", f.getQrData());
					out.put("estadoDian", f.getEstadoDian() != null ? f.getEstadoDian().name() : null);
					out.put("mensajeDian", f.getMensajeDian());
					out.put("fechaValidacionDian", f.getFechaValidacionDian());
					return ResponseEntity.ok(out);
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}")
    public ResponseEntity<Facturas> obtener(@PathVariable Integer id) {
        return facturaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Facturas> crear(@RequestBody Facturas factura) {
    	Facturas guardado = facturaService.guardar(factura);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
}
