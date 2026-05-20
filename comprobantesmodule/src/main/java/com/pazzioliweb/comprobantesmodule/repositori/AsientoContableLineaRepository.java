package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContableLinea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AsientoContableLineaRepository extends JpaRepository<AsientoContableLinea, Long> {

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

    /** Saldo acumulado de una cuenta hasta una fecha (excluye fecha inclusiva). */
    @Query("SELECT COALESCE(SUM(l.debito - l.credito), 0) " +
           "FROM AsientoContableLinea l " +
           "JOIN l.asiento a " +
           "WHERE l.cuentaContable.id = :cuentaId " +
           "  AND a.fecha < :fecha " +
           "  AND a.estado = 'CONFIRMADO'")
    BigDecimal saldoAntesDe(@Param("cuentaId") Integer cuentaId, @Param("fecha") LocalDate fecha);

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
}
