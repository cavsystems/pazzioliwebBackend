package com.pazzioliweb.productosmodule.repositori;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO;
import com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO_basico;
import com.pazzioliweb.productosmodule.dtos.idcaracteristicasDTOS;
import com.pazzioliweb.productosmodule.entity.Caracteristica;
import java.util.Optional;

public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Long>{
	
	Optional<Caracteristica> findByNombre(String nombre);
	
	@Query("""
		    SELECT NEW com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO_basico(
			    c.caracteristicaId,
			    c.nombre,
			    t.tipoCaracteristicaId,
			    t.nombre
			)
			FROM Caracteristica c
			JOIN c.tipo t
		""")
		Page<CaracteristicaDTO_basico> traerTodasCaracteristicas(
		    Pageable pageable
		);
		
	@Query("""
		    SELECT NEW com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO_basico(
			    c.caracteristicaId,
			    c.nombre,
			    t.tipoCaracteristicaId,
			    t.nombre
			)
			FROM Caracteristica c
			JOIN c.tipo t
			WHERE t.tipoCaracteristicaId = :tipoId
		""")
		Page<CaracteristicaDTO_basico> findByTipoIdDTO(
		    @Param("tipoId") Long tipoId,
		    Pageable pageable
		);
	
	Page<Caracteristica> findByTipoTipoCaracteristicaId(Long tipoId, Pageable pageable);
	
	@Query("""
		    SELECT new com.pazzioliweb.productosmodule.dtos.CaracteristicaDTO(
        c.caracteristicaId AS caracteristicaId,
        c.nombre AS nombre,
        
       t
    ) 
		    FROM Caracteristica c 
		    JOIN c.tipo t
		    WHERE t.nombre = :tipo
		      AND c.nombre LIKE CONCAT('%', :ca, '%')
		""")
	Page<CaracteristicaDTO> traerCaracteristicasDetalle(  @Param("ca") String ca,
	        @Param("tipo") String tipo,Pageable pageable);
	

    List<Caracteristica> findByNombreIn(List<String> valores);
    @Query("""
		    SELECT  c
		    FROM Caracteristica c 
		    JOIN c.tipo t
		    WHERE c.nombre = :nombre
		      AND t.tipoCaracteristicaId=:tipocara
		""")
    List<Caracteristica>  findBynombretipo(
    		@Param("nombre") String nombre,
    		 @Param("tipocara")    Long Tipo
    		);
    
	boolean existsByTipo_tipoCaracteristicaId(Long grupoaId);
}



