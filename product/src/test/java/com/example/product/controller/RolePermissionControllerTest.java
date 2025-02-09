package com.example.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.product.dto.request.*;
import com.example.product.dto.response.*;
import com.example.product.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class RolePermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private PermissionService permissionService;

    private ObjectMapper objectMapper;
    private RoleRequest roleRequest;
    private RoleResponse roleResponse;
    private PermissionRequest permissionRequest;
    private PermissionResponse permissionResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        roleRequest = new RoleRequest("ADMIN", "Administrator Role", Set.of("READ_PRIVILEGES"));
        roleResponse = new RoleResponse(
                "ADMIN", "Administrator Role", Set.of(new PermissionResponse("READ_PRIVILEGES", "Read Access")));
        permissionRequest = new PermissionRequest("WRITE_PRIVILEGES", "Write Access");
        permissionResponse = new PermissionResponse("WRITE_PRIVILEGES", "Write Access");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createRole_success() throws Exception {
        Mockito.when(roleService.create(any(RoleRequest.class))).thenReturn(roleResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.name").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllRoles_success() throws Exception {
        Mockito.when(roleService.getAll()).thenReturn(List.of(roleResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/roles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].name").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteRole_success() throws Exception {
        Mockito.doNothing().when(roleService).delete(eq("ADMIN"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/ADMIN").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPermission_success() throws Exception {
        Mockito.when(permissionService.create(any(PermissionRequest.class))).thenReturn(permissionResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.name").value("WRITE_PRIVILEGES"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllPermissions_success() throws Exception {
        Mockito.when(permissionService.getAll()).thenReturn(List.of(permissionResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/permissions").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].name").value("WRITE_PRIVILEGES"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deletePermission_success() throws Exception {
        Mockito.doNothing().when(permissionService).delete(eq("WRITE_PRIVILEGES"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/permissions/WRITE_PRIVILEGES")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
