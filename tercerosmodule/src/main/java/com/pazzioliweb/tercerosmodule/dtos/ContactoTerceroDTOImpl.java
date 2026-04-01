package com.pazzioliweb.tercerosmodule.dtos;

import com.pazzioliweb.tercerosmodule.entity.ContactoTercero;
import com.pazzioliweb.tercerosmodule.entity.TipoContacto;

public class ContactoTerceroDTOImpl implements ContactoTerceroDTO {

    private Integer contactoId;       // <- para getContactoId()
    private String valorContacto;     
    private Boolean esPrincipal;
    private TipoContactoInfoDTOImpl tipoContacto;

    public ContactoTerceroDTOImpl() {}

    @Override
    public Integer getContactoId() {
        return contactoId;
    }

    public void setContactoId(Integer contactoId) {
        this.contactoId = contactoId;
    }

    @Override
    public String getValorContacto() {
        return valorContacto;
    }

    public void setValorContacto(String valorContacto) {
        this.valorContacto = valorContacto;
    }

    @Override
    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    @Override
    public TipoContactoInfoDTOImpl getTipoContacto() {
        return tipoContacto;
    }

    public void setTipoContacto(TipoContactoInfoDTOImpl tipoContacto) {
        this.tipoContacto = tipoContacto;
    }

    // Clase interna concreta para la interfaz TipoContactoInfo
    public static class TipoContactoInfoDTOImpl implements ContactoTerceroDTO.TipoContactoInfo {

        private Integer tipoContactoId;
        private String nombre;

        public TipoContactoInfoDTOImpl() {}

        @Override
        public Integer getTipoContactoId() {
            return tipoContactoId;
        }

        public void setTipoContactoId(Integer tipoContactoId) {
            this.tipoContactoId = tipoContactoId;
        }

        @Override
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String descripcion) {
            this.nombre = descripcion;
        }
    }
    
    public static ContactoTerceroDTOImpl fromEntity(ContactoTercero entity) {
        if (entity == null) return null;

        ContactoTerceroDTOImpl dto = new ContactoTerceroDTOImpl();
        dto.setContactoId(entity.getContactoId());
        dto.setValorContacto(entity.getValorContacto());
        dto.setEsPrincipal(entity.getEsPrincipal());

        if (entity.getTipoContacto() != null) {
            TipoContactoInfoDTOImpl tipoDTO = new TipoContactoInfoDTOImpl();
            tipoDTO.setTipoContactoId(entity.getTipoContacto().getTipoContactoId());
            tipoDTO.setNombre(entity.getTipoContacto().getNombre());
            dto.setTipoContacto(tipoDTO);
        }

        return dto;
    }
    
    public ContactoTercero toEntity() {
    	ContactoTercero entidad = new ContactoTercero();
    	entidad.setContactoId(this.contactoId);
    	entidad.setValorContacto(this.valorContacto);
    	entidad.setEsPrincipal(this.esPrincipal);
    	
    	if(this.tipoContacto !=null) {
    		TipoContacto Tcontacto = new TipoContacto();
    		Tcontacto.setTipoContactoId(this.tipoContacto.getTipoContactoId());
    		entidad.setTipoContacto(Tcontacto);
    	}
    	return entidad;
    }
}