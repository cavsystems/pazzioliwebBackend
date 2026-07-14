package com.pazzioliweb.comprasmodule.repository;

import com.pazzioliweb.comprasmodule.entity.CuentaPorPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CuentaPorPagarRepository extends JpaRepository<CuentaPorPagar, Long> {
    List<CuentaPorPagar> findByNumeroFactura(String numeroFactura);
    List<CuentaPorPagar> findByProveedor_TerceroIdAndEstadoIn(Integer proveedorId, List<String> estados);

    /**
     * Cuentas por pagar de un proveedor que SÍ son pagables — excluye las CxP cuya
     * orden de compra asociada está en estado PENDIENTE (es decir, mercadería
     * todavía no recibida físicamente). Esto evita que el cajero abone facturas
     * cuya mercancía aún no ha llegado.
     *
     * Las CxP manuales (sin orden asociada por numero_factura) se incluyen.
     */
    @Query(value = """
            SELECT cpp.* FROM cuentas_por_pagar cpp
             WHERE cpp.proveedor_id = :proveedorId
               AND cpp.estado IN ('PENDIENTE','PARCIAL','VENCIDA')
               AND NOT EXISTS (
                   SELECT 1 FROM ordenes_compra oc
                    WHERE oc.numero_orden = cpp.numero_factura
                      AND oc.estado = 'PENDIENTE'
               )
             ORDER BY cpp.fecha_vencimiento ASC
            """, nativeQuery = true)
    List<CuentaPorPagar> findPagablesByProveedor(@Param("proveedorId") Integer proveedorId);

    /**
     * Marca como VENCIDA las CxP cuya fecha de vencimiento ya pasó y aún tienen saldo.
     * Solo afecta las que están PENDIENTE o PARCIAL (no toca PAGADA/ANULADA).
     * La ejecuta el scheduler diario de cartera. Devuelve el número de filas afectadas.
     */
    @Modifying
    @Query("UPDATE CuentaPorPagar c SET c.estado = 'VENCIDA' " +
           "WHERE c.fechaVencimiento < :hoy AND c.saldo > 0 AND c.estado IN ('PENDIENTE', 'PARCIAL')")
    int marcarVencidas(@Param("hoy") LocalDate hoy);
}
