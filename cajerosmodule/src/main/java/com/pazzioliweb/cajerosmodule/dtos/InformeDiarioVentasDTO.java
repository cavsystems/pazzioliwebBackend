package com.pazzioliweb.cajerosmodule.dtos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO completo del Informe Diario de Ventas (Reporte Z / Cuadre de Caja).
 *
 * Estructura del reporte impreso:
 *  ─ Encabezado (sesión, cajero, fecha, hora, transacciones, zeta)
 *  ─ Movimiento de Cuentas (bruta, descuentos, retenciones, gravadas, exentas, iva, total)
 *  ─ Ventas por Línea (agrupado por línea de producto)
 *  ─ Formas de Pago (por método de pago)
 *  ─ Recibos de Caja (abonos recibidos)
 *  ─ Comprobantes de Egreso (salidas de caja)
 *  ─ Vales
 *  ─ Devoluciones
 *  ─ Resumen Final (neto caja, UPT, VPT, VPU)
 */
public class InformeDiarioVentasDTO {

    // ══════════════════════════════════════════════════════
    //  SECCIÓN 1 — ENCABEZADO / DATOS DE SESIÓN
    // ══════════════════════════════════════════════════════
    private Long     detalleCajeroId;
    private Integer  cajeroId;
    private String   cajeroNombre;       // USUARIO
    private LocalDate fecha;             // FECHA
    private String   hora;               // HORA (HH:mm:ss al generar el informe)
    private Integer  transaccionInicial; // Primer consecutivo de la sesión
    private Integer  transaccionFinal;   // Último consecutivo de la sesión
    private Integer  numeroTransacciones;// Total de transacciones
    private Integer  zeta;               // Contador Z (número de cierres del cajero)
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private String   estadoSesion;       // ABIERTA / CERRADA

    // ══════════════════════════════════════════════════════
    //  SECCIÓN 2 — MOVIMIENTO DE CUENTAS
    // ══════════════════════════════════════════════════════
    public static class MovimientoCuentas {
        /** Suma de totalVenta de todas las ventas completadas de la sesión */
        private BigDecimal totalVentaBruta     = BigDecimal.ZERO;
        /** Suma de descuentos aplicados */
        private BigDecimal totalDescuentos     = BigDecimal.ZERO;
        /** Retenciones (campo reservado, por defecto 0) */
        private BigDecimal totalRetenciones    = BigDecimal.ZERO;
        /** Suma de gravada (base gravable sin IVA) */
        private BigDecimal ventasGravadas      = BigDecimal.ZERO;
        /** Ventas exentas = totalVentaBruta - ventasGravadas - totalIVA */
        private BigDecimal ventasExentas       = BigDecimal.ZERO;
        /** Suma de IVA cobrado */
        private BigDecimal totalIVA            = BigDecimal.ZERO;
        /** Total Ventas = totalVentaBruta − totalDescuentos */
        private BigDecimal totalVentas         = BigDecimal.ZERO;

        public BigDecimal getTotalVentaBruta()  { return totalVentaBruta; }
        public void setTotalVentaBruta(BigDecimal v)  { this.totalVentaBruta = v; }
        public BigDecimal getTotalDescuentos()  { return totalDescuentos; }
        public void setTotalDescuentos(BigDecimal v)  { this.totalDescuentos = v; }
        public BigDecimal getTotalRetenciones() { return totalRetenciones; }
        public void setTotalRetenciones(BigDecimal v) { this.totalRetenciones = v; }
        public BigDecimal getVentasGravadas()   { return ventasGravadas; }
        public void setVentasGravadas(BigDecimal v)   { this.ventasGravadas = v; }
        public BigDecimal getVentasExentas()    { return ventasExentas; }
        public void setVentasExentas(BigDecimal v)    { this.ventasExentas = v; }
        public BigDecimal getTotalIVA()         { return totalIVA; }
        public void setTotalIVA(BigDecimal v)         { this.totalIVA = v; }
        public BigDecimal getTotalVentas()      { return totalVentas; }
        public void setTotalVentas(BigDecimal v)      { this.totalVentas = v; }
    }

    // ══════════════════════════════════════════════════════
    //  SECCIÓN 3 — VENTAS POR LÍNEA
    // ══════════════════════════════════════════════════════
    public static class VentaLinea {
        private String     linea;
        private BigDecimal total = BigDecimal.ZERO;

