package com.pazzioliweb.ventasmodule.repository;

import com.pazzioliweb.ventasmodule.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.items WHERE p.numeroPedido = :numeroPedido")
    Optional<Pedido> findByNumeroPedidoWithItems(@Param("numeroPedido") String numeroPedido);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.items WHERE p.cliente.terceroId = :clienteId")
    List<Pedido> findPedidosByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('PENDIENTE', 'EN_PROCESO')")
    List<Pedido> findPedidosActivos();

    @Query("SELECT p FROM Pedido p WHERE p.cotizacionId = :cotizacionId")
    Optional<Pedido> findByCotizacionId(@Param("cotizacionId") Long cotizacionId);
}

