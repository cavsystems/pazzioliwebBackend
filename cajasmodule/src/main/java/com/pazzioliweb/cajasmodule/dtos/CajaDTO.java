package com.pazzioliweb.cajasmodule.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pazzioli.comprobantesmodule.dtos.ComprobanteDTO;

public interface CajaDTO {
	Integer getCaja_id();
    /*UsuarioDTO getUsuario();*/
	Integer getUsuario_id();
    BigDecimal getMonto_inicial();
    BigDecimal getMonto_final();
    LocalDateTime getFecha_apertura();
    LocalDateTime getFecha_cierre();
    ComprobanteDTO getComprobante();
    Integer getConsecutivo();
    BigDecimal getTotal_recaudo();
    String getEstado();
}
