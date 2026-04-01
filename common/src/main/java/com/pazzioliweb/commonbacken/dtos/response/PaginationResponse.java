package com.pazzioliweb.commonbacken.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;

@Data

public class PaginationResponse<T> {

	private List<T> content;
    private int currentPage;
    private long totalItems;
    private int totalPages;

    public PaginationResponse() {}

    public PaginationResponse(List<T> content, int currentPage, long totalItems, int totalPages) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public static <T> PaginationResponse<T> of(Page<T> page) {
        return new PaginationResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public List<T> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }
    
}
