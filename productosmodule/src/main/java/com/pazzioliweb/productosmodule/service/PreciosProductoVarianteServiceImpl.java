package com.pazzioliweb.productosmodule.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteCreateDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteResponseDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Precios;
import com.pazzioliweb.productosmodule.entity.PreciosProductoVariante;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.mapper.PreciosProductoVarianteMapper;
import com.pazzioliweb.productosmodule.repositori.PreciosProductoVarianteRepository;
import com.pazzioliweb.productosmodule.repositori.PreciosRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PreciosProductoVarianteServiceImpl implements PreciosProductoVarianteService{
	
	private final PreciosProductoVarianteRepository repository;
    private final ProductoVarianteRepository productoVarianteRepository;
    private final PreciosRepository preciosRepository;
    private final PreciosProductoVarianteMapper mapper;

    public PreciosProductoVarianteServiceImpl(
            PreciosProductoVarianteRepository repository,
            ProductoVarianteRepository productoVarianteRepository,
            PreciosRepository preciosRepository,
            PreciosProductoVarianteMapper mapper
    ) {
        this.repository = repository;
        this.productoVarianteRepository = productoVarianteRepository;
        this.preciosRepository = preciosRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public List<PreciosProductoVarianteResponseDTO> crear(List<PreciosProductoVarianteCreateDTO> dtos) {
        List<PreciosProductoVarianteResponseDTO> respuestas = new ArrayList<>();

        for (PreciosProductoVarianteCreateDTO dto : dtos) {

            // 1. Verificar si ya existe un precio del mismo tipo para esta variante
            Optional<PreciosProductoVariante> existente =
                    repository.findByProductoVariante_ProductoVarianteIdAndPrecio_PrecioId(
                            dto.getProductoVarianteId(), dto.getPrecioId()
                    );

            PreciosProductoVariante entity;

            if (existente.isPresent()) {
                // Si existe, actualizamos
                entity = existente.get();
                entity.setValor(dto.getValor());
                entity.setFechaInicio(dto.getFechaInicio());
                entity.setFechaFin(dto.getFechaFin());
                entity.setFechaModificacion(LocalDateTime.now());
            } else {
                // Si no existe, creamos
                ProductoVariante pv = productoVarianteRepository.findById(dto.getProductoVarianteId())
                        .orElseThrow(() -> new RuntimeException("ProductoVariante no encontrado"));

                Precios precio = preciosRepository.findById(dto.getPrecioId())
                        .orElseThrow(() -> new RuntimeException("Precio no encontrado"));

                entity = mapper.toEntity(dto, pv, precio);
            }

            repository.save(entity);
            respuestas.add(mapper.toResponse(entity));
        }

        return respuestas;
    }

    @Override
    public Optional<PreciosProductoVarianteResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    @Override
    public Page<PreciosProductoVarianteResponseDTO> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public List<PreciosProductoVarianteResponseDTO> actualizar(List<PreciosProductoVarianteUpdateDTO> dtos) {

	    List<PreciosProductoVarianteResponseDTO> respuestas = new ArrayList<>();

	    for (PreciosProductoVarianteUpdateDTO dto : dtos) {
             System.out.println(dto.getPreciosProductoId());
	        PreciosProductoVariante entidad = repository.findById(dto.getPreciosProductoId())
	                .orElseThrow(() -> new EntityNotFoundException("PrecioProductoVariante no encontrado"));

	        if (dto.getPrecioId() != null) {
	            Precios precio = preciosRepository.findById(dto.getPrecioId())
	                    .orElseThrow(() -> new EntityNotFoundException("Precio no encontrado"));
	            entidad.setPrecio(precio);
	        }

	        if (dto.getValor() != null) entidad.setValor(dto.getValor());
	        if (dto.getFechaInicio() != null) entidad.setFechaInicio(dto.getFechaInicio());
	        if (dto.getFechaFin() != null) entidad.setFechaFin(dto.getFechaFin());

	        entidad.setFechaModificacion(LocalDateTime.now());

	        repository.save(entidad);

	        respuestas.add(mapper.toResponse(entidad));
	    }

	    return respuestas;
	}

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        return repository.findById(id).map(entity -> {
            repository.delete(entity);
            return true;
        }).orElse(false);
    }
    
    @Override
    public Page<PreciosProductoVarianteDTO> listarPreciosVariantesProducto(Integer varianteId, Pageable pageable) {
        return repository.preciosPrpductoVariante(varianteId, pageable);
    }

    @Override
    public Page<PreciosProductoVarianteDTO> listarPreciosVariantesProductos(List<Integer> varianteIds, Pageable pageable) {
        return repository.preciosProductoVarianteMultiple(varianteIds, pageable);
    }
}
