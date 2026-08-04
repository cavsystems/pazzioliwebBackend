package com.pazzioliweb.parametros.dtos;

public class ParametroGlobalResponseDTO {
    private Integer id;
    private String clave;
    private String nombre;
    private String categoriaparametro;
    private String categoriacomprobante;
    private String valor;
    private String label;
    private String valores;

    public ParametroGlobalResponseDTO(Integer id, String clave, String nombre, String categoriaparametro, String categoriacomprobante, String valor, String label, String valores) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.categoriaparametro = categoriaparametro;
        this.categoriacomprobante = categoriacomprobante;
        this.valor = valor;
        this.label = label;
        this.valores = valores;
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

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getValores() { return valores; }
    public void setValores(String valores) { this.valores = valores; }
}
