package com.pazzioliweb.empresaback.dtos;

import java.util.List;



public class Empresaresponse {
	private String razonsocial;
	private List<Impuestos> impuestos;
	 private  String Correoempresa;
	 private  int pais;
	 private  int municipio;
	 private int departamento;
	 private String Actividadeconomica;
	 private String celularempresa;
	 private String codigopostal;
	 private String digitodeverificacion;
	 private String nombrecomercial;
	 private  String primerapellido;
		
      private String primernombre;
      private String numeroidentificacion;
      private int  regimen;
      private String segundoapellido;
      private String  segundonombre;
      private String telefonofijo;
  	
  	private int tipodeidentificacion;
  	
  	private int 	tipodepersona;
  	public List<Impuestos> getImpuestos() {
		return impuestos;
	}

	public void setImpuestos(List<Impuestos> impuestos) {
		this.impuestos = impuestos;
	}

	public String getCorreoempresa() {
		return Correoempresa;
	}

	public void setCorreoempresa(String correoempresa) {
		Correoempresa = correoempresa;
	}

	public int getPais() {
		return pais;
	}

	public void setPais (int pais) {
		this.pais = pais;
	}

	public String getActividadeconomica() {
		return Actividadeconomica;
	}

	public void setActividadeconomica(String actividadeconomica) {
		Actividadeconomica = actividadeconomica;
	}

	public String getCelularempresa() {
		return celularempresa;
	}

	public void setCelularempresa(String celularempresa) {
		this.celularempresa = celularempresa;
	}

	public String getCodigopostal() {
		return codigopostal;
	}

	public void setCodigopostal(String codigopostal) {
		this.codigopostal = codigopostal;
	}

	public String getDigitodeverificacion() {
		return digitodeverificacion;
	}

	public void setDigitodeverificacion(String digitodeverificacion) {
		this.digitodeverificacion = digitodeverificacion;
	}

	public String getNombrecomercial() {
		return nombrecomercial;
	}

	public void setNombrecomercial(String nombrecomercial) {
		this.nombrecomercial = nombrecomercial;
	}

	public String getPrimerapellido() {
		return primerapellido;
	}

	public void setPrimerapellido(String primerapellido) {
		this.primerapellido = primerapellido;
	}

	public String getPrimernombre() {
		return primernombre;
	}

	public void setPrimernombre(String primernombre) {
		this.primernombre = primernombre;
	}

	public String getNumeroidentificacion() {
		return numeroidentificacion;
	}

	public void setNumeroidentificacion(String numeroidentificacion) {
		this.numeroidentificacion = numeroidentificacion;
	}

	public int getRegimen() {
		return regimen;
	}

	public void setRegimen(int regimen) {
		this.regimen = regimen;
	}

	public String getSegundoapellido() {
		return segundoapellido;
	}

	public void setSegundoapellido(String segundoapellido) {
		this.segundoapellido = segundoapellido;
	}

	public String getSegundonombre() {
		return segundonombre;
	}

	public void setSegundonombre(String segundonombre) {
		this.segundonombre = segundonombre;
	}

	public String getTelefonofijo() {
		return telefonofijo;
	}

	public void setTelefonofijo(String telefonofijo) {
		this.telefonofijo = telefonofijo;
	}

	public int getTipodeidentificacion() {
		return tipodeidentificacion;
	}

	public void setTipodeidentificacion(int tipodeidentificacion) {
		this.tipodeidentificacion = tipodeidentificacion;
	}

	public int getTipodepersona() {
		return tipodepersona;
	}

	public void setTipodepersona(int tipodepersona) {
		this.tipodepersona = tipodepersona;
	}

	public int getMunicipio() {
		return municipio;
	}

	public void setMunicipio(int municipio) {
		this.municipio = municipio;
	}

	public int getDepartamento() {
		return departamento;
	}

	public void setDepartemento(int departamento) {
		this.departamento = departamento;
	}

