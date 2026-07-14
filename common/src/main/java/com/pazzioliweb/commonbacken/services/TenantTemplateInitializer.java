package com.pazzioliweb.commonbacken.services;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializa el schema plantilla una vez al arrancar la aplicación.
 * Esto corre Flyway sobre '_tenant_template' para que las empresas nuevas
 * se creen clonando ese schema en vez de correr Flyway desde cero.
 */
@Component
public class TenantTemplateInitializer implements ApplicationRunner {

	private final TenantService tenantService;

	public TenantTemplateInitializer(TenantService tenantService) {
		this.tenantService = tenantService;
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
	}
}
