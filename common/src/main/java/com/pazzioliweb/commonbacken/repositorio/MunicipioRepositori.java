package com.pazzioliweb.commonbacken.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;

public interface  MunicipioRepositori extends JpaRepository<Municipio, Long> {
 public Optional<Municipio> findByCodigo(int codigo);
 public List<Municipio> findByCodigoDepartamento(int codigodepartamento);
 
}
