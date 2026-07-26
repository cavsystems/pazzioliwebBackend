package com.pazzioliweb.commonbacken.controller;

import com.pazzioliweb.commonbacken.services.WhatsappService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Dice si el envío automático por WhatsApp está configurado.
 *
 * El frontend lo consulta AL MONTAR el bloque de envío, no al hacer clic. Es a propósito: si no
 * está configurado, el clic tiene que abrir WhatsApp de forma sincrónica dentro del gesto del
 * usuario, porque un window.open después de un await lo bloquea el navegador como popup.
 */
@RestController
@RequestMapping("/api/whatsapp")
public class WhatsappConfigController {

    @Autowired
    private WhatsappService whatsappService;

    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> estado() {
        return ResponseEntity.ok(Map.of("configurado", whatsappService.isConfigurado()));
    }
}
