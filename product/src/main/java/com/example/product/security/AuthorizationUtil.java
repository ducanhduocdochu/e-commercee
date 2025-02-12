package com.example.product.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.product.exception.AppException;
import com.example.product.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizationUtil {

    /**
     * Kiểm tra xem người dùng có ít nhất một trong các quyền được chỉ định
     */
    public static void checkAuthorities(Set<String> requiredRoles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.error("❌ SecurityContext không có Authentication!");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<String> userAuthorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("🔍 Authorities từ SecurityContext: {}", userAuthorities);

        // ✅ Kiểm tra xem user có ít nhất 1 role trong danh sách được phép không
        boolean hasValidRole = userAuthorities.stream().anyMatch(requiredRoles::contains);

        if (!hasValidRole) {
            log.error("❌ Người dùng không có quyền nào trong danh sách: {}", requiredRoles);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
