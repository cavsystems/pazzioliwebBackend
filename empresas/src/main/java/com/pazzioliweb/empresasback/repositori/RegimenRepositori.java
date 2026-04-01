package com.pazzioliweb.empresasback.repositori;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pazzioliweb.empresasback.entity.Actividadeconomica;
import com.pazzioliweb.empresasback.entity.Regimen;

public interface  RegimenRepositori  extends JpaRepository<Regimen, Long> {
 Optional<Regimen> findByCodigo(int codigo);
}
