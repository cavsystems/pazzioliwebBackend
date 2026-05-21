package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
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

    public ConfiguracionContableService(CuentaContableRepository cuentaRepo) {
        this.cuentaRepo = cuentaRepo;
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

    public Optional<CuentaContable> cajaGeneral() { return buscarPorCodigo(COD_CAJA_GENERAL); }
    public Optional<CuentaContable> cxcClientes()  { return buscarPorCodigo(COD_CXC_CLIENTES); }
    public Optional<CuentaContable> inventarios()  { return buscarPorCodigo(COD_INVENTARIOS); }
    public Optional<CuentaContable> ivaDescontable(){ return buscarPorCodigo(COD_IVA_DESCONTABLE); }
    public Optional<CuentaContable> cxpProveedores(){ return buscarPorCodigo(COD_CXP_PROVEEDORES); }
    public Optional<CuentaContable> ivaGenerado()  { return buscarPorCodigo(COD_IVA_GENERADO); }
    public Optional<CuentaContable> ingresosVentas(){ return buscarPorCodigo(COD_INGRESOS_VENTAS); }
    public Optional<CuentaContable> gastosGenerales(){ return buscarPorCodigo(COD_GASTOS_GENERALES); }
    public Optional<CuentaContable> devolucionVentas(){ return buscarPorCodigo(COD_DEVOLUCION_VENTAS); }
    public Optional<CuentaContable> costoVentas()  { return buscarPorCodigo(COD_COSTO_VENTAS); }
}
