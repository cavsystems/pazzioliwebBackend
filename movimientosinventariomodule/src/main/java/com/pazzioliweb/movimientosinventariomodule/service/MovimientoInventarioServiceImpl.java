package com.pazzioliweb.movimientosinventariomodule.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.commonbacken.util.Jwcommon;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.commonbacken.events.MovimientoRegistradoEvent;
import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.comprobantesmodule.service.AsientoContableService;
import com.pazzioliweb.comprobantesmodule.service.ConfiguracionContableService;
import org.springframework.context.ApplicationEventPublisher;
import com.pazzioliweb.movimientosinventariomodule.dtos.KardexReportDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioCreateDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioDetalleCreateDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioResponseDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.MovimientoInventarioUpdateDto;
import com.pazzioliweb.movimientosinventariomodule.entity.Kardex;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventario;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventarioDetalle;
import com.pazzioliweb.movimientosinventariomodule.enums.EstadoMovimiento;
import com.pazzioliweb.movimientosinventariomodule.enums.TipoMovimiento;
import com.pazzioliweb.movimientosinventariomodule.mapper.MovimientoInventarioMapper;
import com.pazzioliweb.movimientosinventariomodule.repository.KardexRepository;
import com.pazzioliweb.movimientosinventariomodule.repository.MovimientoInventarioDetalleRepository;
import com.pazzioliweb.movimientosinventariomodule.repository.MovimientoInventarioRepository;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.Existencias;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.productosmodule.repositori.ExistenciasRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private MovimientoInventarioDetalleRepository detalleRepository;

    @Autowired
    private KardexRepository kardexRepository;

    @Autowired
    private MovimientoInventarioMapper mapper;

    @Autowired
    private ComprobanteContableRepository comprobantesRepository;

    @Autowired
    private ProductoVarianteRepository productoVarianteRepository;

    @Autowired
    private BodegasRepository bodegasRepository;

    @Autowired
    private ExistenciasRepository existenciasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RedisTemplate<String, DatosSesiones> redisTemplate;
    @Autowired
    private Jwcommon jwcommon;
    @Autowired
    private AsientoContableService asientoService;
    @Autowired
    private ConfiguracionContableService configContable;
    @Autowired
    private com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository comprobantesrepositori;
    @Autowired
    private com.pazzioliweb.comprobantesmodule.service.AsientoFallidoService asientoFallidoService;
    @Autowired
    private com.pazzioliweb.comprobantesmodule.service.PeriodoContableService periodoContableService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    @Transactional
    public MovimientoInventarioResponseDto crearMovimiento(
            MovimientoInventarioCreateDto createDto,
            ComprobanteContable comprobante,
            Usuario usuario,
            HttpServletRequest request) {

        // Validar periodo contable abierto antes de mover inventario (afecta kardex y asientos).
        java.time.LocalDate fechaMov = createDto.getFechaEmision() != null
                ? createDto.getFechaEmision() : LocalDate.now();
        periodoContableService.validarPeriodoAbierto(fechaMov);

        // Resolver comprobante desde ID si no viene pasado
        if (comprobante == null && createDto.getComprobanteId() != null) {
            comprobante = comprobantesRepository.findById(createDto.getComprobanteId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + createDto.getComprobanteId()));
        }

        // Resolver usuario desde ID si no viene pasado
        if (usuario == null && createDto.getUsuarioId() != null) {
            usuario = usuarioRepository.findByCodigo(createDto.getUsuarioId().intValue())
                    .orElse(null);
        }

        // Asignar consecutivo automático si no viene
        if (createDto.getConsecutivo() == null && comprobante != null) {
            int nextConsecutivo = movimientoRepository
                    .findTopByComprobanteOrderByConsecutivoDesc(comprobante)
                    .map(m -> m.getConsecutivo() + 1)
                    .orElse(1);
            comprobante.setSiguienteConsecutivo(comprobante.getSiguienteConsecutivo()+1);


            createDto.setConsecutivo(nextConsecutivo);
        }

        // Crear y guardar movimiento
        MovimientoInventario movimiento = mapper.toEntity(createDto, comprobante, usuario);
        movimiento.setFechaCreacion(LocalDateTime.now());
        if (movimiento.getEstado() == null) {
            movimiento.setEstado(EstadoMovimiento.ACTIVO);
        }
        try {
            String token=null;
            if (request.getCookies() != null) {

                for (Cookie cookie : request.getCookies()) {
                    if ("token".equals(cookie.getName())) {
                        System.out.println("Cookie token: " + cookie.getValue());
                        token = cookie.getValue();
                        Claims claims = jwcommon.extraerClaims( token);
                        DatosSesiones datos = redisTemplate.opsForValue().get( claims.get("idsecion",String.class));
                        usuario = entityManager.getReference(Usuario.class, datos.getIdusuario());
                        movimiento.setUsuario(usuario);

                        break;

                    }

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        movimientoRepository.save(movimiento);

        TipoMovimiento tipo = movimiento.getTipo();
        List<MovimientoInventarioDetalle> detalles = new ArrayList<>();

        for (MovimientoInventarioDetalleCreateDto detalleDto : createDto.getDetalles()) {

            // Resolver variante
            ProductoVariante variante = productoVarianteRepository
                    .findById(detalleDto.getProductoVarianteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ProductoVariante no encontrado: " + detalleDto.getProductoVarianteId()));

            // Resolver bodegas
            Bodegas bodegaOrigen = null;
            Bodegas bodegaDestino = null;

            if (detalleDto.getBodegaOrigenId() != null) {
                bodegaOrigen = bodegasRepository.findById(detalleDto.getBodegaOrigenId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Bodega origen no encontrada: " + detalleDto.getBodegaOrigenId()));
            }
            if (detalleDto.getBodegaDestinoId() != null) {
                bodegaDestino = bodegasRepository.findById(detalleDto.getBodegaDestinoId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Bodega destino no encontrada: " + detalleDto.getBodegaDestinoId()));
            }

            double costoUnitario = detalleDto.getCostoUnitario() != null ? detalleDto.getCostoUnitario() : 0.0;
            double cantidad = detalleDto.getCantidad();
            double totalDetalle = detalleDto.getTotalDetalle() != null
                    ? detalleDto.getTotalDetalle()
                    : costoUnitario * cantidad;

            MovimientoInventarioDetalle detalle = mapper.toDetalleEntity(
                    detalleDto, movimiento, variante, bodegaOrigen, bodegaDestino);
            detalle.setCostoUnitario(costoUnitario);
            detalle.setCostoPromedio(costoUnitario);
            detalle.setTotalDetalle(totalDetalle);
            detalleRepository.save(detalle);

            // Kardex: SALIDA o TRASLADO → registrar salida en bodega origen
            if (bodegaOrigen != null && (tipo == TipoMovimiento.SALIDA || tipo == TipoMovimiento.TRASLADO)) {
                crearKardexEntry(movimiento, detalle, variante, bodegaOrigen,
                        0.0, cantidad, costoUnitario, tipo);
            }

            // Kardex: ENTRADA → registrar entrada en bodega destino
            if (tipo == TipoMovimiento.ENTRADA && bodegaDestino != null) {
                crearKardexEntry(movimiento, detalle, variante, bodegaDestino,
                        cantidad, 0.0, costoUnitario, tipo);
            }
            // Kardex: TRASLADO → registrar entrada en bodega destino
            else if (tipo == TipoMovimiento.TRASLADO && bodegaDestino != null) {
                crearKardexEntry(movimiento, detalle, variante, bodegaDestino,
                        cantidad, 0.0, costoUnitario, tipo);
            }

            detalles.add(detalle);
        }

        // Actualizar total del movimiento
        double total = detalles.stream().mapToDouble(MovimientoInventarioDetalle::getTotalDetalle).sum();
        movimiento.setTotal(total);
        movimientoRepository.save(movimiento);

        // ── Asiento contable para entrada/salida manual de inventario ──
        // Traslado entre bodegas (TRASLADO) no genera asiento (neto cero).
        generarAsientoMovimientoInventario(movimiento, total);

        // ── Broadcast WebSocket: notifica a todos los clientes conectados
        // que el comprobante cambió (consecutivo avanzó). AFTER_COMMIT via evento.
        if (movimiento.getComprobante() != null) {
            eventPublisher.publishEvent(new MovimientoRegistradoEvent(
                this,
                movimiento.getComprobante().getId(),
                movimiento.getMovimientoId(),
                movimiento.getTipo() != null ? movimiento.getTipo().name() : null
            ));
        }

        return mapper.toResponse(movimiento, detalles);
    }

    /**
     * Asiento contable para movimientos manuales de inventario:
     *  - ENTRADA (EI): DR Inventarios (1435) / CR Sobrantes-Ingresos no op. (4295)
     *  - SALIDA  (SI): DR Pérdidas en inventario (5295) / CR Inventarios (1435)
     *  - TRASLADO (TI): no genera asiento (mismo dueño, neto cero)
     *
     * El monto se toma del total del movimiento (cantidad × costo unitario).
     * Try/catch defensivo para no romper la persistencia del movimiento si
     * el PUC no tiene alguna de las cuentas configuradas.
     */
    private void generarAsientoMovimientoInventario(MovimientoInventario mov, double totalMov) {
        try {
            if (mov.getTipo() == TipoMovimiento.TRASLADO) return; // neto cero
            if (totalMov <= 0) return; // nada que registrar

            java.math.BigDecimal total = java.math.BigDecimal.valueOf(totalMov);
            CuentaContable inventarios = configContable.inventarios().orElse(null);
            if (inventarios == null) {
                System.out.println("[AsientoMovInv] Cuenta 1435 Inventarios no configurada. Asiento omitido.");
                return;
            }

            java.util.List<AsientoContableService.LineaDTO> lineas = new java.util.ArrayList<>();
            String descripcion;
            String tipoOrigen;

            if (mov.getTipo() == TipoMovimiento.ENTRADA) {
                CuentaContable contra = configContable.ajusteEntradaInventario().orElse(null);
                if (contra == null) {
                    System.out.println("[AsientoMovInv] Cuenta 4295 (sobrantes) no configurada. Asiento omitido.");
                    return;
                }
                lineas.add(AsientoContableService.LineaDTO.debito(inventarios.getId(), total,
                        "Entrada manual de inventario"));
                lineas.add(AsientoContableService.LineaDTO.credito(contra.getId(), total,
                        "Sobrantes / ajuste positivo de inventario"));
                descripcion = "Entrada manual inventario";
                tipoOrigen = "EI";
            } else if (mov.getTipo() == TipoMovimiento.SALIDA) {
                CuentaContable contra = configContable.ajusteSalidaInventario().orElse(null);
                if (contra == null) {
                    System.out.println("[AsientoMovInv] Cuenta 5295 (pérdidas) no configurada. Asiento omitido.");
                    return;
                }
                lineas.add(AsientoContableService.LineaDTO.debito(contra.getId(), total,
                        "Pérdidas/dañados/consumo interno"));
                lineas.add(AsientoContableService.LineaDTO.credito(inventarios.getId(), total,
                        "Salida manual de inventario"));
                descripcion = "Salida manual inventario";
                tipoOrigen = "SI";
            } else {
                return; // tipo desconocido
            }

            String numeroAsiento;
            if (mov.getComprobante() != null && mov.getConsecutivo() != null) {
                numeroAsiento = mov.getComprobante().getPrefijo() + "-" + mov.getConsecutivo();
            } else {
                numeroAsiento = tipoOrigen + "-" + mov.getMovimientoId();
            }

            asientoService.generarAsiento(
                    numeroAsiento,
                    mov.getFechaEmision() != null ? mov.getFechaEmision() : LocalDate.now(),
                    descripcion + " #" + (mov.getMovimientoId() != null ? mov.getMovimientoId() : "?"),
                    tipoOrigen,
                    mov.getMovimientoId(),
                    mov.getComprobante(),
                    lineas
            );
        } catch (Exception ex) {
            System.out.println("[AsientoMovInv] Error generando asiento (no crítico): " + ex.getMessage());
            String tipoOrig = mov.getTipo() == TipoMovimiento.ENTRADA ? "EI"
                            : mov.getTipo() == TipoMovimiento.SALIDA ? "SI" : "TI";
            String numero = (mov.getComprobante() != null && mov.getConsecutivo() != null)
                    ? mov.getComprobante().getPrefijo() + "-" + mov.getConsecutivo()
                    : tipoOrig + "-" + mov.getMovimientoId();
            asientoFallidoService.registrar("INVENTARIO_MANUAL", tipoOrig,
                    mov.getMovimientoId(), numero,
                    "Error generando asiento de movimiento de inventario: " + ex.getMessage(), ex);
        }
    }

    private void crearKardexEntry(MovimientoInventario movimiento,
                                   MovimientoInventarioDetalle detalle,
                                   ProductoVariante variante,
                                   Bodegas bodega,
                                   double entrada,
                                   double salida,
                                   double costoUnitario,
                                   TipoMovimiento tipo) {

        // ── Saldo y costo promedio previos ──
        Kardex ultimo = kardexRepository
                .findTopByProductoVarianteAndBodegaOrderByFechaCreacionDesc(variante, bodega)
                .orElse(null);
        double saldoAnterior;
        double costoPromedioAnterior;

        if (ultimo != null) {
            saldoAnterior = ultimo.getSaldo();
            costoPromedioAnterior = ultimo.getCostoPromedio() != null ? ultimo.getCostoPromedio() : 0.0;
        } else {
            // Si no hay kardex previo, usar el stock actual de la tabla existencias
            Long varianteId = variante.getProductoVarianteId();
            java.util.Optional<Existencias> existenciasOpt = existenciasRepository
                    .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(
                            varianteId, bodega.getCodigo());
            saldoAnterior = existenciasOpt.map(e -> e.getExistencia() != null ? e.getExistencia().doubleValue() : 0.0).orElse(0.0);
            costoPromedioAnterior = 0.0;
        }

        double nuevoSaldo = saldoAnterior + entrada - salida;

        // ── Bloqueo de existencias negativas para SALIDA/TRASLADO ──
        // Solo aplica a salidas reales (no entradas). Si quedan negativas,
        // se distorsiona el COGS y rompe el costo promedio.
        if (salida > 0 && nuevoSaldo < 0) {
            throw new IllegalStateException(
                "Existencias insuficientes para el producto " + variante.getReferenciaVariantes() +
                " en la bodega " + bodega.getNombre() +
                ". Saldo actual: " + saldoAnterior + ", se intenta salir: " + salida +
                ". Operación bloqueada para no dejar inventario negativo."
            );
        }

        // ── Costo promedio ponderado real (NIIF Sec.13 / NIC 2) ──
        //   nuevoCostoProm = (saldoAnterior × costoPromAnt + entrada × costoNuevo) / nuevoSaldo
        // Solo aplica recálculo en ENTRADAS. En SALIDAS, el costo promedio
        // permanece igual al anterior (se usa para valorar la salida).
        double nuevoCostoPromedio;
        if (entrada > 0) {
            double valorSaldoPrevio = saldoAnterior * costoPromedioAnterior;
            double valorEntrada = entrada * costoUnitario;
            nuevoCostoPromedio = nuevoSaldo > 0
                    ? (valorSaldoPrevio + valorEntrada) / nuevoSaldo
                    : costoUnitario;
        } else {
            nuevoCostoPromedio = costoPromedioAnterior;
        }

        Kardex kardex = new Kardex();
        kardex.setMovimiento(movimiento);
        kardex.setDetalle(detalle);
        kardex.setProductoVariante(variante);
        kardex.setBodega(bodega);
        kardex.setFechaEmision(movimiento.getFechaEmision());
        kardex.setFechaCreacion(LocalDateTime.now());
        kardex.setEntrada(entrada);
        kardex.setSalida(salida);
        kardex.setSaldo(nuevoSaldo);
        kardex.setCostoUnitario(costoUnitario);
        kardex.setCostoPromedio(nuevoCostoPromedio);
        // Total del movimiento valorado al costo promedio cuando es SALIDA,
        // al costo unitario cuando es ENTRADA.
        kardex.setTotalCosto(salida > 0
                ? salida * nuevoCostoPromedio
                : entrada * costoUnitario);
        kardex.setTipo(tipo);
        kardex.setEstado(movimiento.getEstado());
        kardex.setObservaciones(movimiento.getObservaciones());
        kardexRepository.save(kardex);
    }

    @Override
    public MovimientoInventarioResponseDto actualizarMovimiento(Long movimientoId, MovimientoInventarioUpdateDto updateDto) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        if (!movimiento.getEstado().equals(EstadoMovimiento.BORRADOR)) {
            throw new RuntimeException("Solo se pueden actualizar movimientos en estado BORRADOR");
        }

        if (updateDto.getComprobanteId() != null) {
            ComprobanteContable comprobante = comprobantesRepository.findById(updateDto.getComprobanteId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + updateDto.getComprobanteId()));
            movimiento.setComprobante(comprobante);
        }
        if (updateDto.getConsecutivo() != null) {
            movimiento.setConsecutivo(updateDto.getConsecutivo());
        }
        if (updateDto.getFechaEmision() != null) {
            movimiento.setFechaEmision(updateDto.getFechaEmision());
        }
        movimiento.setObservaciones(updateDto.getObservaciones());

        movimientoRepository.save(movimiento);

        List<MovimientoInventarioDetalle> detalles =
                detalleRepository.findByMovimiento_MovimientoId(movimiento.getMovimientoId());

        return mapper.toResponse(movimiento, detalles);
    }

    @Override
    @Transactional
    public void anularMovimiento(Long movimientoId) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        // Validar que el periodo de la fecha original esté abierto.
        // La reversa escribe Kardex con la fecha del movimiento original — si está cerrado,
        // mutaríamos el saldo contable de un periodo congelado.
        java.time.LocalDate fechaOrig = movimiento.getFechaEmision();
        if (fechaOrig != null) {
            periodoContableService.validarPeriodoAbierto(fechaOrig);
        }

        movimiento.setEstado(EstadoMovimiento.ANULADO);
        movimientoRepository.save(movimiento);

        reversarKardex(movimientoId);
    }

    @Override
    public Page<MovimientoInventarioResponseDto> listarMovimientos(
            Pageable pageable,
            String tipo,
            LocalDate fechaEmisionDesde,
            LocalDate fechaEmisionHasta) {

        Page<MovimientoInventario> movimientos = movimientoRepository.findByFiltros(
                tipo, fechaEmisionDesde, fechaEmisionHasta, pageable);

        return movimientos.map(mov -> {
            List<MovimientoInventarioDetalle> detalles =
                    detalleRepository.findByMovimiento_MovimientoId(mov.getMovimientoId());
            return mapper.toResponse(mov, detalles);
        });
    }

    @Override
    public MovimientoInventarioResponseDto obtenerMovimientoConDetalles(Long movimientoId) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        List<MovimientoInventarioDetalle> detalles =
                detalleRepository.findByMovimiento_MovimientoId(movimiento.getMovimientoId());
        return mapper.toResponse(movimiento, detalles);
    }

    /**
     * Reversa el kardex de un movimiento anulado.
     *
     * Estrategia: crea ENTRADAS COMPENSATORIAS por cada salida del movimiento
     * original (y salidas compensatorias por cada entrada), preservando la
     * trazabilidad histórica. No borra ni pone en 0 los registros previos —
     * eso corromperia la corrida histórica y el costo promedio.
     *
     * Cada registro de reversa va con observación "Reversa por anulación
     * de movimiento #N" y tipo ANULADO para que el filtro de reportes lo
     * pueda excluir si se requiere mostrar solo movimientos vigentes.
     */
    @Override
    @Transactional
    public void reversarKardex(Long movimientoId) {
        List<Kardex> kardexList = kardexRepository.findByMovimiento_MovimientoId(movimientoId);
        for (Kardex original : kardexList) {
            // Saltar si ya fue reversado (idempotencia básica)
            if (original.getEstado() == EstadoMovimiento.ANULADO) continue;

            // Saldo actual previo a la reversa
            double saldoActual = kardexRepository
                    .findTopByProductoVarianteAndBodegaOrderByFechaCreacionDesc(
                            original.getProductoVariante(), original.getBodega())
                    .map(Kardex::getSaldo)
                    .orElse(0.0);
            double costoPromedioActual = kardexRepository
                    .findTopByProductoVarianteAndBodegaOrderByFechaCreacionDesc(
                            original.getProductoVariante(), original.getBodega())
                    .map(Kardex::getCostoPromedio)
                    .orElse(0.0);

            // Compensación: si el original fue ENTRADA, reversa = SALIDA; viceversa.
            double entradaRev = original.getSalida() != null ? original.getSalida() : 0.0;
            double salidaRev  = original.getEntrada() != null ? original.getEntrada() : 0.0;
            double nuevoSaldo = saldoActual + entradaRev - salidaRev;

            // Bloquear reversa que dejaría existencias negativas (la mercancía ya se vendió).
            if (nuevoSaldo < 0) {
                throw new IllegalStateException(
                    "No se puede reversar el movimiento " + movimientoId + " para el producto " +
                    original.getProductoVariante().getReferenciaVariantes() + " en bodega " +
                    original.getBodega().getNombre() +
                    ": saldo actual " + saldoActual + ", salida a reversar " + salidaRev +
                    " dejaría el inventario en " + nuevoSaldo + ". Esa mercancía ya se vendió/usó. " +
                    "Ajuste manualmente con un movimiento de entrada antes de reversar."
                );
            }

            Kardex reversa = new Kardex();
            reversa.setMovimiento(original.getMovimiento());
            reversa.setDetalle(original.getDetalle());
            reversa.setProductoVariante(original.getProductoVariante());
            reversa.setBodega(original.getBodega());
            reversa.setFechaEmision(original.getFechaEmision());
            reversa.setFechaCreacion(LocalDateTime.now());
            reversa.setEntrada(entradaRev);
            reversa.setSalida(salidaRev);
            reversa.setSaldo(nuevoSaldo);
            reversa.setCostoUnitario(original.getCostoUnitario());
            reversa.setCostoPromedio(costoPromedioActual); // mantiene el promedio histórico
            reversa.setTotalCosto((entradaRev + salidaRev) * (original.getCostoUnitario() != null ? original.getCostoUnitario() : 0.0));
            reversa.setTipo(original.getTipo());
            reversa.setEstado(EstadoMovimiento.ANULADO);
            reversa.setObservaciones("Reversa por anulación de movimiento #" + movimientoId);
            kardexRepository.save(reversa);

            // NO se sobrescribe el estado del original — eso destruye trazabilidad
            // del registro histórico (auditoría debe ver que estuvo ACTIVO antes).
            // La reversa queda con estado ANULADO + observación que la liga al original.
        }
    }

    @Override
    public List<KardexReportDto> getKardexReport(String desde, String hasta) {
        List<Object[]> results = kardexRepository.getKardexReportRaw(desde, hasta);
        List<KardexReportDto> dtos = new java.util.ArrayList<>();
        
        for (Object[] row : results) {
            KardexReportDto dto = new KardexReportDto();
            if (row[0] instanceof java.sql.Timestamp) {
                dto.setFechaCreacion(((java.sql.Timestamp) row[0]).toLocalDateTime());
            } else if (row[0] instanceof java.time.LocalDateTime) {
                dto.setFechaCreacion((java.time.LocalDateTime) row[0]);
            }
            dto.setNumeroFactura((String) row[1]);
            dto.setMovimiento((String) row[2]);
            dto.setTipoMovimiento((String) row[3]);
            dto.setTipo((String) row[4]);
            dto.setProducto((String) row[5]);
            dto.setEntrada(row[6] != null ? getDoubleValue(row[6]) : null);
            dto.setSalida(row[7] != null ? getDoubleValue(row[7]) : null);
            dto.setCostoPromedio(row[8] != null ? getDoubleValue(row[8]) : null);
            dto.setTotalCosto(row[9] != null ? getDoubleValue(row[9]) : null);
            dto.setSaldo(row[10] != null ? getDoubleValue(row[10]) : null);
            dtos.add(dto);
        }
        
        return dtos;
    }

    private Double getDoubleValue(Object value) {
        if (value instanceof java.math.BigDecimal) {
            return ((java.math.BigDecimal) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }
}
