package com.pazzioliweb.movimientosinventariomodule.dtos;

/**
 * Totales del reporte de kardex calculados sobre TODO el período/filtros
 * (no solo la página que se le devuelve al front). Antes esto se calculaba
 * en el frontend con la lista completa; ahora viaja ya resuelto desde el back.
 */
public class KardexTotalesDto {
    private double saldoActual;
    private double totalEntradas;
    private double totalSalidas;
    private double costoPromedioVigente;
    private double valorInventario;
    private int movimientosEntrada;
    private int movimientosSalida;

    public double getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(double saldoActual) {
        this.saldoActual = saldoActual;
    }

    public double getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(double totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public double getTotalSalidas() {
        return totalSalidas;
    }

    public void setTotalSalidas(double totalSalidas) {
        this.totalSalidas = totalSalidas;
    }

    public double getCostoPromedioVigente() {
        return costoPromedioVigente;
    }

    public void setCostoPromedioVigente(double costoPromedioVigente) {
        this.costoPromedioVigente = costoPromedioVigente;
    }

    public double getValorInventario() {
        return valorInventario;
    }

    public void setValorInventario(double valorInventario) {
        this.valorInventario = valorInventario;
    }

    public int getMovimientosEntrada() {
        return movimientosEntrada;
    }

    public void setMovimientosEntrada(int movimientosEntrada) {
        this.movimientosEntrada = movimientosEntrada;
    }

    public int getMovimientosSalida() {
        return movimientosSalida;
    }

    public void setMovimientosSalida(int movimientosSalida) {
        this.movimientosSalida = movimientosSalida;
    }
}
