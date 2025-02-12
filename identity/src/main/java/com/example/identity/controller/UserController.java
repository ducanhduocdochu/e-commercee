package com.example.identity.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.identity.dto.request.ApiResponse;
import com.example.identity.dto.request.UserCreationRequest;
import com.example.identity.dto.request.UserUpdateRequest;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "User API", description = "Quản lý người dùng") // Gắn tag cho nhóm API
public class UserController {
    UserService userService;

    @PostMapping
    @Operation(summary = "Tạo người dùng", description = "API để tạo mới một người dùng trong hệ thống.")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách người dùng",
            description = "API để lấy danh sách toàn bộ người dùng trong hệ thống.")
    ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Lấy thông tin người dùng",
            description = "API để lấy thông tin của một người dùng dựa vào ID.")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/my-info")
    @Operation(summary = "Lấy thông tin cá nhân", description = "API để lấy thông tin của người dùng hiện tại.")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Xóa người dùng", description = "API để xóa người dùng dựa vào ID.")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Cập nhật người dùng", description = "API để cập nhật thông tin người dùng dựa vào ID.")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }
}
