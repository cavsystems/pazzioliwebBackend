package com.pazzioliweb.reportesmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Una fila del detalle documento-por-documento de un certificado de retención. */
public class CertificadoRetencionDocDTO {
    private String numero;
    private LocalDate fecha;
    private BigDecimal base = BigDecimal.ZERO;
    private BigDecimal iva = BigDecimal.ZERO;
    private BigDecimal retefuente = BigDecimal.ZERO;
    private BigDecimal reteiva = BigDecimal.ZERO;
    private BigDecimal reteica = BigDecimal.ZERO;

    public CertificadoRetencionDocDTO() {}

    public CertificadoRetencionDocDTO(String numero, LocalDate fecha, BigDecimal base, BigDecimal iva,
                                      BigDecimal retefuente, BigDecimal reteiva, BigDecimal reteica) {
        this.numero = numero; this.fecha = fecha; this.base = base; this.iva = iva;
        this.retefuente = retefuente; this.reteiva = reteiva; this.reteica = reteica;
    }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public BigDecimal getBase() { return base; }
    public void setBase(BigDecimal base) { this.base = base; }
    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
    public BigDecimal getRetefuente() { return retefuente; }
    public void setRetefuente(BigDecimal retefuente) { this.retefuente = retefuente; }
    public BigDecimal getReteiva() { return reteiva; }
    public void setReteiva(BigDecimal reteiva) { this.reteiva = reteiva; }
    public BigDecimal getReteica() { return reteica; }
    public void setReteica(BigDecimal reteica) { this.reteica = reteica; }
}
