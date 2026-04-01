package com.pazzioliweb.comprasmodule.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import com.pazzioliweb.commonbacken.conexiondb.TenantContext;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() != null) {
                String token = authentication.getCredentials().toString();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
            String tenant = TenantContext.getCurrentTenant();
            if (tenant != null) {
                requestTemplate.header("X-TenantID", tenant);
            }
        };
    }
}