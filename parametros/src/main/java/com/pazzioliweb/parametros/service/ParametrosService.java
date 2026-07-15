package com.pazzioliweb.parametros.service;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.parametros.dtos.ComprobanteContableSimpleDTO;
import com.pazzioliweb.parametros.dtos.ParametroComprobanteResponseDTO;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ParametrosService {

    private static final Map<CategoriaComprobante, List<TipoMovimientoComprobante>> CATEGORIA_TIPOS;
    static {
        Map<CategoriaComprobante, List<TipoMovimientoComprobante>> m = new EnumMap<>(CategoriaComprobante.class);
        m.put(CategoriaComprobante.VENTA,      Arrays.asList(TipoMovimientoComprobante.FC, TipoMovimientoComprobante.VC));
        m.put(CategoriaComprobante.COMPRA,     Arrays.asList(TipoMovimientoComprobante.CC, TipoMovimientoComprobante.CR));
        m.put(CategoriaComprobante.DEVOLUCION, Arrays.asList(TipoMovimientoComprobante.DV));
        m.put(CategoriaComprobante.INGRESO,    Arrays.asList(TipoMovimientoComprobante.RC));
        m.put(CategoriaComprobante.EGRESO,     Arrays.asList(TipoMovimientoComprobante.CE));
        m.put(CategoriaComprobante.AJUSTE,     Arrays.asList(TipoMovimientoComprobante.EI, TipoMovimientoComprobante.SI));
        m.put(CategoriaComprobante.CAJA,       Arrays.asList(TipoMovimientoComprobante.RC, TipoMovimientoComprobante.CE));
        m.put(CategoriaComprobante.TRASLADO,   Arrays.asList(TipoMovimientoComprobante.TI));
        CATEGORIA_TIPOS = Collections.unmodifiableMap(m);
    }

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
        return parametrosRepository.findByCategorias(categoriacomprobante, categoriaparametro);
    }

    public List<ParametroGlobalResponseDTO> obtenerParametrosGlobalesConJoin(String categoriaparametro, String categoriacomprobante) {
        return parametrosglobalesRepository.findJoinByCategorias(categoriaparametro, categoriacomprobante);
    }

    public List<ParametroComprobanteResponseDTO> obtenerParametrosPorComprobante(String categoriacomprobante, Long comprobante, String categoriaparametro) {
        return parametroscomprobantesRepository.findJoinByCategorias(categoriacomprobante, comprobante, categoriaparametro);
    }

    public List<ComprobanteContableSimpleDTO> obtenerComprobantesPorCategoria(String categoriaStr) {
        CategoriaComprobante categoria = CategoriaComprobante.valueOf(categoriaStr.toUpperCase());
        List<TipoMovimientoComprobante> tipos = CATEGORIA_TIPOS.getOrDefault(categoria, Collections.emptyList());
        if (tipos.isEmpty()) {
            return Collections.emptyList();
        }
        return comprobanteContableRepository.findByTiposAndActivoTrue(tipos).stream()
                .map(c -> new ComprobanteContableSimpleDTO(
                        c.getId(),
                        c.getTipoMovimiento().name(),
                        c.getTipoMovimiento().getDescripcion(),
                        c.getPrefijo(),
                        c.getDescripcion()))
                .collect(Collectors.toList());
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
