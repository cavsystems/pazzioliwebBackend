package com.pazzioliweb.movimientosinventariomodule.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.movimientosinventariomodule.dtos.KardexReportDto;
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

    boolean bodegaTieneRegistrosKardex(Integer bodegaId);

}
