package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.dtos.CuentaContableDTO;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CuentaContableService {

    private static final Set<String> TIPOS_VALIDOS =
            Set.of("ACTIVO", "PASIVO", "PATRIMONIO", "INGRESO", "GASTO");

    private final CuentaContableRepository repo;

    public CuentaContableService(CuentaContableRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<CuentaContableDTO> listarActivas() {
        return repo.findByEstadoOrderByCodigoAsc("ACTIVO").stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CuentaContableDTO> listarTodas() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CuentaContableDTO buscarPorId(Integer id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Cuenta contable no encontrada: " + id));
    }

    @Transactional
    public CuentaContableDTO crear(CuentaContableDTO dto) {
        validar(dto, null);
        CuentaContable cc = new CuentaContable();
        aplicar(cc, dto);
        return toDto(repo.save(cc));
    }

    @Transactional
    public CuentaContableDTO actualizar(Integer id, CuentaContableDTO dto) {
        CuentaContable cc = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta contable no encontrada: " + id));
        validar(dto, id);
        aplicar(cc, dto);
        return toDto(repo.save(cc));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Cuenta contable no encontrada: " + id);
        }
        repo.deleteById(id);
    }

    private void validar(CuentaContableDTO dto, Integer idActual) {
        if (dto.getCodigo() == null || dto.getCodigo().isBlank())
            throw new RuntimeException("El código es obligatorio");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new RuntimeException("El nombre es obligatorio");
        if (dto.getTipo() == null || !TIPOS_VALIDOS.contains(dto.getTipo().toUpperCase()))
            throw new RuntimeException("Tipo inválido. Use ACTIVO/PASIVO/PATRIMONIO/INGRESO/GASTO");
        repo.findByCodigo(dto.getCodigo()).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new RuntimeException("Ya existe una cuenta contable con código " + dto.getCodigo());
            }
        });
    }

    private void aplicar(CuentaContable cc, CuentaContableDTO dto) {
        cc.setCodigo(dto.getCodigo());
        cc.setNombre(dto.getNombre());
        cc.setTipo(dto.getTipo().toUpperCase());
        if (dto.getNivel() != null) cc.setNivel(dto.getNivel());
        cc.setPadreId(dto.getPadreId());
        if (dto.getEsMovimiento() != null) cc.setEsMovimiento(dto.getEsMovimiento());
        if (dto.getEstado() != null) cc.setEstado(dto.getEstado());
    }

    private CuentaContableDTO toDto(CuentaContable cc) {
        CuentaContableDTO dto = new CuentaContableDTO();
        dto.setId(cc.getId());
        dto.setCodigo(cc.getCodigo());
        dto.setNombre(cc.getNombre());
        dto.setTipo(cc.getTipo());
        dto.setNaturaleza(cc.getNaturaleza());
        dto.setNivel(cc.getNivel());
        dto.setPadreId(cc.getPadreId());
        dto.setEsMovimiento(cc.getEsMovimiento());
        dto.setEstado(cc.getEstado());
        return dto;
    }
}
