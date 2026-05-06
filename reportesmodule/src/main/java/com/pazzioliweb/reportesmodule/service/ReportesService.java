package com.pazzioliweb.reportesmodule.service;

import com.pazzioliweb.reportesmodule.dtos.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportesService {

    /** Dashboard KPI principal */
    DashboardResumenDTO getDashboard(LocalDate inicio, LocalDate fin);

    /** Ventas agrupadas por día */
    List<VentasPorPeriodoDTO> getVentasPorDia(LocalDate inicio, LocalDate fin);

    /** Ventas agrupadas por mes */
    List<VentasPorPeriodoDTO> getVentasPorMes(LocalDate inicio, LocalDate fin);

    /** Ventas agrupadas por hora del día (horarios pico) */
    List<VentasPorPeriodoDTO> getVentasPorHora(LocalDate inicio, LocalDate fin);

    /** Top N productos más vendidos */
    List<ProductoMasVendidoDTO> getTopProductos(LocalDate inicio, LocalDate fin, int topN);

    /** Ventas agrupadas por vendedor */
    List<VentasPorVendedorDTO> getVentasPorVendedor(LocalDate inicio, LocalDate fin);

    /** Ventas agrupadas por cajero */
    List<VentasPorCajeroDTO> getVentasPorCajero(LocalDate inicio, LocalDate fin);

    /** Ventas agrupadas por método de pago */
    List<VentasPorMetodoPagoDTO> getVentasPorMetodoPago(LocalDate inicio, LocalDate fin);

    /** Top N clientes por monto comprado */
    List<VentasPorClienteDTO> getTopClientes(LocalDate inicio, LocalDate fin, int topN);

    /** Ventas agrupadas por línea de producto */
    List<VentasPorCategoriaDTO> getVentasPorLinea(LocalDate inicio, LocalDate fin);

    /** Cartera — cuentas por cobrar agrupadas por estado */
    List<ReporteCarteraDTO> getCarteraPorEstado();

    /** Compras agrupadas por proveedor */
    List<ComprasPorProveedorDTO> getComprasPorProveedor(LocalDate inicio, LocalDate fin, int topN);

    /** Comparativa compras vs ventas por mes */
    List<ComprasVsVentasDTO> getComprasVsVentas(LocalDate inicio, LocalDate fin);

    /** Productos con stock bajo — listado detallado */
    List<ProductoStockBajoDTO> getProductosStockBajo(int limite);

    /** Movimientos de caja agrupados por tipo */
    List<MovimientoCajaTipoDTO> getMovimientosCajaPorTipo(LocalDate inicio, LocalDate fin);

    /** Rentabilidad por producto (margen de ganancia) */
    List<RentabilidadProductoDTO> getRentabilidadProductos(LocalDate inicio, LocalDate fin, int topN);

    /** Ventas por día de la semana */
    List<VentasPorPeriodoDTO> getVentasPorDiaSemana(LocalDate inicio, LocalDate fin);

    // ── Reportes nuevos ───────────────────────────────────────────

    /** Comparativo periodo actual vs periodo anterior. */
    ComparativoPeriodosDTO getComparativoPeriodos(LocalDate actualInicio, LocalDate actualFin,
                                                  LocalDate anteriorInicio, LocalDate anteriorFin);

    /** Lista unificada de documentos anulados (ventas + recibos + egresos). */
    List<AnulacionDTO> getAnulaciones(LocalDate inicio, LocalDate fin);

    /** Uso de conceptos abiertos en el periodo (cuántos / monto). */
    List<ConceptoAbiertoUsoDTO> getConceptosAbiertosUso(LocalDate inicio, LocalDate fin, String tipo);

    /** Devoluciones detalladas. */
    List<DevolucionDetalladaDTO> getDevolucionesDetalladas(LocalDate inicio, LocalDate fin);

    /** Ticket promedio por cajero o vendedor. */
    List<TicketPromedioDTO> getTicketPromedio(LocalDate inicio, LocalDate fin, String agrupacion);

    /** Cartera por antigüedad (aging buckets). */
    List<CarteraAgingDTO> getCarteraAging();

    /** Productos activos que no han tenido ventas en el periodo. */
    List<ProductoSinMovimientoDTO> getProductosSinMovimiento(LocalDate inicio, LocalDate fin, int topN);
}

