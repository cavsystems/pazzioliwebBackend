package com.pazzioliweb.cajerosmodule.repositori;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.cajerosmodule.dtos.DetalleCajeroDTO;
import com.pazzioliweb.cajerosmodule.entity.DetalleCajero;

public interface DetalleCajeroRepository extends JpaRepository<DetalleCajero, Long> {

    @Query("""
           SELECT 
               d.detalleCajeroId              AS detalleCajeroId,
               d.cajero.cajeroId              AS cajeroId,
               d.cajero.nombre                AS cajeroNombre,
               d.montoInicial                 AS montoInicial,
               d.montoFinal                   AS montoFinal,
               d.fechaApertura                AS fechaApertura,
               d.fechaCierre                  AS fechaCierre,
               d.estado                       AS estado,
               comp                           AS comprobante,
               d.consecutivo                  AS consecutivo,
               d.totalRecaudo                 AS totalRecaudo,
               d.totalCosto                   AS totalCosto,
               d.baseCaja                     AS baseCaja,
               d.totalEfectivo                AS totalEfectivo,
               d.totalMediosElectronicos      AS totalMediosElectronicos,
               d.efectivoDeclarado            AS efectivoDeclarado,
               d.mediosElectronicosDeclarado  AS mediosElectronicosDeclarado,
               d.diferenciaEfectivo           AS diferenciaEfectivo,
               d.diferenciaMediosElectronicos AS diferenciaMediosElectronicos
           FROM DetalleCajero d
           LEFT JOIN d.comprobante comp
           """)
    Page<DetalleCajeroDTO> listarDetalleCajerosDTO(Pageable pageable);

    @Query("""
           SELECT 
               d.detalleCajeroId              AS detalleCajeroId,
               d.cajero.cajeroId              AS cajeroId,
               d.cajero.nombre                AS cajeroNombre,
               d.montoInicial                 AS montoInicial,
               d.montoFinal                   AS montoFinal,
               d.fechaApertura                AS fechaApertura,
               d.fechaCierre                  AS fechaCierre,
               d.estado                       AS estado,
               comp                           AS comprobante,
               d.consecutivo                  AS consecutivo,
               d.totalRecaudo                 AS totalRecaudo,
               d.totalCosto                   AS totalCosto,
               d.baseCaja                     AS baseCaja,
               d.totalEfectivo                AS totalEfectivo,
               d.totalMediosElectronicos      AS totalMediosElectronicos,
               d.efectivoDeclarado            AS efectivoDeclarado,
               d.mediosElectronicosDeclarado  AS mediosElectronicosDeclarado,
               d.diferenciaEfectivo           AS diferenciaEfectivo,
               d.diferenciaMediosElectronicos AS diferenciaMediosElectronicos
           FROM DetalleCajero d
           LEFT JOIN d.comprobante comp
           WHERE d.cajero.cajeroId = :cajeroId
           """)
    Page<DetalleCajeroDTO> listarDetallePorCajeroDTO(@Param("cajeroId") Integer cajeroId, Pageable pageable);

    List<DetalleCajero> findByCajero_CajeroIdAndEstado(Integer cajeroId, DetalleCajero.EstadoDetalleCajero estado);
}

