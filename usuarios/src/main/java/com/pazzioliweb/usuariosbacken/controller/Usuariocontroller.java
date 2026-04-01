package com.pazzioliweb.usuariosbacken.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pazzioliweb.usuariosbacken.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pazzioliweb.commonbacken.util.PasswordUtils;
import com.pazzioliweb.usuariosbacken.entity.Permiso;
import com.pazzioliweb.usuariosbacken.entity.PermisoRol;
import com.pazzioliweb.usuariosbacken.entity.Roles;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.entity.Usuarioclientes;
import com.pazzioliweb.usuariosbacken.entity.Usuarioclientesusuario;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRepository;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRolRepository;


import com.pazzioliweb.usuariosbacken.repositorio.RolesRepository;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioclientesRepository;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioclientesusuarioRepository;

import jakarta.transaction.Transactional;
@Component
@Controller
@RequestMapping("api/usuario")
public class Usuariocontroller {
	@Autowired
	private UsuarioRepository usurepo;
	@Autowired
	 private UsuarioclientesusuarioRepository usurepocli;
	@Autowired
	private RolesRepository rolesrepo;
	@Autowired
	private PermisoRepository repopermiso;
	@Autowired
	private RolesRepository reporoles;
	@Autowired
	private PermisoRolRepository permisorol;
	@Autowired
	private  UsuarioclientesRepository  personacliente;
	@Autowired
	private UsuarioclientesusuarioRepository usuclienteu;

	@GetMapping("/roles")
	public ResponseEntity<	Map<String, Object>> traerroles() {
		  Map<String, Object> response = new HashMap<>();
		  System.out.println("entro aqui");
		  List<RolesDTOS> roles=rolesrepo.findByNombreNot("superusuario");
		  
		  response.put("roles",roles);
		  
		  return ResponseEntity.ok().body(response);
		 
		 
	
	}
	
	
	@GetMapping("/permisos")
	public ResponseEntity<	Map<String, Object>> traerpermisos() {
		  Map<String, Object> response = new HashMap<>();
		  System.out.println("entro aqui");
		  List<PermisosDTOS> permisos=repopermiso.findAllPermisoBy();
		  
		  response.put("permisos",permisos);
		  
		  return ResponseEntity.ok().body(response);
		 
		 
	
	}

	@GetMapping("/vendedores")
	public ResponseEntity<List<VendedoresDTO>> traerusuario(@RequestParam int codigo, @RequestParam int codigobodega ){
         return  ResponseEntity.ok(usurepo.findBybodegausuario(codigo,codigobodega));

	}
	
	@GetMapping("/traerusuarios")
	 public ResponseEntity<UsuarioDTO> traerusuario(@RequestParam int codigousuario) {
		 Map<String, Object> response = new HashMap<>();
		 response.clear();
		 System.out.println("Entro aqui actulizar"+codigousuario);
		
		 return usurepo.findByCodigo(codigousuario)
		            .map(usuario -> {
		                Roles rol = usuario.getCodigorol();
		                RolDTO rolDTO = new RolDTO(
		                        rol.getCodigo(),
		                        rol.getNombre()
		                );

		                UsuarioDTO dto = new UsuarioDTO(
		                        usuario.getCodigo(),
		                        usuario.getNombre(),
		                        usuario.getUsuario(),
		                        usuario.getEstado(),
		                        rolDTO
		                );

		                return ResponseEntity.ok(dto);
		            })
		            .orElse(ResponseEntity.notFound().build());
		 
	 }
	
