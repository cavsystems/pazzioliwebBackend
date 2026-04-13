package com.pazzioliweb.commonbacken.conexiondb;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {
	 @Bean
	    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
	            MultiTenantConnectionProvider connectionProvider,
	            CurrentTenantIdentifierResolver tenantResolver) {

	        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
	        emf.setPackagesToScan("com.pazzioliweb");
	        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

	        Map<String, Object> props = new HashMap<>();
	 	    props.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
	        props.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
	        props.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
	        props.put(Environment.SHOW_SQL, true);

	        emf.setJpaPropertyMap(props);
	        return emf;
	    }

	    @Bean
	    public MultiTenantConnectionProvider connectionProvider() {
	        return new DataSourceMultiTenantConnectionProviderImpl();
	    }

	    @Bean
	    public CurrentTenantIdentifierResolver tenantIdentifierResolver() {
	        return new CurrentTenantIdentifierResolverImpl();
	    }

	    @Bean
	    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
	        return new JpaTransactionManager(emf);
	    }
}
