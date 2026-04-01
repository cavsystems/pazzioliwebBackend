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
    private BigDecimal totalDescuento;
    private BigDecimal totalFactura;

    // ── Métodos de pago ──
    private List<MetodoPagoDTO> metodosPago;

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
}

