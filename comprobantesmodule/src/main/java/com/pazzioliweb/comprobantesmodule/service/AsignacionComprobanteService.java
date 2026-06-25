package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.commonbacken.events.ConsecutivoIncrementadoEvent;
import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Servicio compartido que asigna un comprobante (prefijo + consecutivo)
 * a cualquier movimiento del sistema. Bloquea la fila para evitar carreras.
 *
 * Uso desde los servicios de Venta / Compra / RC / CE / Devolución:
 * <pre>
 *   AsignacionComprobanteService.Resultado r =
 *       asignacionService.asignar(cajeroId, TipoMovimientoComprobante.FC);
 *   venta.setComprobante(r.getComprobante());
 *   venta.setNumeroVenta(r.getNumeroDocumento());
 * </pre>
 */
@Service
public class AsignacionComprobanteService {

    private static final Logger log = LoggerFactory.getLogger(AsignacionComprobanteService.class);

    /** Tipos de comprobante DIAN que requieren resolución vigente para emitirse legalmente. */
    private static final java.util.Set<TipoMovimientoComprobante> TIPOS_DIAN =
            java.util.EnumSet.of(
                    TipoMovimientoComprobante.FC,
                    TipoMovimientoComprobante.VC,
                    TipoMovimientoComprobante.NC,
                    TipoMovimientoComprobante.ND,
                    TipoMovimientoComprobante.TPOS,
                    TipoMovimientoComprobante.DS
            );

    private final ComprobanteContableRepository repo;
    private final ApplicationEventPublisher eventPublisher;

    public AsignacionComprobanteService(ComprobanteContableRepository repo,
                                         ApplicationEventPublisher eventPublisher) {
        this.repo = repo;
        this.eventPublisher = eventPublisher;
    }

    /** Excepción específica cuando la resolución DIAN está vencida o se agotó el rango. */
    public static class ResolucionDianInvalidaException extends RuntimeException {
        public ResolucionDianInvalidaException(String msg) { super(msg); }
    }

