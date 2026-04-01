package com.pazzioliweb.tercerosmodule.dtos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pazzioliweb.commonbacken.dtos.RetencionesDTO;
import com.pazzioliweb.commonbacken.entity.Retenciones;
import com.pazzioliweb.empresaback.dtos.RegimenDTOImpl;
import com.pazzioliweb.tercerosmodule.entity.ContactoTercero;
import com.pazzioliweb.tercerosmodule.entity.SedeTercero;
import com.pazzioliweb.tercerosmodule.entity.Terceros;

public class TerceroimplemenDTO  implements TerceroDtoresponse{
	  private Integer terceroId;
	    private String identificacion;
	    private String dv;
	    private String nombre1;
	    private String nombre2;
	    private String apellido1;
	    private String apellido2;
	    private String razonSocial;
	    private String direccion;
	    private Integer plazo;
	    private Integer cupo;

	    // Relaciones
	    private TipoIdentificacionDTOImpl  tipoIdentificacion;
	    private ClasificacionTerceroDTOImpl  clasificacionTercero;
	    private RegimenDTOImpl regimen;
	    private PrecioimplDTO precio;
	    
	    @JsonDeserialize(contentAs = ContactoTerceroDTOImpl.class)
	    private List<ContactoTerceroDTO> contactos;
	    
	    @JsonDeserialize(contentAs = SedeTerceroDTOImpl.class)
	    private List<SedeTerceroDTO> sedes;
	    
	    private List<RetencionesDTO> retenciones;
	    
	    private String fechaNacimiento;
	    private String matriculaMercantil;
	    private Integer actividadEconomicaId;
	    private TipoPersonaDTOImpl tipoPersona;
	    
	    public List<RetencionesDTO> getRetenciones() {
			return retenciones;
		}
		public void setRetenciones(List<RetencionesDTO> retenciones) {
			this.retenciones = retenciones;
		}
		// Getters y setters
	    public Integer getTerceroId() { return terceroId; }
	    public void setTerceroId(Integer terceroId) { this.terceroId = terceroId; }

	    public String getIdentificacion() { return identificacion; }
	    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

	    public String getDv() { return dv; }
	    public void setDv(String dv) { this.dv = dv; }

	    public String getNombre1() { return nombre1; }
	    public void setNombre1(String nombre1) { this.nombre1 = nombre1; }

	    public String getNombre2() { return nombre2; }
	    public void setNombre2(String nombre2) { this.nombre2 = nombre2; }

	    public String getApellido1() { return apellido1; }
	    public void setApellido1(String apellido1) { this.apellido1 = apellido1; }

	    public String getApellido2() { return apellido2; }
	    public void setApellido2(String apellido2) { this.apellido2 = apellido2; }

	    public String getRazonSocial() { return razonSocial; }
	    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

	    public String getDireccion() { return direccion; }
	    public void setDireccion(String direccion) { this.direccion = direccion; }

	    public Integer getPlazo() { return plazo; }
	    public void setPlazo(Integer plazo) { this.plazo = plazo; }

	    public Integer getCupo() { return cupo; }
	    public void setCupo(Integer cupo) { this.cupo = cupo; }

	    public TipoIdentificacionDTOImpl  getTipoIdentificacion() { return tipoIdentificacion; }
	    public void setTipoIdentificacion(TipoIdentificacionDTOImpl  tipoIdentificacion) { this.tipoIdentificacion = tipoIdentificacion; }

	    public ClasificacionTerceroDTOImpl  getClasificacionTercero() { return clasificacionTercero; }
	    public void setClasificacionTercero(ClasificacionTerceroDTOImpl  clasificacionTercero) { this.clasificacionTercero = clasificacionTercero; }

	    public RegimenDTOImpl getRegimen() { return regimen; }
	    public void setRegimen(RegimenDTOImpl regimen) { this.regimen = regimen; }

	    public PrecioimplDTO getPrecio() { return precio; }
	    public void setPrecio(PrecioimplDTO precio) { this.precio = precio; }

	    public List<ContactoTerceroDTO> getContactos() { return contactos; }
	    public void setContactos(List<ContactoTerceroDTO> contactos) { this.contactos = contactos; }

	    public List<SedeTerceroDTO> getSedes() { return sedes; }
	    public void setSedes(List<SedeTerceroDTO> sedes) { this.sedes = sedes; }
	    
	    
	    
	    public String getFechaNacimiento() {return fechaNacimiento;}
		public void setFechaNacimiento(String fechaNacimiento) {this.fechaNacimiento = fechaNacimiento;}
		public String getMatriculaMercantil() {return matriculaMercantil;}
		public void setMatriculaMercantil(String matriculaMercantil) {this.matriculaMercantil = matriculaMercantil;}
		public Integer getActividadEconomicaId() {return actividadEconomicaId;}
		public void setActividadEconomicaId(Integer actividadEconomicaId) {this.actividadEconomicaId = actividadEconomicaId;}
		
		public TipoPersonaDTOImpl getTipoPersona() {return tipoPersona;}
		public void setTipoPersona(TipoPersonaDTOImpl tipoPersona) {this.tipoPersona = tipoPersona;}

