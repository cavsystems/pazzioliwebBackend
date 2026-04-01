package com.pazzioliweb.productosmodule.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pazzioliweb.productosmodule.entity.Lineas;
import com.pazzioliweb.productosmodule.repositori.LineasRepositori;
import com.pazzioliweb.productosmodule.repositori.ProductosRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class LineasServiceImpl implements LineasService{
	private final LineasRepositori repo;
	private final ProductosRepository product;
	public LineasServiceImpl(LineasRepositori repo, ProductosRepository product) {
		this.repo = repo;
		this.product=product;
	}
	
	@Override
	public Lineas crear(Lineas linea) {
		return repo.save(linea);
	}
	
	@Override
	public Lineas actualizar(Integer id, Lineas linea) {
		Lineas existente = buscarPorId(id);
		
		existente.setDescripcion(linea.getDescripcion());
		
		return repo.save(existente);
	}
	
	@Override
	public void eliminar(Integer id)  {
		if(!repo.existsById(id)) {
			throw new EntityNotFoundException("Linea no encontrada");
		}
	    if (product.existsByLinea_Id(id)) {
	        throw new IllegalStateException(
	            "Esta linea no puede ser eliminada"
	        );
	    }

	    repo.deleteById(id);
		
		
	}
	
	@Override
	public Lineas buscarPorId(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Linea no encontrada"));
    }
	
	@Override
	public Optional<Lineas> buscarPorNombre(String nombre) {
		return repo.findByDescripcion(nombre);
	}
	
	@Override
	public Page<Lineas> listar(String descripcion,Pageable pageable) {
		   if (descripcion == null || descripcion.isBlank()) {
		        return repo.findAll(pageable);
		    }
		    return repo.findByDescripcionContainingIgnoreCase(descripcion, pageable);
		         
    }
}
