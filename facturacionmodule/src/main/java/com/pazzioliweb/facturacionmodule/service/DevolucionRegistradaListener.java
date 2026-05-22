package com.pazzioliweb.facturacionmodule.service;

import com.pazzioliweb.commonbacken.events.DevolucionRegistradaEvent;
import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.facturacionmodule.config.DianConfig;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoRequestDTO;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoResponseDTO;
import com.pazzioliweb.facturacionmodule.entity.Facturas;
import com.pazzioliweb.facturacionmodule.repositori.FacturasRepository;
import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresasback.repositori.EmpresaRepositori;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.ventasmodule.entity.DetalleDevolucion;
import com.pazzioliweb.ventasmodule.entity.Devolucion;
import com.pazzioliweb.ventasmodule.entity.Venta;
import com.pazzioliweb.ventasmodule.repository.DevolucionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener que se dispara al registrarse una devolución de venta.
 * Genera automáticamente la Nota Crédito Electrónica (NC) para DIAN
 * referenciando la factura original (CUFE).
 *
 * El listener corre AFTER_COMMIT en una nueva transacción para que si falla
 * no afecte la devolución que ya fue persistida.
 */
@Component
public class DevolucionRegistradaListener {

    private static final Logger log = LoggerFactory.getLogger(DevolucionRegistradaListener.class);

    private final DevolucionRepository devolucionRepository;
    private final FacturasRepository facturasRepository;
    private final ComprobanteContableRepository comprobantesRepository;
    private final EmpresaRepositori empresaRepositori;
    private final ProveedorFacturacionElectronica proveedorDian;
    private final DianConfig dianConfig;

    public DevolucionRegistradaListener(DevolucionRepository devolucionRepository,
                                         FacturasRepository facturasRepository,
                                         ComprobanteContableRepository comprobantesRepository,
                                         EmpresaRepositori empresaRepositori,
                                         ProveedorFacturacionElectronica proveedorDian,
                                         DianConfig dianConfig) {
        this.devolucionRepository = devolucionRepository;
        this.facturasRepository = facturasRepository;
        this.comprobantesRepository = comprobantesRepository;
        this.empresaRepositori = empresaRepositori;
        this.proveedorDian = proveedorDian;
        this.dianConfig = dianConfig;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onDevolucionRegistrada(DevolucionRegistradaEvent event) {
        log.info("══════ Evento DevolucionRegistrada → Generando Nota Crédito Electrónica ══════");
        log.info("Devolución ID: {}, Venta ID: {}, Concepto: {}",
                event.getDevolucionId(), event.getVentaId(), event.getCodigoConcepto());

        try {
            Devolucion devolucion = devolucionRepository.findById(event.getDevolucionId()).orElse(null);
            if (devolucion == null) {
                log.warn("Devolución {} no encontrada — se omite NC", event.getDevolucionId());
                return;
            }
            Venta venta = devolucion.getVenta();
            if (venta == null) {
                log.warn("Devolución {} sin venta asociada — se omite NC", event.getDevolucionId());
                return;
            }

            // Buscar la factura electrónica original de la venta (para referenciar el CUFE)
            Optional<Facturas> facturaOpt = facturasRepository.findByVentaId(venta.getId());
            if (facturaOpt.isEmpty()) {
                log.warn("La venta {} no tiene factura electrónica previa — NC se omite (no se puede referenciar)",
                        venta.getNumeroVenta());
                return;
            }
            Facturas facturaOriginal = facturaOpt.get();

            // Resolver comprobante NC: primero buscar por cajero (si hay), si no, el primero activo
            List<ComprobanteContable> compsNC = comprobantesRepository.findByTipo(TipoMovimientoComprobante.NC);
            Optional<ComprobanteContable> compOpt = compsNC.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                    .filter(c -> {
                        if (event.getCajeroId() == null) return true;
                        java.util.Set<Integer> ids = c.getCajeroIds();
                        return ids != null && ids.contains(event.getCajeroId());
                    })
                    .findFirst();
            if (compOpt.isEmpty()) {
                // Fallback: cualquier NC activo (incluso sin cajero asignado o LEGACY)
                compOpt = compsNC.stream().filter(c -> Boolean.TRUE.equals(c.getActivo())).findFirst();
            }
            if (compOpt.isEmpty()) {
                log.warn("No hay comprobante activo tipo NC. Configúrelo en pantalla Comprobantes Contables. Se omite NC.");
                return;
            }
            ComprobanteContable compNC = compOpt.get();
            int siguiente = compNC.getSiguienteConsecutivo() != null ? compNC.getSiguienteConsecutivo() : 1;

            // Armar request DIAN para NC
            DianDocumentoRequestDTO req = armarRequestNC(devolucion, venta, facturaOriginal,
                    compNC.getPrefijo(), siguiente, event.getCodigoConcepto());

            log.info("Enviando NC {} {} (referencia CUFE: {})",
                    req.getPrefijo(), req.getConsecutivo(),
                    facturaOriginal.getCufe() != null ? facturaOriginal.getCufe().substring(0, Math.min(20, facturaOriginal.getCufe().length())) + "..." : "N/A");

            DianDocumentoResponseDTO resp = proveedorDian.enviarFactura(req);

            // Persistir resultado en la devolución
            devolucion.setNumeroNc(compNC.getPrefijo() + "-" + siguiente);
            devolucion.setCufeNc(resp.getCufe());
            devolucion.setEstadoDianNc(resp.getEstadoDian());
            devolucion.setMensajeDianNc(resp.getMensajeDian());
            devolucion.setQrDataNc(resp.getQrData());
            devolucionRepository.save(devolucion);

            // Avanzar consecutivo del comprobante
            compNC.setSiguienteConsecutivo(siguiente + 1);
            comprobantesRepository.save(compNC);

            log.info("✅ Nota Crédito generada: {} - Estado DIAN: {} - CUDE: {}",
                    devolucion.getNumeroNc(), resp.getEstadoDian(),
                    resp.getCufe() != null ? resp.getCufe().substring(0, Math.min(30, resp.getCufe().length())) + "..." : "N/A");

        } catch (Exception e) {
            log.error("❌ Error generando NC para devolución {}: {}",
                    event.getDevolucionId(), e.getMessage(), e);
        }
    }

    private DianDocumentoRequestDTO armarRequestNC(Devolucion devolucion, Venta venta,
                                                    Facturas facturaOriginal,
                                                    String prefijoNC, int consecutivoNC,
                                                    Integer concepto) {
        DianDocumentoRequestDTO req = new DianDocumentoRequestDTO();
        req.setTipoDocumento("91"); // 91 = Nota Crédito
        req.setPrefijo(prefijoNC);
        req.setConsecutivo(consecutivoNC);
        req.setFechaEmision(devolucion.getFechaCreacion());
        req.setFormaPago("1");
        req.setResolucionDian(dianConfig.getResolucion().getNumero());
        req.setCodigoConcepto(concepto != null ? concepto : 1);
        req.setRazonConcepto(devolucion.getMotivo());

        // Referencia al documento original (factura electrónica)
        DianDocumentoRequestDTO.DocumentoReferenciaDTO ref = new DianDocumentoRequestDTO.DocumentoReferenciaDTO();
        ref.setNumeroDocumento(facturaOriginal.getNumeroFactura());
        ref.setCufeOriginal(facturaOriginal.getCufe());
        ref.setFechaEmisionOriginal(facturaOriginal.getFechaEmision());
        ref.setTipoDocumentoOriginal("01");
        req.setDocumentoReferencia(ref);

        // Emisor
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
                emisor.setTelefono(empresa.getCelularempresa());
                emisor.setCorreo(empresa.getCorreoempresa());
                emisor.setPais("CO");
            }
        } catch (Exception ex) {
            log.warn("[NC] Error cargando empresa: {}", ex.getMessage());
        }
        req.setEmisor(emisor);

