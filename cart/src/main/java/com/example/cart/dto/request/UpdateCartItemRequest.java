package com.example.cart.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor // 👈 Cần để tránh lỗi "cannot be applied to given types"
@AllArgsConstructor
@Builder // 👈 Cho phép dùng builder trong test
public class UpdateCartItemRequest {
    private String productId;
    private int quantity;
}
