package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.dtos.ComprobanteContableCreateDTO;
import com.pazzioliweb.comprobantesmodule.dtos.ComprobanteContableDTO;
import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComprobanteContableService {

    private final ComprobanteContableRepository repo;
    private final CuentaContableRepository cuentaRepo;

    @PersistenceContext
    private EntityManager em;

    public ComprobanteContableService(ComprobanteContableRepository repo,
                                       CuentaContableRepository cuentaRepo) {
        this.repo = repo;
        this.cuentaRepo = cuentaRepo;
    }

    @Transactional(readOnly = true)
    public List<ComprobanteContableDTO> listar() {
        return enriquecerConNombreCajero(repo.findAll());
    }

    @Transactional(readOnly = true)
    public List<ComprobanteContableDTO> listarActivos() {
        return enriquecerConNombreCajero(repo.findByActivoTrueOrderByCajeroIdAscTipoMovimientoAsc());
    }

    @Transactional(readOnly = true)
    public List<ComprobanteContableDTO> listarPorCajero(Integer cajeroId) {
        return enriquecerConNombreCajero(repo.findByCajeroId(cajeroId));
    }

    @Transactional(readOnly = true)
    public ComprobanteContableDTO obtener(Long id) {
        ComprobanteContable c = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + id));
        ComprobanteContableDTO dto = toDto(c);
        if (c.getCajeroId() != null) dto.setCajeroNombre(buscarNombreCajero(c.getCajeroId()));
        return dto;
    }

    @Transactional
    public ComprobanteContableDTO crear(ComprobanteContableCreateDTO dto) {
        validar(dto, null);
        ComprobanteContable c = new ComprobanteContable();
        aplicarDto(c, dto);
        c.setEsLegacy(false);
        c.setFechaCreacion(LocalDateTime.now());
        ComprobanteContableDTO out = toDto(repo.save(c));
        if (c.getCajeroId() != null) out.setCajeroNombre(buscarNombreCajero(c.getCajeroId()));
        return out;
    }

    @Transactional
    public ComprobanteContableDTO actualizar(Long id, ComprobanteContableCreateDTO dto) {
        ComprobanteContable c = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + id));
        if (Boolean.TRUE.equals(c.getEsLegacy())) {
            throw new IllegalStateException("Los comprobantes LEGACY no se pueden editar.");
        }
        validar(dto, id);
        aplicarDto(c, dto);
        ComprobanteContableDTO out = toDto(repo.save(c));
        if (c.getCajeroId() != null) out.setCajeroNombre(buscarNombreCajero(c.getCajeroId()));
        return out;
    }

    @Transactional
    public void eliminar(Long id) {
        ComprobanteContable c = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + id));
        if (Boolean.TRUE.equals(c.getEsLegacy())) {
            throw new IllegalStateException("Los comprobantes LEGACY no se pueden eliminar.");
        }
        c.setActivo(false);
        repo.save(c);
    }

    // ─── Helpers ────────────────────────────────────────────────

    private void validar(ComprobanteContableCreateDTO dto, Long idActual) {
        if (dto.getTipoMovimiento() == null || dto.getTipoMovimiento().isBlank())
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        if (dto.getPrefijo() == null || dto.getPrefijo().isBlank())
            throw new IllegalArgumentException("El prefijo es obligatorio.");
        if (dto.getCajeroId() == null)
            throw new IllegalArgumentException("El cajero es obligatorio.");

        // Validar que el cajero exista realmente (sin depender del módulo cajerosmodule)
        if (!existeCajero(dto.getCajeroId()))
            throw new IllegalArgumentException("El cajero " + dto.getCajeroId() + " no existe.");

        // Validar prefijo único
        boolean prefijoExiste = idActual == null
                ? repo.existsByPrefijo(dto.getPrefijo().trim())
                : repo.existsByPrefijoAndIdNot(dto.getPrefijo().trim(), idActual);
        if (prefijoExiste)
            throw new IllegalArgumentException("Ya existe un comprobante con el prefijo '" + dto.getPrefijo() + "'");

        TipoMovimientoComprobante tipo;
        try {
            tipo = TipoMovimientoComprobante.valueOf(dto.getTipoMovimiento().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de movimiento inválido: " + dto.getTipoMovimiento());
        }
        Optional<ComprobanteContable> existente = repo.findActivoByCajeroAndTipo(dto.getCajeroId(), tipo);
        if (existente.isPresent() && (idActual == null || !existente.get().getId().equals(idActual))) {
            throw new IllegalArgumentException(
                "Ya existe un comprobante activo para este cajero y tipo de movimiento."
            );
        }
    }

    private void aplicarDto(ComprobanteContable c, ComprobanteContableCreateDTO dto) {
        c.setCajeroId(dto.getCajeroId());
        c.setTipoMovimiento(TipoMovimientoComprobante.valueOf(dto.getTipoMovimiento().toUpperCase()));
        c.setPrefijo(dto.getPrefijo().trim());
        c.setDescripcion(dto.getDescripcion());
        if (dto.getSiguienteConsecutivo() != null && dto.getSiguienteConsecutivo() > 0)
            c.setSiguienteConsecutivo(dto.getSiguienteConsecutivo());
        if (dto.getCuentaContableId() != null) {
            CuentaContable cc = cuentaRepo.findById(dto.getCuentaContableId())
                    .orElseThrow(() -> new EntityNotFoundException("Cuenta contable no encontrada: " + dto.getCuentaContableId()));
            c.setCuentaContable(cc);
        } else {
            c.setCuentaContable(null);
        }
        c.setAfectaInventario(dto.getAfectaInventario() != null ? dto.getAfectaInventario() : true);
        c.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
    }

    /**
     * Verifica que el cajero exista sin depender de CajeroRepository (que generaría
     * dependencia circular comprobantesmodule ↔ cajerosmodule).
     */
    private boolean existeCajero(Integer cajeroId) {
        try {
            Long count = ((Number) em.createNativeQuery(
                    "SELECT COUNT(*) FROM cajeros WHERE cajero_id = :id")
                    .setParameter("id", cajeroId)
                    .getSingleResult()).longValue();
            return count > 0;
        } catch (Exception ex) {
            // Si la tabla aún no existe (primer arranque), no bloquear.
            return true;
        }
    }

    /** Obtiene el nombre del cajero sin importar la entidad. */
    private String buscarNombreCajero(Integer cajeroId) {
        try {
            Object name = em.createNativeQuery(
                    "SELECT nombre FROM cajeros WHERE cajero_id = :id")
                    .setParameter("id", cajeroId)
                    .getSingleResult();
            return name != null ? name.toString() : null;
        } catch (Exception ex) {
            return null;
        }
    }

    /** Resuelve nombres de cajero en bloque y los inyecta en los DTOs. */
    private List<ComprobanteContableDTO> enriquecerConNombreCajero(List<ComprobanteContable> rows) {
        List<ComprobanteContableDTO> dtos = rows.stream().map(this::toDto).collect(Collectors.toList());
        // Resolver todos los nombres en una sola consulta
        List<Integer> ids = dtos.stream()
                .map(ComprobanteContableDTO::getCajeroId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (!ids.isEmpty()) {
            Map<Integer, String> nombres = cargarNombresCajeros(ids);
            for (ComprobanteContableDTO d : dtos) {
                if (d.getCajeroId() != null) {
                    d.setCajeroNombre(nombres.getOrDefault(d.getCajeroId(), "Cajero " + d.getCajeroId()));
                }
            }
        }
        return dtos;
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, String> cargarNombresCajeros(List<Integer> ids) {
        Map<Integer, String> result = new HashMap<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT cajero_id, nombre FROM cajeros WHERE cajero_id IN (:ids)")
                    .setParameter("ids", ids)
                    .getResultList();
            for (Object[] r : rows) {
                Integer id = ((Number) r[0]).intValue();
                result.put(id, r[1] != null ? r[1].toString() : null);
            }
        } catch (Exception ignored) { /* tabla aún no existe */ }
        return result;
    }

    public ComprobanteContableDTO toDto(ComprobanteContable c) {
        ComprobanteContableDTO d = new ComprobanteContableDTO();
        d.setId(c.getId());
        d.setCajeroId(c.getCajeroId());
        d.setTipoMovimiento(c.getTipoMovimiento().name());
        d.setTipoMovimientoDescripcion(c.getTipoMovimiento().getDescripcion());
        d.setPrefijo(c.getPrefijo());
        d.setDescripcion(c.getDescripcion());
        d.setSiguienteConsecutivo(c.getSiguienteConsecutivo());
        if (c.getCuentaContable() != null) {
            d.setCuentaContableId(c.getCuentaContable().getId());
            d.setCuentaContableCodigo(c.getCuentaContable().getCodigo());
            d.setCuentaContableNombre(c.getCuentaContable().getNombre());
        }
        d.setAfectaInventario(c.getAfectaInventario());
        d.setActivo(c.getActivo());
        d.setEsLegacy(c.getEsLegacy());
        d.setFechaCreacion(c.getFechaCreacion());
        return d;
    }
}
