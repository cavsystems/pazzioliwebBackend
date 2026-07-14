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
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private com.pazzioliweb.comprobantesmodule.service.PeriodoContableService periodoContableService;
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

            // Validar periodo contable abierto (la fecha de la factura define el periodo de afectación),
            // y ANCLAR la fecha de emisión de la orden a la fecha de la factura, para que el asiento y
            // el kardex (que se fechan con fechaEmision) caigan en el mismo periodo validado.
            try {
                if (request.getFechaFactura() != null && !request.getFechaFactura().isBlank()) {
                    LocalDate fechaFact = LocalDate.parse(request.getFechaFactura(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    periodoContableService.validarPeriodoAbierto(fechaFact);
                    if (realizarRequest != null) realizarRequest.setFechainicial(request.getFechaFactura());
                }
            } catch (java.time.format.DateTimeParseException ignored) { /* dejar validar por realizarOrden */ }

            // 1. Realizar la orden de compra (crea comprobante, asiento, CxP si aplica)
            OrdenCompraDTO ordenCreada = ordenCompraService.realizarOrden(realizarRequest);

            // 2. Marcar todos los items como 100% recibidos (es una compra directa, llega todo)
            List<DetalleOrdenCompraDTO> itemsRecibidos = ordenCreada.getItems().stream()
                    .peek(e -> {
                        e.setCantidadRecibida(e.getCantidad());
                        e.setRecibido(true);
                    })
                    .collect(Collectors.toList());

            // 3. Ingresar la orden con las cantidades completas
            ingresoOrdenCompraService.ingresarOrdenCompra(ordenCreada.getId(), itemsRecibidos, request.getNumeroFactura());

            // 3. Crear registro de legalización
            crearLegalizacion(request, ordenCreada);

return ordenCreada;
    }

    private void crearLegalizacion(LegalizacionRequestDTO request, OrdenCompraDTO ordenCreada) {
        // Sin try/catch genérico: si falla, la transacción @Transactional revierte todo.
        Legalizacion legalizacion = new Legalizacion();

        OrdenCompra ordenPersistida = ordenCompraRepository.findById(ordenCreada.getId())
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada al crear legalización: " + ordenCreada.getId()));

        legalizacion.setOrdenCompra(ordenPersistida);
        legalizacion.setNumeroFacturaProveedor(request.getNumeroFactura());
        legalizacion.setFechaFactura(LocalDate.parse(request.getFechaFactura(), DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        legalizacion.setTotalFactura(request.getOrdenCompraData().getOrden_compra().getTotalOrdenCompra());
        legalizacion.setProveedorId(request.getOrdenCompraData().getProvedor().getTerceroId().longValue());
        legalizacion.setEstado("LEGALIZADA");
        legalizacion.setUsuarioCreacion(obtenerUsuarioAutenticado());

        if (ordenPersistida.getComprobante() != null) {
            legalizacion.setComprobante(ordenPersistida.getComprobante());
            legalizacion.setConsecutivoComprobante(ordenPersistida.getConsecutivoComprobante());
        }

        legalizacionRepository.save(legalizacion);
    }

    /** Username autenticado actual; "SYSTEM" si no hay sesión válida. */
    private String obtenerUsuarioAutenticado() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) return "SYSTEM";
            Object principal = auth.getPrincipal();
            if (principal instanceof Usuario u && u.getUsuario() != null) {
                return u.getUsuario();
            }
            String name = auth.getName();
            if (name != null && !"anonymousUser".equals(name) && name.length() <= 250) {
                return name;
            }
        } catch (Exception ignored) {}
        return "SYSTEM";
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

        Terceros tercero = terrepo.findByTerceroId(legalizacion.getProveedorId().intValue())
                .orElse(null);
        OrdenCompra ord = ordenCompraRepository.findById(legalizacion.getOrdenCompra().getId())
                .orElseThrow(() -> new RuntimeException("Orden de compra no encontrada: " + legalizacion.getOrdenCompra().getId()));

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        dto.setFechainicial(ord.getFechaCreacion() != null ? ord.getFechaCreacion().format(formato) : null);
        dto.setFechafinal(ord.getFechaEntregaEsperada() != null ? ord.getFechaEntregaEsperada().format(formato) : null);

        dto.setId(legalizacion.getId());
        dto.setOrdenCompraId(legalizacion.getOrdenCompra().getId());
        dto.setNumeroOrden(ord.getNumeroOrden());
        dto.setFechaCreacion(ord.getFechaCreacion());
        dto.setItems(legalizacion.getOrdenCompra().getItems() != null
                ? legalizacion.getOrdenCompra().getItems().stream().map(detallemaper::detalleToDto).collect(Collectors.toList())
                : java.util.Collections.emptyList());
        dto.setNumeroFacturaProveedor(legalizacion.getNumeroFacturaProveedor());
        dto.setFechaFactura(legalizacion.getFechaFactura());
        dto.setProveedorNombre(tercero != null ? tercero.getRazonSocial() : "—");
        dto.setTotalFactura(legalizacion.getTotalFactura());
        dto.setTotal(legalizacion.getTotalFactura());
        dto.setProveedorId(legalizacion.getProveedorId());
        dto.setEstado(legalizacion.getEstado());
        dto.setUsuarioCreacion(legalizacion.getUsuarioCreacion());

        // Retenciones (desde la orden)
        dto.setRetefuente(ord.getRetefuente());
        dto.setReteiva(ord.getReteiva());
        dto.setReteica(ord.getReteica());

        // Comprobante contable (heredado de la orden)
        if (legalizacion.getComprobante() != null) {
            dto.setComprobanteId(legalizacion.getComprobante().getId());
            dto.setPrefijoComprobante(legalizacion.getComprobante().getPrefijo());
            dto.setTipoMovimientoComprobante(legalizacion.getComprobante().getTipoMovimiento().name());
            dto.setConsecutivoComprobante(legalizacion.getConsecutivoComprobante());
        }
        return dto;
    }
}
