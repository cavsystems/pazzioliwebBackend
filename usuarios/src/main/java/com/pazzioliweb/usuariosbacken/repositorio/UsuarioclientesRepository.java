package com.pazzioliweb.usuariosbacken.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pazzioliweb.usuariosbacken.entity.Tipopersona;
import com.pazzioliweb.usuariosbacken.entity.Usuarioclientes;

public interface UsuarioclientesRepository  extends JpaRepository<Usuarioclientes, Long>{
Optional<Usuarioclientes> findByCodigo(int codigo);
    Optional<Usuarioclientes> findByIdentificacion(String identificacion);
void deleteByCodigo( int codigocliente);
}
