package com.pazzioliweb.commonbacken.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Pais;
import com.pazzioliweb.commonbacken.entity.Sesiones;

public interface PaisRepositori  extends  JpaRepository<Pais, Long>{
	public Optional<Pais> findByCodigo(int codigo);
}
