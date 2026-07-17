package com.pazzioliweb.empresaauth.service;

import com.pazzioliweb.empresaback.dtos.EmpresaTenantProjection;
import com.pazzioliweb.empresaback.exception.Empresaexception;
import com.pazzioliweb.empresasback.entity.Estado;
import com.pazzioliweb.empresasback.entity.TipoLicencia;
import com.pazzioliweb.productosmodule.entity.Usuariobodega;
import com.pazzioliweb.productosmodule.repositori.UsuariobodegaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${spring.datasource.username:root}")
	private String dbUser;

	@Value("${spring.datasource.password:root}")
	private String dbPass;

	@Value("${app.backup.path:C:\\backups}")
	private String backupBasePath;

	@Value("${app.backup.mysqldump:mysqldump}")
	private String mysqldumpCmd;

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
			List<Integer> municipioCodes = empre.getSucursales().stream()
				.map(s -> s.getMunicipio().getCodigo()).distinct().collect(Collectors.toList());
			List<Integer> departamentoCodes = empre.getSucursales().stream()
				.map(s -> s.getDepartamento().getCodigo()).distinct().collect(Collectors.toList());
			List<Integer> paisCodes = empre.getSucursales().stream()
				.map(s -> s.getPais().getCodigo()).distinct().collect(Collectors.toList());

			Map<Integer, Municipio> municipioMap = municipiorepositori.findByCodigoIn(municipioCodes).stream()
				.collect(Collectors.toMap(Municipio::getCodigo, m -> m));
			Map<Integer, Departamento> departamentoMap = departamentorepositori.findByCodigoIn(departamentoCodes).stream()
				.collect(Collectors.toMap(Departamento::getCodigo, d -> d));
			Map<Integer, Pais> paisMap = paisrepository.findByCodigoIn(paisCodes).stream()
				.collect(Collectors.toMap(Pais::getCodigo, p -> p));

			List<Bodegas> bodegas = new ArrayList<>();
			for(Sucursales sucu: empre.getSucursales()) {
				Bodegas bodega = new Bodegas();
				bodega.setCelular(sucu.getCelular());
				bodega.setCodigodepartamento(departamentoMap.get(sucu.getDepartamento().getCodigo()));
				bodega.setCodigomunicipio(municipioMap.get(sucu.getMunicipio().getCodigo()));
				bodega.setCodigopais(paisMap.get(sucu.getPais().getCodigo()));
				bodega.setTelefono(sucu.getTelefonofijo());
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
			List<Integer> codigosImpuestos = empre.getImpuestos().stream()
				.map(i -> i.getCodigo())
				.collect(Collectors.toList());
			List<Impuestos> impuestosEntidades = impuestorepositorio.findByCodigoIn(codigosImpuestos);
			impuestosEntidades.forEach(i -> i.setEstado("ACTIVO"));
			impuestorepositorio.saveAll(impuestosEntidades);
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
		empresa.setFecharenovacion(empre.getFecharenovacion());
		empresa.setEstado(Estado.ACTIVA);

		// Establecer tipo de licencia
		if (empre.getTipoLicencia() != null && !empre.getTipoLicencia().trim().isEmpty()) {
			try {
				TipoLicencia tipoLicencia = TipoLicencia.valueOf(empre.getTipoLicencia().toUpperCase());
				empresa.setTipolicencia(tipoLicencia);
			} catch (IllegalArgumentException e) {
				// Si el valor no es válido, usar MENSUAL por defecto
				empresa.setTipolicencia(TipoLicencia.MENSUAL);
				System.err.println("Tipo de licencia inválido: " + empre.getTipoLicencia() + ", usando MENSUAL por defecto");
			}
		} else {
			// Valor por defecto si no se proporciona
			empresa.setTipolicencia(TipoLicencia.MENSUAL);
		}

		// Datos fiscales DIAN (obligatorios para XML UBL TaxLevelCode)
		empresa.setResponsabilidadFiscal(empre.getResponsabilidadFiscal());
		empresa.setTipoContribuyente(empre.getTipoContribuyente());
		empresa.setGranContribuyente(empre.getGranContribuyente() != null ? empre.getGranContribuyente() : false);
		empresa.setAutorretenedor(empre.getAutorretenedor() != null ? empre.getAutorretenedor() : false);
		empresa.setResponsableIva(empre.getResponsableIva() != null ? empre.getResponsableIva() : true);

		if (archivo != null && !archivo.isEmpty()) {
			empresa.setImagenEmpresa(archivo.getBytes());
		    empresa.setTipoImagen(archivo.getContentType()); // Guardamos el tipo MIME
		}

		emprerepo.save(empresa);
		
		// Primero procesar roles y sus permisos, luego crear usuarios
		procesarRolesYPermisos(empre.getRolespermisos());
		crearUsuarioParaEmpresa(empre);
		
		
		
		
		
		  
		
		
		
	}

      public  EmpresaTenantProjection actulizarlicencia(Empresaresponse empre, int codigoempresa){
		Empresa empresa=emprerepo.findByCodigo(codigoempresa).get();
		empresa.setFechainiciolicencia(empre.getFechainiciolicencia());
		empresa.setFechafinallicencia(empre.getFechafinallicencia());
		empresa.setFecharenovacion(LocalDate.now());
		empresa.setPlazo(empre.getPlazo());
		empresa.setNumerousuarios(empre.getNumerousuarios());
		return  EmpresaTenantProjection.mapperdtos(emprerepo.save(empresa));

	  }

	public EmpresaTenantProjection renovarLicencia(int codigoempresa) {
		Empresa empresa = emprerepo.findByCodigo(codigoempresa).get();
		
		if (empresa.getTipolicencia() == null) {
			throw new RuntimeException("Tipo de licencia no definido para la empresa");
		}
		
		LocalDate nuevaFechaFinal;
		if (empresa.getTipolicencia() == TipoLicencia.MENSUAL) {
			nuevaFechaFinal = LocalDate.now().plusMonths(1);  // ✓

		} else if (empresa.getTipolicencia() == TipoLicencia.ANUAL) {
			nuevaFechaFinal = LocalDate.now().plusYears(1);   // ✓
		
		} else {
			throw new RuntimeException("Tipo de licencia no reconocido");
		}
		
		empresa.setFechafinallicencia(nuevaFechaFinal);
		empresa.setFecharenovacion(LocalDate.now());
		
		return EmpresaTenantProjection.mapperdtos(emprerepo.save(empresa));
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
			// Bodegas existentes del tenant actual
			Map<String, Bodegas> existentesMap = repotoribodega.findAll().stream()
				.collect(Collectors.toMap(
					b -> b.getNombre() != null ? b.getNombre().toLowerCase().trim() : "",
					b -> b,
					(a, b) -> a
				));

			// Precargar datos geográficos en 3 queries para todas las sucursales no eliminadas
			List<Sucursales> sucursalesActivas = empre.getSucursales().stream()
				.filter(s -> !s.isEliminar()).collect(Collectors.toList());

			Map<Integer, Municipio> municipioMap = new HashMap<>();
			Map<Integer, Departamento> departamentoMap = new HashMap<>();
			Map<Integer, Pais> paisMap = new HashMap<>();

			if (!sucursalesActivas.isEmpty()) {
				List<Integer> municipioCodes = sucursalesActivas.stream()
					.map(s -> s.getMunicipio().getCodigo()).distinct().collect(Collectors.toList());
				List<Integer> departamentoCodes = sucursalesActivas.stream()
					.map(s -> s.getDepartamento().getCodigo()).distinct().collect(Collectors.toList());
				List<Integer> paisCodes = sucursalesActivas.stream()
					.map(s -> s.getPais().getCodigo()).distinct().collect(Collectors.toList());

				municipioMap = municipiorepositori.findByCodigoIn(municipioCodes).stream()
					.collect(Collectors.toMap(Municipio::getCodigo, m -> m));
				departamentoMap = departamentorepositori.findByCodigoIn(departamentoCodes).stream()
					.collect(Collectors.toMap(Departamento::getCodigo, d -> d));
				paisMap = paisrepository.findByCodigoIn(paisCodes).stream()
					.collect(Collectors.toMap(Pais::getCodigo, p -> p));
			}

			List<Bodegas> bodegasActualizar = new ArrayList<>();
			List<Bodegas> bodegasNuevas = new ArrayList<>();
			List<Bodegas> bodegasEliminar = new ArrayList<>();
			Set<String> sucursalesRecibidas = new HashSet<>();

			for(Sucursales sucu : empre.getSucursales()) {
				String nombreSucursal = sucu.getNombre() != null ? sucu.getNombre().toLowerCase().trim() : "";
				sucursalesRecibidas.add(nombreSucursal);
				Bodegas bodegaExistente = existentesMap.get(nombreSucursal);

				if(sucu.isEliminar()) {
					if(bodegaExistente != null) bodegasEliminar.add(bodegaExistente);
				} else {
					Municipio municipio = municipioMap.get(sucu.getMunicipio().getCodigo());
					Departamento departamento = departamentoMap.get(sucu.getDepartamento().getCodigo());
					Pais pais = paisMap.get(sucu.getPais().getCodigo());

					if(bodegaExistente != null) {
						bodegaExistente.setCelular(sucu.getCelular());
						bodegaExistente.setCodigodepartamento(departamento);
						bodegaExistente.setCodigomunicipio(municipio);
						bodegaExistente.setCodigopais(pais);
						bodegaExistente.setTelefono(sucu.getTelefonofijo());
						bodegaExistente.setDireccion(sucu.getDireccion());
						bodegaExistente.setCodigopostal(sucu.getCodigopostal());
						bodegaExistente.setCorreo(sucu.getCorreo());
						bodegasActualizar.add(bodegaExistente);
					} else {
						Bodegas nuevaBodega = new Bodegas();
						nuevaBodega.setCelular(sucu.getCelular());
						nuevaBodega.setCodigodepartamento(departamento);
						nuevaBodega.setCodigomunicipio(municipio);
						nuevaBodega.setCodigopais(pais);
						nuevaBodega.setTelefono(sucu.getTelefonofijo());
						nuevaBodega.setDireccion(sucu.getDireccion());
						nuevaBodega.setCodigopostal(sucu.getCodigopostal());
						nuevaBodega.setNombre(sucu.getNombre());
						nuevaBodega.setCodigosucursal(sucu.getCodigosucursal());
						nuevaBodega.setCorreo(sucu.getCorreo());
						bodegasNuevas.add(nuevaBodega);
					}
				}
			}

			if(!bodegasEliminar.isEmpty()) repotoribodega.deleteAll(bodegasEliminar);
			if(!bodegasActualizar.isEmpty()) repotoribodega.saveAll(bodegasActualizar);
			if(!bodegasNuevas.isEmpty()) repotoribodega.saveAll(bodegasNuevas);
		}
		
		// Actualizar impuestos
		if(!empre.getImpuestos().isEmpty()) {
			List<Integer> codigosImpuestos = empre.getImpuestos().stream()
				.map(i -> i.getCodigo())
				.collect(Collectors.toList());
			List<Impuestos> impuestosEntidades = impuestorepositorio.findByCodigoIn(codigosImpuestos);
			impuestosEntidades.forEach(i -> i.setEstado("ACTIVO"));
			impuestorepositorio.saveAll(impuestosEntidades);
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

		// Datos fiscales DIAN (TaxLevelCode UBL). Solo actualiza si vienen en el request.
		if (empre.getResponsabilidadFiscal() != null) empresa.setResponsabilidadFiscal(empre.getResponsabilidadFiscal());
		if (empre.getTipoContribuyente() != null)    empresa.setTipoContribuyente(empre.getTipoContribuyente());
		if (empre.getGranContribuyente() != null)    empresa.setGranContribuyente(empre.getGranContribuyente());
		if (empre.getAutorretenedor() != null)       empresa.setAutorretenedor(empre.getAutorretenedor());
		if (empre.getResponsableIva() != null)       empresa.setResponsableIva(empre.getResponsableIva());

		// Actualizar fechas de licencia si se proporcionan
		if (empre.getFechainiciolicencia() != null) empresa.setFechainiciolicencia(empre.getFechainiciolicencia());
		if (empre.getFechafinallicencia() != null) empresa.setFechafinallicencia(empre.getFechafinallicencia());
		if (empre.getFecharenovacion() != null) empresa.setFecharenovacion(empre.getFecharenovacion());

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
			// El rol ya fue creado/verificado en procesarRolesYPermisos — solo buscarlo
			if (usuarioDto.getRol() == null || usuarioDto.getRol().trim().isEmpty()) {
				System.err.println("El usuario " + usuarioDto.getUsuario() + " no tiene rol definido");
				return;
			}
			Roles rol = rolesRepository.findByNombreIgnoreCase(usuarioDto.getRol().trim()).orElse(null);
			if (rol == null) {
				System.err.println("No se encontró el rol: " + usuarioDto.getRol() + " para el usuario: " + usuarioDto.getUsuario());
				return;
			}

			Usuario nuevoUsuario = new Usuario();
			nuevoUsuario.setNombre(usuarioDto.getNombre() != null && !usuarioDto.getNombre().trim().isEmpty()
					? usuarioDto.getNombre() : usuarioDto.getUsuario());
			nuevoUsuario.setUsuario(usuarioDto.getUsuario());

			String contrasena = usuarioDto.getContrasena() != null && !usuarioDto.getContrasena().trim().isEmpty()
					? usuarioDto.getContrasena() : "admin123";
			nuevoUsuario.setContrasena(PasswordUtils.encrypt(contrasena));
			nuevoUsuario.setEstado(usuarioDto.getEstado() != null ? usuarioDto.getEstado() : "ACTIVO");
			nuevoUsuario.setCodigousuariocreado(1);
			nuevoUsuario.setCodigorol(rol);

			if (usuarioDto.getBodega() != null && !usuarioDto.getBodega().trim().isEmpty()) {
				Bodegas bodega = obtenerBodegaPorNombre(usuarioDto.getBodega());
				if (bodega != null) {
					Usuariobodega usuariobodega = new Usuariobodega();
					usuariobodega.setUsuarioid(nuevoUsuario);
					usuariobodega.setBodegaid(bodega);
					bodegarepositori.save(usuariobodega);
				} else {
					System.err.println("No se encontró la bodega: " + usuarioDto.getBodega() + " para el usuario: " + usuarioDto.getUsuario());
				}
			}

			Usuario guardado = usuarioRepository.save(nuevoUsuario);
			System.out.println("Usuario guardado con ID: " + guardado.getCodigo());

		} catch (Exception e) {
			System.err.println("Error al crear usuario individual " + usuarioDto.getUsuario() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Roles obtenerRolPorNombre(String nombreRol) {
		if (nombreRol == null || nombreRol.trim().isEmpty()) {
			return null;
		}
		return rolesRepository.findByNombreIgnoreCase(nombreRol.trim())
			.orElseGet(() -> {
				Roles nuevoRol = new Roles();
				nuevoRol.setNombre(nombreRol.trim());
				return rolesRepository.save(nuevoRol);
			});
	}

	private void procesarRolesYPermisos(List<Empresaresponse.RolPermiso> rolesPermisos) {
		if (rolesPermisos == null || rolesPermisos.isEmpty()) return;

		for (Empresaresponse.RolPermiso rolPermiso : rolesPermisos) {
			try {
				Roles rol;

				if (rolPermiso.getIdrol() == null) {
					// idrol nulo → crear el rol nuevo
					Roles nuevo = new Roles();
					nuevo.setNombre(rolPermiso.getRol());
					rol = rolesRepository.save(nuevo);
				} else {
					// idrol tiene valor → el rol ya existe, solo buscarlo
					rol = rolesRepository.findByCodigo(rolPermiso.getIdrol()).orElse(null);
					if (rol == null) {
						System.err.println("No se encontró el rol con id: " + rolPermiso.getIdrol());
						continue;
					}
				}

				// Crear relaciones permiso-rol que no existan aún
				List<Integer> permisoIds = rolPermiso.getPermisos();
				if (permisoIds == null || permisoIds.isEmpty()) continue;

				Set<Integer> yaAsignados = permisoRolRepository.findPermisosActivosByRol(rol.getCodigo())
						.stream()
						.map(pr -> pr.getCodigopermiso().getCodigo())
						.collect(Collectors.toSet());

				List<PermisoRol> nuevasRelaciones = new ArrayList<>();
				for (Integer permisoId : permisoIds) {
					if (yaAsignados.contains(permisoId)) continue;
					permisoRepository.findByCodigo(permisoId).ifPresent(permiso -> {
						PermisoRol pr = new PermisoRol();
						pr.setCodigorol(rol);
						pr.setCodigopermiso(permiso);
						pr.setEstado("ACTIVO");
						nuevasRelaciones.add(pr);
					});
				}
				if (!nuevasRelaciones.isEmpty()) {
					permisoRolRepository.saveAll(nuevasRelaciones);
				}

			} catch (Exception e) {
				System.err.println("Error al procesar rol '" + rolPermiso.getRol() + "': " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private Bodegas obtenerBodegaPorNombre(String nombreBodega) {
		if (nombreBodega == null || nombreBodega.trim().isEmpty()) {
			return null;
		}
		return repotoribodega.findByNombreIgnoreCase(nombreBodega.trim()).orElse(null);
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
			}
			
			// Actualizar nombre si se proporcionó
			if (usuarioDto.getNombre() != null && !usuarioDto.getNombre().trim().isEmpty()) {
				usuarioExistente.setNombre(usuarioDto.getNombre());
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
			List<Permiso> todosLosPermisos = permisoRepository.findAll();
			List<PermisoRol> relacionesExistentes = permisoRolRepository.findPermisosActivosByRol(rol.getCodigo());
			Set<Integer> permisosYaAsignados = relacionesExistentes.stream()
				.map(pr -> pr.getCodigopermiso().getCodigo())
				.collect(Collectors.toSet());

			List<PermisoRol> nuevosPermisos = new ArrayList<>();
			for (Permiso permiso : todosLosPermisos) {
				if (!permisosYaAsignados.contains(permiso.getCodigo())) {
					PermisoRol permisoRol = new PermisoRol();
					permisoRol.setCodigorol(rol);
					permisoRol.setCodigopermiso(permiso);
					permisoRol.setEstado("ACTIVO");
					nuevosPermisos.add(permisoRol);
				}
			}
			if (!nuevosPermisos.isEmpty()) {
				permisoRolRepository.saveAll(nuevosPermisos);
			}
		} catch (Exception e) {
			System.err.println("Error al asignar permisos al rol Admin: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean crearBackupSchema(String nombreEmpresa) {
		try {
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String backupFileName = nombreEmpresa + "_" + timestamp + ".sql";

			Path path = Paths.get(backupBasePath);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			String backupCommand = String.format(
					"%s -u %s -p%s --single-transaction --routines --triggers %s > \"%s\\%s\"",
					mysqldumpCmd,
					dbUser,
					dbPass,
					nombreEmpresa,
					backupBasePath,
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
