package com.pazzioliweb.comprasmodule.controller;

import com.pazzioliweb.comprasmodule.dtos.DetalleOrdenCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.OrdenCompraDTO;
import com.pazzioliweb.comprasmodule.service.OrdenCompraService;
import com.pazzioliweb.comprasmodule.dtos.CuentaPorPagarDTO;
import com.pazzioliweb.comprasmodule.service.CuentaPorPagarService;
import com.pazzioliweb.comprasmodule.dtos.ItemRecibidoDTO;
import com.pazzioliweb.comprasmodule.dtos.RealizarOrdenRequestDTO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/compras/ordenes-compra")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;
    private final CuentaPorPagarService cuentaPorPagarService;

    @Autowired
    public OrdenCompraController(OrdenCompraService ordenCompraService, CuentaPorPagarService cuentaPorPagarService) {
        this.ordenCompraService = ordenCompraService;
        this.cuentaPorPagarService = cuentaPorPagarService;
    }

    @GetMapping
    public ResponseEntity<Page<OrdenCompraDTO>> buscarConFiltros(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) Integer proveedorId,
            Pageable pageable) {

        Page<OrdenCompraDTO> resultado = ordenCompraService.buscarConFiltros(
                estado, fechaDesde, fechaHasta, proveedorId, pageable);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<OrdenCompraDTO>> obtenerOrdenesPendientes() {
        return ResponseEntity.ok(ordenCompraService.obtenerOrdenesPendientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraDTO> obtenerPorId(@PathVariable Long id) {
        return ordenCompraService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/por-numero/{numeroOrden}")
    public ResponseEntity<OrdenCompraDTO> obtenerPorNumeroOrden(@PathVariable String numeroOrden) {

        return ordenCompraService.obtenerPorNumeroOrden(numeroOrden)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/por-numero/lista/{numeroOrden}")
    public ResponseEntity<List<OrdenCompraDTO>> obtenerPorNumeroOrdenlista(@PathVariable String numeroOrden, 	@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,


                                                                @RequestParam(defaultValue = "numeroOrden") String sortField,
                                                                @RequestParam(defaultValue = "asc") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ordenCompraService.obtenerPorNumeroOrdenlist(numeroOrden,pageable));

    }

    @GetMapping("/siguiente-id")
    public ResponseEntity<Long> obtenerSiguienteId() {
        return ResponseEntity.ok(ordenCompraService.obtenerSiguienteId());
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrdenCompraDTO>> obtenerTodasLasOrdenes() {
        return ResponseEntity.ok(ordenCompraService.obtenerTodasLasOrdenes());
    }

    @GetMapping("/by-proveedor-estado")
    public ResponseEntity<List<OrdenCompraDTO>> obtenerOrdenesPorProveedorYEstado(@RequestParam Integer proveedorId, @RequestParam String estado) {
        return ResponseEntity.ok(ordenCompraService.obtenerOrdenesPorProveedorYEstado(proveedorId, estado));
    }

    @GetMapping("/detalles/{numeroOrden}")
    public ResponseEntity<List<DetalleOrdenCompraDTO>> obtenerDetallesPorNumeroOrden(@PathVariable String numeroOrden) {


        return ResponseEntity.ok(ordenCompraService.obtenerDetallesPorNumeroOrden(numeroOrden));
    }

    @PostMapping("/realizar-orden")
    public ResponseEntity<OrdenCompraDTO> realizarOrden(@RequestBody RealizarOrdenRequestDTO request) {
        OrdenCompraDTO ordenCreada = ordenCompraService.realizarOrden(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenCreada);
    }

    @PostMapping
    public ResponseEntity<OrdenCompraDTO> crear(@RequestBody OrdenCompraRequestDTO request) {
        OrdenCompraDTO ordenCreada = ordenCompraService.crear(request.getOrdenCompra());
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenCreada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenCompraDTO> actualizar(
            @PathVariable Long id,
            @RequestBody OrdenCompraDTO ordenCompraDTO) {
        ordenCompraDTO.setId(id);
        return ResponseEntity.ok(ordenCompraService.actualizar(ordenCompraDTO));
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<Void> anular(
            @PathVariable Long id,
            @RequestParam String motivo) {
        ordenCompraService.anular(id, motivo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/recibir")
    public ResponseEntity<Void> recibirOrden(
            @PathVariable Long id,
            @RequestBody List<ItemRecibidoDTO> itemsRecibidos) {
        ordenCompraService.recibirOrden(id, itemsRecibidos);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cuentas-por-pagar/pendientes")
    public ResponseEntity<List<CuentaPorPagarDTO>> obtenerCuentasPorPagarPendientes() {
        return ResponseEntity.ok(cuentaPorPagarService.listarPendientes());
    }

    @PostMapping("/cuentas-por-pagar/{id}/pagar")
    public ResponseEntity<Void> pagarCuentaPorPagar(@PathVariable Long id) {
        cuentaPorPagarService.pagar(id);
        return ResponseEntity.noContent().build();
    }

    // Clases DTO anidadas para las solicitudes
    @Data
    public static class OrdenCompraRequestDTO {
        private OrdenCompraDTO ordenCompra;
        private List<DetalleOrdenCompraDTO> items;

        // Getters y setters
        public OrdenCompraDTO getOrdenCompra() {
            return ordenCompra;
        }

        public void setOrdenCompra(OrdenCompraDTO ordenCompra) {
            this.ordenCompra = ordenCompra;
        }

        public List<DetalleOrdenCompraDTO> getItems() {
            return items;
        }
        public void setItems(List<DetalleOrdenCompraDTO> items) {
            this.items = items;
        }
    }
}
