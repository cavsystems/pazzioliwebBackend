package com.pazzioliweb.empresaback.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pazzioliweb.empresaback.dtos.EmpresaTenantProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pazzioliweb.commonbacken.repositorio.DepartamentoRepositori;
import com.pazzioliweb.commonbacken.repositorio.ImpuestosRepositori;
import com.pazzioliweb.commonbacken.repositorio.MunicipioRepositori;
import com.pazzioliweb.commonbacken.repositorio.PaisRepositori;
import com.pazzioliweb.commonbacken.services.TenantService;
import com.pazzioliweb.commonbacken.util.Nombredb;
import com.pazzioliweb.empresaauth.service.EmpresaService;
import com.pazzioliweb.empresaauth.service.Insertarregistrosjoin;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.commonbacken.conexiondb.TenantRegister;
import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Impuestos;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Pais;
import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;

@Component
@RestController
@RequestMapping("/api/empresa")
public class Empresacontroller {
	/* Aquí se inyecta la fábrica de EntityManager de Spring/Hibernate*/
	/* El EntityManagerFactory está configurado con tu soporte multi-tenant, por lo que al crear un EntityManager 
	 * se usará el tenant definido en TenantContext.*/
	@Autowired
	private EntityManagerFactory emf;
	@Autowired
	  private JdbcTemplate jdbc;
	  @Autowired
	  private TenantService tenantService;
	  @Autowired 
	  private TipopersonaRepository tipopersonarepositori;
	  @Autowired 
	  private EmpresaRepositori repoempresa;
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
	  private Insertarregistrosjoin insertjoi;
	  private Datosempresa datosempresa=new Datosempresa();
	  @Autowired
	  private  TenantRegister register;
	  @Autowired
	  private EmpresaService serv;
	  private Map<String, Object> response = new HashMap<>();
	  private Map<String, Boolean> responseempresa = new HashMap<>();
	 	 @Transactional
	 //@PostMapping("/crear")
	 	@PostMapping(value = "/crear", consumes = "multipart/form-data")
	 public ResponseEntity<Map<String, Object>> crearEmpresa(@RequestPart("dto") Empresaresponse dto,@RequestPart(value = "archivo", required = false) MultipartFile archivo) throws Exception{
	    	 // Aquí request.db es el tenantId}
		 
		nombredb.setNombre(dto.getRazonsocial());
		
		  String schema = nombredb.getNombre().toLowerCase().replaceAll("[^a-z0-9_]", "");
		  
		  // Verificar si ya existe un esquema con ese nombre
		  Integer schemaExists = jdbc.queryForObject(
			  "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?",
			  Integer.class, schema);
		  
		  if (schemaExists != null && schemaExists > 0) {
			  throw new RuntimeException("Ya existe una empresa creada con el nombre: " + dto.getRazonsocial());
		  }
		  
		   jdbc.execute("CREATE SCHEMA IF NOT EXISTS `" + schema + "`");
		   tenantService.initTenantSchema(schema);
		  	/*Esto establece el tenant actual.  */	  
		   /* Gracias a tu CurrentTenantIdentifierResolver, 
		    * Hibernate sabrá a qué esquema o base de datos debe conectarse cuando abras el EntityManager. */
		   TenantContext.setCurrentTenant(schema);
		   register.registerNewTenant(schema);
		   /*Se crea un EntityManager nuevo, que abrirá una conexión basada en el tenant que se haya establecido.  */
		   /*A diferencia de usar un repositorio inyectado, aquí controlas manualmente la sesión de Hibernate.  */
		   EntityManager em = emf.createEntityManager(); // se abre con el tenant actual
		   /*Inicia una transacción manual.*/
		   /*Necesaria si vas a persistir entidades con em.persist() o ejecutar queries 
		    * nativas que modifiquen la base de datos.*/
		    em.getTransaction().begin();

		    // persistir normalmente
		    /* Ese fragmento de código está mostrando cómo crear y usar un EntityManager 
		     * manualmente para persistir datos en la base de datos usando Hibernate/JPA,
		     *  y cómo se conecta dinámicamente al tenant actual. Vamos  */
           /* Se hace commit de la transacción para que los cambios se guarden en la base de datos.*/
		    
		    em.getTransaction().commit();
		    /*Se cierra el EntityManager y la conexión se libera.*/
		    em.close();
		   
		   class mensajesuccesempres{
			   String mensaje;
			   public String getMensaje() {
				return mensaje;
			}
			   public void setMensaje(String mensaje) {
				   this.mensaje = mensaje;
			   }
			   public boolean isEstado() {
				   return estado;
			   }
			   public void setEstado(boolean estado) {
				   this.estado = estado;
			   }
			   boolean estado;
			   public  mensajesuccesempres(String mensaje,boolean estado) {
				   
				   this.mensaje=mensaje;
				   this.estado=estado;
			   }
			   
   public  mensajesuccesempres() {
				   
				  
			   }
		   }
		   serv.crearempresa(dto,schema,archivo);
		   // modoPOS: aplicar la configuración de contabilidad (suite/POS puro) al schema recién creado.
		   aplicarModoContabilidad(schema, dto);
		   response.put("respuesta", new mensajesuccesempres("Empresa creada",true) );
		   return ResponseEntity.status(HttpStatus.CREATED).body(response);
		 	 }
	 
