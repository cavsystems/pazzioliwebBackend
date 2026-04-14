package com.pazzioliweb.cajerosmodule.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.cajerosmodule.dtos.CuadreCajaDTO;
import com.pazzioliweb.cajerosmodule.dtos.CuadreCajaDTO.ResumenTipoDocumento;
import com.pazzioliweb.cajerosmodule.dtos.DetalleCajeroDTO;
import com.pazzioliweb.cajerosmodule.dtos.MovimientoCajeroDTO;
import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.entity.DetalleCajero;
import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.repositori.DetalleCajeroRepository;
import com.pazzioliweb.cajerosmodule.repositori.MovimientoCajeroRepository;
import com.pazzioliweb.comprobantesmodule.entity.Comprobantes;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobantesRepository;

@Service
public class DetalleCajeroService {

    private static final ZoneId ZONA_BOGOTA = ZoneId.of("America/Bogota");

    private final DetalleCajeroRepository detalleCajeroRepository;
    private final MovimientoCajeroRepository movimientoCajeroRepository;
    private final ComprobantesRepository comprobantesRepository;

    @Autowired
    public DetalleCajeroService(DetalleCajeroRepository detalleCajeroRepository,
                                MovimientoCajeroRepository movimientoCajeroRepository,
                                ComprobantesRepository comprobantesRepository) {
        this.detalleCajeroRepository = detalleCajeroRepository;
        this.movimientoCajeroRepository = movimientoCajeroRepository;
        this.comprobantesRepository = comprobantesRepository;
    }

    // ========================
    // CRUD BÁSICO
    // ========================

    public Page<DetalleCajeroDTO> listar(int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return detalleCajeroRepository.listarDetalleCajerosDTO(pageable);
    }

    public Page<DetalleCajeroDTO> listarPorCajero(Integer cajeroId, int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return detalleCajeroRepository.listarDetallePorCajeroDTO(cajeroId, pageable);
    }

    public Optional<DetalleCajero> buscarPorId(Long id) {
        return detalleCajeroRepository.findById(id);
    }

    public DetalleCajero guardar(DetalleCajero detalle) {
        return detalleCajeroRepository.save(detalle);
    }

    public void eliminar(Long id) {
        detalleCajeroRepository.deleteById(id);
    }

    public List<DetalleCajero> buscarSesionesAbiertas(Integer cajeroId) {
        return detalleCajeroRepository.findByCajero_CajeroIdAndEstado(cajeroId, DetalleCajero.EstadoDetalleCajero.ABIERTA);
    }

    // ========================
    // CONSULTA DE MOVIMIENTOS
    // ========================

