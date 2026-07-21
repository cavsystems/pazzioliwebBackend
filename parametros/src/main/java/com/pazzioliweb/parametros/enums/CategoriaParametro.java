package com.pazzioliweb.parametros.enums;

// categoriaParametro es texto libre en la base de datos (columna varchar, no hay
// FK/constraint), así que este enum solo valida las categorías nuevas que llegan por
// ParametroCreateDTO al crear una asignación; la entidad Parametros y las consultas de
// lectura/filtro siguen usando String y no se ven afectadas por este enum.
public enum CategoriaParametro {
    FORMATO,
    GENERALES,
    IMPUESTOS,
    DESCUENTOS,
    GASTOS,
    RETENCIONES
}
