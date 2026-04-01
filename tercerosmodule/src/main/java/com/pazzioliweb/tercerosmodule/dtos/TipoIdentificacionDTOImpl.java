package com.pazzioliweb.tercerosmodule.dtos;

import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;

public class TipoIdentificacionDTOImpl implements TipoIdentificacionDTO{

    private Integer codigo;
    private Integer codigoTipoIdentificacion;
    private String tipoIdentificacion;

    public TipoIdentificacionDTOImpl() {} // constructor vacío requerido por Jackson

    // Getters y setters
    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public Integer getCodigoTipoIdentificacion() { return codigoTipoIdentificacion; }
    public void setCodigoTipoIdentificacion(Integer codigoTipoIdentificacion) { this.codigoTipoIdentificacion = codigoTipoIdentificacion; }

    public String getTipoIdentificacion() { return tipoIdentificacion; }
    public void setTipoIdentificacion(String tipoIdentificacion) { this.tipoIdentificacion = tipoIdentificacion; }

    public static TipoIdentificacionDTOImpl fromEntity(Tipoidentificacion entity) {
        TipoIdentificacionDTOImpl dto = new TipoIdentificacionDTOImpl();
        dto.codigo = entity.getCodigo();
        dto.codigoTipoIdentificacion = entity.getCodigoTipoIdentificacion();
        dto.tipoIdentificacion = entity.getTipoIdentificacion();
        return dto;
    }
    
    public Tipoidentificacion toEntity() {
    	Tipoidentificacion tipoIdentificacion = new Tipoidentificacion();
    	tipoIdentificacion.setCodigo(this.codigo);
    	tipoIdentificacion.setCodigoTipoIdentificacion(this.codigoTipoIdentificacion);
    	tipoIdentificacion.setTipoIdentificacion(this.tipoIdentificacion);
    	
    	return tipoIdentificacion;
    }
}