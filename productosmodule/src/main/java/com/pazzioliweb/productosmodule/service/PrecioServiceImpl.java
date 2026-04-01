package com.pazzioliweb.productosmodule.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pazzioliweb.productosmodule.dtos.OtroprecioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.productosmodule.dtos.PrecioCreateDTO;
import com.pazzioliweb.productosmodule.dtos.PrecioResponseDTO;
import com.pazzioliweb.productosmodule.dtos.PrecioUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Precios;
import com.pazzioliweb.productosmodule.mapper.PrecioMapper;
import com.pazzioliweb.productosmodule.repositori.PreciosProductoVarianteRepository;
import com.pazzioliweb.productosmodule.repositori.PreciosRepository;

@Service
public class PrecioServiceImpl implements PrecioService{

    private final PreciosRepository repository;
    private final PrecioMapper mapper;
  private final PreciosProductoVarianteRepository preciore;
    public PrecioServiceImpl(PreciosRepository repository, PrecioMapper mapper,PreciosProductoVarianteRepository preciore) {
        this.repository = repository;
        this.mapper = mapper;
        this.preciore=preciore;
    }

    @Override
    @Transactional
    public PrecioResponseDTO crear(PrecioCreateDTO dto) {
        Precios entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toResponseDto(entity);
    }

    @Override
    public Optional<PrecioResponseDTO> obtenerPorId(Integer id) {
        return repository.findById(id)
                .map(mapper::toResponseDto);
    }

    @Override
    public Page<PrecioResponseDTO> listar(String preciodes,Pageable pageable) {
        return repository.findByDescripcionContainingIgnoreCase(preciodes,pageable)
                .map(mapper::toResponseDto);
    }
    
    @Override
    public Page<PrecioResponseDTO> listarporidproduct(int id, Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDto);
    }

    @Override
    @Transactional
    public Optional<PrecioResponseDTO> actualizar(Integer id, PrecioUpdateDTO dto) {
        return repository.findById(id)
                .map(entity -> {
                    mapper.updateEntity(entity, dto);
                    repository.save(entity);
                    return mapper.toResponseDto(entity);
                });
    }

    @Override
    @Transactional
    public boolean eliminar(Integer id) {
    	
    	
    	
    	   if (preciore.existsByPrecio_PrecioId(id)) {
   	        throw new IllegalStateException(
   	            "Este precio no puede ser eliminado"
   	        );
   	    }
        return repository.findById(id)
                .map(entity -> {
                    repository.delete(entity);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<OtroprecioDTO> obtenerenbezados(List<String> encabezados) {
        ArrayList<OtroprecioDTO> precios=new ArrayList<>();
        for(String pre :encabezados){


            if(!repository.obtenerPorDescripciones(pre).isEmpty()){
                OtroprecioDTO resultado= repository.obtenerPorDescripciones(pre).get(0);

              precios.add(resultado);
            }

         }

        return precios;
    }
}
