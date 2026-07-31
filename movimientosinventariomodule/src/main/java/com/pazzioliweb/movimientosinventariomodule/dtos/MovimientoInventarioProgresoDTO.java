package com.pazzioliweb.movimientosinventariomodule.dtos;

public class MovimientoInventarioProgresoDTO {

    private String usuario;
    private Integer paso;
    private String mensaje;
    private Integer porcentaje;
    private Long movimientoId;
    private String estado; // "PROCESANDO", "COMPLETADO", "ERROR"

    public MovimientoInventarioProgresoDTO() {}

    public MovimientoInventarioProgresoDTO(String usuario, Integer paso, String mensaje,
                                            Integer porcentaje, Long movimientoId, String estado) {
        this.usuario = usuario;
        this.paso = paso;
        this.mensaje = mensaje;
        this.porcentaje = porcentaje;
        this.movimientoId = movimientoId;
        this.estado = estado;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public Integer getPaso() { return paso; }
    public void setPaso(Integer paso) { this.paso = paso; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Integer getPorcentaje() { return porcentaje; }
    public void setPorcentaje(Integer porcentaje) { this.porcentaje = porcentaje; }
    public Long getMovimientoId() { return movimientoId; }
    public void setMovimientoId(Long movimientoId) { this.movimientoId = movimientoId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
