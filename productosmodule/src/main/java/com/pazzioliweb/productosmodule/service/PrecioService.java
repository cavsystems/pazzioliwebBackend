package com.pazzioliweb.productosmodule.service;

import java.util.List;
import java.util.Optional;

import com.pazzioliweb.productosmodule.dtos.OtroprecioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.dtos.PrecioCreateDTO;
import com.pazzioliweb.productosmodule.dtos.PrecioResponseDTO;
import com.pazzioliweb.productosmodule.dtos.PrecioUpdateDTO;

public interface PrecioService {
	PrecioResponseDTO crear(PrecioCreateDTO dto);

    Optional<PrecioResponseDTO> obtenerPorId(Integer id);

    Page<PrecioResponseDTO> listar(String preciodes,Pageable pageable);

    Optional<PrecioResponseDTO> actualizar(Integer id, PrecioUpdateDTO dto);
    
    public Page<PrecioResponseDTO> listarporidproduct(int id, Pageable pageable);

    boolean eliminar(Integer id);

    public List<OtroprecioDTO> obtenerenbezados(List<String> encabezados);


}
