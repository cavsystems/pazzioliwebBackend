package com.pazzioliweb.comprasmodule.mapper;

import com.pazzioliweb.comprasmodule.dtos.ProveedorDTO;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {
    @Mapping(target = "id", source = "terceroId")
    @Mapping(target = "nit", source = "identificacion")
    @Mapping(target = "razonSocial", source = "razonSocial")
    @Mapping(target = "direccion", source = "direccion")
    // Los campos telefono, email y contacto no están directamente en Terceros, se manejan a través de la relación con ContactoTercero
    @Mapping(target = "telefono", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "contacto", ignore = true)
    // No hay un campo 'estado' en Terceros, usamos 'activo' del DTO
    @Mapping(target = "activo", expression = "java(true)")
    ProveedorDTO toDto(Terceros terceros);
    
    @Mapping(target = "terceroId", source = "id")
    @Mapping(target = "identificacion", source = "nit")
    @Mapping(target = "razonSocial", source = "razonSocial")
    @Mapping(target = "direccion", source = "direccion")
    // Ignoramos los campos que no se pueden mapear directamente
    @Mapping(target = "contactos", ignore = true)
    @Mapping(target = "sedes", ignore = true)
    @Mapping(target = "tipoIdentificacion", ignore = true)
    @Mapping(target = "dv", ignore = true)
    @Mapping(target = "nombre1", ignore = true)
    @Mapping(target = "nombre2", ignore = true)
    @Mapping(target = "apellido1", ignore = true)
    @Mapping(target = "apellido2", ignore = true)
    @Mapping(target = "plazo", ignore = true)
    @Mapping(target = "cupo", ignore = true)
    @Mapping(target = "regimen", ignore = true)
    @Mapping(target = "clasificacionTercero", ignore = true)
    @Mapping(target = "precio", ignore = true)
    @Mapping(target = "retenciones", ignore = true)
    @Mapping(target = "fechaNacimiento", ignore = true)
    @Mapping(target = "matriculaMercantil", ignore = true)
    @Mapping(target = "actividadEconomicaId", ignore = true)
    @Mapping(target = "tipoPersona", ignore = true)
    Terceros toEntity(ProveedorDTO proveedorDTO);
}
