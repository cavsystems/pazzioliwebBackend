package com.pazzioliweb.productosmodule.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.pazzioliweb.commonbacken.dtos.response.ApiResponse;
import com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO;
import com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO_basico;
import com.pazzioliweb.productosmodule.entity.Caracteristica;
import com.pazzioliweb.productosmodule.entity.TipoCaracteristica;
import com.pazzioliweb.productosmodule.repositori.CaracteristicaRepository;
import com.pazzioliweb.productosmodule.repositori.TipoCaracteristicaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CaracteristicaServiceImpl implements CaracteristicaService {

	private final CaracteristicaRepository repo;
	private final TipoCaracteristicaRepository tipoRepo;

	public CaracteristicaServiceImpl(CaracteristicaRepository repo, TipoCaracteristicaRepository tipoRepo) {
		this.repo = repo;
		this.tipoRepo = tipoRepo;
	}

	@Override
	public ApiResponse<Caracteristica> crear(Caracteristica c) {

		if (c.getTipo() == null || c.getTipo().getTipoCaracteristicaId() == null) {
			
			return   ApiResponse.failure("Debe asociar un tipo de característica");
			
		}

		tipoRepo.findById(c.getTipo().getTipoCaracteristicaId())
				.orElseThrow(() -> new EntityNotFoundException("El tipo de característica no existe"));
		
	 List<Caracteristica> tipocara= repo.findBynombretipo(c.getNombre(),c.getTipo().getTipoCaracteristicaId());
	 if(tipocara.size()>0) {
		   return ApiResponse.failure("Caracteristica ya existente");
	 }
	    Caracteristica  ca=repo.save(c);
		return ApiResponse.success("Caracteristica creada correctamente",ca);
	}
	@Transactional
	@Override
	public ApiResponse<Caracteristica> actualizar(Long id, Caracteristica c) {

		

		

	
		List<Caracteristica> tipocara= repo.findBynombretipo(c.getNombre(),c.getTipo().getTipoCaracteristicaId());
		System.out.println(tipocara.size()+c.getNombre()+c.getTipo().getTipoCaracteristicaId());
		 if(tipocara.size()>0) {
			 System.out.println(tipocara.size()+c.getNombre()+c.getTipo().getTipoCaracteristicaId());
			   return ApiResponse.failure("Caracteristica ya existente");
		 }
		 Caracteristica existente = buscarPorId(id);
	
		 System.out.println("continuo");
		 existente.setNombre(c.getNombre());
			if (c.getTipo() != null) {
				TipoCaracteristica tipo =	tipoRepo.findById(c.getTipo().getTipoCaracteristicaId())
						.orElseThrow(() -> new EntityNotFoundException("El tipo de característica no existe"));

				existente.setTipo(tipo);
			}
			 
		 Caracteristica  ca=repo.save(existente);
			// forzar carga de la relación tipo
		 if (ca.getTipo() != null) {
		     ca.getTipo().getNombre(); // <--- aquí Hibernate carga `tipo` completamente
		 }
			return ApiResponse.success("Caracteristica Actulizada correctamente",null);
	}

	@Override
	public void eliminar(Long id) {
		if (!repo.existsById(id)) {
			throw new EntityNotFoundException("Característica no encontrada");
		}
		repo.deleteById(id);
	}

	@Override
	public Caracteristica buscarPorId(Long id) {
		return repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Característica no encontrada"));
	}

	@Override
	public Page<CaracteristicaDTO_basico> listar(Pageable pageable) {
		return repo.traerTodasCaracteristicas(pageable);
	}

	@Override
	public Page<CaracteristicaDTO_basico> listarPorTipo(Long tipoId, Pageable pageable) {
		return repo.findByTipoIdDTO(tipoId, pageable);
	}

	/*@Override
	public Page<CaracteristicaDTO> traerCaracteristicasDetale(Pageable pageable) {
		return repo.traerCaracteristicasDetalle(pageable);
	}*/

	@Override
	public Page<CaracteristicaDTO> buscarPornombretipo(  String ca,
	        String tipo,Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.traerCaracteristicasDetalle(ca,tipo,pageable);
	}

	
}
