package com.pazzioliweb.reportesmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Datos agregados para un certificado de retención en la fuente.
 *
 * tipo = "PRACTICADAS": retenciones que la empresa (agente retenedor) le practicó a un
 *                        proveedor (fuente: compras / ordenes_compra).
 * tipo = "SUFRIDAS":    retenciones que un cliente le practicó a la empresa
 *                        (fuente: ventas).
 */
public class CertificadoRetencionDTO {

    private String tipo;
    private Integer terceroId;
    private String terceroNit;
    private String terceroNombre;
    private String terceroDireccion;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer anio;

    private long numDocumentos;
    private BigDecimal baseSujeta = BigDecimal.ZERO;
    /** Base específica por concepto: renta e ICA = base gravable; IVA = valor del IVA. */
    private BigDecimal baseRenta = BigDecimal.ZERO;
    private BigDecimal baseIva = BigDecimal.ZERO;
    private BigDecimal baseIca = BigDecimal.ZERO;
    private BigDecimal retefuente = BigDecimal.ZERO;
    private BigDecimal reteiva = BigDecimal.ZERO;
    private BigDecimal reteica = BigDecimal.ZERO;
    private BigDecimal totalRetenido = BigDecimal.ZERO;
    private List<CertificadoRetencionDocDTO> detalle = new ArrayList<>();

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getTerceroId() { return terceroId; }
    public void setTerceroId(Integer terceroId) { this.terceroId = terceroId; }

    public String getTerceroNit() { return terceroNit; }
    public void setTerceroNit(String terceroNit) { this.terceroNit = terceroNit; }

    public String getTerceroNombre() { return terceroNombre; }
    public void setTerceroNombre(String terceroNombre) { this.terceroNombre = terceroNombre; }

    public String getTerceroDireccion() { return terceroDireccion; }
    public void setTerceroDireccion(String terceroDireccion) { this.terceroDireccion = terceroDireccion; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public long getNumDocumentos() { return numDocumentos; }
    public void setNumDocumentos(long numDocumentos) { this.numDocumentos = numDocumentos; }

    public BigDecimal getBaseSujeta() { return baseSujeta; }
    public void setBaseSujeta(BigDecimal baseSujeta) { this.baseSujeta = baseSujeta; }

    public BigDecimal getRetefuente() { return retefuente; }
    public void setRetefuente(BigDecimal retefuente) { this.retefuente = retefuente; }

    public BigDecimal getReteiva() { return reteiva; }
    public void setReteiva(BigDecimal reteiva) { this.reteiva = reteiva; }

    public BigDecimal getReteica() { return reteica; }
    public void setReteica(BigDecimal reteica) { this.reteica = reteica; }

    public BigDecimal getTotalRetenido() { return totalRetenido; }
    public void setTotalRetenido(BigDecimal totalRetenido) { this.totalRetenido = totalRetenido; }

    public BigDecimal getBaseRenta() { return baseRenta; }
    public void setBaseRenta(BigDecimal baseRenta) { this.baseRenta = baseRenta; }
    public BigDecimal getBaseIva() { return baseIva; }
    public void setBaseIva(BigDecimal baseIva) { this.baseIva = baseIva; }
    public BigDecimal getBaseIca() { return baseIca; }
    public void setBaseIca(BigDecimal baseIca) { this.baseIca = baseIca; }
    public List<CertificadoRetencionDocDTO> getDetalle() { return detalle; }
    public void setDetalle(List<CertificadoRetencionDocDTO> detalle) { this.detalle = detalle; }
}
