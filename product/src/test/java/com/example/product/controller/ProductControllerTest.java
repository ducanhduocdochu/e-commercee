package com.example.product.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.product.dto.request.ProductCreationRequest;
import com.example.product.dto.request.ProductUpdateRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void createProduct_ShouldReturnProduct() throws Exception {
        ProductCreationRequest request = new ProductCreationRequest(
                "Laptop Dell XPS 15", "Mô tả", new BigDecimal("2999.99"), 10, "1e0af692-8b4e-4387-a026-fc72156e7f47");

        ProductResponse productResponse = new ProductResponse(
                "123",
                "Laptop Dell XPS 15",
                "Mô tả",
                new BigDecimal("2999.99"),
                10,
                "1e0af692-8b4e-4387-a026-fc72156e7f47",
                "seller123",
                null,
                null);

        when(productService.createProduct(any())).thenReturn(productResponse);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("123"))
                .andExpect(jsonPath("$.result.name").value("Laptop Dell XPS 15"));
    }

    @Test
    void getProduct_ShouldReturnProduct() throws Exception {
        String productId = "123";
        ProductResponse productResponse = new ProductResponse(
                "123",
                "Laptop Dell XPS 15",
                "Mô tả",
                new BigDecimal("2999.99"),
                10,
                "1e0af692-8b4e-4387-a026-fc72156e7f47",
                "seller123",
                null,
                null);

        when(productService.getProduct(eq(productId))).thenReturn(productResponse);

        mockMvc.perform(get("/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("123"))
                .andExpect(jsonPath("$.result.name").value("Laptop Dell XPS 15"));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        String productId = "123";
        ProductUpdateRequest request =
                new ProductUpdateRequest("Laptop Dell XPS 15", "Mô tả cập nhật", new BigDecimal("3199.99"), 12);

        ProductResponse productResponse = new ProductResponse(
                "123",
                "Laptop Dell XPS 15",
                "Mô tả cập nhật",
                new BigDecimal("3199.99"),
                12,
                "1e0af692-8b4e-4387-a026-fc72156e7f47",
                "seller123",
                null,
                null);

        when(productService.updateProduct(eq(productId), any())).thenReturn(productResponse);

        mockMvc.perform(put("/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Laptop Dell XPS 15"))
                .andExpect(jsonPath("$.result.price").value(3199.99));
    }

    @Test
    void deleteProduct_ShouldReturnSuccessMessage() throws Exception {
        String productId = "123";
        doNothing().when(productService).deleteProduct(eq(productId));

        mockMvc.perform(delete("/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Product has been deleted"));
    }

    @Test
    void getProductsBySeller_ShouldReturnListOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                new ProductResponse(
                        "123", "Laptop Dell", "Mô tả", BigDecimal.valueOf(2000), 10, "1", "seller123", null, null),
                new ProductResponse(
                        "456", "MacBook Pro", "Mô tả", BigDecimal.valueOf(2500), 5, "2", "seller123", null, null));

        when(productService.getProductsBySeller(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(products);

        mockMvc.perform(get("/products/seller/seller123")
                        .param("pageSize", "10")
                        .param("pageNumber", "0")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value("123"))
                .andExpect(jsonPath("$.result[1].id").value("456"));
    }

    @Test
    void updateStock_ShouldReturnUpdatedProduct() throws Exception {
        String productId = "123";
        int newStock = 15;

        ProductResponse productResponse = new ProductResponse(
                "123",
                "Laptop Dell XPS 15",
                "Mô tả",
                new BigDecimal("2999.99"),
                newStock,
                "category1",
                "seller123",
                null,
                null);

        when(productService.updateStock(eq(productId), eq(newStock))).thenReturn(productResponse);

        mockMvc.perform(patch("/products/{productId}/stock", productId).param("stock", String.valueOf(newStock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.stock").value(15));
    }

    @Test
    void getProductsByCategory_ShouldReturnListOfProducts() throws Exception {
        String categoryId = "category-123";
        List<ProductResponse> products = List.of(
                new ProductResponse(
                        "123",
                        "Laptop Dell",
                        "Mô tả",
                        BigDecimal.valueOf(2000),
                        10,
                        categoryId,
                        "seller123",
                        null,
                        null),
                new ProductResponse(
                        "456",
                        "MacBook Pro",
                        "Mô tả",
                        BigDecimal.valueOf(2500),
                        5,
                        categoryId,
                        "seller123",
                        null,
                        null));

        when(productService.getProductsByCategory(eq(categoryId), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(products);

        mockMvc.perform(get("/products/category/{categoryId}", categoryId)
                        .param("pageSize", "10")
                        .param("pageNumber", "0")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value("123"))
                .andExpect(jsonPath("$.result[1].id").value("456"));
    }

    @Test
    void getProducts_ShouldReturnListOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                new ProductResponse(
                        "123",
                        "Laptop Dell",
                        "Mô tả",
                        BigDecimal.valueOf(2000),
                        10,
                        "category1",
                        "seller123",
                        null,
                        null),
                new ProductResponse(
                        "456",
                        "MacBook Pro",
                        "Mô tả",
                        BigDecimal.valueOf(2500),
                        5,
                        "category2",
                        "seller456",
                        null,
                        null));

        when(productService.getProducts(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(products);

        mockMvc.perform(get("/products")
                        .param("pageSize", "10")
                        .param("pageNumber", "0")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value("123"))
                .andExpect(jsonPath("$.result[1].id").value("456"));
    }
}
