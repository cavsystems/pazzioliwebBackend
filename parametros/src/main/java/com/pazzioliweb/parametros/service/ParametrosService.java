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
                // Verificar si ya existe un registro global para este parámetro
                parametrosglobalesRepository.findByParametrosId(parametro.getId())
                        .ifPresent(p -> {
                            throw new IllegalArgumentException(
                                "El parámetro '" + parametro.getNombre() + "' ya se aplica de manera global"
                            );
                        });
                // Crear registro en parametrosglobales
                crearParametroGlobal(parametro, dto.getValor());
            } else {
                crearParametroComprobante(parametro, dto.getComprobanteContableId(), dto.getValor());
            }
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
        if (comprobante == null) {
            List<Object[]> results = parametroscomprobantesRepository.findJoinByCategoriasSinComprobante(categoriacomprobante, categoriaparametro);
            return results.stream()
                    .map(row -> new ParametroComprobanteResponseDTO(
                            ((Number) row[0]).intValue(),
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            (String) row[4],
                            ((Number) row[5]).longValue(),
                            (String) row[6]
                    ))
                    .collect(Collectors.toList());
        }
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

    @Transactional
    public Parametrosglobales actualizarParametroGlobal(Integer id, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El valor del parámetro es obligatorio");
        }

        Parametrosglobales parametroGlobal = parametrosglobalesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parámetro global no encontrado: " + id));

        parametroGlobal.setValor(valor);
        return parametrosglobalesRepository.save(parametroGlobal);
    }

    @Transactional
    public Object actualizarParametroComprobante(Integer id, String valor, String prefijo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El valor del parámetro es obligatorio");
        }

        // Si se proporciona prefijo, buscar comprobante por prefijo
        if (prefijo != null && !prefijo.isBlank()) {
            var comprobante = comprobanteContableRepository.findByPrefijo(prefijo);
            if (comprobante.isPresent()) {
                // Buscar parámetro comprobante por ID del parámetro y ID del comprobante
                Parametros parametro = parametrosRepository.findById(Long.valueOf(id))
                        .orElseThrow(() -> new EntityNotFoundException("Parámetro no encontrado: " + id));

                Parametroscomprobantes parametroComprobante = parametroscomprobantesRepository
                        .findByParametrosIdAndComprobanteContableId(parametro.getId(), comprobante.get().getId().intValue())
                        .orElseThrow(() -> new EntityNotFoundException(
                            "Parámetro comprobante no encontrado para parámetro " + id + " y comprobante " + comprobante.get().getId()
                        ));

                parametroComprobante.setValor(valor);
                return parametroscomprobantesRepository.save(parametroComprobante);
            }
        }

        // Si no se encuentra comprobante por prefijo o no se proporciona prefijo, actualizar parámetro global
        Parametrosglobales parametroGlobal = parametrosglobalesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parámetro global no encontrado: " + id));

        parametroGlobal.setValor(valor);
        return parametrosglobalesRepository.save(parametroGlobal);
    }

    @Transactional
    public void eliminarParametroGlobal(Integer id) {
        Parametrosglobales parametroGlobal = parametrosglobalesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parámetro global no encontrado: " + id));
        parametrosglobalesRepository.delete(parametroGlobal);
    }

    @Transactional
    public void eliminarParametroComprobante(Integer id, String prefijo) {
        // Si se proporciona prefijo, buscar comprobante por prefijo
        if (prefijo != null && !prefijo.isBlank()) {
            var comprobante = comprobanteContableRepository.findByPrefijo(prefijo);
            if (comprobante.isPresent()) {
                // Buscar parámetro comprobante por ID del parámetro y ID del comprobante
                Parametros parametro = parametrosRepository.findById(Long.valueOf(id))
                        .orElseThrow(() -> new EntityNotFoundException("Parámetro no encontrado: " + id));

                Parametroscomprobantes parametroComprobante = parametroscomprobantesRepository
                        .findByParametrosIdAndComprobanteContableId(parametro.getId(), comprobante.get().getId().intValue())
                        .orElseThrow(() -> new EntityNotFoundException(
                            "Parámetro comprobante no encontrado para parámetro " + id + " y comprobante " + comprobante.get().getId()
                        ));

                parametroscomprobantesRepository.delete(parametroComprobante);
                return;
            }
        }

        // Si no se encuentra comprobante por prefijo o no se proporciona prefijo, eliminar parámetro global
        Parametrosglobales parametroGlobal = parametrosglobalesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parámetro global no encontrado: " + id));
        parametrosglobalesRepository.delete(parametroGlobal);
    }
}
