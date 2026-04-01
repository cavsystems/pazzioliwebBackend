package com.pazzioliweb.tercerosmodule.entity;

import com.pazzioliweb.commonbacken.entity.Retenciones;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "retenciones_terceros")
public class Retencionesterceros {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "retenciontercero_id")
    private Integer sedeId;
	
	  @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "retencion_id", nullable = false)
	    private Retenciones reteciones;
	  
	  @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "tercero_id", nullable = false)
	    private Terceros tercero;
	  
	  
	  
	  
	  
	  

	

}
