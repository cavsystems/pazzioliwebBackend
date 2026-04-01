package com.pazzioliweb.productosmodule.repositori;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.productosmodule.dtos.LineaProductosDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoInventarioDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteConDetallesDTO;
import com.pazzioliweb.productosmodule.dtos.TotalInventarioDTO;
import com.pazzioliweb.productosmodule.entity.Productos;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;

public interface ProductoVarianteRepository extends JpaRepository<ProductoVariante, Long> {
	
	boolean existsByCodigoBarrasAndProductoVarianteIdNot(String codigoBarras, Long varianteId);
	
	boolean existsBySku(String sku);
	boolean existsByCodigoBarras(String codigoBarras);

	Optional<ProductoVariante> findBySku(String sku);

	Optional<ProductoVariante> findByProductoAndPredeterminada(Productos producto, Boolean predeterminada);
	
	@EntityGraph(attributePaths = { "producto" })
	@Query("SELECT p FROM ProductoVariante p")
	Page<ProductoVariante> traerProductosVariantes(Pageable pageable);
	
	@Query("""
			SELECT new com.pazzioliweb.productosmodule.dtos.TotalInventarioDTO(
		        sum(p.costo * x.existencia) AS total,
		        sum(x.existencia) as cantidadTotal,
		        'GLOBAL' AS bodega
		    )
		    FROM ProductoVariante pv
		    JOIN pv.producto p
		    JOIN pv.existencias x
		    JOIN x.bodega b
			""")
	Optional<TotalInventarioDTO> getTotalInventarioProductosGlobal();
	
	@Query("""
			SELECT new com.pazzioliweb.productosmodule.dtos.TotalInventarioDTO(
		        sum(p.costo * x.existencia) AS total,
		        sum(x.existencia) as cantidadTotal,
		        b.nombre AS bodega
		    )
		    FROM ProductoVariante pv
		    JOIN pv.producto p
		    JOIN pv.existencias x
			JOIN x.bodega b
			WHERE b.codigo = :bodegaId 
			""")
		Optional<TotalInventarioDTO> getTotalInventarioProductosXBodega(@Param("bodegaId")Integer bodedaId);
	
	@Query("""
		    SELECT 
		        l.descripcion AS descripcion,
		        SUM(p.costo * e.existencia) AS totalLinea,
		        SUM(e.existencia) AS cantidadLinea,
		        'GLOBAL' AS bodega
		    FROM ProductoVariante pv
		    JOIN pv.producto p
		    JOIN p.linea l
		    JOIN pv.existencias e
		    GROUP BY l.descripcion
		""")
		Page<LineaProductosDTO> getTotalesPorLineaGlobal(Pageable pageable);
	
	@Query("""
		    SELECT 
		        l.descripcion AS descripcion,
		        SUM(p.costo * e.existencia) AS totalLinea,
		        SUM(e.existencia) AS cantidadLinea,
		        b.nombre AS bodega
		    FROM ProductoVariante pv
		    JOIN pv.producto p
		    JOIN p.linea l
		    JOIN pv.existencias e
		    JOIN e.bodega b
		    WHERE b.codigo = :bodegaId
		    GROUP BY l.descripcion, b.nombre
		""")
		Page<LineaProductosDTO> getTotalesPorLineaXBodega(@Param("bodegaId") Integer bodegaId, Pageable pageable);
	
	@Query("""
		    SELECT 
		        l.descripcion AS descripcion,
		        SUM(p.costo * e.existencia) AS totalLinea,
		        SUM(e.existencia) AS cantidadLinea,
		        b.nombre AS bodega
		    FROM ProductoVariante pv
		    JOIN pv.producto p
		    JOIN p.linea l
		    JOIN pv.existencias e
		    JOIN e.bodega b
		    GROUP BY l.descripcion, b.nombre
		""")
		Page<LineaProductosDTO> getTotalesPorLineaXBodegas(Pageable pageable);
	
	@Query("""
		    SELECT 
		        l.descripcion AS descripcion,
		        SUM(p.costo * e.existencia) AS totalLinea,
		        SUM(e.existencia) AS cantidadLinea,
		        b.nombre AS bodega
		    FROM ProductoVariante pv
		    JOIN pv.producto p
		    JOIN p.linea l
		    JOIN pv.existencias e
		    JOIN e.bodega b
		    GROUP BY l.descripcion, b.nombre
		""")
		List<LineaProductosDTO> getTotalesPorLineaXBodegastotal();
	
