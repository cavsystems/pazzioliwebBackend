package com.pazzioliweb.facturacionmodule.service;

import com.pazzioliweb.facturacionmodule.dtos.*;
import com.pazzioliweb.facturacionmodule.entity.Facturas;
import com.pazzioliweb.facturacionmodule.entity.MetodosPagoFacturas;
import com.pazzioliweb.facturacionmodule.entity.TipoTotalesFacturas;
import com.pazzioliweb.facturacionmodule.repositori.FacturasRepository;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.ventasmodule.entity.DetalleVenta;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.entity.VentaMetodoPago;
import com.pazzioliweb.ventasmodule.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FacturacionElectronicaService {

    private final FacturasRepository facturasRepository;
    private final ProveedorFacturacionElectronica proveedorDian;
    private final VentaRepository ventaRepository;
    private final TercerosRepository tercerosRepository;
    private final MetodosPagoRepository metodosPagoRepository;

    @Autowired
    public FacturacionElectronicaService(FacturasRepository facturasRepository,
                                          ProveedorFacturacionElectronica proveedorDian,
                                          VentaRepository ventaRepository,
                                          TercerosRepository tercerosRepository,
                                          MetodosPagoRepository metodosPagoRepository) {
        this.facturasRepository = facturasRepository;
        this.proveedorDian = proveedorDian;
        this.ventaRepository = ventaRepository;
        this.tercerosRepository = tercerosRepository;
        this.metodosPagoRepository = metodosPagoRepository;
    }

    // ══════════════════════════════════════════════════════════
    //  PASO 3: Venta COMPLETADA → Factura → Proveedor DIAN
    // ══════════════════════════════════════════════════════════

    @Transactional
    public FacturaElectronicaResponseDTO generarDesdeVenta(GenerarFacturaRequestDTO request) {

        // 1. Buscar la venta COMPLETADA con items y métodos de pago
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + request.getVentaId()));

        if (!"COMPLETADA".equals(venta.getEstado())) {
            throw new RuntimeException("Solo se pueden facturar ventas COMPLETADAS. Estado actual: " + venta.getEstado());
        }

        // 2. Validar que no exista ya una factura para esta venta
        Optional<Facturas> facturaExistente = facturasRepository.findByVentaId(request.getVentaId());
        if (facturaExistente.isPresent()) {
            throw new RuntimeException("Ya existe una factura para la venta ID: " + request.getVentaId()
                    + " - Factura: " + facturaExistente.get().getNumeroFactura());
        }

        // 3. Obtener siguiente consecutivo
        Integer siguienteConsecutivo = obtenerSiguienteConsecutivo(request.getComprobanteId());
        String prefijo = "FE";
        String numeroFactura = prefijo + siguienteConsecutivo;

        // 4. Crear factura en BD con TODOS los datos de la venta
        Facturas factura = crearFacturaDesdeVenta(request, venta, siguienteConsecutivo, prefijo, numeroFactura);
        factura = facturasRepository.save(factura);

        // 5. Armar el DTO completo para el proveedor DIAN
        DianDocumentoRequestDTO dianRequest = armarRequestDian(factura, venta);

        // 6. Enviar al proveedor de facturación electrónica
        DianDocumentoResponseDTO dianResponse;
        try {
            dianResponse = proveedorDian.enviarFactura(dianRequest);
        } catch (Exception e) {
            factura.setEstadoDian(Facturas.EstadoDian.RECHAZADA);
            factura.setMensajeDian("Error al enviar: " + e.getMessage());
            facturasRepository.save(factura);
            throw new RuntimeException("Error al enviar factura a la DIAN: " + e.getMessage(), e);
        }

        // 7. Guardar respuesta de la DIAN
        factura.setCufe(dianResponse.getCufe());
        factura.setQrData(dianResponse.getQrData());
        factura.setXmlFirmado(dianResponse.getXmlFirmado());
        factura.setPdfBase64(dianResponse.getPdfBase64());
        factura.setFechaValidacionDian(dianResponse.getFechaValidacion());
        factura.setEstadoDian(dianResponse.isExitoso()
                ? Facturas.EstadoDian.AUTORIZADA
                : Facturas.EstadoDian.RECHAZADA);
        factura.setMensajeDian(dianResponse.getMensajeDian());
        facturasRepository.save(factura);

        return mapToResponse(factura);
    }

    @Transactional
    public FacturaElectronicaResponseDTO consultarEstadoDian(Integer facturaId) {
        Facturas factura = facturasRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));

        if (factura.getCufe() == null || factura.getCufe().isEmpty()) {
            throw new RuntimeException("La factura no tiene CUFE asignado");
        }

        DianDocumentoResponseDTO dianResponse = proveedorDian.consultarEstado(factura.getCufe());
        if (dianResponse.isExitoso()) {
            factura.setEstadoDian(Facturas.EstadoDian.AUTORIZADA);
        }
        factura.setMensajeDian(dianResponse.getMensajeDian());
        factura.setFechaValidacionDian(dianResponse.getFechaValidacion());
        facturasRepository.save(factura);

        return mapToResponse(factura);
    }

    public String obtenerPdfBase64(Integer facturaId) {
        Facturas factura = facturasRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));
        return factura.getPdfBase64();
    }

    // ══════════════════════════════════════════════════════════
    //  MÉTODOS PRIVADOS: Construcción de factura desde venta
    // ══════════════════════════════════════════════════════════

    private Integer obtenerSiguienteConsecutivo(Integer comprobanteId) {
        Optional<Integer> max = facturasRepository.findMaxConsecutivoByComprobanteId(comprobanteId);
        return max.orElse(0) + 1;
    }

    private Facturas crearFacturaDesdeVenta(GenerarFacturaRequestDTO request, Venta venta,
                                             Integer consecutivo, String prefijo, String numeroFactura) {
        Facturas factura = new Facturas();

        // Datos del comprobante
        factura.setConsecutivo(consecutivo);
        factura.setComprobanteId(request.getComprobanteId());
        factura.setPrefijo(prefijo);
        factura.setNumeroFactura(numeroFactura);

        // Datos del tercero (cliente de la venta)
        factura.setTerceroId(venta.getCliente().getTerceroId());

        // Fechas
        factura.setFechaCreacion(LocalDateTime.now());
        factura.setFechaEmision(venta.getFechaEmision());
        factura.setFechaVencimiento(venta.getFechaEmision()); // Contado
        factura.setPlazo(0);

        // Usuario y estado
        factura.setUsuarioIngresoId(1); // TODO: obtener del contexto de seguridad
        factura.setEstado(Facturas.EstadoFactura.ACTIVO);

        // Cajero
        if (venta.getCajero() != null) {
            factura.setCajaId(venta.getCajero().getCajeroId());
        }

        // Totales desde la venta
        factura.setDescuento(venta.getDescuentos().doubleValue());
        factura.setTotalFactura(venta.getTotalVenta().doubleValue());
        factura.setSaldo(0.00); // Contado = saldo 0

        // Otros
        factura.setCajaId(request.getCajaId());
        factura.setObservaciones(request.getObservaciones() != null
                ? request.getObservaciones()
                : "Factura generada desde venta: " + venta.getNumeroVenta());

        // Relación con la venta
        factura.setVentaId(request.getVentaId());
        factura.setEstadoDian(Facturas.EstadoDian.PENDIENTE);

        // TipoTotales (gravado, iva, descuentos)
        Set<TipoTotalesFacturas> tipoTotales = new HashSet<>();

        TipoTotalesFacturas gravado = new TipoTotalesFacturas();
        gravado.setFactura(factura);
        gravado.setTipoTotalId(1); // 1 = Base Gravable
        gravado.setBase(venta.getGravada());
        gravado.setValor(venta.getGravada());
        tipoTotales.add(gravado);

        TipoTotalesFacturas ivaTotal = new TipoTotalesFacturas();
        ivaTotal.setFactura(factura);
        ivaTotal.setTipoTotalId(2); // 2 = IVA
        ivaTotal.setBase(venta.getGravada());
        ivaTotal.setValor(venta.getIva());
        tipoTotales.add(ivaTotal);

        factura.setTipoTotales(tipoTotales);

        // Métodos de pago de la venta → métodos de pago de la factura
        if (venta.getMetodosPago() != null) {
            Set<MetodosPagoFacturas> metodosPagoFactura = new HashSet<>();
            for (VentaMetodoPago vmp : venta.getMetodosPago()) {
                MetodosPagoFacturas mpf = new MetodosPagoFacturas();
                mpf.setFactura(factura);
                mpf.setMetodopago(vmp.getMetodoPago());
                mpf.setValor(vmp.getMonto());
                metodosPagoFactura.add(mpf);
            }
            factura.setMetodosPago(metodosPagoFactura);
        }

        return factura;
    }

    private DianDocumentoRequestDTO armarRequestDian(Facturas factura, Venta venta) {
        DianDocumentoRequestDTO req = new DianDocumentoRequestDTO();

        // Tipo documento
        req.setTipoDocumento("01"); // Factura de venta
        req.setPrefijo(factura.getPrefijo());
        req.setConsecutivo(factura.getConsecutivo());
        req.setFechaEmision(factura.getFechaEmision());
        req.setFechaVencimiento(factura.getFechaVencimiento());
        req.setFormaPago("1"); // Contado
        req.setPlazo(factura.getPlazo());

        // ── Emisor: datos de tu empresa ──
        // TODO: Cargar desde tabla empresa cuando se tenga el ID del contexto
        DianDocumentoRequestDTO.EmisorDTO emisor = new DianDocumentoRequestDTO.EmisorDTO();
        emisor.setTipoIdentificacion("31"); // NIT
        emisor.setRazonSocial("MI EMPRESA S.A.S"); // TODO: reemplazar
        req.setEmisor(emisor);

        // ── Receptor: datos del cliente ──
        Terceros cliente = venta.getCliente();
        DianDocumentoRequestDTO.ReceptorDTO receptor = new DianDocumentoRequestDTO.ReceptorDTO();
        if (cliente.getTipoIdentificacion() != null) {
            receptor.setTipoIdentificacion(String.valueOf(cliente.getTipoIdentificacion().getCodigo()));
        }
        receptor.setNumeroIdentificacion(cliente.getIdentificacion());
        receptor.setDigitoVerificacion(cliente.getDv());
        receptor.setNombre(cliente.getRazonSocial() != null
                ? cliente.getRazonSocial()
                : (cliente.getNombre1() + " " + (cliente.getApellido1() != null ? cliente.getApellido1() : "")).trim());
        receptor.setDireccion(cliente.getDireccion());
        req.setReceptor(receptor);

        // ── Líneas de detalle (items de la venta) ──
        List<DianDocumentoRequestDTO.LineaDTO> lineas = new ArrayList<>();
        AtomicInteger lineaNum = new AtomicInteger(1);
        for (DetalleVenta detalle : venta.getItems()) {
            DianDocumentoRequestDTO.LineaDTO linea = new DianDocumentoRequestDTO.LineaDTO();
            linea.setNumero(lineaNum.getAndIncrement());
            linea.setCodigoProducto(detalle.getCodigoProducto());
            linea.setDescripcion(detalle.getDescripcionProducto());
            linea.setCantidad(detalle.getCantidad());
            linea.setPrecioUnitario(detalle.getPrecioUnitario());
            linea.setDescuento(detalle.getDescuento());
            linea.setValorIva(detalle.getIva());

            // Calcular porcentaje de IVA
            BigDecimal baseLinea = detalle.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()))
                    .subtract(detalle.getDescuento());
            if (baseLinea.compareTo(BigDecimal.ZERO) > 0 && detalle.getIva().compareTo(BigDecimal.ZERO) > 0) {
                linea.setPorcentajeIva(detalle.getIva()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(baseLinea, 2, RoundingMode.HALF_UP));
            } else {
                linea.setPorcentajeIva(BigDecimal.ZERO);
            }

            linea.setTotalLinea(detalle.getTotal());
            lineas.add(linea);
        }
        req.setLineas(lineas);

        // ── Totales ──
        req.setBaseGravable(venta.getGravada());
        req.setTotalIva(venta.getIva());
        req.setTotalDescuento(venta.getDescuentos());
        req.setTotalFactura(venta.getTotalVenta());

        // ── Métodos de pago ──
        List<DianDocumentoRequestDTO.MetodoPagoDTO> metodosPago = new ArrayList<>();
        if (venta.getMetodosPago() != null) {
            for (VentaMetodoPago vmp : venta.getMetodosPago()) {
                DianDocumentoRequestDTO.MetodoPagoDTO mp = new DianDocumentoRequestDTO.MetodoPagoDTO();
                // Mapear sigla del método de pago al código DIAN
                mp.setMedioPago(mapearMedioPagoDian(vmp.getMetodoPago().getSigla()));
                mp.setMonto(vmp.getMonto());
                mp.setReferencia(vmp.getReferencia());
                metodosPago.add(mp);
            }
        }
        req.setMetodosPago(metodosPago);

        return req;
    }

    /**
     * Mapea la sigla del método de pago interno al código DIAN.
     * Códigos DIAN: 10=Efectivo, 48=Tarjeta crédito, 49=Tarjeta débito,
     *               47=Transferencia, ZZZ=Otro medio
     */
    private String mapearMedioPagoDian(String sigla) {
        if (sigla == null) return "ZZZ";
        return switch (sigla.toUpperCase()) {
            case "EF", "EFECTIVO" -> "10";
            case "TC", "TARJETA_CREDITO" -> "48";
            case "TD", "TARJETA_DEBITO" -> "49";
            case "TR", "TRANSFERENCIA" -> "47";
            default -> "ZZZ";
        };
    }

    private FacturaElectronicaResponseDTO mapToResponse(Facturas factura) {
        FacturaElectronicaResponseDTO resp = new FacturaElectronicaResponseDTO();
        resp.setFacturaId(factura.getFacturaId());
        resp.setNumeroFactura(factura.getNumeroFactura());
        resp.setCufe(factura.getCufe());
        resp.setEstadoDian(factura.getEstadoDian() != null ? factura.getEstadoDian().name() : null);
        resp.setMensajeDian(factura.getMensajeDian());
        resp.setFechaValidacion(factura.getFechaValidacionDian());
        resp.setTotalFactura(factura.getTotalFactura());
        resp.setQrData(factura.getQrData());
        resp.setTienePdf(factura.getPdfBase64() != null && !factura.getPdfBase64().isEmpty());
        return resp;
    }
}

