package com.pazzioliweb.tercerosmodule.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pazzioliweb.tercerosmodule.entity.ClasificacionTercero;
import com.pazzioliweb.tercerosmodule.repositori.ClasificacionTerceroRepository;

@Service
public class ClasificacionTerceroService {
	private final ClasificacionTerceroRepository repository;

    @Autowired
    public ClasificacionTerceroService(ClasificacionTerceroRepository repository) {
        this.repository = repository;
    }

    public Page<ClasificacionTercero> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<ClasificacionTercero> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public ClasificacionTercero guardar(ClasificacionTercero clasificacion) {
        return repository.save(clasificacion);
    }

    public void eliminar(Integer id) {
        repository.deleteById(id);
    }
}
