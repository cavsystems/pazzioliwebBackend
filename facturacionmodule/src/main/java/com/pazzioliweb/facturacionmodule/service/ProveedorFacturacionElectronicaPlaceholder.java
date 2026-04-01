package com.pazzioliweb.facturacionmodule.service;

import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoRequestDTO;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ══════════════════════════════════════════════════════════════
 *  IMPLEMENTACIÓN PLACEHOLDER - REEMPLAZAR CON EL API REAL
 * ══════════════════════════════════════════════════════════════
 * 
 * Esta implementación simula la respuesta del proveedor de
 * facturación electrónica. Cuando se tenga el API real:
 * 
 * 1. Crear una nueva clase (ej: ProveedorDianApiRealImpl)
 * 2. Implementar ProveedorFacturacionElectronica
 * 3. Anotar con @Service y @Primary
 * 4. Llamar al API real dentro de enviarFactura()
 * 5. Esta clase placeholder se puede eliminar o dejar sin @Service
 * 
 * Ejemplo:
 *   @Service
 *   @Primary
 *   public class ProveedorDianApiRealImpl implements ProveedorFacturacionElectronica {
 *       // ... llamar al API externo aquí
 *   }
 * ══════════════════════════════════════════════════════════════
 */
@Service
public class ProveedorFacturacionElectronicaPlaceholder implements ProveedorFacturacionElectronica {

    @Override
    public DianDocumentoResponseDTO enviarFactura(DianDocumentoRequestDTO request) {
        // ── Simulación de respuesta exitosa ──
        DianDocumentoResponseDTO response = new DianDocumentoResponseDTO();
        response.setExitoso(true);
        response.setCufe("CUFE-PLACEHOLDER-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase());
        response.setEstadoDian("AUTORIZADA");
        response.setMensajeDian("Documento autorizado por la DIAN (PLACEHOLDER - NO REAL)");
        response.setQrData("https://catalogo-vpfe.dian.gov.co/document/searchqr?documentkey=" + response.getCufe());
        response.setXmlFirmado(null); // El API real retornará el XML firmado
        response.setPdfBase64(null);  // El API real retornará el PDF en base64
        response.setFechaValidacion(LocalDateTime.now());
        return response;
    }

    @Override
    public DianDocumentoResponseDTO consultarEstado(String cufe) {
        // ── Simulación de consulta ──
        DianDocumentoResponseDTO response = new DianDocumentoResponseDTO();
        response.setExitoso(true);
        response.setCufe(cufe);
        response.setEstadoDian("AUTORIZADA");
        response.setMensajeDian("Documento previamente autorizado (PLACEHOLDER - NO REAL)");
        response.setFechaValidacion(LocalDateTime.now());
        return response;
    }
}

