package com.example.identity.controller;

import static org.mockito.ArgumentMatchers.any;

import com.example.identity.dto.request.AuthenticationRequest;
import com.example.identity.dto.request.IntrospectRequest;
import com.example.identity.dto.request.LogoutRequest;
import com.example.identity.dto.request.RefreshRequest;
import com.example.identity.dto.response.AuthenticationResponse;
import com.example.identity.dto.response.IntrospectResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.identity.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    private ObjectMapper objectMapper;
    private AuthenticationRequest authRequest;
    private AuthenticationResponse authResponse;
    private IntrospectRequest introspectRequest;
    private IntrospectResponse introspectResponse;
    private RefreshRequest refreshRequest;
    private LogoutRequest logoutRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        authRequest = new AuthenticationRequest("testuser", "password123");
        authResponse = new AuthenticationResponse("dummyToken", true);
        introspectRequest = new IntrospectRequest("dummyToken");
        introspectResponse = new IntrospectResponse(true);
        refreshRequest = new RefreshRequest("dummyRefreshToken");
        logoutRequest = new LogoutRequest("dummyToken");
    }

    @Test
    void authenticate_success() throws Exception {
        Mockito.when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.token").value("dummyToken"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.result.authenticated").value(true));
    }

    @Test
    void introspect_success() throws Exception {
        Mockito.when(authenticationService.introspect(any(IntrospectRequest.class)))
                .thenReturn(introspectResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(introspectRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.valid").value(true));
    }

    @Test
    void refreshToken_success() throws Exception {
        Mockito.when(authenticationService.refreshToken(any(RefreshRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.token").value("dummyToken"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.result.authenticated").value(true));
    }

    @Test
    void logout_success() throws Exception {
        Mockito.doNothing().when(authenticationService).logout(any(LogoutRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
