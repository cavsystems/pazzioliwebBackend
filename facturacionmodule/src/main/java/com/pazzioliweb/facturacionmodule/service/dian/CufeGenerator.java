package com.pazzioliweb.facturacionmodule.service.dian;

import com.pazzioliweb.facturacionmodule.config.DianConfig;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoRequestDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Genera el CUFE (Código Único de Factura Electrónica) según especificación DIAN.
 * Algoritmo SHA-384 sobre la cadena:
 * NumFac + FecFac + HorFac + ValFac + CodImp1 + ValImp1 + CodImp2 + ValImp2 + CodImp3 + ValImp3 + ValTot + NitOFE + NumAdq + ClTec + TipoAmbiente
 */
@Component
public class CufeGenerator {

    private final DianConfig dianConfig;

    public CufeGenerator(DianConfig dianConfig) {
        this.dianConfig = dianConfig;
    }

    /**
     * Genera el CUFE para una factura electrónica.
     */
    public String generarCufe(String numeroFactura, LocalDate fechaEmision, String horaEmision,
                               BigDecimal totalFactura, BigDecimal baseGravable, BigDecimal totalIva,
                               String nitEmisor, String identificacionReceptor) {

        // Formato: NumFac + FecFac + HorFac + ValFac + CodImp1 + ValImp1 + CodImp2 + ValImp2 + CodImp3 + ValImp3 + ValTot + NitOFE + NumAdq + ClTec + TipoAmbiente
        String fechaStr = fechaEmision.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        StringBuilder sb = new StringBuilder();
        sb.append(numeroFactura);                              // NumFac
        sb.append(fechaStr);                                   // FecFac
        sb.append(horaEmision != null ? horaEmision : "00:00:00-05:00"); // HorFac
        sb.append(formatDecimal(totalFactura));                // ValFac (valor antes de impuestos)
        sb.append("01");                                       // CodImp1 = IVA
        sb.append(formatDecimal(totalIva));                    // ValImp1
        sb.append("04");                                       // CodImp2 = ICA (0 si no aplica)
        sb.append("0.00");                                     // ValImp2
        sb.append("03");                                       // CodImp3 = INC (0 si no aplica)
        sb.append("0.00");                                     // ValImp3
        sb.append(formatDecimal(totalFactura));                // ValTot (total factura)
        sb.append(nitEmisor);                                  // NitOFE
        sb.append(identificacionReceptor);                     // NumAdq
        sb.append(dianConfig.getClaveTecnica() != null ? dianConfig.getClaveTecnica() : ""); // ClTec
        sb.append(dianConfig.getAmbiente());                   // TipoAmbiente

        return sha384(sb.toString());
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
            throw new RuntimeException("Error generando CUFE SHA-384", e);
        }
    }
}

