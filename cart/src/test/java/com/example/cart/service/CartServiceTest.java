package com.example.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import com.example.cart.dto.request.AddToCartRequest;
import com.example.cart.dto.request.ApiResponse;
import com.example.cart.dto.request.UpdateCartItemRequest;
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

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartItem cartItem;
    private ProductResponse productResponse;
    private AddToCartRequest addToCartRequest;
    private CartResponse cartResponse;

    private static MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

    @BeforeEach
    void setUp() {
        if (mockedAuthorizationUtil == null) {
            mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
            mockedAuthorizationUtil
                    .when(() -> AuthorizationUtil.checkAuthorities(any()))
                    .thenAnswer(invocation -> null);
        }

        mockSecurityContext("buyer-123", "ROLE_BUYER");

        cart = new Cart();
        cart.setId("cart-123");
        cart.setBuyerId("buyer-123");

        cartItem = new CartItem();
        cartItem.setProductId("product-123");
        cartItem.setQuantity(2);
        cartItem.setPrice(BigDecimal.valueOf(100));
        cartItem.setCreatedAt(LocalDateTime.now());

        cart.setItems(List.of(cartItem));

        productResponse = new ProductResponse(
                "product-123",
                "Laptop Dell",
                "Description",
                BigDecimal.valueOf(100),
                10,
                "category-123",
                "seller-123",
                null,
                null);

        addToCartRequest = new AddToCartRequest("product-123");

        cartResponse = new CartResponse("cart-123", "buyer-123", null, BigDecimal.valueOf(100), null);
    }

    private void mockSecurityContext(String userId, String rawRoles) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, rawRoles);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(authenticatedUser);

        Collection<GrantedAuthority> authorities = Arrays.stream(rawRoles.split(" "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        lenient().when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCart_ShouldReturnCartResponse_WhenCartExists() {
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        ResponseEntity<ApiResponse<List<ProductResponse>>> responseEntity =
                ResponseEntity.ok(new ApiResponse<>(1000, "Success", List.of(productResponse)));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<CartResponse> actualResponse = cartService.getCart(10, 0, "createdAt", "DESC");

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).hasSize(1);
        assertThat(actualResponse.get(0).getCartId()).isEqualTo("cart-123");
    }

    @Test
    void getCart_ShouldThrowException_WhenCartNotFound() {
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getCart(10, 0, "createdAt", "DESC"))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.CART_NOT_FOUND.getMessage());
    }

    @Test
    void addToCart_ShouldIncreaseQuantity_WhenProductAlreadyInCart() {
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        ResponseEntity<ApiResponse<ProductResponse>> responseEntity =
                ResponseEntity.ok(new ApiResponse<>(1000, "Success", productResponse));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(cartResponse);

        CartResponse result = cartService.addToCart(addToCartRequest);

        verify(cartRepository, times(1)).save(any(Cart.class));
        assertThat(result).isNotNull();
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void addToCart_ShouldAddNewItem_WhenProductNotInCart() {
        // ✅ Tạo danh sách có thể thay đổi được
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        ResponseEntity<ApiResponse<ProductResponse>> responseEntity =
                ResponseEntity.ok(new ApiResponse<>(1000, "Success", productResponse));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(cartResponse);

        CartResponse result = cartService.addToCart(addToCartRequest);

        verify(cartRepository, times(1)).save(any(Cart.class));
        assertThat(result).isNotNull();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    void addToCart_ShouldThrowException_WhenProductNotFound() {
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Product not found"));

        assertThatThrownBy(() -> cartService.addToCart(addToCartRequest))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    void addToCart_ShouldThrowException_WhenUnauthenticated() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("AnonymousUser");

        SecurityContextHolder.setContext(securityContext);

        assertThatThrownBy(() -> cartService.addToCart(addToCartRequest))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.UNAUTHENTICATED.getMessage());
    }

    //    @Test
    //    void getCart_ShouldThrowException_WhenProductServiceFails() {
    //        // Giả lập giỏ hàng tồn tại
    //        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));
    //
    //        // Trả về response không thành công từ product-service
    //        ResponseEntity<ApiResponse<List<ProductResponse>>> responseEntity =
    //                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(1000, "Error",
    // null));
    //
    //        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(),
    // any(ParameterizedTypeReference.class)))
    //                .thenReturn(responseEntity);
    //
    //        // Kiểm tra rằng sẽ ném ra ngoại lệ khi product-service trả về phản hồi bất thường
    //        assertThatThrownBy(() -> cartService.getCart(10, 0, "createdAt", "DESC"))
    //                .isInstanceOf(AppException.class)
    //                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage()); // Kiểm tra thông điệp ngoại lệ
    //    }

    //    @Test
    //    void getCart_ShouldThrowException_WhenProductServiceReturnsEmpty() {
    //        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));
    //
    //        // Trả về phản hồi từ product-service với kết quả trống (danh sách sản phẩm rỗng)
    //        ResponseEntity<ApiResponse<List<ProductResponse>>> responseEntity = ResponseEntity.ok(
    //                new ApiResponse<>(1000, "Success", Collections.emptyList())); // Empty list
    //
    //        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(),
    //                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);
    //
    //        // Kiểm tra rằng ngoại lệ AppException sẽ được ném ra khi productService trả về danh sách trống
    //        assertThatThrownBy(() -> cartService.getCart(10, 0, "createdAt", "DESC"))
    //                .isInstanceOf(AppException.class) // Kiểm tra ngoại lệ
    //                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());  // Kiểm tra thông báo lỗi
    //    }

    @Test
    void getCart_ShouldThrowException_WhenExceptionOccursDuringProductServiceCall() {
        // Giả lập giỏ hàng tồn tại
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        // Giả lập lỗi khi gọi product-service
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Product service is down"));

        // Kiểm tra rằng sẽ ném ra ngoại lệ khi có lỗi xảy ra trong khi gọi product-service
        assertThatThrownBy(() -> cartService.getCart(10, 0, "createdAt", "DESC"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage()); // Kiểm tra thông điệp ngoại lệ
    }

    @Test
    void updateCartItem_ShouldUpdateCartItem_WhenProductExistsAndQuantityIsPositive() {
        // Chuẩn bị đối tượng request và cart
        UpdateCartItemRequest request = new UpdateCartItemRequest("product-123", 3);

        Cart cart = new Cart();
        cart.setId("cart-123");
        cart.setBuyerId("buyer-123");
        cart.setItems(new ArrayList<>());

        // Tạo CartItem với đầy đủ tham số theo constructor
        CartItem cartItem = new CartItem(
                "product-123", // productId
                cart, // Cart
                "product-123", // productName (hoặc có thể là null nếu không sử dụng)
                1, // quantity ban đầu
                BigDecimal.valueOf(100), // price
                LocalDateTime.now(), // createdAt
                LocalDateTime.now() // updatedAt
                );
        cart.getItems().add(cartItem);

        // Mock trả về cart khi tìm kiếm theo buyerId
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        // Mock cartMapper để trả về CartResponse hợp lệ
        CartResponse cartResponseMock = new CartResponse();
        cartResponseMock.setCartId(cart.getId());
        cartResponseMock.setBuyerId(cart.getBuyerId());
        cartResponseMock.setTotalPrice(BigDecimal.valueOf(300));
        when(cartMapper.toCartResponse(cart)).thenReturn(cartResponseMock);

        // Gọi phương thức updateCartItem
        CartResponse cartResponse = cartService.updateCartItem(request);

        // Kiểm tra kết quả
        assertThat(cartResponse).isNotNull();
        assertThat(cartResponse.getTotalPrice())
                .isEqualByComparingTo(BigDecimal.valueOf(300)); // Kiểm tra tổng giá trị giỏ hàng
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3); // Kiểm tra số lượng sản phẩm đã được cập nhật
    }

    @Test
    void updateCartItem_ShouldRemoveCartItem_WhenQuantityIsZero() {
        // Tạo request với số lượng 0
        UpdateCartItemRequest request = new UpdateCartItemRequest("product-123", 0);

        // Mock giỏ hàng
        Cart cart = new Cart();
        cart.setId("cart-123");
        cart.setBuyerId("buyer-123");

        // Thay vì sử dụng danh sách mặc định không thể thay đổi, tạo một ArrayList
        List<CartItem> cartItems = new ArrayList<>();
        cart.setItems(cartItems);

        // Tạo item và thêm vào giỏ hàng
        CartItem cartItem = new CartItem(
                "product-123", // productId
                cart, // Cart
                "product-123", // productName
                1, // quantity
                BigDecimal.valueOf(100), // price
                LocalDateTime.now(), // createdAt
                LocalDateTime.now() // updatedAt
                );
        cartItems.add(cartItem); // Thêm vào ArrayList (giỏ hàng có thể thay đổi)

        // Mock cartRepository
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        // Mock cartMapper trả về CartResponse hợp lệ
        CartResponse mockCartResponse = new CartResponse();
        when(cartMapper.toCartResponse(cart)).thenReturn(mockCartResponse);

        // Gọi phương thức updateCartItem
        CartResponse cartResponse = cartService.updateCartItem(request);

        // Kiểm tra kết quả
        assertThat(cartResponse).isNotNull();
        assertThat(cartItems).isEmpty(); // Kiểm tra sản phẩm đã được xóa khỏi giỏ hàng
    }

    @Test
    void updateCartItem_ShouldThrowException_WhenProductNotInCart() {
        UpdateCartItemRequest request = new UpdateCartItemRequest("product-999", 3);

        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.updateCartItem(request))
                .isInstanceOf(AppException.class)
                .hasMessage(
                        ErrorCode.CART_ITEM_NOT_FOUND
                                .getMessage()); // Kiểm tra nếu sản phẩm không có trong giỏ hàng thì ném ngoại lệ
    }

    @Test
    void removeFromCart_ShouldRemoveProduct_WhenProductExists() {
        String productId = "product-123";

        // Tạo giỏ hàng với một ArrayList để đảm bảo có thể thay đổi
        Cart cart = new Cart();
        cart.setId("cart-123");
        cart.setBuyerId("buyer-123");
        List<CartItem> cartItems = new ArrayList<>();
        cart.setItems(cartItems);

        // Tạo item và thêm vào giỏ hàng
        CartItem cartItem = new CartItem(
                "product-123", // productId
                cart, // Cart
                "product-123", // productName
                1, // quantity
                BigDecimal.valueOf(100), // price
                LocalDateTime.now(), // createdAt
                LocalDateTime.now() // updatedAt
                );
        cartItems.add(cartItem); // Thêm vào giỏ hàng (ArrayList có thể thay đổi)

        // Mock cartRepository trả về giỏ hàng với các sản phẩm
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        // Mock cartMapper trả về CartResponse hợp lệ
        CartResponse mockCartResponse = new CartResponse();
        when(cartMapper.toCartResponse(cart)).thenReturn(mockCartResponse);

        // Gọi phương thức removeFromCart
        CartResponse cartResponse = cartService.removeFromCart(productId);

        // Kiểm tra kết quả
        assertThat(cartResponse).isNotNull();
        assertThat(cartItems).isEmpty(); // Kiểm tra sản phẩm đã được xóa khỏi giỏ hàng
    }

    @Test
    void removeFromCart_ShouldThrowException_WhenProductNotInCart() {
        String productId = "product-999";

        // Tạo giỏ hàng với một ArrayList để đảm bảo có thể thay đổi
        Cart cart = new Cart();
        cart.setId("cart-123");
        cart.setBuyerId("buyer-123");
        List<CartItem> cartItems = new ArrayList<>();
        cart.setItems(cartItems); // Giỏ hàng có thể thay đổi

        // Mock cartRepository trả về giỏ hàng rỗng
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        // Gọi phương thức removeFromCart và kiểm tra ngoại lệ
        assertThatThrownBy(() -> cartService.removeFromCart(productId))
                .isInstanceOf(AppException.class)
                .hasMessage(
                        ErrorCode.CART_ITEM_NOT_FOUND
                                .getMessage()); // Kiểm tra nếu sản phẩm không có trong giỏ hàng thì ném ngoại lệ
    }

    @Test
    void clearCart_ShouldClearAllItems_WhenCartExists() {
        // Giả sử cart đã được khởi tạo và có thể thay đổi
        Cart cart = new Cart();
        cart.setId("cart-123");
        cart.setBuyerId("buyer-123");
        cart.setItems(new ArrayList<CartItem>()); // Dùng ArrayList thay vì một List immutable

        // Mock cartRepository trả về giỏ hàng có thể thay đổi
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.of(cart));

        // Gọi phương thức clearCart và kiểm tra rằng các items đã bị xóa
        cartService.clearCart();

        // Kiểm tra rằng giỏ hàng đã trống
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void clearCart_ShouldThrowException_WhenCartNotFound() {
        when(cartRepository.findByBuyerId("buyer-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.clearCart())
                .isInstanceOf(AppException.class)
                .hasMessage(
                        ErrorCode.CART_NOT_FOUND.getMessage()); // Kiểm tra nếu không tìm thấy giỏ hàng thì ném ngoại lệ
    }
}
