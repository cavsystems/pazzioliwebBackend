package com.pazzioliweb.tercerosmodule.dtos;

import java.util.List;
import java.util.Set;

import com.pazzioliweb.commonbacken.dtos.RetencionesDTO;
import com.pazzioliweb.commonbacken.entity.Retenciones;
import com.pazzioliweb.empresaback.dtos.RegimenDTO;
import com.pazzioliweb.productosmodule.dtos.OtroprecioDTO;

public interface TerceroDtoresponse {
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
    OtroprecioDTO getPrecio();
    RegimenDTO getRegimen();
    List<ContactoTerceroDTO> getContactos();
    List<SedeTerceroDTO> getSedes();
   List<RetencionesDTO> getRetenciones();

    String getFechaNacimiento();
    String getMatriculaMercantil();
    Integer getActividadEconomicaId();
    TipoPersonaDTO getTipoPersona();
}
