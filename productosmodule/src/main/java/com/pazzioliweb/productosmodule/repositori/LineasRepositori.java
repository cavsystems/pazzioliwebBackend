package com.pazzioliweb.productosmodule.repositori;


import org.springframework.data.domain.Pageable;


import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.productosmodule.entity.Lineas;
import java.util.Optional;

public interface LineasRepositori extends JpaRepository<Lineas,Integer>  { 
Page<Lineas>  findByDescripcionContainingIgnoreCase(String descripcion,Pageable pageable);

Optional<Lineas> findByDescripcion(String descripcion);

}
