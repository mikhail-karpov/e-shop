package com.mikhailkarpov.eshop.orders.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO {

    private String code;

    private String title;

    private Integer price;

    private Integer quantity;

    @Builder
    public ProductDTO(String code, String title, Integer price, Integer quantity) {
        this.code = code;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }
}
