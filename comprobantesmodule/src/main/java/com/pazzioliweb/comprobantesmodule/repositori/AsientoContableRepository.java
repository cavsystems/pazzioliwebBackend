package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.AsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsientoContableRepository extends JpaRepository<AsientoContable, Long> {

    // Puede haber varias filas para el mismo documento origen cuando un asiento se
    // anula y se regenera (queda el ANULADO + el CONFIRMADO nuevo). Traemos todas
    // ordenadas por id desc y el método público devuelve la más reciente, evitando
    // NonUniqueResultException.
    @Query("SELECT a FROM AsientoContable a LEFT JOIN FETCH a.comprobante " +
           " WHERE a.documentoOrigenTipo = :tipo AND a.documentoOrigenId = :id ORDER BY a.id DESC")
    List<AsientoContable> findAllByDocumentoOrigen(
            @Param("tipo") String documentoOrigenTipo,
            @Param("id") Long documentoOrigenId);

    /** Compatibilidad: devuelve el asiento MÁS RECIENTE del documento origen. */
    default Optional<AsientoContable> findByDocumentoOrigenTipoAndDocumentoOrigenId(
            String documentoOrigenTipo, Long documentoOrigenId) {
        return findAllByDocumentoOrigen(documentoOrigenTipo, documentoOrigenId).stream().findFirst();
    }

    List<AsientoContable> findByFechaBetweenOrderByFechaAscIdAsc(LocalDate desde, LocalDate hasta);

    /** Listado para el reporte: trae el comprobante en eager para evitar LazyInitException. */
    @Query("SELECT a FROM AsientoContable a LEFT JOIN FETCH a.comprobante " +
           " WHERE a.fecha BETWEEN :desde AND :hasta ORDER BY a.fecha, a.id")
    List<AsientoContable> rangoFechas(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    /** Detalle: trae comprobante + líneas + cuentas contables en eager. */
    @Query("SELECT DISTINCT a FROM AsientoContable a " +
           " LEFT JOIN FETCH a.comprobante " +
           " LEFT JOIN FETCH a.lineas l " +
           " LEFT JOIN FETCH l.cuentaContable " +
           " WHERE a.id = :id")
    Optional<AsientoContable> findByIdConDetalle(@Param("id") Long id);
}
