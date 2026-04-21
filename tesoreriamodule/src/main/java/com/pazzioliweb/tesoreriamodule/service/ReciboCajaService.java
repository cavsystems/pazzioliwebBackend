package com.pazzioliweb.tesoreriamodule.service;

import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.tesoreriamodule.dtos.CrearReciboCajaDTO;
import com.pazzioliweb.tesoreriamodule.dtos.ReciboCajaResponseDTO;
import com.pazzioliweb.tesoreriamodule.entity.DetalleReciboCaja;
import com.pazzioliweb.tesoreriamodule.entity.ReciboCaja;
import com.pazzioliweb.tesoreriamodule.repository.ReciboCajaRepository;
import com.pazzioliweb.ventasmodule.entity.CuentaPorCobrar;
import com.pazzioliweb.ventasmodule.repository.CuentaPorCobrarRepository;
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
public class ReciboCajaService {

    private final ReciboCajaRepository reciboRepository;
    private final CuentaPorCobrarRepository cxcRepository;
    private final TercerosRepository tercerosRepository;
    private final MetodosPagoRepository metodosPagoRepository;
    private final DetalleCajeroService detalleCajeroService;
    private final RedisTemplate<String, DatosSesiones> redisTemplate;

    public ReciboCajaService(ReciboCajaRepository reciboRepository,
                              CuentaPorCobrarRepository cxcRepository,
                              TercerosRepository tercerosRepository,
                              MetodosPagoRepository metodosPagoRepository,
                              DetalleCajeroService detalleCajeroService,
                              RedisTemplate<String, DatosSesiones> redisTemplate) {
        this.reciboRepository = reciboRepository;
        this.cxcRepository = cxcRepository;
        this.tercerosRepository = tercerosRepository;
        this.metodosPagoRepository = metodosPagoRepository;
        this.detalleCajeroService = detalleCajeroService;
        this.redisTemplate = redisTemplate;
    }

    public Integer obtenerSiguienteConsecutivo() {
        return reciboRepository.findMaxConsecutivo() + 1;
    }

    @Transactional
    public ReciboCajaResponseDTO crear(CrearReciboCajaDTO dto) {
        Terceros tercero = tercerosRepository.findById(dto.getTerceroId())
                .orElseThrow(() -> new RuntimeException("Tercero no encontrado: " + dto.getTerceroId()));

        ReciboCaja recibo = new ReciboCaja();
        recibo.setConsecutivo(obtenerSiguienteConsecutivo());
        recibo.setTercero(tercero);
        recibo.setTerceroNombre(tercero.getNombre1() + " " +
                (tercero.getApellido1() != null ? tercero.getApellido1() : ""));
        recibo.setTerceroNit(tercero.getIdentificacion());
        recibo.setFecha(LocalDate.now());
        recibo.setRetefuente(dto.getRetefuente() != null ? dto.getRetefuente() : BigDecimal.ZERO);
        recibo.setReteica(dto.getReteica() != null ? dto.getReteica() : BigDecimal.ZERO);
        recibo.setReteiva(dto.getReteiva() != null ? dto.getReteiva() : BigDecimal.ZERO);
        recibo.setDescuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO);
        recibo.setConcepto(dto.getConcepto());
        recibo.setCentroCosto(dto.getCentroCosto());
        recibo.setCajeroId(dto.getCajeroId());
        recibo.setUsuarioId(dto.getUsuarioId());

        if (dto.getMetodoPagoId() != null) {
            MetodosPago mp = metodosPagoRepository.findById(dto.getMetodoPagoId()).orElse(null);
            recibo.setMetodoPago(mp);
        }

        // Procesar cuentas por cobrar
        BigDecimal subtotal = BigDecimal.ZERO;
        if (dto.getCuentas() != null) {
            for (CrearReciboCajaDTO.DetalleCobroDTO detDto : dto.getCuentas()) {
                CuentaPorCobrar cxc = cxcRepository.findById(detDto.getCuentaPorCobrarId())
                        .orElseThrow(() -> new RuntimeException("CxC no encontrada: " + detDto.getCuentaPorCobrarId()));

                DetalleReciboCaja detalle = new DetalleReciboCaja();
                detalle.setReciboCaja(recibo);
                detalle.setCuentaPorCobrar(cxc);
                detalle.setMontoAbonado(detDto.getMontoAbonado());
                recibo.getDetalles().add(detalle);

                subtotal = subtotal.add(detDto.getMontoAbonado());

                // Actualizar saldo y estado de la CxC
                BigDecimal nuevoSaldo = cxc.getSaldo().subtract(detDto.getMontoAbonado());
                cxc.setSaldo(nuevoSaldo.max(BigDecimal.ZERO));
                if (nuevoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
                    cxc.setEstado("PAGADA");
                } else {
                    cxc.setEstado("PARCIAL");
                }
                cxcRepository.save(cxc);
            }
        }

