package com.pazzioliweb.parametros.repository;

import com.pazzioliweb.parametros.entity.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParametrosRepository extends JpaRepository<Parametros, Long> {
    Optional<Parametros> findByNombre(String nombre);
    List<Parametros> findByCategoriacomprobanteAndCategoriaparametro(String categoriacomprobante, String categoriaparametro);
}
