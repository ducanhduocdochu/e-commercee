package com.example.identity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.identity.dto.request.ApiResponse;
import com.example.identity.dto.request.RoleRequest;
import com.example.identity.dto.response.RoleResponse;
import com.example.identity.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Roles", description = "Quản lý Role")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @Operation(summary = "Tạo role mới", description = "Tạo một role mới trong hệ thống")
    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách role", description = "Trả về danh sách các role hiện có")
    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @Operation(summary = "Xóa role", description = "Xóa một role dựa trên tên role")
    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }
}
