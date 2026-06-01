package com.pazzioliweb.comprobantesmodule.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "configuracion_contable_mapa")
public class ConfiguracionContableMapa {

    @Id
    @Column(name = "clave", length = 80)
    private String clave;

    @Column(name = "codigo_cuenta", length = 20, nullable = false)
    private String codigoCuenta;

    @Column(name = "descripcion", length = 200)
    private String descripcion;
}
