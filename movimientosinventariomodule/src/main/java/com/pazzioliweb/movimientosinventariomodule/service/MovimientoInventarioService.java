package com.pazzioliweb.movimientosinventariomodule.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioCreateDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioResponseDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioUpdateDto;
import com.pazzioliweb.usuariosbacken.entity.Usuario;

public interface MovimientoInventarioService {
	MovimientoInventarioResponseDto crearMovimiento(
            MovimientoInventarioCreateDto createDto,
            Comprobantes comprobante,
            Usuario usuario);

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
	
}
