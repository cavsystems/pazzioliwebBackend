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
        // ── OPTIMIZACIÓN: cargar variantes y precios en batch para evitar N+1 queries ──
        List<Long> varianteIds = dtos.stream().map(PreciosProductoVarianteCreateDTO::getProductoVarianteId).distinct().toList();
        List<ProductoVariante> variantes = productoVarianteRepository.findAllById(varianteIds);
        java.util.Map<Long, ProductoVariante> variantesMap = variantes.stream()
                .collect(java.util.stream.Collectors.toMap(ProductoVariante::getProductoVarianteId, v -> v));

        List<Integer> precioIds = dtos.stream().map(PreciosProductoVarianteCreateDTO::getPrecioId).distinct().toList();
        List<Precios> precios = preciosRepository.findAllById(precioIds);
        java.util.Map<Integer, Precios> preciosMap = precios.stream()
                .collect(java.util.stream.Collectors.toMap(Precios::getPrecioId, p -> p));

        List<PreciosProductoVariante> entidadesAGuardar = new ArrayList<>();
        List<PreciosProductoVarianteResponseDTO> respuestas = new ArrayList<>();

        for (PreciosProductoVarianteCreateDTO dto : dtos) {
            System.out.println("DTO precioId: " + dto.getPrecioId());
            System.out.println("DTO valor: " + dto.getValor());
            System.out.println("DTO predeterminada: " + dto.getPredeterminada());

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
                entity.setPredeterminada(dto.getPredeterminada());
                entity.setFechaModificacion(LocalDateTime.now());
                System.out.println("Actualizando existente - predeterminada seteada: " + entity.getPredeterminada());
                entidadesAGuardar.add(entity);
            } else {
                // Si no existe, creamos
                ProductoVariante pv = variantesMap.get(dto.getProductoVarianteId());
                if (pv == null) {
                    throw new RuntimeException("ProductoVariante no encontrado: " + dto.getProductoVarianteId());
                }

                Precios precio = preciosMap.get(dto.getPrecioId());
                if (precio == null) {
                    throw new RuntimeException("Precio no encontrado: " + dto.getPrecioId());
                }

                entity = mapper.toEntity(dto, pv, precio);
                System.out.println("Creando nueva entidad - predeterminada: " + entity.getPredeterminada());
                entidadesAGuardar.add(entity);
            }
        }

        // ── OPTIMIZACIÓN: saveAll batch en lugar de saves individuales ──
        List<PreciosProductoVariante> guardadas = repository.saveAll(entidadesAGuardar);
        return guardadas.stream().map(mapper::toResponse).toList();
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
        // ── OPTIMIZACIÓN: cargar precios en batch para evitar N+1 queries ──
        List<Integer> precioIds = dtos.stream()
                .map(PreciosProductoVarianteUpdateDTO::getPrecioId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        List<Precios> precios = new ArrayList<>();
        if (!precioIds.isEmpty()) {
            precios = preciosRepository.findAllById(precioIds);
        }
        java.util.Map<Integer, Precios> preciosMap = precios.stream()
                .collect(java.util.stream.Collectors.toMap(Precios::getPrecioId, p -> p));

        List<PreciosProductoVariante> entidadesAGuardar = new ArrayList<>();

	    for (PreciosProductoVarianteUpdateDTO dto : dtos) {
             System.out.println(dto.getPreciosProductoId());
	        PreciosProductoVariante entidad = repository.findById(dto.getPreciosProductoId())
	                .orElseThrow(() -> new EntityNotFoundException("PrecioProductoVariante no encontrado"));

	        if (dto.getPrecioId() != null) {
	            Precios precio = preciosMap.get(dto.getPrecioId());
	            if (precio == null) {
	                throw new EntityNotFoundException("Precio no encontrado: " + dto.getPrecioId());
	            }
	            entidad.setPrecio(precio);
	        }

	        if (dto.getValor() != null) entidad.setValor(dto.getValor());
	        if (dto.getFechaInicio() != null) entidad.setFechaInicio(dto.getFechaInicio());
	        if (dto.getFechaFin() != null) entidad.setFechaFin(dto.getFechaFin());
	        if (dto.getPredeterminada() != null) entidad.setPredeterminada(dto.getPredeterminada());

	        entidad.setFechaModificacion(LocalDateTime.now());
	        entidadesAGuardar.add(entidad);
	    }

        // ── OPTIMIZACIÓN: saveAll batch en lugar de saves individuales ──
        List<PreciosProductoVariante> guardadas = repository.saveAll(entidadesAGuardar);
	    return guardadas.stream().map(mapper::toResponse).toList();
	}

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        return repository.findById(id).map(entity -> {
            try {
                repository.delete(entity);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar el registro: " + e.getMessage());
            }
        }).orElseThrow(() -> new RuntimeException("Registro con id " + id + " no encontrado"));
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
