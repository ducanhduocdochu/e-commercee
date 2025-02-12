package com.example.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.product.dto.request.ApiResponse;
import com.example.product.dto.request.ProductBatchRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Product Internal API", description = "API quản lý sản phẩm để giao tiếp giữa các service")
public class ProductInternalController {
    ProductService productService;

    @GetMapping("/{productId}")
    @Operation(summary = "Lấy thông tin sản phẩm", description = "API để lấy thông tin của một sản phẩm dựa vào ID.")
    ApiResponse<ProductResponse> getProduct(@PathVariable("productId") String productId) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(productId))
                .build();
    }

    @PostMapping("/batch")
    @Operation(
            summary = "Lấy danh sách sản phẩm theo danh sách id",
            description = "API để lấy danh sách sản phẩm theo danh sách id.")
    public ApiResponse<List<ProductResponse>> getProducts(@RequestBody ProductBatchRequest request) {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getProductsByIds(
                        request.getProductIds())) // ✅ Sử dụng DTO để lấy danh sách productIds
                .build();
    }
}
