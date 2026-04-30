package com.pazzioliweb.authbacken.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import com.pazzioliweb.authbacken.dtos.LoginRequest;
import com.pazzioliweb.authbacken.jwt.JwUtilJava;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.entity.DetalleCajero;
import com.pazzioliweb.cajerosmodule.repositori.CajeroRepository;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.conexiondb.ConexionFactory;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.commonbacken.entity.Sesiones;
import com.pazzioliweb.commonbacken.repositorio.SessionRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    private final JwUtilJava jwtUtil;
    @Autowired
    private ConexionFactory conexion;
    @Autowired
    private RedisTemplate<String, DatosSesiones> redisTemplate;
    private final HttpServletResponse servletResponse;

    // ✅ Inyecciones para la lógica de cajeros
    @Autowired
    private CajeroRepository cajeroRepository;
    @Autowired
    private DetalleCajeroService detalleCajeroService;

    @Autowired
    public AuthController(UsuarioRepository usuarioRepository, SessionRepository sessionRepositorio, JwUtilJava jwtUtil, HttpServletResponse servletResponse) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.servletResponse = servletResponse;
        this.sessionRepository = sessionRepositorio;
    }



    // ──────────────────────────────────────────────────────────────────────────
    // /login/destroit  →  cierra la sesión activa y crea una nueva (force login)
    // ──────────────────────────────────────────────────────────────────────────
    @PostMapping("/login/destroit")
    public ResponseEntity<Map<String, Object>> logindestroit(@RequestBody LoginRequest request) {
        try {
            Optional<Usuario> optional = usuarioRepository.findByUsuario(request.usuario.trim());
            Map<String, Object> response = new HashMap<>();
            response.clear();
            System.out.println(optional.isPresent());
            if (optional.isPresent()) {

                Usuario usuario = optional.get();
                System.out.println(usuario.getUsuario());
                if (usuario.getContrasena().equals(request.password)) {
                    Optional<Sesiones> optionalsession = sessionRepository.findFirstBycodigoUsuarioAndEstadoOrderByCodigoDesc(usuario.getCodigo(), "ACTIVO");

                    Sesiones sesion = optionalsession.get();
                    sesion.setEstado("INACTIVO");
                    sessionRepository.save(sesion);

                    Optional<Sesiones> sesioncodigo = sessionRepository.findTopByOrderByCodigoDesc();
                    String token = jwtUtil.generateToken(usuario, request.db.trim(), sesioncodigo.get().getCodigo() + 1);
                    crearSesion(usuario.getCodigo(), token);
                    System.out.println("TOKEN => [" + token + "]");
                    Claims claims = jwtUtil.extraerClaims(token);
                    System.out.println("token creado obtener claims2");
                    DatosSesiones datos = redisTemplate.opsForValue().get(claims.get("idsecion", String.class));
                    System.out.println("codigo usuario" + usuario.getCodigo());

                    // ✅ Verificar si el usuario es cajero y abrir sesión de caja
                    verificarYAbrirSesionCajero(usuario, datos, claims);

                    Cookie jwtCookie = new Cookie("token", token);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setSecure(false);
                    jwtCookie.setPath("/");
                    jwtCookie.setMaxAge(24 * 60 * 60);
                    jwtCookie.setDomain("localhost");
                    servletResponse.addCookie(jwtCookie);
                    response.put("success", true);
                    response.put("sesion", datos);
                    response.put("permisos", datos.getPermisos());

                    return ResponseEntity.ok().body(response);
                } else {
                    response.put("success", false);
                    response.put("message", "Credenciales inválidas");
                    response.put("Activa", false);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

            } else {
                response.put("success", false);
                response.put("message", "Credenciales inválidas");
                response.put("Activa", false);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

return  null;
    }


    // ──────────────────────────────────────────────────────────────────────────
    // /login  →  login normal; avisa si ya hay sesión activa
    // ──────────────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {

        Optional<Usuario> optional = usuarioRepository.findByUsuario(request.usuario.trim());
        Map<String, Object> response = new HashMap<>();
        response.clear();
        System.out.println(optional.isPresent());
        if (optional.isPresent()) {

            Usuario usuario = optional.get();
            System.out.println(usuario.getUsuario());
            if (usuario.getContrasena().equals(request.password)) {
                Optional<Sesiones> optionalsession = sessionRepository.findFirstBycodigoUsuarioAndEstadoOrderByCodigoDesc(usuario.getCodigo(), "ACTIVO");

                if (optionalsession.isPresent()) {
                    System.out.println("sesion activa");
                    Sesiones sesion = optionalsession.get();
                    LocalDateTime fechaFin = sesion.getFechainicio();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    System.out.println("Fecha y hora de fin: " + fechaFin.format(fmt));

                    response.put("success", false);
                    response.put("Mensaje", "Hay una sesión activa desea serrala");
                    response.put("Activa", true);

                    return ResponseEntity.ok().body(response);
                }

                Optional<Sesiones> sesioncodigo = sessionRepository.findTopByOrderByCodigoDesc();
                String token = jwtUtil.generateToken(usuario, request.db.trim(), sesioncodigo.isPresent() ? sesioncodigo.get().getCodigo() + 1:1);
                crearSesion(usuario.getCodigo(), token);
                Claims claims = jwtUtil.extraerClaims(token);
                DatosSesiones datos = redisTemplate.opsForValue().get(claims.get("idsecion", String.class));
                System.out.println("codigo usuario" + usuario.getCodigo());

                // ✅ Verificar si el usuario es cajero y abrir sesión de caja
                verificarYAbrirSesionCajero(usuario, datos, claims);

                Cookie jwtCookie = new Cookie("token", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(false);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(24 * 60 * 60);
                jwtCookie.setDomain("localhost");
                servletResponse.addCookie(jwtCookie);
                response.put("success", true);
                response.put("sesion", datos);
                response.put("permisos", datos.getPermisos());

                return ResponseEntity.ok().body(response);
            } else {
                response.put("success", false);
                response.put("message", "Credenciales inválidas");
                response.put("Activa", false);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } else {
            response.put("success", false);
            response.put("message", "Credenciales inválidas");
            response.put("Activa", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Lógica de cajeros
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Verifica si el usuario tiene el permiso "VENTA" (sin importar su rol).
     * Esto permite que tanto un cajero como un administrador con permiso de venta
     * abran automáticamente una sesión de caja al loguease, siempre que tengan
     * un Cajero activo asignado (one-to-one directo por usuario_id).
     */
    private void verificarYAbrirSesionCajero(Usuario usuario, DatosSesiones datos, Claims claims) {
        try {
            // ✅ Verificar por PERMISO de venta, no por rol
            // Así un admin, supervisor o cualquier rol con permiso VENTA también abre caja
            boolean tienePermisoVenta = datos.getPermisos() != null
                    && datos.getPermisos().stream()
                    .anyMatch(p -> p.equalsIgnoreCase("VENTA"));

            if (!tienePermisoVenta) {
                return; // Usuario sin permiso de venta → no necesita sesión de caja
            }

            // ✅ Búsqueda directa: Usuario → Cajero (one-to-one por usuario_id)
            cajeroRepository.findByUsuario_CodigoAndEstado(usuario.getCodigo(), Cajero.EstadoCajero.ACTIVO)
                    .ifPresent(cajero -> {
                        // Abre sesión de caja o retorna la existente si ya hay una abierta
                        DetalleCajero detalle = detalleCajeroService.abrirSesionCajero(cajero, BigDecimal.ZERO);

                        datos.setCajeroId(cajero.getCajeroId());
                        datos.setDetalleCajeroId(detalle.getDetalleCajeroId());

                        // Actualizar Redis con los datos del cajero
                        String sessionId = claims.get("idsecion", String.class);
                        redisTemplate.opsForValue().set(sessionId, datos);

                        System.out.println("✅ Sesión de cajero abierta — usuario: " + usuario.getUsuario()
                                + " cajeroId: " + cajero.getCajeroId()
                                + " detalleCajeroId: " + detalle.getDetalleCajeroId());
                    });

        } catch (Exception e) {
            System.out.println("⚠️ Error al verificar sesión de cajero: " + e.getMessage());
        }
    }

    //__________________________________________________________________________________
    //verificar seccion activa
    //________________________________________________________________________________
    @GetMapping("verificar")
    public ResponseEntity<Boolean> Verificar(HttpServletRequest request){
        if (request.getCookies() != null) {
            String token=null;
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;

                }

            }

            if(token == null){
                return  ResponseEntity.ok(false);
            }else {
              return   ResponseEntity.ok(jwtUtil.validarToken(token));

            }
        }else {
            return  ResponseEntity.ok(false);
        }

    }
    // ──────────────────────────────────────────────────────────────────────────
    // /logout  →  cierra sesión de caja (si es cajero) y elimina sesión Redis
    // ──────────────────────────────────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() != null) {
                String sessionId = auth.getCredentials().toString();
                DatosSesiones datos = redisTemplate.opsForValue().get(sessionId);

                if (datos != null) {
                    // Si es cajero, cerrar la sesión de caja automáticamente (sin cuadre)
                    if (datos.getCajeroId() != null) {
                        detalleCajeroService.cerrarSesionCajeroSimple(datos.getCajeroId());
                        System.out.println("✅ Sesión de cajero cerrada automáticamente — cajeroId: " + datos.getCajeroId());
                    }

                    // Cerrar sesión en BD
                    Usuario usuario = (Usuario) auth.getPrincipal();
                    Optional<Sesiones> sesionOpt = sessionRepository
                            .findFirstBycodigoUsuarioAndEstadoOrderByCodigoDesc(usuario.getCodigo(), "ACTIVO");
                    if (sesionOpt.isPresent()) {
                        Sesiones sesion = sesionOpt.get();
                        sesion.setEstado("CERRADO");
                        ZoneId zonaBogota = ZoneId.of("America/Bogota");
                        sesion.setFechafin(LocalDateTime.now(zonaBogota));
                        sessionRepository.save(sesion);
                    }

                    // Eliminar sesión de Redis
                    redisTemplate.delete(sessionId);

                    // Eliminar cookie
                    Cookie jwtCookie = new Cookie("token", null);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setSecure(false);
                    jwtCookie.setPath("/");
                    jwtCookie.setMaxAge(0);
                    jwtCookie.setDomain("localhost");
                    servletResponse.addCookie(jwtCookie);
                }

                SecurityContextHolder.clearContext();
                response.put("success", true);
                response.put("message", "Sesión cerrada correctamente");
                return ResponseEntity.ok(response);
            }

            response.put("success", false);
            response.put("message", "No hay sesión activa");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cerrar sesión: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Utilidades
    // ──────────────────────────────────────────────────────────────────────────

    public void essuperadministrador(int id) {
    }

    public Long crearSesion(int codigo, String token) {
        String fechaStr = "1990-01-01 00:00:00";
        ZoneId zonaBogota = ZoneId.of("America/Bogota");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Sesiones sesion = new Sesiones();
        sesion.setCodigoUsuario(codigo);
        sesion.setToken(token);
        sesion.setEstado("ACTIVO");
        System.out.println(LocalDateTime.now(zonaBogota));
        sesion.setFechainicio(LocalDateTime.now(zonaBogota));
        sesion.setFechafin(LocalDateTime.parse(fechaStr, fmt));
        Sesiones datosecion = sessionRepository.save(sesion);
        return datosecion.getCodigo();
    }

    public DatosSesiones obtenerseion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object credenciales = auth.getCredentials();
        DatosSesiones sesiones = redisTemplate.opsForValue().get(credenciales.toString());
        return sesiones;
    }
}
