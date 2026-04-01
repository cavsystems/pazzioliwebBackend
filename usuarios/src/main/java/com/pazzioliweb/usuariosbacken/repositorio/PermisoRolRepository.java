package com.pazzioliweb.usuariosbacken.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.usuariosbacken.dtos.PermisosrolesDTOS;
import com.pazzioliweb.usuariosbacken.entity.Permiso;
import com.pazzioliweb.usuariosbacken.entity.PermisoRol;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;
import java.util.Optional;

public interface PermisoRolRepository extends JpaRepository<PermisoRol, Integer> {
	@Query("SELECT p FROM PermisoRol p JOIN p.codigopermiso  cp join p.codigorol cr WHERE cr.codigo = :codigo AND p.estado='ACTIVO'")
    List<PermisoRol> findPermisosActivosByRol(@Param("codigo") int codigo);
	  @Transactional
	    @Modifying
    void deleteByCodigorol_CodigoAndCodigopermiso_Codigo(int codigorol,int codigopermiso);
    //Optional<PermisoRol> findByCodigoAndEstado(int codigoRol, String estado);

}