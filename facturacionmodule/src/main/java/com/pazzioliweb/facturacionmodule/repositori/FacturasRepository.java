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
