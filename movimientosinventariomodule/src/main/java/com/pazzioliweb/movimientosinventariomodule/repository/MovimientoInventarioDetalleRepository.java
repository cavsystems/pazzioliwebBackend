package com.pazzioliweb.movimientosinventariomodule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventarioDetalle;

public interface MovimientoInventarioDetalleRepository extends JpaRepository<MovimientoInventarioDetalle, Long>{
	List<MovimientoInventarioDetalle> findByMovimiento(Long id);
}