	@GetMapping("/permisosroles")
	public ResponseEntity<	Map<String, Object>> traerpermisos(@RequestParam int codigoroles) {
		  Map<String, Object> response = new HashMap<>();
		  System.out.println("entro aqui");
		  List<PermisosrolesDTOS> permisosroles= permisorol.findPermisosActivosByRol(codigoroles).stream()
				    .map(p -> new PermisosrolesDTOS(
				            p.getCodigo(),
				            p.getCodigopermiso().getCodigo(),
				            p.getCodigopermiso().getNombre(),
				            p.getCodigorol().getNombre()
				        ))
				        .toList();
		  
		  response.put("permisos",permisosroles);
		  
		  return ResponseEntity.ok().body(response);
		 
		 
	
	}
	 @Transactional
	@PostMapping(value = "/crear")
	public ResponseEntity<	Map<String, Object>> crearpermisos(@RequestBody CrearusuarioDTOS  nombrerol) {
		Map<String, Object> response = new HashMap<>();
		
		System.out.println("nombre rol"+nombrerol);
  response.put("permisos",nombrerol);
     Roles rol=new Roles();
     rol.setNombre(nombrerol.getNombre());
     reporoles.save(rol);
     
     class MensajeResponse {
         private String mensaje;
         private boolean estado;

         public MensajeResponse(String mensaje, boolean estado) {
             this.mensaje = mensaje;
             this.estado = estado;
         }

         public String getMensaje() { return mensaje; }
         public boolean isEstado() { return estado; }
     }
		  response.put("mensaje", new  MensajeResponse("rolcreado",true) );
		  return ResponseEntity.ok().body(response);
		 
		
	}
	 
	 @DeleteMapping("/eliminar/{idrol}/{idpermiso}")
	public ResponseEntity<	Map<String, Object>> eliminarpermisorol(@PathVariable int idrol,@PathVariable int idpermiso) {
		 Map<String, Object> response = new HashMap<>();
		 permisorol.deleteByCodigorol_CodigoAndCodigopermiso_Codigo(idrol,idpermiso);
		 response.clear();
		 class MensajeResponsede {
	         private String mensaje;
	         private boolean estado;

	         public MensajeResponsede(String mensaje, boolean estado) {
	             this.mensaje = mensaje;
	             this.estado = estado;
	         }

	         public String getMensaje() { return mensaje; }
	         public boolean isEstado() { return estado; }
	     }
		 
		 response.put("mensaje", new  MensajeResponsede("relacionelimnada",true));
		 return  ResponseEntity.ok().body(response);
		 
		 
	 }
	 public record MensajeResponsederol(String mensaje, boolean estado) {}
	 
	 @PutMapping(value = "/crearpermisorol/{idrol}/{idpermiso}")
	 @Transactional
		public ResponseEntity<	Map<String, Object>> crearpermisorol(@PathVariable int idrol,@PathVariable int idpermiso) {
			Map<String, Object> response = new HashMap<>();
			response.clear();
	    Optional<Roles> roloptional=reporoles.findByCodigo(idrol);
	    Optional<Permiso> permisooptional=repopermiso.findByCodigo(idpermiso);
	    PermisoRol rolpermiso=new PermisoRol();
	    if(!roloptional.isEmpty()) {
	    	System.out.println(roloptional.get().getNombre());
	     	   //  rol.setNombre(nombrerol.getNombre());
	     rolpermiso.setCodigorol(roloptional.get());
	     rolpermiso.setCodigopermiso(permisooptional.get());
	     rolpermiso.setEstado("ACTIVO");
	     
	     permisorol.save(rolpermiso);
	     
	     response.put("mesajae", new  MensajeResponsederol("permisorolhecho",true));
	     
	    
	    }
	  
	  
	     
	     class MensajeResponse {
	         private String mensaje;
	         private boolean estado;

	         public MensajeResponse(String mensaje, boolean estado) {
	             this.mensaje = mensaje;
	             this.estado = estado;
	         }

	         public String getMensaje() { return mensaje; }
	         public boolean isEstado() { return estado; }
	     }
			  response.put("mensaje", new  MensajeResponse("rolcreado",true) );
			  return ResponseEntity.ok().body(response);
			 
			
		}
	 @GetMapping("traer/rolesusuario")
	 public ResponseEntity<	Map<String, Object>> traerrolusuaios(){
		 Map<String, Object> response = new HashMap<>();
			response.clear();
		List<RolesusuariosDTOS> rolesusuario=rolesrepo.findByUsuariobodega();
		response.put("rolesusuario",rolesusuario);
		
		return ResponseEntity.ok().body(response);
		
	 }
	 
	 
	 
	 @GetMapping("traerusuario/persona")
	 public ResponseEntity<	Map<String, Object>> traerpersonausuario(){
		 System.out.println("entro aqui traer cliente");		
		 Map<String, Object> response = new HashMap<>(); 
		 response.clear();
		 List<UsuarioclientesDTOS> usuariopersona=usurepocli.findByUsuariclienteind();
		 response.put("usuariosclientes", usuariopersona);
		 System.out.println(usuariopersona.get(0).getEstado());
		 return ResponseEntity.ok().body( response);
	 }
	 