	@EntityGraph(attributePaths = {"detalles", "detalles.caracteristica", "detalles.caracteristica.tipo"})
	Page<ProductoVariante> findByProductoProductoId(Integer productoId, Pageable pageable);

	@EntityGraph(attributePaths = {"detalles", "detalles.caracteristica", "detalles.caracteristica.tipo"})
	Page<ProductoVariante> findByCodigoBarrasIn(List<String> codigos, Pageable pageable);


	@Query(
			value = """
		  		SELECT *
FROM (
		        SELECT
		  		     pv.producto_variantes_id as productoVarianteId,
		  		       pv.activo as activo,
		  		    p.producto_id AS productoId,
		  		    p.referencia,
		  		    P.estado AS estado,
		  		     p.grupo_id As grupoid,
		  		     p.linea_id As lineaid,
		  		     p.maneja_variantes As manejavariante,
		  		     p.tipo_producto_id As tipoproductid,
		  		     p.impuesto_id As impuestoid,
		  		     pi.tarifa AS tarifa,
		            pv.producto_variantes_id AS varianteId,
		            p.codigo_contable AS codigoContable,
		            P.codigo_barras As codigobarras,
		            IF(pv.predeterminada=0,CONCAT(p.descripcion, ' - ', pv.referencia_variantes),p.descripcion) AS descripcion,
		            COALESCE(ex.totalExistencia, 0) AS cantidadGlobal,
		            SUM(COALESCE(ex.totalExistencia*p.costo, 0)) OVER () AS totalGlobalInventario,
		            p.costo AS costo,
		            u.sigla AS unidadMedida,
		            l.descripcion AS linea,
		            g.descripcion AS grupo,
		            p.fecha_ultima_compra AS fechaUltimaCompra,
		            p.fecha_ultima_venta AS fechaUltimaVenta
		        FROM producto_variantes pv
		        JOIN productos p ON p.producto_id = pv.producto_id
		        JOIN impuestos pi ON pi.codigo=p.impuesto_id
		        LEFT JOIN unidades_medida_producto ump 
				       ON ump.producto_id = p.producto_id
				LEFT JOIN unidades_medida u 
				       ON u.unidad_medida_id = ump.unidad_medida_id
		        LEFT JOIN lineas l ON l.linea_id = p.linea_id
		        LEFT JOIN grupos g ON g.grupo_id = p.grupo_id
		        LEFT JOIN (
		            SELECT 
		                e.producto_variantes_id AS varianteId,
		                SUM(e.existencia) AS totalExistencia
		            FROM existencias e
		            GROUP BY e.producto_variantes_id
		        ) ex ON ex.varianteId = pv.producto_variantes_id) t 
		        WHERE LOWER(t.descripcion) LIKE LOWER(CONCAT('%', :productodes, '%')) AND
 t.activo=:activo 
		        """,
			countQuery = "SELECT COUNT(*) FROM producto_variantes",
			nativeQuery = true
	)
	Page<ProductoInventarioDTO> listarInventario( @Param("activo") int activo,@Param("productodes") String desproduct,Pageable pageable);





