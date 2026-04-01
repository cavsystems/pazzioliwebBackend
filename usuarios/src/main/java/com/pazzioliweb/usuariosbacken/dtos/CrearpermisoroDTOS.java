package com.pazzioliweb.usuariosbacken.dtos;

public class CrearpermisoroDTOS {
	public CrearpermisoroDTOS() {
		
		
	}
public CrearpermisoroDTOS(int codigorol, int codigopermiso) {
	
		this.codigorol = codigorol;
		this.codigopermiso = codigopermiso;
	}
private int codigorol;

public int getCodigorol() {
	return codigorol;
}
public void setCodigorol(int codigorol) {
	this.codigorol = codigorol;
}
public int getCodigopermiso() {
	return codigopermiso;
}
public void setCodigopermiso(int codigopermiso) {
	this.codigopermiso = codigopermiso;
}
private int codigopermiso;

}
