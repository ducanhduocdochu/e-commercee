package com.example.product.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateRequest {
    @NotBlank(message = "CATEGORY_INVALID_NAME")
    private String name;
}
