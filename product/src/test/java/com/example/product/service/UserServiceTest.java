package com.example.product.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import jakarta.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import com.example.product.entity.Role;
import com.example.product.entity.User;
import com.example.product.exception.AppException;
import com.example.product.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
@TestPropertySource("/test.properties")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;
    private User user;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1990, 1, 1);

        request = UserCreationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .email("doejohn@gmail.com")
                .password("12345678")
                .dob(dob)
                .build();

        updateRequest = UserUpdateRequest.builder()
                .password("newpassword")
                .firstName("Updated")
                .lastName("User")
                .dob(LocalDate.of(1995, 5, 15))
                .roles(List.of("ROLE_ADMIN"))
                .build();

        userResponse = UserResponse.builder()
                .id("cf0600f538b3")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .email("doejohn@gmail.com")
                .dob(dob)
                .build();

        user = User.builder()
                .id("cf0600f538b3")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .email("doejohn@gmail.com")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userMapper.toUser(any(UserCreationRequest.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        var response = userService.createUser(request);

        Assertions.assertThat(response.getId()).isEqualTo("cf0600f538b3");
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
    }

    @Test
    void createUser_userExisted_fail() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_EXISTED);
    }

    @Test
    void createUser_invalidEmail_fail() {
        request.setEmail("invalidEmail");
        when(userMapper.toUser(any())).thenThrow(new ConstraintViolationException("INVALID_EMAIL", null));
        var exception = assertThrows(ConstraintViolationException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getMessage()).contains("INVALID_EMAIL");
    }

    @Test
    void createUser_invalidPassword_fail() {
        request.setPassword("123");
        when(userMapper.toUser(any())).thenThrow(new ConstraintViolationException("INVALID_PASSWORD", null));
        var exception = assertThrows(ConstraintViolationException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getMessage()).contains("INVALID_PASSWORD");
    }

    @Test
    void createUser_invalidDob_fail() {
        request.setDob(LocalDate.of(2030, 1, 1));
        when(userMapper.toUser(any())).thenThrow(new ConstraintViolationException("INVALID_DOB", null));
        var exception = assertThrows(ConstraintViolationException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getMessage()).contains("INVALID_DOB");
    }

    @Test
    void createUser_emptyUsername_fail() {
        request.setUsername("");
        when(userMapper.toUser(any())).thenThrow(new ConstraintViolationException("USERNAME_INVALID", null));
        var exception = assertThrows(ConstraintViolationException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getMessage()).contains("USERNAME_INVALID");
    }

    @Test
    void getMyInfo_valid_success() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        var response = userService.getMyInfo();

        Assertions.assertThat(response.getUsername()).isEqualTo("john");
        Assertions.assertThat(response.getId()).isEqualTo("cf0600f538b3");
    }

    @Test
    void getMyInfo_userNotFound_error() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }

    @Test
    void deleteUser_success() {
        doNothing().when(userRepository).deleteById(anyString());
        Assertions.assertThatCode(() -> userService.deleteUser("cf0600f538b3")).doesNotThrowAnyException();
    }

    @Test
    void getUser_success() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);
        var response = userService.getUser("cf0600f538b3");
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
    }

    @Test
    void getUser_fail_notFound() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        var exception = assertThrows(AppException.class, () -> userService.getUser("cf0600f538b3"));
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }

    @Test
    void createUser_dataIntegrityViolation_fail() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userMapper.toUser(any(UserCreationRequest.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        var exception = assertThrows(AppException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_EXISTED);
    }

    @Test
    void updateUser_validRequest_success() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(roleRepository.findAllById(any())).thenReturn(List.of(new Role("ADMIN", "Admin Role", new HashSet<>())));
        doAnswer(invocation -> {
                    User updatedUser = invocation.getArgument(0);
                    updatedUser.setFirstName(updateRequest.getFirstName());
                    updatedUser.setLastName(updateRequest.getLastName());
                    updatedUser.setDob(updateRequest.getDob());
                    return updatedUser;
                })
                .when(userRepository)
                .save(any());
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        var response = userService.updateUser("cf0600f538b3", updateRequest);

        Assertions.assertThat(response.getUsername()).isEqualTo("john");
        Assertions.assertThat(response.getFirstName()).isEqualTo("John");
        Assertions.assertThat(response.getLastName()).isEqualTo("Doe");
        Assertions.assertThat(response.getDob()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void updateUser_userNotFound_fail() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> userService.updateUser("cf0600f538b3", updateRequest));
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }

    @Test
    void getUsers_validRequest_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        var response = userService.getUsers();

        Assertions.assertThat(response).isNotEmpty();
        Assertions.assertThat(response.get(0).getUsername()).isEqualTo("john");
    }
}
