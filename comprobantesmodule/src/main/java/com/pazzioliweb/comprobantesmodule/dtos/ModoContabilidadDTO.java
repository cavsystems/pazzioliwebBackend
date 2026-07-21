package com.pazzioliweb.comprobantesmodule.dtos;

import lombok.Data;

import java.time.LocalDate;

/**
 * modoPOS — payload de lectura/escritura del flag de contabilidad de la empresa (tenant).
 */
@Data
public class ModoContabilidadDTO {
    /** true = lleva contabilidad (suite completa); false = POS puro (sin asientos ni periodos). */
    private Boolean contabilidadActiva;
    /** Fecha de corte: solo se contabilizan documentos con fecha >= esta. null = sin corte. */
    private LocalDate contabilidadDesde;
}
