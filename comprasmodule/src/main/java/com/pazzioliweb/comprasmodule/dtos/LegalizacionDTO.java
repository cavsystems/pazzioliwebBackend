package com.pazzioliweb.comprasmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LegalizacionDTO {

    private Long id;
    private Long ordenCompraId;
    private String numeroFacturaProveedor;
    private LocalDate fechaFactura;
    private BigDecimal totalFactura;
    private Long proveedorId;
    private String estado;
    private String usuarioCreacion;
    private List<DetalleOrdenCompraDTO> items;
    private String proveedorNombre;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    private  BigDecimal total;
    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }


    public List<DetalleOrdenCompraDTO> getItems() {
        return items;
    }

    public void setItems(List<DetalleOrdenCompraDTO> items) {
        this.items = items;
    }


    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrdenCompraId() {
        return ordenCompraId;
    }

    public void setOrdenCompraId(Long ordenCompraId) {
        this.ordenCompraId = ordenCompraId;
    }

    public String getNumeroFacturaProveedor() {
        return numeroFacturaProveedor;
    }

    public void setNumeroFacturaProveedor(String numeroFacturaProveedor) {
        this.numeroFacturaProveedor = numeroFacturaProveedor;
    }

    public LocalDate getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(LocalDate fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public BigDecimal getTotalFactura() {
        return totalFactura;
    }

    public void setTotalFactura(BigDecimal totalFactura) {
        this.totalFactura = totalFactura;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }
}
