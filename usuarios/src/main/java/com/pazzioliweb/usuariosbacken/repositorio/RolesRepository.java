package com.pazzioliweb.usuariosbacken.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pazzioliweb.usuariosbacken.dtos.RolesDTOS;
import com.pazzioliweb.usuariosbacken.dtos.RolesusuariosDTOS;
import com.pazzioliweb.usuariosbacken.entity.Permiso;
import com.pazzioliweb.usuariosbacken.entity.Roles;

public interface RolesRepository extends JpaRepository<Roles, Long>{
public List<RolesDTOS>  findByNombreNot(String nombre);
public Optional<Roles>  findByCodigo(int codigo);
@Query(value = """
		select u.codigo as codigo,r.nombre as  rol ,u.usuario as usuario,
           u.contrasena as contrasena,
           u.estado as estado from  Roles r inner join r.usuarios u 
		 """)
public List<RolesusuariosDTOS>  findByUsuariobodega( );
}
