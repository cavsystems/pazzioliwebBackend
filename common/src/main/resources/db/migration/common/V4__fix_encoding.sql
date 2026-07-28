-- Fix encoding issues in existing data
-- This migration fixes double-encoded UTF-8 characters in the database

-- Fix codigospostalesnacionales table
UPDATE codigospostalesnacionales
SET nombreMunicipio = REPLACE(nombreMunicipio, 'ÃƒÂ¡', 'á')
WHERE nombreMunicipio LIKE '%ÃƒÂ¡%';

UPDATE codigospostalesnacionales
SET nombreMunicipio = REPLACE(nombreMunicipio, 'ÃƒÂ©', 'é')
WHERE nombreMunicipio LIKE '%ÃƒÂ©%';

UPDATE codigospostalesnacionales
SET nombreMunicipio = REPLACE(nombreMunicipio, 'ÃƒÂ­', 'í')
WHERE nombreMunicipio LIKE '%ÃƒÂ­%';

UPDATE codigospostalesnacionales
SET nombreMunicipio = REPLACE(nombreMunicipio, 'ÃƒÂ³', 'ó')
WHERE nombreMunicipio LIKE '%ÃƒÂ³%';

UPDATE codigospostalesnacionales
SET nombreMunicipio = REPLACE(nombreMunicipio, 'ÃƒÂº', 'ú')
WHERE nombreMunicipio LIKE '%ÃƒÂº%';

UPDATE codigospostalesnacionales
SET nombreMunicipio = REPLACE(nombreMunicipio, 'ÃƒÂ±', 'ñ')
WHERE nombreMunicipio LIKE '%ÃƒÂ±%';

UPDATE codigospostalesnacionales
SET nombreMunicipio = REPLACE(nombreMunicipio, 'ÃƒÂ¼', 'ü')
WHERE nombreMunicipio LIKE '%ÃƒÂ¼%';

UPDATE codigospostalesnacionales
SET nombreMunicipio = REPLACE(nombreMunicipio, 'ÃƒÂ', 'Ñ')
WHERE nombreMunicipio LIKE '%ÃƒÂ%';

-- Fix actividadeconomica table
UPDATE actividadeconomica
SET descripcionActividad = REPLACE(descripcionActividad, 'ÃƒÂ¡', 'á')
WHERE descripcionActividad LIKE '%ÃƒÂ¡%';

UPDATE actividadeconomica
SET descripcionActividad = REPLACE(descripcionActividad, 'ÃƒÂ©', 'é')
WHERE descripcionActividad LIKE '%ÃƒÂ©%';

UPDATE actividadeconomica
SET descripcionActividad = REPLACE(descripcionActividad, 'ÃƒÂ­', 'í')
WHERE descripcionActividad LIKE '%ÃƒÂ­%';

UPDATE actividadeconomica
SET descripcionActividad = REPLACE(descripcionActividad, 'ÃƒÂ³', 'ó')
WHERE descripcionActividad LIKE '%ÃƒÂ³%';

UPDATE actividadeconomica
SET descripcionActividad = REPLACE(descripcionActividad, 'ÃƒÂº', 'ú')
WHERE descripcionActividad LIKE '%ÃƒÂº%';

UPDATE actividadeconomica
SET descripcionActividad = REPLACE(descripcionActividad, 'ÃƒÂ±', 'ñ')
WHERE descripcionActividad LIKE '%ÃƒÂ±%';

UPDATE actividadeconomica
SET descripcionActividad = REPLACE(descripcionActividad, 'ÃƒÂ¼', 'ü')
WHERE descripcionActividad LIKE '%ÃƒÂ¼%';

UPDATE actividadeconomica
SET descripcionActividad = REPLACE(descripcionActividad, 'ÃƒÂ', 'Ñ')
WHERE descripcionActividad LIKE '%ÃƒÂ%';

