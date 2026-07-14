package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.ConfiguracionContableMapaRepository;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Resuelve las cuentas contables estándar del PUC colombiano usadas por
 * los asientos automáticos. Si una cuenta no existe en la BD, devuelve
 * Optional.empty() para que el caller decida qué hacer (saltar el asiento,
 * mostrar warning, etc.).
 *
 * Códigos por defecto siguen el Decreto 2650 (PUC Colombia). Si el negocio
 * usa otros códigos, basta con renombrar las constantes o agregar un
 * mecanismo de override.
 */
@Service
public class ConfiguracionContableService {

    private static final Logger log = LoggerFactory.getLogger(ConfiguracionContableService.class);

    private final CuentaContableRepository cuentaRepo;
    private final ConfiguracionContableMapaRepository mapaRepo;

    public ConfiguracionContableService(CuentaContableRepository cuentaRepo,
                                        ConfiguracionContableMapaRepository mapaRepo) {
        this.cuentaRepo = cuentaRepo;
        this.mapaRepo = mapaRepo;
    }

    private String cod(String clave, String fallback) {
        try {
            return mapaRepo.findByClave(clave).map(m -> m.getCodigoCuenta()).orElse(fallback);
        } catch (Exception e) {
            return fallback;
        }
    }

    /** Códigos del PUC colombiano estándar. */
    public static final String COD_CAJA_GENERAL    = "1105";   // Caja general
    public static final String COD_CXC_CLIENTES    = "1305";   // Cuentas por cobrar a clientes
    public static final String COD_INVENTARIOS     = "1435";   // Mercancías no fabricadas
    public static final String COD_IVA_DESCONTABLE = "240810"; // IVA descontable (compras)
    public static final String COD_CXP_PROVEEDORES = "2205";   // Proveedores nacionales
    public static final String COD_IVA_GENERADO    = "240801"; // IVA generado en ventas
    public static final String COD_INGRESOS_VENTAS = "4135";   // Comercio al por mayor y menor
    public static final String COD_GASTOS_GENERALES= "5195";   // Diversos (gastos)
    public static final String COD_DEVOLUCION_VENTAS = "4175"; // Devoluciones en ventas
    public static final String COD_COSTO_VENTAS    = "6135";   // Costo de mercancía vendida
    // ── Ajustes de inventario (EI/SI manuales) ─────────────────────────
    // Sobrantes/donaciones recibidas (entrada manual de inventario):
    //   Contrapartida típica → 4295 (Ingresos no operacionales — recuperaciones)
    //   Alternativamente 3105 (Capital), 4295 o cuenta de "ajuste de inventario"
    public static final String COD_AJUSTE_ENTRADA_INV = "4295"; // Ingreso no operacional / sobrantes
    // Pérdidas/dañados/consumo interno (salida manual de inventario):
    //   Contrapartida típica → 5195 o 5295 (Gastos diversos / pérdidas)
    public static final String COD_AJUSTE_SALIDA_INV  = "5295"; // Pérdidas en inventario

    // ── Retenciones por pagar (cuando la empresa es agente retenedor) ──
    /** Retención en la fuente por pagar (compras): 236505 */
    public static final String COD_RETEFUENTE_PAGAR = "236505";
    /** Retención de IVA por pagar (compras): 236540 */
    public static final String COD_RETEIVA_PAGAR    = "236540";
    /** Retención de ICA por pagar (compras): 236570 */
    public static final String COD_RETEICA_PAGAR    = "236570";

    // ── Cuentas de cierre anual ──
    /** Ganancia/pérdida del ejercicio (resultado del ejercicio): 3605 */
    public static final String COD_RESULTADO_EJERCICIO = "3605";

