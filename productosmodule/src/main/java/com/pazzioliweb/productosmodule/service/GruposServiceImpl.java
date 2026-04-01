package com.pazzioliweb.productosmodule.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pazzioliweb.productosmodule.entity.Grupos;
import com.pazzioliweb.productosmodule.repositori.GrupoRepositori;
import com.pazzioliweb.productosmodule.repositori.ProductosRepository;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GruposServiceImpl implements GruposService{
private final GrupoRepositori repo;
private final ProductosRepository product;	
	public GruposServiceImpl(GrupoRepositori repo,ProductosRepository product) {
		this.repo = repo;
		this.product=product;
	}
	
	@Override
	public Grupos crear(Grupos grupos) {
		return repo.save(grupos);
	}
	
	@Override
	public Grupos actualizar(Integer id, Grupos grupo) {
		Grupos existente = buscarPorId(id);
		
		existente.setDescripcion(grupo.getDescripcion());
		
		return repo.save(existente);
	}
	
	@Override
	public void eliminar(Integer id) {
		if(!repo.existsById(id)) {
			throw new EntityNotFoundException("Grupo no encontrada");
		}
	    if (product. existsByGrupo_Id(id)) {
	        throw new IllegalStateException(
	            "Esta Grupo no puede ser eliminada"
	        );
	    }

	    repo.deleteById(id);
	}
	
	@Override
	public Grupos buscarPorId(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado"));
    }
	
	@Override
	public Optional<Grupos> buscarPorNombre(String nombre) {
		return repo.findByDescripcion(nombre);
	}
	
	@Override
	public Page<Grupos> listar(String descripcion,Pageable pageable) {
        return repo.findByDescripcionContainingIgnoreCase(descripcion,pageable);
    }
}
