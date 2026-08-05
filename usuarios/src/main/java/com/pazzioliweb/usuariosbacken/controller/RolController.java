package com.pazzioliweb.usuariosbacken.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pazzioliweb.usuariosbacken.dtos.*;
import com.pazzioliweb.usuariosbacken.entity.Permiso;
import com.pazzioliweb.usuariosbacken.entity.PermisoRol;
import com.pazzioliweb.usuariosbacken.entity.Roles;
import com.pazzioliweb.usuariosbacken.entity.SubPermiso;
import com.pazzioliweb.usuariosbacken.entity.SubPermisoRol;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRepository;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRolRepository;
import com.pazzioliweb.usuariosbacken.repositorio.RolesRepository;
import com.pazzioliweb.usuariosbacken.repositorio.SubPermisoRepository;
import com.pazzioliweb.usuariosbacken.repositorio.SubPermisoRolRepository;

import jakarta.transaction.Transactional;

// Módulo de administración de Roles, separado del CRUD de Usuario
// (Usuariocontroller). Antes "crear rol" + "asignar permisos" vivía embebido
// en el wizard de creación de usuario (paso "Asignar Roles"); ahora ese paso
// solo elige un rol ya existente (select), y la administración completa del
// rol (crearlo, marcar sus permisos y subpermisos) vive acá, en su propia
// página. También es el primer endpoint que permite asignar/quitar
// SUBpermisos por rol — antes solo existía la lectura (usada en el login).
@RestController
@RequestMapping("api/roles")
public class RolController {

    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private PermisoRepository permisoRepository;
    @Autowired
    private PermisoRolRepository permisoRolRepository;
    @Autowired
    private SubPermisoRepository subPermisoRepository;
    @Autowired
    private SubPermisoRolRepository subPermisoRolRepository;

    public record MensajeDTO(String mensaje, boolean estado) {}

