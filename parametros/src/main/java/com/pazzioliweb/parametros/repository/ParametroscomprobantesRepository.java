package com.pazzioliweb.parametros.repository;

import com.pazzioliweb.parametros.dtos.ParametroComprobanteResponseDTO;
import com.pazzioliweb.parametros.entity.Parametroscomprobantes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParametroscomprobantesRepository extends JpaRepository<Parametroscomprobantes, Integer> {
    List<Parametroscomprobantes> findByParametrosId(Long parametroId);
    Optional<Parametroscomprobantes> findByParametrosIdAndComprobanteContableId(Long parametroId, Integer comprobanteContableId);

    @Query("SELECT new com.pazzioliweb.parametros.dtos.ParametroComprobanteResponseDTO(" +
           "pc.id, p.nombre, p.categoriaparametro, p.categoriacomprobante, pc.valor, " +
           "cc.id, cc.prefijo) " +
           "FROM Parametroscomprobantes pc JOIN pc.parametros p JOIN pc.comprobanteContable cc " +
           "WHERE (:categoriacomprobante IS NULL OR p.categoriacomprobante = :categoriacomprobante) " +
           "AND (:comprobanteId IS NULL OR cc.id = :comprobanteId) " +
           "AND (:categoriaparametro IS NULL OR p.categoriaparametro = :categoriaparametro)")
    List<ParametroComprobanteResponseDTO> findJoinByCategorias(
            @Param("categoriacomprobante") String categoriacomprobante,
            @Param("comprobanteId") Long comprobanteId,
            @Param("categoriaparametro") String categoriaparametro);
}
