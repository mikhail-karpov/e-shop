package com.mikhailkarpov.eshop.shoppingcartservice.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItem implements Serializable {

    private static final long serialVersionUID = -5971783201623581395L;

    @NotNull
    @JsonProperty("product-id")
    private String productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
