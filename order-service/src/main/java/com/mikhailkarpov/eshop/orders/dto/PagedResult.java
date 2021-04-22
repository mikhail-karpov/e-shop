package com.mikhailkarpov.eshop.orders.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
public class PagedResult<T> {

    private List<T> content;
    private long totalElements;
    private int page;
    private int totalPages;

    public PagedResult(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.page = page.getNumber();
        this.totalPages = page.getTotalPages();
    }
}
