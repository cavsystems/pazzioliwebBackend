package com.pazzioliweb.empresasback.entity;



import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Pais;
import com.pazzioliweb.empresaback.dtos.EmpresaResponseauth;
import com.pazzioliweb.empresaback.dtos.EmpresaTenantProjection;
import com.pazzioliweb.usuariosbacken.entity.Roles;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.ConstructorResult;
import lombok.Data;

@Entity

@SqlResultSetMapping(
	    name = "EmpresaResponseauthMapping",
	    classes = @ConstructorResult(
	        targetClass = EmpresaResponseauth.class,
	        columns = {
	            @ColumnResult(name = "nombreconexion", type = String.class),
	          
	        }
	    )
	)

@SqlResultSetMapping(
		name = "EmpresaResponseMapping",
		classes = @ConstructorResult(
				targetClass = EmpresaTenantProjection.class,
				columns = {
						@ColumnResult(name = "tenant", type = String.class),
						@ColumnResult(name = "codigotipopersona", type =  Integer.class),
						@ColumnResult(name = "codigotipoidentificacion", type =  Integer.class),
						@ColumnResult(name = "numeroidentificacion", type = String.class),
						@ColumnResult(name = "digitoverificacion", type = Integer.class),

						@ColumnResult(name = "primernombre", type = String.class),
						@ColumnResult(name = "segundonombre", type = String.class),
						@ColumnResult(name = "primerapellido", type = String.class),
						@ColumnResult(name = "segundoapellido", type = String.class),

						@ColumnResult(name = "razonsocial", type = String.class),
						@ColumnResult(name = "codigopostal", type = String.class),
						@ColumnResult(name = "nombrecomercial", type = String.class),

						@ColumnResult(name = "codigoactividadeconomica", type = String.class),
						@ColumnResult(name = "codigoregimen", type = Integer.class),

						@ColumnResult(name = "correoempresa", type = String.class),
						@ColumnResult(name = "celularempresa", type = String.class),
						@ColumnResult(name = "telfonofijo", type = String.class),

						@ColumnResult(name = "codigopais", type = Integer.class),
						@ColumnResult(name = "codigodepartamento", type = Integer.class),
						@ColumnResult(name = "codigomunicipio", type = Integer.class),

						@ColumnResult(name = "imagenempresa", type = String.class),
						@ColumnResult(name = "tipoImagen", type = String.class)

				}
		)
)
@Data
public class Empresas {

	     @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int codigo;
	     

	     
	
		
}
