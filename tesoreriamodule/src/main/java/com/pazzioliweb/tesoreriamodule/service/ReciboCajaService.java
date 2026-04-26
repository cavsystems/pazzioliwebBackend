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
import com.pazzioliweb.tesoreriamodule.entity.ReciboCajaMedioPago;
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
        // Validaciones
        if (dto.getMediosPago() == null || dto.getMediosPago().isEmpty()) {
            throw new RuntimeException("Debe especificar al menos un medio de pago");
        }
        for (CrearReciboCajaDTO.MedioPagoDTO mp : dto.getMediosPago()) {
            if (mp.getMonto() == null || mp.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Cada medio de pago debe tener un monto mayor a 0");
            }
        }
        if (dto.getFechaRecibo() == null || dto.getFechaRecibo().isBlank()) {
            throw new RuntimeException("La fecha del recibo es obligatoria");
        }

        boolean esConceptoAbierto = Boolean.TRUE.equals(dto.getConceptoAbierto());

        if (esConceptoAbierto) {
            if (dto.getConcepto() == null || dto.getConcepto().isBlank()) {
                throw new RuntimeException("El concepto es obligatorio para concepto abierto");
            }
            if (dto.getMontoConceptoAbierto() == null || dto.getMontoConceptoAbierto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El monto del concepto abierto debe ser mayor a 0");
            }
        } else {
            if (dto.getTerceroId() == null) {
                throw new RuntimeException("El tercero es obligatorio");
            }
            if (dto.getCuentas() == null || dto.getCuentas().isEmpty()) {
                throw new RuntimeException("Debe especificar al menos una cuenta por cobrar");
            }
        }

        ReciboCaja recibo = new ReciboCaja();
        recibo.setConsecutivo(obtenerSiguienteConsecutivo());
        recibo.setConceptoAbierto(esConceptoAbierto);
        recibo.setFecha(LocalDate.now());
        recibo.setFechaRecibo(LocalDate.parse(dto.getFechaRecibo()));
        recibo.setRetefuente(dto.getRetefuente() != null ? dto.getRetefuente() : BigDecimal.ZERO);
        recibo.setReteica(dto.getReteica() != null ? dto.getReteica() : BigDecimal.ZERO);
        recibo.setReteiva(dto.getReteiva() != null ? dto.getReteiva() : BigDecimal.ZERO);
        recibo.setDescuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO);
        recibo.setAverias(dto.getAverias() != null ? dto.getAverias() : BigDecimal.ZERO);
        recibo.setFletes(dto.getFletes() != null ? dto.getFletes() : BigDecimal.ZERO);
        recibo.setConcepto(dto.getConcepto());
        recibo.setCentroCosto(dto.getCentroCosto());
        recibo.setCajeroId(dto.getCajeroId());
        recibo.setUsuarioId(dto.getUsuarioId());

        // Tercero (solo si NO es concepto abierto)
        if (!esConceptoAbierto) {
            Terceros tercero = tercerosRepository.findById(dto.getTerceroId())
                    .orElseThrow(() -> new RuntimeException("Tercero no encontrado: " + dto.getTerceroId()));
            recibo.setTercero(tercero);
            recibo.setTerceroNombre(tercero.getNombre1() + " " +
                    (tercero.getApellido1() != null ? tercero.getApellido1() : ""));
            recibo.setTerceroNit(tercero.getIdentificacion());
        }

        // Procesar medios de pago
        for (CrearReciboCajaDTO.MedioPagoDTO mpDto : dto.getMediosPago()) {
            MetodosPago mp = metodosPagoRepository.findById(mpDto.getMetodoPagoId())
                    .orElseThrow(() -> new RuntimeException("Método de pago no encontrado: " + mpDto.getMetodoPagoId()));
            ReciboCajaMedioPago medioPago = new ReciboCajaMedioPago();
            medioPago.setReciboCaja(recibo);
            medioPago.setMetodoPago(mp);
            medioPago.setMonto(mpDto.getMonto());
            recibo.getMediosPago().add(medioPago);
        }

        // Calcular subtotal
        BigDecimal subtotal;
        if (esConceptoAbierto) {
            subtotal = dto.getMontoConceptoAbierto();
            recibo.setMontoConceptoAbierto(subtotal);
        } else {
            // Procesar cuentas por cobrar
            subtotal = BigDecimal.ZERO;
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
                .add(recibo.getDescuento())
                .add(recibo.getAverias())
                .add(recibo.getFletes());
        BigDecimal totalCobrar = subtotal.subtract(totalRetenciones);

        if (totalCobrar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El total a cobrar debe ser mayor a 0");
        }

        recibo.setTotal(totalCobrar);

        recibo = reciboRepository.save(recibo);

        // Registrar movimiento en cajero si hay sesión activa
        registrarMovimientoCajero(recibo);

        return toResponse(recibo);
    }

    private void registrarMovimientoCajero(ReciboCaja recibo) {
        try {
            Long detalleCajeroId = obtenerDetalleCajeroId(recibo.getCajeroId());
            if (detalleCajeroId != null) {
                // Calcular desglose efectivo vs electrónico según medios de pago
                BigDecimal montoEfectivo = BigDecimal.ZERO;
                BigDecimal montoElectronico = BigDecimal.ZERO;
                if (recibo.getMediosPago() != null) {
                    for (ReciboCajaMedioPago mp : recibo.getMediosPago()) {
                        String sigla = mp.getMetodoPago().getSigla() != null
                                ? mp.getMetodoPago().getSigla().toUpperCase() : "";
                        if (sigla.startsWith("EF")) {
                            montoEfectivo = montoEfectivo.add(mp.getMonto());
                        } else {
                            montoElectronico = montoElectronico.add(mp.getMonto());
                        }
                    }
                }

                detalleCajeroService.registrarMovimiento(
                        detalleCajeroId,
                        MovimientoCajero.TipoMovimiento.RECIBO_CAJA,
                        "RC-" + recibo.getConsecutivo(),
                        recibo.getId(),
                        recibo.getTotal(),
                        BigDecimal.ZERO,
                        montoEfectivo,
                        montoElectronico,
                        "Recibo Caja #" + recibo.getConsecutivo() + " - " +
                                (recibo.getTerceroNombre() != null ? recibo.getTerceroNombre() : recibo.getConcepto())
                );
            }
        } catch (Exception e) {
            System.out.println("Error registrando movimiento cajero recibo: " + e.getMessage());
        }
    }

    /**
     * Busca el detalleCajeroId de la sesión abierta para el cajero dado.
     * Primero intenta por Redis (sesión autenticada), luego busca en BD.
     */
    private Long obtenerDetalleCajeroId(Integer cajeroId) {
        // 1. Intentar obtener desde la sesión Redis
        try {
            DatosSesiones sesion = obtenerSesionActiva();
            if (sesion != null && sesion.getDetalleCajeroId() != null) {
                return sesion.getDetalleCajeroId();
            }
        } catch (Exception e) {
            // Ignorar, se intentará por BD
        }

        // 2. Buscar sesión abierta del cajero directamente en BD
        if (cajeroId != null) {
            var sesionesAbiertas = detalleCajeroService.buscarSesionesAbiertas(cajeroId);
            if (!sesionesAbiertas.isEmpty()) {
                return sesionesAbiertas.get(0).getDetalleCajeroId();
            }
        }

        return null;
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
        return reciboRepository.findAllByOrderByFechaCreacionDesc().stream().map(this::toResponse).collect(Collectors.toList());
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
        dto.setTerceroId(r.getTercero() != null ? r.getTercero().getTerceroId() : null);
        dto.setTerceroNombre(r.getTerceroNombre());
        dto.setTerceroNit(r.getTerceroNit());
        dto.setFecha(r.getFecha());
        dto.setFechaRecibo(r.getFechaRecibo());
        dto.setSubtotal(r.getSubtotal());
        dto.setRetefuente(r.getRetefuente());
        dto.setReteica(r.getReteica());
        dto.setReteiva(r.getReteiva());
        dto.setDescuento(r.getDescuento());
        dto.setAverias(r.getAverias());
        dto.setFletes(r.getFletes());
        dto.setTotal(r.getTotal());
        dto.setConcepto(r.getConcepto());
        dto.setCentroCosto(r.getCentroCosto());
        dto.setEstado(r.getEstado());
        dto.setConceptoAbierto(r.getConceptoAbierto());
        dto.setMontoConceptoAbierto(r.getMontoConceptoAbierto());
        dto.setFechaCreacion(r.getFechaCreacion());

        // Mapear medios de pago
        if (r.getMediosPago() != null) {
            dto.setMediosPago(r.getMediosPago().stream().map(mp -> {
                ReciboCajaResponseDTO.MedioPagoResponseDTO mpDto = new ReciboCajaResponseDTO.MedioPagoResponseDTO();
                mpDto.setId(mp.getId());
                mpDto.setMetodoPagoId(mp.getMetodoPago().getMetodo_pago_id());
                mpDto.setMetodoPagoDescripcion(mp.getMetodoPago().getDescripcion());
                mpDto.setMonto(mp.getMonto());
                return mpDto;
            }).collect(Collectors.toList()));

            // Campo concatenado para la tabla del historial
            dto.setMetodoPago(r.getMediosPago().stream()
                    .map(mp -> mp.getMetodoPago().getDescripcion())
                    .collect(Collectors.joining(", ")));
        }

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

