package com.example.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.example.product.dto.request.ProductBatchRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProductInternalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductInternalController productInternalController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productInternalController).build();
    }

    @Test
    void getProduct_ShouldReturnProduct() throws Exception {
        // Given
        String productId = "062ef0cb-92e6-4a10-8521-f5a3db4ad464";
        ProductResponse productResponse = new ProductResponse(
                productId,
                "Laptop Dell XPS 15",
                "Laptop cao cấp",
                new BigDecimal("2999.99"),
                10,
                "1e0af692-8b4e-4387-a026-fc72156e7f47",
                "ed62ae3d-d973-4af3-9888-87f2a6a43962",
                null,
                null);

        when(productService.getProduct(eq(productId))).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(get("/internal/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(productId))
                .andExpect(jsonPath("$.result.name").value("Laptop Dell XPS 15"))
                .andExpect(jsonPath("$.result.price").value(2999.99));
    }

    @Test
    void getProducts_ShouldReturnListOfProducts() throws Exception {
        // Given
        List<String> productIds =
                List.of("062ef0cb-92e6-4a10-8521-f5a3db4ad464", "0c5430a0-9f30-42e3-a8ed-39c68db8e4ef");

        List<ProductResponse> products = List.of(
                new ProductResponse(
                        "062ef0cb-92e6-4a10-8521-f5a3db4ad464",
                        "Laptop Dell XPS 15",
                        "Laptop cao cấp",
                        new BigDecimal("2999.99"),
                        10,
                        "1e0af692-8b4e-4387-a026-fc72156e7f47",
                        "seller1",
                        null,
                        null),
                new ProductResponse(
                        "0c5430a0-9f30-42e3-a8ed-39c68db8e4ef",
                        "MacBook Pro",
                        "Máy tính xách tay cao cấp",
                        new BigDecimal("3499.99"),
                        5,
                        "2a0af692-8b4e-4387-a026-fc72156e7f47",
                        "seller2",
                        null,
                        null));

        ProductBatchRequest request = new ProductBatchRequest(productIds);

        when(productService.getProductsByIds(any())).thenReturn(products);

        // When & Then
        mockMvc.perform(post("/internal/products/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value("062ef0cb-92e6-4a10-8521-f5a3db4ad464"))
                .andExpect(jsonPath("$.result[0].name").value("Laptop Dell XPS 15"))
                .andExpect(jsonPath("$.result[1].id").value("0c5430a0-9f30-42e3-a8ed-39c68db8e4ef"))
                .andExpect(jsonPath("$.result[1].name").value("MacBook Pro"));
    }
}
