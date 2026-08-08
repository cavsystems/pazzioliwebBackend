package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.commonbacken.events.EmpresaCreadaEvent;
import com.pazzioliweb.comprobantesmodule.dtos.TipoComprobanteManualDTO;
import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Al crear una empresa nueva desde cavsystems (ver EmpresaCreadaEvent), siembra los
 * comprobantes contables básicos con un prefijo por defecto, para que la empresa pueda
 * facturar, comprar y mover inventario sin que el admin tenga que crearlos a mano
 * primero en Contabilidad → Comprobantes.
 *
 * Cada comprobante se crea de forma AISLADA (try/catch individual): un fallo puntual
 * (p.ej. a futuro, alguna validación del comprobante de ventas ligado a facturación
 * electrónica) no le impide al resto crearse ni afecta la empresa, que ya quedó
 * persistida antes de que se publique este evento.
 *
 * Se persiste directo por el repositorio (bypass de ComprobanteContableService.crear()):
 * esa validación exige al menos un cajero asignado para FC/RC/CE, y al momento de crear
 * la empresa todavía no existe ningún cajero (se crean después, atados a bodegas/usuarios).
 * Los comprobantes quedan activos igual; solo falta asignarles cajero para poder usarse
 * en Caja/Ventas — mismo patrón que AsignacionComprobanteService.obtenerOCrearLegacy(),
 * pero SIN marcarlos esLegacy (el admin sí debe poder editarlos).
 */
@Component
public class ComprobantesPorDefectoListener {

    private static final Logger log = LoggerFactory.getLogger(ComprobantesPorDefectoListener.class);

    private record ComprobanteDefault(TipoMovimientoComprobante tipo, String prefijo, String descripcion, boolean afectaInventario) {}

    /** afectaInventario en false para RC/CE: son documentos de caja, nunca tocan existencias. */
    private static final List<ComprobanteDefault> DEFAULTS = List.of(
            new ComprobanteDefault(TipoMovimientoComprobante.FC, "FC", "Ventas", true),
            new ComprobanteDefault(TipoMovimientoComprobante.CC, "CC", "Compras", true),
            new ComprobanteDefault(TipoMovimientoComprobante.EI, "EI", "Entradas de almacén", true),
            new ComprobanteDefault(TipoMovimientoComprobante.SI, "SI", "Salidas de almacén", true),
            new ComprobanteDefault(TipoMovimientoComprobante.RC, "RC", "Recibos de caja", false),
            new ComprobanteDefault(TipoMovimientoComprobante.CE, "CE", "Egresos", false)
    );

    private final ComprobanteContableRepository comprobanteRepo;
    private final TipoComprobanteManualService manualService;

    public ComprobantesPorDefectoListener(ComprobanteContableRepository comprobanteRepo,
                                           TipoComprobanteManualService manualService) {
        this.comprobanteRepo = comprobanteRepo;
        this.manualService = manualService;
    }

    // @Order(100): debe correr ANTES que CajeroPorDefectoListener (@Order(200), en
    // cajerosmodule), que necesita que FC/RC/CE ya existan para poder asignarles cajero.
    @EventListener
    @Order(100)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onEmpresaCreada(EmpresaCreadaEvent event) {
        if (event.getSchema() == null || event.getSchema().isBlank()) return;
        try {
            TenantContext.setCurrentTenant(event.getSchema());
            log.info("══════ Empresa creada ({}) → sembrando comprobantes contables por defecto ══════", event.getSchema());
            for (ComprobanteDefault def : DEFAULTS) {
                crearSiNoExiste(def);
            }
            crearNotasContablesSiNoExiste();
        } finally {
            TenantContext.clear();
        }
    }

    private void crearSiNoExiste(ComprobanteDefault def) {
        try {
            List<ComprobanteContable> existentes = comprobanteRepo.findByTipo(def.tipo());
            if (!existentes.isEmpty()) {
                log.info("Comprobante {} ya existe ({} registro(s)) — se omite", def.tipo(), existentes.size());
                return;
            }
            ComprobanteContable c = new ComprobanteContable();
            c.setTipoMovimiento(def.tipo());
            c.setPrefijo(def.prefijo());
            c.setDescripcion(def.descripcion());
            c.setSiguienteConsecutivo(1);
            c.setAfectaInventario(def.afectaInventario());
            c.setActivo(true);
            c.setEsLegacy(false);
            c.setFechaCreacion(LocalDateTime.now());
            comprobanteRepo.save(c);
            log.info("Comprobante por defecto creado: {} ({}) prefijo {}", def.tipo(), def.descripcion(), def.prefijo());
        } catch (Exception e) {
            log.error("No se pudo crear el comprobante por defecto {} ({}): {}",
                    def.tipo(), def.descripcion(), e.getMessage(), e);
        }
    }

    private void crearNotasContablesSiNoExiste() {
        try {
            TipoComprobanteManualDTO dto = new TipoComprobanteManualDTO();
            dto.setCodigo("NC");
            dto.setNombre("NOTAS CONTABLES");
            dto.setPrefijo("NC");
            dto.setSiguienteConsecutivo(1);
            manualService.crear(dto);
            log.info("Comprobante manual por defecto creado: NOTAS CONTABLES (NC)");
        } catch (Exception e) {
            log.error("No se pudo crear el comprobante manual por defecto NOTAS CONTABLES: {}", e.getMessage(), e);
        }
    }
}
