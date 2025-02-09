package com.example.product.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private String categoryId;
    private String sellerId;
    private LocalDateTime createdAt;  // ✅ Thêm thời gian tạo
    private LocalDateTime updatedAt;  // ✅ Thêm thời gian cập nhật
}
