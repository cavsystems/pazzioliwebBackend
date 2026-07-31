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
    /** "01" Factura · "91" Nota Crédito · "92" Nota Débito · "20" Tiquete POS ·
     *  "05" Documento Soporte · "95" Nota de Ajuste al Documento Soporte. */
    private String tipoDocumento;
    private String prefijo;
    private Integer consecutivo;
    private String resolucionDian;

    /** Tipo de operación (ENC_21, Tabla 38). En DS: "10" Residente, "11" No Residente. */
    private String tipoOperacion;

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

    // ══════════════════════════════════════════════════════════
    //  Documento Soporte (tipo "05") y Nota de Ajuste ("95")
    //  Anexo simplificado DS de Facturatech / Resolución 000167 de 2021.
    // ══════════════════════════════════════════════════════════

    /** IBS_1: fecha de la compra al no obligado a facturar. Si es null se usa fechaEmision. */
    private LocalDate fechaCompra;

    /** IBS_2 (Tabla 41): 1 = Por operación, 2 = Acumulado semanal. Default 1. */
    private Integer formaGeneracionTransmision;

    /** Retención en la fuente (renta) aplicada — se reporta como TIM/IMP con código 06. */
    private BigDecimal retencionFuente;

    /** Retención de IVA aplicada — se reporta como TIM/IMP con código 05. */
    private BigDecimal retencionIva;

    /** ReteICA aplicada — TIM/IMP código 07 (solo factura; el anexo DS no la contempla). */
    private BigDecimal retencionIca;

    /** CDN_1: sección del Documento Soporte original que se corrige (nota de ajuste). Default 1. */
    private Integer seccionCorregida;

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
        private String nombreComercial;
        private String direccion;
        private String municipio;
        private String departamento;
        /** Código DANE del departamento (2 dígitos, ej. "05"). Tabla 34 Facturatech. */
        private String codigoDepartamento;
        /** Código DANE del municipio (5 dígitos, ej. "05001"). Tabla 35 Facturatech. */
        private String codigoMunicipio;
        /** Código postal (Tabla 39 Facturatech). */
        private String codigoPostal;
        /** Matrícula mercantil (nodo ICC_1 Facturatech, persona jurídica). */
        private String matriculaMercantil;
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
        /** Nombres de pila (persona natural) — Facturatech los exige en ADQ_4 (regla FAK20). */
        private String nombres;
        /** Apellidos (persona natural) — Facturatech los exige en ADQ_5 (regla FAK20). */
        private String apellidos;
        private String direccion;
        private String municipio;
        private String departamento;
        /** Código DANE del departamento (2 dígitos). Tabla 34 Facturatech. */
        private String codigoDepartamento;
        /** Código DANE del municipio (5 dígitos). Tabla 35 Facturatech. */
        private String codigoMunicipio;
        /** Código postal (Tabla 39 Facturatech). */
        private String codigoPostal;
        private String correo;
        private String telefono;
        /** Código país alfa-2 (Tabla 1). "CO" por defecto. Usado en PRO_15 del Documento Soporte. */
        private String codigoPais;
        /** Nombre del país (Tabla 1). "COLOMBIA" por defecto. Usado en PRO_21 del Documento Soporte. */
        private String nombrePais;
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

