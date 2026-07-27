-- Migración V3: Funciones y procedimientos almacenados
-- Este esquema se clonará para cada nueva empresa/tenant

-- Función: fn_cajero_tiene_z_pendiente
DROP FUNCTION IF EXISTS `fn_cajero_tiene_z_pendiente`;

DELIMITER $$

CREATE DEFINER=`root`@`localhost` FUNCTION `fn_cajero_tiene_z_pendiente`(p_cajero_id INT) RETURNS tinyint(1)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_transacciones INT DEFAULT 0;
    DECLARE v_apertura_hoy INT DEFAULT 0;
    DECLARE v_resultado BOOLEAN DEFAULT FALSE;

    -- Condición A: ¿tuvo movimientos ayer?
    SELECT COUNT(*) INTO v_transacciones
    FROM movimiento_cajero
    WHERE DATE(fecha_movimiento) = CURDATE() - INTERVAL 1 DAY
      AND cajero_id = p_cajero_id;

    -- Condición B: ¿tiene apertura registrada hoy?
    SELECT COUNT(*) INTO v_apertura_hoy
    FROM detalle_cajero
    WHERE DATE(fecha_apertura) = CURDATE()
      AND cajero_id = p_cajero_id;

    -- Tiene Z pendiente SOLO SI:
    --   tuvo transacciones ayer (v_transacciones > 0)
    --   Y NO ha hecho apertura hoy (v_apertura_hoy = 0)
    IF v_transacciones > 0 AND v_apertura_hoy = 0 THEN
        SET v_resultado = TRUE;
    ELSE
        SET v_resultado = FALSE;
    END IF;

    RETURN v_resultado;
END$$

DELIMITER ;

-- Procedimiento: sp_listar_empresas_todos_tenants
DROP PROCEDURE IF EXISTS `sp_listar_empresas_todos_tenants`;

DELIMITER $$

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
END$$

DELIMITER ;

-- Procedimiento: verificar_relaciones_bodega
DROP PROCEDURE IF EXISTS `verificar_relaciones_bodega`;

DELIMITER $$

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

END$$

DELIMITER ;
