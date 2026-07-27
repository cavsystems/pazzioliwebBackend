package com.pazzioliweb.productosmodule.repositori;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.productosmodule.dtos.ExistenciasBodegaDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasResponseDTO;
import com.pazzioliweb.productosmodule.entity.Existencias;

public interface ExistenciasRepository extends JpaRepository<Existencias, Integer> {
	Page<Existencias> findByProductoVariante_ProductoVarianteId(Long varianteId, Pageable pageable);

	Optional<Existencias> findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(Long varianteId, Integer bodegaId);

	/**
	 * Upsert atómico del saldo (sincronización kardex → existencias). El patrón
	 * SELECT-luego-INSERT fallaba con "Duplicate entry ... uq_existencias_producto_bodega"
	 * cuando otra transacción creaba la fila (variante, bodega) después del snapshot
	 * REPEATABLE READ de esta transacción: el SELECT no la veía pero el INSERT sí chocaba
	 * con el UNIQUE, y ese error marcaba la transacción del documento como rollback-only.
	 */
	@Modifying
	@Query(value = """
			INSERT INTO existencias (producto_variantes_id, bodega_id, existencia)
			VALUES (:varianteId, :bodegaId, :saldo)
			ON DUPLICATE KEY UPDATE existencia = VALUES(existencia)
			""", nativeQuery = true)
	void upsertSaldo(@Param("varianteId") Long varianteId,
	                 @Param("bodegaId") Integer bodegaId,
	                 @Param("saldo") java.math.BigDecimal saldo);

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
