package com.example.product.configuration;

import com.example.product.security.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomJwtAuthenticationConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        // ✅ Lấy userId từ `sub`
        String userId = jwt.getSubject();

        // ✅ Lấy role từ claim `scope` (VD: "ROLE_BUYER ROLE_SELLER")
        String rawRoles = jwt.getClaim("scope");

        // ✅ Chuyển đổi chuỗi scope thành danh sách GrantedAuthority **ĐÚNG CHUẨN**
        List<GrantedAuthority> authorities = Arrays.stream(rawRoles.split(" "))
                .filter(role -> role.startsWith("ROLE_")) // Chỉ lấy đúng quyền hợp lệ
                .map(role -> role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.info("✅ JWT converted: userId={}, roles={}", userId, authorities);

        // ✅ Tạo đối tượng AuthenticatedUser
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, rawRoles);

        // ✅ Trả về authentication token với danh sách authorities
        return new UsernamePasswordAuthenticationToken(authenticatedUser, jwt, authorities);
    }
}
