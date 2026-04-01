package com.pazzioliweb.authbacken.jwt;


import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.commonbacken.redis.RedisConfig;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRepository;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRolRepository;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;
import com.pazzioliweb.usuriosbacken.entyti.Permiso;
import com.pazzioliweb.usuriosbacken.entyti.PermisoRol;
import com.pazzioliweb.usuriosbacken.entyti.Usuario;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwUtilJava {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	PermisoRolRepository permisoRolRepository;
	
	 private final String SECRET_KEY = "clave_secreta_clave_secreta_clave_secreta"; // 🔐 mínimo 32 bytes para HS256
	 private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	 
	 @Autowired
	    private RedisTemplate<String, DatosSesiones> redisTemplate;
	    public String generateToken(Usuario usuario, String db) {
	    	String sessionId = UUID.randomUUID().toString(); // identificador seguro
	    	  Optional<Usuario> optional = usuarioRepository.findByNombreRol(usuario.getCodigorol().getCodigo());
	    	  if(!optional.isEmpty()) {
	    		  Usuario u=optional.get();
	    		  System.out.println(u.getCodigorol().getNombre());
	    		  List<String> permisosUsuarioActivo=cargarPermisosUsuario(u.getCodigorol().getCodigo());
	    		  if(permisosUsuarioActivo != null && !permisosUsuarioActivo.isEmpty()) {
	    			  System.out.println(permisosUsuarioActivo);  
	    		  }	    		  
	    		  DatosSesiones sesion = new DatosSesiones();
	  	    	sesion.setLogin(usuario.getUsuario());
	  	    	sesion.setDbName(db);
	  	    	sesion.setIdUsuario(sessionId);
	  	    	sesion.setNivel(u.getCodigorol().getNombre());
	  	    	sesion.setCreada(Instant.now());
	  	    	sesion.setExpira(Instant.now().plus(24, ChronoUnit.HOURS));
	  	    	redisTemplate.opsForValue().set(sessionId, sesion, Duration.ofHours(24));
	  	    		        return Jwts.builder()
	  	                .setSubject(usuario.getUsuario()).claim("nivel",u.getCodigorol().getNombre()).claim("idsecion", sessionId)
	  	                .setIssuedAt(new Date())
	  	                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
	  	                .signWith(key, SignatureAlgorithm.HS256)
	  	                .compact();
	    	  }
	    	
	    	  
	    	  return null;
	        
	    }
//conn este metodo extrigo el token para obtener informacion de este
	    public Claims extraerClaims(String token) {
	        return Jwts.parserBuilder()
	                .setSigningKey(key)
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	    }
	    
	    
	    public boolean validarToken(String token) {
	        try {
	            // Si esto no lanza excepción, el token es válido
	            Jwts.parserBuilder()
	                .setSigningKey(key)
	                .build()
	                .parseClaimsJws(token);
	            return true;
	        } catch (Exception e) {
	            // Token inválido o expirado
	            return false;
	        }
	    }
	    
	    public List<String> cargarPermisosUsuario(int codigoRolUsuario){
	    	
	    	List<PermisoRol> permisosUsuario = permisoRolRepository.findPermisosActivosByRol(codigoRolUsuario);
	    	List<String> permisosUsuarioActivo=new ArrayList<String>();
	    	if(!permisosUsuario.isEmpty()) {
  			  for (PermisoRol p : permisosUsuario) {
  				  permisosUsuarioActivo.add(p.getCodigopermiso().getNombre());						
				}
  		  	}
	    	return permisosUsuarioActivo;
	    }

}
