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

    List<ComprobanteContable> findByActivoTrueOrderByTipoMovimientoAsc();

    @Query("SELECT c FROM ComprobanteContable c WHERE c.esLegacy = true")
    List<ComprobanteContable> findAllLegacy();

    /**
     * Busca el comprobante asignado a un cajero específico para un tipo de
     * movimiento — con lock pesimista para evitar consecutivos duplicados.
     * El cajero puede tener varios comprobantes asignados; devuelve el primero
     * activo y no-LEGACY.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ComprobanteContable c " +
           " WHERE :cajeroId MEMBER OF c.cajeroIds " +
           "   AND c.tipoMovimiento = :tipo " +
           "   AND c.activo = true " +
           "   AND c.esLegacy = false")
    Optional<ComprobanteContable> findActivoByCajeroAndTipoForUpdate(
            @Param("cajeroId") Integer cajeroId,
            @Param("tipo") TipoMovimientoComprobante tipo);

    @Query("SELECT c FROM ComprobanteContable c " +
           " WHERE :cajeroId MEMBER OF c.cajeroIds " +
           "   AND c.tipoMovimiento = :tipo " +
           "   AND c.activo = true " +
           "   AND c.esLegacy = false")
    Optional<ComprobanteContable> findActivoByCajeroAndTipo(
            @Param("cajeroId") Integer cajeroId,
            @Param("tipo") TipoMovimientoComprobante tipo);

    /** El comprobante LEGACY de un tipo (sin cajeros asignados). Único por tipo. */
    @Query("SELECT c FROM ComprobanteContable c " +
           " WHERE c.tipoMovimiento = :tipo " +
           "   AND c.esLegacy = true")
    Optional<ComprobanteContable> findLegacyByTipo(@Param("tipo") TipoMovimientoComprobante tipo);

    @Query("SELECT c FROM ComprobanteContable c WHERE c.tipoMovimiento = :tipo")
    List<ComprobanteContable> findByTipo(@Param("tipo") TipoMovimientoComprobante tipo);

    @Query("SELECT c FROM ComprobanteContable c WHERE c.tipoMovimiento IN :tipos AND c.activo = true ORDER BY c.tipoMovimiento ASC")
    List<ComprobanteContable> findByTiposAndActivoTrue(@Param("tipos") List<TipoMovimientoComprobante> tipos);

    /** Todos los comprobantes que tienen al menos a este cajero asignado. */
    @Query("SELECT c FROM ComprobanteContable c WHERE :cajeroId MEMBER OF c.cajeroIds")
    List<ComprobanteContable> findByCajeroId(@Param("cajeroId") Integer cajeroId);

    /** Verifica si un prefijo ya está usado para un tipo de movimiento dado. */
    @Query("SELECT COUNT(c) > 0 FROM ComprobanteContable c " +
           " WHERE LOWER(c.prefijo) = LOWER(:prefijo) " +
           "   AND c.tipoMovimiento = :tipo")
    boolean existsByPrefijoAndTipo(@Param("prefijo") String prefijo,
                                    @Param("tipo") TipoMovimientoComprobante tipo);

    @Query("SELECT COUNT(c) > 0 FROM ComprobanteContable c " +
           " WHERE LOWER(c.prefijo) = LOWER(:prefijo) " +
           "   AND c.tipoMovimiento = :tipo " +
           "   AND c.id <> :id")
    boolean existsByPrefijoAndTipoAndIdNot(@Param("prefijo") String prefijo,
                                            @Param("tipo") TipoMovimientoComprobante tipo,
                                            @Param("id") Long id);

    Optional<ComprobanteContable> findByPrefijo(String prefijo);
}
