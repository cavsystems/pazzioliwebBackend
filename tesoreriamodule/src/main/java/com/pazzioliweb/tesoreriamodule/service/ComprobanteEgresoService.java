package com.pazzioliweb.tesoreriamodule.service;

import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.comprasmodule.entity.CuentaPorPagar;
import com.pazzioliweb.comprasmodule.repository.CuentaPorPagarRepository;
import com.pazzioliweb.comprobantesmodule.entity.ConceptoAbierto;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.ConceptoAbiertoRepository;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.tesoreriamodule.dtos.ComprobanteEgresoResponseDTO;
import com.pazzioliweb.tesoreriamodule.dtos.CrearComprobanteEgresoDTO;
import com.pazzioliweb.tesoreriamodule.entity.ComprobanteEgreso;
import com.pazzioliweb.tesoreriamodule.entity.ComprobanteEgresoMedioPago;
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
    private final ConceptoAbiertoRepository conceptoAbiertoRepository;
    private final CuentaContableRepository cuentaContableRepository;
    private final DetalleCajeroService detalleCajeroService;
    private final RedisTemplate<String, DatosSesiones> redisTemplate;

    public ComprobanteEgresoService(ComprobanteEgresoRepository egresoRepository,
                                     CuentaPorPagarRepository cxpRepository,
                                     TercerosRepository tercerosRepository,
                                     MetodosPagoRepository metodosPagoRepository,
                                     ConceptoAbiertoRepository conceptoAbiertoRepository,
                                     CuentaContableRepository cuentaContableRepository,
                                     DetalleCajeroService detalleCajeroService,
                                     RedisTemplate<String, DatosSesiones> redisTemplate) {
        this.egresoRepository = egresoRepository;
        this.cxpRepository = cxpRepository;
        this.tercerosRepository = tercerosRepository;
        this.metodosPagoRepository = metodosPagoRepository;
        this.conceptoAbiertoRepository = conceptoAbiertoRepository;
        this.cuentaContableRepository = cuentaContableRepository;
        this.detalleCajeroService = detalleCajeroService;
        this.redisTemplate = redisTemplate;
    }

    public Integer obtenerSiguienteConsecutivo() {
        return egresoRepository.findMaxConsecutivo() + 1;
    }

    @Transactional
    public ComprobanteEgresoResponseDTO crear(CrearComprobanteEgresoDTO dto) {
        boolean esConceptoAbierto = Boolean.TRUE.equals(dto.getConceptoAbierto());

        // Validaciones de medios de pago
        if (dto.getMediosPago() == null || dto.getMediosPago().isEmpty()) {
            throw new RuntimeException("Debe especificar al menos un medio de pago");
        }
        for (CrearComprobanteEgresoDTO.MedioPagoDTO mp : dto.getMediosPago()) {
            if (mp.getMonto() == null || mp.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Cada medio de pago debe tener un monto mayor a 0");
            }
        }

        if (esConceptoAbierto) {
            if (dto.getConceptoAbiertoId() == null) {
                throw new RuntimeException("Debe seleccionar un concepto abierto registrado");
            }
            if (dto.getMontoConceptoAbierto() == null || dto.getMontoConceptoAbierto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El monto del concepto abierto debe ser mayor a 0");
            }
            boolean tieneTercero = dto.getTerceroId() != null;
            boolean tieneBeneficiario =
                    dto.getBeneficiarioNombre() != null && !dto.getBeneficiarioNombre().isBlank()
                    && dto.getBeneficiarioDocumento() != null && !dto.getBeneficiarioDocumento().isBlank();
            if (!tieneTercero && !tieneBeneficiario) {
                throw new RuntimeException("Para concepto abierto debe registrar el tercero o el nombre y documento del beneficiario");
            }
        } else {
            if (dto.getTerceroId() == null) {
                throw new RuntimeException("El tercero es obligatorio");
            }
            if (dto.getCuentas() == null || dto.getCuentas().isEmpty()) {
                throw new RuntimeException("Debe especificar al menos una cuenta por pagar");
            }
        }

        ComprobanteEgreso egreso = new ComprobanteEgreso();
        egreso.setConsecutivo(obtenerSiguienteConsecutivo());
        egreso.setConceptoAbierto(esConceptoAbierto);
        egreso.setFecha(LocalDate.now());
        egreso.setFechaEgreso(dto.getFechaEgreso() != null && !dto.getFechaEgreso().isBlank()
                ? LocalDate.parse(dto.getFechaEgreso()) : LocalDate.now());
        egreso.setRetefuente(dto.getRetefuente() != null ? dto.getRetefuente() : BigDecimal.ZERO);
        egreso.setReteica(dto.getReteica() != null ? dto.getReteica() : BigDecimal.ZERO);
        egreso.setReteiva(dto.getReteiva() != null ? dto.getReteiva() : BigDecimal.ZERO);
        egreso.setDescuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO);
        egreso.setConcepto(dto.getConcepto());
        egreso.setCentroCosto(dto.getCentroCosto());
        egreso.setCajeroId(dto.getCajeroId());
        egreso.setUsuarioId(dto.getUsuarioId());
        egreso.setBeneficiarioNombre(dto.getBeneficiarioNombre());
        egreso.setBeneficiarioDocumento(dto.getBeneficiarioDocumento());

        if (dto.getCuentaContableId() != null) {
            CuentaContable cc = cuentaContableRepository.findById(dto.getCuentaContableId())
                    .orElseThrow(() -> new RuntimeException("Cuenta contable no encontrada: " + dto.getCuentaContableId()));
            egreso.setCuentaContable(cc);
        }

        if (esConceptoAbierto) {
            ConceptoAbierto ca = conceptoAbiertoRepository.findById(dto.getConceptoAbiertoId())
                    .orElseThrow(() -> new RuntimeException("Concepto abierto no encontrado: " + dto.getConceptoAbiertoId()));
            String tipoCa = ca.getTipo() == null ? "" : ca.getTipo().toUpperCase();
            if (!"EGRESO".equals(tipoCa) && !"AMBOS".equals(tipoCa)) {
                throw new RuntimeException("El concepto seleccionado no aplica para comprobantes de egreso");
            }
            egreso.setConceptoAbiertoRef(ca);
            egreso.setConcepto(dto.getConcepto() != null && !dto.getConcepto().isBlank()
                    ? dto.getConcepto() : ca.getDescripcion());
            if (egreso.getCuentaContable() == null && ca.getCuentaContable() != null) {
                egreso.setCuentaContable(ca.getCuentaContable());
            }
        }

        // Tercero (solo si NO es concepto abierto)
        if (!esConceptoAbierto) {
            Terceros tercero = tercerosRepository.findById(dto.getTerceroId())
                    .orElseThrow(() -> new RuntimeException("Tercero no encontrado: " + dto.getTerceroId()));
            egreso.setTercero(tercero);
            egreso.setTerceroNombre(tercero.getNombre1() + " " +
                    (tercero.getApellido1() != null ? tercero.getApellido1() : ""));
            egreso.setTerceroNit(tercero.getIdentificacion());
        } else if (dto.getTerceroId() != null) {
            Terceros t = tercerosRepository.findById(dto.getTerceroId()).orElse(null);
            if (t != null) {
                egreso.setTercero(t);
                egreso.setTerceroNombre(t.getNombre1() + " " +
                        (t.getApellido1() != null ? t.getApellido1() : ""));
                egreso.setTerceroNit(t.getIdentificacion());
            }
        }

        // Procesar medios de pago
        for (CrearComprobanteEgresoDTO.MedioPagoDTO mpDto : dto.getMediosPago()) {
            MetodosPago mp = metodosPagoRepository.findById(mpDto.getMetodoPagoId())
                    .orElseThrow(() -> new RuntimeException("Método de pago no encontrado: " + mpDto.getMetodoPagoId()));
            ComprobanteEgresoMedioPago medioPago = new ComprobanteEgresoMedioPago();
            medioPago.setComprobanteEgreso(egreso);
            medioPago.setMetodoPago(mp);
            medioPago.setMonto(mpDto.getMonto());
            egreso.getMediosPago().add(medioPago);
        }

        // Calcular subtotal
        BigDecimal subtotal;
        if (esConceptoAbierto) {
            subtotal = dto.getMontoConceptoAbierto();
            egreso.setMontoConceptoAbierto(subtotal);
        } else {
            // Procesar cuentas por pagar
            subtotal = BigDecimal.ZERO;
            for (CrearComprobanteEgresoDTO.DetallePagoDTO detDto : dto.getCuentas()) {
                CuentaPorPagar cxp = cxpRepository.findById(detDto.getCuentaPorPagarId())
                        .orElseThrow(() -> new RuntimeException("CxP no encontrada: " + detDto.getCuentaPorPagarId()));

                DetalleComprobanteEgreso detalle = new DetalleComprobanteEgreso();
                detalle.setComprobanteEgreso(egreso);
                detalle.setCuentaPorPagar(cxp);
                detalle.setMontoAbonado(detDto.getMontoAbonado());
                egreso.getDetalles().add(detalle);

                subtotal = subtotal.add(detDto.getMontoAbonado());

                // Actualizar saldo y estado de la CxP (resta sobre el saldo actual,
                // no contra valorNeto, para soportar múltiples pagos parciales).
                BigDecimal saldoActual = cxp.getSaldo() != null ? cxp.getSaldo() : cxp.getValorNeto();
                BigDecimal nuevoSaldo = saldoActual.subtract(detDto.getMontoAbonado());
                cxp.setSaldo(nuevoSaldo.max(BigDecimal.ZERO));
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
            Long detalleCajeroId = obtenerDetalleCajeroId(egreso.getCajeroId());
            if (detalleCajeroId != null) {
                // Calcular desglose efectivo vs electrónico según medios de pago
                BigDecimal montoEfectivo = BigDecimal.ZERO;
                BigDecimal montoElectronico = BigDecimal.ZERO;
                if (egreso.getMediosPago() != null) {
                    for (ComprobanteEgresoMedioPago mp : egreso.getMediosPago()) {
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
                        MovimientoCajero.TipoMovimiento.EGRESO,
                        "CE-" + egreso.getConsecutivo(),
                        egreso.getId(),
                        egreso.getTotal(),
                        BigDecimal.ZERO,
                        montoEfectivo,
                        montoElectronico,
                        "Comprobante Egreso #" + egreso.getConsecutivo() + " - " +
                                (egreso.getTerceroNombre() != null ? egreso.getTerceroNombre() : egreso.getConcepto())
                );
            }
        } catch (Exception e) {
            System.out.println("Error registrando movimiento cajero egreso: " + e.getMessage());
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

    @Transactional
    public ComprobanteEgresoResponseDTO anular(Long id, String motivo, Integer usuarioId) {
        ComprobanteEgreso egreso = egresoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprobante de egreso no encontrado: " + id));
        if ("ANULADO".equalsIgnoreCase(egreso.getEstado())) {
            throw new RuntimeException("El comprobante ya está anulado");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new RuntimeException("El motivo de anulación es obligatorio");
        }

        if (egreso.getDetalles() != null) {
            for (DetalleComprobanteEgreso det : egreso.getDetalles()) {
                CuentaPorPagar cxp = det.getCuentaPorPagar();
                if (cxp != null) {
                    BigDecimal saldoActual = cxp.getSaldo() != null ? cxp.getSaldo() : BigDecimal.ZERO;
                    BigDecimal saldoNuevo = saldoActual.add(det.getMontoAbonado());
                    BigDecimal valorNeto = cxp.getValorNeto() != null ? cxp.getValorNeto() : saldoNuevo;
                    if (saldoNuevo.compareTo(valorNeto) >= 0) {
                        cxp.setSaldo(valorNeto);
                        cxp.setEstado("PENDIENTE");
                    } else {
                        cxp.setSaldo(saldoNuevo);
                        cxp.setEstado("PARCIAL");
                    }
                    cxpRepository.save(cxp);
                }
            }
        }

        egreso.setEstado("ANULADO");
        egreso.setMotivoAnulacion(motivo);
        egreso.setFechaAnulacion(java.time.LocalDateTime.now());
        egreso.setAnuladoPorUsuarioId(usuarioId);
        egreso = egresoRepository.save(egreso);

        registrarAnulacionCajero(egreso);
        return toResponse(egreso);
    }

    private void registrarAnulacionCajero(ComprobanteEgreso egreso) {
        try {
            Long detalleCajeroId = obtenerDetalleCajeroId(egreso.getCajeroId());
            if (detalleCajeroId == null) return;
            BigDecimal montoEfectivo = BigDecimal.ZERO;
            BigDecimal montoElectronico = BigDecimal.ZERO;
            if (egreso.getMediosPago() != null) {
                for (ComprobanteEgresoMedioPago mp : egreso.getMediosPago()) {
                    String sigla = mp.getMetodoPago().getSigla() != null
                            ? mp.getMetodoPago().getSigla().toUpperCase() : "";
                    if (sigla.startsWith("EF")) montoEfectivo = montoEfectivo.add(mp.getMonto());
                    else montoElectronico = montoElectronico.add(mp.getMonto());
                }
            }
            // Anular un EGRESO = ingreso de vuelta a caja → signo positivo
            detalleCajeroService.registrarMovimiento(
                    detalleCajeroId,
                    MovimientoCajero.TipoMovimiento.ANULACION,
                    "CE-" + egreso.getConsecutivo() + "-A",
                    egreso.getId(),
                    egreso.getTotal(),
                    BigDecimal.ZERO,
                    montoEfectivo,
                    montoElectronico,
                    "Anulación Comprobante Egreso #" + egreso.getConsecutivo()
            );
        } catch (Exception ex) {
            System.out.println("Error registrando anulación cajero egreso: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ComprobanteEgresoResponseDTO> listarTodos() {
        return egresoRepository.findAllByOrderByFechaCreacionDesc().stream()
                .map(this::toResponse).collect(Collectors.toList());
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
        dto.setTerceroId(e.getTercero() != null ? e.getTercero().getTerceroId() : null);
        dto.setTerceroNombre(e.getTerceroNombre());
        dto.setTerceroNit(e.getTerceroNit());
        dto.setFecha(e.getFecha());
        dto.setFechaEgreso(e.getFechaEgreso());
        dto.setSubtotal(e.getSubtotal());
        dto.setRetefuente(e.getRetefuente());
        dto.setReteica(e.getReteica());
        dto.setReteiva(e.getReteiva());
        dto.setDescuento(e.getDescuento());
        dto.setTotal(e.getTotal());
        dto.setConcepto(e.getConcepto());
        dto.setCentroCosto(e.getCentroCosto());
        dto.setEstado(e.getEstado());
        dto.setConceptoAbierto(e.getConceptoAbierto());
        dto.setMontoConceptoAbierto(e.getMontoConceptoAbierto());
        dto.setBeneficiarioNombre(e.getBeneficiarioNombre());
        dto.setBeneficiarioDocumento(e.getBeneficiarioDocumento());
        if (e.getConceptoAbiertoRef() != null) {
            dto.setConceptoAbiertoId(e.getConceptoAbiertoRef().getId());
            dto.setConceptoAbiertoDescripcion(e.getConceptoAbiertoRef().getDescripcion());
        }
        if (e.getCuentaContable() != null) {
            dto.setCuentaContableId(e.getCuentaContable().getId());
            dto.setCuentaContableCodigo(e.getCuentaContable().getCodigo());
            dto.setCuentaContableNombre(e.getCuentaContable().getNombre());
        }
        dto.setFechaCreacion(e.getFechaCreacion());
        dto.setFechaAnulacion(e.getFechaAnulacion());
        dto.setMotivoAnulacion(e.getMotivoAnulacion());
        dto.setAnuladoPorUsuarioId(e.getAnuladoPorUsuarioId());

        // Mapear medios de pago
        if (e.getMediosPago() != null) {
            dto.setMediosPago(e.getMediosPago().stream().map(mp -> {
                ComprobanteEgresoResponseDTO.MedioPagoResponseDTO mpDto = new ComprobanteEgresoResponseDTO.MedioPagoResponseDTO();
                mpDto.setId(mp.getId());
                mpDto.setMetodoPagoId(mp.getMetodoPago().getMetodo_pago_id());
                mpDto.setMetodoPagoDescripcion(mp.getMetodoPago().getDescripcion());
                mpDto.setMonto(mp.getMonto());
                return mpDto;
            }).collect(Collectors.toList()));

            dto.setMetodoPago(e.getMediosPago().stream()
                    .map(mp -> mp.getMetodoPago().getDescripcion())
                    .collect(Collectors.joining(", ")));
        }

        if (e.getDetalles() != null) {
            dto.setDetalles(e.getDetalles().stream().map(d -> {
                ComprobanteEgresoResponseDTO.DetalleEgresoResponseDTO det = new ComprobanteEgresoResponseDTO.DetalleEgresoResponseDTO();
                det.setId(d.getId());
                det.setCuentaPorPagarId(d.getCuentaPorPagar().getId());
                det.setNumeroFactura(d.getCuentaPorPagar().getNumeroFactura());
                det.setValorNeto(d.getCuentaPorPagar().getValorNeto());
                det.setSaldo(d.getCuentaPorPagar().getSaldo());
                det.setMontoAbonado(d.getMontoAbonado());
                det.setEstado(d.getCuentaPorPagar().getEstado());
                return det;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}

