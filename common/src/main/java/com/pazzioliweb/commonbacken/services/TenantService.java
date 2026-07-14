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
		Flyway.configure()
			.dataSource(ds)
			.schemas(TEMPLATE_SCHEMA)
			.locations("classpath:db/migration")
			.load()
			.migrate();
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
		} finally {
			jdbc.execute("SET FOREIGN_KEY_CHECKS = 1");
		}
	}
}
