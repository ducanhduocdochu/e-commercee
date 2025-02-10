package com.example.cart.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.cart.dto.request.AddToCartRequest;
import com.example.cart.dto.request.ApiResponse;
import com.example.cart.dto.request.UpdateCartItemRequest;
import com.example.cart.dto.response.CartResponse;
import com.example.cart.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Cart API", description = "Quản lý giỏ hàng của Buyer")
public class CartController {
    CartService cartService;

    @PostMapping("/add")
    @Operation(
            summary = "Thêm sản phẩm vào giỏ hàng",
            description = "Buyer thêm danh sách sản phẩm vào giỏ hàng của mình.")
    ApiResponse<CartResponse> addToCart(@RequestBody @Valid AddToCartRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addToCart(request))
                .build();
    }

    @GetMapping
    @Operation(
            summary = "Lấy giỏ hàng của Buyer",
            description = "Trả về danh sách sản phẩm trong giỏ hàng của Buyer với phân trang và sắp xếp.")
    public ApiResponse<List<CartResponse>> getCart(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "addedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        return ApiResponse.<List<CartResponse>>builder()
                .result(cartService.getCart(pageSize, pageNumber, sortBy, sortDirection))
                .build();
    }

    @PutMapping("/update")
    @Operation(summary = "Cập nhật số lượng sản phẩm trong giỏ hàng", description = "Buyer cập nhật số lượng sản phẩm.")
    ApiResponse<CartResponse> updateCartItem(@RequestBody @Valid UpdateCartItemRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateCartItem(request))
                .build();
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(
            summary = "Xóa sản phẩm khỏi giỏ hàng",
            description = "Buyer có thể xóa một sản phẩm khỏi giỏ hàng của mình.")
    ApiResponse<CartResponse> removeFromCart(@PathVariable String productId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.removeFromCart(productId))
                .build();
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Xóa toàn bộ giỏ hàng", description = "Buyer có thể xóa toàn bộ sản phẩm trong giỏ hàng.")
    ApiResponse<String> clearCart() {
        cartService.clearCart();
        return ApiResponse.<String>builder()
                .result("Giỏ hàng đã được làm trống")
                .build();
    }
}
