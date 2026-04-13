package com.pazzioliweb.authbacken.config;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pazzioliweb.authbacken.filter.Jwtfilter;

import jakarta.servlet.http.HttpServletResponse;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Autowired
	private Jwtfilter filtro ;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {




		System.out.println(">>> Usando SecurityConfig principal para configurar rutas");
		System.out.println("fitro token"+filtro);
		//CORS ESPECIFICO LA CONFIGURACION QUE TENDRA
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth ->  auth.requestMatchers("/error").permitAll().requestMatchers("/api/auth/**").permitAll().requestMatchers("/ws/**").permitAll().requestMatchers("/api/empresa/**")
						.permitAll().requestMatchers("/api/digitoverificacion/**").permitAll().requestMatchers("/api/codigoPostal/**")
						.permitAll().requestMatchers("/api/productos/**").permitAll().requestMatchers("/api/bodegas/**").permitAll().requestMatchers("/api/grupos/**")
						.permitAll().requestMatchers("/api/lineas/**").permitAll().requestMatchers("/api/precios/**").permitAll().requestMatchers("/api/existencias/**")
						.permitAll().requestMatchers("/api/compras/**").permitAll().requestMatchers("/api/terceros/**").permitAll().requestMatchers("/api/comprobantes/**").permitAll().requestMatchers("/api/cajas/**")
						.permitAll().requestMatchers("/api/vendedores/**").permitAll().requestMatchers("/api/metodos_pago/**").permitAll().requestMatchers("/api/tipo_totales/**")
						.permitAll().requestMatchers("/api/facturas/**").permitAll().requestMatchers("/api/usuario/**").permitAll().requestMatchers("/api/clasificacionesTerceros/**")
						.permitAll().requestMatchers("/api/sedeTercero/**").permitAll().requestMatchers("/api/retencion/**").permitAll().requestMatchers("/api/contactos/**")
						.permitAll().requestMatchers("/api/tipos-caracteristica/**").permitAll().requestMatchers("/api/caracteristicas/**").permitAll().requestMatchers("/api/variantes/**")
						.permitAll().requestMatchers("/api/variante-detalle/**").permitAll().requestMatchers("/api/precios-producto-variante/**").permitAll().requestMatchers("/api/productoMaster/**")
						.permitAll().requestMatchers("/api/unidadesMedida/**").permitAll().requestMatchers("/api/unidadesMedidaProducto/**").permitAll().requestMatchers("/api/tipo-producto/**").permitAll().requestMatchers("/api/cajeros/**")
						.permitAll().requestMatchers("/api/detalle-cajeros/**").permitAll().requestMatchers("/api/ventas/**").permitAll().requestMatchers("/api/devoluciones/**").permitAll().requestMatchers("/api/inventario/**")
						.permitAll().anyRequest().authenticated()).exceptionHandling(ex -> ex
						.accessDeniedHandler((request, response, accessDeniedException) -> {
							if (!response.isCommitted()) {
								response.setStatus(HttpServletResponse.SC_FORBIDDEN);
								response.setContentType("application/json");
								response.getWriter().write("{\"error\": \"Acceso denegado. No tienes permisos suficientes.\"}");
							}

						})

				).addFilterBefore(filtro,  UsernamePasswordAuthenticationFilter.class);
    	/*http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());*/
		return http.build();
	}

	//METODO DE CONFIGURACION DE CORS PARA PODER HACER PETICIONES ENTRE DISTRINTOS ORIGENES
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOriginPattern("http://localhost:5173");      // O usa "*" con cuidado
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}


}