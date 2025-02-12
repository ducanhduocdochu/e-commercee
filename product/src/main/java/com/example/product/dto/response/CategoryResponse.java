package com.example.product.dto.response;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private LocalDateTime createdAt; // ✅ Thêm thời gian tạo
    private LocalDateTime updatedAt; // ✅ Thêm thời gian cập nhật
}
