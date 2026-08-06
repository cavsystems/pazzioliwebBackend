package com.pazzioliweb.commonbacken.services;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializa el schema plantilla una vez al arrancar la aplicación (Flyway sobre
 * '_tenant_template', para que las empresas nuevas se creen clonando ese schema en vez
 * de correr Flyway desde cero) y, a continuación, aplica Flyway a todos los tenants
 * reales existentes vía {@link TenantMigrationRunner}.
 */
@Component
public class TenantTemplateInitializer implements ApplicationRunner {

	private final TenantService tenantService;
	private final TenantMigrationRunner tenantMigrationRunner;

	public TenantTemplateInitializer(TenantService tenantService, TenantMigrationRunner tenantMigrationRunner) {
		this.tenantService = tenantService;
		this.tenantMigrationRunner = tenantMigrationRunner;
	}

	@Override
	public void run(ApplicationArguments args) {
		try {
			System.out.println("[TenantTemplate] Inicializando schema plantilla...");
			tenantService.initTemplateSchema();
			System.out.println("[TenantTemplate] Schema plantilla listo.");
		} catch (Exception e) {
			System.err.println("[TenantTemplate] Error inicializando schema plantilla: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("[TenantMigration] Aplicando Flyway a todos los tenants reales...");
		tenantMigrationRunner.migrateAllTenants();
		System.out.println("[TenantMigration] Listo.");
	}
}