-- Fix municipios table
UPDATE municipios
SET municipio = REPLACE(municipio, 'ÃƒÂ¡', 'á')
WHERE municipio LIKE '%ÃƒÂ¡%';

UPDATE municipios
SET municipio = REPLACE(municipio, 'ÃƒÂ©', 'é')
WHERE municipio LIKE '%ÃƒÂ©%';

UPDATE municipios
SET municipio = REPLACE(municipio, 'ÃƒÂ­', 'í')
WHERE municipio LIKE '%ÃƒÂ­%';

UPDATE municipios
SET municipio = REPLACE(municipio, 'ÃƒÂ³', 'ó')
WHERE municipio LIKE '%ÃƒÂ³%';

UPDATE municipios
SET municipio = REPLACE(municipio, 'ÃƒÂº', 'ú')
WHERE municipio LIKE '%ÃƒÂº%';

UPDATE municipios
SET municipio = REPLACE(municipio, 'ÃƒÂ±', 'ñ')
WHERE municipio LIKE '%ÃƒÂ±%';

UPDATE municipios
SET municipio = REPLACE(municipio, 'ÃƒÂ¼', 'ü')
WHERE municipio LIKE '%ÃƒÂ¼%';

UPDATE municipios
SET municipio = REPLACE(municipio, 'ÃƒÂ', 'Ñ')
WHERE municipio LIKE '%ÃƒÂ%';

-- Fix departamentos table
UPDATE departamentos
SET departamento = REPLACE(departamento, 'ÃƒÂ¡', 'á')
WHERE departamento LIKE '%ÃƒÂ¡%';

UPDATE departamentos
SET departamento = REPLACE(departamento, 'ÃƒÂ©', 'é')
WHERE departamento LIKE '%ÃƒÂ©%';

UPDATE departamentos
SET departamento = REPLACE(departamento, 'ÃƒÂ­', 'í')
WHERE departamento LIKE '%ÃƒÂ­%';

UPDATE departamentos
SET departamento = REPLACE(departamento, 'ÃƒÂ³', 'ó')
WHERE departamento LIKE '%ÃƒÂ³%';

UPDATE departamentos
SET departamento = REPLACE(departamento, 'ÃƒÂº', 'ú')
WHERE departamento LIKE '%ÃƒÂº%';

UPDATE departamentos
SET departamento = REPLACE(departamento, 'ÃƒÂ±', 'ñ')
WHERE departamento LIKE '%ÃƒÂ±%';

UPDATE departamentos
SET departamento = REPLACE(departamento, 'ÃƒÂ¼', 'ü')
WHERE departamento LIKE '%ÃƒÂ¼%';

UPDATE departamentos
SET departamento = REPLACE(departamento, 'ÃƒÂ', 'Ñ')
WHERE departamento LIKE '%ÃƒÂ%';

-- Fix terceros table
UPDATE terceros
SET nombre_1 = REPLACE(nombre_1, 'ÃƒÂ¡', 'á')
WHERE nombre_1 LIKE '%ÃƒÂ¡%';

UPDATE terceros
SET nombre_1 = REPLACE(nombre_1, 'ÃƒÂ©', 'é')
WHERE nombre_1 LIKE '%ÃƒÂ©%';

UPDATE terceros
SET nombre_1 = REPLACE(nombre_1, 'ÃƒÂ­', 'í')
WHERE nombre_1 LIKE '%ÃƒÂ­%';

UPDATE terceros
SET nombre_1 = REPLACE(nombre_1, 'ÃƒÂ³', 'ó')
WHERE nombre_1 LIKE '%ÃƒÂ³%';

UPDATE terceros
SET nombre_1 = REPLACE(nombre_1, 'ÃƒÂº', 'ú')
WHERE nombre_1 LIKE '%ÃƒÂº%';

UPDATE terceros
SET nombre_1 = REPLACE(nombre_1, 'ÃƒÂ±', 'ñ')
WHERE nombre_1 LIKE '%ÃƒÂ±%';

