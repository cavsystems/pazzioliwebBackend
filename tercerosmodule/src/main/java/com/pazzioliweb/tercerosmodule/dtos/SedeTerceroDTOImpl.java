package com.pazzioliweb.tercerosmodule.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.tercerosmodule.entity.SedeTercero;

public class SedeTerceroDTOImpl implements SedeTerceroDTO {

    private Integer sedeId;
    private String nombreSede;
    private String direccion;
    private String telefono;
    private Boolean principal;
    private Boolean activo;
    @JsonDeserialize(as = DepartamentoInfoDTOImpl.class)
    private DepartamentoInfo departamento;
    @JsonDeserialize(as = MunicipioInfoDTOImpl.class)
    private MunicipioInfo municipio;
    
    public SedeTerceroDTOImpl() {} 
    
    // ------------------ Método de conversión ------------------
    public static SedeTerceroDTOImpl fromEntity(SedeTercero sede) {
        SedeTerceroDTOImpl dto = new SedeTerceroDTOImpl();
        dto.sedeId = sede.getSedeId();
        dto.nombreSede = sede.getNombreSede();
        dto.direccion = sede.getDireccion();
        dto.telefono = sede.getTelefono();
        dto.principal = sede.getPrincipal();
        dto.activo = sede.getActivo();
        dto.departamento = sede.getDepartamento() != null
                ? new DepartamentoInfoDTOImpl(sede.getDepartamento())
                : null;
        dto.municipio = sede.getMunicipio() != null
                ? new MunicipioInfoDTOImpl(sede.getMunicipio())
                : null;
        return dto;
    }

    // ------------------ Getters ------------------
    @Override public Integer getSedeId() { return sedeId; }
    @Override public String getNombreSede() { return nombreSede; }
    @Override public String getDireccion() { return direccion; }
    @Override public String getTelefono() { return telefono; }
    @Override public Boolean getPrincipal() { return principal; }
    @Override public Boolean getActivo() { return activo; }
    @Override public DepartamentoInfo getDepartamento() { return departamento; }
    @Override public MunicipioInfo getMunicipio() { return municipio; }
    
    public SedeTercero toEntity() {
        SedeTercero entidad = new SedeTercero();
        entidad.setSedeId(this.getSedeId());
        entidad.setNombreSede(this.getNombreSede());
        entidad.setDireccion(this.getDireccion());
        entidad.setTelefono(this.getTelefono());
        entidad.setPrincipal(this.getPrincipal());
        entidad.setActivo(this.getActivo());

        // Mapeo de relaciones
        if (this.departamento != null) {
            Departamento dep = new Departamento();
            dep.setCodigo(this.departamento.getDepartamentoId()); // <-- aquí usar el método correcto
            entidad.setDepartamento(dep);
        }

        if (this.municipio != null) {
            Municipio mun = new Municipio();
            mun.setCodigo(this.municipio.getMunicipioId());
            entidad.setMunicipio(mun);
        }

        return entidad;
    }
}