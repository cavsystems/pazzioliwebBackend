package com.pazzioliweb.commonbacken.util;

import org.springframework.stereotype.Component;

@Component
public class Nombredb {
	String nombre;
	String subfijo="db";
	public void setNombre(String nombre){
		
		this.nombre="db"+"_"+nombre;
		
		
	}
	
	public String getNombre() {
		return nombre;
	}

}
