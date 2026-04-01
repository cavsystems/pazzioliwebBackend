package com.pazzioliweb.facturacionmodule.dtos;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO que retorna el backend después de generar/enviar la factura electrónica.
 */
@Data
public class FacturaElectronicaResponseDTO {
    private Integer facturaId;
    private String numeroFactura;     // Prefijo + consecutivo (ej: FE154)
    private String cufe;              // Código Único de Factura Electrónica
    private String estadoDian;        // PENDIENTE, ENVIADA, AUTORIZADA, RECHAZADA
    private String mensajeDian;       // Mensaje de respuesta de la DIAN
    private LocalDateTime fechaValidacion;
    private Double totalFactura;
    private String qrData;            // Datos del QR
    private boolean tienePdf;         // Si hay PDF disponible
}

