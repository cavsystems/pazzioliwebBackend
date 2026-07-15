package com.pazzioliweb.parametros.repository;

import com.pazzioliweb.parametros.entity.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParametrosRepository extends JpaRepository<Parametros, Long> {
    Optional<Parametros> findByNombre(String nombre);

    @Query("SELECT p FROM Parametros p WHERE " +
           "(:categoriacomprobante IS NULL OR p.categoriacomprobante = :categoriacomprobante) AND " +
           "(:categoriaparametro IS NULL OR p.categoriaparametro = :categoriaparametro)")
    List<Parametros> findByCategorias(
            @Param("categoriacomprobante") String categoriacomprobante,
            @Param("categoriaparametro") String categoriaparametro);
}
