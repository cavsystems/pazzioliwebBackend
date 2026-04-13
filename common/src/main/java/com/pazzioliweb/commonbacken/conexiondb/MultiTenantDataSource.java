package com.pazzioliweb.commonbacken.conexiondb;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;
@Component
public class MultiTenantDataSource extends AbstractRoutingDataSource  {
	private Map<Object, Object> resolvedDataSources = new HashMap<>();
	
	 public MultiTenantDataSource() {
	        // Para que no falle en el arranque
	        super.setTargetDataSources(resolvedDataSources);

	        // Opcional: un datasource por defecto (puede ser "cavsystems" u otro)
	        DataSource defaultDs = DataSourceBuilder.create()
	                .url("jdbc:mysql://localhost:3306/cavsystems?serverTimezone=UTC")
	                .username("root")
	                .password("root")
	                .driverClassName("com.mysql.cj.jdbc.Driver")
	                .build();
	        super.setDefaultTargetDataSource(defaultDs);

	        super.afterPropertiesSet();
	    }
	@Override
	protected Object determineCurrentLookupKey() {
		// TODO Auto-generated method stub
		  return TenantContext.getCurrentTenant();
	}
	
	 public void addDataSource(String tenantId, DataSource dataSource) {
	        resolvedDataSources.put(tenantId, dataSource);
	        super.setTargetDataSources(resolvedDataSources);
	        super.afterPropertiesSet(); // 🔑 refresca el enrutador
	    }
	
	
}
