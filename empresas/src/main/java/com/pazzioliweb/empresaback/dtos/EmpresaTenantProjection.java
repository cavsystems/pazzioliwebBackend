package com.pazzioliweb.empresaback.dtos;


import com.pazzioliweb.empresasback.entity.Empresa;
import jakarta.persistence.ColumnResult;
import lombok.Data;

@Data
public class EmpresaTenantProjection {

    private String tenant;
    private String estado;
    private Integer codigo;
    private  Integer codigotipopersona;
    private  Integer codigotipoidentificacion;
    private String numeroidentificacion;
    private Integer digitoverificacion;

    private String primernombre;
    private String segundonombre;
    private String primerapellido;
    private String segundoapellido;

    private String razonsocial;
    private String codigopostal;
    private String nombrecomercial;

    private String codigoactividadeconomica;
    private Integer codigoregimen;

    private String correoempresa;
    private String celularempresa;
    private String telfonofijo;

    private Integer codigopais;
    private Integer codigodepartamento;
    private Integer codigomunicipio;

    private String imagenempresa;
    private String tipoImagen;


    private String fechainiciolicencia;
            private String fechafinallicencia;
private   Integer plazo;
            private String estadolicencia;

            private Integer numerousuarios;

    public EmpresaTenantProjection(
            String tenant,
            String estado,
            Integer codigo,
            Integer  codigotipopersona,
            Integer codigotipoidentificacion,
            String numeroidentificacion,
            Integer digitoverificacion,
            String primernombre,
            String segundonombre,
            String primerapellido,
            String segundoapellido,
            String razonsocial,
            String codigopostal,
            String nombrecomercial,
            String codigoactividadeconomica,
            Integer codigoregimen,
            String correoempresa,
            String celularempresa,
            String telfonofijo,
            Integer codigopais,
            Integer codigodepartamento,
            Integer codigomunicipio,
            String imagenempresa,
            String tipoImagen,
            String fechainiciolicencia,
    String fechafinallicencia,

    String estadolicencia,
            Integer plazo,
             Integer numerousuarios

    ) {
        this.codigotipopersona=codigotipopersona;
        this.estado = estado;
        this.tenant = tenant;
        this.codigo = codigo;
        this.codigotipoidentificacion = codigotipoidentificacion;
        this.numeroidentificacion = numeroidentificacion;
        this.digitoverificacion = digitoverificacion;
        this.primernombre = primernombre;
        this.segundonombre = segundonombre;
        this.primerapellido = primerapellido;
        this.segundoapellido = segundoapellido;
        this.razonsocial = razonsocial;
        this.codigopostal = codigopostal;
        this.nombrecomercial = nombrecomercial;
        this.codigoactividadeconomica = codigoactividadeconomica;
        this.codigoregimen = codigoregimen;
        this.correoempresa = correoempresa;
        this.celularempresa = celularempresa;
        this.telfonofijo = telfonofijo;
        this.codigopais = codigopais;
        this.codigodepartamento = codigodepartamento;
        this.codigomunicipio = codigomunicipio;
        this.imagenempresa = imagenempresa;
        this.tipoImagen = tipoImagen;
        this.fechainiciolicencia = fechainiciolicencia;
        this.fechafinallicencia = fechafinallicencia;
        this.estadolicencia = estadolicencia;
        this.plazo = plazo;
        this.numerousuarios = numerousuarios;
    }

    public EmpresaTenantProjection(){

    }
    public static EmpresaTenantProjection mapperdtos(Empresa empresa){
        EmpresaTenantProjection empresaresponse=new EmpresaTenantProjection();
        empresaresponse.setCodigo(empresa.getCodigo());
        empresaresponse.setRazonsocial(empresa.getRazonsocial());

return empresaresponse;


    }
}