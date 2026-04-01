package com.pazzioliweb.tercerosmodule.dtos;

import com.pazzioliweb.productosmodule.dtos.OtroprecioDTO;
import com.pazzioliweb.productosmodule.entity.Precios;

public class PrecioimplDTO implements OtroprecioDTO {
	 private Integer precioId;
	
	    private String 	descripcion;
	    public Precios toEntity() {
	    	Precios entity = new Precios();
	        entity.setPrecioId(this.precioId);
	        entity.setDescripcion(this.descripcion);
	        return entity;
	    }
		@Override
		public Integer getPrecioId() {
			// TODO Auto-generated method stub
			return precioId;
		}

		@Override
		public String getDescripcion() {
			// TODO Auto-generated method stub
			return descripcion;
		}

}
