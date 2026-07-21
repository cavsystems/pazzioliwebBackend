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

    @Query(value = "SELECT pra.id, pr.nombre, pr.categoriaparametro, pr.categoriacomprobante, pra.valor, " +
           "com.id as idcomprobante, com.prefijo FROM parametros pr " +
           "JOIN parametroscomprobantes pra ON pra.parametroid = pr.id " +
           "JOIN comprobantes_contables com ON com.id = pra.comprobanteContableid " +
           "WHERE (:categoriacomprobante IS NULL OR pr.categoriacomprobante = :categoriacomprobante) " +
           "AND (:categoriaparametro IS NULL OR pr.categoriaparametro = :categoriaparametro) " +
           "UNION ALL " +
           "SELECT pa.id, pr.nombre, pr.categoriaparametro, pr.categoriacomprobante, pa.valor, " +
           "0 AS idcomprobante, pr.categoriacomprobante AS prefijo " +
           "FROM parametrosglobales pa JOIN parametros pr ON pa.parametroid = pr.id " +
           "WHERE (:categoriacomprobante IS NULL OR pr.categoriacomprobante = :categoriacomprobante) " +
           "AND (:categoriaparametro IS NULL OR pr.categoriaparametro = :categoriaparametro)",
           nativeQuery = true)
    List<Object[]> findJoinByCategoriasSinComprobante(
            @Param("categoriacomprobante") String categoriacomprobante,
            @Param("categoriaparametro") String categoriaparametro);
}
