package com.example.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.product.dto.response.CategoryResponse;
import com.example.product.dto.request.CategoryCreationRequest;
import com.example.product.dto.request.CategoryUpdateRequest;
import com.example.product.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Chuyển từ Entity -> DTO Response
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    CategoryResponse toCategoryResponse(Category category);

    // Chuyển từ Request -> Entity khi tạo mới
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toCategory(CategoryCreationRequest request);

    // Cập nhật Entity từ Request
    @Mapping(target = "createdAt", ignore = true) // Không thay đổi `createdAt` khi cập nhật
    @Mapping(target = "updatedAt", ignore = true) // Hibernate tự động cập nhật `updatedAt`
    void updateCategoryFromRequest(CategoryUpdateRequest request, @MappingTarget Category category);

    // Chuyển danh sách Entity -> danh sách DTO Response
    List<CategoryResponse> toCategoryResponseList(List<Category> categories);
}
