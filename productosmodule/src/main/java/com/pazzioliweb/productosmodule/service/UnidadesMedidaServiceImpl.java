package com.pazzioliweb.productosmodule.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.commonbacken.dtos.response.PaginationResponse;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaCreateDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaResponseDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaUpdateDTO;
import com.pazzioliweb.productosmodule.entity.UnidadesMedida;
import com.pazzioliweb.productosmodule.mapper.UnidadesMedidaMapper;
import com.pazzioliweb.productosmodule.repositori.ProductosRepository;
import com.pazzioliweb.productosmodule.repositori.UnidadesMedidaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UnidadesMedidaServiceImpl implements UnidadesMedidaService {

    private final UnidadesMedidaRepository repo;
    private final UnidadesMedidaMapper mapper;
    private final ProductosRepository product;
    public UnidadesMedidaServiceImpl(UnidadesMedidaRepository repo, UnidadesMedidaMapper mapper,ProductosRepository product) {
    	this.repo = repo;
    	this.mapper = mapper;
    	this.product=product;
    }

    @Override
    public List<UnidadMedidaResponseDTO> crear(List<UnidadMedidaCreateDTO> dtos) {
    	List<UnidadMedidaResponseDTO> respuestas = new ArrayList<>();
    	
    	for(UnidadMedidaCreateDTO dto : dtos) {
    		UnidadesMedida entity;
    		entity = mapper.toEntity(dto);
    		
    		repo.save(entity);
    		respuestas.add(mapper.toResponse(entity));
    	}

        return respuestas;
    }

    @Override
    public UnidadMedidaResponseDTO actualizar(Integer id, UnidadMedidaUpdateDTO dto) {
        UnidadesMedida existente = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe la unidad de medida"));

        mapper.toEntity(dto, existente);

        return mapper.toResponse(repo.save(existente));
    }

    @Override
    public UnidadMedidaResponseDTO obtenerPorId(Integer id) {
        return repo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("No existe la unidad de medida"));
    }

    @Override
    public Page<UnidadMedidaResponseDTO> listar(String descripcion ,Pageable pageable) {
    	 Page<UnidadesMedida>  pagina ;
    	   if (descripcion == null || descripcion.isBlank()) {
    		   pagina  = repo.findAll(pageable);
		    }else {
		    	   pagina=repo.findByDescripcionContainingIgnoreCase(descripcion, pageable);
		    }
    	

        return pagina.map(mapper::toResponse);
    }

    @Override
    public void eliminar(Integer id) {
    	
    	if(!repo.existsById(id)) {
			throw new EntityNotFoundException("Unidad de medida no existe");
		}
	    if (product.existsByUnidadesMedidaProducto_UnidadMedida_UnidadMedidaId(id)) {
	        throw new IllegalStateException(
	            "Esta unidad de medidad no puede ser eliminada"
	        );
	    }
      
        repo.deleteById(id);
    }
    
    @Override
    public Optional<UnidadMedidaResponseDTO> buscarPorCodigo(String codigo) {
    	return repo.findBySigla(codigo).map(mapper::toResponse);
    }
}
