package com.example.product.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.product.dto.request.ProductCreationRequest;
import com.example.product.dto.request.ProductUpdateRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.entity.Category;
import com.example.product.entity.Product;
import com.example.product.exception.AppException;
import com.example.product.exception.ErrorCode;
import com.example.product.mapper.ProductMapper;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.security.AuthenticatedUser;
import com.example.product.security.AuthorizationUtil;

@ExtendWith(MockitoExtension.class)
class ProductServiceAuthTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;
    private ProductCreationRequest productCreationRequest;
    private ProductUpdateRequest productUpdateRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId("category-123");

        product = new Product();
        product.setId("product-123");
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(10);
        product.setCategory(category);
        product.setSellerId("seller-123");

        productCreationRequest =
                new ProductCreationRequest("Test Product", "Description", BigDecimal.valueOf(100), 10, "category-123");

        productUpdateRequest =
                new ProductUpdateRequest("Updated Product", "Updated Description", BigDecimal.valueOf(150), 15);

        productResponse = new ProductResponse(
                "product-123",
                "Test Product",
                "Description",
                BigDecimal.valueOf(100),
                10,
                "category-123",
                "seller-123",
                null,
                null);

        // ✅ Mock SecurityContext và AuthorizationUtil trong setup
        mockSecurityContext("seller-123", "ROLE_SELLER");
        mockAuthorizationUtil();
    }

    private void mockSecurityContext(String userId, String rawRoles) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, rawRoles);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(authenticatedUser);

        // ✅ Chuyển danh sách role thành Collection<? extends GrantedAuthority>
        Collection<GrantedAuthority> authorities = Arrays.stream(rawRoles.split(" "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        lenient().when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuthorizationUtil() {
        try (var mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class)) {
            mockedAuthorizationUtil
                    .when(() -> AuthorizationUtil.checkAuthorities(any()))
                    .thenAnswer(invocation -> null);
        }
    }

    @Test
    void createProduct_ShouldReturnProductResponse_WhenSuccessful() {
        when(categoryRepository.findById("category-123")).thenReturn(Optional.of(category));
        when(productMapper.toProduct(productCreationRequest, category)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.createProduct(productCreationRequest);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenSellerIdIsMissing() {
        // ✅ Mock SecurityContext nhưng không có sellerId
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(null, "ROLE_SELLER"); // sellerId = null

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        SecurityContextHolder.setContext(securityContext);

        // ✅ Không kiểm tra quyền trong test này để đảm bảo lỗi đúng đến từ sellerId bị null
        try (var mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class)) {
            mockedAuthorizationUtil
                    .when(() -> AuthorizationUtil.checkAuthorities(any()))
                    .thenAnswer(invocation -> null);

            assertThatThrownBy(() -> productService.createProduct(productCreationRequest))
                    .isInstanceOf(AppException.class)
                    .hasMessage(ErrorCode.UNAUTHENTICATED.getMessage());
        }
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProductResponse() {
        when(productRepository.findById("product-123")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.updateProduct("product-123", productUpdateRequest);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldCallRepositoryDeleteMethod() {
        doNothing().when(productRepository).deleteById("product-123");

        productService.deleteProduct("product-123");

        verify(productRepository, times(1)).deleteById("product-123");
    }

    @Test
    void updateStock_ShouldUpdateStock_WhenSuccessful() {
        when(productRepository.findById("product-123")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.updateStock("product-123", 50);

        verify(productRepository, times(1)).save(any(Product.class));
    }
}
