package com.example.identity.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.identity.dto.request.PermissionRequest;
import com.example.identity.dto.request.RoleRequest;
import com.example.identity.dto.response.PermissionResponse;
import com.example.identity.dto.response.RoleResponse;
import com.example.identity.entity.Permission;
import com.example.identity.entity.Role;
import com.example.identity.mapper.PermissionMapper;
import com.example.identity.mapper.RoleMapper;
import com.example.identity.repository.PermissionRepository;
import com.example.identity.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class RolePermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private PermissionService permissionService;

    @InjectMocks
    private RoleService roleService;

    private Permission permission;
    private Role role;

    @BeforeEach
    void setup() {
        permission = Permission.builder()
                .name("READ_PRIVILEGES")
                .description("Allows reading access")
                .build();

        role = Role.builder()
                .name("ADMIN")
                .description("Admin role")
                .permissions(Set.of(permission))
                .build();
    }

    @Test
    void createPermission_success() {
        PermissionRequest request = new PermissionRequest("WRITE_PRIVILEGES", "Allows writing access");
        Permission savedPermission = Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        when(permissionMapper.toPermission(request)).thenReturn(savedPermission);
        when(permissionRepository.save(any(Permission.class))).thenReturn(savedPermission);
        when(permissionMapper.toPermissionResponse(any(Permission.class)))
                .thenReturn(new PermissionResponse(savedPermission.getName(), savedPermission.getDescription()));

        PermissionResponse response = permissionService.create(request);

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getDescription(), response.getDescription());
    }

    @Test
    void getAllPermissions_success() {
        when(permissionRepository.findAll()).thenReturn(List.of(permission));
        when(permissionMapper.toPermissionResponse(permission))
                .thenReturn(new PermissionResponse(permission.getName(), permission.getDescription()));

        List<PermissionResponse> responses = permissionService.getAll();
        assertEquals(1, responses.size());
    }

    @Test
    void deletePermission_success() {
        doNothing().when(permissionRepository).deleteById(anyString());
        permissionService.delete("READ_PRIVILEGES");
        verify(permissionRepository, times(1)).deleteById("READ_PRIVILEGES");
    }

    @Test
    void createRole_success() {
        RoleRequest request = new RoleRequest("MANAGER", "Manager role", Set.of("READ_PRIVILEGES"));
        Role savedRole = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(Set.of(permission))
                .build();

        when(roleMapper.toRole(request)).thenReturn(savedRole);
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);
        when(roleMapper.toRoleResponse(any(Role.class)))
                .thenReturn(new RoleResponse(savedRole.getName(), savedRole.getDescription(), Set.of()));

        RoleResponse response = roleService.create(request);

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getDescription(), response.getDescription());
    }

    @Test
    void getAllRoles_success() {
        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.toRoleResponse(role))
                .thenReturn(new RoleResponse(role.getName(), role.getDescription(), Set.of()));

        List<RoleResponse> responses = roleService.getAll();
        assertEquals(1, responses.size());
    }

    @Test
    void deleteRole_success() {
        doNothing().when(roleRepository).deleteById(anyString());
        roleService.delete("ADMIN");
        verify(roleRepository, times(1)).deleteById("ADMIN");
    }
}
