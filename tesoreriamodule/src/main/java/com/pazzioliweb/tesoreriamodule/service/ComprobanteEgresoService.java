package com.pazzioliweb.tesoreriamodule.service;

import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.comprasmodule.entity.CuentaPorPagar;
import com.pazzioliweb.comprasmodule.repository.CuentaPorPagarRepository;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.tesoreriamodule.dtos.ComprobanteEgresoResponseDTO;
import com.pazzioliweb.tesoreriamodule.dtos.CrearComprobanteEgresoDTO;
import com.pazzioliweb.tesoreriamodule.entity.ComprobanteEgreso;
import com.pazzioliweb.tesoreriamodule.entity.DetalleComprobanteEgreso;
import com.pazzioliweb.tesoreriamodule.repository.ComprobanteEgresoRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComprobanteEgresoService {

    private final ComprobanteEgresoRepository egresoRepository;
    private final CuentaPorPagarRepository cxpRepository;
    private final TercerosRepository tercerosRepository;
    private final MetodosPagoRepository metodosPagoRepository;
    private final DetalleCajeroService detalleCajeroService;
    private final RedisTemplate<String, DatosSesiones> redisTemplate;

    public ComprobanteEgresoService(ComprobanteEgresoRepository egresoRepository,
                                     CuentaPorPagarRepository cxpRepository,
                                     TercerosRepository tercerosRepository,
                                     MetodosPagoRepository metodosPagoRepository,
                                     DetalleCajeroService detalleCajeroService,
                                     RedisTemplate<String, DatosSesiones> redisTemplate) {
        this.egresoRepository = egresoRepository;
        this.cxpRepository = cxpRepository;
        this.tercerosRepository = tercerosRepository;
        this.metodosPagoRepository = metodosPagoRepository;
        this.detalleCajeroService = detalleCajeroService;
        this.redisTemplate = redisTemplate;
    }

    public Integer obtenerSiguienteConsecutivo() {
        return egresoRepository.findMaxConsecutivo() + 1;
    }

    @Transactional
    public ComprobanteEgresoResponseDTO crear(CrearComprobanteEgresoDTO dto) {
        Terceros tercero = tercerosRepository.findById(dto.getTerceroId())
                .orElseThrow(() -> new RuntimeException("Tercero no encontrado: " + dto.getTerceroId()));

        ComprobanteEgreso egreso = new ComprobanteEgreso();
        egreso.setConsecutivo(obtenerSiguienteConsecutivo());
        egreso.setTercero(tercero);
        egreso.setTerceroNombre(tercero.getNombre1() + " " +
                (tercero.getApellido1() != null ? tercero.getApellido1() : ""));
        egreso.setTerceroNit(tercero.getIdentificacion());
        egreso.setFecha(LocalDate.now());
        egreso.setRetefuente(dto.getRetefuente() != null ? dto.getRetefuente() : BigDecimal.ZERO);
        egreso.setReteica(dto.getReteica() != null ? dto.getReteica() : BigDecimal.ZERO);
        egreso.setReteiva(dto.getReteiva() != null ? dto.getReteiva() : BigDecimal.ZERO);
        egreso.setDescuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO);
        egreso.setConcepto(dto.getConcepto());
        egreso.setCentroCosto(dto.getCentroCosto());
        egreso.setCajeroId(dto.getCajeroId());
        egreso.setUsuarioId(dto.getUsuarioId());

        if (dto.getMetodoPagoId() != null) {
            MetodosPago mp = metodosPagoRepository.findById(dto.getMetodoPagoId()).orElse(null);
            egreso.setMetodoPago(mp);
        }

        // Procesar cuentas por pagar
        BigDecimal subtotal = BigDecimal.ZERO;
        if (dto.getCuentas() != null) {
            for (CrearComprobanteEgresoDTO.DetallePagoDTO detDto : dto.getCuentas()) {
                CuentaPorPagar cxp = cxpRepository.findById(detDto.getCuentaPorPagarId())
                        .orElseThrow(() -> new RuntimeException("CxP no encontrada: " + detDto.getCuentaPorPagarId()));

                DetalleComprobanteEgreso detalle = new DetalleComprobanteEgreso();
                detalle.setComprobanteEgreso(egreso);
                detalle.setCuentaPorPagar(cxp);
                detalle.setMontoAbonado(detDto.getMontoAbonado());
                egreso.getDetalles().add(detalle);

                subtotal = subtotal.add(detDto.getMontoAbonado());

                // Actualizar estado de la CxP
                BigDecimal nuevoSaldo = cxp.getValorNeto().subtract(detDto.getMontoAbonado());
                if (nuevoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
                    cxp.setEstado("PAGADA");
                } else {
                    cxp.setEstado("PARCIAL");
                }
                cxpRepository.save(cxp);
            }
        }

        egreso.setSubtotal(subtotal);
        BigDecimal totalRetenciones = egreso.getRetefuente()
                .add(egreso.getReteica())
                .add(egreso.getReteiva())
                .add(egreso.getDescuento());
        egreso.setTotal(subtotal.subtract(totalRetenciones));

        egreso = egresoRepository.save(egreso);

        // Registrar movimiento en cajero si hay sesión activa
        registrarMovimientoCajero(egreso);

        return toResponse(egreso);
    }

    private void registrarMovimientoCajero(ComprobanteEgreso egreso) {
        try {
            DatosSesiones sesion = obtenerSesionActiva();
            if (sesion != null && sesion.getDetalleCajeroId() != null) {
                detalleCajeroService.registrarMovimiento(
                        sesion.getDetalleCajeroId(),
                        MovimientoCajero.TipoMovimiento.EGRESO,
                        "CE-" + egreso.getConsecutivo(),
                        egreso.getId(),
                        egreso.getTotal(),
                        BigDecimal.ZERO,
                        egreso.getTotal(), // todo en efectivo por defecto
                        BigDecimal.ZERO,
                        "Comprobante Egreso #" + egreso.getConsecutivo() + " - " + egreso.getTerceroNombre()
                );
            }
        } catch (Exception e) {
            System.out.println("Error registrando movimiento cajero egreso: " + e.getMessage());
        }
    }

    private DatosSesiones obtenerSesionActiva() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() != null) {
                String sessionId = auth.getCredentials().toString();
                return redisTemplate.opsForValue().get(sessionId);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener sesión activa: " + e.getMessage());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<ComprobanteEgresoResponseDTO> listarTodos() {
        return egresoRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComprobanteEgresoResponseDTO buscarPorId(Long id) {
        ComprobanteEgreso egreso = egresoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante de egreso no encontrado: " + id));
        return toResponse(egreso);
    }

    private ComprobanteEgresoResponseDTO toResponse(ComprobanteEgreso e) {
        ComprobanteEgresoResponseDTO dto = new ComprobanteEgresoResponseDTO();
        dto.setId(e.getId());
        dto.setConsecutivo(e.getConsecutivo());
        dto.setTerceroId(e.getTercero().getTerceroId());
        dto.setTerceroNombre(e.getTerceroNombre());
        dto.setTerceroNit(e.getTerceroNit());
        dto.setFecha(e.getFecha());
        dto.setSubtotal(e.getSubtotal());
        dto.setRetefuente(e.getRetefuente());
        dto.setReteica(e.getReteica());
        dto.setReteiva(e.getReteiva());
        dto.setDescuento(e.getDescuento());
        dto.setTotal(e.getTotal());
        dto.setMetodoPagoId(e.getMetodoPago() != null ? e.getMetodoPago().getMetodo_pago_id() : null);
        dto.setMetodoPagoDescripcion(e.getMetodoPago() != null ? e.getMetodoPago().getDescripcion() : null);
        dto.setConcepto(e.getConcepto());
        dto.setCentroCosto(e.getCentroCosto());
        dto.setEstado(e.getEstado());
        dto.setFechaCreacion(e.getFechaCreacion());

        if (e.getDetalles() != null) {
            dto.setDetalles(e.getDetalles().stream().map(d -> {
                ComprobanteEgresoResponseDTO.DetalleEgresoResponseDTO det = new ComprobanteEgresoResponseDTO.DetalleEgresoResponseDTO();
                det.setId(d.getId());
                det.setCuentaPorPagarId(d.getCuentaPorPagar().getId());
                det.setNumeroFactura(d.getCuentaPorPagar().getNumeroFactura());
                det.setValorNeto(d.getCuentaPorPagar().getValorNeto());
                det.setMontoAbonado(d.getMontoAbonado());
                det.setEstado(d.getCuentaPorPagar().getEstado());
                return det;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}


