package com.pazzioliweb.commonbacken.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pazzioliweb.commonbacken.entity.Impuestos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class ImpuestosDTO {

    @JsonProperty
    private Integer codigo;
    @JsonProperty
    private String nombre;
    @JsonProperty
    private double tarifa;
    @JsonProperty
    private double base;
    @JsonProperty
    private String sigla;
    @JsonProperty
    private String estado;

    // Constructor vacío necesario para Jackson
    public ImpuestosDTO() {}

    public ImpuestosDTO(Integer codigo, String nombre, double tarifa, double base, String sigla, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tarifa = tarifa;
        this.base = base;
        this.sigla = sigla;
        this.estado = estado;
    }

    // Método fromEntity seguro frente a valores nulos
    public static ImpuestosDTO fromEntity(Impuestos impuesto) {
        if (impuesto == null) return null;

        return new ImpuestosDTO(
            impuesto.getCodigo() != null ? impuesto.getCodigo() : 0,
            impuesto.getNombre() != null ? impuesto.getNombre() : "",
            impuesto.getTarifa() != null ? impuesto.getTarifa() : 0.0,
            impuesto.getBase() != null ? impuesto.getBase() : 0.0,
            impuesto.getSigla() != null ? impuesto.getSigla() : "",
            impuesto.getEstado() != null ? impuesto.getEstado() : ""
        );
    }
}