        // Receptor (cliente de la venta)
        Terceros cliente = venta.getCliente();
        DianDocumentoRequestDTO.ReceptorDTO receptor = new DianDocumentoRequestDTO.ReceptorDTO();
        if (cliente != null) {
            if (cliente.getTipoIdentificacion() != null) {
                receptor.setTipoIdentificacion(String.valueOf(cliente.getTipoIdentificacion().getCodigo()));
            }
            receptor.setNumeroIdentificacion(cliente.getIdentificacion());
            receptor.setDigitoVerificacion(cliente.getDv());
            receptor.setNombre(cliente.getRazonSocial() != null && !cliente.getRazonSocial().isBlank()
                    ? cliente.getRazonSocial()
                    : (cliente.getNombre1() != null ? cliente.getNombre1() : ""));
            receptor.setDireccion(cliente.getDireccion());
            receptor.setCorreo(cliente.getCorreo());
        }
        req.setReceptor(receptor);

        // Líneas (productos devueltos)
        List<DianDocumentoRequestDTO.LineaDTO> lineas = new ArrayList<>();
        AtomicInteger num = new AtomicInteger(1);
        for (DetalleDevolucion d : devolucion.getItems()) {
            DianDocumentoRequestDTO.LineaDTO l = new DianDocumentoRequestDTO.LineaDTO();
            l.setNumero(num.getAndIncrement());
            if (d.getDetalleVenta() != null) {
                l.setCodigoProducto(d.getDetalleVenta().getCodigoProducto());
                l.setDescripcion(d.getDetalleVenta().getDescripcionProducto());
            }
            l.setCantidad(d.getCantidadDevuelta() != null ? d.getCantidadDevuelta() : 1);
            l.setPrecioUnitario(d.getPrecioUnitario() != null ? d.getPrecioUnitario() : BigDecimal.ZERO);
            l.setDescuento(BigDecimal.ZERO);
            l.setValorIva(d.getIvaLinea() != null ? d.getIvaLinea() : BigDecimal.ZERO);
            l.setTotalLinea(d.getTotalLinea() != null ? d.getTotalLinea() : BigDecimal.ZERO);
            lineas.add(l);
        }
        req.setLineas(lineas);

        // Totales
        BigDecimal totalNeto = devolucion.getTotalNeto() != null ? devolucion.getTotalNeto() : BigDecimal.ZERO;
        BigDecimal iva       = devolucion.getIvaDevuelto() != null ? devolucion.getIvaDevuelto() : BigDecimal.ZERO;
        BigDecimal base      = devolucion.getTotalDevuelto() != null ? devolucion.getTotalDevuelto() : totalNeto.subtract(iva);
        req.setBaseGravable(base);
        req.setTotalIva(iva);
        req.setTotalDescuento(BigDecimal.ZERO);
        req.setTotalFactura(totalNeto);

        return req;
    }
}
