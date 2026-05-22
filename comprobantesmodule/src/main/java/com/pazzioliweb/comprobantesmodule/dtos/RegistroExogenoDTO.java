package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Registro genérico de Información Exógena DIAN.
 * Cada fila representa un tercero y sus montos por concepto en el año fiscal.
 * Los campos no usados por un formato específico simplemente quedan en cero/null.
 */
@Data
public class RegistroExogenoDTO {
    /** 31=NIT, 13=CC, 22=CE, 12=TI, 41=Pasaporte */
    private String tipoDocumento;
    private String numeroIdentificacion;
    private String dv;
    private String razonSocial;

    /** Código de concepto DIAN específico del formato */
    private String concepto;

    /** Para formato 1001 (pagos a terceros) */
    private BigDecimal pagosSujetosARetencion = BigDecimal.ZERO;
    private BigDecimal pagosNoSujetosARetencion = BigDecimal.ZERO;
    private BigDecimal retencionesPracticadas = BigDecimal.ZERO;
    private BigDecimal retencionIcaPracticada = BigDecimal.ZERO;
    private BigDecimal ivaMayorValorCosto = BigDecimal.ZERO;
    private BigDecimal ivaDescontable = BigDecimal.ZERO;

    /** Para formato 1007 (ingresos) */
    private BigDecimal ingresosBrutos = BigDecimal.ZERO;
    private BigDecimal devolucionesRebajasDescuentos = BigDecimal.ZERO;
    private BigDecimal ivaGenerado = BigDecimal.ZERO;

    /** Para formato 1008 (CxC) / 1009 (CxP) — saldo al 31/dic */
    private BigDecimal saldo = BigDecimal.ZERO;

    /** Para formato 1003 — retenciones */
    private BigDecimal valorBaseRetencion = BigDecimal.ZERO;
}
