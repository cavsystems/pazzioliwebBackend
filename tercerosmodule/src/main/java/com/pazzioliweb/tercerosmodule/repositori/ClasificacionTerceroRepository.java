package com.pazzioliweb.tercerosmodule.repositori;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pazzioliweb.tercerosmodule.entity.ClasificacionTercero;

@Repository
public interface ClasificacionTerceroRepository extends JpaRepository<ClasificacionTercero, Integer>{

}
