package com.mikhailkarpov.eshop.productservice.web.dto;

import lombok.Value;

@Value
public class CategoryResponse {

    private final Long id;
    private final String title;
    private final String description;
}
