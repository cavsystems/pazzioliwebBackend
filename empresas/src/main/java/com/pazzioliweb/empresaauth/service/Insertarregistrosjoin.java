package com.pazzioliweb.empresaauth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Pais;
import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;
import com.pazzioliweb.commonbacken.repositorio.DepartamentoRepositori;
import com.pazzioliweb.commonbacken.repositorio.MunicipioRepositori;
import com.pazzioliweb.commonbacken.repositorio.PaisRepositori;
import com.pazzioliweb.empresasback.entity.Actividadeconomica;
import com.pazzioliweb.empresasback.entity.Regimen;
import com.pazzioliweb.empresasback.repositori.ActividadeconomicaRepositori;
import com.pazzioliweb.empresasback.repositori.RegimenRepositori;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.commonbacken.repositorio.TipoidentificacionRepository;
import com.pazzioliweb.usuariosbacken.entity.Tipopersona;
import com.pazzioliweb.usuariosbacken.repositorio.TipopersonaRepository;



@Service
public class Insertarregistrosjoin {
	
	@Autowired
	private MunicipioRepositori repositorimunicipio;
	
	@Autowired
	private DepartamentoRepositori repositoridepartemento;
	@Autowired
	private PaisRepositori paisrepositoripais;
	@Autowired
	private BodegasRepository bodegarepo;
	@Autowired
	private RegimenRepositori regimenrepo;
	@Autowired
	private TipopersonaRepository tiporepo;
	@Autowired
	private TipoidentificacionRepository tipoidenrepo;
	@Autowired
	private ActividadeconomicaRepositori actividarepo;
	
	
	
	public Object[] obtenercodigos(int codigomunicipio,int codigodepartamento,int codigopais) {
		Optional<Municipio> municipioop=repositorimunicipio.findByCodigo(codigomunicipio);
		Optional<Departamento> departamentoop=repositoridepartemento.findByCodigo(codigodepartamento);
		Optional<Pais> paisop=paisrepositoripais.findByCodigo(codigopais);
	
		
		if(municipioop.isPresent()) {
		Municipio	municipio=municipioop.get();
		
		Departamento departamento=departamentoop.get();
		
		Pais pais=paisop.get();
		
		
		
		return new Object[]{ municipio, departamento, pais };
		
		
			
		}else {
			return new Object[]{ null, null, null };
		}
		
	}
	
	
	
	public Object[] obtenercodigos(int codigomunicipio,int codigodepartamento,int codigopais,int codigotipoper,int codigidenti,
			int regimen,String nombre) {
		try {
			Optional<Municipio> municipioop=repositorimunicipio.findByCodigo(codigomunicipio);
			Optional<Departamento> departamentoop=repositoridepartemento.findByCodigo(codigodepartamento);
			Optional<Pais> paisop=paisrepositoripais.findByCodigo(codigopais);
			Optional<Regimen> regimenop=regimenrepo.findByCodigo(regimen);
			Optional<Tipopersona> tipoperop=tiporepo.findByCodigo(codigotipoper);
			Optional<Tipoidentificacion> tipoidenop=tipoidenrepo.findByCodigo(codigidenti);
			System.out.println("llego aqui controller: "+municipioop.isPresent());
			Optional<Actividadeconomica> actividad=actividarepo.findByDescripcionActividad(nombre);
			System.out.println("llego aqui controller nombre activida: "+ nombre);
			if(actividad.isEmpty()) {
				Actividadeconomica nuevaactividad=new Actividadeconomica();
				nuevaactividad.setDescripcionActividad(nombre);
				actividarepo.save(nuevaactividad);
				actividad=actividarepo.findByDescripcionActividad(nombre);
			}
			System.out.println("llego aqui controller antes: "+municipioop.isPresent());
			if(municipioop.isPresent()) {
				Municipio	municipio=municipioop.get();

				Departamento departamento=departamentoop.get();

				Pais pais=paisop.get();
				Regimen regime=regimenop.get();
				Tipopersona tipopersona=tipoperop.get();
				Tipoidentificacion tipoidentifica=tipoidenop.get();
				Actividadeconomica actividadecono=actividad.get();

				System.out.println("llego aqui controller: ");

				return new Object[]{ municipio, departamento, pais,tipopersona,tipoidentifica,
						regime,actividadecono};



			}else {
				return new Object[]{ null, null, null,null,null,null,null };
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new Object[]{ null, null, null,null,null,null,null };
		}

		
		
		
		
		
	}
}
