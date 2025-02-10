package com.example.cart.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {
    @NotNull(message = "PRODUCT_INVALID_ID")
    private String productId;
}
