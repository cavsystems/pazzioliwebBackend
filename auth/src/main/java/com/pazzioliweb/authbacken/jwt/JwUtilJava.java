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
import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresasback.repositori.EmpresaRepositori;
import com.pazzioliweb.commonbacken.conexiondb.TenantContext;

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

	@Autowired
	private EmpresaRepositori empresaRepositori;

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
		System.out.println("codigo rol"+usuario.getCodigorol().getCodigo());
		Optional<Usuario> optional = usuarioRepository.findByNombreRol(usuario.getCodigo());
		Optional<BodegaDTO> bodegaid = usuarioRepository.findBybo(usuario.getCodigo());

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

			// Obtener datos de la empresa del tenant actual
			String tenantOriginal = TenantContext.getCurrentTenant();
			try {
				TenantContext.setCurrentTenant(db);
				
				Empresa empresa = empresaRepositori.findAll().stream()
					.findFirst()
					.orElse(null);
				
				if (empresa != null) {
					sesion.setImagenempresa(empresa.getImagenEmpresa());
					sesion.setTipoImagen(empresa.getTipoImagen());
					sesion.setNombreEmpresa(empresa.getRazonsocial());
					sesion.setNumeroIdentificacion(empresa.getNumeroidentificacion());
					sesion.setDigitoVerificacion(empresa.getDigitoverificacion());
					
					// Obtener tipo de identificación como sigla (NIT, CC, etc.)
					if (empresa.getCodigotipoidentificacion() != null) {
						sesion.setTipoIdentificacion(
							abreviarTipoIdentificacion(empresa.getCodigotipoidentificacion().getCodigoTipoIdentificacion())
						);
					}
				}
			} catch (Exception e) {
				System.out.println("[JwUtilJava] Error al obtener datos de empresa: " + e.getMessage());
			} finally {
				TenantContext.setCurrentTenant(tenantOriginal);
			}

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

	/** Mapea el código DIAN de tipo de identificación a su sigla estándar. */
	private String abreviarTipoIdentificacion(int codigoDian) {
		switch (codigoDian) {
			case 11: return "RC";   // Registro Civil de Nacimiento
			case 12: return "TI";   // Tarjeta de Identidad
			case 13: return "CC";   // Cédula de Ciudadanía
			case 21: return "TE";   // Tarjeta de Extranjería
			case 22: return "CE";   // Cédula de Extranjería
			case 31: return "NIT";  // NIT
			case 41: return "PA";   // Pasaporte
			case 42: return "DE";   // Documento de identificación extranjero
			case 43: return "IE";   // Sin identificación del exterior
			default: return String.valueOf(codigoDian);
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