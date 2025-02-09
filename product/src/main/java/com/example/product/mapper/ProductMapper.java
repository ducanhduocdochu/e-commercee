package com.example.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.product.dto.request.ProductCreationRequest;
import com.example.product.dto.request.ProductUpdateRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.entity.Product;
import com.example.product.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    ProductResponse toProductResponse(Product product);

    @Mapping(source = "request.name", target = "name")
    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "request.price", target = "price")
    @Mapping(source = "request.stock", target = "stock")
    @Mapping(source = "category", target = "category")
    @Mapping(target = "sellerId", ignore = true) // Không nhận sellerId từ request
    @Mapping(target = "createdAt", ignore = true) // Hibernate tự động set giá trị này
    @Mapping(target = "updatedAt", ignore = true) // Hibernate tự động cập nhật giá trị này
    Product toProduct(ProductCreationRequest request, Category category);

    @Mapping(target = "category", ignore = true) // Không cập nhật category từ request
    @Mapping(target = "createdAt", ignore = true) // Không thay đổi createdAt
    @Mapping(target = "updatedAt", ignore = true) // Hibernate tự động cập nhật updatedAt
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

    List<ProductResponse> toProductResponseList(List<Product> products);
}