UPDATE terceros
SET nombre_1 = REPLACE(nombre_1, 'ÃƒÂ¼', 'ü')
WHERE nombre_1 LIKE '%ÃƒÂ¼%';

UPDATE terceros
SET nombre_1 = REPLACE(nombre_1, 'ÃƒÂ', 'Ñ')
WHERE nombre_1 LIKE '%ÃƒÂ%';

UPDATE terceros
SET nombre_2 = REPLACE(nombre_2, 'ÃƒÂ¡', 'á')
WHERE nombre_2 LIKE '%ÃƒÂ¡%';

UPDATE terceros
SET nombre_2 = REPLACE(nombre_2, 'ÃƒÂ©', 'é')
WHERE nombre_2 LIKE '%ÃƒÂ©%';

UPDATE terceros
SET nombre_2 = REPLACE(nombre_2, 'ÃƒÂ­', 'í')
WHERE nombre_2 LIKE '%ÃƒÂ­%';

UPDATE terceros
SET nombre_2 = REPLACE(nombre_2, 'ÃƒÂ³', 'ó')
WHERE nombre_2 LIKE '%ÃƒÂ³%';

UPDATE terceros
SET nombre_2 = REPLACE(nombre_2, 'ÃƒÂº', 'ú')
WHERE nombre_2 LIKE '%ÃƒÂº%';

UPDATE terceros
SET nombre_2 = REPLACE(nombre_2, 'ÃƒÂ±', 'ñ')
WHERE nombre_2 LIKE '%ÃƒÂ±%';

UPDATE terceros
SET nombre_2 = REPLACE(nombre_2, 'ÃƒÂ¼', 'ü')
WHERE nombre_2 LIKE '%ÃƒÂ¼%';

UPDATE terceros
SET nombre_2 = REPLACE(nombre_2, 'ÃƒÂ', 'Ñ')
WHERE nombre_2 LIKE '%ÃƒÂ%';

UPDATE terceros
SET apellido_1 = REPLACE(apellido_1, 'ÃƒÂ¡', 'á')
WHERE apellido_1 LIKE '%ÃƒÂ¡%';

UPDATE terceros
SET apellido_1 = REPLACE(apellido_1, 'ÃƒÂ©', 'é')
WHERE apellido_1 LIKE '%ÃƒÂ©%';

UPDATE terceros
SET apellido_1 = REPLACE(apellido_1, 'ÃƒÂ­', 'í')
WHERE apellido_1 LIKE '%ÃƒÂ­%';

UPDATE terceros
SET apellido_1 = REPLACE(apellido_1, 'ÃƒÂ³', 'ó')
WHERE apellido_1 LIKE '%ÃƒÂ³%';

UPDATE terceros
SET apellido_1 = REPLACE(apellido_1, 'ÃƒÂº', 'ú')
WHERE apellido_1 LIKE '%ÃƒÂº%';

UPDATE terceros
SET apellido_1 = REPLACE(apellido_1, 'ÃƒÂ±', 'ñ')
WHERE apellido_1 LIKE '%ÃƒÂ±%';

UPDATE terceros
SET apellido_1 = REPLACE(apellido_1, 'ÃƒÂ¼', 'ü')
WHERE apellido_1 LIKE '%ÃƒÂ¼%';

UPDATE terceros
SET apellido_1 = REPLACE(apellido_1, 'ÃƒÂ', 'Ñ')
WHERE apellido_1 LIKE '%ÃƒÂ%';

UPDATE terceros
SET apellido_2 = REPLACE(apellido_2, 'ÃƒÂ¡', 'á')
WHERE apellido_2 LIKE '%ÃƒÂ¡%';

UPDATE terceros
SET apellido_2 = REPLACE(apellido_2, 'ÃƒÂ©', 'é')
WHERE apellido_2 LIKE '%ÃƒÂ©%';

