package com.pazzioliweb.commonbacken.services;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class TenantService {

	private static final String TEMPLATE_SCHEMA = "_tenant_template";

	private final DataSource ds;
	private final JdbcTemplate jdbc;

	@Value("${spring.datasource.host:localhost}")
	private String dbHost;

	@Value("${spring.datasource.port:3306}")
	private String dbPort;

	@Value("${spring.datasource.username:root}")
	private String dbUser;

	@Value("${spring.datasource.password:root}")
	private String dbPass;

	public TenantService(DataSource ds, JdbcTemplate jdbc) {
		this.ds = ds;
		this.jdbc = jdbc;
	}

	/** Crea o actualiza el schema plantilla corriendo Flyway sobre él. Se llama al iniciar la app. */
	public void initTemplateSchema() {
		// Siempre ejecuta Flyway sobre el template para aplicar las migraciones más recientes.
		Flyway flyway = Flyway.configure()
			.dataSource(ds)
			.schemas(TEMPLATE_SCHEMA)
			.locations("classpath:db/migration/common")
			.load();

		// Repair para alinear checksums si algún archivo de migración se editó.
		try {
			flyway.repair();
		} catch (Exception e) {
			System.out.println("[TenantService] Repair skipped (likely first run): " + e.getMessage());
		}

		flyway.migrate();
	}

	/**
	 * Clona el schema plantilla en un nuevo schema de tenant.
	 * Es ~10x más rápido que correr Flyway desde cero porque MySQL copia
	 * las tablas internamente sin parsear SQL ni hacer roundtrips JDBC por cada fila.
	 */
	public void initTenantSchema(String schema) {
		boolean templateListo = templateSchemaExiste() && templateTieneTablasCreadas();

		if (!templateListo) {
			// Primera vez o template roto: crear template primero
			initTemplateSchema();
		}

		clonarSchemaDesdeTemplate(schema);
	}

	private boolean templateSchemaExiste() {
		String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
		Integer count = jdbc.queryForObject(sql, Integer.class, TEMPLATE_SCHEMA);
		return count != null && count > 0;
	}

	private boolean templateTieneTablasCreadas() {
		String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'";
		Integer count = jdbc.queryForObject(sql, Integer.class, TEMPLATE_SCHEMA);
		return count != null && count > 5;
	}

	private void clonarSchemaDesdeTemplate(String nuevoSchema) {
		// Crear el schema destino
		jdbc.execute("CREATE SCHEMA IF NOT EXISTS `" + nuevoSchema + "`");

		// Obtener lista de tablas del template
		String sqlTablas = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
			"WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME";
		List<Map<String, Object>> tablas = jdbc.queryForList(sqlTablas, TEMPLATE_SCHEMA);

		// Desactivar FK checks para la copia
		jdbc.execute("SET FOREIGN_KEY_CHECKS = 0");

		try {
			for (Map<String, Object> tabla : tablas) {
				String nombreTabla = (String) tabla.get("TABLE_NAME");
				String src = "`" + TEMPLATE_SCHEMA + "`.`" + nombreTabla + "`";
				String dst = "`" + nuevoSchema + "`.`" + nombreTabla + "`";

				// Copiar estructura
				jdbc.execute("CREATE TABLE IF NOT EXISTS " + dst + " LIKE " + src);

				// Copiar datos de referencia (actividades económicas, impuestos, etc.)
				jdbc.execute("INSERT IGNORE INTO " + dst + " SELECT * FROM " + src);
			}

			// Copiar funciones y procedimientos almacenados
			clonarRutinasDesdeTemplate(nuevoSchema);
		} finally {
			jdbc.execute("SET FOREIGN_KEY_CHECKS = 1");
		}
	}

	private void clonarRutinasDesdeTemplate(String nuevoSchema) {
		System.out.println("[TenantService] Iniciando clonación de rutinas para schema: " + nuevoSchema);
		try {
			// Obtener funciones del template
			String sqlFunciones = "SELECT ROUTINE_NAME FROM INFORMATION_SCHEMA.ROUTINES " +
				"WHERE ROUTINE_SCHEMA = ? AND ROUTINE_TYPE = 'FUNCTION'";
			List<Map<String, Object>> funciones = jdbc.queryForList(sqlFunciones, TEMPLATE_SCHEMA);
			System.out.println("[TenantService] Funciones encontradas en template: " + funciones.size());

			for (Map<String, Object> funcion : funciones) {
				String nombreFuncion = (String) funcion.get("ROUTINE_NAME");
				System.out.println("[TenantService] Procesando función: " + nombreFuncion);
				try {
					// Obtener el código de creación de la función
					String createSql = jdbc.queryForObject(
						"SHOW CREATE FUNCTION `" + TEMPLATE_SCHEMA + "`.`" + nombreFuncion + "`",
						(rs, rowNum) -> rs.getString("Create Function")
					);
					System.out.println("[TenantService] SQL original obtenido para función: " + nombreFuncion);

					// Eliminar DEFINER para evitar problemas de permisos
					createSql = createSql.replaceAll("DEFINER=`[^`]+`@`[^`]+` ", "");
					System.out.println("[TenantService] SQL sin DEFINER para función: " + nombreFuncion);

					// Reemplazar el nombre del schema en el código
					createSql = createSql.replace(TEMPLATE_SCHEMA, nuevoSchema);
					System.out.println("[TenantService] SQL con schema reemplazado para función: " + nombreFuncion);

					// Cambiar al schema destino antes de eliminar y crear
					jdbc.execute("USE `" + nuevoSchema + "`");

					// Eliminar la función si existe en el destino
					jdbc.execute("DROP FUNCTION IF EXISTS `" + nombreFuncion + "`");

					// Crear la función en el nuevo schema
					jdbc.execute(createSql);
					System.out.println("[TenantService] Función clonada exitosamente: " + nombreFuncion);
				} catch (Exception e) {
					System.err.println("[TenantService] Error clonando función " + nombreFuncion + ": " + e.getMessage());
					e.printStackTrace();
				}
			}

			// Obtener procedimientos del template
			String sqlProcedimientos = "SELECT ROUTINE_NAME FROM INFORMATION_SCHEMA.ROUTINES " +
				"WHERE ROUTINE_SCHEMA = ? AND ROUTINE_TYPE = 'PROCEDURE'";
			List<Map<String, Object>> procedimientos = jdbc.queryForList(sqlProcedimientos, TEMPLATE_SCHEMA);
			System.out.println("[TenantService] Procedimientos encontrados en template: " + procedimientos.size());

			for (Map<String, Object> procedimiento : procedimientos) {
				String nombreProcedimiento = (String) procedimiento.get("ROUTINE_NAME");
				System.out.println("[TenantService] Procesando procedimiento: " + nombreProcedimiento);
				try {
					// Obtener el código de creación del procedimiento
					String createSql = jdbc.queryForObject(
						"SHOW CREATE PROCEDURE `" + TEMPLATE_SCHEMA + "`.`" + nombreProcedimiento + "`",
						(rs, rowNum) -> rs.getString("Create Procedure")
					);
					System.out.println("[TenantService] SQL original obtenido para procedimiento: " + nombreProcedimiento);

					// Eliminar DEFINER para evitar problemas de permisos
					createSql = createSql.replaceAll("DEFINER=`[^`]+`@`[^`]+` ", "");
					System.out.println("[TenantService] SQL sin DEFINER para procedimiento: " + nombreProcedimiento);

					// Reemplazar el nombre del schema en el código
					createSql = createSql.replace(TEMPLATE_SCHEMA, nuevoSchema);
					System.out.println("[TenantService] SQL con schema reemplazado para procedimiento: " + nombreProcedimiento);

					// Cambiar al schema destino antes de eliminar y crear
					jdbc.execute("USE `" + nuevoSchema + "`");

					// Eliminar el procedimiento si existe en el destino
					jdbc.execute("DROP PROCEDURE IF EXISTS `" + nombreProcedimiento + "`");

					// Crear el procedimiento en el nuevo schema
					jdbc.execute(createSql);
					System.out.println("[TenantService] Procedimiento clonado exitosamente: " + nombreProcedimiento);
				} catch (Exception e) {
					System.err.println("[TenantService] Error clonando procedimiento " + nombreProcedimiento + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
			System.out.println("[TenantService] Clonación de rutinas completada para schema: " + nuevoSchema);
		} catch (Exception e) {
			System.err.println("[TenantService] Error general clonando rutinas: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
