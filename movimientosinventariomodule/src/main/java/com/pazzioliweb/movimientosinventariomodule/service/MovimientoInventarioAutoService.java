package com.pazzioliweb.movimientosinventariomodule.service;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.movimientosinventariomodule.entity.Kardex;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventario;
import com.pazzioliweb.movimientosinventariomodule.entity.MovimientoInventarioDetalle;
import com.pazzioliweb.movimientosinventariomodule.enums.EstadoMovimiento;
import com.pazzioliweb.movimientosinventariomodule.enums.TipoMovimiento;
import com.pazzioliweb.movimientosinventariomodule.repository.KardexRepository;
import com.pazzioliweb.movimientosinventariomodule.repository.MovimientoInventarioDetalleRepository;
import com.pazzioliweb.movimientosinventariomodule.repository.MovimientoInventarioRepository;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.Existencias;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.productosmodule.repositori.ExistenciasRepository;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio compartido que crea automáticamente movimientos de inventario
 * y Kardex desde ventas, compras y devoluciones. Sigue el mismo patrón que
 * {@code AsientoContableService} en comprobantesmodule.
 *
 * Tres puntos de entrada principales:
 *  - {@link #registrarSalidaPorVenta(...)}     ← VentaServiceImpl
 *  - {@link #registrarEntradaPorCompra(...)}   ← IngresoOrdenCompraServiceImpl
 *  - {@link #registrarEntradaPorDevolucion(...)} ← DevolucionServiceImpl
 *
 * Características:
 *  - Idempotente: si ya existe un movimiento para el documento, NO crea otro.
 *  - Tolerante a errores: el caller envuelve la llamada en try/catch para que
 *    fallas en el inventario no rompan la operación principal (venta/compra).
 *  - Mantiene saldo corrido en Kardex con costo promedio recalculado.
 */
@Service
public class MovimientoInventarioAutoService {

    private static final Logger log = LoggerFactory.getLogger(MovimientoInventarioAutoService.class);

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    private final MovimientoInventarioRepository movimientoRepo;
    private final MovimientoInventarioDetalleRepository detalleRepo;
    private final KardexRepository kardexRepo;
    private final ProductoVarianteRepository varianteRepo;
    private final BodegasRepository bodegaRepo;
    private final ComprobanteContableRepository comprobanteRepo;
    private final ExistenciasRepository existenciasRepo;

    public MovimientoInventarioAutoService(MovimientoInventarioRepository movimientoRepo,
                                            MovimientoInventarioDetalleRepository detalleRepo,
                                            KardexRepository kardexRepo,
                                            ProductoVarianteRepository varianteRepo,
                                            BodegasRepository bodegaRepo,
                                            ComprobanteContableRepository comprobanteRepo,
                                            ExistenciasRepository existenciasRepo) {
        this.movimientoRepo = movimientoRepo;
        this.detalleRepo = detalleRepo;
        this.kardexRepo = kardexRepo;
        this.varianteRepo = varianteRepo;
        this.bodegaRepo = bodegaRepo;
        this.comprobanteRepo = comprobanteRepo;
        this.existenciasRepo = existenciasRepo;
    }

    /** Item simple usado por los callers para describir una línea del movimiento. */
    public static class ItemMovimiento {
        public String codigoProducto;      // codigo_contable del producto
        public String referenciaVariantes; // SKU o referencia de la variante
        public double cantidad;
        public double costoUnitario;       // SIN IVA — base del costo de inventario / kardex
        public double totalLineaConIva;    // OPCIONAL — total de la línea con IVA. 0 ⇒ se calcula automático sin IVA

        public ItemMovimiento(String codigoProducto, String referenciaVariantes,
                              double cantidad, double costoUnitario) {
            this(codigoProducto, referenciaVariantes, cantidad, costoUnitario, 0.0);
        }

        public ItemMovimiento(String codigoProducto, String referenciaVariantes,
                              double cantidad, double costoUnitario, double totalLineaConIva) {
            this.codigoProducto = codigoProducto;
            this.referenciaVariantes = referenciaVariantes;
            this.cantidad = cantidad;
            this.costoUnitario = costoUnitario;
            this.totalLineaConIva = totalLineaConIva;
        }
    }

    /**
     * Crea un movimiento de SALIDA generado desde una venta.
     * tipoComprobante: "FC" (venta contado) o "VC" (venta crédito) o mixto.
     * Si no se especifica, asume FC por defecto.
     */
    @Transactional
    public void registrarSalidaPorVenta(String numeroVenta, Long ventaId, Integer bodegaCodigo,
                                         LocalDate fecha, List<ItemMovimiento> items,
                                         String tipoComprobante, Integer comprobanteId, Integer consecutivo) {
        String t = (tipoComprobante == null || tipoComprobante.isBlank()) ? "FC" : tipoComprobante.toUpperCase();
        // Usar "VENTA" como documentoTipo fijo para idempotencia, independientemente de FC/VC
        crearMovimientoAuto(t, "Venta " + numeroVenta, ventaId, "VENTA",
                TipoMovimiento.SALIDA, bodegaCodigo, fecha, items, false, comprobanteId, consecutivo);
    }

    /** Compat: sobrecarga que asume FC (venta contado) si el caller no especifica. */
    @Transactional
    public void registrarSalidaPorVenta(String numeroVenta, Long ventaId, Integer bodegaCodigo,
                                         LocalDate fecha, List<ItemMovimiento> items) {
        registrarSalidaPorVenta(numeroVenta, ventaId, bodegaCodigo, fecha, items, "FC", null, null);
    }

    /**
     * Crea un movimiento de ENTRADA generado desde un ingreso de orden de compra.
     * tipoComprobante: "CC" (contado) o "CR" (crédito) — para que el historial
     * muestre correctamente el origen.
     *
     * REQUIRES_NEW: los callers de compras lo invocan en afterCommit (la compra ya
     * commiteó y liberó sus locks). Con transacción propia, cualquier error aquí no
     * puede marcar rollback-only la transacción del documento — antes un Duplicate
     * entry en existencias revertía en silencio TODO el ingreso de la compra aunque
     * el caller lo atrapara como "no crítico".
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void registrarEntradaPorCompra(String numeroOrden, Long ordenId, Integer bodegaCodigo,
                                           LocalDate fecha, List<ItemMovimiento> items,
                                           String tipoComprobante, Integer comprobanteId, Integer consecutivo) {
        String t = (tipoComprobante == null || tipoComprobante.isBlank()) ? "CC" : tipoComprobante.toUpperCase();
        crearMovimientoAuto(t, "Compra " + numeroOrden, ordenId, t,
                TipoMovimiento.ENTRADA, bodegaCodigo, fecha, items, true, comprobanteId, consecutivo);
    }

    /** Compat: sobrecarga que asume CC (compra contado) si el caller no especifica. */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void registrarEntradaPorCompra(String numeroOrden, Long ordenId, Integer bodegaCodigo,
                                           LocalDate fecha, List<ItemMovimiento> items) {
        registrarEntradaPorCompra(numeroOrden, ordenId, bodegaCodigo, fecha, items, "CC", null, null);
    }

    /**
     * Crea un movimiento de ENTRADA generado desde una devolución de venta
     * (la mercancía vuelve al inventario).
     */
    @Transactional
    public void registrarEntradaPorDevolucion(String numeroDevolucion, Long devolucionId,
                                               Integer bodegaCodigo, LocalDate fecha,
                                               List<ItemMovimiento> items, Integer comprobanteId, Integer consecutivo) {
        crearMovimientoAuto("DV", "Devolución " + numeroDevolucion, devolucionId, "DV",
                TipoMovimiento.ENTRADA, bodegaCodigo, fecha, items, false, comprobanteId, consecutivo);
    }

    /** Compat: sobrecarga sin comprobanteId y consecutivo. */
    @Transactional
    public void registrarEntradaPorDevolucion(String numeroDevolucion, Long devolucionId,
                                               Integer bodegaCodigo, LocalDate fecha,
                                               List<ItemMovimiento> items) {
        registrarEntradaPorDevolucion(numeroDevolucion, devolucionId, bodegaCodigo, fecha, items, null, null);
    }

    /**
     * Crea un movimiento de SALIDA por una devolución de COMPRA (la mercancía sale del inventario
     * de vuelta al proveedor). Valora la salida al costo promedio vigente del kardex.
     */
    @Transactional
    public void registrarSalidaPorDevolucionCompra(String numeroDevolucion, Long devolucionId,
                                                   Integer bodegaCodigo, LocalDate fecha,
                                                   List<ItemMovimiento> items, Integer comprobanteId, Integer consecutivo) {
        crearMovimientoAuto("DC", "Devolución compra " + numeroDevolucion, devolucionId, "DC",
                TipoMovimiento.SALIDA, bodegaCodigo, fecha, items, false, comprobanteId, consecutivo);
    }

    /** Compat: sobrecarga sin comprobanteId y consecutivo. */
    @Transactional
    public void registrarSalidaPorDevolucionCompra(String numeroDevolucion, Long devolucionId,
                                                   Integer bodegaCodigo, LocalDate fecha, List<ItemMovimiento> items) {
        registrarSalidaPorDevolucionCompra(numeroDevolucion, devolucionId, bodegaCodigo, fecha, items, null, null);
    }

    /**
     * Ajuste de inventario por ANULACIÓN de un documento (devuelve el stock/kardex al estado previo
     * con un movimiento real, en vez de tocar `existencias` a mano). `documentoTipo` debe terminar
     * en "-ANUL" y ser único por documento (idempotencia). `entrada=true` reingresa stock (revierte
     * una salida: venta anulada, devolución de compra anulada); `entrada=false` lo saca (revierte
     * una entrada: devolución de venta anulada). El costo pasado se respeta (no se promedia) para
     * que el kardex quede coherente con la reversa del asiento contable.
     */
    /** True si ya existe un movimiento de inventario para (tipo, documento). Se usa antes de reversar
     *  en una anulación: si el movimiento original nunca se creó (falló en su momento), NO se debe
     *  aplicar el ajuste inverso (evita stock fantasma o negativo). */
    @Transactional(readOnly = true)
    public boolean existeMovimiento(String documentoTipo, Long documentoId) {
        return movimientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId(documentoTipo, documentoId).isPresent();
    }

    /**
     * Existencia disponible de una variante en una bodega (según la tabla existencias, sincronizada
     * con el kardex). Resuelve la variante con la misma cadena de fallback que el registro de
     * movimientos (codigo_contable+referencia → SKU). Devuelve 0.0 si no se resuelve la variante o
     * no hay registro de existencias. Se usa para validar stock antes de una salida (p. ej. devolución
     * de compra al proveedor), evitando existencias/kardex negativos.
     */
    @Transactional(readOnly = true)
    public double existenciaDisponible(String codigoProducto, String referenciaVariantes, Integer bodegaCodigo) {
        if (bodegaCodigo == null) return 0.0;
        Optional<ProductoVariante> opt =
                varianteRepo.findByProducto_CodigoContableAndReferenciaVariantes(codigoProducto, referenciaVariantes);
        if (opt.isEmpty()) opt = varianteRepo.findBySku(codigoProducto);
        if (opt.isEmpty() && referenciaVariantes != null) opt = varianteRepo.findBySku(referenciaVariantes);
        if (opt.isEmpty()) return 0.0;
        return existenciasRepo
                .findByProductoVariante_ProductoVarianteIdAndBodega_Codigo(
                        opt.get().getProductoVarianteId(), bodegaCodigo)
                .map(e -> e.getExistencia() != null ? e.getExistencia().doubleValue() : 0.0)
                .orElse(0.0);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void registrarAjusteAnulacion(String documentoTipo, Long documentoId, Integer bodegaCodigo,
                                         LocalDate fecha, List<ItemMovimiento> items, boolean entrada) {
        crearMovimientoAuto(documentoTipo, "Anulación inventario " + documentoTipo, documentoId, documentoTipo,
                entrada ? TipoMovimiento.ENTRADA : TipoMovimiento.SALIDA,
                bodegaCodigo, fecha, items, false, null, null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Núcleo: crea el MovimientoInventario + detalles + Kardex
    // ─────────────────────────────────────────────────────────────────────────

    private void crearMovimientoAuto(String origenTipo, String descripcion,
                                      Long documentoOrigenId, String documentoTipo,
                                      TipoMovimiento tipo, Integer bodegaCodigo,
                                      LocalDate fecha, List<ItemMovimiento> items,
                                      boolean costoUnitarioComoPromedio, Integer comprobanteId, Integer consecutivo) {

        if (items == null || items.isEmpty()) {
            log.warn("[MovInv-Auto] {} sin items, omitido.", descripcion);
            return;
        }

        // Idempotencia: si ya existe movimiento para este documento, no duplicar
        Optional<MovimientoInventario> existente =
                movimientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId(documentoTipo, documentoOrigenId);
        if (existente.isPresent()) {
            log.info("[MovInv-Auto] Ya existe movimiento {} para {} #{}, omitiendo.",
                    existente.get().getMovimientoId(), documentoTipo, documentoOrigenId);
            return;
        }

        Bodegas bodega = bodegaRepo.findById(bodegaCodigo)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada: " + bodegaCodigo));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipo(tipo);
        movimiento.setFechaEmision(fecha != null ? fecha : LocalDate.now());
        movimiento.setFechaCreacion(LocalDateTime.now());
        movimiento.setEstado(EstadoMovimiento.ACTIVO);
        movimiento.setObservaciones(descripcion + " (auto)");
        movimiento.setDocumentoOrigenTipo(documentoTipo);
        movimiento.setDocumentoOrigenId(documentoOrigenId);
        movimiento.setConsecutivo(consecutivo != null ? consecutivo : 0);

        // Asignar comprobante si se proporcionó
        if (comprobanteId != null) {
            comprobanteRepo.findById(comprobanteId.longValue()).ifPresent(movimiento::setComprobante);
        }

        // Persistir cabecera para tener ID
        MovimientoInventario movGuardado = movimientoRepo.save(movimiento);

        double totalMov = 0.0;

        // ══════════════════════════════════════════════════════════════════════
        //  PRECARGA EN BLOQUE — antes se hacían ~6 consultas POR ÍTEM (resolver
        //  variante ×3, último kardex, existencias, saves con flush): con 3.500+
        //  ítems eran ~20.000 operaciones y el movimiento tardaba ~9 minutos.
        //  Ahora: 3 consultas masivas + resolución/saldos en memoria + saveAll.
        // ══════════════════════════════════════════════════════════════════════

        // 1. Variantes por código contable y por SKU (2 consultas)
        java.util.Set<String> codigosProducto = new java.util.HashSet<>();
        java.util.Set<String> skus = new java.util.HashSet<>();
        for (ItemMovimiento it : items) {
            if (it.codigoProducto != null && !it.codigoProducto.isBlank()) {
                codigosProducto.add(it.codigoProducto);
                skus.add(it.codigoProducto);
            }
            if (it.referenciaVariantes != null && !it.referenciaVariantes.isBlank()) {
                skus.add(it.referenciaVariantes);
            }
        }
        java.util.Map<String, ProductoVariante> porCodigoYRef = new java.util.HashMap<>();
        if (!codigosProducto.isEmpty()) {
            for (ProductoVariante v : varianteRepo.findAllConProductoByCodigoContableIn(codigosProducto)) {
                if (v.getReferenciaVariantes() != null) {
                    porCodigoYRef.putIfAbsent(v.getProducto().getCodigoContable() + "||" + v.getReferenciaVariantes(), v);
                }
            }
        }
        java.util.Map<String, ProductoVariante> porSku = new java.util.HashMap<>();
        if (!skus.isEmpty()) {
            for (ProductoVariante v : varianteRepo.findAllBySkuIn(skus)) {
                if (v.getSku() != null) porSku.putIfAbsent(v.getSku(), v);
            }
        }

        // 2. Resolver la variante de cada ítem EN MEMORIA (misma cadena de fallback de siempre)
        java.util.List<ItemMovimiento> itemsValidos = new java.util.ArrayList<>();
        java.util.List<ProductoVariante> variantesDeItems = new java.util.ArrayList<>();
        java.util.Set<Long> varianteIds = new java.util.HashSet<>();
        for (ItemMovimiento it : items) {
            if (it.cantidad <= 0) {
                log.debug("[MovInv-Auto] Saltando item {} cantidad=0", it.codigoProducto);
                continue;
            }
            ProductoVariante v = it.referenciaVariantes != null
                    ? porCodigoYRef.get(it.codigoProducto + "||" + it.referenciaVariantes)
                    : null;
            if (v == null && it.codigoProducto != null) v = porSku.get(it.codigoProducto);       // Fallback 1: SKU
            if (v == null && it.referenciaVariantes != null) v = porSku.get(it.referenciaVariantes); // Fallback 2: SKU por referencia
            if (v == null) {
                log.warn("[MovInv-Auto] Variante no encontrada: codigo={} / ref={} — se omite",
                        it.codigoProducto, it.referenciaVariantes);
                continue;
            }
            itemsValidos.add(it);
            variantesDeItems.add(v);
            varianteIds.add(v.getProductoVarianteId());
        }

        // 3. Saldos/promedios base: último kardex por variante y existencias (2 consultas)
        java.util.Map<Long, Kardex> ultimoKardexPorVariante = new java.util.HashMap<>();
        java.util.Map<Long, Double> existenciaPorVariante = new java.util.HashMap<>();
        if (!varianteIds.isEmpty()) {
            for (Kardex k : kardexRepo.findUltimosPorVarianteYBodega(bodega.getCodigo(), varianteIds)) {
                ultimoKardexPorVariante.put(k.getProductoVariante().getProductoVarianteId(), k);
            }
            for (Existencias e : existenciasRepo.findByBodega_CodigoAndProductoVariante_ProductoVarianteIdIn(
                    bodega.getCodigo(), varianteIds)) {
                existenciaPorVariante.put(e.getProductoVariante().getProductoVarianteId(),
                        e.getExistencia() != null ? e.getExistencia().doubleValue() : 0.0);
            }
        }

        // Estado corrido por variante (permite que la MISMA variante aparezca en varios
        // ítems del movimiento encadenando saldo/promedio, igual que el flujo anterior)
        java.util.Map<Long, double[]> estadoVariante = new java.util.HashMap<>(); // [saldo, promedio, totalCosto]
        java.util.List<MovimientoInventarioDetalle> detallesNuevos = new java.util.ArrayList<>();
        java.util.List<Kardex> kardexNuevos = new java.util.ArrayList<>();

        for (int idx = 0; idx < itemsValidos.size(); idx++) {
            ItemMovimiento item = itemsValidos.get(idx);
            ProductoVariante variante = variantesDeItems.get(idx);

            // Detalle
            double entrada = tipo == TipoMovimiento.ENTRADA ? item.cantidad : 0.0;
            double salida  = tipo == TipoMovimiento.SALIDA  ? item.cantidad : 0.0;
            MovimientoInventarioDetalle det = new MovimientoInventarioDetalle();
            det.setMovimiento(movGuardado);
            det.setProductoVariante(variante);
            det.setCantidad(item.cantidad);
            det.setCostoUnitario(item.costoUnitario);     // SIN IVA (base kardex)
            det.setCostoPromedio(item.costoUnitario);     // se recalcula abajo en kardex si es entrada
            // totalDetalle = total con IVA si vino, sino el calculado neto
            double totalLineaDisp = item.totalLineaConIva > 0
                    ? item.totalLineaConIva
                    : item.cantidad * item.costoUnitario;
            det.setTotalDetalle(totalLineaDisp);

            if (tipo == TipoMovimiento.ENTRADA) {
                det.setBodegaDestino(bodega);
            } else if (tipo == TipoMovimiento.SALIDA) {
                det.setBodegaOrigen(bodega);
            }

            // Kardex: saldo y costo promedio anterior desde el estado en memoria
            // (precargado en bloque; si la variante ya apareció en este movimiento,
            // encadena sobre el saldo/promedio recalculado del ítem anterior).
            Long varianteId = variante.getProductoVarianteId();
            double[] estado = estadoVariante.get(varianteId);
            if (estado == null) {
                Kardex ultimo = ultimoKardexPorVariante.get(varianteId);
                if (ultimo != null) {
                    estado = new double[]{
                            ultimo.getSaldo() != null ? ultimo.getSaldo() : 0.0,
                            ultimo.getCostoPromedio() != null ? ultimo.getCostoPromedio() : 0.0,
                            ultimo.getTotalCosto() != null ? ultimo.getTotalCosto() : 0.0};
                } else {
                    // Sin kardex previo: partir del stock actual de la tabla existencias
                    // (no de 0: preserva saldos iniciales cargados sin kardex).
                    estado = new double[]{existenciaPorVariante.getOrDefault(varianteId, 0.0), 0.0, 0.0};
                }
            }
            double saldoAnterior = estado[0];
            double promedioAnterior = estado[1];
            Double saldototal = estado[2];


            double saldoNuevo = saldoAnterior + entrada - salida;

            double promedioNuevo = promedioAnterior;

            // Para SALIDAS, valorar al costo promedio vigente. EXCEPTO devolución de compra ("DC")
            // y reversas de anulación ("-ANUL"), que deben salir/entrar al costo EXACTO que pasa el
            // llamador (para que el kardex coincida con el asiento contable de la nota débito/reversa).
            double costoUnitarioFinal = item.costoUnitario;
            boolean respetaCostoLlamador = "DC".equals(documentoTipo)
                    || (documentoTipo != null && documentoTipo.endsWith("-ANUL"));
            if (salida > 0 && promedioAnterior > 0 && !respetaCostoLlamador) {
                costoUnitarioFinal = promedioAnterior;
            }
            // Para ENTRADAS por devolución (DV): si el llamador NO pasó un costo válido (≤0), se
            // recurre al promedio vigente del kardex. Si pasó un costo válido (el costo promedio
            // del producto, el MISMO que usa el asiento de reversa de COGS), se respeta para que
            // la valorización del kardex y el saldo del mayor (cuenta 1435) coincidan exactamente.
            if (entrada > 0 && "DV".equals(documentoTipo) && item.costoUnitario <= 0 && promedioAnterior > 0) {
                costoUnitarioFinal = promedioAnterior;
            }

            if (entrada > 0) {
                // Costo promedio ponderado en toda entrada (NIIF Sec.13 / NIC 2)
                double costoTotalAnterior = saldoAnterior * promedioAnterior;
                double costoTotalActual = entrada * item.costoUnitario;
                double totalUnidades = saldoAnterior + entrada;
                promedioNuevo = totalUnidades > 0
                        ? (costoTotalAnterior + costoTotalActual) / totalUnidades
                        : item.costoUnitario;
                promedioNuevo = Math.round(promedioNuevo * 100.0) / 100.0;
            }
            // Para devoluciones: mantener promedio anterior (la mercancía vuelve al mismo costo)
            if (entrada > 0 && "DV".equals(documentoTipo) && promedioAnterior > 0) {
                promedioNuevo = promedioAnterior;
            }

            // Si la variante todavía no tenía promedio (compra inicial), guardamos costoUnitario
            if (promedioNuevo <= 0) promedioNuevo = item.costoUnitario;
            // Redondear a 2 decimales para evitar errores de punto flotante
            promedioNuevo = Math.round(promedioNuevo * 100.0) / 100.0;

            // El costo promedio del detalle queda definitivo ANTES de guardar
            // (antes se guardaba el detalle y se volvía a guardar para actualizarlo)
            det.setCostoPromedio(promedioNuevo);

            Kardex kardex = new Kardex();
            kardex.setMovimiento(movGuardado);
            kardex.setDetalle(det);
            kardex.setProductoVariante(variante);
            kardex.setBodega(bodega);
            kardex.setFechaEmision(movGuardado.getFechaEmision());
            kardex.setFechaCreacion(LocalDateTime.now());
            kardex.setEntrada(entrada);
            kardex.setSalida(salida);
            kardex.setSaldo(saldoNuevo);
            kardex.setCostoUnitario(costoUnitarioFinal);
            kardex.setCostoPromedio(promedioNuevo);
            // Total costo: para salidas usar el promedio, para entradas usar el nuevo saldo * nuevo promedio
            double totalCostoLinea = (entrada > 0)
                    ?   (entrada * costoUnitarioFinal) + saldototal
                    :  saldototal - (salida * costoUnitarioFinal) ;
            kardex.setTotalCosto(totalCostoLinea);
            kardex.setTipo(tipo);
            kardex.setEstado(EstadoMovimiento.ACTIVO);
            kardex.setObservaciones(descripcion);

            detallesNuevos.add(det);
            kardexNuevos.add(kardex);

            // Estado corrido de la variante para el próximo ítem del mismo movimiento
            estadoVariante.put(varianteId, new double[]{saldoNuevo, promedioNuevo, totalCostoLinea});

            // Total del movimiento prefiere el total de línea con IVA si el caller lo proveyó
            // (así el header del historial muestra lo mismo que el documento origen).
            totalMov += (item.totalLineaConIva > 0) ? item.totalLineaConIva : totalCostoLinea;
        }

        // ── Persistencia EN BLOQUE ──
        // Detalles primero (el kardex referencia el detalle); luego el kardex.
        detalleRepo.saveAll(detallesNuevos);
        kardexRepo.saveAll(kardexNuevos);

        // Sincronizar y limpiar el contexto de persistencia ANTES de los upserts nativos:
        // con miles de entidades en sesión, cada consulta nativa disparaba un dirty-check
        // completo (O(n²)) y el upsert fila a fila se volvía la parte más lenta.
        entityManager.flush();
        entityManager.clear();

        // Actualizar tabla existencias para mantener sincronización con Kardex.
        // UPSERT atómico MULTI-FILA por lotes, con el saldo FINAL por variante
        // (deduplicado: si la variante apareció en varios ítems se escribe una vez).
        // El viejo SELECT→INSERT chocaba con el UNIQUE (variante, bodega) si otra
        // transacción creaba la fila después del snapshot (rollback-only del documento);
        // y el upsert de a una fila eran 3.500+ sentencias individuales.
        java.util.List<java.util.Map.Entry<Long, double[]>> saldosFinales =
                new java.util.ArrayList<>(estadoVariante.entrySet());
        final int LOTE_UPSERT = 500;
        for (int i = 0; i < saldosFinales.size(); i += LOTE_UPSERT) {
            java.util.List<java.util.Map.Entry<Long, double[]>> lote =
                    saldosFinales.subList(i, Math.min(i + LOTE_UPSERT, saldosFinales.size()));
            StringBuilder sql = new StringBuilder(
                    "INSERT INTO existencias (producto_variantes_id, bodega_id, existencia) VALUES ");
            for (int j = 0; j < lote.size(); j++) {
                if (j > 0) sql.append(',');
                sql.append("(?,?,?)");
            }
            sql.append(" ON DUPLICATE KEY UPDATE existencia = VALUES(existencia)");
            jakarta.persistence.Query q = entityManager.createNativeQuery(sql.toString());
            int p = 1;
            for (java.util.Map.Entry<Long, double[]> e : lote) {
                q.setParameter(p++, e.getKey());
                q.setParameter(p++, bodega.getCodigo());
                q.setParameter(p++, java.math.BigDecimal.valueOf(e.getValue()[0]));
            }
            q.executeUpdate();
        }

        movGuardado.setTotal(totalMov);
        movimientoRepo.save(movGuardado);

        log.info("[MovInv-Auto] {} → movimiento #{} con {} items ({} kardex), total={}",
                descripcion, movGuardado.getMovimientoId(), items.size(), kardexNuevos.size(), totalMov);
    }

    /** Helper utilitario para crear lista de items rápido. */
    public static List<ItemMovimiento> nuevaLista() {
        return new ArrayList<>();
    }
}
