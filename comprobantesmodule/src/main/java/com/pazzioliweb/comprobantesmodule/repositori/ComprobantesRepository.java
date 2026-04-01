package com.pazzioliweb.comprobantesmodule.repositori;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioli.comprobantesmodule.dtos.ComprobanteDTO;
import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;

public interface ComprobantesRepository extends JpaRepository<Comprobantes, Integer>{
	@Query("""
		    SELECT c.comprobante_id AS comprobante_id,
		           c.nombre AS nombre,
		           c.inicio_consecutivo AS inicio_consecutivo,
		           c.afecta_inventario AS afecta_inventario,
		           cat AS categoriaComprobante
		    FROM Comprobantes c
		    LEFT JOIN c.categoriaComprobante cat
		""")
		Page<ComprobanteDTO> listarComprobantes(Pageable pageable);
}
