package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.comprasmodule.client.ProductoClient;
import com.pazzioliweb.comprasmodule.dtos.*;
import com.pazzioliweb.comprasmodule.entity.DetalleOrdenCompra;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import com.pazzioliweb.comprasmodule.exception.OrdenCompraException;
import com.pazzioliweb.comprasmodule.mapper.OrdenCompraMapper;
import com.pazzioliweb.comprasmodule.repository.OrdenCompraRepository;
import com.pazzioliweb.comprasmodule.service.CuentaPorPagarService;
import com.pazzioliweb.comprasmodule.service.OrdenCompraService;
import com.pazzioliweb.comprasmodule.service.ProductoService;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final OrdenCompraMapper ordenCompraMapper;
    private final ProductoService productoService;
    private final ProductoClient productoClient;
    private final CuentaPorPagarService cuentaPorPagarService;
    private final TercerosRepository tercerosRepository;
    private final BodegasRepository bodegasRepository;

    @Autowired
    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                  OrdenCompraMapper ordenCompraMapper,
                                  ProductoService productoService,
                                  ProductoClient productoClient,
                                  CuentaPorPagarService cuentaPorPagarService,
                                  TercerosRepository tercerosRepository,
                                  BodegasRepository bodegasRepository) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraMapper = ordenCompraMapper;
        this.productoService = productoService;
        this.productoClient = productoClient;
        this.cuentaPorPagarService = cuentaPorPagarService;
        this.tercerosRepository = tercerosRepository;
        this.bodegasRepository = bodegasRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdenCompraDTO> buscarConFiltros(String estado, LocalDate fechaDesde,
                                                 LocalDate fechaHasta, Integer proveedorId,
                                                 Pageable pageable) {
        return ordenCompraRepository.buscarConFiltros(estado, fechaDesde, fechaHasta, proveedorId, pageable)
                .map(ordenCompraMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenCompraDTO> obtenerPorId(Long id) {
        return ordenCompraRepository.findById(id)
                .map(ordenCompraMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenCompraDTO> obtenerPorNumeroOrden(String numeroOrden) {
        return ordenCompraRepository.findByNumeroOrden(numeroOrden)
                .map(ordenCompraMapper::toDto);

    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerPorNumeroOrdenlist(String numeroOrden, Pageable pageable) {
        return ordenCompraRepository.findByNumeroOrdenStartingWithIgnoreCase(numeroOrden,pageable)  .map(ordenCompraMapper::toDto).getContent();

    }


    @Override
    @Transactional
    public OrdenCompraDTO crear(OrdenCompraDTO ordenCompraDTO) {
        // This method is outdated, kept for compatibility
        throw new UnsupportedOperationException("Use realizarOrden for new orders");
    }

    @Override
    @Transactional
    public OrdenCompraDTO actualizar(OrdenCompraDTO ordenCompraDTO) {
        // Implementation for updating existing orders
        OrdenCompra ordenExistente = ordenCompraRepository.findById(ordenCompraDTO.getId())
                .orElseThrow(() -> new OrdenCompraException("La orden de compra no existe"));

        if (!"PENDIENTE".equals(ordenExistente.getEstado())) {
            throw new OrdenCompraException("Solo se pueden modificar órdenes en estado PENDIENTE");
        }

        ordenExistente.setObservaciones(ordenCompraDTO.getObservaciones());
        ordenExistente.setFechaEntregaEsperada(ordenCompraDTO.getFechaEntregaEsperada());
        ordenExistente.setGravada(ordenCompraDTO.getSubtotal());
        ordenExistente.setIva(ordenCompraDTO.getIva());
        ordenExistente.setTotalOrdenCompra(ordenCompraDTO.getTotal());

        ordenExistente.getItems().clear();
        ordenCompraDTO.getItems().forEach(itemDto -> {
            DetalleOrdenCompra detalle = new DetalleOrdenCompra();
            detalle.setCodigoProducto(itemDto.getCodigoProducto());
            detalle.setDescripcionProducto(itemDto.getDescripcionProducto());
            detalle.setReferenciaVariantes(itemDto.getReferenciaVariantes());
            detalle.setCantidad(itemDto.getCantidad());
            detalle.setPrecioUnitario(itemDto.getPrecioUnitario());
            detalle.setDescuento(itemDto.getDescuento() != null ? itemDto.getDescuento() : BigDecimal.ZERO);
            detalle.setIva(itemDto.getIvaPorcentaje() != null ? itemDto.getIvaPorcentaje() : BigDecimal.ZERO);
            BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            BigDecimal subtotalConDescuento = subtotal.subtract(detalle.getDescuento());
            BigDecimal ivaAmount = subtotalConDescuento.multiply(detalle.getIva()).divide(BigDecimal.valueOf(100));
            detalle.setTotal(subtotalConDescuento.add(ivaAmount));
            detalle.setOrdenCompra(ordenExistente);
            ordenExistente.getItems().add(detalle);
        });

        OrdenCompra ordenActualizada = ordenCompraRepository.save(ordenExistente);
        return ordenCompraMapper.toDto(ordenActualizada);
    }

    @Override
    @Transactional
    public void anular(Long id, String motivo) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new OrdenCompraException("La orden de compra no existe"));

        if ("ANULADA".equals(orden.getEstado())) {
            throw new OrdenCompraException("La orden ya está anulada");
        }

        // Revertir inventario de lo recibido
        for (DetalleOrdenCompra detalle : orden.getItems()) {
            if (detalle.getCantidadRecibida() > 0) {
                productoService.actualizarInventario(
                        detalle.getCodigoProducto(),
                        null, // No tenemos lote específico para reversión
                        -detalle.getCantidadRecibida(),
                        orden.getBodega().getCodigo()
                );
            }
        }

        // Eliminar detalles
        orden.getItems().clear();

        // Eliminar cuenta por pagar
        cuentaPorPagarService.eliminarPorNumeroFactura(orden.getNumeroOrden());

        orden.setEstado("ANULADA");
        orden.setObservaciones(orden.getObservaciones() + "\nAnulada: " + motivo);
        ordenCompraRepository.save(orden);
    }

    @Override
    @Transactional
    public void recibirOrden(Long id, List<ItemRecibidoDTO> itemsRecibidos) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new OrdenCompraException("La orden de compra no existe"));

        if ("ANULADA".equals(orden.getEstado()) || "RECIBIDA".equals(orden.getEstado())) {
            throw new OrdenCompraException("No se puede recibir una orden " + orden.getEstado());
        }

        for (ItemRecibidoDTO itemRecibido : itemsRecibidos) {
            DetalleOrdenCompra detalle = orden.getItems().stream()
                    .filter(d -> d.getId().equals(itemRecibido.getDetalleId()))
                    .findFirst()
                    .orElseThrow(() -> new OrdenCompraException("Detalle de orden no encontrado: " + itemRecibido.getDetalleId()));

            int cantidadPendiente = detalle.getCantidad() - detalle.getCantidadRecibida();
            if (itemRecibido.getCantidadRecibida() > cantidadPendiente) {
                throw new OrdenCompraException("La cantidad recibida no puede ser mayor a la pendiente");
            }

            int nuevaCantidadRecibida = detalle.getCantidadRecibida() + itemRecibido.getCantidadRecibida();
            detalle.setCantidadRecibida(nuevaCantidadRecibida);
            detalle.setRecibido(nuevaCantidadRecibida >= detalle.getCantidad());

            actualizarInventario(detalle, itemRecibido, orden.getBodega().getCodigo());
        }

        boolean todosRecibidos = orden.getItems().stream().allMatch(DetalleOrdenCompra::isRecibido);
        boolean algunosRecibidos = orden.getItems().stream().anyMatch(d -> d.getCantidadRecibida() > 0);

        if (todosRecibidos) {
            orden.setEstado("RECIBIDA");
            actualizarCostosYPrecios(orden);
        } else if (algunosRecibidos) {
            orden.setEstado("RECIBIDA_PARCIAL");
        }

        ordenCompraRepository.save(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerOrdenesPendientes() {
        return ordenCompraRepository.buscarConFiltros("PENDIENTE", null, null, null, Pageable.unpaged())
                .getContent()
                .stream()
                .map(ordenCompraMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> contarTotalOrdenes() {
        long total = ordenCompraRepository.count();
        return Collections.singletonMap("totalOrdenes", total);
    }

    @Override
    @Transactional
    public OrdenCompraDTO realizarOrden(RealizarOrdenRequestDTO request) {
        // 1. Procesar productos: actualizar o crear productos con variantes
        procesarProductosDesdeRequest(request.getOrden_compra().getProducts());

        // 2. Crear la orden de compra
        OrdenCompra ordenCompra = crearOrdenDesdeRequest(request);

        // 3. Crear detalles de la orden
        List<DetalleOrdenCompra> detalles = crearDetallesDesdeRequest(ordenCompra, request.getOrden_compra().getProducts());
        ordenCompra.setItems(detalles);

        // 4. Guardar la orden
        OrdenCompra ordenGuardada = ordenCompraRepository.save(ordenCompra);

        // 5. Crear cuenta por pagar
        crearCuentaPorPagarDesdeRequest(ordenGuardada, request);

        return ordenCompraMapper.toDto(ordenGuardada);
    }

    private void procesarProductosDesdeRequest(List<RealizarOrdenRequestDTO.ProductoRequestPayloadDTO> products) {
        for (RealizarOrdenRequestDTO.ProductoRequestPayloadDTO product : products) {
            ProductoActualizarCrearDTO productoDTO = mapToProductoActualizarCrearDTO(product);
            productoService.actualizarOCrearProducto(productoDTO);
        }
    }

    private ProductoActualizarCrearDTO mapToProductoActualizarCrearDTO(RealizarOrdenRequestDTO.ProductoRequestPayloadDTO product) {
        ProductoActualizarCrearDTO dto = new ProductoActualizarCrearDTO();
        dto.setCodigo(product.getCodigo());
        dto.setTipoProducto(product.getTipo_producto());
        dto.setDescripcion(product.getDescripcion());
        dto.setReferencia(product.getReferencia());
        dto.setUnidadMedida(product.getUnidad_medida());
        dto.setImpuesto(product.getImpuesto());
        dto.setCosto(product.getCosto());
        dto.setLinea(product.getLinea());
        dto.setGrupo(product.getGrupo());
        dto.setCodigoBarras(product.getCodigobarras());
        dto.setUbicacion(product.getUbicacion());

        if (product.getVariantes() != null && !product.getVariantes().isEmpty()) {
            List<ProductoActualizarCrearDTO.VarianteDTO> variantes = new ArrayList<>();
            for (RealizarOrdenRequestDTO.VariantePayloadDTO variante : product.getVariantes()) {
                ProductoActualizarCrearDTO.VarianteDTO v = new ProductoActualizarCrearDTO.VarianteDTO();
                v.setCodigoBarraVariante(variante.getCodigobarravariante());
                v.setCantidad(variante.getCantidad());
                v.setNotas(variante.getNotas());
                v.setSku(variante.getSku());
                v.setReferenciaVariantes(variante.getReferenciaVariantes());

                if (variante.getExistencias() != null) {
                    List<ProductoActualizarCrearDTO.ExistenciaDTO> existencias = new ArrayList<>();
                    for (RealizarOrdenRequestDTO.ExistenciaDTO exis : variante.getExistencias()) {
                        ProductoActualizarCrearDTO.ExistenciaDTO e = new ProductoActualizarCrearDTO.ExistenciaDTO();
                        e.setExistenciaId(exis.getExistenciaId());
                        e.setBodegaId(exis.getBodegaId());
                        e.setBodega(exis.getBodega());
                        e.setCantidad(exis.getCantidad());
                        e.setMinimo(exis.getMinimo());
                        e.setMaximo(exis.getMaximo());
                        existencias.add(e);
                    }
                    v.setExistencias(existencias);
                }

                if (variante.getAtributos() != null) {
                    List<ProductoActualizarCrearDTO.AtributoDTO> atributos = new ArrayList<>();
                    for (RealizarOrdenRequestDTO.AtributoPayloadDTO attr : variante.getAtributos()) {
                        ProductoActualizarCrearDTO.AtributoDTO a = new ProductoActualizarCrearDTO.AtributoDTO();
                        a.setNombre(attr.getNombre());
                        a.setValor(attr.getValor());
                        atributos.add(a);
                    }
                    v.setAtributos(atributos);
                }

                if (variante.getPrecios() != null) {
                    List<ProductoActualizarCrearDTO.PrecioDTO> precios = new ArrayList<>();
                    for (RealizarOrdenRequestDTO.PrecioDTO prec : variante.getPrecios()) {
                        ProductoActualizarCrearDTO.PrecioDTO p = new ProductoActualizarCrearDTO.PrecioDTO();
                        p.setIdTipoPrecio(prec.getIdTipoPrecio());
                        p.setValor(prec.getValor());
                        precios.add(p);
                    }
                    v.setPrecios(precios);
                }

                v.setDescuento(variante.getDescuento());
                variantes.add(v);
            }
            dto.setVariantes(variantes);
        }

        return dto;
    }

    private OrdenCompra crearOrdenDesdeRequest(RealizarOrdenRequestDTO request) {
        OrdenCompra orden = new OrdenCompra();
    Optional<Long> idnumero=ordenCompraRepository.findMaxId();
        orden.setNumeroOrden("OC-" + String.valueOf(idnumero.orElse(0L) + 1));
        orden.setEstado("PENDIENTE");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate fechaInicial;
        if (request.getFechainicial() != null && !request.getFechainicial().isEmpty()) {
            fechaInicial = LocalDate.parse(request.getFechainicial(), formatter);
        } else {
            fechaInicial = LocalDate.now();
        }
        orden.setFechaEmision(fechaInicial);

        LocalDate fechaEntrega;
        if (request.getFechafinal() != null && !request.getFechafinal().isEmpty()) {
            fechaEntrega = LocalDate.parse(request.getFechafinal(), formatter);
        } else {
            fechaEntrega = fechaInicial.plusDays(request.getPlazo() != null ? request.getPlazo() : 30);
        }
        orden.setFechaEntregaEsperada(fechaEntrega);

        orden.setUsuarioCreacion("SYSTEM");
        orden.setFechaCreacion(LocalDate.now());

        orden.setGravada(request.getOrden_compra().getGravada());
        orden.setIva(request.getOrden_compra().getIva());
        orden.setDescuentos(request.getOrden_compra().getDescuentos());
        orden.setTotalOrdenCompra(request.getOrden_compra().getTotalOrdenCompra());

        // Set proveedor
        orden.setProveedor(tercerosRepository.findById(request.getProvedor().getTerceroId())
                .orElseThrow(() -> new OrdenCompraException("Proveedor no encontrado")));

        orden.setBodega(bodegasRepository.findByCodigo(request.getBodegaId())
                .orElseThrow(() -> new OrdenCompraException("Bodega no encontrada")));

        return orden;
    }

    private List<DetalleOrdenCompra> crearDetallesDesdeRequest(OrdenCompra orden, List<RealizarOrdenRequestDTO.ProductoRequestPayloadDTO> products) {
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        for (RealizarOrdenRequestDTO.ProductoRequestPayloadDTO product : products) {
            if (product.getVariantes() != null && !product.getVariantes().isEmpty()) {
                for (RealizarOrdenRequestDTO.VariantePayloadDTO variante : product.getVariantes()) {
                    DetalleOrdenCompra detalle = new DetalleOrdenCompra();
                    detalle.setOrdenCompra(orden);
                    detalle.setCodigoProducto(product.getCodigo());
                    detalle.setCodigoBarras(variante.getCodigobarravariante());
                    detalle.setDescripcionProducto(product.getDescripcion());
                    detalle.setObservacionProducto(variante.getNotas() != null ? String.join(", ", variante.getNotas()) : "");
                    detalle.setReferenciaVariantes(variante.getReferenciaVariantes());
                    detalle.setCantidad(variante.getCantidad());
                    detalle.setPrecioUnitario(product.getCosto());
                    detalle.setDescuento(variante.getDescuento() != null ? variante.getDescuento() : BigDecimal.ZERO);
                    detalle.setIva(product.getImpuesto() != null ? BigDecimal.valueOf(product.getImpuesto()) : BigDecimal.ZERO);
                    BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                    BigDecimal subtotalConDescuento = subtotal.subtract(detalle.getDescuento());
                    BigDecimal ivaAmount = subtotalConDescuento.multiply(detalle.getIva()).divide(BigDecimal.valueOf(100));
                    detalle.setTotal(subtotalConDescuento.add(ivaAmount));
                    detalle.setSku(variante.getSku());
                    detalle.setRecibido(false);
                    detalle.setCantidadRecibida(0);
                    detalles.add(detalle);
                }
            } else {
                DetalleOrdenCompra detalle = new DetalleOrdenCompra();
                detalle.setOrdenCompra(orden);
                detalle.setCodigoProducto(product.getCodigo());
                detalle.setCodigoBarras(product.getCodigobarras());
                detalle.setDescripcionProducto(product.getDescripcion());
                detalle.setObservacionProducto("");
             //  detalle.setSku(product.set);
                detalle.setReferenciaVariantes(product.getReferencia());
                detalle.setCantidad(0);
                detalle.setPrecioUnitario(product.getCosto());
                detalle.setDescuento(BigDecimal.ZERO);
                detalle.setIva(product.getImpuesto() != null ? BigDecimal.valueOf(product.getImpuesto()) : BigDecimal.ZERO);
                BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                BigDecimal subtotalConDescuento = subtotal.subtract(detalle.getDescuento());
                BigDecimal ivaAmount = subtotalConDescuento.multiply(detalle.getIva()).divide(BigDecimal.valueOf(100));
                detalle.setTotal(subtotalConDescuento.add(ivaAmount));
                detalle.setRecibido(false);
                detalle.setCantidadRecibida(0);
                detalles.add(detalle);
            }
        }
        return detalles;
    }

    private void crearCuentaPorPagarDesdeRequest(OrdenCompra orden, RealizarOrdenRequestDTO request) {
        CuentaPorPagarDTO cuenta = new CuentaPorPagarDTO();
        cuenta.setNit(request.getProvedor().getIdentificacion());
        cuenta.setNombre(request.getProvedor().getNombre());
        cuenta.setNumeroFactura(orden.getNumeroOrden());
        cuenta.setFechaVencimiento(orden.getFechaEntregaEsperada());
        cuenta.setValorNeto(orden.getTotalOrdenCompra());
        cuenta.setEstado("PENDIENTE");
        cuenta.setProveedorId(request.getProvedor().getTerceroId());

        cuentaPorPagarService.crear(cuenta);
    }


    private void actualizarInventario(DetalleOrdenCompra detalle, ItemRecibidoDTO itemRecibido, int bodegaId) {
        productoService.actualizarInventario(
                detalle.getCodigoProducto(),
                itemRecibido.getLote(),
                itemRecibido.getCantidadRecibida(),
                bodegaId
        );
    }

    private void actualizarCostosYPrecios(OrdenCompra orden) {
        for (DetalleOrdenCompra detalle : orden.getItems()) {
            if (detalle.getCantidadRecibida() > 0) {
                ProductoActualizarCrearDTO productoDTO = new ProductoActualizarCrearDTO();
                productoDTO.setCodigo(detalle.getCodigoProducto());
                productoDTO.setCosto(detalle.getPrecioUnitario());

                // Add sale price, assuming 30% markup
                List<ProductoActualizarCrearDTO.PrecioDTO> precios = new ArrayList<>();
                ProductoActualizarCrearDTO.PrecioDTO precioVenta = new ProductoActualizarCrearDTO.PrecioDTO();
                precioVenta.setIdTipoPrecio(1); // Assuming 1 is the ID for sale price type
                precioVenta.setValor(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(1.3)));
                precios.add(precioVenta);
                productoDTO.setPrecios(precios);

                productoService.actualizarOCrearProducto(productoDTO);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerTodasLasOrdenes() {
        return ordenCompraRepository.findAll()
                .stream()
                .map(ordenCompraMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> obtenerOrdenesPorProveedorYEstado(Integer proveedorId, String estado) {
        if(proveedorId==0){
            return ordenCompraRepository.buscarConFiltrossinpro(estado, null, null,  Pageable.unpaged()) .getContent()
                    .stream()
                    .map(ordenCompraMapper::toDto)
                    .collect(Collectors.toList());
        }
        return ordenCompraRepository.buscarConFiltros(estado, null, null, proveedorId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(ordenCompraMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleOrdenCompraDTO> obtenerDetallesPorNumeroOrden(String numeroOrden) {
        return ordenCompraRepository.findByNumeroOrden(numeroOrden)
                .map(orden -> ordenCompraMapper.toDetalleDtoList(orden.getItems()))
                .orElse(Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long obtenerSiguienteId() {
        Optional<Long> maxId = ordenCompraRepository.findMaxId();
        return maxId.orElse(0L) + 1;
    }
}
