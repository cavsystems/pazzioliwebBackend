package com.pazzioliweb.productosmodule.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pazzioliweb.productosmodule.entity.TipoCaracteristica;

public interface TipoCaracteristicaService {
	 TipoCaracteristica crear(TipoCaracteristica tipo);

	    TipoCaracteristica actualizar(Long id, TipoCaracteristica tipo);

	    void eliminar(Long id);

	    TipoCaracteristica buscarPorId(Long id);

	    Page<TipoCaracteristica> listar(String caracteristica,Pageable pageable);
}
