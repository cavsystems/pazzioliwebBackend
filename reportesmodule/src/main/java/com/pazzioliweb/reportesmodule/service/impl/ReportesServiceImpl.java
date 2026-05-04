package com.pazzioliweb.reportesmodule.service.impl;

import com.pazzioliweb.reportesmodule.dtos.*;
import com.pazzioliweb.reportesmodule.repository.ReportesRepository;
import com.pazzioliweb.reportesmodule.service.ReportesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportesServiceImpl implements ReportesService {

    private final ReportesRepository repo;

    public ReportesServiceImpl(ReportesRepository repo) {
        this.repo = repo;
    }

    // ════════════════════════════════════════════════════════════
    // DASHBOARD KPI
    // ════════════════════════════════════════════════════════════

    @Override
    public DashboardResumenDTO getDashboard(LocalDate inicio, LocalDate fin) {
        DashboardResumenDTO dto = new DashboardResumenDTO();

        // Ventas
        BigDecimal totalVentas = repo.totalVentasPeriodo(inicio, fin);
        Long cantidadVentas = repo.cantidadVentasPeriodo(inicio, fin);
        BigDecimal totalCosto = repo.totalCostoPeriodo(inicio, fin);

        dto.setTotalVentas(totalVentas);
        dto.setCantidadVentas(cantidadVentas);
        dto.setTotalCosto(totalCosto);

        // Devoluciones
        List<Object[]> devRows = repo.totalDevolucionesPeriodo(inicio, fin);
        BigDecimal totalDevoluciones = BigDecimal.ZERO;
        Long cantidadDevoluciones = 0L;
        if (devRows != null && !devRows.isEmpty()) {
            Object[] row = devRows.get(0);
            totalDevoluciones = toBigDecimal(row[0]);
            cantidadDevoluciones = toLong(row[1]);
        }
        dto.setTotalDevoluciones(totalDevoluciones);
        dto.setCantidadDevoluciones(cantidadDevoluciones);

        // Venta neta y utilidad
        BigDecimal ventaNeta = totalVentas.subtract(totalDevoluciones);
        dto.setVentaNeta(ventaNeta);
        BigDecimal utilidadBruta = ventaNeta.subtract(totalCosto);
        dto.setUtilidadBruta(utilidadBruta);
        dto.setMargenPorcentaje(ventaNeta.compareTo(BigDecimal.ZERO) > 0
                ? utilidadBruta.multiply(BigDecimal.valueOf(100))
                    .divide(ventaNeta, 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0);

        // Desglose pagos: efectivo, electrónico, crédito
        List<Object[]> pagos = repo.desglosePagos(inicio, fin);
        if (pagos != null && !pagos.isEmpty()) {
            Object[] p = pagos.get(0);
            dto.setTotalEfectivo(toBigDecimal(p[0]));
            dto.setTotalCredito(toBigDecimal(p[1]));
            dto.setTotalMediosElectronicos(toBigDecimal(p[2]));
        } else {
            dto.setTotalEfectivo(BigDecimal.ZERO);
            dto.setTotalCredito(BigDecimal.ZERO);
            dto.setTotalMediosElectronicos(BigDecimal.ZERO);
        }

        // Inventario
        dto.setValorInventario(repo.valorTotalInventario());
        dto.setProductosConStockBajo(repo.contarProductosStockBajo());

        // Cartera
        dto.setCarteraPendiente(repo.carteraPendienteTotal());
        dto.setCuentasPorCobrarVencidas(repo.contarCuentasVencidas());

        // Contadores
        dto.setTotalClientes(repo.contarClientes());
        dto.setTotalProductosActivos(repo.contarProductosActivos());

        // Ticket promedio
        dto.setTicketPromedio(cantidadVentas > 0
                ? totalVentas.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        // Compras
        dto.setTotalCompras(repo.totalComprasPeriodo(inicio, fin));
        dto.setCuentasPorPagarPendientes(repo.cuentasPorPagarTotal());

        return dto;
    }

    // ════════════════════════════════════════════════════════════
    // VENTAS POR DÍA
    // ════════════════════════════════════════════════════════════

    @Override
    public List<VentasPorPeriodoDTO> getVentasPorDia(LocalDate inicio, LocalDate fin) {
        return mapPeriodo(repo.ventasPorDia(inicio, fin));
    }

    @Override
    public List<VentasPorPeriodoDTO> getVentasPorMes(LocalDate inicio, LocalDate fin) {
        return mapPeriodo(repo.ventasPorMes(inicio, fin));
    }

    @Override
    public List<VentasPorPeriodoDTO> getVentasPorHora(LocalDate inicio, LocalDate fin) {
        return mapPeriodo(repo.ventasPorHora(inicio, fin));
    }

    // ════════════════════════════════════════════════════════════
    // TOP PRODUCTOS
    // ════════════════════════════════════════════════════════════

    @Override
    public List<ProductoMasVendidoDTO> getTopProductos(LocalDate inicio, LocalDate fin, int topN) {
        List<ProductoMasVendidoDTO> result = new ArrayList<>();
        for (Object[] r : repo.topProductosMasVendidos(inicio, fin, topN)) {
            BigDecimal totalVendido = toBigDecimal(r[5]);
            BigDecimal costoTotal = toBigDecimal(r[6]);
            result.add(new ProductoMasVendidoDTO(
                    str(r[0]),       // codigoProducto
                    str(r[1]),       // descripcion
                    str(r[2]),       // linea
                    str(r[3]),       // grupo
                    toLong(r[4]),    // cantidadVendida
                    totalVendido,
                    costoTotal,
                    totalVendido.subtract(costoTotal), // utilidad
                    str(r[7])        // imagen
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // VENTAS POR VENDEDOR
    // ════════════════════════════════════════════════════════════

    @Override
    public List<VentasPorVendedorDTO> getVentasPorVendedor(LocalDate inicio, LocalDate fin) {
        List<VentasPorVendedorDTO> result = new ArrayList<>();
        for (Object[] r : repo.ventasPorVendedor(inicio, fin)) {
            Long cant = toLong(r[2]);
            BigDecimal total = toBigDecimal(r[3]);
            result.add(new VentasPorVendedorDTO(
                    toInt(r[0]),
                    str(r[1]),
                    cant,
                    total,
                    cant > 0 ? total.divide(BigDecimal.valueOf(cant), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // VENTAS POR CAJERO
    // ════════════════════════════════════════════════════════════

    @Override
    public List<VentasPorCajeroDTO> getVentasPorCajero(LocalDate inicio, LocalDate fin) {
        List<VentasPorCajeroDTO> result = new ArrayList<>();
        for (Object[] r : repo.ventasPorCajero(inicio, fin)) {
            result.add(new VentasPorCajeroDTO(
                    toInt(r[0]),
                    str(r[1]),
                    toLong(r[2]),
                    toBigDecimal(r[3]),
                    toBigDecimal(r[4]),    // totalEfectivo
                    toBigDecimal(r[5])     // totalElectronico
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // VENTAS POR MÉTODO DE PAGO
    // ════════════════════════════════════════════════════════════

    @Override
    public List<VentasPorMetodoPagoDTO> getVentasPorMetodoPago(LocalDate inicio, LocalDate fin) {
        List<Object[]> rows = repo.ventasPorMetodoPago(inicio, fin);
        BigDecimal granTotal = rows.stream()
                .map(r -> toBigDecimal(r[4]))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<VentasPorMetodoPagoDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            BigDecimal monto = toBigDecimal(r[4]);
            double pct = granTotal.compareTo(BigDecimal.ZERO) > 0
                    ? monto.multiply(BigDecimal.valueOf(100))
                        .divide(granTotal, 2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;
            result.add(new VentasPorMetodoPagoDTO(
                    toInt(r[0]),
                    str(r[1]),
                    str(r[2]),
                    toLong(r[3]),
                    monto,
                    pct
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // TOP CLIENTES
    // ════════════════════════════════════════════════════════════

    @Override
    public List<VentasPorClienteDTO> getTopClientes(LocalDate inicio, LocalDate fin, int topN) {
        List<VentasPorClienteDTO> result = new ArrayList<>();
        for (Object[] r : repo.topClientes(inicio, fin, topN)) {
            result.add(new VentasPorClienteDTO(
                    toInt(r[0]),
                    str(r[1]),
                    str(r[2]),
                    toLong(r[3]),
                    toBigDecimal(r[4]),
                    toBigDecimal(r[5])     // saldo cartera real
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // VENTAS POR LÍNEA
    // ════════════════════════════════════════════════════════════

    @Override
    public List<VentasPorCategoriaDTO> getVentasPorLinea(LocalDate inicio, LocalDate fin) {
        List<Object[]> rows = repo.ventasPorLinea(inicio, fin);
        BigDecimal granTotal = rows.stream()
                .map(r -> toBigDecimal(r[2]))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<VentasPorCategoriaDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            BigDecimal total = toBigDecimal(r[2]);
            double pct = granTotal.compareTo(BigDecimal.ZERO) > 0
                    ? total.multiply(BigDecimal.valueOf(100))
                        .divide(granTotal, 2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;
            result.add(new VentasPorCategoriaDTO(
                    str(r[0]),
                    toLong(r[1]),
                    total,
                    pct
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // CARTERA POR ESTADO
    // ════════════════════════════════════════════════════════════

    @Override
    public List<ReporteCarteraDTO> getCarteraPorEstado() {
        List<ReporteCarteraDTO> result = new ArrayList<>();
        for (Object[] r : repo.carteraPorEstado()) {
            result.add(new ReporteCarteraDTO(
                    str(r[0]),
                    toLong(r[1]),
                    toBigDecimal(r[2]),
                    toBigDecimal(r[3])
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // COMPRAS POR PROVEEDOR
    // ════════════════════════════════════════════════════════════

    @Override
    public List<ComprasPorProveedorDTO> getComprasPorProveedor(LocalDate inicio, LocalDate fin, int topN) {
        List<ComprasPorProveedorDTO> result = new ArrayList<>();
        for (Object[] r : repo.comprasPorProveedor(inicio, fin, topN)) {
            result.add(new ComprasPorProveedorDTO(
                    toInt(r[0]),
                    str(r[1]),
                    toLong(r[2]),
                    toBigDecimal(r[3])
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // COMPRAS VS VENTAS POR MES
    // ════════════════════════════════════════════════════════════

    @Override
    public List<ComprasVsVentasDTO> getComprasVsVentas(LocalDate inicio, LocalDate fin) {
        List<ComprasVsVentasDTO> result = new ArrayList<>();
        for (Object[] r : repo.comprasVsVentasPorMes(inicio, fin)) {
            BigDecimal ventas = toBigDecimal(r[1]);
            BigDecimal compras = toBigDecimal(r[2]);
            result.add(new ComprasVsVentasDTO(
                    str(r[0]),
                    ventas,
                    compras,
                    ventas.subtract(compras)
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // PRODUCTOS CON STOCK BAJO
    // ════════════════════════════════════════════════════════════

    @Override
    public List<ProductoStockBajoDTO> getProductosStockBajo(int limite) {
        List<ProductoStockBajoDTO> result = new ArrayList<>();
        for (Object[] r : repo.productosStockBajo(limite)) {
            result.add(new ProductoStockBajoDTO(
                    toLong(r[0]),
                    str(r[1]),
                    str(r[2]),
                    str(r[3]),
                    str(r[4]),
                    toBigDecimal(r[5]),
                    toBigDecimal(r[6]),
                    toBigDecimal(r[7]),
                    toBigDecimal(r[8]),
                    str(r[9])
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // MOVIMIENTOS DE CAJA POR TIPO
    // ════════════════════════════════════════════════════════════

    @Override
    public List<MovimientoCajaTipoDTO> getMovimientosCajaPorTipo(LocalDate inicio, LocalDate fin) {
        List<MovimientoCajaTipoDTO> result = new ArrayList<>();
        for (Object[] r : repo.movimientosCajaPorTipo(inicio, fin)) {
            result.add(new MovimientoCajaTipoDTO(
                    str(r[0]),
                    toLong(r[1]),
                    toBigDecimal(r[2]),
                    toBigDecimal(r[3]),
                    toBigDecimal(r[4])
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // RENTABILIDAD POR PRODUCTO
    // ════════════════════════════════════════════════════════════

    @Override
    public List<RentabilidadProductoDTO> getRentabilidadProductos(LocalDate inicio, LocalDate fin, int topN) {
        List<RentabilidadProductoDTO> result = new ArrayList<>();
        for (Object[] r : repo.rentabilidadPorProducto(inicio, fin, topN)) {
            BigDecimal ingresos = toBigDecimal(r[4]);
            BigDecimal costo = toBigDecimal(r[5]);
            BigDecimal utilidad = ingresos.subtract(costo);
            double margen = ingresos.compareTo(BigDecimal.ZERO) > 0
                    ? utilidad.multiply(BigDecimal.valueOf(100))
                        .divide(ingresos, 2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;
            result.add(new RentabilidadProductoDTO(
                    str(r[0]),
                    str(r[1]),
                    str(r[2]),
                    toLong(r[3]),
                    ingresos,
                    costo,
                    utilidad,
                    margen
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // VENTAS POR DÍA DE LA SEMANA
    // ════════════════════════════════════════════════════════════

    @Override
    public List<VentasPorPeriodoDTO> getVentasPorDiaSemana(LocalDate inicio, LocalDate fin) {
        List<VentasPorPeriodoDTO> result = new ArrayList<>();
        for (Object[] r : repo.ventasPorDiaSemana(inicio, fin)) {
            result.add(new VentasPorPeriodoDTO(
                    str(r[1]),       // nombreDia (Lunes, Martes...)
                    toBigDecimal(r[2]),
                    toLong(r[3]),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // UTILIDADES PRIVADAS
    // ════════════════════════════════════════════════════════════

    private List<VentasPorPeriodoDTO> mapPeriodo(List<Object[]> rows) {
        List<VentasPorPeriodoDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            BigDecimal total = toBigDecimal(r[1]);
            BigDecimal costo = toBigDecimal(r[3]);
            result.add(new VentasPorPeriodoDTO(
                    str(r[0]),
                    total,
                    toLong(r[2]),
                    costo,
                    total.subtract(costo)
            ));
        }
        return result;
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal bd) return bd;
        return new BigDecimal(obj.toString());
    }

    private Long toLong(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Long l) return l;
        return Long.parseLong(obj.toString());
    }

    private Integer toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Integer i) return i;
        return Integer.parseInt(obj.toString());
    }

    private String str(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private LocalDateTime toLocalDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof LocalDateTime ldt) return ldt;
        if (obj instanceof Timestamp ts) return ts.toLocalDateTime();
        if (obj instanceof java.sql.Date d) return d.toLocalDate().atStartOfDay();
        return LocalDateTime.parse(obj.toString().replace(" ", "T"));
    }

    // ════════════════════════════════════════════════════════════
    // COMPARATIVO PERIODOS
    // ════════════════════════════════════════════════════════════

    @Override
    public ComparativoPeriodosDTO getComparativoPeriodos(LocalDate actualInicio, LocalDate actualFin,
                                                         LocalDate anteriorInicio, LocalDate anteriorFin) {
        ComparativoPeriodosDTO dto = new ComparativoPeriodosDTO();
        dto.setEtiquetaActual(actualInicio + " a " + actualFin);
        dto.setEtiquetaAnterior(anteriorInicio + " a " + anteriorFin);

        BigDecimal vActual = repo.totalVentasPeriodo(actualInicio, actualFin);
        BigDecimal vAnt    = repo.totalVentasPeriodo(anteriorInicio, anteriorFin);
        Long cActual       = repo.cantidadVentasPeriodo(actualInicio, actualFin);
        Long cAnt          = repo.cantidadVentasPeriodo(anteriorInicio, anteriorFin);
        BigDecimal costoActual = repo.totalCostoPeriodo(actualInicio, actualFin);
        BigDecimal costoAnt    = repo.totalCostoPeriodo(anteriorInicio, anteriorFin);

        BigDecimal devActual = BigDecimal.ZERO;
        BigDecimal devAnt    = BigDecimal.ZERO;
        List<Object[]> da = repo.totalDevolucionesPeriodo(actualInicio, actualFin);
        if (da != null && !da.isEmpty()) devActual = toBigDecimal(da.get(0)[0]);
        List<Object[]> dn = repo.totalDevolucionesPeriodo(anteriorInicio, anteriorFin);
        if (dn != null && !dn.isEmpty()) devAnt = toBigDecimal(dn.get(0)[0]);

        BigDecimal utilActual = vActual.subtract(devActual).subtract(costoActual);
        BigDecimal utilAnt    = vAnt.subtract(devAnt).subtract(costoAnt);

        dto.setVentasActual(vActual);
        dto.setVentasAnterior(vAnt);
        dto.setDeltaVentas(porcentajeCambio(vActual, vAnt));

        dto.setCantidadVentasActual(cActual);
        dto.setCantidadVentasAnterior(cAnt);
        dto.setDeltaCantidadVentas(porcentajeCambio(BigDecimal.valueOf(cActual), BigDecimal.valueOf(cAnt)));

        BigDecimal tpA = cActual > 0 ? vActual.divide(BigDecimal.valueOf(cActual), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal tpN = cAnt    > 0 ? vAnt.divide(BigDecimal.valueOf(cAnt),    2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        dto.setTicketPromedioActual(tpA);
        dto.setTicketPromedioAnterior(tpN);
        dto.setDeltaTicketPromedio(porcentajeCambio(tpA, tpN));

        dto.setDevolucionesActual(devActual);
        dto.setDevolucionesAnterior(devAnt);
        dto.setDeltaDevoluciones(porcentajeCambio(devActual, devAnt));

        dto.setUtilidadActual(utilActual);
        dto.setUtilidadAnterior(utilAnt);
        dto.setDeltaUtilidad(porcentajeCambio(utilActual, utilAnt));

        return dto;
    }

    private double porcentajeCambio(BigDecimal actual, BigDecimal anterior) {
        if (anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return actual != null && actual.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return actual.subtract(anterior)
                .multiply(BigDecimal.valueOf(100))
                .divide(anterior, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // ════════════════════════════════════════════════════════════
    // ANULACIONES
    // ════════════════════════════════════════════════════════════

    @Override
    public List<AnulacionDTO> getAnulaciones(LocalDate inicio, LocalDate fin) {
        List<AnulacionDTO> result = new ArrayList<>();
        for (Object[] r : repo.anulacionesEnPeriodo(inicio, fin)) {
            result.add(new AnulacionDTO(
                    str(r[0]),
                    toLong(r[1]),
                    str(r[2]),
                    toLocalDateTime(r[3]),
                    toLocalDateTime(r[4]),
                    str(r[5]),
                    toBigDecimal(r[6]),
                    str(r[7]),
                    r[8] != null ? toInt(r[8]) : null,
                    str(r[9])
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // CONCEPTOS ABIERTOS — USO
    // ════════════════════════════════════════════════════════════

    @Override
    public List<ConceptoAbiertoUsoDTO> getConceptosAbiertosUso(LocalDate inicio, LocalDate fin, String tipo) {
        String tipoFiltro = tipo == null || tipo.isBlank() ? "TODOS" : tipo.toUpperCase();
        List<ConceptoAbiertoUsoDTO> result = new ArrayList<>();
        for (Object[] r : repo.conceptosAbiertosUso(inicio, fin, tipoFiltro)) {
            result.add(new ConceptoAbiertoUsoDTO(
                    toLong(r[0]),
                    str(r[1]),
                    str(r[2]),
                    str(r[3]),
                    str(r[4]),
                    toLong(r[5]),
                    toBigDecimal(r[6])
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // DEVOLUCIONES DETALLADAS
    // ════════════════════════════════════════════════════════════

    @Override
    public List<DevolucionDetalladaDTO> getDevolucionesDetalladas(LocalDate inicio, LocalDate fin) {
        List<DevolucionDetalladaDTO> result = new ArrayList<>();
        for (Object[] r : repo.devolucionesDetalladas(inicio, fin)) {
            result.add(new DevolucionDetalladaDTO(
                    toLong(r[0]),
                    toLocalDateTime(r[1]),
                    str(r[2]),
                    str(r[3]),
                    str(r[4]),
                    toBigDecimal(r[5]),
                    toLong(r[6]),
                    str(r[7])
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════
    // TICKET PROMEDIO
    // ════════════════════════════════════════════════════════════

    @Override
    public List<TicketPromedioDTO> getTicketPromedio(LocalDate inicio, LocalDate fin, String agrupacion) {
        List<Object[]> rows = "vendedor".equalsIgnoreCase(agrupacion)
                ? repo.ticketPromedioPorVendedor(inicio, fin)
                : repo.ticketPromedioPorCajero(inicio, fin);

        List<TicketPromedioDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            Long cantidad = toLong(r[1]);
            BigDecimal total = toBigDecimal(r[2]);
            Long unidades = toLong(r[3]);
            BigDecimal ticketProm = cantidad > 0
                    ? total.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            BigDecimal upt = cantidad > 0
                    ? BigDecimal.valueOf(unidades).divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            result.add(new TicketPromedioDTO(
                    str(r[0]),
                    cantidad,
                    total,
                    ticketProm,
                    upt,
                    unidades
            ));
        }
        return result;
    }
}

