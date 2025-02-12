package com.example.identity.controller;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

import com.example.identity.dto.request.UserCreationRequest;
import com.example.identity.dto.request.UserUpdateRequest;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.exception.AppException;
import com.example.identity.exception.ErrorCode;
import com.example.identity.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1990, 1, 1);

        userCreationRequest = UserCreationRequest.builder()
                .username("tducanh")
                .firstName("Duc")
                .lastName("Anh")
                .password("12345678")
                .email("tducanh263@gmail")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("cf0600f538b3")
                .username("tducanh")
                .firstName("Duc")
                .lastName("Anh")
                .email("tducanh263@gmail")
                .dob(dob)
                .build();
    }

    //
    //  createUser
    //
    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("cf0600f538b3"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("tducanh"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.firstName").value("Duc"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.lastName").value("Anh"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.email").value("tducanh263@gmail"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.dob").value(dob.toString()));
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        // GIVEN
        userCreationRequest.setUsername("joh");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 4 characters"));
    }

    @Test
    void createUser_passwordInvalid_fail() throws Exception {
        // GIVEN
        userCreationRequest.setPassword("123");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1004))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Password must be at least 6 characters"));
    }

    @Test
    void createUser_emailInvalid_fail() throws Exception {
        // GIVEN
        userCreationRequest.setEmail("invalidEmail");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1010))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Invalid email format"));
    }

    @Test
    void createUser_dobInvalid_fail() throws Exception {
        // GIVEN
        dob = LocalDate.of(2030, 1, 1);
        userCreationRequest.setDob(dob);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Your age must be at least 10"));
    }

    @Test
    void createUser_dobIsNull_fail() throws Exception {
        // GIVEN
        dob = null;
        userCreationRequest.setDob(dob);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000));
    }

    //
    // getUsers
    //
    @Test
    @WithMockUser(roles = {"ADMIN", "BUYER_ROLE", "SELLER_ROLE"})
    void getUsers_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LocalDate dob = LocalDate.of(1995, 5, 15);
        List<UserResponse> users = List.of(UserResponse.builder()
                .id("cf0600f538b3")
                .username("tducanh")
                .firstName("Duc")
                .lastName("Anh")
                .email("tducanh263@gmail")
                .dob(dob)
                .build());

        Mockito.when(userService.getUsers()).thenReturn(users);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/users").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].id").value("cf0600f538b3"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.result[0].username").value("tducanh"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.result[0].firstName").value("Duc"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.result[0].lastName").value("Anh"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].email").value("tducanh263@gmail"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].dob").value(dob.toString()));
    }
    //
    // getUser
    //
    @Test
    @WithMockUser(roles = {"ADMIN", "BUYER_ROLE", "SELLER_ROLE"})
    void getUser_validRequest_success() throws Exception {
        // GIVEN
        String userId = "cf0600f538b3";

        Mockito.when(userService.getUser(userId)).thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + userId).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value("cf0600f538b3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("tducanh"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.firstName").value("Duc"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.lastName").value("Anh"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.email").value("tducanh263@gmail"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.dob").value(dob.toString()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "BUYER_ROLE", "SELLER_ROLE"})
    void getUser_userNotFound_fail() throws Exception {
        // GIVEN
        String userId = "unknownId";

        Mockito.when(userService.getUser(userId)).thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + userId).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1005))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("User does not exist"));
    }

    //
    // getMyInfo
    //
    @Test
    @WithMockUser(
            username = "tducanh",
            roles = {"BUYER", "SELLER"})
    void getMyInfo_validRequest_success() throws Exception {
        // GIVEN
        Mockito.when(userService.getMyInfo()).thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/users/my-info").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value("cf0600f538b3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("tducanh"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.firstName").value("Duc"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.lastName").value("Anh"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.email").value("tducanh263@gmail"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.dob").value(dob.toString()));
    }

    //
    // deleteUser
    //
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteUser_success() throws Exception {
        // GIVEN
        String userId = "cf0600f538b3";
        Mockito.doNothing().when(userService).deleteUser(userId);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + userId).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteUser_userNotFound_fail() throws Exception {
        // GIVEN
        String userId = "unknownId";
        Mockito.doThrow(new AppException(ErrorCode.USER_NOT_EXISTED))
                .when(userService)
                .deleteUser(userId);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + userId).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1005))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("User does not exist"));
    }
    //
    // updateUser
    //
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUser_validRequest_success() throws Exception {
        // GIVEN
        String userId = "cf0600f538b3";
        UserUpdateRequest updateRequest =
                new UserUpdateRequest("newPassword", "Duc", "Anh", LocalDate.of(1995, 5, 15), List.of("ROLE_ADMIN"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        Mockito.when(userService.updateUser(userId, updateRequest)).thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value("cf0600f538b3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("tducanh"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUser_dobInvalid_fail() throws Exception {
        // GIVEN
        String userId = "cf0600f538b3";
        UserUpdateRequest updateRequest =
                new UserUpdateRequest("abcdef", "Duc", "Anh", LocalDate.of(2030, 1, 1), List.of("ROLE_ADMIN"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Your age must be at least 10"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUser_dobIsNull_fail() throws Exception {
        // GIVEN
        String userId = "cf0600f538b3";
        UserUpdateRequest updateRequest =
                new UserUpdateRequest("newPassword", "Duc", "Anh", null, List.of("ROLE_ADMIN"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUser_passwordInvalid_fail() throws Exception {
        // GIVEN
        String userId = "cf0600f538b3";
        UserUpdateRequest updateRequest =
                new UserUpdateRequest("", "Duc", "Anh", LocalDate.of(1995, 5, 15), List.of("ROLE_ADMIN"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1004))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Password must be at least 6 characters"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateUser_userNotFound_fail() throws Exception {
        // GIVEN
        String userId = "unknownId";
        UserUpdateRequest updateRequest =
                new UserUpdateRequest("newPassword", "Duc", "Anh", LocalDate.of(1990, 1, 1), List.of("ROLE_ADMIN"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        Mockito.when(userService.updateUser(userId, updateRequest))
                .thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1005))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("User does not exist"));
    }
}
