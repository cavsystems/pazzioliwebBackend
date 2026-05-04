package com.pazzioliweb.metodospagomodule.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pazzioliweb.metodospagomodule.dtos.MetodoPagoDTO;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;

@Service
public class MetodosPagoService {
	private final MetodosPagoRepository metodopagoRepository;
	
	@Autowired
	public MetodosPagoService(MetodosPagoRepository metodopagoRepository) {
		this.metodopagoRepository = metodopagoRepository;
	}
	
	public Page<MetodoPagoDTO> listar(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<MetodoPagoDTO> listadoMetodosPago = metodopagoRepository.listadoMetodosPagoDTO(pageable);
    	
    	return listadoMetodosPago;
	}
	
	public Optional<MetodosPago> buscarPorId(Integer id) {
        return metodopagoRepository.findById(id);
    }

    public MetodosPago guardar(MetodosPago metodoPago) {
        return metodopagoRepository.save(metodoPago);
    }

    public void eliminar(Integer id) {
    	metodopagoRepository.deleteById(id);
    }

    private static final Set<String> TIPOS_VALIDOS = Set.of("RECIBO", "EGRESO", "VENTA");

    public List<MetodosPago> listarActivosPorTipo(String tipo) {
        if (tipo == null) throw new RuntimeException("Tipo es obligatorio");
        String t = tipo.toUpperCase();
        if (!TIPOS_VALIDOS.contains(t)) {
            throw new RuntimeException("Tipo inválido. Use RECIBO, EGRESO o VENTA");
        }
        return metodopagoRepository.listarPorTipo(t);
    }
}
