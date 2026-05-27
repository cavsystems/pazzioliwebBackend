package com.pazzioliweb.facturacionmodule.service.dian;

import com.pazzioliweb.facturacionmodule.config.DianConfig;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Genera el CUFE (Código Único de Factura Electrónica) según
 * el Anexo Técnico 1.9 de la DIAN.
 *
 * Algoritmo SHA-384 sobre la cadena:
 *   NumFac + FecFac + HorFac + ValFac + CodImp1 + ValImp1 +
 *   CodImp2 + ValImp2 + CodImp3 + ValImp3 + ValTot +
 *   NitOFE + NumAdq + ClTec + TipoAmbiente
 *
 * Notas (corregido frente a versión anterior):
 *  - ValFac es la BASE GRAVABLE (subtotal SIN IVA), no el total.
 *  - ValTot es el total final de la factura (con IVA + impuestos).
 *  - Soporta ICA (CodImp2=04) e INC (CodImp3=03) cuando vienen distintos de 0.
 *
 * Para Nota Crédito (CUDE) y Nota Débito el algoritmo cambia y se usa
 * {@link #generarCude(...)} con el CUFE de la factura referenciada.
 */
@Component
public class CufeGenerator {

    private final DianConfig dianConfig;

    public CufeGenerator(DianConfig dianConfig) {
        this.dianConfig = dianConfig;
    }

    /**
     * Genera el CUFE para una factura electrónica (FC, FE, TPOS).
     *
     * @param valIca puede ser null o BigDecimal.ZERO si no aplica
     * @param valInc puede ser null o BigDecimal.ZERO si no aplica
     */
    public String generarCufe(String numeroFactura, LocalDate fechaEmision, String horaEmision,
                              BigDecimal totalFactura, BigDecimal baseGravable, BigDecimal totalIva,
                              BigDecimal valIca, BigDecimal valInc,
                              String nitEmisor, String identificacionReceptor) {
        return generarCufe(numeroFactura, fechaEmision, horaEmision,
                totalFactura, baseGravable, totalIva, valIca, valInc,
                nitEmisor, identificacionReceptor, null);
    }

    /**
     * Variante que recibe la clave técnica DIAN explícitamente (del comprobante
     * usado para emitir el documento). Si es null, se usa la global de DianConfig
     * como fallback. La DIAN exige que la ClTec coincida exactamente con la
     * que emitió en la resolución autorizada — usar la global cuando hay
     * varias resoluciones invalida el CUFE.
     */
    public String generarCufe(String numeroFactura, LocalDate fechaEmision, String horaEmision,
                              BigDecimal totalFactura, BigDecimal baseGravable, BigDecimal totalIva,
                              BigDecimal valIca, BigDecimal valInc,
                              String nitEmisor, String identificacionReceptor,
                              String claveTecnica) {

        String fechaStr = fechaEmision.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String clTec = (claveTecnica != null && !claveTecnica.isBlank())
                ? claveTecnica
                : (dianConfig.getClaveTecnica() != null ? dianConfig.getClaveTecnica() : "");

        StringBuilder sb = new StringBuilder();
        sb.append(numeroFactura);                                            // NumFac
        sb.append(fechaStr);                                                 // FecFac
        sb.append(horaEmision != null ? horaEmision : "00:00:00-05:00");     // HorFac
        sb.append(formatDecimal(baseGravable));                              // ValFac = base gravable (SIN IVA)
        sb.append("01");                                                     // CodImp1 = IVA
        sb.append(formatDecimal(totalIva));                                  // ValImp1
        sb.append("04");                                                     // CodImp2 = ICA
        sb.append(formatDecimal(valIca));                                    // ValImp2
        sb.append("03");                                                     // CodImp3 = INC
        sb.append(formatDecimal(valInc));                                    // ValImp3
        sb.append(formatDecimal(totalFactura));                              // ValTot
        sb.append(nitEmisor);                                                // NitOFE
        sb.append(identificacionReceptor);                                   // NumAdq
        sb.append(clTec);                                                    // ClTec del comprobante
        sb.append(dianConfig.getAmbiente());                                 // TipoAmbiente

        return sha384(sb.toString());
    }

    /**
     * Sobrecarga sin ICA/INC para llamadores legacy — los rellena con cero.
     * NUEVAS llamadas deben usar la versión completa.
     */
    public String generarCufe(String numeroFactura, LocalDate fechaEmision, String horaEmision,
                              BigDecimal totalFactura, BigDecimal baseGravable, BigDecimal totalIva,
                              String nitEmisor, String identificacionReceptor) {
        return generarCufe(numeroFactura, fechaEmision, horaEmision,
                totalFactura, baseGravable, totalIva,
                BigDecimal.ZERO, BigDecimal.ZERO,
                nitEmisor, identificacionReceptor);
    }

    /**
     * CUDE (Código Único de Documento Electrónico) para Notas Crédito/Débito.
     * Algoritmo SHA-384 sobre:
     *   NumNota + FecNota + HorNota + ValNota + CodImp1 + ValImp1 +
     *   CodImp2 + ValImp2 + CodImp3 + ValImp3 + ValTot +
     *   NitOFE + NumAdq + ClTec + TipoAmbiente
     *
     * Diferencias frente al CUFE: el CodImp1 puede ser distinto si la nota
     * no afecta IVA; el ValTot debe incluir signo positivo (el documento
     * lleva el signo según el concepto).
     */
    public String generarCude(String numeroNota, LocalDate fechaEmision, String horaEmision,
                              BigDecimal totalNota, BigDecimal baseGravable, BigDecimal totalIva,
                              BigDecimal valIca, BigDecimal valInc,
                              String nitEmisor, String identificacionReceptor) {
        return generarCude(numeroNota, fechaEmision, horaEmision,
                totalNota, baseGravable, totalIva, valIca, valInc,
                nitEmisor, identificacionReceptor, null);
    }

    public String generarCude(String numeroNota, LocalDate fechaEmision, String horaEmision,
                              BigDecimal totalNota, BigDecimal baseGravable, BigDecimal totalIva,
                              BigDecimal valIca, BigDecimal valInc,
                              String nitEmisor, String identificacionReceptor,
                              String claveTecnica) {
        return generarCufe(numeroNota, fechaEmision, horaEmision,
                totalNota, baseGravable, totalIva,
                valIca, valInc,
                nitEmisor, identificacionReceptor, claveTecnica);
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) return "0.00";
        return value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String sha384(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generando hash SHA-384 para CUFE/CUDE", e);
        }
    }
}
