package com.pazzioliweb.tercerosmodule.dtos;


import java.util.List;

import com.pazzioliweb.empresaback.dtos.RegimenDTO;
import com.pazzioliweb.productosmodule.dtos.PrecioDTO;

public interface TerceroDTO {
	Integer getTerceroId();
    String getIdentificacion();
    String getDv();
    String getNombre1();
    String getNombre2();
    String getApellido1();
    String getApellido2();
    String getRazonSocial();
    String getDireccion();
    Integer getPlazo();
    Integer getCupo();

    TipoIdentificacionDTO getTipoIdentificacion();
    ClasificacionTerceroDTO getClasificacionTercero();
    PrecioDTO getPrecio();
    RegimenDTO getRegimen();
    List<ContactoTerceroDTO> getContactos();
    List<SedeTerceroDTO> getSedes();
    
    String getFechaNacimiento();
    String getMatriculaMercantil();
    Integer getActividadEconomicaId();
    TipoPersonaDTO getTipoPersona();
}
