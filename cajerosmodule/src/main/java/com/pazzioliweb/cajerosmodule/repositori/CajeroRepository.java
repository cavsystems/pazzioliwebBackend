package com.pazzioliweb.cajerosmodule.repositori;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.cajerosmodule.dtos.CajeroDTO;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import org.springframework.data.repository.query.Param;

public interface CajeroRepository extends JpaRepository<Cajero, Integer> {

    /**
     * Busca un cajero activo directamente por el código del Usuario (one-to-one)
     */
    Optional<Cajero> findByUsuario_CodigoAndEstado(int codigoUsuario, Cajero.EstadoCajero estado);

    /**
     * Busca el cajero de un usuario (sin filtrar por estado)
     */
    Optional<Cajero> findByUsuario_Codigo(int codigoUsuario);


 /*   @Query("""
    SELECT c FROM Cajero c
    JOIN FETCH c.usuario u
    JOIN FETCH u.codigorol r
    JOIN FETCH r.permisos_roles
    WHERE u.codigo = :codigoUsuario
""")
    Optional<Cajero> findByUsuarioCodigoConPermisos(  @Param("codigoUsuario") int codigoUsuario);*/
    @Query("""
           SELECT 
               c.cajeroId         AS cajeroId,
               c.usuario.codigo   AS usuarioId,
               c.usuario.nombre   AS usuarioNombre,
               c.nombre           AS nombre,
               c.estado           AS estado,
               c.codigoUsuarioCreo AS codigoUsuarioCreo,
               c.fechacreado      AS fechacreado
           FROM Cajero c
           """)
    Page<CajeroDTO> listarCajerosDTO(Pageable pageable);
}
