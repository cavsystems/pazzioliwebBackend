package com.pazzioliweb.tercerosmodule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pazzioliweb.tercerosmodule.controller.Contactosterceros;
import com.pazzioliweb.tercerosmodule.dtos.ContactoTerceroDTOImpl;
import com.pazzioliweb.tercerosmodule.dtos.TipoContactoInfoImpl;
import com.pazzioliweb.tercerosmodule.entity.ContactoTercero;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.entity.TipoContacto;
import com.pazzioliweb.tercerosmodule.repositori.ContactoTerceroRepository;

@Service
public class ContactosService {
	@Autowired
	private   ContactoTerceroRepository contactosrepo;
	
	public ResponseEntity<Object> crearcontacto(ContactoTerceroDTOImpl contactotercero,int terceroid) {
		TipoContacto tipocontacto= new TipoContacto();
		Terceros terceros=new Terceros();
		terceros.setTerceroId(terceroid);
		tipocontacto.setTipoContactoId(contactotercero.getTipoContacto().getTipoContactoId());
		ContactoTercero contactoter=new ContactoTercero();
		contactoter.setEsPrincipal(false);
		contactoter.setTipoContacto(tipocontacto);
		contactoter.setValorContacto(contactotercero.getValorContacto());
		contactoter.setTercero(terceros);
		contactosrepo.save(contactoter);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(contactoter);
	
		
		
		
	}
}
