package com.pazzioliweb.metodospagomodule.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pazzioliweb.metodospagomodule.dtos.TipoTotalDTO;
import com.pazzioliweb.metodospagomodule.entity.TipoTotales;
import com.pazzioliweb.metodospagomodule.repositori.TipoTotalesRepository;

@Service
public class TipoTotalesService {
	private final TipoTotalesRepository tipoTotalRepository;
	
	@Autowired
	public TipoTotalesService(TipoTotalesRepository tipoTotalRepository) {
		this.tipoTotalRepository = tipoTotalRepository;
	}
	
	public Page<TipoTotalDTO> listar(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection != null && sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<TipoTotalDTO> listadoTipoTotales = tipoTotalRepository.listadoTipoTotalesDTO(pageable);
    	
    	return listadoTipoTotales;
	}
	
	public Optional<TipoTotales> buscarPorId(Integer id) {
        return tipoTotalRepository.findById(id);
    }

    public TipoTotales guardar(TipoTotales tipoTotal) {
        return tipoTotalRepository.save(tipoTotal);
    }

    public void eliminar(Integer id) {
    	tipoTotalRepository.deleteById(id);
    }
}
