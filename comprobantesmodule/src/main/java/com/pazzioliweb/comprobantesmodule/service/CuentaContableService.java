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
            Set.of("ACTIVO", "PASIVO", "PATRIMONIO", "INGRESO", "GASTO",
                   "COSTO", "COSTO_PRODUCCION", "ORDEN_DEUDORAS", "ORDEN_ACREEDORAS");

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

    /**
     * "Eliminar" ahora es inactivación suave: nunca borra la fila (para no
     * perder historial ni romper referencias de asientos). Solo cambia estado.
     */
    @Transactional
    public void eliminar(Integer id) {
        inactivar(id);
    }

    /** Marca la cuenta como INACTIVA. No se permite inactivar cuentas mayores
     *  (las que tienen subcuentas), porque dejaría colgadas las subcuentas. */
    @Transactional
    public CuentaContableDTO inactivar(Integer id) {
        CuentaContable cc = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta contable no encontrada: " + id));
        if (repo.existsByCodigoStartingWithAndCodigoNot(cc.getCodigo(), cc.getCodigo())) {
            throw new RuntimeException(
                "No se puede inactivar la cuenta " + cc.getCodigo() + " porque es una cuenta mayor " +
                "con subcuentas. Inactive primero las subcuentas.");
        }
        cc.setEstado("INACTIVO");
        return toDto(repo.save(cc));
    }

    /** Reactiva una cuenta previamente inactivada. */
    @Transactional
    public CuentaContableDTO activar(Integer id) {
        CuentaContable cc = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta contable no encontrada: " + id));
        cc.setEstado("ACTIVO");
        return toDto(repo.save(cc));
    }

    private void validar(CuentaContableDTO dto, Integer idActual) {
        if (dto.getCodigo() == null || dto.getCodigo().isBlank())
            throw new RuntimeException("El código es obligatorio");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new RuntimeException("El nombre es obligatorio");
        if (dto.getTipo() == null || !TIPOS_VALIDOS.contains(dto.getTipo().toUpperCase()))
            throw new RuntimeException("Tipo inválido. Clases PUC válidas: ACTIVO, PASIVO, PATRIMONIO, "
                    + "INGRESO, GASTO, COSTO, COSTO_PRODUCCION, ORDEN_DEUDORAS, ORDEN_ACREEDORAS");
        repo.findByCodigo(dto.getCodigo()).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new RuntimeException("Ya existe una cuenta contable con código " + dto.getCodigo());
            }
        });
        // Regla PUC: las cuentas de agrupación (clase 1 díg, grupo 2, cuenta 4) NO reciben
        // movimiento. Desde SUBCUENTA (6 dígitos) en adelante SÍ pueden ser de movimiento
        // (el sistema postea impuestos/retenciones a subcuentas de 6 díg. como 240810, 236505).
        // Se valida solo al CREAR para no alterar cuentas existentes ya en uso.
        if (idActual == null
                && Boolean.TRUE.equals(dto.getEsMovimiento())
                && dto.getCodigo().trim().length() < 6) {
            throw new RuntimeException("Las cuentas de menos de 6 dígitos (clase, grupo, cuenta) no pueden ser de "
                    + "movimiento. Use una subcuenta de 6 dígitos o una auxiliar más detallada.");
        }
        // Al CREAR, verificar que existan todas las cuentas PADRE del PUC en los
        // niveles estándar (1, 2, 4, 6, 8…) previos al código que se crea.
        // Ej: para 613595 deben existir 6, 61 y 6135 antes.
        if (idActual == null) {
            validarCuentasPadre(dto.getCodigo().trim());
        }
    }

    private static final int[] NIVELES_PUC = {1, 2, 4, 6, 8, 10, 12};

    /** Verifica que existan las cuentas padre (prefijos en niveles PUC) del código dado. */
    private void validarCuentasPadre(String codigo) {
        for (int nivel : NIVELES_PUC) {
            if (nivel >= codigo.length()) break;
            String padre = codigo.substring(0, nivel);
            if (repo.findByCodigo(padre).isEmpty()) {
                throw new RuntimeException("Debe crear primero la cuenta padre " + padre +
                        " antes de crear la cuenta " + codigo + ".");
            }
        }
    }

    private void aplicar(CuentaContable cc, CuentaContableDTO dto) {
        cc.setCodigo(dto.getCodigo());
        cc.setNombre(dto.getNombre());
        cc.setTipo(dto.getTipo().toUpperCase());
        if (dto.getNivel() != null) cc.setNivel(dto.getNivel());
        cc.setPadreId(dto.getPadreId());
        if (dto.getEsMovimiento() != null) cc.setEsMovimiento(dto.getEsMovimiento());
        if (dto.getRequiereTercero() != null) cc.setRequiereTercero(dto.getRequiereTercero());
        if (dto.getRequiereDocumentoCruce() != null) cc.setRequiereDocumentoCruce(dto.getRequiereDocumentoCruce());
        if (dto.getRequiereCentroCosto() != null) cc.setRequiereCentroCosto(dto.getRequiereCentroCosto());
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
        dto.setRequiereTercero(cc.getRequiereTercero());
        dto.setRequiereDocumentoCruce(cc.getRequiereDocumentoCruce());
        dto.setRequiereCentroCosto(cc.getRequiereCentroCosto());
        dto.setEstado(cc.getEstado());
        return dto;
    }
}
