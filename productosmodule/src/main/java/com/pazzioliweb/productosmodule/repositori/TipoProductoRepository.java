package com.pazzioliweb.productosmodule.repositori;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.productosmodule.entity.TipoProducto;
import java.util.Optional;

public interface TipoProductoRepository extends JpaRepository<TipoProducto, Integer>{

	Optional<TipoProducto> findByNombre(String nombre);

}
