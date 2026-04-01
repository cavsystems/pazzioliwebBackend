package com.pazzioliweb.vendedoresmodule.repositori;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.vendedoresmodule.dtos.VendedorDTO;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;

public interface VendedoresRepository extends JpaRepository<Vendedores, Integer>{
	@Query("""
	           SELECT 
	               v.vendedor_id        AS vendedor_id,
	               v.nombre            AS nombre,
	               v.direccion         AS direccion,
	               v.telefono          AS telefono,
	               v.estado            AS estado,
	               v.codigo_usuario_creo AS codigo_usuario_creo,
	               v.fechacreado       AS fechacreado
	           FROM Vendedores v
	           """)
	    Page<VendedorDTO> listarVendedoresDTO(Pageable pageable);
}
