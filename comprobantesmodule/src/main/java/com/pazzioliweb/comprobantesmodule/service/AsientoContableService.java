package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContable;
import com.pazzioliweb.comprobantesmodule.entity.AsientoContableLinea;
import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoContableLineaRepository;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoContableRepository;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generador de asientos contables (partida doble) desde documentos
 * (RC, CE, FC, VC, CC, CR, DV). Provee una API genérica `generarAsiento`
 * que recibe las líneas ya calculadas por el caller, y helpers para casos
 * comunes (abono CxC, pago CxP, venta, compra).
 *
 * El caller (RC, CE, etc.) sabe los montos y las cuentas; este service
 * solo se encarga de empaquetarlas en un asiento balanceado y persistirlo.
 */
@Service
public class AsientoContableService {

    private static final Logger log = LoggerFactory.getLogger(AsientoContableService.class);

    private final AsientoContableRepository asientoRepo;
    private final AsientoContableLineaRepository lineaRepo;
    private final CuentaContableRepository cuentaRepo;
    private final PeriodoContableService periodoService;

    public AsientoContableService(AsientoContableRepository asientoRepo,
                                   AsientoContableLineaRepository lineaRepo,
                                   CuentaContableRepository cuentaRepo,
                                   PeriodoContableService periodoService) {
        this.asientoRepo = asientoRepo;
        this.lineaRepo = lineaRepo;
        this.cuentaRepo = cuentaRepo;
        this.periodoService = periodoService;
    }

    /** Excepción específica para cuando se intenta registrar en periodo cerrado. */
    public static class PeriodoCerradoException extends RuntimeException {
        public PeriodoCerradoException(String msg) { super(msg); }
    }

    // ─── DTO simple para construir líneas ───────────────────────
    public static class LineaDTO {
        public Integer cuentaContableId;
        public BigDecimal debito;
        public BigDecimal credito;
        public Integer terceroId;
        public String terceroNombre;
        public String descripcion;

        public static LineaDTO debito(Integer cuentaId, BigDecimal monto, String desc) {
            LineaDTO l = new LineaDTO();
            l.cuentaContableId = cuentaId;
            l.debito = monto;
            l.credito = BigDecimal.ZERO;
            l.descripcion = desc;
            return l;
        }

        public static LineaDTO credito(Integer cuentaId, BigDecimal monto, String desc) {
            LineaDTO l = new LineaDTO();
            l.cuentaContableId = cuentaId;
            l.debito = BigDecimal.ZERO;
            l.credito = monto;
            l.descripcion = desc;
            return l;
        }

        public LineaDTO conTercero(Integer terceroId, String terceroNombre) {
            this.terceroId = terceroId;
            this.terceroNombre = terceroNombre;
            return this;
        }
    }

    /**
     * Genera un asiento contable con las líneas dadas. Valida partida doble.
     * Si ya existe un asiento para el documento origen, NO crea otro (idempotente).
     */
    @Transactional
    public AsientoContable generarAsiento(String numeroAsiento, LocalDate fecha, String descripcion,
                                           String documentoOrigenTipo, Long documentoOrigenId,
                                           ComprobanteContable comprobante,
                                           List<LineaDTO> lineas) {
        // Idempotencia: si ya existe asiento para este documento, regresarlo
        if (documentoOrigenTipo != null && documentoOrigenId != null) {
            Optional<AsientoContable> existente = asientoRepo
                    .findByDocumentoOrigenTipoAndDocumentoOrigenId(documentoOrigenTipo, documentoOrigenId);
            if (existente.isPresent()) {
                log.info("[AsientoContable] Ya existe asiento para {} #{}", documentoOrigenTipo, documentoOrigenId);
                return existente.get();
            }
        }

        if (lineas == null || lineas.size() < 2) {
            throw new IllegalArgumentException("Un asiento debe tener al menos 2 líneas.");
        }

        // Filtrar líneas vacías (debito=0 y credito=0)
        List<LineaDTO> validas = new ArrayList<>();
        for (LineaDTO l : lineas) {
            BigDecimal d = l.debito != null ? l.debito : BigDecimal.ZERO;
            BigDecimal c = l.credito != null ? l.credito : BigDecimal.ZERO;
            if (d.compareTo(BigDecimal.ZERO) == 0 && c.compareTo(BigDecimal.ZERO) == 0) continue;
            l.debito = d;
            l.credito = c;
            validas.add(l);
        }

        if (validas.size() < 2) {
            throw new IllegalArgumentException("Después de filtrar líneas en 0, debe haber al menos 2 líneas con valor.");
        }

        BigDecimal totalDebito = validas.stream().map(l -> l.debito).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredito = validas.stream().map(l -> l.credito).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Validar partida doble (con tolerancia de 1 centavo por redondeo)
        BigDecimal diff = totalDebito.subtract(totalCredito).abs();
        if (diff.compareTo(new BigDecimal("0.02")) > 0) {
            throw new IllegalStateException(
                "Asiento no cuadra: débitos=" + totalDebito + " créditos=" + totalCredito
                + " (diferencia " + diff + ") para documento " + documentoOrigenTipo + " #" + documentoOrigenId);
        }

        LocalDate fechaAsiento = fecha != null ? fecha : LocalDate.now();

        // Validar periodo contable. Si el mes/año del asiento está CERRADO,
        // bloquear. Esto NO afecta nada existente: por defecto todos los periodos
        // están abiertos hasta que el admin los cierre manualmente.
        if (periodoService.estaCerrado(fechaAsiento)) {
            throw new PeriodoCerradoException(
                "Periodo contable " + fechaAsiento.getMonthValue() + "/" + fechaAsiento.getYear() +
                " está CERRADO. No se pueden registrar nuevos movimientos en él. " +
                "Reabra el periodo desde Contabilidad → Periodos contables si lo necesita."
            );
        }

        AsientoContable asiento = new AsientoContable();
        asiento.setNumeroAsiento(numeroAsiento);
        asiento.setFecha(fechaAsiento);
        asiento.setDescripcion(descripcion);
        asiento.setDocumentoOrigenTipo(documentoOrigenTipo);
        asiento.setDocumentoOrigenId(documentoOrigenId);
        asiento.setComprobante(comprobante);
        asiento.setTotalDebito(totalDebito);
        asiento.setTotalCredito(totalCredito);
        asiento.setEstado("CONFIRMADO");

        int orden = 1;
        for (LineaDTO ldto : validas) {
            CuentaContable cc = cuentaRepo.findById(ldto.cuentaContableId)
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta contable no existe: " + ldto.cuentaContableId));
            AsientoContableLinea linea = new AsientoContableLinea();
            linea.setAsiento(asiento);
            linea.setCuentaContable(cc);
            linea.setDebito(ldto.debito);
            linea.setCredito(ldto.credito);
            linea.setTerceroId(ldto.terceroId);
            linea.setTerceroNombre(ldto.terceroNombre);
            linea.setDescripcion(ldto.descripcion);
            linea.setOrden(orden++);
            asiento.getLineas().add(linea);
        }

        return asientoRepo.save(asiento);
    }

    /** Anula el asiento de un documento (lo marca como ANULADO sin borrar). */
    @Transactional
    public void anularAsientoDeDocumento(String documentoOrigenTipo, Long documentoOrigenId) {
        asientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId(documentoOrigenTipo, documentoOrigenId)
                .ifPresent(a -> {
                    a.setEstado("ANULADO");
                    asientoRepo.save(a);
                });
    }

    @Transactional(readOnly = true)
    public Optional<AsientoContable> obtenerPorDocumento(String tipo, Long id) {
        return asientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId(tipo, id);
    }
}
