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

        List<ExistenciasResponseDTO> respuestas = new ArrayList<>();

        for (ExistenciasCreateDTO dto : dtos) {

            var variante = varianteRepo.findById(dto.getProductoVarianteId())
                    .orElseThrow(() -> new EntityNotFoundException("Variante no existe"));

            var bodega = bodegasRepo.findById(dto.getBodegaId())
                    .orElseThrow(() -> new EntityNotFoundException("Bodega no existe"));

            Existencias nueva = mapper.toEntity(dto, variante, bodega);
            nueva.setFechaUltimoMovimiento(LocalDateTime.now());

            nueva = repo.save(nueva);

            respuestas.add(mapper.toResponseDto(nueva));
        }

        return respuestas;
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
}
