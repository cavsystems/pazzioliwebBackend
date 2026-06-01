package com.pazzioliweb.vendedoresmodule.repositori;

import com.pazzioliweb.vendedoresmodule.entity.Usuariosvendedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioVendedorRepository extends JpaRepository<Usuariosvendedor, Integer> {

    /**
     * Consulta los vendedores relacionados con un usuario específico mediante pageable
     */
    @Query("SELECT uv FROM Usuariosvendedor uv WHERE uv.usuario.codigo = :usuarioCodigo")
    Page<Usuariosvendedor> findByUsuarioCodigo(@Param("usuarioCodigo") Integer usuarioCodigo, Pageable pageable);

    /**
     * Consulta los vendedores relacionados con un usuario específico (sin paginación)
     */
    @Query("SELECT uv FROM Usuariosvendedor uv WHERE uv.usuario.codigo = :usuarioCodigo")
    java.util.List<Usuariosvendedor> findAllByUsuarioCodigo(@Param("usuarioCodigo") Integer usuarioCodigo);

    /**
     * Consulta las relaciones por vendedor ID
     */
    @Query("SELECT uv FROM Usuariosvendedor uv WHERE uv.vendedor.vendedor_id = :vendedorId")
    java.util.List<Usuariosvendedor> findByVendedorId(@Param("vendedorId") Integer vendedorId);

    /**
     * Verifica si un usuario ya está relacionado con algún vendedor
     */
    @Query("SELECT COUNT(uv) > 0 FROM Usuariosvendedor uv WHERE uv.usuario.codigo = :usuarioCodigo")
    boolean existsByUsuarioCodigo(@Param("usuarioCodigo") Integer usuarioCodigo);

    /**
     * Consulta si existe una relación entre un usuario y un vendedor
     */
    @Query("SELECT uv FROM Usuariosvendedor uv WHERE uv.usuario.codigo = :usuarioCodigo AND uv.vendedor.vendedor_id = :vendedorId")
    Optional<Usuariosvendedor> findByUsuarioCodigoAndVendedorVendedorId(@Param("usuarioCodigo") Integer usuarioCodigo, @Param("vendedorId") Integer vendedorId);
}
