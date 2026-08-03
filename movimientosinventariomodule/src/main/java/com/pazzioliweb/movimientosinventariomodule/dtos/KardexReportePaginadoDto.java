package com.pazzioliweb.movimientosinventariomodule.dtos;

import java.util.List;

/**
 * Respuesta paginada de /kardex-reporte: solo trae la página pedida (para
 * scroll infinito en el front), más los totales ya calculados sobre el
 * período/filtros completo.
 */
public class KardexReportePaginadoDto {
    private List<KardexReportDto> content;
    private KardexTotalesDto totales;
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;

    public List<KardexReportDto> getContent() {
        return content;
    }

    public void setContent(List<KardexReportDto> content) {
        this.content = content;
    }

    public KardexTotalesDto getTotales() {
        return totales;
    }

    public void setTotales(KardexTotalesDto totales) {
        this.totales = totales;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