        public VentaLinea() {}
        public VentaLinea(String linea, BigDecimal total) {
            this.linea = linea;
            this.total = total;
        }
        public String     getLinea()           { return linea; }
        public void       setLinea(String l)   { this.linea = l; }
        public BigDecimal getTotal()           { return total; }
        public void       setTotal(BigDecimal t){ this.total = t; }
    }

    // ══════════════════════════════════════════════════════
    //  SECCIÓN 4 — FORMAS DE PAGO
    // ══════════════════════════════════════════════════════
    public static class FormaPago {
        private String     nombre;
        private BigDecimal total = BigDecimal.ZERO;

        public FormaPago() {}
        public FormaPago(String nombre, BigDecimal total) {
            this.nombre = nombre;
            this.total  = total;
        }
        public String     getNombre()            { return nombre; }
        public void       setNombre(String n)    { this.nombre = n; }
        public BigDecimal getTotal()             { return total; }
        public void       setTotal(BigDecimal t) { this.total = t; }
    }

    // ══════════════════════════════════════════════════════
    //  SECCIÓN 5 — RECIBOS DE CAJA  (ABONOS recibidos)
    // ══════════════════════════════════════════════════════
    public static class ReciboCaja {
        private String     terceroNombre;
        private BigDecimal monto       = BigDecimal.ZERO;
        private String     descripcion; // ej. "CANCELACION FACTURA: 8 1PED"

        public ReciboCaja() {}
        public ReciboCaja(String terceroNombre, BigDecimal monto, String descripcion) {
            this.terceroNombre = terceroNombre;
            this.monto         = monto;
            this.descripcion   = descripcion;
        }
        public String     getTerceroNombre()            { return terceroNombre; }
        public void       setTerceroNombre(String t)    { this.terceroNombre = t; }
        public BigDecimal getMonto()                    { return monto; }
        public void       setMonto(BigDecimal m)        { this.monto = m; }
        public String     getDescripcion()              { return descripcion; }
        public void       setDescripcion(String d)      { this.descripcion = d; }
    }

    public static class SeccionRecibosCaja {
        private List<ReciboCaja> recibos;
        // Totales por forma de pago dentro de los recibos
        private BigDecimal totalEfectivo   = BigDecimal.ZERO;
        private BigDecimal totalTCredito   = BigDecimal.ZERO;
        private BigDecimal totalTDebito    = BigDecimal.ZERO;
        private BigDecimal totalCheque     = BigDecimal.ZERO;
        private BigDecimal totalBancos     = BigDecimal.ZERO;
        private BigDecimal totalDescuentos = BigDecimal.ZERO;
        private BigDecimal totalRecibosCaja= BigDecimal.ZERO;

        public List<ReciboCaja> getRecibos()               { return recibos; }
        public void setRecibos(List<ReciboCaja> r)         { this.recibos = r; }
        public BigDecimal getTotalEfectivo()               { return totalEfectivo; }
        public void setTotalEfectivo(BigDecimal v)         { this.totalEfectivo = v; }
        public BigDecimal getTotalTCredito()               { return totalTCredito; }
        public void setTotalTCredito(BigDecimal v)         { this.totalTCredito = v; }
        public BigDecimal getTotalTDebito()                { return totalTDebito; }
        public void setTotalTDebito(BigDecimal v)          { this.totalTDebito = v; }
        public BigDecimal getTotalCheque()                 { return totalCheque; }
        public void setTotalCheque(BigDecimal v)           { this.totalCheque = v; }
        public BigDecimal getTotalBancos()                 { return totalBancos; }
        public void setTotalBancos(BigDecimal v)           { this.totalBancos = v; }
        public BigDecimal getTotalDescuentos()             { return totalDescuentos; }
        public void setTotalDescuentos(BigDecimal v)       { this.totalDescuentos = v; }
        public BigDecimal getTotalRecibosCaja()            { return totalRecibosCaja; }
        public void setTotalRecibosCaja(BigDecimal v)      { this.totalRecibosCaja = v; }
    }

    // ══════════════════════════════════════════════════════
    //  SECCIÓN 6 — COMPROBANTES DE EGRESO
    // ══════════════════════════════════════════════════════
    public static class ComprobanteEgreso {
        private String     terceroNombre;
        private BigDecimal monto       = BigDecimal.ZERO;
        private String     descripcion;

