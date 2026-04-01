package com.pazzioliweb.cajerosmodule.dtos;

import java.time.LocalDate;

public class ReponsecajeroDTO {

    private Integer cajeroId;

    private String nombre;


    public ReponsecajeroDTO (Integer cajeroId,
                     String nombre) {
        this.cajeroId = cajeroId;

        this.nombre = nombre;

    }

    // getters
    public Integer getCajeroId() { return cajeroId; }

    public String getNombre() { return nombre; }


}
