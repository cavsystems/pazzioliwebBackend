package com.pazzioliweb.productosmodule.repositori;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.productosmodule.entity.UnidadesMedidaProducto;
import com.pazzioliweb.productosmodule.entity.UnidadesMedidaProductoId;

public interface UnidadesMedidaProductoRepository extends JpaRepository<UnidadesMedidaProducto, UnidadesMedidaProductoId>{
	Page<UnidadesMedidaProducto> findByProducto_ProductoId(Integer productoId, Pageable pageable);
	
	List<UnidadesMedidaProducto> findByProducto_ProductoId(Integer productoId);

	void deleteByProducto_ProductoId(Integer productoId);
}