    /**
     * Busca el comprobante del cajero para el tipo dado, reserva el siguiente
     * consecutivo y devuelve el número formateado.
     *
     * @throws ComprobanteNoConfiguradoException si el cajero no tiene comprobante para ese tipo.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Resultado asignar(Integer cajeroId, TipoMovimientoComprobante tipo) {
        if (cajeroId == null) {
            throw new ComprobanteNoConfiguradoException(
                "Se requiere un cajero para asignar comprobante de " + tipo.getDescripcion()
            );
        }
        ComprobanteContable c = repo.findActivoByCajeroAndTipoForUpdate(cajeroId, tipo)
                .orElseThrow(() -> new ComprobanteNoConfiguradoException(
                    "El cajero " + cajeroId + " no tiene un comprobante de tipo " + tipo.name() +
                    " (" + tipo.getDescripcion() + ") configurado. Configúrelo antes de operar."
                ));

        int consecutivo = c.getSiguienteConsecutivo();

        // ── Validar resolución DIAN (solo para tipos que la requieren) ──
        validarResolucionDian(c, consecutivo);

        c.setSiguienteConsecutivo(consecutivo + 1);
        repo.save(c);

        // Publicar evento de WebSocket para notificar incremento de consecutivo
        eventPublisher.publishEvent(new ConsecutivoIncrementadoEvent(
            this, c.getId(), tipo.name(), consecutivo + 1
        ));

        String numero = c.getPrefijo() + "-" + consecutivo;
        return new Resultado(c, numero, consecutivo);
    }

    /**
     * Valida que el comprobante DIAN tenga resolución vigente y consecutivo en rango.
     * Si la resolución existe, exige fechas de vigencia y rango de consecutivo válidos.
     * Si no existe resolución y el tipo la requiere, bloquea con mensaje claro.
     *
     * Emite WARN en logs cuando faltan ≤30 días para que venza o ≤500 consecutivos
     * para que se agote el rango. El frontend puede leer estos warnings vía un
     * endpoint de auditoría para alertar al admin.
     */
    private void validarResolucionDian(ComprobanteContable c, int consecutivoAUsar) {
        if (c.getEsLegacy() != null && c.getEsLegacy()) return; // LEGACY no requieren resolución
        if (!TIPOS_DIAN.contains(c.getTipoMovimiento())) return; // Solo aplica a tipos DIAN

        boolean tieneResolucion = c.getResolucionDian() != null && !c.getResolucionDian().isBlank();

        if (!tieneResolucion) {
            throw new ResolucionDianInvalidaException(
                "El comprobante '" + c.getPrefijo() + "' (" + c.getTipoMovimiento().name() + ") " +
                "no tiene resolución DIAN configurada. Configure la resolución antes de emitir documentos electrónicos."
            );
        }

        LocalDate hoy = LocalDate.now();

        // Validar vigencia
        if (c.getFechaInicioResolucion() != null && hoy.isBefore(c.getFechaInicioResolucion())) {
            throw new ResolucionDianInvalidaException(
                "La resolución DIAN " + c.getResolucionDian() + " del comprobante '" + c.getPrefijo() +
                "' inicia vigencia el " + c.getFechaInicioResolucion() + ". No se pueden emitir documentos antes de esa fecha."
            );
        }
        if (c.getFechaFinResolucion() != null && hoy.isAfter(c.getFechaFinResolucion())) {
            throw new ResolucionDianInvalidaException(
                "La resolución DIAN " + c.getResolucionDian() + " del comprobante '" + c.getPrefijo() +
                "' venció el " + c.getFechaFinResolucion() + ". Solicite nueva resolución a la DIAN."
            );
        }

        // Validar rango de consecutivo
        if (c.getConsecutivoDesde() != null && consecutivoAUsar < c.getConsecutivoDesde()) {
            throw new ResolucionDianInvalidaException(
                "El consecutivo " + consecutivoAUsar + " es inferior al inicio autorizado (" +
                c.getConsecutivoDesde() + ") por la resolución DIAN " + c.getResolucionDian() + "."
            );
        }
        if (c.getConsecutivoHasta() != null && consecutivoAUsar > c.getConsecutivoHasta()) {
            throw new ResolucionDianInvalidaException(
                "El consecutivo " + consecutivoAUsar + " supera el máximo autorizado (" +
                c.getConsecutivoHasta() + ") por la resolución DIAN " + c.getResolucionDian() +
                ". Solicite ampliación o nueva resolución."
            );
        }

        // Alertas por proximidad (no bloquean, solo log)
        if (c.getFechaFinResolucion() != null) {
            long diasParaVencer = ChronoUnit.DAYS.between(hoy, c.getFechaFinResolucion());
            if (diasParaVencer <= 30 && diasParaVencer >= 0) {
                log.warn("[ResolucionDIAN] La resolución {} del comprobante '{}' vence en {} día(s).",
                        c.getResolucionDian(), c.getPrefijo(), diasParaVencer);
            }
        }
        if (c.getConsecutivoHasta() != null) {
            int restantes = c.getConsecutivoHasta() - consecutivoAUsar;
            if (restantes <= 500 && restantes >= 0) {
                log.warn("[ResolucionDIAN] Al comprobante '{}' le quedan {} consecutivos antes de agotar el rango ({}).",
                        c.getPrefijo(), restantes, c.getConsecutivoHasta());
            }
        }
    }

    /**
     * Inspección no destructiva de la salud de la resolución DIAN de un comprobante.
     * Útil para que el frontend muestre alertas tipo "vence en X días" sin tener
     * que emitir un documento. Retorna null si no aplica/no tiene resolución.
     */
    @Transactional(readOnly = true)
    public AlertaResolucion inspeccionarResolucion(Long comprobanteId) {
        ComprobanteContable c = repo.findById(comprobanteId).orElse(null);
        if (c == null) return null;
        if (c.getEsLegacy() != null && c.getEsLegacy()) return null;
        if (!TIPOS_DIAN.contains(c.getTipoMovimiento())) return null;
        AlertaResolucion a = new AlertaResolucion();
        a.comprobanteId = c.getId();
        a.prefijo = c.getPrefijo();
        a.resolucionDian = c.getResolucionDian();
        a.fechaFin = c.getFechaFinResolucion();
        a.consecutivoHasta = c.getConsecutivoHasta();
        a.siguienteConsecutivo = c.getSiguienteConsecutivo();
        LocalDate hoy = LocalDate.now();
        if (c.getFechaFinResolucion() != null) {
            a.diasParaVencer = ChronoUnit.DAYS.between(hoy, c.getFechaFinResolucion());
            a.vencida = a.diasParaVencer < 0;
        }
        if (c.getConsecutivoHasta() != null && c.getSiguienteConsecutivo() != null) {
            a.consecutivosRestantes = c.getConsecutivoHasta() - c.getSiguienteConsecutivo();
            a.rangoAgotado = a.consecutivosRestantes < 0;
        }
        return a;
    }

