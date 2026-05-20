package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContableLinea;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoContableLineaRepository;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Reportes contables tipo Alegra:
 *   - Libro Mayor (movimientos por cuenta con saldo corrido)
 *   - Balance de Comprobación (saldos por cuenta en un rango)
 *   - Movimientos por Cuenta Bancaria (extracto interno)
 */
@RestController
@RequestMapping("/api/reportes-contables")
@CrossOrigin(origins = "*")
public class ReportesContablesController {

    private static final Logger log = LoggerFactory.getLogger(ReportesContablesController.class);

    private final AsientoContableLineaRepository lineaRepo;
    private final CuentaContableRepository cuentaRepo;

    @PersistenceContext
    private EntityManager em;

    public ReportesContablesController(AsientoContableLineaRepository lineaRepo,
                                        CuentaContableRepository cuentaRepo) {
        this.lineaRepo = lineaRepo;
        this.cuentaRepo = cuentaRepo;
    }

    /**
     * Libro mayor de una cuenta: lista todos los movimientos en el rango con
     * saldo inicial + corrido + saldo final. Replica el reporte de Alegra
     * "Movimientos por cuenta contable".
     */
    @GetMapping("/libro-mayor")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> libroMayor(
            @RequestParam Integer cuentaId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {

        LocalDate d = desde != null ? LocalDate.parse(desde) : LocalDate.now().withDayOfMonth(1);
        LocalDate h = hasta != null ? LocalDate.parse(hasta) : LocalDate.now();

        CuentaContable cta = cuentaRepo.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta contable no existe: " + cuentaId));

        // Si es cuenta padre (es_movimiento = false), agregar movimientos de TODAS
        // las subcuentas cuyos códigos empiecen con el código de esta cuenta.
        // Si es cuenta de movimiento (hoja), solo sus propios movimientos.
        boolean esMovimiento = Boolean.TRUE.equals(cta.getEsMovimiento());

        log.info("[LibroMayor] consulta cuentaId={} codigo={} nombre={} esMovimiento={} rango={}→{}",
                cuentaId, cta.getCodigo(), cta.getNombre(), esMovimiento, d, h);

        BigDecimal saldoInicial;
        List<AsientoContableLinea> lineas;
        if (esMovimiento) {
            saldoInicial = lineaRepo.saldoAntesDe(cuentaId, d);
            lineas = lineaRepo.librodeMayorPorCuenta(cuentaId, d, h);
            log.info("[LibroMayor] hoja → query exacta. lineas={}, saldoInicial={}", lineas.size(), saldoInicial);
        } else {
            saldoInicial = lineaRepo.saldoAntesDePorPrefijo(cta.getCodigo(), d);
            lineas = lineaRepo.librodeMayorPorPrefijoCodigo(cta.getCodigo(), d, h);
            log.info("[LibroMayor] padre → query prefijo '{}%'. lineas={}, saldoInicial={}",
                    cta.getCodigo(), lineas.size(), saldoInicial);
        }
        if (saldoInicial == null) saldoInicial = BigDecimal.ZERO;

        List<Map<String, Object>> movimientos = new ArrayList<>();
        BigDecimal saldoCorrido = saldoInicial;
        BigDecimal totalDebito = BigDecimal.ZERO;
        BigDecimal totalCredito = BigDecimal.ZERO;
        for (AsientoContableLinea l : lineas) {
            BigDecimal movNeto = l.getDebito().subtract(l.getCredito());
            BigDecimal saldoAntes = saldoCorrido;
            saldoCorrido = saldoCorrido.add(movNeto);
            totalDebito = totalDebito.add(l.getDebito());
            totalCredito = totalCredito.add(l.getCredito());

            Map<String, Object> m = new HashMap<>();
            m.put("fecha", l.getAsiento().getFecha());
            m.put("asiento", l.getAsiento().getNumeroAsiento());
            m.put("estado", l.getAsiento().getEstado());
            m.put("tercero", l.getTerceroNombre());
            m.put("tipoDocumento", l.getAsiento().getDocumentoOrigenTipo());
            m.put("descripcion", l.getDescripcion() != null ? l.getDescripcion() : l.getAsiento().getDescripcion());
            // Para cuentas padre, mostrar el código/nombre real de la subcuenta del movimiento
            CuentaContable cuentaMov = l.getCuentaContable();
            m.put("codigoContable", cuentaMov != null ? cuentaMov.getCodigo() : cta.getCodigo());
            m.put("cuentaContable", cuentaMov != null ? cuentaMov.getNombre() : cta.getNombre());
            m.put("saldoInicial", saldoAntes);
            m.put("debito", l.getDebito());
            m.put("credito", l.getCredito());
            m.put("movimientoNeto", movNeto);
            m.put("saldoFinal", saldoCorrido);
            movimientos.add(m);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("cuentaId", cta.getId());
        resp.put("codigoContable", cta.getCodigo());
        resp.put("cuentaContable", cta.getNombre());
        resp.put("esCuentaPadre", !esMovimiento);
        resp.put("desde", d);
        resp.put("hasta", h);
        resp.put("saldoInicial", saldoInicial);
        resp.put("totalDebito", totalDebito);
        resp.put("totalCredito", totalCredito);
        resp.put("saldoFinal", saldoCorrido);
        resp.put("movimientos", movimientos);
        return ResponseEntity.ok(resp);
    }

    /**
     * Balance de Comprobación: por cada cuenta del PUC, saldo inicial,
     * movimientos del periodo y saldo final.
     */
    @GetMapping("/balance-comprobacion")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> balanceComprobacion(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {

        LocalDate d = desde != null ? LocalDate.parse(desde) : LocalDate.now().withDayOfMonth(1);
        LocalDate h = hasta != null ? LocalDate.parse(hasta) : LocalDate.now();

        List<Object[]> rows = lineaRepo.balanceComprobacion(d, h);
        List<Map<String, Object>> cuentas = new ArrayList<>();
        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;
        for (Object[] r : rows) {
            Integer ccId = (Integer) r[0];
            BigDecimal saldoIni = lineaRepo.saldoAntesDe(ccId, d);
            if (saldoIni == null) saldoIni = BigDecimal.ZERO;
            BigDecimal deb = (BigDecimal) r[3];
            BigDecimal cre = (BigDecimal) r[4];
            BigDecimal saldoFinal = saldoIni.add(deb.subtract(cre));

            totalDebitos = totalDebitos.add(deb);
            totalCreditos = totalCreditos.add(cre);

            Map<String, Object> m = new HashMap<>();
            m.put("cuentaId", ccId);
            m.put("codigo", r[1]);
            m.put("nombre", r[2]);
            m.put("saldoInicial", saldoIni);
            m.put("debito", deb);
            m.put("credito", cre);
            m.put("saldoFinal", saldoFinal);
            cuentas.add(m);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("desde", d);
        resp.put("hasta", h);
        resp.put("totalDebitos", totalDebitos);
        resp.put("totalCreditos", totalCreditos);
        resp.put("cuentas", cuentas);
        return ResponseEntity.ok(resp);
    }

    /**
     * Estado de Resultados (Pérdidas y Ganancias) — Decreto 2649 Colombia.
     * Estructura:
     *   INGRESOS OPERACIONALES (41)
     *   (-) COSTO DE VENTAS (6)
     *   = UTILIDAD BRUTA
     *   (-) GASTOS DE ADMINISTRACIÓN (51)
     *   (-) GASTOS DE VENTAS (52)
     *   = UTILIDAD OPERACIONAL
     *   (+) INGRESOS NO OPERACIONALES (42)
     *   (-) GASTOS NO OPERACIONALES (53)
     *   = UTILIDAD ANTES IMPUESTOS
     *   (-) IMPUESTO DE RENTA (54)
     *   = UTILIDAD NETA
     */
    @GetMapping("/estado-resultados")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> estadoResultados(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {

        LocalDate d = desde != null ? LocalDate.parse(desde) : LocalDate.now().withDayOfYear(1);
        LocalDate h = hasta != null ? LocalDate.parse(hasta) : LocalDate.now();

        // Saldo de cada cuenta del PUC en el periodo
        Map<String, BigDecimal> saldosPorCodigo = saldosPorCodigoEnPeriodo(d, h);

        // Ingresos = créditos − débitos. La devolución (4175) viene con saldo negativo
        // porque su naturaleza es débito, así que ya RESTA automáticamente al sumar "41".
        // No la restamos otra vez.
        BigDecimal ingresosOperacionalesBrutos = sumaPorPrefijo(saldosPorCodigo, "41", true)
                                                     .subtract(sumaPorPrefijo(saldosPorCodigo, "4175", true));
        // 4175 sale como negativo, pero el dato que el usuario quiere ver es el valor positivo.
        BigDecimal devoluciones             = sumaPorPrefijo(saldosPorCodigo, "4175", true).negate();
        BigDecimal ingresosNoOperacionales  = sumaPorPrefijo(saldosPorCodigo, "42", true);

        // Gastos / costos = débitos − créditos (naturaleza débito)
        BigDecimal costoVentas              = sumaPorPrefijo(saldosPorCodigo, "6", false);
        BigDecimal gastosAdmin              = sumaPorPrefijo(saldosPorCodigo, "51", false);
        BigDecimal gastosVentas             = sumaPorPrefijo(saldosPorCodigo, "52", false);
        BigDecimal gastosNoOperacionales    = sumaPorPrefijo(saldosPorCodigo, "53", false);
        BigDecimal impuestoRenta            = sumaPorPrefijo(saldosPorCodigo, "54", false);

        // Cálculos intermedios (Decreto 2649)
        BigDecimal ingresosOperacionales = ingresosOperacionalesBrutos;
        BigDecimal totalIngresosNetos = ingresosOperacionales.subtract(devoluciones);
        BigDecimal utilidadBruta      = totalIngresosNetos.subtract(costoVentas);
        BigDecimal utilidadOperacional= utilidadBruta.subtract(gastosAdmin).subtract(gastosVentas);
        BigDecimal utilidadAntesImp   = utilidadOperacional.add(ingresosNoOperacionales).subtract(gastosNoOperacionales);
        BigDecimal utilidadNeta       = utilidadAntesImp.subtract(impuestoRenta);

        // Detalle por cuenta (para mostrar líneas en el reporte)
        List<Map<String, Object>> detalleIngresos    = detalleClase(saldosPorCodigo, "4", true);
        List<Map<String, Object>> detalleCostos      = detalleClase(saldosPorCodigo, "6", false);
        List<Map<String, Object>> detalleGastosOp    = detalleClasePorPrefijos(saldosPorCodigo, java.util.Arrays.asList("51","52"), false);
        List<Map<String, Object>> detalleNoOp        = detalleClasePorPrefijos(saldosPorCodigo, java.util.Arrays.asList("42","53"), false);

        Map<String, Object> resp = new HashMap<>();
        resp.put("desde", d);
        resp.put("hasta", h);
        resp.put("ingresosOperacionales", ingresosOperacionales);
        resp.put("devoluciones", devoluciones);
        resp.put("totalIngresosNetos", totalIngresosNetos);
        resp.put("costoVentas", costoVentas);
        resp.put("utilidadBruta", utilidadBruta);
        resp.put("gastosAdministracion", gastosAdmin);
        resp.put("gastosVentas", gastosVentas);
        resp.put("utilidadOperacional", utilidadOperacional);
        resp.put("ingresosNoOperacionales", ingresosNoOperacionales);
        resp.put("gastosNoOperacionales", gastosNoOperacionales);
        resp.put("utilidadAntesImpuestos", utilidadAntesImp);
        resp.put("impuestoRenta", impuestoRenta);
        resp.put("utilidadNeta", utilidadNeta);

        resp.put("detalleIngresos", detalleIngresos);
        resp.put("detalleCostos", detalleCostos);
        resp.put("detalleGastosOperacionales", detalleGastosOp);
        resp.put("detalleNoOperacionales", detalleNoOp);

        return ResponseEntity.ok(resp);
    }

    /**
     * Balance General al cierre de una fecha. Ecuación: Activos = Pasivos + Patrimonio.
     * Estructura:
     *   ACTIVOS (1)
     *     Corrientes (11, 12, 13, 14)
     *     No corrientes (15, 16, 17, 18, 19)
     *   PASIVOS (2)
     *     Corrientes (21, 22, 23, 24, 25, 26)
     *     No corrientes (otros)
     *   PATRIMONIO (3) + Utilidad del ejercicio
     */
    @GetMapping("/balance-general")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> balanceGeneral(
            @RequestParam(required = false) String fecha) {
        LocalDate corte = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
        LocalDate inicioAnio = corte.withDayOfYear(1);

        // Saldos acumulados de TODAS las cuentas hasta la fecha de corte
        Map<String, BigDecimal> saldosActivos = saldosPorCodigoAcumulado(corte, true);   // naturaleza débito
        Map<String, BigDecimal> saldosPasivosPat = saldosPorCodigoAcumulado(corte, false); // naturaleza crédito

        // Activos (clase 1)
        BigDecimal activosCorrientes = sumaPorPrefijo(saldosActivos, "11", true)
                .add(sumaPorPrefijo(saldosActivos, "12", true))
                .add(sumaPorPrefijo(saldosActivos, "13", true))
                .add(sumaPorPrefijo(saldosActivos, "14", true));
        BigDecimal activosNoCorrientes = sumaPorPrefijo(saldosActivos, "15", true)
                .add(sumaPorPrefijo(saldosActivos, "16", true))
                .add(sumaPorPrefijo(saldosActivos, "17", true))
                .add(sumaPorPrefijo(saldosActivos, "18", true))
                .add(sumaPorPrefijo(saldosActivos, "19", true));
        BigDecimal totalActivos = activosCorrientes.add(activosNoCorrientes);

        // Pasivos (clase 2)
        BigDecimal pasivosCorrientes = sumaPorPrefijo(saldosPasivosPat, "21", true)
                .add(sumaPorPrefijo(saldosPasivosPat, "22", true))
                .add(sumaPorPrefijo(saldosPasivosPat, "23", true))
                .add(sumaPorPrefijo(saldosPasivosPat, "24", true))
                .add(sumaPorPrefijo(saldosPasivosPat, "25", true))
                .add(sumaPorPrefijo(saldosPasivosPat, "26", true));
        BigDecimal pasivosNoCorrientes = sumaPorPrefijo(saldosPasivosPat, "27", true)
                .add(sumaPorPrefijo(saldosPasivosPat, "28", true))
                .add(sumaPorPrefijo(saldosPasivosPat, "29", true));
        BigDecimal totalPasivos = pasivosCorrientes.add(pasivosNoCorrientes);

        // Patrimonio (clase 3) + Utilidad del ejercicio (P&G del año)
        BigDecimal patrimonioBase = sumaPorPrefijo(saldosPasivosPat, "3", true);

        // Utilidad del ejercicio = ingresos - costos - gastos del año en curso.
        // Suma directa "4": como 4175 (Devoluciones) tiene naturaleza débito, ya viene con
        // saldo negativo en la suma, así que el resultado ya tiene las devoluciones descontadas.
        Map<String, BigDecimal> saldosAnioActual = saldosPorCodigoEnPeriodo(inicioAnio, corte);
        BigDecimal ingresosAnio = sumaPorPrefijo(saldosAnioActual, "4", true);
        BigDecimal gastosCostosAnio = sumaPorPrefijo(saldosAnioActual, "5", false)
                .add(sumaPorPrefijo(saldosAnioActual, "6", false))
                .add(sumaPorPrefijo(saldosAnioActual, "7", false));
        BigDecimal utilidadEjercicio = ingresosAnio.subtract(gastosCostosAnio);

        BigDecimal totalPatrimonio = patrimonioBase.add(utilidadEjercicio);
        BigDecimal totalPasivoMasPatrimonio = totalPasivos.add(totalPatrimonio);
        BigDecimal diferencia = totalActivos.subtract(totalPasivoMasPatrimonio);

        // Detalles por línea
        List<Map<String, Object>> detalleActivos    = detalleClase(saldosActivos, "1", true);
        List<Map<String, Object>> detallePasivos    = detalleClase(saldosPasivosPat, "2", true);
        List<Map<String, Object>> detallePatrimonio = detalleClase(saldosPasivosPat, "3", true);

        Map<String, Object> resp = new HashMap<>();
        resp.put("fecha", corte);
        resp.put("totalActivos", totalActivos);
        resp.put("activosCorrientes", activosCorrientes);
        resp.put("activosNoCorrientes", activosNoCorrientes);
        resp.put("totalPasivos", totalPasivos);
        resp.put("pasivosCorrientes", pasivosCorrientes);
        resp.put("pasivosNoCorrientes", pasivosNoCorrientes);
        resp.put("patrimonioBase", patrimonioBase);
        resp.put("utilidadEjercicio", utilidadEjercicio);
        resp.put("totalPatrimonio", totalPatrimonio);
        resp.put("totalPasivoMasPatrimonio", totalPasivoMasPatrimonio);
        resp.put("diferencia", diferencia);
        resp.put("cuadra", diferencia.abs().compareTo(new BigDecimal("0.02")) <= 0);

        resp.put("detalleActivos", detalleActivos);
        resp.put("detallePasivos", detallePasivos);
        resp.put("detallePatrimonio", detallePatrimonio);

        return ResponseEntity.ok(resp);
    }

    // ─── Helpers para reportes financieros ─────────────────────────

    /** Saldos por código de cuenta en un periodo (solo movimientos del rango). */
    @SuppressWarnings("unchecked")
    private Map<String, BigDecimal> saldosPorCodigoEnPeriodo(LocalDate d, LocalDate h) {
        List<Object[]> rows = em.createNativeQuery(
                "SELECT cc.codigo, SUM(l.debito) AS d, SUM(l.credito) AS c " +
                "FROM asientos_contables_lineas l " +
                "JOIN asientos_contables a ON a.id = l.asiento_id " +
                "JOIN cuentas_contables cc ON cc.cuenta_id = l.cuenta_contable_id " +
                "WHERE a.fecha BETWEEN :d AND :h AND a.estado = 'CONFIRMADO' " +
                "GROUP BY cc.codigo")
                .setParameter("d", d).setParameter("h", h).getResultList();
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] r : rows) {
            String codigo = (String) r[0];
            BigDecimal debito = r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO;
            BigDecimal credito = r[2] != null ? new BigDecimal(r[2].toString()) : BigDecimal.ZERO;
            // Guardamos crédito - débito (positivo = naturaleza crédito = ingresos/pasivos)
            map.put(codigo, credito.subtract(debito));
        }
        return map;
    }

    /** Saldos acumulados hasta una fecha (incluye TODOS los movimientos hasta ese día). */
    @SuppressWarnings("unchecked")
    private Map<String, BigDecimal> saldosPorCodigoAcumulado(LocalDate hasta, boolean naturalezaDebito) {
        List<Object[]> rows = em.createNativeQuery(
                "SELECT cc.codigo, SUM(l.debito) AS d, SUM(l.credito) AS c " +
                "FROM asientos_contables_lineas l " +
                "JOIN asientos_contables a ON a.id = l.asiento_id " +
                "JOIN cuentas_contables cc ON cc.cuenta_id = l.cuenta_contable_id " +
                "WHERE a.fecha <= :h AND a.estado = 'CONFIRMADO' " +
                "GROUP BY cc.codigo")
                .setParameter("h", hasta).getResultList();
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] r : rows) {
            String codigo = (String) r[0];
            BigDecimal debito = r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO;
            BigDecimal credito = r[2] != null ? new BigDecimal(r[2].toString()) : BigDecimal.ZERO;
            // Para activos (naturaleza débito): saldo = débito - crédito
            // Para pasivos/patrimonio: saldo = crédito - débito
            BigDecimal saldo = naturalezaDebito ? debito.subtract(credito) : credito.subtract(debito);
            map.put(codigo, saldo);
        }
        return map;
    }

    /** Suma valores de cuentas cuyo código empieza con un prefijo. */
    private BigDecimal sumaPorPrefijo(Map<String, BigDecimal> saldos, String prefijo, boolean tomarPositivos) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> e : saldos.entrySet()) {
            if (e.getKey() != null && e.getKey().startsWith(prefijo)) {
                BigDecimal v = e.getValue() != null ? e.getValue() : BigDecimal.ZERO;
                total = total.add(tomarPositivos ? v : v.negate());
            }
        }
        return total;
    }

    /** Devuelve detalle por cuenta (solo cuentas que tengan saldo, de movimiento). */
    private List<Map<String, Object>> detalleClase(Map<String, BigDecimal> saldos, String prefijo, boolean tomarPositivos) {
        return detalleClasePorPrefijos(saldos, java.util.Collections.singletonList(prefijo), tomarPositivos);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> detalleClasePorPrefijos(Map<String, BigDecimal> saldos,
                                                               List<String> prefijos, boolean tomarPositivos) {
        // Cuenta los códigos relevantes y obtiene nombres
        List<String> codigos = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : saldos.entrySet()) {
            if (e.getKey() == null) continue;
            for (String p : prefijos) {
                if (e.getKey().startsWith(p)) { codigos.add(e.getKey()); break; }
            }
        }
        if (codigos.isEmpty()) return new ArrayList<>();

        // Cargar nombres
        List<Object[]> rows = em.createNativeQuery(
                "SELECT codigo, nombre FROM cuentas_contables WHERE codigo IN (:codigos) AND es_movimiento = 1")
                .setParameter("codigos", codigos).getResultList();
        Map<String, String> nombres = new HashMap<>();
        for (Object[] r : rows) nombres.put((String) r[0], (String) r[1]);

        List<Map<String, Object>> det = new ArrayList<>();
        for (String c : codigos) {
            // solo cuentas de movimiento con saldo significativo
            if (!nombres.containsKey(c)) continue;
            BigDecimal v = saldos.get(c) != null ? saldos.get(c) : BigDecimal.ZERO;
            if (!tomarPositivos) v = v.negate();
            if (v.compareTo(BigDecimal.ZERO) == 0) continue;
            Map<String, Object> m = new HashMap<>();
            m.put("codigo", c);
            m.put("nombre", nombres.get(c));
            m.put("valor", v);
            det.add(m);
        }
        det.sort((a, b) -> ((String) a.get("codigo")).compareTo((String) b.get("codigo")));
        return det;
    }

    /**
     * Movimientos por cuenta bancaria — extracto interno del banco.
     * Igual al libro mayor pero filtrado solo por cuentas que están asociadas
     * a una cuenta bancaria (FK cuentas_bancarias.cuenta_contable_id).
     */
    @GetMapping("/movimientos-banco")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> movimientosBanco(
            @RequestParam Long cuentaBancariaId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {

        LocalDate d = desde != null ? LocalDate.parse(desde) : LocalDate.now().withDayOfMonth(1);
        LocalDate h = hasta != null ? LocalDate.parse(hasta) : LocalDate.now();

        // Resuelve la cuenta contable y saldo inicial de la cuenta bancaria
        Object[] cbRow = (Object[]) em.createNativeQuery(
                "SELECT cb.id, cb.nombre, cb.banco, cb.numero_cuenta, cb.saldo_inicial, cb.cuenta_contable_id " +
                "FROM cuentas_bancarias cb WHERE cb.id = :id")
                .setParameter("id", cuentaBancariaId)
                .getSingleResult();

        Integer cuentaContableId = ((Number) cbRow[5]).intValue();
        BigDecimal saldoAperturaCb = (BigDecimal) cbRow[4];

        // Reutilizamos libro mayor pero le sumamos el saldo de apertura propio de la cuenta bancaria
        ResponseEntity<Map<String, Object>> lm = libroMayor(cuentaContableId,
                desde, hasta);
        Map<String, Object> body = new HashMap<>(lm.getBody());

        // Sumar saldo de apertura de la cuenta bancaria
        BigDecimal saldoInicialAjustado = ((BigDecimal) body.get("saldoInicial")).add(saldoAperturaCb);
        BigDecimal saldoFinalAjustado = ((BigDecimal) body.get("saldoFinal")).add(saldoAperturaCb);
        body.put("saldoInicial", saldoInicialAjustado);
        body.put("saldoFinal", saldoFinalAjustado);
        body.put("saldoAperturaCuentaBancaria", saldoAperturaCb);

        Map<String, Object> bancoInfo = new HashMap<>();
        bancoInfo.put("id", cbRow[0]);
        bancoInfo.put("nombre", cbRow[1]);
        bancoInfo.put("banco", cbRow[2]);
        bancoInfo.put("numeroCuenta", cbRow[3]);
        body.put("cuentaBancaria", bancoInfo);

        return ResponseEntity.ok(body);
    }
}
