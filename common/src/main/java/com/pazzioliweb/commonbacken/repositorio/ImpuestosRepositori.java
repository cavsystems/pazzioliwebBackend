package com.pazzioliweb.commonbacken.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.commonbacken.entity.Impuestos;
import com.pazzioliweb.commonbacken.entity.Municipio;

public interface ImpuestosRepositori extends JpaRepository<Impuestos, Integer> {
	public Optional<Impuestos> findByCodigo(int codigo);
	public Optional<Impuestos> findByTarifa(double tarifa);
}
