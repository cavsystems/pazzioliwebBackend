package com.pazzioliweb.commonbacken.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.commonbacken.entity.Pais;
import com.pazzioliweb.commonbacken.entity.Retenciones;

public interface RetencionesRepositori   extends  JpaRepository<Retenciones, Long>{
	

}
