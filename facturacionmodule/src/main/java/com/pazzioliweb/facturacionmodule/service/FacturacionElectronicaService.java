package com.pazzioliweb.facturacionmodule.service;

import com.pazzioliweb.commonbacken.events.FacturaAutorizadaEvent;
import com.pazzioliweb.facturacionmodule.config.DianConfig;
import com.pazzioliweb.facturacionmodule.dtos.*;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.service.AsientoContableService;
import com.pazzioliweb.comprobantesmodule.service.ConfiguracionContableService;
import com.pazzioliweb.facturacionmodule.entity.DocumentoElectronico;
import com.pazzioliweb.facturacionmodule.entity.Facturas;
import com.pazzioliweb.facturacionmodule.entity.MetodosPagoFacturas;
import com.pazzioliweb.facturacionmodule.entity.TipoTotalesFacturas;
import com.pazzioliweb.facturacionmodule.repositori.DocumentoElectronicoRepository;
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
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;
    private final DocumentoElectronicoRepository documentoElectronicoRepository;
    @Autowired private AsientoContableService asientoService;
    @Autowired private ConfiguracionContableService configContable;
    @Autowired private com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService asignacionComprobante;

    @Autowired
    public FacturacionElectronicaService(FacturasRepository facturasRepository,
                                          ProveedorFacturacionElectronica proveedorDian,
                                          VentaRepository ventaRepository,
                                          TercerosRepository tercerosRepository,
                                          MetodosPagoRepository metodosPagoRepository,
                                          EmpresaRepositori empresaRepositori,
                                          DianConfig dianConfig,
                                          ApplicationEventPublisher eventPublisher,
                                          DocumentoElectronicoRepository documentoElectronicoRepository) {
        this.facturasRepository = facturasRepository;
        this.proveedorDian = proveedorDian;
        this.ventaRepository = ventaRepository;
        this.tercerosRepository = tercerosRepository;
        this.metodosPagoRepository = metodosPagoRepository;
        this.empresaRepositori = empresaRepositori;
        this.dianConfig = dianConfig;
        this.eventPublisher = eventPublisher;
        this.documentoElectronicoRepository = documentoElectronicoRepository;
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
        factura.setEstadoDian(mapearEstadoDian(dianResponse));
        factura.setMensajeDian(dianResponse.getMensajeDian());
        facturasRepository.save(factura);

        // Publicar evento para que comprobantesmodule actualice el asiento contable
        // asociado a la venta con el CUFE y estado DIAN. Es desacoplado: si el
        // listener falla, no afecta la persistencia de la factura.
        try {
            eventPublisher.publishEvent(new FacturaAutorizadaEvent(
                this,
                factura.getVentaId(),
                factura.getFacturaId(),
                factura.getCufe(),
                factura.getEstadoDian() != null ? factura.getEstadoDian().name() : null,
                factura.getMensajeDian(),
                LocalDateTime.now()
            ));
        } catch (Exception ex) {
            log.warn("⚠️ Error publicando FacturaAutorizadaEvent (no crítico): {}", ex.getMessage());
        }

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

        DianDocumentoResponseDTO dianResponse;
        if (factura.getPrefijo() != null && factura.getConsecutivo() != null) {
            // Consulta por prefijo+folio (Facturatech identifica el documento así;
            // además funciona cuando el documento quedó EN_PROCESO y aún no hay CUFE)
            log.info("📨 Consultando proveedor por documento {}{}...", factura.getPrefijo(), factura.getConsecutivo());
            dianResponse = proveedorDian.consultarEstadoDocumento(factura.getPrefijo(), factura.getConsecutivo());
        } else if (factura.getCufe() != null && !factura.getCufe().isEmpty()) {
            log.info("📨 Consultando proveedor con CUFE: {}...", factura.getCufe().substring(0, Math.min(30, factura.getCufe().length())));
            dianResponse = proveedorDian.consultarEstado(factura.getCufe());
        } else {
            log.warn("⚠️ Factura {} no tiene CUFE ni prefijo/consecutivo asignado", facturaId);
            throw new RuntimeException("La factura no tiene CUFE ni numeración para consultar");
        }

        if (dianResponse.isExitoso()) {
            factura.setEstadoDian(Facturas.EstadoDian.AUTORIZADA);
            // Completar datos que hayan quedado pendientes (documento firmado después del envío)
            if (dianResponse.getCufe() != null && (factura.getCufe() == null || factura.getCufe().isEmpty())) {
                factura.setCufe(dianResponse.getCufe());
            }
            if (dianResponse.getXmlFirmado() != null) factura.setXmlFirmado(dianResponse.getXmlFirmado());
            if (dianResponse.getPdfBase64() != null) factura.setPdfBase64(dianResponse.getPdfBase64());
            if (dianResponse.getQrData() != null) factura.setQrData(dianResponse.getQrData());
        }
        factura.setMensajeDian(dianResponse.getMensajeDian());
        factura.setFechaValidacionDian(dianResponse.getFechaValidacion());
        facturasRepository.save(factura);
        log.info("✅ Estado actualizado → {}: {}", factura.getEstadoDian(), factura.getMensajeDian());

        // Replicar al asiento contable si el estado cambió a algo definitivo
        try {
            eventPublisher.publishEvent(new FacturaAutorizadaEvent(
                this,
                factura.getVentaId(),
                factura.getFacturaId(),
                factura.getCufe(),
                factura.getEstadoDian() != null ? factura.getEstadoDian().name() : null,
                factura.getMensajeDian(),
                LocalDateTime.now()
            ));
        } catch (Exception ex) {
            log.warn("⚠️ Error publicando FacturaAutorizadaEvent en re-consulta: {}", ex.getMessage());
        }

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
        factura.setEstadoDian(mapearEstadoDian(dianResponse));
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
        // Heredamos el comprobante de la venta (FC contado o VC crédito) en lugar
        // del que llegue en el request, para que la factura siempre refleje el tipo real.
        factura.setConsecutivo(consecutivo);
        Integer comprobanteIdVenta = (venta.getComprobante() != null && venta.getComprobante().getId() != null)
                ? venta.getComprobante().getId().intValue()
                : request.getComprobanteId();
        factura.setComprobanteId(comprobanteIdVenta);
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

        // caja_id en facturas referencia cajeros.cajero_id
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

        // ── Resolución DIAN: priorizar la del ComprobanteContable usado por la venta ──
        // (la global de DianConfig queda como fallback). Esto evita inconsistencia
        // entre la resolución validada al asignar consecutivo y la reportada en el XML.
        com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable comp = venta != null
                ? venta.getComprobante() : null;
        if (comp != null && comp.getResolucionDian() != null && !comp.getResolucionDian().isBlank()) {
            req.setResolucionDian(comp.getResolucionDian());
            req.setClaveTecnicaDian(comp.getClaveTecnicaDian());
            req.setFechaInicioResolucion(comp.getFechaInicioResolucion());
            req.setFechaFinResolucion(comp.getFechaFinResolucion());
            req.setConsecutivoDesde(comp.getConsecutivoDesde());
            req.setConsecutivoHasta(comp.getConsecutivoHasta());
        } else {
            req.setResolucionDian(dianConfig.getResolucion().getNumero());
        }

        // ── Emisor: datos de la empresa desde BD ──
        DianDocumentoRequestDTO.EmisorDTO emisor = armarEmisorDesdeEmpresa();
        try {
            Empresa empresa = empresaRepositori.findById((long) dianConfig.getEmpresaId())
                    .orElse(null);
            if (empresa != null) {
                if (empresa.getCodigomunicipio() != null) {
                    emisor.setMunicipio(empresa.getCodigomunicipio().getMunicipio());
                }
                if (empresa.getCodigodepartamento() != null) {
                    emisor.setDepartamento(empresa.getCodigodepartamento().getDepartamento());
                }
            }
        } catch (Exception ignored) {}
        req.setEmisor(emisor);

        // ── Receptor: datos del cliente (tercero de la venta) ──
        Terceros cliente = venta.getCliente();
        DianDocumentoRequestDTO.ReceptorDTO receptor = new DianDocumentoRequestDTO.ReceptorDTO();
        if (cliente.getTipoIdentificacion() != null) {
            // Código DIAN (13=CC, 31=NIT...), NO el PK interno de la tabla tipoidentificacion
            receptor.setTipoIdentificacion(String.valueOf(cliente.getTipoIdentificacion().getCodigoTipoIdentificacion()));
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
            receptor.setCodigoMunicipio(codigoDaneMunicipio(
                    cliente.getCiudad().getCodigoDepartamento(),
                    cliente.getCiudad().getCodigoMunicipio()));
        }
        if (cliente.getDepartamento() != null) {
            receptor.setDepartamento(cliente.getDepartamento().getDepartamento());
            receptor.setCodigoDepartamento(codigoDaneDepartamento(
                    cliente.getDepartamento().getCodigoDepartamento()));
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

    /** Código DANE del departamento a 2 dígitos (Facturatech tabla 34). Ej: 5 → "05". */
    private String codigoDaneDepartamento(int codigoDepartamento) {
        return String.format("%02d", codigoDepartamento);
    }

    /**
     * Código DANE del municipio a 5 dígitos (Facturatech tabla 35).
     * En BD el municipio guarda el código corto (ej. depto 5, mpio 1 → "05001");
     * si ya viene completo (≥ 4 dígitos) se normaliza a 5.
     */
    private String codigoDaneMunicipio(int codigoDepartamento, int codigoMunicipio) {
        if (codigoMunicipio >= 1000) {
            return String.format("%05d", codigoMunicipio);
        }
        return String.format("%02d%03d", codigoDepartamento, codigoMunicipio);
    }

    /**
     * Mapea el estado que reporta el proveedor al enum de la factura.
     * EN_PROCESO (Facturatech aún validando) se guarda como ENVIADA, no como rechazo.
     */
    private Facturas.EstadoDian mapearEstadoDian(DianDocumentoResponseDTO resp) {
        if ("SIMULADA".equals(resp.getEstadoDian())) return Facturas.EstadoDian.SIMULADA;
        if ("EN_PROCESO".equals(resp.getEstadoDian())) return Facturas.EstadoDian.ENVIADA;
        return resp.isExitoso() ? Facturas.EstadoDian.AUTORIZADA : Facturas.EstadoDian.RECHAZADA;
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

    // ═══════════════════════════════════════════════════════════════════
    //  PASO 4: Tiquete POS Electrónico (TPOS) — documento equivalente
    //          para ventas a clientes NO identificados / consumidor final
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Genera un Tiquete POS Electrónico a partir de una venta.
     * A diferencia de la FE, no requiere datos completos del cliente.
     */
    @Transactional
    public DianDocumentoResponseDTO generarTiquetePOS(Long ventaId) {
        log.info("══════ Generando Tiquete POS Electrónico (TPOS) ═══════");
        log.info("Venta ID: {}", ventaId);

        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + ventaId));

        DianDocumentoRequestDTO req = construirRequestDianBase(venta);
        req.setTipoDocumento("20");  // 20 = Documento equivalente Tiquete POS
        // Numeración PERSISTENTE via AsignacionComprobanteService (no contador en memoria).
        Integer cajeroIdTpos = (venta != null && venta.getCajero() != null) ? venta.getCajero().getCajeroId() : null;
        com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService.Resultado rTpos =
                asignarComprobanteAuto(cajeroIdTpos,
                        com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante.TPOS);
        req.setPrefijo(rTpos.getComprobante().getPrefijo());
        int consec = rTpos.getConsecutivo();
        req.setConsecutivo(consec);
        // Propagar resolución del comprobante TPOS al request
        com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable compTpos = rTpos.getComprobante();
        if (compTpos.getResolucionDian() != null) {
            req.setResolucionDian(compTpos.getResolucionDian());
            req.setClaveTecnicaDian(compTpos.getClaveTecnicaDian());
            req.setFechaInicioResolucion(compTpos.getFechaInicioResolucion());
            req.setFechaFinResolucion(compTpos.getFechaFinResolucion());
            req.setConsecutivoDesde(compTpos.getConsecutivoDesde());
            req.setConsecutivoHasta(compTpos.getConsecutivoHasta());
        }

        DianDocumentoResponseDTO resp = proveedorDian.enviarFactura(req);

        // Persistir
        try {
            DocumentoElectronico doc = new DocumentoElectronico();
            doc.setTipo("TPOS");
            doc.setNumero("TPOS-" + consec);
            doc.setCufe(resp.getCufe());
            doc.setFechaEmision(req.getFechaEmision());
            doc.setTerceroIdentificacion(req.getReceptor() != null ? req.getReceptor().getNumeroIdentificacion() : null);
            doc.setTerceroNombre(req.getReceptor() != null ? req.getReceptor().getNombre() : null);
            doc.setDocumentoReferenciaId(venta.getId());
            doc.setDocumentoReferenciaTipo("VENTA");
            doc.setBaseGravable(req.getBaseGravable());
            doc.setIva(req.getTotalIva());
            doc.setTotal(req.getTotalFactura());
            doc.setConcepto("Tiquete POS — Venta " + venta.getNumeroVenta());
            doc.setEstadoDian(resp.getEstadoDian());
            doc.setMensajeDian(resp.getMensajeDian());
            doc.setQrData(resp.getQrData());
            doc.setXmlFirmado(resp.getXmlFirmado());
            doc.setFechaValidacionDian(resp.getFechaValidacion());
            documentoElectronicoRepository.save(doc);
            log.info("TPOS persistido: {}", doc.getNumero());
            // Generar asiento contable asociado
            Long aId = generarAsientoTPOS(doc, venta);
            if (aId != null) {
                doc.setAsientoId(aId);
                documentoElectronicoRepository.save(doc);
            }
        } catch (Exception ex) {
            log.warn("[TPOS] Error persistiendo (no crítico): {}", ex.getMessage());
        }

        return resp;
    }

    /**
     * Genera una Nota Débito Electrónica a partir de una factura existente
     * (cobro adicional, intereses de mora, ajuste al alza, etc).
     */
    @Transactional
    public DianDocumentoResponseDTO generarNotaDebito(Integer facturaId, Integer codigoConcepto,
                                                       String razon, BigDecimal monto, BigDecimal ivaMonto) {
        log.info("══════ Generando Nota Débito Electrónica (ND) ═══════");
        log.info("Factura ID: {}, Concepto: {}, Monto: {}", facturaId, codigoConcepto, monto);

        Facturas factura = facturasRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + facturaId));
        Venta venta = ventaRepository.findById(factura.getVentaId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + factura.getVentaId()));

        DianDocumentoRequestDTO req = construirRequestDianBase(venta);
        req.setTipoDocumento("92");  // 92 = Nota Débito
        Integer cajeroIdNd = (venta != null && venta.getCajero() != null) ? venta.getCajero().getCajeroId() : null;
        com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService.Resultado rNd =
                asignarComprobanteAuto(cajeroIdNd,
                        com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante.ND);
        req.setPrefijo(rNd.getComprobante().getPrefijo());
        req.setConsecutivo(rNd.getConsecutivo());
        com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable compNd = rNd.getComprobante();
        if (compNd.getResolucionDian() != null) {
            req.setResolucionDian(compNd.getResolucionDian());
            req.setClaveTecnicaDian(compNd.getClaveTecnicaDian());
            req.setFechaInicioResolucion(compNd.getFechaInicioResolucion());
            req.setFechaFinResolucion(compNd.getFechaFinResolucion());
            req.setConsecutivoDesde(compNd.getConsecutivoDesde());
            req.setConsecutivoHasta(compNd.getConsecutivoHasta());
        }
        req.setCodigoConcepto(codigoConcepto != null ? codigoConcepto : 4);
        req.setRazonConcepto(razon);

        // Referencia a la factura original
        DianDocumentoRequestDTO.DocumentoReferenciaDTO ref = new DianDocumentoRequestDTO.DocumentoReferenciaDTO();
        ref.setNumeroDocumento(factura.getNumeroFactura());
        ref.setCufeOriginal(factura.getCufe());
        ref.setFechaEmisionOriginal(factura.getFechaEmision());
        ref.setTipoDocumentoOriginal("01");
        req.setDocumentoReferencia(ref);

        // Sobrescribir totales con monto del cargo extra
        BigDecimal m = monto != null ? monto : BigDecimal.ZERO;
        BigDecimal i = ivaMonto != null ? ivaMonto : BigDecimal.ZERO;
        req.setBaseGravable(m.subtract(i).max(BigDecimal.ZERO));
        req.setTotalIva(i);
        req.setTotalFactura(m);
        req.setTotalDescuento(BigDecimal.ZERO);

        // Una sola línea con el concepto
        java.util.List<DianDocumentoRequestDTO.LineaDTO> lineas = new ArrayList<>();
        DianDocumentoRequestDTO.LineaDTO l = new DianDocumentoRequestDTO.LineaDTO();
        l.setNumero(1);
        l.setCodigoProducto("ND");
        l.setDescripcion(razon != null ? razon : "Cargo adicional");
        l.setCantidad(1);
        l.setPrecioUnitario(m.subtract(i));
        l.setDescuento(BigDecimal.ZERO);
        l.setValorIva(i);
        l.setTotalLinea(m);
        lineas.add(l);
        req.setLineas(lineas);

        int consec = req.getConsecutivo();
        DianDocumentoResponseDTO resp = proveedorDian.enviarFactura(req);

        // Persistir
        try {
            DocumentoElectronico doc = new DocumentoElectronico();
            doc.setTipo("ND");
            doc.setNumero("ND-" + consec);
            doc.setCufe(resp.getCufe());
            doc.setFechaEmision(req.getFechaEmision());
            doc.setTerceroIdentificacion(req.getReceptor() != null ? req.getReceptor().getNumeroIdentificacion() : null);
            doc.setTerceroNombre(req.getReceptor() != null ? req.getReceptor().getNombre() : null);
            doc.setDocumentoReferenciaId(Long.valueOf(facturaId));
            doc.setDocumentoReferenciaTipo("FACTURA");
            doc.setBaseGravable(req.getBaseGravable());
            doc.setIva(req.getTotalIva());
            doc.setTotal(req.getTotalFactura());
            doc.setConcepto(razon);
            doc.setCodigoConceptoDian(codigoConcepto);
            doc.setEstadoDian(resp.getEstadoDian());
            doc.setMensajeDian(resp.getMensajeDian());
            doc.setQrData(resp.getQrData());
            doc.setXmlFirmado(resp.getXmlFirmado());
            doc.setFechaValidacionDian(resp.getFechaValidacion());
            documentoElectronicoRepository.save(doc);
            log.info("ND persistido: {}", doc.getNumero());
            // Generar asiento contable
            Long aId = generarAsientoND(doc, factura);
            if (aId != null) {
                doc.setAsientoId(aId);
                documentoElectronicoRepository.save(doc);
            }
        } catch (Exception ex) {
            log.warn("[ND] Error persistiendo (no crítico): {}", ex.getMessage());
        }

        return resp;
    }

    /**
     * Genera un Documento Soporte de compras a NO obligados a facturar.
     * El proveedor en este caso es típicamente persona natural / régimen simplificado.
     */
    @Transactional
    public DianDocumentoResponseDTO generarDocumentoSoporte(String proveedorIdentificacion,
                                                             String proveedorNombre,
                                                             BigDecimal base,
                                                             BigDecimal iva,
                                                             BigDecimal total,
                                                             String concepto) {
        return generarDocumentoSoporte(proveedorIdentificacion, proveedorNombre, base, iva, total, concepto,
                "SERVICIOS", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Versión completa con retenciones y tipo de compra (productos/servicios).
     */
    @Transactional
    public DianDocumentoResponseDTO generarDocumentoSoporte(String proveedorIdentificacion,
                                                             String proveedorNombre,
                                                             BigDecimal base,
                                                             BigDecimal iva,
                                                             BigDecimal total,
                                                             String concepto,
                                                             String tipoCompra,
                                                             BigDecimal retencionFuente,
                                                             BigDecimal retencionIva,
                                                             BigDecimal retencionIca) {
        log.info("══════ Generando Documento Soporte (DS) ═══════");
        log.info("Proveedor: {} - {}, Total: {}", proveedorIdentificacion, proveedorNombre, total);

        DianDocumentoRequestDTO req = new DianDocumentoRequestDTO();
        req.setTipoDocumento("05");  // 05 = Documento Soporte
        // Numeración persistente del comprobante DS configurado (no contador en memoria).
        com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService.Resultado rDs =
                asignacionComprobante.asignarSinCajero(
                        com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante.DS);
        req.setPrefijo(rDs.getComprobante().getPrefijo());
        req.setConsecutivo(rDs.getConsecutivo());
        com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable compDs = rDs.getComprobante();
        aplicarResolucionDs(req, compDs, "Documento Soporte");
        req.setFechaEmision(LocalDate.now());
        // IBS_1/IBS_2: fecha de compra y forma de generación. "Por operación" (1) exige
        // que la fecha de compra sea la misma de emisión del documento.
        req.setFechaCompra(LocalDate.now());
        req.setFormaPago("1");

        // Adquiriente (nodo ADQ) = MI empresa: en el DS es ella quien expide el documento
        req.setEmisor(armarEmisorDesdeEmpresa());

        // Proveedor no obligado a facturar (nodo PRO)
        req.setReceptor(armarProveedorDs(proveedorIdentificacion, proveedorNombre));

        req.setBaseGravable(base != null ? base : BigDecimal.ZERO);
        req.setTotalIva(iva != null ? iva : BigDecimal.ZERO);
        req.setTotalFactura(total != null ? total : BigDecimal.ZERO);
        req.setTotalDescuento(BigDecimal.ZERO);

        // Retenciones practicadas: se informan como TIM/IMP (06 ReteRenta, 05 ReteIVA).
        // La Tabla 11 del anexo DS no contempla ReteICA, así que no viaja en el XML.
        BigDecimal rf = retencionFuente != null ? retencionFuente : BigDecimal.ZERO;
        BigDecimal ri = retencionIva != null ? retencionIva : BigDecimal.ZERO;
        BigDecimal rc = retencionIca != null ? retencionIca : BigDecimal.ZERO;
        req.setRetencionFuente(rf);
        req.setRetencionIva(ri);
        if (rc.compareTo(BigDecimal.ZERO) > 0) {
            log.warn("[DS] ReteICA de ${} se registra en contabilidad pero no se reporta en el " +
                    "documento soporte: el anexo DIAN sólo admite los tributos 01 (IVA), " +
                    "05 (ReteIVA) y 06 (ReteRenta)", rc);
        }

        // Una línea con el concepto
        java.util.List<DianDocumentoRequestDTO.LineaDTO> lineas = new ArrayList<>();
        DianDocumentoRequestDTO.LineaDTO l = new DianDocumentoRequestDTO.LineaDTO();
        l.setNumero(1);
        l.setCodigoProducto("DS");
        l.setDescripcion(concepto != null ? concepto : "Compra a no facturador");
        l.setCantidad(1);
        l.setPrecioUnitario(req.getBaseGravable());
        l.setDescuento(BigDecimal.ZERO);
        l.setValorIva(req.getTotalIva());
        l.setTotalLinea(req.getTotalFactura());
        lineas.add(l);
        req.setLineas(lineas);

        // Número legal del documento = prefijo + consecutivo (igual al ENC_6 enviado a la DIAN)
        String numeroDs = req.getPrefijo() + req.getConsecutivo();
        DianDocumentoResponseDTO resp = proveedorDian.enviarFactura(req);

        // Persistir
        try {
            DocumentoElectronico doc = new DocumentoElectronico();
            doc.setTipo("DS");
            doc.setNumero(numeroDs);
            doc.setCufe(resp.getCufe());
            doc.setFechaEmision(req.getFechaEmision());
            doc.setTerceroIdentificacion(proveedorIdentificacion);
            doc.setTerceroNombre(proveedorNombre);
            doc.setBaseGravable(req.getBaseGravable());
            doc.setIva(req.getTotalIva());
            doc.setTotal(req.getTotalFactura());
            doc.setConcepto(concepto);
            doc.setEstadoDian(resp.getEstadoDian());
            doc.setMensajeDian(resp.getMensajeDian());
            doc.setQrData(resp.getQrData());
            doc.setXmlFirmado(resp.getXmlFirmado());
            doc.setFechaValidacionDian(resp.getFechaValidacion());
            // Tipo de compra y retenciones
            doc.setTipoCompra(tipoCompra != null ? tipoCompra : "SERVICIOS");
            BigDecimal totalRet = rf.add(ri).add(rc);
            doc.setRetencionFuente(rf);
            doc.setRetencionIva(ri);
            doc.setRetencionIca(rc);
            doc.setTotalRetenciones(totalRet);
            doc.setTotalPagar(req.getTotalFactura().subtract(totalRet));
            documentoElectronicoRepository.save(doc);
            log.info("DS persistido: {} - Retenciones: ${}, Neto: ${}",
                    doc.getNumero(), totalRet, doc.getTotalPagar());
            // Generar asiento contable
            Long aId = generarAsientoDS(doc);
            if (aId != null) {
                doc.setAsientoId(aId);
                documentoElectronicoRepository.save(doc);
            }
        } catch (Exception ex) {
            log.warn("[DS] Error persistiendo (no crítico): {}", ex.getMessage());
        }

        // Incluir el número DS en la respuesta para mostrarlo en el frontend
        resp.setNumero(numeroDs);
        return resp;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Nota de Ajuste al Documento Soporte (tipo 95)
    //  Es el único mecanismo para anular o corregir un DS ya autorizado.
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Emite una Nota de Ajuste (tipo 95) que anula o corrige un Documento Soporte.
     * Los nodos PRO/ADQ/TOT/ITE deben ser idénticos a los del DS referenciado, por lo
     * que se reconstruyen desde el documento guardado.
     *
     * @param codigoCorreccion Tabla 42: 1=Devolución parcial, 2=Anulación (default),
     *                         3=Rebaja o descuento, 4=Ajuste de precio, 5=Otros.
     */
    @Transactional
    public DianDocumentoResponseDTO generarNotaAjusteDocumentoSoporte(Long documentoSoporteId,
                                                                       Integer codigoCorreccion,
                                                                       String razon) {
        log.info("══════ Generando Nota de Ajuste al Documento Soporte ═══════");

        DocumentoElectronico ds = documentoElectronicoRepository.findById(documentoSoporteId)
                .orElseThrow(() -> new RuntimeException("Documento soporte no encontrado: " + documentoSoporteId));
        if (!"DS".equals(ds.getTipo())) {
            throw new IllegalStateException("El documento " + ds.getNumero() + " no es un Documento Soporte (tipo "
                    + ds.getTipo() + "); use la nota crédito electrónica para facturas de venta.");
        }
        if (ds.getCufe() == null || ds.getCufe().isBlank() || ds.getCufe().startsWith("SIMULADO-")) {
            throw new IllegalStateException("El documento soporte " + ds.getNumero() +
                    " no tiene CUDS autorizado por la DIAN: no se puede referenciar en una nota de ajuste " +
                    "(nodo REF_4 obligatorio).");
        }

        int concepto = codigoCorreccion != null ? codigoCorreccion : 2;

        DianDocumentoRequestDTO req = new DianDocumentoRequestDTO();
        req.setTipoDocumento("95");

        // Numeración propia: la nota de ajuste tiene su propio prefijo y resolución.
        com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService.Resultado rNa =
                asignacionComprobante.asignarSinCajero(
                        com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante.NADS);
        req.setPrefijo(rNa.getComprobante().getPrefijo());
        req.setConsecutivo(rNa.getConsecutivo());
        aplicarResolucionDs(req, rNa.getComprobante(), "Nota de Ajuste al Documento Soporte");

        req.setFechaEmision(LocalDate.now());
        req.setFormaPago("1");
        req.setCodigoConcepto(concepto);
        req.setRazonConcepto(razon);

        req.setEmisor(armarEmisorDesdeEmpresa());
        req.setReceptor(armarProveedorDs(ds.getTerceroIdentificacion(), ds.getTerceroNombre()));

        // Totales idénticos al DS anulado
        req.setBaseGravable(ds.getBaseGravable() != null ? ds.getBaseGravable() : BigDecimal.ZERO);
        req.setTotalIva(ds.getIva() != null ? ds.getIva() : BigDecimal.ZERO);
        req.setTotalFactura(ds.getTotal() != null ? ds.getTotal() : BigDecimal.ZERO);
        req.setTotalDescuento(BigDecimal.ZERO);

        java.util.List<DianDocumentoRequestDTO.LineaDTO> lineas = new ArrayList<>();
        DianDocumentoRequestDTO.LineaDTO l = new DianDocumentoRequestDTO.LineaDTO();
        l.setNumero(1);
        l.setCodigoProducto("DS");
        l.setDescripcion(ds.getConcepto() != null ? ds.getConcepto() : "Compra a no facturador");
        l.setCantidad(1);
        l.setPrecioUnitario(req.getBaseGravable());
        l.setDescuento(BigDecimal.ZERO);
        l.setValorIva(req.getTotalIva());
        l.setTotalLinea(req.getTotalFactura());
        lineas.add(l);
        req.setLineas(lineas);

        // REF: documento soporte referenciado (REF_4 = CUDS, no CUFE)
        DianDocumentoRequestDTO.DocumentoReferenciaDTO ref = new DianDocumentoRequestDTO.DocumentoReferenciaDTO();
        ref.setNumeroDocumento(ds.getNumero());
        ref.setCufeOriginal(ds.getCufe());
        ref.setFechaEmisionOriginal(ds.getFechaEmision());
        ref.setTipoDocumentoOriginal("05");
        req.setDocumentoReferencia(ref);

        String numeroNa = req.getPrefijo() + req.getConsecutivo();
        DianDocumentoResponseDTO resp = proveedorDian.enviarFactura(req);

        try {
            DocumentoElectronico na = new DocumentoElectronico();
            na.setTipo("NADS");
            na.setNumero(numeroNa);
            na.setCufe(resp.getCufe());
            na.setFechaEmision(req.getFechaEmision());
            na.setTerceroIdentificacion(ds.getTerceroIdentificacion());
            na.setTerceroNombre(ds.getTerceroNombre());
            na.setDocumentoReferenciaId(ds.getId());
            na.setDocumentoReferenciaTipo("DS");
            na.setBaseGravable(req.getBaseGravable());
            na.setIva(req.getTotalIva());
            na.setTotal(req.getTotalFactura());
            na.setConcepto(razon);
            na.setCodigoConceptoDian(concepto);
            na.setEstadoDian(resp.getEstadoDian());
            na.setMensajeDian(resp.getMensajeDian());
            na.setQrData(resp.getQrData());
            na.setXmlFirmado(resp.getXmlFirmado());
            na.setFechaValidacionDian(resp.getFechaValidacion());
            documentoElectronicoRepository.save(na);

            // Si la nota anula el DS (concepto 2), marcarlo como anulado
            if (concepto == 2 && resp.isExitoso()) {
                ds.setEstado("ANULADO");
                documentoElectronicoRepository.save(ds);
            }
            log.info("Nota de ajuste al DS persistida: {} → anula/corrige {}", numeroNa, ds.getNumero());
        } catch (Exception ex) {
            log.warn("[NADS] Error persistiendo (no crítico): {}", ex.getMessage());
        }

        resp.setNumero(numeroNa);
        return resp;
    }

    /**
     * Copia al request la resolución DIAN del comprobante (nodo DRF, obligatorio en el DS)
     * y falla con un mensaje claro si no está configurada — Facturatech devuelve 409 si el
     * DRF no coincide con la resolución registrada en su plataforma.
     */
    private void aplicarResolucionDs(DianDocumentoRequestDTO req,
                                      com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable comp,
                                      String nombreDoc) {
        if (comp.getResolucionDian() == null || comp.getResolucionDian().isBlank()
                || comp.getFechaInicioResolucion() == null || comp.getFechaFinResolucion() == null
                || comp.getConsecutivoDesde() == null || comp.getConsecutivoHasta() == null) {
            throw new IllegalStateException("El comprobante '" + comp.getPrefijo() + "' (" + nombreDoc +
                    ") no tiene la resolución DIAN completa. Configure número de autorización, " +
                    "fechas de vigencia y rango de numeración en Contabilidad → Comprobantes: " +
                    "el nodo DRF es obligatorio y Facturatech rechaza el documento sin él.");
        }
        req.setResolucionDian(comp.getResolucionDian());
        req.setClaveTecnicaDian(comp.getClaveTecnicaDian());
        req.setFechaInicioResolucion(comp.getFechaInicioResolucion());
        req.setFechaFinResolucion(comp.getFechaFinResolucion());
        req.setConsecutivoDesde(comp.getConsecutivoDesde());
        req.setConsecutivoHasta(comp.getConsecutivoHasta());
    }

    /**
     * Construye el nodo PRO del Documento Soporte: el proveedor NO obligado a facturar.
     * PRO_10/11/13/19/23 (dirección, departamento, municipio y sus códigos DANE) son
     * obligatorios para operaciones con residentes, así que se toman del tercero registrado.
     */
    private DianDocumentoRequestDTO.ReceptorDTO armarProveedorDs(String identificacion, String nombre) {
        DianDocumentoRequestDTO.ReceptorDTO p = new DianDocumentoRequestDTO.ReceptorDTO();
        p.setNumeroIdentificacion(identificacion);
        p.setNombre(nombre);
        p.setTipoIdentificacion("13"); // 13 = CC, típico de persona natural no obligada
        p.setCodigoPais("CO");
        p.setNombrePais("COLOMBIA");
        p.setResponsabilidadFiscal("R-99-PN"); // no obligado a facturar
        p.setResponsableIva(false);

        Terceros t = identificacion != null && !identificacion.isBlank()
                ? tercerosRepository.findByIdentificacion(identificacion).orElse(null)
                : null;
        if (t == null) {
            log.warn("[DS] El proveedor {} no está registrado en terceros: el documento soporte " +
                    "irá sin dirección ni municipio (PRO_10/11/13/19/23) y será rechazado", identificacion);
            return p;
        }

        if (t.getTipoIdentificacion() != null) {
            p.setTipoIdentificacion(String.valueOf(t.getTipoIdentificacion().getCodigoTipoIdentificacion()));
        }
        String razon = t.getRazonSocial() != null && !t.getRazonSocial().isBlank()
                ? t.getRazonSocial()
                : String.join(" ", Arrays.stream(new String[]{
                        t.getNombre1(), t.getNombre2(), t.getApellido1(), t.getApellido2()})
                        .filter(s -> s != null && !s.isBlank()).toList());
        if (!razon.isBlank()) {
            p.setNombre(razon);
        }
        p.setDigitoVerificacion(t.getDv());
        p.setDireccion(t.getDireccion());
        p.setCorreo(t.getCorreo());
        p.setCodigoPostal(t.getCodigoPostal());
        if (t.getCiudad() != null) {
            p.setMunicipio(t.getCiudad().getMunicipio());
            p.setCodigoMunicipio(codigoDaneMunicipio(
                    t.getCiudad().getCodigoDepartamento(), t.getCiudad().getCodigoMunicipio()));
        }
        if (t.getDepartamento() != null) {
            p.setDepartamento(t.getDepartamento().getDepartamento());
            p.setCodigoDepartamento(codigoDaneDepartamento(t.getDepartamento().getCodigoDepartamento()));
        }
        if (t.getTipoPersona() != null) {
            // "Juridica" / "Natural" → PRO_1 (Tabla 20)
            p.setTipoContribuyente(t.getTipoPersona().getNombre());
        }
        if (t.getRegimen() != null) {
            // Régimen 47 (simple) → O-47; el resto no es un código válido de TAC_1
            // en el anexo DS, así que el generador cae a R-99-PN.
            p.setResponsabilidadFiscal("O-" + t.getRegimen().getCodigoRegimen());
        }
        return p;
    }

    /** Construye el request base con datos del emisor + receptor desde una venta. */
    private DianDocumentoRequestDTO construirRequestDianBase(Venta venta) {
        DianDocumentoRequestDTO req = new DianDocumentoRequestDTO();
        req.setFechaEmision(venta.getFechaEmision());
        req.setFormaPago("1");

        // Emisor
        req.setEmisor(armarEmisorDesdeEmpresa());

        // Receptor (cliente de la venta o consumidor final)
        DianDocumentoRequestDTO.ReceptorDTO receptor = new DianDocumentoRequestDTO.ReceptorDTO();
        Terceros cliente = venta.getCliente();
        if (cliente != null) {
            if (cliente.getTipoIdentificacion() != null) {
                // Código DIAN (13=CC, 31=NIT...), NO el PK interno de la tabla tipoidentificacion
                receptor.setTipoIdentificacion(String.valueOf(cliente.getTipoIdentificacion().getCodigoTipoIdentificacion()));
            }
            receptor.setNumeroIdentificacion(cliente.getIdentificacion());
            receptor.setDigitoVerificacion(cliente.getDv());
            receptor.setNombre(cliente.getRazonSocial() != null && !cliente.getRazonSocial().isBlank()
                    ? cliente.getRazonSocial()
                    : cliente.getNombre1());
            receptor.setDireccion(cliente.getDireccion());
            receptor.setCorreo(cliente.getCorreo());
        } else {
            // Consumidor final (para TPOS sin cliente identificado)
            receptor.setTipoIdentificacion("13");
            receptor.setNumeroIdentificacion("222222222222");
            receptor.setNombre("CONSUMIDOR FINAL");
        }
        req.setReceptor(receptor);

        // Líneas
        java.util.List<DianDocumentoRequestDTO.LineaDTO> lineas = new ArrayList<>();
        AtomicInteger num = new AtomicInteger(1);
        for (DetalleVenta d : venta.getItems()) {
            DianDocumentoRequestDTO.LineaDTO l = new DianDocumentoRequestDTO.LineaDTO();
            l.setNumero(num.getAndIncrement());
            l.setCodigoProducto(d.getCodigoProducto());
            l.setDescripcion(d.getDescripcionProducto());
            l.setCantidad(d.getCantidad());
            l.setPrecioUnitario(d.getPrecioUnitario());
            l.setDescuento(d.getDescuento() != null ? d.getDescuento() : BigDecimal.ZERO);
            l.setValorIva(d.getIva() != null ? d.getIva() : BigDecimal.ZERO);
            l.setTotalLinea(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
            lineas.add(l);
        }
        req.setLineas(lineas);

        BigDecimal totalVenta = venta.getTotalVenta() != null ? venta.getTotalVenta() : BigDecimal.ZERO;
        BigDecimal totalIva = venta.getIva() != null ? venta.getIva() : BigDecimal.ZERO;
        req.setBaseGravable(totalVenta.subtract(totalIva).max(BigDecimal.ZERO));
        req.setTotalIva(totalIva);
        req.setTotalFactura(totalVenta);
        req.setTotalDescuento(BigDecimal.ZERO);
        req.setResolucionDian(dianConfig.getResolucion().getNumero());

        return req;
    }

    private DianDocumentoRequestDTO.EmisorDTO armarEmisorDesdeEmpresa() {
        DianDocumentoRequestDTO.EmisorDTO emisor = new DianDocumentoRequestDTO.EmisorDTO();
        try {
            Empresa empresa = empresaRepositori.findById((long) dianConfig.getEmpresaId()).orElse(null);
            if (empresa != null) {
                emisor.setTipoIdentificacion(empresa.getCodigotipoidentificacion() != null
                        ? String.valueOf(empresa.getCodigotipoidentificacion().getCodigoTipoIdentificacion())
                        : "31");
                emisor.setNumeroIdentificacion(empresa.getNumeroidentificacion());
                emisor.setDigitoVerificacion(empresa.getDigitoverificacion());
                emisor.setRazonSocial(empresa.getRazonsocial() != null ? empresa.getRazonsocial() : empresa.getNombrecomercial());
                emisor.setNombreComercial(empresa.getNombrecomercial());
                emisor.setDireccion(empresa.getDireccion());
                emisor.setTelefono(empresa.getCelularempresa());
                emisor.setCorreo(empresa.getCorreoempresa());
                emisor.setPais("CO");
                emisor.setCodigoPostal(empresa.getCodigopostal());
                // Ubicación con códigos DANE (Facturatech tablas 34/35)
                if (empresa.getCodigomunicipio() != null) {
                    emisor.setMunicipio(empresa.getCodigomunicipio().getMunicipio());
                    emisor.setCodigoMunicipio(codigoDaneMunicipio(
                            empresa.getCodigomunicipio().getCodigoDepartamento(),
                            empresa.getCodigomunicipio().getCodigoMunicipio()));
                }
                if (empresa.getCodigodepartamento() != null) {
                    emisor.setDepartamento(empresa.getCodigodepartamento().getDepartamento());
                    emisor.setCodigoDepartamento(codigoDaneDepartamento(
                            empresa.getCodigodepartamento().getCodigoDepartamento()));
                }
                // Datos fiscales DIAN (TaxLevelCode)
                emisor.setResponsabilidadFiscal(empresa.getResponsabilidadFiscal());
                emisor.setTipoContribuyente(empresa.getTipoContribuyente());
                emisor.setGranContribuyente(empresa.getGranContribuyente());
                emisor.setAutorretenedor(empresa.getAutorretenedor());
                emisor.setResponsableIva(empresa.getResponsableIva());
                // Régimen IVA (TaxScheme.ID): "01"=IVA general, "ZZ"=No aplica.
                emisor.setCodigoRegimen(Boolean.TRUE.equals(empresa.getResponsableIva()) ? "01" : "ZZ");
            } else {
                emisor.setTipoIdentificacion("31");
                emisor.setRazonSocial("EMPRESA NO CONFIGURADA");
            }
        } catch (Exception e) {
            log.warn("Error cargando empresa: {}", e.getMessage());
        }
        return emisor;
    }

    /**
     * Asigna prefijo + consecutivo PERSISTENTE para TPOS/ND/DS usando AsignacionComprobanteService.
     * Si el cajero no tiene comprobante del tipo dado, lanza ComprobanteNoConfiguradoException.
     * Esto reemplaza los contadores en memoria que se reseteaban al reiniciar la app.
     */
    private com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService.Resultado
            asignarComprobanteAuto(Integer cajeroId,
                                    com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante tipo) {
        if (cajeroId == null) {
            // Para tipos auto-generados (TPOS/ND/DS) que no traen cajero explícito,
            // fallar con mensaje claro — el operador debe configurar un comprobante
            // o atar la venta/compra origen a un cajero.
            throw new com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService.ComprobanteNoConfiguradoException(
                "No hay cajeroId disponible para asignar consecutivo de " + tipo.getDescripcion() +
                ". Asegure que la venta/compra origen tenga cajero asignado o configure un comprobante " +
                "del tipo " + tipo.name() + " con un cajero default."
            );
        }
        return asignacionComprobante.asignar(cajeroId, tipo);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  Asientos contables automáticos para TPOS / ND / DS
    //  Try/catch defensivo: si falla, no rompe la persistencia del documento.
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Asiento Tiquete POS (igual que una factura contado):
     *   DR  1105 Caja general            por el total
     *   CR  4135 Ingresos por ventas     por la base sin IVA
     *   CR  240801 IVA generado          por el IVA
     *   + asiento de costo: DR 6135 / CR 1435 por costo promedio
     */
    private Long generarAsientoTPOS(DocumentoElectronico doc, Venta venta) {
        try {
            java.util.List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();
            BigDecimal total = doc.getTotal();
            BigDecimal iva = doc.getIva() != null ? doc.getIva() : BigDecimal.ZERO;
            BigDecimal base = total.subtract(iva).max(BigDecimal.ZERO);

            CuentaContable caja = configContable.cajaGeneral().orElse(null);
            CuentaContable ingresos = configContable.ingresosVentas().orElse(null);
            if (caja == null || ingresos == null) {
                log.warn("[AsientoTPOS] Cuentas no configuradas (1105/4135). Asiento omitido.");
                return null;
            }
            lineas.add(AsientoContableService.LineaDTO.debito(caja.getId(), total, "TPOS " + doc.getNumero()));
            lineas.add(AsientoContableService.LineaDTO.credito(ingresos.getId(), base, "Ingreso TPOS " + doc.getNumero()));
            if (iva.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable ivaGen = configContable.ivaGenerado().orElse(null);
                if (ivaGen != null) {
                    lineas.add(AsientoContableService.LineaDTO.credito(ivaGen.getId(), iva, "IVA TPOS " + doc.getNumero()));
                }
            }
            var asiento = asientoService.generarAsiento(
                    doc.getNumero(),
                    doc.getFechaEmision(),
                    "Tiquete POS " + doc.getNumero(),
                    "TPOS",
                    venta != null ? venta.getId() : null,
                    null,
                    lineas
            );
            return asiento != null ? asiento.getId() : null;
        } catch (Exception e) {
            log.warn("[AsientoTPOS] Error generando asiento (no crítico): {}", e.getMessage());
            return null;
        }
    }

    /**
     * Asiento Nota Débito (cargo adicional al cliente):
     *   DR  1305 Clientes (CxC)         por el total
     *   CR  4135 Ingresos               por la base
     *   CR  240801 IVA generado         por el IVA
     */
    private Long generarAsientoND(DocumentoElectronico doc, Facturas facturaOriginal) {
        try {
            java.util.List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();
            BigDecimal total = doc.getTotal();
            BigDecimal iva = doc.getIva() != null ? doc.getIva() : BigDecimal.ZERO;
            BigDecimal base = total.subtract(iva).max(BigDecimal.ZERO);

            CuentaContable cxc = configContable.cxcClientes().orElse(null);
            CuentaContable ingresos = configContable.ingresosVentas().orElse(null);
            if (cxc == null || ingresos == null) {
                log.warn("[AsientoND] Cuentas no configuradas (1305/4135). Asiento omitido.");
                return null;
            }
            lineas.add(AsientoContableService.LineaDTO.debito(cxc.getId(), total, "ND " + doc.getNumero()));
            lineas.add(AsientoContableService.LineaDTO.credito(ingresos.getId(), base, "Cargo " + doc.getConcepto()));
            if (iva.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable ivaGen = configContable.ivaGenerado().orElse(null);
                if (ivaGen != null) {
                    lineas.add(AsientoContableService.LineaDTO.credito(ivaGen.getId(), iva, "IVA ND " + doc.getNumero()));
                }
            }
            var asiento = asientoService.generarAsiento(
                    doc.getNumero(),
                    doc.getFechaEmision(),
                    "Nota Débito " + doc.getNumero() + (doc.getConcepto() != null ? " - " + doc.getConcepto() : ""),
                    "ND",
                    facturaOriginal != null ? Long.valueOf(facturaOriginal.getFacturaId()) : null,
                    null,
                    lineas
            );
            return asiento != null ? asiento.getId() : null;
        } catch (Exception e) {
            log.warn("[AsientoND] Error generando asiento (no crítico): {}", e.getMessage());
            return null;
        }
    }

    /**
     * Asiento Documento Soporte (compra a no facturador):
     *   DR  5135 Gastos varios (o 1435 si es inventario)   por el total sin IVA
     *   DR  240810 IVA descontable                          por el IVA (si aplica)
     *   CR  1105 Caja general                               por el total
     */
    private Long generarAsientoDS(DocumentoElectronico doc) {
        try {
            java.util.List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();
            BigDecimal total = doc.getTotal();
            BigDecimal iva = doc.getIva() != null ? doc.getIva() : BigDecimal.ZERO;
            BigDecimal base = total.subtract(iva).max(BigDecimal.ZERO);

            // Heurística: si el concepto menciona "compra", "mercancía", "inventario" → 1435; sino 5135
            String conceptoLower = (doc.getConcepto() != null ? doc.getConcepto().toLowerCase() : "");
            boolean esInventario = conceptoLower.contains("mercanc") || conceptoLower.contains("inventario")
                                || conceptoLower.contains("producto");

            CuentaContable destino = esInventario
                    ? configContable.inventarios().orElse(null)
                    : configContable.gastosGenerales().orElse(null);
            CuentaContable caja = configContable.cajaGeneral().orElse(null);
            if (destino == null || caja == null) {
                log.warn("[AsientoDS] Cuentas no configuradas. Asiento omitido.");
                return null;
            }
            AsientoContableService.LineaDTO debitoLinea = AsientoContableService.LineaDTO
                    .debito(destino.getId(), base, "DS " + doc.getNumero() + " - " + (doc.getConcepto() != null ? doc.getConcepto() : ""));
            if (doc.getTerceroIdentificacion() != null) {
                try {
                    debitoLinea.conTercero(Integer.parseInt(doc.getTerceroIdentificacion()), doc.getTerceroNombre());
                } catch (NumberFormatException nfe) {
                    // ID no numérico, ignoramos terceroId
                }
            }
            lineas.add(debitoLinea);
            if (iva.compareTo(BigDecimal.ZERO) > 0) {
                CuentaContable ivaDesc = configContable.ivaDescontable().orElse(null);
                if (ivaDesc != null) {
                    lineas.add(AsientoContableService.LineaDTO.debito(ivaDesc.getId(), iva, "IVA DS " + doc.getNumero()));
                }
            }
            AsientoContableService.LineaDTO creditoLinea = AsientoContableService.LineaDTO
                    .credito(caja.getId(), total, "Pago DS " + doc.getNumero());
            lineas.add(creditoLinea);

            var asiento = asientoService.generarAsiento(
                    doc.getNumero(),
                    doc.getFechaEmision(),
                    "Documento Soporte " + doc.getNumero() + " - " + (doc.getTerceroNombre() != null ? doc.getTerceroNombre() : ""),
                    "DS",
                    doc.getId(),
                    null,
                    lineas
            );
            return asiento != null ? asiento.getId() : null;
        } catch (Exception e) {
            log.warn("[AsientoDS] Error generando asiento (no crítico): {}", e.getMessage());
            return null;
        }
    }
}

