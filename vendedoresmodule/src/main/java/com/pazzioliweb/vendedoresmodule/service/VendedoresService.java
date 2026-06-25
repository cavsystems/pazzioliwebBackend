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

import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;
import com.pazzioliweb.vendedoresmodule.dtos.VendedorCreateRequest;
import com.pazzioliweb.vendedoresmodule.dtos.VendedorDTO;
import com.pazzioliweb.vendedoresmodule.dtos.VendedorUpdateRequest;
import com.pazzioliweb.vendedoresmodule.entity.Usuariosvendedor;
import com.pazzioliweb.vendedoresmodule.entity.Vendedores;
import com.pazzioliweb.vendedoresmodule.exception.UsuarioYaAsignadoException;
import com.pazzioliweb.vendedoresmodule.repositori.UsuarioVendedorRepository;
import com.pazzioliweb.vendedoresmodule.repositori.VendedoresRepository;

@Service
public class VendedoresService {
	private final VendedoresRepository vendedorRepository;
	private final UsuarioVendedorRepository usuarioVendedorRepository;
	private final UsuarioRepository usuarioRepository;
	private final BodegasRepository bodegasRepository;
	
	@Autowired
	public VendedoresService(VendedoresRepository vendedorRepository, UsuarioVendedorRepository usuarioVendedorRepository, UsuarioRepository usuarioRepository, BodegasRepository bodegasRepository) {
		this.vendedorRepository = vendedorRepository;
		this.usuarioVendedorRepository = usuarioVendedorRepository;
		this.usuarioRepository = usuarioRepository;
		this.bodegasRepository = bodegasRepository;
	}
	
	public Page<VendedorDTO> listar(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<Object[]> results = vendedorRepository.listarVendedoresDTO(pageable);
    	
    	List<VendedorDTO> dtoList = results.getContent().stream().map(row -> {
    		java.sql.Date sqlDate = (java.sql.Date) row[11];
    		java.time.LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;
    		
    		return new VendedorDTO(
    			(Integer) row[0],           // vendedor_id
    			(String) row[1],            // nombre
    			(String) row[2],            // direccion
    			(String) row[3],            // telefono
    			(String) row[4],            // identificacion
    			(String) row[5],            // correo
    			(Double) row[6],            // comision
    			(Double) row[7],            // meta_ventas
    			(String) row[8],            // tipo_vendedor
    			(String) row[9],            // estado
    			(Integer) row[10],           // codigo_usuario_creo
    			localDate,                  // fechacreado
    			(Integer) row[12],          // usuarioCodigo (can be null)
    			(String) row[13],           // usuarioNombre (can be null)
    			(String) row[14],           // rolNombre (can be null)
    			(Integer) row[15],          // bodegaId (can be null)
    			(String) row[16]            // bodegaNombre (can be null)
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
		vendedor.setIdentificacion(request.getIdentificacion());
		vendedor.setCorreo(request.getCorreo());
		vendedor.setComision(request.getComision() != null ? request.getComision() : 0.0);
		vendedor.setMeta_ventas(request.getMeta_ventas() != null ? request.getMeta_ventas() : 0.0);
		vendedor.setTipo_vendedor(request.getTipo_vendedor() != null ? 
			Vendedores.TipoVendedor.valueOf(request.getTipo_vendedor().toUpperCase()) : Vendedores.TipoVendedor.INTERNO);
		vendedor.setEstado(Vendedores.Estado.valueOf(request.getEstado()));
		vendedor.setCodigo_usuario_creo(0); // Ajustar según lógica de negocio
		vendedor.setFechacreado(java.time.LocalDate.now());

		// Si bodegaId es diferente de null y diferente de 0, asignar la bodega
		if (request.getBodegaId() != null && request.getBodegaId() != 0) {
			Bodegas bodega = bodegasRepository.findById(request.getBodegaId())
				.orElseThrow(() -> new RuntimeException("bodega no encontrada"));
			vendedor.setBodega(bodega);
		}

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

    @jakarta.transaction.Transactional
    public void eliminar(Integer id) {
        Long registros = vendedorRepository.contarRegistrosAsociados(id);
        if (registros != null && registros > 0) {
            throw new RuntimeException(
                "El vendedor tiene registros asociados en ventas, pedidos, cotizaciones o facturas y no puede eliminarse"
            );
        }
        usuarioVendedorRepository.deleteByVendedorId(id);
        vendedorRepository.deleteById(id);
    }

    public Vendedores actualizarVendedor(Integer id, VendedorUpdateRequest request) {
        Vendedores vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("vendedor no encontrado"));

        // Actualizar campos del vendedor
        vendedor.setNombre(request.getNombre());
        vendedor.setDireccion(request.getDireccion());
        vendedor.setTelefono(request.getTelefono());
        vendedor.setEstado(Vendedores.Estado.valueOf(request.getEstado()));

        // Manejar la relación con la bodega
        if (request.getBodegaId() != null && request.getBodegaId() != 0) {
            Bodegas bodega = bodegasRepository.findById(request.getBodegaId())
                    .orElseThrow(() -> new RuntimeException("bodega no encontrada"));
            vendedor.setBodega(bodega);
        } else {
            vendedor.setBodega(null);
        }

        // Manejar la relación con el usuario
        if (request.getUsuarioid() != null && request.getUsuarioid() != 0) {
            // Verificar si el usuario ya está relacionado con otro vendedor
            java.util.List<Usuariosvendedor> relacionesUsuario = usuarioVendedorRepository.findAllByUsuarioCodigo(request.getUsuarioid());
            if (!relacionesUsuario.isEmpty() && relacionesUsuario.get(0).getVendedor().getVendedor_id() != id) {
                throw new UsuarioYaAsignadoException("usuario ya asignado a un vendedor");
            }

            // Buscar si el vendedor ya tiene una relación con algún usuario
            java.util.List<Usuariosvendedor> relacionesVendedor = usuarioVendedorRepository.findByVendedorId(id);
            Usuariosvendedor relacionVendedorActual = relacionesVendedor.isEmpty() ? null : relacionesVendedor.get(0);

            // Si el vendedor ya tiene una relación con un usuario diferente, eliminarla
            if (relacionVendedorActual != null && relacionVendedorActual.getUsuario().getCodigo() != request.getUsuarioid()) {
                usuarioVendedorRepository.delete(relacionVendedorActual);
            }

            // Si no existe relación o fue eliminada, crear la nueva relación
            if (relacionVendedorActual == null || relacionVendedorActual.getUsuario().getCodigo() != request.getUsuarioid()) {
                Usuario usuario = usuarioRepository.findById(request.getUsuarioid())
                        .orElseThrow(() -> new RuntimeException("usuario no encontrado"));

                Usuariosvendedor nuevaRelacion = new Usuariosvendedor();
                nuevaRelacion.setUsuario(usuario);
                nuevaRelacion.setVendedor(vendedor);
                usuarioVendedorRepository.save(nuevaRelacion);
            }
        } else {
            // Si usuarioid es null o 0, eliminar la relación existente si la hay
            java.util.List<Usuariosvendedor> relacionesVendedor = usuarioVendedorRepository.findByVendedorId(id);
            if (!relacionesVendedor.isEmpty()) {
                usuarioVendedorRepository.delete(relacionesVendedor.get(0));
            }
        }

        return vendedorRepository.save(vendedor);
    }
}
