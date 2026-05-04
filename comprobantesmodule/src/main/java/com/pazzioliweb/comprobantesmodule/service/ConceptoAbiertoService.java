package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.dtos.ConceptoAbiertoDTO;
import com.pazzioliweb.comprobantesmodule.entity.ConceptoAbierto;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.ConceptoAbiertoRepository;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConceptoAbiertoService {

    private static final Set<String> TIPOS_VALIDOS = Set.of("RECIBO", "EGRESO", "AMBOS");

    private final ConceptoAbiertoRepository repo;
    private final CuentaContableRepository ccRepo;
    private final TercerosRepository tercerosRepository;

    public ConceptoAbiertoService(ConceptoAbiertoRepository repo,
                                  CuentaContableRepository ccRepo,
                                  TercerosRepository tercerosRepository) {
        this.repo = repo;
        this.ccRepo = ccRepo;
        this.tercerosRepository = tercerosRepository;
    }

    @Transactional(readOnly = true)
    public List<ConceptoAbiertoDTO> listarPorTipo(String tipo) {
        String t = tipo == null ? "" : tipo.toUpperCase();
        if (!TIPOS_VALIDOS.contains(t)) {
            throw new RuntimeException("Tipo inválido: " + tipo);
        }
        return repo.listarPorTipo(t).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConceptoAbiertoDTO> listarTodos() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConceptoAbiertoDTO buscarPorId(Long id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Concepto abierto no encontrado: " + id));
    }

    @Transactional
    public ConceptoAbiertoDTO crear(ConceptoAbiertoDTO dto) {
        validar(dto);
        ConceptoAbierto c = new ConceptoAbierto();
        aplicar(c, dto);
        return toDto(repo.save(c));
    }

    @Transactional
    public ConceptoAbiertoDTO actualizar(Long id, ConceptoAbiertoDTO dto) {
        ConceptoAbierto c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Concepto abierto no encontrado: " + id));
        validar(dto);
        aplicar(c, dto);
        return toDto(repo.save(c));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Concepto abierto no encontrado: " + id);
        }
        repo.deleteById(id);
    }

    private void validar(ConceptoAbiertoDTO dto) {
        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank())
            throw new RuntimeException("La descripción es obligatoria");
        if (dto.getTipo() == null || !TIPOS_VALIDOS.contains(dto.getTipo().toUpperCase()))
            throw new RuntimeException("Tipo inválido. Use RECIBO, EGRESO o AMBOS");
        if (dto.getCuentaContableId() == null)
            throw new RuntimeException("La cuenta contable es obligatoria");
        // Trazabilidad: o tercero o (beneficiario + documento) obligatorios
        boolean tieneTercero = dto.getTerceroId() != null;
        boolean tieneBeneficiario =
                dto.getBeneficiarioNombre() != null && !dto.getBeneficiarioNombre().isBlank()
                && dto.getBeneficiarioDocumento() != null && !dto.getBeneficiarioDocumento().isBlank();
        if (!tieneTercero && !tieneBeneficiario) {
            throw new RuntimeException("Debe asociar un tercero o registrar nombre y documento del beneficiario/pagador");
        }
    }

    private void aplicar(ConceptoAbierto c, ConceptoAbiertoDTO dto) {
        c.setDescripcion(dto.getDescripcion());
        c.setTipo(dto.getTipo().toUpperCase());

        CuentaContable cc = ccRepo.findById(dto.getCuentaContableId())
                .orElseThrow(() -> new RuntimeException("Cuenta contable no encontrada: " + dto.getCuentaContableId()));
        if (Boolean.FALSE.equals(cc.getEsMovimiento())) {
            throw new RuntimeException("La cuenta contable seleccionada no acepta movimientos");
        }
        c.setCuentaContable(cc);

        if (dto.getTerceroId() != null) {
            Terceros t = tercerosRepository.findById(dto.getTerceroId())
                    .orElseThrow(() -> new RuntimeException("Tercero no encontrado: " + dto.getTerceroId()));
            c.setTercero(t);
        } else {
            c.setTercero(null);
        }

        c.setBeneficiarioNombre(dto.getBeneficiarioNombre());
        c.setBeneficiarioDocumento(dto.getBeneficiarioDocumento());
        c.setInfoExtra(dto.getInfoExtra());
        if (dto.getEstado() != null) c.setEstado(dto.getEstado());
    }

    private ConceptoAbiertoDTO toDto(ConceptoAbierto c) {
        ConceptoAbiertoDTO d = new ConceptoAbiertoDTO();
        d.setId(c.getId());
        d.setDescripcion(c.getDescripcion());
        d.setTipo(c.getTipo());
        d.setEstado(c.getEstado());
        d.setBeneficiarioNombre(c.getBeneficiarioNombre());
        d.setBeneficiarioDocumento(c.getBeneficiarioDocumento());
        d.setInfoExtra(c.getInfoExtra());
        if (c.getCuentaContable() != null) {
            d.setCuentaContableId(c.getCuentaContable().getId());
            d.setCuentaContableCodigo(c.getCuentaContable().getCodigo());
            d.setCuentaContableNombre(c.getCuentaContable().getNombre());
        }
        if (c.getTercero() != null) {
            d.setTerceroId(c.getTercero().getTerceroId());
            String nombre = (c.getTercero().getNombre1() != null ? c.getTercero().getNombre1() : "")
                    + (c.getTercero().getApellido1() != null ? " " + c.getTercero().getApellido1() : "");
            d.setTerceroNombre(nombre.trim());
            d.setTerceroIdentificacion(c.getTercero().getIdentificacion());
        }
        return d;
    }
}
