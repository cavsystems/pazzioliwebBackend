package com.pazzioliweb.tesoreriamodule.service;

import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.service.DetalleCajeroService;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.comprasmodule.entity.CuentaPorPagar;
import com.pazzioliweb.comprasmodule.repository.CuentaPorPagarRepository;
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
    private final AsignacionComprobanteService asignacionComprobante;
    private final AsientoContableService asientoService;
    private final ConfiguracionContableService configContable;
    private final PeriodoContableService periodoContableService;
    @org.springframework.beans.factory.annotation.Autowired
    private com.pazzioliweb.comprobantesmodule.service.AsientoFallidoService asientoFallidoService;
    @org.springframework.beans.factory.annotation.Autowired
    private com.pazzioliweb.tercerosmodule.service.SaldoWebSocketBroadcastService saldoWebSocketBroadcastService;

    public ComprobanteEgresoService(ComprobanteEgresoRepository egresoRepository,
                                     CuentaPorPagarRepository cxpRepository,
                                     TercerosRepository tercerosRepository,
                                     MetodosPagoRepository metodosPagoRepository,
                                     ConceptoAbiertoRepository conceptoAbiertoRepository,
                                     CuentaContableRepository cuentaContableRepository,
                                     DetalleCajeroService detalleCajeroService,
                                     RedisTemplate<String, DatosSesiones> redisTemplate,
                                     AsignacionComprobanteService asignacionComprobante,
                                     AsientoContableService asientoService,
                                     ConfiguracionContableService configContable,
                                     PeriodoContableService periodoContableService) {
        this.egresoRepository = egresoRepository;
        this.cxpRepository = cxpRepository;
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
            if (dto.getMontoConceptoAbierto() == null || dto.getMontoConceptoAbierto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El monto del concepto abierto debe ser mayor a 0");
            }
            boolean tieneTercero = dto.getTerceroId() != null;
            boolean tieneBeneficiario =
                    dto.getBeneficiarioNombre() != null && !dto.getBeneficiarioNombre().isBlank();
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
        // ─── Asignar comprobante CE con el prefijo del cajero ───
        if (dto.getCajeroId() == null) {
            throw new RuntimeException("Se requiere el cajero para asignar el comprobante de egreso.");
        }
        try {
            AsignacionComprobanteService.Resultado r =
                    asignacionComprobante.asignar(dto.getCajeroId(), TipoMovimientoComprobante.CE);
            egreso.setComprobante(r.getComprobante());
            egreso.setNumeroDocumento(r.getNumeroDocumento());
            egreso.setConsecutivo(r.getConsecutivo());
        } catch (AsignacionComprobanteService.ComprobanteNoConfiguradoException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        egreso.setConceptoAbierto(esConceptoAbierto);
        egreso.setFecha(LocalDate.now());
        egreso.setFechaEgreso(dto.getFechaEgreso() != null && !dto.getFechaEgreso().isBlank()
                ? LocalDate.parse(dto.getFechaEgreso()) : LocalDate.now());

        // Validar periodo contable abierto en la fecha del egreso.
        periodoContableService.validarPeriodoAbierto(egreso.getFechaEgreso());
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

        if (esConceptoAbierto && dto.getConceptoAbiertoId() != null) {
            java.util.Optional<ConceptoAbierto> caOpt = conceptoAbiertoRepository.findById(dto.getConceptoAbiertoId());
            if (caOpt.isPresent()) {
                ConceptoAbierto ca = caOpt.get();
                String tipoCa = ca.getTipo() == null ? "" : ca.getTipo().toUpperCase();
                if (!"EGRESO".equals(tipoCa) && !"AMBOS".equals(tipoCa)) {
                    throw new RuntimeException("El concepto seleccionado no aplica para comprobantes de egreso");
                }
                egreso.setConceptoAbiertoRef(ca);
                if (egreso.getConcepto() == null || egreso.getConcepto().isBlank()) {
                    egreso.setConcepto(ca.getDescripcion());
                }
                if (egreso.getCuentaContable() == null && ca.getCuentaContable() != null) {
                    egreso.setCuentaContable(ca.getCuentaContable());
                }
            }
        } else if (esConceptoAbierto) {
            // No concept category selected — just use the provided concepto text
        }

        // Tercero (solo si NO es concepto abierto)
        if (!esConceptoAbierto) {
            Terceros tercero = tercerosRepository.findById(dto.getTerceroId())
                    .orElseThrow(() -> new RuntimeException("Tercero no encontrado: " + dto.getTerceroId()));
            egreso.setTercero(tercero);
            egreso.setTerceroNombre(construirNombreCompleto(tercero));
            egreso.setTerceroNit(tercero.getIdentificacion());
        } else if (dto.getTerceroId() != null) {
            Terceros t = tercerosRepository.findById(dto.getTerceroId()).orElse(null);
            if (t != null) {
                egreso.setTercero(t);
                egreso.setTerceroNombre(construirNombreCompleto(t));
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

        // Procesar saldo a favor de la empresa usado
        if (dto.getSaldoFavorUsado() != null && dto.getSaldoFavorUsado().compareTo(BigDecimal.ZERO) > 0) {
            if (egreso.getTercero() != null) {
                Terceros tercero = egreso.getTercero();
                Double saldoActual = tercero.getSaldofavorEmpresa() != null ? tercero.getSaldofavorEmpresa() : 0.0;
                Double nuevoSaldo = saldoActual - dto.getSaldoFavorUsado().doubleValue();
                tercero.setSaldofavorEmpresa(nuevoSaldo);
                tercerosRepository.save(tercero);
                
                // Enviar notificación WebSocket
                saldoWebSocketBroadcastService.notificarSaldoActualizado(tercero.getTerceroId(), nuevoSaldo);
            }
        }

        // Actualizar último movimiento del tercero
        try {
            if (egreso.getTercero() != null) {
                tercerosRepository.actualizarUltimoMovimiento(
                        egreso.getTercero().getTerceroId(), java.time.LocalDateTime.now());
            }
        } catch (Exception ex) {
            System.out.println("[UltimoMovimiento] Error actualizando tercero comprobante egreso: " + ex.getMessage());
        }

        // Registrar movimiento en cajero si hay sesión activa
        registrarMovimientoCajero(egreso);

        // Generar asiento contable (partida doble — egreso es lo opuesto del recibo)
        generarAsientoContable(egreso);

        return toResponse(egreso);
    }

    /**
     * Asiento del egreso:
     *   - CRÉDITO a cada cuenta del método de pago (salida de dinero)
     *   - DÉBITO a CxP (2205) si es pago de factura, o a la cuenta del concepto abierto.
     */
    private void generarAsientoContable(ComprobanteEgreso egreso) {
        try {
            java.util.List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();

            // Crédito a cada método de pago (salida)
            for (ComprobanteEgresoMedioPago mp : egreso.getMediosPago()) {
                MetodosPago metodo = mp.getMetodoPago();
                CuentaContable cta = resolverCuentaMetodoPago(metodo);
                if (cta == null) {
                    System.out.println("[AsientoCE] Método '" + (metodo != null ? metodo.getDescripcion() : "null")
                            + "' sin cuenta contable. Asiento se omite.");
                    return;
                }
                lineas.add(AsientoContableService.LineaDTO.credito(cta.getId(), mp.getMonto(),
                        "Egreso por " + metodo.getDescripcion()));
            }

            // Débito a la contrapartida
            CuentaContable contrapartida;
            String desc;
            if (Boolean.TRUE.equals(egreso.getConceptoAbierto())) {
                contrapartida = egreso.getCuentaContable();
                desc = "Concepto abierto " + (egreso.getConcepto() != null ? egreso.getConcepto() : "");
            } else {
                contrapartida = configContable.cxpProveedores().orElse(null);
                desc = "Pago a Proveedores (CxP) " + (egreso.getTerceroNombre() != null ? egreso.getTerceroNombre() : "");
            }
            if (contrapartida == null) {
                System.out.println("[AsientoCE] Sin cuenta contrapartida. Configure 2205 Proveedores en el PUC.");
                return;
            }

            // Determinar si hay retenciones practicadas al proveedor
            boolean esCA = Boolean.TRUE.equals(egreso.getConceptoAbierto());
            java.math.BigDecimal rf = egreso.getRetefuente() != null ? egreso.getRetefuente() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal rv = egreso.getReteiva()    != null ? egreso.getReteiva()    : java.math.BigDecimal.ZERO;
            java.math.BigDecimal rc = egreso.getReteica()    != null ? egreso.getReteica()    : java.math.BigDecimal.ZERO;
            boolean hayRet = !esCA && (rf.compareTo(java.math.BigDecimal.ZERO) > 0
                                    || rv.compareTo(java.math.BigDecimal.ZERO) > 0
                                    || rc.compareTo(java.math.BigDecimal.ZERO) > 0);

            // DÉBITO CxP: si hay retenciones usamos BRUTO (subtotal), si no usamos NETO (total)
            java.math.BigDecimal montoDebitoContrapartida = hayRet ? egreso.getSubtotal() : egreso.getTotal();
            AsientoContableService.LineaDTO debitoLinea = AsientoContableService.LineaDTO
                    .debito(contrapartida.getId(), montoDebitoContrapartida, desc);
            if (egreso.getTercero() != null) {
                debitoLinea.conTercero(egreso.getTercero().getTerceroId(), egreso.getTerceroNombre());
            }
            lineas.add(debitoLinea);

            // CRÉDITO retenciones a pagar (236505, 236540, 236570) cuando practicamos retención al proveedor
            if (hayRet) {
                Integer tercId = egreso.getTercero() != null ? egreso.getTercero().getTerceroId() : null;
                String tercNom = egreso.getTerceroNombre();
                if (rf.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    configContable.retefuentePagar().ifPresent(cta -> {
                        AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                                .credito(cta.getId(), rf, "ReteFuente retenida a proveedor");
                        if (tercId != null) l.conTercero(tercId, tercNom);
                        lineas.add(l);
                    });
                }
                if (rv.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    configContable.reteivaPagar().ifPresent(cta -> {
                        AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                                .credito(cta.getId(), rv, "ReteIVA retenida a proveedor");
                        if (tercId != null) l.conTercero(tercId, tercNom);
                        lineas.add(l);
                    });
                }
                if (rc.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    configContable.reteicaPagar().ifPresent(cta -> {
                        AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                                .credito(cta.getId(), rc, "ReteICA retenida a proveedor");
                        if (tercId != null) l.conTercero(tercId, tercNom);
                        lineas.add(l);
                    });
                }
            }

            asientoService.generarAsiento(
                    egreso.getNumeroDocumento() != null ? egreso.getNumeroDocumento() : ("CE-" + egreso.getConsecutivo()),
                    egreso.getFechaEgreso() != null ? egreso.getFechaEgreso() : egreso.getFecha(),
                    "Comprobante Egreso " + (egreso.getNumeroDocumento() != null ? egreso.getNumeroDocumento() : egreso.getConsecutivo())
                            + (egreso.getTerceroNombre() != null ? " - " + egreso.getTerceroNombre() : ""),
                    "CE",
                    egreso.getId(),
                    egreso.getComprobante(),
                    lineas
            );
        } catch (Exception ex) {
            System.out.println("[AsientoCE] Error generando asiento (no crítico): " + ex.getMessage());
            asientoFallidoService.registrar("COMPROBANTES_EGRESO", "CE",
                    egreso.getId(), egreso.getNumeroDocumento(),
                    "Error generando asiento de comprobante de egreso: " + ex.getMessage(), ex);
        }
    }

    private CuentaContable resolverCuentaMetodoPago(MetodosPago metodo) {
        if (metodo == null) return null;
        // Cuenta bancaria gana sobre cuenta directa (el dinero entra/sale del banco)
        if (metodo.getCuentaBancaria() != null && metodo.getCuentaBancaria().getCuentaContable() != null) {
            return metodo.getCuentaBancaria().getCuentaContable();
        }
        if (metodo.getCuentaContable() != null) return metodo.getCuentaContable();
        return configContable.cajaGeneral().orElse(null);
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
                        egreso.getNumeroDocumento() != null ? egreso.getNumeroDocumento() : "CE-" + egreso.getConsecutivo(),
                        egreso.getId(),
                        egreso.getTotal(),
                        BigDecimal.ZERO,
                        montoEfectivo,
                        montoElectronico,
                        "Comprobante Egreso " + (egreso.getNumeroDocumento() != null ? egreso.getNumeroDocumento() : egreso.getConsecutivo())
                                + " - " + (egreso.getTerceroNombre() != null ? egreso.getTerceroNombre() : egreso.getConcepto()),
                        egreso.getComprobante()
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

        // Bloquear anulación si el periodo del documento está cerrado.
        periodoContableService.validarPeriodoAbierto(
                egreso.getFechaEgreso() != null ? egreso.getFechaEgreso() : egreso.getFecha());

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
                    (egreso.getNumeroDocumento() != null ? egreso.getNumeroDocumento() : "CE-" + egreso.getConsecutivo()) + "-A",
                    egreso.getId(),
                    egreso.getTotal(),
                    BigDecimal.ZERO,
                    montoEfectivo,
                    montoElectronico,
                    "Anulación Comprobante Egreso " + (egreso.getNumeroDocumento() != null ? egreso.getNumeroDocumento() : egreso.getConsecutivo()),
                    egreso.getComprobante()
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
    public List<ComprobanteEgresoResponseDTO> listarPorFechas(LocalDate desde, LocalDate hasta) {
        return egresoRepository.findByFechaEgresoRango(desde, hasta).stream()
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
        dto.setNumeroDocumento(e.getNumeroDocumento());
        if (e.getComprobante() != null) {
            dto.setComprobanteId(e.getComprobante().getId());
            dto.setPrefijo(e.getComprobante().getPrefijo());
        }
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

