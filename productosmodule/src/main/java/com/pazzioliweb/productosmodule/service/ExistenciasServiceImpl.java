package com.pazzioliweb.productosmodule.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.productosmodule.dtos.ExistenciasBodegaDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Existencias;
import com.pazzioliweb.productosmodule.mapper.ExistenciasMapper;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.productosmodule.repositori.ExistenciasRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ExistenciasServiceImpl implements ExistenciasService{

    private final ExistenciasRepository repo;
    private final ProductoVarianteRepository varianteRepo;
    private final BodegasRepository bodegasRepo;
    private final ExistenciasMapper mapper;

    public ExistenciasServiceImpl(
            ExistenciasRepository repo,
            ProductoVarianteRepository varianteRepo,
            BodegasRepository bodegasRepo,
            ExistenciasMapper mapper
    ) {
        this.repo = repo;
        this.varianteRepo = varianteRepo;
        this.bodegasRepo = bodegasRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public List<ExistenciasResponseDTO> crear(List<ExistenciasCreateDTO> dtos) {

        // ── OPTIMIZACIÓN: cargar variantes y bodegas en batch para evitar N+1 queries ──
        List<Long> varianteIds = dtos.stream().map(ExistenciasCreateDTO::getProductoVarianteId).distinct().toList();
        List<com.pazzioliweb.productosmodule.entity.ProductoVariante> variantes = varianteRepo.findAllById(varianteIds);
        java.util.Map<Long, com.pazzioliweb.productosmodule.entity.ProductoVariante> variantesMap = variantes.stream()
                .collect(java.util.stream.Collectors.toMap(com.pazzioliweb.productosmodule.entity.ProductoVariante::getProductoVarianteId, v -> v));

        List<Integer> bodegaIds = dtos.stream().map(ExistenciasCreateDTO::getBodegaId).distinct().toList();
        List<com.pazzioliweb.productosmodule.entity.Bodegas> bodegas = bodegasRepo.findAllById(bodegaIds);
        java.util.Map<Integer, com.pazzioliweb.productosmodule.entity.Bodegas> bodegasMap = bodegas.stream()
                .collect(java.util.stream.Collectors.toMap(com.pazzioliweb.productosmodule.entity.Bodegas::getCodigo, b -> b));

        List<Existencias> existenciasAGuardar = new ArrayList<>();

        for (ExistenciasCreateDTO dto : dtos) {
            com.pazzioliweb.productosmodule.entity.ProductoVariante variante = variantesMap.get(dto.getProductoVarianteId());
            if (variante == null) {
                throw new EntityNotFoundException("Variante no existe: " + dto.getProductoVarianteId());
            }

            com.pazzioliweb.productosmodule.entity.Bodegas bodega = bodegasMap.get(dto.getBodegaId());
            if (bodega == null) {
                throw new EntityNotFoundException("Bodega no existe: " + dto.getBodegaId());
            }

            Existencias nueva = mapper.toEntity(dto, variante, bodega);
            nueva.setFechaUltimoMovimiento(LocalDateTime.now());
            existenciasAGuardar.add(nueva);
        }

        // ── OPTIMIZACIÓN: saveAll batch en lugar de saves individuales ──
        List<Existencias> guardadas = repo.saveAll(existenciasAGuardar);

        return guardadas.stream().map(mapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public ExistenciasResponseDTO actualizar(Integer id, ExistenciasUpdateDTO dto) {

        Existencias entidad = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Existencia no encontrada"));

        // Solo se actualizan los campos enviados (parcial)
        if (dto.getExistencia() != null) {
            entidad.setExistencia(dto.getExistencia());
            entidad.setFechaUltimoMovimiento(LocalDateTime.now());
        }

        if (dto.getStockMin() != null) {
            entidad.setStockMin(dto.getStockMin());
        }

        if (dto.getStockMax() != null) {
            entidad.setStockMax(dto.getStockMax());
        }

        if (dto.getUbicacion() != null) {
            entidad.setUbicacion(dto.getUbicacion());
        }

        if (dto.getBodegaId() != null) {
            var bodega = bodegasRepo.findById(dto.getBodegaId())
                    .orElseThrow(() -> new EntityNotFoundException("Bodega no existe"));
            entidad.setBodega(bodega);
        }

        // Guardar cambios
        Existencias actualizado = repo.save(entidad);

        return mapper.toResponseDto(actualizado);
    }

    @Override
    public void eliminar(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("La existencia no existe");
        }
        repo.deleteById(id);
    }

    @Override
    public ExistenciasResponseDTO buscarPorId(Integer id) {
        return repo.findById(id)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("No encontrada"));
    }

    @Override
    public Page<ExistenciasResponseDTO> listar(Pageable pageable) {
        return repo.findAll(pageable)
                .map(mapper::toResponseDto);
    }

    @Override
    public Page<ExistenciasResponseDTO> listarPorVariante(Long varianteId, Pageable pageable) {
        return repo.findByProductoVariante_ProductoVarianteId(varianteId, pageable)
                .map(mapper::toResponseDto);
    }

    @Override
    public Page<ExistenciasResponseDTO> listarPorBodega(Integer bodegaId, Pageable pageable) {
        return repo.findByBodega_Codigo(bodegaId, pageable)
                .map(mapper::toResponseDto);
    }
    @Override
    public Page<ExistenciasBodegaDTO> listarExistenciasConNombreBodegaPorVariante(Long varianteId, Pageable pageable) {
        return repo.listadoExistenciasNombreBodegaVariante(varianteId, pageable);
    }

    @Override
    public List<ExistenciasBodegaDTO> listarExistenciasConNombreBodegaPorVariantes(List<Long> varianteIds) {
        return repo.listadoExistenciasNombreBodegaVariantes(varianteIds);
    }
}