        public ComprobanteEgreso() {}
        public ComprobanteEgreso(String terceroNombre, BigDecimal monto, String descripcion) {
            this.terceroNombre = terceroNombre;
            this.monto         = monto;
            this.descripcion   = descripcion;
        }
        public String     getTerceroNombre()         { return terceroNombre; }
        public void       setTerceroNombre(String t) { this.terceroNombre = t; }
        public BigDecimal getMonto()                 { return monto; }
        public void       setMonto(BigDecimal m)     { this.monto = m; }
        public String     getDescripcion()           { return descripcion; }
        public void       setDescripcion(String d)   { this.descripcion = d; }
    }

    public static class SeccionEgresos {
        private List<ComprobanteEgreso> egresos;
        private BigDecimal totalEgresos = BigDecimal.ZERO;

        public List<ComprobanteEgreso> getEgresos()      { return egresos; }
        public void setEgresos(List<ComprobanteEgreso> e){ this.egresos = e; }
        public BigDecimal getTotalEgresos()              { return totalEgresos; }
        public void setTotalEgresos(BigDecimal v)        { this.totalEgresos = v; }
    }

    // ══════════════════════════════════════════════════════
    //  SECCIÓN 7 — VALES
    // ══════════════════════════════════════════════════════
    private BigDecimal totalVales = BigDecimal.ZERO;

    // ══════════════════════════════════════════════════════
    //  SECCIÓN 8 — DEVOLUCIONES
    // ══════════════════════════════════════════════════════
    public static class SeccionDevoluciones {
        /** Monto de devoluciones sobre ventas gravadas (base sin IVA) */
        private BigDecimal devGravada       = BigDecimal.ZERO;
        /** IVA sobre devoluciones gravadas */
        private BigDecimal ivaDevGravada    = BigDecimal.ZERO;
        /** Monto de devoluciones sobre ventas exentas */
        private BigDecimal devExentas       = BigDecimal.ZERO;
        /** Total devoluciones pagadas en contado (efectivo) */
        private BigDecimal totalContado     = BigDecimal.ZERO;
        /** Total devoluciones de ventas a crédito (CxC) */
        private BigDecimal totalCxC         = BigDecimal.ZERO;
        /** Descuentos involucrados en devoluciones */
        private BigDecimal totalDsc         = BigDecimal.ZERO;
        /** TOTAL DEVOLUCIONES = devGravada + ivaDevGravada + devExentas */
        private BigDecimal totDevoluciones  = BigDecimal.ZERO;

        public BigDecimal getDevGravada()        { return devGravada; }
        public void setDevGravada(BigDecimal v)        { this.devGravada = v; }
        public BigDecimal getIvaDevGravada()     { return ivaDevGravada; }
        public void setIvaDevGravada(BigDecimal v)     { this.ivaDevGravada = v; }
        public BigDecimal getDevExentas()        { return devExentas; }
        public void setDevExentas(BigDecimal v)        { this.devExentas = v; }
        public BigDecimal getTotalContado()      { return totalContado; }
        public void setTotalContado(BigDecimal v)      { this.totalContado = v; }
        public BigDecimal getTotalCxC()          { return totalCxC; }
        public void setTotalCxC(BigDecimal v)          { this.totalCxC = v; }
        public BigDecimal getTotalDsc()          { return totalDsc; }
        public void setTotalDsc(BigDecimal v)          { this.totalDsc = v; }
        public BigDecimal getTotDevoluciones()   { return totDevoluciones; }
        public void setTotDevoluciones(BigDecimal v)   { this.totDevoluciones = v; }
    }

    // ══════════════════════════════════════════════════════
    //  SECCIÓN 9 — RESUMEN FINAL
    // ══════════════════════════════════════════════════════
    public static class ResumenFinal {
        /**
         * NETO CAJA = totalVentas − totalEgresos + totalRecibosCaja − totDevoluciones
         */
        private BigDecimal netoCaja  = BigDecimal.ZERO;
        /**
         * UPT (Unidades Por Transacción) = totalUnidades / numeroTransacciones * 100
         * Se expresa como porcentaje en el reporte impreso.
         */
        private BigDecimal upt       = BigDecimal.ZERO;
        /**
         * VPT (Valor Por Transacción) = totalVentas / numeroTransacciones
         */
        private BigDecimal vpt       = BigDecimal.ZERO;
        /**
         * VPU (Valor Por Unidad) = totalVentas / totalUnidades
         */
        private BigDecimal vpu       = BigDecimal.ZERO;
        /** Total de unidades vendidas (suma de cantidades en DetalleVenta) */
        private Integer    totalUnidades = 0;