	 @Transactional
	 @PostMapping(value = "/actualizar/{id}", consumes = "multipart/form-data")
	 public ResponseEntity<Map<String, Object>> actualizarEmpresa(
			 @PathVariable Integer id,
			 @RequestPart("dto") Empresaresponse dto,
			 @RequestPart(value = "archivo", required = false) MultipartFile archivo) throws Exception{

			  try {
				  // Establecer el tenant actual basado en la empresa existente
				  Optional<Empresa> empresaOpt = repoempresa.findById(id.longValue());
				  if (empresaOpt.isEmpty()) {
					  response.clear();
					  response.put("error", "Empresa no encontrada con ID: " + id);
					  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
				  }
				  nombredb.setNombre(dto.getRazonsocial());
				  // Obtener el schema/tenant de la empresa existente
				String schema = nombredb.getNombre().toLowerCase().replaceAll("[^a-z0-9_]", "");
				  TenantContext.setCurrentTenant(schema);

				  class mensajesuccesempres{
					  String mensaje;
					  public String getMensaje() {
						  return mensaje;
					  }
					  public void setMensaje(String mensaje) {
						  this.mensaje = mensaje;
					  }
					  public boolean isEstado() {
						  return estado;
					  }
					  public void setEstado(boolean estado) {
						  this.estado = estado;
					  }
					  boolean estado;
					  public  mensajesuccesempres(String mensaje,boolean estado) {
						  this.mensaje=mensaje;
						  this.estado=estado;
					  }

					  public  mensajesuccesempres() {

					  }
				  }

				  serv.updateEmpresa(dto, schema, archivo, id);
				  // modoPOS: aplicar la configuración de contabilidad (suite/POS puro) al schema de la empresa.
				  aplicarModoContabilidad(schema, dto);
				  response.put("respuesta", new mensajesuccesempres("Empresa actualizada exitosamente", true));
				  return ResponseEntity.ok().body(response);
			  } catch (Exception e) {
				  System.out.println("Error al actualizar empresa: " + e.getMessage());
				  e.printStackTrace();
				  return ResponseEntity.ok().body(null);
			  }
		 

	 }

	 /**
	  * modoPOS: escribe la configuración de contabilidad (activa/desde) en la tabla
	  * configuracion_contabilidad del SCHEMA de la empresa. Best-effort: si falla (p.ej. la tabla aún
	  * no existe en ese schema), NO rompe la creación/actualización de la empresa.
	  */
	 private void aplicarModoContabilidad(String schema, Empresaresponse dto) {
		 if (schema == null || schema.isBlank() || dto == null || dto.getContabilidadActiva() == null) return;
		 boolean activa = Boolean.TRUE.equals(dto.getContabilidadActiva());
		 java.sql.Date desde = (activa && dto.getContabilidadDesde() != null)
				 ? java.sql.Date.valueOf(dto.getContabilidadDesde()) : null;
		 try {
			 jdbc.update(
				 "INSERT INTO `" + schema + "`.configuracion_contabilidad " +
				 "(id, contabilidad_activa, contabilidad_desde, fecha_actualizacion) VALUES (1, ?, ?, NOW()) " +
				 "ON DUPLICATE KEY UPDATE contabilidad_activa=VALUES(contabilidad_activa), " +
				 "contabilidad_desde=VALUES(contabilidad_desde), fecha_actualizacion=NOW()",
				 activa ? 1 : 0, desde);
		 } catch (Exception e) {
			 System.out.println("[modoPOS] No se pudo aplicar contabilidad al schema " + schema + ": " + e.getMessage());
		 }
	 }

	 @PatchMapping("/licencia/{id}")
	 public ResponseEntity<EmpresaTenantProjection> actualizarlicencia(@RequestBody Empresaresponse empre, @PathVariable Integer id) {
			  return  ResponseEntity.ok(serv.actulizarlicencia(empre, id));
	 }

	 @PostMapping("/renovar-licencia/{id}")
	 public ResponseEntity<EmpresaTenantProjection> renovarLicencia(@PathVariable Integer id) {
			  return ResponseEntity.ok(serv.renovarLicencia(id));
	 }

	 @Transactional
	 @RequestMapping("/traerinformacionem")
	 	 public ResponseEntity<Map<String, Object>> traerempresainfo() {
	 		 response.clear();
	 		 List<Empresa>  empresas=repoempresa.findAll();
	 		 response.put("empresa",empresas );
	 		 return ResponseEntity.ok().body(response);

	 	 }


		  @RequestMapping("/todas")
		  public List<EmpresaTenantProjection> traerempresas(){
			  return serv.listartodoslossquemas();
		  }

