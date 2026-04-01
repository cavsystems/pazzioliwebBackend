/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

SET FOREIGN_KEY_CHECKS = 0;

--
-- Table structure for table `grupos`
--

DROP TABLE IF EXISTS `grupos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grupos` (
  `grupo_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`grupo_id`),
  UNIQUE KEY uq_grupos_descripcion (descripcion)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grupos`
--

--
-- Table structure for table `lineas`
--

DROP TABLE IF EXISTS `lineas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lineas` (
  `linea_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`linea_id`),
  UNIQUE KEY uq_lineas_descripcion (descripcion)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lineas`
--

--
-- Table structure for table `bodegas`
--

DROP TABLE IF EXISTS `bodegas`;
create table bodegas(
 codigo  int auto_increment,
 nombre varchar(50) not null,
 codigosucursal varchar(50),
 codigopais int,
 codigodepartamento int,
 codigomunicipio int,
 codigopostal varchar(50),
 direccion varchar(50),
 telefono varchar(50),
 celular varchar(50),
 correo varchar(50),
 primary key(codigo));
 
--
-- Dumping data for table `bodegas`
--

 --
-- Table structure for table `impuestos`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `impuestos`
INSERT INTO `impuestos` VALUES (1,'IMPUESTO SOBRE LAS VENTAS',5,0,'IVA','INACTIVO'),(2,'IMPUESTO SOBRE LAS VENTAS',19,0,'IVA','INACTIVO'),(3,'IMPUESTO NACIONAL AL CONSUMIDOR',8,0,'INC','INACTIVO'),(4,'EXENTO DE IVA',0,0,'EXN','INACTIVO'),(5,'EXCLUIDO DE IVA',-1,0,'EXC','INACTIVO'),(6,'IMPUESTO NACIONAL AL CONSUMO DE BOLSAS PLASTICAS',70,0,'INCBP','INACTIVO');
--

LOCK TABLES `impuestos` WRITE;
/*!40000 ALTER TABLE `impuestos` DISABLE KEYS */;

/*!40000 ALTER TABLE `impuestos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `producto_id` int NOT NULL AUTO_INCREMENT,
  `codigo_contable` varchar(50) DEFAULT NULL,
  `codigo_barras` varchar(50) DEFAULT NULL,
  `referencia` varchar(50) DEFAULT NULL,
  `descripcion` varchar(100) DEFAULT NULL,
  `costo` decimal(10,2) DEFAULT 0.00,
  `impuesto_id` int DEFAULT NULL,
  `linea_id` int DEFAULT NULL,
  `grupo_id` int DEFAULT NULL,
  `usuario_creo_id` int DEFAULT NULL,
  `fecha_creacion` dateTime DEFAULT CURRENT_TIMESTAMP,
  `codigo_usuario_modifico` int DEFAULT NULL,
  `fecha_modificacion` dateTime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `estado` ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `fecha_ultima_venta` dateTime DEFAULT NULL,
  `fecha_ultima_compra` dateTime DEFAULT NULL,
  PRIMARY KEY (`producto_id`),
  FOREIGN KEY (impuesto_id) REFERENCES impuestos(codigo),
  FOREIGN KEY (linea_id) REFERENCES lineas(linea_id),
  FOREIGN KEY (grupo_id) REFERENCES grupos(grupo_id)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

DROP TABLE IF EXISTS `precios_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `precios_producto` (
  `precios_producto_id` int NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `precio_id` int NOT NULL,
  `valor` decimal(10,2) DEFAULT 0.00,
  `fecha_creacion` dateTime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `fecha_modificacion` dateTime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `fecha_inicio` dateTime DEFAULT CURRENT_TIMESTAMP,
  `fecha_fin` dateTime DEFAULT CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`precios_producto_id`),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id),
  FOREIGN KEY (precio_id) REFERENCES precios(precio_id)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `precios_producto`
--

DROP TABLE IF EXISTS `unidades_medida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unidades_medida` (
  `unidad_medida_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `sigla` varchar(10) NOT NULL,
  PRIMARY KEY (`unidad_medida_id`),
  UNIQUE KEY uq_unidades_medida_sigla (sigla)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `unidades_medida`
--

DROP TABLE IF EXISTS `unidades_medida_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unidades_medida_producto` (
  `producto_id` int NOT NULL,
  `unidad_medida_id` int NOT NULL,
  PRIMARY KEY (`producto_id`,`unidad_medida_id`),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id),
  FOREIGN KEY (unidad_medida_id) REFERENCES unidades_medida(unidad_medida_id)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `unidades_medida_producto`
--

DROP TABLE IF EXISTS `precios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `precios` (
  `precio_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`precio_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `precios`
--

--
-- Table structure for table `caracteristicas`
--

DROP TABLE IF EXISTS `caracteristicas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `caracteristicas` (
  `caracteristica_id` int NOT NULL AUTO_INCREMENT,
  `caracteristica` varchar(100) NOT NULL,
  PRIMARY KEY (`caracteristica_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `caracteristicas`
--

--
-- Table structure for table `caracteristicas_producto`
--

DROP TABLE IF EXISTS `caracteristicas_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `caracteristicas_producto` (
  `producto_id` int NOT NULL,
  `caracteristica_id` int NOT NULL,
  PRIMARY KEY (`producto_id`,`caracteristica_id`),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id),
  FOREIGN KEY (caracteristica_id) REFERENCES caracteristicas(caracteristica_id)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `caracteristicas_producto`
--

--
-- Table structure for table `existencias`
--

DROP TABLE IF EXISTS `existencias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `existencias` (
  `existencia_id` int NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `bodega_id` int NOT NULL,
  `existencia` decimal(10,2) DEFAULT 0.00,
  `stock_min` decimal(10,2) DEFAULT 0.00,
  `stock_max` decimal(10,2) DEFAULT 0.00,
  `fecha_ultimo_movimiento` dateTime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`existencia_id`),
  UNIQUE KEY uq_existencias_producto_bodega (producto_id, bodega_id),
  FOREIGN KEY (producto_id) REFERENCES productos(producto_id),
  FOREIGN KEY (bodega_id) REFERENCES bodegas(codigo)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `existencias`
--

--
-- Table structure for table `actividadeconomica`
--

DROP TABLE IF EXISTS `actividadeconomica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actividadeconomica` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `descripcionActividad` text,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actividadeconomica`
--

LOCK TABLES `actividadeconomica` WRITE;
/*!40000 ALTER TABLE `actividadeconomica` DISABLE KEYS */;
INSERT INTO `actividadeconomica` VALUES (111,'Cultivo de cereales (excepto arroz), legumbres y semillas oleaginosas'),(112,'Cultivo de arroz'),(113,'Cultivo de hortalizas, raíces y tubérculos'),(114,'Cultivo de tabaco'),(115,'Cultivo de plantas textiles'),(119,'Otros cultivos transitorios n.c.p.'),(121,'Cultivo de frutas tropicales y subtropicales'),(122,'Cultivo de plátano y banano'),(123,'Cultivo de café'),(124,'Cultivo de caña de azúcar'),(125,'Cultivo de flor de corte'),(126,'Cultivo de palma para aceite (palma africana) y otros frutos oleaginosos'),(127,'Cultivo de plantas con las que se prepararan bebidas'),(128,'Cultivo de especias y de plantas aromáticas y medicinales'),(129,'Otros cultivos permanentes n.c.p.'),(130,'Propagación de plantas (actividades de los viveros, excepto viveros\nforestales)'),(141,'Cría de ganado bovino y bufalino'),(142,'Cría de caballos y otros equinos'),(143,'Cría de ovejas y cabras'),(144,'Cría de ganado porcino'),(145,'Cría de aves de corral'),(149,'Cría de otros animales n.c.p.'),(150,'Explotación primaria mixta (agrícola y pecuaria)'),(161,'Actividades de apoyo a la agricultura'),(162,'Actividades de apoyo a la ganadería'),(163,'Actividades posteriores a la cosecha'),(164,'Tratamiento de semillas para propagación'),(170,'Caza ordinaria y mediante trampas y actividades de servicios conexas'),(210,'Silvicultura y otras actividades forestales'),(220,'Extracción de madera'),(230,'Recolección de productos forestales diferentes a la madera'),(240,'Servicios de apoyo a la silvicultura'),(311,'Pesca marítima'),(312,'Pesca de agua dulce'),(321,'Acuicultura marítima'),(322,'Acuicultura de agua dulce'),(510,'Extracción de hulla (carbón de piedra)'),(520,'Extracción de carbón lignito'),(610,'Extracción de petróleo crudo'),(620,'Extracción de gas natural'),(710,'Extracción de minerales de hierro'),(721,'Extracción de minerales de uranio y de torio'),(722,'Extracción de oro y otros metales preciosos'),(723,'Extracción de minerales de níquel'),(729,'Extracción de otros minerales metalíferos no ferrosos n.c.p.'),(811,'Extracción de piedra, arena,\narcillas comunes, yeso y\nanhidrita'),(812,'Extracción de arcillas de uso industrial, caliza, caolín y bentonitas'),(820,'Extracción de esmeraldas, piedras preciosas y semipreciosas'),(891,'Extracción de minerales para la fabricación de abonos y productos químicos'),(892,'Extracción de halita (sal)'),(899,'Extracción de otros minerales no metálicos n.c.p.'),(910,'Actividades de apoyo para la extracción de petróleo y de gas natural'),(990,'Actividades de apoyo para otras actividades de explotación de minas y canteras'),(1011,'Procesamiento y conservación de carne y productos cárnicos'),(1012,'Procesamiento y conservación de pescados, crustáceos y moluscos'),(1030,'Elaboración de aceites y grasas de origen vegetal y animal'),(1051,'Elaboración de productos de molinería'),(1052,'Elaboración de almidones y productos derivados del almidón'),(1061,'Trilla de café'),(1062,'Descafeinado, tostión y molienda del café'),(1063,'Elaboración de otros derivados del café'),(1071,'Elaboración y refinación de azúcar'),(1072,'Elaboración de panela'),(1081,'Elaboración de productos de panadería'),(1082,'Elaboración de cacao, chocolate y productos de confitería'),(1083,'Elaboración de macarrones, fideos, alcuzcuz y productos farináceos similares'),(1084,'Elaboración de comidas y platos preparados'),(1089,'Elaboración de otros productos alimenticios n.c.p.'),(1090,'Elaboración de alimentos preparados para animales'),(1101,'Destilación, rectificación y mezcla de bebidas alcohólicas'),(1102,'Elaboración de bebidas fermentadas no destiladas'),(1103,'Producción de malta, elaboración de cervezas y otras bebidas malteadas'),(1104,'Elaboración de bebidas no alcohólicas, producción de aguas minerales y de otras aguas embotelladas'),(1200,'Elaboración de productos de tabaco'),(1311,'Preparación e hilatura de fibras textiles'),(1312,'Tejeduría de productos textiles'),(1313,'Acabado de productos textiles'),(1391,'Fabricación de tejidos de punto y ganchillo'),(1392,'Confección de artículos con materiales textiles, excepto prendas de vestir'),(1393,'Fabricación de tapetes y alfombras para pisos'),(1394,'Fabricación de cuerdas, cordeles, cables, bramantes y redes'),(1399,'Fabricación de otros artículos textiles n.c.p.'),(1410,'Confección de prendas de vestir, excepto prendas de piel'),(1511,'Curtido y recurtido de cueros; recurtido y teñido de pieles.'),(1512,'Fabricación de artículos de viaje, bolsos de mano y artículos similares elaborados en cuero, y fabricación de artículos de talabartería y guarnicionería.'),(1513,'Fabricación de artículos de viaje, bolsos de mano y artículos similares; artículos de talabartería y guarnicionería elaborados en otros materiales'),(1521,'Fabricación de calzado de cuero y piel, con cualquier tipo de suela'),(1522,'Fabricación de otros tipos de calzado, excepto calzado de cuero y piel'),(1523,'Fabricación de partes del calzado'),(1610,'Aserrado, acepillado e impregnación de la madera'),(1620,'Fabricación de hojas de madera para enchapado; fabricación de tableros contrachapados, tableros laminados, tableros de partículas y otros tableros y paneles'),(1630,'Fabricación de partes y piezas de madera, de carpintería y ebanistería para la construcción y para edificios'),(1640,'Fabricación de recipientes de madera'),(1690,'Fabricación de otros productos de madera; fabricación de artículos de corcho, cestería y espartería'),(1701,'Fabricación de pulpas (pastas) celulósicas; papel y cartón'),(1702,'Fabricación de papel y cartón ondulado (corrugado); fabricación de envases, empaques y de embalajes de papel y cartón.'),(1709,'Fabricación de otros artículos de papel y cartón'),(1811,'Actividades de impresión'),(1812,'Actividades de servicios relacionados con la impresión'),(1820,'Producción de copias a partir de grabaciones originales'),(1910,'Fabricación de productos de hornos de coque'),(1921,'Fabricación de productos de la refinación del petróleo '),(1922,'Actividad de mezcla de combustibles'),(2011,'Fabricación de sustancias y productos químicos básicos'),(2012,'Fabricación de abonos y compuestos inorgánicos nitrogenados'),(2013,'Fabricación de plásticos en formas primarias'),(2014,'Fabricación de caucho sintético en formas primarias'),(2021,'Fabricación de plaguicidas y otros productos químicos de uso agropecuario'),(2022,'Fabricación de pinturas, barnices y revestimientos similares, tintas para impresión y masillas'),(2023,'Fabricación de jabones y detergentes, preparados para limpiar y pulir; perfumes y preparados de tocador'),(2029,'Fabricación de otros productos químicos n.c.p.'),(2030,'Fabricación de fibras sintéticas y artificiales'),(2100,'Fabricación de productos farmacéuticos, sustancias químicas medicinales y productos botánicos de uso farmacéutico'),(2211,'Fabricación de llantas y neumáticos de caucho'),(2212,'Reencauche de llantas usadas'),(2219,'Fabricación de formas básicas de caucho y otros productos de caucho n.c.p.'),(2221,'Fabricación de formas básicas de plástico'),(2229,'Fabricación de artículos de plástico n.c.p.'),(2310,'Fabricación de vidrio y productos de vidrio'),(2391,'Fabricación de productos refractarios'),(2392,'Fabricación de materiales de arcilla para la construcción'),(2393,'Fabricación de otros productos de cerámica y porcelana'),(2394,'Fabricación de cemento, cal  y yeso'),(2395,'Fabricación de artículos de hormigón, cemento y yeso'),(2396,'Corte, tallado y acabado de la piedra'),(2399,'Fabricación de otros productos minerales no metálicos n.c.p.'),(2410,'Industrias básicas de hierro y de acero'),(2421,'Industrias básicas de metales preciosos'),(2429,'Industrias básicas de otros metales no ferrosos'),(2431,'Fundición de hierro y de acero'),(2432,'Fundición de metales no ferrosos'),(2511,'Fabricación de productos metálicos para uso estructural'),(2512,'Fabricación de tanques, depósitos y recipientes de metal, excepto los utilizados para el envase o transporte de mercancías'),(2513,'Fabricación de generadores de vapor, excepto calderas de agua caliente para calefacción central'),(2520,'Fabricación de armas y municiones'),(2591,'Forja, prensado, estampado y laminado de metal; pulvimetalurgia'),(2592,'Tratamiento y revestimiento\nde metales; mecanizado'),(2593,'Fabricación de artículos de cuchillería, herramientas de mano y artículos de ferretería'),(2599,'Fabricación de otros productos elaborados de metal n.c.p.'),(2610,'Fabricación de componentes y tableros electrónicos'),(2620,'Fabricación de computadoras y de equipo periférico'),(2630,'Fabricación de equipos de comunicación'),(2640,'Fabricación de aparatos electrónicos de consumo'),(2651,'Fabricación de equipo de medición, prueba, navegación y control '),(2652,'Fabricación de relojes'),(2660,'Fabricación de equipo de irradiación y equipo electrónico de uso médico y terapéutico'),(2670,'Fabricación de instrumentos ópticos y equipo fotográfico '),(2680,'Fabricación de soportes magnéticos y ópticos'),(2711,'Fabricación de motores, generadores y transformadores eléctricos.'),(2712,'Fabricación de aparatos de distribución y control de la energía eléctrica'),(2720,'Fabricación de pilas, baterías y acumuladores eléctricos'),(2731,'Fabricación de hilos y cables eléctricos y de fibra óptica'),(2732,'Fabricación de dispositivos de cableado'),(2740,'Fabricación de equipos eléctricos de iluminación'),(2750,'Fabricación de aparatos de uso doméstico'),(2790,'Fabricación de otros tipos de equipo eléctrico n.c.p.'),(2811,'Fabricación de motores, turbinas, y partes para motores de combustión interna'),(2812,'Fabricación de equipos de potencia hidráulica y neumática'),(2813,'Fabricación de otras bombas, compresores, grifos y válvulas'),(2814,'Fabricación de cojinetes, engranajes, trenes de engranajes y piezas de transmisión'),(2815,'Fabricación de hornos, hogares y quemadores industriales'),(2816,'Fabricación de equipo de elevación y manipulación'),(2817,'Fabricación de maquinaria y equipo de oficina (excepto computadoras y equipo periférico)'),(2818,'Fabricación de herramientas manuales con motor'),(2819,'Fabricación de otros tipos de maquinaria y equipo de uso general n.c.p.'),(2821,'Fabricación de maquinaria agropecuaria y forestal'),(2822,'Fabricación de máquinas formadoras de metal y de máquinas herramienta'),(2823,'Fabricación de maquinaria para la metalurgia'),(2824,'Fabricación de maquinaria para explotación de minas y canteras y para obras de construcción'),(2825,'Fabricación de maquinaria para la elaboración de alimentos, bebidas y tabaco'),(2826,'Fabricación de maquinaria para la elaboración de productos textiles, prendas de vestir y cueros'),(2829,'Fabricación de otros tipos de maquinaria y equipo de uso especial n.c.p.'),(2910,'Fabricación de vehículos automotores y sus motores'),(2920,'Fabricación de carrocerías para vehículos automotores; fabricación de remolques y semirremolques'),(2930,'Fabricación de partes, piezas (autopartes) y accesorios (lujos) para vehículos automotores'),(3011,'Construcción de barcos y de estructuras flotantes'),(3012,'Construcción de embarcaciones de recreo y deporte'),(3020,'Fabricación de locomotoras y de material rodante para ferrocarriles '),(3030,'Fabricación de aeronaves, naves espaciales y de maquinaria conexa'),(3040,'Fabricación de vehículos militares de combate'),(3091,'Fabricación de motocicletas'),(3092,'Fabricación de bicicletas y de sillas de ruedas para personas con discapacidad'),(3099,'Fabricación de otros tipos de equipo de transporte n.c.p.'),(3110,'Fabricación de muebles'),(3120,'Fabricación de colchones y somieres'),(3210,'Fabricación de joyas, bisutería y artículos conexos'),(3220,'Fabricación de instrumentos musicales'),(3230,'Fabricación de artículos y\nequipo para la práctica del\ndeporte (excepto prendas\nde vestir y calzado)'),(3240,'Fabricación de juegos,\njuguetes y rompecabezas'),(3250,'Fabricación de\ninstrumentos, aparatos y\nmateriales médicos y\nodontológicos (incluido\nmobiliario)'),(3290,'Otras industrias manufactureras n.c.p.'),(3311,'Mantenimiento y reparación especializado de productos elaborados en metal'),(3312,'Mantenimiento y reparación especializado de maquinaria y equipo'),(3313,'Mantenimiento y reparación especializado de equipo electrónico y óptico'),(3314,'Mantenimiento y reparación especializado de equipo eléctrico'),(3315,'Mantenimiento y reparación especializado de equipo de transporte, excepto los vehículos automotores, motocicletas y bicicletas'),(3319,'Mantenimiento y reparación de otros tipos de equipos y sus componentes n.c.p.'),(3320,'Instalación especializada de maquinaria y equipo industrial'),(3511,'Generación de energía eléctrica'),(3512,'Transmisión de energía eléctrica'),(3513,'Distribución de energía eléctrica'),(3514,'Comercialización de energía eléctrica'),(3530,'Suministro de vapor y aire acondicionado'),(3700,'Evacuación y tratamiento de aguas residuales '),(3811,'Recolección de desechos no peligrosos'),(3812,'Recolección de desechos peligrosos'),(3821,'Tratamiento y disposición de desechos no peligrosos'),(3822,'Tratamiento y disposición de desechos peligrosos'),(3830,'Recuperación de materiales'),(4111,'Construcción de edificios residenciales'),(4112,'Construcción de edificios no residenciales'),(4210,'Construcción de carreteras y vías de ferrocarril'),(4220,'Construcción de proyectos de servicio público'),(4290,'Construcción de otras obras de ingeniería civil'),(4311,'Demolición'),(4312,'Preparación del terreno'),(4321,'Instalaciones eléctricas de la construcción'),(4322,'Instalaciones de fontanería, calefacción y aire acondicionado de la construcción'),(4329,'Otras instalaciones especializadas de la construcción'),(4330,'Terminación y acabado de edificios y obras de ingeniería civil'),(4390,'Otras actividades especializadas para la construcción de edificios y obras de ingeniería civil'),(4511,'Comercio de vehículos automotores nuevos'),(4512,'Comercio de vehículos automotores usados'),(4520,'Mantenimiento y reparación de vehículos automotores.'),(4530,'Comercio de partes, piezas (autopartes) y accesorios (lujos) para vehículos automotores'),(4542,'Mantenimiento y reparación de motocicletas y de sus partes y piezas'),(4610,'Comercio al por mayor a cambio de una retribución o por contrata'),(4631,'Comercio al por mayor de productos alimenticios'),(4641,'Comercio al por mayor de productos textiles y productos confeccionados para uso doméstico '),(4642,'Comercio al por mayor de prendas de vestir'),(4643,'Comercio al por mayor de calzado'),(4644,'Comercio al por mayor de aparatos y equipo de uso doméstico'),(4651,'Comercio al por mayor de computadores, equipo periférico y programas de informática'),(4652,'Comercio al por mayor de equipo, partes y piezas electrónicos y de telecomunicaciones'),(4653,'Comercio al por mayor de maquinaria y equipo agropecuarios'),(4659,'Comercio al por mayor de otros tipos de maquinaria y equipo n.c.p.'),(4662,'Comercio al por mayor de metales y productos metalíferos'),(4664,'Comercio al por mayor de productos químicos básicos, cauchos y plásticos en formas primarias y productos químicos de uso agropecuario'),(4665,'Comercio al por mayor de desperdicios, desechos y chatarra'),(4669,'Comercio al por mayor de otros productos n.c.p.'),(4690,'Comercio al por mayor no especializado'),(4721,'Comercio al por menor de productos agrícolas para el consumo en establecimientos especializados'),(4722,'Comercio al por menor de leche, productos lácteos y huevos, en establecimientos especializados'),(4723,'Comercio al por menor de carnes (incluye aves de corral), productos cárnicos, pescados y productos de mar, en establecimientos especializados'),(4729,'Comercio al por menor de otros productos alimenticios n.c.p., en establecimientos especializados'),(4731,'Comercio al por menor de combustible para automotores'),(4732,'Comercio al por menor de lubricantes (aceites, grasas), aditivos y productos de limpieza para vehículos automotores'),(4741,'Comercio al por menor de computadores, equipos periféricos, programas de informática y equipos de telecomunicaciones en establecimientos especializados'),(4742,'Comercio al por menor de equipos y aparatos de sonido y de video, en establecimientos especializados'),(4751,'Comercio al por menor de productos textiles en establecimientos especializados'),(4753,'Comercio al por menor de tapices, alfombras y recubrimientos para paredes y pisos en establecimientos especializados'),(4754,'Comercio al por menor de electrodomésticos y gasodomésticos de uso doméstico, muebles y equipos de iluminación en establecimientos especializados'),(4755,'Comercio al por menor de artículos y utensilios de uso doméstico en establecimientos especializados'),(4759,'Comercio al por menor de otros artículos domésticos en establecimientos especializados'),(4762,'Comercio al por menor de artículos deportivos, en establecimientos especializados'),(4769,'Comercio al por menor de otros artículos culturales y de entretenimiento n.c.p. en establecimientos especializados'),(4771,'Comercio al por menor de prendas de vestir y sus accesorios (incluye artículos de piel) en establecimientos especializados'),(4772,'Comercio al por menor de todo tipo de calzado y artículos de cuero y sucedáneos del cuero en establecimientos especializados.'),(4774,'Comercio al por menor de otros productos nuevos en establecimientos especializados'),(4775,'Comercio al por menor de artículos de segunda mano'),(4782,'Comercio al por menor de productos textiles, prendas de vestir y calzado, en puestos de venta móviles'),(4789,'Comercio al por menor de otros productos en puestos de venta móviles'),(4911,'Transporte férreo de pasajeros'),(4912,'Transporte férreo de carga'),(4921,'Transporte de pasajeros'),(4922,'Transporte mixto'),(4923,'Transporte de carga por carretera'),(4930,'Transporte por tuberías'),(5011,'Transporte de pasajeros marítimo y de cabotaje'),(5012,'Transporte de carga\nmarítimo y de cabotaje'),(5021,'Transporte fluvial de pasajeros'),(5022,'Transporte fluvial de carga'),(5111,'Transporte aéreo nacional de pasajeros'),(5112,'Transporte aéreo internacional de pasajeros'),(5121,'Transporte aéreo nacional de carga'),(5122,'Transporte aéreo internacional de carga'),(5210,'Almacenamiento y depósito'),(5221,'Actividades de estaciones, vías y servicios complementarios para el transporte terrestre'),(5222,'Actividades de puertos y servicios complementarios para el transporte acuático'),(5223,'Actividades de aeropuertos, servicios de navegación aérea y demás actividades conexas al transporte aéreo'),(5224,'Manipulación de carga'),(5229,'Otras actividades complementarias al transporte'),(5310,'Actividades postales nacionales'),(5320,'Actividades de mensajería'),(5330,'Servicio de pedido, compra, distribución y entrega de productos a través de plataformas o aplicaciones de contacto y que utilizan una red de domiciliarios'),(5511,'Alojamiento en hoteles'),(5512,'Alojamiento en aparta-hoteles'),(5513,'Alojamiento en centros vacacionales'),(5514,'Alojamiento rural'),(5519,'Otros tipos de alojamientos para visitantes'),(5520,'Actividades de zonas de camping y parques para vehículos recreacionales'),(5530,'Servicio por horas  de alojamiento'),(5590,'Otros tipos de alojamiento n.c.p.'),(5611,'Expendio a la mesa de comidas preparadas'),(5612,'Expendio por autoservicio de comidas preparadas'),(5613,'Expendio de comidas preparadas en cafeterías'),(5619,'Otros tipos de expendio de comidas preparadas n.c.p.'),(5621,'Catering para eventos'),(5629,'Actividades de otros servicios de comidas'),(5630,'Expendio de bebidas alcohólicas para el consumo dentro del establecimiento'),(5812,'Edición de directorios y listas de correo'),(5813,'Edición de periódicos, revistas y otras publicaciones periódicas'),(5819,'Otros trabajos de edición'),(5820,'Edición de programas de informática (software)'),(5911,'Actividades de producción de películas cinematográficas, videos, programas, anuncios y comerciales de televisión (excepto programación de televisión)'),(5912,'Actividades de postproducción de películas cinematográficas, videos, programas, anuncios y comerciales de televisión (excepto programación de televisión)'),(5913,'Actividades de distribución de películas cinematográficas, videos, programas, anuncios y comerciales de televisión'),(5914,'Actividades de exhibición de películas cinematográficas y videos'),(5920,'Actividades de grabación de sonido y edición de música'),(6010,'Actividades de programación y transmisión en el servicio de radiodifusión sonora'),(6110,'Actividades de telecomunicaciones alámbricas'),(6120,'Actividades de telecomunicaciones inalámbricas'),(6130,'Actividades de telecomunicación satelital'),(6190,'Otras actividades de telecomunicaciones'),(6201,'Actividades de desarrollo de sistemas informáticos (planificación, análisis, diseño, programación, pruebas)'),(6202,'Actividades de consultoría informática y actividades de administración de instalaciones informáticas'),(6209,'Otras actividades de tecnologías de información y actividades de servicios informáticos'),(6311,'Procesamiento de datos, alojamiento (hosting) y actividades relacionadas'),(6312,'Portales Web'),(6391,'Actividades de agencias de noticias'),(6399,'Otras actividades de servicio de información n.c.p.'),(6411,'Banca Central'),(6412,'Bancos comerciales'),(6421,'Actividades de las corporaciones financieras'),(6422,'Actividades de las compañías de financiamiento'),(6423,'Banca de segundo piso'),(6424,'Actividades de las cooperativas financieras'),(6431,'Fideicomisos, fondos y entidades financieras similares'),(6491,'Leasing financiero (arrendamiento financiero)'),(6492,'Actividades financieras de fondos de empleados y otras formas asociativas del sector solidario'),(6493,'Actividades de compra de cartera o factoring'),(6494,'Otras actividades de distribución de fondos'),(6495,'Instituciones especiales oficiales'),(6496,'Capitalización'),(6511,'Seguros generales'),(6512,'Seguros de vida'),(6513,'Reaseguros'),(6515,'Seguros de salud'),(6521,'Servicios de seguros sociales de salud'),(6522,'Servicios de seguros sociales en riesgos laborales'),(6523,'Servicios de seguros sociales en riesgos familia'),(6531,'Régimen de prima media con prestación definida (RPM)'),(6532,'Régimen de ahorro con solidaridad (RAIS)'),(6612,'Corretaje de valores y de contratos de productos básicos'),(6613,'Otras actividades relacionadas con el mercado de valores'),(6614,'Actividades de las sociedades de intermediación cambiaria y de servicios financieros especiales'),(6615,'Actividades de los profesionales de compra y venta de divisas'),(6619,'Otras actividades auxiliares de las actividades de servicios financieros n.c.p.'),(6621,'Actividades de agentes y corredores de seguros'),(6629,'Evaluación de riesgos y daños, y otras actividades de servicios auxiliares'),(6630,'Actividades de administración de fondos'),(6810,'Actividades inmobiliarias realizadas con bienes propios o arrendados'),(6820,'Actividades inmobiliarias realizadas a cambio de una retribución o por contrata'),(7310,'Publicidad'),(7420,'Actividades de fotografía'),(7500,'Actividades veterinarias'),(7710,'Alquiler y arrendamiento de vehículos automotores'),(7721,'Alquiler y arrendamiento de equipo recreativo y deportivo'),(7722,'Alquiler de videos y discos'),(7729,'Alquiler y arrendamiento de otros efectos personales y enseres domésticos n.c.p.'),(7730,'Alquiler y arrendamiento de otros tipos de maquinaria, equipo y bienes tangibles n.c.p.'),(7740,'Arrendamiento de propiedad intelectual y productos similares, excepto obras protegidas por derechos de autor'),(7810,'Actividades de agencias de gestión y colocación de empleo'),(7820,'Actividades de empresas de servicios temporales'),(7830,'Otras actividades de provisión de talento humano'),(7911,'Actividades de las agencias de viaje'),(7912,'Actividades de operadores turísticos'),(7990,'Otros servicios de reserva y actividades relacionadas'),(8010,'Actividades de seguridad privada'),(8020,'Actividades de servicios de sistemas de seguridad'),(8030,'Actividades de detectives e investigadores privados'),(8110,'Actividades combinadas de apoyo a instalaciones'),(8121,'Limpieza general interior de edificios'),(8129,'Otras actividades de limpieza de edificios e instalaciones industriales'),(8130,'Actividades de paisajismo y servicios de mantenimiento conexos'),(8211,'Actividades combinadas de servicios administrativos de oficina'),(8219,'Fotocopiado, preparación de documentos y otras actividades especializadas de apoyo a oficina'),(8220,'Actividades de centros de llamadas (Call center)'),(8230,'Organización de convenciones y eventos comerciales'),(8291,'Actividades de agencias de cobranza y oficinas de calificación crediticia'),(8292,'Actividades de envase y empaque'),(8299,'Otras actividades de servicio de apoyo a las empresas n.c.p.'),(8411,'Actividades legislativas de la administración pública'),(8412,'Actividades ejecutivas de la administración pública'),(8413,'Regulación de las actividades de organismos que prestan servicios de salud, educativos, culturales y otros servicios sociales, excepto servicios de seguridad social'),(8414,'Actividades reguladoras y facilitadoras de la actividad económica'),(8415,'Actividades de los órganos de control y otras instituciones.'),(8421,'Relaciones exteriores'),(8422,'Actividades de defensa'),(8423,'Orden público y actividades de seguridad publica'),(8424,'Administración de justicia'),(8430,'Actividades de planes de Seguridad Social de afiliación obligatoria'),(8511,'Educación de la primera infancia'),(8512,'Educación preescolar'),(8513,'Educación básica primaria'),(8521,'Educación básica secundaria'),(8522,'Educación media académica'),(8523,'Educación media técnica'),(8530,'Establecimientos que combinan diferentes niveles de educación inicial, preescolar, básica primaria, básica secundaria y media'),(8541,'Educación técnica profesional'),(8542,'Educación tecnológica'),(8543,'Educación de instituciones universitarias o de escuelas tecnológicas'),(8544,'Educación de universidades'),(8551,'Formación para el trabajo'),(8552,'Enseñanza deportiva y recreativa'),(8553,'Enseñanza cultural'),(8559,'Otros tipos de educación n.c.p.'),(8560,'Actividades de apoyo a la educación'),(8610,'Actividades de hospitales y clínicas, con internación'),(8720,'Actividades de atención residencial, para el cuidado de pacientes con retardo mental, enfermedad mental y consumo de sustancias psicoactivas'),(8730,'Actividades de atención en instituciones para el cuidado de personas mayores y/o discapacitadas'),(8790,'Otras actividades de atención en instituciones con alojamiento'),(8810,'Actividades de asistencia social sin alojamiento para personas mayores y discapacitadas'),(8891,'Actividades de guarderías para niños y niñas'),(8899,'Otras actividades de asistencia social n.c.p'),(9001,'Creación literaria'),(9002,'Creación musical'),(9003,'Creación teatral'),(9004,'Creación audiovisual'),(9005,'Artes plásticas y visuales'),(9006,'Actividades teatrales'),(9007,'Actividades de espectáculos musicales en vivo'),(9008,'Otras actividades de espectáculos en vivo n.c.p'),(9101,'Actividades de Bibliotecas y archivos'),(9102,'Actividades y funcionamiento de museos, conservación de edificios y sitios históricos'),(9103,'Actividades de jardines botánicos, zoológicos y reservas naturales'),(9311,'Gestión de instalaciones deportivas'),(9312,'Actividades de clubes deportivos'),(9319,'Otras actividades deportivas'),(9321,'Actividades de parques de atracciones y parques temáticos'),(9411,'Actividades de asociaciones empresariales y de empleadores'),(9412,'Actividades de asociaciones profesionales y gremiales sin ánimo de lucro'),(9420,'Actividades de sindicatos'),(9491,'Actividades de asociaciones religiosas'),(9492,'Actividades de partidos políticos'),(9499,'Actividades de otras asociaciones n.c.p.'),(9511,'Mantenimiento y reparación de computadores y de equipo periférico'),(9512,'Mantenimiento y reparación de equipos de comunicación'),(9521,'Mantenimiento y reparación de aparatos electrónicos de consumo'),(9522,'Mantenimiento y reparación de aparatos domésticos y equipos domésticos y de jardinería'),(9523,'Reparación de calzado y artículos de cuero'),(9524,'Reparación de muebles y accesorios para el hogar'),(9529,'Mantenimiento y reparación de otros efectos personales y enseres domésticos'),(9601,'Lavado y limpieza, incluso la limpieza en seco, de productos textiles y de piel'),(9602,'Peluquería y otros tratamientos de belleza'),(9603,'Pompas fúnebres y actividades relacionadas'),(9609,'Otras actividades de servicios personales n.c.p.'),(9700,'Actividades de los hogares individuales como empleadores de personal doméstico'),(9810,'Actividades no diferenciadas de los hogares individuales como productores de bienes para uso propio'),(9820,'Actividades no diferenciadas de los hogares individuales como productores de servicios para uso propio'),(9900,'Actividades de organizaciones y entidades extraterritoriales signatarios de la Convención de Viena'),(10201,'Procesamiento y conservación de frutas, legumbres, hortalizas y tubérculos (excepto elaboración de jugos de frutas)'),(10202,'Elaboración de jugos de frutas'),(10401,'Elaboración de productos lácteos (excepto bebidas)'),(10402,'Elaboración de bebidas lácteas'),(14201,'Fabricación de prendas de vestir de piel'),(14202,'Fabricación de artículos de piel (excepto prendas de vestir)'),(14301,'Fabricación de prendas de vestir de punto y ganchillo'),(14302,'Fabricación de artículos de punto y ganchillo (excepto prendas de vestir)'),(35201,'Producción de gas'),(35202,'Distribución de combustibles gaseosos por tuberías'),(36001,'Captación y tratamiento de agua'),(36002,'Distribución de agua '),(39001,'Actividades de saneamiento ambiental y otros servicios de gestión de desechos (excepto los servicios prestados por contratistas de construcción, constructores y urbanizadores)'),(39002,'Actividades de saneamiento ambiental y otros  de gestión de desechos prestados por contratistas de construcción, constructores y urbanizadores'),(45411,'Comercio de motocicletas'),(45412,'Comercio de partes, piezas y accesorios de motocicletas'),(46201,'Comercio al por mayor de materias primas agrícolas en bruto (alimentos)'),(46202,'Comercio al por mayor de materias primas pecuarias y animales vivos'),(46321,'Comercio al por mayor de bebidas y tabaco (diferentes a licores y cigarrillos)'),(46322,'Comercio al por mayor de licores y cigarrillos'),(46451,'Comercio al por mayor de productos farmacéuticos y medicinales'),(46452,'Comercio al por mayor de productos cosméticos y de tocador (excepto productos farmacéuticos y medicinales)'),(46491,'Comercio al por mayor de otros utensilios domésticos n.c.p. (excepto joyas)'),(46492,'Venta de joyas'),(46611,'Comercio al por mayor de combustibles sólidos, líquidos, gaseosos y productos conexos (excepto combustibles derivados del petróleo)'),(46612,'Comercio al por mayor de combustibles derivados del petróleo'),(46631,'Comercio al por mayor de materiales de construcción'),(46632,'Comercio al por mayor de artículos de ferretería, pinturas, productos de vidrio, equipo y materiales de fontanería y calefacción'),(47111,'Comercio al por menor en establecimientos no especializados con surtido compuesto principalmente por alimentos, bebidas no alcohólicas o tabaco (excepto cigarrillos)'),(47112,'Comercio al por menor en establecimientos no especializados con surtido compuesto principalmente por licores y cigarrillos'),(47191,'Comercio al por menor en establecimientos no especializados, con surtido compuesto principalmente por productos de bebidas alcohólicas y cigarrillos '),(47192,'Comercio al por menor en establecimientos no especializados, con surtido compuesto principalmente por drogas, medicamentos, textos escolares, libros y cuadernos.'),(47241,'Comercio al por menor de bebidas y productos del tabaco, en establecimientos especializados (excepto licores y cigarrillos)'),(47242,'Comercio al por menor de licores y cigarrillos'),(47521,'Comercio al por menor de materiales de construcción'),(47522,'Comercio al por menor de artículos de ferretería, pinturas y productos de vidrio en establecimientos especializados (excepto materiales de construcción)'),(47611,'Comercio al por menor y al por mayor de libros, textos escolares y cuadernos '),(47612,'Comercio al por menor de periódicos, materiales y artículos de papelería y escritorio, en establecimientos especializados (excepto libros, textos escolares y cuadernos)'),(47731,'Comercio al por menor de productos farmacéuticos y medicinales en establecimientos especializados'),(47732,'Comercio al por menor de productos cosméticos y artículos de tocador en establecimientos especializados (excepto productos farmacéuticos y medicinales)'),(47811,'Comercio al por menor de alimentos en puestos de venta móviles'),(47812,'Comercio al por menor de bebidas y tabaco en puestos de venta móviles (excepto licores y cigarrillos)'),(47813,'Comercio al por menor de cigarrillos y licores en puestos de venta móviles'),(47911,'Comercio al por menor de alimentos y productos agrícolas en bruto; venta de textos escolares y libros (incluye cuadernos escolares); venta de drogas y medicamentos realizado a través de internet'),(47912,'Comercio al por menor y al por mayor de madera y materiales para construcción; venta de automotores (incluidas motocicletas) realizado a través de internet'),(47913,'Comercio al por menor de cigarrillos y licores; venta de combustibles derivados del petróleo y venta de joyas realizado a través de internet'),(47914,'Comercio al por menor de demás productos n.c.p. realizado a través de internet'),(47921,'Comercio al por menor de alimentos y productos agrícolas en bruto; venta de textos escolares y libros (incluye cuadernos escolares); venta de drogas y medicamentos realizado a través de casas de venta o por correo'),(47922,'Comercio al por menor y al por mayor de madera y materiales para construcción; venta de automotores (incluidas motocicletas) realizado a través de casas de venta o por correo'),(47923,'Comercio al por menor de cigarrillos y licores; venta de combustibles derivados del petróleo y venta de joyas realizado a través de casas de venta o por correo'),(47924,'Comercio al por menor de demás productos n.c.p. realizado a través de casas de venta o por correo'),(47991,'Otros tipos de comercio al por menor no realizado en establecimientos, puestos de venta o mercados de textos escolares y libros (incluye cuadernos escolares); venta de drogas y medicamentos'),(47992,'Otros tipos de comercio al por menor no realizado en establecimientos, puestos de venta o mercados de materiales para construcción; venta de automotores (incluidas motocicletas)'),(47993,'Otros tipos de comercio al por menor no realizado en establecimientos, puestos de venta o mercados de cigarrillos y licores; venta de combustibles derivados del petróleo y venta de joyas'),(47994,'Otros tipos de comercio al por menor no realizado en establecimientos, puestos de venta o mercados de demás productos n.c.p.'),(58111,'Servicio de edición de libros'),(58112,'Edición y publicación de libros'),(58113,'Edición y publicación de libros (Tarifa especial para los contribuyentes que cumplen condiciones del Acuerdo 98 de 2003)'),(60201,'Actividades de programación de televisión'),(60202,'Actividades de transmisión de televisión'),(64991,'Otras actividades de servicio financiero, excepto las de seguros y pensiones n.c.p.'),(64992,'Actividades comerciales de las casas de empeño o compraventa'),(64993,'Servicios de las casas de empeño o compraventas'),(66111,'Administración de mercados financieros (excepto actividades de las bolsas de valores)'),(66112,'Actividades de las bolsas de valores'),(69101,'Actividades jurídicas como consultoría profesional'),(69102,'Actividades jurídicas en el ejercicio de una profesión liberal'),(69201,'Actividades de contabilidad, teneduría de libros, auditoría financiera y asesoría tributaria como consultoría profesional'),(69202,'Actividades de contabilidad, teneduría de libros, auditoría financiera y asesoría tributaria en el ejercicio de una profesión liberal'),(70101,'Actividades de administración empresarial como consultoría profesional'),(70102,'Actividades de administración empresarial en el ejercicio de una profesión liberal'),(70201,'Actividades de consultoría de gestión'),(70202,'Actividades de gestión en el ejercicio de una profesión liberal'),(71111,'Actividades de arquitectura'),(71112,'Actividades de arquitectura en ejercicio de una profesión liberal'),(71121,'Actividades de ingeniería y otras actividades conexas de consultoría técnica'),(71122,'Actividades de ingeniería y otras actividades conexas de consultoría técnica en ejercicio de una profesión liberal'),(71201,'Ensayos y análisis técnicos como consultoría profesional'),(71202,'Ensayos y análisis técnicos como consultoría profesional en el ejercicio de una profesión liberal'),(72101,'Investigaciones y desarrollo experimental en el campo de las ciencias naturales y la ingeniería como consultoría profesional'),(72102,'Investigaciones y desarrollo experimental en el campo de las ciencias naturales y la ingeniería en el ejercicio de una profesión liberal'),(72201,'Investigaciones y desarrollo experimental en el campo de las ciencias sociales y las humanidades como consultoría profesional'),(72202,'Investigaciones y desarrollo experimental en el campo de las ciencias sociales y las humanidades en el ejercicio de una profesión liberal'),(73201,'Estudios de mercado y realización de encuestas de opinión pública como consultoría profesional'),(73202,'Estudios de mercado y realización de encuestas de opinión pública en el ejercicio de una profesión liberal'),(74101,'Actividades especializadas de diseño como consultoría profesional'),(74102,'Actividades especializadas de diseño en el ejercicio de una profesión liberal'),(74901,'Otras actividades profesionales, científicas y técnicas n.c.p. como consultoría profesional (incluye actividades de periodistas)'),(74902,'Otras actividades profesionales, científicas y técnicas n.c.p. en el ejercicio de una profesión liberal'),(85591,'Educación académica no formal (excepto programas de educación básica primaria, básica secundaria y media no gradual con fines de validación)'),(85592,'Educación académica no formal impartida mediante programas de educación básica primaria, básica secundaria y media no gradual con fines de validación'),(86211,'Actividades de la práctica médica, sin internación (excepto actividades de promoción y prevención que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pública o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(86221,'Actividades de la práctica odontológica, sin internación (excepto actividades de promoción y prevención que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pública o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(86911,'Actividades de apoyo diagnóstico (excepto actividades de promoción y prevención que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pública o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(86921,'Actividades de apoyo terapéutico (excepto actividades de promoción y prevención que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pública o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(86991,'Otras actividades de atención de la salud humana (excepto actividades de promoción y prevención que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pública o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(87101,'Actividades de atención residencial medicalizada de tipo general (excepto actividades de promoción y prevención que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pública o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(92001,'Actividades de juegos de destreza, habilidad, conocimiento y fuerza'),(93291,'Otras actividades recreativas y de esparcimiento n.c.p. (excepto juegos de suerte y azar, discotecas y similares )');
/*!40000 ALTER TABLE `actividadeconomica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departamentos`
--

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

--
-- Dumping data for table `departamentos`
--

LOCK TABLES `departamentos` WRITE;
/*!40000 ALTER TABLE `departamentos` DISABLE KEYS */;
INSERT INTO `departamentos` VALUES (1,169,5,'ANTIOQUIA'),(2,169,8,'ATLANTICO'),(3,169,11,'BOGOTA'),(4,169,13,'BOLIVAR'),(5,169,15,'BOYACA'),(6,169,17,'CALDAS'),(7,169,18,'CAQUETA'),(8,169,19,'CAUCA'),(9,169,20,'CESAR'),(10,169,23,'CORDOBA'),(11,169,25,'CUNDINAMARCA'),(12,169,27,'CHOCO'),(13,169,41,'HUILA'),(14,169,44,'LA GUAJIRA'),(15,169,47,'MAGDALENA'),(16,169,50,'META'),(17,169,52,'NARI¥O'),(18,169,54,'N. DE SANTANDER'),(19,169,63,'QUINDIO'),(20,169,66,'RISARALDA'),(21,169,68,'SANTANDER'),(22,169,70,'SUCRE'),(23,169,73,'TOLIMA'),(24,169,76,'VALLE DEL CAUCA'),(25,169,81,'ARAUCA'),(26,169,85,'CASANARE'),(27,169,86,'PUTUMAYO'),(28,169,88,'SAN ANDRES'),(29,169,91,'AMAZONAS'),(30,169,94,'GUAINIA'),(31,169,95,'GUAVIARE'),(32,169,97,'VAUPES'),(33,169,99,'VICHADA'),(34,0,0,'NA');
/*!40000 ALTER TABLE `departamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `impuestos`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `impuestos`
INSERT INTO `impuestos` VALUES (1,'IMPUESTO SOBRE LAS VENTAS',5,0,'IVA','INACTIVO'),(2,'IMPUESTO SOBRE LAS VENTAS',19,0,'IVA','INACTIVO'),(3,'IMPUESTO NACIONAL AL CONSUMIDOR',8,0,'INC','INACTIVO'),(4,'EXENTO DE IVA',0,0,'EXN','INACTIVO'),(5,'EXCLUIDO DE IVA',-1,0,'EXC','INACTIVO'),(6,'IMPUESTO NACIONAL AL CONSUMO DE BOLSAS PLASTICAS',70,0,'INCBP','INACTIVO');
--

LOCK TABLES `impuestos` WRITE;
/*!40000 ALTER TABLE `impuestos` DISABLE KEYS */;

/*!40000 ALTER TABLE `impuestos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `municipios`
--

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

--
-- Dumping data for table `municipios`
--

LOCK TABLES `municipios` WRITE;
/*!40000 ALTER TABLE `municipios` DISABLE KEYS */;
INSERT INTO `municipios` VALUES (1,5,1,'MEDELLIN'),(2,5,2,'ABEJORRAL'),(3,5,4,'ABRIAQUI'),(4,5,21,'ALEJANDRIA'),(5,5,30,'AMAGA'),(6,5,31,'AMALFI'),(7,5,34,'ANDES'),(8,5,36,'ANGELOPOLIS'),(9,5,38,'ANGOSTURA'),(10,5,40,'ANORI'),(11,5,42,'SANTAFE DE ANTIOQUIA'),(12,5,44,'ANZA'),(13,5,45,'APARTADO'),(14,5,51,'ARBOLETES'),(15,5,55,'ARGELIA'),(16,5,59,'ARMENIA'),(17,5,79,'BARBOSA'),(18,5,86,'BELMIRA'),(19,5,88,'BELLO'),(20,5,91,'BETANIA'),(21,5,93,'BETULIA'),(22,5,101,'CIUDAD BOLIVAR'),(23,5,107,'BRICE¥O'),(24,5,113,'BURITICA'),(25,5,120,'CACERES'),(26,5,125,'CAICEDO'),(27,5,129,'CALDAS'),(28,5,134,'CAMPAMENTO'),(29,5,138,'CA¥ASGORDAS'),(30,5,142,'CARACOLI'),(31,5,145,'CARAMANTA'),(32,5,147,'CAREPA'),(33,5,148,'EL CARMEN DE VIBORAL'),(34,5,150,'CAROLINA'),(35,5,154,'CAUCASIA'),(36,5,172,'CHIGORODO'),(37,5,190,'CISNEROS'),(38,5,197,'COCORNA'),(39,5,206,'CONCEPCION'),(40,5,209,'CONCORDIA'),(41,5,212,'COPACABANA'),(42,5,234,'DABEIBA'),(43,5,237,'DON MATIAS'),(44,5,240,'EBEJICO'),(45,5,250,'EL BAGRE'),(46,5,264,'ENTRERRIOS'),(47,5,266,'ENVIGADO'),(48,5,282,'FREDONIA'),(49,5,284,'FRONTINO'),(50,5,306,'GIRALDO'),(51,5,308,'GIRARDOTA'),(52,5,310,'GOMEZ PLATA'),(53,5,313,'GRANADA'),(54,5,315,'GUADALUPE'),(55,5,318,'GUARNE'),(56,5,321,'GUATAPE'),(57,5,347,'HELICONIA'),(58,5,353,'HISPANIA'),(59,5,360,'ITAGUI'),(60,5,361,'ITUANGO'),(61,5,364,'JARDIN'),(62,5,368,'JERICO'),(63,5,376,'LA CEJA'),(64,5,380,'LA ESTRELLA'),(65,5,390,'LA PINTADA'),(66,5,400,'LA UNION'),(67,5,411,'LIBORINA'),(68,5,425,'MACEO'),(69,5,440,'MARINILLA'),(70,5,467,'MONTEBELLO'),(71,5,475,'MURINDO'),(72,5,480,'MUTATA'),(73,5,483,'NARI¥O'),(74,5,490,'NECOCLI'),(75,5,495,'NECHI'),(76,5,501,'OLAYA'),(77,5,541,'PEÑOL'),(78,5,543,'PEQUE'),(79,5,576,'PUEBLORRICO'),(80,5,579,'PUERTO BERRIO'),(81,5,585,'PUERTO NARE'),(82,5,591,'PUERTO TRIUNFO'),(83,5,604,'REMEDIOS'),(84,5,607,'RETIRO'),(85,5,615,'RIONEGRO'),(86,5,628,'SABANALARGA'),(87,5,631,'SABANETA'),(88,5,642,'SALGAR'),(89,5,647,'SAN ANDRES DE CUERQUIA'),(90,5,649,'SAN CARLOS'),(91,5,652,'SAN FRANCISCO'),(92,5,656,'SAN JERONIMO'),(93,5,658,'SAN JOSE DE LA MONTA¥A'),(94,5,659,'SAN JUAN DE URABA'),(95,5,660,'SAN LUIS'),(96,5,664,'SAN PEDRO'),(97,5,665,'SAN PEDRO DE URABA'),(98,5,667,'SAN RAFAEL'),(99,5,670,'SAN ROQUE'),(100,5,674,'SAN VICENTE'),(101,5,679,'SANTA BARBARA'),(102,5,686,'SANTA ROSA DE OSOS'),(103,5,690,'SANTO DOMINGO'),(104,5,697,'EL SANTUARIO'),(105,5,736,'SEGOVIA'),(106,5,756,'SONSON'),(107,5,761,'SOPETRAN'),(108,5,789,'TAMESIS'),(109,5,790,'TARAZA'),(110,5,792,'TARSO'),(111,5,809,'TITIRIBI'),(112,5,819,'TOLEDO'),(113,5,837,'TURBO'),(114,5,842,'URAMITA'),(115,5,847,'URRAO'),(116,5,854,'VALDIVIA'),(117,5,856,'VALPARAISO'),(118,5,858,'VEGACHI'),(119,5,861,'VENECIA'),(120,5,873,'VIGIA DEL FUERTE'),(121,5,885,'YALI'),(122,5,887,'YARUMAL'),(123,5,890,'YOLOMBO'),(124,5,893,'YONDO'),(125,5,895,'ZARAGOZA'),(126,8,1,'BARRANQUILLA'),(127,8,78,'BARANOA'),(128,8,137,'CAMPO DE LA CRUZ'),(129,8,141,'CANDELARIA'),(130,8,296,'GALAPA'),(131,8,372,'JUAN DE ACOSTA'),(132,8,421,'LURUACO'),(133,8,433,'MALAMBO'),(134,8,436,'MANATI'),(135,8,520,'PALMAR DE VARELA'),(136,8,549,'PIOJO'),(137,8,558,'POLONUEVO'),(138,8,560,'PONEDERA'),(139,8,573,'PUERTO COLOMBIA'),(140,8,606,'REPELON'),(141,8,634,'SABANAGRANDE'),(142,8,638,'SABANALARGA'),(143,8,675,'SANTA LUCIA'),(144,8,685,'SANTO TOMAS'),(145,8,758,'SOLEDAD'),(146,8,770,'SUAN'),(147,8,832,'TUBARA'),(148,8,849,'USIACURI'),(149,11,1,'BOGOTA, D.C.'),(150,13,1,'CARTAGENA'),(151,13,6,'ACHI'),(152,13,30,'ALTOS DEL ROSARIO'),(153,13,42,'ARENAL'),(154,13,52,'ARJONA'),(155,13,62,'ARROYOHONDO'),(156,13,74,'BARRANCO DE LOBA'),(157,13,140,'CALAMAR'),(158,13,160,'CANTAGALLO'),(159,13,188,'CICUCO'),(160,13,212,'CORDOBA'),(161,13,222,'CLEMENCIA'),(162,13,244,'EL CARMEN DE BOLIVAR'),(163,13,248,'EL GUAMO'),(164,13,268,'EL PE¥ON'),(165,13,300,'HATILLO DE LOBA'),(166,13,430,'MAGANGUE'),(167,13,433,'MAHATES'),(168,13,440,'MARGARITA'),(169,13,442,'MARIA LA BAJA'),(170,13,458,'MONTECRISTO'),(171,13,468,'MOMPOS'),(172,13,490,'NOROSI'),(173,13,473,'MORALES'),(174,13,549,'PINILLOS'),(175,13,580,'REGIDOR'),(176,13,600,'RIO VIEJO'),(177,13,620,'SAN CRISTOBAL'),(178,13,647,'SAN ESTANISLAO'),(179,13,650,'SAN FERNANDO'),(180,13,654,'SAN JACINTO'),(181,13,655,'SAN JACINTO DEL CAUCA'),(182,13,657,'SAN JUAN NEPOMUCENO'),(183,13,667,'SAN MARTIN DE LOBA'),(184,13,670,'SAN PABLO'),(185,13,673,'SANTA CATALINA'),(186,13,683,'SANTA ROSA'),(187,13,688,'SANTA ROSA DEL SUR'),(188,13,744,'SIMITI'),(189,13,760,'SOPLAVIENTO'),(190,13,780,'TALAIGUA NUEVO'),(191,13,810,'TIQUISIO'),(192,13,836,'TURBACO'),(193,13,838,'TURBANA'),(194,13,873,'VILLANUEVA'),(195,13,894,'ZAMBRANO'),(196,15,1,'TUNJA'),(197,15,22,'ALMEIDA'),(198,15,47,'AQUITANIA'),(199,15,51,'ARCABUCO'),(200,15,87,'BELEN'),(201,15,90,'BERBEO'),(202,15,92,'BETEITIVA'),(203,15,97,'BOAVITA'),(204,15,104,'BOYACA'),(205,15,106,'BRICE¥O'),(206,15,109,'BUENAVISTA'),(207,15,114,'BUSBANZA'),(208,15,131,'CALDAS'),(209,15,135,'CAMPOHERMOSO'),(210,15,162,'CERINZA'),(211,15,172,'CHINAVITA'),(212,15,176,'CHIQUINQUIRA'),(213,15,180,'CHISCAS'),(214,15,183,'CHITA'),(215,15,185,'CHITARAQUE'),(216,15,187,'CHIVATA'),(217,15,189,'CIENEGA'),(218,15,204,'COMBITA'),(219,15,212,'COPER'),(220,15,215,'CORRALES'),(221,15,218,'COVARACHIA'),(222,15,223,'CUBARA'),(223,15,224,'CUCAITA'),(224,15,226,'CUITIVA'),(225,15,232,'CHIQUIZA'),(226,15,236,'CHIVOR'),(227,15,238,'DUITAMA'),(228,15,244,'EL COCUY'),(229,15,248,'EL ESPINO'),(230,15,272,'FIRAVITOBA'),(231,15,276,'FLORESTA'),(232,15,293,'GACHANTIVA'),(233,15,296,'GAMEZA'),(234,15,299,'GARAGOA'),(235,15,317,'GUACAMAYAS'),(236,15,322,'GUATEQUE'),(237,15,325,'GUAYATA'),(238,15,332,'GsICAN'),(239,15,362,'IZA'),(240,15,367,'JENESANO'),(241,15,368,'JERICO'),(242,15,377,'LABRANZAGRANDE'),(243,15,380,'LA CAPILLA'),(244,15,401,'LA VICTORIA'),(245,15,403,'LA UVITA'),(246,15,407,'VILLA DE LEYVA'),(247,15,425,'MACANAL'),(248,15,442,'MARIPI'),(249,15,455,'MIRAFLORES'),(250,15,464,'MONGUA'),(251,15,466,'MONGUI'),(252,15,469,'MONIQUIRA'),(253,15,476,'MOTAVITA'),(254,15,480,'MUZO'),(255,15,491,'NOBSA'),(256,15,494,'NUEVO COLON'),(257,15,500,'OICATA'),(258,15,507,'OTANCHE'),(259,15,511,'PACHAVITA'),(260,15,514,'PAEZ'),(261,15,516,'PAIPA'),(262,15,518,'PAJARITO'),(263,15,522,'PANQUEBA'),(264,15,531,'PAUNA'),(265,15,533,'PAYA'),(266,15,537,'PAZ DE RIO'),(267,15,542,'PESCA'),(268,15,550,'PISBA'),(269,15,572,'PUERTO BOYACA'),(270,15,580,'QUIPAMA'),(271,15,599,'RAMIRIQUI'),(272,15,600,'RAQUIRA'),(273,15,621,'RONDON'),(274,15,632,'SABOYA'),(275,15,638,'SACHICA'),(276,15,646,'SAMACA'),(277,15,660,'SAN EDUARDO'),(278,15,664,'SAN JOSE DE PARE'),(279,15,667,'SAN LUIS DE GACENO'),(280,15,673,'SAN MATEO'),(281,15,676,'SAN MIGUEL DE SEMA'),(282,15,681,'SAN PABLO DE BORBUR'),(283,15,686,'SANTANA'),(284,15,690,'SANTA MARIA'),(285,15,693,'SANTA ROSA DE VITERBO'),(286,15,696,'SANTA SOFIA'),(287,15,720,'SATIVANORTE'),(288,15,723,'SATIVASUR'),(289,15,740,'SIACHOQUE'),(290,15,753,'SOATA'),(291,15,755,'SOCOTA'),(292,15,757,'SOCHA'),(293,15,759,'SOGAMOSO'),(294,15,761,'SOMONDOCO'),(295,15,762,'SORA'),(296,15,763,'SOTAQUIRA'),(297,15,764,'SORACA'),(298,15,774,'SUSACON'),(299,15,776,'SUTAMARCHAN'),(300,15,778,'SUTATENZA'),(301,15,790,'TASCO'),(302,15,798,'TENZA'),(303,15,804,'TIBANA'),(304,15,806,'TIBASOSA'),(305,15,808,'TINJACA'),(306,15,810,'TIPACOQUE'),(307,15,814,'TOCA'),(308,15,816,'TOGsI'),(309,15,820,'TOPAGA'),(310,15,822,'TOTA'),(311,15,832,'TUNUNGUA'),(312,15,835,'TURMEQUE'),(313,15,837,'TUTA'),(314,15,839,'TUTAZA'),(315,15,842,'UMBITA'),(316,15,861,'VENTAQUEMADA'),(317,15,879,'VIRACACHA'),(318,15,897,'ZETAQUIRA'),(319,17,1,'MANIZALES'),(320,17,13,'AGUADAS'),(321,17,42,'ANSERMA'),(322,17,50,'ARANZAZU'),(323,17,88,'BELALCAZAR'),(324,17,174,'CHINCHINA'),(325,17,272,'FILADELFIA'),(326,17,380,'LA DORADA'),(327,17,388,'LA MERCED'),(328,17,433,'MANZANARES'),(329,17,442,'MARMATO'),(330,17,444,'MARQUETALIA'),(331,17,446,'MARULANDA'),(332,17,486,'NEIRA'),(333,17,495,'NORCASIA'),(334,17,513,'PACORA'),(335,17,524,'PALESTINA'),(336,17,541,'PENSILVANIA'),(337,17,614,'RIOSUCIO'),(338,17,616,'RISARALDA'),(339,17,653,'SALAMINA'),(340,17,662,'SAMANA'),(341,17,665,'SAN JOSE'),(342,17,777,'SUPIA'),(343,17,867,'VICTORIA'),(344,17,873,'VILLAMARIA'),(345,17,877,'VITERBO'),(346,18,1,'FLORENCIA'),(347,18,29,'ALBANIA'),(348,18,94,'BELEN DE LOS ANDAQUIES'),(349,18,150,'CARTAGENA DEL CHAIRA'),(350,18,205,'CURILLO'),(351,18,247,'EL DONCELLO'),(352,18,256,'EL PAUJIL'),(353,18,410,'LA MONTA¥ITA'),(354,18,460,'MILAN'),(355,18,479,'MORELIA'),(356,18,592,'PUERTO RICO'),(357,18,610,'SAN JOSE DEL FRAGUA'),(358,18,753,'SAN VICENTE DEL CAGUAN'),(359,18,756,'SOLANO'),(360,18,785,'SOLITA'),(361,18,860,'VALPARAISO'),(362,19,1,'POPAYAN'),(363,19,22,'ALMAGUER'),(364,19,50,'ARGELIA'),(365,19,75,'BALBOA'),(366,19,100,'BOLIVAR'),(367,19,110,'BUENOS AIRES'),(368,19,130,'CAJIBIO'),(369,19,137,'CALDONO'),(370,19,142,'CALOTO'),(371,19,212,'CORINTO'),(372,19,256,'EL TAMBO'),(373,19,290,'FLORENCIA'),(374,19,300,'GUACHENE'),(375,19,318,'GUAPI'),(376,19,355,'INZA'),(377,19,364,'JAMBALO'),(378,19,392,'LA SIERRA'),(379,19,397,'LA VEGA'),(380,19,418,'LOPEZ'),(381,19,450,'MERCADERES'),(382,19,455,'MIRANDA'),(383,19,473,'MORALES'),(384,19,513,'PADILLA'),(385,19,517,'PAEZ'),(386,19,532,'PATIA'),(387,19,533,'PIAMONTE'),(388,19,548,'PIENDAMO'),(389,19,573,'PUERTO TEJADA'),(390,19,585,'PURACE'),(391,19,622,'ROSAS'),(392,19,693,'SAN SEBASTIAN'),(393,19,698,'SANTANDER DE QUILICHAO'),(394,19,701,'SANTA ROSA'),(395,19,743,'SILVIA'),(396,19,760,'SOTARA'),(397,19,780,'SUAREZ'),(398,19,785,'SUCRE'),(399,19,807,'TIMBIO'),(400,19,809,'TIMBIQUI'),(401,19,821,'TORIBIO'),(402,19,824,'TOTORO'),(403,19,845,'VILLA RICA'),(404,20,1,'VALLEDUPAR'),(405,20,11,'AGUACHICA'),(406,20,13,'AGUSTIN CODAZZI'),(407,20,32,'ASTREA'),(408,20,45,'BECERRIL'),(409,20,60,'BOSCONIA'),(410,20,175,'CHIMICHAGUA'),(411,20,178,'CHIRIGUANA'),(412,20,228,'CURUMANI'),(413,20,238,'EL COPEY'),(414,20,250,'EL PASO'),(415,20,295,'GAMARRA'),(416,20,310,'GONZALEZ'),(417,20,383,'LA GLORIA'),(418,20,400,'LA JAGUA DE IBIRICO'),(419,20,443,'MANAURE'),(420,20,517,'PAILITAS'),(421,20,550,'PELAYA'),(422,20,570,'PUEBLO BELLO'),(423,20,614,'RIO DE ORO'),(424,20,621,'LA PAZ'),(425,20,710,'SAN ALBERTO'),(426,20,750,'SAN DIEGO'),(427,20,770,'SAN MARTIN'),(428,20,787,'TAMALAMEQUE'),(429,23,1,'MONTERIA'),(430,23,68,'AYAPEL'),(431,23,79,'BUENAVISTA'),(432,23,90,'CANALETE'),(433,23,162,'CERETE'),(434,23,168,'CHIMA'),(435,23,182,'CHINU'),(436,23,189,'CIENAGA DE ORO'),(437,23,300,'COTORRA'),(438,23,350,'LA APARTADA'),(439,23,417,'LORICA'),(440,23,419,'LOS CORDOBAS'),(441,23,464,'MOMIL'),(442,23,466,'MONTELIBANO'),(443,23,500,'MO¥ITOS'),(444,23,555,'PLANETA RICA'),(445,23,570,'PUEBLO NUEVO'),(446,23,574,'PUERTO ESCONDIDO'),(447,23,580,'PUERTO LIBERTADOR'),(448,23,586,'PURISIMA'),(449,23,660,'SAHAGUN'),(450,23,670,'SAN ANDRES SOTAVENTO'),(451,23,672,'SAN ANTERO'),(452,23,675,'SAN BERNARDO DEL VIENTO'),(453,23,678,'SAN CARLOS'),(454,23,686,'SAN PELAYO'),(455,23,807,'TIERRALTA'),(456,23,855,'VALENCIA'),(457,25,1,'AGUA DE DIOS'),(458,25,19,'ALBAN'),(459,25,35,'ANAPOIMA'),(460,25,40,'ANOLAIMA'),(461,25,53,'ARBELAEZ'),(462,25,86,'BELTRAN'),(463,25,95,'BITUIMA'),(464,25,99,'BOJACA'),(465,25,120,'CABRERA'),(466,25,123,'CACHIPAY'),(467,25,126,'CAJICA'),(468,25,148,'CAPARRAPI'),(469,25,151,'CAQUEZA'),(470,25,154,'CARMEN DE CARUPA'),(471,25,168,'CHAGUANI'),(472,25,175,'CHIA'),(473,25,178,'CHIPAQUE'),(474,25,181,'CHOACHI'),(475,25,183,'CHOCONTA'),(476,25,200,'COGUA'),(477,25,214,'COTA'),(478,25,224,'CUCUNUBA'),(479,25,245,'EL COLEGIO'),(480,25,258,'EL PE¥ON'),(481,25,260,'EL ROSAL'),(482,25,269,'FACATATIVA'),(483,25,279,'FOMEQUE'),(484,25,281,'FOSCA'),(485,25,286,'FUNZA'),(486,25,288,'FUQUENE'),(487,25,290,'FUSAGASUGA'),(488,25,293,'GACHALA'),(489,25,295,'GACHANCIPA'),(490,25,297,'GACHETA'),(491,25,299,'GAMA'),(492,25,307,'GIRARDOT'),(493,25,312,'GRANADA'),(494,25,317,'GUACHETA'),(495,25,320,'GUADUAS'),(496,25,322,'GUASCA'),(497,25,324,'GUATAQUI'),(498,25,326,'GUATAVITA'),(499,25,328,'GUAYABAL DE SIQUIMA'),(500,25,335,'GUAYABETAL'),(501,25,339,'GUTIERREZ'),(502,25,368,'JERUSALEN'),(503,25,372,'JUNIN'),(504,25,377,'LA CALERA'),(505,25,386,'LA MESA'),(506,25,394,'LA PALMA'),(507,25,398,'LA PE¥A'),(508,25,402,'LA VEGA'),(509,25,407,'LENGUAZAQUE'),(510,25,426,'MACHETA'),(511,25,430,'MADRID'),(512,25,436,'MANTA'),(513,25,438,'MEDINA'),(514,25,473,'MOSQUERA'),(515,25,483,'NARI¥O'),(516,25,486,'NEMOCON'),(517,25,488,'NILO'),(518,25,489,'NIMAIMA'),(519,25,491,'NOCAIMA'),(520,25,506,'VENECIA'),(521,25,513,'PACHO'),(522,25,518,'PAIME'),(523,25,524,'PANDI'),(524,25,530,'PARATEBUENO'),(525,25,535,'PASCA'),(526,25,572,'PUERTO SALGAR'),(527,25,580,'PULI'),(528,25,592,'QUEBRADANEGRA'),(529,25,594,'QUETAME'),(530,25,596,'QUIPILE'),(531,25,599,'APULO'),(532,25,612,'RICAURTE'),(533,25,645,'SAN ANTONIO DEL TEQUENDAMA'),(534,25,649,'SAN BERNARDO'),(535,25,653,'SAN CAYETANO'),(536,25,658,'SAN FRANCISCO'),(537,25,662,'SAN JUAN DE RIO SECO'),(538,25,718,'SASAIMA'),(539,25,736,'SESQUILE'),(540,25,740,'SIBATE'),(541,25,743,'SILVANIA'),(542,25,745,'SIMIJACA'),(543,25,754,'SOACHA'),(544,25,758,'SOPO'),(545,25,769,'SUBACHOQUE'),(546,25,772,'SUESCA'),(547,25,777,'SUPATA'),(548,25,779,'SUSA'),(549,25,781,'SUTATAUSA'),(550,25,785,'TABIO'),(551,25,793,'TAUSA'),(552,25,797,'TENA'),(553,25,799,'TENJO'),(554,25,805,'TIBACUY'),(555,25,807,'TIBIRITA'),(556,25,815,'TOCAIMA'),(557,25,817,'TOCANCIPA'),(558,25,823,'TOPAIPI'),(559,25,839,'UBALA'),(560,25,841,'UBAQUE'),(561,25,843,'VILLA DE SAN DIEGO DE UBATE'),(562,25,845,'UNE'),(563,25,851,'UTICA'),(564,25,862,'VERGARA'),(565,25,867,'VIANI'),(566,25,871,'VILLAGOMEZ'),(567,25,873,'VILLAPINZON'),(568,25,875,'VILLETA'),(569,25,878,'VIOTA'),(570,25,885,'YACOPI'),(571,25,898,'ZIPACON'),(572,25,899,'ZIPAQUIRA'),(573,27,1,'QUIBDO'),(574,27,6,'ACANDI'),(575,27,25,'ALTO BAUDO'),(576,27,50,'ATRATO'),(577,27,73,'BAGADO'),(578,27,75,'BAHIA SOLANO'),(579,27,77,'BAJO BAUDO'),(580,27,99,'BOJAYA'),(581,27,135,'EL CANTON DEL SAN PABLO'),(582,27,150,'CARMEN DEL DARIEN'),(583,27,160,'CERTEGUI'),(584,27,205,'CONDOTO'),(585,27,245,'EL CARMEN DE ATRATO'),(586,27,250,'EL LITORAL DEL SAN JUAN'),(587,27,361,'ISTMINA'),(588,27,372,'JURADO'),(589,27,413,'LLORO'),(590,27,425,'MEDIO ATRATO'),(591,27,430,'MEDIO BAUDO'),(592,27,450,'MEDIO SAN JUAN'),(593,27,491,'NOVITA'),(594,27,495,'NUQUI'),(595,27,580,'RIO IRO'),(596,27,600,'RIO QUITO'),(597,27,615,'RIOSUCIO'),(598,27,660,'SAN JOSE DEL PALMAR'),(599,27,745,'SIPI'),(600,27,787,'TADO'),(601,27,800,'UNGUIA'),(602,27,810,'UNION PANAMERICANA'),(603,41,1,'NEIVA'),(604,41,6,'ACEVEDO'),(605,41,13,'AGRADO'),(606,41,16,'AIPE'),(607,41,20,'ALGECIRAS'),(608,41,26,'ALTAMIRA'),(609,41,78,'BARAYA'),(610,41,132,'CAMPOALEGRE'),(611,41,206,'COLOMBIA'),(612,41,244,'ELIAS'),(613,41,298,'GARZON'),(614,41,306,'GIGANTE'),(615,41,319,'GUADALUPE'),(616,41,349,'HOBO'),(617,41,357,'IQUIRA'),(618,41,359,'ISNOS'),(619,41,378,'LA ARGENTINA'),(620,41,396,'LA PLATA'),(621,41,483,'NATAGA'),(622,41,503,'OPORAPA'),(623,41,518,'PAICOL'),(624,41,524,'PALERMO'),(625,41,530,'PALESTINA'),(626,41,548,'PITAL'),(627,41,551,'PITALITO'),(628,41,615,'RIVERA'),(629,41,660,'SALADOBLANCO'),(630,41,668,'SAN AGUSTIN'),(631,41,676,'SANTA MARIA'),(632,41,770,'SUAZA'),(633,41,791,'TARQUI'),(634,41,797,'TESALIA'),(635,41,799,'TELLO'),(636,41,801,'TERUEL'),(637,41,807,'TIMANA'),(638,41,872,'VILLAVIEJA'),(639,41,885,'YAGUARA'),(640,44,1,'RIOHACHA'),(641,44,35,'ALBANIA'),(642,44,78,'BARRANCAS'),(643,44,90,'DIBULLA'),(644,44,98,'DISTRACCION'),(645,44,110,'EL MOLINO'),(646,44,279,'FONSECA'),(647,44,378,'HATONUEVO'),(648,44,420,'LA JAGUA DEL PILAR'),(649,44,430,'MAICAO'),(650,44,560,'MANAURE'),(651,44,650,'SAN JUAN DEL CESAR'),(652,44,847,'URIBIA'),(653,44,855,'URUMITA'),(654,44,874,'VILLANUEVA'),(655,47,1,'SANTA MARTA'),(656,47,30,'ALGARROBO'),(657,47,53,'ARACATACA'),(658,47,58,'ARIGUANI'),(659,47,161,'CERRO SAN ANTONIO'),(660,47,170,'CHIBOLO'),(661,47,189,'CIENAGA'),(662,47,205,'CONCORDIA'),(663,47,245,'EL BANCO'),(664,47,258,'EL PI¥ON'),(665,47,268,'EL RETEN'),(666,47,288,'FUNDACION'),(667,47,318,'GUAMAL'),(668,47,460,'NUEVA GRANADA'),(669,47,541,'PEDRAZA'),(670,47,545,'PIJI¥O DEL CARMEN'),(671,47,551,'PIVIJAY'),(672,47,555,'PLATO'),(673,47,570,'PUEBLOVIEJO'),(674,47,605,'REMOLINO'),(675,47,660,'SABANAS DE SAN ANGEL'),(676,47,675,'SALAMINA'),(677,47,692,'SAN SEBASTIAN DE BUENAVISTA'),(678,47,703,'SAN ZENON'),(679,47,707,'SANTA ANA'),(680,47,720,'SANTA BARBARA DE PINTO'),(681,47,745,'SITIONUEVO'),(682,47,798,'TENERIFE'),(683,47,960,'ZAPAYAN'),(684,47,980,'ZONA BANANERA'),(685,50,1,'VILLAVICENCIO'),(686,50,6,'ACACIAS'),(687,50,110,'BARRANCA DE UPIA'),(688,50,124,'CABUYARO'),(689,50,150,'CASTILLA LA NUEVA'),(690,50,223,'CUBARRAL'),(691,50,226,'CUMARAL'),(692,50,245,'EL CALVARIO'),(693,50,251,'EL CASTILLO'),(694,50,270,'EL DORADO'),(695,50,287,'FUENTE DE ORO'),(696,50,313,'GRANADA'),(697,50,318,'GUAMAL'),(698,50,325,'MAPIRIPAN'),(699,50,330,'MESETAS'),(700,50,350,'LA MACARENA'),(701,50,370,'URIBE'),(702,50,400,'LEJANIAS'),(703,50,450,'PUERTO CONCORDIA'),(704,50,568,'PUERTO GAITAN'),(705,50,573,'PUERTO LOPEZ'),(706,50,577,'PUERTO LLERAS'),(707,50,590,'PUERTO RICO'),(708,50,606,'RESTREPO'),(709,50,680,'SAN CARLOS DE GUAROA'),(710,50,683,'SAN JUAN DE ARAMA'),(711,50,686,'SAN JUANITO'),(712,50,689,'SAN MARTIN'),(713,50,711,'VISTAHERMOSA'),(714,52,1,'PASTO'),(715,52,19,'ALBAN'),(716,52,22,'ALDANA'),(717,52,36,'ANCUYA'),(718,52,51,'ARBOLEDA'),(719,52,79,'BARBACOAS'),(720,52,83,'BELEN'),(721,52,110,'BUESACO'),(722,52,203,'COLON'),(723,52,207,'CONSACA'),(724,52,210,'CONTADERO'),(725,52,215,'CORDOBA'),(726,52,224,'CUASPUD'),(727,52,227,'CUMBAL'),(728,52,233,'CUMBITARA'),(729,52,240,'CHACHAGsI'),(730,52,250,'EL CHARCO'),(731,52,254,'EL PE¥OL'),(732,52,256,'EL ROSARIO'),(733,52,258,'EL TABLON DE GOMEZ'),(734,52,260,'EL TAMBO'),(735,52,287,'FUNES'),(736,52,317,'GUACHUCAL'),(737,52,320,'GUAITARILLA'),(738,52,323,'GUALMATAN'),(739,52,352,'ILES'),(740,52,354,'IMUES'),(741,52,356,'IPIALES'),(742,52,378,'LA CRUZ'),(743,52,381,'LA FLORIDA'),(744,52,385,'LA LLANADA'),(745,52,390,'LA TOLA'),(746,52,399,'LA UNION'),(747,52,405,'LEIVA'),(748,52,411,'LINARES'),(749,52,418,'LOS ANDES'),(750,52,427,'MAGsI'),(751,52,435,'MALLAMA'),(752,52,473,'MOSQUERA'),(753,52,480,'NARI¥O'),(754,52,490,'OLAYA HERRERA'),(755,52,506,'OSPINA'),(756,52,520,'FRANCISCO PIZARRO'),(757,52,540,'POLICARPA'),(758,52,560,'POTOSI'),(759,52,565,'PROVIDENCIA'),(760,52,573,'PUERRES'),(761,52,585,'PUPIALES'),(762,52,612,'RICAURTE'),(763,52,621,'ROBERTO PAYAN'),(764,52,678,'SAMANIEGO'),(765,52,683,'SANDONA'),(766,52,685,'SAN BERNARDO'),(767,52,687,'SAN LORENZO'),(768,52,693,'SAN PABLO'),(769,52,694,'SAN PEDRO DE CARTAGO'),(770,52,696,'SANTA BARBARA'),(771,52,699,'SANTACRUZ'),(772,52,720,'SAPUYES'),(773,52,786,'TAMINANGO'),(774,52,788,'TANGUA'),(775,52,835,'SAN ANDRES DE TUMACO'),(776,52,838,'TUQUERRES'),(777,52,885,'YACUANQUER'),(778,54,1,'CUCUTA'),(779,54,3,'ABREGO'),(780,54,51,'ARBOLEDAS'),(781,54,99,'BOCHALEMA'),(782,54,109,'BUCARASICA'),(783,54,125,'CACOTA'),(784,54,128,'CACHIRA'),(785,54,172,'CHINACOTA'),(786,54,174,'CHITAGA'),(787,54,206,'CONVENCION'),(788,54,223,'CUCUTILLA'),(789,54,239,'DURANIA'),(790,54,245,'EL CARMEN'),(791,54,250,'EL TARRA'),(792,54,261,'EL ZULIA'),(793,54,313,'GRAMALOTE'),(794,54,344,'HACARI'),(795,54,347,'HERRAN'),(796,54,377,'LABATECA'),(797,54,385,'LA ESPERANZA'),(798,54,398,'LA PLAYA'),(799,54,405,'LOS PATIOS'),(800,54,418,'LOURDES'),(801,54,480,'MUTISCUA'),(802,54,498,'OCA¥A'),(803,54,518,'PAMPLONA'),(804,54,520,'PAMPLONITA'),(805,54,553,'PUERTO SANTANDER'),(806,54,599,'RAGONVALIA'),(807,54,660,'SALAZAR'),(808,54,670,'SAN CALIXTO'),(809,54,673,'SAN CAYETANO'),(810,54,680,'SANTIAGO'),(811,54,720,'SARDINATA'),(812,54,743,'SILOS'),(813,54,800,'TEORAMA'),(814,54,810,'TIBU'),(815,54,820,'TOLEDO'),(816,54,871,'VILLA CARO'),(817,54,874,'VILLA DEL ROSARIO'),(818,63,1,'ARMENIA'),(819,63,111,'BUENAVISTA'),(820,63,130,'CALARCA'),(821,63,190,'CIRCASIA'),(822,63,212,'CORDOBA'),(823,63,272,'FILANDIA'),(824,63,302,'GENOVA'),(825,63,401,'LA TEBAIDA'),(826,63,470,'MONTENEGRO'),(827,63,548,'PIJAO'),(828,63,594,'QUIMBAYA'),(829,63,690,'SALENTO'),(830,66,1,'PEREIRA'),(831,66,45,'APIA'),(832,66,75,'BALBOA'),(833,66,88,'BELEN DE UMBRIA'),(834,66,170,'DOSQUEBRADAS'),(835,66,318,'GUATICA'),(836,66,383,'LA CELIA'),(837,66,400,'LA VIRGINIA'),(838,66,440,'MARSELLA'),(839,66,456,'MISTRATO'),(840,66,572,'PUEBLO RICO'),(841,66,594,'QUINCHIA'),(842,66,682,'SANTA ROSA DE CABAL'),(843,66,687,'SANTUARIO'),(844,68,1,'BUCARAMANGA'),(845,68,13,'AGUADA'),(846,68,20,'ALBANIA'),(847,68,51,'ARATOCA'),(848,68,77,'BARBOSA'),(849,68,79,'BARICHARA'),(850,68,81,'BARRANCABERMEJA'),(851,68,92,'BETULIA'),(852,68,101,'BOLIVAR'),(853,68,121,'CABRERA'),(854,68,132,'CALIFORNIA'),(855,68,147,'CAPITANEJO'),(856,68,152,'CARCASI'),(857,68,160,'CEPITA'),(858,68,162,'CERRITO'),(859,68,167,'CHARALA'),(860,68,169,'CHARTA'),(861,68,176,'CHIMA'),(862,68,179,'CHIPATA'),(863,68,190,'CIMITARRA'),(864,68,207,'CONCEPCION'),(865,68,209,'CONFINES'),(866,68,211,'CONTRATACION'),(867,68,217,'COROMORO'),(868,68,229,'CURITI'),(869,68,235,'EL CARMEN DE CHUCURI'),(870,68,245,'EL GUACAMAYO'),(871,68,250,'EL PE¥ON'),(872,68,255,'EL PLAYON'),(873,68,264,'ENCINO'),(874,68,266,'ENCISO'),(875,68,271,'FLORIAN'),(876,68,276,'FLORIDABLANCA'),(877,68,296,'GALAN'),(878,68,298,'GAMBITA'),(879,68,307,'GIRON'),(880,68,318,'GUACA'),(881,68,320,'GUADALUPE'),(882,68,322,'GUAPOTA'),(883,68,324,'GUAVATA'),(884,68,327,'GsEPSA'),(885,68,344,'HATO'),(886,68,368,'JESUS MARIA'),(887,68,370,'JORDAN'),(888,68,377,'LA BELLEZA'),(889,68,385,'LANDAZURI'),(890,68,397,'LA PAZ'),(891,68,406,'LEBRIJA'),(892,68,418,'LOS SANTOS'),(893,68,425,'MACARAVITA'),(894,68,432,'MALAGA'),(895,68,444,'MATANZA'),(896,68,464,'MOGOTES'),(897,68,468,'MOLAGAVITA'),(898,68,498,'OCAMONTE'),(899,68,500,'OIBA'),(900,68,502,'ONZAGA'),(901,68,522,'PALMAR'),(902,68,524,'PALMAS DEL SOCORRO'),(903,68,533,'PARAMO'),(904,68,547,'PIEDECUESTA'),(905,68,549,'PINCHOTE'),(906,68,572,'PUENTE NACIONAL'),(907,68,573,'PUERTO PARRA'),(908,68,575,'PUERTO WILCHES'),(909,68,615,'RIONEGRO'),(910,68,655,'SABANA DE TORRES'),(911,68,669,'SAN ANDRES'),(912,68,673,'SAN BENITO'),(913,68,679,'SAN GIL'),(914,68,682,'SAN JOAQUIN'),(915,68,684,'SAN JOSE DE MIRANDA'),(916,68,686,'SAN MIGUEL'),(917,68,689,'SAN VICENTE DE CHUCURI'),(918,68,705,'SANTA BARBARA'),(919,68,720,'SANTA HELENA DEL OPON'),(920,68,745,'SIMACOTA'),(921,68,755,'SOCORRO'),(922,68,770,'SUAITA'),(923,68,773,'SUCRE'),(924,68,780,'SURATA'),(925,68,820,'TONA'),(926,68,855,'VALLE DE SAN JOSE'),(927,68,861,'VELEZ'),(928,68,867,'VETAS'),(929,68,872,'VILLANUEVA'),(930,68,895,'ZAPATOCA'),(931,70,1,'SINCELEJO'),(932,70,110,'BUENAVISTA'),(933,70,124,'CAIMITO'),(934,70,204,'COLOSO'),(935,70,215,'COROZAL'),(936,70,221,'COVE¥AS'),(937,70,230,'CHALAN'),(938,70,233,'EL ROBLE'),(939,70,235,'GALERAS'),(940,70,265,'GUARANDA'),(941,70,400,'LA UNION'),(942,70,418,'LOS PALMITOS'),(943,70,429,'MAJAGUAL'),(944,70,473,'MORROA'),(945,70,508,'OVEJAS'),(946,70,523,'PALMITO'),(947,70,670,'SAMPUES'),(948,70,678,'SAN BENITO ABAD'),(949,70,702,'SAN JUAN DE BETULIA'),(950,70,708,'SAN MARCOS'),(951,70,713,'SAN ONOFRE'),(952,70,717,'SAN PEDRO'),(953,70,742,'SAN LUIS DE SINCE'),(954,70,771,'SUCRE'),(955,70,820,'SANTIAGO DE TOLU'),(956,70,823,'TOLU VIEJO'),(957,73,1,'IBAGUE'),(958,73,24,'ALPUJARRA'),(959,73,26,'ALVARADO'),(960,73,30,'AMBALEMA'),(961,73,43,'ANZOATEGUI'),(962,73,55,'ARMERO'),(963,73,67,'ATACO'),(964,73,124,'CAJAMARCA'),(965,73,148,'CARMEN DE APICALA'),(966,73,152,'CASABIANCA'),(967,73,168,'CHAPARRAL'),(968,73,200,'COELLO'),(969,73,217,'COYAIMA'),(970,73,226,'CUNDAY'),(971,73,236,'DOLORES'),(972,73,268,'ESPINAL'),(973,73,270,'FALAN'),(974,73,275,'FLANDES'),(975,73,283,'FRESNO'),(976,73,319,'GUAMO'),(977,73,347,'HERVEO'),(978,73,349,'HONDA'),(979,73,352,'ICONONZO'),(980,73,408,'LERIDA'),(981,73,411,'LIBANO'),(982,73,443,'MARIQUITA'),(983,73,449,'MELGAR'),(984,73,461,'MURILLO'),(985,73,483,'NATAGAIMA'),(986,73,504,'ORTEGA'),(987,73,520,'PALOCABILDO'),(988,73,547,'PIEDRAS'),(989,73,555,'PLANADAS'),(990,73,563,'PRADO'),(991,73,585,'PURIFICACION'),(992,73,616,'RIOBLANCO'),(993,73,622,'RONCESVALLES'),(994,73,624,'ROVIRA'),(995,73,671,'SALDA¥A'),(996,73,675,'SAN ANTONIO'),(997,73,678,'SAN LUIS'),(998,73,686,'SANTA ISABEL'),(999,73,770,'SUAREZ'),(1000,73,854,'VALLE DE SAN JUAN'),(1001,73,861,'VENADILLO'),(1002,73,870,'VILLAHERMOSA'),(1003,73,873,'VILLARRICA'),(1004,76,1,'CALI'),(1005,76,20,'ALCALA'),(1006,76,36,'ANDALUCIA'),(1007,76,41,'ANSERMANUEVO'),(1008,76,54,'ARGELIA'),(1009,76,100,'BOLIVAR'),(1010,76,109,'BUENAVENTURA'),(1011,76,111,'GUADALAJARA DE BUGA'),(1012,76,113,'BUGALAGRANDE'),(1013,76,122,'CAICEDONIA'),(1014,76,126,'CALIMA'),(1015,76,130,'CANDELARIA'),(1016,76,147,'CARTAGO'),(1017,76,233,'DAGUA'),(1018,76,243,'EL AGUILA'),(1019,76,246,'EL CAIRO'),(1020,76,248,'EL CERRITO'),(1021,76,250,'EL DOVIO'),(1022,76,275,'FLORIDA'),(1023,76,306,'GINEBRA'),(1024,76,318,'GUACARI'),(1025,76,364,'JAMUNDI'),(1026,76,377,'LA CUMBRE'),(1027,76,400,'LA UNION'),(1028,76,403,'LA VICTORIA'),(1029,76,497,'OBANDO'),(1030,76,520,'PALMIRA'),(1031,76,563,'PRADERA'),(1032,76,606,'RESTREPO'),(1033,76,616,'RIOFRIO'),(1034,76,622,'ROLDANILLO'),(1035,76,670,'SAN PEDRO'),(1036,76,736,'SEVILLA'),(1037,76,823,'TORO'),(1038,76,828,'TRUJILLO'),(1039,76,834,'TULUA'),(1040,76,845,'ULLOA'),(1041,76,863,'VERSALLES'),(1042,76,869,'VIJES'),(1043,76,890,'YOTOCO'),(1044,76,892,'YUMBO'),(1045,76,895,'ZARZAL'),(1046,81,1,'ARAUCA'),(1047,81,65,'ARAUQUITA'),(1048,81,220,'CRAVO NORTE'),(1049,81,300,'FORTUL'),(1050,81,591,'PUERTO RONDON'),(1051,81,736,'SARAVENA'),(1052,81,794,'TAME'),(1053,85,1,'YOPAL'),(1054,85,10,'AGUAZUL'),(1055,85,15,'CHAMEZA'),(1056,85,125,'HATO COROZAL'),(1057,85,136,'LA SALINA'),(1058,85,139,'MANI'),(1059,85,162,'MONTERREY'),(1060,85,225,'NUNCHIA'),(1061,85,230,'OROCUE'),(1062,85,250,'PAZ DE ARIPORO'),(1063,85,263,'PORE'),(1064,85,279,'RECETOR'),(1065,85,300,'SABANALARGA'),(1066,85,315,'SACAMA'),(1067,85,325,'SAN LUIS DE PALENQUE'),(1068,85,400,'TAMARA'),(1069,85,410,'TAURAMENA'),(1070,85,430,'TRINIDAD'),(1071,85,440,'VILLANUEVA'),(1072,86,1,'MOCOA'),(1073,86,219,'COLON'),(1074,86,320,'ORITO'),(1075,86,568,'PUERTO ASIS'),(1076,86,569,'PUERTO CAICEDO'),(1077,86,571,'PUERTO GUZMAN'),(1078,86,573,'LEGUIZAMO'),(1079,86,749,'SIBUNDOY'),(1080,86,755,'SAN FRANCISCO'),(1081,86,757,'SAN MIGUEL'),(1082,86,760,'SANTIAGO'),(1083,86,865,'VALLE DEL GUAMUEZ'),(1084,86,885,'VILLAGARZON'),(1085,88,1,'SAN ANDRES'),(1086,88,564,'PROVIDENCIA'),(1087,91,1,'LETICIA'),(1088,91,263,'EL ENCANTO'),(1089,91,405,'LA CHORRERA'),(1090,91,407,'LA PEDRERA'),(1091,91,430,'LA VICTORIA'),(1092,91,460,'MIRITI - PARANA'),(1093,91,530,'PUERTO ALEGRIA'),(1094,91,536,'PUERTO ARICA'),(1095,91,540,'PUERTO NARI¥O'),(1096,91,669,'PUERTO SANTANDER'),(1097,91,798,'TARAPACA'),(1098,94,1,'INIRIDA'),(1099,94,343,'BARRANCO MINAS'),(1100,94,663,'MAPIRIPANA'),(1101,94,883,'SAN FELIPE'),(1102,94,884,'PUERTO COLOMBIA'),(1103,94,885,'LA GUADALUPE'),(1104,94,886,'CACAHUAL'),(1105,94,887,'PANA PANA'),(1106,94,888,'MORICHAL'),(1107,95,1,'SAN JOSE DEL GUAVIARE'),(1108,95,15,'CALAMAR'),(1109,95,25,'EL RETORNO'),(1110,95,200,'MIRAFLORES'),(1111,97,1,'MITU'),(1112,97,161,'CARURU'),(1113,97,511,'PACOA'),(1114,97,666,'TARAIRA'),(1115,97,777,'PAPUNAUA'),(1116,97,889,'YAVARATE'),(1117,99,1,'PUERTO CARRE¥O'),(1118,99,524,'LA PRIMAVERA'),(1119,99,624,'SANTA ROSALIA'),(1120,99,773,'CUMARIBO'),(1121,0,0,'NA');
/*!40000 ALTER TABLE `municipios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `paises`
--

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

--
-- Dumping data for table `paises`
--

LOCK TABLES `paises` WRITE;
/*!40000 ALTER TABLE `paises` DISABLE KEYS */;
INSERT INTO `paises` VALUES (1,13,'AFGANISTAN','AF\r'),(2,17,'ALBANIA','AL\r'),(3,23,'ALEMANIA','DE\r'),(4,37,'ANDORRA','AD\r'),(5,40,'ANGOLA','AO\r'),(6,41,'ANGUILLA','0'),(7,43,'ANTIGUA Y BARBUDA','AG\r'),(8,47,'ANTILLAS HOLANDESAS','0'),(9,53,'ARABIA SAUDITA','SA\r'),(10,59,'ARGELIA','DZ\r'),(11,63,'ARGENTINA','AR\r'),(12,26,'ARMENIA','AM\r'),(13,27,'ARUBA','AW\r'),(14,69,'AUSTRALIA','AU\r'),(15,72,'AUSTRIA','AT\r'),(16,74,'AZERBAIJAN','0'),(17,77,'BAHAMAS','BS\r'),(18,80,'BAHREIN','0'),(19,81,'BANGLADESH','0'),(20,83,'BARBADOS','BB\r'),(21,91,'BELARUS','0'),(22,87,'BELGICA','BE\r'),(23,88,'BELICE','BZ\r'),(24,229,'BENIN','BJ\r'),(25,90,'BERMUDAS','BM\r'),(26,93,'BIRMANIA (MYANMAR)','0'),(27,97,'BOLIVIA','BO\r'),(28,29,'BOSNIA-HERZEGOVINA','0'),(29,101,'BOTSWANA','0'),(30,105,'BRASIL','BR\r'),(31,108,'BRUNEI DARUSSALAM','0'),(32,111,'BULGARIA','BG\r'),(33,31,'BURKINA FASSO','0'),(34,115,'BURUNDI','BI\r'),(35,119,'BUTAN','BT\r'),(36,127,'CABO VERDE','CV\r'),(37,137,'CAIMAN, ISLAS','0'),(38,141,'CAMBOYA (KAMPUCHEA)','0'),(39,145,'CAMERUN, REPUBLICA UNIDA DEL','0'),(40,149,'CANADA','CA\r'),(41,203,'CHAD','TD\r'),(42,211,'CHILE','CL\r'),(43,215,'CHINA','CN\r'),(44,221,'CHIPRE','CY\r'),(45,165,'COCOS (KEELING), ISLAS','0'),(46,169,'COLOMBIA','CO\r'),(47,173,'COMORAS','KM\r'),(48,177,'CONGO','0'),(49,183,'COOK, ISLAS','0'),(50,187,'COREA (NORTE), REPUBLICA POPULAR DEMOCRATICA DE','0'),(51,190,'COREA (SUR), REPUBLICA DE','0'),(52,193,'COSTA DE MARFIL','CI\r'),(53,196,'COSTA RICA','CR\r'),(54,198,'CROACIA','HR\r'),(55,199,'CUBA','CU\r'),(56,232,'DINAMARCA','DK\r'),(57,783,'DJIBOUTI','0'),(58,235,'DOMINICA','DM\r'),(59,239,'ECUADOR','EC\r'),(60,240,'EGIPTO','EG\r'),(61,242,'EL SALVADOR','SV\r'),(62,244,'EMIRATOS ARABES UNIDOS','AE\r'),(63,243,'ERITREA','ER\r'),(64,246,'ESLOVAQUIA','SK\r'),(65,247,'ESLOVENIA','SI\r'),(66,245,'ESPA¥A','0'),(67,249,'ESTADOS UNIDOS','US\r'),(68,251,'ESTONIA','EE\r'),(69,253,'ETIOPIA','ET\r'),(70,259,'FEROE, ISLAS','0'),(71,870,'FIJI','0'),(72,267,'FILIPINAS','PH\r'),(73,271,'FINLANDIA','FI\r'),(74,275,'FRANCIA','FR\r'),(75,281,'GABON','GA\r'),(76,285,'GAMBIA','GM\r'),(77,287,'GEORGIA','GE\r'),(78,289,'GHANA','GH\r'),(79,293,'GIBRALTAR','GI\r'),(80,297,'GRANADA','GD\r'),(81,301,'GRECIA','GR\r'),(82,305,'GROENLANDIA','GL\r'),(83,309,'GUADALUPE','GP\r'),(84,313,'GUAM','GU\r'),(85,317,'GUATEMALA','GT\r'),(86,337,'GUAYANA','0'),(87,325,'GUAYANA FRANCESA','GF\r'),(88,329,'GUINEA','GN\r'),(89,331,'GUINEA ECUATORIAL','GQ\r'),(90,334,'GUINEA-BISSAU','GW\r'),(91,341,'HAITI','HT\r'),(92,345,'HONDURAS','HN\r'),(93,351,'HONG KONG','HK\r'),(94,355,'HUNGRIA','HU\r'),(95,361,'INDIA','IN\r'),(96,365,'INDONESIA','ID\r'),(97,369,'IRAK','IQ\r'),(98,372,'IRAN, REPUBLICA ISLAMICA DEL','0'),(99,375,'IRLANDA (EIRE)','0'),(100,379,'ISLANDIA','IS\r'),(101,383,'ISRAEL','IL\r'),(102,386,'ITALIA','IT\r'),(103,391,'JAMAICA','JM\r'),(104,399,'JAPON','JP\r'),(105,403,'JORDANIA','JO\r'),(106,406,'KASAJSTAN','0'),(107,410,'KENIA','KE\r'),(108,412,'KIRGUIZISTAN','0'),(109,411,'KIRIBATI','KI\r'),(110,413,'KUWAIT','KW\r'),(111,420,'LAOS, REPUBLICA POPULAR DEMOCRATICA DE','0'),(112,426,'LESOTHO','0'),(113,429,'LETONIA','LV\r'),(114,431,'LIBANO','LB\r'),(115,434,'LIBERIA','LR\r'),(116,438,'LIBIA (INCLUYE FEZZAN)','0'),(117,440,'LIECHTENSTEIN','LI\r'),(118,443,'LITUANIA','LT\r'),(119,445,'LUXEMBURGO','LU\r'),(120,447,'MACAO','MO\r'),(121,448,'MACEDONIA','0'),(122,450,'MADAGASCAR','MG\r'),(123,458,'MALAWI','0'),(124,455,'MALAYSIA','0'),(125,461,'MALDIVAS','MV\r'),(126,464,'MALI','ML\r'),(127,467,'MALTA','MT\r'),(128,469,'MARIANAS DEL NORTE, ISLAS','0'),(129,474,'MARRUECOS','MA\r'),(130,472,'MARSHALL, ISLAS','0'),(131,477,'MARTINICA','MQ\r'),(132,485,'MAURICIO','MU\r'),(133,488,'MAURITANIA','MR\r'),(134,493,'MEXICO','MX\r'),(135,494,'MICRONESIA, ESTADOS FEDERADOS DE','0'),(136,496,'MOLDAVIA','MD\r'),(137,498,'MONACO','MC\r'),(138,497,'MONGOLIA','MN\r'),(139,501,'MONSERRAT, ISLA','0'),(140,505,'MOZAMBIQUE','MZ\r'),(141,507,'NAMIBIA','NA\r'),(142,508,'NAURU','NR\r'),(143,511,'NAVIDAD (CHRISTMAS), ISLAS','0'),(144,517,'NEPAL','NP\r'),(145,521,'NICARAGUA','NI\r'),(146,525,'NIGER','NE\r'),(147,528,'NIGERIA','NG\r'),(148,531,'NIUE, ISLA','0'),(149,535,'NORFOLK, ISLA','0'),(150,538,'NORUEGA','NO\r'),(151,542,'NUEVA CALEDONIA','NC\r'),(152,548,'NUEVA ZELANDIA','0'),(153,556,'OMAN','OM\r'),(154,566,'PACIFICO, ISLAS (USA)','0'),(155,573,'PAISES BAJOS (HOLANDA)','0'),(156,576,'PAKISTAN','PK\r'),(157,578,'PALAU, ISLAS','0'),(158,580,'PANAMA','PA\r'),(159,545,'PAPUASIA NUEVA GUINEA','0'),(160,586,'PARAGUAY','PY\r'),(161,589,'PERU','PE\r'),(162,593,'PITCAIRN, ISLA','0'),(163,599,'POLINESIA FRANCESA','PF\r'),(164,603,'POLONIA','PL\r'),(165,607,'PORTUGAL','PT\r'),(166,611,'PUERTO RICO','PR\r'),(167,618,'QATAR','0'),(168,628,'REINO UNIDO','GB\r'),(169,640,'REPUBLICA CENTROAFRICANA','CF\r'),(170,644,'REPUBLICA CHECA','CZ\r'),(171,647,'REPUBLICA DOMINICANA','DO\r'),(172,660,'REUNION','RE\r'),(173,675,'RUANDA','RW\r'),(174,670,'RUMANIA','RO\r'),(175,676,'RUSIA','RU\r'),(176,685,'SAHARA OCCIDENTAL','0'),(177,677,'SALOMON, ISLAS','0'),(178,687,'SAMOA','WS\r'),(179,690,'SAMOA NORTEAMERICANA','0'),(180,695,'SAN CRISTOBAL Y NIEVES','KN\r'),(181,697,'SAN MARINO','SM\r'),(182,700,'SAN PEDRO Y MIGUELON','0'),(183,705,'SAN VICENTE Y LAS GRANADINAS','VC\r'),(184,710,'SANTA ELENA','0'),(185,715,'SANTA LUCIA','LC\r'),(186,159,'SANTA SEDE','0'),(187,720,'SANTO TOME Y PRINCIPE','ST\r'),(188,728,'SENEGAL','SN\r'),(189,731,'SEYCHELLES','SC\r'),(190,735,'SIERRA LEONA','SL\r'),(191,741,'SINGAPUR','SG\r'),(192,744,'SIRIA, REPUBLICA ARABE DE','0'),(193,748,'SOMALIA','SO\r'),(194,750,'SRI LANKA','LK\r'),(195,756,'SUDAFRICA, REPUBLICA DE','0'),(196,759,'SUDAN','SD\r'),(197,764,'SUECIA','SE\r'),(198,767,'SUIZA','CH\r'),(199,770,'SURINAM','SR\r'),(200,773,'SWAZILANDIA','0'),(201,774,'TADJIKISTAN','0'),(202,776,'TAILANDIA','TH\r'),(203,218,'TAIWAN (FORMOSA)','0'),(204,780,'TANZANIA, REPUBLICA UNIDA DE','0'),(205,787,'TERRITORIO BRITANICO DEL OCEANO INDICO','IO\r'),(206,788,'TIMOR DEL ESTE','0'),(207,800,'TOGO','TG\r'),(208,805,'TOKELAU','TK\r'),(209,810,'TONGA','TO\r'),(210,815,'TRINIDAD Y TOBAGO','TT\r'),(211,820,'TUNICIA','0'),(212,823,'TURCAS Y CAICOS, ISLAS','0'),(213,825,'TURKMENISTAN','TM\r'),(214,827,'TURQUIA','TR\r'),(215,828,'TUVALU','TV\r'),(216,830,'UCRANIA','UA\r'),(217,833,'UGANDA','UG\r'),(218,845,'URUGUAY','UY\r'),(219,847,'UZBEKISTAN','UZ\r'),(220,551,'VANUATU','VU\r'),(221,850,'VENEZUELA','VE\r'),(222,855,'VIET NAM','0'),(223,863,'VIRGENES, ISLAS (BRITANICAS)','0'),(224,866,'VIRGENES, ISLAS (NORTEAMERICANAS)','0'),(225,875,'WALLIS Y FORTUNA, ISLAS','0'),(226,880,'YEMEN','YE\r'),(227,885,'YUGOSLAVIA','0'),(228,888,'ZAIRE','0'),(229,890,'ZAMBIA','ZM\r'),(230,665,'ZIMBABWE','0'),(231,897,'ZONA NEUTRAL PALESTINA','0'),(232,0,'NA','0');
/*!40000 ALTER TABLE `paises` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permisos`
--

DROP TABLE IF EXISTS `permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permisos` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permisos`
--

LOCK TABLES `permisos` WRITE;
/*!40000 ALTER TABLE `permisos` DISABLE KEYS */;
INSERT INTO `permisos` VALUES (1,'Manejo de caja'),(2,'Facturacion POS'),(3,'devoluciones'),(4,'creacion de recibo de ingresos'),(5,'creacionusuario'),(6,'creacionsuperusuario'),(7,'creacionsuperusuarioadministrador'),(8,'creacionempresa');
/*!40000 ALTER TABLE `permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permisos_roles`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permisos_roles`
--

LOCK TABLES `permisos_roles` WRITE;
/*!40000 ALTER TABLE `permisos_roles` DISABLE KEYS */;
INSERT INTO `permisos_roles` VALUES (1,1,1,'ACTIVO',0,'2025-07-23 12:26:14'),(2,2,1,'ACTIVO',0,'2025-07-23 12:26:14'),(3,2,1,'ACTIVO',0,'2025-07-23 12:26:14'),(4,4,1,'ACTIVO',0,'2025-07-23 12:26:14'),(5,5,2,'ACTIVO',0,'2025-07-23 12:30:57'),(6,6,2,'ACTIVO',0,'2025-07-23 12:30:57'),(7,8,2,'ACTIVO',0,'2025-08-09 10:49:53');
/*!40000 ALTER TABLE `permisos_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `regimen`
--

DROP TABLE IF EXISTS `regimen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `regimen` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `codigoRegimen` varchar(20) DEFAULT NULL,
  `descripcion` varchar(45) DEFAULT NULL,
  `estado` varchar(8) DEFAULT 'ACTIVO',
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `regimen`
--

LOCK TABLES `regimen` WRITE;
/*!40000 ALTER TABLE `regimen` DISABLE KEYS */;
INSERT INTO `regimen` VALUES (1,'48','Responsable de iva','ACTIVO'),(2,'49','No responsable de iva','ACTIVO'),(3,'47','Regimen simple de tributación','ACTIVO');
/*!40000 ALTER TABLE `regimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'usuariocaja'),(2,'superusuario');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sesiones`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sesiones`
--

LOCK TABLES `sesiones` WRITE;
/*!40000 ALTER TABLE `sesiones` DISABLE KEYS */;
INSERT INTO `sesiones` VALUES (6,2,'2025-07-27 23:15:24','1990-01-01 00:00:00',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYWphMSIsIm5pdmVsIjoidXN1YXJpb2NhamEiLCJpZHNlY2lvbiI6IjQxZWU4MmI1LTQ5MTEtNDk3MC1hMjE0LTM4MzhlYjYwNDFmOCIsImlhdCI6MTc1MzY3NjEyNCwiZXhwIjoxNzUzNzYyNTI0fQ.Au1h1GG5fePPA2yg7AE747kt1MnsQ_rOwOdS88EBAdg','ACTIVO'),(7,1,'2025-08-09 12:45:41','1990-01-01 00:00:00',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMdWlzIERhdmlkIiwibml2ZWwiOiJhZG1pbmlzdHJhZG9ybml2ZWwxIiwiaWRzZWNpb24iOiI2YzhmNDYyYy1jMzBjLTQzNjgtYTE4YS0yMmQzOWIyZDY4YWIiLCJpYXQiOjE3NTQ3NjE1NDEsImV4cCI6MTc1NDg0Nzk0MX0._pz6K_-nbCJL27AKcRfI1PSMNuY1eE7ciO0Vw8C2Pb4','ACTIVO'),(8,1,'2025-08-11 09:28:25','1990-01-01 00:00:00',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMdWlzIERhdmlkIiwibml2ZWwiOiJhZG1pbmlzdHJhZG9ybml2ZWwxIiwiaWRzZWNpb24iOiI3ODc2YjExZC02OTI1LTQ4NzUtOWU4OS1hNzdlMzI5MWUwMDYiLCJpYXQiOjE3NTQ5MjI1MDUsImV4cCI6MTc1NTAwODkwNX0.SFEr_mMp4DTJuxQIH7Nv_YsFCAOIsSfvu6te8eo9gRc','ACTIVO'),(9,1,'2025-08-11 16:55:16','1990-01-01 00:00:00',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMdWlzIERhdmlkIiwibml2ZWwiOiJhZG1pbmlzdHJhZG9ybml2ZWwxIiwiaWRzZWNpb24iOiIxNzk3YmUzMC1mMmRiLTQ4YzktOWIzNS0wYmYxOWNmZTc1NjQiLCJpYXQiOjE3NTQ5NDkzMTYsImV4cCI6MTc1NTAzNTcxNn0.eQRW_fQP07H1S5ZuAUxmifOxOsCi6lAcBqWSAzzjWgg','ACTIVO'),(10,1,'2025-08-16 10:21:42','1990-01-01 00:00:00',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMdWlzIERhdmlkIiwibml2ZWwiOiJhZG1pbmlzdHJhZG9ybml2ZWwxIiwiaWRzZWNpb24iOiI4ODI2Yzk1Ny0wY2JhLTRmNzQtODI1Yi1jNjU5YjQ4Yzc2OWQiLCJpYXQiOjE3NTUzNTc3MDEsImV4cCI6MTc1NTQ0NDEwMX0.PPTh9rcUC2aHujagYwNlxHPE7n7O7JYgpMws7tNRvFY','ACTIVO'),(11,1,'2025-08-19 09:16:44','1990-01-01 00:00:00',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJMdWlzIERhdmlkIiwibml2ZWwiOiJhZG1pbmlzdHJhZG9ybml2ZWwxIiwiaWRzZWNpb24iOiI1Mjc5ODE4ZC03OTU5LTQyMjktODRmZS1jMzEyZDE4OWRmYzEiLCJpYXQiOjE3NTU2MTMwMDMsImV4cCI6MTc1NTY5OTQwM30.9hmTW2rHTnUTWH9-MTDH9jv6g8ZpsF_oGz5ufJDJAeE','ACTIVO');
/*!40000 ALTER TABLE `sesiones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipoidentificacion`
--

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

--
-- Dumping data for table `tipoidentificacion`
--

LOCK TABLES `tipoidentificacion` WRITE;
/*!40000 ALTER TABLE `tipoidentificacion` DISABLE KEYS */;
INSERT INTO `tipoidentificacion` VALUES (1,11,'Registro Civil Nacimiento'),(2,12,'Tarjeta de Identidad'),(3,13,'Cedula de Ciudadania'),(4,21,'Tarjeta de Extranjeria'),(5,22,'Cedula de Extranjeria'),(6,31,'Nit'),(7,41,'Pasaporte'),(8,42,'Tipo de documento Extrajero'),(9,43,'Sin Identificacion del exterior o para uso definido por la DIAN');
/*!40000 ALTER TABLE `tipoidentificacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipopersona`
--

DROP TABLE IF EXISTS `tipopersona`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipopersona` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipopersona`
--

LOCK TABLES `tipopersona` WRITE;
/*!40000 ALTER TABLE `tipopersona` DISABLE KEYS */;
INSERT INTO `tipopersona` VALUES (1,'Juridica'),(2,'Natural');
/*!40000 ALTER TABLE `tipopersona` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `contrasena` varchar(50) NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `codigorol` int DEFAULT '0',
  `codigousuariocreado` int DEFAULT '0',
  `fechacreado` date NOT NULL DEFAULT (curdate()),
  `fechamodificado` date NOT NULL DEFAULT '1990-06-06',
  PRIMARY KEY (`codigo`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Cavsystems','cav','cav2025','ACTIVO',2,0,current_time(),'1990-06-06');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios_clientes`
--

DROP TABLE IF EXISTS `usuarios_clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios_clientes` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `identificacion` varchar(10) NOT NULL,
  `nombres` varchar(100) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
  `codigousuariocreado` int DEFAULT '0',
  `fechacreado` date NOT NULL DEFAULT (curdate()),
  `fechamodificado` date NOT NULL DEFAULT '1990-06-06',
  PRIMARY KEY (`codigo`),
  UNIQUE KEY `identificacion` (`identificacion`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios_clientes`
--

LOCK TABLES `usuarios_clientes` WRITE;
/*!40000 ALTER TABLE `usuarios_clientes` DISABLE KEYS */;
INSERT INTO `usuarios_clientes` VALUES (1,'1005860599','Luis David Castillo Delgado','ACTIVO',0,'2025-07-24','1990-06-06'),(2,'1005860597','juan david','ACTIVO',0,'2025-07-24','1990-06-06'),(3,'3333','camilo','ACTIVO',2,'2025-07-25','1990-06-06'),(4,'4444','juan','ACTIVO',2,'2025-07-25','1990-06-06');
/*!40000 ALTER TABLE `usuarios_clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios_clientes_usuarios`
--

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

--
-- Dumping data for table `usuarios_clientes_usuarios`
--

LOCK TABLES `usuarios_clientes_usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios_clientes_usuarios` DISABLE KEYS */;
INSERT INTO `usuarios_clientes_usuarios` VALUES (1,1,1),(2,2,2),(3,1,1),(4,2,2);
/*!40000 ALTER TABLE `usuarios_clientes_usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-19 12:28:38
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

--
-- Dumping data for table `impuestos`
--


/*!40000 ALTER TABLE `impuestos` DISABLE KEYS */;
INSERT INTO `nuevosimpuestos` VALUES (1,'IMPUESTO SOBRE LAS VENTAS',5,0,'IVA','ACTIVO'),(2,'IMPUESTO SOBRE LAS VENTAS',19,0,'IVA','ACTIVO'),(3,'IMPUESTO NACIONAL AL CONSUMIDOR',8,0,'INC','ACTIVO');
/*!40000 ALTER TABLE `impuestos` ENABLE KEYS */;
 
 
 DROP TABLE IF EXISTS empresa;
 
 create table empresa(
 codigo int auto_increment,
 codigotipopersona int,
 codigotipoidentificacion int,
 numeroidentificacion varchar(10),
 digitoverificacion varchar(10),
 primernombre varchar(50),
 segundonombre varchar(50),
 primerapellido varchar(50),
 segundoapellido varchar(50),
 razonsocial varchar(100),
 codigopostal varchar(100),
 nombrecomercial varchar(50),
 codigoactividadeconomica int,
 codigoregimen int,
 correoempresa varchar(50),
 celularempresa varchar(12),
 telfonofijo varchar(20),
 codigopais int,
 codigodepartamento int,
 codigomunicipio int,
 imagenempresa longblob default null,
 tipoImagen varchar(100) default "",
 primary key(codigo)); 
 
 SET FOREIGN_KEY_CHECKS = 1;