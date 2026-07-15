package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.dtos.TipoComprobanteManualDTO;
import com.pazzioliweb.comprobantesmodule.entity.TipoComprobanteManual;
import com.pazzioliweb.comprobantesmodule.repositori.TipoComprobanteManualRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoComprobanteManualService {

    private final TipoComprobanteManualRepository repo;

    public TipoComprobanteManualService(TipoComprobanteManualRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<TipoComprobanteManualDTO> listar() {
        return repo.findAllByOrderByNombreAsc().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public TipoComprobanteManualDTO crear(TipoComprobanteManualDTO dto) {
        validar(dto, null);
        TipoComprobanteManual t = new TipoComprobanteManual();
        String codigo = dto.getCodigo().trim().toUpperCase();
        t.setCodigo(codigo);
        t.setNombre(dto.getNombre().trim());
        t.setPrefijo(dto.getPrefijo() != null && !dto.getPrefijo().isBlank()
                ? dto.getPrefijo().trim().toUpperCase() : codigo);
        t.setSiguienteConsecutivo(dto.getSiguienteConsecutivo() != null && dto.getSiguienteConsecutivo() > 0
                ? dto.getSiguienteConsecutivo() : 1);
        t.setEstado("ACTIVO");
        return toDto(repo.save(t));
    }

    @Transactional
    public TipoComprobanteManualDTO actualizar(Integer id, TipoComprobanteManualDTO dto) {
        TipoComprobanteManual t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de comprobante no encontrado: " + id));
        validar(dto, id);
        t.setCodigo(dto.getCodigo().trim().toUpperCase());
        t.setNombre(dto.getNombre().trim());
        if (dto.getPrefijo() != null) t.setPrefijo(dto.getPrefijo().trim().toUpperCase());
        if (dto.getSiguienteConsecutivo() != null && dto.getSiguienteConsecutivo() > 0) {
            // No permitir RETROCEDER el consecutivo: numerar por debajo del actual repetiría números
            // de asientos ya emitidos. Solo se acepta mantener o avanzar.
            if (t.getSiguienteConsecutivo() != null && dto.getSiguienteConsecutivo() < t.getSiguienteConsecutivo())
                throw new RuntimeException("El siguiente consecutivo (" + dto.getSiguienteConsecutivo()
                        + ") no puede ser menor al actual (" + t.getSiguienteConsecutivo() + "): duplicaría números ya usados.");
            t.setSiguienteConsecutivo(dto.getSiguienteConsecutivo());
        }
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) t.setEstado(dto.getEstado());
        return toDto(repo.save(t));
    }

    @Transactional
    public TipoComprobanteManualDTO inactivar(Integer id) {
        return cambiarEstado(id, "INACTIVO");
    }

    @Transactional
    public TipoComprobanteManualDTO activar(Integer id) {
        return cambiarEstado(id, "ACTIVO");
    }

    /**
     * Devuelve el número que se asignaría (PREFIJO-N) SIN incrementar el
     * contador. Se usa para numerar el asiento antes de validarlo; el consumo
     * real (incremento) se confirma con siguienteNumero() solo si el asiento
     * se creó bien, para no dejar huecos en la numeración ante un fallo.
     */
    @Transactional(readOnly = true)
    public String peekSiguienteNumero(Integer id) {
        TipoComprobanteManual t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de comprobante no encontrado: " + id));
        int actual = t.getSiguienteConsecutivo() != null ? t.getSiguienteConsecutivo() : 1;
        String prefijo = (t.getPrefijo() != null && !t.getPrefijo().isBlank()) ? t.getPrefijo() : t.getCodigo();
        return prefijo + "-" + actual;
    }

    /**
     * Devuelve el consecutivo actual e incrementa el contador. Usado al numerar
     * un asiento manual con este tipo. Devuelve el número formateado (PREFIJO-N).
     */
    @Transactional
    public String siguienteNumero(Integer id) {
        TipoComprobanteManual t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de comprobante no encontrado: " + id));
        int actual = t.getSiguienteConsecutivo() != null ? t.getSiguienteConsecutivo() : 1;
        t.setSiguienteConsecutivo(actual + 1);
        repo.save(t);
        String prefijo = (t.getPrefijo() != null && !t.getPrefijo().isBlank()) ? t.getPrefijo() : t.getCodigo();
        return prefijo + "-" + actual;
    }

    private TipoComprobanteManualDTO cambiarEstado(Integer id, String estado) {
        TipoComprobanteManual t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de comprobante no encontrado: " + id));
        t.setEstado(estado);
        return toDto(repo.save(t));
    }

    private void validar(TipoComprobanteManualDTO dto, Integer idActual) {
        if (dto.getCodigo() == null || dto.getCodigo().isBlank())
            throw new RuntimeException("El código es obligatorio");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new RuntimeException("El nombre es obligatorio");
        repo.findByCodigo(dto.getCodigo().trim().toUpperCase()).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual))
                throw new RuntimeException("Ya existe un tipo de comprobante con código " + dto.getCodigo());
        });
    }

    private TipoComprobanteManualDTO toDto(TipoComprobanteManual t) {
        TipoComprobanteManualDTO dto = new TipoComprobanteManualDTO();
        dto.setId(t.getId());
        dto.setCodigo(t.getCodigo());
        dto.setNombre(t.getNombre());
        dto.setPrefijo(t.getPrefijo());
        dto.setSiguienteConsecutivo(t.getSiguienteConsecutivo());
        dto.setEstado(t.getEstado());
        return dto;
    }
}
