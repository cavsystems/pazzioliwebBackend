package com.pazzioliweb.productosmodule.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pazzioliweb.usuariosbacken.dtos.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
	public record MensajeResponseUsuario(String mensaje, boolean estado, UsuarioDTO usuario) {}
    
	 @Transactional
		@PostMapping(value = "/crear/usuario")

		public ResponseEntity<	Map<String, Object>> crearusuario(@RequestBody CrearusuariorolDTOS  nombrerol) {
			Map<String, Object> response = new HashMap<>();
			response.clear();

		if (nombrerol.getUsuario() == null || nombrerol.getUsuario().trim().isEmpty()) {
			response.put("mensaje", new MensajeResponsederol("El usuario es obligatorio", false));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (nombrerol.getContrasena() == null || nombrerol.getContrasena().length() < 6) {
			response.put("mensaje", new MensajeResponsederol("La contraseña debe tener mínimo 6 caracteres", false));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (usurepo.findByUsuario(nombrerol.getUsuario().trim()).isPresent()) {
			response.put("mensaje", new MensajeResponsederol("Ya existe un usuario con ese nombre de usuario", false));
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}
		if (nombrerol.getNombre() != null && !nombrerol.getNombre().trim().isEmpty()
				&& usurepo.findByNombre(nombrerol.getNombre().trim()).isPresent()) {
			response.put("mensaje", new MensajeResponsederol("Ya existe un usuario con ese nombre", false));
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}

	      Optional<Roles> rol= reporoles.findByCodigo(nombrerol.getRol());
		if (rol.isEmpty()) {
			response.put("mensaje", new MensajeResponsederol("Rol no encontrado", false));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	    String encrippassword=PasswordUtils.encrypt(nombrerol.getContrasena());

		 Usuario nuevousu=new Usuario();
		 nuevousu.setNombre(nombrerol.getNombre());
		 nuevousu.setUsuario(nombrerol.getUsuario().trim());
		 nuevousu.setContrasena(encrippassword);
		 nuevousu.setEstado(nombrerol.getEstado());
		 nuevousu.setCodigousuariocreado(1);
		 nuevousu.setCodigorol(rol.get());
		  response.put("mensaje", new MensajeResponsederol("usuariocreado",true) );
		  Usuario usuariob=usurepo.save( nuevousu);
		  if(nombrerol.getBodegas().length>0) {
			  for (int bo : nombrerol.getBodegas()) {
		    	  Optional<Bodegas> bode=repobo.findByCodigo(bo);
		    	  if (bode.isEmpty()) continue;
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
	  		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	  	}
	  	if (rol.isEmpty()) {
	  		response.put("mensaje", new MensajeResponsederol("Rol no encontrado", false));
	  		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	  	}
	  	if (nombrerol.getUsuario() != null && !nombrerol.getUsuario().trim().isEmpty()) {
	  		Optional<Usuario> existing = usurepo.findByUsuario(nombrerol.getUsuario().trim());
	  		if (existing.isPresent() && existing.get().getCodigo() != co) {
	  			response.put("mensaje", new MensajeResponsederol("Ya existe otro usuario con ese nombre de usuario", false));
	  			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	  		}
	  	}
	  	if (nombrerol.getNombre() != null && !nombrerol.getNombre().trim().isEmpty()) {
	  		Optional<Usuario> existingN = usurepo.findByNombre(nombrerol.getNombre().trim());
	  		if (existingN.isPresent() && existingN.get().getCodigo() != co) {
	  			response.put("mensaje", new MensajeResponsederol("Ya existe otro usuario con ese nombre", false));
	  			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	  		}
	  	}
		Usuario usuarioupdata= usuop.get();

usuarioupdata.setUsuario(nombrerol.getUsuario());

usuarioupdata.setNombre(nombrerol.getNombre());

		usuarioupdata.setEstado(nombrerol.getEstado());
		 usuarioupdata.setCodigorol(rol.get());
		  response.put("mensaje", new MensajeResponseUsuario("usuarioactulizado",true,UsuarioDTO.fromEntity(usuarioupdata)) );
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
				  if (bode.isEmpty()) continue;
					 Usuariobodega usuariobo=new Usuariobodega();
			    	 usuariobo.setBodegaid( bode.get());
			    	 usuariobo.setUsuarioid(usuariob);

			    	 usurepobode.save(usuariobo);

			  }



		}
		  return ResponseEntity.ok().body(response);



	 }

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> manejarDuplicado(DataIntegrityViolationException ex) {
		String raw = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
		String lower = raw == null ? "" : raw.toLowerCase();
		String mensaje;
		if (lower.contains("usuarios.usuario") || lower.contains("for key 'usuarios.usuario'")) {
			mensaje = "Ya existe un usuario con ese nombre de usuario";
		} else if (lower.contains("usuarios.nombre") || lower.contains("for key 'usuarios.nombre'")) {
			mensaje = "Ya existe un usuario con ese nombre";
		} else if (lower.contains("duplicate entry")) {
			mensaje = "Ya existe un registro con esos datos";
		} else {
			mensaje = "No se puede guardar: viola una restricción de la base de datos";
		}
		Map<String, Object> response = new HashMap<>();
		response.put("mensaje", new MensajeResponsederol(mensaje, false));
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
}
