package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.comprasmodule.client.ProductoClient;
import com.pazzioliweb.comprasmodule.dtos.*;
import com.pazzioliweb.comprasmodule.entity.Legalizacion;
import com.pazzioliweb.comprasmodule.entity.OrdenCompra;
import com.pazzioliweb.comprasmodule.mapper.OrdenCompraMapper;
import com.pazzioliweb.comprasmodule.repository.LegalizacionRepository;
import com.pazzioliweb.comprasmodule.repository.OrdenCompraRepository;
import com.pazzioliweb.comprasmodule.service.*;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LegalizacionServiceImpl implements LegalizacionService {

    private final ProductoService productoService;
    private final ProductoClient productoClient;
    private final CuentaPorPagarService cuentaPorPagarService;
    private final OrdenCompraService ordenCompraService;
    private final IngresoOrdenCompraService ingresoOrdenCompraService;
    private final LegalizacionRepository legalizacionRepository;
    @Autowired
    private OrdenCompraMapper detallemaper;

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;
  @Autowired
  private TercerosRepository terrepo;
    public LegalizacionServiceImpl(ProductoService productoService, ProductoClient productoClient, CuentaPorPagarService cuentaPorPagarService, OrdenCompraService ordenCompraService, IngresoOrdenCompraService ingresoOrdenCompraService, LegalizacionRepository legalizacionRepository) {
        this.productoService = productoService;
        this.productoClient = productoClient;
        this.cuentaPorPagarService = cuentaPorPagarService;
        this.ordenCompraService = ordenCompraService;
        this.ingresoOrdenCompraService = ingresoOrdenCompraService;
        this.legalizacionRepository = legalizacionRepository;
    }

    @Override
    @Transactional
    public   OrdenCompraDTO  legalizarCompra(LegalizacionRequestDTO request) {
       
            RealizarOrdenRequestDTO realizarRequest = request.getOrdenCompraData();
            // 1. Realizar la orden de compra (crea orden, productos, cuenta por pagar)
            OrdenCompraDTO ordenCreada = ordenCompraService.realizarOrden(realizarRequest);
            // 2. Ingresar la orden (recibir productos)

        List<DetalleOrdenCompraDTO> datlles=ordenCreada.getItems();
        ArrayList<DetalleOrdenCompraDTO> nuevodetalles=new ArrayList<>();
        for (DetalleOrdenCompraDTO e: datlles){
            e.setCantidadRecibida(e.getCantidad());
            e.setRecibido(true);
            e.setManifiesto(e.getManifiesto());
            nuevodetalles.add(e);
        }

            // List<DetalleOrdenCompraDTO> detallesRecibidos = crearDetallesRecibidos(request.getOrdenCompraData().getOrden_compra().getProducts());
            ingresoOrdenCompraService.ingresarOrdenCompra(ordenCreada.getId(), nuevodetalles, request.getNumeroFactura());

            // 3. Crear registro de legalización
            crearLegalizacion(request, ordenCreada);

return ordenCreada;
    }

    private void crearLegalizacion(LegalizacionRequestDTO request, OrdenCompraDTO ordenCreada) {
        try {
            Legalizacion legalizacion = new Legalizacion();
            OrdenCompra orden = new OrdenCompra();
            orden.setId(ordenCreada.getId());
            legalizacion.setOrdenCompra(orden);
            legalizacion.setNumeroFacturaProveedor(request.getNumeroFactura());
            legalizacion.setFechaFactura(LocalDate.parse(request.getFechaFactura(), DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            legalizacion.setTotalFactura(request.getOrdenCompraData().getOrden_compra().getTotalOrdenCompra());
            // Set proveedorId
            legalizacion.setProveedorId(request.getOrdenCompraData().getProvedor().getTerceroId().longValue());
            legalizacion.setEstado("LEGALIZADA");
            legalizacion.setUsuarioCreacion("system");

            legalizacionRepository.save(legalizacion);
        } catch (Exception e) {
           System.out.println(e.getMessage());
           e.printStackTrace();
        }

    }

    private List<DetalleOrdenCompraDTO> crearDetallesRecibidos(List<RealizarOrdenRequestDTO.ProductoRequestPayloadDTO> productos) {
        List<DetalleOrdenCompraDTO> detalles = new ArrayList<>();

        for (RealizarOrdenRequestDTO.ProductoRequestPayloadDTO p : productos) {

            if (p.getVariantes() != null && !p.getVariantes().isEmpty()) {
                for (RealizarOrdenRequestDTO.VariantePayloadDTO v : p.getVariantes()) {
                    DetalleOrdenCompraDTO detalle = new DetalleOrdenCompraDTO();
                    detalle.setCodigoProducto(p.getCodigo());
                    detalle.setCantidadRecibida(v.getCantidad());
                    detalles.add(detalle);
                }
            }

        }
        return detalles;
    }

    @Override
    @Transactional(readOnly = true)
    public Long obtenerSiguienteId() {
        Optional<Long> maxId = Optional.ofNullable(legalizacionRepository.findMaxId());
        return maxId.orElse(0L) + 1;
    }
    @Transactional
    public List<LegalizacionDTO> obtenerTodasLasLegalizaciones() {
        List<Legalizacion> legalizaciones = legalizacionRepository.findAll();

        return legalizaciones.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalizacionDTO> obtenerLegalizacionesPorProveedor(Long proveedorId) {
        return legalizacionRepository.findByProveedorId(proveedorId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private LegalizacionDTO mapToDTO(Legalizacion legalizacion) {
        LegalizacionDTO dto = new LegalizacionDTO();
        Optional<Terceros> tercero= terrepo.findByTerceroId((legalizacion.getProveedorId().intValue()));
          Optional<OrdenCompra> ord=ordenCompraRepository.findById(legalizacion.getOrdenCompra().getId());
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String fechainicial = ord.get().getFechaCreacion().format(formato);
        String fechafinal = ord.get().getFechaEntregaEsperada().format(formato);
        dto.setFechainicial(fechainicial);
        dto.setFechafinal(fechafinal);

        dto.setId(legalizacion.getId());
        dto.setOrdenCompraId(legalizacion.getOrdenCompra().getId());
        dto.setNumeroOrden(ord.get().getNumeroOrden());
        dto.setFechaCreacion(ord.get().getFechaCreacion());
        dto.setItems(legalizacion.getOrdenCompra().getItems().stream().map(detallemaper::detalleToDto).collect(Collectors.toList()));
        dto.setNumeroFacturaProveedor(legalizacion.getNumeroFacturaProveedor());
        dto.setFechaFactura(legalizacion.getFechaFactura());
        dto.setProveedorNombre(tercero.get().getRazonSocial());
        dto.setTotalFactura(legalizacion.getTotalFactura());
        dto.setTotal(legalizacion.getTotalFactura());
        dto.setProveedorId(legalizacion.getProveedorId());
        dto.setEstado(legalizacion.getEstado());
        dto.setUsuarioCreacion(legalizacion.getUsuarioCreacion());
        return dto;
    }
}
