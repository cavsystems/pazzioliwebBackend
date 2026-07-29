package com.pazzioliweb.productosmodule.dtos;

/**
 * Proyección usada SOLO por la resolución masiva de productos para importaciones
 * de inventario por plantilla ({@code /variantes/resolver-inventario}).
 *
 * Extiende {@link ProductoInventarioDTO} —así el frontend recibe exactamente los
 * mismos campos que ya pinta en la grilla— y agrega las claves con las que se hizo
 * el match, para que el cliente pueda desambiguar sin adivinar:
 * SKU, referencia de la variante y código de barras del producto.
 */
public interface ProductoInventarioResolverDTO extends ProductoInventarioDTO {

    /** SKU de la variante. */
    String getSku();

    /** Referencia de la variante (p. ej. "S50-1"). */
    String getReferenciaVariantes();

    /** Código de barras del PRODUCTO (el de la variante va en {@code codigobarras}). */
    String getCodigobarrasProducto();

    /** Descripción del producto SIN el sufijo de referencia de variante. */
    String getDescripcionProducto();
}
