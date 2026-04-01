package com.pazzioliweb.productosmodule.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pazzioliweb.productosmodule.entity.TipoProducto;
import com.pazzioliweb.productosmodule.repositori.TipoProductoRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class TipoProductoServiceImpl implements TipoProductoService{
	private final TipoProductoRepository repo;
	
	public TipoProductoServiceImpl(TipoProductoRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public TipoProducto crear (TipoProducto tipoProducto) {
		return repo.save(tipoProducto);
	}
	
	@Override
	public TipoProducto actualizar(Integer id, TipoProducto tipoProducto) {
		TipoProducto existente = buscarPorId(id);
		
		if(tipoProducto.getNombre()!=null)
			existente.setNombre(tipoProducto.getNombre());
		if(tipoProducto.getDescripcion()!=null)
			existente.setDescripcion(tipoProducto.getDescripcion());
		if(tipoProducto.getEstado()!=null)
			existente.setEstado(tipoProducto.getEstado());
		
		return repo.save(existente);
	}
	
	@Override
	public void eliminar(Integer id) {
		if(repo.existsById(id))
			throw new EntityNotFoundException("TipoProducto no encontrado");
		repo.deleteById(id);
	}
	
	@Override
	public TipoProducto buscarPorId(Integer id) {
		return repo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("TipoProducto no rencontrado"));
	}
	
	@Override
	public Optional<TipoProducto> buscarPorNombre(String nombre) {
		return repo.findByNombre(nombre);
	}
	
	@Override
	public Page<TipoProducto> listar(Pageable pageable){
		return repo.findAll(pageable);
	}
	
}
