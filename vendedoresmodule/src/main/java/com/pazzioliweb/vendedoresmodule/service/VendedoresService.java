package com.pazzioliweb.vendedoresmodule.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pazzioliweb.vendedoresmodule.dtos.VendedorDTO;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import com.pazzioliweb.vendedoresmodule.repositori.VendedoresRepository;

@Service
public class VendedoresService {
	private final VendedoresRepository vendedorRepository;
	
	@Autowired
	public VendedoresService(VendedoresRepository vendedorRepository) {
		this.vendedorRepository = vendedorRepository;
	}
	
	public Page<VendedorDTO> listar(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<VendedorDTO> listadoVendedores = vendedorRepository.listarVendedoresDTO(pageable);
    	
    	return listadoVendedores;
	}
	
	public Optional<Vendedores> buscarPorId(Integer id) {
        return vendedorRepository.findById(id);
    }

    public Vendedores guardar(Vendedores vendedor) {
        return vendedorRepository.save(vendedor);
    }

    public void eliminar(Integer id) {
    	vendedorRepository.deleteById(id);
    }
}
