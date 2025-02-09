package com.example.product.service;

import java.util.List;
import java.util.Set;

import com.example.product.security.AuthorizationUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.product.dto.request.CategoryCreationRequest;
import com.example.product.dto.request.CategoryUpdateRequest;
import com.example.product.dto.response.CategoryResponse;
import com.example.product.entity.Category;
import com.example.product.exception.AppException;
import com.example.product.exception.ErrorCode;
import com.example.product.mapper.CategoryMapper;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {
    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryCreationRequest request) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_ADMIN"));
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        Category category = categoryMapper.toCategory(request);
        try {
            category = categoryRepository.save(category);

        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        return categoryMapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> getCategories(int pageSize, int pageNumber, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Category> categories = categoryRepository.findAll(pageable);
        return categoryMapper.toCategoryResponseList(categories.getContent());
    }

    public CategoryResponse getCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public CategoryResponse updateCategory(String categoryId, CategoryUpdateRequest request) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_ADMIN"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryMapper.updateCategoryFromRequest(request, category);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(String categoryId) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_ADMIN"));
        if (productRepository.existsByCategoryId(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_DELETE_FAILED);
        }
        categoryRepository.deleteById(categoryId);
    }
}
