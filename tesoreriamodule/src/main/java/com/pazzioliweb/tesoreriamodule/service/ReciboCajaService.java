package com.pazzioliweb.tesoreriamodule.service;

import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.comprobantesmodule.entity.ConceptoAbierto;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ConceptoAbiertoRepository;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import com.pazzioliweb.comprobantesmodule.service.AsientoContableService;
import com.pazzioliweb.comprobantesmodule.service.AsignacionComprobanteService;
import com.pazzioliweb.comprobantesmodule.service.ConfiguracionContableService;
import com.pazzioliweb.comprobantesmodule.service.PeriodoContableService;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.tercerosmodule.service.TercerosService;
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
    private final ConceptoAbiertoRepository conceptoAbiertoRepository;
    private final CuentaContableRepository cuentaContableRepository;
    private final DetalleCajeroService detalleCajeroService;
    private final RedisTemplate<String, DatosSesiones> redisTemplate;
    private final AsignacionComprobanteService asignacionComprobante;
    private final AsientoContableService asientoService;
    private final ConfiguracionContableService configContable;
    private final PeriodoContableService periodoContableService;
    private final TercerosService tercerosService;
    @org.springframework.beans.factory.annotation.Autowired
    private com.pazzioliweb.comprobantesmodule.service.AsientoFallidoService asientoFallidoService;

    public ReciboCajaService(ReciboCajaRepository reciboRepository,
                              CuentaPorCobrarRepository cxcRepository,
                              TercerosRepository tercerosRepository,
                              MetodosPagoRepository metodosPagoRepository,
                              ConceptoAbiertoRepository conceptoAbiertoRepository,
                              CuentaContableRepository cuentaContableRepository,
                              DetalleCajeroService detalleCajeroService,
                              RedisTemplate<String, DatosSesiones> redisTemplate,
                              AsignacionComprobanteService asignacionComprobante,
                              AsientoContableService asientoService,
                              ConfiguracionContableService configContable,
                              PeriodoContableService periodoContableService,
                              TercerosService tercerosService) {
        this.reciboRepository = reciboRepository;
        this.cxcRepository = cxcRepository;
        this.tercerosRepository = tercerosRepository;
        this.metodosPagoRepository = metodosPagoRepository;
        this.conceptoAbiertoRepository = conceptoAbiertoRepository;
        this.cuentaContableRepository = cuentaContableRepository;
        this.detalleCajeroService = detalleCajeroService;
        this.redisTemplate = redisTemplate;
        this.asignacionComprobante = asignacionComprobante;
        this.asientoService = asientoService;
        this.configContable = configContable;
        this.periodoContableService = periodoContableService;
        this.tercerosService = tercerosService;
    }

    /** Construye el nombre completo de un tercero (nombres + apellidos) o razón social. */
    private String construirNombreCompleto(Terceros t) {
        if (t == null) return "";
        if (t.getRazonSocial() != null && !t.getRazonSocial().isBlank()) {
            return t.getRazonSocial().trim();
        }
        StringBuilder sb = new StringBuilder();
        if (t.getNombre1() != null && !t.getNombre1().isBlank()) sb.append(t.getNombre1().trim());
        if (t.getNombre2() != null && !t.getNombre2().isBlank()) sb.append(" ").append(t.getNombre2().trim());
        if (t.getApellido1() != null && !t.getApellido1().isBlank()) sb.append(" ").append(t.getApellido1().trim());
        if (t.getApellido2() != null && !t.getApellido2().isBlank()) sb.append(" ").append(t.getApellido2().trim());
        return sb.toString().replaceAll("\\s+", " ").trim();
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

        // Validar periodo contable abierto en la fecha del recibo (NIIF / control fiscal).
        LocalDate fechaReciboParsed = LocalDate.parse(dto.getFechaRecibo());
        periodoContableService.validarPeriodoAbierto(fechaReciboParsed);

        boolean esConceptoAbierto = Boolean.TRUE.equals(dto.getConceptoAbierto());

        if (esConceptoAbierto) {
            if (dto.getMontoConceptoAbierto() == null || dto.getMontoConceptoAbierto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El monto del concepto abierto debe ser mayor a 0");
            }
            // Trazabilidad obligatoria: o tercero, o al menos nombre del beneficiario
            boolean tieneTercero = dto.getTerceroId() != null;
            boolean tieneBeneficiario = dto.getBeneficiarioNombre() != null && !dto.getBeneficiarioNombre().isBlank();
            if (!tieneTercero && !tieneBeneficiario) {
                throw new RuntimeException("Para concepto abierto debe registrar el tercero o el nombre del beneficiario");
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
        // ─── Asignar comprobante RC con el prefijo del cajero ───
        if (dto.getCajeroId() == null) {
            throw new RuntimeException("Se requiere el cajero para asignar el comprobante del recibo de caja.");
        }
        try {
            AsignacionComprobanteService.Resultado r =
                    asignacionComprobante.asignar(dto.getCajeroId(), TipoMovimientoComprobante.RC);
            recibo.setComprobante(r.getComprobante());
            recibo.setNumeroDocumento(r.getNumeroDocumento());
            recibo.setConsecutivo(r.getConsecutivo());
        } catch (AsignacionComprobanteService.ComprobanteNoConfiguradoException ex) {
            throw new RuntimeException(ex.getMessage());
        }
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
        recibo.setVendedorId(dto.getVendedorId());
        recibo.setBeneficiarioNombre(dto.getBeneficiarioNombre());
        recibo.setBeneficiarioDocumento(dto.getBeneficiarioDocumento());

        // Cuenta contable (opcional)
        if (dto.getCuentaContableId() != null) {
            CuentaContable cc = cuentaContableRepository.findById(dto.getCuentaContableId())
                    .orElseThrow(() -> new RuntimeException("Cuenta contable no encontrada: " + dto.getCuentaContableId()));
            recibo.setCuentaContable(cc);
        }

        // Concepto abierto registrado (FK, opcional desde que se eliminó el combobox)
        if (esConceptoAbierto && dto.getConceptoAbiertoId() != null) {
            java.util.Optional<ConceptoAbierto> caOpt = conceptoAbiertoRepository.findById(dto.getConceptoAbiertoId());
            if (caOpt.isPresent()) {
                ConceptoAbierto ca = caOpt.get();
                recibo.setConceptoAbiertoRef(ca);
                if (recibo.getConcepto() == null || recibo.getConcepto().isBlank()) {
                    recibo.setConcepto(ca.getDescripcion());
                }
                if (recibo.getCuentaContable() == null && ca.getCuentaContable() != null) {
                    recibo.setCuentaContable(ca.getCuentaContable());
                }
            }
        }

        // Tercero (solo si NO es concepto abierto)
        if (!esConceptoAbierto) {
            Terceros tercero = tercerosRepository.findById(dto.getTerceroId())
                    .orElseThrow(() -> new RuntimeException("Tercero no encontrado: " + dto.getTerceroId()));
            recibo.setTercero(tercero);
            recibo.setTerceroNombre(construirNombreCompleto(tercero));
            recibo.setTerceroNit(tercero.getIdentificacion());
        } else if (dto.getTerceroId() != null) {
            // Concepto abierto con tercero asociado: lo enlazamos para trazabilidad.
            Terceros t = tercerosRepository.findById(dto.getTerceroId()).orElse(null);
            if (t != null) {
                recibo.setTercero(t);
                recibo.setTerceroNombre(construirNombreCompleto(t));
                recibo.setTerceroNit(t.getIdentificacion());
            }
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

        // Actualizar último movimiento del tercero
        try {
            if (recibo.getTercero() != null) {
                tercerosRepository.actualizarUltimoMovimiento(
                        recibo.getTercero().getTerceroId(), java.time.LocalDateTime.now());
            }
        } catch (Exception ex) {
            System.out.println("[UltimoMovimiento] Error actualizando tercero recibo caja: " + ex.getMessage());
        }

        // Registrar movimiento en cajero si hay sesión activa
        registrarMovimientoCajero(recibo);

        // Generar asiento contable (partida doble)
        generarAsientoContable(recibo);

        // Restar saldo a favor del cliente si se usó
        if (dto.getSaldoFavorUsado() != null && dto.getSaldoFavorUsado().compareTo(BigDecimal.ZERO) > 0) {
            if (recibo.getTercero() != null) {
                tercerosService.aumentarSaldofavorCliente(
                    recibo.getTercero().getTerceroId(),
                    null,
                    dto.getSaldoFavorUsado().doubleValue()
                );
            }
        }

        return toResponse(recibo);
    }

    /**
     * Genera el asiento contable del recibo:
     *  - DÉBITO a cada cuenta del método de pago (Bancolombia, Caja, etc.) por su monto
     *  - CRÉDITO a CxC del cliente (1305) por el total — o a la cuenta del concepto abierto.
     */
    private void generarAsientoContable(ReciboCaja recibo) {
        try {
            java.util.List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();

            // Débitos por cada método de pago (entrada de dinero)
            for (ReciboCajaMedioPago mp : recibo.getMediosPago()) {
                MetodosPago metodo = mp.getMetodoPago();
                CuentaContable cta = resolverCuentaMetodoPago(metodo);
                if (cta == null) {
                    System.out.println("[AsientoRC] Método '" + (metodo != null ? metodo.getDescripcion() : "null")
                            + "' sin cuenta contable. Asiento se omite.");
                    return;
                }
                lineas.add(AsientoContableService.LineaDTO.debito(cta.getId(), mp.getMonto(),
                        "Ingreso por " + metodo.getDescripcion()));
            }

            // ── DÉBITO por retenciones SUFRIDAS (el cliente nos retuvo) ──
            // El cliente pagó (total - retenciones) y emitió certificado por las retenciones.
            // Contabilizamos las retenciones como anticipo de impuestos (clase 1355xx).
            // El CRÉDITO a CxC debe ser por el VALOR BRUTO (no neto), para cancelar
            // completamente la factura original.
            java.math.BigDecimal retf = recibo.getRetefuente() != null ? recibo.getRetefuente() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal reti = recibo.getReteiva()    != null ? recibo.getReteiva()    : java.math.BigDecimal.ZERO;
            java.math.BigDecimal retc = recibo.getReteica()    != null ? recibo.getReteica()    : java.math.BigDecimal.ZERO;
            java.math.BigDecimal totalRetenciones = retf.add(reti).add(retc);

            if (retf.compareTo(java.math.BigDecimal.ZERO) > 0) {
                CuentaContable ctaRetf = configContable.anticipoRetefuente().orElse(null);
                if (ctaRetf != null) {
                    AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                            .debito(ctaRetf.getId(), retf, "Retefuente que nos practicaron");
                    if (recibo.getTercero() != null) l.conTercero(recibo.getTercero().getTerceroId(), recibo.getTerceroNombre());
                    lineas.add(l);
                } else {
                    System.out.println("[AsientoRC] Cuenta 135515 (anticipo retefuente) no configurada. Retención no se contabiliza por separado.");
                }
            }
            if (reti.compareTo(java.math.BigDecimal.ZERO) > 0) {
                CuentaContable ctaReti = configContable.anticipoReteiva().orElse(null);
                if (ctaReti != null) {
                    AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                            .debito(ctaReti.getId(), reti, "ReteIVA que nos practicaron");
                    if (recibo.getTercero() != null) l.conTercero(recibo.getTercero().getTerceroId(), recibo.getTerceroNombre());
                    lineas.add(l);
                } else {
                    System.out.println("[AsientoRC] Cuenta 135517 (anticipo reteIVA) no configurada.");
                }
            }
            if (retc.compareTo(java.math.BigDecimal.ZERO) > 0) {
                CuentaContable ctaRetc = configContable.anticipoReteica().orElse(null);
                if (ctaRetc != null) {
                    AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                            .debito(ctaRetc.getId(), retc, "ReteICA que nos practicaron");
                    if (recibo.getTercero() != null) l.conTercero(recibo.getTercero().getTerceroId(), recibo.getTerceroNombre());
                    lineas.add(l);
                } else {
                    System.out.println("[AsientoRC] Cuenta 135518 (anticipo reteICA) no configurada.");
                }
            }

            // Crédito a la contrapartida (CxC se cancela por valor BRUTO = total + retenciones).
            CuentaContable contrapartida;
            String desc;
            java.math.BigDecimal montoCredito;
            if (Boolean.TRUE.equals(recibo.getConceptoAbierto())) {
                // Concepto abierto: usa la cuenta del concepto, o la del recibo. Sin retenciones aplicables.
                contrapartida = recibo.getCuentaContable();
                desc = "Concepto abierto " + (recibo.getConcepto() != null ? recibo.getConcepto() : "");
                montoCredito = recibo.getTotal();
            } else {
                // Abono a CxC: usa la cuenta de Clientes (1305) del PUC. CxC bruta = total + retenciones sufridas.
                contrapartida = configContable.cxcClientes().orElse(null);
                desc = "Abono a Clientes (CxC) " + (recibo.getTerceroNombre() != null ? recibo.getTerceroNombre() : "");
                montoCredito = recibo.getTotal().add(totalRetenciones);
            }
            if (contrapartida == null) {
                System.out.println("[AsientoRC] Sin cuenta contrapartida. Asiento se omite. (configure cuenta 1305 Clientes en el PUC)");
                return;
            }
            AsientoContableService.LineaDTO creditoLinea = AsientoContableService.LineaDTO
                    .credito(contrapartida.getId(), montoCredito, desc);
            if (recibo.getTercero() != null) {
                creditoLinea.conTercero(recibo.getTercero().getTerceroId(), recibo.getTerceroNombre());
            }
            lineas.add(creditoLinea);

            asientoService.generarAsiento(
                    recibo.getNumeroDocumento() != null ? recibo.getNumeroDocumento() : ("RC-" + recibo.getConsecutivo()),
                    recibo.getFechaRecibo() != null ? recibo.getFechaRecibo() : recibo.getFecha(),
                    "Recibo de Caja " + (recibo.getNumeroDocumento() != null ? recibo.getNumeroDocumento() : recibo.getConsecutivo())
                            + (recibo.getTerceroNombre() != null ? " - " + recibo.getTerceroNombre() : ""),
                    "RC",
                    recibo.getId(),
                    recibo.getComprobante(),
                    lineas
            );
        } catch (Exception ex) {
            System.out.println("[AsientoRC] Error generando asiento (no crítico): " + ex.getMessage());
            asientoFallidoService.registrar("RECIBOS_CAJA", "RC",
                    recibo.getId(), recibo.getNumeroDocumento(),
                    "Error generando asiento de recibo de caja: " + ex.getMessage(), ex);
        }
    }

    /** Resuelve la cuenta contable que afecta un método de pago: cuenta bancaria gana sobre directa. */
    private CuentaContable resolverCuentaMetodoPago(MetodosPago metodo) {
        if (metodo == null) return null;
        if (metodo.getCuentaBancaria() != null && metodo.getCuentaBancaria().getCuentaContable() != null) {
            return metodo.getCuentaBancaria().getCuentaContable();
        }
        if (metodo.getCuentaContable() != null) return metodo.getCuentaContable();
        return configContable.cajaGeneral().orElse(null);
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
                        recibo.getNumeroDocumento() != null ? recibo.getNumeroDocumento() : "RC-" + recibo.getConsecutivo(),
                        recibo.getId(),
                        recibo.getTotal(),
                        BigDecimal.ZERO,
                        montoEfectivo,
                        montoElectronico,
                        "Recibo Caja " + (recibo.getNumeroDocumento() != null ? recibo.getNumeroDocumento() : recibo.getConsecutivo())
                                + " - " + (recibo.getTerceroNombre() != null ? recibo.getTerceroNombre() : recibo.getConcepto()),
                        recibo.getComprobante()
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

    @Transactional
    public ReciboCajaResponseDTO anular(Long id, String motivo, Integer usuarioId) {
        ReciboCaja recibo = reciboRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recibo de caja no encontrado: " + id));
        if ("ANULADO".equalsIgnoreCase(recibo.getEstado())) {
            throw new RuntimeException("El recibo ya está anulado");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new RuntimeException("El motivo de anulación es obligatorio");
        }

        // Bloquear anulación si el periodo del documento está cerrado.
        periodoContableService.validarPeriodoAbierto(
                recibo.getFechaRecibo() != null ? recibo.getFechaRecibo() : recibo.getFecha());

        // Revertir saldos en CxC
        if (recibo.getDetalles() != null) {
            for (DetalleReciboCaja det : recibo.getDetalles()) {
                CuentaPorCobrar cxc = det.getCuentaPorCobrar();
                if (cxc != null) {
                    BigDecimal saldoActual = cxc.getSaldo() != null ? cxc.getSaldo() : BigDecimal.ZERO;
                    BigDecimal saldoNuevo = saldoActual.add(det.getMontoAbonado());
                    BigDecimal valorNeto = cxc.getValorNeto() != null ? cxc.getValorNeto() : saldoNuevo;
                    if (saldoNuevo.compareTo(valorNeto) >= 0) {
                        cxc.setSaldo(valorNeto);
                        cxc.setEstado("PENDIENTE");
                    } else {
                        cxc.setSaldo(saldoNuevo);
                        cxc.setEstado("PARCIAL");
                    }
                    cxcRepository.save(cxc);
                }
            }
        }

        recibo.setEstado("ANULADO");
        recibo.setMotivoAnulacion(motivo);
        recibo.setFechaAnulacion(java.time.LocalDateTime.now());
        recibo.setAnuladoPorUsuarioId(usuarioId);
        recibo = reciboRepository.save(recibo);

        // Movimiento de cajero inverso
        registrarAnulacionCajero(recibo);

        return toResponse(recibo);
    }

    private void registrarAnulacionCajero(ReciboCaja recibo) {
        try {
            Long detalleCajeroId = obtenerDetalleCajeroId(recibo.getCajeroId());
            if (detalleCajeroId == null) return;
            BigDecimal montoEfectivo = BigDecimal.ZERO;
            BigDecimal montoElectronico = BigDecimal.ZERO;
            if (recibo.getMediosPago() != null) {
                for (ReciboCajaMedioPago mp : recibo.getMediosPago()) {
                    String sigla = mp.getMetodoPago().getSigla() != null
                            ? mp.getMetodoPago().getSigla().toUpperCase() : "";
                    if (sigla.startsWith("EF")) montoEfectivo = montoEfectivo.add(mp.getMonto());
                    else montoElectronico = montoElectronico.add(mp.getMonto());
                }
            }
            detalleCajeroService.registrarMovimiento(
                    detalleCajeroId,
                    MovimientoCajero.TipoMovimiento.ANULACION,
                    (recibo.getNumeroDocumento() != null ? recibo.getNumeroDocumento() : "RC-" + recibo.getConsecutivo()) + "-A",
                    recibo.getId(),
                    recibo.getTotal().negate(),
                    BigDecimal.ZERO,
                    montoEfectivo.negate(),
                    montoElectronico.negate(),
                    "Anulación Recibo Caja " + (recibo.getNumeroDocumento() != null ? recibo.getNumeroDocumento() : recibo.getConsecutivo()),
                    recibo.getComprobante()
            );
        } catch (Exception e) {
            System.out.println("Error registrando anulación cajero recibo: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ReciboCajaResponseDTO> listarTodos() {
        return reciboRepository.findAllByOrderByFechaCreacionDesc().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReciboCajaResponseDTO> listarPorFechas(LocalDate desde, LocalDate hasta) {
        return reciboRepository.findByFechaRango(desde, hasta).stream().map(this::toResponse).collect(Collectors.toList());
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
        dto.setNumeroDocumento(r.getNumeroDocumento());
        if (r.getComprobante() != null) {
            dto.setComprobanteId(r.getComprobante().getId());
            dto.setPrefijo(r.getComprobante().getPrefijo());
        }
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
        dto.setBeneficiarioNombre(r.getBeneficiarioNombre());
        dto.setBeneficiarioDocumento(r.getBeneficiarioDocumento());
        if (r.getConceptoAbiertoRef() != null) {
            dto.setConceptoAbiertoId(r.getConceptoAbiertoRef().getId());
            dto.setConceptoAbiertoDescripcion(r.getConceptoAbiertoRef().getDescripcion());
        }
        if (r.getCuentaContable() != null) {
            dto.setCuentaContableId(r.getCuentaContable().getId());
            dto.setCuentaContableCodigo(r.getCuentaContable().getCodigo());
            dto.setCuentaContableNombre(r.getCuentaContable().getNombre());
        }
        dto.setVendedorId(r.getVendedorId());
        if (r.getVendedorId() != null) {
            reciboRepository.findVendedorNombreById(r.getVendedorId())
                    .ifPresent(dto::setVendedorNombre);
        }
        dto.setFechaCreacion(r.getFechaCreacion());
        dto.setFechaAnulacion(r.getFechaAnulacion());
        dto.setMotivoAnulacion(r.getMotivoAnulacion());
        dto.setAnuladoPorUsuarioId(r.getAnuladoPorUsuarioId());

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

