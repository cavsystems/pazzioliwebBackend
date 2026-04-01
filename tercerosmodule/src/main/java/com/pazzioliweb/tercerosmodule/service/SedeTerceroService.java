package com.pazzioliweb.tercerosmodule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.tercerosmodule.dtos.SedeTerceroDTO;
import com.pazzioliweb.tercerosmodule.dtos.SedeTerceroDTOImpl;
import com.pazzioliweb.tercerosmodule.entity.SedeTercero;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.SedeTerceroRepository;

import jakarta.persistence.EntityManager;

@Service
public class SedeTerceroService {
	private final SedeTerceroRepository repository;
	private final EntityManager entityManager;
	
	@Autowired
	public SedeTerceroService(SedeTerceroRepository repository, EntityManager entityManager) {
		this.repository = repository;
		this.entityManager = entityManager;
	}
	
	public Page<SedeTerceroDTO> listar(Integer terceroId, int page, int size, String sortField, String sortDirection) {
	    Sort sort = sortDirection.equalsIgnoreCase("asc")
	            ? Sort.by(sortField).ascending()
	            : Sort.by(sortField).descending();

	    Pageable pageable = PageRequest.of(page, size, sort);

	    Page<SedeTercero> sedesPage = repository.findByTercero_TerceroIdConRelaciones(terceroId, pageable);

	    // ✅ convertir usando map
	    return sedesPage.map(SedeTerceroDTOImpl::fromEntity);
	}
	
	@Transactional
	public SedeTerceroDTOImpl guardar(Integer id, SedeTerceroDTOImpl dto) {
		SedeTercero sedeTercero = repository.findById(id)
		.orElseThrow(() -> new RuntimeException("SedeTercero no encontrada con ID: " + id));
		
		if(dto.getNombreSede() != null) {sedeTercero.setNombreSede(dto.getNombreSede());}
		if(dto.getDireccion() != null) {sedeTercero.setDireccion(dto.getDireccion());}
		if(dto.getTelefono() != null) {sedeTercero.setTelefono(dto.getTelefono());}
		if(dto.getPrincipal() != null) {sedeTercero.setPrincipal(dto.getPrincipal());}
		if(dto.getActivo() != null) {sedeTercero.setActivo(dto.getActivo());}
		if(dto.getDepartamento() != null && dto.getDepartamento().getDepartamentoId() != null ) {
			sedeTercero.setDepartamento(entityManager.getReference(Departamento.class,dto.getDepartamento().getDepartamentoId()));
		}
		if(dto.getMunicipio() != null && dto.getMunicipio().getMunicipioId() != null ) {
			sedeTercero.setMunicipio(entityManager.getReference(Municipio.class,dto.getMunicipio().getMunicipioId()));
		}
		
		// Guardando datos actualizados
		SedeTercero actualizado = repository.save(sedeTercero);
		
		return SedeTerceroDTOImpl.fromEntity(actualizado);
	}
	
	@Transactional
	public SedeTerceroDTOImpl actualizar(Integer id, SedeTerceroDTOImpl dto) {
		SedeTercero sedeTercero = repository.findById(id)
		.orElseThrow(() -> new RuntimeException("SedeTercero no encontrada con ID: " + id));
		
		if(dto.getNombreSede() != null) {sedeTercero.setNombreSede(dto.getNombreSede());}
		if(dto.getDireccion() != null) {sedeTercero.setDireccion(dto.getDireccion());}
		if(dto.getTelefono() != null) {sedeTercero.setTelefono(dto.getTelefono());}
		if(dto.getPrincipal() != null) {sedeTercero.setPrincipal(dto.getPrincipal());}
		if(dto.getActivo() != null) {sedeTercero.setActivo(dto.getActivo());}
		if(dto.getDepartamento() != null && dto.getDepartamento().getDepartamentoId() != null ) {
			sedeTercero.setDepartamento(entityManager.getReference(Departamento.class,dto.getDepartamento().getDepartamentoId()));
		}
		if(dto.getMunicipio() != null && dto.getMunicipio().getMunicipioId() != null ) {
			sedeTercero.setMunicipio(entityManager.getReference(Municipio.class,dto.getMunicipio().getMunicipioId()));
		}
		
		// Guardando datos actualizados
		SedeTercero actualizado = repository.save(sedeTercero);
		
		return SedeTerceroDTOImpl.fromEntity(actualizado);
	}
	
	@Transactional
	public void eliminar(Integer id) {
	    SedeTercero sede = repository.findById(id)
	            .orElseThrow(() -> new RuntimeException("SedeTercero no encontrada con ID: " + id));

	    // Remover de la colección del tercero
	    Terceros tercero = sede.getTercero();
	    if (tercero != null) {
	        tercero.getSedes().remove(sede);
	    }

	    // Eliminar la sede
	    repository.delete(sede);
	}
}
