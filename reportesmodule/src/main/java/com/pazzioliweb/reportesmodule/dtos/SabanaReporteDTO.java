package com.pazzioliweb.reportesmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Reporte consolidado (sábana) — agrupa en un solo payload las secciones
 * más relevantes para un rango de fechas. El frontend lo convierte en un
 * Excel multi-hoja para enviar al contador / gerencia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SabanaReporteDTO {
    private LocalDate inicio;
    private LocalDate fin;

    private DashboardResumenDTO resumen;
    private List<VentasPorPeriodoDTO> ventasPorDia;
    private List<VentasPorMetodoPagoDTO> metodosPago;
    private List<VentasPorVendedorDTO> ventasPorVendedor;
    private List<VentasPorCajeroDTO> ventasPorCajero;
    private List<VentasPorClienteDTO> topClientes;
    private List<ProductoMasVendidoDTO> topProductos;
    private List<VentasPorCategoriaDTO> ventasPorLinea;
    private List<RentabilidadProductoDTO> rentabilidad;
    private List<ComprasPorProveedorDTO> comprasPorProveedor;
    private List<MovimientoCajaTipoDTO> movimientosCaja;
    private List<ReporteCarteraDTO> carteraPorEstado;
    private List<CarteraDetalleDTO> carteraDetalle;
    private List<CuentasPorPagarResumenDTO> cuentasPorPagar;
    private List<CuentaPorPagarDetalleDTO> cuentasPorPagarDetalle;
    private List<AnulacionDTO> anulaciones;
    private List<DevolucionDetalladaDTO> devoluciones;
}
