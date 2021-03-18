package com.mikhailkarpov.eshop.productservice.web.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PagedResult<T> {

    private final List<T> result;

    private final long totalResults;

    private final int page;

    private final int totalPages;

    public PagedResult(Page<T> page) {
        this.result = page.getContent();
        this.totalResults = page.getTotalElements();
        this.page = page.getNumber();
        this.totalPages = page.getTotalPages();
    }
}
