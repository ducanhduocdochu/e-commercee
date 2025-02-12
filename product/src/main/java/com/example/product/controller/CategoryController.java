package com.example.product.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.product.dto.request.ApiResponse;
import com.example.product.dto.request.CategoryCreationRequest;
import com.example.product.dto.request.CategoryUpdateRequest;
import com.example.product.dto.response.CategoryResponse;
import com.example.product.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Category API", description = "Quản lý danh mục sản phẩm")
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Tạo danh mục", description = "API để tạo mới một danh mục trong hệ thống.")
    ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryCreationRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách danh mục",
            description = "API để lấy danh sách danh mục sản phẩm với phân trang và sắp xếp.")
    public ApiResponse<List<CategoryResponse>> getCategories(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getCategories(pageSize, pageNumber, sortBy, sortDirection))
                .build();
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Lấy thông tin danh mục", description = "API để lấy thông tin của một danh mục dựa vào ID.")
    ApiResponse<CategoryResponse> getCategory(@PathVariable("categoryId") String categoryId) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.getCategory(categoryId))
                .build();
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Cập nhật danh mục", description = "API để cập nhật thông tin danh mục dựa vào ID.")
    ApiResponse<CategoryResponse> updateCategory(
            @PathVariable String categoryId, @Valid @RequestBody CategoryUpdateRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(categoryId, request))
                .build();
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Xóa danh mục", description = "API để xóa danh mục dựa vào ID.")
    ApiResponse<String> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<String>builder().result("Category has been deleted").build();
    }
}