    // ── Anticipo de impuestos (retenciones SUFRIDAS — el cliente nos retuvo) ──
    /** ReteFuente que NOS practicaron — anticipo de renta: 135515 */
    public static final String COD_ANTICIPO_RETEFUENTE = "135515";
    /** ReteIVA que NOS practicaron — anticipo de IVA: 135517 */
    public static final String COD_ANTICIPO_RETEIVA    = "135517";
    /** ReteICA que NOS practicaron — anticipo de ICA: 135518 */
    public static final String COD_ANTICIPO_RETEICA    = "135518";
    /** Descuento comercial condicionado RECIBIDO de un proveedor (ingreso financiero): 421040.
     *  Se usa al pagar (Comprobante de Egreso) cuando el proveedor nos concede pronto pago. */
    public static final String COD_DESCUENTO_CONDICIONADO = "421040";
    /** Descuento comercial condicionado CONCEDIDO a un cliente (gasto financiero): 530535.
     *  Se usa en el Recibo de Caja cuando le concedemos pronto pago al cliente. NO debe ir a
     *  421040 (cuenta de INGRESO): un descuento otorgado es un gasto, no un menor ingreso. */
    public static final String COD_DESCUENTO_CONCEDIDO   = "530535";
    /** Averías / pérdida en recaudo: 539540 */
    public static final String COD_AVERIAS             = "539540";
    /** Fletes: 513535 */
    public static final String COD_FLETES              = "513535";
    /** Anticipos y avances recibidos DE CLIENTES (saldo a favor del cliente): 2805 (o subcuenta). */
    public static final String COD_ANTICIPO_CLIENTES   = "2805";
    /** Anticipos y avances A PROVEEDORES (saldo a favor de la empresa): 1330 (o subcuenta). */
    public static final String COD_ANTICIPO_PROVEEDORES = "1330";

    @Transactional(readOnly = true)
    public Optional<CuentaContable> buscarPorCodigo(String codigo) {
        if (codigo == null) return Optional.empty();
        // Prioridad 1: coincidencia exacta SI es cuenta de movimiento (hoja del árbol).
        Optional<CuentaContable> exacta = cuentaRepo.findByCodigo(codigo);
        if (exacta.isPresent() && Boolean.TRUE.equals(exacta.get().getEsMovimiento())) {
            return exacta;
        }
        // Prioridad 2: la primera subcuenta de movimiento cuyo código empieza con el prefijo.
        // Esto evita que asientos automáticos se hagan en cuentas padre (no permitido contablemente).
        Optional<CuentaContable> primeraHoja = cuentaRepo.findAll().stream()
                .filter(c -> c.getCodigo() != null
                          && c.getCodigo().startsWith(codigo)
                          && Boolean.TRUE.equals(c.getEsMovimiento()))
                .sorted((a, b) -> a.getCodigo().compareTo(b.getCodigo()))
                .findFirst();
        if (primeraHoja.isPresent()) return primeraHoja;
        // Prioridad 3 (último recurso): devolver la coincidencia exacta aunque sea padre.
        return exacta;
    }

    public CuentaContable obtenerOError(String codigo, String descripcion) {
        return buscarPorCodigo(codigo).orElseThrow(() -> new IllegalStateException(
            "No se encuentra la cuenta contable '" + codigo + "' (" + descripcion + "). " +
            "Créela en el PUC del sistema para que los asientos contables se generen."
        ));
    }

    public Optional<CuentaContable> obtenerOpcional(String codigo, String descripcion) {
        Optional<CuentaContable> r = buscarPorCodigo(codigo);
        if (r.isEmpty()) {
            log.warn("[ConfiguracionContable] Cuenta '{}' ({}) no configurada. Se omite la línea correspondiente del asiento.",
                     codigo, descripcion);
        }
        return r;
    }

