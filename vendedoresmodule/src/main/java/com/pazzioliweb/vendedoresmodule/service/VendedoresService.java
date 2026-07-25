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

import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresasback.repositori.EmpresaRepositori;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.Usuariobodega;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.productosmodule.repositori.UsuariobodegaRepository;
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
	private final UsuariobodegaRepository usuariobodegaRepository;
	private final EmpresaRepositori empresaRepository;
	
	@Autowired
	public VendedoresService(VendedoresRepository vendedorRepository, UsuarioVendedorRepository usuarioVendedorRepository, UsuarioRepository usuarioRepository, BodegasRepository bodegasRepository, UsuariobodegaRepository usuariobodegaRepository, EmpresaRepositori empresaRepository) {
		this.vendedorRepository = vendedorRepository;
		this.usuarioVendedorRepository = usuarioVendedorRepository;
		this.usuarioRepository = usuarioRepository;
		this.bodegasRepository = bodegasRepository;
		this.usuariobodegaRepository = usuariobodegaRepository;
		this.empresaRepository = empresaRepository;
	}
	
	public Page<VendedorDTO> listar(int page, int size, String sortField, String sortDirection){
		Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
    	Page<Object[]> results = vendedorRepository.listarVendedoresDTO(pageable);
    	
    	// Obtener datos de la empresa (correo y dirección)
    	Empresa empresa = empresaRepository.findAll().stream().findFirst().orElse(null);
    	String empresaCorreo = empresa != null ? empresa.getCorreoempresa() : null;
    	String empresaDireccion = empresa != null ? empresa.getDireccion() : null;
    	
    	List<VendedorDTO> dtoList = results.getContent().stream().map(row -> {
    		java.sql.Date sqlDate = row[11] != null ? (java.sql.Date) row[11] : null;
    		java.time.LocalDate localDate = sqlDate != null ? sqlDate.toLocalDate() : null;

    		Integer vendedorId = row[0] != null ? ((Number) row[0]).intValue() : null;
    		String nombre = (String) row[1];
    		String direccion = (String) row[2];
    		String telefono = (String) row[3];
    		String identificacion = (String) row[4];
    		String correo = (String) row[5];
    		Double comision = row[6] != null ? ((Number) row[6]).doubleValue() : null;
    		Double metaVentas = row[7] != null ? ((Number) row[7]).doubleValue() : null;
    		String tipoVendedor = (String) row[8];
    		String estado = (String) row[9];
    		Integer codigoUsuarioCreo = row[10] != null ? ((Number) row[10]).intValue() : null;
    		Integer usuarioCodigo = row[12] != null ? ((Number) row[12]).intValue() : null;
    		String usuarioNombre = (String) row[13];
    		String rolNombre = (String) row[14];
    		Integer bodegaId = row[15] != null ? ((Number) row[15]).intValue() : null;
    		String bodegaNombre = (String) row[16];

    		// Aplicar lógica de mapeo para correo y dirección según tipo de vendedor
    		if ("EXTERNO".equalsIgnoreCase(tipoVendedor)) {
    			// Si es EXTERNO, obtener correo y dirección de la bodega del usuario
    			if (usuarioCodigo != null) {
    				Usuario usuario = usuarioRepository.findById(usuarioCodigo).orElse(null);
    				if (usuario != null) {
    					List<Usuariobodega> usuarioBodegas = usuariobodegaRepository.findByUsuarioid(usuario);
    					if (!usuarioBodegas.isEmpty()) {
    						Bodegas bodegaUsuario = usuarioBodegas.get(0).getBodegaid();
    						if (bodegaUsuario != null) {
    							// Usar correo y dirección de la bodega del usuario si existen
    							correo = (bodegaUsuario.getCorreo() != null && !bodegaUsuario.getCorreo().isEmpty()) 
    								? bodegaUsuario.getCorreo() : empresaCorreo;
    							direccion = (bodegaUsuario.getDireccion() != null && !bodegaUsuario.getDireccion().isEmpty()) 
    								? bodegaUsuario.getDireccion() : empresaDireccion;
    						} else {
    							// Usuario no tiene bodega asignada, usar datos de empresa
    							correo = empresaCorreo;
    							direccion = empresaDireccion;
    						}
    					} else {
    						// Usuario no tiene bodega, usar datos de empresa
    						correo = empresaCorreo;
    						direccion = empresaDireccion;
    					}
    				} else {
    					// Usuario no encontrado, usar datos de empresa
    					correo = empresaCorreo;
    					direccion = empresaDireccion;
    				}
    			} else {
    				// No hay usuario asociado, usar datos de empresa
    				correo = empresaCorreo;
    				direccion = empresaDireccion;
    			}
    		} else {
    			// Si es INTERNO, usar correo y dirección de la empresa
    			correo = empresaCorreo;
    			direccion = empresaDireccion;
    		}

    		return new VendedorDTO(
    			vendedorId,
    			nombre,
    			direccion,
    			telefono,
    			identificacion,
    			correo,
    			comision,
    			metaVentas,
    			tipoVendedor,
    			estado,
    			codigoUsuarioCreo,
    			localDate,
    			usuarioCodigo,
    			usuarioNombre,
    			rolNombre,
    			bodegaId,
    			bodegaNombre
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
