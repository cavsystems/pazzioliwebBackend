package com.pazzioliweb.empresaauth.service;

import com.pazzioliweb.empresaback.dtos.EmpresaTenantProjection;
import com.pazzioliweb.empresaback.exception.Empresaexception;
import com.pazzioliweb.empresasback.entity.Estado;
import com.pazzioliweb.productosmodule.entity.Usuariobodega;
import com.pazzioliweb.productosmodule.repositori.UsuariobodegaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.commonbacken.conexiondb.TenantRegister;
import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Impuestos;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Pais;
import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;
import com.pazzioliweb.commonbacken.repositorio.DepartamentoRepositori;
import com.pazzioliweb.commonbacken.repositorio.ImpuestosRepositori;
import com.pazzioliweb.commonbacken.repositorio.MunicipioRepositori;
import com.pazzioliweb.commonbacken.repositorio.PaisRepositori;
import com.pazzioliweb.commonbacken.services.TenantService;
import com.pazzioliweb.commonbacken.util.Nombredb;
import com.pazzioliweb.empresaback.dtos.Datosempresa;
import com.pazzioliweb.empresaback.dtos.EmpresaResponseauth;
import com.pazzioliweb.empresaback.dtos.Empresaresponse;
import com.pazzioliweb.empresaback.dtos.Empresaresponse.Sucursales;
import com.pazzioliweb.empresasback.entity.Actividadeconomica;
import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresasback.entity.Regimen;
import com.pazzioliweb.empresasback.repositori.ActividadeconomicaRepositori;
import com.pazzioliweb.empresasback.repositori.EmpresaRepositori;
import com.pazzioliweb.empresasback.repositori.RegimenRepositori;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.commonbacken.repositorio.TipoidentificacionRepository;
import com.pazzioliweb.usuariosbacken.entity.Tipopersona;
import com.pazzioliweb.usuariosbacken.repositorio.TipopersonaRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.entity.Roles;
import com.pazzioliweb.usuariosbacken.entity.Permiso;
import com.pazzioliweb.usuariosbacken.entity.PermisoRol;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;
import com.pazzioliweb.usuariosbacken.repositorio.RolesRepository;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRepository;
import com.pazzioliweb.usuariosbacken.repositorio.PermisoRolRepository;
import com.pazzioliweb.commonbacken.util.PasswordUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmpresaService {
	@Autowired
	  private JdbcTemplate jdbc;
	  @Autowired
	  private TenantService tenantService;
	  @Autowired
	  private TipopersonaRepository tipopersonarepositori;
	  @Autowired
	  private TipoidentificacionRepository  tipoidentificacionrepositori;
	  @Autowired
	  private ActividadeconomicaRepositori actividadeconomicarepositorio;
	  @Autowired
	  private RegimenRepositori regimenrepositori;
	  @Autowired
	  private DepartamentoRepositori departamentorepositori;
	  @Autowired
	  private PaisRepositori paisrepository;
	  @Autowired
	  private MunicipioRepositori  municipiorepositori;
	  @Autowired
	  private ImpuestosRepositori impuestorepositorio;
	  @Autowired
	  private Nombredb nombredb;
	  @Autowired
	  private BodegasRepository repotoribodega;
	  @Autowired
	  private UsuarioRepository usuarioRepository;
	  @Autowired
	  private RolesRepository rolesRepository;
	  @Autowired
	  private PermisoRepository permisoRepository;
	  @Autowired
	  private PermisoRolRepository permisoRolRepository;

	  @Autowired
	  private UsuariobodegaRepository bodegarepositori;
	  @Autowired
	  private Insertarregistrosjoin insertjoi;
	  private Datosempresa datosempresa=new Datosempresa();
	  @Autowired
	  private  TenantRegister register;
	  @Autowired
	  private EmpresaRepositori emprerepo;

	@PersistenceContext
    private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<EmpresaResponseauth> buscarNombreconexion(String cc) {
	    return em.createNativeQuery("CALL buscarusuarios(:cc)", "EmpresaResponseauthMapping")
	             .setParameter("cc", cc)
	             .getResultList();
	}

	public List<EmpresaTenantProjection> listartodoslossquemas() {
		return em.createNativeQuery("CALL sp_listar_empresas_todos_tenants()", "EmpresaResponseMapping")

				.getResultList();
	}

	public List<EmpresaTenantProjection> buscarEmpresasConFiltro(String busqueda) {
		return em.createNativeQuery("CALL sp_buscar_empresas_todos_tenants_filtro(:busqueda)", "EmpresaResponseMapping")
				.setParameter("busqueda", busqueda)
				.getResultList();
	}


	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void crearempresa(Empresaresponse empre,String db, MultipartFile archivo) throws Exception{
		if(!empre.getSucursales().isEmpty()) {
			 List<Bodegas> bodegas = new ArrayList<>();
			   for(Sucursales sucu: empre.getSucursales()) {
				   Object[] codigos=insertjoi.obtenercodigos(sucu.getMunicipio().getCodigo(), sucu.getDepartamento().getCodigo(), sucu.getPais().getCodigo());
				   System.out.println(sucu.getDepartamento().getCodigo());
				  Bodegas bodega=new Bodegas();
				  bodega.setCelular(sucu.getCelular());
				  bodega.setCodigodepartamento((Departamento) codigos[1]);
				  bodega.setCodigomunicipio((Municipio) codigos[0]);
				  bodega.setCodigopais((Pais) codigos[2]);
				  bodega.setTelefono(sucu.getTelefonofijo());
				  bodega.setCelular(sucu.getCelular());
				  bodega.setDireccion(sucu.getDireccion());
				  bodega.setCodigopostal(sucu.getCodigopostal());
				  bodega.setNombre(sucu.getNombre());
				  bodega.setCodigosucursal(sucu.getCodigosucursal());
				  bodega.setCorreo(sucu.getCorreo());
				  bodegas.add(bodega);
				  
				  
				  

			   }
			   
			   
			   
			   repotoribodega.saveAll(bodegas);
		}
		
		if(!empre.getImpuestos().isEmpty()) {
			//Impuestos impuesto=new Impuestos();
			 List<Impuestos> impuestos = new ArrayList<>();
			for(com.pazzioliweb.empresaback.dtos.Empresaresponse.Impuestos in:empre.getImpuestos()) {
				Optional<Impuestos> impuestosop=impuestorepositorio.findByCodigo(in.getCodigo());
				Impuestos impuesto=impuestosop.get();
				impuesto.setEstado("ACTIVO");
				impuestorepositorio.save(impuesto);
				
			}
			
			
		}
		
		
		Object[] codigosempr=insertjoi.obtenercodigos(empre.getMunicipio(), empre.getDepartamento(), empre.getPais(), empre.getTipodepersona(), empre.getTipodeidentificacion(), empre.getRegimen(),empre.getActividadeconomica());
		Empresa empresa=new Empresa();
		
		empresa.setCodigomunicipio((Municipio) codigosempr[0]);
		empresa.setCodigodepartamento((Departamento) codigosempr[1] );
		empresa.setCodigopais((Pais) codigosempr[2]);
		empresa.setCodigotipopersona((Tipopersona) codigosempr[3]);
		empresa.setCodigotipoidentificacion((Tipoidentificacion) codigosempr[4]);
		empresa.setCodigoregimen((Regimen) codigosempr[5]);
		empresa.setCodigoactividadeconomica((Actividadeconomica) codigosempr[6]);
		empresa.setCelularempresa(empre.getCelularempresa());
		empresa.setCorreoempresa(empre.getCorreoempresa());
	      empresa.setPlazo(empre.getPlazo());
	      empresa.setNumerousuarios(empre.getNumerousuarios());
		empresa.setNombrecomercial(empre.getNombrecomercial());
		empresa.setNumeroidentificacion(empre.getNumeroidentificacion());
		empresa.setDigitoverificacion(empre.getDigitodeverificacion());
		empresa.setCodigopostal(empre.getCodigopostal());
		empresa.setPrimernombre(empre.getPrimernombre());
		empresa.setPrimerapellido(empre.getPrimerapellido());
		empresa.setSegundonombre(empre.getSegundonombre());
		empresa.setSegundoapellido(empre.getSegundoapellido());
		empresa.setRazonsocial(empre.getRazonsocial());
		empresa.setTelfonofijo(empre.getTelefonofijo());
		empresa.setFechainiciolicencia(empre.getFechainiciolicencia());
		empresa.setFechafinallicencia(empre.getFechafinallicencia());
		empresa.setEstado(Estado.ACTIVA);
		if (archivo != null && !archivo.isEmpty()) {
			empresa.setImagenEmpresa(archivo.getBytes());
		    empresa.setTipoImagen(archivo.getContentType()); // Guardamos el tipo MIME
		}

		emprerepo.save(empresa);
		
		// Crear usuario para la nueva empresa
		crearUsuarioParaEmpresa(empre);
		
		
		
		
		
		  
		
		
		
	}

      public  EmpresaTenantProjection actulizarlicencia(Empresaresponse empre, int codigoempresa){
		Empresa empresa=emprerepo.findByCodigo(codigoempresa).get();
		empresa.setFechainiciolicencia(empre.getFechainiciolicencia());
		empresa.setFechafinallicencia(empre.getFechafinallicencia());
		empresa.setPlazo(empre.getPlazo());
		empresa.setNumerousuarios(empre.getNumerousuarios());
		return  EmpresaTenantProjection.mapperdtos(emprerepo.save(empresa));

	  }
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateEmpresa(Empresaresponse empre, String db, MultipartFile archivo, Integer empresaId) throws Exception{
		// Buscar la empresa existente
		Optional<Empresa> empresaOpt = emprerepo.findById(empresaId.longValue());
		if (empresaOpt.isEmpty()) {
			throw new Exception("Empresa no encontrada con ID: " + empresaId);
		}
		
		Empresa empresa = empresaOpt.get();
		
		// Actualizar sucursales (bodegas) de forma segura
		if(!empre.getSucursales().isEmpty()) {
			// Obtener todas las bodegas existentes para comparar por nombre
			List<Bodegas> todasLasBodegas = repotoribodega.findAll();
			
			// Crear mapas para facilitar la comparación por nombre
			Map<String, Bodegas> existentesMap = new HashMap<>();
			for(Bodegas existente : todasLasBodegas) {
				String key = existente.getNombre() != null ? existente.getNombre().toLowerCase().trim() : "";
				existentesMap.put(key, existente);
			}
			
			List<Bodegas> bodegasActualizar = new ArrayList<>();
			List<Bodegas> bodegasNuevas = new ArrayList<>();
			Set<String> sucursalesRecibidas = new HashSet<>();
			
			// Procesar cada sucursal recibida
			for(Sucursales sucu : empre.getSucursales()) {
				String nombreSucursal = sucu.getNombre() != null ? sucu.getNombre().toLowerCase().trim() : "";
				sucursalesRecibidas.add(nombreSucursal);
				
				Object[] codigos = insertjoi.obtenercodigos(sucu.getMunicipio().getCodigo(), sucu.getDepartamento().getCodigo(), sucu.getPais().getCodigo());
				
				Bodegas bodegaExistente = existentesMap.get(nombreSucursal);
				
				if(bodegaExistente != null) {
					// Actualizar bodega existente (preserva ID y relaciones)
					bodegaExistente.setCelular(sucu.getCelular());
					bodegaExistente.setCodigodepartamento((Departamento) codigos[1]);
					bodegaExistente.setCodigomunicipio((Municipio) codigos[0]);
					bodegaExistente.setCodigopais((Pais) codigos[2]);
					bodegaExistente.setTelefono(sucu.getTelefonofijo());
					bodegaExistente.setDireccion(sucu.getDireccion());
					bodegaExistente.setCodigopostal(sucu.getCodigopostal());
					// El nombre ya es el mismo, no se actualiza
					bodegaExistente.setCorreo(sucu.getCorreo());
					bodegasActualizar.add(bodegaExistente);
				} else {
					// Crear nueva bodega
					Bodegas nuevaBodega = new Bodegas();
					nuevaBodega.setCelular(sucu.getCelular());
					nuevaBodega.setCodigodepartamento((Departamento) codigos[1]);
					nuevaBodega.setCodigomunicipio((Municipio) codigos[0]);
					nuevaBodega.setCodigopais((Pais) codigos[2]);
					nuevaBodega.setTelefono(sucu.getTelefonofijo());
					nuevaBodega.setDireccion(sucu.getDireccion());
					nuevaBodega.setCodigopostal(sucu.getCodigopostal());
					nuevaBodega.setNombre(sucu.getNombre());
					nuevaBodega.setCodigosucursal(sucu.getCodigosucursal());
					nuevaBodega.setCorreo(sucu.getCorreo());
					bodegasNuevas.add(nuevaBodega);
				}
			}
			
			// Identificar bodegas a desactivar (las que no vienen en la actualización)
			List<Bodegas> bodegasDesactivar = new ArrayList<>();
			for(Map.Entry<String, Bodegas> entry : existentesMap.entrySet()) {
				if(!sucursalesRecibidas.contains(entry.getKey())) {
					// Opción 1: Desactivar en lugar de eliminar
					// entry.getValue().setEstado("INACTIVO"); // Si tuviera campo estado
					// bodegasActualizar.add(entry.getValue()));
					
					// Opción 2: Eliminar solo si no tienen transacciones (más seguro)
					// Por ahora, mantenemos las bodegas existentes para no romper relaciones
					System.out.println("Bodega no actualizada: " + entry.getKey() + " - se mantiene para preservar relaciones existentes");
				}
			}
			
			// Guardar cambios
			if(!bodegasActualizar.isEmpty()) {
				repotoribodega.saveAll(bodegasActualizar);
			}
			if(!bodegasNuevas.isEmpty()) {
				repotoribodega.saveAll(bodegasNuevas);
			}
		}
		
		// Actualizar impuestos
		if(!empre.getImpuestos().isEmpty()) {
			for(com.pazzioliweb.empresaback.dtos.Empresaresponse.Impuestos in: empre.getImpuestos()) {
				Optional<Impuestos> impuestosOp = impuestorepositorio.findByCodigo(in.getCodigo());
				if (impuestosOp.isPresent()) {
					Impuestos impuesto = impuestosOp.get();
					impuesto.setEstado("ACTIVO");
					impuestorepositorio.save(impuesto);
				}
			}
		}
		
		// Actualizar datos de la empresa
		Object[] codigosempr = insertjoi.obtenercodigos(empre.getMunicipio(), empre.getDepartamento(), empre.getPais(), empre.getTipodepersona(), empre.getTipodeidentificacion(), empre.getRegimen(), empre.getActividadeconomica());
		
		empresa.setCodigomunicipio((Municipio) codigosempr[0]);
		empresa.setCodigodepartamento((Departamento) codigosempr[1]);
		empresa.setCodigopais((Pais) codigosempr[2]);
		empresa.setCodigotipopersona((Tipopersona) codigosempr[3]);
		empresa.setCodigotipoidentificacion((Tipoidentificacion) codigosempr[4]);
		empresa.setCodigoregimen((Regimen) codigosempr[5]);
		empresa.setCodigoactividadeconomica((Actividadeconomica) codigosempr[6]);
		empresa.setCelularempresa(empre.getCelularempresa());
		empresa.setCorreoempresa(empre.getCorreoempresa());
		empresa.setNombrecomercial(empre.getNombrecomercial());
		empresa.setNumeroidentificacion(empre.getNumeroidentificacion());
		empresa.setDigitoverificacion(empre.getDigitodeverificacion());
		empresa.setCodigopostal(empre.getCodigopostal());
		empresa.setPrimernombre(empre.getPrimernombre());
		empresa.setPrimerapellido(empre.getPrimerapellido());
		empresa.setSegundonombre(empre.getSegundonombre());
		empresa.setSegundoapellido(empre.getSegundoapellido());
		empresa.setRazonsocial(empre.getRazonsocial());
		empresa.setTelfonofijo(empre.getTelefonofijo());
		
		// Actualizar estado si se proporciona
		if (empre.getEstado() != null && !empre.getEstado().trim().isEmpty()) {
			try {
				// Convertir String a enum con validación
				String estadoStr = empre.getEstado().toUpperCase().trim();
				Estado nuevoEstado = Estado.valueOf(estadoStr);
				empresa.setEstado(nuevoEstado);
			} catch (IllegalArgumentException e) {
				throw new Exception("Estado inválido: '" + empre.getEstado() + "'. Valores válidos: ACTIVA, INACTIVA");
			}
		}
		
		// Actualizar imagen solo si se proporciona una nueva
		if (archivo != null && !archivo.isEmpty()) {
			empresa.setImagenEmpresa(archivo.getBytes());
			empresa.setTipoImagen(archivo.getContentType());
		}

		// Procesar usuarios (crear nuevos y actualizar existentes) solo si no es nulo
		if (empre.getUsuarios() != null && !empre.getUsuarios().isEmpty()) {
			procesarUsuariosParaEmpresa(empre.getUsuarios());
		} else if (empre.getUsuarios() == null) {
			System.out.println("La lista de usuarios es nula, no se procesarán usuarios");
		}

		emprerepo.save(empresa);
	}


	public void updateEstado(Integer id, String estado) {

		Empresa empresa = emprerepo.findById(id.longValue()).orElse(null);
		if (empresa == null) {
			throw new Empresaexception("Empresa no encontrada con id: " + id);
		}

		try {
			// Convertir String a enum con validación
			String estadoStr = estado.toUpperCase().trim();
			Estado nuevoEstado = Estado.valueOf(estadoStr);
			empresa.setEstado(nuevoEstado);
			emprerepo.save(empresa);
		} catch (IllegalArgumentException e) {
			throw new Empresaexception("Estado inválido: '" + estado + "'. Valores válidos: ACTIVA, INACTIVA");
		}


	}

	private void crearUsuarioParaEmpresa(Empresaresponse empre) {
		try {
			// Verificar si hay usuarios para crear
			if (empre.getUsuarios() == null || empre.getUsuarios().isEmpty()) {
				System.out.println("No hay usuarios para crear");
				return;
			}
			
			// Procesar cada usuario de la lista
			for (Empresaresponse.usuario usuarioDto : empre.getUsuarios()) {
				crearUsuarioIndividual(usuarioDto, empre);
			}
			
		} catch (Exception e) {
			// En caso de error, lo registramos pero no interrumpimos la creación de la empresa
			System.err.println("Error al crear usuarios para la empresa: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void crearUsuarioIndividual(Empresaresponse.usuario usuarioDto, Empresaresponse empre) {
		try {
			// Obtener rol por nombre
			Roles rol = obtenerRolPorNombre(usuarioDto.getRol());
			if (rol == null) {
				System.err.println("No se encontró el rol: " + usuarioDto.getRol() + " para el usuario: " + usuarioDto.getUsuario());
				return;
			}
			
			// Asignar todos los permisos al rol si es Admin
			if ("Admin".equalsIgnoreCase(rol.getNombre())) {
				asignarTodosLosPermisosAlRol(rol);
			}
			
			// Crear usuario
			Usuario nuevoUsuario = new Usuario();
			nuevoUsuario.setNombre(usuarioDto.getUsuario()); // Usar el nombre de usuario como nombre
			nuevoUsuario.setUsuario(usuarioDto.getUsuario());
			
			// Usar contraseña proporcionada o generar una por defecto
			String contrasena = usuarioDto.getContrasena() != null && !usuarioDto.getContrasena().trim().isEmpty() 
				? usuarioDto.getContrasena() : "admin123";
			
			// Encriptar contraseña
			String contrasenaEncriptada = PasswordUtils.encrypt(contrasena);
			System.out.println("Usuario: " + usuarioDto.getUsuario() + " - Contraseña original: " + contrasena);
			System.out.println("Usuario: " + usuarioDto.getUsuario() + " - Contraseña encriptada: " + contrasenaEncriptada);
			
			nuevoUsuario.setContrasena(contrasenaEncriptada);
			nuevoUsuario.setEstado(usuarioDto.getEstado() != null ? usuarioDto.getEstado() : "ACTIVO");
			nuevoUsuario.setCodigousuariocreado(1); // Usuario por defecto que crea
			nuevoUsuario.setCodigorol(rol);
			
			// Asignar bodega si se proporcionó
			if (usuarioDto.getBodega() != null && !usuarioDto.getBodega().trim().isEmpty()) {
				Bodegas bodega = obtenerBodegaPorNombre(usuarioDto.getBodega());
				if (bodega != null) {
					// Aquí necesitarías tener una relación entre Usuario y Bodega
					// Por ahora, solo registramos que se encontró la bodega
					Usuariobodega usuariobodega = new Usuariobodega();
					usuariobodega.setUsuarioid(nuevoUsuario);
					usuariobodega.setBodegaid(bodega);
					bodegarepositori.save(usuariobodega);
					System.out.println("Bodega asignada para usuario " + usuarioDto.getUsuario() + ": " + bodega.getNombre());
				} else {
					System.err.println("No se encontró la bodega: " + usuarioDto.getBodega() + " para el usuario: " + usuarioDto.getUsuario());
				}
			}
			
			Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
			System.out.println("Usuario guardado con ID: " + usuarioGuardado.getCodigo());
			System.out.println("Contraseña guardada en BD: " + usuarioGuardado.getContrasena());
			
		} catch (Exception e) {
			System.err.println("Error al crear usuario individual " + usuarioDto.getUsuario() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Roles obtenerRolPorNombre(String nombreRol) {
		try {
			if (nombreRol == null || nombreRol.trim().isEmpty()) {
				return null;
			}
			
			// Buscar rol por nombre (ignorando mayúsculas/minúsculas)
			List<Roles> roles = rolesRepository.findAll();
			for (Roles rol : roles) {
				if (rol.getNombre() != null && rol.getNombre().equalsIgnoreCase(nombreRol.trim())) {
					return rol;
				}
			}
			
			// Si no se encuentra, crear un nuevo rol con ese nombre
			Roles nuevoRol = new Roles();
			nuevoRol.setNombre(nombreRol.trim());
			return rolesRepository.save(nuevoRol);
			
		} catch (Exception e) {
			System.err.println("Error al obtener rol por nombre '" + nombreRol + "': " + e.getMessage());
			return null;
		}
	}
	
	private Bodegas obtenerBodegaPorNombre(String nombreBodega) {
		try {
			if (nombreBodega == null || nombreBodega.trim().isEmpty()) {
				return null;
			}
			
			// Buscar bodega por nombre (ignorando mayúsculas/minúsculas)
			List<Bodegas> bodegas = repotoribodega.findAll();
			for (Bodegas bodega : bodegas) {
				if (bodega.getNombre() != null && bodega.getNombre().equalsIgnoreCase(nombreBodega.trim())) {
					return bodega;
				}
			}
			
			return null; // No crear bodega si no existe
			
		} catch (Exception e) {
			System.err.println("Error al obtener bodega por nombre '" + nombreBodega + "': " + e.getMessage());
			return null;
		}
	}
	
	private void procesarUsuariosParaEmpresa(List<Empresaresponse.usuario> usuarios) {
		try {
			for (Empresaresponse.usuario usuarioDto : usuarios) {
				if (usuarioDto.getCodigo() != null && usuarioDto.getCodigo() > 0) {
					// Actualizar usuario existente
					actualizarUsuarioIndividual(usuarioDto);
				} else {
					// Crear nuevo usuario
					crearUsuarioIndividual(usuarioDto, null);
				}
			}
		} catch (Exception e) {
			System.err.println("Error al procesar usuarios para la empresa: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void actualizarUsuarioIndividual(Empresaresponse.usuario usuarioDto) {
		try {
			// Buscar usuario existente por código
			Optional<Usuario> usuarioOpt = usuarioRepository.findByCodigo(usuarioDto.getCodigo());
			if (usuarioOpt.isEmpty()) {
				System.err.println("No se encontró el usuario con código: " + usuarioDto.getCodigo());
				return;
			}
			
			Usuario usuarioExistente = usuarioOpt.get();
			
			// Actualizar rol si se proporcionó
			if (usuarioDto.getRol() != null && !usuarioDto.getRol().trim().isEmpty()) {
				Roles rol = obtenerRolPorNombre(usuarioDto.getRol());
				if (rol != null) {
					usuarioExistente.setCodigorol(rol);
					// Asignar todos los permisos al rol si es Admin
					if ("Admin".equalsIgnoreCase(rol.getNombre())) {
						asignarTodosLosPermisosAlRol(rol);
					}
				} else {
					System.err.println("No se encontró el rol: " + usuarioDto.getRol() + " para el usuario: " + usuarioDto.getUsuario());
				}
			}
			
			// Actualizar nombre de usuario si se proporcionó
			if (usuarioDto.getUsuario() != null && !usuarioDto.getUsuario().trim().isEmpty()) {
				usuarioExistente.setUsuario(usuarioDto.getUsuario());
				usuarioExistente.setNombre(usuarioDto.getUsuario());
			}
			
			// Actualizar contraseña si se proporcionó
			if (usuarioDto.getContrasena() != null && !usuarioDto.getContrasena().trim().isEmpty()) {
				String contrasenaEncriptada = PasswordUtils.encrypt(usuarioDto.getContrasena());
				usuarioExistente.setContrasena(contrasenaEncriptada);
				System.out.println("Contraseña actualizada para usuario: " + usuarioDto.getUsuario());
			}
			
			// Actualizar estado si se proporcionó
			if (usuarioDto.getEstado() != null && !usuarioDto.getEstado().trim().isEmpty()) {
				usuarioExistente.setEstado(usuarioDto.getEstado());
			}
			
			// Actualizar bodega si se proporcionó
			if (usuarioDto.getBodega() != null && !usuarioDto.getBodega().trim().isEmpty()) {
				Bodegas bodega = obtenerBodegaPorNombre(usuarioDto.getBodega());
				if (bodega != null) {
					// Aquí necesitarías tener una relación entre Usuario y Bodega
					// Por ahora, solo registramos que se encontró la bodega
					System.out.println("Bodega actualizada para usuario " + usuarioDto.getUsuario() + ": " + bodega.getNombre());
				} else {
					System.err.println("No se encontró la bodega: " + usuarioDto.getBodega() + " para el usuario: " + usuarioDto.getUsuario());
				}
			}
			
			Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
			System.out.println("Usuario actualizado con ID: " + usuarioActualizado.getCodigo());
			
		} catch (Exception e) {
			System.err.println("Error al actualizar usuario individual " + usuarioDto.getUsuario() + " (código: " + usuarioDto.getCodigo() + "): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void asignarTodosLosPermisosAlRol(Roles rol) {
		try {
			// Obtener todos los permisos disponibles
			List<Permiso> todosLosPermisos = permisoRepository.findAll();
			
			// Para cada permiso, crear la relación con el rol Admin
			for (Permiso permiso : todosLosPermisos) {
				// Verificar si ya existe la relación para evitar duplicados
				List<PermisoRol> relacionesExistentes = permisoRolRepository.findPermisosActivosByRol(rol.getCodigo());
				boolean yaExiste = relacionesExistentes.stream()
					.anyMatch(pr -> pr.getCodigopermiso().getCodigo() == permiso.getCodigo());
				
				if (!yaExiste) {
					PermisoRol permisoRol = new PermisoRol();
					permisoRol.setCodigorol(rol);
					permisoRol.setCodigopermiso(permiso);
					permisoRol.setEstado("ACTIVO");
					permisoRolRepository.save(permisoRol);
				}
			}
			
		} catch (Exception e) {
			System.err.println("Error al asignar permisos al rol Admin: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean crearBackupSchema(String nombreEmpresa) {
		try {
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String backupPath = "C:\\Users\\AUXPAZZIOLI\\Documents\\backupsempresas";
			String backupFileName = nombreEmpresa + "_" + timestamp + ".sql";
			
			Path path = Paths.get(backupPath);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			String backupCommand = String.format(
					"mysqldump -u root -proot125 --single-transaction --routines --triggers %s > \"%s\\%s\"",
					nombreEmpresa,
					backupPath,
					backupFileName
			);
			
			Process process = Runtime.getRuntime().exec("cmd /c " + backupCommand);
			int exitCode = process.waitFor();
			
			return exitCode == 0;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean eliminarEmpresaConBackup(String nombreTenant) {
		try {
			System.out.println("Intentando eliminar empresa con tenant: " + nombreTenant);
			
			// Verificar si el schema existe antes de intentar eliminarlo
			String checkSchemaSql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + nombreTenant + "'";
			List<Map<String, Object>> schemas = jdbc.queryForList(checkSchemaSql);
			
			System.out.println("Schemas encontrados: " + schemas.size());
			for (Map<String, Object> schema : schemas) {
				System.out.println("Schema: " + schema.get("SCHEMA_NAME"));
			}
			
			if (schemas.isEmpty()) {
				throw new RuntimeException("El schema '" + nombreTenant + "' no existe en la base de datos");
			}
			
			if (!crearBackupSchema(nombreTenant)) {
				throw new RuntimeException("No se pudo crear el backup del schema");
			}
			
			// Intentar eliminar el schema
			String dropSchemaSql = "DROP SCHEMA IF EXISTS `" + nombreTenant + "`";
			System.out.println("Ejecutando SQL: " + dropSchemaSql);
			
			jdbc.execute(dropSchemaSql);
			
			// Verificar que se eliminó correctamente
			List<Map<String, Object>> schemasAfter = jdbc.queryForList(checkSchemaSql);
			if (!schemasAfter.isEmpty()) {
				throw new RuntimeException("El schema no pudo ser eliminado");
			}
			
			System.out.println("Schema eliminado exitosamente: " + nombreTenant);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error al eliminar empresa: " + e.getMessage());
		}
	}
	
	public List<String> listarTodosLosSchemas() {
		try {
			String sql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME NOT IN ('information_schema', 'mysql', 'performance_schema', 'sys')";
			List<Map<String, Object>> schemas = jdbc.queryForList(sql);
			List<String> schemaNames = new ArrayList<>();
			for (Map<String, Object> schema : schemas) {
				schemaNames.add((String) schema.get("SCHEMA_NAME"));
			}
			return schemaNames;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error al listar schemas: " + e.getMessage());
		}


	}
	
}
