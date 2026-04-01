package com.pazzioliweb.cajerosmodule.dtos;

import java.time.LocalDate;

public interface CajeroDTO {
    Integer getCajeroId();
    Integer getUsuarioId();
    String getUsuarioNombre();
    String getNombre();
    String getEstado();
    Integer getCodigoUsuarioCreo();
    LocalDate getFechacreado();
}
