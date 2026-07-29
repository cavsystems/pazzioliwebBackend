package com.pazzioliweb.productosmodule.repositori;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.productosmodule.entity.TipoProducto;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TipoProductoRepository extends JpaRepository<TipoProducto, Integer>{

	Optional<TipoProducto> findByNombre(String nombre);
	List<TipoProducto> findByNombreIn(Collection<String> nombres);

}
