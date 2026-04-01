package com.pazzioliweb.cajerosmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta del cuadre de caja.
 * Agrupa los 3 componentes básicos del cuadre POS:
 *   1. Efectivo   → base + ingresos efectivo − egresos efectivo
 *   2. Electrónico → total recibido por medios electrónicos
 *   3. Diferencia  → declarado − esperado (sobrante/faltante)
 * Más el desglose de los 6 tipos de documento.
 */
public class CuadreCajaDTO {

    // ────── Datos de la sesión ──────
    private Long detalleCajeroId;
    private Integer cajeroId;
    private String cajeroNombre;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private String estado;
    private Integer totalTransacciones; // consecutivo final

    // ────── Componente 1: EFECTIVO ──────
    private BigDecimal baseCaja;           // Monto con el que abrió el cajero
    private BigDecimal totalEfectivo;      // Ingresado en efectivo durante la sesión
    private BigDecimal efectivoEsperado;   // baseCaja + totalEfectivo
    private BigDecimal efectivoDeclarado;  // Lo que contó el cajero al cierre
    private BigDecimal diferenciaEfectivo; // declarado − esperado

    // ────── Componente 2: MEDIOS ELECTRÓNICOS ──────
    private BigDecimal totalMediosElectronicos;      // Total por tarjeta/transferencia
    private BigDecimal mediosElectronicosDeclarado;  // Lo que reportó el cajero
    private BigDecimal diferenciaMediosElectronicos; // declarado − esperado

    // ────── Componente 3: TOTALES GENERALES ──────
    private BigDecimal totalRecaudo;  // Suma de todos los ingresos (efectivo + electrónico)
    private BigDecimal totalCosto;    // Suma de costos de las transacciones
    private BigDecimal montoFinal;    // Total esperado en caja al cierre

    // ────── Desglose por tipo de documento (6 documentos POS) ──────
    private List<ResumenTipoDocumento> desglosePorTipo;

    // ────── Lista de movimientos individuales ──────
    private List<MovimientoCajeroDTO> movimientos;

    /**
     * Resumen por tipo de documento POS:
     * VENTA, COTIZACION, PEDIDO, CUENTA_POR_COBRAR, EGRESO, DEVOLUCION
     */
    public static class ResumenTipoDocumento {
        private String tipoMovimiento;
        private String descripcion;
        private Integer cantidad;          // Número de transacciones de este tipo
        private BigDecimal totalMonto;     // Suma de montos
        private BigDecimal totalEfectivo;
        private BigDecimal totalElectronico;
        private boolean afectaCaja;        // Si suma/resta al cuadre
        private int signo;                 // +1 o -1

        public ResumenTipoDocumento() {}

        public ResumenTipoDocumento(String tipoMovimiento, String descripcion,
                                     Integer cantidad, BigDecimal totalMonto,
                                     BigDecimal totalEfectivo, BigDecimal totalElectronico,
                                     boolean afectaCaja, int signo) {
            this.tipoMovimiento = tipoMovimiento;
            this.descripcion = descripcion;
            this.cantidad = cantidad;
            this.totalMonto = totalMonto;
            this.totalEfectivo = totalEfectivo;
            this.totalElectronico = totalElectronico;
            this.afectaCaja = afectaCaja;
            this.signo = signo;
        }

        public String getTipoMovimiento() { return tipoMovimiento; }
        public void setTipoMovimiento(String t) { this.tipoMovimiento = t; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String d) { this.descripcion = d; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer c) { this.cantidad = c; }
        public BigDecimal getTotalMonto() { return totalMonto; }
        public void setTotalMonto(BigDecimal t) { this.totalMonto = t; }
        public BigDecimal getTotalEfectivo() { return totalEfectivo; }
        public void setTotalEfectivo(BigDecimal t) { this.totalEfectivo = t; }
        public BigDecimal getTotalElectronico() { return totalElectronico; }
        public void setTotalElectronico(BigDecimal t) { this.totalElectronico = t; }
        public boolean isAfectaCaja() { return afectaCaja; }
        public void setAfectaCaja(boolean a) { this.afectaCaja = a; }
        public int getSigno() { return signo; }
        public void setSigno(int s) { this.signo = s; }
    }

    // ────── Getters/Setters ──────
    public Long getDetalleCajeroId() { return detalleCajeroId; }
    public void setDetalleCajeroId(Long d) { this.detalleCajeroId = d; }
    public Integer getCajeroId() { return cajeroId; }
    public void setCajeroId(Integer c) { this.cajeroId = c; }
    public String getCajeroNombre() { return cajeroNombre; }
    public void setCajeroNombre(String c) { this.cajeroNombre = c; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime f) { this.fechaApertura = f; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime f) { this.fechaCierre = f; }
    public String getEstado() { return estado; }
    public void setEstado(String e) { this.estado = e; }
    public Integer getTotalTransacciones() { return totalTransacciones; }
    public void setTotalTransacciones(Integer t) { this.totalTransacciones = t; }
    public BigDecimal getBaseCaja() { return baseCaja; }
    public void setBaseCaja(BigDecimal b) { this.baseCaja = b; }
    public BigDecimal getTotalEfectivo() { return totalEfectivo; }
    public void setTotalEfectivo(BigDecimal t) { this.totalEfectivo = t; }
    public BigDecimal getEfectivoEsperado() { return efectivoEsperado; }
    public void setEfectivoEsperado(BigDecimal e) { this.efectivoEsperado = e; }
    public BigDecimal getEfectivoDeclarado() { return efectivoDeclarado; }
    public void setEfectivoDeclarado(BigDecimal e) { this.efectivoDeclarado = e; }
    public BigDecimal getDiferenciaEfectivo() { return diferenciaEfectivo; }
    public void setDiferenciaEfectivo(BigDecimal d) { this.diferenciaEfectivo = d; }
    public BigDecimal getTotalMediosElectronicos() { return totalMediosElectronicos; }
    public void setTotalMediosElectronicos(BigDecimal t) { this.totalMediosElectronicos = t; }
    public BigDecimal getMediosElectronicosDeclarado() { return mediosElectronicosDeclarado; }
    public void setMediosElectronicosDeclarado(BigDecimal m) { this.mediosElectronicosDeclarado = m; }
    public BigDecimal getDiferenciaMediosElectronicos() { return diferenciaMediosElectronicos; }
    public void setDiferenciaMediosElectronicos(BigDecimal d) { this.diferenciaMediosElectronicos = d; }
    public BigDecimal getTotalRecaudo() { return totalRecaudo; }
    public void setTotalRecaudo(BigDecimal t) { this.totalRecaudo = t; }
    public BigDecimal getTotalCosto() { return totalCosto; }
    public void setTotalCosto(BigDecimal t) { this.totalCosto = t; }
    public BigDecimal getMontoFinal() { return montoFinal; }
    public void setMontoFinal(BigDecimal m) { this.montoFinal = m; }
    public List<ResumenTipoDocumento> getDesglosePorTipo() { return desglosePorTipo; }
    public void setDesglosePorTipo(List<ResumenTipoDocumento> d) { this.desglosePorTipo = d; }
    public List<MovimientoCajeroDTO> getMovimientos() { return movimientos; }
    public void setMovimientos(List<MovimientoCajeroDTO> m) { this.movimientos = m; }
}

