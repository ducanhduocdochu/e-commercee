package com.example.product.service;

import com.example.product.dto.request.CategoryCreationRequest;
import com.example.product.dto.request.CategoryUpdateRequest;
import com.example.product.dto.response.CategoryResponse;
import com.example.product.entity.Category;
import com.example.product.exception.AppException;
import com.example.product.exception.ErrorCode;
import com.example.product.mapper.CategoryMapper;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.security.AuthenticatedUser;
import com.example.product.security.AuthorizationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private void mockSecurityContext(String userId, String rawRoles) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, rawRoles);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);

        // ✅ Chuyển danh sách role thành Collection<? extends GrantedAuthority>
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(rawRoles.split(" "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        when(authentication.getAuthorities()).thenReturn(authorities); // ✅ Đúng kiểu dữ liệu

        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuthorizationUtil() {
        try (var mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class)) {
            mockedAuthorizationUtil.when(() -> AuthorizationUtil.checkAuthorities(any()))
                    .thenAnswer(invocation -> null);
        }
    }

    @Test
    void createCategory_ShouldReturnCategoryResponse_WhenSuccessful() {
        mockSecurityContext("admin-123", "ROLE_ADMIN");
        mockAuthorizationUtil();

        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());
        when(categoryMapper.toCategory(categoryCreationRequest)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

        CategoryResponse result = categoryService.createCategory(categoryCreationRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrowException_WhenCategoryExists() {
        mockSecurityContext("admin-123", "ROLE_ADMIN");
        mockAuthorizationUtil();

        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.createCategory(categoryCreationRequest))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.CATEGORY_EXISTED.getMessage());
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

    @Test
    void updateCategory_ShouldUpdateAndReturnCategoryResponse() {
        mockSecurityContext("admin-123", "ROLE_ADMIN");
        mockAuthorizationUtil();

        when(categoryRepository.findById("category-123")).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

        CategoryResponse result = categoryService.updateCategory("category-123", categoryUpdateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldCallRepositoryDeleteMethod_WhenNoProductsExist() {
        mockSecurityContext("admin-123", "ROLE_ADMIN");
        mockAuthorizationUtil();

        when(productRepository.existsByCategoryId("category-123")).thenReturn(false);
        doNothing().when(categoryRepository).deleteById("category-123");

        categoryService.deleteCategory("category-123");

        verify(categoryRepository, times(1)).deleteById("category-123");
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenProductsExist() {
        mockSecurityContext("admin-123", "ROLE_ADMIN");
        mockAuthorizationUtil();

        when(productRepository.existsByCategoryId("category-123")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory("category-123"))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.CATEGORY_DELETE_FAILED.getMessage());
    }
}