    public Page<MovimientoCajeroDTO> listarMovimientosPorSesion(Long detalleCajeroId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("consecutivo").ascending());
        return movimientoCajeroRepository.listarPorSesion(detalleCajeroId, pageable);
    }

    public Page<MovimientoCajeroDTO> listarMovimientosPorCajero(Integer cajeroId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaMovimiento").descending());
        return movimientoCajeroRepository.listarPorCajero(cajeroId, pageable);
    }

    public List<MovimientoCajero> obtenerMovimientosCuadre(Long detalleCajeroId) {
        return movimientoCajeroRepository.findByDetalleCajero_DetalleCajeroIdOrderByConsecutivoAsc(detalleCajeroId);
    }

    public List<MovimientoCajero> obtenerMovimientosPorTipo(Long detalleCajeroId, MovimientoCajero.TipoMovimiento tipo) {
        return movimientoCajeroRepository
                .findByDetalleCajero_DetalleCajeroIdAndTipoMovimientoOrderByConsecutivoTipoAsc(detalleCajeroId, tipo);
    }

    // ========================
    // APERTURA DE SESIÓN
    // ========================

    /**
     * Abre sesión de caja sin comprobante (usado en login automático).
     * Si ya hay una sesión abierta del día de hoy, la retorna.
     * Si hay una sesión abierta de un día anterior, la cierra y abre una nueva.
     */
    @Transactional
    public DetalleCajero abrirSesionCajero(Cajero cajero, BigDecimal baseCaja) {
        return abrirSesionCajero(cajero, baseCaja, null);
    }

    /**
     * Abre sesión de caja asignando un comprobante específico.
     *
     * Lógica de sesión diaria:
     *   - Si ya hay una sesión abierta del DÍA DE HOY → la retorna sin crear nueva.
     *   - Si hay una sesión abierta de un DÍA ANTERIOR → la cierra automáticamente
     *     y crea una nueva sesión para hoy.
     *   - Si no hay sesión abierta → crea una nueva.
     *
     * @param cajero         Cajero que abre la caja
     * @param baseCaja       Monto inicial con el que arranca
     * @param comprobanteId  ID del comprobante de caja a usar (puede ser null)
     */
    @Transactional
    public DetalleCajero abrirSesionCajero(Cajero cajero, BigDecimal baseCaja, Integer comprobanteId) {
        List<DetalleCajero> sesionesAbiertas = detalleCajeroRepository
                .findByCajero_CajeroIdAndEstado(cajero.getCajeroId(), DetalleCajero.EstadoDetalleCajero.ABIERTA);

        LocalDate hoy = LocalDate.now(ZONA_BOGOTA);

        if (!sesionesAbiertas.isEmpty()) {
            DetalleCajero sesionExistente = sesionesAbiertas.get(0);
            LocalDate fechaApertura = sesionExistente.getFechaApertura().toLocalDate();

            if (fechaApertura.equals(hoy)) {
                // Sesión abierta del día de hoy → retornarla
                return sesionExistente;
            }

            // Sesión de un día anterior → cerrarla automáticamente
            cerrarSesionPorCambioDeDia(sesionExistente);
        }

        // Crear nueva sesión para hoy
        DetalleCajero detalle = new DetalleCajero();
        detalle.setCajero(cajero);
        detalle.setMontoInicial(baseCaja);
        detalle.setBaseCaja(baseCaja);
        detalle.setMontoFinal(BigDecimal.ZERO);
        detalle.setFechaApertura(LocalDateTime.now(ZONA_BOGOTA));
        detalle.setEstado(DetalleCajero.EstadoDetalleCajero.ABIERTA);
        detalle.setConsecutivo(0);
        detalle.setTotalRecaudo(BigDecimal.ZERO);
        detalle.setTotalCosto(BigDecimal.ZERO);
        detalle.setTotalEfectivo(BigDecimal.ZERO);
        detalle.setTotalMediosElectronicos(BigDecimal.ZERO);

        // Asignar comprobante si se proporcionó
        if (comprobanteId != null) {
            comprobantesRepository.findById(comprobanteId).ifPresent(detalle::setComprobante);
        }

        return detalleCajeroRepository.save(detalle);
    }

    // ============================================================
    // CIERRE DE CAJA — 3 COMPONENTES: efectivo, electrónico, diferencia
    // ============================================================

    /**
     * Cierra la sesión del cajero calculando los 3 componentes del cuadre:
     *   1. Efectivo esperado  = baseCaja + totalEfectivo(ingresos) − totalEfectivo(egresos)
     *   2. Electrónico esperado = totalMediosElectronicos
     *   3. Diferencia = declarado − esperado (+ sobrante, − faltante)
     *
     * @param cajeroId                    ID del cajero
     * @param efectivoDeclarado           Lo que el cajero cuenta en efectivo
     * @param mediosElectronicosDeclarado Lo que el cajero reporta en medios electrónicos
     */
    @Transactional
    public DetalleCajero cerrarSesionCajero(Integer cajeroId,
                                            BigDecimal efectivoDeclarado,
                                            BigDecimal mediosElectronicosDeclarado) {
        List<DetalleCajero> sesionesAbiertas = detalleCajeroRepository
                .findByCajero_CajeroIdAndEstado(cajeroId, DetalleCajero.EstadoDetalleCajero.ABIERTA);

        if (sesionesAbiertas.isEmpty()) {
            throw new RuntimeException("No hay sesión abierta para el cajero: " + cajeroId);
        }

        DetalleCajero detalle = sesionesAbiertas.get(0);
        detalle.setFechaCierre(LocalDateTime.now(ZONA_BOGOTA));
        detalle.setEstado(DetalleCajero.EstadoDetalleCajero.CERRADA);

        // Componente 1: efectivo esperado = baseCaja + totalEfectivo
        BigDecimal efectivoEsperado = detalle.getBaseCaja().add(detalle.getTotalEfectivo());

        // Componente 2: electrónico esperado = totalMediosElectronicos
        BigDecimal electronicoEsperado = detalle.getTotalMediosElectronicos();

        // Componente 3: diferencias
        detalle.setEfectivoDeclarado(efectivoDeclarado);
        detalle.setMediosElectronicosDeclarado(mediosElectronicosDeclarado);
        detalle.setDiferenciaEfectivo(efectivoDeclarado.subtract(efectivoEsperado));
        detalle.setDiferenciaMediosElectronicos(mediosElectronicosDeclarado.subtract(electronicoEsperado));

        // Monto final = todo lo que debería haber en caja
        detalle.setMontoFinal(efectivoEsperado.add(electronicoEsperado));

        return detalleCajeroRepository.save(detalle);
    }

    /**
     * Cierre simple sin declaración (para logout automático / medianoche).
     */
    @Transactional
    public void cerrarSesionCajeroSimple(Integer cajeroId) {
        List<DetalleCajero> sesionesAbiertas = detalleCajeroRepository
                .findByCajero_CajeroIdAndEstado(cajeroId, DetalleCajero.EstadoDetalleCajero.ABIERTA);

        for (DetalleCajero detalle : sesionesAbiertas) {
            detalle.setFechaCierre(LocalDateTime.now(ZONA_BOGOTA));
            detalle.setEstado(DetalleCajero.EstadoDetalleCajero.CERRADA);
            detalle.setMontoFinal(
                    detalle.getBaseCaja()
                            .add(detalle.getTotalEfectivo())
                            .add(detalle.getTotalMediosElectronicos())
            );
            detalleCajeroRepository.save(detalle);
        }
    }

    /**
     * Cierra una sesión de un día anterior cuando se detecta cambio de día.
     * La fecha de cierre se establece a las 23:59:59 del día en que se abrió.
     */
    @Transactional
    public void cerrarSesionPorCambioDeDia(DetalleCajero detalle) {
        LocalDate fechaOriginal = detalle.getFechaApertura().toLocalDate();
        LocalDateTime cierreFinDelDia = fechaOriginal.atTime(23, 59, 59);

        detalle.setFechaCierre(cierreFinDelDia);
        detalle.setEstado(DetalleCajero.EstadoDetalleCajero.CERRADA);
        detalle.setMontoFinal(
                detalle.getBaseCaja()
                        .add(detalle.getTotalEfectivo())
                        .add(detalle.getTotalMediosElectronicos())
        );
        detalleCajeroRepository.save(detalle);

        System.out.println("🔒 Sesión cerrada por cambio de día — detalleCajeroId: "
                + detalle.getDetalleCajeroId()
                + " cajeroId: " + detalle.getCajero().getCajeroId()
                + " fechaCierre: " + cierreFinDelDia);
    }

    /**
     * Método para la tarea programada de medianoche.
     * Cierra TODAS las sesiones abiertas y crea una nueva sesión para cada cajero.
     *
     * @return cantidad de sesiones cerradas y reabiertas
     */
    @Transactional
    public int cerrarYReabrirSesionesMedianoche() {
        List<DetalleCajero> sesionesAbiertas = detalleCajeroRepository
                .findByEstado(DetalleCajero.EstadoDetalleCajero.ABIERTA);

        int count = 0;
        for (DetalleCajero detalle : sesionesAbiertas) {
            // Cerrar la sesión del día anterior
            cerrarSesionPorCambioDeDia(detalle);

            // Abrir nueva sesión para el nuevo día (base caja = 0, sin comprobante)
            DetalleCajero nueva = new DetalleCajero();
            nueva.setCajero(detalle.getCajero());
            nueva.setMontoInicial(BigDecimal.ZERO);
            nueva.setBaseCaja(BigDecimal.ZERO);
            nueva.setMontoFinal(BigDecimal.ZERO);
            nueva.setFechaApertura(LocalDateTime.now(ZONA_BOGOTA));
            nueva.setEstado(DetalleCajero.EstadoDetalleCajero.ABIERTA);
            nueva.setConsecutivo(0);
            nueva.setTotalRecaudo(BigDecimal.ZERO);
            nueva.setTotalCosto(BigDecimal.ZERO);
            nueva.setTotalEfectivo(BigDecimal.ZERO);
            nueva.setTotalMediosElectronicos(BigDecimal.ZERO);
            nueva.setComprobante(detalle.getComprobante());
            detalleCajeroRepository.save(nueva);

            System.out.println("🔄 Sesión reabierta a medianoche — cajeroId: "
                    + detalle.getCajero().getCajeroId()
                    + " nuevaDetalleCajeroId: " + nueva.getDetalleCajeroId());
            count++;
        }
        return count;
    }

    // ============================================================
    // REGISTRO DE MOVIMIENTOS — 1 registro por transacción
    // ============================================================

    /**
     * Registra un movimiento individual de cualquiera de los 6 documentos POS.
     * Cada llamada = 1 fila en movimiento_cajero + actualiza totales de la sesión.
     *
     * Consecutivos:
     *   - consecutivo: global dentro de la sesión (1, 2, 3…)
     *   - consecutivoTipo: por tipo de documento (VENTA-1, VENTA-2, COTIZACION-1…)
     *
     * @param detalleCajeroId      ID de la sesión activa
     * @param tipoMovimiento       uno de los 6 documentos POS o auxiliares
     * @param numeroComprobante    número del documento (ej: VTA-001, COT-001)
     * @param referenciaDocumentoId ID del documento origen (venta.id, pedido.id, etc.)
     * @param montoTotal           monto total de la transacción
     * @param montoCosto           costo asociado
     * @param montoEfectivo        cuánto fue en efectivo
     * @param montoElectronico     cuánto fue en medios electrónicos
     * @param descripcion          texto descriptivo
     * @return el MovimientoCajero creado
     */
    @Transactional
    public MovimientoCajero registrarMovimiento(Long detalleCajeroId,
                                                MovimientoCajero.TipoMovimiento tipoMovimiento,
                                                String numeroComprobante,
                                                Long referenciaDocumentoId,
                                                BigDecimal montoTotal,
                                                BigDecimal montoCosto,
                                                BigDecimal montoEfectivo,
                                                BigDecimal montoElectronico,
                                                String descripcion) {
        DetalleCajero detalle = detalleCajeroRepository.findById(detalleCajeroId)
                .orElseThrow(() -> new RuntimeException("Sesión de cajero no encontrada: " + detalleCajeroId));

        if (detalle.getEstado() != DetalleCajero.EstadoDetalleCajero.ABIERTA) {
            throw new RuntimeException("La sesión de cajero está cerrada");
        }

        // Consecutivo global
        int siguienteGlobal = detalle.getConsecutivo() + 1;

        // Consecutivo por tipo de documento
        long countTipo = movimientoCajeroRepository
                .countByDetalleCajero_DetalleCajeroIdAndTipoMovimiento(detalleCajeroId, tipoMovimiento);
        int siguienteTipo = (int) countTipo + 1;

        // Crear registro individual
        MovimientoCajero mov = new MovimientoCajero();
        mov.setDetalleCajero(detalle);
        mov.setCajero(detalle.getCajero());
        mov.setTipoMovimiento(tipoMovimiento);
        mov.setNumeroComprobante(numeroComprobante);
        mov.setReferenciaDocumentoId(referenciaDocumentoId);
        mov.setConsecutivo(siguienteGlobal);
        mov.setConsecutivoTipo(siguienteTipo);
        mov.setMonto(montoTotal);
        mov.setCosto(montoCosto);
        mov.setMontoEfectivo(montoEfectivo);
        mov.setMontoElectronico(montoElectronico);
        mov.setDescripcion(descripcion);
        mov.setFechaMovimiento(LocalDateTime.now(ZONA_BOGOTA));

        MovimientoCajero guardado = movimientoCajeroRepository.save(mov);

        // Actualizar totales acumulados de la sesión
        detalle.setConsecutivo(siguienteGlobal);
        int signo = tipoMovimiento.getSigno();

        if (tipoMovimiento.isAfectaCaja()) {
            BigDecimal montoConSigno = montoTotal.multiply(BigDecimal.valueOf(signo));
            BigDecimal costoConSigno = montoCosto.multiply(BigDecimal.valueOf(signo));
            BigDecimal efectivoConSigno = montoEfectivo.multiply(BigDecimal.valueOf(signo));
            BigDecimal electronicoConSigno = montoElectronico.multiply(BigDecimal.valueOf(signo));

            detalle.setTotalRecaudo(detalle.getTotalRecaudo().add(montoConSigno));
            detalle.setTotalCosto(detalle.getTotalCosto().add(costoConSigno));
            detalle.setTotalEfectivo(detalle.getTotalEfectivo().add(efectivoConSigno));
            detalle.setTotalMediosElectronicos(detalle.getTotalMediosElectronicos().add(electronicoConSigno));
        }

        // Recalcular monto_final en tiempo real
        detalle.setMontoFinal(
                detalle.getBaseCaja()
                        .add(detalle.getTotalEfectivo())
                        .add(detalle.getTotalMediosElectronicos())
        );

        detalleCajeroRepository.save(detalle);
        return guardado;
    }

    // ============================================================
    // EGRESO MANUAL — salida de efectivo de caja
    // ============================================================

    /**
     * Registra una salida manual de efectivo (pago a proveedor, gastos, etc.).
     * Afecta el cuadre: resta del total efectivo de la sesión.
     *
     * @param detalleCajeroId ID de la sesión activa
     * @param monto           Monto a retirar
     * @param descripcion     Concepto del egreso
     */
    @Transactional
    public MovimientoCajero registrarEgreso(Long detalleCajeroId, BigDecimal monto, String descripcion) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto del egreso debe ser mayor a cero");
        }
        return registrarMovimiento(
                detalleCajeroId,
                MovimientoCajero.TipoMovimiento.EGRESO,
                "EGRESO-" + System.currentTimeMillis(),
                null,
                monto,
                BigDecimal.ZERO,
                monto,           // todo el egreso sale en efectivo
                BigDecimal.ZERO,
                descripcion
        );
    }

    // ============================================================
    // INGRESO EFECTIVO MANUAL — entrada de efectivo a caja
    // ============================================================

    /**
     * Registra una entrada manual de efectivo (cambio inicial, fondos, etc.).
     * Afecta el cuadre: suma al total efectivo de la sesión.
     *
     * @param detalleCajeroId ID de la sesión activa
     * @param monto           Monto a ingresar
     * @param descripcion     Concepto del ingreso
     */
    @Transactional
    public MovimientoCajero registrarIngresoEfectivo(Long detalleCajeroId, BigDecimal monto, String descripcion) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto del ingreso debe ser mayor a cero");
        }
        return registrarMovimiento(
                detalleCajeroId,
                MovimientoCajero.TipoMovimiento.INGRESO_EFECTIVO,
                "ING-" + System.currentTimeMillis(),
                null,
                monto,
                BigDecimal.ZERO,
                monto,           // todo el ingreso entra en efectivo
                BigDecimal.ZERO,
                descripcion
        );
    }

    // ============================================================
    // CUADRE DE CAJA — construye el DTO completo
    // ============================================================

    /**
     * Construye el cuadre de caja completo para una sesión:
     * - 3 componentes: efectivo, electrónico, diferencia
     * - Desglose de los 6 tipos de documento POS con suma/resta
     * - Lista de todos los movimientos individuales
     */
    @Transactional(readOnly = true)
    public CuadreCajaDTO construirCuadre(Long detalleCajeroId) {
        DetalleCajero sesion = detalleCajeroRepository.findById(detalleCajeroId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada: " + detalleCajeroId));

        List<MovimientoCajero> movimientos =
                movimientoCajeroRepository.findByDetalleCajero_DetalleCajeroIdOrderByConsecutivoAsc(detalleCajeroId);

        CuadreCajaDTO cuadre = new CuadreCajaDTO();

        // Datos de la sesión
        cuadre.setDetalleCajeroId(sesion.getDetalleCajeroId());
        cuadre.setCajeroId(sesion.getCajero().getCajeroId());
        cuadre.setCajeroNombre(sesion.getCajero().getNombre());
        cuadre.setFechaApertura(sesion.getFechaApertura());
        cuadre.setFechaCierre(sesion.getFechaCierre());
        cuadre.setEstado(sesion.getEstado().name());
        cuadre.setTotalTransacciones(sesion.getConsecutivo());

        // Componente 1: Efectivo
        BigDecimal efectivoEsperado = sesion.getBaseCaja().add(sesion.getTotalEfectivo());
        cuadre.setBaseCaja(sesion.getBaseCaja());
        cuadre.setTotalEfectivo(sesion.getTotalEfectivo());
        cuadre.setEfectivoEsperado(efectivoEsperado);
        cuadre.setEfectivoDeclarado(sesion.getEfectivoDeclarado());
        cuadre.setDiferenciaEfectivo(sesion.getDiferenciaEfectivo());

        // Componente 2: Medios electrónicos
        cuadre.setTotalMediosElectronicos(sesion.getTotalMediosElectronicos());
        cuadre.setMediosElectronicosDeclarado(sesion.getMediosElectronicosDeclarado());
        cuadre.setDiferenciaMediosElectronicos(sesion.getDiferenciaMediosElectronicos());

        // Componente 3: Totales
        cuadre.setTotalRecaudo(sesion.getTotalRecaudo());
        cuadre.setTotalCosto(sesion.getTotalCosto());
        cuadre.setMontoFinal(sesion.getMontoFinal());

        // Desglose por cada uno de los 6 tipos de documento POS
        List<ResumenTipoDocumento> desglose = new java.util.ArrayList<>();
        for (MovimientoCajero.TipoMovimiento tipo : MovimientoCajero.TipoMovimiento.values()) {
            List<MovimientoCajero> del_tipo = movimientos.stream()
                    .filter(m -> m.getTipoMovimiento() == tipo)
                    .collect(java.util.stream.Collectors.toList());

            BigDecimal totalMonto = del_tipo.stream()
                    .map(MovimientoCajero::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalEf = del_tipo.stream()
                    .map(MovimientoCajero::getMontoEfectivo)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalEl = del_tipo.stream()
                    .map(MovimientoCajero::getMontoElectronico)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            desglose.add(new ResumenTipoDocumento(
                    tipo.name(),
                    tipo.name().replace("_", " "),
                    del_tipo.size(),
                    totalMonto,
                    totalEf,
                    totalEl,
                    tipo.isAfectaCaja(),
                    tipo.getSigno()
            ));
        }
        cuadre.setDesglosePorTipo(desglose);

        return cuadre;
    }
}
