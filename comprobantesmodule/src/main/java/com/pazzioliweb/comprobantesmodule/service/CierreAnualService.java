package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContable;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoContableRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de cierre anual contable.
 *
 * Funcionalidad principal (art. 27 D.R. 2649 y NIC 1):
 *  1. Cerrar las cuentas de INGRESO (clase 4) y GASTO/COSTO (clase 5, 6, 7) contra
 *     la cuenta 3605 "Resultado del ejercicio" al final del año fiscal (31-Dic).
 *  2. Trasladar el saldo del 3605 a 3705 "Resultados de ejercicios anteriores"
 *     en el asiento de apertura del nuevo año (01-Ene).
 *
 * Las cuentas de balance (ACTIVO, PASIVO, PATRIMONIO) NO se cierran — sus saldos
 * pasan tal cual al nuevo año.
 *
 * Idempotente: si ya existe un asiento marcado como CIERRE para el año,
 * no genera otro.
 */
@Service
public class CierreAnualService {

    private static final Logger log = LoggerFactory.getLogger(CierreAnualService.class);

    private final AsientoContableRepository asientoRepo;
    private final AsientoContableService asientoService;
    private final ConfiguracionContableService configContable;
    private final PeriodoContableService periodoContableService;

    @PersistenceContext
    private EntityManager em;

    public CierreAnualService(AsientoContableRepository asientoRepo,
                               AsientoContableService asientoService,
                               ConfiguracionContableService configContable,
                               PeriodoContableService periodoContableService) {
        this.asientoRepo = asientoRepo;
        this.asientoService = asientoService;
        this.configContable = configContable;
        this.periodoContableService = periodoContableService;
    }

    public static class ResultadoCierre {
        public Integer anio;
        public BigDecimal totalIngresos;
        public BigDecimal totalGastos;
        public BigDecimal utilidadOPerdida;
        public Long asientoCierreId;
        public Long asientoAperturaId;
        public String mensaje;
    }

