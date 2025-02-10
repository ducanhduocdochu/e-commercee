package com.example.cart.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemoveFromCartRequest {
    @NotEmpty(message = "PRODUCT_INVALID_ID")
    private List<String> productIds;
}
