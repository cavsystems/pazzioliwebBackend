-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: cavsystems
-- ------------------------------------------------------
-- Server version	8.0.42

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




INSERT INTO `actividadeconomica` VALUES (111,'Cultivo de cereales (excepto arroz), legumbres y semillas oleaginosas'),(112,'Cultivo de arroz'),(113,'Cultivo de hortalizas, raÃ­ces y tubÃ©rculos'),(114,'Cultivo de tabaco'),(115,'Cultivo de plantas textiles'),(119,'Otros cultivos transitorios n.c.p.'),(121,'Cultivo de frutas tropicales y subtropicales'),(122,'Cultivo de plÃ¡tano y banano'),(123,'Cultivo de cafÃ©'),(124,'Cultivo de caÃ±a de azÃºcar'),(125,'Cultivo de flor de corte'),(126,'Cultivo de palma para aceite (palma africana) y otros frutos oleaginosos'),(127,'Cultivo de plantas con las que se prepararan bebidas'),(128,'Cultivo de especias y de plantas aromÃ¡ticas y medicinales'),(129,'Otros cultivos permanentes n.c.p.'),(130,'PropagaciÃ³n de plantas (actividades de los viveros, excepto viveros\nforestales)'),(141,'CrÃ­a de ganado bovino y bufalino'),(142,'CrÃ­a de caballos y otros equinos'),(143,'CrÃ­a de ovejas y cabras'),(144,'CrÃ­a de ganado porcino'),(145,'CrÃ­a de aves de corral'),(149,'CrÃ­a de otros animales n.c.p.'),(150,'ExplotaciÃ³n primaria mixta (agrÃ­cola y pecuaria)'),(161,'Actividades de apoyo a la agricultura'),(162,'Actividades de apoyo a la ganaderÃ­a'),(163,'Actividades posteriores a la cosecha'),(164,'Tratamiento de semillas para propagaciÃ³n'),(170,'Caza ordinaria y mediante trampas y actividades de servicios conexas'),(210,'Silvicultura y otras actividades forestales'),(220,'ExtracciÃ³n de madera'),(230,'RecolecciÃ³n de productos forestales diferentes a la madera'),(240,'Servicios de apoyo a la silvicultura'),(311,'Pesca marÃ­tima'),(312,'Pesca de agua dulce'),(321,'Acuicultura marÃ­tima'),(322,'Acuicultura de agua dulce'),(510,'ExtracciÃ³n de hulla (carbÃ³n de piedra)'),(520,'ExtracciÃ³n de carbÃ³n lignito'),(610,'ExtracciÃ³n de petrÃ³leo crudo'),(620,'ExtracciÃ³n de gas natural'),(710,'ExtracciÃ³n de minerales de hierro'),(721,'ExtracciÃ³n de minerales de uranio y de torio'),(722,'ExtracciÃ³n de oro y otros metales preciosos'),(723,'ExtracciÃ³n de minerales de nÃ­quel'),(729,'ExtracciÃ³n de otros minerales metalÃ­feros no ferrosos n.c.p.'),(811,'ExtracciÃ³n de piedra, arena,\narcillas comunes, yeso y\nanhidrita'),(812,'ExtracciÃ³n de arcillas de uso industrial, caliza, caolÃ­n y bentonitas'),(820,'ExtracciÃ³n de esmeraldas, piedras preciosas y semipreciosas'),(891,'ExtracciÃ³n de minerales para la fabricaciÃ³n de abonos y productos quÃ­micos'),(892,'ExtracciÃ³n de halita (sal)'),(899,'ExtracciÃ³n de otros minerales no metÃ¡licos n.c.p.'),(910,'Actividades de apoyo para la extracciÃ³n de petrÃ³leo y de gas natural'),(990,'Actividades de apoyo para otras actividades de explotaciÃ³n de minas y canteras'),(1011,'Procesamiento y conservaciÃ³n de carne y productos cÃ¡rnicos'),(1012,'Procesamiento y conservaciÃ³n de pescados, crustÃ¡ceos y moluscos'),(1030,'ElaboraciÃ³n de aceites y grasas de origen vegetal y animal'),(1051,'ElaboraciÃ³n de productos de molinerÃ­a'),(1052,'ElaboraciÃ³n de almidones y productos derivados del almidÃ³n'),(1061,'Trilla de cafÃ©'),(1062,'Descafeinado, tostiÃ³n y molienda del cafÃ©'),(1063,'ElaboraciÃ³n de otros derivados del cafÃ©'),(1071,'ElaboraciÃ³n y refinaciÃ³n de azÃºcar'),(1072,'ElaboraciÃ³n de panela'),(1081,'ElaboraciÃ³n de productos de panaderÃ­a'),(1082,'ElaboraciÃ³n de cacao, chocolate y productos de confiterÃ­a'),(1083,'ElaboraciÃ³n de macarrones, fideos, alcuzcuz y productos farinÃ¡ceos similares'),(1084,'ElaboraciÃ³n de comidas y platos preparados'),(1089,'ElaboraciÃ³n de otros productos alimenticios n.c.p.'),(1090,'ElaboraciÃ³n de alimentos preparados para animales'),(1101,'DestilaciÃ³n, rectificaciÃ³n y mezcla de bebidas alcohÃ³licas'),(1102,'ElaboraciÃ³n de bebidas fermentadas no destiladas'),(1103,'ProducciÃ³n de malta, elaboraciÃ³n de cervezas y otras bebidas malteadas'),(1104,'ElaboraciÃ³n de bebidas no alcohÃ³licas, producciÃ³n de aguas minerales y de otras aguas embotelladas'),(1200,'ElaboraciÃ³n de productos de tabaco'),(1311,'PreparaciÃ³n e hilatura de fibras textiles'),(1312,'TejedurÃ­a de productos textiles'),(1313,'Acabado de productos textiles'),(1391,'FabricaciÃ³n de tejidos de punto y ganchillo'),(1392,'ConfecciÃ³n de artÃ­culos con materiales textiles, excepto prendas de vestir'),(1393,'FabricaciÃ³n de tapetes y alfombras para pisos'),(1394,'FabricaciÃ³n de cuerdas, cordeles, cables, bramantes y redes'),(1399,'FabricaciÃ³n de otros artÃ­culos textiles n.c.p.'),(1410,'ConfecciÃ³n de prendas de vestir, excepto prendas de piel'),(1511,'Curtido y recurtido de cueros; recurtido y teÃ±ido de pieles.'),(1512,'FabricaciÃ³n de artÃ­culos de viaje, bolsos de mano y artÃ­culos similares elaborados en cuero, y fabricaciÃ³n de artÃ­culos de talabarterÃ­a y guarnicionerÃ­a.'),(1513,'FabricaciÃ³n de artÃ­culos de viaje, bolsos de mano y artÃ­culos similares; artÃ­culos de talabarterÃ­a y guarnicionerÃ­a elaborados en otros materiales'),(1521,'FabricaciÃ³n de calzado de cuero y piel, con cualquier tipo de suela'),(1522,'FabricaciÃ³n de otros tipos de calzado, excepto calzado de cuero y piel'),(1523,'FabricaciÃ³n de partes del calzado'),(1610,'Aserrado, acepillado e impregnaciÃ³n de la madera'),(1620,'FabricaciÃ³n de hojas de madera para enchapado; fabricaciÃ³n de tableros contrachapados, tableros laminados, tableros de partÃ­culas y otros tableros y paneles'),(1630,'FabricaciÃ³n de partes y piezas de madera, de carpinterÃ­a y ebanisterÃ­a para la construcciÃ³n y para edificios'),(1640,'FabricaciÃ³n de recipientes de madera'),(1690,'FabricaciÃ³n de otros productos de madera; fabricaciÃ³n de artÃ­culos de corcho, cesterÃ­a y esparterÃ­a'),(1701,'FabricaciÃ³n de pulpas (pastas) celulÃ³sicas; papel y cartÃ³n'),(1702,'FabricaciÃ³n de papel y cartÃ³n ondulado (corrugado); fabricaciÃ³n de envases, empaques y de embalajes de papel y cartÃ³n.'),(1709,'FabricaciÃ³n de otros artÃ­culos de papel y cartÃ³n'),(1811,'Actividades de impresiÃ³n'),(1812,'Actividades de servicios relacionados con la impresiÃ³n'),(1820,'ProducciÃ³n de copias a partir de grabaciones originales'),(1910,'FabricaciÃ³n de productos de hornos de coque'),(1921,'FabricaciÃ³n de productos de la refinaciÃ³n del petrÃ³leo '),(1922,'Actividad de mezcla de combustibles'),(2011,'FabricaciÃ³n de sustancias y productos quÃ­micos bÃ¡sicos'),(2012,'FabricaciÃ³n de abonos y compuestos inorgÃ¡nicos nitrogenados'),(2013,'FabricaciÃ³n de plÃ¡sticos en formas primarias'),(2014,'FabricaciÃ³n de caucho sintÃ©tico en formas primarias'),(2021,'FabricaciÃ³n de plaguicidas y otros productos quÃ­micos de uso agropecuario'),(2022,'FabricaciÃ³n de pinturas, barnices y revestimientos similares, tintas para impresiÃ³n y masillas'),(2023,'FabricaciÃ³n de jabones y detergentes, preparados para limpiar y pulir; perfumes y preparados de tocador'),(2029,'FabricaciÃ³n de otros productos quÃ­micos n.c.p.'),(2030,'FabricaciÃ³n de fibras sintÃ©ticas y artificiales'),(2100,'FabricaciÃ³n de productos farmacÃ©uticos, sustancias quÃ­micas medicinales y productos botÃ¡nicos de uso farmacÃ©utico'),(2211,'FabricaciÃ³n de llantas y neumÃ¡ticos de caucho'),(2212,'Reencauche de llantas usadas'),(2219,'FabricaciÃ³n de formas bÃ¡sicas de caucho y otros productos de caucho n.c.p.'),(2221,'FabricaciÃ³n de formas bÃ¡sicas de plÃ¡stico'),(2229,'FabricaciÃ³n de artÃ­culos de plÃ¡stico n.c.p.'),(2310,'FabricaciÃ³n de vidrio y productos de vidrio'),(2391,'FabricaciÃ³n de productos refractarios'),(2392,'FabricaciÃ³n de materiales de arcilla para la construcciÃ³n'),(2393,'FabricaciÃ³n de otros productos de cerÃ¡mica y porcelana'),(2394,'FabricaciÃ³n de cemento, cal  y yeso'),(2395,'FabricaciÃ³n de artÃ­culos de hormigÃ³n, cemento y yeso'),(2396,'Corte, tallado y acabado de la piedra'),(2399,'FabricaciÃ³n de otros productos minerales no metÃ¡licos n.c.p.'),(2410,'Industrias bÃ¡sicas de hierro y de acero'),(2421,'Industrias bÃ¡sicas de metales preciosos'),(2429,'Industrias bÃ¡sicas de otros metales no ferrosos'),(2431,'FundiciÃ³n de hierro y de acero'),(2432,'FundiciÃ³n de metales no ferrosos'),(2511,'FabricaciÃ³n de productos metÃ¡licos para uso estructural'),(2512,'FabricaciÃ³n de tanques, depÃ³sitos y recipientes de metal, excepto los utilizados para el envase o transporte de mercancÃ­as'),(2513,'FabricaciÃ³n de generadores de vapor, excepto calderas de agua caliente para calefacciÃ³n central'),(2520,'FabricaciÃ³n de armas y municiones'),(2591,'Forja, prensado, estampado y laminado de metal; pulvimetalurgia'),(2592,'Tratamiento y revestimiento\nde metales; mecanizado'),(2593,'FabricaciÃ³n de artÃ­culos de cuchillerÃ­a, herramientas de mano y artÃ­culos de ferreterÃ­a'),(2599,'FabricaciÃ³n de otros productos elaborados de metal n.c.p.'),(2610,'FabricaciÃ³n de componentes y tableros electrÃ³nicos'),(2620,'FabricaciÃ³n de computadoras y de equipo perifÃ©rico'),(2630,'FabricaciÃ³n de equipos de comunicaciÃ³n'),(2640,'FabricaciÃ³n de aparatos electrÃ³nicos de consumo'),(2651,'FabricaciÃ³n de equipo de mediciÃ³n, prueba, navegaciÃ³n y control '),(2652,'FabricaciÃ³n de relojes'),(2660,'FabricaciÃ³n de equipo de irradiaciÃ³n y equipo electrÃ³nico de uso mÃ©dico y terapÃ©utico'),(2670,'FabricaciÃ³n de instrumentos Ã³pticos y equipo fotogrÃ¡fico '),(2680,'FabricaciÃ³n de soportes magnÃ©ticos y Ã³pticos'),(2711,'FabricaciÃ³n de motores, generadores y transformadores elÃ©ctricos.'),(2712,'FabricaciÃ³n de aparatos de distribuciÃ³n y control de la energÃ­a elÃ©ctrica'),(2720,'FabricaciÃ³n de pilas, baterÃ­as y acumuladores elÃ©ctricos'),(2731,'FabricaciÃ³n de hilos y cables elÃ©ctricos y de fibra Ã³ptica'),(2732,'FabricaciÃ³n de dispositivos de cableado'),(2740,'FabricaciÃ³n de equipos elÃ©ctricos de iluminaciÃ³n'),(2750,'FabricaciÃ³n de aparatos de uso domÃ©stico'),(2790,'FabricaciÃ³n de otros tipos de equipo elÃ©ctrico n.c.p.'),(2811,'FabricaciÃ³n de motores, turbinas, y partes para motores de combustiÃ³n interna'),(2812,'FabricaciÃ³n de equipos de potencia hidrÃ¡ulica y neumÃ¡tica'),(2813,'FabricaciÃ³n de otras bombas, compresores, grifos y vÃ¡lvulas'),(2814,'FabricaciÃ³n de cojinetes, engranajes, trenes de engranajes y piezas de transmisiÃ³n'),(2815,'FabricaciÃ³n de hornos, hogares y quemadores industriales'),(2816,'FabricaciÃ³n de equipo de elevaciÃ³n y manipulaciÃ³n'),(2817,'FabricaciÃ³n de maquinaria y equipo de oficina (excepto computadoras y equipo perifÃ©rico)'),(2818,'FabricaciÃ³n de herramientas manuales con motor'),(2819,'FabricaciÃ³n de otros tipos de maquinaria y equipo de uso general n.c.p.'),(2821,'FabricaciÃ³n de maquinaria agropecuaria y forestal'),(2822,'FabricaciÃ³n de mÃ¡quinas formadoras de metal y de mÃ¡quinas herramienta'),(2823,'FabricaciÃ³n de maquinaria para la metalurgia'),(2824,'FabricaciÃ³n de maquinaria para explotaciÃ³n de minas y canteras y para obras de construcciÃ³n'),(2825,'FabricaciÃ³n de maquinaria para la elaboraciÃ³n de alimentos, bebidas y tabaco'),(2826,'FabricaciÃ³n de maquinaria para la elaboraciÃ³n de productos textiles, prendas de vestir y cueros'),(2829,'FabricaciÃ³n de otros tipos de maquinaria y equipo de uso especial n.c.p.'),(2910,'FabricaciÃ³n de vehÃ­culos automotores y sus motores'),(2920,'FabricaciÃ³n de carrocerÃ­as para vehÃ­culos automotores; fabricaciÃ³n de remolques y semirremolques'),(2930,'FabricaciÃ³n de partes, piezas (autopartes) y accesorios (lujos) para vehÃ­culos automotores'),(3011,'ConstrucciÃ³n de barcos y de estructuras flotantes'),(3012,'ConstrucciÃ³n de embarcaciones de recreo y deporte'),(3020,'FabricaciÃ³n de locomotoras y de material rodante para ferrocarriles '),(3030,'FabricaciÃ³n de aeronaves, naves espaciales y de maquinaria conexa'),(3040,'FabricaciÃ³n de vehÃ­culos militares de combate'),(3091,'FabricaciÃ³n de motocicletas'),(3092,'FabricaciÃ³n de bicicletas y de sillas de ruedas para personas con discapacidad'),(3099,'FabricaciÃ³n de otros tipos de equipo de transporte n.c.p.'),(3110,'FabricaciÃ³n de muebles'),(3120,'FabricaciÃ³n de colchones y somieres'),(3210,'FabricaciÃ³n de joyas, bisuterÃ­a y artÃ­culos conexos'),(3220,'FabricaciÃ³n de instrumentos musicales'),(3230,'FabricaciÃ³n de artÃ­culos y\nequipo para la prÃ¡ctica del\ndeporte (excepto prendas\nde vestir y calzado)'),(3240,'FabricaciÃ³n de juegos,\njuguetes y rompecabezas'),(3250,'FabricaciÃ³n de\ninstrumentos, aparatos y\nmateriales mÃ©dicos y\nodontolÃ³gicos (incluido\nmobiliario)'),(3290,'Otras industrias manufactureras n.c.p.'),(3311,'Mantenimiento y reparaciÃ³n especializado de productos elaborados en metal'),(3312,'Mantenimiento y reparaciÃ³n especializado de maquinaria y equipo'),(3313,'Mantenimiento y reparaciÃ³n especializado de equipo electrÃ³nico y Ã³ptico'),(3314,'Mantenimiento y reparaciÃ³n especializado de equipo elÃ©ctrico'),(3315,'Mantenimiento y reparaciÃ³n especializado de equipo de transporte, excepto los vehÃ­culos automotores, motocicletas y bicicletas'),(3319,'Mantenimiento y reparaciÃ³n de otros tipos de equipos y sus componentes n.c.p.'),(3320,'InstalaciÃ³n especializada de maquinaria y equipo industrial'),(3511,'GeneraciÃ³n de energÃ­a elÃ©ctrica'),(3512,'TransmisiÃ³n de energÃ­a elÃ©ctrica'),(3513,'DistribuciÃ³n de energÃ­a elÃ©ctrica'),(3514,'ComercializaciÃ³n de energÃ­a elÃ©ctrica'),(3530,'Suministro de vapor y aire acondicionado'),(3700,'EvacuaciÃ³n y tratamiento de aguas residuales '),(3811,'RecolecciÃ³n de desechos no peligrosos'),(3812,'RecolecciÃ³n de desechos peligrosos'),(3821,'Tratamiento y disposiciÃ³n de desechos no peligrosos'),(3822,'Tratamiento y disposiciÃ³n de desechos peligrosos'),(3830,'RecuperaciÃ³n de materiales'),(4111,'ConstrucciÃ³n de edificios residenciales'),(4112,'ConstrucciÃ³n de edificios no residenciales'),(4210,'ConstrucciÃ³n de carreteras y vÃ­as de ferrocarril'),(4220,'ConstrucciÃ³n de proyectos de servicio pÃºblico'),(4290,'ConstrucciÃ³n de otras obras de ingenierÃ­a civil'),(4311,'DemoliciÃ³n'),(4312,'PreparaciÃ³n del terreno'),(4321,'Instalaciones elÃ©ctricas de la construcciÃ³n'),(4322,'Instalaciones de fontanerÃ­a, calefacciÃ³n y aire acondicionado de la construcciÃ³n'),(4329,'Otras instalaciones especializadas de la construcciÃ³n'),(4330,'TerminaciÃ³n y acabado de edificios y obras de ingenierÃ­a civil'),(4390,'Otras actividades especializadas para la construcciÃ³n de edificios y obras de ingenierÃ­a civil'),(4511,'Comercio de vehÃ­culos automotores nuevos'),(4512,'Comercio de vehÃ­culos automotores usados'),(4520,'Mantenimiento y reparaciÃ³n de vehÃ­culos automotores.'),(4530,'Comercio de partes, piezas (autopartes) y accesorios (lujos) para vehÃ­culos automotores'),(4542,'Mantenimiento y reparaciÃ³n de motocicletas y de sus partes y piezas'),(4610,'Comercio al por mayor a cambio de una retribuciÃ³n o por contrata'),(4631,'Comercio al por mayor de productos alimenticios'),(4641,'Comercio al por mayor de productos textiles y productos confeccionados para uso domÃ©stico '),(4642,'Comercio al por mayor de prendas de vestir'),(4643,'Comercio al por mayor de calzado'),(4644,'Comercio al por mayor de aparatos y equipo de uso domÃ©stico'),(4651,'Comercio al por mayor de computadores, equipo perifÃ©rico y programas de informÃ¡tica'),(4652,'Comercio al por mayor de equipo, partes y piezas electrÃ³nicos y de telecomunicaciones'),(4653,'Comercio al por mayor de maquinaria y equipo agropecuarios'),(4659,'Comercio al por mayor de otros tipos de maquinaria y equipo n.c.p.'),(4662,'Comercio al por mayor de metales y productos metalÃ­feros'),(4664,'Comercio al por mayor de productos quÃ­micos bÃ¡sicos, cauchos y plÃ¡sticos en formas primarias y productos quÃ­micos de uso agropecuario'),(4665,'Comercio al por mayor de desperdicios, desechos y chatarra'),(4669,'Comercio al por mayor de otros productos n.c.p.'),(4690,'Comercio al por mayor no especializado'),(4721,'Comercio al por menor de productos agrÃ­colas para el consumo en establecimientos especializados'),(4722,'Comercio al por menor de leche, productos lÃ¡cteos y huevos, en establecimientos especializados'),(4723,'Comercio al por menor de carnes (incluye aves de corral), productos cÃ¡rnicos, pescados y productos de mar, en establecimientos especializados'),(4729,'Comercio al por menor de otros productos alimenticios n.c.p., en establecimientos especializados'),(4731,'Comercio al por menor de combustible para automotores'),(4732,'Comercio al por menor de lubricantes (aceites, grasas), aditivos y productos de limpieza para vehÃ­culos automotores'),(4741,'Comercio al por menor de computadores, equipos perifÃ©ricos, programas de informÃ¡tica y equipos de telecomunicaciones en establecimientos especializados'),(4742,'Comercio al por menor de equipos y aparatos de sonido y de video, en establecimientos especializados'),(4751,'Comercio al por menor de productos textiles en establecimientos especializados'),(4753,'Comercio al por menor de tapices, alfombras y recubrimientos para paredes y pisos en establecimientos especializados'),(4754,'Comercio al por menor de electrodomÃ©sticos y gasodomÃ©sticos de uso domÃ©stico, muebles y equipos de iluminaciÃ³n en establecimientos especializados'),(4755,'Comercio al por menor de artÃ­culos y utensilios de uso domÃ©stico en establecimientos especializados'),(4759,'Comercio al por menor de otros artÃ­culos domÃ©sticos en establecimientos especializados'),(4762,'Comercio al por menor de artÃ­culos deportivos, en establecimientos especializados'),(4769,'Comercio al por menor de otros artÃ­culos culturales y de entretenimiento n.c.p. en establecimientos especializados'),(4771,'Comercio al por menor de prendas de vestir y sus accesorios (incluye artÃ­culos de piel) en establecimientos especializados'),(4772,'Comercio al por menor de todo tipo de calzado y artÃ­culos de cuero y sucedÃ¡neos del cuero en establecimientos especializados.'),(4774,'Comercio al por menor de otros productos nuevos en establecimientos especializados'),(4775,'Comercio al por menor de artÃ­culos de segunda mano'),(4782,'Comercio al por menor de productos textiles, prendas de vestir y calzado, en puestos de venta mÃ³viles'),(4789,'Comercio al por menor de otros productos en puestos de venta mÃ³viles'),(4911,'Transporte fÃ©rreo de pasajeros'),(4912,'Transporte fÃ©rreo de carga'),(4921,'Transporte de pasajeros'),(4922,'Transporte mixto'),(4923,'Transporte de carga por carretera'),(4930,'Transporte por tuberÃ­as'),(5011,'Transporte de pasajeros marÃ­timo y de cabotaje'),(5012,'Transporte de carga\nmarÃ­timo y de cabotaje'),(5021,'Transporte fluvial de pasajeros'),(5022,'Transporte fluvial de carga'),(5111,'Transporte aÃ©reo nacional de pasajeros'),(5112,'Transporte aÃ©reo internacional de pasajeros'),(5121,'Transporte aÃ©reo nacional de carga'),(5122,'Transporte aÃ©reo internacional de carga'),(5210,'Almacenamiento y depÃ³sito'),(5221,'Actividades de estaciones, vÃ­as y servicios complementarios para el transporte terrestre'),(5222,'Actividades de puertos y servicios complementarios para el transporte acuÃ¡tico'),(5223,'Actividades de aeropuertos, servicios de navegaciÃ³n aÃ©rea y demÃ¡s actividades conexas al transporte aÃ©reo'),(5224,'ManipulaciÃ³n de carga'),(5229,'Otras actividades complementarias al transporte'),(5310,'Actividades postales nacionales'),(5320,'Actividades de mensajerÃ­a'),(5330,'Servicio de pedido, compra, distribuciÃ³n y entrega de productos a travÃ©s de plataformas o aplicaciones de contacto y que utilizan una red de domiciliarios'),(5511,'Alojamiento en hoteles'),(5512,'Alojamiento en aparta-hoteles'),(5513,'Alojamiento en centros vacacionales'),(5514,'Alojamiento rural'),(5519,'Otros tipos de alojamientos para visitantes'),(5520,'Actividades de zonas de camping y parques para vehÃ­culos recreacionales'),(5530,'Servicio por horas  de alojamiento'),(5590,'Otros tipos de alojamiento n.c.p.'),(5611,'Expendio a la mesa de comidas preparadas'),(5612,'Expendio por autoservicio de comidas preparadas'),(5613,'Expendio de comidas preparadas en cafeterÃ­as'),(5619,'Otros tipos de expendio de comidas preparadas n.c.p.'),(5621,'Catering para eventos'),(5629,'Actividades de otros servicios de comidas'),(5630,'Expendio de bebidas alcohÃ³licas para el consumo dentro del establecimiento'),(5812,'EdiciÃ³n de directorios y listas de correo'),(5813,'EdiciÃ³n de periÃ³dicos, revistas y otras publicaciones periÃ³dicas'),(5819,'Otros trabajos de ediciÃ³n'),(5820,'EdiciÃ³n de programas de informÃ¡tica (software)'),(5911,'Actividades de producciÃ³n de pelÃ­culas cinematogrÃ¡ficas, videos, programas, anuncios y comerciales de televisiÃ³n (excepto programaciÃ³n de televisiÃ³n)'),(5912,'Actividades de postproducciÃ³n de pelÃ­culas cinematogrÃ¡ficas, videos, programas, anuncios y comerciales de televisiÃ³n (excepto programaciÃ³n de televisiÃ³n)'),(5913,'Actividades de distribuciÃ³n de pelÃ­culas cinematogrÃ¡ficas, videos, programas, anuncios y comerciales de televisiÃ³n'),(5914,'Actividades de exhibiciÃ³n de pelÃ­culas cinematogrÃ¡ficas y videos'),(5920,'Actividades de grabaciÃ³n de sonido y ediciÃ³n de mÃºsica'),(6010,'Actividades de programaciÃ³n y transmisiÃ³n en el servicio de radiodifusiÃ³n sonora'),(6110,'Actividades de telecomunicaciones alÃ¡mbricas'),(6120,'Actividades de telecomunicaciones inalÃ¡mbricas'),(6130,'Actividades de telecomunicaciÃ³n satelital'),(6190,'Otras actividades de telecomunicaciones'),(6201,'Actividades de desarrollo de sistemas informÃ¡ticos (planificaciÃ³n, anÃ¡lisis, diseÃ±o, programaciÃ³n, pruebas)'),(6202,'Actividades de consultorÃ­a informÃ¡tica y actividades de administraciÃ³n de instalaciones informÃ¡ticas'),(6209,'Otras actividades de tecnologÃ­as de informaciÃ³n y actividades de servicios informÃ¡ticos'),(6311,'Procesamiento de datos, alojamiento (hosting) y actividades relacionadas'),(6312,'Portales Web'),(6391,'Actividades de agencias de noticias'),(6399,'Otras actividades de servicio de informaciÃ³n n.c.p.'),(6411,'Banca Central'),(6412,'Bancos comerciales'),(6421,'Actividades de las corporaciones financieras'),(6422,'Actividades de las compaÃ±Ã­as de financiamiento'),(6423,'Banca de segundo piso'),(6424,'Actividades de las cooperativas financieras'),(6431,'Fideicomisos, fondos y entidades financieras similares'),(6491,'Leasing financiero (arrendamiento financiero)'),(6492,'Actividades financieras de fondos de empleados y otras formas asociativas del sector solidario'),(6493,'Actividades de compra de cartera o factoring'),(6494,'Otras actividades de distribuciÃ³n de fondos'),(6495,'Instituciones especiales oficiales'),(6496,'CapitalizaciÃ³n'),(6511,'Seguros generales'),(6512,'Seguros de vida'),(6513,'Reaseguros'),(6515,'Seguros de salud'),(6521,'Servicios de seguros sociales de salud'),(6522,'Servicios de seguros sociales en riesgos laborales'),(6523,'Servicios de seguros sociales en riesgos familia'),(6531,'RÃ©gimen de prima media con prestaciÃ³n definida (RPM)'),(6532,'RÃ©gimen de ahorro con solidaridad (RAIS)'),(6612,'Corretaje de valores y de contratos de productos bÃ¡sicos'),(6613,'Otras actividades relacionadas con el mercado de valores'),(6614,'Actividades de las sociedades de intermediaciÃ³n cambiaria y de servicios financieros especiales'),(6615,'Actividades de los profesionales de compra y venta de divisas'),(6619,'Otras actividades auxiliares de las actividades de servicios financieros n.c.p.'),(6621,'Actividades de agentes y corredores de seguros'),(6629,'EvaluaciÃ³n de riesgos y daÃ±os, y otras actividades de servicios auxiliares'),(6630,'Actividades de administraciÃ³n de fondos'),(6810,'Actividades inmobiliarias realizadas con bienes propios o arrendados'),(6820,'Actividades inmobiliarias realizadas a cambio de una retribuciÃ³n o por contrata'),(7310,'Publicidad'),(7420,'Actividades de fotografÃ­a'),(7500,'Actividades veterinarias'),(7710,'Alquiler y arrendamiento de vehÃ­culos automotores'),(7721,'Alquiler y arrendamiento de equipo recreativo y deportivo'),(7722,'Alquiler de videos y discos'),(7729,'Alquiler y arrendamiento de otros efectos personales y enseres domÃ©sticos n.c.p.'),(7730,'Alquiler y arrendamiento de otros tipos de maquinaria, equipo y bienes tangibles n.c.p.'),(7740,'Arrendamiento de propiedad intelectual y productos similares, excepto obras protegidas por derechos de autor'),(7810,'Actividades de agencias de gestiÃ³n y colocaciÃ³n de empleo'),(7820,'Actividades de empresas de servicios temporales'),(7830,'Otras actividades de provisiÃ³n de talento humano'),(7911,'Actividades de las agencias de viaje'),(7912,'Actividades de operadores turÃ­sticos'),(7990,'Otros servicios de reserva y actividades relacionadas'),(8010,'Actividades de seguridad privada'),(8020,'Actividades de servicios de sistemas de seguridad'),(8030,'Actividades de detectives e investigadores privados'),(8110,'Actividades combinadas de apoyo a instalaciones'),(8121,'Limpieza general interior de edificios'),(8129,'Otras actividades de limpieza de edificios e instalaciones industriales'),(8130,'Actividades de paisajismo y servicios de mantenimiento conexos'),(8211,'Actividades combinadas de servicios administrativos de oficina'),(8219,'Fotocopiado, preparaciÃ³n de documentos y otras actividades especializadas de apoyo a oficina'),(8220,'Actividades de centros de llamadas (Call center)'),(8230,'OrganizaciÃ³n de convenciones y eventos comerciales'),(8291,'Actividades de agencias de cobranza y oficinas de calificaciÃ³n crediticia'),(8292,'Actividades de envase y empaque'),(8299,'Otras actividades de servicio de apoyo a las empresas n.c.p.'),(8411,'Actividades legislativas de la administraciÃ³n pÃºblica'),(8412,'Actividades ejecutivas de la administraciÃ³n pÃºblica'),(8413,'RegulaciÃ³n de las actividades de organismos que prestan servicios de salud, educativos, culturales y otros servicios sociales, excepto servicios de seguridad social'),(8414,'Actividades reguladoras y facilitadoras de la actividad econÃ³mica'),(8415,'Actividades de los Ã³rganos de control y otras instituciones.'),(8421,'Relaciones exteriores'),(8422,'Actividades de defensa'),(8423,'Orden pÃºblico y actividades de seguridad publica'),(8424,'AdministraciÃ³n de justicia'),(8430,'Actividades de planes de Seguridad Social de afiliaciÃ³n obligatoria'),(8511,'EducaciÃ³n de la primera infancia'),(8512,'EducaciÃ³n preescolar'),(8513,'EducaciÃ³n bÃ¡sica primaria'),(8521,'EducaciÃ³n bÃ¡sica secundaria'),(8522,'EducaciÃ³n media acadÃ©mica'),(8523,'EducaciÃ³n media tÃ©cnica'),(8530,'Establecimientos que combinan diferentes niveles de educaciÃ³n inicial, preescolar, bÃ¡sica primaria, bÃ¡sica secundaria y media'),(8541,'EducaciÃ³n tÃ©cnica profesional'),(8542,'EducaciÃ³n tecnolÃ³gica'),(8543,'EducaciÃ³n de instituciones universitarias o de escuelas tecnolÃ³gicas'),(8544,'EducaciÃ³n de universidades'),(8551,'FormaciÃ³n para el trabajo'),(8552,'EnseÃ±anza deportiva y recreativa'),(8553,'EnseÃ±anza cultural'),(8559,'Otros tipos de educaciÃ³n n.c.p.'),(8560,'Actividades de apoyo a la educaciÃ³n'),(8610,'Actividades de hospitales y clÃ­nicas, con internaciÃ³n'),(8720,'Actividades de atenciÃ³n residencial, para el cuidado de pacientes con retardo mental, enfermedad mental y consumo de sustancias psicoactivas'),(8730,'Actividades de atenciÃ³n en instituciones para el cuidado de personas mayores y/o discapacitadas'),(8790,'Otras actividades de atenciÃ³n en instituciones con alojamiento'),(8810,'Actividades de asistencia social sin alojamiento para personas mayores y discapacitadas'),(8891,'Actividades de guarderÃ­as para niÃ±os y niÃ±as'),(8899,'Otras actividades de asistencia social n.c.p'),(9001,'CreaciÃ³n literaria'),(9002,'CreaciÃ³n musical'),(9003,'CreaciÃ³n teatral'),(9004,'CreaciÃ³n audiovisual'),(9005,'Artes plÃ¡sticas y visuales'),(9006,'Actividades teatrales'),(9007,'Actividades de espectÃ¡culos musicales en vivo'),(9008,'Otras actividades de espectÃ¡culos en vivo n.c.p'),(9101,'Actividades de Bibliotecas y archivos'),(9102,'Actividades y funcionamiento de museos, conservaciÃ³n de edificios y sitios histÃ³ricos'),(9103,'Actividades de jardines botÃ¡nicos, zoolÃ³gicos y reservas naturales'),(9311,'GestiÃ³n de instalaciones deportivas'),(9312,'Actividades de clubes deportivos'),(9319,'Otras actividades deportivas'),(9321,'Actividades de parques de atracciones y parques temÃ¡ticos'),(9411,'Actividades de asociaciones empresariales y de empleadores'),(9412,'Actividades de asociaciones profesionales y gremiales sin Ã¡nimo de lucro'),(9420,'Actividades de sindicatos'),(9491,'Actividades de asociaciones religiosas'),(9492,'Actividades de partidos polÃ­ticos'),(9499,'Actividades de otras asociaciones n.c.p.'),(9511,'Mantenimiento y reparaciÃ³n de computadores y de equipo perifÃ©rico'),(9512,'Mantenimiento y reparaciÃ³n de equipos de comunicaciÃ³n'),(9521,'Mantenimiento y reparaciÃ³n de aparatos electrÃ³nicos de consumo'),(9522,'Mantenimiento y reparaciÃ³n de aparatos domÃ©sticos y equipos domÃ©sticos y de jardinerÃ­a'),(9523,'ReparaciÃ³n de calzado y artÃ­culos de cuero'),(9524,'ReparaciÃ³n de muebles y accesorios para el hogar'),(9529,'Mantenimiento y reparaciÃ³n de otros efectos personales y enseres domÃ©sticos'),(9601,'Lavado y limpieza, incluso la limpieza en seco, de productos textiles y de piel'),(9602,'PeluquerÃ­a y otros tratamientos de belleza'),(9603,'Pompas fÃºnebres y actividades relacionadas'),(9609,'Otras actividades de servicios personales n.c.p.'),(9700,'Actividades de los hogares individuales como empleadores de personal domÃ©stico'),(9810,'Actividades no diferenciadas de los hogares individuales como productores de bienes para uso propio'),(9820,'Actividades no diferenciadas de los hogares individuales como productores de servicios para uso propio'),(9900,'Actividades de organizaciones y entidades extraterritoriales signatarios de la ConvenciÃ³n de Viena'),(10201,'Procesamiento y conservaciÃ³n de frutas, legumbres, hortalizas y tubÃ©rculos (excepto elaboraciÃ³n de jugos de frutas)'),(10202,'ElaboraciÃ³n de jugos de frutas'),(10401,'ElaboraciÃ³n de productos lÃ¡cteos (excepto bebidas)'),(10402,'ElaboraciÃ³n de bebidas lÃ¡cteas'),(14201,'FabricaciÃ³n de prendas de vestir de piel'),(14202,'FabricaciÃ³n de artÃ­culos de piel (excepto prendas de vestir)'),(14301,'FabricaciÃ³n de prendas de vestir de punto y ganchillo'),(14302,'FabricaciÃ³n de artÃ­culos de punto y ganchillo (excepto prendas de vestir)'),(35201,'ProducciÃ³n de gas'),(35202,'DistribuciÃ³n de combustibles gaseosos por tuberÃ­as'),(36001,'CaptaciÃ³n y tratamiento de agua'),(36002,'DistribuciÃ³n de agua '),(39001,'Actividades de saneamiento ambiental y otros servicios de gestiÃ³n de desechos (excepto los servicios prestados por contratistas de construcciÃ³n, constructores y urbanizadores)'),(39002,'Actividades de saneamiento ambiental y otros  de gestiÃ³n de desechos prestados por contratistas de construcciÃ³n, constructores y urbanizadores'),(45411,'Comercio de motocicletas'),(45412,'Comercio de partes, piezas y accesorios de motocicletas'),(46201,'Comercio al por mayor de materias primas agrÃ­colas en bruto (alimentos)'),(46202,'Comercio al por mayor de materias primas pecuarias y animales vivos'),(46321,'Comercio al por mayor de bebidas y tabaco (diferentes a licores y cigarrillos)'),(46322,'Comercio al por mayor de licores y cigarrillos'),(46451,'Comercio al por mayor de productos farmacÃ©uticos y medicinales'),(46452,'Comercio al por mayor de productos cosmÃ©ticos y de tocador (excepto productos farmacÃ©uticos y medicinales)'),(46491,'Comercio al por mayor de otros utensilios domÃ©sticos n.c.p. (excepto joyas)'),(46492,'Venta de joyas'),(46611,'Comercio al por mayor de combustibles sÃ³lidos, lÃ­quidos, gaseosos y productos conexos (excepto combustibles derivados del petrÃ³leo)'),(46612,'Comercio al por mayor de combustibles derivados del petrÃ³leo'),(46631,'Comercio al por mayor de materiales de construcciÃ³n'),(46632,'Comercio al por mayor de artÃ­culos de ferreterÃ­a, pinturas, productos de vidrio, equipo y materiales de fontanerÃ­a y calefacciÃ³n'),(47111,'Comercio al por menor en establecimientos no especializados con surtido compuesto principalmente por alimentos, bebidas no alcohÃ³licas o tabaco (excepto cigarrillos)'),(47112,'Comercio al por menor en establecimientos no especializados con surtido compuesto principalmente por licores y cigarrillos'),(47191,'Comercio al por menor en establecimientos no especializados, con surtido compuesto principalmente por productos de bebidas alcohÃ³licas y cigarrillos '),(47192,'Comercio al por menor en establecimientos no especializados, con surtido compuesto principalmente por drogas, medicamentos, textos escolares, libros y cuadernos.'),(47241,'Comercio al por menor de bebidas y productos del tabaco, en establecimientos especializados (excepto licores y cigarrillos)'),(47242,'Comercio al por menor de licores y cigarrillos'),(47521,'Comercio al por menor de materiales de construcciÃ³n'),(47522,'Comercio al por menor de artÃ­culos de ferreterÃ­a, pinturas y productos de vidrio en establecimientos especializados (excepto materiales de construcciÃ³n)'),(47611,'Comercio al por menor y al por mayor de libros, textos escolares y cuadernos '),(47612,'Comercio al por menor de periÃ³dicos, materiales y artÃ­culos de papelerÃ­a y escritorio, en establecimientos especializados (excepto libros, textos escolares y cuadernos)'),(47731,'Comercio al por menor de productos farmacÃ©uticos y medicinales en establecimientos especializados'),(47732,'Comercio al por menor de productos cosmÃ©ticos y artÃ­culos de tocador en establecimientos especializados (excepto productos farmacÃ©uticos y medicinales)'),(47811,'Comercio al por menor de alimentos en puestos de venta mÃ³viles'),(47812,'Comercio al por menor de bebidas y tabaco en puestos de venta mÃ³viles (excepto licores y cigarrillos)'),(47813,'Comercio al por menor de cigarrillos y licores en puestos de venta mÃ³viles'),(47911,'Comercio al por menor de alimentos y productos agrÃ­colas en bruto; venta de textos escolares y libros (incluye cuadernos escolares); venta de drogas y medicamentos realizado a travÃ©s de internet'),(47912,'Comercio al por menor y al por mayor de madera y materiales para construcciÃ³n; venta de automotores (incluidas motocicletas) realizado a travÃ©s de internet'),(47913,'Comercio al por menor de cigarrillos y licores; venta de combustibles derivados del petrÃ³leo y venta de joyas realizado a travÃ©s de internet'),(47914,'Comercio al por menor de demÃ¡s productos n.c.p. realizado a travÃ©s de internet'),(47921,'Comercio al por menor de alimentos y productos agrÃ­colas en bruto; venta de textos escolares y libros (incluye cuadernos escolares); venta de drogas y medicamentos realizado a travÃ©s de casas de venta o por correo'),(47922,'Comercio al por menor y al por mayor de madera y materiales para construcciÃ³n; venta de automotores (incluidas motocicletas) realizado a travÃ©s de casas de venta o por correo'),(47923,'Comercio al por menor de cigarrillos y licores; venta de combustibles derivados del petrÃ³leo y venta de joyas realizado a travÃ©s de casas de venta o por correo'),(47924,'Comercio al por menor de demÃ¡s productos n.c.p. realizado a travÃ©s de casas de venta o por correo'),(47991,'Otros tipos de comercio al por menor no realizado en establecimientos, puestos de venta o mercados de textos escolares y libros (incluye cuadernos escolares); venta de drogas y medicamentos'),(47992,'Otros tipos de comercio al por menor no realizado en establecimientos, puestos de venta o mercados de materiales para construcciÃ³n; venta de automotores (incluidas motocicletas)'),(47993,'Otros tipos de comercio al por menor no realizado en establecimientos, puestos de venta o mercados de cigarrillos y licores; venta de combustibles derivados del petrÃ³leo y venta de joyas'),(47994,'Otros tipos de comercio al por menor no realizado en establecimientos, puestos de venta o mercados de demÃ¡s productos n.c.p.'),(58111,'Servicio de ediciÃ³n de libros'),(58112,'EdiciÃ³n y publicaciÃ³n de libros'),(58113,'EdiciÃ³n y publicaciÃ³n de libros (Tarifa especial para los contribuyentes que cumplen condiciones del Acuerdo 98 de 2003)'),(60201,'Actividades de programaciÃ³n de televisiÃ³n'),(60202,'Actividades de transmisiÃ³n de televisiÃ³n'),(64991,'Otras actividades de servicio financiero, excepto las de seguros y pensiones n.c.p.'),(64992,'Actividades comerciales de las casas de empeÃ±o o compraventa'),(64993,'Servicios de las casas de empeÃ±o o compraventas'),(66111,'AdministraciÃ³n de mercados financieros (excepto actividades de las bolsas de valores)'),(66112,'Actividades de las bolsas de valores'),(69101,'Actividades jurÃ­dicas como consultorÃ­a profesional'),(69102,'Actividades jurÃ­dicas en el ejercicio de una profesiÃ³n liberal'),(69201,'Actividades de contabilidad, tenedurÃ­a de libros, auditorÃ­a financiera y asesorÃ­a tributaria como consultorÃ­a profesional'),(69202,'Actividades de contabilidad, tenedurÃ­a de libros, auditorÃ­a financiera y asesorÃ­a tributaria en el ejercicio de una profesiÃ³n liberal'),(70101,'Actividades de administraciÃ³n empresarial como consultorÃ­a profesional'),(70102,'Actividades de administraciÃ³n empresarial en el ejercicio de una profesiÃ³n liberal'),(70201,'Actividades de consultorÃ­a de gestiÃ³n'),(70202,'Actividades de gestiÃ³n en el ejercicio de una profesiÃ³n liberal'),(71111,'Actividades de arquitectura'),(71112,'Actividades de arquitectura en ejercicio de una profesiÃ³n liberal'),(71121,'Actividades de ingenierÃ­a y otras actividades conexas de consultorÃ­a tÃ©cnica'),(71122,'Actividades de ingenierÃ­a y otras actividades conexas de consultorÃ­a tÃ©cnica en ejercicio de una profesiÃ³n liberal'),(71201,'Ensayos y anÃ¡lisis tÃ©cnicos como consultorÃ­a profesional'),(71202,'Ensayos y anÃ¡lisis tÃ©cnicos como consultorÃ­a profesional en el ejercicio de una profesiÃ³n liberal'),(72101,'Investigaciones y desarrollo experimental en el campo de las ciencias naturales y la ingenierÃ­a como consultorÃ­a profesional'),(72102,'Investigaciones y desarrollo experimental en el campo de las ciencias naturales y la ingenierÃ­a en el ejercicio de una profesiÃ³n liberal'),(72201,'Investigaciones y desarrollo experimental en el campo de las ciencias sociales y las humanidades como consultorÃ­a profesional'),(72202,'Investigaciones y desarrollo experimental en el campo de las ciencias sociales y las humanidades en el ejercicio de una profesiÃ³n liberal'),(73201,'Estudios de mercado y realizaciÃ³n de encuestas de opiniÃ³n pÃºblica como consultorÃ­a profesional'),(73202,'Estudios de mercado y realizaciÃ³n de encuestas de opiniÃ³n pÃºblica en el ejercicio de una profesiÃ³n liberal'),(74101,'Actividades especializadas de diseÃ±o como consultorÃ­a profesional'),(74102,'Actividades especializadas de diseÃ±o en el ejercicio de una profesiÃ³n liberal'),(74901,'Otras actividades profesionales, cientÃ­ficas y tÃ©cnicas n.c.p. como consultorÃ­a profesional (incluye actividades de periodistas)'),(74902,'Otras actividades profesionales, cientÃ­ficas y tÃ©cnicas n.c.p. en el ejercicio de una profesiÃ³n liberal'),(85591,'EducaciÃ³n acadÃ©mica no formal (excepto programas de educaciÃ³n bÃ¡sica primaria, bÃ¡sica secundaria y media no gradual con fines de validaciÃ³n)'),(85592,'EducaciÃ³n acadÃ©mica no formal impartida mediante programas de educaciÃ³n bÃ¡sica primaria, bÃ¡sica secundaria y media no gradual con fines de validaciÃ³n'),(86211,'Actividades de la prÃ¡ctica mÃ©dica, sin internaciÃ³n (excepto actividades de promociÃ³n y prevenciÃ³n que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pÃºblica o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(86221,'Actividades de la prÃ¡ctica odontolÃ³gica, sin internaciÃ³n (excepto actividades de promociÃ³n y prevenciÃ³n que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pÃºblica o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(86911,'Actividades de apoyo diagnÃ³stico (excepto actividades de promociÃ³n y prevenciÃ³n que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pÃºblica o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(86921,'Actividades de apoyo terapÃ©utico (excepto actividades de promociÃ³n y prevenciÃ³n que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pÃºblica o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(86991,'Otras actividades de atenciÃ³n de la salud humana (excepto actividades de promociÃ³n y prevenciÃ³n que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pÃºblica o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(87101,'Actividades de atenciÃ³n residencial medicalizada de tipo general (excepto actividades de promociÃ³n y prevenciÃ³n que realicen las entidades e instituciones promotoras y prestadoras de servicios de salud de naturaleza pÃºblica o privada, con recursos que provengan del Sistema General de Seguridad Social en Salud.)'),(92001,'Actividades de juegos de destreza, habilidad, conocimiento y fuerza'),(93291,'Otras actividades recreativas y de esparcimiento n.c.p. (excepto juegos de suerte y azar, discotecas y similares )');


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

--
-- Dumping data for table `activos_fijos`
--



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

--
-- Dumping data for table `bderror`
--


--
-- Table structure for table `bodegas`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asientos_contables_lineas`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=242 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asientos_fallidos`
--

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

--
-- Dumping data for table `cantidadestotales`
--

INSERT INTO `cantidadestotales` VALUES (321,376,1,'001',55),(44,79,2,'002',35),(116,148,3,'003',32),(61,63,4,'004',2),(62,94,5,'005',32),(138,170,6,'006',32),(52,147,7,'',95),(70,430,8,'008',360),(153,249,9,'009',96),(96,696,10,'010',600),(53,85,11,'012',32),(20,52,12,'013',32),(17,19,13,'014',2),(638,1138,14,'015',500),(15,100,15,'016',85),(189,289,16,'017',100),(15,35,17,'018',20),(67,117,18,'019',50),(26,326,19,'001002',300),(37,537,20,'023',500),(74,94,21,'0303',20),(47,79,22,'030',32),(15,47,23,'031',32),(15,47,24,'032',32),(15,47,25,'033',32),(15,47,26,'034',32),(15,47,27,'035',32),(15,47,28,'036',32),(15,47,29,'037',32),(15,47,30,'038',32),(15,47,31,'039',32),(15,47,32,'040',32),(15,47,33,'041',32),(15,47,34,'042',32),(15,47,35,'043',32),(15,47,36,'044',32),(0,22,37,'20251478',22),(1,701,38,'1435001',700),(3513,3523,39,'1435002',10),(0,75,40,'1435003',75),(7,8,41,'1435004',1),(96,128,42,'1435005',32),(67,99,43,'1435006',32),(62,82,44,'1435007',20),(0,89,45,'1435008',89),(835,855,46,'1435009',20),(0,36,47,'1435010',36),(0,5,48,'1435011',5),(109,116,49,'1435012',7),(181,190,50,'1435013',9),(149,151,51,'1435014',2),(0,8,52,'1435015',8),(0,86,53,'1435016',86),(10,12,54,'1435017',2),(0,85,55,'1435018',85),(1750,1846,56,'1435019',96),(387,399,57,'1435020',12),(103,126,58,'1435021',23),(493,525,59,'1435022',32),(1,53,60,'1435023',52),(631,633,61,'1435024',2),(618,707,62,'1435025',89),(0,32,63,'1435026',32),(984,990,64,'1435027',6),(2249,2318,65,'1435028',69),(656,660,66,'1435029',4),(0,32,67,'1435030',32),(45,77,68,'1435031',32),(12,48,69,'1435032',36),(466,987,70,'1435033',521),(0,58,71,'1435034',58),(12,20,72,'1435035',8),(14,59,73,'1435036',45),(0,1,74,'1435037',1),(397,453,75,'1435038',56),(63,67,76,'1435039',4),(0,7,77,'1435040',7),(0,8,78,'1435041',8),(0,96,79,'1435042',96),(262,271,80,'1435043',9),(480,1049,81,'1435044',569),(546,578,82,'1435045',32),(571,580,83,'1435046',9),(18,39,84,'1435047',21),(0,32,85,'1435048',32),(3,6,86,'1435049',3),(6,42,87,'1435050',36),(291,491,88,'1435051',200),(4,36,89,'1435052',32),(33,65,90,'1435053',32),(35,36,91,'1435054',1),(1415,1422,92,'1435055',7),(336,405,93,'1435056',69),(20,52,94,'001002003',32),(4,36,95,'8SSHD0P03180T1PL97MCW0V',32);

--
-- Table structure for table `caracteristicas`
--

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

--
-- Table structure for table `cajas`
--

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

--
-- Table structure for table `cajero`
--


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

--
-- Table structure for table `categorias_comprobantes`
--

DROP TABLE IF EXISTS `categorias_comprobantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias_comprobantes` (
  `categoria_comprobante_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`categoria_comprobante_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `categorias_comprobantes` VALUES (1,'Ventas'),(2,'Devoluciones'),(3,'Compras');

--
-- Table structure for table `categoriascomprobantes`
--

DROP TABLE IF EXISTS `categoriascomprobantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoriascomprobantes` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;



INSERT INTO `categoriascomprobantes` VALUES (1,'VENTAS'),(2,'COMPRAS'),(3,'EGRESOS'),(4,'INGRESOS'),(5,'NOTAS'),(6,'NOMINA'),(7,'INVENTARIO'),(8,'DEVOLUCIONES'),(9,'PEDSEB');

--
-- Table structure for table `categoriasegresos`
--


--
-- Table structure for table `categoriasingresos`
--


--
-- Table structure for table `centrocosto`
--


--
-- Table structure for table `clasificaciones_terceros`
--

DROP TABLE IF EXISTS `clasificaciones_terceros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clasificaciones_terceros` (
  `clasificacion_tercero_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`clasificacion_tercero_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


INSERT INTO `clasificaciones_terceros` VALUES (1,'Cliente'),(2,'Proveedor');

--
-- Table structure for table `codigospostalesnacionales`
--

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


INSERT INTO `codigospostalesnacionales` VALUES (1,68615,'RIONEGRO',6875,'687519\r'),(2,70204,'COLOSO',7070,'707030\r'),(3,5674,'SAN VICENTE',540,'054018\r'),(4,44090,'DIBULLA',4460,'446007\r'),(5,15621,'RONDON',1534,'153420\r'),(6,47545,'PIJIÃO DEL CARMEN',4740,'474048\r'),(7,25592,'QUEBRADANEGRA',2534,'253427\r'),(8,5002,'ABEJORRAL',550,'055030\r'),(9,15753,'SOATA',1510,'151007\r'),(10,50711,'VISTAHERMOSA',5040,'504068\r'),(11,73449,'MELGAR',7340,'734007\r'),(12,5264,'ENTRERRIOS',514,'051437\r'),(13,19142,'CALOTO',1910,'191077\r'),(14,50577,'PUERTO LLERAS',5030,'503008\r'),(15,5001,'MEDELLIN',500,'050032\r'),(16,5138,'CAÃASGORDAS',570,'057067\r'),(17,88001,'SAN ANDRES',8800,'880007\r'),(18,23672,'SAN ANTERO',2315,'231520\r'),(19,52835,'SAN ANDRES DE TUMACO',5285,'528538\r'),(20,41359,'ISNOS',4180,'418049\r'),(21,50287,'FUENTE DE ORO',5040,'504027\r'),(22,41013,'AGRADO',4140,'414040\r'),(23,73200,'COELLO',7335,'733508\r'),(24,52317,'GUACHUCAL',5245,'524587\r'),(25,11001,'LOCALIDAD TEUSAQUILLO',1113,'111321\r'),(26,54670,'SAN CALIXTO',5470,'547018\r'),(27,73001,'IBAGUE',7300,'730002\r'),(28,15180,'CHISCAS',1514,'151407\r'),(29,68264,'ENCINO',6825,'682548\r'),(30,20001,'VALLEDUPAR',2000,'200009\r'),(31,5264,'ENTRERRIOS',514,'051430\r'),(32,76001,'CALI',7600,'760036\r'),(33,13442,'MARIA LA BAJA',1310,'131077\r'),(34,19142,'CALOTO',1910,'191088\r'),(35,25491,'NOCAIMA',2536,'253627\r'),(36,8078,'BARANOA',820,'082020\r'),(37,5145,'CARAMANTA',560,'056040\r'),(38,27361,'ISTMINA',2740,'274017\r'),(39,50110,'BARRANCA DE UPIA',5010,'501001\r'),(40,52250,'EL CHARCO',5275,'527529\r'),(41,5209,'CONCORDIA',564,'056417\r'),(42,25797,'TENA',2526,'252610\r'),(43,52720,'SAPUYES',5255,'525550\r'),(44,41001,'NEIVA',4100,'410003\r'),(45,50001,'VILLAVICENCIO',5000,'500004\r'),(46,15638,'SACHICA',1538,'153880\r'),(47,52250,'EL CHARCO',5275,'527520\r'),(48,15572,'PUERTO BOYACA',1552,'155218\r'),(49,5660,'SAN LUIS',544,'054438\r'),(50,17653,'SALAMINA',1720,'172001\r'),(51,85440,'VILLANUEVA',8550,'855030\r'),(52,44001,'RIOHACHA',4400,'440007\r'),(53,97001,'MITU',9700,'970009\r'),(54,54418,'LOURDES',5450,'545077\r'),(55,18001,'FLORENCIA',1800,'180017\r'),(56,68209,'CONFINES',6835,'683531\r'),(57,70670,'SAMPUES',7050,'705070\r'),(58,19256,'EL TAMBO',1935,'193588\r'),(59,5308,'GIRARDOTA',510,'051037\r'),(60,66170,'DOSQUEBRADAS',6610,'661007\r'),(61,5390,'LA PINTADA',550,'055068\r'),(62,52560,'POTOSI',5240,'524038\r'),(63,66001,'PEREIRA',6600,'660004\r'),(64,54001,'CUCUTA',5400,'540004\r'),(65,8573,'PUERTO COLOMBIA',810,'081007\r'),(66,70400,'LA UNION',7040,'704050\r'),(67,20175,'CHIMICHAGUA',2010,'201057\r'),(68,85001,'YOPAL',8500,'850001\r'),(69,52260,'EL TAMBO',5220,'522068\r'),(70,13001,'CARTAGENA',1300,'130015\r'),(71,68397,'LA PAZ',6855,'685517\r'),(72,13430,'MAGANGUE',1325,'132527\r'),(73,68081,'BARRANCABERMEJA',6870,'687038\r'),(74,20250,'EL PASO',2010,'201038\r'),(75,15047,'AQUITANIA',1524,'152429\r'),(76,25612,'RICAURTE',2524,'252417\r'),(77,70110,'BUENAVISTA',7020,'702037\r'),(78,41359,'ISNOS',4180,'418047\r'),(79,5125,'CAICEDO',568,'056847\r'),(80,73675,'SAN ANTONIO',7355,'735538\r'),(81,68500,'OIBA',6830,'683019\r'),(82,8001,'BARRANQUILLA',800,'080006\r'),(83,20032,'ASTREA',2010,'201048\r'),(84,11001,'LOCALIDAD CHAPINERO',1102,'110231\r'),(85,5400,'LA UNION',550,'055020\r'),(86,27660,'SAN JOSE DEL PALMAR',2730,'273077\r'),(87,15798,'TENZA',1532,'153207\r'),(88,20228,'CURUMANI',2030,'203067\r'),(89,25815,'TOCAIMA',2528,'252848\r'),(90,15464,'MONGUA',1520,'152001\r'),(91,44090,'DIBULLA',4460,'446009\r'),(92,63272,'FILANDIA',6340,'634001\r'),(93,19397,'LA VEGA',1940,'194028\r'),(94,41676,'SANTA MARIA',4120,'412020\r'),(95,5667,'SAN RAFAEL',538,'053838\r'),(96,68001,'BUCARAMANGA',6800,'680001\r'),(97,15673,'SAN MATEO',1512,'151201\r'),(98,52435,'MALLAMA',5250,'525068\r'),(99,5030,'AMAGA',558,'055840\r'),(100,85440,'VILLANUEVA',8550,'855039\r'),(101,85263,'PORE',8520,'852058\r'),(102,41551,'PITALITO',4170,'417038\r'),(103,97889,'YAVARATE',9710,'971007\r'),(104,50577,'PUERTO LLERAS',5030,'503007\r'),(105,27050,'ATRATO',2720,'272017\r'),(106,15092,'BETEITIVA',1506,'150617\r'),(107,97161,'CARURU',9730,'973008\r'),(108,27025,'ALTO BAUDO',2760,'276078\r'),(109,85010,'AGUAZUL',8560,'856017\r'),(110,5819,'TOLEDO',520,'052050\r'),(111,73671,'SALDAÃA',7335,'733578\r'),(112,5306,'GIRALDO',570,'057040\r'),(113,17001,'MANIZALES',1700,'170003\r'),(114,68160,'CEPITA',6820,'682061\r'),(115,5483,'NARIÃO',548,'054848\r'),(116,41298,'GARZON',4140,'414027\r'),(117,76845,'ULLOA',7620,'762037\r'),(118,54109,'BUCARASICA',5455,'545557\r'),(119,15244,'EL COCUY',1512,'151287\r'),(120,15816,'TOGÃI',1544,'154407\r'),(121,54670,'SAN CALIXTO',5470,'547010\r'),(122,8433,'MALAMBO',830,'083027\r'),(123,25148,'CAPARRAPI',2534,'253460\r'),(124,20295,'GAMARRA',2050,'205008\r'),(125,68720,'SANTA HELENA DEL OPON',6855,'685501\r'),(126,23555,'PLANETA RICA',2330,'233049\r'),(127,68468,'MOLAGAVITA',6820,'682031\r'),(128,68533,'PARAMO',6835,'683528\r'),(129,25815,'TOCAIMA',2528,'252847\r'),(130,47161,'CERRO SAN ANTONIO',4760,'476020\r'),(131,54001,'CUCUTA',5400,'540003\r'),(132,27160,'CERTEGUI',2720,'272020\r'),(133,17873,'VILLAMARIA',1760,'176001\r'),(134,25407,'LENGUAZAQUE',2506,'250601\r'),(135,68296,'GALAN',6840,'684051\r'),(136,25839,'UBALA',2512,'251267\r'),(137,73861,'VENADILLO',7305,'730588\r'),(138,85230,'OROCUE',8530,'853059\r'),(139,13458,'MONTECRISTO',1340,'134077\r'),(140,76895,'ZARZAL',7625,'762520\r'),(141,11001,'LOCALIDAD FONTIBON',1109,'110931\r'),(142,41319,'GUADALUPE',4160,'416048\r'),(143,47189,'CIENAGA',4780,'478007\r'),(144,44098,'DISTRACCION',4440,'444008\r'),(145,23670,'SAN ANDRES SOTAVENTO',2320,'232039\r'),(146,20011,'AGUACHICA',2050,'205010\r'),(147,13810,'TIQUISIO',1340,'134040\r'),(148,15646,'SAMACA',1536,'153667\r'),(149,63401,'LA TEBAIDA',6330,'633027\r'),(150,25372,'JUNIN',2512,'251227\r'),(151,15172,'CHINAVITA',1532,'153287\r'),(152,73686,'SANTA ISABEL',7305,'730568\r'),(153,11001,'LOCALIDAD USME',1105,'110511\r'),(154,5113,'BURITICA',570,'057030\r'),(155,50270,'EL DORADO',5060,'506027\r'),(156,5761,'SOPETRAN',514,'051448\r'),(157,23686,'SAN PELAYO',2305,'230520\r'),(158,73870,'VILLAHERMOSA',7315,'731501\r'),(159,68217,'COROMORO',6825,'682538\r'),(160,52612,'RICAURTE',5250,'525038\r'),(161,25181,'CHOACHI',2516,'251628\r'),(162,15879,'VIRACACHA',1534,'153450\r'),(163,13140,'CALAMAR',1315,'131547\r'),(164,19075,'BALBOA',1955,'195539\r'),(165,86219,'COLON',8610,'861040\r'),(166,68780,'SURATA',6805,'680507\r'),(167,63130,'CALARCA',6320,'632008\r'),(168,27600,'RIO QUITO',2720,'272050\r'),(169,91530,'PUERTO ALEGRIA',9130,'913057\r'),(170,19455,'MIRANDA',1915,'191528\r'),(171,41791,'TARQUI',4160,'416001\r'),(172,68773,'SUCRE',6850,'685041\r'),(173,68773,'SUCRE',6850,'685048\r'),(174,73001,'IBAGUE',7300,'730018\r'),(175,15681,'SAN PABLO DE BORBUR',1550,'155047\r'),(176,66001,'PEREIRA',6600,'660007\r'),(177,5308,'GIRARDOTA',510,'051030\r'),(178,52612,'RICAURTE',5250,'525030\r'),(179,20175,'CHIMICHAGUA',2010,'201058\r'),(180,19130,'CAJIBIO',1905,'190509\r'),(181,8137,'CAMPO DE LA CRUZ',840,'084047\r'),(182,91669,'PUERTO SANTANDER',9150,'915010\r'),(183,52540,'POLICARPA',5270,'527007\r'),(184,73067,'ATACO',7350,'735058\r'),(185,11001,'LOCALIDAD KENNEDY',1108,'110841\r'),(186,52473,'MOSQUERA',5275,'527580\r'),(187,25754,'SOACHA',2500,'250051\r'),(188,17001,'MANIZALES',1700,'170008\r'),(189,70523,'PALMITO',7060,'706037\r'),(190,52405,'LEIVA',5270,'527060\r'),(191,15442,'MARIPI',1548,'154827\r'),(192,81300,'FORTUL',8140,'814057\r'),(193,70001,'SINCELEJO',7000,'700001\r'),(194,70235,'GALERAS',7020,'702057\r'),(195,81736,'SARAVENA',8150,'815017\r'),(196,73319,'GUAMO',7335,'733558\r'),(197,68820,'TONA',6805,'680547\r'),(198,11001,'LOCALIDAD SUBA',1111,'111161\r'),(199,68298,'GAMBITA',6830,'683037\r'),(200,52418,'LOS ANDES',5265,'526520\r'),(201,27800,'UNGUIA',2780,'278037\r'),(202,44378,'HATONUEVO',4430,'443027\r'),(203,13433,'MAHATES',1310,'131040\r'),(204,5042,'SANTAFE DE ANTIOQUIA',570,'057050\r'),(205,5756,'SONSON',548,'054828\r'),(206,25873,'VILLAPINZON',2508,'250818\r'),(207,23660,'SAHAGUN',2325,'232557\r'),(208,5425,'MACEO',534,'053467\r'),(209,15092,'BETEITIVA',1506,'150610\r'),(210,99001,'PUERTO CARREÃO',9900,'990001\r'),(211,5284,'FRONTINO',574,'057457\r'),(212,76109,'BUENAVENTURA',7645,'764503\r'),(213,17873,'VILLAMARIA',1760,'176007\r'),(214,19573,'PUERTO TEJADA',1915,'191501\r'),(215,19392,'LA SIERRA',1940,'194007\r'),(216,68689,'SAN VICENTE DE CHUCURI',6865,'686537\r'),(217,20001,'VALLEDUPAR',2000,'200002\r'),(218,5792,'TARSO',564,'056438\r'),(219,41615,'RIVERA',4130,'413008\r'),(220,5147,'CAREPA',578,'057857\r'),(221,52203,'COLON',5210,'521060\r'),(222,8001,'BARRANQUILLA',800,'080011\r'),(223,23001,'MONTERIA',2300,'230017\r'),(224,54206,'CONVENCION',5470,'547050\r'),(225,15425,'MACANAL',1528,'152847\r'),(226,5665,'SAN PEDRO DE URABA',578,'057837\r'),(227,52585,'PUPIALES',5245,'524527\r'),(228,13894,'ZAMBRANO',1320,'132047\r'),(229,68081,'BARRANCABERMEJA',6870,'687039\r'),(230,25040,'ANOLAIMA',2530,'253047\r'),(231,54001,'CUCUTA',5400,'540007\r'),(232,18756,'SOLANO',1840,'184010\r'),(233,52565,'PROVIDENCIA',5260,'526027\r'),(234,8558,'POLONUEVO',820,'082040\r'),(235,5001,'MEDELLIN',500,'050040\r'),(236,99524,'LA PRIMAVERA',9920,'992009\r'),(237,68385,'LANDAZURI',6860,'686029\r'),(238,94885,'GUADALUPE',9420,'942057\r'),(239,25491,'NOCAIMA',2536,'253620\r'),(240,23682,'SAN JOSE DE URE',2340,'234010\r'),(241,25040,'ANOLAIMA',2530,'253040\r'),(242,15693,'SANTA ROSA DE VITERBO',1504,'150487\r'),(243,15407,'VILLA DE LEYVA',1540,'154001\r'),(244,8832,'TUBARA',810,'081028\r'),(245,5679,'SANTA BARBARA',550,'055057\r'),(246,8638,'SABANALARGA',850,'085001\r'),(247,25053,'ARBELAEZ',2520,'252008\r'),(248,11001,'LOCALIDAD BARRIOS UNIDOS',1112,'111221\r'),(249,44001,'RIOHACHA',4400,'440003\r'),(250,44279,'FONSECA',4440,'444010\r'),(251,23350,'LA APARTADA',2335,'233501\r'),(252,19364,'JAMBALO',1920,'192027\r'),(253,44847,'URIBIA',4410,'441048\r'),(254,13052,'ARJONA',1310,'131028\r'),(255,52258,'EL TABLON DE GOMEZ',5205,'520530\r'),(256,44650,'SAN JUAN DEL CESAR',4440,'444037\r'),(257,8001,'BARRANQUILLA',800,'080016\r'),(258,5093,'BETULIA',568,'056860\r'),(259,44035,'ALBANIA',4430,'443007\r'),(260,52573,'PUERRES',5235,'523540\r'),(261,15507,'OTANCHE',1550,'155060\r'),(262,19809,'TIMBIQUI',1960,'196038\r'),(263,94663,'MAPIRIPANA',9440,'944058\r'),(264,94343,'BARRANCO MINAS',9440,'944018\r'),(265,52036,'ANCUYA',5260,'526001\r'),(266,44650,'SAN JUAN DEL CESAR',4440,'444030\r'),(267,5658,'SAN JOSE DE LA MONTAÃA',514,'051417\r'),(268,47053,'ARACATACA',4720,'472007\r'),(269,73236,'DOLORES',7345,'734540\r'),(270,76001,'CALI',7600,'760011\r'),(271,70670,'SAMPUES',7050,'705077\r'),(272,68344,'HATO',6835,'683571\r'),(273,15759,'SOGAMOSO',1522,'152211\r'),(274,20750,'SAN DIEGO',2020,'202037\r'),(275,5686,'SANTA ROSA DE OSOS',518,'051860\r'),(276,44090,'DIBULLA',4460,'446008\r'),(277,73520,'PALOCABILDO',7315,'731580\r'),(278,70742,'SAN LUIS DE SINCE',7020,'702070\r'),(279,68872,'VILLANUEVA',6840,'684021\r'),(280,25290,'FUSAGASUGA',2522,'252212\r'),(281,73443,'MARIQUITA',7320,'732028\r'),(282,15248,'EL ESPINO',1512,'151240\r'),(283,5040,'ANORI',528,'052850\r'),(284,47660,'SABANAS DE SAN ANGEL',4750,'475008\r'),(285,76113,'BUGALAGRANDE',7630,'763001\r'),(286,25899,'ZIPAQUIRA',2502,'250257\r'),(287,47288,'FUNDACION',4720,'472028\r'),(288,54520,'PAMPLONITA',5430,'543037\r'),(289,52427,'MAGÃI',5280,'528008\r'),(290,20250,'EL PASO',2010,'201037\r'),(291,54347,'HERRAN',5420,'542017\r'),(292,91540,'PUERTO NARIÃO',9110,'911018\r'),(293,19397,'LA VEGA',1940,'194037\r'),(294,52001,'PASTO',5200,'520004\r'),(295,20011,'AGUACHICA',2050,'205017\r'),(296,15814,'TOCA',1502,'150267\r'),(297,19824,'TOTORO',1925,'192579\r'),(298,23300,'COTORRA',2305,'230507\r'),(299,5628,'SABANALARGA',570,'057028\r'),(300,73124,'CAJAMARCA',7325,'732508\r'),(301,66440,'MARSELLA',6610,'661048\r'),(302,47660,'SABANAS DE SAN ANGEL',4750,'475007\r'),(303,94887,'PANA PANA',9430,'943018\r'),(304,76670,'SAN PEDRO',7630,'763038\r'),(305,23189,'CIENAGA DE ORO',2325,'232537\r'),(306,70702,'SAN JUAN DE BETULIA',7050,'705018\r'),(307,5667,'SAN RAFAEL',538,'053830\r'),(308,50400,'LEJANIAS',5060,'506068\r'),(309,63690,'SALENTO',6310,'631028\r'),(310,63272,'FILANDIA',6340,'634008\r'),(311,19760,'SOTARA',1935,'193507\r'),(312,17616,'RISARALDA',1770,'177060\r'),(313,5284,'FRONTINO',574,'057458\r'),(314,47980,'ZONA BANANERA',4780,'478020\r'),(315,23855,'VALENCIA',2345,'234538\r'),(316,27135,'EL CANTON DEL SAN PABLO',2720,'272048\r'),(317,86573,'LEGUIZAMO',8640,'864007\r'),(318,5885,'YALI',530,'053010\r'),(319,8573,'PUERTO COLOMBIA',810,'081001\r'),(320,76892,'YUMBO',7605,'760507\r'),(321,70124,'CAIMITO',7040,'704018\r'),(322,76497,'OBANDO',7625,'762501\r'),(323,25286,'FUNZA',2500,'250020\r'),(324,5642,'SALGAR',564,'056470\r'),(325,68276,'FLORIDABLANCA',6810,'681002\r'),(326,44078,'BARRANCAS',4430,'443049\r'),(327,5237,'DON MATIAS',518,'051857\r'),(328,85225,'NUNCHIA',8510,'851070\r'),(329,8296,'GALAPA',820,'082001\r'),(330,20001,'VALLEDUPAR',2000,'200008\r'),(331,5842,'URAMITA',574,'057440\r'),(332,5318,'GUARNE',540,'054057\r'),(333,52227,'CUMBAL',5250,'525001\r'),(334,68522,'PALMAR',6835,'683587\r'),(335,8137,'CAMPO DE LA CRUZ',840,'084040\r'),(336,11001,'LOCALIDAD CIUDAD BOLIVAR',1119,'111961\r'),(337,52233,'CUMBITARA',5265,'526560\r'),(338,73349,'HONDA',7320,'732040\r'),(339,19130,'CAJIBIO',1905,'190517\r'),(340,5579,'PUERTO BERRIO',534,'053420\r'),(341,44110,'EL MOLINO',4440,'444057\r'),(342,15537,'PAZ DE RIO',1506,'150680\r'),(343,68689,'SAN VICENTE DE CHUCURI',6865,'686531\r'),(344,52036,'ANCUYA',5260,'526007\r'),(345,23466,'MONTELIBANO',2340,'234007\r'),(346,41349,'HOBO',4130,'413067\r'),(347,5036,'ANGELOPOLIS',558,'055837\r'),(348,99773,'CUMARIBO',9910,'991057\r'),(349,70429,'MAJAGUAL',7030,'703058\r'),(350,50711,'VISTAHERMOSA',5040,'504067\r'),(351,5031,'AMALFI',528,'052848\r'),(352,27745,'SIPI',2740,'274050\r'),(353,85015,'CHAMEZA',8560,'856030\r'),(354,85325,'SAN LUIS DE PALENQUE',8530,'853038\r'),(355,86755,'SAN FRANCISCO',8610,'861007\r'),(356,25377,'LA CALERA',2512,'251208\r'),(357,5679,'SANTA BARBARA',550,'055050\r'),(358,52612,'RICAURTE',5250,'525037\r'),(359,19548,'PIENDAMO',1905,'190547\r'),(360,68502,'ONZAGA',6825,'682527\r'),(361,5079,'BARBOSA',510,'051020\r'),(362,17272,'FILADELFIA',1710,'171020\r'),(363,50330,'MESETAS',5050,'505008\r'),(364,97666,'TARAIRA',9720,'972047\r'),(365,15500,'OICATA',1502,'150220\r'),(366,50680,'SAN CARLOS DE GUAROA',5070,'507019\r'),(367,20383,'LA GLORIA',2040,'204067\r'),(368,91530,'PUERTO ALEGRIA',9130,'913050\r'),(369,15087,'BELEN',1506,'150640\r'),(370,19622,'ROSAS',1935,'193550\r'),(371,15808,'TINJACA',1538,'153847\r'),(372,66001,'PEREIRA',6600,'660001\r'),(373,94001,'INIRIDA',9400,'940001\r'),(374,5148,'EL CARMEN DE VIBORAL',540,'054038\r'),(375,76246,'EL CAIRO',7615,'761501\r'),(376,68669,'SAN ANDRES',6820,'682001\r'),(377,97777,'PAPUNAUA',9730,'973048\r'),(378,5086,'BELMIRA',514,'051427\r'),(379,70771,'SUCRE',7030,'703038\r'),(380,47189,'CIENAGA',4780,'478001\r'),(381,68770,'SUAITA',6830,'683049\r'),(382,76001,'CALI',7600,'760009\r'),(383,54001,'CUCUTA',5400,'540008\r'),(384,19585,'PURACE',1930,'193001\r'),(385,25885,'YACOPI',2538,'253840\r'),(386,5847,'URRAO',568,'056838\r'),(387,25120,'CABRERA',2520,'252040\r'),(388,54599,'RAGONVALIA',5410,'541057\r'),(389,94663,'MAPIRIPANA',9440,'944057\r'),(390,27372,'JURADO',2760,'276018\r'),(391,73001,'IBAGUE',7300,'730006\r'),(392,5088,'BELLO',510,'051054\r'),(393,52233,'CUMBITARA',5265,'526567\r'),(394,52473,'MOSQUERA',5275,'527589\r'),(395,27075,'BAHIA SOLANO',2760,'276030\r'),(396,25885,'YACOPI',2538,'253847\r'),(397,25200,'COGUA',2504,'250407\r'),(398,41668,'SAN AGUSTIN',4180,'418077\r'),(399,41668,'SAN AGUSTIN',4180,'418067\r'),(400,17013,'AGUADAS',1720,'172027\r'),(401,47001,'SANTA MARTA',4700,'470009\r'),(402,19022,'ALMAGUER',1940,'194080\r'),(403,68190,'CIMITARRA',6860,'686058\r'),(404,25779,'SUSA',2506,'250630\r'),(405,73319,'GUAMO',7335,'733547\r'),(406,17662,'SAMANA',1740,'174008\r'),(407,85315,'SACAMA',8510,'851030\r'),(408,15238,'DUITAMA',1504,'150462\r'),(409,68720,'SANTA HELENA DEL OPON',6855,'685507\r'),(410,76041,'ANSERMANUEVO',7620,'762017\r'),(411,15550,'PISBA',1518,'151801\r'),(412,63548,'PIJAO',6320,'632060\r'),(413,68245,'EL GUACAMAYO',6830,'683061\r'),(414,47318,'GUAMAL',4730,'473029\r'),(415,41797,'TESALIA',4150,'415008\r'),(416,11001,'LOCALIDAD FONTIBON',1109,'110911\r'),(417,52381,'LA FLORIDA',5220,'522048\r'),(418,25743,'SILVANIA',2522,'252247\r'),(419,52435,'MALLAMA',5250,'525069\r'),(420,50001,'VILLAVICENCIO',5000,'500003\r'),(421,41615,'RIVERA',4130,'413001\r'),(422,50006,'ACACIAS',5070,'507008\r'),(423,15572,'PUERTO BOYACA',1552,'155219\r'),(424,47798,'TENERIFE',4750,'475057\r'),(425,52838,'TUQUERRES',5255,'525529\r'),(426,70265,'GUARANDA',7030,'703070\r'),(427,68861,'VELEZ',6855,'685561\r'),(428,23417,'LORICA',2310,'231048\r'),(429,13244,'EL CARMEN DE BOLIVAR',1320,'132057\r'),(430,15469,'MONIQUIRA',1542,'154269\r'),(431,41396,'LA PLATA',4160,'416067\r'),(432,25295,'GACHANCIPA',2510,'251020\r'),(433,5649,'SAN CARLOS',544,'054428\r'),(434,15022,'ALMEIDA',1530,'153020\r'),(435,68397,'LA PAZ',6855,'685518\r'),(436,5789,'TAMESIS',560,'056027\r'),(437,66170,'DOSQUEBRADAS',6610,'661001\r'),(438,68425,'MACARAVITA',6815,'681531\r'),(439,76823,'TORO',7615,'761528\r'),(440,5318,'GUARNE',540,'054058\r'),(441,68895,'ZAPATOCA',6840,'684069\r'),(442,15572,'PUERTO BOYACA',1552,'155201\r'),(443,15839,'TUTAZA',1506,'150660\r'),(444,63001,'ARMENIA',6300,'630004\r'),(445,76001,'CALI',7600,'760001\r'),(446,15686,'SANTANA',1544,'154448\r'),(447,18029,'ALBANIA',1860,'186037\r'),(448,25769,'SUBACHOQUE',2502,'250220\r'),(449,99524,'LA PRIMAVERA',9920,'992001\r'),(450,5652,'SAN FRANCISCO',548,'054817\r'),(451,85010,'AGUAZUL',8560,'856019\r'),(452,25899,'ZIPAQUIRA',2502,'250251\r'),(453,86749,'SIBUNDOY',8610,'861027\r'),(454,52405,'LEIVA',5270,'527067\r'),(455,23807,'TIERRALTA',2345,'234517\r'),(456,68276,'FLORIDABLANCA',6810,'681003\r'),(457,15822,'TOTA',1524,'152447\r'),(458,5736,'SEGOVIA',528,'052810\r'),(459,52693,'SAN PABLO',5210,'521048\r'),(460,68406,'LEBRIJA',6875,'687587\r'),(461,68669,'SAN ANDRES',6820,'682009\r'),(462,73001,'IBAGUE',7300,'730001\r'),(463,17433,'MANZANARES',1730,'173020\r'),(464,52083,'BELEN',5210,'521080\r'),(465,76001,'CALI',7600,'760024\r'),(466,76670,'SAN PEDRO',7630,'763037\r'),(467,5308,'GIRARDOTA',510,'051038\r'),(468,85225,'NUNCHIA',8510,'851077\r'),(469,73168,'CHAPARRAL',7355,'735567\r'),(470,23419,'LOS CORDOBAS',2350,'235020\r'),(471,70508,'OVEJAS',7010,'701038\r'),(472,20710,'SAN ALBERTO',2050,'205078\r'),(473,19256,'EL TAMBO',1935,'193589\r'),(474,25320,'GUADUAS',2534,'253440\r'),(475,41503,'OPORAPA',4180,'418007\r'),(476,13744,'SIMITI',1350,'135027\r'),(477,23555,'PLANETA RICA',2330,'233048\r'),(478,15522,'PANQUEBA',1512,'151267\r'),(479,25779,'SUSA',2506,'250637\r'),(480,68861,'VELEZ',6855,'685567\r'),(481,41001,'NEIVA',4100,'410007\r'),(482,25793,'TAUSA',2504,'250418\r'),(483,70233,'EL ROBLE',7050,'705050\r'),(484,25513,'PACHO',2540,'254007\r'),(485,85410,'TAURAMENA',8540,'854037\r'),(486,68101,'BOLIVAR',6850,'685007\r'),(487,11001,'LOCALIDAD SUBA',1111,'111121\r'),(488,47268,'EL RETEN',4780,'478067\r'),(489,25845,'UNE',2518,'251810\r'),(490,50318,'GUAMAL',5070,'507051\r'),(491,19698,'SANTANDER DE QUILICHAO',1910,'191048\r'),(492,25224,'CUCUNUBA',2504,'250450\r'),(493,23855,'VALENCIA',2345,'234539\r'),(494,70713,'SAN ONOFRE',7070,'707010\r'),(495,15212,'COPER',1548,'154860\r'),(496,25486,'NEMOCON',2510,'251038\r'),(497,25777,'SUPATA',2536,'253660\r'),(498,5044,'ANZA',568,'056850\r'),(499,76869,'VIJES',7605,'760557\r'),(500,50573,'PUERTO LOPEZ',5020,'502008\r'),(501,27075,'BAHIA SOLANO',2760,'276038\r'),(502,13001,'CARTAGENA',1300,'130011\r'),(503,68524,'PALMAS DEL SOCORRO',6835,'683541\r'),(504,99773,'CUMARIBO',9910,'991059\r'),(505,25851,'UTICA',2534,'253437\r'),(506,76306,'GINEBRA',7635,'763517\r'),(507,52258,'EL TABLON DE GOMEZ',5205,'520537\r'),(508,13222,'CLEMENCIA',1305,'130510\r'),(509,95025,'EL RETORNO',9510,'951008\r'),(510,85139,'MANI',8540,'854017\r'),(511,85300,'SABANALARGA',8550,'855050\r'),(512,18256,'EL PAUJIL',1810,'181037\r'),(513,27077,'BAJO BAUDO',2750,'275038\r'),(514,19100,'BOLIVAR',1950,'195008\r'),(515,5607,'RETIRO',554,'055430\r'),(516,5861,'VENECIA',564,'056427\r'),(517,73168,'CHAPARRAL',7355,'735569\r'),(518,68855,'VALLE DE SAN JOSE',6825,'682578\r'),(519,8296,'GALAPA',820,'082007\r'),(520,15087,'BELEN',1506,'150647\r'),(521,25580,'PULI',2528,'252801\r'),(522,52885,'YACUANQUER',5230,'523007\r'),(523,5847,'URRAO',568,'056837\r'),(524,95025,'EL RETORNO',9510,'951007\r'),(525,5129,'CALDAS',554,'055440\r'),(526,68179,'CHIPATA',6855,'685551\r'),(527,68152,'CARCASI',6815,'681521\r'),(528,8606,'REPELON',850,'085040\r'),(529,13300,'HATILLO DE LOBA',1330,'133040\r'),(530,15542,'PESCA',1524,'152468\r'),(531,68575,'PUERTO WILCHES',6870,'687061\r'),(532,47170,'CHIBOLO',4760,'476067\r'),(533,41551,'PITALITO',4170,'417039\r'),(534,23162,'CERETE',2305,'230558\r'),(535,91669,'PUERTO SANTANDER',9150,'915019\r'),(536,52260,'EL TAMBO',5220,'522067\r'),(537,70418,'LOS PALMITOS',7010,'701057\r'),(538,5059,'ARMENIA',558,'055867\r'),(539,13683,'SANTA ROSA',1305,'130528\r'),(540,13667,'SAN MARTIN DE LOBA',1335,'133537\r'),(541,52001,'PASTO',5200,'520003\r'),(542,52683,'SANDONA',5225,'522520\r'),(543,25658,'SAN FRANCISCO',2536,'253607\r'),(544,11001,'LOCALIDAD BOSA',1107,'110721\r'),(545,15109,'BUENAVISTA',1548,'154847\r'),(546,52560,'POTOSI',5240,'524039\r'),(547,52685,'SAN BERNARDO',5210,'521001\r'),(548,27615,'RIOSUCIO',2770,'277010\r'),(549,52051,'ARBOLEDA - BERRUECOS',5205,'520577\r'),(550,70001,'SINCELEJO',7000,'700003\r'),(551,17513,'PACORA',1720,'172048\r'),(552,68547,'PIEDECUESTA',6810,'681027\r'),(553,15776,'SUTAMARCHAN',1538,'153867\r'),(554,73275,'FLANDES',7335,'733517\r'),(555,52110,'BUESACO',5205,'520509\r'),(556,95200,'MIRAFLORES',9520,'952001\r'),(557,68298,'GAMBITA',6830,'683031\r'),(558,54810,'TIBU',5480,'548017\r'),(559,15238,'DUITAMA',1504,'150469\r'),(560,50150,'CASTILLA LA NUEVA',5070,'507048\r'),(561,5475,'MURINDO',568,'056818\r'),(562,17001,'MANIZALES',1700,'170001\r'),(563,25658,'SAN FRANCISCO',2536,'253601\r'),(564,25095,'BITUIMA',2532,'253227\r'),(565,19473,'MORALES',1905,'190567\r'),(566,50226,'CUMARAL',5010,'501028\r'),(567,5045,'APARTADO',578,'057840\r'),(568,5400,'LA UNION',550,'055027\r'),(569,50251,'EL CASTILLO',5060,'506047\r'),(570,52687,'SAN LORENZO',5215,'521548\r'),(571,19785,'SUCRE',1940,'194068\r'),(572,5895,'ZARAGOZA',524,'052448\r'),(573,76736,'SEVILLA',7625,'762530\r'),(574,41298,'GARZON',4140,'414020\r'),(575,85139,'MANI',8540,'854018\r'),(576,68406,'LEBRIJA',6875,'687578\r'),(577,73268,'ESPINAL',7335,'733527\r'),(578,52696,'SANTA BARBARA',5275,'527508\r'),(579,19821,'TORIBIO',1920,'192008\r'),(580,5361,'ITUANGO',520,'052079\r'),(581,54405,'LOS PATIOS',5410,'541017\r'),(582,25407,'LENGUAZAQUE',2506,'250607\r'),(583,15676,'SAN MIGUEL DE SEMA',1538,'153827\r'),(584,27491,'NOVITA',2730,'273058\r'),(585,11001,'LOCALIDAD SAN CRISTOBAL',1104,'110441\r'),(586,18001,'FLORENCIA',1800,'180002\r'),(587,68500,'OIBA',6830,'683028\r'),(588,8078,'BARANOA',820,'082027\r'),(589,25295,'GACHANCIPA',2510,'251027\r'),(590,52694,'SAN PEDRO DE CARTAGO',5215,'521501\r'),(591,25524,'PANDI',2520,'252010\r'),(592,17388,'LA MERCED',1720,'172060\r'),(593,76890,'YOTOCO',7610,'761047\r'),(594,41807,'TIMANA',4170,'417018\r'),(595,41206,'COLOMBIA',4110,'411087\r'),(596,73270,'FALAN',7320,'732001\r'),(597,5021,'ALEJANDRIA',538,'053820\r'),(598,15693,'SANTA ROSA DE VITERBO',1504,'150488\r'),(599,15162,'CERINZA',1506,'150620\r'),(600,25772,'SUESCA',2510,'251047\r'),(601,68051,'ARATOCA',6820,'682051\r'),(602,19318,'GUAPI',1960,'196009\r'),(603,13458,'MONTECRISTO',1340,'134070\r'),(604,41518,'PAICOL',4150,'415047\r'),(605,41797,'TESALIA',4150,'415001\r'),(606,15842,'UMBITA',1532,'153240\r'),(607,63130,'CALARCA',6320,'632001\r'),(608,50001,'VILLAVICENCIO',5000,'500001\r'),(609,41799,'TELLO',4110,'411048\r'),(610,50683,'SAN JUAN DE ARAMA',5040,'504047\r'),(611,52390,'LA TOLA',5275,'527547\r'),(612,5148,'EL CARMEN DE VIBORAL',540,'054030\r'),(613,13300,'HATILLO DE LOBA',1330,'133049\r'),(614,13894,'ZAMBRANO',1320,'132040\r'),(615,23162,'CERETE',2305,'230550\r'),(616,15362,'IZA',1522,'152240\r'),(617,19130,'CAJIBIO',1905,'190518\r'),(618,68547,'PIEDECUESTA',6810,'681019\r'),(619,19450,'MERCADERES',1950,'195067\r'),(620,66045,'APIA',6630,'663037\r'),(621,17442,'MARMATO',1780,'178001\r'),(622,19256,'EL TAMBO',1935,'193578\r'),(623,52356,'IPIALES',5240,'524069\r'),(624,23570,'PUEBLO NUEVO',2330,'233001\r'),(625,11001,'LOCALIDAD USME',1105,'110551\r'),(626,25324,'GUATAQUI',2528,'252820\r'),(627,23182,'CHINU',2320,'232050\r'),(628,68079,'BARICHARA',6840,'684041\r'),(629,27413,'LLORO',2710,'271037\r'),(630,52621,'ROBERTO PAYAN',5280,'528039\r'),(631,11001,'LOCALIDAD USME',1105,'110561\r'),(632,47555,'PLATO',4750,'475030\r'),(633,68013,'AGUADA',6855,'685527\r'),(634,52001,'PASTO',5200,'520027\r'),(635,66440,'MARSELLA',6610,'661047\r'),(636,5890,'YOLOMBO',530,'053020\r'),(637,68320,'GUADALUPE',6830,'683057\r'),(638,54385,'LA ESPERANZA',5460,'546058\r'),(639,85250,'PAZ DE ARIPORO',8520,'852039\r'),(640,41306,'GIGANTE',4140,'414008\r'),(641,20517,'PAILITAS',2040,'204001\r'),(642,54245,'EL CARMEN',5470,'547077\r'),(643,44430,'MAICAO',4420,'442009\r'),(644,70233,'EL ROBLE',7050,'705057\r'),(645,54498,'OCAÃA',5465,'546551\r'),(646,47460,'NUEVA GRANADA',4750,'475020\r'),(647,15762,'SORA',1540,'154040\r'),(648,5138,'CAÃASGORDAS',570,'057068\r'),(649,27372,'JURADO',2760,'276019\r'),(650,68522,'PALMAR',6835,'683581\r'),(651,11001,'LOCALIDAD SUBA',1111,'111151\r'),(652,68370,'JORDAN',6840,'684017\r'),(653,91405,'LA CHORRERA',9140,'914050\r'),(654,68615,'RIONEGRO',6875,'687518\r'),(655,19142,'CALOTO',1910,'191079\r'),(656,50400,'LEJANIAS',5060,'506061\r'),(657,86573,'LEGUIZAMO',8640,'864008\r'),(658,52520,'FRANCISCO PIZARRO',5285,'528560\r'),(659,41298,'GARZON',4140,'414029\r'),(660,5266,'ENVIGADO',554,'055420\r'),(661,27430,'MEDIO BAUDO',2750,'275010\r'),(662,73873,'VILLARRICA',7340,'734060\r'),(663,52678,'SAMANIEGO',5260,'526057\r'),(664,20001,'VALLEDUPAR',2000,'200001\r'),(665,52210,'CONTADERO',5230,'523087\r'),(666,19821,'TORIBIO',1920,'192007\r'),(667,68264,'ENCINO',6825,'682547\r'),(668,17001,'MANIZALES',1700,'170017\r'),(669,68255,'EL PLAYON',6875,'687509\r'),(670,25518,'PAIME',2540,'254040\r'),(671,19001,'POPAYAN',1900,'190009\r'),(672,73861,'VENADILLO',7305,'730587\r'),(673,5885,'YALI',530,'053017\r'),(674,8372,'JUAN DE ACOSTA',810,'081047\r'),(675,76020,'ALCALA',7620,'762047\r'),(676,25506,'VENECIA',2520,'252030\r'),(677,52110,'BUESACO',5205,'520517\r'),(678,15401,'LA VICTORIA',1550,'155001\r'),(679,47960,'ZAPAYAN',4760,'476058\r'),(680,41872,'VILLAVIEJA',4110,'411027\r'),(681,19130,'CAJIBIO',1905,'190508\r'),(682,27205,'CONDOTO',2730,'273037\r'),(683,13001,'CARTAGENA',1300,'130008\r'),(684,95015,'CALAMAR',9530,'953007\r'),(685,91407,'LA PEDRERA',9170,'917010\r'),(686,25754,'SOACHA',2500,'250052\r'),(687,25793,'TAUSA',2504,'250410\r'),(688,76275,'FLORIDA',7635,'763567\r'),(689,47001,'SANTA MARTA',4700,'470004\r'),(690,18410,'LA MONTAÃITA',1810,'181059\r'),(691,5480,'MUTATA',574,'057420\r'),(692,68001,'BUCARAMANGA',6800,'680004\r'),(693,25506,'VENECIA',2520,'252037\r'),(694,68500,'OIBA',6830,'683021\r'),(695,73483,'NATAGAIMA',7350,'735007\r'),(696,15218,'COVARACHIA',1510,'151047\r'),(697,95001,'SAN JOSE DEL GUAVIARE',9500,'950001\r'),(698,47170,'CHIBOLO',4760,'476068\r'),(699,50313,'GRANADA',5040,'504001\r'),(700,19001,'POPAYAN',1900,'190018\r'),(701,68498,'OCAMONTE',6825,'682568\r'),(702,5873,'VIGIA DEL FUERTE',568,'056820\r'),(703,5002,'ABEJORRAL',550,'055038\r'),(704,25019,'ALBAN',2532,'253201\r'),(705,8558,'POLONUEVO',820,'082047\r'),(706,66001,'PEREIRA',6600,'660002\r'),(707,44560,'MANAURE',4410,'441009\r'),(708,5543,'PEQUE',570,'057017\r'),(709,47980,'ZONA BANANERA',4780,'478027\r'),(710,47245,'EL BANCO',4730,'473047\r'),(711,15740,'SIACHOQUE',1534,'153468\r'),(712,13490,'NOROSI',1345,'134517\r'),(713,5051,'ARBOLETES',578,'057829\r'),(714,47318,'GUAMAL',4730,'473027\r'),(715,5541,'PEÃOL',538,'053857\r'),(716,94886,'CACAHUAL',9410,'941017\r'),(717,15299,'GARAGOA',1528,'152867\r'),(718,50223,'CUBARRAL',5060,'506001\r'),(719,8606,'REPELON',850,'085047\r'),(720,94883,'SAN FELIPE',9420,'942010\r'),(721,50689,'SAN MARTIN',5070,'507028\r'),(722,47707,'SANTA ANA',4740,'474028\r'),(723,11001,'LOCALIDAD KENNEDY',1108,'110871\r'),(724,73024,'ALPUJARRA',7345,'734560\r'),(725,23580,'PUERTO LIBERTADOR',2340,'234030\r'),(726,19130,'CAJIBIO',1905,'190501\r'),(727,73283,'FRESNO',7315,'731568\r'),(728,15204,'COMBITA',1502,'150207\r'),(729,41548,'PITAL',4140,'414068\r'),(730,73024,'ALPUJARRA',7345,'734567\r'),(731,15740,'SIACHOQUE',1534,'153460\r'),(732,54670,'SAN CALIXTO',5470,'547017\r'),(733,25899,'ZIPAQUIRA',2502,'250252\r'),(734,11001,'LOCALIDAD RAFAEL URIBE URIBE',1118,'111821\r'),(735,25086,'BELTRAN',2532,'253260\r'),(736,41770,'SUAZA',4160,'416087\r'),(737,52381,'LA FLORIDA',5220,'522047\r'),(738,41378,'LA ARGENTINA',4150,'415080\r'),(739,73555,'PLANADAS',7350,'735070\r'),(740,85250,'PAZ DE ARIPORO',8520,'852038\r'),(741,73870,'VILLAHERMOSA',7315,'731507\r'),(742,17614,'RIOSUCIO',1780,'178047\r'),(743,5055,'ARGELIA',548,'054838\r'),(744,73217,'COYAIMA',7350,'735037\r'),(745,8770,'SUAN',840,'084067\r'),(746,47245,'EL BANCO',4730,'473048\r'),(747,13001,'CARTAGENA',1300,'130002\r'),(748,5837,'TURBO',578,'057868\r'),(749,23419,'LOS CORDOBAS',2350,'235028\r'),(750,5440,'MARINILLA',540,'054020\r'),(751,47058,'ARIGUANI',4750,'475017\r'),(752,23678,'SAN CARLOS',2325,'232508\r'),(753,19290,'FLORENCIA',1950,'195040\r'),(754,73461,'MURILLO',7310,'731060\r'),(755,47058,'ARIGUANI',4750,'475010\r'),(756,41801,'TERUEL',4120,'412047\r'),(757,52786,'TAMINANGO',5215,'521569\r'),(758,85325,'SAN LUIS DE PALENQUE',8530,'853039\r'),(759,50110,'BARRANCA DE UPIA',5010,'501007\r'),(760,23079,'BUENAVISTA',2330,'233020\r'),(761,52699,'SANTACRUZ',5255,'525579\r'),(762,5658,'SAN JOSE DE LA MONTAÃA',514,'051410\r'),(763,8372,'JUAN DE ACOSTA',810,'081040\r'),(764,19364,'JAMBALO',1920,'192020\r'),(765,15533,'PAYA',1518,'151827\r'),(766,20001,'VALLEDUPAR',2000,'200007\r'),(767,5088,'BELLO',510,'051058\r'),(768,13600,'RIO VIEJO',1345,'134501\r'),(769,91798,'TARAPACA',9110,'911039\r'),(770,25843,'VILLA DE SAN DIEGO DE UBATE',2504,'250430\r'),(771,73217,'COYAIMA',7350,'735028\r'),(772,19290,'FLORENCIA',1950,'195047\r'),(773,18094,'BELEN DE LOS ANDAQUIES',1860,'186017\r'),(774,73001,'IBAGUE',7300,'730004\r'),(775,15600,'RAQUIRA',1538,'153801\r'),(776,41396,'LA PLATA',4150,'415069\r'),(777,86571,'PUERTO GUZMAN',8630,'863007\r'),(778,5628,'SABANALARGA',570,'057020\r'),(779,11001,'LOCALIDAD ENGATIVA',1110,'111041\r'),(780,73001,'IBAGUE',7300,'730019\r'),(781,47703,'SAN ZENON',4740,'474067\r'),(782,68307,'GIRON',6875,'687557\r'),(783,11001,'LOCALIDAD PUENTE ARANDA',1116,'111621\r'),(784,5282,'FREDONIA',550,'055070\r'),(785,23189,'CIENAGA DE ORO',2325,'232529\r'),(786,15580,'QUIPAMA',1550,'155027\r'),(787,5206,'CONCEPCION',538,'053810\r'),(788,91798,'TARAPACA',9110,'911037\r'),(789,17001,'MANIZALES',1700,'170002\r'),(790,70670,'SAMPUES',7050,'705078\r'),(791,99773,'CUMARIBO',9910,'991007\r'),(792,73001,'IBAGUE',7300,'730003\r'),(793,76001,'CALI',7600,'760026\r'),(794,15804,'TIBANA',1532,'153260\r'),(795,52110,'BUESACO',5205,'520518\r'),(796,19137,'CALDONO',1920,'192048\r'),(797,54820,'TOLEDO',5420,'542037\r'),(798,52835,'SAN ANDRES DE TUMACO',5285,'528517\r'),(799,15820,'TOPAGA',1520,'152047\r'),(800,5842,'URAMITA',574,'057447\r'),(801,68001,'BUCARAMANGA',6800,'680002\r'),(802,68820,'TONA',6805,'680541\r'),(803,5001,'MEDELLIN',500,'050015\r'),(804,70215,'COROZAL',7050,'705037\r'),(805,54206,'CONVENCION',5470,'547057\r'),(806,70717,'SAN PEDRO',7020,'702018\r'),(807,15114,'BUSBANZA',1520,'152080\r'),(808,11001,'LOCALIDAD CIUDAD BOLIVAR',1119,'111971\r'),(809,86569,'PUERTO CAICEDO',8620,'862080\r'),(810,27495,'NUQUI',2760,'276050\r'),(811,52079,'BARBACOAS',5280,'528078\r'),(812,11001,'LOCALIDAD CIUDAD BOLIVAR',1119,'111941\r'),(813,23068,'AYAPEL',2335,'233538\r'),(814,15380,'LA CAPILLA',1532,'153227\r'),(815,23417,'LORICA',2310,'231038\r'),(816,18756,'SOLANO',1840,'184017\r'),(817,76126,'CALIMA',7605,'760538\r'),(818,25599,'APULO',2526,'252657\r'),(819,73443,'MARIQUITA',7320,'732027\r'),(820,23500,'MOÃITOS',2310,'231007\r'),(821,76400,'LA UNION',7615,'761540\r'),(822,76122,'CAICEDONIA',7625,'762548\r'),(823,23660,'SAHAGUN',2325,'232547\r'),(824,5237,'DON MATIAS',518,'051858\r'),(825,68533,'PARAMO',6835,'683521\r'),(826,44001,'RIOHACHA',4400,'440008\r'),(827,70717,'SAN PEDRO',7020,'702017\r'),(828,68572,'PUENTE NACIONAL',6845,'684527\r'),(829,86885,'VILLAGARZON',8610,'861088\r'),(830,70678,'SAN BENITO ABAD',7030,'703018\r'),(831,19824,'TOTORO',1925,'192578\r'),(832,8001,'BARRANQUILLA',800,'080010\r'),(833,68385,'LANDAZURI',6860,'686028\r'),(834,15248,'EL ESPINO',1512,'151247\r'),(835,17614,'RIOSUCIO',1780,'178057\r'),(836,47245,'EL BANCO',4730,'473049\r'),(837,5142,'CARACOLI',534,'053457\r'),(838,70508,'OVEJAS',7010,'701037\r'),(839,76616,'RIOFRIO',7610,'761038\r'),(840,5079,'BARBOSA',510,'051028\r'),(841,25402,'LA VEGA',2536,'253610\r'),(842,27600,'RIO QUITO',2720,'272058\r'),(843,91540,'PUERTO NARIÃO',9110,'911017\r'),(844,44078,'BARRANCAS',4430,'443048\r'),(845,70708,'SAN MARCOS',7040,'704030\r'),(846,17662,'SAMANA',1740,'174001\r'),(847,94001,'INIRIDA',9400,'940008\r'),(848,23001,'MONTERIA',2300,'230027\r'),(849,54820,'TOLEDO',5420,'542030\r'),(850,76828,'TRUJILLO',7610,'761028\r'),(851,17042,'ANSERMA',1770,'177087\r'),(852,66440,'MARSELLA',6610,'661040\r'),(853,15808,'TINJACA',1538,'153840\r'),(854,5107,'BRICEÃO',520,'052060\r'),(855,19137,'CALDONO',1920,'192057\r'),(856,18205,'CURILLO',1860,'186058\r'),(857,15135,'CAMPOHERMOSO',1526,'152647\r'),(858,27025,'ALTO BAUDO',2760,'276077\r'),(859,13430,'MAGANGUE',1325,'132511\r'),(860,54520,'PAMPLONITA',5430,'543030\r'),(861,15362,'IZA',1522,'152247\r'),(862,86885,'VILLAGARZON',8610,'861087\r'),(863,5206,'CONCEPCION',538,'053817\r'),(864,13442,'MARIA LA BAJA',1310,'131067\r'),(865,23586,'PURISIMA',2315,'231540\r'),(866,13440,'MARGARITA',1330,'133028\r'),(867,19698,'SANTANDER DE QUILICHAO',1910,'191047\r'),(868,23580,'PUERTO LIBERTADOR',2340,'234039\r'),(869,19809,'TIMBIQUI',1960,'196030\r'),(870,50226,'CUMARAL',5010,'501021\r'),(871,19100,'BOLIVAR',1950,'195017\r'),(872,97666,'TARAIRA',9720,'972048\r'),(873,15761,'SOMONDOCO',1530,'153037\r'),(874,25745,'SIMIJACA',2506,'250640\r'),(875,54498,'OCAÃA',5465,'546552\r'),(876,17524,'PALESTINA',1760,'176048\r'),(877,70823,'TOLU VIEJO',7070,'707057\r'),(878,25053,'ARBELAEZ',2520,'252001\r'),(879,25320,'GUADUAS',2534,'253449\r'),(880,44874,'VILLANUEVA',4450,'445007\r'),(881,76147,'CARTAGO',7620,'762027\r'),(882,63272,'FILANDIA',6340,'634007\r'),(883,13430,'MAGANGUE',1325,'132519\r'),(884,50711,'VISTAHERMOSA',5040,'504061\r'),(885,68001,'BUCARAMANGA',6800,'680007\r'),(886,19075,'BALBOA',1955,'195538\r'),(887,19455,'MIRANDA',1915,'191529\r'),(888,54003,'ABREGO',5460,'546077\r'),(889,5690,'SANTO DOMINGO',530,'053048\r'),(890,97001,'MITU',9700,'970008\r'),(891,50330,'MESETAS',5050,'505007\r'),(892,47675,'SALAMINA',4770,'477047\r'),(893,25530,'PARATEBUENO',2514,'251407\r'),(894,13001,'CARTAGENA',1300,'130027\r'),(895,68464,'MOGOTES',6825,'682509\r'),(896,15599,'RAMIRIQUI',1534,'153407\r'),(897,5001,'MEDELLIN',500,'050004\r'),(898,15533,'PAYA',1518,'151820\r'),(899,25743,'SILVANIA',2522,'252248\r'),(900,5686,'SANTA ROSA DE OSOS',518,'051868\r'),(901,27361,'ISTMINA',2740,'274010\r'),(902,76377,'LA CUMBRE',7605,'760518\r'),(903,5890,'YOLOMBO',530,'053028\r'),(904,23807,'TIERRALTA',2345,'234508\r'),(905,5197,'COCORNA',544,'054448\r'),(906,47258,'EL PIÃON',4760,'476007\r'),(907,76001,'CALI',7600,'760023\r'),(908,13001,'CARTAGENA',1300,'130014\r'),(909,25430,'MADRID',2500,'250030\r'),(910,5001,'MEDELLIN',500,'050006\r'),(911,54480,'MUTISCUA',5440,'544070\r'),(912,85230,'OROCUE',8530,'853057\r'),(913,91430,'LA VICTORIA',9160,'916017\r'),(914,19256,'EL TAMBO',1935,'193579\r'),(915,27099,'BOJAYA',2770,'277050\r'),(916,15660,'SAN EDUARDO',1526,'152601\r'),(917,85300,'SABANALARGA',8550,'855057\r'),(918,76001,'CALI',7600,'760035\r'),(919,68406,'LEBRIJA',6875,'687577\r'),(920,13140,'CALAMAR',1315,'131540\r'),(921,15293,'GACHANTIVA',1542,'154220\r'),(922,52435,'MALLAMA',5250,'525067\r'),(923,94887,'PANA PANA',9430,'943019\r'),(924,70215,'COROZAL',7050,'705039\r'),(925,5001,'MEDELLIN',500,'050043\r'),(926,73504,'ORTEGA',7355,'735507\r'),(927,25592,'QUEBRADANEGRA',2534,'253420\r'),(928,52215,'CORDOBA',5240,'524009\r'),(929,15322,'GUATEQUE',1530,'153057\r'),(930,5756,'SONSON',548,'054827\r'),(931,99773,'CUMARIBO',9910,'991019\r'),(932,25328,'GUAYABAL DE SIQUIMA',2532,'253217\r'),(933,25594,'QUETAME',2518,'251847\r'),(934,41306,'GIGANTE',4140,'414009\r'),(935,68755,'SOCORRO',6835,'683557\r'),(936,25772,'SUESCA',2510,'251040\r'),(937,47288,'FUNDACION',4720,'472027\r'),(938,68162,'CERRITO',6815,'681501\r'),(939,68324,'GUAVATA',6845,'684507\r'),(940,70418,'LOS PALMITOS',7010,'701050\r'),(941,25320,'GUADUAS',2534,'253447\r'),(942,25740,'SIBATE',2500,'250078\r'),(943,68615,'RIONEGRO',6875,'687517\r'),(944,85263,'PORE',8520,'852050\r'),(945,20238,'EL COPEY',2010,'201010\r'),(946,8520,'PALMAR DE VARELA',830,'083080\r'),(947,47001,'SANTA MARTA',4700,'470017\r'),(948,41518,'PAICOL',4150,'415040\r'),(949,47570,'PUEBLOVIEJO',4780,'478048\r'),(950,11001,'LOCALIDAD SANTA FE',1103,'110311\r'),(951,68684,'SAN JOSE DE MIRANDA',6820,'682021\r'),(952,76622,'ROLDANILLO',7615,'761558\r'),(953,68229,'CURITI',6820,'682041\r'),(954,15189,'CIENEGA',1534,'153440\r'),(955,17541,'PENSILVANIA',1730,'173060\r'),(956,68318,'GUACA',6810,'681048\r'),(957,70001,'SINCELEJO',7000,'700009\r'),(958,25407,'LENGUAZAQUE',2506,'250608\r'),(959,70771,'SUCRE',7030,'703030\r'),(960,11001,'LOCALIDAD SUBA',1111,'111156\r'),(961,68147,'CAPITANEJO',6815,'681547\r'),(962,47555,'PLATO',4750,'475038\r'),(963,25483,'NARIÃO',2528,'252830\r'),(964,54680,'SANTIAGO',5450,'545037\r'),(965,52585,'PUPIALES',5245,'524520\r'),(966,19743,'SILVIA',1920,'192077\r'),(967,15187,'CHIVATA',1502,'150247\r'),(968,52788,'TANGUA',5235,'523507\r'),(969,94888,'MORICHAL',9430,'943067\r'),(970,85440,'VILLANUEVA',8550,'855038\r'),(971,76001,'CALI',7600,'760016\r'),(972,76364,'JAMUNDI',7640,'764007\r'),(973,25662,'SAN JUAN DE RIO SECO',2532,'253257\r'),(974,15757,'SOCHA',1516,'151647\r'),(975,19533,'PIAMONTE',1945,'194558\r'),(976,19693,'SAN SEBASTIAN',1945,'194508\r'),(977,68444,'MATANZA',6805,'680567\r'),(978,68686,'SAN MIGUEL',6815,'681557\r'),(979,19450,'MERCADERES',1950,'195060\r'),(980,5656,'SAN JERONIMO',510,'051070\r'),(981,41530,'PALESTINA',4170,'417060\r'),(982,81065,'ARAUQUITA',8160,'816018\r'),(983,27135,'EL CANTON DEL SAN PABLO',2720,'272040\r'),(984,20400,'LA JAGUA DE IBIRICO',2030,'203020\r'),(985,50318,'GUAMAL',5070,'507058\r'),(986,68092,'BETULIA',6865,'686508\r'),(987,63470,'MONTENEGRO',6330,'633001\r'),(988,68344,'HATO',6835,'683578\r'),(989,76001,'CALI',7600,'760010\r'),(990,11001,'LOCALIDAD SUBA',1111,'111176\r'),(991,5591,'PUERTO TRIUNFO',534,'053447\r'),(992,68867,'VETAS',6805,'680527\r'),(993,5893,'YONDO',534,'053410\r'),(994,27600,'RIO QUITO',2720,'272057\r'),(995,76001,'CALI',7600,'760031\r'),(996,8560,'PONEDERA',840,'084001\r'),(997,68121,'CABRERA',6835,'683507\r'),(998,15542,'PESCA',1524,'152469\r'),(999,73861,'VENADILLO',7305,'730580\r'),(1000,86219,'COLON',8610,'861047\r'),(1001,70508,'OVEJAS',7010,'701030\r'),(1002,15720,'SATIVANORTE',1508,'150820\r'),(1003,5579,'PUERTO BERRIO',534,'053428\r'),(1004,76001,'CALI',7600,'760006\r'),(1005,5411,'LIBORINA',514,'051460\r'),(1006,13650,'SAN FERNANDO',1330,'133007\r'),(1007,76890,'YOTOCO',7610,'761048\r'),(1008,13655,'SAN JACINTO DEL CAUCA',1340,'134068\r'),(1009,52287,'FUNES',5235,'523527\r'),(1010,68296,'GALAN',6840,'684058\r'),(1011,50590,'PUERTO RICO',5030,'503061\r'),(1012,54720,'SARDINATA',5455,'545537\r'),(1013,8832,'TUBARA',810,'081027\r'),(1014,15047,'AQUITANIA',1524,'152427\r'),(1015,5001,'MEDELLIN',500,'050016\r'),(1016,25645,'SAN ANTONIO DEL TEQUENDAMA',2526,'252627\r'),(1017,52022,'ALDANA',5245,'524540\r'),(1018,5543,'PEQUE',570,'057010\r'),(1019,15531,'PAUNA',1548,'154808\r'),(1020,23001,'MONTERIA',2300,'230002\r'),(1021,85162,'MONTERREY',8550,'855010\r'),(1022,52621,'ROBERTO PAYAN',5280,'528037\r'),(1023,19256,'EL TAMBO',1935,'193570\r'),(1024,54206,'CONVENCION',5470,'547058\r'),(1025,5591,'PUERTO TRIUNFO',534,'053440\r'),(1026,11001,'LOCALIDAD USAQUEN',1101,'110111\r'),(1027,68235,'EL CARMEN DE CHUCURI',6865,'686569\r'),(1028,52427,'MAGÃI',5280,'528009\r'),(1029,52210,'CONTADERO',5230,'523080\r'),(1030,41885,'YAGUARA',4120,'412080\r'),(1031,18410,'LA MONTAÃITA',1810,'181058\r'),(1032,5001,'MEDELLIN',500,'050024\r'),(1033,52051,'ARBOLEDA - BERRUECOS',5205,'520570\r'),(1034,47030,'ALGARROBO',4720,'472047\r'),(1035,68271,'FLORIAN',6845,'684541\r'),(1036,5895,'ZARAGOZA',524,'052447\r'),(1037,5282,'FREDONIA',550,'055077\r'),(1038,76616,'RIOFRIO',7610,'761030\r'),(1039,5004,'ABRIAQUI',574,'057467\r'),(1040,15367,'JENESANO',1536,'153608\r'),(1041,27425,'MEDIO ATRATO',2700,'270070\r'),(1042,41885,'YAGUARA',4120,'412087\r'),(1043,54001,'CUCUTA',5400,'540019\r'),(1044,23417,'LORICA',2310,'231029\r'),(1045,73520,'PALOCABILDO',7315,'731587\r'),(1046,25035,'ANAPOIMA',2526,'252640\r'),(1047,54800,'TEORAMA',5470,'547037\r'),(1048,47161,'CERRO SAN ANTONIO',4760,'476027\r'),(1049,25398,'LA PEÃA',2536,'253647\r'),(1050,86757,'SAN MIGUEL',8620,'862047\r'),(1051,5234,'DABEIBA',574,'057438\r'),(1052,68327,'GÃEPSA',6855,'685541\r'),(1053,20032,'ASTREA',2010,'201040\r'),(1054,68573,'PUERTO PARRA',6860,'686007\r'),(1055,44560,'MANAURE',4410,'441008\r'),(1056,25873,'VILLAPINZON',2508,'250810\r'),(1057,27160,'CERTEGUI',2720,'272027\r'),(1058,63212,'CORDOBA',6320,'632027\r'),(1059,63001,'ARMENIA',6300,'630001\r'),(1060,68207,'CONCEPCION',6815,'681518\r'),(1061,41001,'NEIVA',4100,'410001\r'),(1062,91669,'PUERTO SANTANDER',9150,'915017\r'),(1063,73443,'MARIQUITA',7320,'732020\r'),(1064,73770,'SUAREZ',7335,'733587\r'),(1065,54418,'LOURDES',5450,'545070\r'),(1066,68895,'ZAPATOCA',6840,'684061\r'),(1067,25867,'VIANI',2532,'253230\r'),(1068,47745,'SITIONUEVO',4770,'477009\r'),(1069,47205,'CONCORDIA',4760,'476037\r'),(1070,68152,'CARCASI',6815,'681527\r'),(1071,15776,'SUTAMARCHAN',1538,'153860\r'),(1072,11001,'LOCALIDAD PUENTE ARANDA',1116,'111631\r'),(1073,23001,'MONTERIA',2300,'230009\r'),(1074,5887,'YARUMAL',520,'052030\r'),(1075,13052,'ARJONA',1310,'131029\r'),(1076,68745,'SIMACOTA',6835,'683568\r'),(1077,94001,'INIRIDA',9400,'940018\r'),(1078,5607,'RETIRO',554,'055438\r'),(1079,19821,'TORIBIO',1920,'192001\r'),(1080,13006,'ACHI',1340,'134027\r'),(1081,25335,'GUAYABETAL',2518,'251850\r'),(1082,76736,'SEVILLA',7625,'762538\r'),(1083,15380,'LA CAPILLA',1532,'153220\r'),(1084,47707,'SANTA ANA',4740,'474020\r'),(1085,13780,'TALAIGUA NUEVO',1325,'132547\r'),(1086,15476,'MOTAVITA',1540,'154080\r'),(1087,44110,'EL MOLINO',4440,'444050\r'),(1088,47058,'ARIGUANI',4750,'475018\r'),(1089,8001,'BARRANQUILLA',800,'080004\r'),(1090,19517,'PAEZ',1925,'192508\r'),(1091,11001,'LOCALIDAD USAQUEN',1101,'110151\r'),(1092,8433,'MALAMBO',830,'083020\r'),(1093,25793,'TAUSA',2504,'250417\r'),(1094,5674,'SAN VICENTE',540,'054010\r'),(1095,15759,'SOGAMOSO',1522,'152210\r'),(1096,25281,'FOSCA',2518,'251830\r'),(1097,54250,'EL TARRA',5480,'548050\r'),(1098,73026,'ALVARADO',7305,'730520\r'),(1099,47288,'FUNDACION',4720,'472020\r'),(1100,17013,'AGUADAS',1720,'172029\r'),(1101,15185,'CHITARAQUE',1544,'154427\r'),(1102,15516,'PAIPA',1504,'150448\r'),(1103,25535,'PASCA',2522,'252201\r'),(1104,73411,'LIBANO',7310,'731040\r'),(1105,25436,'MANTA',2508,'250830\r'),(1106,19548,'PIENDAMO',1905,'190538\r'),(1107,50124,'CABUYARO',5010,'501017\r'),(1108,68615,'RIONEGRO',6875,'687527\r'),(1109,76403,'LA VICTORIA',7625,'762517\r'),(1110,8421,'LURUACO',850,'085067\r'),(1111,5585,'PUERTO NARE',534,'053430\r'),(1112,68575,'PUERTO WILCHES',6870,'687069\r'),(1113,5001,'MEDELLIN',500,'050036\r'),(1114,44430,'MAICAO',4420,'442001\r'),(1115,25754,'SOACHA',2500,'250055\r'),(1116,76001,'CALI',7600,'760042\r'),(1117,68549,'PINCHOTE',6835,'683517\r'),(1118,54800,'TEORAMA',5470,'547039\r'),(1119,11001,'LOCALIDAD SUMAPAZ',1120,'112021\r'),(1120,15480,'MUZO',1548,'154887\r'),(1121,15183,'CHITA',1516,'151608\r'),(1122,8421,'LURUACO',850,'085060\r'),(1123,5480,'MUTATA',574,'057427\r'),(1124,50568,'PUERTO GAITAN',5020,'502048\r'),(1125,41206,'COLOMBIA',4110,'411088\r'),(1126,81001,'ARAUCA',8100,'810009\r'),(1127,5134,'CAMPAMENTO',520,'052020\r'),(1128,47268,'EL RETEN',4780,'478060\r'),(1129,5001,'MEDELLIN',500,'050021\r'),(1130,54377,'LABATECA',5420,'542050\r'),(1131,99001,'PUERTO CARREÃO',9900,'990009\r'),(1132,11001,'LOCALIDAD SUMAPAZ',1120,'112031\r'),(1133,27077,'BAJO BAUDO',2750,'275030\r'),(1134,94888,'MORICHAL',9430,'943058\r'),(1135,5266,'ENVIGADO',554,'055427\r'),(1136,54003,'ABREGO',5460,'546070\r'),(1137,27413,'LLORO',2710,'271030\r'),(1138,47555,'PLATO',4750,'475037\r'),(1139,25740,'SIBATE',2500,'250077\r'),(1140,73001,'IBAGUE',7300,'730005\r'),(1141,17174,'CHINCHINA',1760,'176028\r'),(1142,81591,'PUERTO RONDON',8130,'813017\r'),(1143,15051,'ARCABUCO',1542,'154201\r'),(1144,44847,'URIBIA',4410,'441039\r'),(1145,11001,'LOCALIDAD SUBA',1111,'111131\r'),(1146,50689,'SAN MARTIN',5070,'507021\r'),(1147,68276,'FLORIDABLANCA',6810,'681007\r'),(1148,68266,'ENCISO',6815,'681561\r'),(1149,73873,'VILLARRICA',7340,'734068\r'),(1150,94001,'INIRIDA',9400,'940007\r'),(1151,5501,'OLAYA',514,'051450\r'),(1152,25245,'EL COLEGIO',2526,'252638\r'),(1153,27361,'ISTMINA',2740,'274018\r'),(1154,23660,'SAHAGUN',2325,'232558\r'),(1155,25181,'CHOACHI',2516,'251620\r'),(1156,20621,'LA PAZ',2020,'202017\r'),(1157,15839,'TUTAZA',1506,'150667\r'),(1158,41016,'AIPE',4110,'411007\r'),(1159,27450,'MEDIO SAN JUAN',2740,'274039\r'),(1160,68432,'MALAGA',6820,'682017\r'),(1161,25823,'TOPAIPI',2538,'253827\r'),(1162,94001,'INIRIDA',9400,'940027\r'),(1163,25322,'GUASCA',2512,'251217\r'),(1164,68575,'PUERTO WILCHES',6870,'687068\r'),(1165,13042,'ARENAL',1345,'134527\r'),(1166,5761,'SOPETRAN',514,'051447\r'),(1167,52254,'EL PEÃOL',5220,'522088\r'),(1168,5347,'HELICONIA',558,'055827\r'),(1169,52835,'SAN ANDRES DE TUMACO',5285,'528519\r'),(1170,41001,'NEIVA',4100,'410017\r'),(1171,76109,'BUENAVENTURA',7645,'764504\r'),(1172,11001,'LOCALIDAD PUENTE ARANDA',1116,'111611\r'),(1173,70771,'SUCRE',7030,'703037\r'),(1174,25754,'SOACHA',2500,'250054\r'),(1175,5051,'ARBOLETES',578,'057820\r'),(1176,41319,'GUADALUPE',4160,'416047\r'),(1177,52411,'LINARES',5225,'522501\r'),(1178,68867,'VETAS',6805,'680531\r'),(1179,66383,'LA CELIA',6620,'662030\r'),(1180,25126,'CAJICA',2502,'250247\r'),(1181,19698,'SANTANDER DE QUILICHAO',1910,'191039\r'),(1182,13580,'REGIDOR',1335,'133567\r'),(1183,52356,'IPIALES',5240,'524060\r'),(1184,25245,'EL COLEGIO',2526,'252630\r'),(1185,19100,'BOLIVAR',1950,'195009\r'),(1186,41483,'NATAGA',4150,'415027\r'),(1187,13001,'CARTAGENA',1300,'130012\r'),(1188,41872,'VILLAVIEJA',4110,'411028\r'),(1189,15790,'TASCO',1516,'151660\r'),(1190,54743,'SILOS',5440,'544058\r'),(1191,13001,'CARTAGENA',1300,'130013\r'),(1192,52560,'POTOSI',5240,'524030\r'),(1193,20228,'CURUMANI',2030,'203060\r'),(1194,15879,'VIRACACHA',1534,'153457\r'),(1195,47551,'PIVIJAY',4770,'477050\r'),(1196,52001,'PASTO',5200,'520019\r'),(1197,25658,'SAN FRANCISCO',2536,'253608\r'),(1198,20178,'CHIRIGUANA',2030,'203048\r'),(1199,5031,'AMALFI',528,'052847\r'),(1200,76147,'CARTAGO',7620,'762021\r'),(1201,27425,'MEDIO ATRATO',2700,'270077\r'),(1202,5001,'MEDELLIN',500,'050012\r'),(1203,50590,'PUERTO RICO',5030,'503067\r'),(1204,25807,'TIBIRITA',2508,'250827\r'),(1205,5172,'CHIGORODO',574,'057418\r'),(1206,25123,'CACHIPAY',2530,'253020\r'),(1207,25001,'AGUA DE DIOS',2528,'252850\r'),(1208,50325,'MAPIRIPAN',5030,'503021\r'),(1209,5001,'MEDELLIN',500,'050010\r'),(1210,5318,'GUARNE',540,'054050\r'),(1211,52254,'EL PEÃOL',5220,'522087\r'),(1212,41503,'OPORAPA',4180,'418001\r'),(1213,44420,'LA JAGUA DEL PILAR',4450,'445040\r'),(1214,76001,'CALI',7600,'760034\r'),(1215,52720,'SAPUYES',5255,'525558\r'),(1216,25178,'CHIPAQUE',2518,'251808\r'),(1217,25862,'VERGARA',2536,'253650\r'),(1218,54498,'OCAÃA',5465,'546557\r'),(1219,70708,'SAN MARCOS',7040,'704037\r'),(1220,25530,'PARATEBUENO',2514,'251401\r'),(1221,76054,'ARGELIA',7615,'761517\r'),(1222,5380,'LA ESTRELLA',554,'055460\r'),(1223,17662,'SAMANA',1740,'174007\r'),(1224,50686,'SAN JUANITO',5010,'501057\r'),(1225,52405,'LEIVA',5270,'527068\r'),(1226,27205,'CONDOTO',2730,'273030\r'),(1227,52612,'RICAURTE',5250,'525047\r'),(1228,13001,'CARTAGENA',1300,'130018\r'),(1229,52687,'SAN LORENZO',5215,'521540\r'),(1230,25260,'EL ROSAL',2502,'250217\r'),(1231,52320,'GUAITARILLA',5255,'525501\r'),(1232,25368,'JERUSALEN',2528,'252810\r'),(1233,8520,'PALMAR DE VARELA',830,'083087\r'),(1234,52352,'ILES',5230,'523067\r'),(1235,41006,'ACEVEDO',4170,'417079\r'),(1236,5659,'SAN JUAN DE URABA',578,'057817\r'),(1237,54498,'OCAÃA',5465,'546559\r'),(1238,68077,'BARBOSA',6845,'684517\r'),(1239,47692,'SAN SEBASTIAN DE BUENAVISTA',4730,'473001\r'),(1240,52250,'EL CHARCO',5275,'527527\r'),(1241,73030,'AMBALEMA',7310,'731001\r'),(1242,47745,'SITIONUEVO',4770,'477001\r'),(1243,68081,'BARRANCABERMEJA',6870,'687047\r'),(1244,44078,'BARRANCAS',4430,'443040\r'),(1245,13001,'CARTAGENA',1300,'130019\r'),(1246,81591,'PUERTO RONDON',8130,'813010\r'),(1247,5190,'CISNEROS',530,'053050\r'),(1248,85325,'SAN LUIS DE PALENQUE',8530,'853030\r'),(1249,52699,'SANTACRUZ',5255,'525570\r'),(1250,25035,'ANAPOIMA',2526,'252648\r'),(1251,70717,'SAN PEDRO',7020,'702010\r'),(1252,99773,'CUMARIBO',9910,'991048\r'),(1253,54125,'CACOTA',5440,'544017\r'),(1254,15550,'PISBA',1518,'151807\r'),(1255,20178,'CHIRIGUANA',2030,'203047\r'),(1256,18029,'ALBANIA',1860,'186038\r'),(1257,5615,'RIONEGRO',540,'054048\r'),(1258,86760,'SANTIAGO',8610,'861060\r'),(1259,5579,'PUERTO BERRIO',534,'053427\r'),(1260,47460,'NUEVA GRANADA',4750,'475027\r'),(1261,70473,'MORROA',7010,'701077\r'),(1262,11001,'LOCALIDAD USME',1105,'110571\r'),(1263,41551,'PITALITO',4170,'417030\r'),(1264,27810,'UNION PANAMERICANA',2720,'272037\r'),(1265,5674,'SAN VICENTE',540,'054017\r'),(1266,5040,'ANORI',528,'052857\r'),(1267,5313,'GRANADA',544,'054410\r'),(1268,13620,'SAN CRISTOBAL',1315,'131520\r'),(1269,8436,'MANATI',850,'085020\r'),(1270,19001,'POPAYAN',1900,'190002\r'),(1271,13683,'SANTA ROSA',1305,'130527\r'),(1272,11001,'LOCALIDAD SAN CRISTOBAL',1104,'110421\r'),(1273,5310,'GOMEZ PLATA',518,'051837\r'),(1274,47030,'ALGARROBO',4720,'472040\r'),(1275,91536,'PUERTO ARICA',9120,'912010\r'),(1276,50001,'VILLAVICENCIO',5000,'500017\r'),(1277,76122,'CAICEDONIA',7625,'762547\r'),(1278,73152,'CASABIANCA',7315,'731528\r'),(1279,50150,'CASTILLA LA NUEVA',5070,'507047\r'),(1280,20060,'BOSCONIA',2010,'201027\r'),(1281,25736,'SESQUILE',2510,'251057\r'),(1282,88001,'SAN ANDRES',8800,'880001\r'),(1283,44430,'MAICAO',4420,'442008\r'),(1284,5079,'BARBOSA',510,'051027\r'),(1285,76109,'BUENAVENTURA',7645,'764509\r'),(1286,54223,'CUCUTILLA',5445,'544527\r'),(1287,44378,'HATONUEVO',4430,'443028\r'),(1288,5368,'JERICO',560,'056017\r'),(1289,13188,'CICUCO',1325,'132557\r'),(1290,73585,'PURIFICACION',7345,'734509\r'),(1291,25817,'TOCANCIPA',2510,'251010\r'),(1292,52001,'PASTO',5200,'520010\r'),(1293,13810,'TIQUISIO',1340,'134047\r'),(1294,17495,'NORCASIA',1750,'175001\r'),(1295,5854,'VALDIVIA',520,'052018\r'),(1296,73268,'ESPINAL',7335,'733529\r'),(1297,23815,'TUCHIN',2320,'232020\r'),(1298,25781,'SUTATAUSA',2504,'250440\r'),(1299,68547,'PIEDECUESTA',6810,'681017\r'),(1300,68773,'SUCRE',6850,'685047\r'),(1301,52835,'SAN ANDRES DE TUMACO',5285,'528509\r'),(1302,17614,'RIOSUCIO',1780,'178049\r'),(1303,66001,'PEREIRA',6600,'660006\r'),(1304,18247,'EL DONCELLO',1810,'181017\r'),(1305,5030,'AMAGA',558,'055848\r'),(1306,54001,'CUCUTA',5400,'540001\r'),(1307,52256,'EL ROSARIO',5270,'527039\r'),(1308,52835,'SAN ANDRES DE TUMACO',5285,'528502\r'),(1309,50001,'VILLAVICENCIO',5000,'500005\r'),(1310,68655,'SABANA DE TORRES',6870,'687001\r'),(1311,19473,'MORALES',1905,'190558\r'),(1312,50223,'CUBARRAL',5060,'506007\r'),(1313,11001,'LOCALIDAD FONTIBON',1109,'110921\r'),(1314,52621,'ROBERTO PAYAN',5280,'528047\r'),(1315,44847,'URIBIA',4410,'441057\r'),(1316,11001,'LOCALIDAD CHAPINERO',1102,'110221\r'),(1317,81300,'FORTUL',8140,'814050\r'),(1318,41791,'TARQUI',4160,'416007\r'),(1319,25307,'GIRARDOT',2524,'252431\r'),(1320,19075,'BALBOA',1955,'195547\r'),(1321,23678,'SAN CARLOS',2325,'232507\r'),(1322,25430,'MADRID',2500,'250037\r'),(1323,94884,'PUERTO COLOMBIA',9410,'941038\r'),(1324,68167,'CHARALA',6825,'682559\r'),(1325,25290,'FUSAGASUGA',2522,'252217\r'),(1326,5475,'MURINDO',568,'056817\r'),(1327,68121,'CABRERA',6835,'683501\r'),(1328,15494,'NUEVO COLON',1536,'153620\r'),(1329,15723,'SATIVASUR',1508,'150807\r'),(1330,27025,'ALTO BAUDO',2760,'276070\r'),(1331,23419,'LOS CORDOBAS',2350,'235027\r'),(1332,15790,'TASCO',1516,'151667\r'),(1333,76248,'EL CERRITO',7635,'763527\r'),(1334,52079,'BARBACOAS',5280,'528069\r'),(1335,19845,'VILLA RICA',1910,'191067\r'),(1336,73217,'COYAIMA',7350,'735029\r'),(1337,13468,'MOMPOS',1325,'132567\r'),(1338,68500,'OIBA',6830,'683029\r'),(1339,13074,'BARRANCO DE LOBA',1335,'133518\r'),(1340,15469,'MONIQUIRA',1542,'154268\r'),(1341,13873,'VILLANUEVA',1305,'130530\r'),(1342,19212,'CORINTO',1915,'191568\r'),(1343,19809,'TIMBIQUI',1960,'196037\r'),(1344,70124,'CAIMITO',7040,'704010\r'),(1345,5266,'ENVIGADO',554,'055422\r'),(1346,8436,'MANATI',850,'085027\r'),(1347,76563,'PRADERA',7635,'763557\r'),(1348,5101,'CIUDAD BOLIVAR',564,'056467\r'),(1349,91669,'PUERTO SANTANDER',9150,'915018\r'),(1350,25596,'QUIPILE',2530,'253030\r'),(1351,19022,'ALMAGUER',1940,'194089\r'),(1352,25612,'RICAURTE',2524,'252410\r'),(1353,66318,'GUATICA',6640,'664017\r'),(1354,41551,'PITALITO',4170,'417037\r'),(1355,5266,'ENVIGADO',554,'055421\r'),(1356,44847,'URIBIA',4410,'441058\r'),(1357,68296,'GALAN',6840,'684057\r'),(1358,15226,'CUITIVA',1522,'152237\r'),(1359,76041,'ANSERMANUEVO',7620,'762018\r'),(1360,17042,'ANSERMA',1770,'177089\r'),(1361,68444,'MATANZA',6805,'680568\r'),(1362,25269,'FACATATIVA',2530,'253052\r'),(1363,5001,'MEDELLIN',500,'050007\r'),(1364,5150,'CAROLINA',518,'051847\r'),(1365,50313,'GRANADA',5040,'504007\r'),(1366,11001,'LOCALIDAD BOSA',1107,'110741\r'),(1367,27491,'NOVITA',2730,'273057\r'),(1368,52835,'SAN ANDRES DE TUMACO',5285,'528518\r'),(1369,23001,'MONTERIA',2300,'230028\r'),(1370,27150,'CARMEN DEL DARIEN',2770,'277038\r'),(1371,70204,'COLOSO',7070,'707037\r'),(1372,15131,'CALDAS',1546,'154667\r'),(1373,85250,'PAZ DE ARIPORO',8520,'852030\r'),(1374,76233,'DAGUA',7605,'760527\r'),(1375,8001,'BARRANQUILLA',800,'080001\r'),(1376,52838,'TUQUERRES',5255,'525520\r'),(1377,41396,'LA PLATA',4150,'415060\r'),(1378,54223,'CUCUTILLA',5445,'544520\r'),(1379,63001,'ARMENIA',6300,'630002\r'),(1380,25299,'GAMA',2512,'251240\r'),(1381,25843,'VILLA DE SAN DIEGO DE UBATE',2504,'250437\r'),(1382,52788,'TANGUA',5235,'523501\r'),(1383,5809,'TITIRIBI',558,'055858\r'),(1384,15162,'CERINZA',1506,'150627\r'),(1385,20250,'EL PASO',2010,'201030\r'),(1386,76130,'CANDELARIA',7635,'763578\r'),(1387,44847,'URIBIA',4410,'441027\r'),(1388,70708,'SAN MARCOS',7040,'704038\r'),(1389,52001,'PASTO',5200,'520029\r'),(1390,66045,'APIA',6630,'663038\r'),(1391,15842,'UMBITA',1532,'153248\r'),(1392,54001,'CUCUTA',5400,'540011\r'),(1393,5576,'PUEBLORRICO',564,'056447\r'),(1394,5495,'NECHI',524,'052428\r'),(1395,18753,'SAN VICENTE DEL CAGUAN',1820,'182017\r'),(1396,54820,'TOLEDO',5420,'542038\r'),(1397,86760,'SANTIAGO',8610,'861067\r'),(1398,94001,'INIRIDA',9400,'940028\r'),(1399,15176,'CHIQUINQUIRA',1546,'154640\r'),(1400,73624,'ROVIRA',7330,'733048\r'),(1401,68679,'SAN GIL',6840,'684037\r'),(1402,76863,'VERSALLES',7615,'761530\r'),(1403,68167,'CHARALA',6825,'682557\r'),(1404,94884,'PUERTO COLOMBIA',9410,'941047\r'),(1405,25743,'SILVANIA',2522,'252240\r'),(1406,50251,'EL CASTILLO',5060,'506048\r'),(1407,54553,'PUERTO SANTANDER',5480,'548030\r'),(1408,70233,'EL ROBLE',7050,'705058\r'),(1409,97511,'PACOA',9720,'972007\r'),(1410,11001,'LOCALIDAD CIUDAD BOLIVAR',1119,'111911\r'),(1411,23001,'MONTERIA',2300,'230019\r'),(1412,5854,'VALDIVIA',520,'052017\r'),(1413,66001,'PEREIRA',6600,'660008\r'),(1414,25841,'UBAQUE',2516,'251607\r'),(1415,15494,'NUEVO COLON',1536,'153627\r'),(1416,66594,'QUINCHIA',6640,'664008\r'),(1417,63001,'ARMENIA',6300,'630007\r'),(1418,5376,'LA CEJA',550,'055018\r'),(1419,15810,'TIPACOQUE',1510,'151027\r'),(1420,54874,'VILLA DEL ROSARIO',5410,'541030\r'),(1421,68211,'CONTRATACION',6830,'683077\r'),(1422,73124,'CAJAMARCA',7325,'732507\r'),(1423,54051,'ARBOLEDAS',5445,'544557\r'),(1424,15759,'SOGAMOSO',1522,'152217\r'),(1425,17665,'SAN JOSE',1770,'177040\r'),(1426,18479,'MORELIA',1850,'185017\r'),(1427,54250,'EL TARRA',5480,'548057\r'),(1428,68615,'RIONEGRO',6875,'687511\r'),(1429,13244,'EL CARMEN DE BOLIVAR',1320,'132050\r'),(1430,66088,'BELEN DE UMBRIA',6640,'664047\r'),(1431,85250,'PAZ DE ARIPORO',8520,'852037\r'),(1432,68780,'SURATA',6805,'680501\r'),(1433,68322,'GUAPOTA',6830,'683017\r'),(1434,23068,'AYAPEL',2335,'233530\r'),(1435,19110,'BUENOS AIRES',1910,'191007\r'),(1436,15755,'SOCOTA',1516,'151627\r'),(1437,44560,'MANAURE',4410,'441018\r'),(1438,81794,'TAME',8140,'814017\r'),(1439,15325,'GUAYATA',1530,'153047\r'),(1440,5001,'MEDELLIN',500,'050041\r'),(1441,47001,'SANTA MARTA',4700,'470002\r'),(1442,68013,'AGUADA',6855,'685521\r'),(1443,85001,'YOPAL',8500,'850007\r'),(1444,99524,'LA PRIMAVERA',9920,'992017\r'),(1445,18094,'BELEN DE LOS ANDAQUIES',1860,'186010\r'),(1446,19318,'GUAPI',1960,'196008\r'),(1447,5376,'LA CEJA',550,'055010\r'),(1448,5250,'EL BAGRE',524,'052438\r'),(1449,20614,'RIO DE ORO',2050,'205040\r'),(1450,5001,'MEDELLIN',500,'050029\r'),(1451,76834,'TULUA',7630,'763021\r'),(1452,15172,'CHINAVITA',1532,'153280\r'),(1453,15798,'TENZA',1532,'153201\r'),(1454,27425,'MEDIO ATRATO',2700,'270078\r'),(1455,5051,'ARBOLETES',578,'057827\r'),(1456,19693,'SAN SEBASTIAN',1945,'194501\r'),(1457,18029,'ALBANIA',1860,'186030\r'),(1458,70678,'SAN BENITO ABAD',7030,'703010\r'),(1459,25662,'SAN JUAN DE RIO SECO',2532,'253250\r'),(1460,44098,'DISTRACCION',4440,'444007\r'),(1461,68573,'PUERTO PARRA',6860,'686001\r'),(1462,41020,'ALGECIRAS',4130,'413040\r'),(1463,76001,'CALI',7600,'760014\r'),(1464,25402,'LA VEGA',2536,'253618\r'),(1465,27150,'CARMEN DEL DARIEN',2770,'277037\r'),(1466,25875,'VILLETA',2534,'253410\r'),(1467,73168,'CHAPARRAL',7355,'735560\r'),(1468,23189,'CIENAGA DE ORO',2325,'232527\r'),(1469,13442,'MARIA LA BAJA',1310,'131068\r'),(1470,15693,'SANTA ROSA DE VITERBO',1504,'150480\r'),(1471,68162,'CERRITO',6815,'681507\r'),(1472,41548,'PITAL',4140,'414060\r'),(1473,11001,'LOCALIDAD TEUSAQUILLO',1113,'111311\r'),(1474,25214,'COTA',2500,'250010\r'),(1475,73622,'RONCESVALLES',7355,'735550\r'),(1476,19110,'BUENOS AIRES',1910,'191017\r'),(1477,17446,'MARULANDA',1730,'173001\r'),(1478,95200,'MIRAFLORES',9520,'952017\r'),(1479,54003,'ABREGO',5460,'546079\r'),(1480,5847,'URRAO',568,'056830\r'),(1481,68235,'EL CARMEN DE CHUCURI',6865,'686567\r'),(1482,68051,'ARATOCA',6820,'682058\r'),(1483,73411,'LIBANO',7310,'731047\r'),(1484,52240,'CHACHAGÃI',5220,'522007\r'),(1485,50251,'EL CASTILLO',5060,'506041\r'),(1486,5088,'BELLO',510,'051059\r'),(1487,15514,'PAEZ',1526,'152627\r'),(1488,41206,'COLOMBIA',4110,'411080\r'),(1489,23580,'PUERTO LIBERTADOR',2340,'234038\r'),(1490,73067,'ATACO',7350,'735057\r'),(1491,5631,'SABANETA',554,'055450\r'),(1492,23464,'MOMIL',2320,'232008\r'),(1493,25489,'NIMAIMA',2536,'253637\r'),(1494,63212,'CORDOBA',6320,'632020\r'),(1495,70670,'SAMPUES',7050,'705079\r'),(1496,66572,'PUEBLO RICO',6630,'663017\r'),(1497,52786,'TAMINANGO',5215,'521560\r'),(1498,13442,'MARIA LA BAJA',1310,'131069\r'),(1499,5856,'VALPARAISO',560,'056037\r'),(1500,41244,'ELIAS',4170,'417007\r'),(1501,54344,'HACARI',5465,'546510\r'),(1502,15804,'TIBANA',1532,'153268\r'),(1503,25269,'FACATATIVA',2530,'253057\r'),(1504,15600,'RAQUIRA',1538,'153809\r'),(1505,8638,'SABANALARGA',850,'085007\r'),(1506,52215,'CORDOBA',5240,'524007\r'),(1507,54001,'CUCUTA',5400,'540005\r'),(1508,81794,'TAME',8140,'814010\r'),(1509,70820,'SANTIAGO DE TOLU',7060,'706018\r'),(1510,11001,'LOCALIDAD ENGATIVA',1110,'111011\r'),(1511,85010,'AGUAZUL',8560,'856010\r'),(1512,47545,'PIJIÃO DEL CARMEN',4740,'474040\r'),(1513,85162,'MONTERREY',8550,'855018\r'),(1514,15837,'TUTA',1504,'150408\r'),(1515,23675,'SAN BERNARDO DEL VIENTO',2315,'231507\r'),(1516,70230,'CHALAN',7010,'701017\r'),(1517,20550,'PELAYA',2040,'204047\r'),(1518,15842,'UMBITA',1532,'153247\r'),(1519,15720,'SATIVANORTE',1508,'150827\r'),(1520,54099,'BOCHALEMA',5430,'543010\r'),(1521,23090,'CANALETE',2350,'235048\r'),(1522,23079,'BUENAVISTA',2330,'233028\r'),(1523,41001,'NEIVA',4100,'410010\r'),(1524,25290,'FUSAGASUGA',2522,'252211\r'),(1525,76001,'CALI',7600,'760004\r'),(1526,15778,'SUTATENZA',1530,'153067\r'),(1527,17486,'NEIRA',1710,'171008\r'),(1528,68705,'SANTA BARBARA',6810,'681038\r'),(1529,15332,'GÃICAN',1514,'151447\r'),(1530,73268,'ESPINAL',7335,'733520\r'),(1531,11001,'LOCALIDAD SUMAPAZ',1120,'112011\r'),(1532,11001,'LOCALIDAD KENNEDY',1108,'110881\r'),(1533,76001,'CALI',7600,'760003\r'),(1534,5440,'MARINILLA',540,'054028\r'),(1535,66001,'PEREIRA',6600,'660017\r'),(1536,13468,'MOMPOS',1325,'132560\r'),(1537,76001,'CALI',7600,'760044\r'),(1538,15001,'TUNJA',1500,'150008\r'),(1539,50683,'SAN JUAN DE ARAMA',5040,'504048\r'),(1540,19532,'PATIA',1955,'195507\r'),(1541,8770,'SUAN',840,'084060\r'),(1542,76001,'CALI',7600,'760041\r'),(1543,25269,'FACATATIVA',2530,'253051\r'),(1544,5310,'GOMEZ PLATA',518,'051838\r'),(1545,25718,'SASAIMA',2534,'253407\r'),(1546,99524,'LA PRIMAVERA',9920,'992018\r'),(1547,52390,'LA TOLA',5275,'527540\r'),(1548,5001,'MEDELLIN',500,'050023\r'),(1549,99773,'CUMARIBO',9910,'991009\r'),(1550,52233,'CUMBITARA',5265,'526568\r'),(1551,68855,'VALLE DE SAN JOSE',6825,'682577\r'),(1552,86749,'SIBUNDOY',8610,'861020\r'),(1553,52480,'NARIÃO',5220,'522027\r'),(1554,15455,'MIRAFLORES',1526,'152667\r'),(1555,13001,'CARTAGENA',1300,'130009\r'),(1556,25436,'MANTA',2508,'250837\r'),(1557,68745,'SIMACOTA',6835,'683569\r'),(1558,68524,'PALMAS DEL SOCORRO',6835,'683547\r'),(1559,68320,'GUADALUPE',6830,'683051\r'),(1560,66594,'QUINCHIA',6640,'664007\r'),(1561,76248,'EL CERRITO',7635,'763520\r'),(1562,25297,'GACHETA',2512,'251230\r'),(1563,27250,'EL LITORAL DEL SAN JUAN',2750,'275058\r'),(1564,68266,'ENCISO',6815,'681567\r'),(1565,5237,'DON MATIAS',518,'051850\r'),(1566,76895,'ZARZAL',7625,'762527\r'),(1567,15215,'CORRALES',1520,'152060\r'),(1568,23807,'TIERRALTA',2345,'234518\r'),(1569,70001,'SINCELEJO',7000,'700008\r'),(1570,23855,'VALENCIA',2345,'234537\r'),(1571,76823,'TORO',7615,'761520\r'),(1572,19001,'POPAYAN',1900,'190017\r'),(1573,19137,'CALDONO',1920,'192040\r'),(1574,19780,'SUAREZ',1905,'190589\r'),(1575,99624,'SANTA ROSALIA',9950,'995007\r'),(1576,68255,'EL PLAYON',6875,'687501\r'),(1577,13647,'SAN ESTANISLAO',1305,'130547\r'),(1578,15835,'TURMEQUE',1536,'153637\r'),(1579,41078,'BARAYA',4110,'411060\r'),(1580,85230,'OROCUE',8530,'853058\r'),(1581,25426,'MACHETA',2508,'250847\r'),(1582,25281,'FOSCA',2518,'251837\r'),(1583,19418,'LOPEZ',1960,'196060\r'),(1584,25181,'CHOACHI',2516,'251627\r'),(1585,41660,'SALADOBLANCO',4180,'418028\r'),(1586,15632,'SABOYA',1546,'154601\r'),(1587,27250,'EL LITORAL DEL SAN JUAN',2750,'275057\r'),(1588,15223,'CUBARA',1514,'151427\r'),(1589,25386,'LA MESA',2526,'252607\r'),(1590,15740,'SIACHOQUE',1534,'153467\r'),(1591,23168,'CHIMA',2320,'232017\r'),(1592,52001,'PASTO',5200,'520002\r'),(1593,19397,'LA VEGA',1940,'194027\r'),(1594,15686,'SANTANA',1544,'154447\r'),(1595,25777,'SUPATA',2536,'253667\r'),(1596,23807,'TIERRALTA',2345,'234501\r'),(1597,25758,'SOPO',2510,'251001\r'),(1598,73461,'MURILLO',7310,'731067\r'),(1599,73675,'SAN ANTONIO',7355,'735537\r'),(1600,23464,'MOMIL',2320,'232001\r'),(1601,23417,'LORICA',2310,'231027\r'),(1602,11001,'LOCALIDAD ENGATIVA',1110,'111061\r'),(1603,73001,'IBAGUE',7300,'730009\r'),(1604,73226,'CUNDAY',7340,'734040\r'),(1605,52356,'IPIALES',5240,'524077\r'),(1606,91263,'EL ENCANTO',9130,'913017\r'),(1607,50245,'EL CALVARIO',5010,'501041\r'),(1608,27075,'BAHIA SOLANO',2760,'276037\r'),(1609,52001,'PASTO',5200,'520039\r'),(1610,76364,'JAMUNDI',7640,'764001\r'),(1611,15189,'CIENEGA',1534,'153447\r'),(1612,68101,'BOLIVAR',6850,'685008\r'),(1613,23574,'PUERTO ESCONDIDO',2350,'235008\r'),(1614,41298,'GARZON',4140,'414028\r'),(1615,52427,'MAGÃI',5280,'528007\r'),(1616,20787,'TAMALAMEQUE',2040,'204020\r'),(1617,47720,'SANTA BARBARA DE PINTO',4740,'474007\r'),(1618,13688,'SANTA ROSA DEL SUR',1350,'135008\r'),(1619,25279,'FOMEQUE',2516,'251648\r'),(1620,15001,'TUNJA',1500,'150009\r'),(1621,20001,'VALLEDUPAR',2000,'200017\r'),(1622,11001,'LOCALIDAD CIUDAD BOLIVAR',1119,'111921\r'),(1623,86571,'PUERTO GUZMAN',8630,'863008\r'),(1624,54720,'SARDINATA',5455,'545530\r'),(1625,25312,'GRANADA',2522,'252257\r'),(1626,52385,'LA LLANADA',5265,'526507\r'),(1627,5665,'SAN PEDRO DE URABA',578,'057839\r'),(1628,5631,'SABANETA',554,'055457\r'),(1629,99773,'CUMARIBO',9910,'991001\r'),(1630,47541,'PEDRAZA',4760,'476047\r'),(1631,5142,'CARACOLI',534,'053450\r'),(1632,13654,'SAN JACINTO',1320,'132030\r'),(1633,68307,'GIRON',6875,'687548\r'),(1634,19212,'CORINTO',1915,'191567\r'),(1635,15442,'MARIPI',1548,'154820\r'),(1636,68573,'PUERTO PARRA',6860,'686008\r'),(1637,17777,'SUPIA',1780,'178020\r'),(1638,27006,'ACANDI',2780,'278010\r'),(1639,54001,'CUCUTA',5400,'540002\r'),(1640,8606,'REPELON',850,'085048\r'),(1641,76377,'LA CUMBRE',7605,'760517\r'),(1642,5576,'PUEBLORRICO',564,'056440\r'),(1643,15001,'TUNJA',1500,'150002\r'),(1644,19513,'PADILLA',1915,'191540\r'),(1645,19698,'SANTANDER DE QUILICHAO',1910,'191038\r'),(1646,70265,'GUARANDA',7030,'703078\r'),(1647,68533,'PARAMO',6835,'683527\r'),(1648,5001,'MEDELLIN',500,'050037\r'),(1649,17513,'PACORA',1720,'172047\r'),(1650,17513,'PACORA',1720,'172040\r'),(1651,54223,'CUCUTILLA',5445,'544528\r'),(1652,23090,'CANALETE',2350,'235040\r'),(1653,52356,'IPIALES',5240,'524078\r'),(1654,19100,'BOLIVAR',1950,'195019\r'),(1655,11001,'LOCALIDAD ENGATIVA',1110,'111031\r'),(1656,50689,'SAN MARTIN',5070,'507037\r'),(1657,25290,'FUSAGASUGA',2522,'252219\r'),(1658,52320,'GUAITARILLA',5255,'525507\r'),(1659,25718,'SASAIMA',2534,'253401\r'),(1660,5310,'GOMEZ PLATA',518,'051830\r'),(1661,17614,'RIOSUCIO',1780,'178048\r'),(1662,76001,'CALI',7600,'760020\r'),(1663,15638,'SACHICA',1538,'153887\r'),(1664,52224,'CUASPUD',5245,'524567\r'),(1665,19760,'SOTARA',1935,'193508\r'),(1666,54720,'SARDINATA',5455,'545539\r'),(1667,13244,'EL CARMEN DE BOLIVAR',1320,'132058\r'),(1668,41396,'LA PLATA',4150,'415078\r'),(1669,66456,'MISTRATO',6640,'664020\r'),(1670,19548,'PIENDAMO',1905,'190539\r'),(1671,44855,'URUMITA',4450,'445027\r'),(1672,25377,'LA CALERA',2512,'251201\r'),(1673,76111,'GUADALAJARA DE BUGA',7630,'763049\r'),(1674,5030,'AMAGA',558,'055847\r'),(1675,73352,'ICONONZO',7340,'734020\r'),(1676,66682,'SANTA ROSA DE CABAL',6610,'661027\r'),(1677,8141,'CANDELARIA',840,'084027\r'),(1678,18610,'SAN JOSE DEL FRAGUA',1860,'186077\r'),(1679,66456,'MISTRATO',6640,'664027\r'),(1680,19698,'SANTANDER DE QUILICHAO',1910,'191037\r'),(1681,73678,'SAN LUIS',7330,'733001\r'),(1682,50606,'RESTREPO',5010,'501038\r'),(1683,19001,'POPAYAN',1900,'190004\r'),(1684,66594,'QUINCHIA',6640,'664001\r'),(1685,54174,'CHITAGA',5440,'544037\r'),(1686,70820,'SANTIAGO DE TOLU',7060,'706017\r'),(1687,5001,'MEDELLIN',500,'050033\r'),(1688,70702,'SAN JUAN DE BETULIA',7050,'705017\r'),(1689,5059,'ARMENIA',558,'055860\r'),(1690,15822,'TOTA',1524,'152448\r'),(1691,85001,'YOPAL',8500,'850008\r'),(1692,13688,'SANTA ROSA DEL SUR',1350,'135007\r'),(1693,41357,'IQUIRA',4120,'412060\r'),(1694,44098,'DISTRACCION',4440,'444001\r'),(1695,70215,'COROZAL',7050,'705038\r'),(1696,17050,'ARANZAZU',1710,'171047\r'),(1697,73585,'PURIFICACION',7345,'734507\r'),(1698,15531,'PAUNA',1548,'154801\r'),(1699,85139,'MANI',8540,'854019\r'),(1700,73873,'VILLARRICA',7340,'734067\r'),(1701,5197,'COCORNA',544,'054440\r'),(1702,68745,'SIMACOTA',6835,'683567\r'),(1703,19001,'POPAYAN',1900,'190001\r'),(1704,81220,'CRAVO NORTE',8120,'812017\r'),(1705,99524,'LA PRIMAVERA',9920,'992007\r'),(1706,86001,'MOCOA',8600,'860001\r'),(1707,68264,'ENCINO',6825,'682549\r'),(1708,68255,'EL PLAYON',6875,'687507\r'),(1709,15810,'TIPACOQUE',1510,'151020\r'),(1710,50568,'PUERTO GAITAN',5020,'502047\r'),(1711,76563,'PRADERA',7635,'763550\r'),(1712,73483,'NATAGAIMA',7350,'735008\r'),(1713,5670,'SAN ROQUE',530,'053037\r'),(1714,15296,'GAMEZA',1520,'152020\r'),(1715,25288,'FUQUENE',2506,'250620\r'),(1716,25299,'GAMA',2512,'251247\r'),(1717,52227,'CUMBAL',5250,'525008\r'),(1718,5240,'EBEJICO',558,'055817\r'),(1719,13490,'NOROSI',1345,'134510\r'),(1720,70230,'CHALAN',7010,'701010\r'),(1721,17867,'VICTORIA',1740,'174037\r'),(1722,27580,'RIO IRO',2730,'273010\r'),(1723,19355,'INZA',1925,'192547\r'),(1724,68162,'CERRITO',6815,'681508\r'),(1725,70702,'SAN JUAN DE BETULIA',7050,'705010\r'),(1726,94001,'INIRIDA',9400,'940019\r'),(1727,73678,'SAN LUIS',7330,'733008\r'),(1728,15322,'GUATEQUE',1530,'153050\r'),(1729,52022,'ALDANA',5245,'524547\r'),(1730,5001,'MEDELLIN',500,'050013\r'),(1731,15238,'DUITAMA',1504,'150467\r'),(1732,44855,'URUMITA',4450,'445020\r'),(1733,52227,'CUMBAL',5250,'525009\r'),(1734,27150,'CARMEN DEL DARIEN',2770,'277030\r'),(1735,15176,'CHIQUINQUIRA',1546,'154647\r'),(1736,25473,'MOSQUERA',2500,'250047\r'),(1737,73563,'PRADO',7345,'734528\r'),(1738,52110,'BUESACO',5205,'520501\r'),(1739,86865,'VALLE DEL GUAMUEZ',8620,'862020\r'),(1740,66045,'APIA',6630,'663030\r'),(1741,52573,'PUERRES',5235,'523547\r'),(1742,70523,'PALMITO',7060,'706030\r'),(1743,91798,'TARAPACA',9110,'911038\r'),(1744,20175,'CHIMICHAGUA',2010,'201050\r'),(1745,13052,'ARJONA',1310,'131020\r'),(1746,17665,'SAN JOSE',1770,'177047\r'),(1747,25335,'GUAYABETAL',2518,'251857\r'),(1748,23686,'SAN PELAYO',2305,'230529\r'),(1749,63401,'LA TEBAIDA',6330,'633020\r'),(1750,68547,'PIEDECUESTA',6810,'681018\r'),(1751,50006,'ACACIAS',5070,'507007\r'),(1752,44560,'MANAURE',4410,'441007\r'),(1753,68686,'SAN MIGUEL',6815,'681551\r'),(1754,27205,'CONDOTO',2730,'273038\r'),(1755,5660,'SAN LUIS',544,'054430\r'),(1756,86571,'PUERTO GUZMAN',8630,'863001\r'),(1757,52390,'LA TOLA',5275,'527548\r'),(1758,41016,'AIPE',4110,'411001\r'),(1759,13074,'BARRANCO DE LOBA',1335,'133510\r'),(1760,94001,'INIRIDA',9400,'940009\r'),(1761,25126,'CAJICA',2502,'250240\r'),(1762,25154,'CARMEN DE CARUPA',2504,'250428\r'),(1763,25754,'SOACHA',2500,'250058\r'),(1764,73030,'AMBALEMA',7310,'731007\r'),(1765,73236,'DOLORES',7345,'734547\r'),(1766,15572,'PUERTO BOYACA',1552,'155217\r'),(1767,76520,'PALMIRA',7635,'763538\r'),(1768,5483,'NARIÃO',548,'054847\r'),(1769,5792,'TARSO',564,'056430\r'),(1770,41306,'GIGANTE',4140,'414001\r'),(1771,25019,'ALBAN',2532,'253207\r'),(1772,19130,'CAJIBIO',1905,'190507\r'),(1773,27615,'RIOSUCIO',2780,'278057\r'),(1774,85315,'SACAMA',8510,'851038\r'),(1775,17495,'NORCASIA',1750,'175007\r'),(1776,52256,'EL ROSARIO',5270,'527030\r'),(1777,85015,'CHAMEZA',8560,'856038\r'),(1778,27450,'MEDIO SAN JUAN',2740,'274037\r'),(1779,52001,'PASTO',5200,'520028\r'),(1780,41359,'ISNOS',4180,'418040\r'),(1781,54874,'VILLA DEL ROSARIO',5410,'541038\r'),(1782,91407,'LA PEDRERA',9170,'917017\r'),(1783,25781,'SUTATAUSA',2504,'250447\r'),(1784,17524,'PALESTINA',1760,'176047\r'),(1785,5543,'PEQUE',570,'057018\r'),(1786,54245,'EL CARMEN',5470,'547070\r'),(1787,68207,'CONCEPCION',6815,'681519\r'),(1788,73200,'COELLO',7335,'733507\r'),(1789,25535,'PASCA',2522,'252207\r'),(1790,52585,'PUPIALES',5245,'524528\r'),(1791,13268,'EL PEÃON',1335,'133550\r'),(1792,23807,'TIERRALTA',2345,'234507\r'),(1793,54313,'GRAMALOTE',5450,'545050\r'),(1794,15272,'FIRAVITOBA',1522,'152250\r'),(1795,54720,'SARDINATA',5455,'545538\r'),(1796,19392,'LA SIERRA',1940,'194008\r'),(1797,27245,'EL CARMEN DE ATRATO',2710,'271017\r'),(1798,25099,'BOJACA',2530,'253007\r'),(1799,8758,'SOLEDAD',830,'083005\r'),(1800,44078,'BARRANCAS',4430,'443047\r'),(1801,23417,'LORICA',2310,'231047\r'),(1802,13001,'CARTAGENA',1300,'130017\r'),(1803,70742,'SAN LUIS DE SINCE',7020,'702077\r'),(1804,19100,'BOLIVAR',1950,'195001\r'),(1805,27615,'RIOSUCIO',2770,'277017\r'),(1806,91536,'PUERTO ARICA',9120,'912019\r'),(1807,68689,'SAN VICENTE DE CHUCURI',6865,'686539\r'),(1808,15537,'PAZ DE RIO',1506,'150687\r'),(1809,18756,'SOLANO',1840,'184019\r'),(1810,76243,'EL AGUILA',7620,'762001\r'),(1811,13744,'SIMITI',1350,'135028\r'),(1812,11001,'LOCALIDAD ENGATIVA',1110,'111021\r'),(1813,54385,'LA ESPERANZA',5460,'546057\r'),(1814,5490,'NECOCLI',578,'057878\r'),(1815,25488,'NILO',2524,'252401\r'),(1816,19517,'PAEZ',1925,'192518\r'),(1817,68385,'LANDAZURI',6860,'686021\r'),(1818,23686,'SAN PELAYO',2305,'230537\r'),(1819,8675,'SANTA LUCIA',840,'084087\r'),(1820,15762,'SORA',1540,'154047\r'),(1821,50568,'PUERTO GAITAN',5020,'502049\r'),(1822,25754,'SOACHA',2500,'250057\r'),(1823,15368,'JERICO',1508,'150840\r'),(1824,76113,'BUGALAGRANDE',7630,'763008\r'),(1825,76377,'LA CUMBRE',7605,'760510\r'),(1826,63111,'BUENAVISTA',6320,'632040\r'),(1827,27745,'SIPI',2740,'274058\r'),(1828,41770,'SUAZA',4160,'416088\r'),(1829,5147,'CAREPA',578,'057858\r'),(1830,68209,'CONFINES',6835,'683537\r'),(1831,68872,'VILLANUEVA',6840,'684028\r'),(1832,94888,'MORICHAL',9430,'943057\r'),(1833,54377,'LABATECA',5420,'542058\r'),(1834,88564,'PROVIDENCIA',8800,'880028\r'),(1835,50590,'PUERTO RICO',5030,'503068\r'),(1836,54245,'EL CARMEN',5470,'547078\r'),(1837,5360,'ITAGÃI',554,'055411\r'),(1838,41001,'NEIVA',4100,'410006\r'),(1839,50568,'PUERTO GAITAN',5020,'502041\r'),(1840,41013,'AGRADO',4140,'414047\r'),(1841,20550,'PELAYA',2040,'204040\r'),(1842,52418,'LOS ANDES',5265,'526527\r'),(1843,44001,'RIOHACHA',4400,'440001\r'),(1844,91263,'EL ENCANTO',9130,'913019\r'),(1845,15276,'FLORESTA',1506,'150607\r'),(1846,52683,'SANDONA',5225,'522527\r'),(1847,13006,'ACHI',1340,'134020\r'),(1848,54003,'ABREGO',5460,'546078\r'),(1849,19785,'SUCRE',1940,'194067\r'),(1850,25258,'EL PEÃON',2540,'254020\r'),(1851,50124,'CABUYARO',5010,'501018\r'),(1852,27800,'UNGUIA',2780,'278038\r'),(1853,94886,'CACAHUAL',9410,'941018\r'),(1854,15646,'SAMACA',1536,'153668\r'),(1855,44847,'URIBIA',4410,'441059\r'),(1856,50325,'MAPIRIPAN',5030,'503027\r'),(1857,85430,'TRINIDAD',8530,'853017\r'),(1858,23182,'CHINU',2320,'232059\r'),(1859,52352,'ILES',5230,'523068\r'),(1860,19821,'TORIBIO',1920,'192009\r'),(1861,18150,'CARTAGENA DEL CHAIRA',1830,'183010\r'),(1862,52258,'EL TABLON DE GOMEZ',5205,'520538\r'),(1863,47541,'PEDRAZA',4760,'476040\r'),(1864,25513,'PACHO',2540,'254008\r'),(1865,15332,'GÃICAN',1514,'151448\r'),(1866,19212,'CORINTO',1915,'191569\r'),(1867,73319,'GUAMO',7335,'733540\r'),(1868,85430,'TRINIDAD',8530,'853010\r'),(1869,17380,'LA DORADA',1750,'175037\r'),(1870,25154,'CARMEN DE CARUPA',2504,'250420\r'),(1871,15759,'SOGAMOSO',1522,'152218\r'),(1872,86568,'PUERTO ASIS',8620,'862069\r'),(1873,41378,'LA ARGENTINA',4150,'415087\r'),(1874,76243,'EL AGUILA',7620,'762008\r'),(1875,11001,'LOCALIDAD SAN CRISTOBAL',1104,'110431\r'),(1876,17001,'MANIZALES',1700,'170009\r'),(1877,20178,'CHIRIGUANA',2030,'203040\r'),(1878,13670,'SAN PABLO',1350,'135048\r'),(1879,25175,'CHIA',2500,'250002\r'),(1880,19807,'TIMBIO',1935,'193537\r'),(1881,99773,'CUMARIBO',9910,'991049\r'),(1882,15806,'TIBASOSA',1522,'152268\r'),(1883,68318,'GUACA',6810,'681031\r'),(1884,25279,'FOMEQUE',2516,'251647\r'),(1885,68217,'COROMORO',6825,'682537\r'),(1886,70820,'SANTIAGO DE TOLU',7060,'706010\r'),(1887,85139,'MANI',8540,'854010\r'),(1888,73555,'PLANADAS',7350,'735078\r'),(1889,68190,'CIMITARRA',6860,'686047\r'),(1890,50001,'VILLAVICENCIO',5000,'500007\r'),(1891,15135,'CAMPOHERMOSO',1526,'152640\r'),(1892,70742,'SAN LUIS DE SINCE',7020,'702078\r'),(1893,73504,'ORTEGA',7355,'735509\r'),(1894,52001,'PASTO',5200,'520037\r'),(1895,73622,'RONCESVALLES',7355,'735557\r'),(1896,91460,'MIRITI - PARANA',9160,'916057\r'),(1897,44560,'MANAURE',4410,'441001\r'),(1898,19698,'SANTANDER DE QUILICHAO',1910,'191030\r'),(1899,13760,'SOPLAVIENTO',1315,'131507\r'),(1900,91536,'PUERTO ARICA',9120,'912018\r'),(1901,68385,'LANDAZURI',6860,'686027\r'),(1902,25878,'VIOTA',2526,'252667\r'),(1903,17653,'SALAMINA',1720,'172007\r'),(1904,25326,'GUATAVITA',2510,'251067\r'),(1905,66075,'BALBOA',6620,'662017\r'),(1906,5001,'MEDELLIN',500,'050020\r'),(1907,68444,'MATANZA',6805,'680561\r'),(1908,73026,'ALVARADO',7305,'730527\r'),(1909,52001,'PASTO',5200,'520008\r'),(1910,76243,'EL AGUILA',7620,'762007\r'),(1911,68418,'LOS SANTOS',6840,'684007\r'),(1912,73349,'HONDA',7320,'732047\r'),(1913,19548,'PIENDAMO',1905,'190548\r'),(1914,54261,'EL ZULIA',5455,'545518\r'),(1915,11001,'LOCALIDAD BOSA',1107,'110731\r'),(1916,63130,'CALARCA',6320,'632007\r'),(1917,52418,'LOS ANDES',5265,'526529\r'),(1918,8849,'USIACURI',820,'082067\r'),(1919,76606,'RESTREPO',7605,'760547\r'),(1920,19585,'PURACE',1930,'193008\r'),(1921,54673,'SAN CAYETANO',5450,'545010\r'),(1922,5642,'SALGAR',564,'056477\r'),(1923,5467,'MONTEBELLO',550,'055048\r'),(1924,20621,'LA PAZ',2020,'202018\r'),(1925,44378,'HATONUEVO',4430,'443020\r'),(1926,52001,'PASTO',5200,'520007\r'),(1927,19100,'BOLIVAR',1950,'195018\r'),(1928,68682,'SAN JOAQUIN',6825,'682517\r'),(1929,44001,'RIOHACHA',4400,'440009\r'),(1930,23555,'PLANETA RICA',2330,'233040\r'),(1931,11001,'LOCALIDAD RAFAEL URIBE URIBE',1118,'111811\r'),(1932,25258,'EL PEÃON',2540,'254027\r'),(1933,20750,'SAN DIEGO',2020,'202030\r'),(1934,15572,'PUERTO BOYACA',1552,'155208\r'),(1935,68264,'ENCINO',6825,'682541\r'),(1936,25718,'SASAIMA',2534,'253408\r'),(1937,5113,'BURITICA',570,'057037\r'),(1938,94884,'PUERTO COLOMBIA',9410,'941039\r'),(1939,52083,'BELEN',5210,'521087\r'),(1940,25438,'MEDINA',2514,'251428\r'),(1941,15047,'AQUITANIA',1524,'152428\r'),(1942,25123,'CACHIPAY',2530,'253027\r'),(1943,23001,'MONTERIA',2300,'230007\r'),(1944,19548,'PIENDAMO',1905,'190530\r'),(1945,41001,'NEIVA',4100,'410009\r'),(1946,17442,'MARMATO',1780,'178007\r'),(1947,27077,'BAJO BAUDO',2750,'275037\r'),(1948,25807,'TIBIRITA',2508,'250820\r'),(1949,68001,'BUCARAMANGA',6800,'680003\r'),(1950,25662,'SAN JUAN DE RIO SECO',2532,'253258\r'),(1951,13670,'SAN PABLO',1350,'135040\r'),(1952,15511,'PACHAVITA',1532,'153217\r'),(1953,5858,'VEGACHI',528,'052837\r'),(1954,5001,'MEDELLIN',500,'050031\r'),(1955,63470,'MONTENEGRO',6330,'633007\r'),(1956,85279,'RECETOR',8560,'856050\r'),(1957,15664,'SAN JOSE DE PARE',1544,'154460\r'),(1958,76041,'ANSERMANUEVO',7620,'762010\r'),(1959,15774,'SUSACON',1508,'150887\r'),(1960,5001,'MEDELLIN',500,'050048\r'),(1961,13810,'TIQUISIO',1340,'134048\r'),(1962,15814,'TOCA',1502,'150260\r'),(1963,20310,'GONZALEZ',2050,'205037\r'),(1964,11001,'LOCALIDAD SUMAPAZ',1120,'112041\r'),(1965,5001,'MEDELLIN',500,'050018\r'),(1966,99773,'CUMARIBO',9910,'991047\r'),(1967,5107,'BRICEÃO',520,'052068\r'),(1968,52678,'SAMANIEGO',5260,'526047\r'),(1969,19355,'INZA',1925,'192539\r'),(1970,23068,'AYAPEL',2335,'233537\r'),(1971,13473,'MORALES',1345,'134548\r'),(1972,15476,'MOTAVITA',1540,'154087\r'),(1973,19137,'CALDONO',1920,'192049\r'),(1974,52838,'TUQUERRES',5255,'525528\r'),(1975,50370,'URIBE',5050,'505048\r'),(1976,52203,'COLON',5210,'521067\r'),(1977,41668,'SAN AGUSTIN',4180,'418069\r'),(1978,17486,'NEIRA',1710,'171001\r'),(1979,54250,'EL TARRA',5480,'548058\r'),(1980,15522,'PANQUEBA',1512,'151260\r'),(1981,15516,'PAIPA',1504,'150447\r'),(1982,5004,'ABRIAQUI',574,'057460\r'),(1983,19585,'PURACE',1930,'193007\r'),(1984,25148,'CAPARRAPI',2534,'253468\r'),(1985,25867,'VIANI',2532,'253237\r'),(1986,94884,'PUERTO COLOMBIA',9410,'941037\r'),(1987,27250,'EL LITORAL DEL SAN JUAN',2750,'275050\r'),(1988,25649,'SAN BERNARDO',2520,'252020\r'),(1989,68679,'SAN GIL',6840,'684031\r'),(1990,25394,'LA PALMA',2538,'253807\r'),(1991,68755,'SOCORRO',6835,'683558\r'),(1992,5467,'MONTEBELLO',550,'055040\r'),(1993,15518,'PAJARITO',1524,'152401\r'),(1994,68101,'BOLIVAR',6850,'685001\r'),(1995,68502,'ONZAGA',6825,'682528\r'),(1996,76563,'PRADERA',7635,'763558\r'),(1997,85300,'SABANALARGA',8550,'855058\r'),(1998,20621,'LA PAZ',2020,'202010\r'),(1999,15667,'SAN LUIS DE GACENO',1528,'152807\r'),(2000,88001,'SAN ANDRES',8800,'880008\r'),(2001,8634,'SABANAGRANDE',830,'083040\r'),(2002,15507,'OTANCHE',1550,'155067\r'),(2003,5101,'CIUDAD BOLIVAR',564,'056468\r'),(2004,54743,'SILOS',5440,'544057\r'),(2005,52001,'PASTO',5200,'520038\r'),(2006,73624,'ROVIRA',7330,'733040\r'),(2007,20787,'TAMALAMEQUE',2040,'204028\r'),(2008,5125,'CAICEDO',568,'056840\r'),(2009,73616,'RIOBLANCO',7355,'735588\r'),(2010,52079,'BARBACOAS',5280,'528067\r'),(2011,76147,'CARTAGO',7620,'762022\r'),(2012,50245,'EL CALVARIO',5010,'501047\r'),(2013,25317,'GUACHETA',2506,'250617\r'),(2014,94886,'CACAHUAL',9410,'941010\r'),(2015,41006,'ACEVEDO',4170,'417070\r'),(2016,5038,'ANGOSTURA',518,'051818\r'),(2017,25488,'NILO',2524,'252408\r'),(2018,52399,'LA UNION',5215,'521520\r'),(2019,52612,'RICAURTE',5250,'525039\r'),(2020,68077,'BARBOSA',6845,'684511\r'),(2021,41006,'ACEVEDO',4170,'417078\r'),(2022,5120,'CACERES',524,'052458\r'),(2023,15804,'TIBANA',1532,'153267\r'),(2024,54172,'CHINACOTA',5410,'541070\r'),(2025,86760,'SANTIAGO',8610,'861068\r'),(2026,13667,'SAN MARTIN DE LOBA',1335,'133538\r'),(2027,68147,'CAPITANEJO',6815,'681548\r'),(2028,68167,'CHARALA',6825,'682551\r'),(2029,15469,'MONIQUIRA',1542,'154267\r'),(2030,94888,'MORICHAL',9430,'943059\r'),(2031,68370,'JORDAN',6840,'684011\r'),(2032,68217,'COROMORO',6825,'682531\r'),(2033,66687,'SANTUARIO',6630,'663007\r'),(2034,68211,'CONTRATACION',6830,'683071\r'),(2035,25168,'CHAGUANI',2532,'253240\r'),(2036,52687,'SAN LORENZO',5215,'521547\r'),(2037,47745,'SITIONUEVO',4770,'477008\r'),(2038,41483,'NATAGA',4150,'415020\r'),(2039,19622,'ROSAS',1935,'193557\r'),(2040,8560,'PONEDERA',840,'084008\r'),(2041,68176,'CHIMA',6830,'683007\r'),(2042,18785,'SOLITA',1850,'185077\r'),(2043,68235,'EL CARMEN DE CHUCURI',6865,'686568\r'),(2044,5001,'MEDELLIN',500,'050047\r'),(2045,11001,'LOCALIDAD CANDELARIA',1117,'111711\r'),(2046,11001,'LOCALIDAD SAN CRISTOBAL',1104,'110411\r'),(2047,25322,'GUASCA',2512,'251210\r'),(2048,8001,'BARRANQUILLA',800,'080012\r'),(2049,13473,'MORALES',1345,'134547\r'),(2050,54398,'LA PLAYA',5465,'546537\r'),(2051,13244,'EL CARMEN DE BOLIVAR',1320,'132059\r'),(2052,68190,'CIMITARRA',6860,'686049\r'),(2053,85225,'NUNCHIA',8510,'851078\r'),(2054,19256,'EL TAMBO',1935,'193577\r'),(2055,17653,'SALAMINA',1720,'172009\r'),(2056,11001,'LOCALIDAD ANTONIO NARIÃO',1115,'111511\r'),(2057,52019,'SAN JOSE DE ALBAN',5205,'520557\r'),(2058,68655,'SABANA DE TORRES',6870,'687008\r'),(2059,15051,'ARCABUCO',1542,'154207\r'),(2060,15632,'SABOYA',1546,'154609\r'),(2061,52678,'SAMANIEGO',5260,'526040\r'),(2062,97001,'MITU',9700,'970007\r'),(2063,5001,'MEDELLIN',500,'050044\r'),(2064,68464,'MOGOTES',6825,'682508\r'),(2065,18205,'CURILLO',1860,'186059\r'),(2066,25769,'SUBACHOQUE',2502,'250227\r'),(2067,25845,'UNE',2518,'251817\r'),(2068,68229,'CURITI',6820,'682048\r'),(2069,15238,'DUITAMA',1504,'150461\r'),(2070,85400,'TAMARA',8510,'851057\r'),(2071,85125,'HATO COROZAL',8520,'852019\r'),(2072,50287,'FUENTE DE ORO',5040,'504021\r'),(2073,44279,'FONSECA',4440,'444018\r'),(2074,15223,'CUBARA',1514,'151420\r'),(2075,44090,'DIBULLA',4460,'446017\r'),(2076,27006,'ACANDI',2780,'278018\r'),(2077,76736,'SEVILLA',7625,'762537\r'),(2078,5250,'EL BAGRE',524,'052437\r'),(2079,41524,'PALERMO',4120,'412008\r'),(2080,23417,'LORICA',2310,'231020\r'),(2081,73026,'ALVARADO',7305,'730528\r'),(2082,5107,'BRICEÃO',520,'052067\r'),(2083,17444,'MARQUETALIA',1730,'173047\r'),(2084,50226,'CUMARAL',5010,'501027\r'),(2085,20238,'EL COPEY',2010,'201018\r'),(2086,94343,'BARRANCO MINAS',9440,'944019\r'),(2087,68673,'SAN BENITO',6855,'685531\r'),(2088,15276,'FLORESTA',1506,'150601\r'),(2089,70001,'SINCELEJO',7000,'700007\r'),(2090,13673,'SANTA CATALINA',1305,'130501\r'),(2091,70473,'MORROA',7010,'701078\r'),(2092,50325,'MAPIRIPAN',5030,'503029\r'),(2093,27073,'BAGADO',2710,'271050\r'),(2094,5001,'MEDELLIN',500,'050022\r'),(2095,5154,'CAUCASIA',524,'052418\r'),(2096,13667,'SAN MARTIN DE LOBA',1335,'133530\r'),(2097,25290,'FUSAGASUGA',2522,'252218\r'),(2098,52352,'ILES',5230,'523060\r'),(2099,76834,'TULUA',7630,'763029\r'),(2100,25736,'SESQUILE',2510,'251058\r'),(2101,76616,'RIOFRIO',7610,'761037\r'),(2102,13440,'MARGARITA',1330,'133020\r'),(2103,17541,'PENSILVANIA',1730,'173067\r'),(2104,52693,'SAN PABLO',5210,'521040\r'),(2105,41132,'CAMPOALEGRE',4130,'413028\r'),(2106,5001,'MEDELLIN',500,'050014\r'),(2107,15238,'DUITAMA',1504,'150468\r'),(2108,20045,'BECERRIL',2030,'203001\r'),(2109,20383,'LA GLORIA',2040,'204068\r'),(2110,25426,'MACHETA',2508,'250848\r'),(2111,15131,'CALDAS',1546,'154660\r'),(2112,52540,'POLICARPA',5270,'527001\r'),(2113,68327,'GÃEPSA',6855,'685547\r'),(2114,54128,'CACHIRA',5460,'546038\r'),(2115,5001,'MEDELLIN',500,'050030\r'),(2116,13780,'TALAIGUA NUEVO',1325,'132540\r'),(2117,20770,'SAN MARTIN',2050,'205050\r'),(2118,52418,'LOS ANDES',5265,'526528\r'),(2119,23660,'SAHAGUN',2325,'232559\r'),(2120,52885,'YACUANQUER',5230,'523008\r'),(2121,27001,'QUIBDO',2700,'270007\r'),(2122,76100,'BOLIVAR',7610,'761008\r'),(2123,13300,'HATILLO DE LOBA',1330,'133047\r'),(2124,13780,'TALAIGUA NUEVO',1325,'132548\r'),(2125,5893,'YONDO',534,'053417\r'),(2126,63001,'ARMENIA',6300,'630003\r'),(2127,52573,'PUERRES',5235,'523548\r'),(2128,85136,'LA SALINA',8510,'851010\r'),(2129,11001,'LOCALIDAD USAQUEN',1101,'110121\r'),(2130,18205,'CURILLO',1860,'186057\r'),(2131,68861,'VELEZ',6855,'685568\r'),(2132,68162,'CERRITO',6815,'681509\r'),(2133,76869,'VIJES',7605,'760550\r'),(2134,25394,'LA PALMA',2538,'253808\r'),(2135,99773,'CUMARIBO',9910,'991038\r'),(2136,19845,'VILLA RICA',1910,'191060\r'),(2137,25871,'VILLAGOMEZ',2540,'254030\r'),(2138,54128,'CACHIRA',5460,'546030\r'),(2139,73483,'NATAGAIMA',7350,'735001\r'),(2140,52560,'POTOSI',5240,'524037\r'),(2141,50450,'PUERTO CONCORDIA',5030,'503041\r'),(2142,54347,'HERRAN',5420,'542010\r'),(2143,13647,'SAN ESTANISLAO',1305,'130540\r'),(2144,95015,'CALAMAR',9530,'953009\r'),(2145,76520,'PALMIRA',7635,'763537\r'),(2146,27491,'NOVITA',2730,'273050\r'),(2147,68406,'LEBRIJA',6875,'687579\r'),(2148,5411,'LIBORINA',514,'051468\r'),(2149,13140,'CALAMAR',1315,'131548\r'),(2150,13001,'CARTAGENA',1300,'130001\r'),(2151,76001,'CALI',7600,'760030\r'),(2152,73547,'PIEDRAS',7305,'730501\r'),(2153,25317,'GUACHETA',2506,'250618\r'),(2154,5376,'LA CEJA',550,'055017\r'),(2155,5893,'YONDO',534,'053418\r'),(2156,85279,'RECETOR',8560,'856058\r'),(2157,19022,'ALMAGUER',1940,'194088\r'),(2158,25214,'COTA',2500,'250017\r'),(2159,76109,'BUENAVENTURA',7645,'764517\r'),(2160,63690,'SALENTO',6310,'631020\r'),(2161,54347,'HERRAN',5420,'542018\r'),(2162,52685,'SAN BERNARDO',5210,'521007\r'),(2163,27660,'SAN JOSE DEL PALMAR',2730,'273078\r'),(2164,19743,'SILVIA',1920,'192078\r'),(2165,70713,'SAN ONOFRE',7070,'707017\r'),(2166,13001,'CARTAGENA',1300,'130007\r'),(2167,52260,'EL TAMBO',5220,'522060\r'),(2168,15109,'BUENAVISTA',1548,'154840\r'),(2169,70400,'LA UNION',7040,'704057\r'),(2170,68770,'SUAITA',6830,'683048\r'),(2171,17877,'VITERBO',1770,'177020\r'),(2172,47058,'ARIGUANI',4750,'475019\r'),(2173,19693,'SAN SEBASTIAN',1945,'194509\r'),(2174,66075,'BALBOA',6620,'662010\r'),(2175,68669,'SAN ANDRES',6820,'682008\r'),(2176,15296,'GAMEZA',1520,'152027\r'),(2177,15632,'SABOYA',1546,'154608\r'),(2178,66400,'LA VIRGINIA',6620,'662007\r'),(2179,52720,'SAPUYES',5255,'525557\r'),(2180,20060,'BOSCONIA',2010,'201020\r'),(2181,52835,'SAN ANDRES DE TUMACO',5285,'528508\r'),(2182,25815,'TOCAIMA',2528,'252840\r'),(2183,52001,'PASTO',5200,'520009\r'),(2184,44090,'DIBULLA',4460,'446001\r'),(2185,68468,'MOLAGAVITA',6820,'682038\r'),(2186,66088,'BELEN DE UMBRIA',6640,'664040\r'),(2187,91263,'EL ENCANTO',9130,'913010\r'),(2188,41001,'NEIVA',4100,'410005\r'),(2189,8421,'LURUACO',850,'085068\r'),(2190,5425,'MACEO',534,'053460\r'),(2191,52254,'EL PEÃOL',5220,'522080\r'),(2192,5088,'BELLO',510,'051052\r'),(2193,54261,'EL ZULIA',5455,'545517\r'),(2194,5093,'BETULIA',568,'056867\r'),(2195,15646,'SAMACA',1536,'153660\r'),(2196,76111,'GUADALAJARA DE BUGA',7630,'763048\r'),(2197,52835,'SAN ANDRES DE TUMACO',5285,'528507\r'),(2198,54128,'CACHIRA',5460,'546037\r'),(2199,13160,'CANTAGALLO',1350,'135060\r'),(2200,76318,'GUACARI',7635,'763501\r'),(2201,15442,'MARIPI',1548,'154828\r'),(2202,23001,'MONTERIA',2300,'230018\r'),(2203,23466,'MONTELIBANO',2340,'234001\r'),(2204,50150,'CASTILLA LA NUEVA',5070,'507041\r'),(2205,13160,'CANTAGALLO',1350,'135067\r'),(2206,52378,'LA CRUZ',5210,'521020\r'),(2207,27245,'EL CARMEN DE ATRATO',2710,'271018\r'),(2208,15580,'QUIPAMA',1550,'155028\r'),(2209,17446,'MARULANDA',1730,'173007\r'),(2210,15861,'VENTAQUEMADA',1536,'153647\r'),(2211,73055,'ARMERO',7320,'732060\r'),(2212,44035,'ALBANIA',4430,'443008\r'),(2213,11001,'LOCALIDAD KENNEDY',1108,'110811\r'),(2214,70473,'MORROA',7010,'701070\r'),(2215,13430,'MAGANGUE',1325,'132518\r'),(2216,68406,'LEBRIJA',6875,'687571\r'),(2217,54051,'ARBOLEDAS',5445,'544558\r'),(2218,70001,'SINCELEJO',7000,'700002\r'),(2219,63001,'ARMENIA',6300,'630008\r'),(2220,41001,'NEIVA',4100,'410008\r'),(2221,54174,'CHITAGA',5440,'544030\r'),(2222,25053,'ARBELAEZ',2520,'252007\r'),(2223,25426,'MACHETA',2508,'250840\r'),(2224,27615,'RIOSUCIO',2780,'278050\r'),(2225,54810,'TIBU',5480,'548010\r'),(2226,5887,'YARUMAL',520,'052037\r'),(2227,19532,'PATIA',1955,'195508\r'),(2228,54518,'PAMPLONA',5430,'543050\r'),(2229,44001,'RIOHACHA',4400,'440017\r'),(2230,68147,'CAPITANEJO',6815,'681541\r'),(2231,8638,'SABANALARGA',850,'085008\r'),(2232,5361,'ITUANGO',520,'052077\r'),(2233,47460,'NUEVA GRANADA',4750,'475028\r'),(2234,19397,'LA VEGA',1940,'194029\r'),(2235,19050,'ARGELIA',1955,'195567\r'),(2236,15183,'CHITA',1516,'151601\r'),(2237,27450,'MEDIO SAN JUAN',2740,'274038\r'),(2238,54001,'CUCUTA',5400,'540013\r'),(2239,20238,'EL COPEY',2010,'201017\r'),(2240,17380,'LA DORADA',1750,'175030\r'),(2241,76111,'GUADALAJARA DE BUGA',7630,'763047\r'),(2242,23660,'SAHAGUN',2325,'232540\r'),(2243,15531,'PAUNA',1548,'154807\r'),(2244,5001,'MEDELLIN',500,'050001\r'),(2245,47960,'ZAPAYAN',4760,'476057\r'),(2246,99001,'PUERTO CARREÃO',9900,'990008\r'),(2247,73217,'COYAIMA',7350,'735027\r'),(2248,70124,'CAIMITO',7040,'704017\r'),(2249,25898,'ZIPACON',2530,'253010\r'),(2250,52207,'CONSACA',5225,'522547\r'),(2251,15516,'PAIPA',1504,'150440\r'),(2252,54385,'LA ESPERANZA',5460,'546050\r'),(2253,18460,'MILAN',1850,'185037\r'),(2254,15764,'SORACA',1534,'153487\r'),(2255,54109,'BUCARASICA',5455,'545550\r'),(2256,5001,'MEDELLIN',500,'050025\r'),(2257,18001,'FLORENCIA',1800,'180001\r'),(2258,13549,'PINILLOS',1340,'134008\r'),(2259,18860,'VALPARAISO',1850,'185057\r'),(2260,15114,'BUSBANZA',1520,'152087\r'),(2261,8001,'BARRANQUILLA',800,'080020\r'),(2262,15185,'CHITARAQUE',1544,'154428\r'),(2263,81220,'CRAVO NORTE',8120,'812010\r'),(2264,95001,'SAN JOSE DEL GUAVIARE',9500,'950007\r'),(2265,68689,'SAN VICENTE DE CHUCURI',6865,'686538\r'),(2266,68679,'SAN GIL',6840,'684039\r'),(2267,20750,'SAN DIEGO',2020,'202038\r'),(2268,5145,'CARAMANTA',560,'056047\r'),(2269,99624,'SANTA ROSALIA',9950,'995008\r'),(2270,5690,'SANTO DOMINGO',530,'053040\r'),(2271,54099,'BOCHALEMA',5430,'543018\r'),(2272,19022,'ALMAGUER',1940,'194087\r'),(2273,68418,'LOS SANTOS',6840,'684001\r'),(2274,15106,'BRICEÃO',1546,'154677\r'),(2275,25486,'NEMOCON',2510,'251030\r'),(2276,85010,'AGUAZUL',8560,'856018\r'),(2277,68682,'SAN JOAQUIN',6825,'682511\r'),(2278,54405,'LOS PATIOS',5410,'541018\r'),(2279,23570,'PUEBLO NUEVO',2330,'233007\r'),(2280,11001,'LOCALIDAD USME',1105,'110531\r'),(2281,54261,'EL ZULIA',5455,'545510\r'),(2282,5475,'MURINDO',568,'056810\r'),(2283,70215,'COROZAL',7050,'705030\r'),(2284,5368,'JERICO',560,'056010\r'),(2285,54518,'PAMPLONA',5430,'543057\r'),(2286,5038,'ANGOSTURA',518,'051817\r'),(2287,5647,'SAN ANDRES DE CUERQUIA',520,'052047\r'),(2288,5861,'VENECIA',564,'056420\r'),(2289,23464,'MOMIL',2320,'232007\r'),(2290,73001,'IBAGUE',7300,'730010\r'),(2291,5038,'ANGOSTURA',518,'051810\r'),(2292,15835,'TURMEQUE',1536,'153630\r'),(2293,5790,'TARAZA',524,'052468\r'),(2294,68276,'FLORIDABLANCA',6810,'681001\r'),(2295,13248,'EL GUAMO',1320,'132001\r'),(2296,54871,'VILLA CARO',5460,'546010\r'),(2297,47001,'SANTA MARTA',4700,'470008\r'),(2298,5495,'NECHI',524,'052427\r'),(2299,20383,'LA GLORIA',2040,'204060\r'),(2300,27001,'QUIBDO',2700,'270002\r'),(2301,8832,'TUBARA',810,'081020\r'),(2302,81065,'ARAUQUITA',8160,'816017\r'),(2303,73352,'ICONONZO',7340,'734027\r'),(2304,68770,'SUAITA',6830,'683041\r'),(2305,15861,'VENTAQUEMADA',1536,'153648\r'),(2306,91405,'LA CHORRERA',9140,'914057\r'),(2307,76863,'VERSALLES',7615,'761537\r'),(2308,54673,'SAN CAYETANO',5450,'545017\r'),(2309,5001,'MEDELLIN',500,'050017\r'),(2310,25772,'SUESCA',2510,'251048\r'),(2311,15690,'SANTA MARIA',1528,'152820\r'),(2312,23672,'SAN ANTERO',2315,'231528\r'),(2313,5736,'SEGOVIA',528,'052818\r'),(2314,23189,'CIENAGA DE ORO',2325,'232528\r'),(2315,20045,'BECERRIL',2030,'203007\r'),(2316,5315,'GUADALUPE',518,'051820\r'),(2317,18256,'EL PAUJIL',1810,'181039\r'),(2318,20001,'VALLEDUPAR',2000,'200003\r'),(2319,25649,'SAN BERNARDO',2520,'252028\r'),(2320,73270,'FALAN',7320,'732007\r'),(2321,95200,'MIRAFLORES',9520,'952007\r'),(2322,73283,'FRESNO',7315,'731560\r'),(2323,18410,'LA MONTAÃITA',1810,'181050\r'),(2324,23574,'PUERTO ESCONDIDO',2350,'235007\r'),(2325,23675,'SAN BERNARDO DEL VIENTO',2315,'231508\r'),(2326,70265,'GUARANDA',7030,'703077\r'),(2327,86755,'SAN FRANCISCO',8610,'861001\r'),(2328,97511,'PACOA',9720,'972008\r'),(2329,19256,'EL TAMBO',1935,'193597\r'),(2330,66088,'BELEN DE UMBRIA',6640,'664048\r'),(2331,11001,'LOCALIDAD ENGATIVA',1110,'111051\r'),(2332,25095,'BITUIMA',2532,'253220\r'),(2333,73226,'CUNDAY',7340,'734048\r'),(2334,41357,'IQUIRA',4120,'412067\r'),(2335,85410,'TAURAMENA',8540,'854038\r'),(2336,27495,'NUQUI',2760,'276057\r'),(2337,52385,'LA LLANADA',5265,'526501\r'),(2338,18460,'MILAN',1850,'185030\r'),(2339,52356,'IPIALES',5240,'524067\r'),(2340,5045,'APARTADO',578,'057847\r'),(2341,66400,'LA VIRGINIA',6620,'662001\r'),(2342,5604,'REMEDIOS',528,'052820\r'),(2343,52207,'CONSACA',5225,'522540\r'),(2344,68468,'MOLAGAVITA',6820,'682037\r'),(2345,25862,'VERGARA',2536,'253658\r'),(2346,13838,'TURBANA',1310,'131017\r'),(2347,50270,'EL DORADO',5060,'506021\r'),(2348,15632,'SABOYA',1546,'154617\r'),(2349,76111,'GUADALAJARA DE BUGA',7630,'763042\r'),(2350,15761,'SOMONDOCO',1530,'153030\r'),(2351,19300,'CALOTO',1910,'191087\r'),(2352,23500,'MOÃITOS',2310,'231001\r'),(2353,97001,'MITU',9700,'970001\r'),(2354,73055,'ARMERO',7320,'732068\r'),(2355,25535,'PASCA',2522,'252208\r'),(2356,18610,'SAN JOSE DEL FRAGUA',1860,'186070\r'),(2357,5113,'BURITICA',570,'057038\r'),(2358,73275,'FLANDES',7335,'733510\r'),(2359,15425,'MACANAL',1528,'152840\r'),(2360,76233,'DAGUA',7605,'760529\r'),(2361,20570,'PUEBLO BELLO',2010,'201001\r'),(2362,15600,'RAQUIRA',1538,'153807\r'),(2363,23686,'SAN PELAYO',2305,'230538\r'),(2364,8001,'BARRANQUILLA',800,'080002\r'),(2365,41548,'PITAL',4140,'414067\r'),(2366,50577,'PUERTO LLERAS',5030,'503001\r'),(2367,81065,'ARAUQUITA',8160,'816019\r'),(2368,52215,'CORDOBA',5240,'524008\r'),(2369,25438,'MEDINA',2514,'251420\r'),(2370,19001,'POPAYAN',1900,'190003\r'),(2371,5001,'MEDELLIN',500,'050011\r'),(2372,13836,'TURBACO',1310,'131007\r'),(2373,5887,'YARUMAL',520,'052038\r'),(2374,73547,'PIEDRAS',7305,'730507\r'),(2375,15367,'JENESANO',1536,'153607\r'),(2376,66682,'SANTA ROSA DE CABAL',6610,'661028\r'),(2377,94883,'SAN FELIPE',9420,'942018\r'),(2378,25260,'EL ROSAL',2502,'250210\r'),(2379,5697,'EL SANTUARIO',544,'054450\r'),(2380,85430,'TRINIDAD',8530,'853018\r'),(2381,76100,'BOLIVAR',7610,'761001\r'),(2382,15272,'FIRAVITOBA',1522,'152257\r'),(2383,54680,'SANTIAGO',5450,'545030\r'),(2384,52256,'EL ROSARIO',5270,'527038\r'),(2385,68255,'EL PLAYON',6875,'687508\r'),(2386,18150,'CARTAGENA DEL CHAIRA',1830,'183019\r'),(2387,5240,'EBEJICO',558,'055818\r'),(2388,23090,'CANALETE',2350,'235047\r'),(2389,76122,'CAICEDONIA',7625,'762540\r'),(2390,18592,'PUERTO RICO',1820,'182058\r'),(2391,8001,'BARRANQUILLA',800,'080015\r'),(2392,44420,'LA JAGUA DEL PILAR',4450,'445047\r'),(2393,73504,'ORTEGA',7355,'735517\r'),(2394,17524,'PALESTINA',1760,'176040\r'),(2395,5360,'ITAGÃI',554,'055410\r'),(2396,5615,'RIONEGRO',540,'054040\r'),(2397,15317,'GUACAMAYAS',1512,'151220\r'),(2398,68176,'CHIMA',6830,'683001\r'),(2399,15244,'EL COCUY',1512,'151280\r'),(2400,63190,'CIRCASIA',6310,'631001\r'),(2401,19355,'INZA',1925,'192531\r'),(2402,25322,'GUASCA',2512,'251218\r'),(2403,52835,'SAN ANDRES DE TUMACO',5285,'528539\r'),(2404,5138,'CAÃASGORDAS',570,'057060\r'),(2405,47053,'ARACATACA',4720,'472008\r'),(2406,44847,'URIBIA',4410,'441047\r'),(2407,23079,'BUENAVISTA',2330,'233027\r'),(2408,15293,'GACHANTIVA',1542,'154227\r'),(2409,18410,'LA MONTAÃITA',1810,'181057\r'),(2410,54800,'TEORAMA',5470,'547030\r'),(2411,5088,'BELLO',510,'051057\r'),(2412,5172,'CHIGORODO',574,'057410\r'),(2413,76834,'TULUA',7630,'763028\r'),(2414,70429,'MAJAGUAL',7030,'703050\r'),(2415,68276,'FLORIDABLANCA',6810,'681004\r'),(2416,23670,'SAN ANDRES SOTAVENTO',2320,'232038\r'),(2417,54871,'VILLA CARO',5460,'546017\r'),(2418,18785,'SOLITA',1850,'185070\r'),(2419,68207,'CONCEPCION',6815,'681511\r'),(2420,15183,'CHITA',1516,'151609\r'),(2421,25183,'CHOCONTA',2508,'250801\r'),(2422,5101,'CIUDAD BOLIVAR',564,'056460\r'),(2423,41132,'CAMPOALEGRE',4130,'413027\r'),(2424,19392,'LA SIERRA',1940,'194001\r'),(2425,52838,'TUQUERRES',5255,'525527\r'),(2426,5209,'CONCORDIA',564,'056410\r'),(2427,5172,'CHIGORODO',574,'057417\r'),(2428,20614,'RIO DE ORO',2050,'205047\r'),(2429,18860,'VALPARAISO',1850,'185058\r'),(2430,50606,'RESTREPO',5010,'501031\r'),(2431,15332,'GÃICAN',1514,'151440\r'),(2432,11001,'LOCALIDAD CIUDAD BOLIVAR',1119,'111951\r'),(2433,5134,'CAMPAMENTO',520,'052027\r'),(2434,15469,'MONIQUIRA',1542,'154260\r'),(2435,23189,'CIENAGA DE ORO',2325,'232520\r'),(2436,73283,'FRESNO',7315,'731567\r'),(2437,41306,'GIGANTE',4140,'414007\r'),(2438,19701,'SANTA ROSA',1945,'194528\r'),(2439,97777,'PAPUNAUA',9730,'973047\r'),(2440,19318,'GUAPI',1960,'196001\r'),(2441,11001,'LOCALIDAD RAFAEL URIBE URIBE',1118,'111831\r'),(2442,25736,'SESQUILE',2510,'251050\r'),(2443,15763,'SOTAQUIRA',1504,'150427\r'),(2444,68377,'LA BELLEZA',6850,'685061\r'),(2445,73504,'ORTEGA',7355,'735508\r'),(2446,25178,'CHIPAQUE',2518,'251801\r'),(2447,73067,'ATACO',7350,'735050\r'),(2448,66687,'SANTUARIO',6630,'663008\r'),(2449,19780,'SUAREZ',1905,'190588\r'),(2450,17877,'VITERBO',1770,'177027\r'),(2451,15681,'SAN PABLO DE BORBUR',1550,'155048\r'),(2452,23168,'CHIMA',2320,'232010\r'),(2453,25148,'CAPARRAPI',2534,'253467\r'),(2454,68464,'MOGOTES',6825,'682507\r'),(2455,54344,'HACARI',5465,'546517\r'),(2456,50350,'LA MACARENA',5050,'505021\r'),(2457,17616,'RISARALDA',1770,'177067\r'),(2458,19701,'SANTA ROSA',1945,'194527\r'),(2459,20295,'GAMARRA',2050,'205007\r'),(2460,11001,'LOCALIDAD BARRIOS UNIDOS',1112,'111211\r'),(2461,25572,'PUERTO SALGAR',2534,'253480\r'),(2462,5197,'COCORNA',544,'054447\r'),(2463,52001,'PASTO',5200,'520018\r'),(2464,17001,'MANIZALES',1700,'170006\r'),(2465,5607,'RETIRO',554,'055437\r'),(2466,52110,'BUESACO',5205,'520507\r'),(2467,8685,'SANTO TOMAS',830,'083060\r'),(2468,52399,'LA UNION',5215,'521528\r'),(2469,11001,'LOCALIDAD KENNEDY',1108,'110851\r'),(2470,5284,'FRONTINO',574,'057450\r'),(2471,5690,'SANTO DOMINGO',530,'053047\r'),(2472,68081,'BARRANCABERMEJA',6870,'687032\r'),(2473,25200,'COGUA',2504,'250401\r'),(2474,13620,'SAN CRISTOBAL',1315,'131527\r'),(2475,76275,'FLORIDA',7635,'763560\r'),(2476,52256,'EL ROSARIO',5270,'527037\r'),(2477,25898,'ZIPACON',2530,'253017\r'),(2478,76100,'BOLIVAR',7610,'761007\r'),(2479,19807,'TIMBIO',1935,'193520\r'),(2480,15621,'RONDON',1534,'153427\r'),(2481,13212,'CORDOBA',1325,'132501\r'),(2482,5873,'VIGIA DEL FUERTE',568,'056828\r'),(2483,5313,'GRANADA',544,'054417\r'),(2484,25372,'JUNIN',2512,'251220\r'),(2485,76828,'TRUJILLO',7610,'761020\r'),(2486,68132,'CALIFORNIA',6805,'680517\r'),(2487,23670,'SAN ANDRES SOTAVENTO',2320,'232030\r'),(2488,52687,'SAN LORENZO',5215,'521549\r'),(2489,47980,'ZONA BANANERA',4780,'478037\r'),(2490,18205,'CURILLO',1860,'186050\r'),(2491,68298,'GAMBITA',6830,'683038\r'),(2492,68101,'BOLIVAR',6850,'685009\r'),(2493,76020,'ALCALA',7620,'762040\r'),(2494,23660,'SAHAGUN',2325,'232548\r'),(2495,25224,'CUCUNUBA',2504,'250457\r'),(2496,73624,'ROVIRA',7330,'733047\r'),(2497,23350,'LA APARTADA',2335,'233507\r'),(2498,52490,'OLAYA HERRERA',5275,'527567\r'),(2499,76275,'FLORIDA',7635,'763568\r'),(2500,68001,'BUCARAMANGA',6800,'680008\r'),(2501,20787,'TAMALAMEQUE',2040,'204027\r'),(2502,17614,'RIOSUCIO',1780,'178040\r'),(2503,88564,'PROVIDENCIA',8800,'880027\r'),(2504,25823,'TOPAIPI',2538,'253820\r'),(2505,15757,'SOCHA',1516,'151640\r'),(2506,76001,'CALI',7600,'760002\r'),(2507,18247,'EL DONCELLO',1810,'181010\r'),(2508,73504,'ORTEGA',7355,'735501\r'),(2509,68655,'SABANA DE TORRES',6870,'687007\r'),(2510,23182,'CHINU',2320,'232058\r'),(2511,54660,'SALAZAR',5445,'544570\r'),(2512,15814,'TOCA',1502,'150268\r'),(2513,19585,'PURACE',1930,'193009\r'),(2514,13222,'CLEMENCIA',1305,'130517\r'),(2515,5088,'BELLO',510,'051053\r'),(2516,73449,'MELGAR',7340,'734001\r'),(2517,41530,'PALESTINA',4170,'417067\r'),(2518,17433,'MANZANARES',1730,'173028\r'),(2519,54001,'CUCUTA',5400,'540010\r'),(2520,41797,'TESALIA',4150,'415007\r'),(2521,19110,'BUENOS AIRES',1910,'191009\r'),(2522,70235,'GALERAS',7020,'702050\r'),(2523,94663,'MAPIRIPANA',9440,'944059\r'),(2524,41006,'ACEVEDO',4170,'417088\r'),(2525,23182,'CHINU',2320,'232057\r'),(2526,27135,'EL CANTON DEL SAN PABLO',2720,'272047\r'),(2527,5051,'ARBOLETES',578,'057828\r'),(2528,23001,'MONTERIA',2300,'230003\r'),(2529,41676,'SANTA MARIA',4120,'412027\r'),(2530,23068,'AYAPEL',2335,'233539\r'),(2531,73124,'CAJAMARCA',7325,'732501\r'),(2532,18479,'MORELIA',1850,'185018\r'),(2533,52520,'FRANCISCO PIZARRO',5285,'528567\r'),(2534,5667,'SAN RAFAEL',538,'053837\r'),(2535,81001,'ARAUCA',8100,'810008\r'),(2536,8758,'SOLEDAD',830,'083003\r'),(2537,15491,'NOBSA',1522,'152280\r'),(2538,25178,'CHIPAQUE',2518,'251807\r'),(2539,68745,'SIMACOTA',6835,'683561\r'),(2540,13657,'SAN JUAN NEPOMUCENO',1320,'132018\r'),(2541,17001,'MANIZALES',1700,'170004\r'),(2542,52565,'PROVIDENCIA',5260,'526020\r'),(2543,41396,'LA PLATA',4150,'415077\r'),(2544,5585,'PUERTO NARE',534,'053437\r'),(2545,15806,'TIBASOSA',1522,'152267\r'),(2546,44001,'RIOHACHA',4400,'440002\r'),(2547,99773,'CUMARIBO',9910,'991039\r'),(2548,5490,'NECOCLI',578,'057877\r'),(2549,15516,'PAIPA',1504,'150449\r'),(2550,50370,'URIBE',5050,'505047\r'),(2551,50568,'PUERTO GAITAN',5020,'502058\r'),(2552,85440,'VILLANUEVA',8550,'855037\r'),(2553,15542,'PESCA',1524,'152467\r'),(2554,15774,'SUSACON',1508,'150880\r'),(2555,15097,'BOAVITA',1510,'151067\r'),(2556,25035,'ANAPOIMA',2526,'252647\r'),(2557,70713,'SAN ONOFRE',7070,'707018\r'),(2558,15224,'CUCAITA',1540,'154060\r'),(2559,73152,'CASABIANCA',7315,'731527\r'),(2560,19532,'PATIA',1955,'195501\r'),(2561,73347,'HERVEO',7315,'731540\r'),(2562,25320,'GUADUAS',2534,'253448\r'),(2563,52788,'TANGUA',5235,'523508\r'),(2564,73555,'PLANADAS',7350,'735077\r'),(2565,86757,'SAN MIGUEL',8620,'862040\r'),(2566,25368,'JERUSALEN',2528,'252817\r'),(2567,66001,'PEREIRA',6600,'660005\r'),(2568,15696,'SANTA SOFIA',1542,'154240\r'),(2569,5212,'COPACABANA',510,'051048\r'),(2570,19418,'LOPEZ',1960,'196068\r'),(2571,25151,'CAQUEZA',2518,'251820\r'),(2572,52835,'SAN ANDRES DE TUMACO',5285,'528527\r'),(2573,15837,'TUTA',1504,'150401\r'),(2574,25326,'GUATAVITA',2510,'251060\r'),(2575,76520,'PALMIRA',7635,'763547\r'),(2576,70823,'TOLU VIEJO',7070,'707050\r'),(2577,5091,'BETANIA',560,'056077\r'),(2578,5044,'ANZA',568,'056858\r'),(2579,25307,'GIRARDOT',2524,'252432\r'),(2580,76834,'TULUA',7630,'763027\r'),(2581,25653,'SAN CAYETANO',2540,'254050\r'),(2582,91540,'PUERTO NARIÃO',9110,'911010\r'),(2583,47189,'CIENAGA',4780,'478008\r'),(2584,95025,'EL RETORNO',9510,'951001\r'),(2585,68020,'ALBANIA',6845,'684537\r'),(2586,15599,'RAMIRIQUI',1534,'153408\r'),(2587,17653,'SALAMINA',1720,'172008\r'),(2588,15861,'VENTAQUEMADA',1536,'153649\r'),(2589,13760,'SOPLAVIENTO',1315,'131501\r'),(2590,68895,'ZAPATOCA',6840,'684067\r'),(2591,5234,'DABEIBA',574,'057437\r'),(2592,15491,'NOBSA',1522,'152288\r'),(2593,8758,'SOLEDAD',830,'083010\r'),(2594,85410,'TAURAMENA',8540,'854039\r'),(2595,5480,'MUTATA',574,'057428\r'),(2596,18150,'CARTAGENA DEL CHAIRA',1830,'183017\r'),(2597,27001,'QUIBDO',2700,'270009\r'),(2598,18753,'SAN VICENTE DEL CAGUAN',1820,'182019\r'),(2599,13052,'ARJONA',1310,'131027\r'),(2600,5315,'GUADALUPE',518,'051827\r'),(2601,25758,'SOPO',2510,'251008\r'),(2602,5390,'LA PINTADA',550,'055067\r'),(2603,5321,'GUATAPE',538,'053847\r'),(2604,68855,'VALLE DE SAN JOSE',6825,'682571\r'),(2605,73671,'SALDAÃA',7335,'733577\r'),(2606,68322,'GUAPOTA',6830,'683011\r'),(2607,15514,'PAEZ',1526,'152620\r'),(2608,99001,'PUERTO CARREÃO',9900,'990007\r'),(2609,86568,'PUERTO ASIS',8620,'862060\r'),(2610,68502,'ONZAGA',6825,'682519\r'),(2611,73585,'PURIFICACION',7345,'734508\r'),(2612,52019,'SAN JOSE DE ALBAN',5210,'521050\r'),(2613,13838,'TURBANA',1310,'131010\r'),(2614,11001,'LOCALIDAD LOS MARTIRES',1114,'111411\r'),(2615,47675,'SALAMINA',4770,'477040\r'),(2616,13001,'CARTAGENA',1300,'130005\r'),(2617,15806,'TIBASOSA',1522,'152260\r'),(2618,5120,'CACERES',524,'052450\r'),(2619,20517,'PAILITAS',2040,'204007\r'),(2620,11001,'LOCALIDAD SUBA',1111,'111111\r'),(2621,23189,'CIENAGA DE ORO',2325,'232538\r'),(2622,25769,'SUBACHOQUE',2502,'250228\r'),(2623,15176,'CHIQUINQUIRA',1546,'154648\r'),(2624,15407,'VILLA DE LEYVA',1540,'154007\r'),(2625,85001,'YOPAL',8500,'850009\r'),(2626,17867,'VICTORIA',1740,'174030\r'),(2627,23672,'SAN ANTERO',2315,'231527\r'),(2628,76001,'CALI',7600,'760050\r'),(2629,63594,'QUIMBAYA',6340,'634027\r'),(2630,50318,'GUAMAL',5070,'507057\r'),(2631,50573,'PUERTO LOPEZ',5020,'502009\r'),(2632,68051,'ARATOCA',6820,'682057\r'),(2633,50287,'FUENTE DE ORO',5040,'504028\r'),(2634,5088,'BELLO',510,'051051\r'),(2635,41660,'SALADOBLANCO',4180,'418027\r'),(2636,23162,'CERETE',2305,'230557\r'),(2637,54660,'SALAZAR',5445,'544578\r'),(2638,73616,'RIOBLANCO',7355,'735587\r'),(2639,27615,'RIOSUCIO',2770,'277018\r'),(2640,25745,'SIMIJACA',2506,'250647\r'),(2641,20400,'LA JAGUA DE IBIRICO',2030,'203027\r'),(2642,13062,'ARROYOHONDO',1315,'131567\r'),(2643,25339,'GUTIERREZ',2518,'251860\r'),(2644,76890,'YOTOCO',7610,'761040\r'),(2645,66170,'DOSQUEBRADAS',6610,'661002\r'),(2646,47001,'SANTA MARTA',4700,'470007\r'),(2647,54553,'PUERTO SANTANDER',5480,'548037\r'),(2648,19548,'PIENDAMO',1905,'190537\r'),(2649,5001,'MEDELLIN',500,'050005\r'),(2650,19785,'SUCRE',1940,'194060\r'),(2651,19533,'PIAMONTE',1945,'194557\r'),(2652,5854,'VALDIVIA',520,'052010\r'),(2653,20770,'SAN MARTIN',2050,'205057\r'),(2654,27006,'ACANDI',2780,'278017\r'),(2655,25878,'VIOTA',2526,'252660\r'),(2656,73001,'IBAGUE',7300,'730017\r'),(2657,27001,'QUIBDO',2700,'270001\r'),(2658,54206,'CONVENCION',5470,'547059\r'),(2659,23580,'PUERTO LIBERTADOR',2340,'234037\r'),(2660,52473,'MOSQUERA',5275,'527587\r'),(2661,11001,'LOCALIDAD ENGATIVA',1110,'111071\r'),(2662,19780,'SUAREZ',1905,'190580\r'),(2663,19110,'BUENOS AIRES',1910,'191001\r'),(2664,52835,'SAN ANDRES DE TUMACO',5285,'528528\r'),(2665,19355,'INZA',1925,'192548\r'),(2666,73678,'SAN LUIS',7330,'733007\r'),(2667,68502,'ONZAGA',6825,'682529\r'),(2668,5837,'TURBO',578,'057860\r'),(2669,13001,'CARTAGENA',1300,'130003\r'),(2670,25386,'LA MESA',2526,'252608\r'),(2671,41770,'SUAZA',4160,'416080\r'),(2672,23678,'SAN CARLOS',2325,'232509\r'),(2673,54223,'CUCUTILLA',5445,'544529\r'),(2674,13030,'ALTOS DEL ROSARIO',1335,'133508\r'),(2675,76318,'GUACARI',7635,'763507\r'),(2676,15572,'PUERTO BOYACA',1552,'155209\r'),(2677,5001,'MEDELLIN',500,'050026\r'),(2678,5890,'YOLOMBO',530,'053027\r'),(2679,5045,'APARTADO',578,'057841\r'),(2680,11001,'LOCALIDAD SUBA',1111,'111171\r'),(2681,5021,'ALEJANDRIA',538,'053827\r'),(2682,68432,'MALAGA',6820,'682018\r'),(2683,15106,'BRICEÃO',1546,'154670\r'),(2684,25871,'VILLAGOMEZ',2540,'254037\r'),(2685,11001,'LOCALIDAD KENNEDY',1108,'110861\r'),(2686,25805,'TIBACUY',2522,'252237\r'),(2687,8758,'SOLEDAD',830,'083001\r'),(2688,73200,'COELLO',7335,'733501\r'),(2689,5483,'NARIÃO',548,'054840\r'),(2690,25307,'GIRARDOT',2524,'252437\r'),(2691,68549,'PINCHOTE',6835,'683511\r'),(2692,47053,'ARACATACA',4720,'472001\r'),(2693,15778,'SUTATENZA',1530,'153060\r'),(2694,68190,'CIMITARRA',6860,'686041\r'),(2695,68575,'PUERTO WILCHES',6870,'687067\r'),(2696,19450,'MERCADERES',1950,'195068\r'),(2697,54480,'MUTISCUA',5440,'544077\r'),(2698,17174,'CHINCHINA',1760,'176027\r'),(2699,52227,'CUMBAL',5250,'525007\r'),(2700,52378,'LA CRUZ',5210,'521028\r'),(2701,5604,'REMEDIOS',528,'052827\r'),(2702,18256,'EL PAUJIL',1810,'181038\r'),(2703,44560,'MANAURE',4410,'441017\r'),(2704,11001,'LOCALIDAD TUNJUELITO',1106,'110611\r'),(2705,19137,'CALDONO',1920,'192047\r'),(2706,66456,'MISTRATO',6640,'664028\r'),(2707,85315,'SACAMA',8510,'851037\r'),(2708,68190,'CIMITARRA',6860,'686057\r'),(2709,76520,'PALMIRA',7635,'763532\r'),(2710,15047,'AQUITANIA',1524,'152420\r'),(2711,15180,'CHISCAS',1514,'151401\r'),(2712,8675,'SANTA LUCIA',840,'084080\r'),(2713,95200,'MIRAFLORES',9520,'952008\r'),(2714,15455,'MIRAFLORES',1526,'152660\r'),(2715,68092,'BETULIA',6865,'686507\r'),(2716,76130,'CANDELARIA',7635,'763579\r'),(2717,52001,'PASTO',5200,'520017\r'),(2718,25839,'UBALA',2512,'251260\r'),(2719,17013,'AGUADAS',1720,'172028\r'),(2720,76892,'YUMBO',7605,'760508\r'),(2721,20013,'AGUSTIN CODAZZI',2020,'202050\r'),(2722,76622,'ROLDANILLO',7615,'761557\r'),(2723,19142,'CALOTO',1910,'191078\r'),(2724,54125,'CACOTA',5440,'544010\r'),(2725,13212,'CORDOBA',1325,'132507\r'),(2726,25518,'PAIME',2540,'254047\r'),(2727,70221,'COVEÃAS',7060,'706057\r'),(2728,5400,'LA UNION',550,'055028\r'),(2729,25151,'CAQUEZA',2518,'251827\r'),(2730,5652,'SAN FRANCISCO',548,'054810\r'),(2731,19698,'SANTANDER DE QUILICHAO',1910,'191049\r'),(2732,52079,'BARBACOAS',5280,'528068\r'),(2733,5837,'TURBO',578,'057869\r'),(2734,18001,'FLORENCIA',1800,'180007\r'),(2735,5034,'ANDES',560,'056067\r'),(2736,19473,'MORALES',1905,'190550\r'),(2737,13655,'SAN JACINTO DEL CAUCA',1340,'134067\r'),(2738,17486,'NEIRA',1710,'171007\r'),(2739,18756,'SOLANO',1840,'184018\r'),(2740,15491,'NOBSA',1522,'152287\r'),(2741,5347,'HELICONIA',558,'055820\r'),(2742,5282,'FREDONIA',550,'055078\r'),(2743,70678,'SAN BENITO ABAD',7030,'703017\r'),(2744,19110,'BUENOS AIRES',1910,'191008\r'),(2745,5042,'SANTAFE DE ANTIOQUIA',570,'057058\r'),(2746,73001,'IBAGUE',7300,'730008\r'),(2747,47570,'PUEBLOVIEJO',4780,'478040\r'),(2748,15518,'PAJARITO',1524,'152407\r'),(2749,27361,'ISTMINA',2740,'274019\r'),(2750,68425,'MACARAVITA',6815,'681537\r'),(2751,81220,'CRAVO NORTE',8120,'812019\r'),(2752,52490,'OLAYA HERRERA',5275,'527569\r'),(2753,18460,'MILAN',1850,'185038\r'),(2754,19743,'SILVIA',1920,'192079\r'),(2755,8758,'SOLEDAD',830,'083004\r'),(2756,66572,'PUEBLO RICO',6630,'663018\r'),(2757,15466,'MONGUI',1522,'152207\r'),(2758,41668,'SAN AGUSTIN',4180,'418068\r'),(2759,19001,'POPAYAN',1900,'190008\r'),(2760,5789,'TAMESIS',560,'056020\r'),(2761,11001,'LOCALIDAD BOSA',1107,'110711\r'),(2762,66572,'PUEBLO RICO',6630,'663011\r'),(2763,15837,'TUTA',1504,'150407\r'),(2764,44430,'MAICAO',4420,'442007\r'),(2765,15226,'CUITIVA',1522,'152230\r'),(2766,76403,'LA VICTORIA',7625,'762510\r'),(2767,5364,'JARDIN',560,'056057\r'),(2768,52480,'NARIÃO',5220,'522020\r'),(2769,15723,'SATIVASUR',1508,'150801\r'),(2770,15832,'TUNUNGUA',1546,'154687\r'),(2771,19355,'INZA',1925,'192537\r'),(2772,68705,'SANTA BARBARA',6810,'681037\r'),(2773,68152,'CARCASI',6815,'681529\r'),(2774,68092,'BETULIA',6865,'686501\r'),(2775,52520,'FRANCISCO PIZARRO',5285,'528568\r'),(2776,76306,'GINEBRA',7635,'763518\r'),(2777,68689,'SAN VICENTE DE CHUCURI',6865,'686547\r'),(2778,23570,'PUEBLO NUEVO',2330,'233008\r'),(2779,73411,'LIBANO',7310,'731048\r'),(2780,68229,'CURITI',6820,'682047\r'),(2781,27495,'NUQUI',2760,'276058\r'),(2782,44650,'SAN JUAN DEL CESAR',4440,'444038\r'),(2783,54743,'SILOS',5440,'544050\r'),(2784,15897,'ZETAQUIRA',1526,'152680\r'),(2785,63302,'GENOVA',6320,'632080\r'),(2786,19824,'TOTORO',1925,'192570\r'),(2787,20013,'AGUSTIN CODAZZI',2020,'202058\r'),(2788,23675,'SAN BERNARDO DEL VIENTO',2315,'231501\r'),(2789,52838,'TUQUERRES',5255,'525537\r'),(2790,68572,'PUENTE NACIONAL',6845,'684521\r'),(2791,5790,'TARAZA',524,'052460\r'),(2792,17088,'BELALCAZAR',1770,'177007\r'),(2793,52323,'GUALMATAN',5245,'524507\r'),(2794,85162,'MONTERREY',8550,'855017\r'),(2795,68861,'VELEZ',6855,'685569\r'),(2796,15236,'CHIVOR',1530,'153001\r'),(2797,23417,'LORICA',2310,'231028\r'),(2798,68368,'JESUS MARIA',6845,'684551\r'),(2799,15686,'SANTANA',1544,'154440\r'),(2800,73563,'PRADO',7345,'734520\r'),(2801,68298,'GAMBITA',6830,'683039\r'),(2802,23001,'MONTERIA',2300,'230029\r'),(2803,25754,'SOACHA',2500,'250053\r'),(2804,25324,'GUATAQUI',2528,'252827\r'),(2805,27800,'UNGUIA',2780,'278030\r'),(2806,13836,'TURBACO',1310,'131008\r'),(2807,18001,'FLORENCIA',1800,'180009\r'),(2808,17001,'MANIZALES',1700,'170007\r'),(2809,54405,'LOS PATIOS',5410,'541010\r'),(2810,15377,'LABRANZAGRANDE',1518,'151840\r'),(2811,27050,'ATRATO',2720,'272010\r'),(2812,68307,'GIRON',6875,'687542\r'),(2813,20570,'PUEBLO BELLO',2010,'201007\r'),(2814,76001,'CALI',7600,'760040\r'),(2815,11001,'LOCALIDAD SUBA',1111,'111166\r'),(2816,47980,'ZONA BANANERA',4780,'478029\r'),(2817,50001,'VILLAVICENCIO',5000,'500002\r'),(2818,47660,'SABANAS DE SAN ANGEL',4750,'475001\r'),(2819,41799,'TELLO',4110,'411047\r'),(2820,73686,'SANTA ISABEL',7305,'730567\r'),(2821,19743,'SILVIA',1920,'192070\r'),(2822,52378,'LA CRUZ',5210,'521027\r'),(2823,5002,'ABEJORRAL',550,'055037\r'),(2824,47703,'SAN ZENON',4740,'474068\r'),(2825,68235,'EL CARMEN DE CHUCURI',6865,'686577\r'),(2826,86320,'ORITO',8620,'862001\r'),(2827,54660,'SALAZAR',5445,'544577\r'),(2828,19455,'MIRANDA',1915,'191520\r'),(2829,73770,'SUAREZ',7335,'733588\r'),(2830,91001,'LETICIA',9100,'910001\r'),(2831,13655,'SAN JACINTO DEL CAUCA',1340,'134060\r'),(2832,15759,'SOGAMOSO',1522,'152219\r'),(2833,15676,'SAN MIGUEL DE SEMA',1538,'153820\r'),(2834,44874,'VILLANUEVA',4450,'445001\r'),(2835,97889,'YAVARATE',9710,'971008\r'),(2836,76364,'JAMUNDI',7640,'764008\r'),(2837,8433,'MALAMBO',830,'083021\r'),(2838,11001,'LOCALIDAD USME',1105,'110541\r'),(2839,41801,'TERUEL',4120,'412040\r'),(2840,52693,'SAN PABLO',5210,'521047\r'),(2841,25297,'GACHETA',2512,'251237\r'),(2842,50350,'LA MACARENA',5050,'505028\r'),(2843,50006,'ACACIAS',5070,'507017\r'),(2844,25797,'TENA',2526,'252617\r'),(2845,18410,'LA MONTAÃITA',1810,'181067\r'),(2846,27430,'MEDIO BAUDO',2750,'275017\r'),(2847,54109,'BUCARASICA',5455,'545558\r'),(2848,68500,'OIBA',6830,'683027\r'),(2849,73563,'PRADO',7345,'734527\r'),(2850,41349,'HOBO',4130,'413060\r'),(2851,13549,'PINILLOS',1340,'134001\r'),(2852,94001,'INIRIDA',9400,'940017\r'),(2853,19473,'MORALES',1905,'190559\r'),(2854,17050,'ARANZAZU',1710,'171040\r'),(2855,95001,'SAN JOSE DEL GUAVIARE',9500,'950008\r'),(2856,47245,'EL BANCO',4730,'473040\r'),(2857,5604,'REMEDIOS',528,'052828\r'),(2858,68425,'MACARAVITA',6815,'681538\r'),(2859,52490,'OLAYA HERRERA',5275,'527568\r'),(2860,8638,'SABANALARGA',850,'085009\r'),(2861,50325,'MAPIRIPAN',5030,'503037\r'),(2862,68307,'GIRON',6875,'687558\r'),(2863,99001,'PUERTO CARREÃO',9900,'990018\r'),(2864,52506,'OSPINA',5230,'523040\r'),(2865,54239,'DURANIA',5445,'544517\r'),(2866,19807,'TIMBIO',1935,'193527\r'),(2867,41872,'VILLAVIEJA',4110,'411020\r'),(2868,68755,'SOCORRO',6835,'683551\r'),(2869,41503,'OPORAPA',4180,'418008\r'),(2870,5736,'SEGOVIA',528,'052817\r'),(2871,68307,'GIRON',6875,'687549\r'),(2872,15660,'SAN EDUARDO',1526,'152607\r'),(2873,5642,'SALGAR',564,'056478\r'),(2874,13744,'SIMITI',1350,'135020\r'),(2875,19824,'TOTORO',1925,'192577\r'),(2876,76520,'PALMIRA',7635,'763533\r'),(2877,41357,'IQUIRA',4120,'412068\r'),(2878,99773,'CUMARIBO',9910,'991008\r'),(2879,76823,'TORO',7615,'761527\r'),(2880,73675,'SAN ANTONIO',7355,'735530\r'),(2881,95200,'MIRAFLORES',9520,'952009\r'),(2882,47258,'EL PIÃON',4760,'476008\r'),(2883,63548,'PIJAO',6320,'632067\r'),(2884,11001,'LOCALIDAD KENNEDY',1108,'110821\r'),(2885,73319,'GUAMO',7335,'733557\r'),(2886,47318,'GUAMAL',4730,'473020\r'),(2887,8549,'PIOJO',810,'081067\r'),(2888,73226,'CUNDAY',7340,'734047\r'),(2889,13670,'SAN PABLO',1350,'135047\r'),(2890,44279,'FONSECA',4440,'444017\r'),(2891,25438,'MEDINA',2514,'251427\r'),(2892,19473,'MORALES',1905,'190557\r'),(2893,19701,'SANTA ROSA',1945,'194520\r'),(2894,25175,'CHIA',2500,'250007\r'),(2895,85400,'TAMARA',8510,'851058\r'),(2896,85136,'LA SALINA',8510,'851017\r'),(2897,68207,'CONCEPCION',6815,'681517\r'),(2898,5001,'MEDELLIN',500,'050034\r'),(2899,17272,'FILADELFIA',1710,'171028\r'),(2900,13836,'TURBACO',1310,'131001\r'),(2901,76001,'CALI',7600,'760015\r'),(2902,76606,'RESTREPO',7605,'760540\r'),(2903,50683,'SAN JUAN DE ARAMA',5040,'504041\r'),(2904,73616,'RIOBLANCO',7355,'735589\r'),(2905,15097,'BOAVITA',1510,'151060\r'),(2906,27660,'SAN JOSE DEL PALMAR',2730,'273070\r'),(2907,15104,'BOYACA',1536,'153617\r'),(2908,81001,'ARAUCA',8100,'810001\r'),(2909,76109,'BUENAVENTURA',7645,'764507\r'),(2910,25175,'CHIA',2500,'250008\r'),(2911,15215,'CORRALES',1520,'152067\r'),(2912,19364,'JAMBALO',1920,'192029\r'),(2913,52320,'GUAITARILLA',5255,'525508\r'),(2914,5361,'ITUANGO',520,'052070\r'),(2915,41026,'ALTAMIRA',4160,'416027\r'),(2916,27050,'ATRATO',2720,'272018\r'),(2917,66687,'SANTUARIO',6630,'663001\r'),(2918,15600,'RAQUIRA',1538,'153808\r'),(2919,63111,'BUENAVISTA',6320,'632047\r'),(2920,8372,'JUAN DE ACOSTA',810,'081048\r'),(2921,73319,'GUAMO',7335,'733549\r'),(2922,66383,'LA CELIA',6620,'662037\r'),(2923,66001,'PEREIRA',6600,'660009\r'),(2924,15464,'MONGUA',1520,'152007\r'),(2925,52356,'IPIALES',5240,'524061\r'),(2926,68307,'GIRON',6875,'687547\r'),(2927,52540,'POLICARPA',5270,'527008\r'),(2928,23815,'TUCHIN',2320,'232027\r'),(2929,5615,'RIONEGRO',540,'054047\r'),(2930,85001,'YOPAL',8500,'850002\r'),(2931,13030,'ALTOS DEL ROSARIO',1335,'133507\r'),(2932,99773,'CUMARIBO',9910,'991017\r'),(2933,15090,'BERBEO',1526,'152617\r'),(2934,15500,'OICATA',1502,'150227\r'),(2935,5790,'TARAZA',524,'052467\r'),(2936,41799,'TELLO',4110,'411040\r'),(2937,73349,'HONDA',7320,'732048\r'),(2938,47541,'PEDRAZA',4760,'476048\r'),(2939,13268,'EL PEÃON',1335,'133558\r'),(2940,25839,'UBALA',2512,'251268\r'),(2941,76126,'CALIMA',7605,'760530\r'),(2942,19693,'SAN SEBASTIAN',1945,'194507\r'),(2943,86569,'PUERTO CAICEDO',8620,'862087\r'),(2944,25653,'SAN CAYETANO',2540,'254057\r'),(2945,99773,'CUMARIBO',9910,'991027\r'),(2946,68020,'ALBANIA',6845,'684531\r'),(2947,25398,'LA PEÃA',2536,'253640\r'),(2948,68217,'COROMORO',6825,'682539\r'),(2949,5154,'CAUCASIA',524,'052417\r'),(2950,73352,'ICONONZO',7340,'734028\r'),(2951,8573,'PUERTO COLOMBIA',810,'081008\r'),(2952,15204,'COMBITA',1502,'150201\r'),(2953,76250,'EL DOVIO',7615,'761567\r'),(2954,68498,'OCAMONTE',6825,'682567\r'),(2955,76001,'CALI',7600,'760013\r'),(2956,94883,'SAN FELIPE',9420,'942017\r'),(2957,15755,'SOCOTA',1516,'151628\r'),(2958,44847,'URIBIA',4410,'441020\r'),(2959,5440,'MARINILLA',540,'054027\r'),(2960,68152,'CARCASI',6815,'681528\r'),(2961,27450,'MEDIO SAN JUAN',2740,'274030\r'),(2962,18479,'MORELIA',1850,'185010\r'),(2963,19450,'MERCADERES',1950,'195069\r'),(2964,13268,'EL PEÃON',1335,'133557\r'),(2965,19364,'JAMBALO',1920,'192028\r'),(2966,25099,'BOJACA',2530,'253001\r'),(2967,81736,'SARAVENA',8150,'815010\r'),(2968,52317,'GUACHUCAL',5245,'524588\r'),(2969,52381,'LA FLORIDA',5220,'522040\r'),(2970,25645,'SAN ANTONIO DEL TEQUENDAMA',2526,'252620\r'),(2971,17444,'MARQUETALIA',1730,'173048\r'),(2972,15238,'DUITAMA',1504,'150477\r'),(2973,76233,'DAGUA',7605,'760528\r'),(2974,15753,'SOATA',1510,'151001\r'),(2975,25594,'QUETAME',2518,'251840\r'),(2976,15822,'TOTA',1524,'152440\r'),(2977,52001,'PASTO',5200,'520001\r'),(2978,19212,'CORINTO',1915,'191560\r'),(2979,5001,'MEDELLIN',500,'050002\r'),(2980,19075,'BALBOA',1955,'195530\r'),(2981,68001,'BUCARAMANGA',6800,'680011\r'),(2982,41660,'SALADOBLANCO',4180,'418020\r'),(2983,25293,'GACHALA',2512,'251257\r'),(2984,17444,'MARQUETALIA',1730,'173040\r'),(2985,25288,'FUQUENE',2506,'250627\r'),(2986,41244,'ELIAS',4170,'417001\r'),(2987,73319,'GUAMO',7335,'733548\r'),(2988,11001,'LOCALIDAD USAQUEN',1101,'110131\r'),(2989,5031,'AMALFI',528,'052840\r'),(2990,52835,'SAN ANDRES DE TUMACO',5285,'528537\r'),(2991,94343,'BARRANCO MINAS',9440,'944010\r'),(2992,52399,'LA UNION',5215,'521527\r'),(2993,41020,'ALGECIRAS',4130,'413047\r'),(2994,15001,'TUNJA',1500,'150007\r'),(2995,63302,'GENOVA',6320,'632087\r'),(2996,99624,'SANTA ROSALIA',9920,'992050\r'),(2997,86755,'SAN FRANCISCO',8610,'861008\r'),(2998,17777,'SUPIA',1780,'178028\r'),(2999,15816,'TOGÃI',1544,'154401\r'),(3000,54800,'TEORAMA',5470,'547038\r'),(3001,23417,'LORICA',2310,'231037\r'),(3002,19517,'PAEZ',1925,'192501\r'),(3003,19780,'SUAREZ',1905,'190587\r'),(3004,54518,'PAMPLONA',5430,'543058\r'),(3005,76001,'CALI',7600,'760007\r'),(3006,76622,'ROLDANILLO',7615,'761550\r'),(3007,13654,'SAN JACINTO',1320,'132037\r'),(3008,70001,'SINCELEJO',7000,'700017\r'),(3009,73585,'PURIFICACION',7345,'734501\r'),(3010,5380,'LA ESTRELLA',554,'055467\r'),(3011,68160,'CEPITA',6820,'682067\r'),(3012,73347,'HERVEO',7315,'731547\r'),(3013,18860,'VALPARAISO',1850,'185050\r'),(3014,11001,'LOCALIDAD CIUDAD BOLIVAR',1119,'111981\r'),(3015,68250,'EL PEÃON',6850,'685021\r'),(3016,20310,'GONZALEZ',2050,'205030\r'),(3017,5895,'ZARAGOZA',524,'052440\r'),(3018,76400,'LA UNION',7615,'761548\r'),(3019,50350,'LA MACARENA',5050,'505027\r'),(3020,86320,'ORITO',8620,'862008\r'),(3021,5649,'SAN CARLOS',544,'054427\r'),(3022,15763,'SOTAQUIRA',1504,'150420\r'),(3023,19256,'EL TAMBO',1935,'193587\r'),(3024,5495,'NECHI',524,'052420\r'),(3025,52079,'BARBACOAS',5280,'528060\r'),(3026,18753,'SAN VICENTE DEL CAGUAN',1820,'182018\r'),(3027,5490,'NECOCLI',578,'057870\r'),(3028,76606,'RESTREPO',7605,'760548\r'),(3029,8758,'SOLEDAD',830,'083007\r'),(3030,25596,'QUIPILE',2530,'253037\r'),(3031,5360,'ITAGÃI',554,'055413\r'),(3032,97666,'TARAIRA',9720,'972040\r'),(3033,23570,'PUEBLO NUEVO',2330,'233009\r'),(3034,13873,'VILLANUEVA',1305,'130537\r'),(3035,76845,'ULLOA',7620,'762030\r'),(3036,68235,'EL CARMEN DE CHUCURI',6865,'686561\r'),(3037,41668,'SAN AGUSTIN',4180,'418060\r'),(3038,52696,'SANTA BARBARA',5275,'527501\r'),(3039,13673,'SANTA CATALINA',1305,'130507\r'),(3040,52215,'CORDOBA',5240,'524001\r'),(3041,13006,'ACHI',1340,'134028\r'),(3042,85325,'SAN LUIS DE PALENQUE',8530,'853037\r'),(3043,54099,'BOCHALEMA',5430,'543017\r'),(3044,27430,'MEDIO BAUDO',2750,'275018\r'),(3045,68081,'BARRANCABERMEJA',6870,'687033\r'),(3046,52427,'MAGÃI',5280,'528017\r'),(3047,18592,'PUERTO RICO',1820,'182057\r'),(3048,11001,'LOCALIDAD CIUDAD BOLIVAR',1119,'111931\r'),(3049,50001,'VILLAVICENCIO',5000,'500008\r'),(3050,41006,'ACEVEDO',4170,'417087\r'),(3051,52207,'CONSACA',5225,'522548\r'),(3052,54599,'RAGONVALIA',5410,'541050\r'),(3053,23675,'SAN BERNARDO DEL VIENTO',2315,'231509\r'),(3054,19397,'LA VEGA',1940,'194020\r'),(3055,13042,'ARENAL',1345,'134520\r'),(3056,68669,'SAN ANDRES',6820,'682007\r'),(3057,76130,'CANDELARIA',7635,'763577\r'),(3058,52621,'ROBERTO PAYAN',5280,'528030\r'),(3059,25799,'TENJO',2502,'250207\r'),(3060,76001,'CALI',7600,'760043\r'),(3061,76001,'CALI',7600,'760022\r'),(3062,19050,'ARGELIA',1955,'195560\r'),(3063,25286,'FUNZA',2500,'250027\r'),(3064,44847,'URIBIA',4410,'441038\r'),(3065,15218,'COVARACHIA',1510,'151040\r'),(3066,15183,'CHITA',1516,'151607\r'),(3067,13430,'MAGANGUE',1325,'132512\r'),(3068,27073,'BAGADO',2710,'271058\r'),(3069,52224,'CUASPUD',5245,'524560\r'),(3070,81065,'ARAUQUITA',8160,'816010\r'),(3071,13600,'RIO VIEJO',1345,'134507\r'),(3072,19455,'MIRANDA',1915,'191527\r'),(3073,27615,'RIOSUCIO',2780,'278058\r'),(3074,5086,'BELMIRA',514,'051420\r'),(3075,44874,'VILLANUEVA',4450,'445008\r'),(3076,50689,'SAN MARTIN',5070,'507029\r'),(3077,95001,'SAN JOSE DEL GUAVIARE',9500,'950009\r'),(3078,99773,'CUMARIBO',9910,'991018\r'),(3079,76892,'YUMBO',7605,'760502\r'),(3080,25168,'CHAGUANI',2532,'253247\r'),(3081,68001,'BUCARAMANGA',6800,'680005\r'),(3082,5360,'ITAGÃI',554,'055417\r'),(3083,25851,'UTICA',2534,'253430\r'),(3084,85125,'HATO COROZAL',8520,'852017\r'),(3085,25245,'EL COLEGIO',2526,'252637\r'),(3086,91798,'TARAPACA',9110,'911030\r'),(3087,25120,'CABRERA',2520,'252047\r'),(3088,68745,'SIMACOTA',6835,'683577\r'),(3089,18610,'SAN JOSE DEL FRAGUA',1860,'186078\r'),(3090,18592,'PUERTO RICO',1820,'182050\r'),(3091,15507,'OTANCHE',1550,'155068\r'),(3092,73055,'ARMERO',7320,'732067\r'),(3093,52540,'POLICARPA',5270,'527009\r'),(3094,5856,'VALPARAISO',560,'056030\r'),(3095,68895,'ZAPATOCA',6840,'684068\r'),(3096,5240,'EBEJICO',558,'055810\r'),(3097,8634,'SABANAGRANDE',830,'083047\r'),(3098,76036,'ANDALUCIA',7630,'763010\r'),(3099,86885,'VILLAGARZON',8610,'861080\r'),(3100,5306,'GIRALDO',570,'057047\r'),(3101,54001,'CUCUTA',5400,'540017\r'),(3102,52250,'EL CHARCO',5275,'527537\r'),(3103,70110,'BUENAVISTA',7020,'702030\r'),(3104,17042,'ANSERMA',1770,'177080\r'),(3105,85263,'PORE',8520,'852057\r'),(3106,5150,'CAROLINA',518,'051840\r'),(3107,41006,'ACEVEDO',4170,'417077\r'),(3108,76306,'GINEBRA',7635,'763510\r'),(3109,15696,'SANTA SOFIA',1542,'154247\r'),(3110,50606,'RESTREPO',5010,'501037\r'),(3111,13030,'ALTOS DEL ROSARIO',1335,'133501\r'),(3112,76001,'CALI',7600,'760033\r'),(3113,23686,'SAN PELAYO',2305,'230528\r'),(3114,27580,'RIO IRO',2730,'273017\r'),(3115,63190,'CIRCASIA',6310,'631007\r'),(3116,20770,'SAN MARTIN',2050,'205058\r'),(3117,47551,'PIVIJAY',4770,'477058\r'),(3118,5380,'LA ESTRELLA',554,'055468\r'),(3119,66001,'PEREIRA',6600,'660003\r'),(3120,15755,'SOCOTA',1516,'151620\r'),(3121,5809,'TITIRIBI',558,'055857\r'),(3122,54498,'OCAÃA',5465,'546558\r'),(3123,25394,'LA PALMA',2538,'253801\r'),(3124,15367,'JENESANO',1536,'153601\r'),(3125,91001,'LETICIA',9100,'910007\r'),(3126,20710,'SAN ALBERTO',2050,'205070\r'),(3127,68705,'SANTA BARBARA',6810,'681021\r'),(3128,25580,'PULI',2528,'252807\r'),(3129,44847,'URIBIA',4410,'441049\r'),(3130,5147,'CAREPA',578,'057850\r'),(3131,23855,'VALENCIA',2345,'234530\r'),(3132,13440,'MARGARITA',1330,'133027\r'),(3133,13188,'CICUCO',1325,'132550\r'),(3134,52411,'LINARES',5225,'522507\r'),(3135,70429,'MAJAGUAL',7030,'703057\r'),(3136,13248,'EL GUAMO',1320,'132007\r'),(3137,13430,'MAGANGUE',1325,'132517\r'),(3138,88564,'PROVIDENCIA',8800,'880020\r'),(3139,76828,'TRUJILLO',7610,'761027\r'),(3140,5034,'ANDES',560,'056068\r'),(3141,50689,'SAN MARTIN',5070,'507027\r'),(3142,68081,'BARRANCABERMEJA',6870,'687031\r'),(3143,17088,'BELALCAZAR',1770,'177001\r'),(3144,19533,'PIAMONTE',1945,'194550\r'),(3145,25154,'CARMEN DE CARUPA',2504,'250427\r'),(3146,25599,'APULO',2526,'252650\r'),(3147,47189,'CIENAGA',4780,'478002\r'),(3148,13673,'SANTA CATALINA',1305,'130508\r'),(3149,76834,'TULUA',7630,'763022\r'),(3150,15001,'TUNJA',1500,'150003\r'),(3151,66318,'GUATICA',6640,'664018\r'),(3152,76520,'PALMIRA',7635,'763531\r'),(3153,52001,'PASTO',5200,'520006\r'),(3154,20614,'RIO DE ORO',2050,'205048\r'),(3155,5809,'TITIRIBI',558,'055850\r'),(3156,25488,'NILO',2524,'252407\r'),(3157,15664,'SAN JOSE DE PARE',1544,'154467\r'),(3158,15224,'CUCAITA',1540,'154067\r'),(3159,17388,'LA MERCED',1720,'172067\r'),(3160,8549,'PIOJO',810,'081060\r'),(3161,27245,'EL CARMEN DE ATRATO',2710,'271010\r'),(3162,76001,'CALI',7600,'760045\r'),(3163,54245,'EL CARMEN',5470,'547079\r'),(3164,41001,'NEIVA',4100,'410004\r'),(3165,23678,'SAN CARLOS',2325,'232517\r'),(3166,13650,'SAN FERNANDO',1330,'133001\r'),(3167,5647,'SAN ANDRES DE CUERQUIA',520,'052040\r'),(3168,5055,'ARGELIA',548,'054837\r'),(3169,73043,'ANZOATEGUI',7305,'730548\r'),(3170,41359,'ISNOS',4180,'418048\r'),(3171,5001,'MEDELLIN',500,'050027\r'),(3172,5697,'EL SANTUARIO',544,'054457\r'),(3173,52699,'SANTACRUZ',5255,'525577\r'),(3174,17541,'PENSILVANIA',1730,'173068\r'),(3175,25805,'TIBACUY',2522,'252230\r'),(3176,73347,'HERVEO',7315,'731548\r'),(3177,50006,'ACACIAS',5070,'507001\r'),(3178,5001,'MEDELLIN',500,'050028\r'),(3179,25269,'FACATATIVA',2530,'253058\r'),(3180,85410,'TAURAMENA',8540,'854030\r'),(3181,68368,'JESUS MARIA',6845,'684557\r'),(3182,81300,'FORTUL',8140,'814058\r'),(3183,25312,'GRANADA',2522,'252250\r'),(3184,66682,'SANTA ROSA DE CABAL',6610,'661020\r'),(3185,47605,'REMOLINO',4770,'477020\r'),(3186,76892,'YUMBO',7605,'760501\r'),(3187,52399,'LA UNION',5215,'521529\r'),(3188,76834,'TULUA',7630,'763023\r'),(3189,19517,'PAEZ',1925,'192509\r'),(3190,17174,'CHINCHINA',1760,'176020\r'),(3191,15401,'LA VICTORIA',1550,'155007\r'),(3192,15204,'COMBITA',1502,'150208\r'),(3193,73168,'CHAPARRAL',7355,'735568\r'),(3194,68377,'LA BELLEZA',6850,'685067\r'),(3195,50573,'PUERTO LOPEZ',5020,'502001\r'),(3196,25001,'AGUA DE DIOS',2528,'252857\r'),(3197,99773,'CUMARIBO',9910,'991058\r'),(3198,52258,'EL TABLON DE GOMEZ',5205,'520539\r'),(3199,76111,'GUADALAJARA DE BUGA',7630,'763041\r'),(3200,73408,'LERIDA',7310,'731027\r'),(3201,81001,'ARAUCA',8100,'810007\r'),(3202,8560,'PONEDERA',840,'084007\r'),(3203,86865,'VALLE DEL GUAMUEZ',8620,'862028\r'),(3204,25785,'TABIO',2502,'250237\r'),(3205,85279,'RECETOR',8560,'856057\r'),(3206,50313,'GRANADA',5040,'504008\r'),(3207,5044,'ANZA',568,'056857\r'),(3208,68547,'PIEDECUESTA',6810,'681012\r'),(3209,11001,'LOCALIDAD TUNJUELITO',1106,'110621\r'),(3210,13001,'CARTAGENA',1300,'130006\r'),(3211,5234,'DABEIBA',574,'057430\r'),(3212,44035,'ALBANIA',4430,'443009\r'),(3213,5819,'TOLEDO',520,'052057\r'),(3214,68179,'CHIPATA',6855,'685557\r'),(3215,25513,'PACHO',2540,'254001\r'),(3216,5321,'GUATAPE',538,'053840\r'),(3217,41016,'AIPE',4110,'411008\r'),(3218,52678,'SAMANIEGO',5260,'526049\r'),(3219,19780,'SUAREZ',1905,'190597\r'),(3220,47551,'PIVIJAY',4770,'477057\r'),(3221,23300,'COTORRA',2305,'230501\r'),(3222,5660,'SAN LUIS',544,'054437\r'),(3223,47960,'ZAPAYAN',4760,'476050\r'),(3224,52473,'MOSQUERA',5275,'527588\r'),(3225,15325,'GUAYATA',1530,'153040\r'),(3226,19517,'PAEZ',1925,'192507\r'),(3227,19517,'PAEZ',1925,'192517\r'),(3228,18094,'BELEN DE LOS ANDAQUIES',1860,'186018\r'),(3229,68872,'VILLANUEVA',6840,'684027\r'),(3230,73854,'VALLE DE SAN JUAN',7330,'733020\r'),(3231,20013,'AGUSTIN CODAZZI',2020,'202057\r'),(3232,73854,'VALLE DE SAN JUAN',7330,'733027\r'),(3233,73217,'COYAIMA',7350,'735020\r'),(3234,25200,'COGUA',2504,'250408\r'),(3235,13300,'HATILLO DE LOBA',1330,'133048\r'),(3236,17616,'RISARALDA',1770,'177068\r'),(3237,5411,'LIBORINA',514,'051467\r'),(3238,50330,'MESETAS',5050,'505001\r'),(3239,23417,'LORICA',2310,'231039\r'),(3240,73043,'ANZOATEGUI',7305,'730547\r'),(3241,52435,'MALLAMA',5250,'525060\r'),(3242,5686,'SANTA ROSA DE OSOS',518,'051867\r'),(3243,76318,'GUACARI',7635,'763508\r'),(3244,11001,'LOCALIDAD SUBA',1111,'111141\r'),(3245,76130,'CANDELARIA',7635,'763570\r'),(3246,86573,'LEGUIZAMO',8640,'864009\r'),(3247,68684,'SAN JOSE DE MIRANDA',6820,'682027\r'),(3248,50124,'CABUYARO',5010,'501011\r'),(3249,25489,'NIMAIMA',2536,'253630\r'),(3250,13580,'REGIDOR',1335,'133560\r'),(3251,73686,'SANTA ISABEL',7305,'730560\r'),(3252,19807,'TIMBIO',1935,'193529\r'),(3253,52885,'YACUANQUER',5230,'523001\r'),(3254,17380,'LA DORADA',1750,'175038\r'),(3255,13468,'MOMPOS',1325,'132568\r'),(3256,47205,'CONCORDIA',4760,'476030\r'),(3257,41524,'PALERMO',4120,'412001\r'),(3258,5001,'MEDELLIN',500,'050042\r'),(3259,5036,'ANGELOPOLIS',558,'055830\r'),(3260,86568,'PUERTO ASIS',8620,'862068\r'),(3261,5212,'COPACABANA',510,'051047\r'),(3262,99773,'CUMARIBO',9910,'991029\r'),(3263,54520,'PAMPLONITA',5430,'543038\r'),(3264,52506,'OSPINA',5230,'523047\r'),(3265,41026,'ALTAMIRA',4160,'416020\r'),(3266,5390,'LA PINTADA',550,'055060\r'),(3267,8001,'BARRANQUILLA',800,'080005\r'),(3268,73043,'ANZOATEGUI',7305,'730540\r'),(3269,41132,'CAMPOALEGRE',4130,'413020\r'),(3270,76001,'CALI',7600,'760021\r'),(3271,25402,'LA VEGA',2536,'253617\r'),(3272,44035,'ALBANIA',4430,'443001\r'),(3273,68773,'SUCRE',6850,'685049\r'),(3274,5541,'PEÃOL',538,'053850\r'),(3275,20001,'VALLEDUPAR',2000,'200004\r'),(3276,52411,'LINARES',5225,'522508\r'),(3277,68770,'SUAITA',6830,'683047\r'),(3278,54001,'CUCUTA',5400,'540006\r'),(3279,47001,'SANTA MARTA',4700,'470003\r'),(3280,68432,'MALAGA',6820,'682011\r'),(3281,63190,'CIRCASIA',6310,'631008\r'),(3282,63594,'QUIMBAYA',6340,'634020\r'),(3283,25377,'LA CALERA',2512,'251207\r'),(3284,13657,'SAN JUAN NEPOMUCENO',1320,'132017\r'),(3285,17867,'VICTORIA',1740,'174038\r'),(3286,13062,'ARROYOHONDO',1315,'131560\r'),(3287,5001,'MEDELLIN',500,'050003\r'),(3288,44847,'URIBIA',4410,'441029\r'),(3289,47570,'PUEBLOVIEJO',4780,'478047\r'),(3290,8001,'BARRANQUILLA',800,'080003\r'),(3291,68418,'LOS SANTOS',6840,'684008\r'),(3292,50223,'CUBARRAL',5060,'506008\r'),(3293,23686,'SAN PELAYO',2305,'230527\r'),(3294,63470,'MONTENEGRO',6330,'633008\r'),(3295,5664,'SAN PEDRO DE LOS MILAGROS',510,'051010\r'),(3296,52678,'SAMANIEGO',5260,'526058\r'),(3297,97161,'CARURU',9730,'973001\r'),(3298,68679,'SAN GIL',6840,'684038\r'),(3299,50370,'URIBE',5050,'505041\r'),(3300,13549,'PINILLOS',1340,'134007\r'),(3301,19318,'GUAPI',1960,'196007\r'),(3302,25841,'UBAQUE',2516,'251601\r'),(3303,47001,'SANTA MARTA',4700,'470005\r'),(3304,86568,'PUERTO ASIS',8620,'862067\r'),(3305,20001,'VALLEDUPAR',2000,'200018\r'),(3306,23574,'PUERTO ESCONDIDO',2350,'235001\r'),(3307,41524,'PALERMO',4120,'412007\r'),(3308,19397,'LA VEGA',1940,'194038\r'),(3309,11001,'LOCALIDAD RAFAEL URIBE URIBE',1118,'111841\r'),(3310,15632,'SABOYA',1546,'154607\r'),(3311,99524,'LA PRIMAVERA',9920,'992008\r'),(3312,44847,'URIBIA',4410,'441037\r'),(3313,52490,'OLAYA HERRERA',5275,'527560\r'),(3314,73616,'RIOBLANCO',7355,'735580\r'),(3315,54398,'LA PLAYA',5465,'546530\r'),(3316,81794,'TAME',8140,'814018\r'),(3317,52694,'SAN PEDRO DE CARTAGO',5215,'521508\r'),(3318,97161,'CARURU',9730,'973007\r'),(3319,41001,'NEIVA',4100,'410002\r'),(3320,76001,'CALI',7600,'760008\r'),(3321,18150,'CARTAGENA DEL CHAIRA',1830,'183027\r'),(3322,54377,'LABATECA',5420,'542057\r'),(3323,76520,'PALMIRA',7635,'763539\r'),(3324,73152,'CASABIANCA',7315,'731520\r'),(3325,68276,'FLORIDABLANCA',6810,'681008\r'),(3326,47001,'SANTA MARTA',4700,'470001\r'),(3327,19100,'BOLIVAR',1950,'195007\r'),(3328,15377,'LABRANZAGRANDE',1518,'151847\r'),(3329,15511,'PACHAVITA',1532,'153210\r'),(3330,25875,'VILLETA',2534,'253417\r'),(3331,11001,'LOCALIDAD USME',1105,'110521\r'),(3332,54051,'ARBOLEDAS',5445,'544550\r'),(3333,85125,'HATO COROZAL',8520,'852010\r'),(3334,68081,'BARRANCABERMEJA',6870,'687037\r'),(3335,68397,'LA PAZ',6855,'685511\r'),(3336,25875,'VILLETA',2534,'253418\r'),(3337,47318,'GUAMAL',4730,'473028\r'),(3338,52354,'IMUES',5230,'523027\r'),(3339,15897,'ZETAQUIRA',1526,'152687\r'),(3340,91460,'MIRITI - PARANA',9160,'916058\r'),(3341,68079,'BARICHARA',6840,'684048\r'),(3342,25572,'PUERTO SALGAR',2534,'253487\r'),(3343,47720,'SANTA BARBARA DE PINTO',4740,'474001\r'),(3344,5353,'HISPANIA',564,'056457\r'),(3345,25899,'ZIPAQUIRA',2502,'250258\r'),(3346,25317,'GUACHETA',2506,'250610\r'),(3347,20032,'ASTREA',2010,'201047\r'),(3348,5467,'MONTEBELLO',550,'055047\r'),(3349,15185,'CHITARAQUE',1544,'154420\r'),(3350,8001,'BARRANQUILLA',800,'080007\r'),(3351,23001,'MONTERIA',2300,'230001\r'),(3352,47707,'SANTA ANA',4740,'474027\r'),(3353,13433,'MAHATES',1310,'131048\r'),(3354,50568,'PUERTO GAITAN',5020,'502057\r'),(3355,54172,'CHINACOTA',5410,'541077\r'),(3356,54001,'CUCUTA',5400,'540018\r'),(3357,68307,'GIRON',6875,'687541\r'),(3358,23660,'SAHAGUN',2325,'232549\r'),(3359,13074,'BARRANCO DE LOBA',1335,'133517\r'),(3360,99624,'SANTA ROSALIA',9950,'995009\r'),(3361,11001,'LOCALIDAD SANTA FE',1103,'110321\r'),(3362,13657,'SAN JUAN NEPOMUCENO',1320,'132010\r'),(3363,23555,'PLANETA RICA',2330,'233047\r'),(3364,20001,'VALLEDUPAR',2000,'200005\r'),(3365,5129,'CALDAS',554,'055447\r'),(3366,25386,'LA MESA',2526,'252601\r'),(3367,5034,'ANDES',560,'056060\r'),(3368,17042,'ANSERMA',1770,'177088\r'),(3369,95025,'EL RETORNO',9510,'951009\r'),(3370,52240,'CHACHAGÃI',5220,'522001\r'),(3371,23555,'PLANETA RICA',2330,'233057\r'),(3372,41396,'LA PLATA',4150,'415068\r'),(3373,5628,'SABANALARGA',570,'057027\r'),(3374,41615,'RIVERA',4130,'413007\r'),(3375,18592,'PUERTO RICO',1820,'182059\r'),(3376,25862,'VERGARA',2536,'253657\r'),(3377,86573,'LEGUIZAMO',8640,'864001\r'),(3378,73870,'VILLAHERMOSA',7315,'731508\r'),(3379,5088,'BELLO',510,'051050\r'),(3380,19743,'SILVIA',1920,'192087\r'),(3381,47170,'CHIBOLO',4760,'476060\r'),(3382,17541,'PENSILVANIA',1730,'173069\r'),(3383,18150,'CARTAGENA DEL CHAIRA',1830,'183018\r'),(3384,15403,'LA UVITA',1508,'150860\r'),(3385,50573,'PUERTO LOPEZ',5020,'502007\r'),(3386,5670,'SAN ROQUE',530,'053030\r'),(3387,52051,'ARBOLEDA - BERRUECOS',5205,'520578\r'),(3388,76126,'CALIMA',7605,'760537\r'),(3389,23678,'SAN CARLOS',2325,'232501\r'),(3390,20770,'SAN MARTIN',2050,'205059\r'),(3391,5873,'VIGIA DEL FUERTE',568,'056827\r'),(3392,13212,'CORDOBA',1325,'132508\r'),(3393,15022,'ALMEIDA',1530,'153027\r'),(3394,70221,'COVEÃAS',7060,'706050\r'),(3395,50568,'PUERTO GAITAN',5020,'502059\r'),(3396,13683,'SANTA ROSA',1305,'130520\r'),(3397,5789,'TAMESIS',560,'056028\r'),(3398,17662,'SAMANA',1740,'174009\r'),(3399,19418,'LOPEZ',1960,'196067\r'),(3400,8685,'SANTO TOMAS',830,'083067\r'),(3401,50680,'SAN CARLOS DE GUAROA',5070,'507011\r'),(3402,81220,'CRAVO NORTE',8120,'812018\r'),(3403,15368,'JERICO',1508,'150847\r'),(3404,41378,'LA ARGENTINA',4150,'415088\r'),(3405,70418,'LOS PALMITOS',7010,'701058\r'),(3406,54174,'CHITAGA',5440,'544038\r'),(3407,73268,'ESPINAL',7335,'733528\r'),(3408,15104,'BOYACA',1536,'153610\r'),(3409,23001,'MONTERIA',2300,'230004\r'),(3410,76233,'DAGUA',7605,'760520\r'),(3411,25799,'TENJO',2502,'250201\r'),(3412,15236,'CHIVOR',1530,'153007\r'),(3413,27745,'SIPI',2740,'274057\r'),(3414,15232,'CHIQUIZA',1540,'154020\r'),(3415,25430,'MADRID',2500,'250038\r'),(3416,8758,'SOLEDAD',830,'083002\r'),(3417,73408,'LERIDA',7310,'731020\r'),(3418,20443,'MANAURE',2020,'202001\r'),(3419,52786,'TAMINANGO',5215,'521568\r'),(3420,47545,'PIJIÃO DEL CARMEN',4740,'474047\r'),(3421,76250,'EL DOVIO',7615,'761560\r'),(3422,15690,'SANTA MARIA',1528,'152827\r'),(3423,52621,'ROBERTO PAYAN',5280,'528038\r'),(3424,73148,'CARMEN DE APICALA',7335,'733597\r'),(3425,94887,'PANA PANA',9430,'943017\r'),(3426,15542,'PESCA',1524,'152460\r'),(3427,8001,'BARRANQUILLA',800,'080013\r'),(3428,41770,'SUAZA',4160,'416089\r'),(3429,85125,'HATO COROZAL',8520,'852018\r'),(3430,68418,'LOS SANTOS',6840,'684009\r'),(3431,68167,'CHARALA',6825,'682558\r'),(3432,54313,'GRAMALOTE',5450,'545057\r'),(3433,19355,'INZA',1925,'192538\r'),(3434,95015,'CALAMAR',9530,'953017\r'),(3435,68001,'BUCARAMANGA',6800,'680006\r'),(3436,76895,'ZARZAL',7625,'762528\r'),(3437,19513,'PADILLA',1915,'191547\r'),(3438,5665,'SAN PEDRO DE URABA',578,'057838\r'),(3439,8433,'MALAMBO',830,'083028\r'),(3440,5659,'SAN JUAN DE URABA',578,'057810\r'),(3441,73001,'IBAGUE',7300,'730007\r'),(3442,52287,'FUNES',5235,'523520\r'),(3443,41551,'PITALITO',4170,'417047\r'),(3444,52354,'IMUES',5230,'523028\r'),(3445,50325,'MAPIRIPAN',5030,'503028\r'),(3446,52323,'GUALMATAN',5245,'524501\r'),(3447,15763,'SOTAQUIRA',1504,'150428\r'),(3448,86320,'ORITO',8620,'862007\r'),(3449,15187,'CHIVATA',1502,'150240\r'),(3450,15403,'LA UVITA',1508,'150867\r'),(3451,25740,'SIBATE',2500,'250070\r'),(3452,76001,'CALI',7600,'760012\r'),(3453,76400,'LA UNION',7615,'761547\r'),(3454,5664,'SAN PEDRO DE LOS MILAGROS',510,'051017\r'),(3455,52786,'TAMINANGO',5215,'521567\r'),(3456,94343,'BARRANCO MINAS',9440,'944017\r'),(3457,25297,'GACHETA',2512,'251238\r'),(3458,15861,'VENTAQUEMADA',1536,'153640\r'),(3459,68169,'CHARTA',6805,'680551\r'),(3460,50400,'LEJANIAS',5060,'506067\r'),(3461,47798,'TENERIFE',4750,'475050\r'),(3462,20570,'PUEBLO BELLO',2010,'201008\r'),(3463,15832,'TUNUNGUA',1546,'154680\r'),(3464,52110,'BUESACO',5205,'520508\r'),(3465,25279,'FOMEQUE',2516,'251640\r'),(3466,52694,'SAN PEDRO DE CARTAGO',5215,'521507\r'),(3467,52835,'SAN ANDRES DE TUMACO',5285,'528529\r'),(3468,68324,'GUAVATA',6845,'684501\r'),(3469,68502,'ONZAGA',6825,'682521\r'),(3470,15667,'SAN LUIS DE GACENO',1528,'152801\r'),(3471,99773,'CUMARIBO',9910,'991028\r'),(3472,91263,'EL ENCANTO',9130,'913018\r'),(3473,68464,'MOGOTES',6825,'682501\r'),(3474,63690,'SALENTO',6310,'631027\r'),(3475,15466,'MONGUI',1522,'152201\r'),(3476,5129,'CALDAS',554,'055448\r'),(3477,68079,'BARICHARA',6840,'684047\r'),(3478,15572,'PUERTO BOYACA',1552,'155207\r'),(3479,68190,'CIMITARRA',6860,'686048\r'),(3480,76109,'BUENAVENTURA',7645,'764502\r'),(3481,41319,'GUADALUPE',4160,'416040\r'),(3482,5591,'PUERTO TRIUNFO',534,'053448\r'),(3483,73671,'SALDAÃA',7335,'733570\r'),(3484,25486,'NEMOCON',2510,'251037\r'),(3485,95025,'EL RETORNO',9510,'951017\r'),(3486,76246,'EL CAIRO',7615,'761507\r'),(3487,5360,'ITAGÃI',554,'055412\r'),(3488,15820,'TOPAGA',1520,'152040\r'),(3489,27372,'JURADO',2760,'276010\r'),(3490,27810,'UNION PANAMERICANA',2720,'272030\r'),(3491,17380,'LA DORADA',1750,'175031\r'),(3492,5756,'SONSON',548,'054820\r'),(3493,18753,'SAN VICENTE DEL CAGUAN',1820,'182010\r'),(3494,25040,'ANOLAIMA',2530,'253048\r'),(3495,25086,'BELTRAN',2532,'253267\r'),(3496,66170,'DOSQUEBRADAS',6610,'661008\r'),(3497,52427,'MAGÃI',5280,'528001\r'),(3498,68169,'CHARTA',6805,'680557\r'),(3499,68250,'EL PEÃON',6850,'685027\r'),(3500,5120,'CACERES',524,'052457\r'),(3501,25878,'VIOTA',2526,'252668\r'),(3502,23001,'MONTERIA',2300,'230008\r'),(3503,25799,'TENJO',2502,'250208\r'),(3504,23807,'TIERRALTA',2345,'234509\r'),(3505,68318,'GUACA',6810,'681047\r'),(3506,27372,'JURADO',2760,'276017\r'),(3507,23162,'CERETE',2305,'230559\r'),(3508,25473,'MOSQUERA',2500,'250040\r'),(3509,17873,'VILLAMARIA',1760,'176008\r'),(3510,19807,'TIMBIO',1935,'193528\r'),(3511,15681,'SAN PABLO DE BORBUR',1550,'155040\r'),(3512,76109,'BUENAVENTURA',7645,'764508\r'),(3513,8141,'CANDELARIA',840,'084020\r'),(3514,52835,'SAN ANDRES DE TUMACO',5285,'528501\r'),(3515,68820,'TONA',6805,'680548\r'),(3516,73270,'FALAN',7320,'732008\r'),(3517,19760,'SOTARA',1935,'193501\r'),(3518,5679,'SANTA BARBARA',550,'055058\r'),(3519,25398,'LA PEÃA',2536,'253648\r'),(3520,85400,'TAMARA',8510,'851050\r'),(3521,25524,'PANDI',2520,'252017\r'),(3522,13650,'SAN FERNANDO',1330,'133008\r'),(3523,47692,'SAN SEBASTIAN DE BUENAVISTA',4730,'473007\r'),(3524,19622,'ROSAS',1935,'193558\r'),(3525,76001,'CALI',7600,'760025\r'),(3526,41551,'PITALITO',4170,'417048\r'),(3527,27787,'TADO',2710,'271077\r'),(3528,76001,'CALI',7600,'760032\r'),(3529,52250,'EL CHARCO',5275,'527528\r'),(3530,19573,'PUERTO TEJADA',1915,'191507\r'),(3531,19532,'PATIA',1955,'195509\r'),(3532,95015,'CALAMAR',9530,'953001\r'),(3533,25175,'CHIA',2500,'250001\r'),(3534,5364,'JARDIN',560,'056050\r'),(3535,23586,'PURISIMA',2315,'231547\r'),(3536,68547,'PIEDECUESTA',6810,'681011\r'),(3537,5250,'EL BAGRE',524,'052430\r'),(3538,76001,'CALI',7600,'760046\r'),(3539,20011,'AGUACHICA',2050,'205018\r'),(3540,85230,'OROCUE',8530,'853050\r'),(3541,50001,'VILLAVICENCIO',5000,'500009\r'),(3542,5353,'HISPANIA',564,'056450\r'),(3543,23466,'MONTELIBANO',2340,'234008\r'),(3544,47692,'SAN SEBASTIAN DE BUENAVISTA',4730,'473008\r'),(3545,15764,'SORACA',1534,'153480\r'),(3546,81591,'PUERTO RONDON',8130,'813018\r'),(3547,47980,'ZONA BANANERA',4780,'478028\r'),(3548,91001,'LETICIA',9100,'910008\r'),(3549,23500,'MOÃITOS',2310,'231008\r'),(3550,13001,'CARTAGENA',1300,'130004\r'),(3551,25649,'SAN BERNARDO',2520,'252027\r'),(3552,50686,'SAN JUANITO',5010,'501051\r'),(3553,5093,'BETULIA',568,'056868\r'),(3554,5091,'BETANIA',560,'056070\r'),(3555,19001,'POPAYAN',1900,'190007\r'),(3556,8849,'USIACURI',820,'082060\r'),(3557,5154,'CAUCASIA',524,'052410\r'),(3558,54239,'DURANIA',5445,'544510\r'),(3559,52835,'SAN ANDRES DE TUMACO',5285,'528503\r'),(3560,27787,'TADO',2710,'271070\r'),(3561,5665,'SAN PEDRO DE URABA',578,'057830\r'),(3562,5361,'ITUANGO',520,'052078\r'),(3563,23682,'SAN JOSE DE URE',2340,'234017\r'),(3564,27073,'BAGADO',2710,'271057\r'),(3565,8001,'BARRANQUILLA',800,'080014\r'),(3566,41001,'NEIVA',4100,'410018\r'),(3567,91407,'LA PEDRERA',9170,'917018\r'),(3568,86001,'MOCOA',8600,'860007\r'),(3569,52696,'SANTA BARBARA',5275,'527507\r'),(3570,8758,'SOLEDAD',830,'083006\r'),(3571,41078,'BARAYA',4110,'411067\r'),(3572,52287,'FUNES',5235,'523528\r'),(3573,54810,'TIBU',5480,'548018\r'),(3574,13433,'MAHATES',1310,'131047\r'),(3575,73770,'SUAREZ',7335,'733580\r'),(3576,17272,'FILADELFIA',1710,'171027\r'),(3577,47745,'SITIONUEVO',4770,'477007\r'),(3578,11001,'LOCALIDAD KENNEDY',1108,'110831\r'),(3579,20710,'SAN ALBERTO',2050,'205077\r'),(3580,15001,'TUNJA',1500,'150001\r'),(3581,50006,'ACACIAS',5070,'507009\r'),(3582,5148,'EL CARMEN DE VIBORAL',540,'054037\r'),(3583,99773,'CUMARIBO',9910,'991037\r'),(3584,76109,'BUENAVENTURA',7645,'764501\r'),(3585,68498,'OCAMONTE',6825,'682561\r'),(3586,20443,'MANAURE',2020,'202007\r'),(3587,66318,'GUATICA',6640,'664010\r'),(3588,17013,'AGUADAS',1720,'172020\r'),(3589,76246,'EL CAIRO',7615,'761508\r'),(3590,27099,'BOJAYA',2770,'277057\r'),(3591,5001,'MEDELLIN',500,'050035\r'),(3592,15299,'GARAGOA',1528,'152860\r'),(3593,25817,'TOCANCIPA',2510,'251017\r'),(3594,11001,'LOCALIDAD CHAPINERO',1102,'110211\r'),(3595,68549,'PINCHOTE',6835,'683518\r'),(3596,76248,'EL CERRITO',7635,'763528\r'),(3597,52678,'SAMANIEGO',5260,'526048\r'),(3598,95015,'CALAMAR',9530,'953018\r'),(3599,44847,'URIBIA',4410,'441028\r'),(3600,15480,'MUZO',1548,'154880\r'),(3601,19142,'CALOTO',1910,'191070\r'),(3602,47605,'REMOLINO',4770,'477027\r'),(3603,47001,'SANTA MARTA',4700,'470006\r'),(3604,5055,'ARGELIA',548,'054830\r'),(3605,73148,'CARMEN DE APICALA',7335,'733590\r'),(3606,25483,'NARIÃO',2528,'252837\r'),(3607,13688,'SANTA ROSA DEL SUR',1350,'135001\r'),(3608,20295,'GAMARRA',2050,'205001\r'),(3609,85015,'CHAMEZA',8560,'856037\r'),(3610,76670,'SAN PEDRO',7630,'763030\r'),(3611,5858,'VEGACHI',528,'052830\r'),(3612,25328,'GUAYABAL DE SIQUIMA',2532,'253210\r'),(3613,5649,'SAN CARLOS',544,'054420\r'),(3614,95015,'CALAMAR',9530,'953008\r'),(3615,25885,'YACOPI',2538,'253848\r'),(3616,25293,'GACHALA',2512,'251250\r'),(3617,5756,'SONSON',548,'054829\r'),(3618,17433,'MANZANARES',1730,'173027\r'),(3619,52699,'SANTACRUZ',5255,'525578\r'),(3620,17777,'SUPIA',1780,'178027\r'),(3621,91536,'PUERTO ARICA',9120,'912017\r'),(3622,47258,'EL PIÃON',4760,'476001\r'),(3623,86865,'VALLE DEL GUAMUEZ',8620,'862027\r'),(3624,18247,'EL DONCELLO',1810,'181018\r'),(3625,5501,'OLAYA',514,'051457\r'),(3626,76113,'BUGALAGRANDE',7630,'763007\r'),(3627,25183,'CHOCONTA',2508,'250808\r'),(3628,5042,'SANTAFE DE ANTIOQUIA',570,'057057\r'),(3629,15109,'BUENAVISTA',1548,'154848\r'),(3630,52356,'IPIALES',5240,'524068\r'),(3631,15317,'GUACAMAYAS',1512,'151227\r'),(3632,86001,'MOCOA',8600,'860008\r'),(3633,99001,'PUERTO CARREÃO',9900,'990017\r'),(3634,68245,'EL GUACAMAYO',6830,'683067\r'),(3635,81736,'SARAVENA',8150,'815018\r'),(3636,25758,'SOPO',2510,'251007\r'),(3637,76497,'OBANDO',7625,'762507\r'),(3638,52317,'GUACHUCAL',5245,'524580\r'),(3639,27001,'QUIBDO',2700,'270008\r'),(3640,5652,'SAN FRANCISCO',548,'054818\r'),(3641,15673,'SAN MATEO',1512,'151207\r'),(3642,18256,'EL PAUJIL',1810,'181030\r'),(3643,18001,'FLORENCIA',1800,'180008\r'),(3644,52354,'IMUES',5230,'523020\r'),(3645,25183,'CHOCONTA',2508,'250807\r'),(3646,18247,'EL DONCELLO',1810,'181019\r'),(3647,5656,'SAN JERONIMO',510,'051077\r'),(3648,76054,'ARGELIA',7615,'761510\r'),(3649,15090,'BERBEO',1526,'152610\r'),(3650,5761,'SOPETRAN',514,'051440\r'),(3651,50450,'PUERTO CONCORDIA',5030,'503047\r'),(3652,5266,'ENVIGADO',554,'055428\r'),(3653,25785,'TABIO',2502,'250230\r'),(3654,76036,'ANDALUCIA',7630,'763017\r'),(3655,18785,'SOLITA',1850,'185078\r'),(3656,15599,'RAMIRIQUI',1534,'153401\r'),(3657,5837,'TURBO',578,'057867\r'),(3658,8078,'BARANOA',820,'082028\r'),(3659,41807,'TIMANA',4170,'417017\r'),(3660,25873,'VILLAPINZON',2508,'250817\r'),(3661,19075,'BALBOA',1955,'195537\r'),(3662,5190,'CISNEROS',530,'053057\r'),(3663,13442,'MARIA LA BAJA',1310,'131060\r'),(3664,68271,'FLORIAN',6845,'684547\r'),(3665,13001,'CARTAGENA',1300,'130010\r'),(3666,15212,'COPER',1548,'154867\r'),(3667,5792,'TARSO',564,'056437\r'),(3668,5212,'COPACABANA',510,'051040\r'),(3669,13473,'MORALES',1345,'134540\r'),(3670,25339,'GUTIERREZ',2518,'251867\r'),(3671,23168,'CHIMA',2320,'232018\r'),(3672,85430,'TRINIDAD',8530,'853019\r'),(3673,68673,'SAN BENITO',6855,'685537\r'),(3674,15232,'CHIQUIZA',1540,'154027\r'),(3675,52079,'BARBACOAS',5280,'528077\r'),(3676,11001,'LOCALIDAD USAQUEN',1101,'110141\r'),(3677,15580,'QUIPAMA',1550,'155020\r'),(3678,68132,'CALIFORNIA',6805,'680511\r'),(3679,47703,'SAN ZENON',4740,'474060\r'),(3680,41807,'TIMANA',4170,'417010\r'),(3681,41020,'ALGECIRAS',4130,'413048\r');






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

--
-- Table structure for table `comprobante_egreso_medio_pago`
--

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

--
-- Dumping data for table `comprobantes`
--


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

--
-- Table structure for table `comprobantes_egreso`
--

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

--
-- Table structure for table `conceptos_abiertos`
--

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

--
-- Table structure for table `configuracion_compras`
--

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

--
-- Table structure for table `configuracion_contable_mapa`
--

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

--
-- Table structure for table `contactos_tercero`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cotizaciones`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuentas_bancarias`
--

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

--
-- Table structure for table `cuentas_contables`
--

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



INSERT INTO `cuentas_contables` VALUES (93292,'4295','Otros ingresos no operacionales','INGRESO',5,NULL,1,'ACTIVO','2026-05-03 20:22:20',0,0,0),(93293,'5135','Gastos varios','GASTO',5,NULL,1,'ACTIVO','2026-05-03 20:22:20',0,0,0),(93294,'1105','Caja general','ACTIVO',5,NULL,0,'ACTIVO','2026-05-03 20:22:20',0,0,0),(93295,'5105','Sueldos y salarios','GASTO',5,NULL,1,'ACTIVO','2026-05-03 20:22:20',0,0,0),(93296,'1','ACTIVO','ACTIVO',1,NULL,0,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93297,'11','DISPONIBLE','ACTIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93298,'110505','Caja general','ACTIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93299,'110510','Cajas menores','ACTIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93300,'110515','Moneda extranjera','ACTIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93301,'1110','BANCOS','ACTIVO',3,NULL,0,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93302,'111005','Moneda nacional','ACTIVO',4,NULL,0,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93303,'111010','Moneda extranjera (bancos)','ACTIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93304,'1115','REMESAS EN TRANSITO','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93305,'1120','CUENTAS DE AHORRO','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93306,'1125','FONDOS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93307,'12','INVERSIONES','ACTIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93308,'1205','Acciones','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93309,'1215','Bonos','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93310,'1225','Certificados','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93311,'1230','Papeles comerciales','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93312,'1235','Titulos','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93313,'1295','Otras inversiones','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93314,'13','DEUDORES','ACTIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93315,'1305','CLIENTES','ACTIVO',3,NULL,0,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93316,'130505','Clientes nacionales','ACTIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93317,'130510','Clientes del exterior','ACTIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93318,'1310','CUENTAS CORRIENTES COMERCIALES','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93319,'1315','CUENTAS POR COBRAR A CASA MATRIZ','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93320,'1320','CUENTAS POR COBRAR A VINCULADOS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93321,'1325','CUENTAS POR COBRAR A SOCIOS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93322,'1330','ANTICIPOS Y AVANCES','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93323,'1335','DEPOSITOS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93324,'1345','INGRESOS POR COBRAR','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93325,'1355','ANTICIPO DE IMPUESTOS Y CONTRIBUCIONES','ACTIVO',3,NULL,0,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93326,'135515','Retencion en la fuente','ACTIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93327,'135517','Impuesto a las ventas retenido','ACTIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93328,'135518','Impuesto de industria y comercio retenido','ACTIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93329,'1365','CUENTAS POR COBRAR A TRABAJADORES','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93330,'1380','DEUDORES VARIOS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93331,'1399','PROVISIONES DEUDORES','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',1,0,0),(93332,'14','INVENTARIOS','ACTIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93333,'1405','MATERIAS PRIMAS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93334,'1410','PRODUCTOS EN PROCESO','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93335,'1430','PRODUCTOS TERMINADOS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93336,'1435','MERCANCIAS NO FABRICADAS POR LA EMPRESA','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93337,'1455','MATERIALES REPUESTOS Y ACCESORIOS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93338,'1460','ENVASES Y EMPAQUES','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93339,'1465','INVENTARIOS EN TRANSITO','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93340,'15','PROPIEDADES PLANTA Y EQUIPO','ACTIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93341,'1504','TERRENOS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93342,'1516','CONSTRUCCIONES Y EDIFICACIONES','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93343,'1520','MAQUINARIA Y EQUIPO','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93344,'1524','EQUIPO DE OFICINA','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93345,'1528','EQUIPO DE COMPUTACION Y COMUNICACION','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93346,'1540','FLOTA Y EQUIPO DE TRANSPORTE','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93347,'1592','DEPRECIACION ACUMULADA','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93348,'16','INTANGIBLES','ACTIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93349,'1605','CREDITO MERCANTIL','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93350,'1610','MARCAS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93351,'1615','PATENTES','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93352,'1635','LICENCIAS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93353,'17','DIFERIDOS','ACTIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93354,'1705','GASTOS PAGADOS POR ANTICIPADO','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93355,'1710','CARGOS DIFERIDOS','ACTIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:18',0,0,0),(93357,'2','PASIVO','PASIVO',1,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93358,'21','OBLIGACIONES FINANCIERAS','PASIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93359,'2105','BANCOS NACIONALES','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93360,'2110','BANCOS DEL EXTERIOR','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93361,'2115','CORPORACIONES FINANCIERAS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93362,'2120','COMPANIAS DE FINANCIAMIENTO COMERCIAL','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93363,'2195','OTRAS OBLIGACIONES','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93364,'22','PROVEEDORES','PASIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93365,'2205','NACIONALES','PASIVO',3,NULL,0,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93366,'220505','Proveedores nacionales','PASIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93367,'2210','DEL EXTERIOR','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93368,'2215','CUENTAS CORRIENTES COMERCIALES (Prov.)','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93369,'23','CUENTAS POR PAGAR','PASIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93370,'2305','CUENTAS CORRIENTES COMERCIALES (CxP)','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93371,'2320','A CONTRATISTAS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93372,'2335','COSTOS Y GASTOS POR PAGAR','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93373,'2355','DEUDAS CON ACCIONISTAS O SOCIOS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93374,'2365','RETENCION EN LA FUENTE','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93375,'2367','IMPUESTO A LAS VENTAS RETENIDO','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93376,'2368','IMPUESTO DE INDUSTRIA Y COMERCIO RETENIDO','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93377,'2370','RETENCIONES Y APORTES DE NOMINA','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93378,'2380','ACREEDORES VARIOS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93379,'24','IMPUESTOS GRAVAMENES Y TASAS','PASIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93380,'2404','DE RENTA Y COMPLEMENTARIOS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93381,'2408','IMPUESTO SOBRE LAS VENTAS POR PAGAR','PASIVO',3,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93382,'240801','IVA generado','PASIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93383,'240810','IVA descontable','PASIVO',4,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93384,'2412','DE INDUSTRIA Y COMERCIO','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93385,'2495','OTROS IMPUESTOS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93386,'25','OBLIGACIONES LABORALES','PASIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93387,'2505','SALARIOS POR PAGAR','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93388,'2510','CESANTIAS CONSOLIDADAS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93389,'2515','INTERESES SOBRE CESANTIAS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93390,'2520','PRIMA DE SERVICIOS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93391,'2525','VACACIONES CONSOLIDADAS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93392,'26','PASIVOS ESTIMADOS Y PROVISIONES','PASIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93393,'2605','PARA COSTOS Y GASTOS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93394,'2610','PARA OBLIGACIONES LABORALES','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93395,'27','DIFERIDOS','PASIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93396,'2705','INGRESOS RECIBIDOS POR ANTICIPADO','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93397,'28','OTROS PASIVOS','PASIVO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93398,'2805','ANTICIPOS Y AVANCES RECIBIDOS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93399,'2810','DEPOSITOS RECIBIDOS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',1,0,0),(93400,'2895','DIVERSOS','PASIVO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93401,'3','PATRIMONIO','PATRIMONIO',1,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93402,'31','CAPITAL SOCIAL','PATRIMONIO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93403,'3105','CAPITAL SUSCRITO Y PAGADO','PATRIMONIO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93404,'3115','APORTES SOCIALES','PATRIMONIO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93405,'33','RESERVAS','PATRIMONIO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93406,'3305','RESERVAS OBLIGATORIAS','PATRIMONIO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93407,'36','RESULTADOS DEL EJERCICIO','PATRIMONIO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93408,'3605','UTILIDAD DEL EJERCICIO','PATRIMONIO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93409,'3610','PERDIDA DEL EJERCICIO','PATRIMONIO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93410,'37','RESULTADOS DE EJERCICIOS ANTERIORES','PATRIMONIO',2,NULL,0,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93411,'3705','UTILIDADES ACUMULADAS','PATRIMONIO',3,NULL,1,'ACTIVO','2026-05-19 09:48:59',0,0,0),(93412,'4','INGRESOS','INGRESO',1,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93413,'41','OPERACIONALES','INGRESO',2,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93414,'4105','AGRICULTURA GANADERIA','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93415,'4120','INDUSTRIAS MANUFACTURERAS','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93416,'4130','CONSTRUCCION','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93417,'4135','COMERCIO AL POR MAYOR Y AL POR MENOR','INGRESO',3,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93418,'413505','Venta de mercancias','INGRESO',4,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93419,'413510','Comercio al detal','INGRESO',4,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93420,'4140','HOTELES Y RESTAURANTES','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93421,'4145','TRANSPORTE ALMACENAMIENTO Y COMUNICACIONES','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93422,'4150','ACTIVIDAD FINANCIERA','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93423,'4155','ACTIVIDADES INMOBILIARIAS','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93424,'4160','ENSENANZA','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93425,'4165','SERVICIOS SOCIALES Y DE SALUD','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93426,'4170','OTRAS ACTIVIDADES DE SERVICIOS','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93427,'4175','DEVOLUCIONES EN VENTAS','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93428,'42','NO OPERACIONALES','INGRESO',2,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93429,'4205','OTRAS VENTAS','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93430,'4210','FINANCIEROS','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93431,'4215','DIVIDENDOS Y PARTICIPACIONES','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93432,'4220','ARRENDAMIENTOS (Ingresos)','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93433,'4225','COMISIONES','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93434,'4230','HONORARIOS (Ingresos)','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93435,'4235','SERVICIOS (Ingresos)','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93436,'4255','RECUPERACIONES','INGRESO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93437,'5','GASTOS','GASTO',1,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93438,'51','OPERACIONALES DE ADMINISTRACION','GASTO',2,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93439,'5110','HONORARIOS','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93440,'5115','IMPUESTOS (Gastos)','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93441,'5120','ARRENDAMIENTOS','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93442,'5125','CONTRIBUCIONES Y AFILIACIONES','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93443,'5130','SEGUROS','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93444,'5140','GASTOS LEGALES','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93445,'5145','MANTENIMIENTO Y REPARACIONES','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93446,'5150','ADECUACION E INSTALACION','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93447,'5155','GASTOS DE VIAJE','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93448,'5160','DEPRECIACIONES','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93449,'5165','AMORTIZACIONES','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93450,'5195','DIVERSOS (Gastos)','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93451,'52','OPERACIONALES DE VENTAS','GASTO',2,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93452,'5205','GASTOS DE PERSONAL (Ventas)','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93453,'5235','SERVICIOS (Ventas)','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93454,'5295','DIVERSOS (Ventas)','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93455,'53','NO OPERACIONALES','GASTO',2,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93456,'5305','FINANCIEROS (Gastos)','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93457,'5310','PERDIDAS EN VENTA Y RETIRO DE BIENES','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93458,'5315','GASTOS EXTRAORDINARIOS','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93459,'5395','GASTOS DIVERSOS NO OPERACIONALES','GASTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93460,'54','IMPUESTO DE RENTA Y COMPLEMENTARIOS','GASTO',2,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93461,'6','COSTOS DE VENTAS','COSTO',1,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93462,'61','COSTO DE VENTAS Y SERVICIOS','COSTO',2,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93463,'6135','COMERCIO AL POR MAYOR Y AL POR MENOR (Costos)','COSTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93464,'62','COMPRAS','COSTO',2,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93465,'6205','DE MERCANCIAS','COSTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93466,'6210','DE MATERIA PRIMA','COSTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93467,'6225','DEVOLUCIONES EN COMPRAS','COSTO',3,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93468,'7','COSTOS DE PRODUCCION','COSTO_PRODUCCION',1,NULL,0,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93469,'71','MATERIA PRIMA','COSTO_PRODUCCION',2,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93470,'72','MANO DE OBRA DIRECTA','COSTO_PRODUCCION',2,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93471,'73','COSTOS INDIRECTOS','COSTO_PRODUCCION',2,NULL,1,'ACTIVO','2026-05-19 09:49:36',0,0,0),(93475,'11100501','Bancolombia Principal','ACTIVO',5,93302,1,'ACTIVO','2026-05-20 10:38:49',0,0,0),(93476,'11100502','Davivienda Corriente','ACTIVO',5,93302,1,'ACTIVO','2026-05-20 10:38:49',0,0,0),(93477,'11100503','Nequi','ACTIVO',5,93302,1,'ACTIVO','2026-05-20 10:38:49',0,0,0),(93478,'11100504','Daviplata','ACTIVO',5,93302,1,'ACTIVO','2026-05-20 10:38:49',0,0,0),(93479,'236505','Retenciâân en la fuente','PASIVO',5,NULL,1,'ACTIVO','2026-05-25 19:36:06',1,0,0),(93480,'236540','Retenciâân de IVA','PASIVO',5,NULL,1,'ACTIVO','2026-05-25 19:36:06',1,0,0),(93481,'236570','Retenciâân de ICA','PASIVO',5,NULL,1,'ACTIVO','2026-05-25 19:36:06',1,0,0),(93482,'8','CUENTAS DE ORDEN DEUDORAS','ORDEN_DEUDORAS',1,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93483,'81','DERECHOS CONTINGENTES','ORDEN_DEUDORAS',2,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93484,'8105','BIENES Y VALORES ENTREGADOS EN CUSTODIA','ORDEN_DEUDORAS',3,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93485,'810505','Bienes y valores entregados en custodia','ORDEN_DEUDORAS',4,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93486,'8110','BIENES Y VALORES ENTREGADOS EN GARANTIA','ORDEN_DEUDORAS',3,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93487,'811005','Bienes y valores entregados en garantia','ORDEN_DEUDORAS',4,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93488,'8120','LITIGIOS Y/O DEMANDAS','ORDEN_DEUDORAS',3,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93489,'812005','Litigios y/o demandas','ORDEN_DEUDORAS',4,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93490,'83','DEUDORAS DE CONTROL','ORDEN_DEUDORAS',2,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93491,'8315','PROPIEDADES PLANTA Y EQUIPO TOTALMENTE DEPRECIADAS','ORDEN_DEUDORAS',3,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93492,'831505','Propiedad planta y equipo totalmente depreciada','ORDEN_DEUDORAS',4,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93493,'84','DERECHOS CONTINGENTES POR CONTRA (CR)','ORDEN_DEUDORAS',2,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93494,'8405','Derechos contingentes por contra','ORDEN_DEUDORAS',3,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93495,'9','CUENTAS DE ORDEN ACREEDORAS','ORDEN_ACREEDORAS',1,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93496,'91','RESPONSABILIDADES CONTINGENTES','ORDEN_ACREEDORAS',2,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93497,'9105','BIENES Y VALORES RECIBIDOS EN CUSTODIA','ORDEN_ACREEDORAS',3,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93498,'910505','Bienes y valores recibidos en custodia','ORDEN_ACREEDORAS',4,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93499,'9110','BIENES Y VALORES RECIBIDOS EN GARANTIA','ORDEN_ACREEDORAS',3,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93500,'911005','Bienes y valores recibidos en garantia','ORDEN_ACREEDORAS',4,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93501,'9120','LITIGIOS Y/O DEMANDAS','ORDEN_ACREEDORAS',3,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93502,'912005','Litigios y/o demandas','ORDEN_ACREEDORAS',4,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93503,'93','ACREEDORAS DE CONTROL','ORDEN_ACREEDORAS',2,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93504,'9395','OTRAS CUENTAS DE ORDEN ACREEDORAS DE CONTROL','ORDEN_ACREEDORAS',3,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93505,'939505','Otras cuentas de orden acreedoras de control','ORDEN_ACREEDORAS',4,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93506,'94','RESPONSABILIDADES CONTINGENTES POR CONTRA (DB)','ORDEN_ACREEDORAS',2,NULL,0,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93507,'9405','Responsabilidades contingentes por contra','ORDEN_ACREEDORAS',3,NULL,1,'ACTIVO','2026-07-08 09:26:35',0,0,0),(93509,'421040','Descuentos comerciales condicionados (pronto pago)','INGRESO',6,NULL,1,'ACTIVO','2026-07-14 00:00:00',0,0,0),(93510,'539540','Averias','GASTO',6,NULL,1,'ACTIVO','2026-07-14 00:00:00',0,0,0),(93511,'513535','Fletes','GASTO',6,NULL,1,'ACTIVO','2026-07-14 00:00:00',0,0,0),(93512,'530535','Descuentos comerciales condicionados (concedidos)','GASTO',6,93456,1,'ACTIVO','2026-07-14 12:11:04',0,0,0);

--
-- Table structure for table `cuentas_por_cobrar`
--

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

--
-- Table structure for table `cuentas_por_pagar`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=156 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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



INSERT INTO `departamentos` VALUES (1,169,5,'ANTIOQUIA'),(2,169,8,'ATLANTICO'),(3,169,11,'BOGOTA'),(4,169,13,'BOLIVAR'),(5,169,15,'BOYACA'),(6,169,17,'CALDAS'),(7,169,18,'CAQUETA'),(8,169,19,'CAUCA'),(9,169,20,'CESAR'),(10,169,23,'CORDOBA'),(11,169,25,'CUNDINAMARCA'),(12,169,27,'CHOCO'),(13,169,41,'HUILA'),(14,169,44,'LA GUAJIRA'),(15,169,47,'MAGDALENA'),(16,169,50,'META'),(17,169,52,'NARIÂ¥O'),(18,169,54,'N. DE SANTANDER'),(19,169,63,'QUINDIO'),(20,169,66,'RISARALDA'),(21,169,68,'SANTANDER'),(22,169,70,'SUCRE'),(23,169,73,'TOLIMA'),(24,169,76,'VALLE DEL CAUCA'),(25,169,81,'ARAUCA'),(26,169,85,'CASANARE'),(27,169,86,'PUTUMAYO'),(28,169,88,'SAN ANDRES'),(29,169,91,'AMAZONAS'),(30,169,94,'GUAINIA'),(31,169,95,'GUAVIARE'),(32,169,97,'VAUPES'),(33,169,99,'VICHADA'),(34,0,0,'NA');

--
-- Table structure for table `depreciacion_detalle`
--

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

--
-- Table structure for table `detalle_cajero`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalle_comprobante_egreso`
--

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

--
-- Table structure for table `detalle_devoluciones`
--

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

--
-- Table structure for table `detalle_recibo_caja`
--

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

--
-- Table structure for table `detalles_cotizacion`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalles_devolucion_compra`
--

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

--
-- Table structure for table `detalles_devolucion_venta`
--

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

--
-- Table structure for table `detalles_orden_compra`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=209 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalles_pedido`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalles_venta`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=185 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `devoluciones_compra`
--

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

--
-- Table structure for table `devoluciones_venta`
--

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

--
-- Table structure for table `documentos_electronicos`
--

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

--
-- Table structure for table `empresa`
--

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

--
-- Table structure for table `existencias`
--

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

--
-- Table structure for table `facturas`
--

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
  CONSTRAINT `facturas_ibfk_1` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`comprobante_id`),
  CONSTRAINT `facturas_ibfk_2` FOREIGN KEY (`tercero_id`) REFERENCES `terceros` (`tercero_id`),
  CONSTRAINT `facturas_ibfk_3` FOREIGN KEY (`usuario_ingreso_id`) REFERENCES `usuarios` (`codigo`),
  CONSTRAINT `facturas_ibfk_4` FOREIGN KEY (`vendedor_id`) REFERENCES `vendedores` (`vendedor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93346 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  UNIQUE KEY `uq_grupos_descripcion` (`descripcion`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;



INSERT INTO `impuestos` VALUES (1,'IMPUESTO SOBRE LAS VENTAS',5,0,'IVA','ACTIVO'),(2,'IMPUESTO SOBRE LAS VENTAS',19,0,'IVA','ACTIVO'),(3,'IMPUESTO NACIONAL AL CONSUMIDOR',8,0,'INC','ACTIVO'),(4,'EXENTO DE IVA',0,0,'EXN','ACTIVO'),(5,'EXCLUIDO DE IVA',-1,0,'EXC','ACTIVO'),(6,'IMPUESTO NACIONAL AL CONSUMO DE BOLSAS PLASTICAS',70,0,'INCBP','ACTIVO');

--
-- Table structure for table `item_transacciones`
--

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

--
-- Table structure for table `kardex`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `legalizaciones`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  UNIQUE KEY `uq_lineas_descripcion` (`descripcion`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `manifestos_importacion`
--

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

--
-- Table structure for table `metodos_pago`
--

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

INSERT INTO `metodos_pago` VALUES (1,'Efectivo','Efc','ACTIVO','Contado','RECIBO,EGRESO,VENTA,COMPRA',NULL,93298),(2,'Transferencia','Transf','ACTIVO','Contado','RECIBO,EGRESO,VENTA,COMPRA',1,93475),(3,'T.Debito','tdb','ACTIVO','Contado','RECIBO,EGRESO,VENTA,COMPRA',1,93475),(93292,'Credito','CRE','ACTIVO','Credito','RECIBO,EGRESO,VENTA,COMPRA',NULL,NULL),(93294,'NEQUI','NQ','ACTIVO','Contado','RECIBO,VENTA',3,93477);

--
-- Table structure for table `metodos_pago_facturas`
--

DROP TABLE IF EXISTS `metodos_pago_facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metodos_pago_facturas` (
  `metodo_pago_factura_id` int NOT NULL AUTO_INCREMENT,
  `metodo_pago_id` int NOT NULL,
  `factura_id` int NOT NULL,
  `valor` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`metodo_pago_factura_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93342 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimiento_cajero`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=282 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimientos_inventario`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimientos_inventario_detalles`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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

INSERT INTO `municipios` VALUES (1,5,1,'MEDELLIN'),(2,5,2,'ABEJORRAL'),(3,5,4,'ABRIAQUI'),(4,5,21,'ALEJANDRIA'),(5,5,30,'AMAGA'),(6,5,31,'AMALFI'),(7,5,34,'ANDES'),(8,5,36,'ANGELOPOLIS'),(9,5,38,'ANGOSTURA'),(10,5,40,'ANORI'),(11,5,42,'SANTAFE DE ANTIOQUIA'),(12,5,44,'ANZA'),(13,5,45,'APARTADO'),(14,5,51,'ARBOLETES'),(15,5,55,'ARGELIA'),(16,5,59,'ARMENIA'),(17,5,79,'BARBOSA'),(18,5,86,'BELMIRA'),(19,5,88,'BELLO'),(20,5,91,'BETANIA'),(21,5,93,'BETULIA'),(22,5,101,'CIUDAD BOLIVAR'),(23,5,107,'BRICEÂ¥O'),(24,5,113,'BURITICA'),(25,5,120,'CACERES'),(26,5,125,'CAICEDO'),(27,5,129,'CALDAS'),(28,5,134,'CAMPAMENTO'),(29,5,138,'CAÂ¥ASGORDAS'),(30,5,142,'CARACOLI'),(31,5,145,'CARAMANTA'),(32,5,147,'CAREPA'),(33,5,148,'EL CARMEN DE VIBORAL'),(34,5,150,'CAROLINA'),(35,5,154,'CAUCASIA'),(36,5,172,'CHIGORODO'),(37,5,190,'CISNEROS'),(38,5,197,'COCORNA'),(39,5,206,'CONCEPCION'),(40,5,209,'CONCORDIA'),(41,5,212,'COPACABANA'),(42,5,234,'DABEIBA'),(43,5,237,'DON MATIAS'),(44,5,240,'EBEJICO'),(45,5,250,'EL BAGRE'),(46,5,264,'ENTRERRIOS'),(47,5,266,'ENVIGADO'),(48,5,282,'FREDONIA'),(49,5,284,'FRONTINO'),(50,5,306,'GIRALDO'),(51,5,308,'GIRARDOTA'),(52,5,310,'GOMEZ PLATA'),(53,5,313,'GRANADA'),(54,5,315,'GUADALUPE'),(55,5,318,'GUARNE'),(56,5,321,'GUATAPE'),(57,5,347,'HELICONIA'),(58,5,353,'HISPANIA'),(59,5,360,'ITAGUI'),(60,5,361,'ITUANGO'),(61,5,364,'JARDIN'),(62,5,368,'JERICO'),(63,5,376,'LA CEJA'),(64,5,380,'LA ESTRELLA'),(65,5,390,'LA PINTADA'),(66,5,400,'LA UNION'),(67,5,411,'LIBORINA'),(68,5,425,'MACEO'),(69,5,440,'MARINILLA'),(70,5,467,'MONTEBELLO'),(71,5,475,'MURINDO'),(72,5,480,'MUTATA'),(73,5,483,'NARIÂ¥O'),(74,5,490,'NECOCLI'),(75,5,495,'NECHI'),(76,5,501,'OLAYA'),(77,5,541,'PEÃOL'),(78,5,543,'PEQUE'),(79,5,576,'PUEBLORRICO'),(80,5,579,'PUERTO BERRIO'),(81,5,585,'PUERTO NARE'),(82,5,591,'PUERTO TRIUNFO'),(83,5,604,'REMEDIOS'),(84,5,607,'RETIRO'),(85,5,615,'RIONEGRO'),(86,5,628,'SABANALARGA'),(87,5,631,'SABANETA'),(88,5,642,'SALGAR'),(89,5,647,'SAN ANDRES DE CUERQUIA'),(90,5,649,'SAN CARLOS'),(91,5,652,'SAN FRANCISCO'),(92,5,656,'SAN JERONIMO'),(93,5,658,'SAN JOSE DE LA MONTAÂ¥A'),(94,5,659,'SAN JUAN DE URABA'),(95,5,660,'SAN LUIS'),(96,5,664,'SAN PEDRO'),(97,5,665,'SAN PEDRO DE URABA'),(98,5,667,'SAN RAFAEL'),(99,5,670,'SAN ROQUE'),(100,5,674,'SAN VICENTE'),(101,5,679,'SANTA BARBARA'),(102,5,686,'SANTA ROSA DE OSOS'),(103,5,690,'SANTO DOMINGO'),(104,5,697,'EL SANTUARIO'),(105,5,736,'SEGOVIA'),(106,5,756,'SONSON'),(107,5,761,'SOPETRAN'),(108,5,789,'TAMESIS'),(109,5,790,'TARAZA'),(110,5,792,'TARSO'),(111,5,809,'TITIRIBI'),(112,5,819,'TOLEDO'),(113,5,837,'TURBO'),(114,5,842,'URAMITA'),(115,5,847,'URRAO'),(116,5,854,'VALDIVIA'),(117,5,856,'VALPARAISO'),(118,5,858,'VEGACHI'),(119,5,861,'VENECIA'),(120,5,873,'VIGIA DEL FUERTE'),(121,5,885,'YALI'),(122,5,887,'YARUMAL'),(123,5,890,'YOLOMBO'),(124,5,893,'YONDO'),(125,5,895,'ZARAGOZA'),(126,8,1,'BARRANQUILLA'),(127,8,78,'BARANOA'),(128,8,137,'CAMPO DE LA CRUZ'),(129,8,141,'CANDELARIA'),(130,8,296,'GALAPA'),(131,8,372,'JUAN DE ACOSTA'),(132,8,421,'LURUACO'),(133,8,433,'MALAMBO'),(134,8,436,'MANATI'),(135,8,520,'PALMAR DE VARELA'),(136,8,549,'PIOJO'),(137,8,558,'POLONUEVO'),(138,8,560,'PONEDERA'),(139,8,573,'PUERTO COLOMBIA'),(140,8,606,'REPELON'),(141,8,634,'SABANAGRANDE'),(142,8,638,'SABANALARGA'),(143,8,675,'SANTA LUCIA'),(144,8,685,'SANTO TOMAS'),(145,8,758,'SOLEDAD'),(146,8,770,'SUAN'),(147,8,832,'TUBARA'),(148,8,849,'USIACURI'),(149,11,1,'BOGOTA, D.C.'),(150,13,1,'CARTAGENA'),(151,13,6,'ACHI'),(152,13,30,'ALTOS DEL ROSARIO'),(153,13,42,'ARENAL'),(154,13,52,'ARJONA'),(155,13,62,'ARROYOHONDO'),(156,13,74,'BARRANCO DE LOBA'),(157,13,140,'CALAMAR'),(158,13,160,'CANTAGALLO'),(159,13,188,'CICUCO'),(160,13,212,'CORDOBA'),(161,13,222,'CLEMENCIA'),(162,13,244,'EL CARMEN DE BOLIVAR'),(163,13,248,'EL GUAMO'),(164,13,268,'EL PEÂ¥ON'),(165,13,300,'HATILLO DE LOBA'),(166,13,430,'MAGANGUE'),(167,13,433,'MAHATES'),(168,13,440,'MARGARITA'),(169,13,442,'MARIA LA BAJA'),(170,13,458,'MONTECRISTO'),(171,13,468,'MOMPOS'),(172,13,490,'NOROSI'),(173,13,473,'MORALES'),(174,13,549,'PINILLOS'),(175,13,580,'REGIDOR'),(176,13,600,'RIO VIEJO'),(177,13,620,'SAN CRISTOBAL'),(178,13,647,'SAN ESTANISLAO'),(179,13,650,'SAN FERNANDO'),(180,13,654,'SAN JACINTO'),(181,13,655,'SAN JACINTO DEL CAUCA'),(182,13,657,'SAN JUAN NEPOMUCENO'),(183,13,667,'SAN MARTIN DE LOBA'),(184,13,670,'SAN PABLO'),(185,13,673,'SANTA CATALINA'),(186,13,683,'SANTA ROSA'),(187,13,688,'SANTA ROSA DEL SUR'),(188,13,744,'SIMITI'),(189,13,760,'SOPLAVIENTO'),(190,13,780,'TALAIGUA NUEVO'),(191,13,810,'TIQUISIO'),(192,13,836,'TURBACO'),(193,13,838,'TURBANA'),(194,13,873,'VILLANUEVA'),(195,13,894,'ZAMBRANO'),(196,15,1,'TUNJA'),(197,15,22,'ALMEIDA'),(198,15,47,'AQUITANIA'),(199,15,51,'ARCABUCO'),(200,15,87,'BELEN'),(201,15,90,'BERBEO'),(202,15,92,'BETEITIVA'),(203,15,97,'BOAVITA'),(204,15,104,'BOYACA'),(205,15,106,'BRICEÂ¥O'),(206,15,109,'BUENAVISTA'),(207,15,114,'BUSBANZA'),(208,15,131,'CALDAS'),(209,15,135,'CAMPOHERMOSO'),(210,15,162,'CERINZA'),(211,15,172,'CHINAVITA'),(212,15,176,'CHIQUINQUIRA'),(213,15,180,'CHISCAS'),(214,15,183,'CHITA'),(215,15,185,'CHITARAQUE'),(216,15,187,'CHIVATA'),(217,15,189,'CIENEGA'),(218,15,204,'COMBITA'),(219,15,212,'COPER'),(220,15,215,'CORRALES'),(221,15,218,'COVARACHIA'),(222,15,223,'CUBARA'),(223,15,224,'CUCAITA'),(224,15,226,'CUITIVA'),(225,15,232,'CHIQUIZA'),(226,15,236,'CHIVOR'),(227,15,238,'DUITAMA'),(228,15,244,'EL COCUY'),(229,15,248,'EL ESPINO'),(230,15,272,'FIRAVITOBA'),(231,15,276,'FLORESTA'),(232,15,293,'GACHANTIVA'),(233,15,296,'GAMEZA'),(234,15,299,'GARAGOA'),(235,15,317,'GUACAMAYAS'),(236,15,322,'GUATEQUE'),(237,15,325,'GUAYATA'),(238,15,332,'GsICAN'),(239,15,362,'IZA'),(240,15,367,'JENESANO'),(241,15,368,'JERICO'),(242,15,377,'LABRANZAGRANDE'),(243,15,380,'LA CAPILLA'),(244,15,401,'LA VICTORIA'),(245,15,403,'LA UVITA'),(246,15,407,'VILLA DE LEYVA'),(247,15,425,'MACANAL'),(248,15,442,'MARIPI'),(249,15,455,'MIRAFLORES'),(250,15,464,'MONGUA'),(251,15,466,'MONGUI'),(252,15,469,'MONIQUIRA'),(253,15,476,'MOTAVITA'),(254,15,480,'MUZO'),(255,15,491,'NOBSA'),(256,15,494,'NUEVO COLON'),(257,15,500,'OICATA'),(258,15,507,'OTANCHE'),(259,15,511,'PACHAVITA'),(260,15,514,'PAEZ'),(261,15,516,'PAIPA'),(262,15,518,'PAJARITO'),(263,15,522,'PANQUEBA'),(264,15,531,'PAUNA'),(265,15,533,'PAYA'),(266,15,537,'PAZ DE RIO'),(267,15,542,'PESCA'),(268,15,550,'PISBA'),(269,15,572,'PUERTO BOYACA'),(270,15,580,'QUIPAMA'),(271,15,599,'RAMIRIQUI'),(272,15,600,'RAQUIRA'),(273,15,621,'RONDON'),(274,15,632,'SABOYA'),(275,15,638,'SACHICA'),(276,15,646,'SAMACA'),(277,15,660,'SAN EDUARDO'),(278,15,664,'SAN JOSE DE PARE'),(279,15,667,'SAN LUIS DE GACENO'),(280,15,673,'SAN MATEO'),(281,15,676,'SAN MIGUEL DE SEMA'),(282,15,681,'SAN PABLO DE BORBUR'),(283,15,686,'SANTANA'),(284,15,690,'SANTA MARIA'),(285,15,693,'SANTA ROSA DE VITERBO'),(286,15,696,'SANTA SOFIA'),(287,15,720,'SATIVANORTE'),(288,15,723,'SATIVASUR'),(289,15,740,'SIACHOQUE'),(290,15,753,'SOATA'),(291,15,755,'SOCOTA'),(292,15,757,'SOCHA'),(293,15,759,'SOGAMOSO'),(294,15,761,'SOMONDOCO'),(295,15,762,'SORA'),(296,15,763,'SOTAQUIRA'),(297,15,764,'SORACA'),(298,15,774,'SUSACON'),(299,15,776,'SUTAMARCHAN'),(300,15,778,'SUTATENZA'),(301,15,790,'TASCO'),(302,15,798,'TENZA'),(303,15,804,'TIBANA'),(304,15,806,'TIBASOSA'),(305,15,808,'TINJACA'),(306,15,810,'TIPACOQUE'),(307,15,814,'TOCA'),(308,15,816,'TOGsI'),(309,15,820,'TOPAGA'),(310,15,822,'TOTA'),(311,15,832,'TUNUNGUA'),(312,15,835,'TURMEQUE'),(313,15,837,'TUTA'),(314,15,839,'TUTAZA'),(315,15,842,'UMBITA'),(316,15,861,'VENTAQUEMADA'),(317,15,879,'VIRACACHA'),(318,15,897,'ZETAQUIRA'),(319,17,1,'MANIZALES'),(320,17,13,'AGUADAS'),(321,17,42,'ANSERMA'),(322,17,50,'ARANZAZU'),(323,17,88,'BELALCAZAR'),(324,17,174,'CHINCHINA'),(325,17,272,'FILADELFIA'),(326,17,380,'LA DORADA'),(327,17,388,'LA MERCED'),(328,17,433,'MANZANARES'),(329,17,442,'MARMATO'),(330,17,444,'MARQUETALIA'),(331,17,446,'MARULANDA'),(332,17,486,'NEIRA'),(333,17,495,'NORCASIA'),(334,17,513,'PACORA'),(335,17,524,'PALESTINA'),(336,17,541,'PENSILVANIA'),(337,17,614,'RIOSUCIO'),(338,17,616,'RISARALDA'),(339,17,653,'SALAMINA'),(340,17,662,'SAMANA'),(341,17,665,'SAN JOSE'),(342,17,777,'SUPIA'),(343,17,867,'VICTORIA'),(344,17,873,'VILLAMARIA'),(345,17,877,'VITERBO'),(346,18,1,'FLORENCIA'),(347,18,29,'ALBANIA'),(348,18,94,'BELEN DE LOS ANDAQUIES'),(349,18,150,'CARTAGENA DEL CHAIRA'),(350,18,205,'CURILLO'),(351,18,247,'EL DONCELLO'),(352,18,256,'EL PAUJIL'),(353,18,410,'LA MONTAÂ¥ITA'),(354,18,460,'MILAN'),(355,18,479,'MORELIA'),(356,18,592,'PUERTO RICO'),(357,18,610,'SAN JOSE DEL FRAGUA'),(358,18,753,'SAN VICENTE DEL CAGUAN'),(359,18,756,'SOLANO'),(360,18,785,'SOLITA'),(361,18,860,'VALPARAISO'),(362,19,1,'POPAYAN'),(363,19,22,'ALMAGUER'),(364,19,50,'ARGELIA'),(365,19,75,'BALBOA'),(366,19,100,'BOLIVAR'),(367,19,110,'BUENOS AIRES'),(368,19,130,'CAJIBIO'),(369,19,137,'CALDONO'),(370,19,142,'CALOTO'),(371,19,212,'CORINTO'),(372,19,256,'EL TAMBO'),(373,19,290,'FLORENCIA'),(374,19,300,'GUACHENE'),(375,19,318,'GUAPI'),(376,19,355,'INZA'),(377,19,364,'JAMBALO'),(378,19,392,'LA SIERRA'),(379,19,397,'LA VEGA'),(380,19,418,'LOPEZ'),(381,19,450,'MERCADERES'),(382,19,455,'MIRANDA'),(383,19,473,'MORALES'),(384,19,513,'PADILLA'),(385,19,517,'PAEZ'),(386,19,532,'PATIA'),(387,19,533,'PIAMONTE'),(388,19,548,'PIENDAMO'),(389,19,573,'PUERTO TEJADA'),(390,19,585,'PURACE'),(391,19,622,'ROSAS'),(392,19,693,'SAN SEBASTIAN'),(393,19,698,'SANTANDER DE QUILICHAO'),(394,19,701,'SANTA ROSA'),(395,19,743,'SILVIA'),(396,19,760,'SOTARA'),(397,19,780,'SUAREZ'),(398,19,785,'SUCRE'),(399,19,807,'TIMBIO'),(400,19,809,'TIMBIQUI'),(401,19,821,'TORIBIO'),(402,19,824,'TOTORO'),(403,19,845,'VILLA RICA'),(404,20,1,'VALLEDUPAR'),(405,20,11,'AGUACHICA'),(406,20,13,'AGUSTIN CODAZZI'),(407,20,32,'ASTREA'),(408,20,45,'BECERRIL'),(409,20,60,'BOSCONIA'),(410,20,175,'CHIMICHAGUA'),(411,20,178,'CHIRIGUANA'),(412,20,228,'CURUMANI'),(413,20,238,'EL COPEY'),(414,20,250,'EL PASO'),(415,20,295,'GAMARRA'),(416,20,310,'GONZALEZ'),(417,20,383,'LA GLORIA'),(418,20,400,'LA JAGUA DE IBIRICO'),(419,20,443,'MANAURE'),(420,20,517,'PAILITAS'),(421,20,550,'PELAYA'),(422,20,570,'PUEBLO BELLO'),(423,20,614,'RIO DE ORO'),(424,20,621,'LA PAZ'),(425,20,710,'SAN ALBERTO'),(426,20,750,'SAN DIEGO'),(427,20,770,'SAN MARTIN'),(428,20,787,'TAMALAMEQUE'),(429,23,1,'MONTERIA'),(430,23,68,'AYAPEL'),(431,23,79,'BUENAVISTA'),(432,23,90,'CANALETE'),(433,23,162,'CERETE'),(434,23,168,'CHIMA'),(435,23,182,'CHINU'),(436,23,189,'CIENAGA DE ORO'),(437,23,300,'COTORRA'),(438,23,350,'LA APARTADA'),(439,23,417,'LORICA'),(440,23,419,'LOS CORDOBAS'),(441,23,464,'MOMIL'),(442,23,466,'MONTELIBANO'),(443,23,500,'MOÂ¥ITOS'),(444,23,555,'PLANETA RICA'),(445,23,570,'PUEBLO NUEVO'),(446,23,574,'PUERTO ESCONDIDO'),(447,23,580,'PUERTO LIBERTADOR'),(448,23,586,'PURISIMA'),(449,23,660,'SAHAGUN'),(450,23,670,'SAN ANDRES SOTAVENTO'),(451,23,672,'SAN ANTERO'),(452,23,675,'SAN BERNARDO DEL VIENTO'),(453,23,678,'SAN CARLOS'),(454,23,686,'SAN PELAYO'),(455,23,807,'TIERRALTA'),(456,23,855,'VALENCIA'),(457,25,1,'AGUA DE DIOS'),(458,25,19,'ALBAN'),(459,25,35,'ANAPOIMA'),(460,25,40,'ANOLAIMA'),(461,25,53,'ARBELAEZ'),(462,25,86,'BELTRAN'),(463,25,95,'BITUIMA'),(464,25,99,'BOJACA'),(465,25,120,'CABRERA'),(466,25,123,'CACHIPAY'),(467,25,126,'CAJICA'),(468,25,148,'CAPARRAPI'),(469,25,151,'CAQUEZA'),(470,25,154,'CARMEN DE CARUPA'),(471,25,168,'CHAGUANI'),(472,25,175,'CHIA'),(473,25,178,'CHIPAQUE'),(474,25,181,'CHOACHI'),(475,25,183,'CHOCONTA'),(476,25,200,'COGUA'),(477,25,214,'COTA'),(478,25,224,'CUCUNUBA'),(479,25,245,'EL COLEGIO'),(480,25,258,'EL PEÂ¥ON'),(481,25,260,'EL ROSAL'),(482,25,269,'FACATATIVA'),(483,25,279,'FOMEQUE'),(484,25,281,'FOSCA'),(485,25,286,'FUNZA'),(486,25,288,'FUQUENE'),(487,25,290,'FUSAGASUGA'),(488,25,293,'GACHALA'),(489,25,295,'GACHANCIPA'),(490,25,297,'GACHETA'),(491,25,299,'GAMA'),(492,25,307,'GIRARDOT'),(493,25,312,'GRANADA'),(494,25,317,'GUACHETA'),(495,25,320,'GUADUAS'),(496,25,322,'GUASCA'),(497,25,324,'GUATAQUI'),(498,25,326,'GUATAVITA'),(499,25,328,'GUAYABAL DE SIQUIMA'),(500,25,335,'GUAYABETAL'),(501,25,339,'GUTIERREZ'),(502,25,368,'JERUSALEN'),(503,25,372,'JUNIN'),(504,25,377,'LA CALERA'),(505,25,386,'LA MESA'),(506,25,394,'LA PALMA'),(507,25,398,'LA PEÂ¥A'),(508,25,402,'LA VEGA'),(509,25,407,'LENGUAZAQUE'),(510,25,426,'MACHETA'),(511,25,430,'MADRID'),(512,25,436,'MANTA'),(513,25,438,'MEDINA'),(514,25,473,'MOSQUERA'),(515,25,483,'NARIÂ¥O'),(516,25,486,'NEMOCON'),(517,25,488,'NILO'),(518,25,489,'NIMAIMA'),(519,25,491,'NOCAIMA'),(520,25,506,'VENECIA'),(521,25,513,'PACHO'),(522,25,518,'PAIME'),(523,25,524,'PANDI'),(524,25,530,'PARATEBUENO'),(525,25,535,'PASCA'),(526,25,572,'PUERTO SALGAR'),(527,25,580,'PULI'),(528,25,592,'QUEBRADANEGRA'),(529,25,594,'QUETAME'),(530,25,596,'QUIPILE'),(531,25,599,'APULO'),(532,25,612,'RICAURTE'),(533,25,645,'SAN ANTONIO DEL TEQUENDAMA'),(534,25,649,'SAN BERNARDO'),(535,25,653,'SAN CAYETANO'),(536,25,658,'SAN FRANCISCO'),(537,25,662,'SAN JUAN DE RIO SECO'),(538,25,718,'SASAIMA'),(539,25,736,'SESQUILE'),(540,25,740,'SIBATE'),(541,25,743,'SILVANIA'),(542,25,745,'SIMIJACA'),(543,25,754,'SOACHA'),(544,25,758,'SOPO'),(545,25,769,'SUBACHOQUE'),(546,25,772,'SUESCA'),(547,25,777,'SUPATA'),(548,25,779,'SUSA'),(549,25,781,'SUTATAUSA'),(550,25,785,'TABIO'),(551,25,793,'TAUSA'),(552,25,797,'TENA'),(553,25,799,'TENJO'),(554,25,805,'TIBACUY'),(555,25,807,'TIBIRITA'),(556,25,815,'TOCAIMA'),(557,25,817,'TOCANCIPA'),(558,25,823,'TOPAIPI'),(559,25,839,'UBALA'),(560,25,841,'UBAQUE'),(561,25,843,'VILLA DE SAN DIEGO DE UBATE'),(562,25,845,'UNE'),(563,25,851,'UTICA'),(564,25,862,'VERGARA'),(565,25,867,'VIANI'),(566,25,871,'VILLAGOMEZ'),(567,25,873,'VILLAPINZON'),(568,25,875,'VILLETA'),(569,25,878,'VIOTA'),(570,25,885,'YACOPI'),(571,25,898,'ZIPACON'),(572,25,899,'ZIPAQUIRA'),(573,27,1,'QUIBDO'),(574,27,6,'ACANDI'),(575,27,25,'ALTO BAUDO'),(576,27,50,'ATRATO'),(577,27,73,'BAGADO'),(578,27,75,'BAHIA SOLANO'),(579,27,77,'BAJO BAUDO'),(580,27,99,'BOJAYA'),(581,27,135,'EL CANTON DEL SAN PABLO'),(582,27,150,'CARMEN DEL DARIEN'),(583,27,160,'CERTEGUI'),(584,27,205,'CONDOTO'),(585,27,245,'EL CARMEN DE ATRATO'),(586,27,250,'EL LITORAL DEL SAN JUAN'),(587,27,361,'ISTMINA'),(588,27,372,'JURADO'),(589,27,413,'LLORO'),(590,27,425,'MEDIO ATRATO'),(591,27,430,'MEDIO BAUDO'),(592,27,450,'MEDIO SAN JUAN'),(593,27,491,'NOVITA'),(594,27,495,'NUQUI'),(595,27,580,'RIO IRO'),(596,27,600,'RIO QUITO'),(597,27,615,'RIOSUCIO'),(598,27,660,'SAN JOSE DEL PALMAR'),(599,27,745,'SIPI'),(600,27,787,'TADO'),(601,27,800,'UNGUIA'),(602,27,810,'UNION PANAMERICANA'),(603,41,1,'NEIVA'),(604,41,6,'ACEVEDO'),(605,41,13,'AGRADO'),(606,41,16,'AIPE'),(607,41,20,'ALGECIRAS'),(608,41,26,'ALTAMIRA'),(609,41,78,'BARAYA'),(610,41,132,'CAMPOALEGRE'),(611,41,206,'COLOMBIA'),(612,41,244,'ELIAS'),(613,41,298,'GARZON'),(614,41,306,'GIGANTE'),(615,41,319,'GUADALUPE'),(616,41,349,'HOBO'),(617,41,357,'IQUIRA'),(618,41,359,'ISNOS'),(619,41,378,'LA ARGENTINA'),(620,41,396,'LA PLATA'),(621,41,483,'NATAGA'),(622,41,503,'OPORAPA'),(623,41,518,'PAICOL'),(624,41,524,'PALERMO'),(625,41,530,'PALESTINA'),(626,41,548,'PITAL'),(627,41,551,'PITALITO'),(628,41,615,'RIVERA'),(629,41,660,'SALADOBLANCO'),(630,41,668,'SAN AGUSTIN'),(631,41,676,'SANTA MARIA'),(632,41,770,'SUAZA'),(633,41,791,'TARQUI'),(634,41,797,'TESALIA'),(635,41,799,'TELLO'),(636,41,801,'TERUEL'),(637,41,807,'TIMANA'),(638,41,872,'VILLAVIEJA'),(639,41,885,'YAGUARA'),(640,44,1,'RIOHACHA'),(641,44,35,'ALBANIA'),(642,44,78,'BARRANCAS'),(643,44,90,'DIBULLA'),(644,44,98,'DISTRACCION'),(645,44,110,'EL MOLINO'),(646,44,279,'FONSECA'),(647,44,378,'HATONUEVO'),(648,44,420,'LA JAGUA DEL PILAR'),(649,44,430,'MAICAO'),(650,44,560,'MANAURE'),(651,44,650,'SAN JUAN DEL CESAR'),(652,44,847,'URIBIA'),(653,44,855,'URUMITA'),(654,44,874,'VILLANUEVA'),(655,47,1,'SANTA MARTA'),(656,47,30,'ALGARROBO'),(657,47,53,'ARACATACA'),(658,47,58,'ARIGUANI'),(659,47,161,'CERRO SAN ANTONIO'),(660,47,170,'CHIBOLO'),(661,47,189,'CIENAGA'),(662,47,205,'CONCORDIA'),(663,47,245,'EL BANCO'),(664,47,258,'EL PIÂ¥ON'),(665,47,268,'EL RETEN'),(666,47,288,'FUNDACION'),(667,47,318,'GUAMAL'),(668,47,460,'NUEVA GRANADA'),(669,47,541,'PEDRAZA'),(670,47,545,'PIJIÂ¥O DEL CARMEN'),(671,47,551,'PIVIJAY'),(672,47,555,'PLATO'),(673,47,570,'PUEBLOVIEJO'),(674,47,605,'REMOLINO'),(675,47,660,'SABANAS DE SAN ANGEL'),(676,47,675,'SALAMINA'),(677,47,692,'SAN SEBASTIAN DE BUENAVISTA'),(678,47,703,'SAN ZENON'),(679,47,707,'SANTA ANA'),(680,47,720,'SANTA BARBARA DE PINTO'),(681,47,745,'SITIONUEVO'),(682,47,798,'TENERIFE'),(683,47,960,'ZAPAYAN'),(684,47,980,'ZONA BANANERA'),(685,50,1,'VILLAVICENCIO'),(686,50,6,'ACACIAS'),(687,50,110,'BARRANCA DE UPIA'),(688,50,124,'CABUYARO'),(689,50,150,'CASTILLA LA NUEVA'),(690,50,223,'CUBARRAL'),(691,50,226,'CUMARAL'),(692,50,245,'EL CALVARIO'),(693,50,251,'EL CASTILLO'),(694,50,270,'EL DORADO'),(695,50,287,'FUENTE DE ORO'),(696,50,313,'GRANADA'),(697,50,318,'GUAMAL'),(698,50,325,'MAPIRIPAN'),(699,50,330,'MESETAS'),(700,50,350,'LA MACARENA'),(701,50,370,'URIBE'),(702,50,400,'LEJANIAS'),(703,50,450,'PUERTO CONCORDIA'),(704,50,568,'PUERTO GAITAN'),(705,50,573,'PUERTO LOPEZ'),(706,50,577,'PUERTO LLERAS'),(707,50,590,'PUERTO RICO'),(708,50,606,'RESTREPO'),(709,50,680,'SAN CARLOS DE GUAROA'),(710,50,683,'SAN JUAN DE ARAMA'),(711,50,686,'SAN JUANITO'),(712,50,689,'SAN MARTIN'),(713,50,711,'VISTAHERMOSA'),(714,52,1,'PASTO'),(715,52,19,'ALBAN'),(716,52,22,'ALDANA'),(717,52,36,'ANCUYA'),(718,52,51,'ARBOLEDA'),(719,52,79,'BARBACOAS'),(720,52,83,'BELEN'),(721,52,110,'BUESACO'),(722,52,203,'COLON'),(723,52,207,'CONSACA'),(724,52,210,'CONTADERO'),(725,52,215,'CORDOBA'),(726,52,224,'CUASPUD'),(727,52,227,'CUMBAL'),(728,52,233,'CUMBITARA'),(729,52,240,'CHACHAGsI'),(730,52,250,'EL CHARCO'),(731,52,254,'EL PEÂ¥OL'),(732,52,256,'EL ROSARIO'),(733,52,258,'EL TABLON DE GOMEZ'),(734,52,260,'EL TAMBO'),(735,52,287,'FUNES'),(736,52,317,'GUACHUCAL'),(737,52,320,'GUAITARILLA'),(738,52,323,'GUALMATAN'),(739,52,352,'ILES'),(740,52,354,'IMUES'),(741,52,356,'IPIALES'),(742,52,378,'LA CRUZ'),(743,52,381,'LA FLORIDA'),(744,52,385,'LA LLANADA'),(745,52,390,'LA TOLA'),(746,52,399,'LA UNION'),(747,52,405,'LEIVA'),(748,52,411,'LINARES'),(749,52,418,'LOS ANDES'),(750,52,427,'MAGsI'),(751,52,435,'MALLAMA'),(752,52,473,'MOSQUERA'),(753,52,480,'NARIÂ¥O'),(754,52,490,'OLAYA HERRERA'),(755,52,506,'OSPINA'),(756,52,520,'FRANCISCO PIZARRO'),(757,52,540,'POLICARPA'),(758,52,560,'POTOSI'),(759,52,565,'PROVIDENCIA'),(760,52,573,'PUERRES'),(761,52,585,'PUPIALES'),(762,52,612,'RICAURTE'),(763,52,621,'ROBERTO PAYAN'),(764,52,678,'SAMANIEGO'),(765,52,683,'SANDONA'),(766,52,685,'SAN BERNARDO'),(767,52,687,'SAN LORENZO'),(768,52,693,'SAN PABLO'),(769,52,694,'SAN PEDRO DE CARTAGO'),(770,52,696,'SANTA BARBARA'),(771,52,699,'SANTACRUZ'),(772,52,720,'SAPUYES'),(773,52,786,'TAMINANGO'),(774,52,788,'TANGUA'),(775,52,835,'SAN ANDRES DE TUMACO'),(776,52,838,'TUQUERRES'),(777,52,885,'YACUANQUER'),(778,54,1,'CUCUTA'),(779,54,3,'ABREGO'),(780,54,51,'ARBOLEDAS'),(781,54,99,'BOCHALEMA'),(782,54,109,'BUCARASICA'),(783,54,125,'CACOTA'),(784,54,128,'CACHIRA'),(785,54,172,'CHINACOTA'),(786,54,174,'CHITAGA'),(787,54,206,'CONVENCION'),(788,54,223,'CUCUTILLA'),(789,54,239,'DURANIA'),(790,54,245,'EL CARMEN'),(791,54,250,'EL TARRA'),(792,54,261,'EL ZULIA'),(793,54,313,'GRAMALOTE'),(794,54,344,'HACARI'),(795,54,347,'HERRAN'),(796,54,377,'LABATECA'),(797,54,385,'LA ESPERANZA'),(798,54,398,'LA PLAYA'),(799,54,405,'LOS PATIOS'),(800,54,418,'LOURDES'),(801,54,480,'MUTISCUA'),(802,54,498,'OCAÂ¥A'),(803,54,518,'PAMPLONA'),(804,54,520,'PAMPLONITA'),(805,54,553,'PUERTO SANTANDER'),(806,54,599,'RAGONVALIA'),(807,54,660,'SALAZAR'),(808,54,670,'SAN CALIXTO'),(809,54,673,'SAN CAYETANO'),(810,54,680,'SANTIAGO'),(811,54,720,'SARDINATA'),(812,54,743,'SILOS'),(813,54,800,'TEORAMA'),(814,54,810,'TIBU'),(815,54,820,'TOLEDO'),(816,54,871,'VILLA CARO'),(817,54,874,'VILLA DEL ROSARIO'),(818,63,1,'ARMENIA'),(819,63,111,'BUENAVISTA'),(820,63,130,'CALARCA'),(821,63,190,'CIRCASIA'),(822,63,212,'CORDOBA'),(823,63,272,'FILANDIA'),(824,63,302,'GENOVA'),(825,63,401,'LA TEBAIDA'),(826,63,470,'MONTENEGRO'),(827,63,548,'PIJAO'),(828,63,594,'QUIMBAYA'),(829,63,690,'SALENTO'),(830,66,1,'PEREIRA'),(831,66,45,'APIA'),(832,66,75,'BALBOA'),(833,66,88,'BELEN DE UMBRIA'),(834,66,170,'DOSQUEBRADAS'),(835,66,318,'GUATICA'),(836,66,383,'LA CELIA'),(837,66,400,'LA VIRGINIA'),(838,66,440,'MARSELLA'),(839,66,456,'MISTRATO'),(840,66,572,'PUEBLO RICO'),(841,66,594,'QUINCHIA'),(842,66,682,'SANTA ROSA DE CABAL'),(843,66,687,'SANTUARIO'),(844,68,1,'BUCARAMANGA'),(845,68,13,'AGUADA'),(846,68,20,'ALBANIA'),(847,68,51,'ARATOCA'),(848,68,77,'BARBOSA'),(849,68,79,'BARICHARA'),(850,68,81,'BARRANCABERMEJA'),(851,68,92,'BETULIA'),(852,68,101,'BOLIVAR'),(853,68,121,'CABRERA'),(854,68,132,'CALIFORNIA'),(855,68,147,'CAPITANEJO'),(856,68,152,'CARCASI'),(857,68,160,'CEPITA'),(858,68,162,'CERRITO'),(859,68,167,'CHARALA'),(860,68,169,'CHARTA'),(861,68,176,'CHIMA'),(862,68,179,'CHIPATA'),(863,68,190,'CIMITARRA'),(864,68,207,'CONCEPCION'),(865,68,209,'CONFINES'),(866,68,211,'CONTRATACION'),(867,68,217,'COROMORO'),(868,68,229,'CURITI'),(869,68,235,'EL CARMEN DE CHUCURI'),(870,68,245,'EL GUACAMAYO'),(871,68,250,'EL PEÂ¥ON'),(872,68,255,'EL PLAYON'),(873,68,264,'ENCINO'),(874,68,266,'ENCISO'),(875,68,271,'FLORIAN'),(876,68,276,'FLORIDABLANCA'),(877,68,296,'GALAN'),(878,68,298,'GAMBITA'),(879,68,307,'GIRON'),(880,68,318,'GUACA'),(881,68,320,'GUADALUPE'),(882,68,322,'GUAPOTA'),(883,68,324,'GUAVATA'),(884,68,327,'GsEPSA'),(885,68,344,'HATO'),(886,68,368,'JESUS MARIA'),(887,68,370,'JORDAN'),(888,68,377,'LA BELLEZA'),(889,68,385,'LANDAZURI'),(890,68,397,'LA PAZ'),(891,68,406,'LEBRIJA'),(892,68,418,'LOS SANTOS'),(893,68,425,'MACARAVITA'),(894,68,432,'MALAGA'),(895,68,444,'MATANZA'),(896,68,464,'MOGOTES'),(897,68,468,'MOLAGAVITA'),(898,68,498,'OCAMONTE'),(899,68,500,'OIBA'),(900,68,502,'ONZAGA'),(901,68,522,'PALMAR'),(902,68,524,'PALMAS DEL SOCORRO'),(903,68,533,'PARAMO'),(904,68,547,'PIEDECUESTA'),(905,68,549,'PINCHOTE'),(906,68,572,'PUENTE NACIONAL'),(907,68,573,'PUERTO PARRA'),(908,68,575,'PUERTO WILCHES'),(909,68,615,'RIONEGRO'),(910,68,655,'SABANA DE TORRES'),(911,68,669,'SAN ANDRES'),(912,68,673,'SAN BENITO'),(913,68,679,'SAN GIL'),(914,68,682,'SAN JOAQUIN'),(915,68,684,'SAN JOSE DE MIRANDA'),(916,68,686,'SAN MIGUEL'),(917,68,689,'SAN VICENTE DE CHUCURI'),(918,68,705,'SANTA BARBARA'),(919,68,720,'SANTA HELENA DEL OPON'),(920,68,745,'SIMACOTA'),(921,68,755,'SOCORRO'),(922,68,770,'SUAITA'),(923,68,773,'SUCRE'),(924,68,780,'SURATA'),(925,68,820,'TONA'),(926,68,855,'VALLE DE SAN JOSE'),(927,68,861,'VELEZ'),(928,68,867,'VETAS'),(929,68,872,'VILLANUEVA'),(930,68,895,'ZAPATOCA'),(931,70,1,'SINCELEJO'),(932,70,110,'BUENAVISTA'),(933,70,124,'CAIMITO'),(934,70,204,'COLOSO'),(935,70,215,'COROZAL'),(936,70,221,'COVEÂ¥AS'),(937,70,230,'CHALAN'),(938,70,233,'EL ROBLE'),(939,70,235,'GALERAS'),(940,70,265,'GUARANDA'),(941,70,400,'LA UNION'),(942,70,418,'LOS PALMITOS'),(943,70,429,'MAJAGUAL'),(944,70,473,'MORROA'),(945,70,508,'OVEJAS'),(946,70,523,'PALMITO'),(947,70,670,'SAMPUES'),(948,70,678,'SAN BENITO ABAD'),(949,70,702,'SAN JUAN DE BETULIA'),(950,70,708,'SAN MARCOS'),(951,70,713,'SAN ONOFRE'),(952,70,717,'SAN PEDRO'),(953,70,742,'SAN LUIS DE SINCE'),(954,70,771,'SUCRE'),(955,70,820,'SANTIAGO DE TOLU'),(956,70,823,'TOLU VIEJO'),(957,73,1,'IBAGUE'),(958,73,24,'ALPUJARRA'),(959,73,26,'ALVARADO'),(960,73,30,'AMBALEMA'),(961,73,43,'ANZOATEGUI'),(962,73,55,'ARMERO'),(963,73,67,'ATACO'),(964,73,124,'CAJAMARCA'),(965,73,148,'CARMEN DE APICALA'),(966,73,152,'CASABIANCA'),(967,73,168,'CHAPARRAL'),(968,73,200,'COELLO'),(969,73,217,'COYAIMA'),(970,73,226,'CUNDAY'),(971,73,236,'DOLORES'),(972,73,268,'ESPINAL'),(973,73,270,'FALAN'),(974,73,275,'FLANDES'),(975,73,283,'FRESNO'),(976,73,319,'GUAMO'),(977,73,347,'HERVEO'),(978,73,349,'HONDA'),(979,73,352,'ICONONZO'),(980,73,408,'LERIDA'),(981,73,411,'LIBANO'),(982,73,443,'MARIQUITA'),(983,73,449,'MELGAR'),(984,73,461,'MURILLO'),(985,73,483,'NATAGAIMA'),(986,73,504,'ORTEGA'),(987,73,520,'PALOCABILDO'),(988,73,547,'PIEDRAS'),(989,73,555,'PLANADAS'),(990,73,563,'PRADO'),(991,73,585,'PURIFICACION'),(992,73,616,'RIOBLANCO'),(993,73,622,'RONCESVALLES'),(994,73,624,'ROVIRA'),(995,73,671,'SALDAÂ¥A'),(996,73,675,'SAN ANTONIO'),(997,73,678,'SAN LUIS'),(998,73,686,'SANTA ISABEL'),(999,73,770,'SUAREZ'),(1000,73,854,'VALLE DE SAN JUAN'),(1001,73,861,'VENADILLO'),(1002,73,870,'VILLAHERMOSA'),(1003,73,873,'VILLARRICA'),(1004,76,1,'CALI'),(1005,76,20,'ALCALA'),(1006,76,36,'ANDALUCIA'),(1007,76,41,'ANSERMANUEVO'),(1008,76,54,'ARGELIA'),(1009,76,100,'BOLIVAR'),(1010,76,109,'BUENAVENTURA'),(1011,76,111,'GUADALAJARA DE BUGA'),(1012,76,113,'BUGALAGRANDE'),(1013,76,122,'CAICEDONIA'),(1014,76,126,'CALIMA'),(1015,76,130,'CANDELARIA'),(1016,76,147,'CARTAGO'),(1017,76,233,'DAGUA'),(1018,76,243,'EL AGUILA'),(1019,76,246,'EL CAIRO'),(1020,76,248,'EL CERRITO'),(1021,76,250,'EL DOVIO'),(1022,76,275,'FLORIDA'),(1023,76,306,'GINEBRA'),(1024,76,318,'GUACARI'),(1025,76,364,'JAMUNDI'),(1026,76,377,'LA CUMBRE'),(1027,76,400,'LA UNION'),(1028,76,403,'LA VICTORIA'),(1029,76,497,'OBANDO'),(1030,76,520,'PALMIRA'),(1031,76,563,'PRADERA'),(1032,76,606,'RESTREPO'),(1033,76,616,'RIOFRIO'),(1034,76,622,'ROLDANILLO'),(1035,76,670,'SAN PEDRO'),(1036,76,736,'SEVILLA'),(1037,76,823,'TORO'),(1038,76,828,'TRUJILLO'),(1039,76,834,'TULUA'),(1040,76,845,'ULLOA'),(1041,76,863,'VERSALLES'),(1042,76,869,'VIJES'),(1043,76,890,'YOTOCO'),(1044,76,892,'YUMBO'),(1045,76,895,'ZARZAL'),(1046,81,1,'ARAUCA'),(1047,81,65,'ARAUQUITA'),(1048,81,220,'CRAVO NORTE'),(1049,81,300,'FORTUL'),(1050,81,591,'PUERTO RONDON'),(1051,81,736,'SARAVENA'),(1052,81,794,'TAME'),(1053,85,1,'YOPAL'),(1054,85,10,'AGUAZUL'),(1055,85,15,'CHAMEZA'),(1056,85,125,'HATO COROZAL'),(1057,85,136,'LA SALINA'),(1058,85,139,'MANI'),(1059,85,162,'MONTERREY'),(1060,85,225,'NUNCHIA'),(1061,85,230,'OROCUE'),(1062,85,250,'PAZ DE ARIPORO'),(1063,85,263,'PORE'),(1064,85,279,'RECETOR'),(1065,85,300,'SABANALARGA'),(1066,85,315,'SACAMA'),(1067,85,325,'SAN LUIS DE PALENQUE'),(1068,85,400,'TAMARA'),(1069,85,410,'TAURAMENA'),(1070,85,430,'TRINIDAD'),(1071,85,440,'VILLANUEVA'),(1072,86,1,'MOCOA'),(1073,86,219,'COLON'),(1074,86,320,'ORITO'),(1075,86,568,'PUERTO ASIS'),(1076,86,569,'PUERTO CAICEDO'),(1077,86,571,'PUERTO GUZMAN'),(1078,86,573,'LEGUIZAMO'),(1079,86,749,'SIBUNDOY'),(1080,86,755,'SAN FRANCISCO'),(1081,86,757,'SAN MIGUEL'),(1082,86,760,'SANTIAGO'),(1083,86,865,'VALLE DEL GUAMUEZ'),(1084,86,885,'VILLAGARZON'),(1085,88,1,'SAN ANDRES'),(1086,88,564,'PROVIDENCIA'),(1087,91,1,'LETICIA'),(1088,91,263,'EL ENCANTO'),(1089,91,405,'LA CHORRERA'),(1090,91,407,'LA PEDRERA'),(1091,91,430,'LA VICTORIA'),(1092,91,460,'MIRITI - PARANA'),(1093,91,530,'PUERTO ALEGRIA'),(1094,91,536,'PUERTO ARICA'),(1095,91,540,'PUERTO NARIÂ¥O'),(1096,91,669,'PUERTO SANTANDER'),(1097,91,798,'TARAPACA'),(1098,94,1,'INIRIDA'),(1099,94,343,'BARRANCO MINAS'),(1100,94,663,'MAPIRIPANA'),(1101,94,883,'SAN FELIPE'),(1102,94,884,'PUERTO COLOMBIA'),(1103,94,885,'LA GUADALUPE'),(1104,94,886,'CACAHUAL'),(1105,94,887,'PANA PANA'),(1106,94,888,'MORICHAL'),(1107,95,1,'SAN JOSE DEL GUAVIARE'),(1108,95,15,'CALAMAR'),(1109,95,25,'EL RETORNO'),(1110,95,200,'MIRAFLORES'),(1111,97,1,'MITU'),(1112,97,161,'CARURU'),(1113,97,511,'PACOA'),(1114,97,666,'TARAIRA'),(1115,97,777,'PAPUNAUA'),(1116,97,889,'YAVARATE'),(1117,99,1,'PUERTO CARREÂ¥O'),(1118,99,524,'LA PRIMAVERA'),(1119,99,624,'SANTA ROSALIA'),(1120,99,773,'CUMARIBO'),(1121,0,0,'NA');

--
-- Table structure for table `notas_estados_financieros`
--

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

--
-- Table structure for table `nuevosimpuestos`
--

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
-- Table structure for table `orden_compra_metodos_pago`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ordenes_compra`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `origen_destino_inventario`
--

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

INSERT INTO `paises` VALUES (1,13,'AFGANISTAN','AF\r'),(2,17,'ALBANIA','AL\r'),(3,23,'ALEMANIA','DE\r'),(4,37,'ANDORRA','AD\r'),(5,40,'ANGOLA','AO\r'),(6,41,'ANGUILLA','0'),(7,43,'ANTIGUA Y BARBUDA','AG\r'),(8,47,'ANTILLAS HOLANDESAS','0'),(9,53,'ARABIA SAUDITA','SA\r'),(10,59,'ARGELIA','DZ\r'),(11,63,'ARGENTINA','AR\r'),(12,26,'ARMENIA','AM\r'),(13,27,'ARUBA','AW\r'),(14,69,'AUSTRALIA','AU\r'),(15,72,'AUSTRIA','AT\r'),(16,74,'AZERBAIJAN','0'),(17,77,'BAHAMAS','BS\r'),(18,80,'BAHREIN','0'),(19,81,'BANGLADESH','0'),(20,83,'BARBADOS','BB\r'),(21,91,'BELARUS','0'),(22,87,'BELGICA','BE\r'),(23,88,'BELICE','BZ\r'),(24,229,'BENIN','BJ\r'),(25,90,'BERMUDAS','BM\r'),(26,93,'BIRMANIA (MYANMAR)','0'),(27,97,'BOLIVIA','BO\r'),(28,29,'BOSNIA-HERZEGOVINA','0'),(29,101,'BOTSWANA','0'),(30,105,'BRASIL','BR\r'),(31,108,'BRUNEI DARUSSALAM','0'),(32,111,'BULGARIA','BG\r'),(33,31,'BURKINA FASSO','0'),(34,115,'BURUNDI','BI\r'),(35,119,'BUTAN','BT\r'),(36,127,'CABO VERDE','CV\r'),(37,137,'CAIMAN, ISLAS','0'),(38,141,'CAMBOYA (KAMPUCHEA)','0'),(39,145,'CAMERUN, REPUBLICA UNIDA DEL','0'),(40,149,'CANADA','CA\r'),(41,203,'CHAD','TD\r'),(42,211,'CHILE','CL\r'),(43,215,'CHINA','CN\r'),(44,221,'CHIPRE','CY\r'),(45,165,'COCOS (KEELING), ISLAS','0'),(46,169,'COLOMBIA','CO\r'),(47,173,'COMORAS','KM\r'),(48,177,'CONGO','0'),(49,183,'COOK, ISLAS','0'),(50,187,'COREA (NORTE), REPUBLICA POPULAR DEMOCRATICA DE','0'),(51,190,'COREA (SUR), REPUBLICA DE','0'),(52,193,'COSTA DE MARFIL','CI\r'),(53,196,'COSTA RICA','CR\r'),(54,198,'CROACIA','HR\r'),(55,199,'CUBA','CU\r'),(56,232,'DINAMARCA','DK\r'),(57,783,'DJIBOUTI','0'),(58,235,'DOMINICA','DM\r'),(59,239,'ECUADOR','EC\r'),(60,240,'EGIPTO','EG\r'),(61,242,'EL SALVADOR','SV\r'),(62,244,'EMIRATOS ARABES UNIDOS','AE\r'),(63,243,'ERITREA','ER\r'),(64,246,'ESLOVAQUIA','SK\r'),(65,247,'ESLOVENIA','SI\r'),(66,245,'ESPAÂ¥A','0'),(67,249,'ESTADOS UNIDOS','US\r'),(68,251,'ESTONIA','EE\r'),(69,253,'ETIOPIA','ET\r'),(70,259,'FEROE, ISLAS','0'),(71,870,'FIJI','0'),(72,267,'FILIPINAS','PH\r'),(73,271,'FINLANDIA','FI\r'),(74,275,'FRANCIA','FR\r'),(75,281,'GABON','GA\r'),(76,285,'GAMBIA','GM\r'),(77,287,'GEORGIA','GE\r'),(78,289,'GHANA','GH\r'),(79,293,'GIBRALTAR','GI\r'),(80,297,'GRANADA','GD\r'),(81,301,'GRECIA','GR\r'),(82,305,'GROENLANDIA','GL\r'),(83,309,'GUADALUPE','GP\r'),(84,313,'GUAM','GU\r'),(85,317,'GUATEMALA','GT\r'),(86,337,'GUAYANA','0'),(87,325,'GUAYANA FRANCESA','GF\r'),(88,329,'GUINEA','GN\r'),(89,331,'GUINEA ECUATORIAL','GQ\r'),(90,334,'GUINEA-BISSAU','GW\r'),(91,341,'HAITI','HT\r'),(92,345,'HONDURAS','HN\r'),(93,351,'HONG KONG','HK\r'),(94,355,'HUNGRIA','HU\r'),(95,361,'INDIA','IN\r'),(96,365,'INDONESIA','ID\r'),(97,369,'IRAK','IQ\r'),(98,372,'IRAN, REPUBLICA ISLAMICA DEL','0'),(99,375,'IRLANDA (EIRE)','0'),(100,379,'ISLANDIA','IS\r'),(101,383,'ISRAEL','IL\r'),(102,386,'ITALIA','IT\r'),(103,391,'JAMAICA','JM\r'),(104,399,'JAPON','JP\r'),(105,403,'JORDANIA','JO\r'),(106,406,'KASAJSTAN','0'),(107,410,'KENIA','KE\r'),(108,412,'KIRGUIZISTAN','0'),(109,411,'KIRIBATI','KI\r'),(110,413,'KUWAIT','KW\r'),(111,420,'LAOS, REPUBLICA POPULAR DEMOCRATICA DE','0'),(112,426,'LESOTHO','0'),(113,429,'LETONIA','LV\r'),(114,431,'LIBANO','LB\r'),(115,434,'LIBERIA','LR\r'),(116,438,'LIBIA (INCLUYE FEZZAN)','0'),(117,440,'LIECHTENSTEIN','LI\r'),(118,443,'LITUANIA','LT\r'),(119,445,'LUXEMBURGO','LU\r'),(120,447,'MACAO','MO\r'),(121,448,'MACEDONIA','0'),(122,450,'MADAGASCAR','MG\r'),(123,458,'MALAWI','0'),(124,455,'MALAYSIA','0'),(125,461,'MALDIVAS','MV\r'),(126,464,'MALI','ML\r'),(127,467,'MALTA','MT\r'),(128,469,'MARIANAS DEL NORTE, ISLAS','0'),(129,474,'MARRUECOS','MA\r'),(130,472,'MARSHALL, ISLAS','0'),(131,477,'MARTINICA','MQ\r'),(132,485,'MAURICIO','MU\r'),(133,488,'MAURITANIA','MR\r'),(134,493,'MEXICO','MX\r'),(135,494,'MICRONESIA, ESTADOS FEDERADOS DE','0'),(136,496,'MOLDAVIA','MD\r'),(137,498,'MONACO','MC\r'),(138,497,'MONGOLIA','MN\r'),(139,501,'MONSERRAT, ISLA','0'),(140,505,'MOZAMBIQUE','MZ\r'),(141,507,'NAMIBIA','NA\r'),(142,508,'NAURU','NR\r'),(143,511,'NAVIDAD (CHRISTMAS), ISLAS','0'),(144,517,'NEPAL','NP\r'),(145,521,'NICARAGUA','NI\r'),(146,525,'NIGER','NE\r'),(147,528,'NIGERIA','NG\r'),(148,531,'NIUE, ISLA','0'),(149,535,'NORFOLK, ISLA','0'),(150,538,'NORUEGA','NO\r'),(151,542,'NUEVA CALEDONIA','NC\r'),(152,548,'NUEVA ZELANDIA','0'),(153,556,'OMAN','OM\r'),(154,566,'PACIFICO, ISLAS (USA)','0'),(155,573,'PAISES BAJOS (HOLANDA)','0'),(156,576,'PAKISTAN','PK\r'),(157,578,'PALAU, ISLAS','0'),(158,580,'PANAMA','PA\r'),(159,545,'PAPUASIA NUEVA GUINEA','0'),(160,586,'PARAGUAY','PY\r'),(161,589,'PERU','PE\r'),(162,593,'PITCAIRN, ISLA','0'),(163,599,'POLINESIA FRANCESA','PF\r'),(164,603,'POLONIA','PL\r'),(165,607,'PORTUGAL','PT\r'),(166,611,'PUERTO RICO','PR\r'),(167,618,'QATAR','0'),(168,628,'REINO UNIDO','GB\r'),(169,640,'REPUBLICA CENTROAFRICANA','CF\r'),(170,644,'REPUBLICA CHECA','CZ\r'),(171,647,'REPUBLICA DOMINICANA','DO\r'),(172,660,'REUNION','RE\r'),(173,675,'RUANDA','RW\r'),(174,670,'RUMANIA','RO\r'),(175,676,'RUSIA','RU\r'),(176,685,'SAHARA OCCIDENTAL','0'),(177,677,'SALOMON, ISLAS','0'),(178,687,'SAMOA','WS\r'),(179,690,'SAMOA NORTEAMERICANA','0'),(180,695,'SAN CRISTOBAL Y NIEVES','KN\r'),(181,697,'SAN MARINO','SM\r'),(182,700,'SAN PEDRO Y MIGUELON','0'),(183,705,'SAN VICENTE Y LAS GRANADINAS','VC\r'),(184,710,'SANTA ELENA','0'),(185,715,'SANTA LUCIA','LC\r'),(186,159,'SANTA SEDE','0'),(187,720,'SANTO TOME Y PRINCIPE','ST\r'),(188,728,'SENEGAL','SN\r'),(189,731,'SEYCHELLES','SC\r'),(190,735,'SIERRA LEONA','SL\r'),(191,741,'SINGAPUR','SG\r'),(192,744,'SIRIA, REPUBLICA ARABE DE','0'),(193,748,'SOMALIA','SO\r'),(194,750,'SRI LANKA','LK\r'),(195,756,'SUDAFRICA, REPUBLICA DE','0'),(196,759,'SUDAN','SD\r'),(197,764,'SUECIA','SE\r'),(198,767,'SUIZA','CH\r'),(199,770,'SURINAM','SR\r'),(200,773,'SWAZILANDIA','0'),(201,774,'TADJIKISTAN','0'),(202,776,'TAILANDIA','TH\r'),(203,218,'TAIWAN (FORMOSA)','0'),(204,780,'TANZANIA, REPUBLICA UNIDA DE','0'),(205,787,'TERRITORIO BRITANICO DEL OCEANO INDICO','IO\r'),(206,788,'TIMOR DEL ESTE','0'),(207,800,'TOGO','TG\r'),(208,805,'TOKELAU','TK\r'),(209,810,'TONGA','TO\r'),(210,815,'TRINIDAD Y TOBAGO','TT\r'),(211,820,'TUNICIA','0'),(212,823,'TURCAS Y CAICOS, ISLAS','0'),(213,825,'TURKMENISTAN','TM\r'),(214,827,'TURQUIA','TR\r'),(215,828,'TUVALU','TV\r'),(216,830,'UCRANIA','UA\r'),(217,833,'UGANDA','UG\r'),(218,845,'URUGUAY','UY\r'),(219,847,'UZBEKISTAN','UZ\r'),(220,551,'VANUATU','VU\r'),(221,850,'VENEZUELA','VE\r'),(222,855,'VIET NAM','0'),(223,863,'VIRGENES, ISLAS (BRITANICAS)','0'),(224,866,'VIRGENES, ISLAS (NORTEAMERICANAS)','0'),(225,875,'WALLIS Y FORTUNA, ISLAS','0'),(226,880,'YEMEN','YE\r'),(227,885,'YUGOSLAVIA','0'),(228,888,'ZAIRE','0'),(229,890,'ZAMBIA','ZM\r'),(230,665,'ZIMBABWE','0'),(231,897,'ZONA NEUTRAL PALESTINA','0'),(232,0,'NA','0');

--
-- Table structure for table `paracomprobantes`
--


DROP TABLE IF EXISTS `parametros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parametros` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `categoriaParametro` varchar(100) DEFAULT NULL,
  `categoriaComprobante` varchar(100) DEFAULT NULL,
  `estado` varchar(100) DEFAULT NULL,
  `tipoDato` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO parametros (id, clave, nombre, categoriaParametro, categoriaComprobante, estado, tipoDato) VALUES
(2, 'COD_CAJA_GENERAL', 'Caja general (entrada/salida de efectivo)', 'GENERALES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(3, 'COD_CAJA_GENERAL', 'Caja general - Ventas', 'GENERALES', 'VENTA', 'ACTIVO', 'TEXTO'),
(4, 'COD_CAJA_GENERAL', 'Caja general - Ingresos', 'GENERALES', 'INGRESO', 'ACTIVO', 'TEXTO'),
(5, 'COD_CAJA_GENERAL', 'Caja general - Egresos', 'GENERALES', 'EGRESO', 'ACTIVO', 'TEXTO'),
(6, 'COD_CXC_CLIENTES', 'Cuentas por cobrar - Clientes', 'GENERALES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(7, 'COD_CXC_CLIENTES', 'Cuentas por cobrar - Ventas', 'GENERALES', 'VENTA', 'ACTIVO', 'TEXTO'),
(8, 'COD_CXC_CLIENTES', 'Cuentas por cobrar - Ingresos', 'GENERALES', 'INGRESO', 'ACTIVO', 'TEXTO'),
(9, 'COD_CXC_CLIENTES', 'Cuentas por cobrar - Devoluciones', 'GENERALES', 'DEVOLUCION', 'ACTIVO', 'TEXTO'),
(10, 'COD_INVENTARIOS', 'Inventarios (mercancías)', 'GENERALES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(11, 'COD_INVENTARIOS', 'Inventarios - Compras', 'GENERALES', 'COMPRA', 'ACTIVO', 'TEXTO'),
(12, 'COD_INVENTARIOS', 'Inventarios - Ventas', 'GENERALES', 'VENTA', 'ACTIVO', 'TEXTO'),
(13, 'COD_INVENTARIOS', 'Inventarios - Entradas', 'GENERALES', 'ENTRADA', 'ACTIVO', 'TEXTO'),
(14, 'COD_INVENTARIOS', 'Inventarios - Salidas', 'GENERALES', 'SALIDA', 'ACTIVO', 'TEXTO'),
(15, 'COD_IVA_DESCONTABLE', 'IVA descontable (compras)', 'IMPUESTOS', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(16, 'COD_IVA_DESCONTABLE', 'IVA descontable - Compras', 'IMPUESTOS', 'COMPRA', 'ACTIVO', 'TEXTO'),
(17, 'COD_CXP_PROVEEDORES', 'Cuentas por pagar - Proveedores', 'GENERALES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(18, 'COD_CXP_PROVEEDORES', 'Cuentas por pagar - Compras', 'GENERALES', 'COMPRA', 'ACTIVO', 'TEXTO'),
(19, 'COD_CXP_PROVEEDORES', 'Cuentas por pagar - Egresos', 'GENERALES', 'EGRESO', 'ACTIVO', 'TEXTO'),
(20, 'COD_IVA_GENERADO', 'IVA generado (ventas)', 'IMPUESTOS', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(21, 'COD_IVA_GENERADO', 'IVA generado - Ventas', 'IMPUESTOS', 'VENTA', 'ACTIVO', 'TEXTO'),
(22, 'COD_IVA_GENERADO', 'IVA generado - Devoluciones', 'IMPUESTOS', 'DEVOLUCION', 'ACTIVO', 'TEXTO'),
(23, 'COD_INGRESOS_VENTAS', 'Ingresos por ventas', 'GENERALES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(24, 'COD_INGRESOS_VENTAS', 'Ingresos por ventas - Ventas', 'GENERALES', 'VENTA', 'ACTIVO', 'TEXTO'),
(25, 'COD_GASTOS_GENERALES', 'Gastos generales', 'GASTOS', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(26, 'COD_GASTOS_GENERALES', 'Gastos generales - Egresos', 'GASTOS', 'EGRESO', 'ACTIVO', 'TEXTO'),
(27, 'COD_DEVOLUCION_VENTAS', 'Devoluciones en ventas', 'GENERALES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(28, 'COD_DEVOLUCION_VENTAS', 'Devoluciones - Devoluciones', 'GENERALES', 'DEVOLUCION', 'ACTIVO', 'TEXTO'),
(29, 'COD_COSTO_VENTAS', 'Costo de ventas (COGS)', 'GENERALES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(30, 'COD_COSTO_VENTAS', 'Costo de ventas - Ventas', 'GENERALES', 'VENTA', 'ACTIVO', 'TEXTO'),
(31, 'COD_COSTO_VENTAS', 'Costo de ventas - Entradas', 'GENERALES', 'ENTRADA', 'ACTIVO', 'TEXTO'),
(32, 'COD_COSTO_VENTAS', 'Costo de ventas - Salidas', 'GENERALES', 'SALIDA', 'ACTIVO', 'TEXTO'),
(33, 'COD_AJUSTE_ENTRADA_INV', 'Ajuste entrada inventario (sobrantes)', 'GENERALES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(34, 'COD_AJUSTE_ENTRADA_INV', 'Ajuste entrada inventario - Entradas', 'GENERALES', 'ENTRADA', 'ACTIVO', 'TEXTO'),
(35, 'COD_RETEFUENTE_PAGAR', 'Retención en la fuente por pagar', 'RETENCIONES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(36, 'COD_RETEFUENTE_PAGAR', 'Retefuente por pagar - Compras', 'RETENCIONES', 'COMPRA', 'ACTIVO', 'TEXTO'),
(37, 'COD_RETEFUENTE_PAGAR', 'Retefuente por pagar - Ventas', 'RETENCIONES', 'VENTA', 'ACTIVO', 'TEXTO'),
(38, 'COD_RETEFUENTE_PAGAR', 'Retefuente por pagar - Egresos', 'RETENCIONES', 'EGRESO', 'ACTIVO', 'TEXTO'),
(39, 'COD_RETEIVA_PAGAR', 'Retención de IVA por pagar', 'RETENCIONES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(40, 'COD_RETEIVA_PAGAR', 'ReteIVA por pagar - Compras', 'RETENCIONES', 'COMPRA', 'ACTIVO', 'TEXTO'),
(41, 'COD_RETEIVA_PAGAR', 'ReteIVA por pagar - Ventas', 'RETENCIONES', 'VENTA', 'ACTIVO', 'TEXTO'),
(42, 'COD_RETEIVA_PAGAR', 'ReteIVA por pagar - Egresos', 'RETENCIONES', 'EGRESO', 'ACTIVO', 'TEXTO'),
(43, 'COD_RESULTADO_EJERCICIO', 'Resultado del ejercicio (cierre anual)', 'GENERALES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(44, 'COD_ANTICIPO_RETEFUENTE', 'Anticipo retefuente sufrida (activo)', 'RETENCIONES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(45, 'COD_ANTICIPO_RETEFUENTE', 'Anticipo retefuente - Compras', 'RETENCIONES', 'COMPRA', 'ACTIVO', 'TEXTO'),
(46, 'COD_ANTICIPO_RETEFUENTE', 'Anticipo retefuente - Ventas', 'RETENCIONES', 'VENTA', 'ACTIVO', 'TEXTO'),
(47, 'COD_ANTICIPO_RETEIVA', 'Anticipo reteIVA sufrida (activo)', 'RETENCIONES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(48, 'COD_ANTICIPO_RETEIVA', 'Anticipo reteIVA - Compras', 'RETENCIONES', 'COMPRA', 'ACTIVO', 'TEXTO'),
(49, 'COD_ANTICIPO_RETEIVA', 'Anticipo reteIVA - Ventas', 'RETENCIONES', 'VENTA', 'ACTIVO', 'TEXTO'),
(50, 'COD_ANTICIPO_RETEICA', 'Anticipo reteICA sufrida (activo)', 'RETENCIONES', 'GLOBAL', 'ACTIVO', 'TEXTO'),
(51, 'COD_ANTICIPO_RETEICA', 'Anticipo reteICA - Compras', 'RETENCIONES', 'COMPRA', 'ACTIVO', 'TEXTO'),
(52, 'COD_ANTICIPO_RETEICA', 'Anticipo reteICA - Ventas', 'RETENCIONES', 'VENTA', 'ACTIVO', 'TEXTO');;
ALTER TABLE parametros AUTO_INCREMENT = 53;

--
-- Table structure for table `parametroscomprobantes`
--

DROP TABLE IF EXISTS `parametroscomprobantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parametroscomprobantes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `parametroid` int DEFAULT NULL,
  `comprobanteContableid` bigint DEFAULT NULL,
  `valor` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parametroid` (`parametroid`),
  KEY `comprobanteContableid` (`comprobanteContableid`),
  CONSTRAINT `parametroscomprobantes_ibfk_1` FOREIGN KEY (`parametroid`) REFERENCES `parametros` (`id`),
  CONSTRAINT `parametroscomprobantes_ibfk_2` FOREIGN KEY (`comprobanteContableid`) REFERENCES `comprobantes_contables` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `parametrosglobales`
--

DROP TABLE IF EXISTS `parametrosglobales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parametrosglobales` (
  `id` int NOT NULL AUTO_INCREMENT,
  `parametroid` int DEFAULT NULL,
  `valor` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parametroid` (`parametroid`),
  CONSTRAINT `parametrosglobales_ibfk_1` FOREIGN KEY (`parametroid`) REFERENCES `parametros` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pedidos`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `periodos_contables`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `permisos` VALUES (4,'creacion de recibo de ingresos'),(5,'creacionusuario'),(8,'creacionempresa'),(9,'VENTA'),(10,'Anular comprobantes'),(11,'Reportes generales'),(12,'Configuracion contable'),(13,'superadmin'),(14,'Administradorroles'),(15,'Parametros'),(16,'Crear devolucion'),(17,'Anular devolucion'),(18,'Cionsultar devolucion'),(19,'Crear recibo'),(20,'Anular recibo'),(21,'Modificar recibo'),(22,'Crear usuario'),(23,'Modificar usuario'),(24,'Eliminar usuario'),(25,'Crear egreso'),(26,'Eliminar egreso'),(27,'Modificar egreso'),(28,'Crear comprobantes'),(29,'Modificar comprobantes');

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
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `precios`
--

DROP TABLE IF EXISTS `precios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `precios` (
  `precio_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`precio_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `precios` VALUES (1,'Detal'),(2,'Por mayor'),(3,'Especial');

--
-- Table structure for table `precios_producto_variante`
--

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

--
-- Table structure for table `producto_variantes`
--

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

--
-- Table structure for table `producto_variantes_detalle`
--

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

--
-- Table structure for table `recibo_caja_medio_pago`
--

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

--
-- Table structure for table `recibos_caja`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `regimen` VALUES (1,'48','Responsable de iva','ACTIVO'),(2,'49','No responsable de iva','ACTIVO'),(3,'47','Regimen simple de tributaciÃ³n','ACTIVO'),(4,'50','Grande contribuyente','ACTIVO');

--
-- Table structure for table `resoluciones`
--

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

--
-- Table structure for table `resoluciones_comprobantes`
--

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

--
-- Table structure for table `retenciones`
--

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

INSERT INTO `retenciones` VALUES (1,5,'ReteIva',0.00,16.00),(2,6,'ReteFuente Compra Bienes',1200000.00,2.50),(3,7,'ReteFuente Servicios',180000.00,4.00);

--
-- Table structure for table `retenciones_terceros`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `roles` VALUES (6,'Administrador'),(7,'Inventario'),(8,'Caja'),(9,'Facturacion');

--
-- Table structure for table `sedes_tercero`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `terceros`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `terceros` VALUES (1,3,'222222222222','7','Consumidor','Final','','','Consumidor Final',1,1,'Direccion',NULL,NULL,NULL,'consu@gmail.com',0,2000000,1,NULL,NULL,0,1,'2026-06-30 04:34:02','ACTIVO',0,0);

--
-- Table structure for table `tipo_caracteristica`
--

DROP TABLE IF EXISTS `tipo_caracteristica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_caracteristica` (
  `tipo_caracteristica_id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`tipo_caracteristica_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `tipo_caracteristica` VALUES (1,'Color'),(2,'Talla');

--
-- Table structure for table `tipo_contacto`
--

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

INSERT INTO `tipo_contacto` VALUES (1,'CELULAR','CELULAR',1),(2,'CORREO','CORREO',1),(3,'TELEFONO','TELEFONO',1);

--
-- Table structure for table `tipo_producto`
--

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

INSERT INTO `tipo_producto` VALUES (1,'Producto','producto principal',1,'2026-01-15 16:56:29'),(2,'Servicio','servicio producto',1,'2026-02-28 10:03:15');

--
-- Table structure for table `tipo_totales`
--

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

INSERT INTO `tipo_totales` VALUES (1,'Gravada','Grav','ACTIVO','BASE'),(2,'Impuesto de venta','Iva19','ACTIVO','IMPUESTO'),(3,'Exenta','Exc','ACTIVO','BASE');

--
-- Table structure for table `tipo_totales_facturas`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=93360 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tipo_transaccion`
--

DROP TABLE IF EXISTS `tipo_transaccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_transaccion` (
  `tipo_transaccion_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`tipo_transaccion_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93292 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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

INSERT INTO `tipoidentificacion` VALUES (1,11,'Registro Civil Nacimiento'),(2,12,'Tarjeta de Identidad'),(3,13,'Cedula de Ciudadania'),(4,21,'Tarjeta de Extranjeria'),(5,22,'Cedula de Extranjeria'),(6,31,'Nit'),(7,41,'Pasaporte'),(8,42,'Tipo de documento Extrajero'),(9,43,'Sin Identificacion del exterior o para uso definido por la DIAN');

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

INSERT INTO `tipopersona` VALUES (1,'Juridica'),(2,'Natural');

--
-- Table structure for table `tipos_comprobante_manual`
--

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

--
-- Table structure for table `transacciones`
--

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

--
-- Table structure for table `unidades_medida`
--

DROP TABLE IF EXISTS `unidades_medida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unidades_medida` (
  `unidad_medida_id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `sigla` varchar(10) NOT NULL,
  `valorsigla` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`unidad_medida_id`),
  UNIQUE KEY `uq_unidades_medida_sigla` (`sigla`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `unidades_medida` VALUES (1,'unidad','UND','94'),(2,'gramos','GR','GRM'),(3,'Kilogramos','KG','KGM'),(4,'Caja','BX','BX'),(5,'Bolsa','BG','BG');

--
-- Table structure for table `unidades_medida_producto`
--

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

--
-- Table structure for table `usuariobodega`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuarios`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuarios_clientes`
--



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

--
-- Table structure for table `vendedores`
--

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

--
-- Table structure for table `venta_metodos_pago`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ventas`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=186 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-16 21:58:44









--
-- Dumping routines for database 'cavsystems'
--
/*!50003 DROP FUNCTION IF EXISTS `fn_cajero_tiene_z_pendiente` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `fn_cajero_tiene_z_pendiente`(p_cajero_id INT) RETURNS tinyint(1)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_transacciones INT DEFAULT 0;
    DECLARE v_apertura_hoy INT DEFAULT 0;
    DECLARE v_resultado BOOLEAN DEFAULT FALSE;

    -- CondiciÃ³n A: Â¿tuvo movimientos ayer?
    SELECT COUNT(*) INTO v_transacciones
    FROM movimiento_cajero
    WHERE DATE(fecha_movimiento) = CURDATE() - INTERVAL 1 DAY
      AND cajero_id = p_cajero_id;

    -- CondiciÃ³n B: Â¿tiene apertura registrada hoy?
    SELECT COUNT(*) INTO v_apertura_hoy
    FROM detalle_cajero
    WHERE DATE(fecha_apertura) = CURDATE()
      AND cajero_id = p_cajero_id;

    -- Si NO tiene apertura hoy (v_apertura_hoy = 0) => condiciÃ³n B = TRUE
    -- Si tuvo transacciones ayer (v_transacciones > 0) => condiciÃ³n A = TRUE
    -- Ambas TRUE => tiene Z pendiente
    IF v_transacciones <= 0 or v_apertura_hoy > 0 THEN
        SET v_resultado = TRUE;
    ELSE
        SET v_resultado = FALSE;
    END IF;

    RETURN v_resultado;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_listar_empresas_todos_tenants` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = cp850 */ ;
/*!50003 SET character_set_results = cp850 */ ;
/*!50003 SET collation_connection  = cp850_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_listar_empresas_todos_tenants`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE db_name VARCHAR(255);
    DECLARE sql_query LONGTEXT DEFAULT '';

    DECLARE cur CURSOR FOR
        SELECT t.TABLE_SCHEMA
        FROM information_schema.TABLES t
        WHERE t.TABLE_NAME = 'empresa'
          AND t.TABLE_SCHEMA NOT IN ('information_schema','mysql','performance_schema','sys','administrador');

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO db_name;
        IF done THEN
            LEAVE read_loop;
        END IF;

        SET sql_query = CONCAT(sql_query,
        'SELECT
            "', db_name, '" AS tenant,
            estado,
            codigo,
            codigotipopersona,
            codigotipoidentificacion,
            numeroidentificacion,
            digitoverificacion,
            primernombre,
            segundonombre,
            primerapellido,
            segundoapellido,
            razonsocial,
            codigopostal,
            nombrecomercial,
            codigoactividadeconomica,
            codigoregimen,
            correoempresa,
            celularempresa,
            telfonofijo,
            codigopais,
            codigodepartamento,
            codigomunicipio,
            imagenempresa,
            tipoImagen,
            fechainiciolicencia,
            fecharenovacion,
            fechafinallicencia,
            CASE
                WHEN fechafinallicencia IS NULL THEN ''SIN_LICENCIA''
                WHEN CURDATE() <= DATE_ADD(fechafinallicencia, INTERVAL IFNULL(plazo,0) DAY) THEN ''ACTIVA''
                ELSE ''VENCIDA''
            END AS estadolicencia,
            plazo,
            numerousuarios,
            responsabilidad_fiscal AS responsabilidadFiscal,
            tipo_contribuyente     AS tipoContribuyente,
            gran_contribuyente     AS granContribuyente,
            autorretenedor,
            responsable_iva        AS responsableIva
        FROM ', db_name, '.empresa
        UNION ALL ');
    END LOOP;

    CLOSE cur;

    IF sql_query IS NULL OR sql_query = '' THEN
        SELECT
            NULL AS tenant, NULL AS estado, NULL AS codigo,
            NULL AS codigotipopersona, NULL AS codigotipoidentificacion,
            NULL AS numeroidentificacion, NULL AS digitoverificacion,
            NULL AS primernombre, NULL AS segundonombre,
            NULL AS primerapellido, NULL AS segundoapellido,
            NULL AS razonsocial, NULL AS codigopostal, NULL AS nombrecomercial,
            NULL AS codigoactividadeconomica, NULL AS codigoregimen,
            NULL AS correoempresa, NULL AS celularempresa, NULL AS telfonofijo,
            NULL AS codigopais, NULL AS codigodepartamento, NULL AS codigomunicipio,
            NULL AS imagenempresa, NULL AS tipoImagen,
            NULL AS fechainiciolicencia, NULL AS fecharenovacion, NULL AS fechafinallicencia,
            NULL AS estadolicencia, NULL AS plazo, NULL AS numerousuarios,
            NULL AS responsabilidadFiscal, NULL AS tipoContribuyente,
            NULL AS granContribuyente, NULL AS autorretenedor, NULL AS responsableIva
        FROM dual WHERE 1=0;
    ELSE
        SET sql_query = LEFT(sql_query, LENGTH(sql_query) - 10);
        SET @final_query = sql_query;
        PREPARE stmt FROM @final_query;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `verificar_relaciones_bodega` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `verificar_relaciones_bodega`(
    IN p_codigo_bodega INT
)
BEGIN

    DECLARE done INT DEFAULT FALSE;

    DECLARE v_table_name VARCHAR(255);
    DECLARE v_column_name VARCHAR(255);

    DECLARE total_relaciones INT DEFAULT 0;

    DECLARE sql_query TEXT;

    DECLARE cur CURSOR FOR
        SELECT
            TABLE_NAME,
            COLUMN_NAME
        FROM information_schema.KEY_COLUMN_USAGE
        WHERE REFERENCED_TABLE_NAME = 'bodegas'
        AND TABLE_SCHEMA = DATABASE();

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET done = TRUE;


    OPEN cur;

    read_loop: LOOP

        FETCH cur INTO v_table_name, v_column_name;

        IF done THEN
            LEAVE read_loop;
        END IF;


        SET @sql = CONCAT(
            'SELECT COUNT(*) INTO @cantidad FROM ',
            v_table_name,
            ' WHERE ',
            v_column_name,
            ' = ',
            p_codigo_bodega
        );

        PREPARE stmt FROM @sql;

        EXECUTE stmt;

        DEALLOCATE PREPARE stmt;


        IF @cantidad > 0 THEN
            SET total_relaciones = total_relaciones + 1;
        END IF;

    END LOOP;

    CLOSE cur;


    SELECT
        total_relaciones > 0 AS tieneRelaciones;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-16 22:49:19