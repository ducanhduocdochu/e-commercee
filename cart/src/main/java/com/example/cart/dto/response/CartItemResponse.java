package com.example.cart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private LocalDateTime addedAt;
}
