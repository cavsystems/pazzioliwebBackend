
package com.pazzioliweb.usuariosbacken.repositorio;

import com.pazzioliweb.usuariosbacken.entity.SubPermisoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubPermisoRolRepository extends JpaRepository<SubPermisoRol, Integer> {

    @Query("""
        SELECT spr FROM SubPermisoRol spr
        JOIN FETCH spr.codigosubpermiso sp
        JOIN FETCH sp.permisoPadre pp
        WHERE spr.codigorol.codigo = :codigoRol
          AND spr.estado = 'ACTIVO'
          AND sp.activo = true
        """)

        
    List<SubPermisoRol> findSubPermisosActivosByRol(@Param("codigoRol") int codigoRol);

    @Transactional
    @Modifying
    void deleteByCodigorol_CodigoAndCodigosubpermiso_Codigo(int codigoRol, int codigoSubpermiso);
}
