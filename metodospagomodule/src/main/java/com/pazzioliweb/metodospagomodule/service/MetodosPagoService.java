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
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import com.pazzioliweb.comprobantesmodule.entity.CuentaContable;
import com.pazzioliweb.comprobantesmodule.repositori.CuentaContableRepository;
import com.pazzioliweb.metodospagomodule.dtos.MetodoPagoDTO;
import com.pazzioliweb.metodospagomodule.entity.CuentaBancaria;
import com.pazzioliweb.metodospagomodule.entity.MetodosPago;
import com.pazzioliweb.metodospagomodule.repositori.CuentaBancariaRepository;
import com.pazzioliweb.metodospagomodule.repositori.MetodosPagoRepository;

@Service
public class MetodosPagoService {
	private final MetodosPagoRepository metodopagoRepository;
	private final CuentaBancariaRepository cuentaBancariaRepository;
	private final CuentaContableRepository cuentaContableRepository;

	@Autowired
	public MetodosPagoService(MetodosPagoRepository metodopagoRepository,
							  CuentaBancariaRepository cuentaBancariaRepository,
							  CuentaContableRepository cuentaContableRepository) {
		this.metodopagoRepository = metodopagoRepository;
		this.cuentaBancariaRepository = cuentaBancariaRepository;
		this.cuentaContableRepository = cuentaContableRepository;
	}
	
	public Page<MetodoPagoDTO> listar(int page, int size, String sortField, String sortDirection){
		// Si llega "id" (default genérico) lo mapeamos a la PK real para evitar fallos.
		String campo = (sortField == null || sortField.isBlank() || "id".equals(sortField))
				? "metodo_pago_id" : sortField;
		Sort sort = sortDirection != null && sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(campo).ascending()
                : Sort.by(campo).descending();
    	Pageable pageable = PageRequest.of(page, size, sort);

    	Page<MetodoPagoDTO> listadoMetodosPago = metodopagoRepository.listadoMetodosPagoDTO(pageable);

    	return listadoMetodosPago;
	}
	
	public Optional<MetodosPago> buscarPorId(Integer id) {
        return metodopagoRepository.findById(id);
    }

    @Transactional
    public MetodosPago guardar(MetodosPago metodoPago) {
        // Si el frontend mandó solo IDs en las FK (sin objeto completo), resolverlos.
        if (metodoPago.getCuentaBancaria() != null && metodoPago.getCuentaBancaria().getId() != null) {
            CuentaBancaria cb = cuentaBancariaRepository.findById(metodoPago.getCuentaBancaria().getId()).orElse(null);
            metodoPago.setCuentaBancaria(cb);
        }
        if (metodoPago.getCuentaContable() != null && metodoPago.getCuentaContable().getId() != null) {
            CuentaContable cc = cuentaContableRepository.findById(metodoPago.getCuentaContable().getId()).orElse(null);
            metodoPago.setCuentaContable(cc);
        }
        MetodosPago saved = metodopagoRepository.save(metodoPago);
        // Forzar inicialización (incluyendo nested) para que Jackson no falle al serializar
        if (saved.getCuentaBancaria() != null) {
            Hibernate.initialize(saved.getCuentaBancaria());
            Hibernate.initialize(saved.getCuentaBancaria().getCuentaContable());
        }
        if (saved.getCuentaContable() != null) {
            Hibernate.initialize(saved.getCuentaContable());
        }
        return saved;
    }

    public void eliminar(Integer id) {
    	metodopagoRepository.deleteById(id);
    }

    private static final Set<String> TIPOS_VALIDOS = Set.of("RECIBO", "EGRESO", "VENTA", "COMPRA");

    @Transactional(readOnly = true)
    public List<MetodosPago> listarActivosPorTipo(String tipo) {
        if (tipo == null) throw new RuntimeException("Tipo es obligatorio");
        String t = tipo.toUpperCase();
        if (!TIPOS_VALIDOS.contains(t)) {
            throw new RuntimeException("Tipo inválido. Use RECIBO, EGRESO, VENTA o COMPRA");
        }
        List<MetodosPago> resultado = metodopagoRepository.listarPorTipo(t);
        // Inicializar relaciones lazy (incluyendo nested) para evitar errores de serialización
        for (MetodosPago m : resultado) {
            if (m.getCuentaBancaria() != null) {
                Hibernate.initialize(m.getCuentaBancaria());
                Hibernate.initialize(m.getCuentaBancaria().getCuentaContable());
            }
            if (m.getCuentaContable() != null) {
                Hibernate.initialize(m.getCuentaContable());
            }
        }
        return resultado;
    }
}
