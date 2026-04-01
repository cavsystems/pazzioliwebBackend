package com.pazzioliweb.cajerosmodule.repositori;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.cajerosmodule.dtos.MovimientoCajeroDTO;
import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;

public interface MovimientoCajeroRepository extends JpaRepository<MovimientoCajero, Long> {

    @Query("""
           SELECT 
               m.movimientoCajeroId   AS movimientoCajeroId,
               m.detalleCajero.detalleCajeroId AS detalleCajeroId,
               m.cajero.cajeroId       AS cajeroId,
               m.cajero.nombre         AS cajeroNombre,
               m.tipoMovimiento       AS tipoMovimiento,
               m.numeroComprobante    AS numeroComprobante,
               m.referenciaDocumentoId AS referenciaDocumentoId,
               m.monto                AS monto,
               m.costo                AS costo,
               m.montoEfectivo        AS montoEfectivo,
               m.montoElectronico     AS montoElectronico,
               m.consecutivo          AS consecutivo,
               m.consecutivoTipo      AS consecutivoTipo,
               m.descripcion          AS descripcion,
               m.fechaMovimiento      AS fechaMovimiento
           FROM MovimientoCajero m
           WHERE m.detalleCajero.detalleCajeroId = :detalleCajeroId
           ORDER BY m.consecutivo ASC
           """)
    Page<MovimientoCajeroDTO> listarPorSesion(@Param("detalleCajeroId") Long detalleCajeroId, Pageable pageable);

    @Query("""
           SELECT 
               m.movimientoCajeroId   AS movimientoCajeroId,
               m.detalleCajero.detalleCajeroId AS detalleCajeroId,
               m.cajero.cajeroId       AS cajeroId,
               m.cajero.nombre         AS cajeroNombre,
               m.tipoMovimiento       AS tipoMovimiento,
               m.numeroComprobante    AS numeroComprobante,
               m.referenciaDocumentoId AS referenciaDocumentoId,
               m.monto                AS monto,
               m.costo                AS costo,
               m.montoEfectivo        AS montoEfectivo,
               m.montoElectronico     AS montoElectronico,
               m.consecutivo          AS consecutivo,
               m.consecutivoTipo      AS consecutivoTipo,
               m.descripcion          AS descripcion,
               m.fechaMovimiento      AS fechaMovimiento
           FROM MovimientoCajero m
           WHERE m.cajero.cajeroId = :cajeroId
           ORDER BY m.fechaMovimiento DESC
           """)
    Page<MovimientoCajeroDTO> listarPorCajero(@Param("cajeroId") Integer cajeroId, Pageable pageable);

    /** Cuenta movimientos de un tipo específico en una sesión para calcular el consecutivo por tipo */
    long countByDetalleCajero_DetalleCajeroIdAndTipoMovimiento(
            Long detalleCajeroId, MovimientoCajero.TipoMovimiento tipoMovimiento);

    /** Cuenta movimientos globales en una sesión */
    long countByDetalleCajero_DetalleCajeroId(Long detalleCajeroId);

    /** Todos los movimientos de una sesión (para cuadre de caja) */
    List<MovimientoCajero> findByDetalleCajero_DetalleCajeroIdOrderByConsecutivoAsc(Long detalleCajeroId);

    /** Movimientos filtrados por tipo dentro de una sesión */
    List<MovimientoCajero> findByDetalleCajero_DetalleCajeroIdAndTipoMovimientoOrderByConsecutivoTipoAsc(
            Long detalleCajeroId, MovimientoCajero.TipoMovimiento tipoMovimiento);
}
