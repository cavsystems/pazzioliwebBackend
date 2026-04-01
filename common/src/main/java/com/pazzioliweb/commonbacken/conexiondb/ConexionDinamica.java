package com.pazzioliweb.commonbacken.conexiondb;

import javax.sql.DataSource;

public class ConexionDinamica {
	private String nombre;
    private DataSource dataSource;

    public ConexionDinamica( DataSource dataSource) {
       
        this.dataSource = dataSource;
    }

  

    public DataSource getDataSource() {
        return dataSource;
    }
}