        public BigDecimal getNetoCaja()              { return netoCaja; }
        public void       setNetoCaja(BigDecimal v)  { this.netoCaja = v; }
        public BigDecimal getUpt()                   { return upt; }
        public void       setUpt(BigDecimal v)       { this.upt = v; }
        public BigDecimal getVpt()                   { return vpt; }
        public void       setVpt(BigDecimal v)       { this.vpt = v; }
        public BigDecimal getVpu()                   { return vpu; }
        public void       setVpu(BigDecimal v)       { this.vpu = v; }
        public Integer    getTotalUnidades()         { return totalUnidades; }
        public void       setTotalUnidades(Integer v){ this.totalUnidades = v; }
    }

    // ══════════════════════════════════════════════════════
    //  CAMPOS RAÍZ DEL DTO
    // ══════════════════════════════════════════════════════
    private MovimientoCuentas     movimientoCuentas  = new MovimientoCuentas();
    private List<VentaLinea>      ventasPorLinea;
    private List<FormaPago>       formasDePago;
    private SeccionRecibosCaja    recibosCaja        = new SeccionRecibosCaja();
    private SeccionEgresos        egresos            = new SeccionEgresos();
    private SeccionDevoluciones   devoluciones       = new SeccionDevoluciones();
    private ResumenFinal          resumenFinal       = new ResumenFinal();

    // ══════════════════════════════════════════════════════
    //  GETTERS / SETTERS RAÍZ
    // ══════════════════════════════════════════════════════
    public Long getDetalleCajeroId()                    { return detalleCajeroId; }
    public void setDetalleCajeroId(Long v)              { this.detalleCajeroId = v; }
    public Integer getCajeroId()                        { return cajeroId; }
    public void setCajeroId(Integer v)                  { this.cajeroId = v; }
    public String getCajeroNombre()                     { return cajeroNombre; }
    public void setCajeroNombre(String v)               { this.cajeroNombre = v; }
    public LocalDate getFecha()                         { return fecha; }
    public void setFecha(LocalDate v)                   { this.fecha = v; }
    public String getHora()                             { return hora; }
    public void setHora(String v)                       { this.hora = v; }
    public Integer getTransaccionInicial()              { return transaccionInicial; }
    public void setTransaccionInicial(Integer v)        { this.transaccionInicial = v; }
    public Integer getTransaccionFinal()                { return transaccionFinal; }
    public void setTransaccionFinal(Integer v)          { this.transaccionFinal = v; }
    public Integer getNumeroTransacciones()             { return numeroTransacciones; }
    public void setNumeroTransacciones(Integer v)       { this.numeroTransacciones = v; }
    public Integer getZeta()                            { return zeta; }
    public void setZeta(Integer v)                      { this.zeta = v; }
    public LocalDateTime getFechaApertura()             { return fechaApertura; }
    public void setFechaApertura(LocalDateTime v)       { this.fechaApertura = v; }
    public LocalDateTime getFechaCierre()               { return fechaCierre; }
    public void setFechaCierre(LocalDateTime v)         { this.fechaCierre = v; }
    public String getEstadoSesion()                     { return estadoSesion; }
    public void setEstadoSesion(String v)               { this.estadoSesion = v; }
    public BigDecimal getTotalVales()                   { return totalVales; }
    public void setTotalVales(BigDecimal v)             { this.totalVales = v; }
    public MovimientoCuentas getMovimientoCuentas()     { return movimientoCuentas; }
    public void setMovimientoCuentas(MovimientoCuentas v){ this.movimientoCuentas = v; }
    public List<VentaLinea> getVentasPorLinea()         { return ventasPorLinea; }
    public void setVentasPorLinea(List<VentaLinea> v)   { this.ventasPorLinea = v; }
    public List<FormaPago> getFormasDePago()            { return formasDePago; }
    public void setFormasDePago(List<FormaPago> v)      { this.formasDePago = v; }
    public SeccionRecibosCaja getRecibosCaja()          { return recibosCaja; }
    public void setRecibosCaja(SeccionRecibosCaja v)    { this.recibosCaja = v; }
    public SeccionEgresos getEgresos()                  { return egresos; }
    public void setEgresos(SeccionEgresos v)            { this.egresos = v; }
    public SeccionDevoluciones getDevoluciones()        { return devoluciones; }
    public void setDevoluciones(SeccionDevoluciones v)  { this.devoluciones = v; }
    public ResumenFinal getResumenFinal()               { return resumenFinal; }
    public void setResumenFinal(ResumenFinal v)         { this.resumenFinal = v; }
}

