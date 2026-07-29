package com.pazzioliweb.productosmodule.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.productosmodule.entity.*;
import com.pazzioliweb.productosmodule.entity.PreciosProductoVariante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.commonbacken.entity.Impuestos;
import com.pazzioliweb.commonbacken.repositorio.ImpuestosRepositori;
import com.pazzioliweb.productosmodule.dtos.LineaProductosDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoUpdateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoActualizarCrearDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoConVariantesDTO;
import com.pazzioliweb.productosmodule.dtos.VarianteDTO;
import com.pazzioliweb.productosmodule.dtos.CaracteristicaDetalleDTO;
import com.pazzioliweb.productosmodule.dtos.TipoCaracteristicaDTO;
import com.pazzioliweb.productosmodule.mapper.ProductoMapper;
import com.pazzioliweb.productosmodule.repositori.GrupoRepositori;
import com.pazzioliweb.productosmodule.repositori.LineasRepositori;
import com.pazzioliweb.productosmodule.repositori.ProductosRepository;
import com.pazzioliweb.productosmodule.repositori.UnidadesMedidaRepository;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.productosmodule.repositori.ExistenciasRepository;
import com.pazzioliweb.productosmodule.repositori.TipoCaracteristicaRepository;
import com.pazzioliweb.productosmodule.repositori.CaracteristicaRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteDetalleRepository;
import com.pazzioliweb.productosmodule.repositori.TipoProductoRepository;
import com.pazzioliweb.productosmodule.repositori.PreciosProductoVarianteRepository;
import com.pazzioliweb.productosmodule.repositori.PreciosRepository;
import com.pazzioliweb.productosmodule.repositori.UnidadesMedidaProductoRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
public class ProductosServiceImpl implements ProductosService{
	private final ProductosRepository productosRepository;
	private final GrupoRepositori grupoRepository;
	private final LineasRepositori lineaRepository;
	private final ImpuestosRepositori impuestoRepository;
	private final UsuarioRepository usuarioRepository;
	private final ProductoMapper mapper;
	private final UnidadesMedidaProductoRepository unidadesMedidaProductoRepository;
	private final TipoProductoRepository tipoProductoRepository;
	private final CaracteristicaRepository caracteristicaRepository;
	private final ProductoVarianteRepository productoVarianteRepository;
    private final ProductoVarianteDetalleRepository productoVarianteDetalleRepository;
	private final UnidadesMedidaRepository unidadesMedidaRepository;
	private final BodegasRepository bodegasRepository;
	private final ExistenciasRepository existenciasRepository;
	private final TipoCaracteristicaRepository tipoCaracteristicaRepository;
    private final PreciosProductoVarianteRepository preciosProductoVarianteRepository;
    private final PreciosRepository preciosRepository;

    public ProductosServiceImpl(ProductosRepository productosRepository, GrupoRepositori grupoRepository,
                                LineasRepositori lineaRepository,ImpuestosRepositori impuestoRepository,UsuarioRepository usuarioRepository,
                                ProductoMapper mapper,UnidadesMedidaProductoRepository unidadesMedidaProductoRepository,
                                TipoProductoRepository tipoProductoRepository, CaracteristicaRepository caracteristicaRepository,
                                ProductoVarianteRepository productoVarianteRepository, ProductoVarianteDetalleRepository productoVarianteDetalleRepository, UnidadesMedidaRepository unidadesMedidaRepository,
                                BodegasRepository bodegasRepository, ExistenciasRepository existenciasRepository, TipoCaracteristicaRepository tipoCaracteristicaRepository,
                                PreciosProductoVarianteRepository preciosProductoVarianteRepository, PreciosRepository preciosRepository) {
        this.productosRepository = productosRepository;
        this.grupoRepository = grupoRepository;
        this.lineaRepository = lineaRepository;
        this.impuestoRepository = impuestoRepository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
        this.unidadesMedidaProductoRepository = unidadesMedidaProductoRepository;
        this.tipoProductoRepository = tipoProductoRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.productoVarianteRepository = productoVarianteRepository;
        this.productoVarianteDetalleRepository = productoVarianteDetalleRepository;
        this.unidadesMedidaRepository = unidadesMedidaRepository;
        this.bodegasRepository = bodegasRepository;
        this.existenciasRepository = existenciasRepository;
        this.tipoCaracteristicaRepository = tipoCaracteristicaRepository;
        this.preciosProductoVarianteRepository = preciosProductoVarianteRepository;
        this.preciosRepository = preciosRepository;
    }

