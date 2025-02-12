package com.example.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

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

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

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
    }

    @Test
    void getProducts_ShouldReturnListOfProducts() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toProductResponseList(productList)).thenReturn(List.of(productResponse));

        List<ProductResponse> result = productService.getProducts(10, 0, "name", "ASC");

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    void getProduct_ShouldReturnProductResponse_WhenProductExists() {
        when(productRepository.findById("product-123")).thenReturn(Optional.of(product));
        when(productMapper.toProductResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.getProduct("product-123");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
    }

    @Test
    void getProduct_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct("invalid-id"))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    void getProductsByIds_ShouldReturnListOfProductResponses() {
        List<String> productIds = List.of("product-123");
        List<Product> products = List.of(product);

        when(productRepository.findAllById(productIds)).thenReturn(products);

        List<ProductResponse> result = productService.getProductsByIds(productIds);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    void getProductsBySeller_ShouldReturnListOfProducts() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findBySellerId("seller-123", pageable)).thenReturn(productPage);
        when(productMapper.toProductResponseList(productList)).thenReturn(List.of(productResponse));

        List<ProductResponse> result = productService.getProductsBySeller("seller-123", 10, 0, "createdAt", "DESC");

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    void getProductsByCategory_ShouldReturnListOfProducts() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findByCategoryId("category-123", pageable)).thenReturn(productPage);
        when(productMapper.toProductResponseList(productList)).thenReturn(List.of(productResponse));

        List<ProductResponse> result = productService.getProductsByCategory("category-123", 10, 0, "name", "ASC");

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
    }
}
