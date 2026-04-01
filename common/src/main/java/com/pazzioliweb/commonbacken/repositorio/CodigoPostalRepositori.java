package com.pazzioliweb.commonbacken.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.commonbacken.entity.CodigoPostal;

public interface CodigoPostalRepositori extends JpaRepository<CodigoPostal, Long>{
	public List<CodigoPostal> findByCodigoMunicipioOrderByCodigoPostal(int codigo);

}
