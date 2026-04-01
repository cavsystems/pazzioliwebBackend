package com.pazzioliweb.movimientosinventariomodule.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class MovimientoInventarioCreateDto {
	@NotNull
    private Integer comprobanteId;        // corresponde a Comprobantes.comprobante_id

    // consecutivo lo puede asignar el servidor (se puede omitir en create),
    // pero lo incluimos opcionalmente si el cliente ya lo define:
    private Integer consecutivo;

    @NotNull
    private String tipo;                  // ENTRADA, SALIDA, TRASLADO -> usar TipoMovimiento.name()

    private Long usuarioId;               // quien crea / responsable (opcional)

    @NotNull
    private LocalDate fechaEmision;       // fecha contable del movimiento

    // fechaCreacion la maneja el servidor (now)

    // estado por defecto: BORRADOR. Normalmente el cliente no necesita enviarlo al crear
    private String estado;                // BORRADOR, ACTIVO, ANULADO (opcional)

    private Double total;                 // opcional: puede calcularse en server

    @Size(max = 1000)
    private String observaciones;

    @NotNull
    private List<MovimientoInventarioDetalleCreateDto> detalles;

	public Integer getComprobanteId() {
		return comprobanteId;
	}

	public void setComprobanteId(Integer comprobanteId) {
		this.comprobanteId = comprobanteId;
	}

	public Integer getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(Integer consecutivo) {
		this.consecutivo = consecutivo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public LocalDate getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(LocalDate fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public List<MovimientoInventarioDetalleCreateDto> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<MovimientoInventarioDetalleCreateDto> detalles) {
		this.detalles = detalles;
	}
    
    
}
