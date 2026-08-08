package com.pazzioliweb.cajerosmodule.service;

import com.pazzioliweb.cajerosmodule.entity.Cajero;
import com.pazzioliweb.cajerosmodule.repositori.CajeroRepository;
import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.commonbacken.events.EmpresaCreadaEvent;
import com.pazzioliweb.comprobantesmodule.entity.ComprobanteContable;
import com.pazzioliweb.comprobantesmodule.enums.TipoMovimientoComprobante;
import com.pazzioliweb.comprobantesmodule.repositori.ComprobanteContableRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Al crear una empresa nueva desde cavsystems (ver EmpresaCreadaEvent), crea un
 * cajero por defecto asociado al usuario admin recién creado y se lo asigna a los
 * comprobantes que lo requieren (FC/RC/CE — ver TipoMovimientoComprobante.requiereCajero()),
 * para que ese usuario pueda vender/recibir pagos/hacer egresos sin que el admin
 * tenga que crear el cajero a mano primero (Caja → Cajeros) ni asignárselo a cada
 * comprobante uno por uno.
 *
 * @Order(200): debe correr DESPUÉS de ComprobantesPorDefectoListener (@Order(100),
 * en comprobantesmodule) porque necesita que FC/RC/CE ya existan para poder
 * agregarles el cajero. Spring no garantiza orden entre listeners del mismo evento
 * salvo que se declare explícitamente.
 *
 * Igual que ComprobantesPorDefectoListener, se persiste directo por el repositorio
 * de comprobantes (no vía ComprobanteContableService.actualizar()) para no disparar
 * las validaciones pensadas para el CRUD manual del admin (prefijo único, cajero sin
 * otro comprobante activo del mismo tipo, etc. — no aplican a este sembrado inicial).
 */
@Component
public class CajeroPorDefectoListener {

    private static final Logger log = LoggerFactory.getLogger(CajeroPorDefectoListener.class);

    /** Tipos de comprobante que exigen cajero (TipoMovimientoComprobante.requiereCajero()
     *  para FC/VC/RC/CE/DV) y que sembramos por defecto — VC y DV no se crean por defecto. */
    private static final List<TipoMovimientoComprobante> TIPOS_A_ASIGNAR = List.of(
            TipoMovimientoComprobante.FC,
            TipoMovimientoComprobante.RC,
            TipoMovimientoComprobante.CE
    );

    private final UsuarioRepository usuarioRepository;
    private final CajeroRepository cajeroRepository;
    private final ComprobanteContableRepository comprobanteRepository;

    public CajeroPorDefectoListener(UsuarioRepository usuarioRepository,
                                     CajeroRepository cajeroRepository,
                                     ComprobanteContableRepository comprobanteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cajeroRepository = cajeroRepository;
        this.comprobanteRepository = comprobanteRepository;
    }

    @EventListener
    @Order(200)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onEmpresaCreada(EmpresaCreadaEvent event) {
        if (event.getSchema() == null || event.getSchema().isBlank()) return;
        if (event.getUsuarioIdAdmin() == null) {
            log.info("Empresa creada ({}) sin usuario admin identificado — se omite el cajero por defecto", event.getSchema());
            return;
        }
        try {
            TenantContext.setCurrentTenant(event.getSchema());
            log.info("══════ Empresa creada ({}) → creando cajero por defecto para el usuario {} ══════",
                    event.getSchema(), event.getUsuarioIdAdmin());
            Cajero cajero = crearCajeroSiNoExiste(event.getUsuarioIdAdmin());
            if (cajero != null) {
                asignarCajeroAComprobantes(cajero.getCajeroId());
            }
        } catch (Exception e) {
            log.error("No se pudo crear el cajero por defecto de la empresa {}: {}",
                    event.getSchema(), e.getMessage(), e);
        } finally {
            TenantContext.clear();
        }
    }

    private Cajero crearCajeroSiNoExiste(Integer usuarioId) {
        try {
            if (cajeroRepository.findByUsuario_Codigo(usuarioId).isPresent()) {
                log.info("El usuario {} ya tiene un cajero asignado — se omite", usuarioId);
                return null;
            }
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            if (usuario == null) {
                log.warn("No se encontró el usuario {} para crear su cajero por defecto", usuarioId);
                return null;
            }
            Cajero cajero = new Cajero();
            cajero.setUsuario(usuario);
            cajero.setNombre("Caja principal");
            cajero.setEstado(Cajero.EstadoCajero.ACTIVO);
            cajero.setCodigoUsuarioCreo(0);
            cajero.setFechacreado(LocalDate.now());
            cajero = cajeroRepository.save(cajero);
            log.info("Cajero por defecto creado (id {}) para el usuario {}", cajero.getCajeroId(), usuarioId);
            return cajero;
        } catch (Exception e) {
            log.error("No se pudo crear el cajero por defecto para el usuario {}: {}", usuarioId, e.getMessage(), e);
            return null;
        }
    }

    private void asignarCajeroAComprobantes(Integer cajeroId) {
        for (TipoMovimientoComprobante tipo : TIPOS_A_ASIGNAR) {
            try {
                List<ComprobanteContable> comprobantes = comprobanteRepository.findByTipo(tipo);
                for (ComprobanteContable c : comprobantes) {
                    if (c.getCajeroIds() == null) continue;
                    if (c.getCajeroIds().add(cajeroId)) {
                        comprobanteRepository.save(c);
                        log.info("Cajero {} asignado al comprobante {} ({})", cajeroId, tipo, c.getPrefijo());
                    }
                }
            } catch (Exception e) {
                log.error("No se pudo asignar el cajero {} al comprobante {}: {}", cajeroId, tipo, e.getMessage(), e);
            }
        }
    }
}
