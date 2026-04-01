package com.pazzioliweb.productosmodule.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.dtos.ExistenciasBodegaDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasUpdateDTO;

public interface ExistenciasService {
    List<ExistenciasResponseDTO> crear(List<ExistenciasCreateDTO> existencia);

    ExistenciasResponseDTO actualizar(Integer id, ExistenciasUpdateDTO existencia);

    void eliminar(Integer id);

    ExistenciasResponseDTO buscarPorId(Integer id);

    Page<ExistenciasResponseDTO> listar(Pageable pageable);

    Page<ExistenciasResponseDTO> listarPorVariante(Long varianteId, Pageable pageable);

    Page<ExistenciasResponseDTO> listarPorBodega(Integer bodegaId, Pageable pageable);

    Page<ExistenciasBodegaDTO> listarExistenciasConNombreBodegaPorVariante(Long varianteId, Pageable pageable);
}
