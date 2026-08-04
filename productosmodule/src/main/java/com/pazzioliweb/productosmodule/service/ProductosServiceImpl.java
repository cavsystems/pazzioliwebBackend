package com.pazzioliweb.productosmodule.service;

import java.util.List;
import java.util.Optional;
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
    
    /** Trabajo diferido: existencia a actualizar-o-crear una vez que la variante ya tiene ID. */
    private record PendienteExistencia(ProductoVariante variante, Bodegas bodega,
            ProductoActualizarCrearDTO.ExistenciaDTO existenciaDto, String ubicacionProducto) {
    }

    /** Trabajo diferido: precio de variante a actualizar-o-crear una vez que la variante ya tiene ID. */
    private record PendientePrecio(ProductoVariante variante,
            com.pazzioliweb.productosmodule.entity.Precios precio, ProductoActualizarCrearDTO.PrecioDTO precioDto) {
    }

    /** Código de barras "válido" = no nulo, no vacío y distinto de "0" (las plantillas de
     *  importación llegan con "0" cuando el producto no tiene código de barras). */
    private static boolean codigoBarraValido(String codigoBarraTrim) {
        return codigoBarraTrim != null && !codigoBarraTrim.isEmpty() && !"0".equals(codigoBarraTrim);
    }

    @Override
    @Transactional
    public void actualizarOCrearProducto(List<ProductoActualizarCrearDTO> dtos) {

        try {

            System.out.println("Tenant actual: " + TenantContext.getCurrentTenant()
                    + " — actualizarOCrearProducto lote de " + dtos.size() + " producto(s)");

            // ── Cachés por lote ──
            // En importaciones masivas (plantilla de 3000+ productos) casi todos los
            // productos comparten grupo/línea/impuesto/tipo/unidad: resolverlos con una
            // consulta POR PRODUCTO multiplicaba las queries (~6 × N). Se resuelven una
            // vez por valor distinto dentro del lote.
            java.util.Map<String, Grupos> cacheGrupos = new java.util.HashMap<>();
            java.util.Map<String, Lineas> cacheLineas = new java.util.HashMap<>();
            java.util.Map<Integer, Impuestos> cacheImpuestos = new java.util.HashMap<>();
            java.util.Map<String, TipoProducto> cacheTipos = new java.util.HashMap<>();
            java.util.Map<String, UnidadesMedida> cacheUnidades = new java.util.HashMap<>();
            java.util.Map<Integer, Bodegas> cacheBodegas = new java.util.HashMap<>();
            java.util.Map<Integer, com.pazzioliweb.productosmodule.entity.Precios> cachePrecios = new java.util.HashMap<>();
            Usuario usuarioDefault = usuarioRepository.findById(1).orElse(null);

            // ── Precarga EN BLOQUE de productos y variantes ──
            // Antes: 1 SELECT por producto (findByCodigoContable) + 1-2 SELECT por
            // variante (findByCodigoBarras / findByProducto_CodigoContableAndReferenciaVariantes
            // / findByProducto_ProductoId) DENTRO del bucle. Con un lote de 500-3000
            // productos eran miles de round-trips. Ahora se resuelve todo en 3 consultas
            // para el lote completo y se busca en memoria con Maps.
            java.util.List<String> codigosProductos = dtos.stream()
                    .map(ProductoActualizarCrearDTO::getCodigo)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            java.util.Map<String, Productos> productosPorCodigo = codigosProductos.isEmpty()
                    ? new java.util.HashMap<>()
                    : productosRepository.findByCodigoContableIn(codigosProductos).stream()
                            .collect(Collectors.toMap(Productos::getCodigoContable, p -> p, (a, b) -> a,
                                    java.util.HashMap::new));

            java.util.List<Integer> productoIdsExistentes = productosPorCodigo.values().stream()
                    .map(Productos::getProductoId)
                    .collect(Collectors.toList());
            // Variantes existentes agrupadas por producto (para el fallback por
            // referenciaVariantes y para el fallback de "variante única del producto").
            java.util.Map<Integer, java.util.List<ProductoVariante>> variantesPorProductoId = productoIdsExistentes
                    .isEmpty()
                            ? new java.util.HashMap<>()
                            : productoVarianteRepository.findByProducto_ProductoIdIn(productoIdsExistentes).stream()
                                    .collect(Collectors.groupingBy(v -> v.getProducto().getProductoId()));

            // Variantes por código de barras, EN CUALQUIER producto (la búsqueda original
            // es global: primero se busca por código de barras y luego se valida que no
            // pertenezca a otro producto del lote).
            java.util.Set<String> codigosBarrasVariantes = new java.util.HashSet<>();
            for (ProductoActualizarCrearDTO dto : dtos) {
                if (dto.getVariantes() == null) continue;
                for (ProductoActualizarCrearDTO.VarianteDTO v : dto.getVariantes()) {
                    String cb = v.getCodigoBarraVariante() != null ? v.getCodigoBarraVariante().trim() : "";
                    if (codigoBarraValido(cb)) {
                        codigosBarrasVariantes.add(cb);
                    }
                }
            }
            java.util.Map<String, ProductoVariante> variantesPorCodigoBarras = codigosBarrasVariantes.isEmpty()
                    ? new java.util.HashMap<>()
                    : productoVarianteRepository.findByCodigoBarrasIn(codigosBarrasVariantes).stream()
                            .collect(Collectors.toMap(ProductoVariante::getCodigoBarras, v -> v, (a, b) -> a,
                                    java.util.HashMap::new));

            // Trabajo diferido: existencias y precios de variante solo se pueden
            // resolver contra la BD después de que la variante tenga ID (recién
            // guardada). Se difieren aquí y se procesan en bloque al final del bucle
            // principal, con 2 consultas totales en vez de 1 por variante.
            java.util.List<PendienteExistencia> pendientesExistencias = new java.util.ArrayList<>();
            java.util.List<PendientePrecio> pendientesPrecios = new java.util.ArrayList<>();

        for (ProductoActualizarCrearDTO dto : dtos) {
        // Find or create product

        Productos producto = productosPorCodigo.get(dto.getCodigo());
        if (producto == null) {
            producto = new Productos();
        }

        // Set basic fields
        producto.setCodigoContable(dto.getCodigo());
        producto.setDescripcion(dto.getDescripcion());
        producto.setReferencia(dto.getReferencia());
        producto.setCosto(dto.getCosto().doubleValue());
        producto.setCodigoBarras(dto.getCodigoBarras());
        // Handle precios - TODO: implement logic for dto.getPrecios()
        producto.setEstado("Activo");

        // Set maneja variantes
        boolean tieneAtributoDescripcion = dto.getVariantes() != null && dto.getVariantes().stream()
            .anyMatch(v -> v.getAtributos() != null && v.getAtributos().stream()
                .anyMatch(a -> "descripcion".equalsIgnoreCase(a.getNombre())));
        producto.setManejaVariantes(dto.getVariantes() != null && !dto.getVariantes().isEmpty() && !tieneAtributoDescripcion);

        // Set relations (resueltas vía caché del lote)
        Grupos grupo = cacheGrupos.computeIfAbsent(dto.getGrupo(), g ->
                grupoRepository.findByDescripcion(g)
                        .orElseGet(() -> {
                            Grupos nuevoGrupo = new Grupos();
                            nuevoGrupo.setId(grupoRepository.findPrimerHueco());
                            nuevoGrupo.setDescripcion(g);
                            return grupoRepository.save(nuevoGrupo);
                        }));
        producto.setGrupo(grupo);

        Lineas linea = cacheLineas.computeIfAbsent(dto.getLinea(), l ->
                lineaRepository.findByDescripcion(l)
                        .orElseGet(() -> {
                            Lineas nuevaLinea = new Lineas();
                            nuevaLinea.setId(lineaRepository.findPrimerHueco());
                            nuevaLinea.setDescripcion(l);
                            return lineaRepository.save(nuevaLinea);
                        }));
        producto.setLinea(linea);

        Impuestos impuesto = cacheImpuestos.computeIfAbsent(dto.getImpuesto(), t -> impuestoRepository.findByTarifa(t)
                .orElseThrow(() -> new EntityNotFoundException("Impuesto no encontrado: " + t)));
        producto.setImpuestos(impuesto);

        TipoProducto tipoProducto = cacheTipos.computeIfAbsent(dto.getTipoProducto(), n -> tipoProductoRepository.findByNombre(n)
                .orElseGet(() -> {
                    TipoProducto nuevo = new TipoProducto();
                    nuevo.setNombre(n);
                    nuevo.setDescripcion(n);
                    nuevo.setEstado(true);
                    nuevo.setFechaCreacion(java.time.LocalDateTime.now());
                    return tipoProductoRepository.save(nuevo);
                }));
        producto.setTipoProducto(tipoProducto);

        // Assume default usuario, perhaps system
        producto.setUsuario(usuarioDefault);

        producto = productosRepository.save(producto);
        // Reflejar el producto recién guardado en la caché: si el mismo código
        // contable se repite más adelante en el lote (dato sucio de la plantilla),
        // debe reutilizarse este producto en vez de crear uno duplicado — igual que
        // hacía el findByCodigoContable original (que veía el save anterior gracias
        // al auto-flush de Hibernate).
        productosPorCodigo.put(producto.getCodigoContable(), producto);
        // Variantes ya conocidas de este producto (precargadas o creadas más arriba
        // en este mismo lote): se resuelven en memoria más abajo en vez de consultar
        // la BD por cada variante del DTO.
        java.util.List<ProductoVariante> variantesDeProducto = variantesPorProductoId
                .computeIfAbsent(producto.getProductoId(), k -> new java.util.ArrayList<>());
        // Handle unidad medida
        if (dto.getUnidadMedida() != null) {
            UnidadesMedida unidadesMedida = cacheUnidades.computeIfAbsent(dto.getUnidadMedida(), s ->
                    unidadesMedidaRepository.findBySigla(s)
                            .orElseThrow(() -> new EntityNotFoundException("UnidadMedida no encontrada: " + s)));
            UnidadesMedidaProducto ump = new UnidadesMedidaProducto();
            UnidadesMedidaProductoId umpId = new UnidadesMedidaProductoId();
            umpId.setProductoId(producto.getProductoId());
            umpId.setUnidadMedidaId(unidadesMedida.getUnidadMedidaId()); // Asumiendo que necesita ID
            ump.setId(umpId);
            ump.setProducto(producto);
            ump.setUnidadMedida(unidadesMedida);
            unidadesMedidaProductoRepository.save(ump);
        }

        // Handle variants
        if (dto.getVariantes() != null && !dto.getVariantes().isEmpty()) {
            for (ProductoActualizarCrearDTO.VarianteDTO varianteDto : dto.getVariantes()) {
                // Código de barras VÁLIDO = no nulo, no vacío y distinto de "0" (las plantillas
                // de importación llegan con "0" cuando el producto no tiene código de barras).
                String codBarraVar = varianteDto.getCodigoBarraVariante() != null
                        ? varianteDto.getCodigoBarraVariante().trim() : "";
                boolean codBarraValido = !codBarraVar.isEmpty() && !"0".equals(codBarraVar);

                // Resolución EN MEMORIA (sin consultas a BD): usa los Maps precargados
                // antes del bucle (variantesPorCodigoBarras / variantesPorProductoId), que
                // se van actualizando a medida que se crean/guardan variantes en este mismo
                // lote — equivalente a lo que antes lograba el auto-flush de Hibernate con
                // consultas repetidas.
                ProductoVariante variante = null;
                if (codBarraValido) {
                    variante = variantesPorCodigoBarras.get(codBarraVar);
                    // NUNCA reasignar la variante de OTRO producto. Con códigos vacíos/"0"
                    // repetidos, la búsqueda global encontraba la variante del producto anterior
                    // del lote y se la "robaba": una importación de 3000+ productos terminaba con
                    // UNA sola variante (la del último) y 3000 productos sin variantes ni kardex.
                    if (variante != null && variante.getProducto() != null
                            && variante.getProducto().getProductoId() != null
                            && !variante.getProducto().getProductoId().equals(producto.getProductoId())) {
                        variante = null;
                    }
                } else if (varianteDto.getReferenciaVariantes() != null && !varianteDto.getReferenciaVariantes().isBlank()) {
                    // Sin código de barras: resolver DENTRO del mismo producto por referencia
                    final String referenciaBuscada = varianteDto.getReferenciaVariantes();
                    variante = variantesDeProducto.stream()
                            .filter(v -> referenciaBuscada.equals(v.getReferenciaVariantes()))
                            .findFirst()
                            .orElse(null);
                }
                // Producto de UNA sola variante en el DTO que no se resolvió arriba:
                // reutilizar la única variante existente del producto (predeterminada o no),
                // siempre que no tenga OTRO código de barras distinto. Evita crear variantes
                // duplicadas del mismo producto (caso TUR014: la variante existente no estaba
                // marcada como predeterminada y una reimportación con código de barras nuevo
                // creaba una segunda variante con la misma referencia → el kardex reventaba
                // con "query did not return a unique result").
                if (variante == null && dto.getVariantes().size() == 1 && producto.getProductoId() != null) {
                    if (variantesDeProducto.size() == 1) {
                        ProductoVariante unica = variantesDeProducto.get(0);
                        boolean sinCodigo = unica.getCodigoBarras() == null || unica.getCodigoBarras().isBlank();
                        if (sinCodigo || !codBarraValido || codBarraVar.equals(unica.getCodigoBarras())) {
                            variante = unica;
                        }
                    }
                }
                if (variante == null) {
                    variante = new ProductoVariante();
                }
                // ¿Es una variante nueva o una ya existente?
                boolean esNueva = variante.getProductoVarianteId() == null;
                // Código de barras ANTES de esta actualización: si cambia, hay que
                // desregistrarlo del Map global para que no quede una entrada apuntando
                // a un valor que la variante ya no tiene (mismo efecto que tendría un
                // SELECT fresco a la BD tras el UPDATE).
                String codigoBarrasAnterior = variante.getCodigoBarras();

                variante.setProducto(producto);
                // SKU: NO degradar el SKU de una variante existente usando el código de barras como fallback.
                // La variante por defecto (producto sin variantes) guarda sku = código contable; la compra
                // resuelve el kardex por ese SKU. Pisarlo con un código de barras rompía la actualización de
                // existencias (bug #9) y dejaba variantes descuadradas.
                if (varianteDto.getSku() != null) {
                    variante.setSku(varianteDto.getSku());
                } else if (esNueva) {
                    // Sin código de barras válido, el SKU es el código contable: así el kardex
                    // resuelve la variante por su fallback findBySku(codigoProducto).
                    variante.setSku(codBarraValido ? codBarraVar : dto.getCodigo());
                }
                variante.setCodigoBarras(codBarraValido ? codBarraVar : null);
                // Referencia: idem, no pisar la referencia existente con el código de barras.
                if (varianteDto.getReferenciaVariantes() != null) {
                    variante.setReferenciaVariantes(varianteDto.getReferenciaVariantes());
                } else if (esNueva) {
                    variante.setReferenciaVariantes(codBarraValido ? codBarraVar
                            : (dto.getReferencia() != null && !dto.getReferencia().isBlank()
                                ? dto.getReferencia() : dto.getCodigo()));
                }
                variante.setActivo(true);
                // Predeterminada: honrar el flag del DTO; si no viene, conservar el valor actual.
                // Antes se forzaba SIEMPRE a false, degradando la variante por defecto (predeterminada=true)
                // en el flujo de compras → la vista concatenaba "descripcion-descripcion" (bug #8).
                if (varianteDto.getPredeterminada() != null) {
                    variante.setPredeterminada(varianteDto.getPredeterminada());
                } else if (esNueva) {
                    // Producto de una sola variante sin código de barras = su variante por defecto
                    variante.setPredeterminada(!codBarraValido && dto.getVariantes().size() == 1);
                }

                variante = productoVarianteRepository.save(variante);

                // Mantener los Maps del lote sincronizados con la BD para que las
                // siguientes iteraciones (mismo lote) resuelvan correctamente en memoria.
                if (esNueva) {
                    variantesDeProducto.add(variante);
                }
                if (codigoBarrasAnterior != null && !codigoBarrasAnterior.equals(variante.getCodigoBarras())
                        && variantesPorCodigoBarras.get(codigoBarrasAnterior) == variante) {
                    variantesPorCodigoBarras.remove(codigoBarrasAnterior);
                }
                if (codBarraValido) {
                    variantesPorCodigoBarras.put(codBarraVar, variante);
                }

                // Handle existencias - Solo para la bodega destino
                // (diferido: se resuelve en bloque después del bucle principal, una vez
                // que todas las variantes del lote ya tienen ID — ver pendientesExistencias)
                if (varianteDto.getExistencias() != null && !varianteDto.getExistencias().isEmpty()) {
                    // Asume que el bodegaId es el mismo en todas las existencias (la destino)
                    Integer bodegaDestinoId = varianteDto.getExistencias().get(0).getBodegaId();
                    Bodegas bodegaDestino = cacheBodegas.computeIfAbsent(bodegaDestinoId, b ->
                            bodegasRepository.findByCodigo(b)
                                    .orElseThrow(() -> new EntityNotFoundException("Bodega destino no encontrada: " + b)));

                    ProductoActualizarCrearDTO.ExistenciaDTO existenciaDto = varianteDto.getExistencias().get(0);
                    pendientesExistencias.add(new PendienteExistencia(variante, bodegaDestino, existenciaDto, dto.getUbicacion()));
                } else {
                    // No crear existencia por defecto, se creará en el ingreso con 0
                }

                // Handle precios
                // (diferido: se resuelve en bloque después del bucle principal — ver pendientesPrecios)
                if (varianteDto.getPrecios() != null && !varianteDto.getPrecios().isEmpty()) {
                    for (ProductoActualizarCrearDTO.PrecioDTO precioDto : varianteDto.getPrecios()) {
                        com.pazzioliweb.productosmodule.entity.Precios precio = cachePrecios.computeIfAbsent(precioDto.getIdTipoPrecio(), id ->
                                preciosRepository.findById(id)
                                        .orElseThrow(() -> new EntityNotFoundException("Precio no encontrado: " + id)));

                        pendientesPrecios.add(new PendientePrecio(variante, precio, precioDto));
                    }
                }

                // Handle attributes
                if (varianteDto.getAtributos() != null) {
                    for (ProductoActualizarCrearDTO.AtributoDTO attrDto : varianteDto.getAtributos()) {
                        // Skip if attribute name is "descripcion"
                        if ("descripcion".equalsIgnoreCase(attrDto.getNombre())) {
                            continue;
                        }

                        TipoCaracteristica tipo = tipoCaracteristicaRepository.findByNombre(attrDto.getNombre())
                                .orElse(null); // Or throw if needed

                        Caracteristica caracteristica = caracteristicaRepository.findByNombre(attrDto.getValor())
                                .orElse(new Caracteristica());

                        caracteristica.setNombre(attrDto.getValor());
                        caracteristica.setTipo(tipo);

                        caracteristica = caracteristicaRepository.save(caracteristica);

                        ProductoVarianteDetalle detalle = productoVarianteDetalleRepository.findByProductoVarianteAndCaracteristica(variante, caracteristica)
                                .orElse(new ProductoVarianteDetalle());

                        detalle.setProductoVariante(variante);
                        detalle.setCaracteristica(caracteristica);

                        productoVarianteDetalleRepository.save(detalle);
                    }
                }
            }

        } else {
            // Create default variant (resuelta en memoria con la lista de variantes ya
            // conocidas del producto, en vez de un findByProductoAndPredeterminada por DTO)
            ProductoVariante variante = variantesDeProducto.stream()
                    .filter(v -> Boolean.TRUE.equals(v.getPredeterminada()))
                    .findFirst()
                    .orElse(new ProductoVariante());
            boolean eraNueva = variante.getProductoVarianteId() == null;
            String codigoBarrasAnteriorDefault = variante.getCodigoBarras();

            variante.setProducto(producto);
            variante.setSku(dto.getCodigo());
            variante.setCodigoBarras(dto.getCodigoBarras());
            variante.setReferenciaVariantes(dto.getReferencia());
            variante.setActivo(true);
            variante.setPredeterminada(true);

            variante = productoVarianteRepository.save(variante);

            if (eraNueva) {
                variantesDeProducto.add(variante);
            }
            String cbAnteriorTrim = codigoBarrasAnteriorDefault != null ? codigoBarrasAnteriorDefault.trim() : "";
            if (codigoBarraValido(cbAnteriorTrim) && !cbAnteriorTrim.equals(variante.getCodigoBarras())
                    && variantesPorCodigoBarras.get(cbAnteriorTrim) == variante) {
                variantesPorCodigoBarras.remove(cbAnteriorTrim);
            }
            String cbNuevoTrim = variante.getCodigoBarras() != null ? variante.getCodigoBarras().trim() : "";
            if (codigoBarraValido(cbNuevoTrim)) {
                variantesPorCodigoBarras.put(cbNuevoTrim, variante);
            }
        }

    }

            // ── Resolución EN BLOQUE de existencias y precios diferidos ──
            // Antes: 1 SELECT por (variante, bodega) + 1 SELECT por (variante, tipoPrecio)
            // DENTRO del bucle de variantes. Ahora, con todas las variantes del lote ya
            // guardadas (tienen ID), se resuelve en 2 consultas totales para todo el lote.
            java.util.Set<Long> varianteIdsTocados = new java.util.HashSet<>();
            for (PendienteExistencia pe : pendientesExistencias) {
                if (pe.variante().getProductoVarianteId() != null) {
                    varianteIdsTocados.add(pe.variante().getProductoVarianteId());
                }
            }
            for (PendientePrecio pp : pendientesPrecios) {
                if (pp.variante().getProductoVarianteId() != null) {
                    varianteIdsTocados.add(pp.variante().getProductoVarianteId());
                }
            }

            java.util.Map<String, Existencias> existenciasPorClave = varianteIdsTocados.isEmpty()
                    ? new java.util.HashMap<>()
                    : existenciasRepository.findByProductoVariante_ProductoVarianteIdIn(varianteIdsTocados).stream()
                            .collect(Collectors.toMap(
                                    e -> e.getProductoVariante().getProductoVarianteId() + "|" + e.getBodega().getCodigo(),
                                    e -> e, (a, b) -> a, java.util.HashMap::new));

            java.util.Map<String, PreciosProductoVariante> preciosPorClave = varianteIdsTocados.isEmpty()
                    ? new java.util.HashMap<>()
                    : preciosProductoVarianteRepository.findByProductoVariante_ProductoVarianteIdIn(varianteIdsTocados).stream()
                            .collect(Collectors.toMap(
                                    p -> p.getProductoVariante().getProductoVarianteId() + "|" + p.getPrecio().getPrecioId(),
                                    p -> p, (a, b) -> a, java.util.HashMap::new));

            for (PendienteExistencia pe : pendientesExistencias) {
                String clave = pe.variante().getProductoVarianteId() + "|" + pe.bodega().getCodigo();
                Existencias existencia = existenciasPorClave.get(clave);
                if (existencia == null) {
                    existencia = new Existencias();
                }
                existencia.setBodega(pe.bodega());
                existencia.setProductoVariante(pe.variante());
                // No setear existencia aquí, ya que la mercancía no ha llegado peros si se crea el registro en
                // caso de que no exista si existe no hace nada.
                if (pe.existenciaDto().getMinimo() != null) {
                    existencia.setStockMin(BigDecimal.valueOf(pe.existenciaDto().getMinimo()));
                }
                if (pe.existenciaDto().getMaximo() != null) {
                    existencia.setStockMax(BigDecimal.valueOf(pe.existenciaDto().getMaximo()));
                }
                // Opcional: Si quieres setear ubicación desde alguna fuente (ej. del DTO principal), agrégalo aquí
                if (pe.ubicacionProducto() != null) {
                    existencia.setUbicacion(pe.ubicacionProducto());
                }
                if (existencia.getExistenciaId() == null) {
                    existencia.setExistencia(BigDecimal.ZERO); // Inicializar en 0
                    existencia.setFechaUltimoMovimiento(LocalDateTime.now());
                    // Registrar en el mapa por si el mismo lote referencia la misma
                    // variante+bodega más de una vez (evita crear duplicados).
                    existenciasPorClave.put(clave, existencia);
                }
                existenciasRepository.save(existencia);
            }

            for (PendientePrecio pp : pendientesPrecios) {
                String clave = pp.variante().getProductoVarianteId() + "|" + pp.precio().getPrecioId();
                PreciosProductoVariante ppv = preciosPorClave.get(clave);
                if (ppv != null) {
                    // Ya existe: solo actualiza el valor
                    ppv.setValor(pp.precioDto().getValor().doubleValue());
                } else {
                    // No existe: crea uno nuevo
                    ppv = new PreciosProductoVariante();
                    ppv.setProductoVariante(pp.variante());
                    ppv.setPrecio(pp.precio());
                    ppv.setValor(pp.precioDto().getValor().doubleValue());
                    ppv.setFechaCreacion(LocalDateTime.now());
                    ppv.setFechaInicio(LocalDateTime.now());
                    preciosPorClave.put(clave, ppv);
                }
                preciosProductoVarianteRepository.save(ppv);
            }

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

        // ── Precarga EN BLOQUE ──
        // Antes se hacían ~4 consultas POR producto/variante (unidad de medida,
        // características, existencias y precios): con 3.500 productos eran ~14.000
        // consultas y la vista de productos tardaba minutos en cargar. Ahora todo
        // se trae en 4 consultas masivas y se arma en memoria.
        List<Long> varianteIds = productos.stream()
                .flatMap(p -> p.getVariantes().stream())
                .map(ProductoVariante::getProductoVarianteId)
                .collect(Collectors.toList());
        List<Integer> productoIds = productos.stream()
                .map(Productos::getProductoId)
                .collect(Collectors.toList());

        java.util.Map<Long, List<ProductoVarianteDetalle>> detallesPorVariante = varianteIds.isEmpty()
                ? java.util.Collections.emptyMap()
                : productoVarianteDetalleRepository.findByProductoVariante_ProductoVarianteIdIn(varianteIds).stream()
                        .collect(Collectors.groupingBy(d -> d.getProductoVariante().getProductoVarianteId()));

        java.util.Map<Long, List<com.pazzioliweb.productosmodule.dtos.ExistenciasBodegaDTO>> existenciasPorVariante = varianteIds.isEmpty()
                ? java.util.Collections.emptyMap()
                : existenciasRepository.listadoExistenciasNombreBodegaVariantes(varianteIds).stream()
                        .collect(Collectors.groupingBy(e -> e.getProductoVarianteId().longValue()));

        java.util.Map<Long, List<com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteDTO>> preciosPorVariante = varianteIds.isEmpty()
                ? java.util.Collections.emptyMap()
                : preciosProductoVarianteRepository.preciosProductoVarianteMultiple(
                                varianteIds.stream().map(Long::intValue).collect(Collectors.toList()),
                                org.springframework.data.domain.Pageable.unpaged())
                        .getContent().stream()
                        .collect(Collectors.groupingBy(p -> p.getProductoVarianteId().longValue()));

        java.util.Map<Integer, String> unidadPorProducto = new java.util.HashMap<>();
        if (!productoIds.isEmpty()) {
            for (UnidadesMedidaProducto ump : unidadesMedidaProductoRepository.findByProducto_ProductoIdIn(productoIds)) {
                if (ump.getUnidadMedida() != null && ump.getUnidadMedida().getSigla() != null) {
                    unidadPorProducto.putIfAbsent(ump.getProducto().getProductoId(), ump.getUnidadMedida().getSigla());
                }
            }
        }

        return productos.stream()
                .map(p -> convertirAProductoConVariantesDTO(p, detallesPorVariante, existenciasPorVariante,
                        preciosPorVariante, unidadPorProducto))
                .collect(Collectors.toList());
    }

    /** Conversión con datos PRE-CARGADOS en bloque (sin consultas por producto/variante). */
    private ProductoConVariantesDTO convertirAProductoConVariantesDTO(
            Productos producto,
            java.util.Map<Long, List<ProductoVarianteDetalle>> detallesPorVariante,
            java.util.Map<Long, List<com.pazzioliweb.productosmodule.dtos.ExistenciasBodegaDTO>> existenciasPorVariante,
            java.util.Map<Long, List<com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteDTO>> preciosPorVariante,
            java.util.Map<Integer, String> unidadPorProducto) {
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
        dto.setUnidadMedida(unidadPorProducto.get(producto.getProductoId()));
        dto.setVariantes(producto.getVariantes().stream()
                .map(v -> convertirAVarianteDTO(v, detallesPorVariante, existenciasPorVariante, preciosPorVariante))
                .collect(Collectors.toList()));
        return dto;
    }

    /** Conversión de variante con datos PRE-CARGADOS (sin consultas por variante). */
    private VarianteDTO convertirAVarianteDTO(
            ProductoVariante variante,
            java.util.Map<Long, List<ProductoVarianteDetalle>> detallesPorVariante,
            java.util.Map<Long, List<com.pazzioliweb.productosmodule.dtos.ExistenciasBodegaDTO>> existenciasPorVariante,
            java.util.Map<Long, List<com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteDTO>> preciosPorVariante) {
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

        Long id = variante.getProductoVarianteId();
        dto.setCaracteristicas(detallesPorVariante.getOrDefault(id, java.util.Collections.emptyList()).stream()
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
                .collect(Collectors.toList()));
        dto.setExistencias(existenciasPorVariante.getOrDefault(id, java.util.Collections.emptyList()));
        dto.setPrecios(preciosPorVariante.getOrDefault(id, java.util.Collections.emptyList()));
        return dto;
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
