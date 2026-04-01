package com.pazzioliweb.facturacionmodule.dtos;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Respuesta que devuelve el proveedor de facturación electrónica
 * después de enviar el documento a la DIAN.
 */
@Data
public class DianDocumentoResponseDTO {
    private boolean exitoso;            // true si la DIAN autorizó
    private String cufe;                // Código Único de Factura Electrónica
    private String estadoDian;          // AUTORIZADA, RECHAZADA
    private String mensajeDian;         // Descripción de la respuesta
    private String qrData;             // Datos para generar QR
    private String xmlFirmado;          // XML UBL 2.1 firmado (base64 o texto)
    private String pdfBase64;           // PDF de la representación gráfica en base64
    private LocalDateTime fechaValidacion;
}

