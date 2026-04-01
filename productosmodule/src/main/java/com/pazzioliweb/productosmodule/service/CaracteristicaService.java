package com.pazzioliweb.productosmodule.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.commonbacken.dtos.response.ApiResponse;
import com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO;
import com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO_basico;
import com.pazzioliweb.productosmodule.entity.Caracteristica;

public interface CaracteristicaService {
	ApiResponse<Caracteristica>  crear(Caracteristica c);

	ApiResponse<Caracteristica> actualizar(Long id, Caracteristica c);

    void eliminar(Long id);

    Caracteristica buscarPorId(Long id);
    Page<CaracteristicaDTO> buscarPornombretipo(  String ca,
          String tipo,Pageable pageable);

    Page<CaracteristicaDTO_basico> listar(Pageable pageable);

    Page<CaracteristicaDTO_basico> listarPorTipo(Long tipoId, Pageable pageable);
    
   /* Page<CaracteristicaDTO> traerCaracteristicasDetale(Pageable pageable);*/
}
