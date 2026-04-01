package com.pazzioliweb.empresaback.dtos;

import com.pazzioliweb.empresasback.entity.Regimen;

public class RegimenDTOImpl implements com.pazzioliweb.empresaback.dtos.RegimenDTO {

    private Integer codigo;
    private String codigoRegimen;
    private String nombre;
    private String estado;
    
    public RegimenDTOImpl () {}
    
    public static RegimenDTOImpl fromEntity(Regimen r) {
        if (r == null) return null;
        RegimenDTOImpl dto = new RegimenDTOImpl();
        dto.codigo = r.getCodigo();
        dto.codigoRegimen = r.getCodigoRegimen();
        dto.nombre = r.getDescripcion();
        dto.estado = r.getEstado();
        return dto;
    }
    
    public Regimen toEntity() {
        Regimen entity = new Regimen();
        entity.setCodigo(this.codigo);
        entity.setCodigoRegimen(this.codigoRegimen);
        entity.setDescripcion(this.nombre);
        entity.setEstado(this.estado);
        return entity;
    }

    @Override
    public Integer getCodigo() {
        return codigo;
    }
    
    @Override
    public String getCodigoRegimen() {
        return codigoRegimen;
    }
    
    @Override
    public String getDescripcion() {
        return nombre;
    }
    
    @Override
    public String getEstado() {
        return estado;
    }
}
