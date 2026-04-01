package com.pazzioliweb.commonbacken.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Sesiones;

public interface DepartamentoRepositori extends  JpaRepository<Departamento, Long>{
	public Optional<Departamento> findByCodigo(int codigo);
	public List<Departamento> findByCodigopais(int codigoPais);
}
