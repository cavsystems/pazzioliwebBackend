-- Migración V1: Estructura de tablas para el esquema plantilla
-- Este esquema se clonará para cada nueva empresa/tenant

-- Tablas de catálogos de referencia
CREATE TABLE IF NOT EXISTS actividadeconomica (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    descripcionActividad TEXT
);

CREATE TABLE IF NOT EXISTS departamentos (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigopais INT,
    codigo INT,
    nombre VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS municipios (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigo INT,
    codigodepartamento INT,
    nombre VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS paises (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigo INT,
    nombre VARCHAR(100),
    sigla VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS codigospostalesnacionales (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigo INT,
    municipio VARCHAR(100),
    codigo INT,
    codigo_postal VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS regimen (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoRegimen VARCHAR(20),
    descripcion VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS tipoidentificacion (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoTipoIdentificacion INT,
    tipoIdentificacion VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS tipopersona (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS tipo_producto (
    tipo_producto_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    descripcion VARCHAR(100),
    estado TINYINT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tipo_caracteristica (
    tipo_caracteristica_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS caracteristicas (
    caracteristica_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_caracteristica_id BIGINT,
    nombre VARCHAR(100),
    valor VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (tipo_caracteristica_id) REFERENCES tipo_caracteristica(tipo_caracteristica_id)
);

CREATE TABLE IF NOT EXISTS tipo_contacto (
    tipo_contacto_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    descripcion VARCHAR(150),
    activo TINYINT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS tipo_totales (
    tipo_total_id INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100),
    sigla VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    tipo VARCHAR(20) DEFAULT 'IMPUESTO'
);

CREATE TABLE IF NOT EXISTS unidades_medida (
    unidad_medida_id INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100),
    sigla VARCHAR(10),
    valorsigla VARCHAR(10)
);

-- Tablas de usuarios y seguridad
CREATE TABLE IF NOT EXISTS roles (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS permisos (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS permisos_roles (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigopermiso INT,
    codigorol INT,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    codigousuariocreado INT DEFAULT 0,
    fechacreado DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuarios (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    usuario VARCHAR(300) NOT NULL UNIQUE,
    contrasena TEXT NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    codigorol INT DEFAULT 0,
    codigousuariocreado INT DEFAULT 0,
    fechacreado DATE DEFAULT (CURDATE()),
    fechamodificado DATE DEFAULT '1990-06-06',
    avatar LONGBLOB,
    avatar_tipo VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS sesiones (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigousuario INT NOT NULL,
    fechainicio DATETIME DEFAULT CURRENT_TIMESTAMP,
    fechafin DATETIME DEFAULT '1990-06-06 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
    ipclient VARCHAR(14),
    token VARCHAR(300),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

-- Tablas de productos e inventario
CREATE TABLE IF NOT EXISTS grupos (
    grupo_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS lineas (
    linea_id INT AUTO_INCREMENT PRIMARY KEY,
    grupo_id INT,
    nombre VARCHAR(100),
    descripcion VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (grupo_id) REFERENCES grupos(grupo_id)
);

CREATE TABLE IF NOT EXISTS productos (
    producto_id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_contable VARCHAR(50),
    codigo_barras VARCHAR(50),
    referencia VARCHAR(50),
    descripcion VARCHAR(100),
    costo DECIMAL(10,2) DEFAULT 0.00,
    impuesto_id INT,
    linea_id INT,
    grupo_id INT,
    tipo_producto_id INT,
    usuario_creo_id INT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    codigo_usuario_modifico INT,
    fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_ultima_venta DATETIME,
    fecha_ultima_compra DATETIME,
    manifiesto VARCHAR(100),
    maneja_variantes TINYINT,
    imagen LONGTEXT,
    FOREIGN KEY (impuesto_id) REFERENCES impuestos(codigo),
    FOREIGN KEY (linea_id) REFERENCES lineas(linea_id),
    FOREIGN KEY (grupo_id) REFERENCES grupos(grupo_id)
);

CREATE TABLE IF NOT EXISTS producto_variantes (
    producto_variantes_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT NOT NULL,
    sku VARCHAR(120),
    referencia_variantes VARCHAR(200),
    precio DECIMAL(18,2),
    codigo_barras VARCHAR(120),
    activo TINYINT DEFAULT 1,
    predeterminada TINYINT,
    imagen LONGTEXT,
    ultima_fecha_venta TIMESTAMP,
    FOREIGN KEY (producto_id) REFERENCES productos(producto_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS producto_variantes_detalle (
    producto_variantes_detalle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_variantes_id BIGINT NOT NULL,
    caracteristica_id BIGINT NOT NULL,
    FOREIGN KEY (producto_variantes_id) REFERENCES producto_variantes(producto_variantes_id) ON DELETE CASCADE,
    FOREIGN KEY (caracteristica_id) REFERENCES caracteristicas(caracteristica_id)
);

CREATE TABLE IF NOT EXISTS precios (
    precio_id INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS precios_producto_variante (
    precios_producto_id INT AUTO_INCREMENT PRIMARY KEY,
    producto_variantes_id BIGINT NOT NULL,
    precio_id INT NOT NULL,
    valor DECIMAL(10,2) DEFAULT 0.00,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fecha_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_fin DATETIME DEFAULT CURRENT_TIMESTAMP,
    predeterminada TINYINT DEFAULT 0,
    UNIQUE KEY uq_precio_variante (precio_id, producto_variantes_id),
    FOREIGN KEY (producto_variantes_id) REFERENCES producto_variantes(producto_variantes_id),
    FOREIGN KEY (precio_id) REFERENCES precios(precio_id)
);

CREATE TABLE IF NOT EXISTS bodegas (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    direccion VARCHAR(200),
    telefono VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS existencias (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoproducto INT NOT NULL,
    codigobodega INT NOT NULL,
    cantidad DECIMAL(18,3) DEFAULT 0,
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id),
    FOREIGN KEY (codigobodega) REFERENCES bodegas(codigo)
);

CREATE TABLE IF NOT EXISTS unidades_medida_producto (
    producto_id INT NOT NULL,
    unidad_medida_id INT NOT NULL,
    PRIMARY KEY (producto_id, unidad_medida_id),
    FOREIGN KEY (producto_id) REFERENCES productos(producto_id),
    FOREIGN KEY (unidad_medida_id) REFERENCES unidades_medida(unidad_medida_id)
);

-- Tablas de terceros
CREATE TABLE IF NOT EXISTS clasificaciones_terceros (
    clasificacion_tercero_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS terceros (
    tercero_id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_identificacion_id INT NOT NULL,
    identificacion VARCHAR(100) NOT NULL,
    dv VARCHAR(20),
    nombre_1 VARCHAR(100),
    nombre_2 VARCHAR(100),
    apellido_1 VARCHAR(100),
    apellido_2 VARCHAR(100),
    razon_social VARCHAR(100),
    regimen_id INT NOT NULL,
    clasificacion_tercero_id INT NOT NULL,
    direccion VARCHAR(100),
    departamento_id INT,
    ciudad_id INT,
    codigo_postal VARCHAR(20),
    correo VARCHAR(100),
    plazo INT DEFAULT 0,
    cupo INT DEFAULT 0,
    precio_id INT DEFAULT 0,
    fecha_nacimiento VARCHAR(45),
    matricula_mercantil VARCHAR(45),
    actividad_economica_id INT DEFAULT 0,
    tipo_persona_id INT,
    ultimo_movimiento TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    saldofavor_cliente DOUBLE DEFAULT 0,
    saldofavor_empresa DOUBLE DEFAULT 0,
    FOREIGN KEY (regimen_id) REFERENCES regimen(codigo),
    FOREIGN KEY (clasificacion_tercero_id) REFERENCES clasificaciones_terceros(clasificacion_tercero_id),
    FOREIGN KEY (precio_id) REFERENCES precios(precio_id),
    FOREIGN KEY (departamento_id) REFERENCES departamentos(codigo),
    FOREIGN KEY (ciudad_id) REFERENCES municipios(codigo)
);

CREATE TABLE IF NOT EXISTS sedes_tercero (
    sede_id INT AUTO_INCREMENT PRIMARY KEY,
    tercero_id INT NOT NULL,
    nombre_sede VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(50),
    departamento_id INT,
    municipio_id INT,
    principal TINYINT DEFAULT 0,
    activo TINYINT DEFAULT 1,
    FOREIGN KEY (tercero_id) REFERENCES terceros(tercero_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (departamento_id) REFERENCES departamentos(codigo) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (municipio_id) REFERENCES municipios(codigo) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Tablas de contabilidad
CREATE TABLE IF NOT EXISTS categoriascomprobantes (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS comprobantes (
    comprobante_id INT AUTO_INCREMENT PRIMARY KEY,
    codigocategoria INT,
    prefijo VARCHAR(10),
    nombre VARCHAR(100),
    consecutivo_actual INT DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigocategoria) REFERENCES categoriascomprobantes(codigo)
);

CREATE TABLE IF NOT EXISTS cuentas_contables (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigo_cuenta VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    naturaleza VARCHAR(20) NOT NULL,
    nivel INT NOT NULL,
    codigopadre INT,
    requiere_centro_costo BOOLEAN DEFAULT FALSE,
    cuenta_descuento_concedido BOOLEAN DEFAULT FALSE,
    cuenta_deducciones_recaudo BOOLEAN DEFAULT FALSE,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS periodos_contables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    anio INT NOT NULL,
    mes INT NOT NULL,
    estado VARCHAR(20) DEFAULT 'ABIERTO',
    fecha_cierre DATETIME,
    usuario_cierre VARCHAR(80),
    observaciones VARCHAR(500),
    fecha_creacion DATETIME NOT NULL,
    UNIQUE KEY uq_periodo_anio_mes (anio, mes)
);

CREATE TABLE IF NOT EXISTS comprobantes_contables (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigocomprobante INT NOT NULL,
    numerocomprobante VARCHAR(50) NOT NULL,
    fecha DATE NOT NULL,
    descripcion TEXT,
    codigoperiodo INT NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigocomprobante) REFERENCES comprobantes(comprobante_id),
    FOREIGN KEY (codigoperiodo) REFERENCES periodos_contables(id)
);

CREATE TABLE IF NOT EXISTS asientos_contables (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigocomprobantecontable INT NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigocomprobantecontable) REFERENCES comprobantes_contables(codigo)
);

CREATE TABLE IF NOT EXISTS asientos_contables_lineas (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigasiento INT NOT NULL,
    codigocuenta INT NOT NULL,
    debito DECIMAL(18,2) DEFAULT 0,
    credito DECIMAL(18,2) DEFAULT 0,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigasiento) REFERENCES asientos_contables(codigo),
    FOREIGN KEY (codigocuenta) REFERENCES cuentas_contables(codigo)
);

CREATE TABLE IF NOT EXISTS centrocosto (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigo_centro VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    codigopadre INT,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS tipos_comprobante_manual (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    prefijo VARCHAR(20),
    siguiente_consecutivo INT DEFAULT 1,
    estado VARCHAR(15) DEFAULT 'ACTIVO',
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_tipo_comprobante_manual_codigo (codigo)
);

CREATE TABLE IF NOT EXISTS notas_estados_financieros (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigo_nota VARCHAR(50) NOT NULL UNIQUE,
    titulo VARCHAR(255) NOT NULL,
    contenido TEXT,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS activos_fijos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(60),
    fecha_adquisicion DATE NOT NULL,
    fecha_inicio_depreciacion DATE NOT NULL,
    valor_adquisicion DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    valor_residual DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    vida_util_meses INT NOT NULL,
    valor_actual DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS depreciacion_detalle (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoactivofijo BIGINT NOT NULL,
    ano INT NOT NULL,
    mes INT NOT NULL,
    valor_depreciacion DECIMAL(18,2),
    depreciacion_acumulada DECIMAL(18,2),
    valor_neto_libros DECIMAL(18,2),
    FOREIGN KEY (codigoactivofijo) REFERENCES activos_fijos(id),
    UNIQUE KEY uk_activo_ano_mes (codigoactivofijo, ano, mes)
);

CREATE TABLE IF NOT EXISTS auditoria_mantenimiento (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    tabla VARCHAR(100) NOT NULL,
    registro_id INT NOT NULL,
    accion VARCHAR(50) NOT NULL,
    usuario VARCHAR(100),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valores_anteriores TEXT,
    valores_nuevos TEXT
);

CREATE TABLE IF NOT EXISTS asientos_fallidos (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigocomprobantecontable INT,
    descripcion TEXT,
    error_mensaje TEXT,
    fecha_error TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (codigocomprobantecontable) REFERENCES comprobantes_contables(codigo)
);

CREATE TABLE IF NOT EXISTS configuracion_contable_mapa (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    modulo VARCHAR(50) NOT NULL,
    operacion VARCHAR(100) NOT NULL,
    codigocuenta_debito INT,
    codigocuenta_credito INT,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigocuenta_debito) REFERENCES cuentas_contables(codigo),
    FOREIGN KEY (codigocuenta_credito) REFERENCES cuentas_contables(codigo)
);

CREATE TABLE IF NOT EXISTS conceptoabierto (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

-- Tablas de cajas
CREATE TABLE IF NOT EXISTS cajas (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS cajeros (
    cajero_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS detalle_cajero (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    cajero_id INT NOT NULL,
    tipo_movimiento VARCHAR(50) NOT NULL,
    valor DECIMAL(18,2) NOT NULL,
    descripcion TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (cajero_id) REFERENCES cajeros(cajero_id)
);

CREATE TABLE IF NOT EXISTS movimiento_cajero (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    cajero_id INT NOT NULL,
    fecha_apertura TIMESTAMP,
    fecha_cierre TIMESTAMP,
    saldo_inicial DECIMAL(18,2) DEFAULT 0,
    saldo_final DECIMAL(18,2) DEFAULT 0,
    total_ventas DECIMAL(18,2) DEFAULT 0,
    total_devoluciones DECIMAL(18,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ABIERTO',
    FOREIGN KEY (cajero_id) REFERENCES cajeros(cajero_id)
);

-- Tablas de vendedores
CREATE TABLE IF NOT EXISTS vendedores (
    vendedor_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    direccion VARCHAR(50) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    codigo_usuario_creo INT DEFAULT 0,
    fechacreado DATE DEFAULT (CURDATE()),
    comision DECIMAL(10,0) DEFAULT 0,
    correo VARCHAR(100),
    tipo_vendedor VARCHAR(20),
    identificacion VARCHAR(50),
    meta_ventas DOUBLE,
    bodega_id INT,
    FOREIGN KEY (bodega_id) REFERENCES bodegas(codigo)
);

CREATE TABLE IF NOT EXISTS usuarios_vendedor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendedor_id INT,
    usuario_id INT,
    FOREIGN KEY (vendedor_id) REFERENCES vendedores(vendedor_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(codigo)
);

-- Tablas de métodos de pago
CREATE TABLE IF NOT EXISTS metodos_pago (
    metodo_pago_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS cuentas_bancarias (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    banco VARCHAR(100) NOT NULL,
    tipo_cuenta VARCHAR(50) NOT NULL,
    numero_cuenta VARCHAR(50) NOT NULL,
    codigometodopago INT,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigometodopago) REFERENCES metodos_pago(metodo_pago_id)
);

-- Tablas de facturación
CREATE TABLE IF NOT EXISTS facturas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    tercero_id INT NOT NULL,
    fecha DATE NOT NULL,
    subtotal DECIMAL(18,2) DEFAULT 0,
    total_iva DECIMAL(18,2) DEFAULT 0,
    total_descuento DECIMAL(18,2) DEFAULT 0,
    total DECIMAL(18,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (tercero_id) REFERENCES terceros(tercero_id)
);

CREATE TABLE IF NOT EXISTS tipo_totales_facturas (
    tipo_total_factura_id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_total_id INT NOT NULL,
    factura_id INT NOT NULL,
    base DECIMAL(10,2) DEFAULT 0.00,
    valor DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (tipo_total_id) REFERENCES tipo_totales(tipo_total_id),
    FOREIGN KEY (factura_id) REFERENCES facturas(id)
);

CREATE TABLE IF NOT EXISTS metodos_pago_facturas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    factura_id BIGINT NOT NULL,
    metodo_pago_id INT NOT NULL,
    monto DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (factura_id) REFERENCES facturas(id),
    FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(metodo_pago_id)
);

CREATE TABLE IF NOT EXISTS documentos_electronicos (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigofactura BIGINT NOT NULL,
    cufe VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    fecha_envio TIMESTAMP,
    fecha_acuse TIMESTAMP,
    estado_dian VARCHAR(50),
    mensaje_dian TEXT,
    FOREIGN KEY (codigofactura) REFERENCES facturas(id)
);

-- Tablas de compras
CREATE TABLE IF NOT EXISTS ordenes_compra (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_orden VARCHAR(50) NOT NULL UNIQUE,
    tercero_id INT NOT NULL,
    fecha DATE NOT NULL,
    subtotal DECIMAL(18,2) DEFAULT 0,
    total_iva DECIMAL(18,2) DEFAULT 0,
    total_descuento DECIMAL(18,2) DEFAULT 0,
    total DECIMAL(18,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (tercero_id) REFERENCES terceros(tercero_id)
);

CREATE TABLE IF NOT EXISTS detalles_orden_compra (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoordencompra BIGINT NOT NULL,
    codigoproducto INT NOT NULL,
    cantidad DECIMAL(18,3) NOT NULL,
    precio_unitario DECIMAL(18,2) NOT NULL,
    subtotal DECIMAL(18,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigoordencompra) REFERENCES ordenes_compra(id),
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id)
);

CREATE TABLE IF NOT EXISTS orden_compra_metodos_pago (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoordencompra BIGINT NOT NULL,
    codigometodopago INT NOT NULL,
    valor DECIMAL(18,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigoordencompra) REFERENCES ordenes_compra(id),
    FOREIGN KEY (codigometodopago) REFERENCES metodos_pago(metodo_pago_id)
);

CREATE TABLE IF NOT EXISTS cuentas_por_pagar (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoordencompra BIGINT,
    codigotercero INT NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    valor_total DECIMAL(18,2) NOT NULL,
    valor_pagado DECIMAL(18,2) DEFAULT 0,
    saldo_pendiente DECIMAL(18,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    FOREIGN KEY (codigoordencompra) REFERENCES ordenes_compra(id),
    FOREIGN KEY (codigotercero) REFERENCES terceros(tercero_id)
);

CREATE TABLE IF NOT EXISTS legalizaciones (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigocuentaspagar INT NOT NULL,
    fecha_legalizacion DATE NOT NULL,
    valor_legalizado DECIMAL(18,2) NOT NULL,
    observaciones TEXT,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigocuentaspagar) REFERENCES cuentas_por_pagar(codigo)
);

CREATE TABLE IF NOT EXISTS configuracion_compras (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    parametro VARCHAR(100) NOT NULL UNIQUE,
    valor VARCHAR(255),
    descripcion VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS manifestoimportacion (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    numero_manifiesto VARCHAR(50) NOT NULL,
    fecha_importacion DATE NOT NULL,
    pais_origen VARCHAR(100),
    puerto_embarque VARCHAR(100),
    puerto_arribo VARCHAR(100),
    archivo_ruta VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS devoluciones_compra (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    numero_devolucion VARCHAR(50) NOT NULL UNIQUE,
    codigoordencompra BIGINT NOT NULL,
    fecha DATE NOT NULL,
    subtotal DECIMAL(18,2) DEFAULT 0,
    total_iva DECIMAL(18,2) DEFAULT 0,
    total_descuento DECIMAL(18,2) DEFAULT 0,
    total DECIMAL(18,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigoordencompra) REFERENCES ordenes_compra(id)
);

CREATE TABLE IF NOT EXISTS detalles_devolucion_compra (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigodevolucioncompra INT NOT NULL,
    codigoproducto INT NOT NULL,
    cantidad DECIMAL(18,3) NOT NULL,
    precio_unitario DECIMAL(18,2) NOT NULL,
    subtotal DECIMAL(18,2) NOT NULL,
    costo_devolucion DECIMAL(18,2),
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigodevolucioncompra) REFERENCES devoluciones_compra(codigo),
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id)
);

-- Tablas de ventas
CREATE TABLE IF NOT EXISTS ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_venta VARCHAR(50) NOT NULL,
    cliente_id INT NOT NULL,
    bodega_id INT NOT NULL,
    cajero_id INT,
    fecha_emision DATE NOT NULL,
    fecha_entrega_esperada DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    observaciones TEXT,
    gravada DECIMAL(18,2) DEFAULT 0.00,
    iva DECIMAL(18,2) DEFAULT 0.00,
    descuentos DECIMAL(18,2) DEFAULT 0.00,
    total_venta DECIMAL(18,2) DEFAULT 0.00,
    usuario_creacion VARCHAR(100) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    vendedor_id INT,
    comprobante_id BIGINT,
    consecutivo_comprobante INT,
    retefuente DECIMAL(18,2) DEFAULT 0.00,
    reteiva DECIMAL(18,2) DEFAULT 0.00,
    reteica DECIMAL(18,2) DEFAULT 0.00,
    FOREIGN KEY (cliente_id) REFERENCES terceros(tercero_id),
    FOREIGN KEY (bodega_id) REFERENCES bodegas(codigo),
    FOREIGN KEY (cajero_id) REFERENCES cajeros(cajero_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (vendedor_id) REFERENCES vendedores(vendedor_id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS detalles_venta (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoventa BIGINT NOT NULL,
    codigoproducto INT NOT NULL,
    cantidad DECIMAL(18,3) NOT NULL,
    precio_unitario DECIMAL(18,2) NOT NULL,
    subtotal DECIMAL(18,2) NOT NULL,
    costo_venta DECIMAL(18,2),
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigoventa) REFERENCES ventas(id),
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id)
);

CREATE TABLE IF NOT EXISTS venta_metodos_pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    metodo_pago_id INT NOT NULL,
    monto DECIMAL(18,2) NOT NULL,
    plazo_en_dias INT,
    referencia VARCHAR(255),
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(metodo_pago_id)
);

CREATE TABLE IF NOT EXISTS cuentas_por_cobrar (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoventa BIGINT,
    codigotercero INT NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    valor_total DECIMAL(18,2) NOT NULL,
    valor_pagado DECIMAL(18,2) DEFAULT 0,
    saldo_pendiente DECIMAL(18,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    FOREIGN KEY (codigoventa) REFERENCES ventas(id),
    FOREIGN KEY (codigotercero) REFERENCES terceros(tercero_id)
);

CREATE TABLE IF NOT EXISTS cotizaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cotizacion VARCHAR(50) NOT NULL UNIQUE,
    tercero_id INT NOT NULL,
    fecha DATE NOT NULL,
    subtotal DECIMAL(18,2) DEFAULT 0,
    total_iva DECIMAL(18,2) DEFAULT 0,
    total_descuento DECIMAL(18,2) DEFAULT 0,
    total DECIMAL(18,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (tercero_id) REFERENCES terceros(tercero_id)
);

CREATE TABLE IF NOT EXISTS detalles_cotizacion (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigocotizacion BIGINT NOT NULL,
    codigoproducto INT NOT NULL,
    cantidad DECIMAL(18,3) NOT NULL,
    precio_unitario DECIMAL(18,2) NOT NULL,
    subtotal DECIMAL(18,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigocotizacion) REFERENCES cotizaciones(id),
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id)
);

CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido VARCHAR(50) NOT NULL UNIQUE,
    cliente_id INT NOT NULL,
    bodega_id INT NOT NULL,
    cajero_id INT,
    cotizacion_id BIGINT,
    fecha DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    observaciones TEXT,
    gravada DECIMAL(18,2) DEFAULT 0.00,
    iva DECIMAL(18,2) DEFAULT 0.00,
    descuentos DECIMAL(18,2) DEFAULT 0.00,
    total_venta DECIMAL(18,2) DEFAULT 0.00,
    usuario_creacion VARCHAR(100) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    vendedor_id INT,
    comprobante_id BIGINT,
    consecutivo_comprobante INT,
    retefuente DECIMAL(18,2) DEFAULT 0.00,
    reteiva DECIMAL(18,2) DEFAULT 0.00,
    reteica DECIMAL(18,2) DEFAULT 0.00,
    FOREIGN KEY (cliente_id) REFERENCES terceros(tercero_id),
    FOREIGN KEY (bodega_id) REFERENCES bodegas(codigo),
    FOREIGN KEY (cajero_id) REFERENCES cajeros(cajero_id),
    FOREIGN KEY (vendedor_id) REFERENCES vendedores(vendedor_id),
    FOREIGN KEY (cotizacion_id) REFERENCES cotizaciones(id)
);

CREATE TABLE IF NOT EXISTS detalles_pedido (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigopedido BIGINT NOT NULL,
    codigoproducto INT NOT NULL,
    cantidad DECIMAL(18,3) NOT NULL,
    precio_unitario DECIMAL(18,2) NOT NULL,
    subtotal DECIMAL(18,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigopedido) REFERENCES pedidos(id),
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id)
);

CREATE TABLE IF NOT EXISTS devoluciones_venta (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    numero_devolucion VARCHAR(50) NOT NULL UNIQUE,
    codigoventa BIGINT NOT NULL,
    fecha DATE NOT NULL,
    subtotal DECIMAL(18,2) DEFAULT 0,
    total_iva DECIMAL(18,2) DEFAULT 0,
    total_descuento DECIMAL(18,2) DEFAULT 0,
    total DECIMAL(18,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigoventa) REFERENCES ventas(id)
);

CREATE TABLE IF NOT EXISTS detalles_devolucion_venta (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigodevolucion INT NOT NULL,
    codigoproducto INT NOT NULL,
    cantidad DECIMAL(18,3) NOT NULL,
    precio_unitario DECIMAL(18,2) NOT NULL,
    subtotal DECIMAL(18,2) NOT NULL,
    costo_devolucion DECIMAL(18,2),
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigodevolucion) REFERENCES devoluciones_venta(codigo),
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id)
);

-- Tablas de movimiento de inventario
CREATE TABLE IF NOT EXISTS kardex (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigoproducto INT NOT NULL,
    codigobodega INT NOT NULL,
    fecha DATE NOT NULL,
    tipo_movimiento VARCHAR(50) NOT NULL,
    cantidad DECIMAL(18,3) NOT NULL,
    valor_unitario DECIMAL(18,2) NOT NULL,
    valor_total DECIMAL(18,2) NOT NULL,
    saldo_cantidad DECIMAL(18,3) NOT NULL,
    saldo_valor DECIMAL(18,2) NOT NULL,
    documento_referencia VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id),
    FOREIGN KEY (codigobodega) REFERENCES bodegas(codigo)
);

CREATE TABLE IF NOT EXISTS movimientos_inventario (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    numero_movimiento VARCHAR(50) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    tipo_movimiento VARCHAR(50) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS movimientos_inventario_detalles (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigomovimiento INT NOT NULL,
    codigoproducto INT NOT NULL,
    codigobodega INT NOT NULL,
    cantidad DECIMAL(18,3) NOT NULL,
    costo_unitario DECIMAL(18,2) NOT NULL,
    costo_total DECIMAL(18,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigomovimiento) REFERENCES movimientos_inventario(codigo),
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id),
    FOREIGN KEY (codigobodega) REFERENCES bodegas(codigo)
);

-- Tablas de tesorería
CREATE TABLE IF NOT EXISTS comprobantes_egreso (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    numero_comprobante VARCHAR(50) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    codigotercero INT,
    subtotal DECIMAL(18,2) DEFAULT 0,
    total DECIMAL(18,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigotercero) REFERENCES terceros(tercero_id)
);

CREATE TABLE IF NOT EXISTS detalle_comprobante_egreso (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigocomprobanteegreso INT NOT NULL,
    codigocuenta INT NOT NULL,
    valor DECIMAL(18,2) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigocomprobanteegreso) REFERENCES comprobantes_egreso(codigo),
    FOREIGN KEY (codigocuenta) REFERENCES cuentas_contables(codigo)
);

CREATE TABLE IF NOT EXISTS comprobante_egreso_medio_pago (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigocomprobanteegreso INT NOT NULL,
    codigometodopago INT NOT NULL,
    valor DECIMAL(18,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigocomprobanteegreso) REFERENCES comprobantes_egreso(codigo),
    FOREIGN KEY (codigometodopago) REFERENCES metodos_pago(metodo_pago_id)
);

CREATE TABLE IF NOT EXISTS recibos_caja (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consecutivo INT NOT NULL,
    tercero_id INT,
    tercero_nombre VARCHAR(200),
    tercero_nit VARCHAR(50),
    fecha DATE NOT NULL,
    subtotal DECIMAL(15,2) DEFAULT 0.00,
    retefuente DECIMAL(15,2) DEFAULT 0.00,
    reteica DECIMAL(15,2) DEFAULT 0.00,
    reteiva DECIMAL(15,2) DEFAULT 0.00,
    descuento DECIMAL(15,2) DEFAULT 0.00,
    averias DECIMAL(15,2) DEFAULT 0.00,
    fletes DECIMAL(15,2) DEFAULT 0.00,
    total DECIMAL(15,2) DEFAULT 0.00,
    metodo_pago_id INT,
    concepto TEXT,
    centro_costo VARCHAR(100),
    estado VARCHAR(30) DEFAULT 'ACTIVO',
    usuario_id INT,
    cajero_id INT,
    vendedor_id INT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_recibo DATE,
    concepto_abierto TINYINT DEFAULT 0,
    monto_concepto_abierto DECIMAL(18,2),
    concepto_abierto_id BIGINT,
    cuenta_contable_id INT,
    beneficiario_nombre VARCHAR(200),
    beneficiario_documento VARCHAR(50),
    motivo_anulacion TEXT,
    fecha_anulacion DATETIME,
    anulado_por_usuario_id INT,
    comprobante_id BIGINT,
    numero_documento VARCHAR(40),
    saldo_favor_usado DECIMAL(18,2) DEFAULT 0.00,
    FOREIGN KEY (tercero_id) REFERENCES terceros(tercero_id),
    FOREIGN KEY (cajero_id) REFERENCES cajeros(cajero_id),
    FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(metodo_pago_id)
);

CREATE TABLE IF NOT EXISTS detalle_recibo_caja (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigorecibocaja BIGINT NOT NULL,
    codigocuenta INT NOT NULL,
    valor DECIMAL(18,2) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigorecibocaja) REFERENCES recibos_caja(id),
    FOREIGN KEY (codigocuenta) REFERENCES cuentas_contables(codigo)
);

CREATE TABLE IF NOT EXISTS recibo_caja_medio_pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recibo_caja_id BIGINT NOT NULL,
    metodo_pago_id INT NOT NULL,
    monto DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (recibo_caja_id) REFERENCES recibos_caja(id),
    FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(metodo_pago_id)
);

-- Tablas de parámetros
CREATE TABLE IF NOT EXISTS parametros (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    parametro VARCHAR(100) NOT NULL UNIQUE,
    valor VARCHAR(255),
    descripcion VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS parametroscomprobantes (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigocomprobante INT NOT NULL,
    parametro VARCHAR(100) NOT NULL,
    valor VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    FOREIGN KEY (codigocomprobante) REFERENCES comprobantes(comprobante_id)
);

CREATE TABLE IF NOT EXISTS parametrosglobales (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    parametro VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT,
    descripcion VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

-- Tablas de impuestos
CREATE TABLE IF NOT EXISTS impuestos (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigo INT,
    nombre VARCHAR(100),
    porcentaje DECIMAL(10,2) DEFAULT 0.00,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS nuevosimpuestos (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigo INT,
    nombre VARCHAR(100),
    porcentaje DECIMAL(10,2) DEFAULT 0.00,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

-- Tablas de resoluciones
CREATE TABLE IF NOT EXISTS resoluciones (
    resolucion_id INT AUTO_INCREMENT PRIMARY KEY,
    numero_formulario VARCHAR(150) NOT NULL,
    fecha_inicio DATE,
    fecha_fin DATE,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    UNIQUE KEY uq_resolucion_numero_formulario (numero_formulario)
);

CREATE TABLE IF NOT EXISTS resoluciones_comprobantes (
    resolucion_comprobantes_id INT AUTO_INCREMENT PRIMARY KEY,
    resolucion_id INT NOT NULL,
    comprobante_id INT NOT NULL,
    FOREIGN KEY (resolucion_id) REFERENCES resoluciones(resolucion_id),
    FOREIGN KEY (comprobante_id) REFERENCES comprobantes(comprobante_id)
);

-- Tablas de retenciones
CREATE TABLE IF NOT EXISTS retenciones (
    retencion_id INT AUTO_INCREMENT PRIMARY KEY,
    codigo INT DEFAULT 0,
    nombre VARCHAR(100),
    base DECIMAL(10,2) DEFAULT 0.00,
    porcentaje DECIMAL(10,2) DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS retenciones_terceros (
    retenciontercero_id INT AUTO_INCREMENT PRIMARY KEY,
    retencion_id INT NOT NULL,
    tercero_id INT NOT NULL,
    FOREIGN KEY (retencion_id) REFERENCES retenciones(retencion_id),
    FOREIGN KEY (tercero_id) REFERENCES terceros(tercero_id)
);

-- Tablas de transacciones
CREATE TABLE IF NOT EXISTS transacciones (
    transaccion_id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    documento_tipo VARCHAR(100) NOT NULL,
    documento_id INT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    usuario_id INT NOT NULL,
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(codigo)
);

CREATE TABLE IF NOT EXISTS tipo_transaccion (
    tipo_transaccion_id INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS item_transacciones (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    transaccion_id INT NOT NULL,
    codigoproducto INT NOT NULL,
    cantidad DECIMAL(18,3) NOT NULL,
    precio DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (transaccion_id) REFERENCES transacciones(transaccion_id),
    FOREIGN KEY (codigoproducto) REFERENCES productos(producto_id)
);

-- Tablas de usuarios y bodegas
CREATE TABLE IF NOT EXISTS usuariobodega (
    id_usuariobodega INT AUTO_INCREMENT PRIMARY KEY,
    usuarioid INT,
    bodegaid INT,
    FOREIGN KEY (usuarioid) REFERENCES usuarios(codigo),
    FOREIGN KEY (bodegaid) REFERENCES bodegas(codigo)
);

-- Tabla de empresa
CREATE TABLE IF NOT EXISTS empresa (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    codigotipopersona INT,
    codigotipoidentificacion INT,
    numeroidentificacion VARCHAR(50),
    digitoverificacion INT,
    primernombre VARCHAR(100),
    segundonombre VARCHAR(100),
    primerapellido VARCHAR(100),
    segundoapellido VARCHAR(100),
    razonsocial VARCHAR(100),
    codigopostal VARCHAR(20),
    nombrecomercial VARCHAR(100),
    codigoactividadeconomica INT,
    codigoregimen INT,
    correoempresa VARCHAR(100),
    celularempresa VARCHAR(50),
    telfonofijo VARCHAR(50),
    codigopais INT,
    codigodepartamento INT,
    codigomunicipio INT,
    imagenempresa LONGBLOB,
    tipoImagen VARCHAR(100),
    fechainiciolicencia DATE,
    fecharenovacion DATE,
    fechafinallicencia DATE,
    plazo INT,
    numerousuarios INT,
    responsabilidad_fiscal VARCHAR(100),
    tipo_contribuyente VARCHAR(100),
    gran_contribuyente VARCHAR(100),
    autorretenedor VARCHAR(100),
    responsable_iva VARCHAR(100)
);

-- Tabla de errores
CREATE TABLE IF NOT EXISTS bderror (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    tabla VARCHAR(100),
    error TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
