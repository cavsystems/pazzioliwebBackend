package com.pazzioliweb.usuariosbacken.repositorio;

import com.pazzioliweb.usuariosbacken.dtos.BodegaDTO;
import com.pazzioliweb.usuariosbacken.dtos.VendedoresDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.usuariosbacken.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsuario(String usuario);
    Optional <Usuario> findByCodigo(int codigo);
    @Query("SELECT u FROM Usuario u JOIN u.codigorol r WHERE u.codigo = :codigo")
    Optional<Usuario> findByNombreRol(@Param("codigo") int codigo);
    @Query("SELECT new com.pazzioliweb.usuariosbacken.dtos.BodegaDTO(b.codigo) FROM   Usuariobodega us join us.bodegaid b JOIN  us.usuarioid  u  JOIN u.codigorol r WHERE u.codigo = :codigo")
    Optional<BodegaDTO> findBybo(@Param("codigo") int codigo);


    @Query(value = """
select 
u.codigo as clienteId,
cl.codigo as vendedorId,
cl.nombre as cliente
from usuariobodega us
inner join usuarios u on us.usuarioid=u.codigo
inner join bodegas b on b.codigo=us.bodegaid
inner join roles r on r.codigo=u.codigorol
inner join permisos_roles pr on pr.codigorol=r.codigo
inner join permisos prm on prm.codigo=pr.codigopermiso
where u.codigo = :codigo
and prm.nombre = 'Facturacion POS'
and b.codigo= :codigobodega
""", nativeQuery = true)
    List<VendedoresDTO> findBybodegausuario(@Param("codigo") int codigo ,@Param("codigobodega") int codigobodega);
}

