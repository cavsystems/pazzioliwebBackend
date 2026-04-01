package com.pazzioliweb.commonbacken.services;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;
@Service
public class TenantService {
	  private final DataSource ds;

	  public TenantService(DataSource ds) {
	    this.ds = ds;
	  }

	  public void initTenantSchema(String schema) {
	    Flyway.configure()
	      .dataSource(ds)
	      .schemas(schema)
	      .locations("classpath:db/migration"
	      		+ "")
	      .baselineOnMigrate(true)
	      .load()
	      .migrate();
	  }

}
