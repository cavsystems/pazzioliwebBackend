package com.pazzioliweb.commonbacken.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.commonbacken.entity.CodigoPostal;
import com.pazzioliweb.commonbacken.entity.Retenciones;
import com.pazzioliweb.commonbacken.repositorio.CodigoPostalRepositori;
import com.pazzioliweb.commonbacken.repositorio.RetencionesRepositori;

@Component
@RestController
@RequestMapping("/api/retencion")
public class 	Retencionescontroller {
	@Autowired
	private RetencionesRepositori repore;
	@Autowired
	private CodigoPostalRepositori codigorepositorio;
	
	@GetMapping("traerretenciones/retenciones")
	public  ResponseEntity<Object> obtenercodigopostal(){
	   List<Retenciones> retencionlist=repore.findAll();
		
		return ResponseEntity.ok(retencionlist);
		
	}
}
