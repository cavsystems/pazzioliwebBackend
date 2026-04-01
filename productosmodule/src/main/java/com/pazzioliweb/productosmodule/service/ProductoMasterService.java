package com.pazzioliweb.productosmodule.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.productosmodule.dtos.ExistenciasCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ExistenciasUpdateDTO;
import com.pazzioliweb.productosmodule.dtos.PreciosProductoVarianteCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoUpdateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteDetalleUpdateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteMasterDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteMasterUpdateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteResponseDTO;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.Existencias;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.entity.Productos;
import com.pazzioliweb.productosmodule.entity.UnidadesMedida;
import com.pazzioliweb.productosmodule.entity.UnidadesMedidaProducto;
import com.pazzioliweb.productosmodule.entity.UnidadesMedidaProductoId;
import com.pazzioliweb.productosmodule.repositori.ExistenciasRepository;
import com.pazzioliweb.productosmodule.repositori.UnidadesMedidaProductoRepository;
import com.pazzioliweb.productosmodule.repositori.UnidadesMedidaRepository;

import jakarta.persistence.EntityNotFoundException;


@Service
public class ProductoMasterService {

    private final ProductosService productosService;
    private final ProductoVarianteService productoVarianteService;
    private final ProductoVarianteDetalleService detalleService;
    private final PreciosProductoVarianteService preciosService;
    private final ExistenciasService existenciasService;
    private final UnidadesMedidaRepository unidadMedidaRepository;
    private final UnidadesMedidaProductoRepository unidadesMedidaProductoRepository;
    private final ExistenciasRepository existenciasRepository;

    public ProductoMasterService(
            ProductosService productosService,
            ProductoVarianteService productoVarianteService,
            ProductoVarianteDetalleService detalleService,
            PreciosProductoVarianteService preciosService,
            ExistenciasService existenciasService,
            UnidadesMedidaRepository unidadMedidaRepository,
            UnidadesMedidaProductoRepository unidadesMedidaProductoRepository,
            ExistenciasRepository existenciasRepository
    ) {
        this.productosService = productosService;
        this.productoVarianteService = productoVarianteService;
        this.detalleService = detalleService;
        this.preciosService = preciosService;
        this.existenciasService = existenciasService;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.unidadesMedidaProductoRepository = unidadesMedidaProductoRepository;
        this.existenciasRepository = existenciasRepository;
    }

    @Transactional
    public Productos crearProductoMaster(ProductoCreateDTO productoDTO,
                                         List<ProductoVarianteMasterDTO> variantesDTO) {

        // 1. Crear el producto
        Productos producto = productosService.guardarDesdeDTO(productoDTO);
        
        // 2. Crea Unidad medida Producto
        asignarUnidadesMedida(producto, productoDTO.getUnidadesMedida());
        
        boolean manejarVariantes = productoDTO.getManejaVariantes();// respondería true
        
        // 3. Por cada variante en el JSON
        for (ProductoVarianteMasterDTO masterDTO : variantesDTO) {
        	
            // 🔹 A. Setear productoId en variante
            ProductoVarianteCreateDTO varianteDTO = masterDTO.getVariante();
            varianteDTO.setProductoId(producto.getProductoId());

            // 4. Crear la variante
            ProductoVarianteResponseDTO variante = productoVarianteService.crear(varianteDTO);

            Long varianteId = variante.getProductoVarianteId(); // IMPORTANTE

            // 🔹 B. Crear detalles
            if(manejarVariantes) {
            	for (ProductoVarianteDetalleCreateDTO d : masterDTO.getDetalles()) {
                    d.setProductoVarianteId(varianteId);
                }
                detalleService.crear(masterDTO.getDetalles());
            }

            // 🔹 C. Crear existencias
            for (ExistenciasCreateDTO e : masterDTO.getExistencias()) {
                e.setProductoVarianteId(varianteId);
            }
            existenciasService.crear(masterDTO.getExistencias());

            // 🔹 D. Crear precios
            for (PreciosProductoVarianteCreateDTO p : masterDTO.getPrecios()) {
                p.setProductoVarianteId(varianteId);
            }
            preciosService.crear(masterDTO.getPrecios());
        }
        
        return producto;
    }
    
    private void asignarUnidadesMedida(Productos producto, List<Integer> unidadesIds) {

        if (unidadesIds == null || unidadesIds.isEmpty()) {
            return;
        }

        List<UnidadesMedidaProducto> relaciones = unidadesIds.stream()
            .map(unidadId -> {

                UnidadesMedida unidad = unidadMedidaRepository.findById(unidadId)
                        .orElseThrow(() -> new RuntimeException("Unidad de medida no encontrada: " + unidadId));

                UnidadesMedidaProducto rel = new UnidadesMedidaProducto();

                UnidadesMedidaProductoId id = new UnidadesMedidaProductoId(
                        producto.getProductoId(),
                        unidadId
                );

                rel.setId(id);
                rel.setProducto(producto);
                rel.setUnidadMedida(unidad);

                return rel;
            })
            .collect(Collectors.toList());

        unidadesMedidaProductoRepository.saveAll(relaciones);
    }
    
