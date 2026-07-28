package com.pazzioliweb.facturacionmodule.service;

import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoRequestDTO;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoResponseDTO;

/**
 * Contrato para el proveedor de facturación electrónica.
 * 
 * ──────────────────────────────────────────────────────────
 *  AQUÍ SE CONECTA EL API EXTERNO DE FACTURACIÓN DIAN.
 *  
 *  Cuando la empresa entregue el API, solo se debe:
 *  1. Crear una nueva clase que implemente esta interfaz
 *  2. Anotar con @Service y @Primary para reemplazar el placeholder
 *  3. Implementar enviarFactura() llamando al API real
 *  4. Implementar consultarEstado() si el API lo soporta
 * ──────────────────────────────────────────────────────────
 */
public interface ProveedorFacturacionElectronica {

    /**
     * Envía un documento (factura/nota) al proveedor de facturación electrónica
     * para que lo firme, genere el XML UBL 2.1 y lo envíe a la DIAN.
     *
     * @param request datos completos del documento
     * @return respuesta con CUFE, estado, XML firmado, PDF
     */
    DianDocumentoResponseDTO enviarFactura(DianDocumentoRequestDTO request);

    /**
     * Consulta el estado de un documento previamente enviado.
     *
     * @param cufe Código Único de Factura Electrónica
     * @return respuesta actualizada con el estado DIAN
     */
    DianDocumentoResponseDTO consultarEstado(String cufe);

    /**
     * Consulta el estado de un documento por prefijo + folio (consecutivo).
     * Los proveedores tecnológicos como Facturatech identifican el documento
     * por su numeración, no por CUFE. La implementación por defecto no la soporta.
     *
     * @param prefijo prefijo del comprobante (ej. "FE")
     * @param folio   consecutivo del documento
     * @return respuesta actualizada con estado, CUFE, XML, PDF y QR si ya está firmado
     */
    default DianDocumentoResponseDTO consultarEstadoDocumento(String prefijo, Integer folio) {
        DianDocumentoResponseDTO response = new DianDocumentoResponseDTO();
        response.setExitoso(false);
        response.setMensajeDian("Consulta por prefijo+folio no soportada por este proveedor");
        return response;
    }
}