    /** Snapshot de salud de una resolución DIAN para alertas del frontend. */
    public static class AlertaResolucion {
        public Long comprobanteId;
        public String prefijo;
        public String resolucionDian;
        public LocalDate fechaFin;
        public Long diasParaVencer;
        public Integer consecutivoHasta;
        public Integer siguienteConsecutivo;
        public Integer consecutivosRestantes;
        public Boolean vencida = false;
        public Boolean rangoAgotado = false;
    }

    /**
     * Asignación SIN cajero para tipos auto-generados (DS, etc.). Busca el primer
     * comprobante activo del tipo y avanza el consecutivo. Si no hay ninguno
     * configurado, falla con mensaje claro.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Resultado asignarSinCajero(TipoMovimientoComprobante tipo) {
        ComprobanteContable c = repo.findAll().stream()
                .filter(x -> tipo.equals(x.getTipoMovimiento())
                          && Boolean.TRUE.equals(x.getActivo())
                          && !Boolean.TRUE.equals(x.getEsLegacy()))
                .findFirst()
                .orElseThrow(() -> new ComprobanteNoConfiguradoException(
                        "No hay comprobante " + tipo.name() + " (" + tipo.getDescripcion() +
                        ") configurado y activo. Configure uno antes de operar."));

        int consecutivo = c.getSiguienteConsecutivo();
        validarResolucionDian(c, consecutivo);
        c.setSiguienteConsecutivo(consecutivo + 1);
        repo.save(c);
        return new Resultado(c, c.getPrefijo() + "-" + consecutivo, consecutivo);
    }

    /** Devuelve el comprobante LEGACY del tipo (lo crea si no existe). */
    @Transactional
    public ComprobanteContable obtenerOCrearLegacy(TipoMovimientoComprobante tipo) {
        return repo.findLegacyByTipo(tipo).orElseGet(() -> {
            ComprobanteContable legacy = new ComprobanteContable();
            // Los LEGACY no tienen cajeros asignados (cajeroIds queda vacío por defecto)
            legacy.setTipoMovimiento(tipo);
            legacy.setPrefijo("LEGACY-" + tipo.name());
            legacy.setDescripcion("Comprobante LEGACY para registros previos a la migración");
            legacy.setSiguienteConsecutivo(1);
            legacy.setActivo(true);
            legacy.setEsLegacy(true);
            legacy.setAfectaInventario(false);
            return repo.save(legacy);
        });
    }

    /**
     * Resultado de la asignación: el comprobante encontrado, el número del
     * documento ya formateado (PREFIJO-N) y el consecutivo usado.
     */
    public static class Resultado {
        private final ComprobanteContable comprobante;
        private final String numeroDocumento;
        private final Integer consecutivo;

        public Resultado(ComprobanteContable comprobante, String numeroDocumento, Integer consecutivo) {
            this.comprobante = comprobante;
            this.numeroDocumento = numeroDocumento;
            this.consecutivo = consecutivo;
        }
        public ComprobanteContable getComprobante() { return comprobante; }
        public String getNumeroDocumento() { return numeroDocumento; }
        public Integer getConsecutivo() { return consecutivo; }
    }

    /** Excepción específica para bloquear movimientos sin comprobante. */
    public static class ComprobanteNoConfiguradoException extends RuntimeException {
        public ComprobanteNoConfiguradoException(String msg) { super(msg); }
    }
}
