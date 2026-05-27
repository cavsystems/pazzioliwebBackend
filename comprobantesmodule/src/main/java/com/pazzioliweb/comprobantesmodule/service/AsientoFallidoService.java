package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.entity.AsientoFallido;
import com.pazzioliweb.comprobantesmodule.repositori.AsientoFallidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * Registra los intentos fallidos de generación de asiento contable.
 * Centraliza el manejo de errores: cuando un módulo de negocio (venta,
 * compra, etc.) intenta generar su asiento y falla por config o validación,
 * llama a {@link #registrar(...)} para dejar el rastro en BD.
 *
 * Diseño:
 *  - Usa REQUIRES_NEW para que el registro del fallo NO sea afectado por
 *    rollback de la transacción origen.
 *  - Loguea ERROR siempre (visible en app.log).
 */
@Service
public class AsientoFallidoService {

    private static final Logger log = LoggerFactory.getLogger(AsientoFallidoService.class);

    private final AsientoFallidoRepository repo;

    public AsientoFallidoService(AsientoFallidoRepository repo) {
        this.repo = repo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String modulo, String tipoDocumento, Long documentoId,
                          String numeroDocumento, String motivo, Throwable cause) {
        try {
            AsientoFallido f = new AsientoFallido();
            f.setModulo(modulo);
            f.setTipoDocumento(tipoDocumento);
            f.setDocumentoId(documentoId);
            f.setNumeroDocumento(numeroDocumento);
            f.setMotivo(motivo != null
                ? (motivo.length() > 500 ? motivo.substring(0, 500) : motivo)
                : "Sin descripción");
            if (cause != null) {
                StringWriter sw = new StringWriter();
                cause.printStackTrace(new PrintWriter(sw));
                String st = sw.toString();
                f.setStacktrace(st.length() > 4000 ? st.substring(0, 4000) : st);
            }
            f.setFechaIntento(LocalDateTime.now());
            f.setResuelto(false);
            repo.save(f);

            log.error("[AsientoFallido] {} #{} ({}): {}", modulo, documentoId,
                    numeroDocumento != null ? numeroDocumento : "?", motivo);
        } catch (Exception saveEx) {
            // Si el insert del registro falla también, mínimo dejamos rastro en log.
            log.error("[AsientoFallido] No se pudo persistir el registro de fallo. Modulo={} doc={} motivo={}",
                    modulo, documentoId, motivo, saveEx);
        }
    }
}
