package com.pazzioliweb.productosmodule.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.commonbacken.dtos.response.ApiResponse;
import com.pazzioliweb.commonbacken.util.PasswordUtils;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.Usuariobodega;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.productosmodule.repositori.UsuariobodegaRepository;
import com.pazzioliweb.productosmodule.service.BodegasService;

import com.pazzioliweb.usuariosbacken.dtos.CrearusuariorolDTOS;
import com.pazzioliweb.usuariosbacken.dtos.UsuariobodegasidDTOS;
import com.pazzioliweb.usuariosbacken.entity.Roles;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRepository;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRolRepository;
import com.pazzioliweb.usuariosbacken.repositorio.RolesRepository;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/bodegas")
public class BodegasController {
	/*@Autowired
	private PasswordEncoder passwordEncoder;*/
	@Autowired
	private  UsuariobodegaRepository usubo;
	@Autowired
	private BodegasService bodegaService;
	@Autowired
	private UsuarioRepository usurepo;
	@Autowired
	private BodegasRepository repobo;
	@Autowired
	private RolesRepository rolesrepo;
	@Autowired
	private PermisoRepository repopermiso;
	@Autowired
	private RolesRepository reporoles;
	@Autowired
	private PermisoRolRepository permisorol;
	@Autowired
	private UsuariobodegaRepository  usurepobode;
    @Autowired
    public BodegasController(BodegasService bodegaService) {
        this.bodegaService = bodegaService;
    }
    
    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<List<Bodegas>>> listar() {
    	List<Bodegas> bodegasListadas = bodegaService.listarBodegas();
    	if(!bodegasListadas.isEmpty()) {
    		return ResponseEntity
    				.ok(ApiResponse.success("Bodegas encontradas",bodegasListadas));
    	}else {
    		System.out.println("no hay bodegas");
    		return ResponseEntity
    			    .status(HttpStatus.OK)
    			    .body(ApiResponse.failure("No hay Bodegas disponibles"));
    	} 
    }
    public record MensajeResponsederol(String mensaje, boolean estado) {}
    
    
	 @Transactional
		@PostMapping(value = "/crear/usuario")
	 
		public ResponseEntity<	Map<String, Object>> crearusuario(@RequestBody CrearusuariorolDTOS  nombrerol) {
			Map<String, Object> response = new HashMap<>();
			response.clear();
		 System.out.println(nombrerol.getContrasena());
	      Optional<Roles> rol= reporoles.findByCodigo(nombrerol.getRol());
	    String encrippassword=PasswordUtils.encrypt(nombrerol.getContrasena());
	    
		 Usuario nuevousu=new Usuario();
		 nuevousu.setNombre(nombrerol.getNombre());
		 nuevousu.setUsuario(nombrerol.getUsuario());
		 nuevousu.setContrasena(encrippassword);
		 nuevousu.setEstado(nombrerol.getEstado());
		 nuevousu.setCodigousuariocreado(1);
		 nuevousu.setCodigorol(rol.get());
		  response.put("mensaje", new MensajeResponsederol("rolcreado",true) );
		  Usuario usuariob=usurepo.save( nuevousu);
		  int idusu=usuariob.getCodigo();
		  if(nombrerol.getBodegas().length>0) {
			  for (int bo : nombrerol.getBodegas()) {
		    	  Optional<Bodegas> bode=repobo.findByCodigo(bo);
		    	  
		    	 Usuariobodega usuariobo=new Usuariobodega();
		    	 usuariobo.setBodegaid( bode.get());
		    	 usuariobo.setUsuarioid(usuariob);
		    	 
		    	 usurepobode.save(usuariobo);
		    	 
		    	 
		  }
		 
	    	  
	    	  
			
		}
		  return ResponseEntity.ok().body(response);
		 
		 
		 
	 }
	
	 
	 
	 
	 @Transactional
		@PostMapping(value = "/actualizar/usuario")
	 
		public ResponseEntity<	Map<String, Object>> Actulizar(@RequestBody CrearusuariorolDTOS  nombrerol,@RequestParam int co) {
			Map<String, Object> response = new HashMap<>();
			response.clear();
		 System.out.println("idusuario"+co);
	      Optional<Roles> rol= reporoles.findByCodigo(nombrerol.getRol());
	      Optional<Usuario> usuop=usurepo.findByCodigo(co);
	  	if(usuop.isEmpty()) {
	  		response.put("mensaje", new MensajeResponsederol("usuariono existe",false));
	  		return ResponseEntity.status(500).body(response);
	  	}
		Usuario usuarioupdata= usuop.get();
	usuarioupdata.setUsuario(nombrerol.getUsuario());
		usuarioupdata.setNombre(nombrerol.getNombre());
	
		usuarioupdata.setEstado(nombrerol.getEstado());
		 usuarioupdata.setCodigorol(rol.get());
		  response.put("mensaje", new MensajeResponsederol("usuarioactulizado",true) );
		  Usuario usuariob=usurepo.save(usuarioupdata);
		  int idusu=usuariob.getCodigo();
		  System.out.println("id usuario actual"+idusu);
	    	 List<UsuariobodegasidDTOS>  usuabo= repobo.findByUsuariobodegausuario( idusu );
	    	 System.out.println(usuabo.size()+"idusuario final"+idusu);
	    	 if(usuabo.size()>0) { 
	   	  for (UsuariobodegasidDTOS bo : usuabo) {
			  
			    
	   		 usubo.deleteByIdUsuariobodega( bo.getCodigo());
		  }
	    	 
	    		
	    		 
	    	  }
	    
		  if(nombrerol.getBodegas().length>0) {
		
			  
			  
			  
			  for (int bo : nombrerol.getBodegas()) {
				  Optional<Bodegas> bode=repobo.findByCodigo(bo);
					 Usuariobodega usuariobo=new Usuariobodega();
			    	 usuariobo.setBodegaid( bode.get());
			    	 usuariobo.setUsuarioid(usuariob);
			    	 
			    	 usurepobode.save(usuariobo);
			    	 
			  }
		 
	    	  
	    	  
			
		}
		  return ResponseEntity.ok().body(response);
		 
		 
		 
	 }
	
	
}
