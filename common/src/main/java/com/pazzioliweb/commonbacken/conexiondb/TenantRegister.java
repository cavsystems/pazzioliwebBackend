package com.pazzioliweb.commonbacken.conexiondb;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;
@Component
public class TenantRegister {
	 private final MultiTenantDataSource multiTenantDataSource;

	    @Autowired
	    public TenantRegister(MultiTenantDataSource multiTenantDataSource) {
	        this.multiTenantDataSource = multiTenantDataSource;
	    }

	    public void registerNewTenant(String tenantId) {
	        DataSource ds = DataSourceBuilder.create()
	                .url("jdbc:mysql://localhost:3306/" + tenantId + "?serverTimezone=UTC")
	                .username("root")
	                .password("root")
	                .driverClassName("com.mysql.cj.jdbc.Driver")
	                .build();

	        multiTenantDataSource.addDataSource(tenantId, ds);
	    }

}
