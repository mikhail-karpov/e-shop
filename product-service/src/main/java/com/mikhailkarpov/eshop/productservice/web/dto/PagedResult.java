package com.mikhailkarpov.eshop.productservice.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PagedResult<T> {

    private final List<T> result;

    @JsonProperty(value = "total_results")
    private final long totalResults;

    private final int page;

    @JsonProperty(value = "total_pages")
    private final int totalPages;

    public PagedResult(Page<T> page) {
        this.result = page.getContent();
        this.totalResults = page.getTotalElements();
        this.page = page.getNumber();
        this.totalPages = page.getTotalPages();
    }
}
