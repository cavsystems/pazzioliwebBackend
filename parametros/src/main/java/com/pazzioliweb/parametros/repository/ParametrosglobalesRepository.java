package com.pazzioliweb.parametros.repository;

import com.pazzioliweb.parametros.dtos.ParametroGlobalResponseDTO;
import com.pazzioliweb.parametros.entity.Parametrosglobales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParametrosglobalesRepository extends JpaRepository<Parametrosglobales, Integer> {
    Optional<Parametrosglobales> findByParametrosId(Long parametroId);

    @Query("SELECT new com.pazzioliweb.parametros.dtos.ParametroGlobalResponseDTO(" +
           "pg.id, p.nombre, p.categoriaparametro, p.categoriacomprobante, pg.valor) " +
           "FROM Parametrosglobales pg JOIN pg.parametros p " +
           "WHERE (:categoriaparametro IS NULL OR p.categoriaparametro = :categoriaparametro) " +
           "AND (:categoriacomprobante IS NULL OR p.categoriacomprobante = :categoriacomprobante)")
    List<ParametroGlobalResponseDTO> findJoinByCategorias(
            @Param("categoriaparametro") String categoriaparametro,
            @Param("categoriacomprobante") String categoriacomprobante);
}
