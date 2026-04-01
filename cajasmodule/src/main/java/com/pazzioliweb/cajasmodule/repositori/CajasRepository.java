package com.pazzioliweb.cajasmodule.repositori;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.cajasmodule.dtos.CajaDTO;
import com.pazzioliweb.cajasmodule.entity.Cajas;

public interface CajasRepository extends JpaRepository<Cajas, Integer>{
	@Query("""
	        SELECT 
	            c.caja_id          AS caja_id,
	            usuario_id         AS usuario_id,
	            c.monto_inicial    AS monto_inicial,
	            c.monto_final      AS monto_final,
	            c.fecha_apertura   AS fecha_apertura,
	            c.fecha_cierre     AS fecha_cierre,
	            comp               AS comprobante,
	            c.consecutivo      AS consecutivo,
	            c.total_recaudo    AS total_recaudo,
	            c.estado           AS estado
	        FROM Cajas c
	        JOIN c.comprobante comp
	        """)
	    Page<CajaDTO> listarCajasDTO(Pageable pageable);
}
