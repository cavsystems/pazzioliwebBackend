package com.pazzioliweb.facturacionmodule.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Datos que se envían al proveedor de facturación electrónica (API externo DIAN).
 * Esta estructura contiene TODA la información necesaria para que el proveedor
 * arme el XML UBL 2.1, firme y envíe a la DIAN.
 */
@Data
public class DianDocumentoRequestDTO {

    // ── Tipo de documento ──
    private String tipoDocumento;  // "01" = Factura, "91" = Nota Crédito, "92" = Nota Débito
    private String prefijo;
    private Integer consecutivo;
    private String resolucionDian;

    // ── Resolución DIAN del comprobante (debe coincidir con la asignada) ──
    /** Clave técnica DIAN específica del comprobante (no la global de DianConfig). */
    private String claveTecnicaDian;
    private LocalDate fechaInicioResolucion;
    private LocalDate fechaFinResolucion;
    private Integer consecutivoDesde;
    private Integer consecutivoHasta;

    // ── Fechas ──
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String formaPago;       // "1" = Contado, "2" = Crédito
    private Integer plazo;          // días

    // ── Emisor (tu empresa) ──
    private EmisorDTO emisor;

    // ── Receptor (cliente) ──
    private ReceptorDTO receptor;

    // ── Líneas de detalle ──
    private List<LineaDTO> lineas;

    // ── Totales ──
    private BigDecimal baseGravable;
    private BigDecimal totalIva;
    private BigDecimal totalIca;     // Impuesto de Industria y Comercio (CodImp2=04)
    private BigDecimal totalInc;     // Impuesto Nacional al Consumo (CodImp3=03)
    private BigDecimal totalDescuento;
    private BigDecimal totalFactura;

    // ── Métodos de pago ──
    private List<MetodoPagoDTO> metodosPago;

    // ── Referencia a documento original (solo para NC/ND, null para FC/TPOS) ──
    private DocumentoReferenciaDTO documentoReferencia;
    /** Código DIAN concepto NC: 1=Devolución, 2=Anulación, 3=Rebaja, 4=Descuento, 5=Otro
     *  Código DIAN concepto ND: 1=Intereses, 2=Gastos por cobrar, 3=Cambio del valor, 4=Otros */
    private Integer codigoConcepto;
    private String razonConcepto;

    @Data
    public static class EmisorDTO {
        private String tipoIdentificacion; // "31" = NIT
        private String numeroIdentificacion;
        private String digitoVerificacion;
        private String razonSocial;
        private String direccion;
        private String municipio;
        private String departamento;
        private String pais;
        private String telefono;
        private String correo;
        // ── Datos fiscales DIAN (obligatorios en UBL TaxLevelCode) ──
        /** Responsabilidad fiscal: códigos DIAN separados por ";" (ej. "O-13;O-15"). */
        private String responsabilidadFiscal;
        /** PERSONA_NATURAL / PERSONA_JURIDICA */
        private String tipoContribuyente;
        private Boolean granContribuyente;
        private Boolean autorretenedor;
        private Boolean responsableIva;
        /** Código del régimen tributario (TaxScheme): "01"=IVA, "ZZ"=No aplica. */
        private String codigoRegimen;
    }

    @Data
    public static class ReceptorDTO {
        private String tipoIdentificacion; // "13" = CC, "31" = NIT
        private String numeroIdentificacion;
        private String digitoVerificacion;
        private String nombre;
        private String direccion;
        private String municipio;
        private String departamento;
        private String correo;
        private String telefono;
        /** Responsabilidad fiscal del receptor (códigos DIAN). "R-99-PN" si no aplica. */
        private String responsabilidadFiscal;
        private String tipoContribuyente;
        private Boolean responsableIva;
    }

    @Data
    public static class LineaDTO {
        private Integer numero;           // línea 1, 2, 3...
        private String codigoProducto;
        private String descripcion;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal porcentajeIva;  // 0, 5, 19
        private BigDecimal valorIva;
        private BigDecimal totalLinea;
    }

    @Data
    public static class MetodoPagoDTO {
        private String medioPago;    // "10" = Efectivo, "49" = Tarjeta débito, "48" = Tarjeta crédito, "ZZZ" = Otro
        private BigDecimal monto;
        private String referencia;
    }

    /** Referencia al documento original que se está corrigiendo (NC/ND). */
    @Data
    public static class DocumentoReferenciaDTO {
        private String numeroDocumento;    // Ej: FE-100
        private String cufeOriginal;       // CUFE de la factura original
        private LocalDate fechaEmisionOriginal;
        private String tipoDocumentoOriginal; // "01" para factura
    }
}

