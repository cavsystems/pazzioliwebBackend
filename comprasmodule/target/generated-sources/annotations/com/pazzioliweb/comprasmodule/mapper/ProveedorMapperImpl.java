package com.pazzioliweb.comprasmodule.mapper;

import com.pazzioliweb.comprasmodule.dtos.ProveedorDTO;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-13T07:38:54-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class ProveedorMapperImpl implements ProveedorMapper {

    @Override
    public ProveedorDTO toDto(Terceros terceros) {
        if ( terceros == null ) {
            return null;
        }

        ProveedorDTO proveedorDTO = new ProveedorDTO();

        if ( terceros.getTerceroId() != null ) {
            proveedorDTO.setId( terceros.getTerceroId().longValue() );
        }
        proveedorDTO.setNit( terceros.getIdentificacion() );
        proveedorDTO.setRazonSocial( terceros.getRazonSocial() );
        proveedorDTO.setDireccion( terceros.getDireccion() );

        proveedorDTO.setActivo( true );

        return proveedorDTO;
    }

    @Override
    public Terceros toEntity(ProveedorDTO proveedorDTO) {
        if ( proveedorDTO == null ) {
            return null;
        }

        Terceros terceros = new Terceros();

        if ( proveedorDTO.getId() != null ) {
            terceros.setTerceroId( proveedorDTO.getId().intValue() );
        }
        terceros.setIdentificacion( proveedorDTO.getNit() );
        terceros.setRazonSocial( proveedorDTO.getRazonSocial() );
        terceros.setDireccion( proveedorDTO.getDireccion() );

        return terceros;
    }
}
