package com.pazzioliweb.parametros.service;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.parametros.dtos.ParametroCreateDTO;
import com.pazzioliweb.parametros.dtos.ParametroGlobalResponseDTO;
import com.pazzioliweb.parametros.entity.Parametros;
import com.pazzioliweb.parametros.entity.Parametroscomprobantes;
import com.pazzioliweb.parametros.entity.Parametrosglobales;
import com.pazzioliweb.parametros.enums.CategoriaComprobante;
import com.pazzioliweb.parametros.repository.ParametrosRepository;
import com.pazzioliweb.parametros.repository.ParametroscomprobantesRepository;
import com.pazzioliweb.parametros.repository.ParametrosglobalesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParametrosService {

    private final ParametrosRepository parametrosRepository;
    private final ParametroscomprobantesRepository parametroscomprobantesRepository;
    private final ParametrosglobalesRepository parametrosglobalesRepository;
    private final ComprobanteContableRepository comprobanteContableRepository;

    public ParametrosService(
            ParametrosRepository parametrosRepository,
            ParametroscomprobantesRepository parametroscomprobantesRepository,
            ParametrosglobalesRepository parametrosglobalesRepository,
            ComprobanteContableRepository comprobanteContableRepository) {
        this.parametrosRepository = parametrosRepository;
        this.parametroscomprobantesRepository = parametroscomprobantesRepository;
        this.parametrosglobalesRepository = parametrosglobalesRepository;
        this.comprobanteContableRepository = comprobanteContableRepository;
    }

    @Transactional
    public Parametros crearParametro(ParametroCreateDTO dto) {
        if (dto.getParametroId() == null) {
            throw new IllegalArgumentException("El ID del parámetro es obligatorio");
        }
        if (dto.getCategoriaComprobante() == null) {
            throw new IllegalArgumentException("La categoría de comprobante es obligatoria");
        }
        if (dto.getValor() == null || dto.getValor().isBlank()) {
            throw new IllegalArgumentException("El valor del parámetro es obligatorio");
        }

        Parametros parametro = parametrosRepository.findById(dto.getParametroId())
                .orElseThrow(() -> new EntityNotFoundException("Parámetro no encontrado: " + dto.getParametroId()));

        if (dto.getCategoriaComprobante() == CategoriaComprobante.GLOBAL) {
            crearParametroGlobal(parametro, dto.getValor());
        } else {
            if (dto.getComprobanteContableId() == null) {
                throw new IllegalArgumentException(
                    "Para categorías diferentes a GLOBAL, el comprobanteContableId es obligatorio"
                );
            }
            crearParametroComprobante(parametro, dto.getComprobanteContableId(), dto.getValor());
        }

        return parametro;
    }

    public List<Parametros> buscarPorCategorias(String categoriacomprobante, String categoriaparametro) {
        return parametrosRepository.findByCategoriacomprobanteAndCategoriaparametro(categoriacomprobante, categoriaparametro);
    }

    public List<ParametroGlobalResponseDTO> obtenerParametrosGlobalesConJoin(String categoriaparametro, String categoriacomprobante) {
        return parametrosglobalesRepository.findJoinByCategorias(categoriaparametro, categoriacomprobante);
    }

    private void crearParametroGlobal(Parametros parametro, String valor) {
        Parametrosglobales parametroGlobal = new Parametrosglobales();
        parametroGlobal.setParametros(parametro);
        parametroGlobal.setValor(valor);
        parametrosglobalesRepository.save(parametroGlobal);
    }

    private void crearParametroComprobante(Parametros parametro, Integer comprobanteContableId, String valor) {
        ComprobanteContable comprobante = comprobanteContableRepository.findById(Long.valueOf(comprobanteContableId))
                .orElseThrow(() -> new EntityNotFoundException(
                    "Comprobante contable no encontrado: " + comprobanteContableId
                ));

        Parametroscomprobantes parametroComprobante = new Parametroscomprobantes();
        parametroComprobante.setParametros(parametro);
        parametroComprobante.setComprobanteContable(comprobante);
        parametroComprobante.setValor(valor);
        parametroscomprobantesRepository.save(parametroComprobante);
    }
}
