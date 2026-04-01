package com.pazzioliweb.productosmodule.repositori;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.usuariosbacken.dtos.UsuariobodegasidDTOS;

@Repository
public interface BodegasRepository extends JpaRepository<Bodegas, Integer>{
  Optional<Bodegas> findByCodigo(int codigo);
  Optional<Bodegas> findByNombre(String nombre);
  
  
	@Query(value = """
			 select   e.id_usuariobodega as codigo
            
			from usuariobodega e
			join usuarios c on c.codigo = e.usuarioid
		
			join bodegas b on b.codigo = e.bodegaid
		
			where c.codigo = :usuarioid and  b.codigo=:bodegaid
			""", nativeQuery = true)
	
	List<UsuariobodegasidDTOS> findByUsuariobodega(@Param("usuarioid") int usuarioid,@Param("bodegaid") int bodegaid);
	
	
	
	@Query(value = """
			 select   e.id_usuariobodega as codigo
           
			from usuariobodega e
			join usuarios c on c.codigo = e.usuarioid
		
			join bodegas b on b.codigo = e.bodegaid
		
			where c.codigo = :usuarioid
			""", nativeQuery = true)
	
	List<UsuariobodegasidDTOS> findByUsuariobodegausuario(@Param("usuarioid") int usuarioid);
	
	

}
