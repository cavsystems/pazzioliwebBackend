package com.pazzioliweb.tesoreriamodule.service;

import com.pazzioliweb.comprasmodule.repository.CuentaPorPagarRepository;
import com.pazzioliweb.ventasmodule.repository.CuentaPorCobrarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Actualiza el estado de la cartera a VENCIDA cuando la fecha de vencimiento ya pasó.
 * Vive en tesoreriamodule porque este módulo ya depende de ventasmodule (CxC) y
 * comprasmodule (CxP). Lo invoca {@code CarteraVencimientoScheduler} por cada tenant.
 *
 * NOTA: solo cambia PENDIENTE/PARCIAL -> VENCIDA (nunca toca PAGADA/ANULADA). Las
 * cuentas VENCIDA siguen apareciendo en las listas de cobro/pago y cuentan al cupo,
 * porque esas consultas ya incluyen el estado 'VENCIDA'.
 */
@Service
public class CarteraVencimientoService {

    private final CuentaPorCobrarRepository cxcRepository;
    private final CuentaPorPagarRepository cxpRepository;

    @Autowired
    public CarteraVencimientoService(CuentaPorCobrarRepository cxcRepository,
                                     CuentaPorPagarRepository cxpRepository) {
        this.cxcRepository = cxcRepository;
        this.cxpRepository = cxpRepository;
    }

    /**
     * Marca como VENCIDA la cartera del tenant activo cuya fecha de vencimiento es anterior a {@code hoy}.
     * @return arreglo {cxcVencidas, cxpVencidas} con el número de filas afectadas.
     */
    @Transactional
    public int[] marcarVencidas(LocalDate hoy) {
        int cxc = cxcRepository.marcarVencidas(hoy);
        int cxp = cxpRepository.marcarVencidas(hoy);
        return new int[]{ cxc, cxp };
    }
}