		  @RequestMapping("/buscar")
		  public List<EmpresaTenantProjection> buscarEmpresas(@RequestParam String busqueda){
			  return serv.buscarEmpresasConFiltro(busqueda);
		  }

		  @DeleteMapping("/eliminar")
		  public ResponseEntity<Map<String, Object>> eliminarEmpresa(@RequestBody Map<String, String> request) {
			  response.clear();
			  try {
				  String nombreTenant = request.get("empresa");
				  if (nombreTenant == null || nombreTenant.trim().isEmpty()) {
					  response.put("error", "El nombre de la empresa es requerido");
					  return ResponseEntity.badRequest().body(response);
				  }
				  
				  serv.eliminarEmpresaConBackup(nombreTenant);
				  response.put("mensaje", "Empresa eliminada exitosamente con backup");
				  response.put("empresa", nombreTenant);
				  return ResponseEntity.ok().body(response);
			  } catch (Exception e) {
				  response.put("error", "Error al eliminar empresa: " + e.getMessage());
				  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			  }
		  }

		  @RequestMapping("/schemas")
		  public ResponseEntity<Map<String, Object>> listarSchemas() {
			  response.clear();
			  try {
				  List<String> schemas = serv.listarTodosLosSchemas();
				  response.put("schemas", schemas);
				  return ResponseEntity.ok().body(response);
			  } catch (Exception e) {
				  response.put("error", "Error al listar schemas: " + e.getMessage());
				  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			  }
		  }


		  @PatchMapping(value = "/estado/{id}")
		  public ResponseEntity<Void> updateEstado(@PathVariable Integer id, @RequestParam String estado) {

              serv.updateEstado(id, estado);
			  return ResponseEntity.ok().build();
		  }

	 @Transactional
	 @RequestMapping("/traerempresa")
	 public ResponseEntity<Map<String, Object>> traerempresa() {
		 response.clear();

		 List<Tipopersona>  tipersona=tipopersonarepositori.findAll();
		 List<Tipoidentificacion> tipoidentificacion=tipoidentificacionrepositori.findAll();
		 List<Regimen> regimen=regimenrepositori.findAll();
		 List<Pais> pais=paisrepository.findAll();
		 List<Departamento> departamento=departamentorepositori.findAll();
		 List<Municipio> municipio=municipiorepositori.findAll();
		 System.out.println("nombre tipo persona"+tipersona.get(0).getNombre());
		 datosempresa.setTipopersona(tipersona);
		 datosempresa.setTipoidentificacion(tipoidentificacion);
		 datosempresa.setRegimen(regimen);
		 datosempresa.setDepartamento(departamento);
		 datosempresa.setPais(pais);
		 datosempresa.setMunicipio(municipio);

            response.put("datos",  datosempresa );
            return ResponseEntity
                    .ok()
                    .body(response);



	 }
	    
	 @RequestMapping("/traeractividadeseconomicas")
	 public ResponseEntity<Map<String, Object>> traeractividades(@RequestParam(defaultValue ="") String descripcion, @RequestParam(defaultValue ="0") int codigo) {
		 List<Actividadeconomica> actividades;
		 if (codigo > 0 && (descripcion == null || descripcion.isBlank())) {
			 // Buscar exclusivamente por código exacto (caso edición de tercero)
			 actividades = actividadeconomicarepositorio.findById((long) codigo)
					 .map(List::of)
					 .orElseGet(List::of);
		 } else if (codigo == 0 && (descripcion == null || descripcion.isBlank())) {
			 // Sin filtro: todas las actividades (CIIU son ~600 códigos)
			 PageRequest pageRequest = PageRequest.of(0, 2000);
			 actividades = actividadeconomicarepositorio.findByDescripcionActividadContainingOrCodigo(descripcion, codigo, pageRequest);
		 } else {
			 // Con descripcion: autocompletar hasta 50 resultados
			 PageRequest pageRequest = PageRequest.of(0, 50);
			 actividades = actividadeconomicarepositorio.findByDescripcionActividadContainingOrCodigo(descripcion, codigo, pageRequest);
		 }
         response.put("datosactividad",  actividades );
         return ResponseEntity
                 .ok()
                 .body(response);
	 }
	 @Transactional
	 @RequestMapping("/traerimpuestos")
	 public ResponseEntity<Map<String, Object>> traerimpuestos() {
		 response.clear();
		 List<Impuestos> impuestos=impuestorepositorio.findAll();
		   response.put("datosimpuestos",  impuestos );
		   return ResponseEntity
	                 .ok()
	                 .body(response);
		}
	 
	 
	 
	 
	 @RequestMapping("/codigodeparta")
	 public ResponseEntity<Map<String, Object>> traermunicipio(@RequestParam(defaultValue ="0") int codigo){
	System.out.println("codigo departamento traer municipio");
		 List<Municipio> municipios=municipiorepositori.findByCodigoDepartamento(codigo);
		 
		 
		 
		 response.put("repuesta", municipios);
		 
		 return ResponseEntity.ok().body(response);
		 
		 
	 }
	    

}
