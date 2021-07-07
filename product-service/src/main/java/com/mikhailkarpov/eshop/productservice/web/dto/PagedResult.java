package com.mikhailkarpov.eshop.productservice.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

@Value
public class PagedResult<T> {

    private final List<T> result;

    private final long totalResults;

    private final int page;

    private final int totalPages;

    @Builder
    public PagedResult(@JsonProperty("result") List<T> result,
                       @JsonProperty("totalResults") long totalResults,
                       @JsonProperty("page") int page,
                       @JsonProperty("totalPages") int totalPages) {
        this.result = result;
        this.totalResults = totalResults;
        this.page = page;
        this.totalPages = totalPages;
    }

    public PagedResult(Page<T> page) {
        this.result = page.getContent();
        this.totalResults = page.getTotalElements();
        this.page = page.getNumber();
        this.totalPages = page.getTotalPages();
    }
}
