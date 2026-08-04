package com.pazzioliweb.usuariosbacken.repositorio;

import com.pazzioliweb.usuariosbacken.entity.SubPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubPermisoRepository extends JpaRepository<SubPermiso, Integer> {

    Optional<SubPermiso> findByCodigo(int codigo);

    @Query("""
        SELECT sp FROM SubPermiso sp JOIN FETCH sp.permisoPadre pp
        WHERE sp.activo = true
        ORDER BY pp.nombre, sp.nombre
        """)
    List<SubPermiso> findActivosConPadre();
}
