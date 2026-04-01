package com.pazzioliweb.movimientosinventariomodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.movimientosinventariomodule.entity.Kardex;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventarioDetalle;

public interface KardexRepository extends JpaRepository<Kardex, Long>{
	
	List<Kardex> findByMovimientoId(Long id);
	
}
