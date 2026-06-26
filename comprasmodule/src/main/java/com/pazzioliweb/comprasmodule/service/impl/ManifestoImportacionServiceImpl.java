package com.pazzioliweb.comprasmodule.service.impl;

import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.comprasmodule.dtos.ManifestoImportacionDTO;
import com.pazzioliweb.comprasmodule.entity.ManifestoImportacion;
import com.pazzioliweb.comprasmodule.repository.ManifestoImportacionRepository;
import com.pazzioliweb.comprasmodule.repository.OrdenCompraRepository;
import com.pazzioliweb.comprasmodule.service.ManifestoImportacionService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManifestoImportacionServiceImpl implements ManifestoImportacionService {

    @Value("${app.storage.manifiestos.base-path:C:/DocumentosImportacion}")
    private String basePath;

    @Value("${spring.mail.username:noreply@pazzioli.com}")
    private String remitente;

    @Autowired
    private ManifestoImportacionRepository repository;

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public ManifestoImportacionDTO crear(ManifestoImportacionDTO dto, MultipartFile pdf, String tenant) {
        ManifestoImportacion entity = new ManifestoImportacion();
        entity.setNumeroDeclaracion(dto.getNumeroDeclaracion());
        entity.setFechaImportacion(dto.getFechaImportacion());
        entity.setAduana(dto.getAduana());
        entity.setPaisOrigen(dto.getPaisOrigen());
        entity.setProveedorInternacional(dto.getProveedorInternacional());
        entity.setNumeroContenedor(dto.getNumeroContenedor());
        entity.setObservaciones(dto.getObservaciones());
        entity.setUsuarioCreacion(dto.getUsuarioCreacion());
        entity.setFechaCreacion(LocalDate.now());
        entity.setEstado("ACTIVO");

        if (dto.getOrdenCompraId() != null) {
            ordenCompraRepository.findById(dto.getOrdenCompraId())
                .ifPresent(entity::setOrdenCompra);
        }

        if (pdf != null && !pdf.isEmpty()) {
            String rutaPdf = guardarPdf(pdf, tenant);
            entity.setRutaPdf(rutaPdf);
            String originalName = pdf.getOriginalFilename();
            entity.setNombreArchivoOriginal(originalName != null ? originalName : "manifiesto.pdf");
        }

        ManifestoImportacion saved = repository.save(entity);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManifestoImportacionDTO> porOrdenCompra(Long ordenCompraId) {
        return repository.findByOrdenCompra_Id(ordenCompraId).stream()
            .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManifestoImportacionDTO> porVenta(Long ventaId) {
        return repository.findByVentaId(ventaId).stream()
            .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ManifestoImportacionDTO porId(Long id) {
        return repository.findById(id).map(this::toDTO)
            .orElseThrow(() -> new RuntimeException("Manifiesto no encontrado: " + id));
    }

    @Override
    public byte[] obtenerPdf(Long id, String tenant) {
        ManifestoImportacion m = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manifiesto no encontrado: " + id));
        if (m.getRutaPdf() == null) {
            throw new RuntimeException("Este manifiesto no tiene PDF asociado");
        }
        try {
            Path path = Paths.get(m.getRutaPdf());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el PDF: " + e.getMessage());
        }
    }

    private String guardarPdf(MultipartFile pdf, String tenant) {
        try {
            LocalDate hoy = LocalDate.now();
            String tenantDir = (tenant != null ? tenant : TenantContext.getCurrentTenant());
            if (tenantDir == null) tenantDir = "default";
            Path dir = Paths.get(basePath, tenantDir,
                String.valueOf(hoy.getYear()),
                String.format("%02d", hoy.getMonthValue()));
            Files.createDirectories(dir);
            String originalName = pdf.getOriginalFilename() != null
                ? pdf.getOriginalFilename().replaceAll("[^a-zA-Z0-9._\\-]", "_")
                : "documento.pdf";
            String filename = UUID.randomUUID() + "_" + originalName;
            Path dest = dir.resolve(filename);
            pdf.transferTo(dest.toFile());
            return dest.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando PDF: " + e.getMessage());
        }
    }

    @Override
    public void enviarEmail(Long manifiestoId, String correo, Long ventaId) {
        if (mailSender == null) {
            throw new RuntimeException("El servicio de email no está configurado. Configure spring.mail.* en application.properties.");
        }
        ManifestoImportacion m = repository.findById(manifiestoId)
            .orElseThrow(() -> new RuntimeException("Manifiesto no encontrado: " + manifiestoId));
        try {
            MimeMessage message = mailSender.createMimeMessage();
            boolean tieneAdjunto = m.getRutaPdf() != null && Files.exists(Paths.get(m.getRutaPdf()));
            MimeMessageHelper helper = new MimeMessageHelper(message, tieneAdjunto, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(correo);
            helper.setSubject("Declaración de Importación N° " + m.getNumeroDeclaracion());
            helper.setText(construirHtmlEmail(m, ventaId), true);
            if (tieneAdjunto) {
                helper.addAttachment(
                    m.getNombreArchivoOriginal() != null ? m.getNombreArchivoOriginal() : "manifiesto.pdf",
                    Paths.get(m.getRutaPdf()).toFile()
                );
            }
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando email: " + e.getMessage(), e);
        }
    }

    private String construirHtmlEmail(ManifestoImportacion m, Long ventaId) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><style>" +
            "body{font-family:Arial,sans-serif;color:#333;padding:20px}" +
            "h2{color:#f09700;border-bottom:2px solid #f09700;padding-bottom:8px}" +
            "table{width:100%;border-collapse:collapse;margin-top:12px}" +
            "th{background:#f09700;color:#fff;padding:8px 12px;text-align:left}" +
            "td{padding:8px 12px;border-bottom:1px solid #e2e8f0}" +
            "</style></head><body>" +
            "<h2>Declaración de Importación</h2>" +
            "<table><tr><th>Campo</th><th>Valor</th></tr>" +
            "<tr><td><b>N° Declaración</b></td><td>" + m.getNumeroDeclaracion() + "</td></tr>" +
            "<tr><td><b>Fecha Importación</b></td><td>" + m.getFechaImportacion() + "</td></tr>" +
            (m.getAduana() != null ? "<tr><td><b>Aduana</b></td><td>" + m.getAduana() + "</td></tr>" : "") +
            (m.getPaisOrigen() != null ? "<tr><td><b>País de Origen</b></td><td>" + m.getPaisOrigen() + "</td></tr>" : "") +
            (m.getProveedorInternacional() != null ? "<tr><td><b>Proveedor Internacional</b></td><td>" + m.getProveedorInternacional() + "</td></tr>" : "") +
            (m.getNumeroContenedor() != null ? "<tr><td><b>N° Contenedor</b></td><td>" + m.getNumeroContenedor() + "</td></tr>" : "") +
            (m.getObservaciones() != null ? "<tr><td><b>Observaciones</b></td><td>" + m.getObservaciones() + "</td></tr>" : "") +
            "</table>" +
            (ventaId != null ? "<p style='margin-top:16px;color:#6c757d;font-size:12px'>Asociado a venta ID: " + ventaId + "</p>" : "") +
            "<p style='color:#6c757d;font-size:11px;margin-top:20px'>Generado por Pazzioli WEB</p>" +
            "</body></html>";
    }

    private ManifestoImportacionDTO toDTO(ManifestoImportacion m) {
        ManifestoImportacionDTO dto = new ManifestoImportacionDTO();
        dto.setId(m.getId());
        dto.setNumeroDeclaracion(m.getNumeroDeclaracion());
        dto.setFechaImportacion(m.getFechaImportacion());
        dto.setAduana(m.getAduana());
        dto.setPaisOrigen(m.getPaisOrigen());
        dto.setProveedorInternacional(m.getProveedorInternacional());
        dto.setNumeroContenedor(m.getNumeroContenedor());
        dto.setObservaciones(m.getObservaciones());
        dto.setRutaPdf(m.getRutaPdf());
        dto.setNombreArchivoOriginal(m.getNombreArchivoOriginal());
        dto.setEstado(m.getEstado());
        dto.setUsuarioCreacion(m.getUsuarioCreacion());
        dto.setFechaCreacion(m.getFechaCreacion());
        if (m.getOrdenCompra() != null) {
            dto.setOrdenCompraId(m.getOrdenCompra().getId());
            dto.setNumeroOrden(m.getOrdenCompra().getNumeroOrden());
        }
        return dto;
    }
}
