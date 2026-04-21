package com.pazzioliweb.ventasmodule.repository;

import com.pazzioliweb.ventasmodule.entity.CuentaPorCobrar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
    @Query("SELECT COALESCE(SUM(c.saldo), 0) FROM CuentaPorCobrar c WHERE c.cliente.terceroId = :clienteId AND c.estado IN ('PENDIENTE', 'PARCIAL')")
    BigDecimal sumSaldoPendienteByClienteId(@Param("clienteId") Integer clienteId);
}
