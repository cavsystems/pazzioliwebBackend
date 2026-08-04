package com.pazzioliweb.parametros.dtos;

public class ParametroComprobanteResponseDTO {
    private Integer id;
    private String clave;
    private String nombre;
    private String categoriaparametro;
    private String categoriacomprobante;
    private String valor;
    private Long comprobanteContableId;
    private String prefijo;
    private String label;
    private String valores;

    public ParametroComprobanteResponseDTO(Integer id, String clave, String nombre, String categoriaparametro,
                                           String categoriacomprobante, String valor,
                                           Long comprobanteContableId, String prefijo, String label, String valores) {
        this.id = id;
        this.clave = clave;
        this.nombre = nombre;
        this.categoriaparametro = categoriaparametro;
        this.categoriacomprobante = categoriacomprobante;
        this.valor = valor;
        this.comprobanteContableId = comprobanteContableId;
        this.prefijo = prefijo;
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

    public Long getComprobanteContableId() { return comprobanteContableId; }
    public void setComprobanteContableId(Long comprobanteContableId) { this.comprobanteContableId = comprobanteContableId; }

    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getValores() { return valores; }
    public void setValores(String valores) { this.valores = valores; }
}
