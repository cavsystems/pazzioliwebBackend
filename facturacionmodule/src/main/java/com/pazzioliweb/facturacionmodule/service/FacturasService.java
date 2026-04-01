package com.pazzioliweb.facturacionmodule.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pazzioliweb.facturacionmodule.dtos.FacturaResumenDTO;
import com.pazzioliweb.facturacionmodule.entity.Facturas;
import com.pazzioliweb.facturacionmodule.repositori.FacturasRepository;

@Service
public class FacturasService {
	
    private final FacturasRepository facturaRepository;
    
    public FacturasService(FacturasRepository facturaRepository) {
    	this.facturaRepository = facturaRepository;
    }

    public Page<FacturaResumenDTO> listarFacturasResumen(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<FacturaResumenDTO> listadoFacturas = facturaRepository.listadoFacturasResumenDTO(pageable);
    	
    	return listadoFacturas;
	}
    
    public Page<FacturaResumenDTO> listarFacturasResumenPorFecha(
	        LocalDateTime fechaInicio,
	        LocalDateTime fechaFin,
	        int page,
	        int size,
	        String sortField,
	        String sortDirection) {

	    Sort sort = sortDirection.equalsIgnoreCase("asc")
	            ? Sort.by(sortField).ascending()
	            : Sort.by(sortField).descending();

	    Pageable pageable = PageRequest.of(page, size, sort);

	    return facturaRepository.listadoFacturasResumenPorFecha(fechaInicio, fechaFin, pageable);
	}
    
    public List<FacturaResumenDTO> listarFacturasResumenPorFechaTodas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return facturaRepository.listadoFacturasResumenPorFechaTodas(fechaInicio, fechaFin);
    }
    
    public List<Facturas> listarfacturasResumenPorFechasConDetalles(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return facturaRepository.listadoFacturasConDetalles(fechaInicio, fechaFin);
    }
    
    public Optional<Facturas> buscarPorId(Integer id) {
        return facturaRepository.findById(id);
    }

    public Facturas guardar(Facturas factura) {
        return facturaRepository.save(factura);
    }

    public void eliminar(Integer id) {
    	facturaRepository.deleteById(id);
    }
}
