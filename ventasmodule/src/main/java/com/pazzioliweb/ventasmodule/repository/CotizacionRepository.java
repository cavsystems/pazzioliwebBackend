package com.pazzioliweb.ventasmodule.repository;

import com.pazzioliweb.ventasmodule.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    Optional<Cotizacion> findByNumeroCotizacion(String numeroCotizacion);

    @Query("SELECT c FROM Cotizacion c LEFT JOIN FETCH c.items WHERE c.numeroCotizacion = :numeroCotizacion")
    Optional<Cotizacion> findByNumeroCotizacionWithItems(@Param("numeroCotizacion") String numeroCotizacion);

    @Query("SELECT DISTINCT c FROM Cotizacion c LEFT JOIN FETCH c.items WHERE c.cliente.terceroId = :clienteId")
    List<Cotizacion> findCotizacionesByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT c FROM Cotizacion c WHERE c.estado IN ('BORRADOR', 'ENVIADA')")
    List<Cotizacion> findCotizacionesActivas();

    @Query("SELECT DISTINCT c FROM Cotizacion c LEFT JOIN FETCH c.items WHERE c.estado = 'ACEPTADA' AND c.cliente.terceroId = :clienteId")
    List<Cotizacion> findCotizacionesAceptadasByClienteId(@Param("clienteId") Long clienteId);
}

