package com.pazzioliweb.empresaback.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
		   response.put("respuesta", new mensajesuccesempres("Empresa creada",true) );
		   return ResponseEntity.status(HttpStatus.CREATED).body(response);
		 	 }
	 
	 
	 	 @RequestMapping("/traerinformacionem")
	 	 public ResponseEntity<Map<String, Object>> traerempresainfo() {
	 		 List<Empresa>  empresas=repoempresa.findAll();
	 		 response.clear();
	 		 response.put("empresa",empresas );
	 		 return ResponseEntity.ok().body(response);
	 		 
	 	 }
	 	 
	 @RequestMapping("/traerempresa")
	 public ResponseEntity<Map<String, Object>> traerempresa() {
		 
		
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
		 Object req;
	
		   System.out.println(descripcion);
		
		 
		 /*PageRequest pageRequest = PageRequest.of(1, 15); // página 1 (empieza en 0), 15 registros
		 List<Actividadeconomica> actividades = actividadeconomicarepositorio.findWithLimit(pageRequest);*/
		 
		 PageRequest pageRequest = PageRequest.of(0, 15); // página 1 (empieza en 0), 15 registros
		 List<Actividadeconomica> actividades = actividadeconomicarepositorio.findByDescripcionActividadContainingOrCodigo(descripcion, codigo, pageRequest);
		 
         response.put("datosactividad",  actividades );
         return ResponseEntity
                 .ok()
                 .body(response);
		 
		 
		 
	 }
	 @RequestMapping("/traerimpuestos")
	 public ResponseEntity<Map<String, Object>> traerimpuestos() {
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