    // ---------------------------------------------
    // GUARDAR POR DTO
    // ---------------------------------------------
	@Override
    @Transactional
    public Productos guardarDesdeDTO(ProductoCreateDTO dto) {
		
		if (productosRepository.existsByCodigoContable(dto.getCodigo_contable())) {
		    throw new RuntimeException("El código contable ya existe");
		}

		if (productosRepository.existsByCodigoBarras(dto.getCodigo_barras())) {
		    throw new RuntimeException("El código de barras ya existe");
		}
		
        Grupos grupo = grupoRepository.findById(dto.getGrupo_id())
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado"));

        Lineas linea = lineaRepository.findById(dto.getLinea_id())
                .orElseThrow(() -> new EntityNotFoundException("Línea no encontrada"));

        Impuestos impuesto = impuestoRepository.findById(dto.getImpuesto_id())
                .orElseThrow(() -> new EntityNotFoundException("Impuesto no encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuario_creo_id())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        TipoProducto tipoProducto = tipoProductoRepository.findById(dto.getTipo_producto_id())
        		.orElseThrow(() -> new EntityNotFoundException("TipoProducto no encontrado"));

        Productos entidad = mapper.fromCreateDto(dto, grupo, linea, impuesto, usuario, tipoProducto);

        return productosRepository.save(entidad);
    }
    
    @Override
    public ProductoResponseDTO convertirAResponse(Productos p) {
        return mapper.toResponseDto(p);
    }

    // ---------------------------------------------
    // BUSCAR POR ID SIMPLE
    // ---------------------------------------------
    @Override
    public Optional<Productos> buscarPorId(Integer id) {
        return productosRepository.findById(id);
    }

    // ---------------------------------------------
    // BUSCAR POR ID CON RELACIONES (FETCH)
    // ---------------------------------------------
    @Override
    public Optional<Productos> buscarPorIdConRelaciones(Integer id) {
        return productosRepository.findByIdWithRelations(id);
    }

    // ---------------------------------------------
    // LISTAR PAGINADO
    // ---------------------------------------------
    @Override
    public Page<Productos> listar(Pageable pageable) {
        return productosRepository.traerProductos(pageable);
    }

    // ---------------------------------------------
    // BUSCAR CON FILTRO (LIKE)
    // ---------------------------------------------
    @Override
    public Page<Productos> buscarPorFiltro(String busqueda, Pageable pageable) {
        return productosRepository.traerProductosXFiltro(busqueda, pageable);
    }
    
    // ---------------------------------------------
    // ELIMINA POR ID
    // ---------------------------------------------
    @Override
    public void eliminar(Integer id) {

        // Verifica que exista antes de eliminar
        if (!productosRepository.existsById(id)) {
            throw new EntityNotFoundException("El producto con ID " + id + " no existe.");
        }

        try {
            productosRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando el producto con ID " + id, e);
        }
    }
    
    @Transactional
    public void eliminarProducto(Integer productoId) {

        // 1. Borrar manual los hijos con claves compuestas
        unidadesMedidaProductoRepository.deleteByProducto_ProductoId(productoId);

        // 2. Obtener producto para cascada del resto
        Productos producto = productosRepository.findById(productoId)
            .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // 3. Eliminar producto → cascada se encarga de variantes, detalles, existencias
        productosRepository.delete(producto);
    }
    
 // ---------------------------------------------
    // ACTUALIZAR POR DTO
    // ---------------------------------------------
    @Override
    @Transactional
    public Productos actualizarDesdeDTO(Integer id, ProductoUpdateDTO dto) {

        Productos existente = productosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        
        // 1️⃣ VALIDAR UNICIDAD DE CÓDIGO CONTABLE
        if (dto.getCodigo_contable() != null) {
            boolean existeContable = productosRepository
                    .existsByCodigoContableAndProductoIdNot(dto.getCodigo_contable(), id);

            if (existeContable) {
                throw new IllegalArgumentException("El código contable ya está asignado a otro producto.");
            }
        }

        // 2️⃣ VALIDAR UNICIDAD DE CÓDIGO DE BARRAS
        if (dto.getCodigo_barras() != null) {
            boolean existeBarras = productosRepository
                    .existsByCodigoBarrasAndProductoIdNot(dto.getCodigo_barras(), id);

            if (existeBarras) {
                throw new IllegalArgumentException("El código de barras ya está asignado a otro producto.");
            }
        }

        // Actualizamos solo si llega un valor distinto de null
        if(dto.getEstado().equals("INACTIVO")) {
        	existente.setEstado(dto.getEstado());
        }else {
        	
        	 if (dto.getReferencia() != null) {
                 existente.setReferencia(dto.getReferencia());
             }

             if (dto.getDescripcion() != null) {
                 existente.setDescripcion(dto.getDescripcion());
             }

             if (dto.getCodigo_contable() != null) {
                 existente.setCodigoContable(dto.getCodigo_contable());
             }

             if (dto.getCodigo_barras() != null) {
                 existente.setCodigoBarras(dto.getCodigo_barras());
             }

             if (dto.getCosto() != null) {
                 existente.setCosto(dto.getCosto());
             }

             if (dto.getManifiesto() != null) {
                 existente.setManifiesto(dto.getManifiesto());
             }

             // Imagen / manifiesto (PDF): "" es una orden EXPLÍCITA de quitarlo (se guarda null).
             // null = campo no enviado / no cambiar (para actualizaciones parciales de otros endpoints).
             if (dto.getImagen() != null) {
                 existente.setImagen(dto.getImagen().isEmpty() ? null : dto.getImagen());
             }

             // Relaciones
             if (dto.getGrupo_id() != null) {
                 existente.setGrupo(
                         grupoRepository.findById(dto.getGrupo_id())
                                 .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado"))
                 );
             }

             if (dto.getLinea_id() != null) {
                 existente.setLinea(
                         lineaRepository.findById(dto.getLinea_id())
                                 .orElseThrow(() -> new EntityNotFoundException("Linea no encontrada"))
                 );
             }

             if (dto.getImpuesto_id() != null) {
                 existente.setImpuestos(
                         impuestoRepository.findById(dto.getImpuesto_id())
                                 .orElseThrow(() -> new EntityNotFoundException("Impuesto no encontrado"))
                 );
             }
             
             existente.setEstado(dto.getEstado());

        }
       
        return productosRepository.save(existente);
    }
    
    @Override
    public Page<LineaProductosDTO> listarTotalesPorLineaTodasBodegas(Pageable pageable){
    	return productosRepository.getTotalesPorLineaTodasBodegas(pageable);
    }
    
    @Override
    public Page<LineaProductosDTO> listarTotalesPorLineaXBodegaId(Integer bodegaId, Pageable pageable){
    	return productosRepository.getTotalesPorLineaPorBodegaId(bodegaId, pageable);
    }
    
    @Override
    @Transactional
    public void actualizarOCrearProducto(List<ProductoActualizarCrearDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return;
        try {
            // === FASE 1: Recolectar todas las claves únicas del batch ===
            Set<String> codigos = new HashSet<>();
            Set<String> gruposDesc = new HashSet<>();
            Set<String> lineasDesc = new HashSet<>();
            Set<Double> tarifas = new HashSet<>();
            Set<String> tiposProductoNombres = new HashSet<>();
            Set<String> siglas = new HashSet<>();
            Set<String> codigosBarrasVariante = new HashSet<>();
            Set<Integer> bodegasIds = new HashSet<>();
            Set<Integer> preciosTypeIds = new HashSet<>();
            Set<String> tiposCaracteristicaNombres = new HashSet<>();
            Set<String> valoresCaracteristica = new HashSet<>();

            for (ProductoActualizarCrearDTO dto : dtos) {
                if (dto.getCodigo() != null) codigos.add(dto.getCodigo());
                if (dto.getGrupo() != null) gruposDesc.add(dto.getGrupo());
                if (dto.getLinea() != null) lineasDesc.add(dto.getLinea());
                if (dto.getImpuesto() != null) tarifas.add(dto.getImpuesto().doubleValue());
                if (dto.getTipoProducto() != null) tiposProductoNombres.add(dto.getTipoProducto());
                if (dto.getUnidadMedida() != null) siglas.add(dto.getUnidadMedida());
                if (dto.getVariantes() != null) {
                    for (ProductoActualizarCrearDTO.VarianteDTO v : dto.getVariantes()) {
                        if (v.getCodigoBarraVariante() != null) codigosBarrasVariante.add(v.getCodigoBarraVariante());
                        if (v.getExistencias() != null) {
                            v.getExistencias().forEach(e -> { if (e.getBodegaId() != null) bodegasIds.add(e.getBodegaId()); });
                        }
                        if (v.getPrecios() != null) {
                            v.getPrecios().forEach(p -> { if (p.getIdTipoPrecio() != null) preciosTypeIds.add(p.getIdTipoPrecio()); });
                        }
                        if (v.getAtributos() != null) {
                            v.getAtributos().forEach(a -> {
                                if (a.getNombre() != null && !"descripcion".equalsIgnoreCase(a.getNombre())) tiposCaracteristicaNombres.add(a.getNombre());
                                if (a.getValor() != null) valoresCaracteristica.add(a.getValor());
                            });
                        }
                    }
                }
            }

            // === FASE 2: Carga masiva de lookups (1 query por tabla en vez de N) ===
            Map<String, Productos> productosMap = new HashMap<>(
                productosRepository.findByCodigoContableIn(codigos).stream()
                    .collect(Collectors.toMap(Productos::getCodigoContable, p -> p)));

            Map<String, Grupos> gruposMap = new HashMap<>(
                grupoRepository.findByDescripcionIn(gruposDesc).stream()
                    .collect(Collectors.toMap(Grupos::getDescripcion, g -> g)));

            Map<String, Lineas> lineasMap = new HashMap<>(
                lineaRepository.findByDescripcionIn(lineasDesc).stream()
                    .collect(Collectors.toMap(Lineas::getDescripcion, l -> l)));

            Map<Double, Impuestos> impuestosMap = impuestoRepository.findByTarifaIn(new ArrayList<>(tarifas)).stream()
                    .collect(Collectors.toMap(Impuestos::getTarifa, i -> i));

            Map<String, TipoProducto> tiposProductoMap = tipoProductoRepository.findByNombreIn(tiposProductoNombres).stream()
                    .collect(Collectors.toMap(TipoProducto::getNombre, t -> t));

            Map<String, UnidadesMedida> unidadesMedidaMap = siglas.isEmpty() ? Collections.emptyMap()
                : unidadesMedidaRepository.findBySiglaIn(siglas).stream()
                    .collect(Collectors.toMap(UnidadesMedida::getSigla, u -> u));

            Map<String, ProductoVariante> variantesMap = new HashMap<>(codigosBarrasVariante.isEmpty()
                ? Collections.emptyMap()
                : productoVarianteRepository.findByCodigoBarrasIn(codigosBarrasVariante).stream()
                    .collect(Collectors.toMap(ProductoVariante::getCodigoBarras, v -> v)));

            Map<Integer, Bodegas> bodegasMap = bodegasIds.isEmpty() ? Collections.emptyMap()
                : bodegasRepository.findByCodigoIn(bodegasIds).stream()
                    .collect(Collectors.toMap(b -> b.getCodigo(), b -> b));

            Map<Integer, com.pazzioliweb.productosmodule.entity.Precios> preciosMap = preciosTypeIds.isEmpty()
                ? Collections.emptyMap()
                : preciosRepository.findAllById(preciosTypeIds).stream()
                    .collect(Collectors.toMap(com.pazzioliweb.productosmodule.entity.Precios::getPrecioId, p -> p));

            Map<String, TipoCaracteristica> tiposCaracteristicaMap = tiposCaracteristicaNombres.isEmpty()
                ? Collections.emptyMap()
                : tipoCaracteristicaRepository.findByNombreIn(tiposCaracteristicaNombres).stream()
                    .collect(Collectors.toMap(TipoCaracteristica::getNombre, t -> t));

            Map<String, Caracteristica> caracteristicasMap = new HashMap<>(valoresCaracteristica.isEmpty()
                ? Collections.emptyMap()
                : caracteristicaRepository.findByNombreIn(new ArrayList<>(valoresCaracteristica)).stream()
                    .collect(Collectors.toMap(Caracteristica::getNombre, c -> c)));

            Usuario usuario = usuarioRepository.findById(1).orElse(null);

            // === FASE 3: Construir entidades Producto usando los mapas cacheados ===
            List<Productos> productosToSave = new ArrayList<>();
            for (ProductoActualizarCrearDTO dto : dtos) {
                Productos producto = productosMap.getOrDefault(dto.getCodigo(), new Productos());
                producto.setCodigoContable(dto.getCodigo());
                producto.setDescripcion(dto.getDescripcion());
                producto.setReferencia(dto.getReferencia());
                producto.setCosto(dto.getCosto().doubleValue());
                producto.setCodigoBarras(dto.getCodigoBarras());
                producto.setEstado("Activo");

                boolean tieneAtributoDescripcion = dto.getVariantes() != null && dto.getVariantes().stream()
                    .anyMatch(v -> v.getAtributos() != null && v.getAtributos().stream()
                        .anyMatch(a -> "descripcion".equalsIgnoreCase(a.getNombre())));
                producto.setManejaVariantes(dto.getVariantes() != null && !dto.getVariantes().isEmpty() && !tieneAtributoDescripcion);

                Grupos grupo = gruposMap.computeIfAbsent(dto.getGrupo(), desc -> {
                    Grupos nuevoGrupo = new Grupos();
                    nuevoGrupo.setId(grupoRepository.findPrimerHueco());
                    nuevoGrupo.setDescripcion(desc);
                    return grupoRepository.save(nuevoGrupo);
                });
                producto.setGrupo(grupo);

                Lineas linea = lineasMap.computeIfAbsent(dto.getLinea(), desc -> {
                    Lineas nuevaLinea = new Lineas();
                    nuevaLinea.setId(lineaRepository.findPrimerHueco());
                    nuevaLinea.setDescripcion(desc);
                    return lineaRepository.save(nuevaLinea);
                });
                producto.setLinea(linea);

                Impuestos impuesto = dto.getImpuesto() != null ? impuestosMap.get(dto.getImpuesto().doubleValue()) : null;
                if (impuesto == null) throw new EntityNotFoundException("Impuesto no encontrado: " + dto.getImpuesto());
                producto.setImpuestos(impuesto);

                TipoProducto tipoProducto = tiposProductoMap.get(dto.getTipoProducto());
                if (tipoProducto == null) throw new EntityNotFoundException("TipoProducto no encontrado: " + dto.getTipoProducto());
                producto.setTipoProducto(tipoProducto);

                producto.setUsuario(usuario);
                productosToSave.add(producto);
            }

            // Guardar todos los productos en un solo batch
            List<Productos> savedProductos = productosRepository.saveAll(productosToSave);
            Map<String, Productos> savedProductosMap = savedProductos.stream()
                    .collect(Collectors.toMap(Productos::getCodigoContable, p -> p));
            // Actualizar el mapa compartido con los IDs recién generados
            savedProductosMap.forEach(productosMap::put);

            // === UnidadesMedida (requiere IDs de productos) ===
            List<UnidadesMedidaProducto> umpToSave = new ArrayList<>();
            for (ProductoActualizarCrearDTO dto : dtos) {
                if (dto.getUnidadMedida() != null) {
                    UnidadesMedida um = unidadesMedidaMap.get(dto.getUnidadMedida());
                    if (um == null) throw new EntityNotFoundException("UnidadMedida no encontrada: " + dto.getUnidadMedida());
                    Productos producto = savedProductosMap.get(dto.getCodigo());
                    UnidadesMedidaProducto ump = new UnidadesMedidaProducto();
                    UnidadesMedidaProductoId umpId = new UnidadesMedidaProductoId();
                    umpId.setProductoId(producto.getProductoId());
                    umpId.setUnidadMedidaId(um.getUnidadMedidaId());
                    ump.setId(umpId);
                    ump.setProducto(producto);
                    ump.setUnidadMedida(um);
                    umpToSave.add(ump);
                }
            }
            if (!umpToSave.isEmpty()) unidadesMedidaProductoRepository.saveAll(umpToSave);

            // === FASE 4: Construir variantes ===
            List<ProductoVariante> variantesToSave = new ArrayList<>();
            // Guardar referencia a (varianteDto, dto) para procesarlos tras tener IDs de variantes
            List<Object[]> variantePairs = new ArrayList<>();

            for (ProductoActualizarCrearDTO dto : dtos) {
                Productos producto = savedProductosMap.get(dto.getCodigo());

                if (Boolean.TRUE.equals(producto.getManejaVariantes()) && dto.getVariantes() != null && !dto.getVariantes().isEmpty()) {
                    for (ProductoActualizarCrearDTO.VarianteDTO varianteDto : dto.getVariantes()) {
                        ProductoVariante variante = variantesMap.getOrDefault(varianteDto.getCodigoBarraVariante(), new ProductoVariante());
                        boolean esNueva = variante.getProductoVarianteId() == null;

                        variante.setProducto(producto);
                        // SKU: NO degradar el SKU de una variante existente usando el código de barras como fallback.
                        // La variante por defecto guarda sku = código contable; pisarlo con un código de barras
                        // rompía la actualización de existencias y dejaba variantes descuadradas (bug #9).
                        if (varianteDto.getSku() != null) {
                            variante.setSku(varianteDto.getSku());
                        } else if (esNueva) {
                            variante.setSku(varianteDto.getCodigoBarraVariante());
                        }
                        variante.setCodigoBarras(varianteDto.getCodigoBarraVariante());
                        if (varianteDto.getReferenciaVariantes() != null) {
                            variante.setReferenciaVariantes(varianteDto.getReferenciaVariantes());
                        } else if (esNueva) {
                            variante.setReferenciaVariantes(varianteDto.getCodigoBarraVariante());
                        }
                        variante.setActivo(true);
                        // Predeterminada: honrar el flag del DTO; si no viene, conservar el valor actual.
                        // Antes se forzaba SIEMPRE a false, degradando la variante por defecto (bug #8).
                        if (varianteDto.getPredeterminada() != null) {
                            variante.setPredeterminada(varianteDto.getPredeterminada());
                        } else if (esNueva) {
                            variante.setPredeterminada(false);
                        }

                        variantesToSave.add(variante);
                        variantePairs.add(new Object[]{varianteDto, variante, dto});
                    }
                } else {
                    ProductoVariante variante = productoVarianteRepository.findByProductoAndPredeterminada(producto, true)
                            .orElse(new ProductoVariante());
                    variante.setProducto(producto);
                    variante.setSku(dto.getCodigo());
                    if (dto.getCodigoBarras() != null) variante.setCodigoBarras(dto.getCodigoBarras());
                    if (dto.getReferencia() != null) variante.setReferenciaVariantes(dto.getReferencia());
                    variante.setActivo(true);
                    variante.setPredeterminada(true);
                    variantesToSave.add(variante);
                }
            }

            // Guardar todas las variantes en un solo batch
            List<ProductoVariante> savedVariantes = productoVarianteRepository.saveAll(variantesToSave);
            for (ProductoVariante sv : savedVariantes) {
                if (sv.getCodigoBarras() != null) variantesMap.put(sv.getCodigoBarras(), sv);
            }

            if (variantePairs.isEmpty()) return;

            // === FASE 5: Pre-cargar PreciosProductoVariante existentes en batch ===
            Set<Long> savedVarianteIds = savedVariantes.stream()
                    .filter(v -> v.getProductoVarianteId() != null)
                    .map(ProductoVariante::getProductoVarianteId)
                    .collect(Collectors.toSet());

            Map<String, PreciosProductoVariante> preciosVarianteMap = new HashMap<>();
            if (!savedVarianteIds.isEmpty()) {
                preciosProductoVarianteRepository.findByProductoVariante_ProductoVarianteIdIn(savedVarianteIds)
                    .forEach(ppv -> {
                        String key = ppv.getProductoVariante().getProductoVarianteId() + "_" + ppv.getPrecio().getPrecioId();
                        preciosVarianteMap.put(key, ppv);
                    });
            }

            // === FASE 6: Pre-crear Caracteristicas nuevas antes de construir detalles ===
            Map<String, Caracteristica> newCaracteristicasPerAtributo = new HashMap<>();
            for (Object[] pair : variantePairs) {
                ProductoActualizarCrearDTO.VarianteDTO varianteDto = (ProductoActualizarCrearDTO.VarianteDTO) pair[0];
                if (varianteDto.getAtributos() == null) continue;
                for (ProductoActualizarCrearDTO.AtributoDTO attrDto : varianteDto.getAtributos()) {
                    if ("descripcion".equalsIgnoreCase(attrDto.getNombre()) || attrDto.getValor() == null) continue;
                    if (!caracteristicasMap.containsKey(attrDto.getValor()) && !newCaracteristicasPerAtributo.containsKey(attrDto.getValor())) {
                        Caracteristica nueva = new Caracteristica();
                        nueva.setNombre(attrDto.getValor());
                        nueva.setTipo(tiposCaracteristicaMap.get(attrDto.getNombre()));
                        newCaracteristicasPerAtributo.put(attrDto.getValor(), nueva);
                    }
                }
            }
            if (!newCaracteristicasPerAtributo.isEmpty()) {
                caracteristicaRepository.saveAll(newCaracteristicasPerAtributo.values())
                    .forEach(c -> caracteristicasMap.put(c.getNombre(), c));
            }

            // === FASE 7: Construir existencias, precios y detalles ===
            List<Existencias> existenciasToSave = new ArrayList<>();
            List<PreciosProductoVariante> preciosToSave = new ArrayList<>();
            List<ProductoVarianteDetalle> detallesToSave = new ArrayList<>();

            for (Object[] pair : variantePairs) {
                ProductoActualizarCrearDTO.VarianteDTO varianteDto = (ProductoActualizarCrearDTO.VarianteDTO) pair[0];
                ProductoActualizarCrearDTO dto = (ProductoActualizarCrearDTO) pair[2];
                ProductoVariante variante = variantesMap.get(varianteDto.getCodigoBarraVariante());
                if (variante == null) continue;

                // Existencias
                if (varianteDto.getExistencias() != null && !varianteDto.getExistencias().isEmpty()) {
                    Integer bodegaDestinoId = varianteDto.getExistencias().get(0).getBodegaId();
                    Bodegas bodegaDestino = bodegasMap.get(bodegaDestinoId);
                    if (bodegaDestino == null) throw new EntityNotFoundException("Bodega destino no encontrada: " + bodegaDestinoId);

                    Existencias existencia = existenciasRepository
                            .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(variante.getProductoVarianteId(), bodegaDestino.getCodigo())
                            .orElse(new Existencias());
                    existencia.setBodega(bodegaDestino);
                    existencia.setProductoVariante(variante);
                    ProductoActualizarCrearDTO.ExistenciaDTO existenciaDto = varianteDto.getExistencias().get(0);
                    if (existenciaDto.getMinimo() != null) existencia.setStockMin(BigDecimal.valueOf(existenciaDto.getMinimo()));
                    if (existenciaDto.getMaximo() != null) existencia.setStockMax(BigDecimal.valueOf(existenciaDto.getMaximo()));
                    if (dto.getUbicacion() != null) existencia.setUbicacion(dto.getUbicacion());
                    if (existencia.getExistenciaId() == null) {
                        existencia.setExistencia(BigDecimal.ZERO);
                        existencia.setFechaUltimoMovimiento(LocalDateTime.now());
                    }
                    existenciasToSave.add(existencia);
                }

                // Precios
                if (varianteDto.getPrecios() != null) {
                    for (ProductoActualizarCrearDTO.PrecioDTO precioDto : varianteDto.getPrecios()) {
                        com.pazzioliweb.productosmodule.entity.Precios precio = preciosMap.get(precioDto.getIdTipoPrecio());
                        if (precio == null) throw new EntityNotFoundException("Precio no encontrado: " + precioDto.getIdTipoPrecio());

                        String key = variante.getProductoVarianteId() + "_" + precio.getPrecioId();
                        PreciosProductoVariante ppv = preciosVarianteMap.getOrDefault(key, null);
                        if (ppv != null) {
                            ppv.setValor(precioDto.getValor().doubleValue());
                        } else {
                            ppv = new PreciosProductoVariante();
                            ppv.setProductoVariante(variante);
                            ppv.setPrecio(precio);
                            ppv.setValor(precioDto.getValor().doubleValue());
                            ppv.setFechaCreacion(LocalDateTime.now());
                            ppv.setFechaInicio(LocalDateTime.now());
                        }
                        preciosToSave.add(ppv);
                    }
                }

                // Atributos
                if (varianteDto.getAtributos() != null) {
                    for (ProductoActualizarCrearDTO.AtributoDTO attrDto : varianteDto.getAtributos()) {
                        if ("descripcion".equalsIgnoreCase(attrDto.getNombre()) || attrDto.getValor() == null) continue;

                        Caracteristica caracteristica = caracteristicasMap.get(attrDto.getValor());
                        if (caracteristica == null) continue;

                        // Actualizar tipo si cambió
                        TipoCaracteristica tipo = tiposCaracteristicaMap.get(attrDto.getNombre());
                        caracteristica.setTipo(tipo);

                        ProductoVarianteDetalle detalle = productoVarianteDetalleRepository
                                .findByProductoVarianteAndCaracteristica(variante, caracteristica)
                                .orElse(new ProductoVarianteDetalle());
                        detalle.setProductoVariante(variante);
                        detalle.setCaracteristica(caracteristica);
                        detallesToSave.add(detalle);
                    }
                }
            }

            // Guardar todo en batch
            if (!existenciasToSave.isEmpty()) existenciasRepository.saveAll(existenciasToSave);
            if (!preciosToSave.isEmpty()) preciosProductoVarianteRepository.saveAll(preciosToSave);
            if (!detallesToSave.isEmpty()) productoVarianteDetalleRepository.saveAll(detallesToSave);

        } catch (Exception e) {
            System.out.println("ERROR REAL:");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public void actualizarInventario(String codigoProducto, String codigoVariante, Integer cantidad, Integer bodegaId) {
        // Find the product variant
        ProductoVariante variante = productoVarianteRepository.findByProducto_CodigoContableAndReferenciaVariantes(codigoProducto, codigoVariante)
                .orElseThrow(() -> new EntityNotFoundException("Producto variante no encontrado: " + codigoProducto + " - " + codigoVariante));

        // Find or create Existencias
        Existencias existencia = existenciasRepository.findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(variante.getProductoVarianteId(), bodegaId)
                .orElse(new Existencias());

        if (existencia.getExistenciaId() == null) {
            existencia.setProductoVariante(variante);
            Bodegas bodega = bodegasRepository.findById(bodegaId)
                    .orElseThrow(() -> new EntityNotFoundException("Bodega no encontrada: " + bodegaId));
            existencia.setBodega(bodega);
            existencia.setExistencia(BigDecimal.ZERO);
            existencia.setFechaUltimoMovimiento(LocalDateTime.now());
        }

        if(existencia.getExistencia() == null){
            existencia.setExistencia(BigDecimal.valueOf(cantidad));
        }else {
            existencia.setExistencia(existencia.getExistencia().add(BigDecimal.valueOf(cantidad)));
        }
        // Add the cantidad (can be negative)

      

        existenciasRepository.save(existencia);
    }

    @Override
    public Page<ProductoConVariantesDTO> listarProductosConVariantesYCaracteristicas(Pageable pageable) {
        Page<Productos> productosPage = productosRepository.findAllWithVariantesAndCaracteristicas(pageable);
        return productosPage.map(this::convertirAProductoConVariantesDTO);
    }

    @Override
    public List<ProductoConVariantesDTO> listarTodosProductosConVariantesYCaracteristicas() {
        List<Productos> productos = productosRepository.findAllWithVariantes();
        return productos.stream()
                .map(this::convertirAProductoConVariantesDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Productos> findAllWithVariantes() {
        return productosRepository.findAllWithVariantes();
    }

    private ProductoConVariantesDTO convertirAProductoConVariantesDTO(Productos producto) {
        ProductoConVariantesDTO dto = new ProductoConVariantesDTO();
        dto.setProductoId(producto.getProductoId());
        dto.setCodigoContable(producto.getCodigoContable());
        dto.setCodigoBarras(producto.getCodigoBarras());
        dto.setReferencia(producto.getReferencia());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCosto(producto.getCosto());
        dto.setEstado(producto.getEstado());
        dto.setImagen(producto.getImagen());
        dto.setManejaVariantes(producto.getManejaVariantes());
        dto.setGrupo(producto.getGrupo() != null ? producto.getGrupo().getDescripcion() : null);
        dto.setLinea(producto.getLinea() != null ? producto.getLinea().getDescripcion() : null);
        dto.setImpuesto(producto.getImpuestos() != null ? String.valueOf(producto.getImpuestos().getTarifa()) : null);
        dto.setTipoProducto(producto.getTipoProducto() != null ? producto.getTipoProducto().getNombre() : null);

        List<VarianteDTO> variantesDTO = producto.getVariantes().stream()
                .map(this::convertirAVarianteDTO)
                .collect(Collectors.toList());
        dto.setVariantes(variantesDTO);

        // Fetch unit of measurement for the product (take first position)
        List<UnidadesMedidaProducto> unidadesMedidaProductos = unidadesMedidaProductoRepository.findByProducto_ProductoId(producto.getProductoId());
        String unidadMedidaSigla = unidadesMedidaProductos.stream()
                .map(ump -> ump.getUnidadMedida() != null ? ump.getUnidadMedida().getSigla() : null)
                .filter(sigla -> sigla != null)
                .findFirst()
                .orElse(null);
        dto.setUnidadMedida(unidadMedidaSigla);

        return dto;
    }

    private VarianteDTO convertirAVarianteDTO(ProductoVariante variante) {
        VarianteDTO dto = new VarianteDTO();
        dto.setProductoVarianteId(variante.getProductoVarianteId());
        dto.setSku(variante.getSku());
        dto.setReferenciaVariantes(variante.getReferenciaVariantes());
        dto.setCodigoBarras(variante.getCodigoBarras());
        dto.setPrecio(variante.getPrecio());
        dto.setActivo(variante.getActivo());
        dto.setPredeterminada(variante.getPredeterminada());
        dto.setImagen(variante.getImagen());
        dto.setUltimaFechaVenta(variante.getUltimaFechaVenta());

        // Fetch detalles separately to avoid MultipleBagFetchException
        List<ProductoVarianteDetalle> detalles = productoVarianteDetalleRepository
                .findByProductoVariante_ProductoVarianteId(variante.getProductoVarianteId());

        List<CaracteristicaDetalleDTO> caracteristicasDTO = detalles.stream()
                .map(detalle -> {
                    CaracteristicaDetalleDTO caracteristicaDTO = new CaracteristicaDetalleDTO();
                    if (detalle.getCaracteristica() != null) {
                        caracteristicaDTO.setCaracteristicaId(detalle.getCaracteristica().getCaracteristicaId());
                        caracteristicaDTO.setNombre(detalle.getCaracteristica().getNombre());
                        if (detalle.getCaracteristica().getTipo() != null) {
                            TipoCaracteristicaDTO tipoDTO = new TipoCaracteristicaDTO();
                            tipoDTO.setTipoCaracteristicaId(detalle.getCaracteristica().getTipo().getTipoCaracteristicaId());
                            tipoDTO.setNombre(detalle.getCaracteristica().getTipo().getNombre());
                            caracteristicaDTO.setTipo(tipoDTO);
                        }
                    }
                    return caracteristicaDTO;
                })
                .collect(Collectors.toList());
        dto.setCaracteristicas(caracteristicasDTO);

        // Fetch existencias
        List<com.pazzioliweb.productosmodule.dtos.ExistenciasBodegaDTO> existenciasDTO = 
            existenciasRepository.listadoExistenciasNombreBodegaVariante(variante.getProductoVarianteId());
        dto.setExistencias(existenciasDTO);

        // Fetch precios
        List<com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteDTO> preciosDTO = 
            preciosProductoVarianteRepository.preciosPrpductoVariante(
                variante.getProductoVarianteId().intValue(), 
                org.springframework.data.domain.Pageable.unpaged()
            ).getContent();
        dto.setPrecios(preciosDTO);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> obtenerUltimoCodigoContable() {
        return productosRepository.findUltimoCodigoContable().stream().findFirst();
    }
}
