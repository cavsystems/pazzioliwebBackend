package com.pazzioliweb.ventasmodule.repository;

import com.pazzioliweb.ventasmodule.entity.CuentaPorCobrar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CuentaPorCobrarRepository extends JpaRepository<CuentaPorCobrar, Long> {

    List<CuentaPorCobrar> findByEstado(String estado);

    List<CuentaPorCobrar> findByCliente_TerceroId(Integer clienteId);

    List<CuentaPorCobrar> findByNumeroVenta(String numeroVenta);

    List<CuentaPorCobrar> findByEstadoIn(List<String> estados);

    /**
     * Suma el saldo pendiente de todas las CxC activas (PENDIENTE o PARCIAL) de un cliente.
     * Retorna 0 si no tiene CxC pendientes.
     */
    @Query("SELECT COALESCE(SUM(c.saldo), 0) FROM CuentaPorCobrar c WHERE c.cliente.terceroId = :clienteId AND c.estado IN ('PENDIENTE', 'PARCIAL', 'VENCIDA')")
    BigDecimal sumSaldoPendienteByClienteId(@Param("clienteId") Integer clienteId);

    /**
     * Marca como VENCIDA las CxC cuya fecha de vencimiento ya pasó y aún tienen saldo.
     * Solo afecta las que están PENDIENTE o PARCIAL (no toca PAGADA/ANULADA).
     * La ejecuta el scheduler diario de cartera. Devuelve el número de filas afectadas.
     */
    @Modifying
    @Query("UPDATE CuentaPorCobrar c SET c.estado = 'VENCIDA' " +
           "WHERE c.fechaVencimiento < :hoy AND c.saldo > 0 AND c.estado IN ('PENDIENTE', 'PARCIAL')")
    int marcarVencidas(@Param("hoy") LocalDate hoy);
}
