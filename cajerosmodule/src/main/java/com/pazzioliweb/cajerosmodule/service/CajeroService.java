package com.pazzioliweb.cajerosmodule.service;

import java.time.LocalDate;
import java.util.Optional;

import com.pazzioliweb.cajerosmodule.dtos.ReponsecajeroDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.cajerosmodule.dtos.CajeroDTO;
import com.pazzioliweb.cajerosmodule.dtos.CajeroRequestDTO;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.repositori.CajeroRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;

@Service
public class CajeroService {

    private final CajeroRepository cajeroRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CajeroService(CajeroRepository cajeroRepository,
                         UsuarioRepository usuarioRepository) {
        this.cajeroRepository = cajeroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ─────────────────────────────────────────────
    // LISTADO
    // ─────────────────────────────────────────────

    public Page<CajeroDTO> listar(int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return cajeroRepository.listarCajerosDTO(pageable);
    }

    public Optional<Cajero> buscarPorId(Integer id) {
        return cajeroRepository.findById(id);
    }

    /**
     * Busca el cajero de un usuario por su código de usuario.
     */
    public Optional<ReponsecajeroDTO> buscarPorUsuario(int usuarioId) {
        return cajeroRepository.findByUsuario_Codigo(usuarioId)
                .map(c -> new ReponsecajeroDTO(
                        c.getCajeroId(),
                        c.getNombre()
                ));
    }


    // ─────────────────────────────────────────────
    // ASOCIACIÓN — crear cajero para un usuario
    // ─────────────────────────────────────────────

    /**
     * Asocia un usuario como cajero.
     * Valida que:
     *  - El usuario exista en la BD
     *  - El usuario no tenga ya un cajero asignado (one-to-one)
     *
     * @throws RuntimeException si el usuario no existe o ya tiene cajero
     */
    @Transactional
    public Cajero asociar(CajeroRequestDTO dto) {
        // 1. Validar que el usuario exista
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado con id: " + dto.getUsuarioId()));

        // 2. Validar que no tenga ya un cajero (one-to-one)
        cajeroRepository.findByUsuario_Codigo(dto.getUsuarioId())
                .ifPresent(existente -> {
                    throw new RuntimeException(
                            "El usuario '" + usuario.getUsuario() + "' ya tiene un cajero asignado " +
                                    "(cajeroId: " + existente.getCajeroId() + ")");
                });

        // 3. Crear el cajero
        Cajero cajero = new Cajero();
        cajero.setUsuario(usuario);
        cajero.setNombre(dto.getNombre() != null ? dto.getNombre() : usuario.getNombre());
        cajero.setEstado(dto.getEstado() != null && dto.getEstado().equalsIgnoreCase("INACTIVO")
                ? Cajero.EstadoCajero.INACTIVO
                : Cajero.EstadoCajero.ACTIVO);
        cajero.setCodigoUsuarioCreo(dto.getCodigoUsuarioCreo() != null ? dto.getCodigoUsuarioCreo() : 0);
        cajero.setFechacreado(LocalDate.now());

        return cajeroRepository.save(cajero);
    }

    // ─────────────────────────────────────────────
    // CAMBIO DE ESTADO
    // ─────────────────────────────────────────────

    /**
     * Activa o desactiva un cajero.
     */
    @Transactional
    public Cajero cambiarEstado(Integer cajeroId, String nuevoEstado) {
        Cajero cajero = cajeroRepository.findById(cajeroId)
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado: " + cajeroId));

        cajero.setEstado(nuevoEstado.equalsIgnoreCase("INACTIVO")
                ? Cajero.EstadoCajero.INACTIVO
                : Cajero.EstadoCajero.ACTIVO);

        return cajeroRepository.save(cajero);
    }

    // ─────────────────────────────────────────────
    // CRUD BÁSICO
    // ─────────────────────────────────────────────

    public Cajero guardar(Cajero cajero) {
        return cajeroRepository.save(cajero);
    }

    public void eliminar(Integer id) {
        cajeroRepository.deleteById(id);
    }
}
