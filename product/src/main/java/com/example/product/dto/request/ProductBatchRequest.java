package com.example.product.dto.request;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductBatchRequest {
    private List<String> productIds;
}