	 @GetMapping("traerusuarioper/persona")
	 public ResponseEntity<	Map<String, Object>> traerpersonausuarioper(@RequestParam int codigopersona){
		 System.out.println("entro aqui traer cliente"+codigopersona);		
		 Map<String, Object> response = new HashMap<>(); 
		 response.clear();
		 List<UsuarioclientesDTOS> usuariopersona=usurepocli.findByUsuaricliente(codigopersona);
		 response.put("usuariosclientes", usuariopersona);
		
		 return ResponseEntity.ok().body( response);
	 }
	 @Transactional
	 @PostMapping(value="/crearpersona",consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
		public ResponseEntity<	Map<String, Object>> crearpersonas(@ModelAttribute  crearpersonaDTOS usuarioper) {
		 Map<String, Object> response = new HashMap<>(); 
		 System.out.println("usuariocliente"+usuarioper.getCorreo());
		 response.clear();
		 Usuarioclientes usucliente=new  Usuarioclientes();
		 usucliente.setNombre(usuarioper.getNombre());
		 usucliente.setApellido(usuarioper.getApellido());
		 usucliente.setCorreo(usuarioper.getCorreo());
		 usucliente.setDireccion(usuarioper.getDireccion());
		 usucliente.setEstado(usuarioper.getEstado());
		 usucliente.setIdentificacion(usuarioper.getIdentificacion());
		 if(usuarioper.getImagenperfil()!=null) {
			 try {
					usucliente.setImagenperfil(usuarioper.getImagenperfil().getBytes());
					
					usucliente.setTipoimagen(usuarioper.getImagenperfil().getContentType()+'/'+usuarioper.getImagenperfil().getOriginalFilename());
					System.out.println("usuariocliente"+usuarioper.getImagenperfil().getName()+'/'+usuarioper.getImagenperfil().getOriginalFilename());
					Optional<Usuario> optiousu= usurepo.findByCodigo(1);
					usucliente.setCodigousuariocreado(optiousu.get());
					Usuarioclientes responu=personacliente.save(usucliente);
					Usuarioclientesusuario newusucliente=new Usuarioclientesusuario();
					Optional<Usuario> optiousuc= usurepo.findByCodigo(usuarioper.getCodigousuario());
					newusucliente.setCodigocliente(responu);
					newusucliente.setCodigousuario(optiousuc.get());
					usuclienteu.save(newusucliente);
					response.put("mensaje",new  MensajeResponsederol("usuarioc cliente creado",true));
					return ResponseEntity.status(200).body(response);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					response.put("mensaje",new  MensajeResponsederol("hubo un error al cargar la imagem",false));
					return ResponseEntity.status(500).body(response);
					
				}

		 }else {
				usucliente.setImagenperfil(null);
				
				usucliente.setTipoimagen("");
			
				Optional<Usuario> optiousu= usurepo.findByCodigo(1);
				usucliente.setCodigousuariocreado(optiousu.get());
				Usuarioclientes responu=personacliente.save(usucliente);
				Usuarioclientesusuario newusucliente=new Usuarioclientesusuario();
				Optional<Usuario> optiousuc= usurepo.findByCodigo(usuarioper.getCodigousuario());
				newusucliente.setCodigocliente(responu);
				newusucliente.setCodigousuario(optiousuc.get());
				usuclienteu.save(newusucliente);
				response.put("mensaje",new  MensajeResponsederol("usuarioc cliente creado",true));
				return ResponseEntity.status(200).body(response);
			 
		 }
		 		}
	 
	 
	 @Transactional
	 @PutMapping(value="/asignarpersona/{idusuario}/{codigoper}")
		public ResponseEntity<	Map<String, Object>> asignarpersona(@PathVariable int idusuario,@PathVariable int codigoper) {
		 Map<String, Object> response = new HashMap<>(); 
		 
		 
		 
		
		 response.clear();
		 Usuarioclientes usucliente=new  Usuarioclientes();
		 
		 Optional<Usuario> usuop=usurepo.findByCodigo(idusuario);
		 Optional<Usuarioclientes> usuopcli=personacliente.findByCodigo(codigoper);
		 
		 List<UsuarioclientesDTOS> usuclientes=usuclienteu.findByUsuariclienteusuario(idusuario, codigoper);
		 if(usuclientes.size()<=0) {
			 Usuarioclientesusuario usucli=new  Usuarioclientesusuario();
			 
			 usucli.setCodigocliente(usuopcli.get());
			 usucli.setCodigousuario(usuop.get());
			 usuclienteu.save(usucli);
			 
			 return ResponseEntity.status(200).body(null);
			 
		 }else {
			 usuclienteu.deleteByCodigo(usuclientes.get(0).getCodigo());
			 return ResponseEntity.status(200).body(null);
		 }
		 
		 	}
	 
	 
	 @Transactional
	 @DeleteMapping("/eliminarper/{codigo}/{codigocliente}")
	 public ResponseEntity<	Map<String, Object>> eliminarpersonas( @PathVariable int codigo,@PathVariable int codigocliente) {
		 Map<String, Object> response = new HashMap<>(); 
		 response.clear();
		 usuclienteu.deleteByCodigo(codigo);
		 
		 
		 personacliente.deleteByCodigo(codigocliente);
		 

		 response.put("mensaje", new MensajeResponsederol("cliente eliminado",true));
		 
		 return ResponseEntity.status(200).body(response);
		 
		 
		 
		 
		 
	 	
	 }
	 
	
	 



  @PutMapping(value="actulizarpersona/{idper}",consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
@Transactional
public ResponseEntity<	Map<String, Object>> crearpermisorol(@PathVariable int idper,@ModelAttribute  crearpersonaDTOS usuarioper) throws IOException {
	  Map<String, Object> response = new HashMap<>(); 
	  response.clear();
	  System.out.println("entro actulizar persona");
try {
	
	 // me devuelve el usuariocliente y en caso contrario una exepcion
	  Usuarioclientes persona = personacliente.findByCodigo(idper)
			  
		        .orElseThrow(() -> new ResponseStatusException(
		                HttpStatus.NOT_FOUND, "Usuario no encontrado"));
	  
	  persona.setApellido(usuarioper.getApellido());
	  persona.setNombre(usuarioper.getNombre());
	  persona.setCorreo(usuarioper.getCorreo());
	  persona.setEstado(usuarioper.getEstado());
	  persona.setDireccion(usuarioper.getDireccion());
	  persona.setIdentificacion(usuarioper.getIdentificacion());
	  if(usuarioper.getImagenperfil()!=null && !usuarioper.getImagenperfil().isEmpty()) {
		  persona.setImagenperfil(usuarioper.getImagenperfil().getBytes());
		  persona.setTipoimagen(usuarioper.getImagenperfil().getContentType()+"/"+usuarioper.getImagenperfil().getOriginalFilename());
	
	  }else {
		  persona.setImagenperfil(null);
		  persona.setTipoimagen("");
		  
	  }
	  
	  personacliente.save(persona);
	  
	  response.put("mesaje", new MensajeResponsederol("Usuarioactulizado", true));
	  
	
  } catch (Exception e) {
	   e.printStackTrace();
	}
	 
return ResponseEntity.ok().body(response);

}
  
  
  @GetMapping("traerusuariobodega")
  public ResponseEntity<	Map<String, Object>> traerusuariobodega(@RequestParam  int usuarioid){
	  Map<String, Object> response = new HashMap<>(); 
	  response.clear();

	  System.out.println( usuarioid+"usuario id");
	  List<UsuariobodegasidDTOS> usuariobo=usuclienteu.findByUsuariobodega(usuarioid);
	  response.put("data", usuariobo);
	  return ResponseEntity.ok().body(response);
	  
	  
	  
	  
	  
  }
  
  
  
  @PutMapping("actualizar/id/{idusuario}")
  public ResponseEntity<Object> restablecercontrasena(@PathVariable Integer idusuario, @RequestBody CrearusuarioDTOS dto){
	  Optional<Usuario> usuarioop=usurepo.findByCodigo(idusuario);
	  if(!usuarioop.isEmpty() && usuarioop.isPresent()) {
		
		  Usuario usuario=usuarioop.get();
		  usuario.setContrasena(PasswordUtils.encrypt(usuario.getContrasena()));
		  
		  usurepo.save(usuario);
		  
	
		  
		  
		  
		  return ResponseEntity.ok(true); // Status 200 con booleano
	    }

	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
  }


  }




