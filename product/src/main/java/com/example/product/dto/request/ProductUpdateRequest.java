package com.example.product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    @NotBlank(message = "PRODUCT_INVALID_NAME")
    private String name;

    private String description;

    @NotNull(message = "PRODUCT_INVALID_STOCK")
    @Min(value = 0, message = "PRODUCT_INVALID_PRICE")
    private BigDecimal price;

    @NotNull(message = "PRODUCT_INVALID_STOCK")
    @Min(value = 0, message = "PRODUCT_INVALID_STOCK")
    private int stock;
}
