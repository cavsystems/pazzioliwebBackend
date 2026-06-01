package com.pazzioliweb.authbacken.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
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
import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.commonbacken.entity.Sesiones;
import com.pazzioliweb.commonbacken.repositorio.SessionRepository;
import com.pazzioliweb.commonbacken.util.PasswordUtils;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;
import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresasback.entity.Estado;
import com.pazzioliweb.empresasback.repositori.EmpresaRepositori;

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
    
    // ✅ Inyección para validación de empresa
    @Autowired
    private EmpresaRepositori empresaRepositori;

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
                System.out.println("Usuario encontrado: " + usuario.getUsuario());

                boolean contrasenaValida = verificarContrasena(request.password, usuario.getContrasena());
                
                if (contrasenaValida) {
                    // ✅ Validar estado de la empresa y licencia
                    if(!request.db.equals("cavsystems")){
                        Map<String, Object> validacionEmpresa = validarEmpresa(request.db);
                        if (!(boolean) validacionEmpresa.get("valido")) {
                            response.put("success", false);
                            response.put("message", validacionEmpresa.get("mensaje"));
                            response.put("Activa", false);
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        }
                    }
                    Optional<Sesiones> optionalsession = sessionRepository.findFirstBycodigoUsuarioAndEstadoOrderByCodigoDesc(usuario.getCodigo(), "ACTIVO");

                    if (optionalsession.isPresent()) {
                        Sesiones sesion = optionalsession.get();
                        sesion.setEstado("INACTIVO");
                        sessionRepository.save(sesion);
                    }

                    Optional<Sesiones> sesioncodigo = sessionRepository.findTopByOrderByCodigoDesc();
                    long siguienteCodigo = sesioncodigo.isPresent() ? sesioncodigo.get().getCodigo() + 1 : 1;
                    String token = jwtUtil.generateToken(usuario, request.db.trim(), siguienteCodigo);
                    crearSesion(usuario.getCodigo(), token);
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
            e.printStackTrace();
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Error interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
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
            System.out.println("Usuario encontrado: " + usuario.getUsuario());

            boolean contrasenaValida = verificarContrasena(request.password, usuario.getContrasena());
            
            if (contrasenaValida) {
                // ✅ Validar estado de la empresa y licencia
                System.out.println("db actual es esta"+request.db+request.usuario);
                if(!request.db.equals("cavsystems")){
                    Map<String, Object> validacionEmpresa = validarEmpresa(request.db);
                    if (!(boolean) validacionEmpresa.get("valido")) {
                        response.put("success", false);
                        response.put("message", validacionEmpresa.get("mensaje"));
                        response.put("Activa", false);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                }

                Optional<Sesiones> optionalsession = sessionRepository.findFirstBycodigoUsuarioAndEstadoOrderByCodigoDesc(usuario.getCodigo(), "ACTIVO");

                if (optionalsession.isPresent()) {
                    Sesiones sesion = optionalsession.get();
                    boolean sesionViva = false;
                    try {
                        if (sesion.getToken() != null && jwtUtil.validarToken(sesion.getToken())) {
                            String idsecionPrev = jwtUtil.extraerClaims(sesion.getToken()).get("idsecion", String.class);
                            sesionViva = idsecionPrev != null && redisTemplate.hasKey(idsecionPrev);
                        }
                    } catch (Exception ignored) {
                        sesionViva = false;
                    }

                    if (sesionViva) {
                        System.out.println("sesion activa");
                        response.put("success", false);
                        response.put("Mensaje", "Hay una sesión activa desea cerrarla");
                        response.put("Activa", true);
                        return ResponseEntity.ok().body(response);
                    }

                    // Sesión huérfana en BD (token expirado o Redis sin la clave): la cerramos silenciosamente.
                    sesion.setEstado("INACTIVO");
                    sessionRepository.save(sesion);
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
        if (request.getCookies() == null) {
            return ResponseEntity.ok(false);
        }
        String token = null;
        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }
        if (token == null) {
            return ResponseEntity.ok(false);
        }
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.ok(false);
        }
        try {
            Claims claims = jwtUtil.extraerClaims(token);
            String sessionId = claims.get("idsecion", String.class);

            DatosSesiones datos = redisTemplate.opsForValue().get(sessionId);

            // Fallback a MySQL cuando Redis perdió la sesión (restart, eviction, etc.)
            if (datos == null) {
                String dbNameFromJwt = claims.get("dbname", String.class);
                try {
                    if (dbNameFromJwt != null && !dbNameFromJwt.isEmpty()) {
                        TenantContext.setCurrentTenant(dbNameFromJwt);
                    }
                    Optional<Sesiones> sesionDB =
                            sessionRepository.findFirstByTokenAndEstado(token, "ACTIVO");
                    if (sesionDB.isEmpty()) {
                        System.out.println("[verificar] Sesión no encontrada en Redis ni en DB para sessionId=" + sessionId);
                        return ResponseEntity.ok(false);
                    }
                    System.out.println("[verificar] Sesión recuperada desde DB (Redis caído). sessionId=" + sessionId);
                    return ResponseEntity.ok(true);
                } finally {
                    TenantContext.clear();
                }
            }

            String dbName = datos.getDbName();
            if (dbName != null && !dbName.equals("cavsystems")) {
                Map<String, Object> validacionEmpresa = validarEmpresa(dbName);
                if (!(boolean) validacionEmpresa.get("valido")) {
                    return ResponseEntity.ok(false);
                }
            }
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            System.out.println("[verificar] Error: " + e.getMessage());
            return ResponseEntity.ok(false);
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

    /**
     * Valida el estado de la empresa y la vigencia de la licencia
     */
    private Map<String, Object> validarEmpresa(String dbName) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("valido", true);
        resultado.put("mensaje", "");
        
        try {
            // Obtener el primer registro de empresa del esquema actual (cada esquema tiene solo un registro)
            Optional<Empresa> empresaOpt = empresaRepositori.findAll().stream().findFirst();
            
            if (empresaOpt.isEmpty()) {
                resultado.put("valido", false);
                resultado.put("mensaje", "Empresa no encontrada");
                return resultado;
            }
            
            Empresa empresa = empresaOpt.get();
            
            // Validar estado de la empresa
            if (empresa.getEstado() != Estado.ACTIVA) {
                resultado.put("valido", false);
                resultado.put("mensaje", "Empresa actualmente no activa");
                return resultado;
            }
            
            // Validar vigencia de la licencia (considerando el plazo)
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaFinalLicencia = empresa.getFechafinallicencia();
            int plazo = empresa.getPlazo();
            
            if (fechaFinalLicencia != null) {
                // Calcular fecha de vencimiento considerando el plazo (en días)
                LocalDate fechaVencimientoConPlazo = fechaFinalLicencia.plusDays(plazo);
                
                if (fechaActual.isAfter(fechaVencimientoConPlazo)) {
                    resultado.put("valido", false);
                    resultado.put("mensaje", "Licencia vencida");
                    return resultado;
                }
            }
            
        } catch (Exception e) {
            resultado.put("valido", false);
            resultado.put("mensaje", "Error al validar empresa: " + e.getMessage());
        }
        
        return resultado;
    }

    /**
     * Verifica la contraseña del usuario manejando ambos casos:
     * - Contraseñas encriptadas (nuevos usuarios)
     * - Contraseñas en texto plano (usuarios existentes)
     */
    private boolean verificarContrasena(String passwordIngresada, String passwordGuardada) {
        // Primero intentar con contraseña encriptada (nuevos usuarios)
        if (PasswordUtils.matches(passwordIngresada, passwordGuardada)) {
            return true;
        }
        
        // Si no funciona, intentar con texto plano (usuarios existentes)
        if (passwordIngresada.equals(passwordGuardada)) {
            System.out.println("⚠️ Usuario con contraseña en texto plano detectado - se recomienda migrar a contraseña encriptada");
            return true;
        }
        
        return false;
    }

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
