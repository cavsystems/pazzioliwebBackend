package com.pazzioliweb.productosmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarianteDTO {
    private Long productoVarianteId;
    private String sku;
    private String referenciaVariantes;
    private String codigoBarras;
    private BigDecimal precio;
    private Boolean activo;
    private Boolean predeterminada;
    private String imagen;
    private LocalDateTime ultimaFechaVenta;
    private List<CaracteristicaDetalleDTO> caracteristicas;
    private List<ExistenciasBodegaDTO> existencias;
    private List<PreciosProductoVarianteDTO> precios;

}
