package com.pazzioliweb.tercerosmodule.dtos;

import java.math.BigDecimal;

public interface SaldoTerceroDTO {
    String getRazonSocial();
    Integer getTerceroId();
    BigDecimal getSaldo();
    String getTipo();
}
