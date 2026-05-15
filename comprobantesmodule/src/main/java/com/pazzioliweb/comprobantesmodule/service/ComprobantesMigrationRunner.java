package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Al arrancar la aplicación:
 *  1. Crea los 7 comprobantes LEGACY (uno por cada tipo de movimiento) si no existen.
 *  2. Asigna ese LEGACY a TODOS los movimientos viejos que no tienen comprobante_id.
 *
 * Idempotente: si ya están migrados, no hace nada.
 */
@Component
@Order(50)
public class ComprobantesMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ComprobantesMigrationRunner.class);

    private final AsignacionComprobanteService asignacion;

    @PersistenceContext
    private EntityManager em;

    public ComprobantesMigrationRunner(AsignacionComprobanteService asignacion) {
        this.asignacion = asignacion;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("[ComprobantesMigration] Iniciando migración LEGACY...");

        // Verificar primero que la tabla comprobantes_contables exista.
        // Si no, salir limpiamente — la migración correrá cuando el DBA cree el schema.
        if (!tablaExiste("comprobantes_contables")) {
            log.warn("[ComprobantesMigration]  · La tabla 'comprobantes_contables' no existe todavía. " +
                     "Saltando migración. Cree el schema y reinicie la app.");
            return;
        }

        try {
            ejecutarMigracion();
            log.info("[ComprobantesMigration] Migración LEGACY completada.");
        } catch (Exception ex) {
            // Cualquier error en la migración NO debe tumbar la app.
            log.error("[ComprobantesMigration] Error en migración LEGACY (la app sigue arriba): {}",
                      ex.getMessage(), ex);
        }
    }

    @Transactional
    public void ejecutarMigracion() {
        // 1. Crear comprobantes LEGACY por tipo
        ComprobanteContable legFC = asignacion.obtenerOCrearLegacy(TipoMovimientoComprobante.FC);
        ComprobanteContable legVC = asignacion.obtenerOCrearLegacy(TipoMovimientoComprobante.VC);
        ComprobanteContable legCC = asignacion.obtenerOCrearLegacy(TipoMovimientoComprobante.CC);
        // Asegurar LEGACY-CR (no lo usamos abajo, pero queremos que exista)
        asignacion.obtenerOCrearLegacy(TipoMovimientoComprobante.CR);
        ComprobanteContable legRC = asignacion.obtenerOCrearLegacy(TipoMovimientoComprobante.RC);
        ComprobanteContable legCE = asignacion.obtenerOCrearLegacy(TipoMovimientoComprobante.CE);
        ComprobanteContable legDV = asignacion.obtenerOCrearLegacy(TipoMovimientoComprobante.DV);

        // 2. Asignar LEGACY a movimientos viejos sin comprobante.
        //    Las consultas son nativas porque trabajamos por tabla; los UPDATE solo
        //    afectan filas con comprobante_id NULL así que son idempotentes.

        // Ventas: FC para no-crédito, VC si tiene método de pago crédito.
        actualizar(
            "UPDATE ventas v SET v.comprobante_id = :idVC " +
            "WHERE v.comprobante_id IS NULL AND EXISTS (" +
            "  SELECT 1 FROM venta_metodos_pago vmp " +
            "  JOIN metodos_pago mp ON mp.metodo_pago_id = vmp.metodo_pago_id " +
            "  WHERE vmp.venta_id = v.id AND mp.tipo_negociacion = 'Credito')",
            "idVC", legVC.getId(), "ventas a VC");

        actualizar(
            "UPDATE ventas SET comprobante_id = :idFC WHERE comprobante_id IS NULL",
            "idFC", legFC.getId(), "ventas restantes a FC");

        // Órdenes de compra: por ahora todas a CC (no diferenciamos crédito en la tabla todavía)
        actualizar(
            "UPDATE ordenes_compra SET comprobante_id = :id WHERE comprobante_id IS NULL",
            "id", legCC.getId(), "órdenes de compra a CC");

        // Devoluciones
        actualizar(
            "UPDATE devoluciones_venta SET comprobante_id = :id WHERE comprobante_id IS NULL",
            "id", legDV.getId(), "devoluciones a DV");

        // Recibos de caja
        actualizar(
            "UPDATE recibos_caja SET comprobante_id = :id WHERE comprobante_id IS NULL",
            "id", legRC.getId(), "recibos de caja a RC");

        // Comprobantes de egreso
        actualizar(
            "UPDATE comprobantes_egreso SET comprobante_id = :id WHERE comprobante_id IS NULL",
            "id", legCE.getId(), "egresos a CE");

        // Legalizaciones — heredan el comprobante de su orden de compra.
        // (Query sin parámetro: hace JOIN directo y copia el comprobante_id.)
        actualizarSinParam(
            "UPDATE legalizaciones l " +
            "JOIN ordenes_compra oc ON oc.id = l.orden_compra_id " +
            "SET l.comprobante_id = oc.comprobante_id, " +
            "    l.consecutivo_comprobante = oc.consecutivo_comprobante " +
            "WHERE l.comprobante_id IS NULL AND oc.comprobante_id IS NOT NULL",
            "legalizaciones heredando comprobante de la orden");

        // Las legalizaciones huérfanas (sin orden con comprobante) caen al LEGACY-CC.
        actualizar(
            "UPDATE legalizaciones SET comprobante_id = :id WHERE comprobante_id IS NULL",
            "id", legCC.getId(), "legalizaciones huérfanas a LEGACY-CC");
    }

    /** Verifica si una tabla existe en la BD actual usando information_schema. */
    private boolean tablaExiste(String nombreTabla) {
        try {
            Object result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE() AND table_name = :t")
                    .setParameter("t", nombreTabla)
                    .getSingleResult();
            return ((Number) result).longValue() > 0;
        } catch (Exception ex) {
            log.warn("[ComprobantesMigration]  · no pude verificar si la tabla '{}' existe: {}",
                     nombreTabla, ex.getMessage());
            return false;
        }
    }

    private void actualizar(String sql, String paramName, Object paramValue, String descripcion) {
        try {
            int n = em.createNativeQuery(sql)
                    .setParameter(paramName, paramValue)
                    .executeUpdate();
            if (n > 0) log.info("[ComprobantesMigration]  · {} actualizados: {}", n, descripcion);
        } catch (Exception ex) {
            // Tabla puede no existir todavía si el schema aún no se generó completo. No es fatal.
            log.warn("[ComprobantesMigration]  · skip {} ({})", descripcion, ex.getMessage());
        }
    }

    private void actualizarSinParam(String sql, String descripcion) {
        try {
            int n = em.createNativeQuery(sql).executeUpdate();
            if (n > 0) log.info("[ComprobantesMigration]  · {} actualizados: {}", n, descripcion);
        } catch (Exception ex) {
            log.warn("[ComprobantesMigration]  · skip {} ({})", descripcion, ex.getMessage());
        }
    }
}
