package com.example.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // General Errors
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),

    // User Errors
    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User does not exist", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1010, "Invalid email format", HttpStatus.BAD_REQUEST),

    // Product Errors
    PRODUCT_NOT_FOUND(2001, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_EXISTED(2002, "Product already exists", HttpStatus.BAD_REQUEST),
    PRODUCT_INVALID_PRICE(2003, "Product price must be greater than zero", HttpStatus.BAD_REQUEST),
    PRODUCT_INVALID_STOCK(2004, "Stock quantity must be non-negative", HttpStatus.BAD_REQUEST),
    PRODUCT_CATEGORY_NOT_FOUND(2005, "Category associated with the product not found", HttpStatus.NOT_FOUND),
    PRODUCT_UNAUTHORIZED(2006, "You do not have permission to modify this product", HttpStatus.FORBIDDEN),
    PRODUCT_INVALID_ID(2007, "Product ID cannot be null", HttpStatus.BAD_REQUEST),
    PRODUCT_INVALID_QUANTITY(2008, "Quantity must be at least {min}", HttpStatus.BAD_REQUEST),
    PRODUCT_INVALID_NAME(2009, "Product ID cannot be empty", HttpStatus.BAD_REQUEST),

    // Category Errors
    CATEGORY_NOT_FOUND(3001, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(3002, "Category already exists", HttpStatus.BAD_REQUEST),
    CATEGORY_DELETE_FAILED(3003, "Cannot delete category with existing products", HttpStatus.BAD_REQUEST),
    CATEGORY_INVALID_NAME(3004, "Category not blank", HttpStatus.BAD_REQUEST),
    CATEGORY_INVALID_ID(3004, "Id not null", HttpStatus.BAD_REQUEST),

    // Cart Errors
    CART_NOT_FOUND(6001, "Cart not found", HttpStatus.NOT_FOUND),
    CART_EMPTY(6002, "Cart is empty", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(6003, "Cart item not found", HttpStatus.NOT_FOUND),
    CART_ITEM_ALREADY_EXISTS(6004, "Item already exists in cart", HttpStatus.BAD_REQUEST),
    CART_INVALID_QUANTITY(6005, "Invalid quantity for cart item", HttpStatus.BAD_REQUEST),
    CART_PRODUCT_OUT_OF_STOCK(6006, "Product is out of stock", HttpStatus.BAD_REQUEST),
    CART_UNAUTHORIZED_ACCESS(6007, "You do not have permission to modify this cart", HttpStatus.FORBIDDEN),

    // Order Errors (Dự phòng nếu có Order Service)
    ORDER_NOT_FOUND(4001, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_INVALID_STATUS(4002, "Invalid order status", HttpStatus.BAD_REQUEST),
    ORDER_PAYMENT_FAILED(4003, "Order payment failed", HttpStatus.BAD_REQUEST),

    // Discount Errors (Dự phòng nếu có Discount Service)
    DISCOUNT_NOT_FOUND(5001, "Discount code not found", HttpStatus.NOT_FOUND),
    DISCOUNT_EXPIRED(5002, "Discount code has expired", HttpStatus.BAD_REQUEST),
    DISCOUNT_INVALID(5003, "Invalid discount code", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
