-- Manifiestos de Importación
-- Ejecutar manualmente en la base de datos de cada tenant

CREATE TABLE IF NOT EXISTS manifestos_importacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_declaracion VARCHAR(200) NOT NULL,
    fecha_importacion DATE NOT NULL,
    aduana VARCHAR(300),
    pais_origen VARCHAR(100),
    proveedor_internacional VARCHAR(300),
    numero_contenedor VARCHAR(100),
    observaciones TEXT,
    ruta_pdf VARCHAR(500),
    nombre_archivo_original VARCHAR(200),
    estado VARCHAR(50) DEFAULT 'ACTIVO',
    usuario_creacion VARCHAR(100) NOT NULL,
    fecha_creacion DATE NOT NULL,
    orden_compra_id BIGINT,
    CONSTRAINT fk_manifiesto_orden_compra FOREIGN KEY (orden_compra_id)
        REFERENCES ordenes_compra(id) ON DELETE SET NULL
);