    public Optional<CuentaContable> cajaGeneral()          { return buscarPorCodigo(cod("COD_CAJA_GENERAL",         COD_CAJA_GENERAL)); }
    public Optional<CuentaContable> cxcClientes()          { return buscarPorCodigo(cod("COD_CXC_CLIENTES",         COD_CXC_CLIENTES)); }
    public Optional<CuentaContable> inventarios()          { return buscarPorCodigo(cod("COD_INVENTARIOS",          COD_INVENTARIOS)); }
    public Optional<CuentaContable> ivaDescontable()       { return buscarPorCodigo(cod("COD_IVA_DESCONTABLE",      COD_IVA_DESCONTABLE)); }
    public Optional<CuentaContable> cxpProveedores()       { return buscarPorCodigo(cod("COD_CXP_PROVEEDORES",      COD_CXP_PROVEEDORES)); }
    public Optional<CuentaContable> ivaGenerado()          { return buscarPorCodigo(cod("COD_IVA_GENERADO",         COD_IVA_GENERADO)); }
    public Optional<CuentaContable> ingresosVentas()       { return buscarPorCodigo(cod("COD_INGRESOS_VENTAS",      COD_INGRESOS_VENTAS)); }
    public Optional<CuentaContable> gastosGenerales()      { return buscarPorCodigo(cod("COD_GASTOS_GENERALES",     COD_GASTOS_GENERALES)); }
    public Optional<CuentaContable> devolucionVentas()     { return buscarPorCodigo(cod("COD_DEVOLUCION_VENTAS",    COD_DEVOLUCION_VENTAS)); }
    public Optional<CuentaContable> costoVentas()          { return buscarPorCodigo(cod("COD_COSTO_VENTAS",         COD_COSTO_VENTAS)); }
    public Optional<CuentaContable> ajusteEntradaInventario(){ return buscarPorCodigo(cod("COD_AJUSTE_ENTRADA_INV", COD_AJUSTE_ENTRADA_INV)); }
    public Optional<CuentaContable> ajusteSalidaInventario() { return buscarPorCodigo(cod("COD_AJUSTE_SALIDA_INV",  COD_AJUSTE_SALIDA_INV)); }
    public Optional<CuentaContable> retefuentePagar()      { return buscarPorCodigo(cod("COD_RETEFUENTE_PAGAR",     COD_RETEFUENTE_PAGAR)); }
    public Optional<CuentaContable> reteivaPagar()         { return buscarPorCodigo(cod("COD_RETEIVA_PAGAR",        COD_RETEIVA_PAGAR)); }
    public Optional<CuentaContable> reteicaPagar()         { return buscarPorCodigo(cod("COD_RETEICA_PAGAR",        COD_RETEICA_PAGAR)); }
    public Optional<CuentaContable> resultadoEjercicio()   { return buscarPorCodigo(cod("COD_RESULTADO_EJERCICIO",  COD_RESULTADO_EJERCICIO)); }
    public Optional<CuentaContable> anticipoRetefuente()   { return buscarPorCodigo(cod("COD_ANTICIPO_RETEFUENTE",  COD_ANTICIPO_RETEFUENTE)); }
    public Optional<CuentaContable> anticipoReteiva()      { return buscarPorCodigo(cod("COD_ANTICIPO_RETEIVA",     COD_ANTICIPO_RETEIVA)); }
    public Optional<CuentaContable> anticipoReteica()      { return buscarPorCodigo(cod("COD_ANTICIPO_RETEICA",     COD_ANTICIPO_RETEICA)); }
    public Optional<CuentaContable> descuentoCondicionado(){ return buscarPorCodigo(cod("COD_DESCUENTO_CONDICIONADO", COD_DESCUENTO_CONDICIONADO)); }
    public Optional<CuentaContable> descuentoConcedido()   { return buscarPorCodigo(cod("COD_DESCUENTO_CONCEDIDO",   COD_DESCUENTO_CONCEDIDO)); }
    public Optional<CuentaContable> averias()              { return buscarPorCodigo(cod("COD_AVERIAS",               COD_AVERIAS)); }
    public Optional<CuentaContable> fletes()               { return buscarPorCodigo(cod("COD_FLETES",                COD_FLETES)); }
    public Optional<CuentaContable> anticipoClientes()     { return buscarPorCodigo(cod("COD_ANTICIPO_CLIENTES",    COD_ANTICIPO_CLIENTES)); }
    public Optional<CuentaContable> anticipoProveedores()  { return buscarPorCodigo(cod("COD_ANTICIPO_PROVEEDORES", COD_ANTICIPO_PROVEEDORES)); }
}
