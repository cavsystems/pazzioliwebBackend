package com.pazzioliweb.movimientosinventariomodule.dtos;

public class ProductoInfoDto {
    // Variant information
    private Long varianteId;
    private String varianteSku;
    private String varianteReferencia;
    private String varianteCodigoBarras;
    private Boolean varianteActiva;
    private Boolean variantePredeterminada;
    private String varianteImagen;
    
    // Product information
    private Integer productoId;
    private String productoCodigoContable;
    private String productoReferencia;
    private String productoDescripcion;
    private String productoEstado;
    private String productoImagen;

    public Long getVarianteId() {
        return varianteId;
    }

    public void setVarianteId(Long varianteId) {
        this.varianteId = varianteId;
    }

    public String getVarianteSku() {
        return varianteSku;
    }

    public void setVarianteSku(String varianteSku) {
        this.varianteSku = varianteSku;
    }

    public String getVarianteReferencia() {
        return varianteReferencia;
    }

    public void setVarianteReferencia(String varianteReferencia) {
        this.varianteReferencia = varianteReferencia;
    }

    public String getVarianteCodigoBarras() {
        return varianteCodigoBarras;
    }

    public void setVarianteCodigoBarras(String varianteCodigoBarras) {
        this.varianteCodigoBarras = varianteCodigoBarras;
    }

    public Boolean getVarianteActiva() {
        return varianteActiva;
    }

    public void setVarianteActiva(Boolean varianteActiva) {
        this.varianteActiva = varianteActiva;
    }

    public Boolean getVariantePredeterminada() {
        return variantePredeterminada;
    }

    public void setVariantePredeterminada(Boolean variantePredeterminada) {
        this.variantePredeterminada = variantePredeterminada;
    }

    public String getVarianteImagen() {
        return varianteImagen;
    }

    public void setVarianteImagen(String varianteImagen) {
        this.varianteImagen = varianteImagen;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getProductoCodigoContable() {
        return productoCodigoContable;
    }

    public void setProductoCodigoContable(String productoCodigoContable) {
        this.productoCodigoContable = productoCodigoContable;
    }

    public String getProductoReferencia() {
        return productoReferencia;
    }

    public void setProductoReferencia(String productoReferencia) {
        this.productoReferencia = productoReferencia;
    }

    public String getProductoDescripcion() {
        return productoDescripcion;
    }

    public void setProductoDescripcion(String productoDescripcion) {
        this.productoDescripcion = productoDescripcion;
    }

    public String getProductoEstado() {
        return productoEstado;
    }

    public void setProductoEstado(String productoEstado) {
        this.productoEstado = productoEstado;
    }

    public String getProductoImagen() {
        return productoImagen;
    }

    public void setProductoImagen(String productoImagen) {
        this.productoImagen = productoImagen;
    }
}
