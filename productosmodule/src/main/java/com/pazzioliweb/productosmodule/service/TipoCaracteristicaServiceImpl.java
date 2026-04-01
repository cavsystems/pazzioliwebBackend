package com.pazzioliweb.productosmodule.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pazzioliweb.productosmodule.entity.TipoCaracteristica;
import com.pazzioliweb.productosmodule.repositori.CaracteristicaRepository;
import com.pazzioliweb.productosmodule.repositori.TipoCaracteristicaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TipoCaracteristicaServiceImpl implements TipoCaracteristicaService{
	private final TipoCaracteristicaRepository repo;
  private final CaracteristicaRepository car;
    public TipoCaracteristicaServiceImpl(TipoCaracteristicaRepository repo,CaracteristicaRepository car) {
        this.repo = repo;
        this.car=car;
    }

    @Override
    public TipoCaracteristica crear(TipoCaracteristica tipo) {
        return repo.save(tipo);
    }

    @Override
    public TipoCaracteristica actualizar(Long id, TipoCaracteristica tipo) {
        TipoCaracteristica existente = buscarPorId(id);

        existente.setNombre(tipo.getNombre());

        return repo.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Tipo de característica no encontrado");
        }
        
        if(car.existsByTipo_tipoCaracteristicaId(id)) {
        	 throw new IllegalStateException(
     	            "Tipo de caracteristica no puede ser eliminada"
     	        );
        }
        repo.deleteById(id);
    }

    @Override
    public TipoCaracteristica buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TipoCaracteristica no encontrado"));
    }

    @Override
    public Page<TipoCaracteristica> listar(String descripcion,Pageable pageable) {
    	System.out.println("[" + descripcion + "]");
    	System.out.println("length = " + descripcion.length()+descripcion.isBlank());
    	 if (descripcion == null || descripcion.isBlank() || descripcion.equals("")) {
		        return repo.findAll(pageable);
		    }
        return repo.findByNombreContainingIgnoreCase( descripcion,pageable);
    }
}
