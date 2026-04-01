package com.pazzioliweb.usuariosbacken.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.usuariosbacken.dtos.UsuariobodegasidDTOS;
import com.pazzioliweb.usuariosbacken.dtos.UsuarioclientesDTOS;
import com.pazzioliweb.usuariosbacken.entity.Tipopersona;
import com.pazzioliweb.usuariosbacken.entity.Usuarioclientesusuario;

public interface UsuarioclientesusuarioRepository extends JpaRepository<Usuarioclientesusuario, Long> {
	@Query(""" 
			select 
			o.codigo as codigo,
			o.nombre as nombre ,
			o.apellido as apellido ,
			o.direccion as direccion,
			o.correo as correo,
			o.identificacion as identificacion,
			o.imagenperfil as imagenperfil,
			o.tipoimagen as tipoimagen,
			o.estado as estado
			 from  Usuarioclientesusuario e join e.codigousuario c  join  e.codigocliente o 
			where c.codigo=:usuarioid
			""")

	List<UsuarioclientesDTOS> findByUsuaricliente(@Param("usuarioid") int usuarioid);

	@Query(""" 
			select e.codigo as codigo ,
			o.codigo as codigocliente,
			o.nombre as nombre ,
			o.apellido as apellido ,
			o.direccion as direccion,
			o.correo as correo,
			o.identificacion as identificacion,
			o.imagenperfil as imagenperfil,
			o.tipoimagen as tipoimagen,
			o.estado as estado
			 from  Usuarioclientesusuario e join e.codigousuario c  join  e.codigocliente o 
			where c.codigo=:usuarioid and  o.codigo=:clienteid
			""")

	List<UsuarioclientesDTOS> findByUsuariclienteusuario(@Param("usuarioid") int usuarioid,@Param("clienteid") int clienteid);


	@Query(""" 
			select 
			  o.codigo as codigo,
        o.imagenperfil as imagenperfil,
        o.identificacion as identificacion,
        o.tipoimagen as tipoimagen,
        o.nombre as nombre,
        o.apellido as apellido,
        o.direccion as direccion,
        o.correo as correo,
        o.estado as estado
			 from  Usuarioclientes o 
			
			""")

	List<UsuarioclientesDTOS> findByUsuariclienteind();


	@Query(value = """
			 select                 c.codigo as codigousuario,
               b.codigo as codigo,
			    b.nombre as nombre,
			    b.direccion as direccion,
			    m.municipio as municipio,
			    d.departamento as departamento
			from usuariobodega e
			join usuarios c on c.codigo = e.usuarioid
		
			join bodegas b on b.codigo = e.bodegaid
			join municipios m on m.codigo = b.codigomunicipio
			join departamentos d on d.codigo = b.codigodepartamento
			where c.codigo = :usuarioid
			""", nativeQuery = true)

	List<UsuariobodegasidDTOS> findByUsuariobodega(@Param("usuarioid") int usuarioid);
	@Transactional
	@Modifying
	void deleteByCodigo(int codigo);

	/**
	 * Busca la primera relación usuario-cliente por el código de usuario
	 */
	Optional<Usuarioclientesusuario> findFirstByCodigousuario_Codigo(int codigoUsuario);

}
