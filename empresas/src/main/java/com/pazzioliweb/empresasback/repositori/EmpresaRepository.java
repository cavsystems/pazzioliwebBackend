package com.pazzioliweb.empresasback.repositori;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pazzioliweb.empresaback.dtos.EmpresaResponseauth;
import com.pazzioliweb.empresasback.entity.Empresas;

import jakarta.persistence.EntityManager;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresas, Long> {
  //java annotation para especificar que este va ser un procedimiento almacenado
  // Si el nombre del método coincide con el del procedimiento:  o especificamos el nombre del procedimiento
	

  
}