	public List<Sucursales> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursales> sucursales) {
		this.sucursales = sucursales;
	}


	public List<Sucursales> sucursales;

      
      
      
     
	
	/*
	
	
	
	
	
	
	
	
	
	
	pais
	
	primerapellido
	
	primernombre
		razonsocial
	
	 
		segundoapellido
	
	segundonombre
		sucursales

	telefonofijo
	
	tipodeidentificacion
		tipodepersona
*/
	
	public String getRazonsocial() {
		return razonsocial;
	}

	public void setRazonsocial(String razonsocial) {
		this.razonsocial = razonsocial;
	}
	
	public static class Impuestos{
		 int base;
		
		public int getBase() {
			return base;
		}

		 public void setBase(int base) {
			 this.base = base;
		 }

		 public int getCodigo() {
			 return codigo;
		 }

		 public void setCodigo(int codigo) {
			 this.codigo = codigo;
		 }

		 public String getNombre() {
			 return nombre;
		 }

		 public void setNombre(String nombre) {
			 this.nombre = nombre;
		 }

		 public String getSigla() {
			 return sigla;
		 }

		 public void setSigla(String sigla) {
			 this.sigla = sigla;
		 }

		 public int getTarifa() {
			 return tarifa;
		 }

		 public void setTarifa(int tarifa) {
			 this.tarifa = tarifa;
		 }

		int codigo;
		 String nombre;
	
		String sigla;
		
		int tarifa;
	
		
	}
	
	public static class Sucursales{
		private String celular;
		
		private String codigopostal;
		
		private String codigosucursal;
		
		 private String correo;
		
		 private Departamento departamento;
		 
		 public String getCelular() {
			return celular;
		}


		 public void setCelular(String celular) {
			 this.celular = celular;
		 }


		 public String getCodigopostal() {
			 return codigopostal;
		 }


		 public void setCodigopostal(String codigopostal) {
			 this.codigopostal = codigopostal;
		 }


		 public String getCodigosucursal() {
			 return codigosucursal;
		 }


		 public void setCodigosucursal(String codigosucursal) {
			 this.codigosucursal = codigosucursal;
		 }


		 public String getCorreo() {
			 return correo;
		 }


		 public void setCorreo(String correo) {
			 this.correo = correo;
		 }


		 public Departamento getDepartamento() {
			 return departamento;
		 }


		 public void setDepartamento(Departamento departamento) {
			 this.departamento = departamento;
		 }


		 public String getDireccion() {
			 return direccion;
		 }


		 public void setDireccion(String direccion) {
			 this.direccion = direccion;
		 }


		 public String getId() {
			 return id;
		 }


		 public void setId(String id) {
			 this.id = id;
		 }


		 public Municipio getMunicipio() {
			 return municipio;
		 }


		 public void setMunicipio(Municipio municipio) {
			 this.municipio = municipio;
		 }


		 public String getNombre() {
			 return nombre;
		 }


		 public void setNombre(String nombre) {
			 this.nombre = nombre;
		 }


		 public Pais getPais() {
			 return pais;
		 }


		 public void setPais(Pais pais) {
			 this.pais = pais;
		 }


		 private String direccion;
			
			private String id;
			private String telefonofijo;
			public String getTelefonofijo() {
				return telefonofijo;
			}


			public void setTelefonofijo(String telefonofijo) {
				this.telefonofijo = telefonofijo;
			}


			private Municipio municipio;
			private String nombre;
			
			private  Pais pais;
			public static class Departamento{
			int codigo;  
			String departamento;
			public int getCodigo() {
				return codigo;
			}
			public void setCodigo(int codigo) {
				this.codigo = codigo;
			}
			public String getDepartamento() {
				return departamento;
			}
			public void setDepartamento(String departamento) {
				this.departamento = departamento;
			}
		}
	
		
		public static class Municipio {
			private int codigo;   
			private  String municipio;
			public void setCodigo(int codigo) {
				this.codigo=codigo;
			}
			
			public int getCodigo() {
				return codigo;
			}
			
			public void setMunicipio(String municipio) {
				this.municipio=municipio;
			}
			
			public String getMunicipio() {
				return this.municipio;
			}
			
		}
		
		
		public static  class Pais{
			private int codigo; private String pais;
			public int getCodigo() {
				return codigo;
			}
			public void setCodigo(int codigo) {
				this.codigo = codigo;
			}
			public String getPais() {
				return pais;
			}
			public void setPais(String pais) {
				this.pais = pais;
			}
		}
		
		
	}
	
    

	
}
