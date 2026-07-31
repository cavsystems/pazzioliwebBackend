package com.pazzioliweb.facturacionmodule.repositori;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.facturacionmodule.dtos.FacturaResumenDTO;
import com.pazzioliweb.facturacionmodule.entity.Facturas;



public interface FacturasRepository extends JpaRepository<Facturas, Integer>{

	// ── Facturación Electrónica ──
	Optional<Facturas> findByVentaId(Long ventaId);

	@Query("SELECT MAX(f.consecutivo) FROM Facturas f WHERE f.comprobanteId = :comprobanteId")
	Optional<Integer> findMaxConsecutivoByComprobanteId(@Param("comprobanteId") Integer comprobanteId);

	/**
	 * Máximo folio usado por PREFIJO. La serie del folio (ENC_6) pertenece a la
	 * resolución/prefijo, no al comprobante: contado (FC) y crédito (VC) comparten
	 * prefijo y resolución, así que numerar por comprobante duplicaría folios.
	 */
	@Query("SELECT MAX(f.consecutivo) FROM Facturas f WHERE f.prefijo = :prefijo")
	Optional<Integer> findMaxConsecutivoByPrefijo(@Param("prefijo") String prefijo);

	/**
	 * Ventas COMPLETADAS de los últimos N días SIN factura electrónica: el hueco
	 * que queda cuando el listener de facturación falla (venta sin cliente, folios
	 * agotados, caída del backend). Antes solo se veía venta a venta con el 404 de
	 * /por-venta; con esto el operador tiene la lista para regenerar.
	 */
	@Query(value = """
	        SELECT v.id, v.numero_venta, v.fecha_emision, v.total_venta
	        FROM ventas v
	        WHERE v.estado = 'COMPLETADA'
	          AND v.fecha_emision >= :desde
	          AND NOT EXISTS (SELECT 1 FROM facturas f WHERE f.venta_id = v.id)
	        ORDER BY v.id DESC
	        """, nativeQuery = true)
	List<Object[]> findVentasCompletadasSinFactura(@Param("desde") java.time.LocalDate desde);

	// ── Listados existentes ──
	@Query("""
	        SELECT 
	            f.facturaId AS facturaId,
	            f.consecutivo AS consecutivo,
	            f.comprobanteId AS comprobanteId,
	            f.fechaCreacion AS fechaCreacion,
	            f.fechaEmision AS fechaEmision,
	            f.terceroId AS terceroId,
	            f.totalFactura AS totalFactura,
	            f.estado AS estado
	        FROM Facturas f
	        """)
	    Page<FacturaResumenDTO> listadoFacturasResumenDTO(Pageable pageable);
	
	@Query("""
	        SELECT 
	            f.facturaId AS facturaId,
	            f.consecutivo AS consecutivo,
	            f.comprobanteId AS comprobanteId,
	            f.fechaCreacion AS fechaCreacion,
	            f.fechaEmision AS fechaEmision,
	            f.terceroId AS terceroId,
	            f.totalFactura AS totalFactura,
	            f.estado AS estado
	        FROM Facturas f
	        WHERE f.fechaCreacion BETWEEN :fechaInicio AND :fechaFin
	        """)
	Page<FacturaResumenDTO> listadoFacturasResumenPorFecha(
	        @Param("fechaInicio") LocalDateTime fechaInicio,
	        @Param("fechaFin") LocalDateTime fechaFin,
	        Pageable pageable
	);
	
	@Query("""
	        SELECT 
	            f.facturaId AS facturaId,
	            f.consecutivo AS consecutivo,
	            f.comprobanteId AS comprobanteId,
	            f.fechaCreacion AS fechaCreacion,
	            f.fechaEmision AS fechaEmision,
	            f.terceroId AS terceroId,
	            f.totalFactura AS totalFactura,
	            f.estado AS estado
	        FROM Facturas f
	        WHERE f.fechaCreacion BETWEEN :fechaInicio AND :fechaFin
	    """)
	    List<FacturaResumenDTO> listadoFacturasResumenPorFechaTodas(
	            @Param("fechaInicio") LocalDateTime fechaInicio,
	            @Param("fechaFin") LocalDateTime fechaFin
	    );
	
	@EntityGraph(attributePaths = {"metodosPago", "tipoTotales"})
	@Query("""
	    SELECT f
	    FROM Facturas f
	    WHERE f.fechaCreacion BETWEEN :fechaInicio AND :fechaFin
	""")
	List<Facturas> listadoFacturasConDetalles(
	        @Param("fechaInicio") LocalDateTime fechaInicio,
	        @Param("fechaFin") LocalDateTime fechaFin
	);
}