	@Query(
			  value = """
			  	SELECT *
FROM (
    SELECT
      
        pv.producto_variantes_id AS varianteId,
        pv.activo as activo,
        pv.codigo_barras as codigobarravariante,
        p.codigo_barras as codigobarra,
        p.producto_id AS productoId,
        ex.bodegaid as bodegaid,
        p.referencia,
       
        p.estado,
        p.grupo_id AS grupoid,
        p.linea_id AS lineaid,
        p.maneja_variantes AS manejavariante,
        p.tipo_producto_id AS tipoproductid,
        p.impuesto_id AS impuestoid,
        p.codigo_contable AS codigoContable,
        IF(
          pv.predeterminada = 0,
          CONCAT(p.descripcion, '-', pv.referencia_variantes),
          p.descripcion
        ) AS descripcion,
        COALESCE(ex.totalExistencia, 0) AS cantidadGlobal,
        p.costo,
        u.sigla AS unidadMedida,
        l.descripcion AS linea,
        g.descripcion AS grupo,
        p.fecha_ultima_compra,
        p.fecha_ultima_venta
    FROM producto_variantes pv
    JOIN productos p ON p.producto_id = pv.producto_id
    LEFT JOIN unidades_medida_producto ump ON ump.producto_id = p.producto_id
    LEFT JOIN unidades_medida u ON u.unidad_medida_id = ump.unidad_medida_id
    LEFT JOIN lineas l ON l.linea_id = p.linea_id
    LEFT JOIN grupos g ON g.grupo_id = p.grupo_id
    LEFT JOIN (
        SELECT
             
            e.producto_variantes_id AS varianteId,
            e.bodega_id as bodegaid,
            e.existencia AS totalExistencia
        FROM existencias e
       
    ) ex ON ex.varianteId = pv.producto_variantes_id
) t
WHERE LOWER(t.descripcion)LIKE LOWER(CONCAT('%', :productodes, '%')) or LOWER(t.codigobarra)LIKE LOWER(CONCAT('%', :productodes, '%')) or  LOWER(t.codigoContable)LIKE LOWER(CONCAT('%', :productodes, '%'))  or  LOWER(t.codigobarravariante)LIKE LOWER(CONCAT('%', :productodes, '%')) or  LOWER(t.referencia)LIKE LOWER(CONCAT('%', :productodes, '%'))  
and  t.estado="ACTIVO"
and t.activo=:activo
and t.bodegaid=:bodega
			        """,
			  countQuery = "SELECT COUNT(*) FROM producto_variantes",
			  nativeQuery = true
			)
			Page<ProductoInventarioDTO> listarInventarioentradasalida( @Param("activo") int activo, @Param("bodega") int bodega,@Param("productodes") String desproduct,Pageable pageable);

	@Query(
			value = """
		  		SELECT *
			FROM (
		        SELECT
		  		     pv.producto_variantes_id as productoVarianteId,
		  		       pv.activo as activo,
		  		    p.producto_id AS productoId,
		  		    p.referencia,
		  		    P.estado AS estado,
		  		     p.grupo_id As grupoid,
		  		     p.linea_id As lineaid,
		  		     p.maneja_variantes As manejavariante,
		  		     p.tipo_producto_id As tipoproductid,
		  		     p.impuesto_id As impuestoid,
		  		     pi.tarifa AS tarifa,
		            pv.producto_variantes_id AS varianteId,
		            p.codigo_contable AS codigoContable,
		            P.codigo_barras As codigobarras,
		              p.descripcion AS descripcion,
		            COALESCE(ex.totalExistencia, 0) AS cantidadGlobal,
		            SUM(COALESCE(ex.totalExistencia*p.costo, 0)) OVER () AS totalGlobalInventario,
		            p.costo AS costo,
		            u.sigla AS unidadMedida,
		            l.descripcion AS linea,
		            g.descripcion AS grupo,
		            p.fecha_ultima_compra AS fechaUltimaCompra,
		            p.fecha_ultima_venta AS fechaUltimaVenta
		        FROM producto_variantes pv
		        JOIN productos p ON p.producto_id = pv.producto_id
		        JOIN impuestos pi ON pi.codigo=p.impuesto_id
		        LEFT JOIN unidades_medida_producto ump 
				       ON ump.producto_id = p.producto_id
				LEFT JOIN unidades_medida u 
				       ON u.unidad_medida_id = ump.unidad_medida_id
		        LEFT JOIN lineas l ON l.linea_id = p.linea_id
		        LEFT JOIN grupos g ON g.grupo_id = p.grupo_id
		        LEFT JOIN (
		            SELECT 
		                e.producto_variantes_id AS varianteId,
		                SUM(e.existencia) AS totalExistencia
		            FROM existencias e
		            GROUP BY e.producto_variantes_id
		        ) ex ON ex.varianteId = pv.producto_variantes_id) t 
		        WHERE TRIM(t.descripcion) IN (:descripciones) AND
			 	t.activo=:activo 
		        """,
			nativeQuery = true
	)
	List<ProductoInventarioDTO> listarInventarioPorDescripciones(@Param("descripciones") List<String> descripciones, @Param("activo") int activo);

	Optional<ProductoVariante> findByProducto_CodigoContableAndReferenciaVariantes(String codigoContable, String referenciaVariantes);

	Optional<ProductoVariante> findByProductoVarianteId(Long id);
	Optional<ProductoVariante> findByCodigoBarras(String codigobarras);
	
	void deleteByProductoVarianteId(Long id);
	
}
