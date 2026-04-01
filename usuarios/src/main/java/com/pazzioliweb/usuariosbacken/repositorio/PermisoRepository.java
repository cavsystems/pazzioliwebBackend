package com.pazzioliweb.usuariosbacken.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.usuariosbacken.dtos.PermisosDTOS;
import com.pazzioliweb.usuariosbacken.entity.Permiso;

import java.util.List;
import java.util.Optional;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    List< PermisosDTOS> findAllPermisoBy();
    Optional<Permiso> findByCodigo(int codigo);

}