package com.pazzioliweb.movimientosinventariomodule.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.movimientosinventariomodule.entity.Kardex;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.entity.Bodegas;

public interface KardexRepository extends JpaRepository<Kardex, Long> {

        List<Kardex> findByMovimiento_MovimientoId(Long id);

        Optional<Kardex> findTopByProductoVarianteAndBodegaOrderByFechaCreacionDesc(ProductoVariante variante, Bodegas bodega);

}
