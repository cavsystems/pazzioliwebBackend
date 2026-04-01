package com.pazzioliweb.commonbacken.repositorio;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.commonbacken.entity.Sesiones;




public interface SessionRepository  extends  JpaRepository<Sesiones, Long>{
	Optional<Sesiones> findByCodigoAndCodigoUsuario(long codigo,long CodigoUsuario);
	Optional<Sesiones> findFirstBycodigoUsuarioAndEstadoOrderByCodigoDesc(long CodigoUsuario,String estado);
	Optional<Sesiones> findTopByOrderByCodigoDesc();
}