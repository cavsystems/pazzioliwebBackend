package com.pazzioliweb.empresaauth.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
		if (archivo != null && !archivo.isEmpty()) {
			empresa.setImagenEmpresa(archivo.getBytes());
		    empresa.setTipoImagen(archivo.getContentType()); // Guardamos el tipo MIME
		}

		emprerepo.save(empresa);
		
		
		
		
		
		  
		
		
		
	}
	
}