    @Transactional
    public void eliminarProductoMaster(Integer productoId) {

        // 1. Eliminar las relaciones Producto ↔ UnidadMedida (no tiene cascade)
        List<UnidadesMedidaProducto> relaciones = unidadesMedidaProductoRepository.findByProducto_ProductoId(productoId);
        if (!relaciones.isEmpty()) {
            unidadesMedidaProductoRepository.deleteAll(relaciones);
            System.out.println("Se eliminaron " + relaciones.size() + " relaciones UnidadMedida-Producto.");
        }

        // 2. Obtener el producto completo
        Productos producto = productosService.buscarPorId(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // 3. Antes de eliminar, mostrar cuántas variantes tiene
        int numVariantes = producto.getVariantes() != null ? producto.getVariantes().size() : 0;
        System.out.println("Se eliminarán " + numVariantes + " variantes asociadas al producto.");

        // 4. Eliminar el producto → cascada se encargará de variantes, detalles, existencias y precios
        productosService.eliminar(producto.getProductoId());

        System.out.println("Producto con ID " + productoId + " eliminado correctamente.");
    }
    
    @Transactional
    public Productos actualizarProductoMaster(Integer productoId,
                                              ProductoUpdateDTO productoDTO,
                                              List<ProductoVarianteMasterUpdateDTO> variantesDTO) {

        // 1. Obtener el producto
        Productos producto = productosService.buscarPorId(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // 2. Actualizar datos básicos
        productosService.actualizarDesdeDTO(producto.getProductoId(), productoDTO);

        // 3. Actualizar o asignar unidad de medida
        actualizarUnidadMedida(producto, productoDTO.getUnidadesMedida());

        boolean manejarVariantes = productoDTO.getManejaVariantes();// respondería true
        
        // 4. Manejar variantes -----------------------------------------------
        for (ProductoVarianteMasterUpdateDTO master : variantesDTO) {

            Long varianteId = master.getProductoVarianteId();
            System.out.println( "producto variante id"+master);
            System.out.println( "producto variante id"+master);
            ProductoVarianteResponseDTO varianteResp;

            // -------- VARIANTE EXISTENTE --------
            if (varianteId != null) {
                varianteResp = productoVarianteService.actualizar(
                        varianteId,
                        master.getVariante()
                );
            } else {
                ProductoVarianteCreateDTO createDTO = master.getVarianteCreate();
                System.out.println( "producto variante nuevo"+master.getVarianteCreate());
                createDTO.setProductoId(productoId);

                varianteResp = productoVarianteService.crear(createDTO);
                varianteId = varianteResp.getProductoVarianteId();
            }

           if(master.getVariante().getEstadovariante()) {
            // --- DETALLES ----------------------------------------------------
            if(manejarVariantes) {
            	// Crear nuevos detalles
                if (master.getDetalles() != null) {
                    for (ProductoVarianteDetalleCreateDTO d : master.getDetalles()) {
                        d.setProductoVarianteId(varianteId);
                    }
                   
                    detalleService.crear(master.getDetalles());
                }

                // Actualizar detalles existentes
                if (master.getDetallesUpdate() != null) {
                    for (ProductoVarianteDetalleUpdateDTO d : master.getDetallesUpdate()) {
                        d.setProductoVarianteId(varianteId);
                        detalleService.actualizarDesdeDTO(d.getProductoVariantesDetalleId(), d);
                    }
                }
            }

            // --- PRECIOS ----------------------------------------------------

            // Crear nuevos precios
            if (master.getPrecios() != null) {
                for (PreciosProductoVarianteCreateDTO p : master.getPrecios()) {
                    p.setProductoVarianteId(varianteId);
                }
                preciosService.crear(master.getPrecios());
            }

            // Actualizar precios existentes
            if (master.getPreciosUpdate() != null) {
                preciosService.actualizar(master.getPreciosUpdate());
            }

            // 🚫 Existencias NO se actualizan aquí
            
            // --- ACTUALIZAR EXISTENCIAS ---
            for (ExistenciasUpdateDTO exDTO : master.getExistencias()) {

            	// Buscar existencia por variante + bodega
                Existencias existencia = existenciasRepository
                        .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(varianteId, exDTO.getBodegaId())
                        .orElse(null);

                if (existencia == null) {

                    existencia = new Existencias();

                    // referencia ligera a la variante
                    ProductoVariante varianteRef = new ProductoVariante();
                    varianteRef.setProductoVarianteId(varianteId);
                    existencia.setProductoVariante(varianteRef);

                    // referencia ligera a la bodega
                    Bodegas bodega = new Bodegas();
                    bodega.setCodigo(exDTO.getBodegaId());
                    existencia.setBodega(bodega);
                }

                // Actualizar valores
                //existencia.setExistencia(exDTO.getExistencia()); // este cmapo no lo actualizamos desde acá
                existencia.setStockMin(exDTO.getStockMin());
                existencia.setStockMax(exDTO.getStockMax());
                existencia.setUbicacion(exDTO.getUbicacion());

                existenciasRepository.save(existencia);
            }
        }
        }

        return producto;
    }

    private void actualizarUnidadMedida(Productos producto, List<Integer> unidadesIds) {

        if (unidadesIds == null || unidadesIds.isEmpty()) return;

        List<UnidadesMedidaProducto> relacionesExistentes =
                unidadesMedidaProductoRepository.findByProducto_ProductoId(producto.getProductoId());

        Integer nuevaUnidadId = unidadesIds.get(0);

        if (relacionesExistentes.isEmpty()) {
            // Si no existía, asignar
            asignarUnidadesMedida(producto, List.of(nuevaUnidadId));
        } else {
            // Si existía, actualizar si es diferente
            UnidadesMedidaProducto relExistente = relacionesExistentes.get(0);
            Integer existenteId = relExistente.getUnidadMedida().getUnidadMedidaId();
            Integer nuevoId = nuevaUnidadId;
            if (!existenteId.equals(nuevoId)) {
                UnidadesMedida nuevaUnidad = unidadMedidaRepository.findById(nuevaUnidadId)
                        .orElseThrow(() -> new RuntimeException("Unidad de medida no encontrada: " + nuevaUnidadId));
                relExistente.setUnidadMedida(nuevaUnidad);
                unidadesMedidaProductoRepository.save(relExistente);
            }
        }
    }
}
