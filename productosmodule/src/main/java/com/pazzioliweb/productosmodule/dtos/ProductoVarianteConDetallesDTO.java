package com.pazzioliweb.productosmodule.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.pazzioliweb.productosmodule.entity.ProductoVarianteDetalle;
import com.pazzioliweb.productosmodule.entity.Productos;

import lombok.Data;

public class ProductoVarianteConDetallesDTO {
	private Long productoVarianteId;
	private String sku;
	private String referenciaVariantes;
	private String codigoBarras;
	private Boolean activo;
	private String imagen;

	private List<DetalleDTO> detalles;
	private List<existenciaDTO> existencia;

	public List<PreciosProductoVarianteDTO> getPrecios() {
		return precios;
	}

	public void setPrecios(List<PreciosProductoVarianteDTO> precios) {
		this.precios = precios;
	}

	private List<PreciosProductoVarianteDTO> precios;
	public static class existenciaDTO{
		private Long existenciaId;
		private String bodega;
		private BigDecimal cantidad;
		public Long getExistenciaId() {
			return existenciaId;
		}
		public void setExistenciaId(Long existenciaId) {
			this.existenciaId = existenciaId;
		}
		public String getBodega() {
			return bodega;
		}
		public void setBodega(String bodega) {
			this.bodega = bodega;
		}
		public BigDecimal getCantidad() {
			return cantidad;
		}
		public void setCantidad(BigDecimal cantidad) {
			this.cantidad = cantidad;
		}
	}


	public static class DetalleDTO {
		private Long detalleId;
		private Long caracteristicaId;
		private String caracteristicaNombre;
		private String tipo;
		public Long getDetalleId() {
			return detalleId;
		}
		public void setDetalleId(Long detalleId) {
			this.detalleId = detalleId;
		}
		public Long getCaracteristicaId() {
			return caracteristicaId;
		}
		public void setCaracteristicaId(Long caracteristicaId) {
			this.caracteristicaId = caracteristicaId;
		}
		public String getCaracteristicaNombre() {
			return caracteristicaNombre;
		}
		public void setCaracteristicaNombre(String caracteristicaNombre) {
			this.caracteristicaNombre = caracteristicaNombre;
		}
		public String getTipo() {
			return tipo;
		}
		public void setTipo(String tipo) {
			this.tipo = tipo;
		}


	}

	public Long getProductoVarianteId() {
		return productoVarianteId;
	}

	public void setProductoVarianteId(Long productoVarianteId) {
		this.productoVarianteId = productoVarianteId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getReferenciaVariantes() {
		return referenciaVariantes;
	}

	public void setReferenciaVariantes(String referenciaVariantes) {
		this.referenciaVariantes = referenciaVariantes;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public List<DetalleDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public List<existenciaDTO> getExistencia() {
		return existencia;
	}

	public void setExistencia(List<existenciaDTO> existencia) {
		this.existencia = existencia;
	}



}
