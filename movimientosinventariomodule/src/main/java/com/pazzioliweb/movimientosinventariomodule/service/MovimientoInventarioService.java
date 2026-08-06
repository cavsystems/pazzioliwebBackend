package com.pazzioliweb.movimientosinventariomodule.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.movimientosinventariomodule.dtos.KardexReportDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.KardexReportePaginadoDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioCreateDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioResponseDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioUpdateDto;
import com.pazzioliweb.usuariosbacken.entity.Usuario;

public interface MovimientoInventarioService {
	MovimientoInventarioResponseDto crearMovimiento(
            MovimientoInventarioCreateDto createDto,
            ComprobanteContable comprobante,
            Usuario usuario,
            HttpServletRequest request);

    MovimientoInventarioResponseDto actualizarMovimiento(
            Long movimientoId, 
            MovimientoInventarioUpdateDto updateDto);

    void anularMovimiento(Long movimientoId);

    Page<MovimientoInventarioResponseDto> listarMovimientos(
            Pageable pageable, 
            String tipo, 
            LocalDate fechaEmisionDesde, 
            LocalDate fechaEmisionHasta);

    MovimientoInventarioResponseDto obtenerMovimientoConDetalles(Long movimientoId);

    void reversarKardex(Long movimientoId);

    List<KardexReportDto> getKardexReport(String desde, String hasta, Integer varianteproductoid, String bodega, String movimiento);

    // Igual que getKardexReport, pero devuelve solo la página pedida (para scroll infinito)
    // más los totales ya calculados sobre TODO el período/filtros.
    KardexReportePaginadoDto getKardexReportPaginado(String desde, String hasta, Integer varianteproductoid,
            String bodega, String movimiento, int page, int size);

    boolean bodegaTieneRegistrosKardex(Integer bodegaId);

    // Costo promedio del registro de kardex MÁS RECIENTE de esa variante (cualquier
    // bodega). Usado en el formulario de producto: al editar, "Costo promedio" se
    // precarga con este valor en vez de dejarlo editable. null si la variante nunca
    // tuvo movimientos de inventario.
    Double obtenerUltimoCostoPromedio(Long productoVarianteId);

}
