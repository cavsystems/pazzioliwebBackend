package com.pazzioliweb.commonbacken.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.commonbacken.entity.Pais;

public interface PaisRepositori  extends  JpaRepository<Pais, Long>{
	public Optional<Pais> findByCodigo(int codigo);
	public List<Pais> findByCodigoIn(List<Integer> codigos);
}
