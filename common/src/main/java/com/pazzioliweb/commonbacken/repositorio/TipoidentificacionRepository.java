package com.pazzioliweb.commonbacken.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;

public interface TipoidentificacionRepository  extends JpaRepository<Tipoidentificacion, Long> {

	Optional<Tipoidentificacion> findByCodigo(int codigo);
}
