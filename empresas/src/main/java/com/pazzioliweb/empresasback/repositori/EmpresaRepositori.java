package com.pazzioliweb.empresasback.repositori;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresaback.dtos.EmpresaContactoDTO;
import com.pazzioliweb.productosmodule.entity.Bodegas;

public interface EmpresaRepositori extends JpaRepository<Empresa, Long> {

 Optional<Empresa> findByCodigo(int id);
 Optional<Empresa> findByRazonsocial(String razonsocial);

 @Query("SELECT new com.pazzioliweb.empresaback.dtos.EmpresaContactoDTO(e.celularempresa, e.correoempresa, e.telfonofijo) FROM com.pazzioliweb.empresasback.entity.Empresa e")
 List<EmpresaContactoDTO> findAllContactoInfo();

 @EntityGraph(attributePaths = {"codigoregimen"})
 List<Empresa> findAll();
}
