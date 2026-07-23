
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `actividadeconomica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actividadeconomica` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `descripcionActividad` text,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `activos_fijos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activos_fijos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(200) NOT NULL,
  `codigo` varchar(60) DEFAULT NULL,
  `fecha_adquisicion` date NOT NULL,
  `fecha_inicio_depreciacion` date NOT NULL,
  `valor_adquisicion` decimal(18,2) NOT NULL DEFAULT '0.00',
  `valor_residual` decimal(18,2) NOT NULL DEFAULT '0.00',
  `vida_util_meses` int NOT NULL,
  `metodo` varchar(20) NOT NULL DEFAULT 'LINEA_RECTA',
  `depreciacion_acumulada` decimal(18,2) NOT NULL DEFAULT '0.00',
  `meses_depreciados` int NOT NULL DEFAULT '0',
  `cuenta_activo_id` int DEFAULT NULL,
  `cuenta_depreciacion_id` int DEFAULT NULL,
  `cuenta_gasto_id` int DEFAULT NULL,
  `centro_costo_id` int DEFAULT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'ACTIVO',
  `observaciones` varchar(500) DEFAULT NULL,
  `fecha_creacion` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `aliasalmacen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aliasalmacen` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `alias` varchar(100) DEFAULT NULL,
  `almacen` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `anexos_terceros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anexos_terceros` (
  `anexo_tercero_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  PRIMARY KEY (`anexo_tercero_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `anexos_terceros_terceros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anexos_terceros_terceros` (
  `id` int NOT NULL AUTO_INCREMENT,
  `anexo_tercero_id` int NOT NULL,
  `tercero_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_anexo_tercero` (`anexo_tercero_id`),
  KEY `fk_tercero` (`tercero_id`),
  CONSTRAINT `fk_anexo_tercero` FOREIGN KEY (`anexo_tercero_id`) REFERENCES `anexos_terceros` (`anexo_tercero_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_tercero` FOREIGN KEY (`tercero_id`) REFERENCES `terceros` (`tercero_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `anexositemsfactura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anexositemsfactura` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoItemfactura` int DEFAULT NULL,
  `codigoFactura` int DEFAULT NULL,
  `codigoComprobanteFactura` int DEFAULT NULL,
  `comentario1` varchar(100) DEFAULT NULL,
  `comentario2` varchar(100) DEFAULT NULL,
  `comentario3` varchar(100) DEFAULT NULL,
  `comentario4` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `anexositemsremision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anexositemsremision` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoItemRemision` int DEFAULT NULL,
  `codigoRemision` int DEFAULT NULL,
  `codigoComprobanteRemision` int DEFAULT NULL,
  `comentario1` varchar(100) DEFAULT NULL,
  `comentario2` varchar(100) DEFAULT NULL,
  `comentario3` varchar(100) DEFAULT NULL,
  `comentario4` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `anexos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anexosparametros` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoParametro` int DEFAULT '0',
  `nombreParametro` varchar(50) DEFAULT NULL,
  `Clasificacion` varchar(50) DEFAULT NULL,
  `estado` varchar(10) DEFAULT 'ACTIVO',
  `categoriaParametro` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `anexosproducto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anexosproducto` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoProducto` int DEFAULT '0',
  `estado` varchar(8) DEFAULT 'ACTIVO',
  `fecha` datetime DEFAULT NULL,
  `codigoUsuarioModifico` int DEFAULT '0',
  `observacion` varchar(200) DEFAULT NULL,
  `codigoTalla` int DEFAULT '0',
  `codigoColor` int DEFAULT '0',
  `adicion1` varchar(200) DEFAULT NULL,
  `adicion2` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `anexosreciboingreso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anexosreciboingreso` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoReciboIngreso` int DEFAULT '0',
  `codigoComprobante` int DEFAULT '0',
  `saldo` double DEFAULT NULL,
  `codigoCuentaCxc` int DEFAULT '0',
  `codigoCuentaOtros` int DEFAULT '0',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `anexotercero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anexotercero` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoTercero` int DEFAULT NULL,
  `codigoTipoPersona` int DEFAULT NULL,
  `descripcionTipoPersona` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `matriculaMercantil` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ubicacion` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `observaciones` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ciiu` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `genero` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fechaNacimiento` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `asientos_contables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asientos_contables` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `numero_asiento` varchar(40) NOT NULL,
  `fecha` date NOT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `total_debito` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_credito` decimal(18,2) NOT NULL DEFAULT '0.00',
  `documento_origen_tipo` varchar(10) DEFAULT NULL,
  `documento_origen_id` bigint DEFAULT NULL,
  `comprobante_id` bigint DEFAULT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'CONFIRMADO',
  `fecha_creacion` datetime(6) NOT NULL,
  `estado_dian` varchar(20) DEFAULT NULL,
  `cufe` varchar(200) DEFAULT NULL,
  `fecha_autorizacion_dian` datetime DEFAULT NULL,
  `mensaje_dian` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_asiento_fecha` (`fecha`),
  KEY `idx_asiento_origen` (`documento_origen_tipo`,`documento_origen_id`),
  KEY `idx_asiento_comp` (`comprobante_id`),
  KEY `idx_asiento_dian_cufe` (`cufe`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `asientos_contables_lineas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asientos_contables_lineas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `asiento_id` bigint NOT NULL,
  `cuenta_contable_id` int NOT NULL,
  `debito` decimal(18,2) NOT NULL DEFAULT '0.00',
  `credito` decimal(18,2) NOT NULL DEFAULT '0.00',
  `tercero_id` int DEFAULT NULL,
  `tercero_nombre` varchar(200) DEFAULT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `orden` int NOT NULL DEFAULT '0',
  `conciliado` bit(1) NOT NULL DEFAULT b'0',
  `fecha_conciliacion` date DEFAULT NULL,
  `referencia_extracto` varchar(100) DEFAULT NULL,
  `usuario_concilio` varchar(80) DEFAULT NULL,
  `documento_cruce` varchar(100) DEFAULT NULL,
  `centro_costo_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_linea_asiento` (`asiento_id`),
  KEY `idx_linea_cuenta` (`cuenta_contable_id`),
  KEY `idx_linea_tercero` (`tercero_id`),
  KEY `idx_l_conciliado` (`cuenta_contable_id`,`conciliado`)
) ENGINE=InnoDB AUTO_INCREMENT=280 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `asientos_fallidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asientos_fallidos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `modulo` varchar(40) NOT NULL,
  `tipo_documento` varchar(20) DEFAULT NULL,
  `documento_id` bigint DEFAULT NULL,
  `numero_documento` varchar(60) DEFAULT NULL,
  `motivo` varchar(500) NOT NULL,
  `stacktrace` text,
  `fecha_intento` datetime NOT NULL,
  `resuelto` tinyint(1) NOT NULL DEFAULT '0',
  `notas_resolucion` varchar(500) DEFAULT NULL,
  `fecha_resolucion` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_asientos_fallidos_resuelto` (`resuelto`),
  KEY `idx_asientos_fallidos_modulo` (`modulo`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `auditoria_mantenimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria_mantenimiento` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operacion` varchar(40) NOT NULL,
  `detalle` varchar(500) DEFAULT NULL,
  `usuario` varchar(100) DEFAULT NULL,
  `fecha_hora` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `bderror`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bderror` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `CodigoComprobante` int DEFAULT '0',
  `codigoDocumento` int DEFAULT '0',
  `codigoUsuario` int DEFAULT '0',
  `tabla_afectada` varchar(50) DEFAULT NULL,
  `fechaBloqueo` datetime DEFAULT '1990-01-01 00:00:00',
  `observaciones` varchar(1000) DEFAULT NULL,
  `estado` varchar(10) DEFAULT 'INACTIVO',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `bodegas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bodegas` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `codigosucursal` varchar(50) DEFAULT NULL,
  `codigopais` int DEFAULT NULL,
  `codigodepartamento` int DEFAULT NULL,
  `codigomunicipio` int DEFAULT NULL,
  `codigopostal` varchar(50) DEFAULT NULL,
  `direccion` varchar(50) DEFAULT NULL,
  `telefono` varchar(50) DEFAULT NULL,
  `celular` varchar(50) DEFAULT NULL,
  `correo` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `boleta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `boleta` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoFactura` int DEFAULT NULL,
  `codigoComprobante` int DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `caja` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoUsuario` int DEFAULT NULL,
  `montoInicial` int DEFAULT NULL,
  `montoFinal` int DEFAULT NULL,
  `fechaApertura` datetime DEFAULT NULL,
  `fechaCierre` datetime DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL,
  `codigoComprobante` int DEFAULT NULL,
  `consecutivo` int DEFAULT '0',
  `totalRecaudo` int DEFAULT '0',
  `totalCosto` int DEFAULT '0',
  `baseCaja` double DEFAULT '0',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cajas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cajas` (
  `caja_id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `monto_inicial` decimal(10,2) NOT NULL DEFAULT '0.00',
  `monto_final` decimal(10,2) NOT NULL DEFAULT '0.00',
  `fecha_apertura` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_cierre` timestamp NULL DEFAULT NULL,
  `comprobante_id` int NOT NULL,
  `consecutivo` int NOT NULL DEFAULT '0',
  `total_recaudo` decimal(10,2) NOT NULL DEFAULT '0.00',
  `estado` enum('ABIERTA','CERRADA') DEFAULT 'ABIERTA',
  PRIMARY KEY (`caja_id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `comprobante_id` (`comprobante_id`),
  CONSTRAINT `cajas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`codigo`),
  CONSTRAINT `cajas_ibfk_2` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`comprobante_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cajero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cajero` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cajeros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cajeros` (
  `cajero_id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  `codigo_usuario_creo` int NOT NULL DEFAULT '0',
  `fechacreado` date NOT NULL,
  PRIMARY KEY (`cajero_id`),
  UNIQUE KEY `uq_cajero_usuario` (`usuario_id`),
  CONSTRAINT `fk_cajero_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`codigo`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cambios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cambios` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoEntrada` int DEFAULT '0',
  `codigoComprobanteEntrada` int DEFAULT '0',
  `codigoSalida` int DEFAULT '0',
  `codigoComprobanteSalida` int DEFAULT '0',
  `almacen` varchar(20) DEFAULT NULL,
  `codigoUsuarioIngreso` int DEFAULT '0',
  `fecha` datetime DEFAULT '1990-01-01 00:00:00',
  `estado` varchar(10) DEFAULT 'ACTIVO',
  `observaciones` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cantidades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cantidades` (
  `salidas` double DEFAULT NULL,
  `entradas` double DEFAULT NULL,
  `codigoProducto` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cantidadestotales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cantidadestotales` (
  `salidas` double DEFAULT NULL,
  `entradas` double DEFAULT NULL,
  `codigoProducto` int DEFAULT NULL,
  `codigoContable` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `caracteristicas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `caracteristicas` (
  `caracteristica_id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `tipo_caracteristica_id` bigint DEFAULT NULL,
  PRIMARY KEY (`caracteristica_id`),
  KEY `tipo_caracteristica_id` (`tipo_caracteristica_id`),
  CONSTRAINT `caracteristicas_ibfk_1` FOREIGN KEY (`tipo_caracteristica_id`) REFERENCES `tipo_caracteristica` (`tipo_caracteristica_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `categorias_comprobantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias_comprobantes` (
  `categoria_comprobante_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`categoria_comprobante_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `categoriascomprobantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoriascomprobantes` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `categoriasegresos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoriasegresos` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `codigoCuenta` int DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `categoriasingresos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoriasingresos` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `codigoCuenta` int DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `centrocosto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `centrocosto` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(500) DEFAULT NULL,
  `estado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `clasificaciones_terceros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clasificaciones_terceros` (
  `clasificacion_tercero_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`clasificacion_tercero_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `codigospostalesnacionales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `codigospostalesnacionales` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoMunicipio` int DEFAULT NULL,
  `nombreMunicipio` varchar(100) DEFAULT NULL,
  `zonaPostal` int DEFAULT NULL,
  `codigoPostal` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=3682 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `colores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `colores` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `comanda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comanda` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoMesero` double DEFAULT NULL,
  `mesa` double DEFAULT NULL,
  `fechaCreacion` date DEFAULT NULL,
  `horaCreacion` time DEFAULT NULL,
  `codigoFactura` double DEFAULT NULL,
  `codigoUsuarioAnulo` double DEFAULT NULL,
  `fechaAnulo` date DEFAULT NULL,
  `estado` varchar(400) DEFAULT NULL,
  `ubicacion` varchar(400) DEFAULT NULL,
  `codigoUsuario` int DEFAULT NULL,
  `descuento` int DEFAULT NULL,
  `codigoTercero` int DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compra` (
  `codigo` int DEFAULT NULL,
  `codigoTercero` int DEFAULT NULL,
  `codigoComprobante` int DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT NULL,
  `fechaEmision` date DEFAULT NULL,
  `fechaVencimiento` date DEFAULT NULL,
  `plazo` int DEFAULT NULL,
  `fechaCancelada` date DEFAULT NULL,
  `fechaAnulada` date DEFAULT NULL,
  `codigoUsuarioIngreso` int DEFAULT NULL,
  `codigoUsuarioAnulo` int DEFAULT NULL,
  `codigoUsuarioCancelo` int DEFAULT NULL,
  `estado` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pedido` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ordenCompra` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `codigoVendedor` int DEFAULT NULL,
  `remision` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `observaciones` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descuentoPieCompra` double DEFAULT NULL,
  `saldo` int DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `comprobante_cajero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobante_cajero` (
  `comprobante_id` bigint NOT NULL,
  `cajero_id` int NOT NULL,
  PRIMARY KEY (`comprobante_id`,`cajero_id`),
  KEY `idx_cc_cajero` (`cajero_id`),
  CONSTRAINT `fk_cc_comprobante` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes_contables` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `comprobante_egreso_medio_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobante_egreso_medio_pago` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comprobante_egreso_id` bigint NOT NULL,
  `metodo_pago_id` int NOT NULL,
  `monto` decimal(18,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `comprobante_egreso_id` (`comprobante_egreso_id`),
  KEY `metodo_pago_id` (`metodo_pago_id`),
  CONSTRAINT `comprobante_egreso_medio_pago_ibfk_1` FOREIGN KEY (`comprobante_egreso_id`) REFERENCES `comprobantes_egreso` (`id`),
  CONSTRAINT `comprobante_egreso_medio_pago_ibfk_2` FOREIGN KEY (`metodo_pago_id`) REFERENCES `metodos_pago` (`metodo_pago_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `comprobantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobantes` (
  `comprobante_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `categoria_comprobante_id` int NOT NULL,
  `inicio_consecutivo` int DEFAULT NULL,
  `afecta_inventario` enum('SI','NO') DEFAULT 'SI',
  PRIMARY KEY (`comprobante_id`),
  KEY `categoria_comprobante_id` (`categoria_comprobante_id`),
  CONSTRAINT `comprobantes_ibfk_1` FOREIGN KEY (`categoria_comprobante_id`) REFERENCES `categorias_comprobantes` (`categoria_comprobante_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `comprobantes_contables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobantes_contables` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tipo_movimiento` varchar(10) NOT NULL,
  `prefijo` varchar(20) NOT NULL,
  `descripcion` varchar(150) DEFAULT NULL,
  `siguiente_consecutivo` int NOT NULL DEFAULT '1',
  `cuenta_contable_id` int DEFAULT NULL,
  `afecta_inventario` bit(1) NOT NULL DEFAULT b'1',
  `activo` bit(1) NOT NULL DEFAULT b'1',
  `es_legacy` bit(1) NOT NULL DEFAULT b'0',
  `fecha_creacion` datetime(6) NOT NULL,
  `bodega_id` int DEFAULT NULL,
  `resolucion_dian` varchar(50) DEFAULT NULL,
  `fecha_inicio_resolucion` date DEFAULT NULL,
  `fecha_fin_resolucion` date DEFAULT NULL,
  `consecutivo_desde` int DEFAULT NULL,
  `consecutivo_hasta` int DEFAULT NULL,
  `clave_tecnica_dian` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_bodega` (`bodega_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `comprobantes_egreso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobantes_egreso` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `consecutivo` int NOT NULL,
  `tercero_id` int DEFAULT NULL,
  `tercero_nombre` varchar(200) DEFAULT NULL,
  `tercero_nit` varchar(50) DEFAULT NULL,
  `fecha` date NOT NULL,
  `subtotal` decimal(15,2) NOT NULL DEFAULT '0.00',
  `retefuente` decimal(15,2) NOT NULL DEFAULT '0.00',
  `reteica` decimal(15,2) NOT NULL DEFAULT '0.00',
  `reteiva` decimal(15,2) NOT NULL DEFAULT '0.00',
  `descuento` decimal(15,2) NOT NULL DEFAULT '0.00',
  `total` decimal(15,2) NOT NULL DEFAULT '0.00',
  `metodo_pago_id` int DEFAULT NULL,
  `concepto` varchar(500) DEFAULT NULL,
  `centro_costo` varchar(100) DEFAULT NULL,
  `estado` varchar(30) NOT NULL DEFAULT 'ACTIVO',
  `usuario_id` int DEFAULT NULL,
  `cajero_id` int DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `concepto_abierto` tinyint(1) NOT NULL DEFAULT '0',
  `monto_concepto_abierto` decimal(18,2) DEFAULT NULL,
  `fecha_egreso` date DEFAULT NULL,
  `concepto_abierto_id` bigint DEFAULT NULL,
  `cuenta_contable_id` int DEFAULT NULL,
  `beneficiario_nombre` varchar(200) DEFAULT NULL,
  `beneficiario_documento` varchar(50) DEFAULT NULL,
  `motivo_anulacion` text,
  `fecha_anulacion` datetime DEFAULT NULL,
  `anulado_por_usuario_id` int DEFAULT NULL,
  `comprobante_id` bigint DEFAULT NULL,
  `numero_documento` varchar(40) DEFAULT NULL,
  `saldo_favor_usado` decimal(18,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `tercero_id` (`tercero_id`),
  KEY `cajero_id` (`cajero_id`),
  KEY `metodo_pago_id` (`metodo_pago_id`),
  CONSTRAINT `comprobantes_egreso_ibfk_1` FOREIGN KEY (`tercero_id`) REFERENCES `terceros` (`tercero_id`),
  CONSTRAINT `comprobantes_egreso_ibfk_2` FOREIGN KEY (`cajero_id`) REFERENCES `cajeros` (`cajero_id`),
  CONSTRAINT `comprobantes_egreso_ibfk_3` FOREIGN KEY (`metodo_pago_id`) REFERENCES `metodos_pago` (`metodo_pago_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `conceptos_abiertos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conceptos_abiertos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(200) NOT NULL,
  `tipo` varchar(15) NOT NULL,
  `cuenta_contable_id` int NOT NULL,
  `tercero_id` int DEFAULT NULL,
  `beneficiario_nombre` varchar(200) DEFAULT NULL,
  `beneficiario_documento` varchar(50) DEFAULT NULL,
  `info_extra` text,
  `estado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_ca_cuenta_contable` (`cuenta_contable_id`),
  KEY `fk_ca_tercero` (`tercero_id`),
  CONSTRAINT `fk_ca_cuenta_contable` FOREIGN KEY (`cuenta_contable_id`) REFERENCES `cuentas_contables` (`cuenta_id`),
  CONSTRAINT `fk_ca_tercero` FOREIGN KEY (`tercero_id`) REFERENCES `terceros` (`tercero_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `configuracion_compras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion_compras` (
  `id` int NOT NULL,
  `cajero_default_id` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `configuracion_contabilidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion_contabilidad` (
  `id` int NOT NULL,
  `contabilidad_activa` tinyint(1) NOT NULL DEFAULT '1',
  `contabilidad_desde` date DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `configuracion_contable_mapa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion_contable_mapa` (
  `clave` varchar(80) NOT NULL,
  `codigo_cuenta` varchar(20) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`clave`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `contactos_tercero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contactos_tercero` (
  `contacto_id` int NOT NULL AUTO_INCREMENT,
  `tipo_contacto_id` int NOT NULL,
  `valor_contacto` varchar(150) NOT NULL,
  `es_principal` tinyint(1) NOT NULL DEFAULT '0',
  `tercero_id` int NOT NULL,
  PRIMARY KEY (`contacto_id`),
  KEY `fk_contacto_tercero` (`tercero_id`),
  KEY `fk_contacto_tipo` (`tipo_contacto_id`),
  CONSTRAINT `fk_contacto_tercero` FOREIGN KEY (`tercero_id`) REFERENCES `terceros` (`tercero_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_contacto_tipo` FOREIGN KEY (`tipo_contacto_id`) REFERENCES `tipo_contacto` (`tipo_contacto_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cotizaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cotizaciones` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `numero_cotizacion` varchar(50) NOT NULL,
  `cliente_id` int DEFAULT NULL,
  `bodega_id` int NOT NULL,
  `vendedor_id` int DEFAULT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'BORRADOR',
  `observaciones` text,
  `gravada` decimal(18,2) NOT NULL DEFAULT '0.00',
  `iva` decimal(18,2) NOT NULL DEFAULT '0.00',
  `descuentos` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_cotizacion` decimal(18,2) NOT NULL DEFAULT '0.00',
  `usuario_creacion` varchar(100) NOT NULL,
  `fecha_creacion` date NOT NULL,
  `cajero_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bodega_id` (`bodega_id`),
  KEY `vendedor_id` (`vendedor_id`),
  KEY `fk_cotizacion_cajero` (`cajero_id`),
  KEY `cotizaciones_ibfk_1` (`cliente_id`),
  CONSTRAINT `cotizaciones_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `terceros` (`tercero_id`),
  CONSTRAINT `cotizaciones_ibfk_2` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`codigo`),
  CONSTRAINT `cotizaciones_ibfk_3` FOREIGN KEY (`vendedor_id`) REFERENCES `vendedores` (`vendedor_id`),
  CONSTRAINT `fk_cotizacion_cajero` FOREIGN KEY (`cajero_id`) REFERENCES `cajeros` (`cajero_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cuentas_bancarias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuentas_bancarias` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `banco` varchar(80) NOT NULL,
  `numero_cuenta` varchar(50) DEFAULT NULL,
  `tipo` varchar(20) NOT NULL DEFAULT 'AHORROS',
  `moneda` varchar(10) NOT NULL DEFAULT 'COP',
  `saldo_inicial` decimal(18,2) NOT NULL DEFAULT '0.00',
  `fecha_apertura` date NOT NULL,
  `cuenta_contable_id` int NOT NULL,
  `activo` bit(1) NOT NULL DEFAULT b'1',
  `observaciones` varchar(500) DEFAULT NULL,
  `fecha_creacion` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cb_cuenta_contable` (`cuenta_contable_id`),
  KEY `idx_cb_banco` (`banco`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cuentas_contables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuentas_contables` (
  `cuenta_id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `tipo` enum('ACTIVO','PASIVO','PATRIMONIO','INGRESO','GASTO','COSTO','COSTO_PRODUCCION','ORDEN_DEUDORAS','ORDEN_ACREEDORAS') NOT NULL,
  `nivel` int NOT NULL DEFAULT '5',
  `padre_id` int DEFAULT NULL,
  `es_movimiento` tinyint(1) DEFAULT '1',
  `estado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `requiere_tercero` tinyint(1) NOT NULL DEFAULT '0',
  `requiere_documento_cruce` tinyint(1) NOT NULL DEFAULT '0',
  `requiere_centro_costo` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`cuenta_id`),
  UNIQUE KEY `uq_cuenta_contable_codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=93513 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cuentas_por_cobrar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuentas_por_cobrar` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cliente_id` int DEFAULT NULL,
  `nit` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `numero_venta` varchar(255) NOT NULL,
  `venta_id` bigint DEFAULT NULL,
  `valor_neto` decimal(19,2) NOT NULL,
  `saldo` decimal(19,2) NOT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `plazo_dias` int NOT NULL,
  `estado` varchar(50) NOT NULL DEFAULT 'PENDIENTE',
  `fecha_creacion` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cxc_cliente` (`cliente_id`),
  KEY `fk_cxc_venta` (`venta_id`),
  CONSTRAINT `fk_cxc_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `terceros` (`tercero_id`),
  CONSTRAINT `fk_cxc_venta` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cuentas_por_pagar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuentas_por_pagar` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_factura` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_factura_proveedor` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_vencimiento` date NOT NULL,
  `valor_neto` decimal(15,2) NOT NULL,
  `estado` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `proveedor_id` int DEFAULT NULL,
  `fecha_creacion` date NOT NULL,
  `saldo` decimal(15,2) NOT NULL DEFAULT '0.00',
  `retefuente` decimal(15,2) NOT NULL DEFAULT '0.00',
  `reteiva` decimal(15,2) NOT NULL DEFAULT '0.00',
  `reteica` decimal(15,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `fk_cuentas_proveedor` (`proveedor_id`),
  CONSTRAINT `fk_cuentas_proveedor` FOREIGN KEY (`proveedor_id`) REFERENCES `terceros` (`tercero_id`)
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `departamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departamentos` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoPais` int DEFAULT NULL,
  `codigoDepartamento` int DEFAULT NULL,
  `departamento` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `depreciacion_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `depreciacion_detalle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo_id` bigint NOT NULL,
  `anio` int NOT NULL,
  `mes` int NOT NULL,
  `cuota` decimal(18,2) NOT NULL DEFAULT '0.00',
  `numero_asiento` varchar(40) DEFAULT NULL,
  `fecha_creacion` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_dep_periodo` (`anio`,`mes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalle_cajero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_cajero` (
  `detalle_cajero_id` bigint NOT NULL AUTO_INCREMENT,
  `cajero_id` int NOT NULL,
  `comprobante_id` int DEFAULT NULL,
  `fecha_apertura` datetime NOT NULL,
  `fecha_cierre` datetime DEFAULT NULL,
  `estado` enum('ABIERTA','CERRADA') NOT NULL DEFAULT 'ABIERTA',
  `consecutivo` int NOT NULL DEFAULT '0',
  `monto_inicial` decimal(15,2) NOT NULL DEFAULT '0.00',
  `monto_final` decimal(15,2) NOT NULL DEFAULT '0.00',
  `base_caja` decimal(15,2) NOT NULL DEFAULT '0.00',
  `total_efectivo` decimal(15,2) NOT NULL DEFAULT '0.00',
  `efectivo_declarado` decimal(15,2) DEFAULT NULL,
  `diferencia_efectivo` decimal(15,2) DEFAULT NULL,
  `total_medios_electronicos` decimal(15,2) NOT NULL DEFAULT '0.00',
  `medios_electronicos_declarado` decimal(15,2) DEFAULT NULL,
  `diferencia_medios_electronicos` decimal(15,2) DEFAULT NULL,
  `total_recaudo` decimal(15,2) NOT NULL DEFAULT '0.00',
  `total_costo` decimal(15,2) NOT NULL DEFAULT '0.00',
  `numeroz` double DEFAULT '1',
  PRIMARY KEY (`detalle_cajero_id`),
  KEY `fk_detalle_cajero_comprobante` (`comprobante_id`),
  KEY `idx_detalle_cajero_cajero` (`cajero_id`),
  KEY `idx_detalle_cajero_estado` (`estado`),
  KEY `idx_detalle_cajero_apertura` (`fecha_apertura`),
  CONSTRAINT `fk_detalle_cajero_cajero` FOREIGN KEY (`cajero_id`) REFERENCES `cajeros` (`cajero_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_cajero_comprobante` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`comprobante_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalle_comprobante_egreso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_comprobante_egreso` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comprobante_egreso_id` bigint NOT NULL,
  `cuenta_por_pagar_id` bigint NOT NULL,
  `monto_abonado` decimal(15,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `comprobante_egreso_id` (`comprobante_egreso_id`),
  KEY `cuenta_por_pagar_id` (`cuenta_por_pagar_id`),
  CONSTRAINT `detalle_comprobante_egreso_ibfk_1` FOREIGN KEY (`comprobante_egreso_id`) REFERENCES `comprobantes_egreso` (`id`),
  CONSTRAINT `detalle_comprobante_egreso_ibfk_2` FOREIGN KEY (`cuenta_por_pagar_id`) REFERENCES `cuentas_por_pagar` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalle_devoluciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_devoluciones` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `devolucion_id` bigint NOT NULL,
  `codigo_producto` varchar(255) NOT NULL,
  `descripcion_producto` varchar(500) NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(19,2) NOT NULL,
  `iva` decimal(19,2) NOT NULL DEFAULT '0.00',
  `total` decimal(19,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `fk_detdev_devolucion` (`devolucion_id`),
  CONSTRAINT `fk_detdev_devolucion` FOREIGN KEY (`devolucion_id`) REFERENCES `devoluciones_venta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Detalle de ítems devueltos';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalle_recibo_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_recibo_caja` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `recibo_caja_id` bigint NOT NULL,
  `cuenta_por_cobrar_id` bigint NOT NULL,
  `monto_abonado` decimal(15,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `recibo_caja_id` (`recibo_caja_id`),
  KEY `cuenta_por_cobrar_id` (`cuenta_por_cobrar_id`),
  CONSTRAINT `detalle_recibo_caja_ibfk_1` FOREIGN KEY (`recibo_caja_id`) REFERENCES `recibos_caja` (`id`),
  CONSTRAINT `detalle_recibo_caja_ibfk_2` FOREIGN KEY (`cuenta_por_cobrar_id`) REFERENCES `cuentas_por_cobrar` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalles_cotizacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_cotizacion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cotizacion_id` bigint NOT NULL,
  `codigo_producto` varchar(50) NOT NULL,
  `codigo_barras` varchar(100) NOT NULL,
  `descripcion_producto` varchar(255) NOT NULL,
  `observacion_producto` varchar(255) DEFAULT '',
  `referencia_variantes` varchar(255) NOT NULL DEFAULT '',
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(18,2) NOT NULL,
  `descuento` decimal(18,2) NOT NULL DEFAULT '0.00',
  `iva` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total` decimal(18,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_detalle_cotizacion` (`cotizacion_id`),
  CONSTRAINT `fk_detalle_cotizacion` FOREIGN KEY (`cotizacion_id`) REFERENCES `cotizaciones` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalles_devolucion_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_devolucion_compra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `devolucion_compra_id` bigint NOT NULL,
  `detalle_orden_compra_id` bigint NOT NULL,
  `cantidad_devuelta` int NOT NULL,
  `costo_unitario` decimal(18,2) NOT NULL DEFAULT '0.00',
  `iva_linea` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_linea` decimal(18,2) NOT NULL DEFAULT '0.00',
  `motivo` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_detdevcompra_dev` (`devolucion_compra_id`),
  KEY `idx_detdevcompra_det` (`detalle_orden_compra_id`),
  CONSTRAINT `fk_detdevcompra_det` FOREIGN KEY (`detalle_orden_compra_id`) REFERENCES `detalles_orden_compra` (`id`),
  CONSTRAINT `fk_detdevcompra_dev` FOREIGN KEY (`devolucion_compra_id`) REFERENCES `devoluciones_compra` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalles_devolucion_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_devolucion_venta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `devolucion_id` bigint NOT NULL,
  `detalle_venta_id` bigint NOT NULL,
  `cantidad_devuelta` int NOT NULL,
  `precio_unitario` decimal(18,2) NOT NULL,
  `iva_linea` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_linea` decimal(18,2) NOT NULL DEFAULT '0.00',
  `motivo` varchar(500) DEFAULT NULL,
  `costo_unitario` decimal(18,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `fk_detalle_dev_devolucion` (`devolucion_id`),
  KEY `fk_detalle_dev_detalleventa` (`detalle_venta_id`),
  CONSTRAINT `fk_detalle_dev_detalleventa` FOREIGN KEY (`detalle_venta_id`) REFERENCES `detalles_venta` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_dev_devolucion` FOREIGN KEY (`devolucion_id`) REFERENCES `devoluciones_venta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalles_orden_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_orden_compra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orden_compra_id` bigint NOT NULL,
  `codigo_producto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sku` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `codigo_barras` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion_producto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `observacion_producto` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `referencia_variantes` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(15,2) NOT NULL,
  `descuento` decimal(15,2) NOT NULL DEFAULT '0.00',
  `iva` decimal(15,2) NOT NULL,
  `total` decimal(15,2) NOT NULL,
  `recibido` tinyint(1) NOT NULL DEFAULT '0',
  `cantidad_recibida` int NOT NULL DEFAULT '0',
  `manifiesto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_detalles_orden_id` (`orden_compra_id`),
  KEY `idx_detalles_codigo` (`codigo_producto`),
  CONSTRAINT `fk_detalles_orden` FOREIGN KEY (`orden_compra_id`) REFERENCES `ordenes_compra` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalles_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_pedido` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pedido_id` bigint NOT NULL,
  `codigo_producto` varchar(50) NOT NULL,
  `codigo_barras` varchar(100) NOT NULL,
  `descripcion_producto` varchar(255) NOT NULL,
  `observacion_producto` varchar(255) DEFAULT NULL,
  `referencia_variantes` varchar(255) NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(18,2) NOT NULL,
  `descuento` decimal(18,2) NOT NULL DEFAULT '0.00',
  `iva` decimal(18,2) NOT NULL,
  `total` decimal(18,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pedido_id` (`pedido_id`),
  CONSTRAINT `detalles_pedido_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `detalles_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_venta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `venta_id` bigint NOT NULL,
  `codigo_producto` varchar(50) NOT NULL,
  `codigo_barras` varchar(100) NOT NULL,
  `descripcion_producto` varchar(255) NOT NULL,
  `observacion_producto` varchar(255) DEFAULT '',
  `referencia_variantes` varchar(255) NOT NULL DEFAULT '',
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(18,2) NOT NULL,
  `descuento` decimal(18,2) NOT NULL DEFAULT '0.00',
  `iva` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total` decimal(18,2) NOT NULL,
  `manifiesto` varchar(255) DEFAULT NULL,
  `costo_unitario` decimal(18,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_detalle_venta_venta` (`venta_id`),
  CONSTRAINT `fk_detalle_venta` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `devoluciones_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `devoluciones_compra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orden_compra_id` bigint NOT NULL,
  `numero_devolucion` varchar(100) NOT NULL,
  `motivo` varchar(300) DEFAULT NULL,
  `observaciones` text,
  `estado` varchar(20) NOT NULL DEFAULT 'REGISTRADA',
  `total_devuelto` decimal(18,2) NOT NULL DEFAULT '0.00',
  `iva_devuelto` decimal(18,2) NOT NULL DEFAULT '0.00',
  `retencion_devuelta` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_neto` decimal(18,2) NOT NULL DEFAULT '0.00',
  `nit_proveedor` varchar(50) DEFAULT NULL,
  `nombre_proveedor` varchar(200) DEFAULT NULL,
  `usuario_creacion` varchar(100) NOT NULL,
  `fecha_creacion` date NOT NULL,
  `motivo_anulacion` varchar(300) DEFAULT NULL,
  `fecha_anulacion` date DEFAULT NULL,
  `usuario_anulacion` varchar(100) DEFAULT NULL,
  `monto_cxp_debitado` decimal(18,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `idx_devcompra_orden` (`orden_compra_id`),
  CONSTRAINT `fk_devcompra_orden` FOREIGN KEY (`orden_compra_id`) REFERENCES `ordenes_compra` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `devoluciones_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `devoluciones_venta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `venta_id` bigint NOT NULL,
  `cajero_id` int DEFAULT NULL,
  `numero_devolucion` varchar(100) NOT NULL,
  `motivo` varchar(500) NOT NULL,
  `observaciones` text,
  `estado` varchar(30) NOT NULL DEFAULT 'REGISTRADA',
  `total_devuelto` decimal(18,2) NOT NULL DEFAULT '0.00',
  `iva_devuelto` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_neto` decimal(18,2) NOT NULL DEFAULT '0.00',
  `usuario_creacion` varchar(100) NOT NULL,
  `fecha_creacion` date NOT NULL DEFAULT (curdate()),
  `comprobante_id` bigint DEFAULT NULL,
  `consecutivo_comprobante` int DEFAULT NULL,
  `numero_nc` varchar(50) DEFAULT NULL,
  `cufe_nc` varchar(200) DEFAULT NULL,
  `estado_dian_nc` varchar(20) DEFAULT NULL,
  `mensaje_dian_nc` varchar(500) DEFAULT NULL,
  `qr_data_nc` text,
  `motivo_anulacion` varchar(300) DEFAULT NULL,
  `fecha_anulacion` date DEFAULT NULL,
  `usuario_anulacion` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_devolucion` (`numero_devolucion`),
  KEY `fk_devolucion_venta` (`venta_id`),
  KEY `fk_devolucion_cajero` (`cajero_id`),
  CONSTRAINT `fk_devolucion_cajero` FOREIGN KEY (`cajero_id`) REFERENCES `cajeros` (`cajero_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_devolucion_venta` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `documentos_electronicos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `documentos_electronicos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tipo` varchar(10) NOT NULL,
  `numero` varchar(50) NOT NULL,
  `cufe` varchar(200) DEFAULT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tercero_identificacion` varchar(50) DEFAULT NULL,
  `tercero_nombre` varchar(200) DEFAULT NULL,
  `documento_referencia_id` bigint DEFAULT NULL,
  `documento_referencia_tipo` varchar(20) DEFAULT NULL,
  `base_gravable` decimal(18,2) DEFAULT '0.00',
  `iva` decimal(18,2) DEFAULT '0.00',
  `total` decimal(18,2) NOT NULL,
  `concepto` varchar(500) DEFAULT NULL,
  `codigo_concepto_dian` int DEFAULT NULL,
  `estado_dian` varchar(20) DEFAULT NULL,
  `mensaje_dian` varchar(500) DEFAULT NULL,
  `qr_data` text,
  `xml_firmado` longtext,
  `fecha_validacion_dian` datetime DEFAULT NULL,
  `asiento_id` bigint DEFAULT NULL,
  `estado` varchar(20) DEFAULT 'ACTIVO',
  `tipo_compra` varchar(20) DEFAULT NULL,
  `retencion_fuente` decimal(18,2) NOT NULL DEFAULT '0.00',
  `retencion_iva` decimal(18,2) NOT NULL DEFAULT '0.00',
  `retencion_ica` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_retenciones` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_pagar` decimal(18,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `idx_tipo` (`tipo`),
  KEY `idx_fecha` (`fecha_emision`),
  KEY `idx_estado_dian` (`estado_dian`),
  KEY `idx_doc_ref` (`documento_referencia_tipo`,`documento_referencia_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `empresa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empresa` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigotipopersona` int DEFAULT NULL,
  `codigotipoidentificacion` int DEFAULT NULL,
  `numeroidentificacion` varchar(10) DEFAULT NULL,
  `digitoverificacion` varchar(10) DEFAULT NULL,
  `primernombre` varchar(50) DEFAULT NULL,
  `segundonombre` varchar(50) DEFAULT NULL,
  `primerapellido` varchar(50) DEFAULT NULL,
  `segundoapellido` varchar(50) DEFAULT NULL,
  `razonsocial` varchar(100) DEFAULT NULL,
  `codigopostal` varchar(100) DEFAULT NULL,
  `nombrecomercial` varchar(50) DEFAULT NULL,
  `codigoactividadeconomica` int DEFAULT NULL,
  `codigoregimen` int DEFAULT NULL,
  `correoempresa` varchar(50) DEFAULT NULL,
  `celularempresa` varchar(12) DEFAULT NULL,
  `telfonofijo` varchar(20) DEFAULT NULL,
  `codigopais` int DEFAULT NULL,
  `codigodepartamento` int DEFAULT NULL,
  `codigomunicipio` int DEFAULT NULL,
  `imagenempresa` longblob,
  `tipoImagen` varchar(100) DEFAULT '',
  `estado` enum('ACTIVA','INACTIVA') DEFAULT NULL,
  `numerousuarios` int DEFAULT NULL,
  `fechainiciolicencia` date DEFAULT NULL,
  `fechafinallicencia` date DEFAULT NULL,
  `plazo` int NOT NULL DEFAULT '0',
  `responsabilidad_fiscal` varchar(100) DEFAULT NULL,
  `tipo_contribuyente` varchar(30) DEFAULT NULL,
  `gran_contribuyente` tinyint(1) NOT NULL DEFAULT '0',
  `autorretenedor` tinyint(1) NOT NULL DEFAULT '0',
  `responsable_iva` tinyint(1) NOT NULL DEFAULT '1',
  `fecharenovacion` date DEFAULT NULL,
  `tipolicencia` varchar(50) DEFAULT NULL,
  `direccion` double DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `existencias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `existencias` (
  `existencia_id` int NOT NULL AUTO_INCREMENT,
  `producto_variantes_id` bigint NOT NULL,
  `bodega_id` int NOT NULL,
  `existencia` decimal(10,2) DEFAULT '0.00',
  `stock_min` decimal(10,2) DEFAULT '0.00',
  `stock_max` decimal(10,2) DEFAULT '0.00',
  `fecha_ultimo_movimiento` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ubicacion` varchar(100) DEFAULT NULL,
  `predeterminada` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`existencia_id`),
  UNIQUE KEY `uq_existencias_producto_bodega` (`producto_variantes_id`,`bodega_id`),
  KEY `bodega_id` (`bodega_id`),
  CONSTRAINT `existencias_ibfk_1` FOREIGN KEY (`producto_variantes_id`) REFERENCES `producto_variantes` (`producto_variantes_id`),
  CONSTRAINT `existencias_ibfk_2` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facturas` (
  `factura_id` int NOT NULL AUTO_INCREMENT,
  `consecutivo` int NOT NULL,
  `comprobante_id` int NOT NULL,
  `tercero_id` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_emision` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `plazo` int NOT NULL DEFAULT '0',
  `usuario_ingreso_id` int NOT NULL,
  `fecha_anulo` datetime DEFAULT NULL,
  `usuario_anulo_id` int DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `pedido_id` int DEFAULT NULL,
  `remision_id` int DEFAULT NULL,
  `vendedor_id` int DEFAULT NULL,
  `descuento` decimal(10,2) DEFAULT '0.00',
  `caja_id` int DEFAULT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `saldo` decimal(10,2) DEFAULT '0.00',
  `total_factura` decimal(10,2) DEFAULT '0.00',
  `venta_id` bigint DEFAULT NULL,
  `cufe` varchar(255) DEFAULT NULL,
  `estado_dian` varchar(50) DEFAULT NULL,
  `xml_firmado` longtext,
  `pdf_base64` longtext,
  `qr_data` text,
  `mensaje_dian` text,
  `fecha_validacion_dian` datetime DEFAULT NULL,
  `resolucion_dian` varchar(100) DEFAULT NULL,
  `prefijo` varchar(10) DEFAULT NULL,
  `numero_factura` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`factura_id`),
  KEY `comprobante_id` (`comprobante_id`),
  KEY `tercero_id` (`tercero_id`),
  KEY `usuario_ingreso_id` (`usuario_ingreso_id`),
  KEY `vendedor_id` (`vendedor_id`),
  KEY `caja_id` (`caja_id`),
  CONSTRAINT `facturas_fk_cajero` FOREIGN KEY (`caja_id`) REFERENCES `cajeros` (`cajero_id`),
  CONSTRAINT `facturas_ibfk_2` FOREIGN KEY (`tercero_id`) REFERENCES `terceros` (`tercero_id`),
  CONSTRAINT `facturas_ibfk_3` FOREIGN KEY (`usuario_ingreso_id`) REFERENCES `usuarios` (`codigo`),
  CONSTRAINT `facturas_ibfk_4` FOREIGN KEY (`vendedor_id`) REFERENCES `vendedores` (`vendedor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93349 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `grupos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grupos` (
  `grupo_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`grupo_id`),
  UNIQUE KEY `uq_grupos_descripcion` (`descripcion`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `impuestos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `impuestos` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `tarifa` double DEFAULT NULL,
  `base` double DEFAULT NULL,
  `sigla` varchar(10) DEFAULT NULL,
  `estado` varchar(8) DEFAULT 'ACTIVO',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `item_transacciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_transacciones` (
  `item_transaccion_id` int NOT NULL AUTO_INCREMENT,
  `transaccion_id` int NOT NULL,
  `cuenta_id` int NOT NULL,
  `credito` decimal(10,2) DEFAULT '0.00',
  `debito` decimal(10,2) DEFAULT '0.00',
  `referencia` varchar(100) NOT NULL,
  PRIMARY KEY (`item_transaccion_id`),
  KEY `transaccion_id` (`transaccion_id`),
  KEY `cuenta_id` (`cuenta_id`),
  CONSTRAINT `item_transacciones_ibfk_1` FOREIGN KEY (`transaccion_id`) REFERENCES `transacciones` (`transaccion_id`),
  CONSTRAINT `item_transacciones_ibfk_2` FOREIGN KEY (`cuenta_id`) REFERENCES `cuentas_contables` (`cuenta_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `kardex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kardex` (
  `kardex_id` bigint NOT NULL AUTO_INCREMENT,
  `movimiento_inventario_id` bigint NOT NULL,
  `movimiento_inventario_detalle_id` bigint NOT NULL,
  `producto_variante_id` bigint NOT NULL,
  `bodega_id` int NOT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `entrada` double NOT NULL DEFAULT '0',
  `salida` double NOT NULL DEFAULT '0',
  `saldo` double NOT NULL DEFAULT '0',
  `costo_unitario` double NOT NULL,
  `costo_promedio` double NOT NULL,
  `total_costo` double NOT NULL,
  `tipo_movimiento` varchar(50) NOT NULL,
  `estado_movimiento` varchar(50) NOT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`kardex_id`),
  KEY `fk_kardex_movimiento` (`movimiento_inventario_id`),
  KEY `fk_kardex_detalle` (`movimiento_inventario_detalle_id`),
  KEY `fk_kardex_producto_variante` (`producto_variante_id`),
  KEY `fk_kardex_bodega` (`bodega_id`),
  CONSTRAINT `fk_kardex_bodega` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`codigo`),
  CONSTRAINT `fk_kardex_detalle` FOREIGN KEY (`movimiento_inventario_detalle_id`) REFERENCES `movimientos_inventario_detalles` (`movimiento_inventario_detalle_id`),
  CONSTRAINT `fk_kardex_movimiento` FOREIGN KEY (`movimiento_inventario_id`) REFERENCES `movimientos_inventario` (`movimiento_inventario_id`),
  CONSTRAINT `fk_kardex_producto_variante` FOREIGN KEY (`producto_variante_id`) REFERENCES `producto_variantes` (`producto_variantes_id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `legalizaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `legalizaciones` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orden_compra_id` bigint NOT NULL,
  `numero_factura_proveedor` varchar(255) NOT NULL,
  `fecha_factura` date NOT NULL,
  `total_factura` decimal(15,2) NOT NULL,
  `proveedor_id` int NOT NULL,
  `estado` varchar(50) DEFAULT 'LEGALIZADA',
  `usuario_creacion` varchar(255) NOT NULL,
  `fecha_creacion` date NOT NULL DEFAULT (curdate()),
  `comprobante_id` bigint DEFAULT NULL,
  `consecutivo_comprobante` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `orden_compra_id` (`orden_compra_id`),
  KEY `proveedor_id` (`proveedor_id`),
  CONSTRAINT `legalizaciones_ibfk_1` FOREIGN KEY (`orden_compra_id`) REFERENCES `ordenes_compra` (`id`),
  CONSTRAINT `legalizaciones_ibfk_2` FOREIGN KEY (`proveedor_id`) REFERENCES `terceros` (`tercero_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `lineas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lineas` (
  `linea_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`linea_id`),
  UNIQUE KEY `uq_lineas_descripcion` (`descripcion`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `manifestos_importacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manifestos_importacion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `numero_declaracion` varchar(200) NOT NULL,
  `fecha_importacion` date NOT NULL,
  `aduana` varchar(300) DEFAULT NULL,
  `pais_origen` varchar(100) DEFAULT NULL,
  `proveedor_internacional` varchar(300) DEFAULT NULL,
  `numero_contenedor` varchar(100) DEFAULT NULL,
  `observaciones` text,
  `ruta_pdf` varchar(500) DEFAULT NULL,
  `nombre_archivo_original` varchar(200) DEFAULT NULL,
  `estado` varchar(50) DEFAULT 'ACTIVO',
  `usuario_creacion` varchar(100) NOT NULL,
  `fecha_creacion` date NOT NULL,
  `orden_compra_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_manifiesto_orden_compra` (`orden_compra_id`),
  CONSTRAINT `fk_manifiesto_orden_compra` FOREIGN KEY (`orden_compra_id`) REFERENCES `ordenes_compra` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `metodos_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metodos_pago` (
  `metodo_pago_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `sigla` varchar(50) NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `tipo_negociacion` enum('Contado','Credito') DEFAULT 'Contado',
  `tipos` varchar(100) NOT NULL DEFAULT 'RECIBO,EGRESO,VENTA',
  `cuenta_bancaria_id` bigint DEFAULT NULL,
  `cuenta_contable_id` int DEFAULT NULL,
  PRIMARY KEY (`metodo_pago_id`),
  KEY `idx_mp_cuenta_bancaria` (`cuenta_bancaria_id`),
  KEY `idx_mp_cuenta_contable` (`cuenta_contable_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93295 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `metodos_pago_facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metodos_pago_facturas` (
  `metodo_pago_factura_id` int NOT NULL AUTO_INCREMENT,
  `metodo_pago_id` int NOT NULL,
  `factura_id` int NOT NULL,
  `valor` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`metodo_pago_factura_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93344 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `movimiento_cajero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_cajero` (
  `movimiento_cajero_id` bigint NOT NULL AUTO_INCREMENT,
  `detalle_cajero_id` bigint NOT NULL,
  `cajero_id` int NOT NULL,
  `tipo_movimiento` varchar(30) NOT NULL COMMENT 'VENTA | COTIZACION | PEDIDO | CUENTA_POR_COBRAR | EGRESO | DEVOLUCION | ANULACION | INGRESO_EFECTIVO | ABONO',
  `numero_comprobante` varchar(100) DEFAULT NULL,
  `referencia_documento_id` bigint DEFAULT NULL,
  `consecutivo` int NOT NULL DEFAULT '0',
  `consecutivo_tipo` int NOT NULL DEFAULT '0',
  `monto` decimal(15,2) NOT NULL DEFAULT '0.00',
  `costo` decimal(15,2) NOT NULL DEFAULT '0.00',
  `monto_efectivo` decimal(15,2) NOT NULL DEFAULT '0.00',
  `monto_electronico` decimal(15,2) NOT NULL DEFAULT '0.00',
  `descripcion` varchar(500) DEFAULT NULL,
  `tercero_nombre` varchar(200) DEFAULT NULL COMMENT 'Nombre del tercero (proveedor en EGRESO, cliente en ABONO)',
  `fecha_movimiento` datetime NOT NULL,
  `comprobante_contable_id` bigint DEFAULT NULL,
  PRIMARY KEY (`movimiento_cajero_id`),
  KEY `idx_mov_cajero_sesion` (`detalle_cajero_id`),
  KEY `idx_mov_cajero_cajero` (`cajero_id`),
  KEY `idx_mov_cajero_tipo` (`tipo_movimiento`),
  KEY `idx_mov_cajero_fecha` (`fecha_movimiento`),
  KEY `idx_mov_cajero_doc_ref` (`referencia_documento_id`),
  KEY `idx_mc_comprobante` (`comprobante_contable_id`),
  CONSTRAINT `fk_movimiento_cajero_cajero` FOREIGN KEY (`cajero_id`) REFERENCES `cajeros` (`cajero_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_movimiento_detalle_cajero` FOREIGN KEY (`detalle_cajero_id`) REFERENCES `detalle_cajero` (`detalle_cajero_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=288 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `movimientos_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientos_inventario` (
  `movimiento_inventario_id` bigint NOT NULL AUTO_INCREMENT,
  `comprobante_id` bigint DEFAULT NULL,
  `consecutivo` int DEFAULT NULL,
  `tipo_movimiento` enum('ENTRADA','SALIDA','TRASLADO') DEFAULT NULL,
  `usuario_id` int DEFAULT NULL,
  `fecha_emision` date DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT NULL,
  `estado_movimiento` varchar(50) DEFAULT NULL,
  `total` decimal(18,2) DEFAULT NULL,
  `observaciones` varchar(200) DEFAULT NULL,
  `documento_origen_tipo` varchar(10) DEFAULT NULL,
  `documento_origen_id` bigint DEFAULT NULL,
  PRIMARY KEY (`movimiento_inventario_id`),
  KEY `comprobante_id` (`comprobante_id`),
  KEY `idx_mov_origen` (`documento_origen_tipo`,`documento_origen_id`),
  CONSTRAINT `fk_movinv_comprobante_contable` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes_contables` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `movimientos_inventario_detalles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientos_inventario_detalles` (
  `movimiento_inventario_detalle_id` bigint NOT NULL AUTO_INCREMENT,
  `movimiento_inventario_id` bigint NOT NULL,
  `producto_variante_id` bigint NOT NULL,
  `bodega_origen_id` int DEFAULT NULL,
  `bodega_destino_id` int DEFAULT NULL,
  `cantidad` double NOT NULL,
  `costo_unitario` double NOT NULL,
  `costo_promedio` double NOT NULL,
  `total_detalle` double NOT NULL,
  PRIMARY KEY (`movimiento_inventario_detalle_id`),
  KEY `fk_movimiento` (`movimiento_inventario_id`),
  KEY `fk_producto_variante` (`producto_variante_id`),
  KEY `fk_bodega_origen` (`bodega_origen_id`),
  KEY `fk_bodega_destino` (`bodega_destino_id`),
  CONSTRAINT `fk_bodega_destino` FOREIGN KEY (`bodega_destino_id`) REFERENCES `bodegas` (`codigo`),
  CONSTRAINT `fk_bodega_origen` FOREIGN KEY (`bodega_origen_id`) REFERENCES `bodegas` (`codigo`),
  CONSTRAINT `fk_movimiento` FOREIGN KEY (`movimiento_inventario_id`) REFERENCES `movimientos_inventario` (`movimiento_inventario_id`),
  CONSTRAINT `fk_producto_variante` FOREIGN KEY (`producto_variante_id`) REFERENCES `producto_variantes` (`producto_variantes_id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `municipios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `municipios` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoDepartamento` int DEFAULT NULL,
  `codigoMunicipio` int DEFAULT NULL,
  `municipio` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=1122 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `notas_estados_financieros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notas_estados_financieros` (
  `id` int NOT NULL AUTO_INCREMENT,
  `anio` int NOT NULL,
  `numero` int NOT NULL DEFAULT '1',
  `titulo` varchar(200) NOT NULL,
  `contenido` text,
  `estado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notas_anio` (`anio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `nuevosimpuestos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nuevosimpuestos` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `tarifa` double DEFAULT NULL,
  `base` double DEFAULT NULL,
  `sigla` varchar(10) DEFAULT NULL,
  `estado` varchar(8) DEFAULT 'ACTIVO',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `orden_compra_metodos_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orden_compra_metodos_pago` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `orden_compra_id` bigint NOT NULL,
  `metodo_pago_id` int NOT NULL,
  `monto` decimal(18,2) NOT NULL DEFAULT '0.00',
  `referencia` varchar(100) DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_orden` (`orden_compra_id`),
  KEY `idx_metodo` (`metodo_pago_id`),
  CONSTRAINT `orden_compra_metodos_pago_ibfk_1` FOREIGN KEY (`orden_compra_id`) REFERENCES `ordenes_compra` (`id`) ON DELETE CASCADE,
  CONSTRAINT `orden_compra_metodos_pago_ibfk_2` FOREIGN KEY (`metodo_pago_id`) REFERENCES `metodos_pago` (`metodo_pago_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `ordenes_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordenes_compra` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `numero_orden` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_oc` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `proveedor_id` int NOT NULL,
  `bodega_id` int NOT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_entrega_esperada` date NOT NULL,
  `estado` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `observaciones` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `gravada` decimal(15,2) NOT NULL DEFAULT '0.00',
  `iva` decimal(15,2) NOT NULL DEFAULT '0.00',
  `descuentos` decimal(15,2) NOT NULL DEFAULT '0.00',
  `total_orden_compra` decimal(15,2) NOT NULL DEFAULT '0.00',
  `usuario_creacion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_creacion` date NOT NULL,
  `comprobante_id` bigint DEFAULT NULL,
  `consecutivo_comprobante` int DEFAULT NULL,
  `cajero_id` int DEFAULT NULL,
  `retefuente` decimal(18,2) NOT NULL DEFAULT '0.00',
  `reteiva` decimal(18,2) NOT NULL DEFAULT '0.00',
  `reteica` decimal(18,2) NOT NULL DEFAULT '0.00',
  `fecha_recibida` timestamp NULL DEFAULT NULL,
  `plazo` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ordenes_proveedor` (`proveedor_id`),
  KEY `fk_ordenes_bodega` (`bodega_id`),
  CONSTRAINT `fk_ordenes_bodega` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`codigo`),
  CONSTRAINT `fk_ordenes_proveedor` FOREIGN KEY (`proveedor_id`) REFERENCES `terceros` (`tercero_id`)
) ENGINE=InnoDB AUTO_INCREMENT=174 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `origen_destino_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `origen_destino_inventario` (
  `origen_destino_inventario_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `tipo` enum('BODEGA','PROCESO') DEFAULT 'BODEGA',
  `bodega_id` int DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`origen_destino_inventario_id`),
  KEY `bodega_id` (`bodega_id`),
  CONSTRAINT `origen_destino_inventario_ibfk_1` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `paises`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `paises` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoPais` int DEFAULT NULL,
  `pais` varchar(50) DEFAULT NULL,
  `codigoLetras` varchar(5) DEFAULT '0',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=233 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `numero_pedido` varchar(50) DEFAULT NULL,
  `cliente_id` int NOT NULL,
  `bodega_id` int NOT NULL,
  `vendedor_id` int DEFAULT NULL,
  `cotizacion_id` bigint DEFAULT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_entrega_esperada` date NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  `observaciones` text,
  `gravada` decimal(18,2) NOT NULL DEFAULT '0.00',
  `iva` decimal(18,2) NOT NULL DEFAULT '0.00',
  `descuentos` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_pedido` decimal(18,2) NOT NULL DEFAULT '0.00',
  `usuario_creacion` varchar(100) NOT NULL,
  `fecha_creacion` date NOT NULL,
  `cajero_id` int DEFAULT NULL,
  `retefuente` decimal(15,2) NOT NULL DEFAULT '0.00',
  `reteiva` decimal(15,2) NOT NULL DEFAULT '0.00',
  `reteica` decimal(15,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `cliente_id` (`cliente_id`),
  KEY `bodega_id` (`bodega_id`),
  KEY `vendedor_id` (`vendedor_id`),
  KEY `cotizacion_id` (`cotizacion_id`),
  KEY `fk_pedidos_cajeros` (`cajero_id`),
  CONSTRAINT `fk_pedidos_cajeros` FOREIGN KEY (`cajero_id`) REFERENCES `cajeros` (`cajero_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `pedidos_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `terceros` (`tercero_id`),
  CONSTRAINT `pedidos_ibfk_2` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`codigo`),
  CONSTRAINT `pedidos_ibfk_3` FOREIGN KEY (`vendedor_id`) REFERENCES `vendedores` (`vendedor_id`),
  CONSTRAINT `pedidos_ibfk_4` FOREIGN KEY (`cotizacion_id`) REFERENCES `cotizaciones` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `periodos_contables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `periodos_contables` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `anio` int NOT NULL,
  `mes` int NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'ABIERTO',
  `fecha_cierre` datetime DEFAULT NULL,
  `usuario_cierre` varchar(80) DEFAULT NULL,
  `observaciones` varchar(500) DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_periodo_anio_mes` (`anio`,`mes`),
  KEY `idx_periodo_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permisos` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `permisos_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permisos_roles` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigopermiso` int DEFAULT NULL,
  `codigorol` int DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `codigousuariocreado` int DEFAULT '0',
  `fechacreado` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `precios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `precios` (
  `precio_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`precio_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `precios_producto_variante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `precios_producto_variante` (
  `precios_producto_id` int NOT NULL AUTO_INCREMENT,
  `producto_variantes_id` bigint NOT NULL,
  `precio_id` int NOT NULL,
  `valor` decimal(10,2) DEFAULT '0.00',
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `fecha_modificacion` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `fecha_inicio` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_fin` datetime DEFAULT CURRENT_TIMESTAMP,
  `predeterminada` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`precios_producto_id`),
  UNIQUE KEY `uq_precio_variante` (`precio_id`,`producto_variantes_id`),
  KEY `producto_variantes_id` (`producto_variantes_id`),
  KEY `precio_id` (`precio_id`),
  CONSTRAINT `precios_producto_variante_ibfk_1` FOREIGN KEY (`producto_variantes_id`) REFERENCES `producto_variantes` (`producto_variantes_id`),
  CONSTRAINT `precios_producto_variante_ibfk_2` FOREIGN KEY (`precio_id`) REFERENCES `precios` (`precio_id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `producto_variantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_variantes` (
  `producto_variantes_id` bigint NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `sku` varchar(120) DEFAULT NULL,
  `referencia_variantes` varchar(200) DEFAULT NULL,
  `precio` decimal(18,2) DEFAULT NULL,
  `codigo_barras` varchar(120) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `predeterminada` tinyint(1) DEFAULT NULL,
  `imagen` longtext,
  `ultima_fecha_venta` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`producto_variantes_id`),
  KEY `producto_id` (`producto_id`),
  CONSTRAINT `producto_variantes_ibfk_1` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`producto_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `producto_variantes_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_variantes_detalle` (
  `producto_variantes_detalle_id` bigint NOT NULL AUTO_INCREMENT,
  `producto_variantes_id` bigint NOT NULL,
  `caracteristica_id` bigint NOT NULL,
  PRIMARY KEY (`producto_variantes_detalle_id`),
  KEY `producto_variantes_id` (`producto_variantes_id`),
  KEY `caracteristica_id` (`caracteristica_id`),
  CONSTRAINT `producto_variantes_detalle_ibfk_1` FOREIGN KEY (`producto_variantes_id`) REFERENCES `producto_variantes` (`producto_variantes_id`) ON DELETE CASCADE,
  CONSTRAINT `producto_variantes_detalle_ibfk_2` FOREIGN KEY (`caracteristica_id`) REFERENCES `caracteristicas` (`caracteristica_id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `producto_id` int NOT NULL AUTO_INCREMENT,
  `codigo_contable` varchar(50) DEFAULT NULL,
  `codigo_barras` varchar(50) DEFAULT NULL,
  `referencia` varchar(50) DEFAULT NULL,
  `descripcion` varchar(100) DEFAULT NULL,
  `costo` decimal(10,2) DEFAULT '0.00',
  `impuesto_id` int DEFAULT NULL,
  `linea_id` int DEFAULT NULL,
  `grupo_id` int DEFAULT NULL,
  `tipo_producto_id` int DEFAULT NULL,
  `usuario_creo_id` int DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `codigo_usuario_modifico` int DEFAULT NULL,
  `fecha_modificacion` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `fecha_ultima_venta` datetime DEFAULT NULL,
  `fecha_ultima_compra` datetime DEFAULT NULL,
  `manifiesto` varchar(100) DEFAULT NULL,
  `maneja_variantes` tinyint(1) DEFAULT NULL,
  `imagen` longtext,
  PRIMARY KEY (`producto_id`),
  KEY `impuesto_id` (`impuesto_id`),
  KEY `linea_id` (`linea_id`),
  KEY `grupo_id` (`grupo_id`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`impuesto_id`) REFERENCES `impuestos` (`codigo`),
  CONSTRAINT `productos_ibfk_2` FOREIGN KEY (`linea_id`) REFERENCES `lineas` (`linea_id`),
  CONSTRAINT `productos_ibfk_3` FOREIGN KEY (`grupo_id`) REFERENCES `grupos` (`grupo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `recibo_caja_medio_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recibo_caja_medio_pago` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `recibo_caja_id` bigint NOT NULL,
  `metodo_pago_id` int NOT NULL,
  `monto` decimal(18,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `recibo_caja_id` (`recibo_caja_id`),
  KEY `metodo_pago_id` (`metodo_pago_id`),
  CONSTRAINT `recibo_caja_medio_pago_ibfk_1` FOREIGN KEY (`recibo_caja_id`) REFERENCES `recibos_caja` (`id`),
  CONSTRAINT `recibo_caja_medio_pago_ibfk_2` FOREIGN KEY (`metodo_pago_id`) REFERENCES `metodos_pago` (`metodo_pago_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `recibos_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recibos_caja` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `consecutivo` int NOT NULL,
  `tercero_id` int DEFAULT NULL,
  `tercero_nombre` varchar(200) DEFAULT NULL,
  `tercero_nit` varchar(50) DEFAULT NULL,
  `fecha` date NOT NULL,
  `subtotal` decimal(15,2) NOT NULL DEFAULT '0.00',
  `retefuente` decimal(15,2) NOT NULL DEFAULT '0.00',
  `reteica` decimal(15,2) NOT NULL DEFAULT '0.00',
  `reteiva` decimal(15,2) NOT NULL DEFAULT '0.00',
  `descuento` decimal(15,2) NOT NULL DEFAULT '0.00',
  `averias` decimal(15,2) NOT NULL DEFAULT '0.00',
  `fletes` decimal(15,2) NOT NULL DEFAULT '0.00',
  `total` decimal(15,2) NOT NULL DEFAULT '0.00',
  `metodo_pago_id` int DEFAULT NULL,
  `concepto` text,
  `centro_costo` varchar(100) DEFAULT NULL,
  `estado` varchar(30) NOT NULL DEFAULT 'ACTIVO',
  `usuario_id` int DEFAULT NULL,
  `cajero_id` int DEFAULT NULL,
  `vendedor_id` int DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_recibo` date DEFAULT NULL,
  `concepto_abierto` tinyint(1) NOT NULL DEFAULT '0',
  `monto_concepto_abierto` decimal(18,2) DEFAULT NULL,
  `concepto_abierto_id` bigint DEFAULT NULL,
  `cuenta_contable_id` int DEFAULT NULL,
  `beneficiario_nombre` varchar(200) DEFAULT NULL,
  `beneficiario_documento` varchar(50) DEFAULT NULL,
  `motivo_anulacion` text,
  `fecha_anulacion` datetime DEFAULT NULL,
  `anulado_por_usuario_id` int DEFAULT NULL,
  `comprobante_id` bigint DEFAULT NULL,
  `numero_documento` varchar(40) DEFAULT NULL,
  `saldo_favor_usado` decimal(18,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `tercero_id` (`tercero_id`),
  KEY `cajero_id` (`cajero_id`),
  KEY `metodo_pago_id` (`metodo_pago_id`),
  KEY `idx_recibos_caja_vendedor` (`vendedor_id`),
  CONSTRAINT `recibos_caja_ibfk_1` FOREIGN KEY (`tercero_id`) REFERENCES `terceros` (`tercero_id`),
  CONSTRAINT `recibos_caja_ibfk_2` FOREIGN KEY (`cajero_id`) REFERENCES `cajeros` (`cajero_id`),
  CONSTRAINT `recibos_caja_ibfk_3` FOREIGN KEY (`metodo_pago_id`) REFERENCES `metodos_pago` (`metodo_pago_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `regimen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `regimen` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoRegimen` varchar(20) DEFAULT NULL,
  `descripcion` varchar(45) DEFAULT NULL,
  `estado` varchar(8) DEFAULT 'ACTIVO',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `resoluciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resoluciones` (
  `resolucion_id` int NOT NULL AUTO_INCREMENT,
  `numero_formulario` varchar(150) NOT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  PRIMARY KEY (`resolucion_id`),
  UNIQUE KEY `uq_resolucion_numero_formulario` (`numero_formulario`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `resoluciones_comprobantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resoluciones_comprobantes` (
  `resolucion_comprobantes_id` int NOT NULL AUTO_INCREMENT,
  `resolucion_id` int NOT NULL,
  `comprobante_id` int NOT NULL,
  PRIMARY KEY (`resolucion_comprobantes_id`),
  KEY `resolucion_id` (`resolucion_id`),
  KEY `comprobante_id` (`comprobante_id`),
  CONSTRAINT `resoluciones_comprobantes_ibfk_1` FOREIGN KEY (`resolucion_id`) REFERENCES `resoluciones` (`resolucion_id`),
  CONSTRAINT `resoluciones_comprobantes_ibfk_2` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`comprobante_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `retenciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `retenciones` (
  `retencion_id` int NOT NULL AUTO_INCREMENT,
  `codigo` int NOT NULL DEFAULT '0',
  `nombre` varchar(100) DEFAULT NULL,
  `base` decimal(10,2) NOT NULL DEFAULT '0.00',
  `porcentaje` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`retencion_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `retenciones_terceros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `retenciones_terceros` (
  `retenciontercero_id` int NOT NULL AUTO_INCREMENT,
  `retencion_id` int NOT NULL,
  `tercero_id` int NOT NULL,
  PRIMARY KEY (`retenciontercero_id`),
  KEY `retencion_id` (`retencion_id`),
  KEY `tercero_id` (`tercero_id`),
  CONSTRAINT `retenciones_terceros_ibfk_1` FOREIGN KEY (`retencion_id`) REFERENCES `retenciones` (`retencion_id`),
  CONSTRAINT `retenciones_terceros_ibfk_2` FOREIGN KEY (`tercero_id`) REFERENCES `terceros` (`tercero_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sedes_tercero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sedes_tercero` (
  `sede_id` int NOT NULL AUTO_INCREMENT,
  `tercero_id` int NOT NULL,
  `nombre_sede` varchar(100) NOT NULL,
  `direccion` varchar(200) NOT NULL,
  `telefono` varchar(50) DEFAULT NULL,
  `departamento_id` int DEFAULT NULL,
  `municipio_id` int DEFAULT NULL,
  `principal` tinyint(1) NOT NULL DEFAULT '0',
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`sede_id`),
  KEY `fk_sede_tercero` (`tercero_id`),
  KEY `fk_sede_departamento` (`departamento_id`),
  KEY `fk_sede_municipio` (`municipio_id`),
  CONSTRAINT `fk_sede_departamento` FOREIGN KEY (`departamento_id`) REFERENCES `departamentos` (`codigo`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_sede_municipio` FOREIGN KEY (`municipio_id`) REFERENCES `municipios` (`codigo`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_sede_tercero` FOREIGN KEY (`tercero_id`) REFERENCES `terceros` (`tercero_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sesiones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sesiones` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigousuario` int NOT NULL,
  `fechainicio` datetime DEFAULT CURRENT_TIMESTAMP,
  `fechafin` datetime DEFAULT '1990-06-06 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `ipclient` varchar(14) DEFAULT NULL,
  `token` varchar(300) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `terceros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terceros` (
  `tercero_id` int NOT NULL AUTO_INCREMENT,
  `tipo_identificacion_id` int NOT NULL,
  `identificacion` varchar(100) NOT NULL,
  `dv` varchar(20) DEFAULT NULL,
  `nombre_1` varchar(100) DEFAULT NULL,
  `nombre_2` varchar(100) DEFAULT NULL,
  `apellido_1` varchar(100) DEFAULT NULL,
  `apellido_2` varchar(100) DEFAULT NULL,
  `razon_social` varchar(100) DEFAULT NULL,
  `regimen_id` int NOT NULL,
  `clasificacion_tercero_id` int NOT NULL,
  `direccion` varchar(100) DEFAULT NULL,
  `departamento_id` int DEFAULT NULL COMMENT 'FK al departamento del tercero',
  `ciudad_id` int DEFAULT NULL COMMENT 'FK al municipio (ciudad) del tercero',
  `codigo_postal` varchar(20) DEFAULT NULL COMMENT 'Código postal del tercero',
  `correo` varchar(100) DEFAULT NULL,
  `plazo` int NOT NULL DEFAULT '0',
  `cupo` int NOT NULL DEFAULT '0',
  `precio_id` int NOT NULL DEFAULT '0',
  `fecha_nacimiento` varchar(45) DEFAULT NULL,
  `matricula_mercantil` varchar(45) DEFAULT NULL,
  `actividad_economica_id` int DEFAULT '0',
  `tipo_persona_id` int DEFAULT NULL,
  `ultimo_movimiento` timestamp NULL DEFAULT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'ACTIVO',
  `saldofavor_cliente` double DEFAULT '0',
  `saldofavor_empresa` double DEFAULT '0',
  PRIMARY KEY (`tercero_id`),
  KEY `regimen_id` (`regimen_id`),
  KEY `clasificacion_tercero_id` (`clasificacion_tercero_id`),
  KEY `precio_id` (`precio_id`),
  KEY `fk_terceros_departamento` (`departamento_id`),
  KEY `fk_terceros_ciudad` (`ciudad_id`),
  CONSTRAINT `fk_terceros_ciudad` FOREIGN KEY (`ciudad_id`) REFERENCES `municipios` (`codigo`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_terceros_departamento` FOREIGN KEY (`departamento_id`) REFERENCES `departamentos` (`codigo`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `terceros_ibfk_1` FOREIGN KEY (`regimen_id`) REFERENCES `regimen` (`codigo`),
  CONSTRAINT `terceros_ibfk_2` FOREIGN KEY (`clasificacion_tercero_id`) REFERENCES `clasificaciones_terceros` (`clasificacion_tercero_id`),
  CONSTRAINT `terceros_ibfk_3` FOREIGN KEY (`precio_id`) REFERENCES `precios` (`precio_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_caracteristica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_caracteristica` (
  `tipo_caracteristica_id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`tipo_caracteristica_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_contacto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_contacto` (
  `tipo_contacto_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(150) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`tipo_contacto_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_producto` (
  `tipo_producto_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  `descripcion` varchar(100) DEFAULT NULL,
  `estado` tinyint(1) DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tipo_producto_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_totales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_totales` (
  `tipo_total_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `sigla` varchar(50) NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `tipo` enum('IMPUESTO','BASE','DESCUENTO') DEFAULT 'IMPUESTO',
  PRIMARY KEY (`tipo_total_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_totales_facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_totales_facturas` (
  `tipo_total_factura_id` int NOT NULL AUTO_INCREMENT,
  `tipo_total_id` int NOT NULL,
  `factura_id` int NOT NULL,
  `base` decimal(10,2) DEFAULT '0.00',
  `valor` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`tipo_total_factura_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93362 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_transaccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_transaccion` (
  `tipo_transaccion_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`tipo_transaccion_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipoidentificacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipoidentificacion` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoTipoIdentificacion` int DEFAULT NULL,
  `tipoIdentificacion` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipopersona`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipopersona` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipos_comprobante_manual`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipos_comprobante_manual` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `prefijo` varchar(20) DEFAULT NULL,
  `siguiente_consecutivo` int NOT NULL DEFAULT '1',
  `estado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tipo_comprobante_manual_codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `transacciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transacciones` (
  `transaccion_id` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `documento_tipo` varchar(100) NOT NULL,
  `documento_id` int NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `usuario_id` int NOT NULL,
  `creado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`transaccion_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `transacciones_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `unidades_medida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unidades_medida` (
  `unidad_medida_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `sigla` varchar(10) NOT NULL,
  PRIMARY KEY (`unidad_medida_id`),
  UNIQUE KEY `uq_unidades_medida_sigla` (`sigla`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `unidades_medida_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unidades_medida_producto` (
  `producto_id` int NOT NULL,
  `unidad_medida_id` int NOT NULL,
  PRIMARY KEY (`producto_id`,`unidad_medida_id`),
  KEY `unidad_medida_id` (`unidad_medida_id`),
  CONSTRAINT `unidades_medida_producto_ibfk_1` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`producto_id`),
  CONSTRAINT `unidades_medida_producto_ibfk_2` FOREIGN KEY (`unidad_medida_id`) REFERENCES `unidades_medida` (`unidad_medida_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `usuariobodega`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuariobodega` (
  `id_usuariobodega` int NOT NULL AUTO_INCREMENT,
  `usuarioid` int DEFAULT NULL,
  `bodegaid` int DEFAULT NULL,
  PRIMARY KEY (`id_usuariobodega`),
  KEY `usuarioid` (`usuarioid`),
  KEY `bodegaid` (`bodegaid`),
  CONSTRAINT `usuariobodega_ibfk_1` FOREIGN KEY (`usuarioid`) REFERENCES `usuarios` (`codigo`),
  CONSTRAINT `usuariobodega_ibfk_2` FOREIGN KEY (`bodegaid`) REFERENCES `bodegas` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `usuario` varchar(300) NOT NULL,
  `contrasena` text NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `codigorol` int DEFAULT '0',
  `codigousuariocreado` int DEFAULT '0',
  `fechacreado` date NOT NULL DEFAULT (curdate()),
  `fechamodificado` date NOT NULL DEFAULT '1990-06-06',
  `avatar` longblob,
  `avatar_tipo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codigo`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `usuarios_clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios_clientes` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `identificacion` varchar(10) NOT NULL,
  `nombres` varchar(100) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `codigousuariocreado` int DEFAULT '0',
  `correo` varchar(100) DEFAULT NULL,
  `direccion` varchar(100) DEFAULT NULL,
  `apellido` varchar(100) DEFAULT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `tipoimagen` varchar(100) DEFAULT NULL,
  `imagenperfil` blob,
  `fechacreado` date NOT NULL DEFAULT (curdate()),
  `fechamodificado` date NOT NULL DEFAULT '1990-06-06',
  PRIMARY KEY (`codigo`),
  UNIQUE KEY `identificacion` (`identificacion`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `usuarios_clientes_usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios_clientes_usuarios` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigocliente` int DEFAULT NULL,
  `codigousuario` int DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `usuarios_vendedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios_vendedor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vendedor_id` int DEFAULT NULL,
  `usuario_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `vendedor_id` (`vendedor_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `usuarios_vendedor_ibfk_1` FOREIGN KEY (`vendedor_id`) REFERENCES `vendedores` (`vendedor_id`),
  CONSTRAINT `usuarios_vendedor_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `vendedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendedores` (
  `vendedor_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `direccion` varchar(50) NOT NULL,
  `telefono` varchar(50) NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `codigo_usuario_creo` int DEFAULT '0',
  `fechacreado` date NOT NULL DEFAULT (curdate()),
  `comision` decimal(10,0) DEFAULT '0',
  `correo` varchar(100) DEFAULT NULL,
  `tipo_vendedor` enum('INTERNO','EXTERNO') DEFAULT NULL,
  `identificacion` varchar(50) DEFAULT NULL,
  `meta_ventas` double DEFAULT NULL,
  `bodega_id` int DEFAULT NULL,
  PRIMARY KEY (`vendedor_id`),
  KEY `fk_vendedores_bodega` (`bodega_id`),
  CONSTRAINT `fk_vendedores_bodega` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `venta_metodos_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venta_metodos_pago` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `venta_id` bigint NOT NULL,
  `metodo_pago_id` int NOT NULL,
  `monto` decimal(18,2) NOT NULL,
  `plazo_en_dias` int DEFAULT NULL,
  `referencia` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_vmp_venta` (`venta_id`),
  KEY `idx_vmp_metodo_pago` (`metodo_pago_id`),
  CONSTRAINT `fk_vmp_metodo_pago` FOREIGN KEY (`metodo_pago_id`) REFERENCES `metodos_pago` (`metodo_pago_id`),
  CONSTRAINT `fk_vmp_venta` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ventas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `numero_venta` varchar(50) NOT NULL,
  `cliente_id` int NOT NULL,
  `bodega_id` int NOT NULL,
  `cajero_id` int DEFAULT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_entrega_esperada` date NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  `observaciones` text,
  `gravada` decimal(18,2) NOT NULL DEFAULT '0.00',
  `iva` decimal(18,2) NOT NULL DEFAULT '0.00',
  `descuentos` decimal(18,2) NOT NULL DEFAULT '0.00',
  `total_venta` decimal(18,2) NOT NULL DEFAULT '0.00',
  `usuario_creacion` varchar(100) NOT NULL,
  `fecha_creacion` timestamp NOT NULL,
  `vendedor_id` int DEFAULT NULL,
  `comprobante_id` bigint DEFAULT NULL,
  `consecutivo_comprobante` int DEFAULT NULL,
  `retefuente` decimal(18,2) NOT NULL DEFAULT '0.00',
  `reteiva` decimal(18,2) NOT NULL DEFAULT '0.00',
  `reteica` decimal(18,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `idx_venta_cliente` (`cliente_id`),
  KEY `idx_venta_cajero` (`cajero_id`),
  KEY `idx_venta_estado` (`estado`),
  KEY `idx_venta_fecha` (`fecha_emision`),
  KEY `fk_venta_bodega` (`bodega_id`),
  KEY `fk_ventas_vendedor` (`vendedor_id`),
  CONSTRAINT `fk_venta_bodega` FOREIGN KEY (`bodega_id`) REFERENCES `bodegas` (`codigo`),
  CONSTRAINT `fk_venta_cajero` FOREIGN KEY (`cajero_id`) REFERENCES `cajeros` (`cajero_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_venta_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `terceros` (`tercero_id`),
  CONSTRAINT `fk_ventas_vendedor` FOREIGN KEY (`vendedor_id`) REFERENCES `vendedores` (`vendedor_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

