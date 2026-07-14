package com.pazzioliweb.parametros.entity;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import jakarta.persistence.*;

@Entity
@Table(name = "parametroscomprobantes")
public class Parametroscomprobantes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parametroid",nullable = false)
    private Parametros parametros;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprobanteContableid",nullable = false)
    private ComprobanteContable comprobanteContable;

    @Column(name = "valor", nullable = false)
    private String valor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Parametros getParametros() {
        return parametros;
    }

    public void setParametros(Parametros parametros) {
        this.parametros = parametros;
    }

    public ComprobanteContable getComprobanteContable() {
        return comprobanteContable;
    }

    public void setComprobanteContable(ComprobanteContable comprobanteContable) {
        this.comprobanteContable = comprobanteContable;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
