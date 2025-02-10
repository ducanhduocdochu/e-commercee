package com.example.cart.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.cart.dto.request.AddToCartRequest;
import com.example.cart.dto.request.ApiResponse;
import com.example.cart.dto.request.UpdateCartItemRequest;
import com.example.cart.dto.response.CartItemResponse;
import com.example.cart.dto.response.CartResponse;
import com.example.cart.dto.response.ProductResponse;
import com.example.cart.entity.Cart;
import com.example.cart.entity.CartItem;
import com.example.cart.exception.AppException;
import com.example.cart.exception.ErrorCode;
import com.example.cart.mapper.CartMapper;
import com.example.cart.repository.CartRepository;
import com.example.cart.security.AuthenticatedUser;
import com.example.cart.security.AuthorizationUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartService {
    CartRepository cartRepository;
    CartMapper cartMapper;
    RestTemplate restTemplate;

    @PreAuthorize("hasRole('BUYER')")
    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_BUYER"));
        String buyerId = getAuthenticatedBuyerId();
        Cart cart = cartRepository.findByBuyerId(buyerId).orElseGet(() -> new Cart(buyerId));

        log.info("Received AddToCartRequest with productId: {}", request.getProductId());

        // 1️⃣ Gửi API đến product-service để lấy thông tin sản phẩm
        String productServiceUrl = "http://localhost:5000/product/internal/products/" + request.getProductId();

        try {
            ResponseEntity<ApiResponse<ProductResponse>> response = restTemplate.exchange(
                    productServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<ProductResponse>>() {});

            log.info("Response status: {}", response.getStatusCode());
            log.info("Raw response body: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().getResult() != null) {
                ProductResponse product = response.getBody().getResult(); // ✅ Lấy dữ liệu từ "result"
                log.info("Received product: {}", product);

                Optional<CartItem> existingItem = cart.getItems().stream()
                        .filter(item -> item.getProductId().equals(product.getId()))
                        .findFirst();

                if (existingItem.isPresent()) {
                    existingItem.get().setQuantity(existingItem.get().getQuantity() + 1);
                } else {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProductId(product.getId());
                    newItem.setQuantity(1);
                    newItem.setPrice(product.getPrice());

                    cart.getItems().add(newItem);
                }

                cartRepository.save(cart);
                return cartMapper.toCartResponse(cart);
            } else {
                log.error("Unexpected response from product-service: {}", response);
            }
        } catch (Exception e) {
            log.error("Failed to fetch product from product-service for productId: {}", request.getProductId(), e);
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
    }

    @PreAuthorize("hasRole('BUYER')")
    @Transactional
    public List<CartResponse> getCart(int pageSize, int pageNumber, String sortBy, String sortDirection) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_BUYER"));

        String buyerId = getAuthenticatedBuyerId();
        Cart cart = cartRepository.findByBuyerId(buyerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), cart.getItems().size());
        List<CartItem> pagedCartItems = cart.getItems().subList(start, end);

        // 1️⃣ Gửi danh sách productId đến product-service bằng phương thức POST
        String productServiceUrl = "http://localhost:5000/product/internal/products/batch";
        Map<String, List<String>> requestBody = Map.of(
                "productIds",
                pagedCartItems.stream().map(CartItem::getProductId).toList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, List<String>>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<ApiResponse<List<ProductResponse>>> response = restTemplate.exchange(
                    productServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<List<ProductResponse>>>() {});

            log.info("Response status: {}", response.getStatusCode());
            log.info("Raw response body: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().getResult() != null) {
                List<ProductResponse> products = response.getBody().getResult();
                log.info("Received products: {}", products);

                // 2️⃣ Map ProductResponse vào CartItemResponse
                Map<String, ProductResponse> productMap =
                        products.stream().collect(Collectors.toMap(ProductResponse::getId, Function.identity()));

                List<CartItemResponse> cartItemResponses = pagedCartItems.stream()
                        .map(cartItem -> {
                            ProductResponse product = productMap.get(cartItem.getProductId());
                            CartItemResponse itemResponse = new CartItemResponse();
                            itemResponse.setProductId(cartItem.getProductId());
                            itemResponse.setProductName(product != null ? product.getName() : "Unknown Product");
                            itemResponse.setQuantity(cartItem.getQuantity());
                            itemResponse.setPrice(cartItem.getPrice());
                            itemResponse.setAddedAt(cartItem.getCreatedAt());
                            return itemResponse;
                        })
                        .toList();

                // 3️⃣ Tính tổng giá trị giỏ hàng
                BigDecimal totalPrice = cartItemResponses.stream()
                        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 4️⃣ Tạo CartResponse và trả về
                CartResponse cartResponse = new CartResponse();
                cartResponse.setCartId(cart.getId());
                cartResponse.setBuyerId(cart.getBuyerId());
                cartResponse.setItems(cartItemResponses);
                cartResponse.setTotalPrice(totalPrice);

                return List.of(cartResponse);
            } else {
                log.error("Unexpected response from product-service: {}", response);
            }
        } catch (Exception e) {
            log.error("Failed to fetch products from product-service for productIds: {}", requestBody, e);
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return List.of(cartMapper.toCartResponse(cart));
    }

    @PreAuthorize("hasRole('BUYER')")
    @Transactional
    public CartResponse updateCartItem(UpdateCartItemRequest request) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_BUYER"));

        String buyerId = getAuthenticatedBuyerId();
        Cart cart = cartRepository.findByBuyerId(buyerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            if (request.getQuantity() <= 0) {
                cart.getItems().remove(cartItem);
            } else {
                cartItem.setQuantity(request.getQuantity());
            }
        } else {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // Tính tổng giá trị giỏ hàng
        BigDecimal totalPrice = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartRepository.save(cart);
        CartResponse cartResponse = cartMapper.toCartResponse(cart);
        cartResponse.setTotalPrice(totalPrice);

        return cartResponse;
    }

    @PreAuthorize("hasRole('BUYER')")
    @Transactional
    public CartResponse removeFromCart(String productId) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_BUYER"));

        String buyerId = getAuthenticatedBuyerId();
        Cart cart = cartRepository.findByBuyerId(buyerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (!removed) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // Tính tổng giá trị giỏ hàng sau khi xóa sản phẩm
        BigDecimal totalPrice = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartRepository.save(cart);
        CartResponse cartResponse = cartMapper.toCartResponse(cart);
        cartResponse.setTotalPrice(totalPrice);

        return cartResponse;
    }

    @PreAuthorize("hasRole('BUYER')")
    @Transactional
    public void clearCart() {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_BUYER"));

        String buyerId = getAuthenticatedBuyerId();
        Cart cart = cartRepository.findByBuyerId(buyerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private String getAuthenticatedBuyerId() {
        Object principal =
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof AuthenticatedUser)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return ((AuthenticatedUser) principal).getUserId();
    }
}
