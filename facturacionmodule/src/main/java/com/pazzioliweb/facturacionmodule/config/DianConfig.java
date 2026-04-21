package com.pazzioliweb.facturacionmodule.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dian")
@Data
public class DianConfig {

    /** 1 = Producción, 2 = Pruebas (Habilitación) */
    private int ambiente = 2;

    private Url url = new Url();
    private String testSetId;
    private String claveTecnica;
    private Resolucion resolucion = new Resolucion();
    private Certificado certificado = new Certificado();
    private Software software = new Software();
    private int empresaId = 1;

    public String getUrlActiva() {
        return ambiente == 1 ? url.getProduccion() : url.getPruebas();
    }

    @Data
    public static class Url {
        private String produccion = "https://vpfe.dian.gov.co/WcfDianCustomerServices.svc";
        private String pruebas = "https://vpfe-hab.dian.gov.co/WcfDianCustomerServices.svc";
    }

    @Data
    public static class Resolucion {
        private String numero;
        private String fechaDesde;
        private String fechaHasta;
        private String prefijo = "FE";
        private long rangoDesde = 1;
        private long rangoHasta = 5000000;
    }

    @Data
    public static class Certificado {
        private String ruta;
        private String password;
    }

    @Data
    public static class Software {
        private String id;
        private String pin;
    }
}

