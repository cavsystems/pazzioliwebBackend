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
import java.util.*;
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
        return enriquecerConNombresCajero(repo.findAll());
    }

    @Transactional(readOnly = true)
    public List<ComprobanteContableDTO> listarActivos() {
        return enriquecerConNombresCajero(repo.findByActivoTrueOrderByTipoMovimientoAsc());
    }

    @Transactional(readOnly = true)
    public List<ComprobanteContableDTO> listarPorCajero(Integer cajeroId) {
        return enriquecerConNombresCajero(repo.findByCajeroId(cajeroId));
    }

    @Transactional(readOnly = true)
    public ComprobanteContableDTO obtener(Long id) {
        ComprobanteContable c = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + id));
        ComprobanteContableDTO dto = toDto(c);
        agregarNombresCajero(dto);
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
        agregarNombresCajero(out);
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
        agregarNombresCajero(out);
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

    // ─── Validación y mapeo ─────────────────────────────────────

    private void validar(ComprobanteContableCreateDTO dto, Long idActual) {
        if (dto.getTipoMovimiento() == null || dto.getTipoMovimiento().isBlank())
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        if (dto.getPrefijo() == null || dto.getPrefijo().isBlank())
            throw new IllegalArgumentException("El prefijo es obligatorio.");
        // Cajeros son opcionales — comprobantes de compra (CC/CR) y movimientos de inventario
        // no requieren cajero, se vinculan a una bodega/sucursal.
        if (dto.getCajeroIds() != null) {
            for (Integer cajId : dto.getCajeroIds()) {
                if (!existeCajero(cajId))
                    throw new IllegalArgumentException("El cajero " + cajId + " no existe.");
            }
        }

        TipoMovimientoComprobante tipo;
        try {
            tipo = TipoMovimientoComprobante.valueOf(dto.getTipoMovimiento().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de movimiento inválido: " + dto.getTipoMovimiento());
        }

        // Si el tipo requiere cajero (FC, VC, CC, CR, RC, CE, DV), debe traer al menos uno.
        boolean activo = dto.getActivo() == null || Boolean.TRUE.equals(dto.getActivo());
        if (tipo.requiereCajero() && activo
                && (dto.getCajeroIds() == null || dto.getCajeroIds().isEmpty())) {
            throw new IllegalArgumentException(
                "El comprobante de tipo " + tipo.name() + " (" + tipo.getDescripcion()
                + ") debe tener al menos un cajero asignado para poder usarse. " +
                "Sin cajero, el sistema no podrá generar el consecutivo al hacer una " + tipo.getDescripcion().toLowerCase() + "."
            );
        }

        // Prefijo único POR TIPO (no global) — varios cajeros pueden compartir el mismo prefijo+tipo,
        // pero NO el mismo prefijo dentro del mismo tipo está duplicado.
        boolean prefijoExiste = idActual == null
                ? repo.existsByPrefijoAndTipo(dto.getPrefijo().trim(), tipo)
                : repo.existsByPrefijoAndTipoAndIdNot(dto.getPrefijo().trim(), tipo, idActual);
        if (prefijoExiste)
            throw new IllegalArgumentException("Ya existe un comprobante " + tipo.name() +
                    " con el prefijo '" + dto.getPrefijo() + "'.");

        // Validar que ningún cajero asignado ya tenga OTRO comprobante activo del mismo tipo.
        // Excepción: si el otro comprobante es éste mismo (idActual), está bien.
        // Excepción COMPRAS (CC/CR): la compra NO está atada al cajero; se permiten VARIOS
        // prefijos de compra aunque compartan cajero, por eso no se aplica esta regla a CC/CR.
        boolean esCompra = tipo == TipoMovimientoComprobante.CC || tipo == TipoMovimientoComprobante.CR;
        if (dto.getCajeroIds() != null && !esCompra) {
            for (Integer cajId : dto.getCajeroIds()) {
                Optional<ComprobanteContable> existente = repo.findActivoByCajeroAndTipo(cajId, tipo);
                if (existente.isPresent() && (idActual == null || !existente.get().getId().equals(idActual))) {
                    throw new IllegalArgumentException(
                        "El cajero " + cajId + " ya tiene asignado el comprobante '"
                        + existente.get().getPrefijo() + "' para " + tipo.name()
                        + ". Quítalo de allí primero o usa ese mismo comprobante."
                    );
                }
            }
        }
    }

    private void aplicarDto(ComprobanteContable c, ComprobanteContableCreateDTO dto) {
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

        // Multi-sucursal y resolución DIAN
        c.setBodegaId(dto.getBodegaId());
        c.setResolucionDian(dto.getResolucionDian());
        c.setFechaInicioResolucion(dto.getFechaInicioResolucion());
        c.setFechaFinResolucion(dto.getFechaFinResolucion());
        c.setConsecutivoDesde(dto.getConsecutivoDesde());
        c.setConsecutivoHasta(dto.getConsecutivoHasta());
        c.setClaveTecnicaDian(dto.getClaveTecnicaDian());

        // Reemplazar cajeros asignados con los del DTO (puede ser vacío)
        Set<Integer> nuevos = new HashSet<>(dto.getCajeroIds() != null ? dto.getCajeroIds() : List.of());
        if (c.getCajeroIds() == null) {
            c.setCajeroIds(nuevos);
        } else {
            c.getCajeroIds().clear();
            c.getCajeroIds().addAll(nuevos);
        }
    }

    private boolean existeCajero(Integer cajeroId) {
        try {
            Long count = ((Number) em.createNativeQuery(
                    "SELECT COUNT(*) FROM cajeros WHERE cajero_id = :id")
                    .setParameter("id", cajeroId)
                    .getSingleResult()).longValue();
            return count > 0;
        } catch (Exception ex) { return true; }
    }

    // ─── Enriquecimiento con nombres de cajeros ─────────────────

    @SuppressWarnings("unchecked")
    private Map<Integer, String> cargarNombresCajeros(List<Integer> ids) {
        Map<Integer, String> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) return result;
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT cajero_id, nombre FROM cajeros WHERE cajero_id IN (:ids)")
                    .setParameter("ids", ids)
                    .getResultList();
            for (Object[] r : rows) {
                Integer id = ((Number) r[0]).intValue();
                result.put(id, r[1] != null ? r[1].toString() : null);
            }
        } catch (Exception ignored) { }
        return result;
    }

    private List<ComprobanteContableDTO> enriquecerConNombresCajero(List<ComprobanteContable> rows) {
        List<ComprobanteContableDTO> dtos = rows.stream().map(this::toDto).collect(Collectors.toList());
        Set<Integer> todosIds = new HashSet<>();
        dtos.forEach(d -> todosIds.addAll(d.getCajeroIds()));
        Map<Integer, String> nombres = cargarNombresCajeros(new ArrayList<>(todosIds));
        for (ComprobanteContableDTO d : dtos) inyectarNombres(d, nombres);
        return dtos;
    }

    private void agregarNombresCajero(ComprobanteContableDTO d) {
        if (d.getCajeroIds() == null || d.getCajeroIds().isEmpty()) return;
        Map<Integer, String> nombres = cargarNombresCajeros(d.getCajeroIds());
        inyectarNombres(d, nombres);
    }

    private void inyectarNombres(ComprobanteContableDTO d, Map<Integer, String> nombres) {
        List<String> resueltos = new ArrayList<>();
        for (Integer id : d.getCajeroIds()) {
            resueltos.add(nombres.getOrDefault(id, "Cajero " + id));
        }
        d.setCajeroNombres(resueltos);
        // Resumen para listas: "Juan, María, Pedro" o "Juan, María +3"
        if (resueltos.size() <= 2) {
            d.setCajerosResumen(String.join(", ", resueltos));
        } else {
            d.setCajerosResumen(resueltos.get(0) + ", " + resueltos.get(1) + " +" + (resueltos.size() - 2));
        }
    }

    public ComprobanteContableDTO toDto(ComprobanteContable c) {
        ComprobanteContableDTO d = new ComprobanteContableDTO();
        d.setId(c.getId());
        d.setCajeroIds(c.getCajeroIds() != null
                ? c.getCajeroIds().stream().sorted().collect(Collectors.toList())
                : new ArrayList<>());
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

        // Multi-sucursal y resolución DIAN
        d.setBodegaId(c.getBodegaId());
        if (c.getBodegaId() != null) {
            try {
                Object nombre = em.createNativeQuery(
                        "SELECT nombre FROM bodegas WHERE codigo = :id")
                        .setParameter("id", c.getBodegaId())
                        .getSingleResult();
                d.setBodegaNombre(nombre != null ? nombre.toString() : null);
            } catch (Exception ignored) {}
        }
        d.setResolucionDian(c.getResolucionDian());
        d.setFechaInicioResolucion(c.getFechaInicioResolucion());
        d.setFechaFinResolucion(c.getFechaFinResolucion());
        d.setConsecutivoDesde(c.getConsecutivoDesde());
        d.setConsecutivoHasta(c.getConsecutivoHasta());
        d.setClaveTecnicaDian(c.getClaveTecnicaDian());
        return d;
    }
}
