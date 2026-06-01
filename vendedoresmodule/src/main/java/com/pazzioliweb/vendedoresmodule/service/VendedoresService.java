package com.pazzioliweb.vendedoresmodule.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;
import com.pazzioliweb.vendedoresmodule.dtos.VendedorCreateRequest;
import com.pazzioliweb.vendedoresmodule.dtos.VendedorDTO;
import com.pazzioliweb.vendedoresmodule.entity.Usuariosvendedor;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import com.pazzioliweb.vendedoresmodule.repositori.UsuarioVendedorRepository;
import com.pazzioliweb.vendedoresmodule.repositori.VendedoresRepository;

@Service
public class VendedoresService {
	private final VendedoresRepository vendedorRepository;
	private final UsuarioVendedorRepository usuarioVendedorRepository;
	private final UsuarioRepository usuarioRepository;
	
	@Autowired
	public VendedoresService(VendedoresRepository vendedorRepository, UsuarioVendedorRepository usuarioVendedorRepository, UsuarioRepository usuarioRepository) {
		this.vendedorRepository = vendedorRepository;
		this.usuarioVendedorRepository = usuarioVendedorRepository;
		this.usuarioRepository = usuarioRepository;
	}
	
	public Page<VendedorDTO> listar(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<Object[]> results = vendedorRepository.listarVendedoresDTO(pageable);
    	
    	List<VendedorDTO> dtoList = results.getContent().stream().map(row -> {
    		java.sql.Date sqlDate = (java.sql.Date) row[6];
    		java.time.LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;
    		
    		return new VendedorDTO(
    			(Integer) row[0],           // vendedor_id
    			(String) row[1],            // nombre
    			(String) row[2],            // direccion
    			(String) row[3],            // telefono
    			(String) row[4],            // estado
    			(Integer) row[5],           // codigo_usuario_creo
    			localDate,                  // fechacreado
    			(Integer) row[7],           // usuarioCodigo (can be null)
    			(String) row[8],            // usuarioNombre (can be null)
    			(String) row[9]             // rolNombre (can be null)
    		);
    	}).collect(Collectors.toList());
    	
    	return new PageImpl<>(dtoList, pageable, results.getTotalElements());
	}
	/*
	metodo para listar vendedores por cede
	 */
	public List<Vendedores> findByBodegaId(Integer bodegaId) {
        return vendedorRepository.findByBodegaId(bodegaId);
    }
	public Optional<Vendedores> buscarPorId(Integer id) {
        return vendedorRepository.findById(id);
    }

	public Vendedores crearVendedor(VendedorCreateRequest request) {
		// Crear el vendedor
		Vendedores vendedor = new Vendedores();
		vendedor.setNombre(request.getNombre());
		vendedor.setDireccion(request.getDireccion());
		vendedor.setTelefono(request.getTelefono());
		vendedor.setEstado(Vendedores.Estado.valueOf(request.getEstado()));
		vendedor.setCodigo_usuario_creo(0); // Ajustar según lógica de negocio
		vendedor.setFechacreado(java.time.LocalDate.now());
		
		// Guardar el vendedor primero
		Vendedores vendedorGuardado = vendedorRepository.save(vendedor);
		
		// Si usuarioid es diferente de 0 y no es null, verificar y crear relación
		if (request.getUsuarioid() != null && request.getUsuarioid() != 0) {
			// Verificar si el usuario ya está relacionado con otro vendedor
			if (usuarioVendedorRepository.existsByUsuarioCodigo(request.getUsuarioid())) {
				throw new RuntimeException("usuario relacionado con otro vendedor");
			}
			
			// Buscar el usuario
			Usuario usuario = usuarioRepository.findById(request.getUsuarioid())
				.orElseThrow(() -> new RuntimeException("usuario no encontrado"));
			
			// Crear la relación
			Usuariosvendedor relacion = new Usuariosvendedor();
			relacion.setUsuario(usuario);
			relacion.setVendedor(vendedorGuardado);
			usuarioVendedorRepository.save(relacion);
		}
		
		return vendedorGuardado;
	}

    public Vendedores guardar(Vendedores vendedor) {
        return vendedorRepository.save(vendedor);
    }

    public void eliminar(Integer id) {
    	vendedorRepository.deleteById(id);
    }
}
