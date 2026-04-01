package com.pazzioliweb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pazzioliweb.commonbacken.util.TenantInterceptor;
//creacion de clase personaliza para registrar un interceptor http
//WebMvcConfigurer:Es una interfaz de Spring que te permite personalizar el comportamiento del módulo Spring MVC, como:
//Agregar interceptores
//Agregar mapeos personalizados
//Configurar CORS, formateadores, etc.
@Configuration
public class WebConfig implements WebMvcConfigurer {
	//inyectamos el interceptor personalizada para manejar muti-tenency
    @Autowired
    private TenantInterceptor tenantInterceptor;
//Agrega ese interceptor al pipeline de peticiones
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor);
    }
}