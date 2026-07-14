package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.ActivoFijo;
import com.pazzioliweb.comprobantesmodule.entity.AsientoContable;
import com.pazzioliweb.comprobantesmodule.entity.DepreciacionDetalle;
import com.pazzioliweb.comprobantesmodule.repositori.ActivoFijoRepository;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoContableRepository;
import com.pazzioliweb.comprobantesmodule.repositori.DepreciacionDetalleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Gestión de activos fijos y cálculo de depreciación por LÍNEA RECTA.
 *
 * La corrida mensual genera UN asiento por periodo (documentoOrigenTipo="DEP",
 * documentoOrigenId = aaaamm) con una línea de débito (gasto) y una de crédito
 * (depreciación acumulada) por cada activo. Es idempotente por periodo: si ya
 * existe el asiento del mes, no vuelve a depreciar.
 */
@Service
public class ActivoFijoService {

    private final ActivoFijoRepository activoRepo;
    private final AsientoContableRepository asientoRepo;
    private final AsientoContableService asientoService;
    private final DepreciacionDetalleRepository detalleRepo;

    public ActivoFijoService(ActivoFijoRepository activoRepo,
                             AsientoContableRepository asientoRepo,
                             AsientoContableService asientoService,
                             DepreciacionDetalleRepository detalleRepo) {
        this.activoRepo = activoRepo;
        this.asientoRepo = asientoRepo;
        this.asientoService = asientoService;
        this.detalleRepo = detalleRepo;
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ActivoFijo> listar() {
        return activoRepo.findAllByOrderByFechaAdquisicionDesc();
    }

    @Transactional(readOnly = true)
    public ActivoFijo obtener(Long id) {
        return activoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activo fijo no encontrado: " + id));
    }

    @Transactional
    public ActivoFijo crear(ActivoFijo a) {
        validar(a);
        a.setId(null);
        a.setDepreciacionAcumulada(a.getDepreciacionAcumulada() != null ? a.getDepreciacionAcumulada() : BigDecimal.ZERO);
        a.setMesesDepreciados(a.getMesesDepreciados() != null ? a.getMesesDepreciados() : 0);
        a.setEstado(a.getEstado() != null ? a.getEstado() : "ACTIVO");
        a.setMetodo("LINEA_RECTA");
        a.setFechaCreacion(LocalDate.now());
        return activoRepo.save(a);
    }

    @Transactional
    public ActivoFijo actualizar(Long id, ActivoFijo datos) {
        ActivoFijo a = obtener(id);
        // No se permite cambiar los importes ni la vida útil si ya tiene depreciación corrida.
        boolean yaDeprecio = a.getMesesDepreciados() != null && a.getMesesDepreciados() > 0;
        if (!yaDeprecio) {
            a.setValorAdquisicion(datos.getValorAdquisicion());
            a.setValorResidual(datos.getValorResidual());
            a.setVidaUtilMeses(datos.getVidaUtilMeses());
            a.setFechaAdquisicion(datos.getFechaAdquisicion());
            a.setFechaInicioDepreciacion(datos.getFechaInicioDepreciacion());
        }
        a.setNombre(datos.getNombre());
        a.setCodigo(datos.getCodigo());
        a.setCuentaActivoId(datos.getCuentaActivoId());
        a.setCuentaDepreciacionId(datos.getCuentaDepreciacionId());
        a.setCuentaGastoId(datos.getCuentaGastoId());
        a.setCentroCostoId(datos.getCentroCostoId());
        a.setObservaciones(datos.getObservaciones());
        validar(a);
        return activoRepo.save(a);
    }

    @Transactional
    public void eliminar(Long id) {
        ActivoFijo a = obtener(id);
        if (a.getDepreciacionAcumulada() != null && a.getDepreciacionAcumulada().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException(
                "No se puede eliminar un activo que ya tiene depreciación registrada. Use 'Dar de baja'.");
        }
        activoRepo.deleteById(id);
    }

    /**
     * Da de baja (retiro) el activo y genera el asiento contable de reverso:
     *   Débito  Depreciación acumulada (por lo ya depreciado)
     *   Débito  Cuenta contrapartida (pérdida/otra) por el valor en libros restante
     *   Crédito Cuenta del activo (por el valor de adquisición)
     * Requiere la cuenta del activo configurada. Si queda valor en libros > 0,
     * exige la cuenta de contrapartida.
     */
    @Transactional
    public ActivoFijo darDeBaja(Long id, LocalDate fecha, Integer cuentaContrapartidaId) {
        ActivoFijo a = obtener(id);
        if ("BAJA".equals(a.getEstado())) throw new IllegalStateException("El activo ya está dado de baja.");

        LocalDate fechaBaja = fecha != null ? fecha : LocalDate.now();
        BigDecimal valorAdq = a.getValorAdquisicion();
        BigDecimal depAcum = a.getDepreciacionAcumulada() != null ? a.getDepreciacionAcumulada() : BigDecimal.ZERO;

        // Depreciar los meses PENDIENTES hasta la fecha de baja para que el valor en libros sea
        // correcto al retirar (genera un asiento de depreciación de puesta al día, tipo DEP_BAJA).
        if (a.getFechaInicioDepreciacion() != null && a.getCuentaGastoId() != null && a.getCuentaDepreciacionId() != null
                && !a.getFechaInicioDepreciacion().isAfter(fechaBaja)) {
            long transcurridos = java.time.temporal.ChronoUnit.MONTHS.between(
                    a.getFechaInicioDepreciacion().withDayOfMonth(1), fechaBaja.withDayOfMonth(1)) + 1;
            int objetivo = (int) Math.min(transcurridos, a.getVidaUtilMeses());
            int pendientes = objetivo - (a.getMesesDepreciados() == null ? 0 : a.getMesesDepreciados());
            if (pendientes > 0) {
                BigDecimal baseDep = valorAdq.subtract(a.getValorResidual());
                BigDecimal cuota = baseDep.divide(new BigDecimal(a.getVidaUtilMeses()), 2, RoundingMode.HALF_UP);
                BigDecimal restante = baseDep.subtract(depAcum).max(BigDecimal.ZERO);
                BigDecimal catchUp = cuota.multiply(new BigDecimal(pendientes)).min(restante);
                if (catchUp.compareTo(BigDecimal.ZERO) > 0) {
                    List<AsientoContableService.LineaDTO> depLineas = new ArrayList<>();
                    AsientoContableService.LineaDTO dGasto = AsientoContableService.LineaDTO
                            .debito(a.getCuentaGastoId(), catchUp, "Depreciación hasta baja " + a.getNombre());
                    dGasto.centroCostoId = a.getCentroCostoId();
                    AsientoContableService.LineaDTO cAcum = AsientoContableService.LineaDTO
                            .credito(a.getCuentaDepreciacionId(), catchUp, "Depreciación hasta baja " + a.getNombre());
                    cAcum.centroCostoId = a.getCentroCostoId();
                    depLineas.add(dGasto);
                    depLineas.add(cAcum);
                    asientoService.generarAsiento("DEP-BAJA-" + a.getId(), fechaBaja,
                            "Depreciación hasta la baja de " + a.getNombre(), "DEP_BAJA", a.getId(), null, depLineas);
                    depAcum = depAcum.add(catchUp);
                    a.setDepreciacionAcumulada(depAcum);
                    a.setMesesDepreciados(objetivo);
                }
            }
        }

        BigDecimal valorEnLibros = valorAdq.subtract(depAcum);

        if (a.getCuentaActivoId() != null) {
            List<AsientoContableService.LineaDTO> lineas = new ArrayList<>();
            String desc = "Baja de activo fijo " + a.getNombre();

            if (depAcum.compareTo(BigDecimal.ZERO) > 0) {
                if (a.getCuentaDepreciacionId() == null)
                    throw new IllegalStateException("El activo no tiene cuenta de depreciación acumulada configurada.");
                AsientoContableService.LineaDTO d1 =
                        AsientoContableService.LineaDTO.debito(a.getCuentaDepreciacionId(), depAcum, desc);
                d1.centroCostoId = a.getCentroCostoId();
                lineas.add(d1);
            }
            if (valorEnLibros.compareTo(BigDecimal.ZERO) > 0) {
                if (cuentaContrapartidaId == null)
                    throw new IllegalStateException("El activo tiene valor en libros de " + valorEnLibros
                            + ". Indique la cuenta de contrapartida (pérdida en baja / otra) para el retiro.");
                AsientoContableService.LineaDTO d2 =
                        AsientoContableService.LineaDTO.debito(cuentaContrapartidaId, valorEnLibros, desc);
                d2.centroCostoId = a.getCentroCostoId();
                lineas.add(d2);
            }
            AsientoContableService.LineaDTO cred =
                    AsientoContableService.LineaDTO.credito(a.getCuentaActivoId(), valorAdq, desc);
            cred.centroCostoId = a.getCentroCostoId();
            lineas.add(cred);

            if (lineas.size() >= 2) {
                asientoService.generarAsiento("BAJA-" + a.getId(), fechaBaja, desc, "BAJA", a.getId(), null, lineas);
            }
        }

        a.setEstado("BAJA");
        return activoRepo.save(a);
    }

    private void validar(ActivoFijo a) {
        if (a.getNombre() == null || a.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del activo es obligatorio.");
        if (a.getValorAdquisicion() == null || a.getValorAdquisicion().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El valor de adquisición debe ser mayor a 0.");
        if (a.getVidaUtilMeses() == null || a.getVidaUtilMeses() <= 0)
            throw new IllegalArgumentException("La vida útil (meses) debe ser mayor a 0.");
        if (a.getValorResidual() == null) a.setValorResidual(BigDecimal.ZERO);
        if (a.getValorResidual().compareTo(a.getValorAdquisicion()) >= 0)
            throw new IllegalArgumentException("El valor residual debe ser menor al valor de adquisición.");
        if (a.getFechaAdquisicion() == null)
            throw new IllegalArgumentException("La fecha de adquisición es obligatoria.");
        if (a.getFechaInicioDepreciacion() == null)
            a.setFechaInicioDepreciacion(a.getFechaAdquisicion());
    }

    // ── Corrida de depreciación ───────────────────────────────────────────────

    public static class ResultadoDepreciacion {
        public String numeroAsiento;
        public BigDecimal totalDepreciado = BigDecimal.ZERO;
        public int activosDepreciados = 0;
        public String periodo;
    }

    /** Compat: corre sin regenerar. */
    @Transactional
    public ResultadoDepreciacion correrDepreciacion(int anio, int mes) {
        return correrDepreciacion(anio, mes, false);
    }

    @Transactional
    public ResultadoDepreciacion correrDepreciacion(int anio, int mes, boolean regenerar) {
        if (mes < 1 || mes > 12) throw new IllegalArgumentException("Mes inválido: " + mes);

        long periodKey = anio * 100L + mes;
        LocalDate primerDia = LocalDate.of(anio, mes, 1);
        LocalDate fechaAsiento = primerDia.withDayOfMonth(primerDia.lengthOfMonth());
        String periodoTxt = String.format("%02d/%d", mes, anio);
        String numero = "DEP-" + anio + String.format("%02d", mes);

        Optional<AsientoContable> existente =
                asientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId("DEP", periodKey);
        boolean yaCorrido = existente.isPresent() && !"ANULADO".equals(existente.get().getEstado());

        if (yaCorrido && !regenerar) {
            throw new IllegalStateException("La depreciación del periodo " + periodoTxt + " ya fue registrada (asiento "
                    + existente.get().getNumeroAsiento() + "). Marque 'regenerar' para recalcularla e incluir activos nuevos.");
        }

        // Regeneración: anula el asiento previo y reversa los contadores desde el detalle guardado.
        if (yaCorrido) {
            asientoService.anularAsientoDeDocumento("DEP", periodKey);
            for (DepreciacionDetalle det : detalleRepo.findByAnioAndMes(anio, mes)) {
                ActivoFijo a = activoRepo.findById(det.getActivoId()).orElse(null);
                if (a == null) continue;
                a.setDepreciacionAcumulada(a.getDepreciacionAcumulada().subtract(det.getCuota()));
                if (a.getDepreciacionAcumulada().compareTo(BigDecimal.ZERO) < 0) a.setDepreciacionAcumulada(BigDecimal.ZERO);
                a.setMesesDepreciados(Math.max(0, (a.getMesesDepreciados() == null ? 0 : a.getMesesDepreciados()) - 1));
                if ("DEPRECIADO".equals(a.getEstado())) a.setEstado("ACTIVO");
                activoRepo.save(a);
            }
            detalleRepo.deleteByAnioAndMes(anio, mes);
        }

        List<ActivoFijo> activos = activoRepo.findByEstadoOrderByNombreAsc("ACTIVO");
        List<AsientoContableService.LineaDTO> lineas = new ArrayList<>();
        List<ActivoFijo> modificados = new ArrayList<>();
        List<DepreciacionDetalle> detalles = new ArrayList<>();
        ResultadoDepreciacion res = new ResultadoDepreciacion();
        res.periodo = periodoTxt;

        for (ActivoFijo a : activos) {
            // Aún no inicia su depreciación en este periodo
            if (a.getFechaInicioDepreciacion() != null && a.getFechaInicioDepreciacion().isAfter(fechaAsiento)) continue;
            // Ya cumplió su vida útil
            if (a.getMesesDepreciados() != null && a.getMesesDepreciados() >= a.getVidaUtilMeses()) continue;

            BigDecimal base = a.getValorAdquisicion().subtract(a.getValorResidual());
            if (base.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal cuota = base.divide(new BigDecimal(a.getVidaUtilMeses()), 2, RoundingMode.HALF_UP);
            BigDecimal restante = base.subtract(a.getDepreciacionAcumulada());
            BigDecimal cuotaEfectiva = cuota.min(restante);   // última cuota ajusta el redondeo
            if (cuotaEfectiva.compareTo(BigDecimal.ZERO) <= 0) continue;

            if (a.getCuentaGastoId() == null || a.getCuentaDepreciacionId() == null) {
                throw new IllegalStateException("El activo '" + a.getNombre()
                        + "' no tiene configuradas las cuentas de gasto y/o depreciación acumulada.");
            }

            String desc = "Depreciación " + a.getNombre() + " " + periodoTxt;
            AsientoContableService.LineaDTO deb =
                    AsientoContableService.LineaDTO.debito(a.getCuentaGastoId(), cuotaEfectiva, desc);
            deb.centroCostoId = a.getCentroCostoId();
            AsientoContableService.LineaDTO cred =
                    AsientoContableService.LineaDTO.credito(a.getCuentaDepreciacionId(), cuotaEfectiva, desc);
            cred.centroCostoId = a.getCentroCostoId();
            lineas.add(deb);
            lineas.add(cred);

            a.setDepreciacionAcumulada(a.getDepreciacionAcumulada().add(cuotaEfectiva));
            a.setMesesDepreciados((a.getMesesDepreciados() == null ? 0 : a.getMesesDepreciados()) + 1);
            if (a.getDepreciacionAcumulada().compareTo(base) >= 0) a.setEstado("DEPRECIADO");
            modificados.add(a);

            DepreciacionDetalle det = new DepreciacionDetalle();
            det.setActivoId(a.getId());
            det.setAnio(anio);
            det.setMes(mes);
            det.setCuota(cuotaEfectiva);
            det.setNumeroAsiento(numero);
            det.setFechaCreacion(LocalDate.now());
            detalles.add(det);

            res.totalDepreciado = res.totalDepreciado.add(cuotaEfectiva);
            res.activosDepreciados++;
        }

        if (lineas.isEmpty()) {
            throw new IllegalStateException("No hay activos por depreciar en el periodo " + periodoTxt + ".");
        }

        // Persistir activos + detalle ANTES del asiento: si generarAsiento falla,
        // la transacción externa hace rollback de todo (consistencia).
        activoRepo.saveAll(modificados);
        detalleRepo.saveAll(detalles);

        AsientoContable asiento = asientoService.generarAsiento(
                numero, fechaAsiento, "Depreciación de activos fijos " + periodoTxt,
                "DEP", periodKey, null, lineas);

        res.numeroAsiento = asiento.getNumeroAsiento();
        return res;
    }
}
