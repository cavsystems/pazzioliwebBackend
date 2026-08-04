package com.pazzioliweb.movimientosinventariomodule.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.commonbacken.util.Jwcommon;
import com.pazzioliweb.productosmodule.entity.Productos;
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
import com.pazzioliweb.comprobantesmodule.entity.CentroCosto;
import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.comprobantesmodule.service.AsientoContableService;
import com.pazzioliweb.comprobantesmodule.service.ConfiguracionContableService;
import org.springframework.context.ApplicationEventPublisher;
import com.pazzioliweb.movimientosinventariomodule.dtos.KardexReportDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.KardexReportePaginadoDto;
import com.pazzioliweb.movimientosinventariomodule.dtos.KardexTotalesDto;
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

    // Caché en memoria del reporte de kardex COMPLETO por tenant + combinación de filtros.
    // El scroll infinito pide una página nueva cada vez que el usuario baja en la tabla;
    // sin esto, cada página re-ejecutaba la consulta nativa completa (joins + ROW_NUMBER)
    // desde cero. Con esto, solo la primera página de una búsqueda paga ese costo; el resto
    // reutiliza el mismo resultado mientras no haya expirado.
    private static final long KARDEX_CACHE_TTL_MS = 90_000;
    private final java.util.concurrent.ConcurrentHashMap<String, KardexCacheEntry> kardexCache =
            new java.util.concurrent.ConcurrentHashMap<>();

    private static class KardexCacheEntry {
        final List<KardexReportDto> datos;
        final long creadoEn = System.currentTimeMillis();
        KardexCacheEntry(List<KardexReportDto> datos) { this.datos = datos; }
        boolean expirado() { return System.currentTimeMillis() - creadoEn > KARDEX_CACHE_TTL_MS; }
    }

    private List<KardexReportDto> getKardexReportCacheado(String desde, String hasta,
            Integer varianteproductoid, String bodega, String movimiento) {
        // La clave incluye el tenant explícitamente: el caché es un campo de instancia
        // compartido por todas las requests de este bean, y en un backend multi-tenant
        // NUNCA se puede servir el resultado de un tenant a otro.
        String tenant = com.pazzioliweb.commonbacken.conexiondb.TenantContext.getCurrentTenant();
        String clave = tenant + "|" + desde + "|" + hasta + "|" + varianteproductoid + "|" + bodega + "|" + movimiento;

        kardexCache.entrySet().removeIf(e -> e.getValue().expirado());

        KardexCacheEntry entry = kardexCache.get(clave);
        if (entry != null) {
            return entry.datos;
        }
        List<KardexReportDto> datos = getKardexReport(desde, hasta, varianteproductoid, bodega, movimiento);
        kardexCache.put(clave, new KardexCacheEntry(datos));
        return datos;
    }
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
    @Autowired
    private MovimientoInventarioWebSocketService wsProgreso;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    @Transactional
    public MovimientoInventarioResponseDto crearMovimiento(
            MovimientoInventarioCreateDto createDto,
            ComprobanteContable comprobante,
            Usuario usuario,
            HttpServletRequest request) {

        // ── Extraer login del usuario desde cookie/JWT para progreso WebSocket ──
        String loginUsuario = null;
        try {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("token".equals(cookie.getName())) {
                        Claims claims = jwcommon.extraerClaims(cookie.getValue());
                        DatosSesiones datos = redisTemplate.opsForValue().get(claims.get("idsecion", String.class));
                        if (datos != null) loginUsuario = datos.getLogin();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // No crítico: el progreso WS es informativo, no bloquea la operación
        }

        // Paso 1 — validar periodo contable
        wsProgreso.enviarProgreso(loginUsuario, 1, "Validando periodo contable...", 10, null);
        java.time.LocalDate fechaMov = createDto.getFechaEmision() != null
                ? createDto.getFechaEmision() : LocalDate.now();
        periodoContableService.validarPeriodoAbierto(fechaMov);

        // Paso 2 — resolver comprobante, usuario y consecutivo
        wsProgreso.enviarProgreso(loginUsuario, 2, "Resolviendo comprobante y consecutivo...", 25, null);
        if (comprobante == null && createDto.getComprobanteId() != null) {
            comprobante = comprobantesRepository.findById(createDto.getComprobanteId().longValue())
                    .orElseThrow(() -> new EntityNotFoundException("Comprobante no encontrado: " + createDto.getComprobanteId()));
        }
        if (usuario == null && createDto.getUsuarioId() != null) {
            usuario = usuarioRepository.findByCodigo(createDto.getUsuarioId().intValue())
                    .orElse(null);
        }
        if (createDto.getConsecutivo() == null && comprobante != null) {
            int nextConsecutivo = movimientoRepository
                    .findTopByComprobanteOrderByConsecutivoDesc(comprobante)
                    .map(m -> m.getConsecutivo() + 1)
                    .orElse(1);
            comprobante.setSiguienteConsecutivo(comprobante.getSiguienteConsecutivo() + 1);
            createDto.setConsecutivo(nextConsecutivo);
        }

        // Crear cabecera del movimiento
        MovimientoInventario movimiento = mapper.toEntity(createDto, comprobante, usuario);
        movimiento.setFechaCreacion(LocalDateTime.now());
        if (movimiento.getEstado() == null) {
            movimiento.setEstado(EstadoMovimiento.ACTIVO);
        }
        // Resolver usuario desde sesión activa (cookie)
        try {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("token".equals(cookie.getName())) {
                        Claims claims = jwcommon.extraerClaims(cookie.getValue());
                        DatosSesiones datos = redisTemplate.opsForValue().get(claims.get("idsecion", String.class));
                        if (datos != null) {
                            usuario = entityManager.getReference(Usuario.class, datos.getIdusuario());
                            movimiento.setUsuario(usuario);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (createDto.getCentroCostoId() != null && createDto.getCentroCostoId() > 0) {
            movimiento.setCentroCosto(entityManager.getReference(CentroCosto.class, createDto.getCentroCostoId()));
        }

        movimientoRepository.save(movimiento);

        TipoMovimiento tipo = movimiento.getTipo();

        // Paso 3 — precargar datos en bloque
        wsProgreso.enviarProgreso(loginUsuario, 3, "Precargando productos, bodegas y kardex...", 40, null);

        // ── PRECARGA EN BLOQUE — evita N+1 queries en el loop ──

        // 1. Variantes en una sola query
        List<Long> varianteIds = createDto.getDetalles().stream()
                .map(MovimientoInventarioDetalleCreateDto::getProductoVarianteId)
                .distinct().toList();
        java.util.Map<Long, ProductoVariante> variantesMap = productoVarianteRepository.findAllById(varianteIds)
                .stream().collect(java.util.stream.Collectors.toMap(ProductoVariante::getProductoVarianteId, v -> v));

        // 2. Bodegas en una sola query
        java.util.Set<Integer> bodegaIdsSet = new java.util.HashSet<>();
        for (MovimientoInventarioDetalleCreateDto dto : createDto.getDetalles()) {
            if (dto.getBodegaOrigenId() != null) bodegaIdsSet.add(dto.getBodegaOrigenId());
            if (dto.getBodegaDestinoId() != null) bodegaIdsSet.add(dto.getBodegaDestinoId());
        }
        java.util.Map<Integer, Bodegas> bodegasMap = new java.util.HashMap<>();
        if (!bodegaIdsSet.isEmpty()) {
            bodegasMap = bodegasRepository.findAllById(bodegaIdsSet).stream()
                    .collect(java.util.stream.Collectors.toMap(Bodegas::getCodigo, b -> b));
        }

        // 3. Último kardex y existencias por (variante, bodega) — agrupados por bodega para reeusar query batch
        //    Key: "varianteId_bodegaId"
        java.util.Map<String, Kardex> ultimoKardexMap = new java.util.HashMap<>();
        java.util.Map<String, Double> existenciasMap = new java.util.HashMap<>();

        java.util.Map<Integer, java.util.Set<Long>> variantesPorBodega = new java.util.HashMap<>();
        for (MovimientoInventarioDetalleCreateDto dto : createDto.getDetalles()) {
            Long vId = dto.getProductoVarianteId();
            if (dto.getBodegaOrigenId() != null)
                variantesPorBodega.computeIfAbsent(dto.getBodegaOrigenId(), k -> new java.util.HashSet<>()).add(vId);
            if (dto.getBodegaDestinoId() != null)
                variantesPorBodega.computeIfAbsent(dto.getBodegaDestinoId(), k -> new java.util.HashSet<>()).add(vId);
        }
        for (java.util.Map.Entry<Integer, java.util.Set<Long>> entry : variantesPorBodega.entrySet()) {
            Integer bId = entry.getKey();
            java.util.Set<Long> vIds = entry.getValue();
            for (Kardex k : kardexRepository.findUltimosPorVarianteYBodega(bId, vIds)) {
                ultimoKardexMap.put(k.getProductoVariante().getProductoVarianteId() + "_" + bId, k);
            }
            for (Existencias e : existenciasRepository.findByBodega_CodigoAndProductoVariante_ProductoVarianteIdIn(bId, new java.util.ArrayList<>(vIds))) {
                existenciasMap.put(e.getProductoVariante().getProductoVarianteId() + "_" + bId,
                        e.getExistencia() != null ? e.getExistencia().doubleValue() : 0.0);
            }
        }

        // Paso 4 — calcular kardex y costos en memoria
        wsProgreso.enviarProgreso(loginUsuario, 4, "Calculando movimientos y costos promedio...", 60, null);

        // ── LOOP: construir detalles y kardex EN MEMORIA sin tocar la BD ──
        // Estado corrido por (varianteId_bodegaId): [saldo, costoPromedio, totalCosto]
        java.util.Map<String, double[]> estadoKardex = new java.util.HashMap<>();
        // Saldo final por (varianteId_bodegaId) para el UPSERT de existencias al final
        java.util.Map<String, long[]> saldosFinales = new java.util.LinkedHashMap<>();

        List<MovimientoInventarioDetalle> detallesAGuardar = new ArrayList<>();
        List<Kardex> kardexAGuardar = new ArrayList<>();

        for (MovimientoInventarioDetalleCreateDto detalleDto : createDto.getDetalles()) {

            ProductoVariante variante = variantesMap.get(detalleDto.getProductoVarianteId());
            if (variante == null) {
                throw new EntityNotFoundException("ProductoVariante no encontrado: " + detalleDto.getProductoVarianteId());
            }

            Bodegas bodegaOrigen = null;
            Bodegas bodegaDestino = null;
            if (detalleDto.getBodegaOrigenId() != null) {
                bodegaOrigen = bodegasMap.get(detalleDto.getBodegaOrigenId());
                if (bodegaOrigen == null)
                    throw new EntityNotFoundException("Bodega origen no encontrada: " + detalleDto.getBodegaOrigenId());
            }
            if (detalleDto.getBodegaDestinoId() != null) {
                bodegaDestino = bodegasMap.get(detalleDto.getBodegaDestinoId());
                if (bodegaDestino == null)
                    throw new EntityNotFoundException("Bodega destino no encontrada: " + detalleDto.getBodegaDestinoId());
            }

            double costoUnitario = detalleDto.getCostoUnitario() != null ? detalleDto.getCostoUnitario() : 0.0;
            double cantidad = detalleDto.getCantidad();
            double totalDetalle = detalleDto.getTotalDetalle() != null
                    ? detalleDto.getTotalDetalle() : costoUnitario * cantidad;

            MovimientoInventarioDetalle detalle = mapper.toDetalleEntity(
                    detalleDto, movimiento, variante, bodegaOrigen, bodegaDestino);
            detalle.setCostoUnitario(costoUnitario);
            detalle.setTotalDetalle(totalDetalle);
            detallesAGuardar.add(detalle);

            // Construir kardex en memoria para cada bodega involucrada
            if (tipo == TipoMovimiento.SALIDA || tipo == TipoMovimiento.TRASLADO) {
                if (bodegaOrigen != null) {
                    double[] k = construirKardexEnMemoria(
                            variante, bodegaOrigen, 0.0, cantidad, costoUnitario,
                            tipo, movimiento, detalle, ultimoKardexMap, existenciasMap, estadoKardex, kardexAGuardar);
                    saldosFinales.put(variante.getProductoVarianteId() + "_" + bodegaOrigen.getCodigo(),
                            new long[]{variante.getProductoVarianteId(), bodegaOrigen.getCodigo(), Double.doubleToRawLongBits(k[0])});
                    // Para TRASLADO, el costo de entrada al destino es el promedio que quedó en origen
                    costoUnitario = k[1];
                }
            }
            if (tipo == TipoMovimiento.ENTRADA || tipo == TipoMovimiento.TRASLADO) {
                if (bodegaDestino != null) {
                    double[] k = construirKardexEnMemoria(
                            variante, bodegaDestino, cantidad, 0.0, costoUnitario,
                            tipo, movimiento, detalle, ultimoKardexMap, existenciasMap, estadoKardex, kardexAGuardar);
                    saldosFinales.put(variante.getProductoVarianteId() + "_" + bodegaDestino.getCodigo(),
                            new long[]{variante.getProductoVarianteId(), bodegaDestino.getCodigo(), Double.doubleToRawLongBits(k[0])});
                }
            }

            // El costoPromedio del detalle queda con el promedio calculado (ya seteado en construirKardexEnMemoria)
        }

        // Paso 5 — persistir detalles y kardex
        wsProgreso.enviarProgreso(loginUsuario, 5, "Guardando " + detallesAGuardar.size() + " item(s) en inventario...", 78, null);

        // ── PERSISTENCIA EN BLOQUE ──
        // 1. Detalles primero: saveAll asigna IDs que el kardex referencia (mismo objeto en memoria)
        detalleRepository.saveAll(detallesAGuardar);
        // 2. Kardex en bloque
        kardexRepository.saveAll(kardexAGuardar);

        // Calcular total y construir el DTO de respuesta ANTES de flush/clear:
        // después del clear() los proxies lazy (variante.getProducto()) quedan sin sesión
        // y mapper.toResponse fallaría con LazyInitializationException.
        double total = detallesAGuardar.stream().mapToDouble(MovimientoInventarioDetalle::getTotalDetalle).sum();
        movimiento.setTotal(total);
        MovimientoInventarioResponseDto responseDto = mapper.toResponse(movimiento, detallesAGuardar);

        // 3. Flush + clear DESPUÉS de construir el response: evita dirty-check O(n²) en el UPSERT nativo
        entityManager.flush();
        entityManager.clear();

        // Paso 6 — actualizar existencias
        wsProgreso.enviarProgreso(loginUsuario, 6, "Actualizando existencias en bodega...", 92, null);

        // 4. UPSERT batch de existencias (lotes de 500)
        final int LOTE_UPSERT = 500;
        List<long[]> saldosList = new java.util.ArrayList<>(saldosFinales.values());
        for (int i = 0; i < saldosList.size(); i += LOTE_UPSERT) {
            List<long[]> lote = saldosList.subList(i, Math.min(i + LOTE_UPSERT, saldosList.size()));
            StringBuilder sql = new StringBuilder(
                    "INSERT INTO existencias (producto_variantes_id, bodega_id, existencia) VALUES ");
            for (int j = 0; j < lote.size(); j++) {
                if (j > 0) sql.append(',');
                sql.append("(?,?,?)");
            }
            sql.append(" ON DUPLICATE KEY UPDATE existencia = VALUES(existencia)");
            jakarta.persistence.Query q = entityManager.createNativeQuery(sql.toString());
            int p = 1;
            for (long[] row : lote) {
                q.setParameter(p++, row[0]);
                q.setParameter(p++, (int) row[1]);
                q.setParameter(p++, java.math.BigDecimal.valueOf(Double.longBitsToDouble(row[2])));
            }
            q.executeUpdate();
        }

        // Persistir el total en la cabecera del movimiento (merge sobre entidad detached)
        movimientoRepository.save(movimiento);

        // Asiento contable (usa campos primitivos del movimiento, no proxies lazy)
        generarAsientoMovimientoInventario(movimiento, total);

        // Broadcast WebSocket al consecutivo del comprobante (AFTER_COMMIT via evento)
        if (movimiento.getComprobante() != null) {
            eventPublisher.publishEvent(new MovimientoRegistradoEvent(
                this,
                movimiento.getComprobante().getId(),
                movimiento.getMovimientoId(),
                movimiento.getTipo() != null ? movimiento.getTipo().name() : null
            ));
        }

        // Paso 7 — completado
        wsProgreso.enviarCompletado(loginUsuario, movimiento.getMovimientoId());

        return responseDto;
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

    /**
     * Calcula el nuevo estado de kardex EN MEMORIA para una (variante, bodega),
     * crea el objeto Kardex (sin persistir) y lo agrega a la lista batch.
     * Devuelve [nuevoSaldo, nuevoCostoPromedio] para encadenar con el siguiente ítem.
     */
    private double[] construirKardexEnMemoria(
            ProductoVariante variante, Bodegas bodega,
            double entrada, double salida, double costoUnitario,
            TipoMovimiento tipo,
            MovimientoInventario movimiento, MovimientoInventarioDetalle detalle,
            java.util.Map<String, Kardex> ultimoKardexMap,
            java.util.Map<String, Double> existenciasMap,
            java.util.Map<String, double[]> estadoKardex,
            List<Kardex> kardexAGuardar) {

        String key = variante.getProductoVarianteId() + "_" + bodega.getCodigo();
        double[] estado = estadoKardex.get(key);
        if (estado == null) {
            Kardex ultimo = ultimoKardexMap.get(key);
            if (ultimo != null) {
                estado = new double[]{
                    ultimo.getSaldo() != null ? ultimo.getSaldo() : 0.0,
                    ultimo.getCostoPromedio() != null ? ultimo.getCostoPromedio() : 0.0,
                    ultimo.getTotalCosto() != null ? ultimo.getTotalCosto() : 0.0};
            } else {
                estado = new double[]{existenciasMap.getOrDefault(key, 0.0), 0.0, 0.0};
            }
        }
        double saldoAnterior = estado[0];
        double promedioAnterior = estado[1];
        double totalCostoAnterior = estado[2];

        double nuevoSaldo = saldoAnterior + entrada - salida;
        if (salida > 0 && nuevoSaldo < 0) {
            throw new IllegalStateException(
                "Existencias insuficientes para " + variante.getReferenciaVariantes() +
                " en bodega " + bodega.getNombre() +
                ". Saldo: " + saldoAnterior + ", salida: " + salida);
        }

        double costoUnitarioFinal = costoUnitario;
        if (salida > 0 && promedioAnterior > 0) costoUnitarioFinal = promedioAnterior;

        double nuevoCostoPromedio;
        if (entrada > 0) {
            double totalUnidades = saldoAnterior + entrada;
            nuevoCostoPromedio = totalUnidades > 0
                    ? (saldoAnterior * promedioAnterior + entrada * costoUnitario) / totalUnidades
                    : costoUnitario;
        } else {
            nuevoCostoPromedio = promedioAnterior;
        }
        nuevoCostoPromedio = Math.round(nuevoCostoPromedio * 100.0) / 100.0;
        if (nuevoCostoPromedio <= 0) nuevoCostoPromedio = costoUnitario;

        double nuevoTotalCosto = entrada > 0
                ? totalCostoAnterior + (entrada * costoUnitarioFinal)
                : totalCostoAnterior - (salida * costoUnitarioFinal);

        detalle.setCostoPromedio(nuevoCostoPromedio);

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
        kardex.setCostoUnitario(costoUnitarioFinal);
        kardex.setCostoPromedio(nuevoCostoPromedio);
        kardex.setTotalCosto(nuevoTotalCosto);
        kardex.setTipo(tipo);
        kardex.setEstado(movimiento.getEstado());
        kardex.setObservaciones(movimiento.getObservaciones());
        kardexAGuardar.add(kardex);

        estadoKardex.put(key, new double[]{nuevoSaldo, nuevoCostoPromedio, nuevoTotalCosto});
        return new double[]{nuevoSaldo, nuevoCostoPromedio};
    }

    private void crearKardexEntry(MovimientoInventario movimiento,
                                   MovimientoInventarioDetalle detalle,
                                   ProductoVariante variante,
                                   Bodegas bodega,
                                   double entrada,
                                   double salida,
                                   double costoUnitario,
                                   TipoMovimiento tipo) {

        Productos product=variante.getProducto();
        // ── Saldo y costo promedio previos ──
        System.out.println(" ── Saldo y costo promedio previos ──"+bodega.getNombre()+variante.getCodigoBarras());
        Kardex ultimo = kardexRepository
                .findTopByProductoVarianteAndBodegaOrderByFechaCreacionDesc(variante, bodega)
                .orElse(null);
        double saldoAnterior;
        double costounitarioanterior;
      double saldototal;
        if (ultimo != null) {
            saldoAnterior = ultimo.getSaldo();
            costounitarioanterior = ultimo.getCostoPromedio() != null ? ultimo.getCostoPromedio() : 0.0;
            saldototal = ultimo.getTotalCosto();
        } else {
            // Si no hay kardex previo, usar el stock actual de la tabla existencias
            Long varianteId = variante.getProductoVarianteId();
            java.util.Optional<Existencias> existenciasOpt = existenciasRepository
                    .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(
                            varianteId, bodega.getCodigo());
          //  saldoAnterior = existenciasOpt.map(e -> e.getExistencia() != null ? e.getExistencia().doubleValue() : 0.0).orElse(0.0);
            saldototal=0.0;
            saldoAnterior=0.0;
            costounitarioanterior = 0.0;
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

        // Costo promedio ponderado (NIIF Sec.13 / NIC 2):
        //   nuevoCosto = (saldoAnterior * promedioAnterior + entrada * costoUnitario) / (saldoAnterior + entrada)
        // En SALIDAS, el promedio se mantiene igual al anterior.
        double nuevoCostoPromedio;

        // Para SALIDAS, valorar al costo promedio vigente, no al precio de entrada
        double costoUnitarioFinal = costoUnitario;
        if (salida > 0 && costounitarioanterior > 0) {
            costoUnitarioFinal = costounitarioanterior;
        }

        if (entrada > 0) {
            double costoTotalAnterior = saldoAnterior * costounitarioanterior;
            double costoTotalActual = entrada * costoUnitario;
            double totalUnidades = saldoAnterior + entrada;
            nuevoCostoPromedio = totalUnidades > 0
                    ? (costoTotalAnterior + costoTotalActual) / totalUnidades
                    : costoUnitario;
            nuevoCostoPromedio = Math.round(nuevoCostoPromedio * 100.0) / 100.0;
        } else {
            nuevoCostoPromedio = Math.round(costounitarioanterior * 100.0) / 100.0;
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
        kardex.setCostoUnitario(costoUnitarioFinal);
        kardex.setCostoPromedio(nuevoCostoPromedio);
        // Total del movimiento valorado al costo promedio cuando es SALIDA,
        // al nuevo costo promedio cuando es ENTRADA (valor total del inventario).

        kardex.setTotalCosto(salida > 0
                ?  saldototal - (salida * costoUnitarioFinal)
                :  saldototal + (entrada * costoUnitarioFinal)  );
        kardex.setTipo(tipo);
        kardex.setEstado(movimiento.getEstado());
        kardex.setObservaciones(movimiento.getObservaciones());
        kardexRepository.save(kardex);

        // Actualizar tabla existencias para mantener sincronización con Kardex
        java.util.Optional<Existencias> existenciasOpt = existenciasRepository
                .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(
                        variante.getProductoVarianteId(), bodega.getCodigo());
        if (existenciasOpt.isPresent()) {
            Existencias existencias = existenciasOpt.get();
            existencias.setExistencia(java.math.BigDecimal.valueOf(nuevoSaldo));
            existenciasRepository.save(existencias);
        } else {
            // Si no existe registro de existencias, crearlo
            Existencias nuevasExistencias = new Existencias();
            nuevasExistencias.setProductoVariante(variante);
            nuevasExistencias.setBodega(bodega);
            nuevasExistencias.setExistencia(java.math.BigDecimal.valueOf(nuevoSaldo));
            existenciasRepository.save(nuevasExistencias);
        }
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

        // La paginación va sobre la consulta simple (sin fetch-join de colección,
        // por eso es segura con Pageable). Los detalles de TODA la página se traen
        // en una segunda consulta EN BLOQUE (WHERE movimiento_id IN (...)) — patrón
        // estándar para evitar el "cannot paginate on fetch join of a collection"
        // que daría fetch-joinear detalles junto con la página.
        Page<MovimientoInventario> movimientos = movimientoRepository.findByFiltros(
                tipo, fechaEmisionDesde, fechaEmisionHasta, pageable);

        List<Long> movimientoIds = movimientos.getContent().stream()
                .map(MovimientoInventario::getMovimientoId)
                .toList();

        // ── Antes: 1 consulta de detalles POR movimiento de la página (N+1) ──
        // Con 20 movimientos por página eran 1 (movimientos) + 20 (detalles) = 21
        // consultas, y por cada detalle además 2 SELECT extra (bodegaOrigen/
        // bodegaDestino EAGER sin fetch-join). Ahora: 1 consulta de detalles para
        // TODA la página, agrupada en memoria por movimientoId.
        java.util.Map<Long, List<MovimientoInventarioDetalle>> detallesPorMovimiento = movimientoIds.isEmpty()
                ? java.util.Collections.emptyMap()
                : detalleRepository.findByMovimiento_MovimientoIdInWithProducto(movimientoIds).stream()
                        .collect(java.util.stream.Collectors.groupingBy(d -> d.getMovimiento().getMovimientoId()));

        return movimientos.map(mov -> {
            List<MovimientoInventarioDetalle> detalles =
                    detallesPorMovimiento.getOrDefault(mov.getMovimientoId(), java.util.Collections.emptyList());
            return mapper.toResponse(mov, detalles);
        });
    }

    @Override
    public MovimientoInventarioResponseDto obtenerMovimientoConDetalles(Long movimientoId) {
        MovimientoInventario movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        List<MovimientoInventarioDetalle> detalles =
                detalleRepository.findByMovimiento_MovimientoIdWithProducto(movimiento.getMovimientoId());
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

            // Actualizar tabla existencias para mantener sincronización con Kardex
            java.util.Optional<Existencias> existenciasOpt = existenciasRepository
                    .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(
                            original.getProductoVariante().getProductoVarianteId(), original.getBodega().getCodigo());
            if (existenciasOpt.isPresent()) {
                Existencias existencias = existenciasOpt.get();
                existencias.setExistencia(java.math.BigDecimal.valueOf(nuevoSaldo));
                existenciasRepository.save(existencias);
            } else {
                // Si no existe registro de existencias, crearlo
                Existencias nuevasExistencias = new Existencias();
                nuevasExistencias.setProductoVariante(original.getProductoVariante());
                nuevasExistencias.setBodega(original.getBodega());
                nuevasExistencias.setExistencia(java.math.BigDecimal.valueOf(nuevoSaldo));
                existenciasRepository.save(nuevasExistencias);
            }

            // NO se sobrescribe el estado del original — eso destruye trazabilidad
            // del registro histórico (auditoría debe ver que estuvo ACTIVO antes).
            // La reversa queda con estado ANULADO + observación que la liga al original.
        }
    }

    @Override
    public List<KardexReportDto> getKardexReport(String desde, String hasta, Integer varianteproductoid, String bodega, String movimiento) {
        List<Object[]> results = kardexRepository.getKardexReportRaw(desde, hasta, varianteproductoid, bodega, movimiento);
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
            dto.setCostoUnitario(row[8] != null ? getDoubleValue(row[8]) : null);
            dto.setCostoPromedio(row[9] != null ? getDoubleValue(row[9]) : null);
            dto.setTotalCosto(row[10] != null ? getDoubleValue(row[10]) : null);
            dto.setSaldo(row[11] != null ? getDoubleValue(row[11]) : null);
            dto.setNombrebodega(row[12] != null ? (String) row[12] : null);
            dto.setCliente(row[13] != null ? (String) row[13] : null);
            if (row.length > 14 && row[14] != null) {
                if (row[14] instanceof java.sql.Timestamp) {
                    dto.setFechaEmision(((java.sql.Timestamp) row[14]).toLocalDateTime());
                } else if (row[14] instanceof java.sql.Date) {
                    dto.setFechaEmision(((java.sql.Date) row[14]).toLocalDate().atStartOfDay());
                } else if (row[14] instanceof java.time.LocalDate) {
                    dto.setFechaEmision(((java.time.LocalDate) row[14]).atStartOfDay());
                } else if (row[14] instanceof java.time.LocalDateTime) {
                    dto.setFechaEmision((java.time.LocalDateTime) row[14]);
                }
            }
            if (row.length > 15 && row[15] != null) {
                dto.setKardexId(((Number) row[15]).longValue());
            }
            if (row.length > 16 && row[16] != null) {
                dto.setPrecioVenta(getDoubleValue(row[16]));
            }
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public KardexReportePaginadoDto getKardexReportPaginado(String desde, String hasta, Integer varianteproductoid,
            String bodega, String movimiento, int page, int size) {
        // La consulta ya trae TODO el período filtrado (misma consulta de siempre) porque
        // el saldo/costo vigentes se calculan sobre el conjunto completo, no solo la página
        // visible. Lo que cambia es que ahora el total se calcula aquí (antes lo hacía el
        // front con los datos completos) y solo se le manda al front la página pedida —
        // así el payload de cada request queda acotado en vez de mandar todo de una vez.
        // Se usa la versión CACHEADA: así "cargar más" (páginas siguientes del mismo scroll)
        // no vuelve a pagar el costo de la consulta completa cada vez.
        List<KardexReportDto> completo = getKardexReportCacheado(desde, hasta, varianteproductoid, bodega, movimiento);

        KardexTotalesDto totales = new KardexTotalesDto();
        double totalEntradas = 0, totalSalidas = 0;
        int movimientosEntrada = 0, movimientosSalida = 0;
        // "Última fila por bodega" (el reporte viene ordenado por fecha_emision ascendente),
        // para sumar el saldo/valor real del producto entre todas sus bodegas.
        java.util.LinkedHashMap<String, KardexReportDto> ultimaPorBodega = new java.util.LinkedHashMap<>();

        for (KardexReportDto fila : completo) {
            double entrada = fila.getEntrada() != null ? fila.getEntrada() : 0;
            double salida = fila.getSalida() != null ? fila.getSalida() : 0;
            totalEntradas += entrada;
            totalSalidas += salida;
            if (entrada > 0) movimientosEntrada++;
            if (salida > 0) movimientosSalida++;
            ultimaPorBodega.put(fila.getNombrebodega(), fila);
        }

        double saldoActual = 0, valorInventario = 0;
        for (KardexReportDto ultima : ultimaPorBodega.values()) {
            double saldo = ultima.getSaldo() != null ? ultima.getSaldo() : 0;
            double costoProm = ultima.getCostoPromedio() != null ? ultima.getCostoPromedio() : 0;
            saldoActual += saldo;
            valorInventario += saldo * costoProm;
        }
        // Costo promedio "vigente": el de la fila más reciente del período (solo referencial).
        double costoPromedioVigente = completo.isEmpty() ? 0
                : (completo.get(completo.size() - 1).getCostoPromedio() != null
                        ? completo.get(completo.size() - 1).getCostoPromedio() : 0);

        totales.setSaldoActual(saldoActual);
        totales.setTotalEntradas(totalEntradas);
        totales.setTotalSalidas(totalSalidas);
        totales.setCostoPromedioVigente(costoPromedioVigente);
        totales.setValorInventario(valorInventario);
        totales.setMovimientosEntrada(movimientosEntrada);
        totales.setMovimientosSalida(movimientosSalida);

        int totalElements = completo.size();
        int desdeIdx = Math.max(0, page) * Math.max(1, size);
        int hastaIdx = Math.min(totalElements, desdeIdx + Math.max(1, size));
        List<KardexReportDto> contenidoPagina = desdeIdx >= totalElements
                ? java.util.Collections.emptyList()
                : completo.subList(desdeIdx, hastaIdx);
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;

        KardexReportePaginadoDto resultado = new KardexReportePaginadoDto();
        resultado.setContent(contenidoPagina);
        resultado.setTotales(totales);
        resultado.setPage(page);
        resultado.setSize(size);
        resultado.setTotalElements(totalElements);
        resultado.setTotalPages(totalPages);
        return resultado;
    }

    private Double getDoubleValue(Object value) {
        if (value instanceof java.math.BigDecimal) {
            return ((java.math.BigDecimal) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? 1.0 : 0.0;
        }
        return null;
    }

    @Override
    public boolean bodegaTieneRegistrosKardex(Integer bodegaId) {
        Bodegas bodega = bodegasRepository.findById(bodegaId)
                .orElseThrow(() -> new EntityNotFoundException("Bodega no encontrada: " + bodegaId));
        List<Kardex> kardexRecords = kardexRepository.findByBodega(bodega);
        return !kardexRecords.isEmpty();
    }
}
