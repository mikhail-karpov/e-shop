package com.mikhailkarpov.eshop.productservice.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "title must be provided")
    private String title;

    private String description;
}
