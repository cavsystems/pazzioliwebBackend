package com.pazzioliweb.commonbacken.conexiondb;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        return (tenantId != null) ? tenantId : "administrador";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
