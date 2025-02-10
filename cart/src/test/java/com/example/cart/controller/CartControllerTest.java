package com.example.cart.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import com.example.cart.dto.request.AddToCartRequest;
import com.example.cart.dto.request.RemoveFromCartRequest;
import com.example.cart.dto.request.UpdateCartItemRequest;
import com.example.cart.dto.response.CartItemResponse;
import com.example.cart.dto.response.CartResponse;
import com.example.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CartResponse cartResponse;
    private CartItemResponse cartItemResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        cartItemResponse = CartItemResponse.builder()
                .productId("product-123")
                .productName("Laptop Dell")
                .quantity(2)
                .price(new BigDecimal("999.99"))
                .addedAt(LocalDateTime.now())
                .build();

        cartResponse = CartResponse.builder()
                .cartId("cart-123")
                .buyerId("buyer-123")
                .items(List.of(cartItemResponse))
                .totalPrice(new BigDecimal("1999.98"))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void addToCart_ShouldReturnCartResponse_WhenSuccessful() throws Exception {
        AddToCartRequest request = new AddToCartRequest("product-123");

        when(cartService.addToCart(any(AddToCartRequest.class))).thenReturn(cartResponse);

        mockMvc.perform(post("/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.cartId").value("cart-123"))
                .andExpect(jsonPath("$.result.buyerId").value("buyer-123"))
                .andExpect(jsonPath("$.result.items[0].productId").value("product-123"))
                .andExpect(jsonPath("$.result.items[0].quantity").value(2));
    }

    @Test
    void getCart_ShouldReturnCartList() throws Exception {
        List<CartResponse> cartItems = List.of(cartResponse);

        when(cartService.getCart(10, 0, "addedAt", "DESC")).thenReturn(cartItems);

        mockMvc.perform(get("/").param("pageSize", "10")
                        .param("pageNumber", "0")
                        .param("sortBy", "addedAt")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].cartId").value("cart-123"))
                .andExpect(jsonPath("$.result[0].buyerId").value("buyer-123"))
                .andExpect(jsonPath("$.result[0].items[0].productId").value("product-123"));
    }

    @Test
    void updateCartItem_ShouldReturnUpdatedCartResponse() throws Exception {
        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .productId("product-123")
                .quantity(3)
                .build();

        CartResponse updatedCartResponse = CartResponse.builder()
                .cartId("cart-123")
                .buyerId("buyer-123")
                .items(List.of(CartItemResponse.builder()
                        .productId("product-123")
                        .productName("Laptop Dell")
                        .quantity(3)
                        .price(new BigDecimal("999.99"))
                        .addedAt(LocalDateTime.now())
                        .build()))
                .totalPrice(new BigDecimal("2999.97"))
                .updatedAt(LocalDateTime.now())
                .build();

        when(cartService.updateCartItem(any(UpdateCartItemRequest.class))).thenReturn(updatedCartResponse);

        mockMvc.perform(put("/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.cartId").value("cart-123"))
                .andExpect(jsonPath("$.result.items[0].quantity").value(3));
    }

    @Test
    void removeFromCart_ShouldReturnCartResponse() throws Exception {
        when(cartService.removeFromCart("product-123")).thenReturn(cartResponse);

        mockMvc.perform(delete("/remove/{productId}", "product-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.cartId").value("cart-123"))
                .andExpect(jsonPath("$.result.buyerId").value("buyer-123"))
                .andExpect(jsonPath("$.result.items[0].productId").value("product-123"));
    }

    @Test
    void clearCart_ShouldReturnSuccessMessage() throws Exception {
        doNothing().when(cartService).clearCart();

        mockMvc.perform(delete("/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Giỏ hàng đã được làm trống"));
    }

    @Test
    void removeMultipleItemsFromCart_ShouldReturnUpdatedCartResponse() throws Exception {
        RemoveFromCartRequest request = RemoveFromCartRequest.builder()
                .productIds(List.of("product-123", "product-456"))
                .build();

        when(cartService.removeFromCart(anyString())).thenReturn(cartResponse);

        mockMvc.perform(delete("/remove/{productId}", "product-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.cartId").value("cart-123"));
    }
}
