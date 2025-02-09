package com.example.product.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private LocalDateTime createdAt;  // ✅ Thêm thời gian tạo
    private LocalDateTime updatedAt;  // ✅ Thêm thời gian cập nhật
}
