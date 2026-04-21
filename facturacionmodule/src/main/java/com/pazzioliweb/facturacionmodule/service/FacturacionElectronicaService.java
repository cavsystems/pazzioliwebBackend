package com.pazzioliweb.facturacionmodule.service;

import com.pazzioliweb.facturacionmodule.config.DianConfig;
import com.pazzioliweb.facturacionmodule.dtos.*;
import com.pazzioliweb.facturacionmodule.entity.Facturas;
import com.pazzioliweb.facturacionmodule.entity.MetodosPagoFacturas;
import com.pazzioliweb.facturacionmodule.entity.TipoTotalesFacturas;
import com.pazzioliweb.facturacionmodule.repositori.FacturasRepository;
import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresasback.repositori.EmpresaRepositori;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.ventasmodule.entity.DetalleVenta;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.entity.VentaMetodoPago;
import com.pazzioliweb.ventasmodule.repository.VentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FacturacionElectronicaService {

    private static final Logger log = LoggerFactory.getLogger(FacturacionElectronicaService.class);

    private final FacturasRepository facturasRepository;
    private final ProveedorFacturacionElectronica proveedorDian;
    private final VentaRepository ventaRepository;
    private final TercerosRepository tercerosRepository;
    private final MetodosPagoRepository metodosPagoRepository;
    private final EmpresaRepositori empresaRepositori;
    private final DianConfig dianConfig;

    @Autowired
    public FacturacionElectronicaService(FacturasRepository facturasRepository,
                                          ProveedorFacturacionElectronica proveedorDian,
                                          VentaRepository ventaRepository,
                                          TercerosRepository tercerosRepository,
                                          MetodosPagoRepository metodosPagoRepository,
                                          EmpresaRepositori empresaRepositori,
                                          DianConfig dianConfig) {
        this.facturasRepository = facturasRepository;
        this.proveedorDian = proveedorDian;
        this.ventaRepository = ventaRepository;
        this.tercerosRepository = tercerosRepository;
        this.metodosPagoRepository = metodosPagoRepository;
        this.empresaRepositori = empresaRepositori;
        this.dianConfig = dianConfig;
    }

    // ══════════════════════════════════════════════════════════
    //  PASO 3: Venta COMPLETADA → Factura → Proveedor DIAN
    // ══════════════════════════════════════════════════════════

    @Transactional
    public FacturaElectronicaResponseDTO generarDesdeVenta(GenerarFacturaRequestDTO request) {
        log.info("╔══════════════════════════════════════════════════════════╗");
        log.info("║  FACTURACIÓN ELECTRÓNICA - Generando desde venta       ║");
        log.info("╚══════════════════════════════════════════════════════════╝");
        log.info("📋 Request → ventaId: {}, comprobanteId: {}, cajaId: {}",
                request.getVentaId(), request.getComprobanteId(), request.getCajaId());

        // 1. Buscar la venta COMPLETADA con items y métodos de pago
        log.info("🔍 Paso 1: Buscando venta ID: {}...", request.getVentaId());
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> {
                    log.error("❌ Venta no encontrada: {}", request.getVentaId());
                    return new RuntimeException("Venta no encontrada: " + request.getVentaId());
                });
        log.info("✅ Venta encontrada → Número: {}, Estado: {}, Cliente: {}, Total: {}",
                venta.getNumeroVenta(), venta.getEstado(),
                venta.getCliente() != null ? venta.getCliente().getNombre1() : "SIN CLIENTE",
                venta.getTotalVenta());

        if (!"COMPLETADA".equals(venta.getEstado())) {
            log.error("❌ Venta no está COMPLETADA. Estado actual: {}", venta.getEstado());
            throw new RuntimeException("Solo se pueden facturar ventas COMPLETADAS. Estado actual: " + venta.getEstado());
        }

        // 2. Validar que no exista ya una factura para esta venta
        log.info("🔍 Paso 2: Verificando si ya existe factura para venta {}...", request.getVentaId());
        Optional<Facturas> facturaExistente = facturasRepository.findByVentaId(request.getVentaId());
        if (facturaExistente.isPresent()) {
            log.warn("⚠️ Ya existe factura para venta {} → Factura: {}", request.getVentaId(), facturaExistente.get().getNumeroFactura());
            throw new RuntimeException("Ya existe una factura para la venta ID: " + request.getVentaId()
                    + " - Factura: " + facturaExistente.get().getNumeroFactura());
        }
        log.info("✅ No existe factura previa para esta venta");

        // 3. Obtener siguiente consecutivo
        log.info("🔢 Paso 3: Obteniendo siguiente consecutivo para comprobante {}...", request.getComprobanteId());
        Integer siguienteConsecutivo = obtenerSiguienteConsecutivo(request.getComprobanteId());
        String prefijo = dianConfig.getResolucion().getPrefijo() != null
                ? dianConfig.getResolucion().getPrefijo() : "FE";
        String numeroFactura = prefijo + siguienteConsecutivo;
        log.info("✅ Consecutivo: {} → Número factura: {}", siguienteConsecutivo, numeroFactura);

        // 4. Crear factura en BD con TODOS los datos de la venta
        log.info("📝 Paso 4: Creando factura en BD...");
        Facturas factura = crearFacturaDesdeVenta(request, venta, siguienteConsecutivo, prefijo, numeroFactura);
        log.info("   → Tercero ID: {}, Fecha emisión: {}, Total: {}, Descuento: {}",
                factura.getTerceroId(), factura.getFechaEmision(), factura.getTotalFactura(), factura.getDescuento());
        log.info("   → TipoTotales: {}, MétodosPago: {}",
                factura.getTipoTotales() != null ? factura.getTipoTotales().size() : 0,
                factura.getMetodosPago() != null ? factura.getMetodosPago().size() : 0);
        factura = facturasRepository.save(factura);
        log.info("✅ Factura guardada en BD → ID: {}, Número: {}", factura.getFacturaId(), factura.getNumeroFactura());

        // 5. Armar el DTO completo para el proveedor DIAN
        log.info("📦 Paso 5: Armando request para DIAN...");
        DianDocumentoRequestDTO dianRequest = armarRequestDian(factura, venta);
        log.info("   → Emisor: {} ({})", 
                dianRequest.getEmisor() != null ? dianRequest.getEmisor().getRazonSocial() : "N/A",
                dianRequest.getEmisor() != null ? dianRequest.getEmisor().getNumeroIdentificacion() : "N/A");
        log.info("   → Receptor: {} ({})",
                dianRequest.getReceptor() != null ? dianRequest.getReceptor().getNombre() : "N/A",
                dianRequest.getReceptor() != null ? dianRequest.getReceptor().getNumeroIdentificacion() : "N/A");
        log.info("   → Líneas: {}, Base gravable: {}, IVA: {}, Total: {}",
                dianRequest.getLineas() != null ? dianRequest.getLineas().size() : 0,
                dianRequest.getBaseGravable(), dianRequest.getTotalIva(), dianRequest.getTotalFactura());
        log.info("   → Métodos de pago: {}", dianRequest.getMetodosPago() != null ? dianRequest.getMetodosPago().size() : 0);

        // 6. Enviar al proveedor de facturación electrónica
        log.info("🚀 Paso 6: Enviando a proveedor DIAN (ambiente: {})...", dianConfig.getAmbiente());
        DianDocumentoResponseDTO dianResponse;
        try {
            dianResponse = proveedorDian.enviarFactura(dianRequest);
            log.info("📨 Respuesta DIAN recibida → Exitoso: {}, CUFE: {}", dianResponse.isExitoso(), dianResponse.getCufe());
        } catch (Exception e) {
            log.error("❌ Error enviando a DIAN: {}", e.getMessage(), e);
            factura.setEstadoDian(Facturas.EstadoDian.RECHAZADA);
            factura.setMensajeDian("Error al enviar: " + e.getMessage());
            facturasRepository.save(factura);
            throw new RuntimeException("Error al enviar factura a la DIAN: " + e.getMessage(), e);
        }

        // 7. Guardar respuesta de la DIAN
        log.info("💾 Paso 7: Guardando respuesta DIAN en BD...");
        factura.setCufe(dianResponse.getCufe());
        factura.setQrData(dianResponse.getQrData());
        factura.setXmlFirmado(dianResponse.getXmlFirmado());
        factura.setPdfBase64(dianResponse.getPdfBase64());
        factura.setFechaValidacionDian(dianResponse.getFechaValidacion());
        factura.setEstadoDian(
                "SIMULADA".equals(dianResponse.getEstadoDian()) ? Facturas.EstadoDian.SIMULADA
                : dianResponse.isExitoso() ? Facturas.EstadoDian.AUTORIZADA
                : Facturas.EstadoDian.RECHAZADA);
        factura.setMensajeDian(dianResponse.getMensajeDian());
        facturasRepository.save(factura);

        log.info("╔══════════════════════════════════════════════════════════╗");
        log.info("║  RESULTADO FACTURACIÓN ELECTRÓNICA                     ║");
        log.info("╠══════════════════════════════════════════════════════════╣");
        log.info("║  Factura: {} (ID: {})", factura.getNumeroFactura(), factura.getFacturaId());
        log.info("║  CUFE: {}", factura.getCufe() != null ? factura.getCufe().substring(0, Math.min(40, factura.getCufe().length())) + "..." : "N/A");
        log.info("║  Estado DIAN: {}", factura.getEstadoDian());
        log.info("║  Mensaje: {}", factura.getMensajeDian());
        log.info("║  QR: {}", factura.getQrData() != null ? "SÍ" : "NO");
        log.info("║  XML: {}", factura.getXmlFirmado() != null ? "SÍ (" + factura.getXmlFirmado().length() + " chars)" : "NO");
        log.info("║  PDF: {}", factura.getPdfBase64() != null ? "SÍ" : "NO");
        log.info("╚══════════════════════════════════════════════════════════╝");

        return mapToResponse(factura);
    }

    @Transactional
    public FacturaElectronicaResponseDTO consultarEstadoDian(Integer facturaId) {
        log.info("🔍 Consultando estado DIAN para factura ID: {}", facturaId);
        Facturas factura = facturasRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));

        if (factura.getCufe() == null || factura.getCufe().isEmpty()) {
            log.warn("⚠️ Factura {} no tiene CUFE asignado", facturaId);
            throw new RuntimeException("La factura no tiene CUFE asignado");
        }

        log.info("📨 Consultando DIAN con CUFE: {}...", factura.getCufe().substring(0, Math.min(30, factura.getCufe().length())));
        DianDocumentoResponseDTO dianResponse = proveedorDian.consultarEstado(factura.getCufe());
        if (dianResponse.isExitoso()) {
            factura.setEstadoDian(Facturas.EstadoDian.AUTORIZADA);
        }
        factura.setMensajeDian(dianResponse.getMensajeDian());
        factura.setFechaValidacionDian(dianResponse.getFechaValidacion());
        facturasRepository.save(factura);
        log.info("✅ Estado actualizado → {}: {}", factura.getEstadoDian(), factura.getMensajeDian());

        return mapToResponse(factura);
    }

    public String obtenerPdfBase64(Integer facturaId) {
        Facturas factura = facturasRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));
        return factura.getPdfBase64();
    }

    /**
     * Reenvía una factura con estado RECHAZADA o PENDIENTE a la DIAN.
     */
    @Transactional
    public FacturaElectronicaResponseDTO reenviarFacturaDian(Integer facturaId) {
        log.info("🔄 Reenviando factura ID: {} a DIAN...", facturaId);
        Facturas factura = facturasRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));

        if (factura.getEstadoDian() == Facturas.EstadoDian.AUTORIZADA) {
            log.error("❌ La factura ya está AUTORIZADA por la DIAN");
            throw new RuntimeException("La factura ya está AUTORIZADA por la DIAN");
        }

        Venta venta = ventaRepository.findById(factura.getVentaId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + factura.getVentaId()));

        DianDocumentoRequestDTO dianRequest = armarRequestDian(factura, venta);

        DianDocumentoResponseDTO dianResponse;
        try {
            dianResponse = proveedorDian.enviarFactura(dianRequest);
            log.info("📨 Respuesta DIAN recibida → Exitoso: {}, CUFE: {}", dianResponse.isExitoso(), dianResponse.getCufe());
        } catch (Exception e) {
            log.error("❌ Error al reenviar a DIAN: {}", e.getMessage(), e);
            factura.setEstadoDian(Facturas.EstadoDian.RECHAZADA);
            factura.setMensajeDian("Error al reenviar: " + e.getMessage());
            facturasRepository.save(factura);
            throw new RuntimeException("Error al reenviar factura a la DIAN: " + e.getMessage(), e);
        }

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
        LocalDate fechaEmision = venta.getFechaEmision() != null ? venta.getFechaEmision() : LocalDate.now();
        factura.setFechaCreacion(LocalDateTime.now());
        factura.setFechaEmision(fechaEmision);
        factura.setFechaVencimiento(fechaEmision); // Contado
        factura.setPlazo(0);

        // Usuario y estado
        factura.setUsuarioIngresoId(1); // TODO: obtener del contexto de seguridad
        factura.setEstado(Facturas.EstadoFactura.ACTIVO);

        // Totales desde la venta
        factura.setDescuento(venta.getDescuentos() != null ? venta.getDescuentos().doubleValue() : 0.00);
        factura.setTotalFactura(venta.getTotalVenta() != null ? venta.getTotalVenta().doubleValue() : 0.00);
        factura.setSaldo(0.00); // Contado = saldo 0

        // Cajero (caja_id referencia cajeros)
        if (venta.getCajero() != null) {
            factura.setCajaId(venta.getCajero().getCajeroId());
        } else if (request.getCajaId() != null) {
            factura.setCajaId(request.getCajaId());
        }

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
        gravado.setBase(venta.getGravada() != null ? venta.getGravada() : BigDecimal.ZERO);
        gravado.setValor(venta.getGravada() != null ? venta.getGravada() : BigDecimal.ZERO);
        tipoTotales.add(gravado);

        TipoTotalesFacturas ivaTotal = new TipoTotalesFacturas();
        ivaTotal.setFactura(factura);
        ivaTotal.setTipoTotalId(2); // 2 = IVA
        ivaTotal.setBase(venta.getGravada() != null ? venta.getGravada() : BigDecimal.ZERO);
        ivaTotal.setValor(venta.getIva() != null ? venta.getIva() : BigDecimal.ZERO);
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

        // Resolución DIAN desde configuración
        req.setResolucionDian(dianConfig.getResolucion().getNumero());

        // ── Emisor: datos de la empresa desde BD ──
        DianDocumentoRequestDTO.EmisorDTO emisor = new DianDocumentoRequestDTO.EmisorDTO();
        try {
            Empresa empresa = empresaRepositori.findById((long) dianConfig.getEmpresaId())
                    .orElse(null);
            if (empresa != null) {
                emisor.setTipoIdentificacion(
                        empresa.getCodigotipoidentificacion() != null
                                ? String.valueOf(empresa.getCodigotipoidentificacion().getCodigoTipoIdentificacion())
                                : "31");
                emisor.setNumeroIdentificacion(empresa.getNumeroidentificacion());
                emisor.setDigitoVerificacion(empresa.getDigitoverificacion());
                emisor.setRazonSocial(empresa.getRazonsocial() != null
                        ? empresa.getRazonsocial()
                        : empresa.getNombrecomercial());
                emisor.setTelefono(empresa.getCelularempresa());
                emisor.setCorreo(empresa.getCorreoempresa());
                if (empresa.getCodigomunicipio() != null) {
                    emisor.setMunicipio(empresa.getCodigomunicipio().getMunicipio());
                }
                if (empresa.getCodigodepartamento() != null) {
                    emisor.setDepartamento(empresa.getCodigodepartamento().getDepartamento());
                }
                emisor.setPais("CO");
            } else {
                emisor.setTipoIdentificacion("31");
                emisor.setRazonSocial("EMPRESA NO CONFIGURADA");
            }
        } catch (Exception e) {
            emisor.setTipoIdentificacion("31");
            emisor.setRazonSocial("ERROR CARGANDO EMPRESA");
        }
        req.setEmisor(emisor);

        // ── Receptor: datos del cliente (tercero de la venta) ──
        Terceros cliente = venta.getCliente();
        DianDocumentoRequestDTO.ReceptorDTO receptor = new DianDocumentoRequestDTO.ReceptorDTO();
        if (cliente.getTipoIdentificacion() != null) {
            receptor.setTipoIdentificacion(String.valueOf(cliente.getTipoIdentificacion().getCodigo()));
        }
        receptor.setNumeroIdentificacion(cliente.getIdentificacion());
        receptor.setDigitoVerificacion(cliente.getDv());
        receptor.setNombre(cliente.getRazonSocial() != null && !cliente.getRazonSocial().isBlank()
                ? cliente.getRazonSocial()
                : (cliente.getNombre1() + " "
                   + (cliente.getNombre2() != null ? cliente.getNombre2() + " " : "")
                   + (cliente.getApellido1() != null ? cliente.getApellido1() + " " : "")
                   + (cliente.getApellido2() != null ? cliente.getApellido2() : "")).trim());
        receptor.setDireccion(cliente.getDireccion());
        receptor.setCorreo(cliente.getCorreo());
        if (cliente.getCiudad() != null) {
            receptor.setMunicipio(cliente.getCiudad().getMunicipio());
        }
        if (cliente.getDepartamento() != null) {
            receptor.setDepartamento(cliente.getDepartamento().getDepartamento());
        }
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
        req.setBaseGravable(venta.getGravada() != null ? venta.getGravada() : BigDecimal.ZERO);
        req.setTotalIva(venta.getIva() != null ? venta.getIva() : BigDecimal.ZERO);
        req.setTotalDescuento(venta.getDescuentos() != null ? venta.getDescuentos() : BigDecimal.ZERO);
        req.setTotalFactura(venta.getTotalVenta() != null ? venta.getTotalVenta() : BigDecimal.ZERO);

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

