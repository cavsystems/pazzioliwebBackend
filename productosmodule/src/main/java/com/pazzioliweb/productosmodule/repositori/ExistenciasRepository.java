package com.pazzioliweb.productosmodule.repositori;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.productosmodule.dtos.ExistenciasBodegaDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasResponseDTO;
import com.pazzioliweb.productosmodule.entity.Existencias;

public interface ExistenciasRepository extends JpaRepository<Existencias, Integer> {
	Page<Existencias> findByProductoVariante_ProductoVarianteId(Long varianteId, Pageable pageable);

	Optional<Existencias> findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(Long varianteId, Integer bodegaId);

	Page<Existencias> findByBodega_Codigo(Integer bodegaId, Pageable pageable);

	@Query(
			value = """
  		        SELECT
  		  			e.existencia_id as existenciaId,
  		  		    e.producto_variantes_id as productoVarianteId,
  		  		    e.bodega_id as bodegaId,
  		  		    e.existencia,
  		  		    e.stock_min as stockMin,
  		  		    e.stock_max as stockMax,
  		  		    e.ubicacion,
  		  		    e.fecha_ultimo_movimiento as fechaUltimoMovimiento,
  		  		    b.nombre as bodega
  		        FROM existencias e
  		        JOIN bodegas b ON b.codigo = e.bodega_id
  		        WHERE e.producto_variantes_id = :varianteId
  		        """,
			countQuery = "SELECT COUNT(*) FROM existencias",
			nativeQuery = true
	)
	Page<ExistenciasBodegaDTO> listadoExistenciasNombreBodegaVariante(@Param("varianteId") Long varianteId, Pageable pageable);



	@Query(
			value = """
		        SELECT
		  			e.existencia_id as existenciaId,
		  		    e.producto_variantes_id as productoVarianteId,
		  		    e.bodega_id as bodegaId,
		  		    e.existencia,
		  		    e.stock_min as stockMin,
		  		    e.stock_max as stockMax,
		  		    e.ubicacion,
		  		    e.fecha_ultimo_movimiento as fechaUltimoMovimiento,
		  		    b.nombre as bodega
		        FROM existencias e
		        JOIN bodegas b ON b.codigo = e.bodega_id
		        WHERE e.producto_variantes_id = :varianteId
		        """,
			countQuery = "SELECT COUNT(*) FROM existencias",
			nativeQuery = true
	)
	List<ExistenciasBodegaDTO> listadoExistenciasNombreBodegaVariante(@Param("varianteId")  Long varianteId);
}
