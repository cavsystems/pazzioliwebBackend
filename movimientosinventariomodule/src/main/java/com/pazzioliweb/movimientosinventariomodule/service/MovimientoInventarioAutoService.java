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
import java.util.*;
import java.util.stream.Collectors;

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

        // Idempotencia (sin cambios, ya es 1 sola query)
        Optional<MovimientoInventario> existente =
                movimientoRepo.findByDocumentoOrigenTipoAndDocumentoOrigenId(documentoTipo, documentoOrigenId);
        if (existente.isPresent()) {
            log.info("[MovInv-Auto] Ya existe movimiento {} para {} #{}, omitiendo.",
                    existente.get().getMovimientoId(), documentoTipo, documentoOrigenId);
            return;
        }

        Bodegas bodega = bodegaRepo.findById(bodegaCodigo)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada: " + bodegaCodigo));

        // ── OPTIMIZACIÓN 1: filtrar items válidos ANTES de cualquier query, así no
        // desperdiciamos lookups en items con cantidad<=0 (antes se filtraban dentro del for) ──
        List<ItemMovimiento> itemsValidos = items.stream()
                .filter(i -> i.cantidad > 0)
                .toList();

        if (itemsValidos.isEmpty()) {
            log.warn("[MovInv-Auto] {} sin items con cantidad>0, omitido.", descripcion);
            return;
        }

        // ── OPTIMIZACIÓN 2: resolver TODAS las variantes en 1-3 queries batch en vez de
        // hasta 3 SELECTs por item (esto era el peor N+1: con 50 items podían ser 150 SELECTs) ──
        // Requiere agregar al repo métodos "In": findByProducto_CodigoContableInAndReferenciaVariantesIn,
        // findBySkuIn. Ver nota al final del mensaje sobre estos métodos nuevos.
        Set<String> codigos = itemsValidos.stream().map(i -> i.codigoProducto).collect(Collectors.toSet());
        Set<String> refs = itemsValidos.stream().map(i -> i.referenciaVariantes)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        // Mapa código+ref -> variante (match principal por codigo_contable+referencia)
        Map<String, ProductoVariante> porCodigoRef = varianteRepo
                .findByProducto_CodigoContableInAndReferenciaVariantesIn(codigos, refs)
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getProducto().getCodigoContable() + "|" + v.getReferenciaVariantes(),
                        v -> v, (a, b) -> a));

        // Mapa SKU -> variante para los fallbacks (una sola query trae todos los SKUs candidatos)
        Set<String> skusCandidatos = new HashSet<>();
        skusCandidatos.addAll(codigos);
        skusCandidatos.addAll(refs);
        Map<String, ProductoVariante> porSku = varianteRepo.findBySkuIn(skusCandidatos).stream()
                .collect(Collectors.toMap(ProductoVariante::getSku, v -> v, (a, b) -> a));

        // Resolvemos cada item contra los mapas en memoria (0 queries adicionales)
        Map<ItemMovimiento, ProductoVariante> variantePorItem = new HashMap<>();
        for (ItemMovimiento item : itemsValidos) {
            ProductoVariante v = porCodigoRef.get(item.codigoProducto + "|" + item.referenciaVariantes);
            if (v == null) v = porSku.get(item.codigoProducto);
            if (v == null && item.referenciaVariantes != null) v = porSku.get(item.referenciaVariantes);
            if (v == null) {
                log.warn("[MovInv-Auto] Variante no encontrada: codigo={} / ref={} — se omite",
                        item.codigoProducto, item.referenciaVariantes);
                continue;
            }
            variantePorItem.put(item, v);
        }

        if (variantePorItem.isEmpty()) {
            log.warn("[MovInv-Auto] {} sin variantes resueltas, omitido.", descripcion);
            return;
        }

        // ── OPTIMIZACIÓN 3: traer TODOS los últimos kardex y existencias de una vez
        // en vez de 1 SELECT por item. Requiere método batch en KardexRepo que traiga,
        // para una lista de varianteIds + bodega, el último registro por variante
        // (ej. con window function ROW_NUMBER() OVER (PARTITION BY variante ORDER BY fecha_creacion DESC) = 1,
        // o una query nativa equivalente). Ver nota al final. ──
        List<Long> varianteIds = variantePorItem.values().stream()
                .map(ProductoVariante::getProductoVarianteId).distinct().toList();

        Map<Long, Kardex> ultimoKardexPorVariante = kardexRepo
                .findUltimosPorVariantesYBodega(varianteIds, bodega.getCodigo())
                .stream()
                .collect(Collectors.toMap(k -> k.getProductoVariante().getProductoVarianteId(), k -> k));

        Map<Long, Existencias> existenciasPorVariante = existenciasRepo
                .findByProductoVariante_ProductoVarianteIdInAndBodega_Codigo(varianteIds, bodega.getCodigo())
                .stream()
                .collect(Collectors.toMap(e -> e.getProductoVariante().getProductoVarianteId(), e -> e));

        // Cabecera (necesitamos el ID antes de crear los detalles/kardex por la FK)
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipo(tipo);
        movimiento.setFechaEmision(fecha != null ? fecha : LocalDate.now());
        movimiento.setFechaCreacion(LocalDateTime.now());
        movimiento.setEstado(EstadoMovimiento.ACTIVO);
        movimiento.setObservaciones(descripcion + " (auto)");
        movimiento.setDocumentoOrigenTipo(documentoTipo);
        movimiento.setDocumentoOrigenId(documentoOrigenId);
        movimiento.setConsecutivo(consecutivo != null ? consecutivo : 0);

        if (comprobanteId != null) {
            comprobanteRepo.findById(comprobanteId.longValue()).ifPresent(movimiento::setComprobante);
        }

        MovimientoInventario movGuardado = movimientoRepo.save(movimiento);

        double totalMov = 0.0;

        // ── OPTIMIZACIÓN 4: acumular detalles/kardex/existencias en listas y hacer
        // saveAll() al final en vez de save() individual por item (menos round-trips,
        // y JPA puede hacer batching real de INSERTs si tienes spring.jpa.properties.hibernate.jdbc.batch_size configurado) ──
        List<MovimientoInventarioDetalle> detallesAGuardar = new ArrayList<>();
        List<Kardex> kardexAGuardar = new ArrayList<>();
        // saldoNuevo por variante para el upsert batch de existencias al final
        Map<Long, java.math.BigDecimal> saldosFinales = new HashMap<>();

        for (ItemMovimiento item : itemsValidos) {
            ProductoVariante variante = variantePorItem.get(item);
            if (variante == null) continue; // ya logueado arriba

            double entrada = tipo == TipoMovimiento.ENTRADA ? item.cantidad : 0.0;
            double salida  = tipo == TipoMovimiento.SALIDA  ? item.cantidad : 0.0;

            double totalLineaDisp = item.totalLineaConIva > 0
                    ? item.totalLineaConIva
                    : item.cantidad * item.costoUnitario;

            Kardex ultimo = ultimoKardexPorVariante.get(variante.getProductoVarianteId());
            double saldoAnterior;
            double promedioAnterior;
            double saldototal;

            if (ultimo != null) {
                saldoAnterior = ultimo.getSaldo();
                promedioAnterior = ultimo.getCostoPromedio() != null ? ultimo.getCostoPromedio() : 0.0;
                saldototal = ultimo.getTotalCosto();
            } else {
                Existencias exist = existenciasPorVariante.get(variante.getProductoVarianteId());
                saldoAnterior = (exist != null && exist.getExistencia() != null)
                        ? exist.getExistencia().doubleValue() : 0.0;
                promedioAnterior = 0.0;
                saldototal = 0.0;
            }

            double saldoNuevo = saldoAnterior + entrada - salida;
            double promedioNuevo = promedioAnterior;

            double costoUnitarioFinal = item.costoUnitario;
            boolean respetaCostoLlamador = "DC".equals(documentoTipo)
                    || (documentoTipo != null && documentoTipo.endsWith("-ANUL"));
            if (salida > 0 && promedioAnterior > 0 && !respetaCostoLlamador) {
                costoUnitarioFinal = promedioAnterior;
            }
            if (entrada > 0 && "DV".equals(documentoTipo) && item.costoUnitario <= 0 && promedioAnterior > 0) {
                costoUnitarioFinal = promedioAnterior;
            }

            if (entrada > 0) {
                double costoTotalAnterior = saldoAnterior * promedioAnterior;
                double costoTotalActual = entrada * item.costoUnitario;
                double totalUnidades = saldoAnterior + entrada;
                promedioNuevo = totalUnidades > 0
                        ? (costoTotalAnterior + costoTotalActual) / totalUnidades
                        : item.costoUnitario;
                promedioNuevo = Math.round(promedioNuevo * 100.0) / 100.0;
            }
            if (entrada > 0 && "DV".equals(documentoTipo) && promedioAnterior > 0) {
                promedioNuevo = promedioAnterior;
            }
            if (promedioNuevo <= 0) promedioNuevo = item.costoUnitario;
            promedioNuevo = Math.round(promedioNuevo * 100.0) / 100.0;

            // ── OPTIMIZACIÓN 5: setear costoPromedio UNA sola vez antes de armar el detalle,
            // así eliminamos el segundo detalleRepo.save(detGuardado) que era un UPDATE extra
            // repetido para el mismo registro que ya se iba a guardar en el saveAll ──
            MovimientoInventarioDetalle det = new MovimientoInventarioDetalle();
            det.setMovimiento(movGuardado);
            det.setProductoVariante(variante);
            det.setCantidad(item.cantidad);
            det.setCostoUnitario(item.costoUnitario);
            det.setCostoPromedio(promedioNuevo); // ya calculado, sin necesidad de update posterior
            det.setTotalDetalle(totalLineaDisp);
            if (tipo == TipoMovimiento.ENTRADA) {
                det.setBodegaDestino(bodega);
            } else if (tipo == TipoMovimiento.SALIDA) {
                det.setBodegaOrigen(bodega);
            }
            detallesAGuardar.add(det);

            Kardex kardex = new Kardex();
            kardex.setMovimiento(movGuardado);
            kardex.setDetalle(det); // referencia en memoria; se persiste via cascade o tras saveAll de detalles
            kardex.setProductoVariante(variante);
            kardex.setBodega(bodega);
            kardex.setFechaEmision(movGuardado.getFechaEmision());
            kardex.setFechaCreacion(LocalDateTime.now());
            kardex.setEntrada(entrada);
            kardex.setSalida(salida);
            kardex.setSaldo(saldoNuevo);
            kardex.setCostoUnitario(costoUnitarioFinal);
            kardex.setCostoPromedio(promedioNuevo);
            double totalCostoLinea = (entrada > 0)
                    ? (entrada * costoUnitarioFinal) + saldototal
                    : saldototal - (salida * costoUnitarioFinal);
            kardex.setTotalCosto(totalCostoLinea);
            kardex.setTipo(tipo);
            kardex.setEstado(EstadoMovimiento.ACTIVO);
            kardex.setObservaciones(descripcion);
            kardexAGuardar.add(kardex);

            // Actualizamos el "último kardex en memoria" de esta variante por si hay
            // varios items de la MISMA variante en el mismo movimiento (evita que el
            // segundo item lea un saldo desactualizado del batch inicial)
            saldosFinales.put(variante.getProductoVarianteId(), java.math.BigDecimal.valueOf(saldoNuevo));

            totalMov += (item.totalLineaConIva > 0) ? item.totalLineaConIva : totalCostoLinea;
        }

        // ── OPTIMIZACIÓN 4 (cont.): 2 saveAll en vez de N saves individuales ──
        detalleRepo.saveAll(detallesAGuardar);
        kardexRepo.saveAll(kardexAGuardar);

        // ── OPTIMIZACIÓN 6: 1 upsert por variante afectada (ya no había forma fácil de
        // batchear el upsert nativo salvo iterar, pero ahora es solo 1 vuelta por variante
        // única en vez de potencialmente repetirse sin necesidad) ──
        for (Map.Entry<Long, java.math.BigDecimal> entry : saldosFinales.entrySet()) {
            existenciasRepo.upsertSaldo(entry.getKey(), bodega.getCodigo(), entry.getValue());
        }

        movGuardado.setTotal(totalMov);
        movimientoRepo.save(movGuardado);

        log.info("[MovInv-Auto] {} → movimiento #{} con {} items, total={}",
                descripcion, movGuardado.getMovimientoId(), detallesAGuardar.size(), totalMov);
    }

    /** Helper utilitario para crear lista de items rápido. */
    public static List<ItemMovimiento> nuevaLista() {
        return new ArrayList<>();
    }
}
