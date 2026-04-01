package com.pazzioliweb.facturacionmodule.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pazzioliweb.facturacionmodule.entity.Facturas;

public class FacturaDTO {

    private Integer facturaId;
    private Integer consecutivo;
    private Integer comprobanteId;
    private Integer terceroId;
    private LocalDateTime fechaCreacion;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private Integer plazo;
    private Integer usuarioIngresoId;
    private LocalDateTime fechaAnulo;
    private Integer usuarioAnuloId;
    private String estado;
    private Integer pedidoId;
    private Integer remisionId;
    private Integer vendedorId;
    private Double descuento;
    private Integer cajaId;
    private String observaciones;
    private Double saldo;
    private Double totalFactura;

    // ✅ Constructor vacío (necesario para Jackson)
    public FacturaDTO() {
    }

    // ✅ Constructor completo (opcional)
    public FacturaDTO(Integer facturaId, Integer consecutivo, Integer comprobanteId, Integer terceroId,
                      LocalDateTime fechaCreacion, LocalDate fechaEmision, LocalDate fechaVencimiento,
                      Integer plazo, Integer usuarioIngresoId, LocalDateTime fechaAnulo, Integer usuarioAnuloId,
                      String estado, Integer pedidoId, Integer remisionId, Integer vendedorId,
                      Double descuento, Integer cajaId, String observaciones, Double saldo, Double totalFactura) {
        this.facturaId = facturaId;
        this.consecutivo = consecutivo;
        this.comprobanteId = comprobanteId;
        this.terceroId = terceroId;
        this.fechaCreacion = fechaCreacion;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.plazo = plazo;
        this.usuarioIngresoId = usuarioIngresoId;
        this.fechaAnulo = fechaAnulo;
        this.usuarioAnuloId = usuarioAnuloId;
        this.estado = estado;
        this.pedidoId = pedidoId;
        this.remisionId = remisionId;
        this.vendedorId = vendedorId;
        this.descuento = descuento;
        this.cajaId = cajaId;
        this.observaciones = observaciones;
        this.saldo = saldo;
        this.totalFactura = totalFactura;
    }

    // ✅ Método estático para mapear desde la entidad
    public static FacturaDTO fromEntity(Facturas factura) {
        if (factura == null) {
            return null;
        }

        return new FacturaDTO(
            factura.getFacturaId(),
            factura.getConsecutivo(),
            factura.getComprobanteId(),
            factura.getTerceroId(),
            factura.getFechaCreacion(),
            factura.getFechaEmision(),
            factura.getFechaVencimiento(),
            factura.getPlazo(),
            factura.getUsuarioIngresoId(),
            factura.getFechaAnulo(),
            factura.getUsuarioAnuloId(),
            factura.getEstado() != null ? factura.getEstado().name() : "ACTIVO",
            factura.getPedidoId(),
            factura.getRemisionId(),
            factura.getVendedorId(),
            factura.getDescuento(),
            factura.getCajaId(),
            factura.getObservaciones(),
            factura.getSaldo(),
            factura.getTotalFactura()
        );
    }

    // ✅ Getters y Setters
    public Integer getFacturaId() { return facturaId; }
    public void setFacturaId(Integer facturaId) { this.facturaId = facturaId; }

    public Integer getConsecutivo() { return consecutivo; }
    public void setConsecutivo(Integer consecutivo) { this.consecutivo = consecutivo; }

    public Integer getComprobanteId() { return comprobanteId; }
    public void setComprobanteId(Integer comprobanteId) { this.comprobanteId = comprobanteId; }

    public Integer getTerceroId() { return terceroId; }
    public void setTerceroId(Integer terceroId) { this.terceroId = terceroId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Integer getPlazo() { return plazo; }
    public void setPlazo(Integer plazo) { this.plazo = plazo; }

    public Integer getUsuarioIngresoId() { return usuarioIngresoId; }
    public void setUsuarioIngresoId(Integer usuarioIngresoId) { this.usuarioIngresoId = usuarioIngresoId; }

    public LocalDateTime getFechaAnulo() { return fechaAnulo; }
    public void setFechaAnulo(LocalDateTime fechaAnulo) { this.fechaAnulo = fechaAnulo; }

    public Integer getUsuarioAnuloId() { return usuarioAnuloId; }
    public void setUsuarioAnuloId(Integer usuarioAnuloId) { this.usuarioAnuloId = usuarioAnuloId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    public Integer getRemisionId() { return remisionId; }
    public void setRemisionId(Integer remisionId) { this.remisionId = remisionId; }

    public Integer getVendedorId() { return vendedorId; }
    public void setVendedorId(Integer vendedorId) { this.vendedorId = vendedorId; }

    public Double getDescuento() { return descuento; }
    public void setDescuento(Double descuento) { this.descuento = descuento; }

    public Integer getCajaId() { return cajaId; }
    public void setCajaId(Integer cajaId) { this.cajaId = cajaId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }

    public Double getTotalFactura() { return totalFactura; }
    public void setTotalFactura(Double totalFactura) { this.totalFactura = totalFactura; }
}
