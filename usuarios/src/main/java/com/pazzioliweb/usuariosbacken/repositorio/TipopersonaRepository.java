package com.pazzioliweb.usuariosbacken.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.usuariosbacken.entity.Permiso;
import com.pazzioliweb.usuariosbacken.entity.Tipopersona;

public interface TipopersonaRepository extends JpaRepository<Tipopersona, Long>  {

	Optional<Tipopersona> findByCodigo(int codigo);
}