    // ── Roles ──────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<RolesDTOS>> listarRoles() {
        return ResponseEntity.ok(rolesRepository.findByNombreNot("superusuario"));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Object> crearRol(@RequestBody RolCreateDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensajeDTO("El nombre del rol es obligatorio", false));
        }
        if (rolesRepository.findByNombreIgnoreCase(dto.getNombre().trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeDTO("Ya existe un rol con ese nombre", false));
        }
        Roles rol = new Roles();
        rol.setNombre(dto.getNombre().trim());
        rol = rolesRepository.save(rol);
        // Se devuelve un DTO plano, no la entidad: Roles tiene colecciones LAZY
        // (permisos_roles, usuarios) que con spring.jpa.open-in-view=false
        // revientan con LazyInitializationException al serializar la respuesta,
        // porque para entonces la sesión de Hibernate ya se cerró.
        return ResponseEntity.status(HttpStatus.CREATED).body(RolDTO.fromEntity(rol));
    }

    // ── Catálogos ──────────────────────────────────────────────────────────

    @GetMapping("/permisos")
    public ResponseEntity<List<PermisosDTOS>> listarPermisos() {
        return ResponseEntity.ok(permisoRepository.findAllPermisoBy());
    }

    @GetMapping("/subpermisos")
    public ResponseEntity<List<SubPermisoDTO>> listarSubpermisos() {
        List<SubPermisoDTO> lista = subPermisoRepository.findActivosConPadre().stream()
                .map(sp -> new SubPermisoDTO(
                        sp.getCodigo(), sp.getNombre(), sp.getCodigoAccion(),
                        sp.getPermisoPadre().getCodigo(), sp.getPermisoPadre().getNombre()))
                .toList();
        return ResponseEntity.ok(lista);
    }

    // ── Permisos asignados a un rol ───────────────────────────────────────

    @GetMapping("/{idRol}/permisos")
    public ResponseEntity<List<PermisosrolesDTOS>> listarPermisosDeRol(@PathVariable int idRol) {
        List<PermisosrolesDTOS> permisos = permisoRolRepository.findPermisosActivosByRol(idRol).stream()
                .map(p -> new PermisosrolesDTOS(
                        p.getCodigo(),
                        p.getCodigopermiso().getCodigo(),
                        p.getCodigopermiso().getNombre(),
                        p.getCodigorol().getNombre()))
                .toList();
        return ResponseEntity.ok(permisos);
    }

    @Transactional
    @PutMapping("/{idRol}/permisos/{idPermiso}")
    public ResponseEntity<Object> asignarPermiso(@PathVariable int idRol, @PathVariable int idPermiso) {
        Optional<Roles> rol = rolesRepository.findByCodigo(idRol);
        Optional<Permiso> permiso = permisoRepository.findByCodigo(idPermiso);
        if (rol.isEmpty() || permiso.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeDTO("Rol o permiso no encontrado", false));
        }
        boolean yaAsignado = permisoRolRepository.findPermisosActivosByRol(idRol).stream()
                .anyMatch(p -> p.getCodigopermiso().getCodigo() == idPermiso);
        if (!yaAsignado) {
            PermisoRol pr = new PermisoRol();
            pr.setCodigorol(rol.get());
            pr.setCodigopermiso(permiso.get());
            pr.setEstado("ACTIVO");
            permisoRolRepository.save(pr);
        }
        return ResponseEntity.ok(new MensajeDTO("Permiso asignado", true));
    }

    @Transactional
    @DeleteMapping("/{idRol}/permisos/{idPermiso}")
    public ResponseEntity<Object> quitarPermiso(@PathVariable int idRol, @PathVariable int idPermiso) {
        permisoRolRepository.deleteByCodigorol_CodigoAndCodigopermiso_Codigo(idRol, idPermiso);
        return ResponseEntity.ok(new MensajeDTO("Permiso quitado", true));
    }

    // Asigna el permiso Y TODOS sus subpermisos activos de un solo golpe (lo que
    // dispara la UI al tildar el checkbox del permiso padre). Cada relación que ya
    // existía se deja tal cual — no se duplica ni se pisa.
    @Transactional
    @PutMapping("/{idRol}/permisos/{idPermiso}/con-subpermisos")
    public ResponseEntity<Object> asignarPermisoConSubpermisos(@PathVariable int idRol, @PathVariable int idPermiso) {
        Optional<Roles> rol = rolesRepository.findByCodigo(idRol);
        Optional<Permiso> permiso = permisoRepository.findByCodigo(idPermiso);
        if (rol.isEmpty() || permiso.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeDTO("Rol o permiso no encontrado", false));
        }

        boolean permisoYaAsignado = permisoRolRepository.findPermisosActivosByRol(idRol).stream()
                .anyMatch(p -> p.getCodigopermiso().getCodigo() == idPermiso);
        if (!permisoYaAsignado) {
            PermisoRol pr = new PermisoRol();
            pr.setCodigorol(rol.get());
            pr.setCodigopermiso(permiso.get());
            pr.setEstado("ACTIVO");
            permisoRolRepository.save(pr);
        }

        List<SubPermiso> subpermisosDelPermiso = subPermisoRepository.findByPermisoPadre_CodigoAndActivoTrue(idPermiso);
        if (!subpermisosDelPermiso.isEmpty()) {
            Set<Integer> yaAsignados = subPermisoRolRepository.findSubPermisosActivosByRol(idRol).stream()
                    .map(spr -> spr.getCodigosubpermiso().getCodigo())
                    .collect(Collectors.toSet());
            for (SubPermiso sp : subpermisosDelPermiso) {
                if (yaAsignados.contains(sp.getCodigo())) continue;
                SubPermisoRol spr = new SubPermisoRol();
                spr.setCodigorol(rol.get());
                spr.setCodigosubpermiso(sp);
                spr.setEstado("ACTIVO");
                subPermisoRolRepository.save(spr);
            }
        }

        return ResponseEntity.ok(new MensajeDTO("Permiso y subpermisos asignados", true));
    }

    // ── Subpermisos asignados a un rol ────────────────────────────────────

    @GetMapping("/{idRol}/subpermisos")
    public ResponseEntity<List<SubPermisoRolDTO>> listarSubpermisosDeRol(@PathVariable int idRol) {
        List<SubPermisoRolDTO> lista = subPermisoRolRepository.findSubPermisosActivosByRol(idRol).stream()
                .map(spr -> new SubPermisoRolDTO(
                        spr.getCodigo(),
                        spr.getCodigosubpermiso().getCodigo(),
                        spr.getCodigosubpermiso().getNombre(),
                        spr.getCodigosubpermiso().getPermisoPadre().getCodigo(),
                        spr.getCodigosubpermiso().getPermisoPadre().getNombre()))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @Transactional
    @PutMapping("/{idRol}/subpermisos/{idSubpermiso}")
    public ResponseEntity<Object> asignarSubpermiso(@PathVariable int idRol, @PathVariable int idSubpermiso) {
        Optional<Roles> rol = rolesRepository.findByCodigo(idRol);
        Optional<SubPermiso> subpermiso = subPermisoRepository.findByCodigo(idSubpermiso);
        if (rol.isEmpty() || subpermiso.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeDTO("Rol o subpermiso no encontrado", false));
        }
        boolean yaAsignado = subPermisoRolRepository.findSubPermisosActivosByRol(idRol).stream()
                .anyMatch(spr -> spr.getCodigosubpermiso().getCodigo() == idSubpermiso);
        if (!yaAsignado) {
            SubPermisoRol spr = new SubPermisoRol();
            spr.setCodigorol(rol.get());
            spr.setCodigosubpermiso(subpermiso.get());
            spr.setEstado("ACTIVO");
            subPermisoRolRepository.save(spr);
        }
        return ResponseEntity.ok(new MensajeDTO("Subpermiso asignado", true));
    }

    @Transactional
    @DeleteMapping("/{idRol}/subpermisos/{idSubpermiso}")
    public ResponseEntity<Object> quitarSubpermiso(@PathVariable int idRol, @PathVariable int idSubpermiso) {
        subPermisoRolRepository.deleteByCodigorol_CodigoAndCodigosubpermiso_Codigo(idRol, idSubpermiso);
        return ResponseEntity.ok(new MensajeDTO("Subpermiso quitado", true));
    }
}
