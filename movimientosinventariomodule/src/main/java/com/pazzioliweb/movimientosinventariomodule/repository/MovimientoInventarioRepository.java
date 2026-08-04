package com.pazzioliweb.movimientosinventariomodule.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventario;

import org.springframework.data.repository.query.Param;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long>{
	Optional<MovimientoInventario> findTopByComprobanteOrderByConsecutivoDesc(ComprobanteContable comprobante);

	/** Idempotencia: verifica si ya existe un movimiento generado desde un documento. */
	Optional<MovimientoInventario> findByDocumentoOrigenTipoAndDocumentoOrigenId(String tipo, Long id);
	
	/**
	 * Compara el enum tipo con el string del parámetro usando .name() para
	 * que sea compatible con cualquier modo de Hibernate. La comparación
	 * directa enum = String falla silenciosamente y devuelve cero filas.
	 *
	 * LEFT JOIN FETCH de comprobante/usuario: son @ManyToOne EAGER por defecto
	 * (JPA) y sin fetch-join Hibernate resolvía cada uno con una SELECT extra
	 * POR FILA de la página al armar el DTO (mapper.toResponse los toca a
	 * todos). Al ser asociaciones a-uno (no colección), el fetch-join es seguro
	 * junto con Pageable: no multiplica filas ni rompe la paginación.
	 */
	@Query("""
		    SELECT m FROM MovimientoInventario m
		    LEFT JOIN FETCH m.comprobante
		    LEFT JOIN FETCH m.usuario
		    WHERE (:tipo IS NULL OR :tipo = '' OR CAST(m.tipo AS string) = :tipo)
		      AND (:desde IS NULL OR m.fechaEmision >= :desde)
		      AND (:hasta IS NULL OR m.fechaEmision <= :hasta)
		   AND m.documentoOrigenTipo IS NULL
		      
		""")
		Page<MovimientoInventario> findByFiltros(
		        @Param("tipo") String tipo,
		        @Param("desde") LocalDate desde,
		        @Param("hasta") LocalDate hasta,
		        Pageable pageable
		);
}
