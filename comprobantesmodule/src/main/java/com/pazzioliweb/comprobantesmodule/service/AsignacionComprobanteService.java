package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    private final ComprobanteContableRepository repo;

    public AsignacionComprobanteService(ComprobanteContableRepository repo) {
        this.repo = repo;
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
        c.setSiguienteConsecutivo(consecutivo + 1);
        repo.save(c);

        String numero = c.getPrefijo() + "-" + consecutivo;
        return new Resultado(c, numero, consecutivo);
    }

    /** Devuelve el comprobante LEGACY del tipo (lo crea si no existe). */
    @Transactional
    public ComprobanteContable obtenerOCrearLegacy(TipoMovimientoComprobante tipo) {
        return repo.findLegacyByTipo(tipo).orElseGet(() -> {
            ComprobanteContable legacy = new ComprobanteContable();
            legacy.setCajeroId(null);
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
