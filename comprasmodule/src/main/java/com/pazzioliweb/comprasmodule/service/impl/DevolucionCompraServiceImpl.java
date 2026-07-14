package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.comprasmodule.dtos.DevolucionCompraDTO;
import com.pazzioliweb.comprasmodule.dtos.DevolucionCompraRequestDTO;
import com.pazzioliweb.comprasmodule.entity.*;
import com.pazzioliweb.comprasmodule.exception.OrdenCompraException;
import com.pazzioliweb.comprasmodule.repository.CuentaPorPagarRepository;
import com.pazzioliweb.comprasmodule.repository.DevolucionCompraRepository;
import com.pazzioliweb.comprasmodule.repository.OrdenCompraRepository;
import com.pazzioliweb.comprasmodule.service.DevolucionCompraService;
import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.service.AsientoContableService;
import com.pazzioliweb.comprobantesmodule.service.ConfiguracionContableService;
import com.pazzioliweb.comprobantesmodule.service.PeriodoContableService;
import com.pazzioliweb.movimientosinventariomodule.service.MovimientoInventarioAutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DevolucionCompraServiceImpl implements DevolucionCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final DevolucionCompraRepository devolucionCompraRepository;
    private final CuentaPorPagarRepository cuentaPorPagarRepository;

    @Autowired private AsientoContableService asientoService;
    @Autowired private ConfiguracionContableService configContable;
    @Autowired private PeriodoContableService periodoContableService;
    @Autowired private MovimientoInventarioAutoService movimientoInventarioAutoService;

    public DevolucionCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                       DevolucionCompraRepository devolucionCompraRepository,
                                       CuentaPorPagarRepository cuentaPorPagarRepository) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.devolucionCompraRepository = devolucionCompraRepository;
        this.cuentaPorPagarRepository = cuentaPorPagarRepository;
    }

    @Override
    @Transactional
    public DevolucionCompraDTO registrar(DevolucionCompraRequestDTO request) {
        if (request.getOrdenCompraId() == null)
            throw new OrdenCompraException("Falta la orden de compra a devolver.");
        if (request.getItems() == null || request.getItems().isEmpty())
            throw new OrdenCompraException("Debe indicar al menos un ítem a devolver.");

        OrdenCompra orden = ordenCompraRepository.findById(request.getOrdenCompraId())
                .orElseThrow(() -> new OrdenCompraException("Orden de compra no encontrada: " + request.getOrdenCompraId()));

        // Solo se puede devolver mercancía YA RECIBIDA (ingreso / legalización).
        String estado = orden.getEstado() != null ? orden.getEstado().toUpperCase() : "";
        boolean recibida = estado.contains("RECIBIDA") || estado.equals("LEGALIZADA") || estado.equals("CERRADA");
        boolean tieneRecibido = orden.getItems() != null && orden.getItems().stream()
                .anyMatch(d -> d.getCantidadRecibida() != null && d.getCantidadRecibida() > 0);
        if (!recibida && !tieneRecibido)
            throw new OrdenCompraException("Solo se puede devolver una compra con mercancía recibida (ingreso o legalización).");

        // Bloquear si el periodo contable está cerrado (fecha de emisión de la orden).
        LocalDate fechaDoc = orden.getFechaEmision() != null ? orden.getFechaEmision() : LocalDate.now();
        periodoContableService.validarPeriodoAbierto(fechaDoc);

        DevolucionCompra dev = new DevolucionCompra();
        dev.setOrdenCompra(orden);
        dev.setMotivo(request.getMotivo());
        dev.setObservaciones(request.getObservaciones());
        dev.setEstado("REGISTRADA");
        dev.setUsuarioCreacion(request.getUsuario() != null ? request.getUsuario() : "sistema");
        dev.setFechaCreacion(LocalDate.now());
        if (orden.getProveedor() != null) {
            com.pazzioliweb.tercerosmodule.entity.Terceros prov = orden.getProveedor();
            dev.setNitProveedor(prov.getIdentificacion());
            String nombre = (prov.getRazonSocial() != null && !prov.getRazonSocial().isBlank())
                    ? prov.getRazonSocial() : prov.getNombre1();
            dev.setNombreProveedor(nombre);
        }

        long seq = devolucionCompraRepository.countByOrdenCompra_Id(orden.getId()) + 1;
        dev.setNumeroDevolucion("DC-" + orden.getNumeroOrden() + "-" + seq);

        List<DetalleDevolucionCompra> detalles = new ArrayList<>();
        BigDecimal baseTotal = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;

        for (DevolucionCompraRequestDTO.ItemDevolucionCompra it : request.getItems()) {
            if (it.getCantidadDevolver() == null || it.getCantidadDevolver() <= 0) continue;
            DetalleOrdenCompra detalle = orden.getItems().stream()
                    .filter(d -> d.getId().equals(it.getDetalleId()))
                    .findFirst()
                    .orElseThrow(() -> new OrdenCompraException("Detalle de orden no encontrado: " + it.getDetalleId()));

            int yaDevuelto = devolucionCompraRepository.totalDevueltoPorDetalle(detalle.getId());
            int recibido = detalle.getCantidadRecibida() != null ? detalle.getCantidadRecibida() : 0;
            int disponible = recibido - yaDevuelto;
            if (it.getCantidadDevolver() > disponible)
                throw new OrdenCompraException("No puede devolver " + it.getCantidadDevolver()
                        + " de '" + detalle.getDescripcionProducto() + "': disponible para devolver = " + disponible);

            // Validar STOCK FÍSICO disponible en la bodega: la mercancía sale hacia el proveedor, así
            // que debe existir en inventario. Sin esta validación, si ya se vendió/consumió el stock,
            // la salida dejaba existencias y kardex NEGATIVOS. La cantidad recibida-devuelta puede ser
            // mayor que el stock actual (el producto ya rotó), por eso se valida contra existencias.
            if (orden.getBodega() != null) {
                String codigoStock = (detalle.getSku() != null && !detalle.getSku().isBlank())
                        ? detalle.getSku() : detalle.getCodigoProducto();
                double stock = movimientoInventarioAutoService.existenciaDisponible(
                        codigoStock, detalle.getReferenciaVariantes(), orden.getBodega().getCodigo());
                if (it.getCantidadDevolver() > stock)
                    throw new OrdenCompraException("No hay stock suficiente para devolver "
                            + it.getCantidadDevolver() + " de '" + detalle.getDescripcionProducto()
                            + "': existencia actual en bodega = " + stock);
            }

            BigDecimal precio = detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : BigDecimal.ZERO;
            BigDecimal descPct = detalle.getDescuento() != null ? detalle.getDescuento() : BigDecimal.ZERO; // PORCENTAJE
            BigDecimal ivaPct = detalle.getIva() != null ? detalle.getIva() : BigDecimal.ZERO;             // PORCENTAJE
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(it.getCantidadDevolver()));
            BigDecimal montoDesc = subtotal.multiply(descPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal base = subtotal.subtract(montoDesc).max(BigDecimal.ZERO);
            BigDecimal ivaLinea = base.multiply(ivaPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Costo unitario NETO de descuento = base/cant. Se usa para la salida de inventario y
            // debe coincidir con el crédito contable a Inventarios (que también usa la base neta),
            // para que kardex y mayor 1435 no diverjan.
            BigDecimal costoNetoUnit = it.getCantidadDevolver() > 0
                    ? base.divide(BigDecimal.valueOf(it.getCantidadDevolver()), 2, RoundingMode.HALF_UP) : precio;
            DetalleDevolucionCompra dd = new DetalleDevolucionCompra();
            dd.setDevolucionCompra(dev);
            dd.setDetalleOrdenCompra(detalle);
            dd.setCantidadDevuelta(it.getCantidadDevolver());
            dd.setCostoUnitario(costoNetoUnit);
            dd.setIvaLinea(ivaLinea);
            dd.setTotalLinea(base.add(ivaLinea));
            dd.setMotivo(it.getMotivo() != null ? it.getMotivo() : request.getMotivo());
            detalles.add(dd);

            baseTotal = baseTotal.add(base);
            ivaTotal = ivaTotal.add(ivaLinea);
        }

        if (detalles.isEmpty())
            throw new OrdenCompraException("No hay cantidades válidas para devolver.");

        BigDecimal brutoDevuelto = baseTotal.add(ivaTotal);

        // Retención practicada, proporcional a lo devuelto vs. el bruto de la orden.
        BigDecimal brutoOrden = (orden.getGravada() != null ? orden.getGravada() : BigDecimal.ZERO)
                .add(orden.getIva() != null ? orden.getIva() : BigDecimal.ZERO);
        BigDecimal prop = brutoOrden.compareTo(BigDecimal.ZERO) > 0
                ? brutoDevuelto.divide(brutoOrden, 6, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal rfDev = nz(orden.getRetefuente()).multiply(prop).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rvDev = nz(orden.getReteiva()).multiply(prop).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rcDev = nz(orden.getReteica()).multiply(prop).setScale(2, RoundingMode.HALF_UP);
        BigDecimal retDevuelta = rfDev.add(rvDev).add(rcDev);
        BigDecimal netoDevuelto = brutoDevuelto.subtract(retDevuelta).max(BigDecimal.ZERO);

        dev.setTotalDevuelto(baseTotal);
        dev.setIvaDevuelto(ivaTotal);
        dev.setRetencionDevuelta(retDevuelta);
        dev.setTotalNeto(netoDevuelto);
        dev.setItems(detalles);

        DevolucionCompra guardada = devolucionCompraRepository.save(dev);

        // Salida de inventario (la mercancía vuelve al proveedor).
        generarSalidaInventario(guardada, orden);

        // ¿La compra fue a crédito (tiene CxP vigente) o de contado?
        boolean esCredito = ordenTieneCxPVigente(orden);

        // Asiento contable de la nota débito. Devuelve el monto realmente debitado a CxP (0 si contado
        // o si no se pudo postear), para reducir el auxiliar de CxP exactamente por ese valor.
        BigDecimal montoCxPDebitado = generarAsientoNotaDebito(guardada, orden, baseTotal, ivaTotal, rfDev, rvDev, rcDev, esCredito);

        // Solo se reduce la CxP si la compra era a crédito y se debitó a CxP (misma cifra del asiento).
        if (esCredito && montoCxPDebitado.compareTo(BigDecimal.ZERO) > 0) {
            reducirCuentaPorPagar(orden, montoCxPDebitado);
        }
        // Persistir el monto debitado a CxP para reponer EXACTAMENTE ese valor al anular.
        guardada.setMontoCxpDebitado(esCredito ? montoCxPDebitado : BigDecimal.ZERO);
        devolucionCompraRepository.save(guardada);

        return toDTO(guardada, orden);
    }

    /** Reingresa al inventario (ENTRADA de kardex) las cantidades de una devolución de compra anulada. */
    private void revertirInventarioAnulacionCompra(DevolucionCompra dev, OrdenCompra orden) {
        try {
            if (dev.getItems() == null || orden == null || orden.getBodega() == null) return;
            // Solo reversar si la SALIDA original ("DC") realmente se registró; si falló en su momento,
            // no reingresar (evitaría stock fantasma).
            if (!movimientoInventarioAutoService.existeMovimiento("DC", dev.getId())) return;
            List<MovimientoInventarioAutoService.ItemMovimiento> items = MovimientoInventarioAutoService.nuevaLista();
            for (DetalleDevolucionCompra dd : dev.getItems()) {
                DetalleOrdenCompra det = dd.getDetalleOrdenCompra();
                if (det == null || dd.getCantidadDevuelta() == null || dd.getCantidadDevuelta() <= 0) continue;
                String codigo = det.getSku() != null && !det.getSku().isBlank() ? det.getSku() : det.getCodigoProducto();
                double costo = dd.getCostoUnitario() != null ? dd.getCostoUnitario().doubleValue() : 0.0;
                items.add(new MovimientoInventarioAutoService.ItemMovimiento(
                        codigo, det.getReferenciaVariantes(), dd.getCantidadDevuelta().doubleValue(), costo));
            }
            movimientoInventarioAutoService.registrarAjusteAnulacion(
                    "DC-ANUL", dev.getId(), orden.getBodega().getCodigo(), LocalDate.now(), items, true);
        } catch (Exception ex) {
            System.out.println("[AnularDevolucionCompra] Reingreso de inventario (no crítico): " + ex.getMessage());
        }
    }

    /** True si la orden tiene una CxP con SALDO PENDIENTE (compra a crédito por pagar); false =
     *  contado o ya pagada. Una CxP PAGADA (saldo 0) NO es crédito vigente: si se tratara como tal,
     *  la nota débito debitaría 2205 sin que el auxiliar tenga saldo que bajar (mayor ≠ subledger).*/
    private boolean ordenTieneCxPVigente(OrdenCompra orden) {
        try {
            return cuentaPorPagarRepository.findByNumeroFactura(orden.getNumeroOrden()).stream()
                    .anyMatch(c -> !"ANULADA".equalsIgnoreCase(c.getEstado())
                            && c.getSaldo() != null && c.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        } catch (Exception ex) {
            return false;
        }
    }

    /** Saldo total aún pendiente de pago de la orden (Σ saldos de CxP no anuladas). 0 si contado/pagada. */
    private BigDecimal saldoPendienteCxP(OrdenCompra orden) {
        try {
            return cuentaPorPagarRepository.findByNumeroFactura(orden.getNumeroOrden()).stream()
                    .filter(c -> !"ANULADA".equalsIgnoreCase(c.getEstado()) && c.getSaldo() != null)
                    .map(CuentaPorPagar::getSaldo)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private void generarSalidaInventario(DevolucionCompra dev, OrdenCompra orden) {
        try {
            List<MovimientoInventarioAutoService.ItemMovimiento> items = MovimientoInventarioAutoService.nuevaLista();
            for (DetalleDevolucionCompra dd : dev.getItems()) {
                DetalleOrdenCompra det = dd.getDetalleOrdenCompra();
                String codigo = det.getSku() != null && !det.getSku().isBlank() ? det.getSku() : det.getCodigoProducto();
                double costo = dd.getCostoUnitario() != null ? dd.getCostoUnitario().doubleValue() : 0.0;
                items.add(new MovimientoInventarioAutoService.ItemMovimiento(
                        codigo, det.getReferenciaVariantes(), dd.getCantidadDevuelta().doubleValue(), costo));
            }
            movimientoInventarioAutoService.registrarSalidaPorDevolucionCompra(
                    dev.getNumeroDevolucion(), dev.getId(),
                    orden.getBodega() != null ? orden.getBodega().getCodigo() : null,
                    dev.getFechaCreacion(), items);
        } catch (Exception ex) {
            System.out.println("[DevolucionCompra] Salida de inventario (no crítica): " + ex.getMessage());
        }
    }

    /**
     * Asiento de la nota débito de compra:
     *   DR CxP Proveedores (2205)          por el neto devuelto (baja lo que le debemos)
     *   DR Retenciones por pagar (2365xx)  por la retención reversada (proporcional)
     *   CR Inventario (1435)               por la base devuelta
     *   CR IVA descontable (2408)          por el IVA devuelto
     * El débito a CxP absorbe la retención que no se pueda reversar por falta de cuenta, de modo
     * que el asiento SIEMPRE cuadre.
     */
    private BigDecimal generarAsientoNotaDebito(DevolucionCompra dev, OrdenCompra orden,
                                          BigDecimal base, BigDecimal iva,
                                          BigDecimal rfDev, BigDecimal rvDev, BigDecimal rcDev,
                                          boolean esCredito) {
        BigDecimal montoContrapartidaCxP = BigDecimal.ZERO;
        try {
            List<AsientoContableService.LineaDTO> lineas = new ArrayList<>();
            Integer provId = orden.getProveedor() != null ? orden.getProveedor().getTerceroId() : null;
            String provNom = dev.getNombreProveedor();

            // ── CRÉDITOS ──
            BigDecimal creditoTotal = BigDecimal.ZERO;
            CuentaContable inv = configContable.inventarios().orElse(null);
            CuentaContable ivaDesc = configContable.ivaDescontable().orElse(null);
            if (inv == null) {
                System.out.println("[NotaDebitoCompra] Cuenta 1435 Inventarios no configurada. Asiento omitido.");
                return BigDecimal.ZERO;
            }
            // Si no hay cuenta de IVA descontable, el IVA se pliega al crédito de inventario para cuadrar.
            BigDecimal creditoInventario = base;
            if (iva.compareTo(BigDecimal.ZERO) > 0 && ivaDesc == null) creditoInventario = creditoInventario.add(iva);
            if (creditoInventario.compareTo(BigDecimal.ZERO) > 0) {
                lineas.add(AsientoContableService.LineaDTO.credito(inv.getId(), creditoInventario,
                        "Devolución compra " + dev.getNumeroDevolucion()));
                creditoTotal = creditoTotal.add(creditoInventario);
            }
            if (iva.compareTo(BigDecimal.ZERO) > 0 && ivaDesc != null) {
                lineas.add(AsientoContableService.LineaDTO.credito(ivaDesc.getId(), iva,
                        "Reversa IVA descontable devolución compra " + dev.getNumeroDevolucion()));
                creditoTotal = creditoTotal.add(iva);
            }

            // ── DÉBITOS de retención por pagar (reversa), solo las que tienen cuenta ──
            BigDecimal retPosted = BigDecimal.ZERO;
            retPosted = retPosted.add(debitoRetencion(lineas, configContable.retefuentePagar().orElse(null), rfDev,
                    "Reversa Retefuente devolución compra", provId, provNom));
            retPosted = retPosted.add(debitoRetencion(lineas, configContable.reteivaPagar().orElse(null), rvDev,
                    "Reversa ReteIVA devolución compra", provId, provNom));
            retPosted = retPosted.add(debitoRetencion(lineas, configContable.reteicaPagar().orElse(null), rcDev,
                    "Reversa ReteICA devolución compra", provId, provNom));

            // ── DÉBITO a la contrapartida = crédito total − retención reversada (absorbe lo no posteado) ──
            // Si la compra fue a CRÉDITO → DR CxP Proveedores (2205) (baja lo que le debemos).
            // Si fue de CONTADO → DR Caja general (recuperamos el efectivo del proveedor).
            BigDecimal montoContrapartida = creditoTotal.subtract(retPosted).max(BigDecimal.ZERO);
            if (montoContrapartida.compareTo(BigDecimal.ZERO) > 0) {
                if (esCredito) {
                    // Compra a crédito: puede estar PARCIALMENTE pagada. Solo el SALDO PENDIENTE aún se
                    // le debe al proveedor → DR 2205 por esa porción (baja la CxP). La porción YA PAGADA
                    // no se le debe: el proveedor nos reintegra ese efectivo → DR Caja. Antes se debitaba
                    // TODO a 2205 aunque el saldo fuera menor: el mayor 2205 bajaba más que el auxiliar
                    // (reducirCuentaPorPagar topa en el saldo) → mayor ≠ subledger.
                    BigDecimal saldoPend = saldoPendienteCxP(orden);
                    BigDecimal aCxP = montoContrapartida.min(saldoPend).max(BigDecimal.ZERO);
                    BigDecimal aCaja = montoContrapartida.subtract(aCxP).max(BigDecimal.ZERO);

                    if (aCxP.compareTo(BigDecimal.ZERO) > 0) {
                        CuentaContable cxp = configContable.cxpProveedores().orElse(null);
                        if (cxp == null) {
                            System.out.println("[NotaDebitoCompra] Cuenta 2205 CxP no configurada. Asiento omitido.");
                            return BigDecimal.ZERO;
                        }
                        AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO
                                .debito(cxp.getId(), aCxP, "Nota débito a proveedor " + dev.getNumeroDevolucion());
                        if (provId != null) l.conTercero(provId, provNom);
                        lineas.add(l);
                        montoContrapartidaCxP = aCxP;
                    }
                    if (aCaja.compareTo(BigDecimal.ZERO) > 0) {
                        CuentaContable caja = configContable.cajaGeneral().orElse(configContable.cxpProveedores().orElse(null));
                        if (caja == null) {
                            System.out.println("[NotaDebitoCompra] Cuenta caja/2205 no configurada. Asiento omitido.");
                            return BigDecimal.ZERO;
                        }
                        lineas.add(AsientoContableService.LineaDTO
                                .debito(caja.getId(), aCaja, "Reintegro proveedor (porción pagada) " + dev.getNumeroDevolucion()));
                    }
                } else {
                    // Compra de CONTADO (o CxP ya saldada): el proveedor reintegra todo el efectivo → DR Caja.
                    CuentaContable caja = configContable.cajaGeneral().orElse(configContable.cxpProveedores().orElse(null));
                    if (caja == null) {
                        System.out.println("[NotaDebitoCompra] Cuenta caja/2205 no configurada. Asiento omitido.");
                        return BigDecimal.ZERO;
                    }
                    lineas.add(AsientoContableService.LineaDTO
                            .debito(caja.getId(), montoContrapartida, "Devolución contado proveedor " + dev.getNumeroDevolucion()));
                }
            }

            asientoService.generarAsiento(
                    dev.getNumeroDevolucion(),
                    dev.getFechaCreacion(),
                    "Devolución de compra " + dev.getNumeroDevolucion()
                            + (provNom != null ? " - " + provNom : ""),
                    "DC",
                    dev.getId(),
                    null,
                    lineas);
        } catch (Exception ex) {
            System.out.println("[NotaDebitoCompra] Error generando asiento (no crítico): " + ex.getMessage());
        }
        return montoContrapartidaCxP;
    }

    /** Agrega un débito de reversa de retención si la cuenta existe y el monto > 0. Devuelve el monto posteado. */
    private BigDecimal debitoRetencion(List<AsientoContableService.LineaDTO> lineas, CuentaContable cta,
                                       BigDecimal monto, String desc, Integer tercId, String tercNom) {
        if (cta == null || monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        AsientoContableService.LineaDTO l = AsientoContableService.LineaDTO.debito(cta.getId(), monto, desc);
        if (tercId != null) l.conTercero(tercId, tercNom);
        lineas.add(l);
        return monto;
    }

    private void reducirCuentaPorPagar(OrdenCompra orden, BigDecimal netoDevuelto) {
        if (netoDevuelto == null || netoDevuelto.compareTo(BigDecimal.ZERO) <= 0) return;
        try {
            List<CuentaPorPagar> cxps = cuentaPorPagarRepository.findByNumeroFactura(orden.getNumeroOrden());
            BigDecimal restante = netoDevuelto;
            for (CuentaPorPagar cxp : cxps) {
                if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
                if ("ANULADA".equalsIgnoreCase(cxp.getEstado())) continue;
                BigDecimal saldo = cxp.getSaldo() != null ? cxp.getSaldo() : BigDecimal.ZERO;
                BigDecimal aplicar = saldo.min(restante);
                BigDecimal nuevo = saldo.subtract(aplicar);
                cxp.setSaldo(nuevo);
                if (nuevo.compareTo(BigDecimal.ZERO) <= 0) cxp.setEstado("PAGADA");
                cuentaPorPagarRepository.save(cxp);
                restante = restante.subtract(aplicar);
            }
        } catch (Exception ex) {
            System.out.println("[DevolucionCompra] Ajuste de CxP (no crítico): " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public DevolucionCompraDTO anular(Long id, String motivo, String usuario) {
        DevolucionCompra dev = devolucionCompraRepository.findById(id)
                .orElseThrow(() -> new OrdenCompraException("Devolución de compra no encontrada: " + id));
        if ("ANULADA".equalsIgnoreCase(dev.getEstado()))
            throw new OrdenCompraException("La devolución de compra ya está anulada.");
        if (motivo == null || motivo.isBlank())
            throw new OrdenCompraException("El motivo de anulación es obligatorio.");

        OrdenCompra orden = dev.getOrdenCompra();
        LocalDate fechaDoc = dev.getFechaCreacion() != null ? dev.getFechaCreacion() : LocalDate.now();
        periodoContableService.validarPeriodoAbierto(fechaDoc);

        // Reversar el asiento de la nota débito.
        asientoService.anularAsientoDeDocumento("DC", dev.getId());
        // Reingresar la mercancía al inventario (revierte la salida de la devolución) con un
        // movimiento REAL de kardex, para que existencias/kardex/mayor 1435 queden consistentes.
        // Sin esto, anular dejaba el stock descontado y permitía volver a devolver (doble descuento).
        revertirInventarioAnulacionCompra(dev, orden);
        // Devolver la CxP reducida: se repone EXACTAMENTE lo que se debitó a CxP en el registro
        // (no el neto teórico), para que auxiliar y mayor 2205 vuelvan al mismo punto.
        aumentarCuentaPorPagar(orden, dev.getMontoCxpDebitado());

        dev.setEstado("ANULADA");
        dev.setMotivoAnulacion(motivo);
        dev.setFechaAnulacion(LocalDate.now());
        dev.setUsuarioAnulacion(usuario);
        devolucionCompraRepository.save(dev);
        return toDTO(dev, orden);
    }

    private void aumentarCuentaPorPagar(OrdenCompra orden, BigDecimal montoAReponer) {
        if (orden == null || montoAReponer == null || montoAReponer.compareTo(BigDecimal.ZERO) <= 0) return;
        try {
            List<CuentaPorPagar> cxps = cuentaPorPagarRepository.findByNumeroFactura(orden.getNumeroOrden());
            BigDecimal restante = montoAReponer;
            // Distribuye la reposición entre las CxP no anuladas (simétrico con reducirCuentaPorPagar,
            // que reparte entre varias), sin exceder el valorNeto de cada una.
            for (CuentaPorPagar cxp : cxps) {
                if (restante.compareTo(BigDecimal.ZERO) <= 0) break;
                if ("ANULADA".equalsIgnoreCase(cxp.getEstado())) continue;
                BigDecimal saldo = cxp.getSaldo() != null ? cxp.getSaldo() : BigDecimal.ZERO;
                BigDecimal valorNeto = cxp.getValorNeto() != null ? cxp.getValorNeto() : saldo.add(restante);
                BigDecimal cupo = valorNeto.subtract(saldo).max(BigDecimal.ZERO); // cuánto cabe sin pasar el valorNeto
                BigDecimal aplicar = cupo.min(restante);
                if (aplicar.compareTo(BigDecimal.ZERO) <= 0) continue;
                cxp.setSaldo(saldo.add(aplicar));
                cxp.setEstado(cxp.getSaldo().compareTo(BigDecimal.ZERO) > 0 ? "PENDIENTE" : "PAGADA");
                cuentaPorPagarRepository.save(cxp);
                restante = restante.subtract(aplicar);
            }
        } catch (Exception ex) {
            System.out.println("[AnularDevolucionCompra] Ajuste de CxP (no crítico): " + ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionCompraDTO> listarPorOrden(Long ordenCompraId) {
        List<DevolucionCompraDTO> out = new ArrayList<>();
        for (DevolucionCompra d : devolucionCompraRepository.findByOrdenCompra_IdOrderByIdDesc(ordenCompraId)) {
            out.add(toDTO(d, d.getOrdenCompra()));
        }
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public DevolucionCompraDTO obtenerPorId(Long id) {
        DevolucionCompra d = devolucionCompraRepository.findById(id)
                .orElseThrow(() -> new OrdenCompraException("Devolución de compra no encontrada: " + id));
        return toDTO(d, d.getOrdenCompra());
    }

    private static BigDecimal nz(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }

    private DevolucionCompraDTO toDTO(DevolucionCompra d, OrdenCompra orden) {
        DevolucionCompraDTO dto = new DevolucionCompraDTO();
        dto.setId(d.getId());
        dto.setOrdenCompraId(orden != null ? orden.getId() : null);
        dto.setNumeroOrden(orden != null ? orden.getNumeroOrden() : null);
        dto.setNumeroDevolucion(d.getNumeroDevolucion());
        dto.setMotivo(d.getMotivo());
        dto.setObservaciones(d.getObservaciones());
        dto.setEstado(d.getEstado());
        dto.setTotalDevuelto(d.getTotalDevuelto());
        dto.setIvaDevuelto(d.getIvaDevuelto());
        dto.setRetencionDevuelta(d.getRetencionDevuelta());
        dto.setTotalNeto(d.getTotalNeto());
        dto.setNombreProveedor(d.getNombreProveedor());
        dto.setNitProveedor(d.getNitProveedor());
        dto.setUsuarioCreacion(d.getUsuarioCreacion());
        dto.setFechaCreacion(d.getFechaCreacion());
        List<DevolucionCompraDTO.Item> items = new ArrayList<>();
        if (d.getItems() != null) {
            for (DetalleDevolucionCompra dd : d.getItems()) {
                DevolucionCompraDTO.Item i = new DevolucionCompraDTO.Item();
                i.setDetalleId(dd.getDetalleOrdenCompra() != null ? dd.getDetalleOrdenCompra().getId() : null);
                if (dd.getDetalleOrdenCompra() != null) {
                    i.setCodigoProducto(dd.getDetalleOrdenCompra().getCodigoProducto());
                    i.setDescripcionProducto(dd.getDetalleOrdenCompra().getDescripcionProducto());
                }
                i.setCantidadDevuelta(dd.getCantidadDevuelta());
                i.setCostoUnitario(dd.getCostoUnitario());
                i.setIvaLinea(dd.getIvaLinea());
                i.setTotalLinea(dd.getTotalLinea());
                items.add(i);
            }
        }
        dto.setItems(items);
        return dto;
    }
}
