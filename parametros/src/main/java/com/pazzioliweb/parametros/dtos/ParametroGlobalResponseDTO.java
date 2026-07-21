package com.pazzioliweb.parametros.dtos;

public class ParametroGlobalResponseDTO {
    private Integer id;
    private String clave;
    private String nombre;
    private String categoriaparametro;
    private String categoriacomprobante;
    private String valor;

    public ParametroGlobalResponseDTO(Integer id, String clave, String nombre, String categoriaparametro, String categoriacomprobante, String valor) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.categoriaparametro = categoriaparametro;
        this.categoriacomprobante = categoriacomprobante;
        this.valor = valor;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoriaparametro() { return categoriaparametro; }
    public void setCategoriaparametro(String categoriaparametro) { this.categoriaparametro = categoriaparametro; }

    public String getCategoriacomprobante() { return categoriacomprobante; }
    public void setCategoriacomprobante(String categoriacomprobante) { this.categoriacomprobante = categoriacomprobante; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}
