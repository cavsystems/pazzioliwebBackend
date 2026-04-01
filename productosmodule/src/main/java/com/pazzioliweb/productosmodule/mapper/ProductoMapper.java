package com.pazzioliweb.productosmodule.mapper;

import org.springframework.stereotype.Component;

import com.pazzioliweb.commonbacken.entity.Impuestos;
import com.pazzioliweb.productosmodule.dtos.ProductoCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoResponseDTO;
import com.pazzioliweb.productosmodule.entity.Grupos;
import com.pazzioliweb.productosmodule.entity.Lineas;
import com.pazzioliweb.productosmodule.entity.Productos;
import com.pazzioliweb.productosmodule.entity.TipoProducto;
import com.pazzioliweb.usuariosbacken.entity.Usuario;

@Component
public class ProductoMapper {

    public Productos fromCreateDto(
            ProductoCreateDTO dto,
            Grupos grupo,
            Lineas linea,
            Impuestos impuesto,
            Usuario usuario,
            TipoProducto tipoProducto
    ) {
        Productos p = new Productos();
        p.setEstado(dto.getEstado());
        p.setReferencia(dto.getReferencia());
        p.setCodigoContable(dto.getCodigo_contable());
        p.setCodigoBarras(dto.getCodigo_barras());
        p.setDescripcion(dto.getDescripcion());
        p.setCosto(dto.getCosto());
        p.setManifiesto(dto.getManifiesto());
         p.setManejaVariantes(dto.getManejaVariantes());
        p.setGrupo(grupo);
        p.setLinea(linea);
        p.setImpuestos(impuesto);
        p.setUsuario(usuario);
        p.setTipoProducto(tipoProducto);

        return p;
    }

    public ProductoResponseDTO toResponseDto(Productos p) {
        ProductoResponseDTO dto = new ProductoResponseDTO();

        dto.setProductoId(p.getProductoId());
        dto.setReferencia(p.getReferencia());
        dto.setCodigoContable(p.getCodigoContable());
        dto.setCodigoBarras(p.getCodigoBarras());
        dto.setDescripcion(p.getDescripcion());
        dto.setCosto(p.getCosto());
        dto.setManifiesto(p.getManifiesto());

        dto.setGrupo(p.getGrupo().getDescripcion());
        dto.setLinea(p.getLinea().getDescripcion());
        dto.setImpuesto(p.getImpuestos().getNombre());
        dto.setUsuarioCreo(p.getUsuario().getNombre());
        dto.setTipoProducto(p.getTipoProducto().getNombre());

        return dto;
    }
}
