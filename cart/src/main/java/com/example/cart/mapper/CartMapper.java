package com.example.cart.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.cart.dto.response.CartItemResponse;
import com.example.cart.dto.response.CartResponse;
import com.example.cart.entity.Cart;
import com.example.cart.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "id", target = "cartId")
    @Mapping(source = "buyerId", target = "buyerId")
    @Mapping(source = "items", target = "items")
    CartResponse toCartResponse(Cart cart);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "createdAt", target = "addedAt")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);
}
