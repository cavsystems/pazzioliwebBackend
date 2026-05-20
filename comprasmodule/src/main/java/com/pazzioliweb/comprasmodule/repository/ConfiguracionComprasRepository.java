package com.pazzioliweb.comprasmodule.repository;

import com.pazzioliweb.comprasmodule.entity.ConfiguracionCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionComprasRepository extends JpaRepository<ConfiguracionCompras, Integer> {
}
