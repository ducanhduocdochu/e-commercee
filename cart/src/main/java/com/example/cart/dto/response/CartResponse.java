package com.example.cart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private String cartId;
    private String buyerId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
    private LocalDateTime updatedAt;
}
