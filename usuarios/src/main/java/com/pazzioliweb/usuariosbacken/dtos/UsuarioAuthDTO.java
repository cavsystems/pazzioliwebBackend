package com.pazzioliweb.usuariosbacken.dtos;

import java.time.LocalDate;

public interface UsuarioAuthDTO {
    int getCodigo();
    String getNombre();
    String getUsuario();
    String getContrasena();
    String getEstado();
    int getCodigorol();
    int getCodigousuariocreado();
    LocalDate getFechacreado();
    LocalDate getFechamodificado();
}
