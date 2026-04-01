package com.pazzioliweb.cajasmodule.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pazzioliweb.cajasmodule.dtos.CajaDTO;
import com.pazzioliweb.cajasmodule.entity.Cajas;
import com.pazzioliweb.cajasmodule.repositori.CajasRepository;

@Service
public class CajasService {
	private final CajasRepository cajasRepository;
	
	@Autowired
	public CajasService(CajasRepository cajasRepository) {
		this.cajasRepository = cajasRepository;
	}
	
	public Page<CajaDTO> listar(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<CajaDTO> listadoCajas = cajasRepository.listarCajasDTO(pageable);
    	
    	return listadoCajas;
	}
	
	public Optional<Cajas> buscarPorId(Integer id) {
        return cajasRepository.findById(id);
    }

    public Cajas guardar(Cajas caja) {
        return cajasRepository.save(caja);
    }

    public void eliminar(Integer id) {
    	cajasRepository.deleteById(id);
    }
}