    /**
     * Ejecuta el cierre anual del año dado. Crea un único asiento de cierre con
     * todas las cuentas de resultado contra 3605, y un asiento de apertura el
     * 01-Ene del año siguiente trasladando 3605 → 3705.
     */
    @Transactional
    public ResultadoCierre cerrarAnio(int anio, String usuario) {
        // No se debe permitir cierre si todavía hay periodos abiertos del año.
        // Más bien, los 12 meses deben estar cerrados antes (control fiscal).
        for (int mes = 1; mes <= 12; mes++) {
            if (!periodoContableService.estaCerrado(LocalDate.of(anio, mes, 1))) {
                throw new IllegalStateException(
                    "No se puede cerrar el año " + anio + " porque el periodo " + mes + "/" + anio +
                    " todavía está ABIERTO. Cierre los 12 periodos antes de hacer el cierre anual.");
            }
        }

        // Idempotencia por PASO: el cierre y la apertura son asientos independientes (REQUIRES_NEW),
        // así que si un intento previo dejó el CIERRE pero falló antes de la APERTURA, reintentar
        // debe COMPLETAR la apertura (traslado 3605→3705), no abortar. Solo se considera "ya cerrado"
        // cuando AMBOS asientos existen (antes se retornaba con solo el CIERRE y el traslado a 3705
        // se perdía para siempre, dejando la utilidad fuera del patrimonio del año siguiente).
        java.util.Optional<AsientoContable> cierreExistente =
                asientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId("CIERRE", (long) anio);
        java.util.Optional<AsientoContable> aperturaExistente =
                asientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId("APERTURA", (long) (anio + 1));
        if (cierreExistente.isPresent() && aperturaExistente.isPresent()) {
            ResultadoCierre r = new ResultadoCierre();
            r.anio = anio;
            r.asientoCierreId = cierreExistente.get().getId();
            r.asientoAperturaId = aperturaExistente.get().getId();
            r.mensaje = "El año " + anio + " ya estaba cerrado.";
            return r;
        }

        LocalDate fechaCierre = LocalDate.of(anio, 12, 31);
        LocalDate fechaApertura = LocalDate.of(anio + 1, 1, 1);

        // ── 1. Recolectar saldos por cuenta para el año (solo cuentas de resultado) ──
        // Una cuenta es de resultado si su código empieza por 4 (INGRESO), 5, 6 o 7 (GASTO/COSTO).
        @SuppressWarnings("unchecked")
        List<Object[]> saldos = em.createNativeQuery(
            "SELECT cc.cuenta_id, cc.codigo, cc.nombre, cc.tipo, " +
            "       COALESCE(SUM(l.debito), 0) AS deb, COALESCE(SUM(l.credito), 0) AS cred " +
            "  FROM cuentas_contables cc " +
            "  JOIN asientos_contables_lineas l ON l.cuenta_contable_id = cc.cuenta_id " +
            "  JOIN asientos_contables a ON a.id = l.asiento_id " +
            " WHERE YEAR(a.fecha) = :anio " +
            "   AND a.estado = 'CONFIRMADO' " +
            "   AND a.documento_origen_tipo NOT IN ('CIERRE','APERTURA') " +
            "   AND cc.es_movimiento = 1 " +
            "   AND (cc.codigo LIKE '4%' OR cc.codigo LIKE '5%' OR cc.codigo LIKE '6%' OR cc.codigo LIKE '7%') " +
            " GROUP BY cc.cuenta_id, cc.codigo, cc.nombre, cc.tipo " +
            " HAVING ABS(deb - cred) > 0.01"
        ).setParameter("anio", anio).getResultList();

        if (saldos.isEmpty()) {
            ResultadoCierre r = new ResultadoCierre();
            r.anio = anio;
            r.mensaje = "No hay movimientos de resultado para el año " + anio + ". Cierre omitido.";
            r.totalIngresos = BigDecimal.ZERO;
            r.totalGastos = BigDecimal.ZERO;
            r.utilidadOPerdida = BigDecimal.ZERO;
            return r;
        }

        CuentaContable resultado = configContable.resultadoEjercicio().orElseThrow(() ->
                new IllegalStateException("Cuenta 3605 'Resultado del ejercicio' no configurada en el PUC."));
        // 3605 DEBE ser cuenta de movimiento (hoja), no padre — si no, no admite asientos.
        if (resultado.getEsMovimiento() != null && !resultado.getEsMovimiento()) {
            throw new IllegalStateException(
                "La cuenta 3605 '" + resultado.getNombre() + "' está marcada como PADRE (no movimiento). " +
                "El cierre anual no se puede ejecutar. Cree una subcuenta hoja de 3605 (ej. 360505) y márquela como movimiento."
            );
        }

        List<AsientoContableService.LineaDTO> lineasCierre = new ArrayList<>();
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        for (Object[] row : saldos) {
            Integer cuentaId = ((Number) row[0]).intValue();
            String codigo = (String) row[1];
            String nombre = (String) row[2];
            BigDecimal deb = (BigDecimal) row[4];
            BigDecimal cred = (BigDecimal) row[5];
            // Se cierra por el SIGNO REAL del saldo, no por el prefijo de la clase. Así una cuenta
            // de resultado con saldo contrario a su naturaleza (p. ej. 4175 devoluciones, débito;
            // o una 5x con saldo crédito por reverso) también se cierra y no arrastra saldo. Antes
            // se asumía la naturaleza por la clase (4=crédito, resto=débito) y se OMITÍA la cuenta si
            // su saldo iba al contrario → 3605 quedaba sobrestimado y la cuenta sin cerrar.
            BigDecimal saldo = deb.subtract(cred).setScale(2, RoundingMode.HALF_UP);
            if (saldo.abs().compareTo(new BigDecimal("0.01")) <= 0) continue;

            if (saldo.signum() < 0) {
                // Saldo neto CRÉDITO (ingreso típico): para cerrar → DÉBITO la cuenta por |saldo|.
                BigDecimal monto = saldo.negate();
                lineasCierre.add(AsientoContableService.LineaDTO.debito(cuentaId, monto,
                        "Cierre " + codigo + " - " + nombre));
                totalIngresos = totalIngresos.add(monto);
            } else {
                // Saldo neto DÉBITO (gasto/costo típico, o contra-cuenta como 4175): CRÉDITO la cuenta.
                lineasCierre.add(AsientoContableService.LineaDTO.credito(cuentaId, saldo,
                        "Cierre " + codigo + " - " + nombre));
                totalGastos = totalGastos.add(saldo);
            }
        }

        // Línea de balance contra 3605 (utilidad o pérdida del ejercicio)
        BigDecimal utilidadOPerdida = totalIngresos.subtract(totalGastos);
        if (utilidadOPerdida.compareTo(BigDecimal.ZERO) > 0) {
            // Utilidad: CR 3605 por la diferencia
            lineasCierre.add(AsientoContableService.LineaDTO.credito(resultado.getId(), utilidadOPerdida,
                    "Utilidad del ejercicio " + anio));
        } else if (utilidadOPerdida.compareTo(BigDecimal.ZERO) < 0) {
            // Pérdida: DR 3605 por el valor absoluto
            lineasCierre.add(AsientoContableService.LineaDTO.debito(resultado.getId(), utilidadOPerdida.abs(),
                    "Pérdida del ejercicio " + anio));
        }

        // Generar el asiento de CIERRE solo si no existe (idempotente). NO se reabre/cierra
        // diciembre: generarAsiento ya exime a los asientos de CIERRE/APERTURA del bloqueo de
        // periodo, y el reabrir/cerrar dejaba diciembre reabierto si algo fallaba en el medio.
        AsientoContable asientoCierre = cierreExistente.orElseGet(() ->
                asientoService.generarAsiento(
                        "CIERRE-" + anio,
                        fechaCierre,
                        "Cierre del ejercicio " + anio + " (saldo cuentas de resultado contra 3605)",
                        "CIERRE",
                        (long) anio,
                        null,
                        lineasCierre
                ));

        // ── 2. Asiento de apertura: trasladar 3605 → 3705 al 01-Ene del año siguiente ──
        // Solo se genera si aún no existe (idempotente); así un reintento tras un cierre a
        // medias completa el traslado que faltaba.
        Long asientoAperturaId = aperturaExistente.map(AsientoContable::getId).orElse(null);
        if (aperturaExistente.isEmpty() && utilidadOPerdida.compareTo(BigDecimal.ZERO) != 0) {
            java.util.Optional<CuentaContable> optResAnt = configContable.buscarPorCodigo("3705")
                    .filter(c -> c.getEsMovimiento() == null || c.getEsMovimiento());
            if (optResAnt.isPresent()) {
                CuentaContable resAnteriores = optResAnt.get();
                List<AsientoContableService.LineaDTO> lineasApertura = new ArrayList<>();
                BigDecimal montoAbs = utilidadOPerdida.abs();
                if (utilidadOPerdida.compareTo(BigDecimal.ZERO) > 0) {
                    // Utilidad anterior: DR 3605 (lo deja en 0), CR 3705 (lo aumenta)
                    lineasApertura.add(AsientoContableService.LineaDTO.debito(resultado.getId(), montoAbs,
                            "Traslado utilidad ejercicio " + anio + " a resultados de ejercicios anteriores"));
                    lineasApertura.add(AsientoContableService.LineaDTO.credito(resAnteriores.getId(), montoAbs,
                            "Acumulado utilidad años anteriores tras cierre " + anio));
                } else {
                    // Pérdida anterior: CR 3605 (lo deja en 0), DR 3705 (lo disminuye)
                    lineasApertura.add(AsientoContableService.LineaDTO.credito(resultado.getId(), montoAbs,
                            "Traslado pérdida ejercicio " + anio + " a resultados de ejercicios anteriores"));
                    lineasApertura.add(AsientoContableService.LineaDTO.debito(resAnteriores.getId(), montoAbs,
                            "Acumulado pérdida años anteriores tras cierre " + anio));
                }
                AsientoContable asientoApertura = asientoService.generarAsiento(
                        "APERTURA-" + (anio + 1),
                        fechaApertura,
                        "Apertura ejercicio " + (anio + 1) + " - traslado 3605 → 3705",
                        "APERTURA",
                        (long) (anio + 1),
                        null,
                        lineasApertura
                );
                asientoAperturaId = asientoApertura.getId();
            } else {
                log.warn("[CierreAnual] Cuenta 3705 no configurada o es cuenta padre — no se generó asiento de apertura.");
            }
        }

        ResultadoCierre r = new ResultadoCierre();
        r.anio = anio;
        r.totalIngresos = totalIngresos;
        r.totalGastos = totalGastos;
        r.utilidadOPerdida = utilidadOPerdida;
        r.asientoCierreId = asientoCierre.getId();
        r.asientoAperturaId = asientoAperturaId;
        r.mensaje = "Cierre " + anio + " ejecutado. Ingresos " + totalIngresos +
                    " — Gastos " + totalGastos + " — " +
                    (utilidadOPerdida.compareTo(BigDecimal.ZERO) >= 0 ? "Utilidad " : "Pérdida ") +
                    utilidadOPerdida.abs();
        return r;
    }
}
