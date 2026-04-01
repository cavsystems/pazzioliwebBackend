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

import com.pazzioliweb.usuariosbacken.dtos.BodegaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.usuariosbacken.entity.PermisoRol;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRolRepository;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwUtilJava {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PermisoRolRepository permisoRolRepository;

	private final String SECRET_KEY = "clave_secreta_clave_secreta_clave_secreta"; // 🔐 mínimo 32 bytes para HS256
	private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

	@Autowired
	private RedisTemplate<String, DatosSesiones> redisTemplate;

	/**
	 * Genera el JWT y crea la sesión base en Redis.
	 * La lógica de cajero (cajeroId, detalleCajeroId) la maneja AuthController
	 * después del login mediante verificarYAbrirSesionCajero().
	 */
	public String generateToken(Usuario usuario, String db, Long numerosession) {
		int boid = 0;
		String sessionId = UUID.randomUUID().toString();
		Optional<Usuario> optional = usuarioRepository.findByNombreRol(usuario.getCodigorol().getCodigo());
		Optional<BodegaDTO> bodegaid = usuarioRepository.findBybo(usuario.getCodigorol().getCodigo());
		if (bodegaid.isPresent()) {
			boid = bodegaid.get().bodegaid();
		}
		if (optional.isPresent()) {
			Usuario u = optional.get();
			System.out.println(u.getCodigorol().getNombre());
			List<String> permisosUsuarioActivo = cargarPermisosUsuario(u.getCodigorol().getCodigo());
			System.out.println("permisos actuales" + permisosUsuarioActivo);

			DatosSesiones sesion = new DatosSesiones();
			sesion.setLogin(usuario.getUsuario());
			sesion.setIdusuario(usuario.getCodigo());
			sesion.setDbName(db);
			sesion.setBodegaid(boid);
			sesion.setIdUsuario(sessionId);
			sesion.setNivel(u.getCodigorol().getNombre());
			sesion.setPermisos(permisosUsuarioActivo);
			sesion.setCreada(Instant.now());
			sesion.setExpira(Instant.now().plus(24, ChronoUnit.HOURS));
			// cajeroId y detalleCajeroId se establecen en AuthController.verificarYAbrirSesionCajero()

			redisTemplate.opsForValue().set(sessionId, sesion, Duration.ofHours(24));

			return Jwts.builder()
					.setSubject(usuario.getUsuario())
					.claim("nivel", u.getCodigorol().getNombre())
					.claim("idsecion", sessionId)
					.claim("numerosesion", numerosession)
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
					.signWith(key, SignatureAlgorithm.HS256)
					.compact();
		}

		return null;
	}

	public Claims extraerClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public boolean validarToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<String> cargarPermisosUsuario(int codigoRolUsuario) {
		List<PermisoRol> permisosUsuario = permisoRolRepository.findPermisosActivosByRol(codigoRolUsuario);
		List<String> permisosUsuarioActivo = new ArrayList<>();
		if (!permisosUsuario.isEmpty()) {
			for (PermisoRol p : permisosUsuario) {
				permisosUsuarioActivo.add(p.getCodigopermiso().getNombre());
			}
		}
		return permisosUsuarioActivo;
	}

}