package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.dtos.CentroCostoDTO;
import com.pazzioliweb.comprobantesmodule.entity.CentroCosto;
import com.pazzioliweb.comprobantesmodule.repositori.CentroCostoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CentroCostoService {

    private final CentroCostoRepository repo;

    public CentroCostoService(CentroCostoRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<CentroCostoDTO> listar() {
        return repo.findAllByOrderByNombreAsc().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CentroCostoDTO crear(CentroCostoDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new RuntimeException("El nombre del centro de costo es obligatorio");
        CentroCosto cc = new CentroCosto();
        cc.setNombre(dto.getNombre().trim());
        cc.setEstado("ACTIVO");
        return toDto(repo.save(cc));
    }

    @Transactional
    public CentroCostoDTO actualizar(Integer codigo, CentroCostoDTO dto) {
        CentroCosto cc = repo.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Centro de costo no encontrado: " + codigo));
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) cc.setNombre(dto.getNombre().trim());
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) cc.setEstado(dto.getEstado());
        return toDto(repo.save(cc));
    }

    /** Inactivación suave (nunca borra la fila). */
    @Transactional
    public CentroCostoDTO inactivar(Integer codigo) {
        return cambiarEstado(codigo, "INACTIVO");
    }

    @Transactional
    public CentroCostoDTO activar(Integer codigo) {
        return cambiarEstado(codigo, "ACTIVO");
    }

    private CentroCostoDTO cambiarEstado(Integer codigo, String estado) {
        CentroCosto cc = repo.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Centro de costo no encontrado: " + codigo));
        cc.setEstado(estado);
        return toDto(repo.save(cc));
    }

    private CentroCostoDTO toDto(CentroCosto cc) {
        CentroCostoDTO dto = new CentroCostoDTO();
        dto.setCodigo(cc.getCodigo());
        dto.setNombre(cc.getNombre());
        dto.setEstado(cc.getEstado());
        return dto;
    }
}
