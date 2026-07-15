package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContableLinea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AsientoContableLineaRepository extends JpaRepository<AsientoContableLinea, Long> {

    /** ¿La cuenta tiene al menos un movimiento (línea de asiento)? Se usa para permitir el borrado
     *  real de una cuenta solo si NO tiene movimientos. */
    boolean existsByCuentaContable_Id(Integer cuentaId);

    /** Líneas que afectan una cuenta contable específica (para libro mayor). */
    @Query("SELECT l FROM AsientoContableLinea l " +
           "JOIN FETCH l.asiento a " +
           "WHERE l.cuentaContable.id = :cuentaId " +
           "  AND a.fecha BETWEEN :desde AND :hasta " +
           "  AND a.estado = 'CONFIRMADO' " +
           "ORDER BY a.fecha ASC, a.id ASC, l.orden ASC")
    List<AsientoContableLinea> librodeMayorPorCuenta(
            @Param("cuentaId") Integer cuentaId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    /**
     * Libro mayor agregado: incluye movimientos de una cuenta padre y todas sus subcuentas
     * (buscadas por prefijo de código). Útil para consultar "Bancos" o "Moneda nacional"
     * y ver los movimientos consolidados de las subcuentas reales.
     */
    @Query("SELECT l FROM AsientoContableLinea l " +
           "JOIN FETCH l.asiento a " +
           "JOIN FETCH l.cuentaContable cc " +
           "WHERE cc.codigo LIKE CONCAT(:prefijoCodigo, '%') " +
           "  AND a.fecha BETWEEN :desde AND :hasta " +
           "  AND a.estado = 'CONFIRMADO' " +
           "ORDER BY a.fecha ASC, a.id ASC, l.orden ASC")
    List<AsientoContableLinea> librodeMayorPorPrefijoCodigo(
            @Param("prefijoCodigo") String prefijoCodigo,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    /** Saldo acumulado de una cuenta hasta una fecha (excluye fecha inclusiva). */
    @Query("SELECT COALESCE(SUM(l.debito - l.credito), 0) " +
           "FROM AsientoContableLinea l " +
           "JOIN l.asiento a " +
           "WHERE l.cuentaContable.id = :cuentaId " +
           "  AND a.fecha < :fecha " +
           "  AND a.estado = 'CONFIRMADO'")
    BigDecimal saldoAntesDe(@Param("cuentaId") Integer cuentaId, @Param("fecha") LocalDate fecha);

    /** Saldo acumulado de una cuenta y sus subcuentas (por prefijo) hasta una fecha. */
    @Query("SELECT COALESCE(SUM(l.debito - l.credito), 0) " +
           "FROM AsientoContableLinea l " +
           "JOIN l.asiento a " +
           "JOIN l.cuentaContable cc " +
           "WHERE cc.codigo LIKE CONCAT(:prefijoCodigo, '%') " +
           "  AND a.fecha < :fecha " +
           "  AND a.estado = 'CONFIRMADO'")
    BigDecimal saldoAntesDePorPrefijo(@Param("prefijoCodigo") String prefijoCodigo, @Param("fecha") LocalDate fecha);

    /** Para Balance de Comprobación. */
    @Query("SELECT l.cuentaContable.id, l.cuentaContable.codigo, l.cuentaContable.nombre, " +
           "       SUM(l.debito), SUM(l.credito) " +
           "FROM AsientoContableLinea l " +
           "JOIN l.asiento a " +
           "WHERE a.fecha BETWEEN :desde AND :hasta " +
           "  AND a.estado = 'CONFIRMADO' " +
           "GROUP BY l.cuentaContable.id, l.cuentaContable.codigo, l.cuentaContable.nombre " +
           "ORDER BY l.cuentaContable.codigo")
    List<Object[]> balanceComprobacion(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    /** Cuentas con saldo inicial != 0 al final del período anterior — útil para
     *  el Balance de Comprobación: aún sin movimientos en el período hay que mostrarlas
     *  para mantener la partida doble en saldos finales (ej: capital aportado). */
    @Query("SELECT l.cuentaContable.id, l.cuentaContable.codigo, l.cuentaContable.nombre, " +
           "       SUM(l.debito - l.credito) AS saldo " +
           "FROM AsientoContableLinea l " +
           "JOIN l.asiento a " +
           "WHERE a.fecha < :desde " +
           "  AND a.estado = 'CONFIRMADO' " +
           "GROUP BY l.cuentaContable.id, l.cuentaContable.codigo, l.cuentaContable.nombre " +
           "HAVING ABS(SUM(l.debito - l.credito)) > 0.005 " +
           "ORDER BY l.cuentaContable.codigo")
    List<Object[]> cuentasConSaldoAntesDe(@Param("desde") LocalDate desde);
}