		private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		// Método helper para convertir desde la entidad
	    public static TerceroDTOImpl fromEntity(Terceros t) {
	        TerceroDTOImpl dto = new TerceroDTOImpl();
	        dto.setTerceroId(t.getTerceroId());
	        dto.setIdentificacion(t.getIdentificacion());
	        dto.setDv(t.getDv());
	        dto.setNombre1(t.getNombre1());
	        dto.setNombre2(t.getNombre2());
	        dto.setApellido1(t.getApellido1());
	        dto.setApellido2(t.getApellido2());
	        dto.setRazonSocial(t.getRazonSocial());
	        dto.setDireccion(t.getDireccion());
	        dto.setPlazo(t.getPlazo());
	        dto.setCupo(t.getCupo());

	        if (t.getTipoIdentificacion() != null) {
	            dto.setTipoIdentificacion(TipoIdentificacionDTOImpl.fromEntity(t.getTipoIdentificacion()));
	        }

	        if (t.getClasificacionTercero() != null) {
	            dto.setClasificacionTercero(ClasificacionTerceroDTOImpl.fromEntity(t.getClasificacionTercero()));
	        }

	        if (t.getRegimen() != null) {
	            dto.setRegimen(RegimenDTOImpl.fromEntity(t.getRegimen()));
	        }

	        if (t.getPrecio() != null) {
	            dto.setPrecio(PrecioDTOImpl.fromEntity(t.getPrecio()));
	        }
	        
	        if (t.getRetenciones() != null) {
	            dto.setRetenciones(
	                t.getRetenciones().stream()
	                 .map(r -> new RetencionesDTO(r.getRetencionId(), r.getCodigo(), r.getNombre(), r.getBase(), r.getPorcentaje()))
	                 .collect(Collectors.toList())
	            );
	        }
	        
	        if (t.getContactos() != null) {
	        	dto.setContactos(
	        		    t.getContactos().stream()
	        		     .map(c -> (ContactoTerceroDTO) ContactoTerceroDTOImpl.fromEntity(c))
	        		     .collect(Collectors.toList())
	        		);
	        }

	        if (t.getSedes() != null) {
	            dto.setSedes(
	                t.getSedes().stream()
	                 .map(c -> (SedeTerceroDTO) SedeTerceroDTOImpl.fromEntity(c))
	                 .collect(Collectors.toList())
	            );
	        }
	        
	        if(t.getFechaNacimiento() != null) {
	        	dto.setFechaNacimiento(t.getFechaNacimiento().format(FORMATTER));
	        }
	        
	        if(t.getMatriculaMercantil() != null) {
	        	dto.setMatriculaMercantil(t.getMatriculaMercantil());
	        }
	        
	        if(t.getActividadEconomicaId() != null) {
	        	dto.setActividadEconomicaId(t.getActividadEconomicaId());
	        }
	        
	        if(t.getTipoPersona() != null) {
	        	dto.setTipoPersona(TipoPersonaDTOImpl.fromEntity(t.getTipoPersona()));
	        }
	        
	        return dto;
	    }
	    
	 // Conversión a entidad
	    public Terceros toEntity() {
	        Terceros entidad = new Terceros();
	        entidad.setTerceroId(this.terceroId);
	        entidad.setIdentificacion(this.identificacion);
	        entidad.setDv(this.dv);
	        entidad.setNombre1(this.nombre1);
	        entidad.setNombre2(this.nombre2);
	        entidad.setApellido1(this.apellido1);
	        entidad.setApellido2(this.apellido2);
	        entidad.setRazonSocial(this.razonSocial);
	        entidad.setDireccion(this.direccion);
	        entidad.setPlazo(this.plazo);
	        entidad.setCupo(this.cupo);

	        if (this.tipoIdentificacion != null)
	            entidad.setTipoIdentificacion(this.tipoIdentificacion.toEntity());

	        if (this.regimen != null)
	            entidad.setRegimen(this.regimen.toEntity());

	        if (this.clasificacionTercero != null)
	            entidad.setClasificacionTercero(this.clasificacionTercero.toEntity());

	        if (this.precio != null)
	            entidad.setPrecio(this.precio.toEntity());
	        
	        if (this.retenciones != null && !this.retenciones.isEmpty()) {
	            Set<Retenciones> retencionesEntity = this.retenciones.stream()
	                .map(r -> {
	                    Retenciones ret = new Retenciones();
	                    ret.setRetencion_id(r.getRetencionId());
	                    ret.setCodigo(r.getCodigo());
	                    ret.setNombre(r.getNombre());
	                    ret.setBase(r.getBase());
	                    ret.setPorcentaje(r.getPorcentaje());
	                    return ret;
	                })
	                .collect(Collectors.toSet());
	            entidad.setRetenciones(retencionesEntity);
	        }
	        
	        if (this.sedes != null) {
	            Set<SedeTercero> sedesEntity = this.sedes.stream()
	                .map((SedeTerceroDTO sedeDTO) -> ((SedeTerceroDTOImpl) sedeDTO).toEntity())
	                .collect(Collectors.toSet());
	            entidad.setSedes(sedesEntity);
	        }

	        if (this.contactos != null) {
	            Set<ContactoTercero> contactosEntity = this.contactos.stream()
	                .map((ContactoTerceroDTO contactoDTO) -> ((ContactoTerceroDTOImpl) contactoDTO).toEntity())
	                .collect(Collectors.toSet());
	            entidad.setContactos(contactosEntity);
	        }
	        
	        if (this.fechaNacimiento != null)
	            entidad.setFechaNacimiento(LocalDate.parse(this.fechaNacimiento, FORMATTER));
	        
	        if (this.matriculaMercantil != null)
	            entidad.setMatriculaMercantil(this.matriculaMercantil);
	        
	        if (this.actividadEconomicaId != null)
	            entidad.setActividadEconomicaId(this.actividadEconomicaId);
	        
	        if(this.tipoPersona != null)
	        	entidad.setTipoPersona(this.tipoPersona.toEntity());
	        
	        return entidad;
	    }
}
