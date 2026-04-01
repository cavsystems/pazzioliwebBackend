package com.pazzioliweb.commonbacken.conexiondb;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.stereotype.Component;

@Component
public class DataSourceMultiTenantConnectionProvider
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private final DataSource defaultDataSource;
    private final Map<String, DataSource> dataSources = new HashMap<>();

    public DataSourceMultiTenantConnectionProvider(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
        dataSources.put("default", defaultDataSource);
    }

    public void addDataSource(String tenantId, DataSource dataSource) {
        dataSources.put(tenantId, dataSource);
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return defaultDataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return dataSources.getOrDefault(tenantIdentifier, defaultDataSource);
    }
}
