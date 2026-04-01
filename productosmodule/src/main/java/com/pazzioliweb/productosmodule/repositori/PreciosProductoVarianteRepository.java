package com.pazzioliweb.productosmodule.repositori;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteDTO;
import com.pazzioliweb.productosmodule.entity.PreciosProductoVariante;

public interface PreciosProductoVarianteRepository extends JpaRepository<PreciosProductoVariante, Long> {
	boolean existsByProductoVariante_ProductoVarianteIdAndPrecio_PrecioId(
	        Long productoVarianteId,
	        Integer precioId
	);
	
	boolean existsByPrecio_PrecioId(
	        
	        Integer precioId
	);

	Optional<PreciosProductoVariante> findByProductoVariante_ProductoVarianteIdAndPrecio_PrecioId(
	        Long productoVarianteId,
	        Integer precioId
	);
	
	@Query(
	  		  value = """
	  		        SELECT
	  		  			pp.valor,
	  		  			pp.producto_variantes_id as productoVarianteId,
	  		  		    pp.precios_producto_id as preciosProductoId,
	  		  		    p.precio_id as precioId,
	  		  		    p.descripcion as precio,
	  		  		    pp.fecha_creacion as fechaCreacion,
	  		  		    pp.fecha_inicio as fechaInicio,
	  		  		    pp.fecha_fin as fechaFin
	  		        FROM precios_producto_variante pp
	  		        JOIN precios p ON p.precio_id = pp.precio_id
	  		        WHERE pp.producto_variantes_id = :varianteId
	  		        """,
	  		  countQuery = "SELECT COUNT(*) FROM precios_producto_variante",
	  		  nativeQuery = true
	  		)
	    Page<PreciosProductoVarianteDTO> preciosPrpductoVariante(@Param("varianteId")  Integer varianteId, Pageable pageable);

	@Query(
			value = """
					   SELECT
					pp.valor,
					pp.producto_variantes_id as productoVarianteId,
					   pp.precios_producto_id as preciosProductoId,
					   p.precio_id as precioId,
					   p.descripcion as precio,
					   pp.fecha_creacion as fechaCreacion,
					   pp.fecha_inicio as fechaInicio,
					   pp.fecha_fin as fechaFin
					   FROM precios_producto_variante pp
					   JOIN precios p ON p.precio_id = pp.precio_id
					   WHERE pp.producto_variantes_id IN :varianteIds
					""",
			countQuery = "SELECT COUNT(*) FROM precios_producto_variante WHERE producto_variantes_id IN :varianteIds",
			nativeQuery = true
	)
	Page<PreciosProductoVarianteDTO> preciosProductoVarianteMultiple(@Param("varianteIds") List<Integer> varianteIds, Pageable pageable);
}
