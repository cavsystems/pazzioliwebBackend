package com.pazzioliweb.productosmodule.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.productosmodule.dtos.ProductoVarianteConDetallesDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Caracteristica;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.entity.ProductoVarianteDetalle;
import com.pazzioliweb.productosmodule.mapper.ProductoVarianteDetalleMapper;
import com.pazzioliweb.productosmodule.repositori.CaracteristicaRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteDetalleRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductoVarianteDetalleServiceImpl implements ProductoVarianteDetalleService{
	
	private final ProductoVarianteDetalleRepository detalleRepository;
    private final ProductoVarianteRepository varianteRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final ProductoVarianteDetalleMapper mapper;

    public ProductoVarianteDetalleServiceImpl(
            ProductoVarianteDetalleRepository detalleRepository,
            ProductoVarianteRepository varianteRepository,
            CaracteristicaRepository caracteristicaRepository,
            ProductoVarianteDetalleMapper mapper
    ) {
        this.detalleRepository = detalleRepository;
        this.varianteRepository = varianteRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ProductoVarianteDetalleResponseDTO> crear(List<ProductoVarianteDetalleCreateDTO> dtos) {

        List<ProductoVarianteDetalleResponseDTO> respuestas = new ArrayList<>();

        for (ProductoVarianteDetalleCreateDTO dto : dtos) {

            ProductoVariante variante = varianteRepository.findById(dto.getProductoVarianteId())
                    .orElseThrow(() -> new EntityNotFoundException("ProductoVariante no encontrado"));

            List<Long> ids = dto.getCaracteristicaId();

            if (ids == null || ids.isEmpty()) {
                throw new IllegalArgumentException("Debe enviar al menos una caracteristicaId");
            }

            List<Caracteristica> caracteristicas = caracteristicaRepository.findAllById(ids);

            if (caracteristicas.size() != ids.size()) {
                throw new EntityNotFoundException("Una o más características no existen");
            }

            List<ProductoVarianteDetalle> entidades = caracteristicas.stream()
                    .map(c -> mapper.fromCreateDto(variante, c))
                    .toList();

            entidades = detalleRepository.saveAll(entidades);

            // Convertir CADA entidad, no la lista completa
            entidades.forEach(e ->
                    respuestas.add(mapper.toResponseDto(e))
            );
        }

        return respuestas;
    }

    @Override
    @Transactional
    public ProductoVarianteDetalleResponseDTO actualizarDesdeDTO(Long id, ProductoVarianteDetalleUpdateDTO dto) {
    	ProductoVarianteDetalle existente = detalleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado"));

        if (dto.getProductoVarianteId() != null) {
            ProductoVariante pv = varianteRepository.findById(dto.getProductoVarianteId())
                    .orElseThrow(() -> new EntityNotFoundException("ProductoVariante no encontrado"));
            existente.setProductoVariante(pv);
        }

        if (dto.getCaracteristicaId() != null) {
            Caracteristica c = caracteristicaRepository.findById(dto.getCaracteristicaId())
                    .orElseThrow(() -> new EntityNotFoundException("Caracteristica no encontrada"));
            existente.setCaracteristica(c);
        }

        ProductoVarianteDetalle actualizado = detalleRepository.save(existente);
        return mapper.toResponseDto(actualizado);
    }
    @Transactional
    @Override
    public void eliminar(Long id) {
        if (!varianteRepository.existsById(id)) {
            throw new EntityNotFoundException("El detalle no existe");
        }
        
        
        Optional<ProductoVariante> varian=varianteRepository.findByProductoVarianteId(id);
        detalleRepository.deleteByProductoVariante(varian.get());
        varianteRepository.deleteByProductoVarianteId(id);
       
    }

    @Override
    public ProductoVarianteDetalle buscarPorId(Long id) {
        return detalleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado"));
    }

    @Override
    public Page<ProductoVarianteDetalleResponseDTO> listar(Pageable pageable) {
    	Page<ProductoVarianteDetalle> pagina = detalleRepository.traerProductosVariantesDetalles(pageable);
        return pagina.map(mapper::toResponseDto);
    }

    @Override
    public Page<ProductoVarianteDetalleResponseDTO> listarPorVariante(Long varianteId, Pageable pageable) {
        Page<ProductoVarianteDetalle> pagina = detalleRepository.findByProductoVariante_ProductoVarianteId(varianteId, pageable);
        return pagina.map(mapper::toResponseDto);
    }
}
