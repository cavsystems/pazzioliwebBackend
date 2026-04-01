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
import com.pazzioliweb.productosmodule.entity.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Integer>{
	
	boolean existsByCodigoContableAndProductoIdNot(String codigoContable, Integer productoId);
	boolean existsByCodigoBarrasAndProductoIdNot(String codigoBarras, Integer productoId);
	
	boolean existsByCodigoContable(String codigoContable);
	boolean existsByCodigoBarras(String codigoBarras);
	
	Optional<Productos> findByCodigoContable(String codigo);
	
	@EntityGraph(attributePaths = {
		    "grupo",
		    "linea",
		    "impuestos",
		    "usuario",
		    "tipoProducto"
		})
		@Query("SELECT p FROM Productos p WHERE p.productoId = :id")
		Optional<Productos> findByIdWithRelations(@Param("id") Integer id);
	
	@EntityGraph(attributePaths = {
		    "impuestos",
		    "linea",
		    "grupo",
		    "usuario",
		    "tipoProducto"
		})
		@Query("SELECT p FROM Productos p")
		Page<Productos> traerProductos(Pageable pageable);
	
	@EntityGraph(attributePaths = {
		    "impuestos",
		    "linea",
		    "grupo",
		    "usuario",
		    "tipoProducto"
		})
		@Query("""
		    SELECT p FROM Productos p
		    WHERE p.descripcion LIKE %:busqueda%
		       OR p.codigoContable LIKE %:busqueda%
		       OR p.codigoBarras LIKE %:busqueda%
		       OR p.referencia LIKE %:busqueda%
		""")
		Page<Productos> traerProductosXFiltro(@Param("busqueda") String busqueda, Pageable pageable);
	
	@Query("""
		    SELECT 
		        l.descripcion AS descripcion,
		        SUM(p.costo * e.existencia) AS totalLinea,
		        SUM(e.existencia) AS cantidadLinea,
		        'Global' AS bodega
		    FROM Existencias e
		    JOIN e.productoVariante pv
		    JOIN pv.producto p
		    JOIN p.linea l
		    JOIN e.bodega b
		    GROUP BY l.descripcion
		""")
		Page<LineaProductosDTO> getTotalesPorLineaTodasBodegas(Pageable pageable);
	
	@Query("""
		    SELECT 
		        l.descripcion AS descripcion,
		        SUM(p.costo * e.existencia) AS totalLinea,
		        SUM(e.existencia) AS cantidadLinea,
		        b.nombre AS bodega
		    FROM Existencias e
		    JOIN e.productoVariante pv
		    JOIN pv.producto p
		    JOIN p.linea l
		    JOIN e.bodega b
		    WHERE b.codigo = :bodegaId 
		    GROUP BY l.descripcion, b.nombre
		""")
		Page<LineaProductosDTO> getTotalesPorLineaPorBodegaId(@Param("bodegaId") Integer bodegaId, Pageable pageable);
	
	boolean existsByLinea_Id(Integer lineaId);
	
	boolean existsByGrupo_Id(Integer grupoaId);
	
	boolean existsByUnidadesMedidaProducto_UnidadMedida_UnidadMedidaId(Integer unidadMedidaId);

}
