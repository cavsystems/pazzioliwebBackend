package com.pazzioliweb.authbacken.filter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pazzioliweb.authbacken.jwt.JwUtilJava;
import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class Jwtfilter extends OncePerRequestFilter {
	@Autowired
	private RedisTemplate<String, DatosSesiones> redisTemplate;
	@Autowired
	private JwUtilJava jwtUtil;

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Override
	protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {


		// TODO Auto-generated method stub

		String token = null;

		// ✅ Buscar cookie con nombre "token"
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("token".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}

		if (token != null && jwtUtil.validarToken(token)) {
			Claims claims = jwtUtil.extraerClaims(token);
			String nivel = claims.get("nivel", String.class);
			// etc. lógica de autenticación...
			//registro el usuario en el serividor para reconocer un usuario como logueado en springsecurity y en el resto de la plicacion
			Optional<Usuario> optional;
			optional=usuarioRepository.findByUsuario(claims.getSubject());
			Usuario usuario;
			if(optional.isPresent()) {
				usuario=optional.get();
			}else {
				usuario=null;
			}
			String role=nivel;

			List<SimpleGrantedAuthority> authorities = List.of(
					new SimpleGrantedAuthority("ROLE_" + role)
			);
			String db = claims.get("dbname", String.class);
			DatosSesiones datos = redisTemplate.opsForValue().get( claims.get("idsecion",String.class));
			if (datos.getDbName() != null && !datos.getDbName().isEmpty()) {
				TenantContext.setCurrentTenant(datos.getDbName()); // igual que en tu interceptor
			}
			UsernamePasswordAuthenticationToken authToken =
					new UsernamePasswordAuthenticationToken(usuario, claims.get("idsecion",String.class), authorities);
			SecurityContextHolder.getContext().setAuthentication(authToken);


		}else {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Token invalido o ausente\"}");
			return;

		}
		filterChain.doFilter(request, response);


	}


	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		System.out.println("productos"+path.startsWith("/api/productos") +path);
		return path.startsWith("/api/auth") || path.startsWith("/ws/") ||  path.startsWith("/api/empresa") ||  path.startsWith("/api/productos")
				|| path.startsWith("/api/bodegas") || path.startsWith("/api/grupos") || path.startsWith("/api/lineas") || path.startsWith("/api/precios")
				|| path.startsWith("/api/existencias") || path.startsWith("/api/terceros") || path.startsWith("/api/comprobantes") || path.startsWith("/api/cajas")
				|| path.startsWith("/api/vendedores") || path.startsWith("/api/metodos_pago") || path.startsWith("/api/tipo_totales") || path.startsWith("/api/facturas")
				|| path.startsWith("/api/usuario") || path.startsWith("/api/clasificacionesTerceros") || path.startsWith("/api/sedeTercero") || path.startsWith("/api/retencion")
				|| path.startsWith("/api/contactos") || path.startsWith("/api/tipos-caracteristica") || path.startsWith("/api/caracteristicas") || path.startsWith("/api/variantes")
				|| path.startsWith("/api/variante-detalle") || path.startsWith("/api/precios-producto-variante") || path.startsWith("/api/productoMaster")
				|| path.startsWith("/api/unidadesMedida") || path.startsWith("/api/unidadesMedidaProducto") || path.startsWith("/api/tipo-producto")
				|| path.startsWith("/api/cajeros") || path.startsWith("/api/detalle-cajeros")
				|| path.startsWith("/api/comprobantes-egreso") || path.startsWith("/api/recibos-caja")
				|| path.startsWith("/api/cuentas-por-cobrar") || path.startsWith("/api/cuentas-por-pagar");
	}


}