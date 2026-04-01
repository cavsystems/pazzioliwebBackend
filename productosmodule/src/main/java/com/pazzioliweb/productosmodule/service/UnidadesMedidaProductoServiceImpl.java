package com.pazzioliweb.productosmodule.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pazzioliweb.productosmodule.dtos.UnidadMedidaProductoCreateDTO;
import com.pazzioliweb.productosmodule.dtos.UnidadMedidaProductoResponseDTO;
import com.pazzioliweb.productosmodule.entity.Productos;
import com.pazzioliweb.productosmodule.entity.UnidadesMedida;
import com.pazzioliweb.productosmodule.entity.UnidadesMedidaProducto;
import com.pazzioliweb.productosmodule.entity.UnidadesMedidaProductoId;
import com.pazzioliweb.productosmodule.mapper.UnidadesMedidaProductoMapper;
import com.pazzioliweb.productosmodule.repositori.ProductosRepository;
import com.pazzioliweb.productosmodule.repositori.UnidadesMedidaProductoRepository;
import com.pazzioliweb.productosmodule.repositori.UnidadesMedidaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UnidadesMedidaProductoServiceImpl implements UnidadesMedidaProductoService {

    private final UnidadesMedidaProductoRepository repo;
    private final ProductosRepository productoRepo;
    private final UnidadesMedidaRepository unidadRepo;
    private final UnidadesMedidaProductoMapper mapper;

    public UnidadesMedidaProductoServiceImpl(
            UnidadesMedidaProductoRepository repo,
            ProductosRepository productoRepo,
            UnidadesMedidaRepository unidadRepo,
            UnidadesMedidaProductoMapper mapper
    ) {
        this.repo = repo;
        this.productoRepo = productoRepo;
        this.unidadRepo = unidadRepo;
        this.mapper = mapper;
    }

    @Override
    public UnidadMedidaProductoResponseDTO crear(UnidadMedidaProductoCreateDTO dto) {

        Productos producto = productoRepo.findById(dto.getProductoId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no existe"));

        UnidadesMedida unidad = unidadRepo.findById(dto.getUnidadMedidaId())
                .orElseThrow(() -> new EntityNotFoundException("Unidad de medida no existe"));

        UnidadesMedidaProducto entity = mapper.toEntity(dto, producto, unidad);

        repo.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public UnidadMedidaProductoResponseDTO obtenerPorId(Integer productoId, Integer unidadMedidaId) {

        UnidadesMedidaProductoId id = new UnidadesMedidaProductoId(productoId, unidadMedidaId);

        return repo.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Registro no existe"));
    }

    @Override
    public Page<UnidadMedidaProductoResponseDTO> listar(Pageable pageable) {

        return repo.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public Page<UnidadMedidaProductoResponseDTO> listarPorProducto(
            Integer productoId, Pageable pageable) {

        return repo.findByProducto_ProductoId(productoId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public void eliminar(Integer productoId, Integer unidadMedidaId) {

        UnidadesMedidaProductoId id = new UnidadesMedidaProductoId(productoId, unidadMedidaId);

        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Registro no existe");
        }

        repo.deleteById(id);
    }
}
