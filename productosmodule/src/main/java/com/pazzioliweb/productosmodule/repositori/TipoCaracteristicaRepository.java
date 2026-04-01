package com.pazzioliweb.productosmodule.repositori;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.productosmodule.entity.TipoCaracteristica;

public interface TipoCaracteristicaRepository extends JpaRepository<TipoCaracteristica, Long>{
	Optional<TipoCaracteristica> findByNombre(String nombre);
	Page<TipoCaracteristica>  findByNombreContainingIgnoreCase(String descripcion,Pageable pageable);

}
