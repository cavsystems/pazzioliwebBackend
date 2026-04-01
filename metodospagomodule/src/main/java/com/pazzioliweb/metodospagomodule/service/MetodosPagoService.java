package com.pazzioliweb.metodospagomodule.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pazzioliweb.metodospagomodule.dtos.MetodoPagoDTO;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;

@Service
public class MetodosPagoService {
	private final MetodosPagoRepository metodopagoRepository;
	
	@Autowired
	public MetodosPagoService(MetodosPagoRepository metodopagoRepository) {
		this.metodopagoRepository = metodopagoRepository;
	}
	
	public Page<MetodoPagoDTO> listar(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<MetodoPagoDTO> listadoMetodosPago = metodopagoRepository.listadoMetodosPagoDTO(pageable);
    	
    	return listadoMetodosPago;
	}
	
	public Optional<MetodosPago> buscarPorId(Integer id) {
        return metodopagoRepository.findById(id);
    }

    public MetodosPago guardar(MetodosPago metodoPago) {
        return metodopagoRepository.save(metodoPago);
    }

    public void eliminar(Integer id) {
    	metodopagoRepository.deleteById(id);
    }
}
