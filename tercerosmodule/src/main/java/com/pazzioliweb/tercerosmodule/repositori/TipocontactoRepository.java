package com.pazzioliweb.tercerosmodule.repositori;

import com.pazzioliweb.tercerosmodule.entity.TipoContacto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipocontactoRepository  extends JpaRepository<TipoContacto, Integer> {
    Optional<TipoContacto> findByNombreIgnoreCase(String nombre);
}
