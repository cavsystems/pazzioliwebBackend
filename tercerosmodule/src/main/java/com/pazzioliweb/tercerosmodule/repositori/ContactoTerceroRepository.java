package com.pazzioliweb.tercerosmodule.repositori;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.tercerosmodule.dtos.ContactoTerceroDTO;
import com.pazzioliweb.tercerosmodule.dtos.TipoContactoInfoImpl;
import com.pazzioliweb.tercerosmodule.entity.ContactoTercero;

import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface ContactoTerceroRepository extends JpaRepository<ContactoTercero, Integer> {
	@Query("SELECT c FROM ContactoTercero c WHERE c.tercero.terceroId = :terceroId")
    List<ContactoTerceroDTO> findByTerceroId(@Param("terceroId") Integer terceroId);
	
	@Query(value=""" 
			SELECT tipo_contacto_id as tipoContactoId ,nombre as nombre ,descripcion as descripcion, activo as activo FROM tipo_contacto 
			""",nativeQuery = true)
	List<ContactoTerceroDTO.TipoContactoInfo> findByIdcontacto();
	
	 @Transactional
	    @Modifying
	    void deleteByContactoId(int codigo);
	
}
