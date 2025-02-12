package com.example.product.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreationRequest {
    @NotBlank(message = "CATEGORY_INVALID_NAME")
    private String name;
}
