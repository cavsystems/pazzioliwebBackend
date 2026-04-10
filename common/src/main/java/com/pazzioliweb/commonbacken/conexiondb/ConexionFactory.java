package com.pazzioliweb.commonbacken.conexiondb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
//allowPublicKeyRetrieval lo especificamos en la url mas que todo cuando tranbajmos con mysql 8 en a delante
@Component
public class ConexionFactory {

	@Value("${spring.datasource.host:localhost}")
	private String dbHost;

	@Value("${spring.datasource.port:3306}")
	private String dbPort;

	@Value("${spring.datasource.username:root}")
	private String dbUser;

	@Value("${spring.datasource.password:root}")
	private String dbPass;

	public ConexionDinamica crearConexion(String nombreConexion) {
		HikariConfig config = new HikariConfig();
	         /*config.setJdbcUrl("jdbc:mysql://127.0.0.1:3307/" + nombreConexion +
	        		  "?administrador&serverTimezone=America/Bogota");*/
		config.setJdbcUrl("jdbc:mysql://localhost:3306/" + nombreConexion);
		config.setUsername("root");
		config.setPassword("root");
		config.setDriverClassName("com.mysql.cj.jdbc.Driver");

	        /*config.setJdbcUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + nombreConexion);
	        config.setUsername(dbUser);
	        config.setPassword(dbPass);
	        config.setDriverClassName("com.mysql.cj.jdbc.Driver");*/

		HikariDataSource dataSource = new HikariDataSource(config);
		return new ConexionDinamica(dataSource);
	}
}