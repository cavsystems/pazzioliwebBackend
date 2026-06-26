package com.pazzioliweb.comprasmodule.service;

import com.pazzioliweb.comprasmodule.dtos.ManifestoImportacionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ManifestoImportacionService {
    ManifestoImportacionDTO crear(ManifestoImportacionDTO dto, MultipartFile pdf, String tenant);
    List<ManifestoImportacionDTO> porOrdenCompra(Long ordenCompraId);
    List<ManifestoImportacionDTO> porVenta(Long ventaId);
    ManifestoImportacionDTO porId(Long id);
    byte[] obtenerPdf(Long id, String tenant);
    void enviarEmail(Long manifiestoId, String correo, Long ventaId);
}
