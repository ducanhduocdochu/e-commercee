package com.example.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import com.example.product.dto.request.CategoryCreationRequest;
import com.example.product.dto.request.CategoryUpdateRequest;
import com.example.product.dto.response.CategoryResponse;
import com.example.product.entity.Category;
import com.example.product.mapper.CategoryMapper;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryCreationRequest categoryCreationRequest;
    private CategoryUpdateRequest categoryUpdateRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId("category-123");
        category.setName("Electronics");

        categoryCreationRequest = new CategoryCreationRequest("Electronics");
        categoryUpdateRequest = new CategoryUpdateRequest("Updated Electronics");

        categoryResponse = new CategoryResponse("category-123", "Electronics", null, null);
    }

    @Test
    void getCategories_ShouldReturnListOfCategories() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toCategoryResponseList(categories)).thenReturn(List.of(categoryResponse));

        List<CategoryResponse> result = categoryService.getCategories(10, 0, "name", "ASC");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
    }

    @Test
    void getCategory_ShouldReturnCategoryResponse_WhenCategoryExists() {
        when(categoryRepository.findById("category-123")).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

        CategoryResponse result = categoryService.getCategory("category-123");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
    }
}