UPDATE terceros
SET apellido_2 = REPLACE(apellido_2, 'ÃƒÂ­', 'í')
WHERE apellido_2 LIKE '%ÃƒÂ­%';

UPDATE terceros
SET apellido_2 = REPLACE(apellido_2, 'ÃƒÂ³', 'ó')
WHERE apellido_2 LIKE '%ÃƒÂ³%';

UPDATE terceros
SET apellido_2 = REPLACE(apellido_2, 'ÃƒÂº', 'ú')
WHERE apellido_2 LIKE '%ÃƒÂº%';

UPDATE terceros
SET apellido_2 = REPLACE(apellido_2, 'ÃƒÂ±', 'ñ')
WHERE apellido_2 LIKE '%ÃƒÂ±%';

UPDATE terceros
SET apellido_2 = REPLACE(apellido_2, 'ÃƒÂ¼', 'ü')
WHERE apellido_2 LIKE '%ÃƒÂ¼%';

UPDATE terceros
SET apellido_2 = REPLACE(apellido_2, 'ÃƒÂ', 'Ñ')
WHERE apellido_2 LIKE '%ÃƒÂ%';

UPDATE terceros
SET razon_social = REPLACE(razon_social, 'ÃƒÂ¡', 'á')
WHERE razon_social LIKE '%ÃƒÂ¡%';

UPDATE terceros
SET razon_social = REPLACE(razon_social, 'ÃƒÂ©', 'é')
WHERE razon_social LIKE '%ÃƒÂ©%';

UPDATE terceros
SET razon_social = REPLACE(razon_social, 'ÃƒÂ­', 'í')
WHERE razon_social LIKE '%ÃƒÂ­%';

UPDATE terceros
SET razon_social = REPLACE(razon_social, 'ÃƒÂ³', 'ó')
WHERE razon_social LIKE '%ÃƒÂ³%';

UPDATE terceros
SET razon_social = REPLACE(razon_social, 'ÃƒÂº', 'ú')
WHERE razon_social LIKE '%ÃƒÂº%';

UPDATE terceros
SET razon_social = REPLACE(razon_social, 'ÃƒÂ±', 'ñ')
WHERE razon_social LIKE '%ÃƒÂ±%';

UPDATE terceros
SET razon_social = REPLACE(razon_social, 'ÃƒÂ¼', 'ü')
WHERE razon_social LIKE '%ÃƒÂ¼%';

UPDATE terceros
SET razon_social = REPLACE(razon_social, 'ÃƒÂ', 'Ñ')
WHERE razon_social LIKE '%ÃƒÂ%';

UPDATE terceros
SET direccion = REPLACE(direccion, 'ÃƒÂ¡', 'á')
WHERE direccion LIKE '%ÃƒÂ¡%';

UPDATE terceros
SET direccion = REPLACE(direccion, 'ÃƒÂ©', 'é')
WHERE direccion LIKE '%ÃƒÂ©%';

UPDATE terceros
SET direccion = REPLACE(direccion, 'ÃƒÂ­', 'í')
WHERE direccion LIKE '%ÃƒÂ­%';

UPDATE terceros
SET direccion = REPLACE(direccion, 'ÃƒÂ³', 'ó')
WHERE direccion LIKE '%ÃƒÂ³%';

UPDATE terceros
SET direccion = REPLACE(direccion, 'ÃƒÂº', 'ú')
WHERE direccion LIKE '%ÃƒÂº%';

UPDATE terceros
SET direccion = REPLACE(direccion, 'ÃƒÂ±', 'ñ')
WHERE direccion LIKE '%ÃƒÂ±%';

UPDATE terceros
SET direccion = REPLACE(direccion, 'ÃƒÂ¼', 'ü')
WHERE direccion LIKE '%ÃƒÂ¼%';

UPDATE terceros
SET direccion = REPLACE(direccion, 'ÃƒÂ', 'Ñ')
WHERE direccion LIKE '%ÃƒÂ%';
