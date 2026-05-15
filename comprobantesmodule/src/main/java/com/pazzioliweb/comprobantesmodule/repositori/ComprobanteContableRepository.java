package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComprobanteContableRepository extends JpaRepository<ComprobanteContable, Long> {

    List<ComprobanteContable> findByActivoTrueOrderByCajeroIdAscTipoMovimientoAsc();

    @Query("SELECT c FROM ComprobanteContable c WHERE c.cajeroId IS NULL AND c.esLegacy = true")
    List<ComprobanteContable> findAllLegacy();

    /** Busca el comprobante específico de un cajero para un tipo de movimiento. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ComprobanteContable c WHERE c.cajeroId = :cajeroId AND c.tipoMovimiento = :tipo AND c.activo = true AND c.esLegacy = false")
    Optional<ComprobanteContable> findActivoByCajeroAndTipoForUpdate(
            @Param("cajeroId") Integer cajeroId,
            @Param("tipo") TipoMovimientoComprobante tipo);

    @Query("SELECT c FROM ComprobanteContable c WHERE c.cajeroId = :cajeroId AND c.tipoMovimiento = :tipo AND c.activo = true AND c.esLegacy = false")
    Optional<ComprobanteContable> findActivoByCajeroAndTipo(
            @Param("cajeroId") Integer cajeroId,
            @Param("tipo") TipoMovimientoComprobante tipo);

    /** El comprobante LEGACY de un tipo (cajero null, esLegacy true). Único por tipo. */
    @Query("SELECT c FROM ComprobanteContable c WHERE c.cajeroId IS NULL AND c.tipoMovimiento = :tipo AND c.esLegacy = true")
    Optional<ComprobanteContable> findLegacyByTipo(@Param("tipo") TipoMovimientoComprobante tipo);

    @Query("SELECT c FROM ComprobanteContable c WHERE c.tipoMovimiento = :tipo")
    List<ComprobanteContable> findByTipo(@Param("tipo") TipoMovimientoComprobante tipo);

    List<ComprobanteContable> findByCajeroId(Integer cajeroId);

    boolean existsByPrefijoAndIdNot(String prefijo, Long id);

    boolean existsByPrefijo(String prefijo);
}
