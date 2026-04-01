package com.pazzioliweb.commonbacken.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "retenciones")
public class Retenciones {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name = "retencion_id")
	    private Integer retencionId;
	 
	 private Integer codigo;
	 private  String nombre;
	 private Double base;
	 private Double porcentaje;
	 public Integer getRetencionId() {
		 return retencionId;
	 }
	 public void setRetencion_id(Integer retencion_id) {
		 this.retencionId = retencion_id;
	 }
	 public Integer getCodigo() {
		 return codigo;
	 }
	 public void setCodigo(Integer codigo) {
		 this.codigo = codigo;
	 }
	 public String getNombre() {
		 return nombre;
	 }
	 public void setNombre(String nombre) {
		 this.nombre = nombre;
	 }
	 public Double getBase() {
		 return base;
	 }
	 public void setBase(Double base) {
		 this.base = base;
	 }
	 public Double getPorcentaje() {
		 return porcentaje;
	 }
	 public void setPorcentaje(Double porcentaje) {
		 this.porcentaje = porcentaje;
	 }
	 
	 
}
