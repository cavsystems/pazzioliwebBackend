package com.pazzioliweb.commonbacken.services;

import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Aplica Flyway a todos los tenants reales (todo schema que no sea de sistema, el
 * schema administrador ni el template) cada vez que arranca el backend. Reemplaza el
 * flujo manual de aplicar db_migrations/*.sql a mano, tenant por tenant.
 */
@Service
public class TenantMigrationRunner {

	private static final String TEMPLATE_SCHEMA = "_tenant_template";

	/** Último Vx destructivo (DROP+CREATE, estilo mysqldump). Cualquier tenant real
	 * pre-existente se marca como "ya aplicado" hasta acá, para no reejecutarlo contra
	 * datos reales. Los Vx posteriores a este se escriben siempre de forma no
	 * destructiva (ALTER/CREATE INDEX IF NOT EXISTS/INSERT IGNORE) y sí se ejecutan
	 * de verdad contra tenants reales. */
	private static final String ULTIMO_VX_DESTRUCTIVO = "7";

	private static final List<String> SCHEMAS_EXCLUIDOS = List.of(
			"information_schema", "mysql", "performance_schema", "sys", "administrador", TEMPLATE_SCHEMA);

	private final DataSource ds;
	private final JdbcTemplate jdbc;

	public TenantMigrationRunner(DataSource ds, JdbcTemplate jdbc) {
		this.ds = ds;
		this.jdbc = jdbc;
	}

	public void migrateAllTenants() {
		List<String> schemas = jdbc.queryForList("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA", String.class);

		for (String schema : schemas) {
			if (SCHEMAS_EXCLUIDOS.contains(schema.toLowerCase(Locale.ROOT))) {
				continue;
			}
			migrarTenant(schema);
		}
	}

	private void migrarTenant(String schema) {
		try {
			boolean yaTieneHistorial = tieneHistorialFlyway(schema);

			Flyway flyway = Flyway.configure()
					.dataSource(ds)
					.schemas(schema)
					.locations("classpath:db/migration/common")
					.baselineOnMigrate(!yaTieneHistorial)
					.baselineVersion(ULTIMO_VX_DESTRUCTIVO)
					// outOfOrder: algunos tenants tienen la versión 6 ocupada por un registro
					// viejo mal etiquetado ("subpermisos", previo a la convención Vn) en vez
					// del V6 real (add_vendedor_id_terceros). Sin esto Flyway se niega a avanzar
					// aunque el V6 real sea un ALTER idempotente y seguro de aplicar tarde.
					.outOfOrder(true)
					.load();

			// Repair antes de migrar (mismo patrón que TenantService.initTemplateSchema):
			// limpia filas de migraciones fallidas (p.ej. un intento previo con un archivo
			// que tenía un error de sintaxis) y realinea checksums si algún Vn se editó.
			// Sin esto, un fallo puntual deja el tenant bloqueado en cada arranque hasta
			// una intervención manual.
			try {
				flyway.repair();
			} catch (Exception e) {
				System.out.println("[TenantMigration] " + schema + " -> repair() omitido: " + e.getMessage());
			}

			MigrateResult resultado = flyway.migrate();
			System.out.println("[TenantMigration] " + schema + " -> OK"
					+ (yaTieneHistorial ? "" : " (baseline en V" + ULTIMO_VX_DESTRUCTIVO + ")")
					+ ", " + resultado.migrationsExecuted + " migraciones aplicadas"
					+ (resultado.targetSchemaVersion != null ? ", version final " + resultado.targetSchemaVersion : ""));
		} catch (Exception e) {
			// Aislamiento por tenant: si uno falla no debe tumbar el arranque ni bloquear a los demás.
			System.err.println("[TenantMigration] " + schema + " -> FALLÓ, se continúa con el resto de tenants: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean tieneHistorialFlyway(String schema) {
		Integer count = jdbc.queryForObject(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = 'flyway_schema_history'",
				Integer.class, schema);
		return count != null && count > 0;
	}
}
