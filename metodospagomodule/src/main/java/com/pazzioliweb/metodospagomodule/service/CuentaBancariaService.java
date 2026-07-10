package com.pazzioliweb.metodospagomodule.service;

import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import com.pazzioliweb.metodospagomodule.dtos.CuentaBancariaDTO;
import com.pazzioliweb.metodospagomodule.entity.CuentaBancaria;
import com.pazzioliweb.metodospagomodule.repositori.CuentaBancariaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaBancariaService {

    private final CuentaBancariaRepository repo;
    private final CuentaContableRepository cuentaContableRepo;

    @PersistenceContext
    private EntityManager em;

    public CuentaBancariaService(CuentaBancariaRepository repo,
                                  CuentaContableRepository cuentaContableRepo) {
        this.repo = repo;
        this.cuentaContableRepo = cuentaContableRepo;
    }

    @Transactional(readOnly = true)
    public List<CuentaBancariaDTO> listar() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CuentaBancariaDTO> listarActivas() {
        return repo.findByActivoTrueOrderByBancoAscNombreAsc().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CuentaBancariaDTO obtener(Long id) {
        CuentaBancaria c = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta bancaria no encontrada: " + id));
        return toDto(c);
    }

    @Transactional
    public CuentaBancariaDTO crear(CuentaBancariaDTO dto) {
        validar(dto, null);
        CuentaBancaria c = new CuentaBancaria();
        aplicarDto(c, dto);
        c.setFechaCreacion(LocalDateTime.now());
        return toDto(repo.save(c));
    }

    @Transactional
    public CuentaBancariaDTO actualizar(Long id, CuentaBancariaDTO dto) {
        CuentaBancaria c = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta bancaria no encontrada: " + id));
        validar(dto, id);
        aplicarDto(c, dto);
        return toDto(repo.save(c));
    }

    @Transactional
    public void eliminar(Long id) {
        CuentaBancaria c = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta bancaria no encontrada: " + id));
        c.setActivo(false);
        repo.save(c);
    }

    private void validar(CuentaBancariaDTO dto, Long idActual) {
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        // El campo "banco" ya no se captura en el formulario (Caja y Bancos): la
        // cuenta se identifica por su nombre y su cuenta contable PUC. Se autocompleta
        // en aplicarDto() para respetar el NOT NULL de la columna.
        if (dto.getCuentaContableId() == null)
            throw new IllegalArgumentException("La cuenta contable asociada es obligatoria.");

        boolean exists = idActual == null
                ? repo.existsByNombreIgnoreCase(dto.getNombre().trim())
                : repo.existsByNombreIgnoreCaseAndIdNot(dto.getNombre().trim(), idActual);
        if (exists)
            throw new IllegalArgumentException("Ya existe una cuenta bancaria con el nombre '" + dto.getNombre() + "'");
    }

    private void aplicarDto(CuentaBancaria c, CuentaBancariaDTO dto) {
        c.setNombre(dto.getNombre().trim());
        // "banco" tolerante a null: si no viene (nuevo form sin ese campo), se
        // autocompleta con el nombre para no violar el NOT NULL de la columna.
        c.setBanco(dto.getBanco() != null && !dto.getBanco().isBlank()
                ? dto.getBanco().trim() : dto.getNombre().trim());
        c.setNumeroCuenta(dto.getNumeroCuenta());
        if (dto.getTipo() != null) {
            try {
                c.setTipo(CuentaBancaria.TipoCuenta.valueOf(dto.getTipo().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                c.setTipo(CuentaBancaria.TipoCuenta.AHORROS);
            }
        }
        c.setMoneda(dto.getMoneda() != null ? dto.getMoneda() : "COP");
        c.setSaldoInicial(dto.getSaldoInicial() != null ? dto.getSaldoInicial() : BigDecimal.ZERO);
        if (dto.getFechaApertura() != null) c.setFechaApertura(dto.getFechaApertura());

        CuentaContable cc = cuentaContableRepo.findById(dto.getCuentaContableId())
                .orElseThrow(() -> new EntityNotFoundException("Cuenta contable no encontrada: " + dto.getCuentaContableId()));
        c.setCuentaContable(cc);

        c.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        c.setObservaciones(dto.getObservaciones());
    }

    /**
     * Calcula el saldo actual = saldo_inicial + (débitos − créditos) de los asientos
     * que afectan la cuenta contable asociada a esta cuenta bancaria. Usa SQL nativo
     * para evitar dependencia circular con contabilidadmodule.
     */
    private BigDecimal calcularSaldoActual(CuentaBancaria c) {
        if (c.getCuentaContable() == null) return c.getSaldoInicial();
        try {
            Object result = em.createNativeQuery(
                    "SELECT COALESCE(SUM(l.debito - l.credito), 0) " +
                    "FROM asientos_contables_lineas l " +
                    "WHERE l.cuenta_contable_id = :ccId")
                    .setParameter("ccId", c.getCuentaContable().getId())
                    .getSingleResult();
            BigDecimal movimientos = (result instanceof BigDecimal) ? (BigDecimal) result
                    : new BigDecimal(result.toString());
            return c.getSaldoInicial().add(movimientos);
        } catch (Exception ex) {
            // La tabla de asientos podría no existir todavía si la Fase 2 no se ha aplicado.
            return c.getSaldoInicial();
        }
    }

    public CuentaBancariaDTO toDto(CuentaBancaria c) {
        CuentaBancariaDTO d = new CuentaBancariaDTO();
        d.setId(c.getId());
        d.setNombre(c.getNombre());
        d.setBanco(c.getBanco());
        d.setNumeroCuenta(c.getNumeroCuenta());
        d.setTipo(c.getTipo() != null ? c.getTipo().name() : null);
        d.setMoneda(c.getMoneda());
        d.setSaldoInicial(c.getSaldoInicial());
        d.setFechaApertura(c.getFechaApertura());
        if (c.getCuentaContable() != null) {
            d.setCuentaContableId(c.getCuentaContable().getId());
            d.setCuentaContableCodigo(c.getCuentaContable().getCodigo());
            d.setCuentaContableNombre(c.getCuentaContable().getNombre());
        }
        d.setActivo(c.getActivo());
        d.setObservaciones(c.getObservaciones());
        d.setFechaCreacion(c.getFechaCreacion());
        d.setSaldoActual(calcularSaldoActual(c));
        return d;
    }
}
