package com.pazzioliweb.tercerosmodule.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.tercerosmodule.dtos.ContactoTerceroDTO;
import com.pazzioliweb.tercerosmodule.dtos.ContactoTerceroDTOImpl;
import com.pazzioliweb.tercerosmodule.dtos.TipoContactoInfoImpl;
import com.pazzioliweb.tercerosmodule.entity.ContactoTercero;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.entity.TipoContacto;
import com.pazzioliweb.tercerosmodule.repositori.ContactoTerceroRepository;
import com.pazzioliweb.tercerosmodule.service.ContactosService;

@RestController
@RequestMapping("/api/contactos")
public class Contactosterceros {
	@Autowired
	private ContactosService contactoservi;
	@Autowired
	private ContactoTerceroRepository repocontacto;
	@Autowired
	private   ContactoTerceroRepository contactosrepo;
@GetMapping("/listar")
public ResponseEntity<Object> Listarconctatotipo(){
	List<ContactoTerceroDTO.TipoContactoInfo> tipocont=repocontacto.findByIdcontacto();
	
	return  ResponseEntity.ok(tipocont);
	
}

@GetMapping("/listarconctatos")
public ResponseEntity<Object> Listarconctato(@RequestParam int idtercero){
	List<ContactoTerceroDTO> tipocont=repocontacto.findByTerceroId(idtercero);
	
	return  ResponseEntity.ok(tipocont);
	
}

@PostMapping(value="/crear")
public ResponseEntity<Object> crearcontacto(@RequestBody ContactoTerceroDTOImpl tipocontacto , @RequestParam int idtercero ){
	List<ContactoTerceroDTO> tipocont=repocontacto.findByTerceroId(idtercero);
	return contactoservi.crearcontacto(tipocontacto, idtercero);

	
}

@PutMapping(value="/actulizar")
public ResponseEntity<Object> updatecontacto(@RequestBody ContactoTerceroDTOImpl contacto , @RequestParam int idtercero ){
	ContactoTercero contactotercero=new ContactoTercero();
	Terceros terceros=new Terceros();
	terceros.setTerceroId(idtercero);
	TipoContacto tipoconta=new TipoContacto();
	tipoconta.setTipoContactoId(contacto.getTipoContacto().getTipoContactoId());
	contactotercero.setContactoId(contacto.getContactoId());
	contactotercero.setEsPrincipal(contacto.getEsPrincipal());
	contactotercero.setValorContacto(contacto.getValorContacto());
	contactotercero.setTipoContacto(tipoconta);
	contactotercero.setTercero(terceros);
	contactosrepo.save(contactotercero);
	ContactoTercero ter=contactosrepo.save(contactotercero);
	ContactoTerceroDTOImpl conta=new  ContactoTerceroDTOImpl ();
	conta.fromEntity(ter);
	return ResponseEntity.ok(conta);

	
	
}
@DeleteMapping("eliminar/{id}")
public ResponseEntity<Void>eliminarcon(@PathVariable Integer id) {
	contactosrepo.deleteByContactoId(id);
	   return ResponseEntity.ok().build();
}


}
