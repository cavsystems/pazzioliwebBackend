DROP PROCEDURE IF EXISTS sp_listar_empresas_todos_tenants;

DELIMITER //
CREATE PROCEDURE sp_listar_empresas_todos_tenants()
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
END//
DELIMITER ;
