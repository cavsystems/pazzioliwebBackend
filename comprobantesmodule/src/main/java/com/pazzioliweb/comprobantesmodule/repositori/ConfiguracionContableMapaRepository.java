package com.pazzioliweb.comprobantesmodule.repositori;

import com.pazzioliweb.comprobantesmodule.entity.ConfiguracionContableMapa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConfiguracionContableMapaRepository extends JpaRepository<ConfiguracionContableMapa, String> {
    Optional<ConfiguracionContableMapa> findByClave(String clave);
}