        recibo.setSubtotal(subtotal);
        BigDecimal totalRetenciones = recibo.getRetefuente()
                .add(recibo.getReteica())
                .add(recibo.getReteiva())
                .add(recibo.getDescuento());
        recibo.setTotal(subtotal.subtract(totalRetenciones));

        recibo = reciboRepository.save(recibo);

        // Registrar movimiento en cajero si hay sesión activa
        registrarMovimientoCajero(recibo);

        return toResponse(recibo);
    }

    private void registrarMovimientoCajero(ReciboCaja recibo) {
        try {
            DatosSesiones sesion = obtenerSesionActiva();
            if (sesion != null && sesion.getDetalleCajeroId() != null) {
                detalleCajeroService.registrarMovimiento(
                        sesion.getDetalleCajeroId(),
                        MovimientoCajero.TipoMovimiento.RECIBO_CAJA,
                        "RC-" + recibo.getConsecutivo(),
                        recibo.getId(),
                        recibo.getTotal(),
                        BigDecimal.ZERO,
                        recibo.getTotal(),
                        BigDecimal.ZERO,
                        "Recibo Caja #" + recibo.getConsecutivo() + " - " + recibo.getTerceroNombre()
                );
            }
        } catch (Exception e) {
            System.out.println("Error registrando movimiento cajero recibo: " + e.getMessage());
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
    public List<ReciboCajaResponseDTO> listarTodos() {
        return reciboRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReciboCajaResponseDTO buscarPorId(Long id) {
        ReciboCaja recibo = reciboRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recibo de caja no encontrado: " + id));
        return toResponse(recibo);
    }

    private ReciboCajaResponseDTO toResponse(ReciboCaja r) {
        ReciboCajaResponseDTO dto = new ReciboCajaResponseDTO();
        dto.setId(r.getId());
        dto.setConsecutivo(r.getConsecutivo());
        dto.setTerceroId(r.getTercero().getTerceroId());
        dto.setTerceroNombre(r.getTerceroNombre());
        dto.setTerceroNit(r.getTerceroNit());
        dto.setFecha(r.getFecha());
        dto.setSubtotal(r.getSubtotal());
        dto.setRetefuente(r.getRetefuente());
        dto.setReteica(r.getReteica());
        dto.setReteiva(r.getReteiva());
        dto.setDescuento(r.getDescuento());
        dto.setTotal(r.getTotal());
        dto.setMetodoPagoId(r.getMetodoPago() != null ? r.getMetodoPago().getMetodo_pago_id() : null);
        dto.setMetodoPagoDescripcion(r.getMetodoPago() != null ? r.getMetodoPago().getDescripcion() : null);
        dto.setConcepto(r.getConcepto());
        dto.setCentroCosto(r.getCentroCosto());
        dto.setEstado(r.getEstado());
        dto.setFechaCreacion(r.getFechaCreacion());

        if (r.getDetalles() != null) {
            dto.setDetalles(r.getDetalles().stream().map(d -> {
                ReciboCajaResponseDTO.DetalleReciboResponseDTO det = new ReciboCajaResponseDTO.DetalleReciboResponseDTO();
                det.setId(d.getId());
                det.setCuentaPorCobrarId(d.getCuentaPorCobrar().getId());
                det.setNumeroVenta(d.getCuentaPorCobrar().getNumeroVenta());
                det.setValorNeto(d.getCuentaPorCobrar().getValorNeto());
                det.setSaldo(d.getCuentaPorCobrar().getSaldo());
                det.setMontoAbonado(d.getMontoAbonado());
                det.setEstado(d.getCuentaPorCobrar().getEstado());
                return det;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}


