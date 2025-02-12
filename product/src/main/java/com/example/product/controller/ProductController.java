package com.example.product.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.product.dto.request.ApiResponse;
import com.example.product.dto.request.ProductCreationRequest;
import com.example.product.dto.request.ProductUpdateRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Product API", description = "Quản lý sản phẩm")
public class ProductController {
    ProductService productService;

    @PostMapping
    @Operation(summary = "Tạo sản phẩm", description = "API để tạo mới một sản phẩm trong hệ thống.")
    ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreationRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request))
                .build();
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách sản phẩm",
            description = "API để lấy danh sách toàn bộ sản phẩm trong hệ thống với phân trang và sắp xếp.")
    public ApiResponse<List<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getProducts(pageSize, pageNumber, sortBy, sortDirection))
                .build();
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Lấy thông tin sản phẩm", description = "API để lấy thông tin của một sản phẩm dựa vào ID.")
    ApiResponse<ProductResponse> getProduct(@PathVariable("productId") String productId) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProduct(productId))
                .build();
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(
            summary = "Lấy sản phẩm theo seller",
            description = "API để lấy danh sách sản phẩm của một người bán cụ thể với phân trang và sắp xếp.")
    public ApiResponse<List<ProductResponse>> getProductsBySeller(
            @PathVariable String sellerId,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getProductsBySeller(sellerId, pageSize, pageNumber, sortBy, sortDirection))
                .build();
    }

    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "Lấy sản phẩm theo danh mục",
            description = "API để lấy danh sách sản phẩm thuộc một danh mục cụ thể với phân trang và sắp xếp.")
    public ApiResponse<List<ProductResponse>> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getProductsByCategory(categoryId, pageSize, pageNumber, sortBy, sortDirection))
                .build();
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Xóa sản phẩm", description = "API để xóa một sản phẩm dựa vào ID.")
    ApiResponse<String> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ApiResponse.<String>builder().result("Product has been deleted").build();
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Cập nhật sản phẩm", description = "API để cập nhật thông tin của một sản phẩm.")
    ApiResponse<ProductResponse> updateProduct(
            @PathVariable String productId, @Valid @RequestBody ProductUpdateRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(productId, request))
                .build();
    }

    @PatchMapping("/{productId}/stock")
    @Operation(
            summary = "Cập nhật số lượng tồn kho",
            description = "API để cập nhật số lượng tồn kho của một sản phẩm.")
    ApiResponse<ProductResponse> updateStock(@PathVariable String productId, @RequestParam int stock) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateStock(productId, stock))
                .build();
    }
}
