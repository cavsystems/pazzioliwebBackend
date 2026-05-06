package com.pazzioliweb.comprobantesmodule.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pazzioli.comprobantesmodule.dtos.ComprobanteDTO;
import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobantesRepository;

@Service
public class ComprobantesService {
	private final ComprobantesRepository comprobanteRepository;
	
	@Autowired
	public ComprobantesService(ComprobantesRepository comprobanteRepository) {
		this.comprobanteRepository = comprobanteRepository;
	}
	
	public Page<ComprobanteDTO> listar(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection != null && sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<ComprobanteDTO> listadoComprobantes = comprobanteRepository.listarComprobantes(pageable);
    	
    	return listadoComprobantes;
	}
	
	public Optional<Comprobantes> buscarPorId(Integer id) {
        return comprobanteRepository.findById(id);
    }

    public Comprobantes guardar(Comprobantes comprobante) {
        return comprobanteRepository.save(comprobante);
    }

    public void eliminar(Integer id) {
    	comprobanteRepository.deleteById(id);
    }
}
