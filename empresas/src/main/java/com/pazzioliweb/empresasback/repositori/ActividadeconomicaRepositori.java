package com.pazzioliweb.empresasback.repositori;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.empresasback.entity.Actividadeconomica;
import com.pazzioliweb.empresasback.entity.Empresas;

public interface ActividadeconomicaRepositori  extends JpaRepository<Actividadeconomica, Long>  {
	 @Query("SELECT a FROM Actividadeconomica a")
	    List<Actividadeconomica> findWithLimit(Pageable pageable);
	 List<Actividadeconomica> findByDescripcionActividadContainingOrCodigo(
			    String descripcion,
			    int codigo,
			    Pageable pageable
			);
	 
	Optional<Actividadeconomica> findByDescripcionActividad(String nombre);
	
	
}
