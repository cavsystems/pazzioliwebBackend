package com.pazzioliweb.productosmodule.service;

import java.util.List;
import java.util.Optional;
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

             if (dto.getImagen() != null) {
                 existente.setImagen(dto.getImagen());
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

        try {

            System.out.println("Tenant actual: " + TenantContext.getCurrentTenant());
        for (ProductoActualizarCrearDTO dto : dtos) {
        // Find or create product

        Productos producto = productosRepository.findByCodigoContable(dto.getCodigo())
                .orElse(new Productos());

        // Set basic fields
        producto.setCodigoContable(dto.getCodigo());
        producto.setDescripcion(dto.getDescripcion());
        producto.setReferencia(dto.getReferencia());
        producto.setCosto(dto.getCosto().doubleValue());
        producto.setCodigoBarras(dto.getCodigoBarras());
        // Handle precios - TODO: implement logic for dto.getPrecios()
        producto.setEstado("Activo");

        // Set maneja variantes
        producto.setManejaVariantes(dto.getVariantes() != null && !dto.getVariantes().isEmpty());

        // Set relations
        Grupos grupo = grupoRepository.findByDescripcion(dto.getGrupo())
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado: " + dto.getGrupo()));
        producto.setGrupo(grupo);

        Lineas linea = lineaRepository.findByDescripcion(dto.getLinea())
                .orElseThrow(() -> new EntityNotFoundException("Linea no encontrada: " + dto.getLinea()));
        producto.setLinea(linea);

        Impuestos impuesto = impuestoRepository.findByTarifa(dto.getImpuesto())
                .orElseThrow(() -> new EntityNotFoundException("Impuesto no encontrado: " + dto.getImpuesto()));
        producto.setImpuestos(impuesto);

        TipoProducto tipoProducto = tipoProductoRepository.findByNombre(dto.getTipoProducto())
                .orElseThrow(() -> new EntityNotFoundException("TipoProducto no encontrado: " + dto.getTipoProducto()));
        producto.setTipoProducto(tipoProducto);

        // Assume default usuario, perhaps system
        Usuario usuario = usuarioRepository.findById(1).orElse(null); // Default
        producto.setUsuario(usuario);

        producto = productosRepository.save(producto);
            System.out.println("Producto actulizado");
        // Handle unidad medida
        if (dto.getUnidadMedida() != null) {
            UnidadesMedida unidadesMedida = unidadesMedidaRepository.findBySigla(dto.getUnidadMedida())
                    .orElseThrow(() -> new EntityNotFoundException("UnidadMedida no encontrada: " + dto.getUnidadMedida()));
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
                System.out.println("Producto actulizado VARIANTES" +varianteDto.getCodigoBarraVariante());
                ProductoVariante variante = productoVarianteRepository.findByCodigoBarras(varianteDto.getCodigoBarraVariante())
                        .orElse(new ProductoVariante());

                variante.setProducto(producto);
                variante.setSku(varianteDto.getSku() != null ? varianteDto.getSku() : varianteDto.getCodigoBarraVariante());
                variante.setCodigoBarras(varianteDto.getCodigoBarraVariante());
                variante.setReferenciaVariantes(varianteDto.getReferenciaVariantes() != null ? varianteDto.getReferenciaVariantes() : varianteDto.getCodigoBarraVariante());
                variante.setActivo(true);
                variante.setPredeterminada(false); // Set based on logic, for now false

                variante = productoVarianteRepository.save(variante);

                // Handle existencias - Solo para la bodega destino
                if (varianteDto.getExistencias() != null && !varianteDto.getExistencias().isEmpty()) {
                    // Asume que el bodegaId es el mismo en todas las existencias (la destino)
                    Integer bodegaDestinoId = varianteDto.getExistencias().get(0).getBodegaId();
                    Bodegas bodegaDestino = bodegasRepository.findByCodigo(bodegaDestinoId)
                            .orElseThrow(() -> new EntityNotFoundException("Bodega destino no encontrada: " + bodegaDestinoId));

                    Existencias existencia = existenciasRepository.findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(variante.getProductoVarianteId(), bodegaDestino.getCodigo())
                            .orElse(new Existencias());
                    System.out.println("Producto actulizado VARIANTES procesada existencia"+variante.getProductoVarianteId()+bodegaDestino.getCodigo());
                    existencia.setBodega(bodegaDestino);
                    existencia.setProductoVariante(variante);
                    // No setear existencia aquí, ya que la mercancía no ha llegado peros si se crea el registro en
                    // caso de que no exista si existe no hace nada.
                    ProductoActualizarCrearDTO.ExistenciaDTO existenciaDto = varianteDto.getExistencias().get(0);
                    if (existenciaDto.getMinimo() != null) {
                        existencia.setStockMin(BigDecimal.valueOf(existenciaDto.getMinimo()));
                    }
                    if (existenciaDto.getMaximo() != null) {
                        existencia.setStockMax(BigDecimal.valueOf(existenciaDto.getMaximo()));
                    }
                    // Opcional: Si quieres setear ubicación desde alguna fuente (ej. del DTO principal), agrégalo aquí
                    if (dto.getUbicacion() != null) {
                        existencia.setUbicacion(dto.getUbicacion());
                    }
                    if (existencia.getExistenciaId() == null) {
                        existencia.setExistencia(BigDecimal.ZERO); // Inicializar en 0
                        existencia.setFechaUltimoMovimiento(LocalDateTime.now());
                    }
                    existenciasRepository.save(existencia);
                } else {
                    // No crear existencia por defecto, se creará en el ingreso con 0
                }

                // Handle precios
                if (varianteDto.getPrecios() != null && !varianteDto.getPrecios().isEmpty()) {
                    for (ProductoActualizarCrearDTO.PrecioDTO precioDto : varianteDto.getPrecios()) {
                        com.pazzioliweb.productosmodule.entity.Precios precio = preciosRepository.findById(precioDto.getIdTipoPrecio())
                                .orElseThrow(() -> new EntityNotFoundException("Precio no encontrado: " + precioDto.getIdTipoPrecio()));

                        Optional<PreciosProductoVariante> existente =
                                preciosProductoVarianteRepository.findByProductoVariante_ProductoVarianteIdAndPrecio_PrecioId(
                                        variante.getProductoVarianteId(), precio.getPrecioId());

                        PreciosProductoVariante ppv;
                        if (existente.isPresent()) {
                            // Ya existe: solo actualiza el valor
                            ppv = existente.get();
                            ppv.setValor(precioDto.getValor().doubleValue());
                        } else {
                            // No existe: crea uno nuevo
                            ppv = new PreciosProductoVariante();
                            ppv.setProductoVariante(variante);
                            ppv.setPrecio(precio);
                            ppv.setValor(precioDto.getValor().doubleValue());
                            ppv.setFechaCreacion(LocalDateTime.now());
                            ppv.setFechaInicio(LocalDateTime.now());
                        }
                        preciosProductoVarianteRepository.save(ppv);
                    }
                }

                // Handle attributes
                if (varianteDto.getAtributos() != null) {
                    for (ProductoActualizarCrearDTO.AtributoDTO attrDto : varianteDto.getAtributos()) {
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
            // Create default variant
            ProductoVariante variante = productoVarianteRepository.findByProductoAndPredeterminada(producto, true)
                    .orElse(new ProductoVariante());

            variante.setProducto(producto);
            variante.setSku(dto.getCodigo());
            variante.setCodigoBarras(dto.getCodigoBarras());
            variante.setReferenciaVariantes(dto.getReferencia());
            variante.setActivo(true);
            variante.setPredeterminada(true);

            productoVarianteRepository.save(variante);
        }

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
}
