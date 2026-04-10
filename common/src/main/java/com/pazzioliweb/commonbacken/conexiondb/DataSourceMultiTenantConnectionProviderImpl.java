package com.pazzioliweb.commonbacken.conexiondb;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Value;

import com.zaxxer.hikari.HikariDataSource;

public class DataSourceMultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
	
	@Value("${spring.datasource.host:localhost}")
    private String dbHost;

    @Value("${spring.datasource.port:3306}")
    private String dbPort;

    @Value("${spring.datasource.username:root}")
    private String dbUser;

    @Value("${spring.datasource.password:root}")
    private String dbPass;
	
    private final Map<String, DataSource> dataSources = new HashMap<>();

    public DataSourceMultiTenantConnectionProviderImpl() {
        // Cargar conexiones disponibles (puede ser dinámico también)
    	dataSources.put("administrador", buildDataSource("administrador"));

    }

    private DataSource buildDataSource(String dbName) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/" + dbName);
        ds.setUsername("root");
        ds.setPassword("root");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        /*ds.setJdbcUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName);
        ds.setUsername(dbUser);
        ds.setPassword(dbPass);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");*/
        return ds;
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        DataSource dataSource = dataSources.get(tenantIdentifier);

        // Si no existe aún, lo construyes
        if (dataSource == null) {
            dataSource = buildDataSource(tenantIdentifier);
            dataSources.put(tenantIdentifier, dataSource); // guardas la instancia para la próxima vez
        }

        return dataSource.getConnection();
    }

   
	@Override
	public boolean isUnwrappableAs(Class<?> unwrapType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection getAnyConnection() throws SQLException {
		// TODO Auto-generated method stub
		return dataSources.get("administrador").getConnection();
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		  if (connection != null && !connection.isClosed()) {
		        connection.close(); // 🔹 Devolver al pool
		    }
		
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		if (connection != null && !connection.isClosed()) {
	        connection.close(); // 🔹 Devolver al pool
	    }
		
	}

	@Override
	public boolean supportsAggressiveRelease() {
		// TODO Auto-generated method stub
		return false;
	}

}