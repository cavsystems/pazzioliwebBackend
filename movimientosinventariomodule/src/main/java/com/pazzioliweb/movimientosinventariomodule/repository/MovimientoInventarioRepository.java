package com.pazzioliweb.movimientosinventariomodule.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventario;

import io.lettuce.core.dynamic.annotation.Param;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long>{
	Optional<MovimientoInventario> findTopByComprobanteOrderByConsecutivoDesc(Comprobantes comprobante);
	
	@Query("""
		    SELECT m FROM MovimientoInventario m
		    WHERE (:tipo IS NULL OR m.tipo = :tipo)
		      AND (:desde IS NULL OR m.fechaEmision >= :desde)
		      AND (:hasta IS NULL OR m.fechaEmision <= :hasta)
		""")
		Page<MovimientoInventario> findByFiltros(
		        @Param("tipo") String tipo,
		        @Param("desde") LocalDate desde,
		        @Param("hasta") LocalDate hasta,
		        Pageable pageable
		);
}
