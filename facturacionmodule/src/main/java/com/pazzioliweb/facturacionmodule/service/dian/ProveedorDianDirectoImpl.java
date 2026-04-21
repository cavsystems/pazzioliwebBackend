package com.pazzioliweb.facturacionmodule.service.dian;

import com.pazzioliweb.facturacionmodule.config.DianConfig;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoRequestDTO;
import com.pazzioliweb.facturacionmodule.dtos.DianDocumentoResponseDTO;
import com.pazzioliweb.facturacionmodule.service.ProveedorFacturacionElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Implementación REAL del proveedor de facturación electrónica.
 * Conecta directamente con el Web Service SOAP de la DIAN.
 *
 * Flujo completo:
 * 1. Genera XML UBL 2.1 desde DianDocumentoRequestDTO
 * 2. Calcula el CUFE (SHA-384)
 * 3. Firma digitalmente el XML con certificado .p12/.pfx
 * 4. Comprime en ZIP, codifica en Base64
 * 5. Envía al Web Service DIAN (SendBillSync o SendTestSetAsync)
 * 6. Parsea la respuesta y retorna DianDocumentoResponseDTO
 */
@Service
@Primary
public class ProveedorDianDirectoImpl implements ProveedorFacturacionElectronica {

    private static final Logger log = LoggerFactory.getLogger(ProveedorDianDirectoImpl.class);

    private final DianConfig dianConfig;
    private final UblXmlGenerator ublXmlGenerator;
    private final CufeGenerator cufeGenerator;
    private final XmlDigitalSigner xmlDigitalSigner;
    private final DianSoapClient dianSoapClient;

    public ProveedorDianDirectoImpl(DianConfig dianConfig,
                                     UblXmlGenerator ublXmlGenerator,
                                     CufeGenerator cufeGenerator,
                                     XmlDigitalSigner xmlDigitalSigner,
                                     DianSoapClient dianSoapClient) {
        this.dianConfig = dianConfig;
        this.ublXmlGenerator = ublXmlGenerator;
        this.cufeGenerator = cufeGenerator;
        this.xmlDigitalSigner = xmlDigitalSigner;
        this.dianSoapClient = dianSoapClient;
    }

    @Override
    public DianDocumentoResponseDTO enviarFactura(DianDocumentoRequestDTO request) {
        log.info("══════ INICIO Facturación Electrónica DIAN ══════");
        log.info("Documento: {} - {}{}", request.getTipoDocumento(), request.getPrefijo(), request.getConsecutivo());
        log.info("Ambiente: {} ({})", dianConfig.getAmbiente(), dianConfig.getAmbiente() == 1 ? "PRODUCCIÓN" : "PRUEBAS");

        DianDocumentoResponseDTO response = new DianDocumentoResponseDTO();
        String numeroFactura = request.getPrefijo() + request.getConsecutivo();

        try {
            // 1. Calcular CUFE
            log.info("Paso 1: Calculando CUFE...");
            String horaEmision = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "-05:00";
            String nitEmisor = request.getEmisor() != null ? request.getEmisor().getNumeroIdentificacion() : "";
            String idReceptor = request.getReceptor() != null ? request.getReceptor().getNumeroIdentificacion() : "";

            String cufe = cufeGenerator.generarCufe(
                    numeroFactura,
                    request.getFechaEmision(),
                    horaEmision,
                    request.getTotalFactura(),
                    request.getBaseGravable(),
                    request.getTotalIva(),
                    nitEmisor,
                    idReceptor
            );
            log.info("CUFE generado: {}", cufe);

            // 2. Generar XML UBL 2.1
            log.info("Paso 2: Generando XML UBL 2.1...");
            String xmlSinFirmar = ublXmlGenerator.generarXml(request, cufe);
            log.info("XML generado exitosamente ({} caracteres)", xmlSinFirmar.length());

            // 3. Firmar digitalmente
            log.info("Paso 3: Firmando XML digitalmente...");
            String xmlFirmado = xmlDigitalSigner.firmarXml(xmlSinFirmar);
            log.info("XML firmado exitosamente ({} caracteres)", xmlFirmado.length());

            // 4. Enviar a la DIAN (o simular si no hay certificado)
            String fileName = "fv" + numeroFactura.toLowerCase() + ".xml";
            log.info("Paso 4: Enviando a DIAN... (archivo: {})", fileName);

            boolean modoSimulacion = dianConfig.getCertificado() == null
                    || dianConfig.getCertificado().getRuta() == null
                    || dianConfig.getCertificado().getRuta().isBlank();

            if (modoSimulacion) {
                log.warn("⚠️ MODO SIMULACIÓN: No hay certificado configurado. Se genera factura local sin enviar a DIAN.");
                response.setExitoso(true);
                response.setCufe(cufe);
                response.setEstadoDian("SIMULADA");
                response.setMensajeDian("Factura generada en modo simulación (sin certificado DIAN)");
                response.setQrData("https://catalogo-vpfe.dian.gov.co/document/searchqr?documentkey=" + cufe);
                response.setXmlFirmado(Base64.getEncoder().encodeToString(xmlFirmado.getBytes()));
                response.setFechaValidacion(LocalDateTime.now());
            } else {
                DianSoapClient.DianSoapResponse soapResponse = dianSoapClient.enviarDocumento(xmlFirmado, fileName);

                // 5. Mapear respuesta
                response.setExitoso(soapResponse.isExitoso());
                response.setCufe(cufe);
                response.setEstadoDian(soapResponse.isExitoso() ? "AUTORIZADA" : "RECHAZADA");
                response.setMensajeDian(soapResponse.getMensaje());
                response.setQrData("https://catalogo-vpfe.dian.gov.co/document/searchqr?documentkey=" + cufe);
                response.setXmlFirmado(Base64.getEncoder().encodeToString(xmlFirmado.getBytes()));
                response.setFechaValidacion(LocalDateTime.now());
            }

            log.info("══════ RESULTADO: {} - {} ══════",
                    response.getEstadoDian(), response.getMensajeDian());

        } catch (Exception e) {
            log.error("Error en facturación electrónica: {}", e.getMessage(), e);
            response.setExitoso(false);
            response.setCufe(null);
            response.setEstadoDian("RECHAZADA");
            response.setMensajeDian("Error: " + e.getMessage());
            response.setFechaValidacion(LocalDateTime.now());
        }

        return response;
    }

    @Override
    public DianDocumentoResponseDTO consultarEstado(String cufe) {
        log.info("Consultando estado DIAN para CUFE: {}", cufe);
        DianDocumentoResponseDTO response = new DianDocumentoResponseDTO();

        try {
            DianSoapClient.DianSoapResponse soapResponse = dianSoapClient.consultarEstado(cufe);
            response.setExitoso(soapResponse.isExitoso());
            response.setCufe(cufe);
            response.setEstadoDian(soapResponse.isExitoso() ? "AUTORIZADA" : "RECHAZADA");
            response.setMensajeDian(soapResponse.getMensaje());
            response.setFechaValidacion(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error consultando estado DIAN: {}", e.getMessage(), e);
            response.setExitoso(false);
            response.setCufe(cufe);
            response.setMensajeDian("Error consultando: " + e.getMessage());
        }

        return response;
    }
}

