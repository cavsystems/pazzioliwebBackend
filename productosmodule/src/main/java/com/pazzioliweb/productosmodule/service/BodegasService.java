package com.pazzioliweb.productosmodule.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;

@Service
public class BodegasService {
	private final BodegasRepository bodegaRepository;
	
	@Autowired
    public BodegasService(BodegasRepository bodegaRepository) {
        this.bodegaRepository = bodegaRepository;
    }

    public List<Bodegas> listarBodegas() {
        return bodegaRepository.findAll();
    }
}
