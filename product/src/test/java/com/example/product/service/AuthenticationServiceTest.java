package com.example.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.product.dto.request.*;
import com.example.product.dto.response.*;
import com.example.product.exception.AppException;
import com.example.product.exception.ErrorCode;
import com.example.product.repository.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private static final String TEST_SIGNER_KEY = "1TjXchw5FloESb63Kc+DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij";
    private static final String INVALID_SIGNER_KEY = "2XjYdkw6FloGSd74Kc+EFhUARwpXL5kVGExfHWyvG6TIf/2z/MkKyJmOqaF7B/kl";

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(authenticationService, "SIGNER_KEY", TEST_SIGNER_KEY);
        ReflectionTestUtils.setField(authenticationService, "VALID_DURATION", 3600L);
        ReflectionTestUtils.setField(authenticationService, "REFRESHABLE_DURATION", 7200L);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user = User.builder()
                .username("john")
                .password(passwordEncoder.encode("password"))
                .build();
    }

    private String generateTestToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("example.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", "ROLE_USER")
                .build();

        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(new MACSigner(INVALID_SIGNER_KEY.getBytes()));

        return signedJWT.serialize();
    }

    private String generateInvalidTestToken() throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("invalidUser")
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", "ROLE_USER")
                .build();

        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(new MACSigner(INVALID_SIGNER_KEY.getBytes())); // Sử dụng khóa sai

        return signedJWT.serialize();
    }

    private String generateMockedToken() throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("mockedUser")
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", "ROLE_USER")
                .build();

        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(new MACSigner(TEST_SIGNER_KEY.getBytes()));

        return signedJWT.serialize();
    }

    private SignedJWT generateTestSignedJWT(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        String jwtID = UUID.randomUUID().toString();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 7200000)) // Phù hợp với REFRESHABLE_DURATION
                .jwtID(jwtID)
                .claim("scope", "ROLE_USER")
                .build();

        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(new MACSigner(TEST_SIGNER_KEY.getBytes()));
        return signedJWT;
    }

    @Test
    void refreshToken_success() throws Exception {
        SignedJWT signedJWT = generateTestSignedJWT(user);
        String validToken = signedJWT.serialize();
        RefreshRequest request = new RefreshRequest(validToken);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(invalidatedTokenRepository.existsById(anyString())).thenReturn(false); // Token chưa bị vô hiệu hóa

        // Sử dụng Reflection để gọi phương thức private verifyToken
        Method verifyTokenMethod =
                AuthenticationService.class.getDeclaredMethod("verifyToken", String.class, boolean.class);
        verifyTokenMethod.setAccessible(true);
        SignedJWT verifiedToken = (SignedJWT) verifyTokenMethod.invoke(authenticationService, validToken, true);

        assertNotNull(verifiedToken);
        AuthenticationResponse response = authenticationService.refreshToken(request);

        assertNotNull(response.getToken());
        assertTrue(response.isAuthenticated());
    }

    @Test
    void authenticate_success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        AuthenticationRequest request = new AuthenticationRequest("john", "password");
        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response.getToken());
        assertTrue(response.isAuthenticated());
    }

    @Test
    void authenticate_invalidPassword_fail() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        AuthenticationRequest request = new AuthenticationRequest("john", "wrongpassword");
        assertThrows(AppException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void introspect_validToken_success() throws Exception {
        SignedJWT signedJWT = generateTestSignedJWT(user);
        String validToken = signedJWT.serialize();
        IntrospectRequest request = new IntrospectRequest(validToken);

        // Kiểm tra token hợp lệ
        IntrospectResponse response = authenticationService.introspect(request);
        assertTrue(response.isValid());
    }

    @Test
    void introspect_invalidToken_fail() throws Exception {
        String invalidToken = "invalid.token.value";
        IntrospectRequest request = new IntrospectRequest(invalidToken);

        boolean isValid = true;
        try {
            // Sử dụng Reflection để gọi phương thức private
            Method method = AuthenticationService.class.getDeclaredMethod("verifyToken", String.class, boolean.class);
            method.setAccessible(true);
            method.invoke(authenticationService, invalidToken, false);
        } catch (Exception e) {
            isValid = false;
        }

        assertFalse(isValid, "Expected introspection to fail but it returned valid=true");
    }

    @Test
    void logout_success() throws ParseException, JOSEException {
        String mockedToken = generateMockedToken();
        when(invalidatedTokenRepository.save(any(InvalidatedToken.class)))
                .thenReturn(new InvalidatedToken("123", new Date()));
        LogoutRequest request = new LogoutRequest(mockedToken);
        authenticationService.logout(request);
        verify(invalidatedTokenRepository, times(1)).save(any(InvalidatedToken.class));
    }

    @Test
    void authenticate_userNotFound_throwsException() {
        AuthenticationRequest request = new AuthenticationRequest("nonexistent", "password");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        AppException thrown = assertThrows(AppException.class, () -> authenticationService.authenticate(request));
        assertEquals(ErrorCode.USER_NOT_EXISTED, thrown.getErrorCode());
    }

    @Test
    void logout_success_tokenExpired_noExceptionThrown() throws ParseException, JOSEException {
        SignedJWT signedJWT = generateTestSignedJWT(user);
        String mockedToken = signedJWT.serialize();
        when(invalidatedTokenRepository.save(any(InvalidatedToken.class)))
                .thenReturn(new InvalidatedToken("123", new Date()));
        LogoutRequest request = new LogoutRequest(mockedToken);
        assertDoesNotThrow(() -> authenticationService.logout(request));
    }

    @Test
    void generateToken_success() throws Exception {
        Method generateTokenMethod = AuthenticationService.class.getDeclaredMethod("generateToken", User.class);
        generateTokenMethod.setAccessible(true);

        String token = (String) generateTokenMethod.invoke(authenticationService, user);

        assertNotNull(token);
    }

    @Test
    void buildScope_withRolesAndPermissions() throws Exception {
        Role role = new Role();
        role.setName("ADMIN");

        Permission permission = new Permission();
        permission.setName("READ_PRIVILEGES");

        role.setPermissions(Set.of(permission)); // Sửa thành Set

        user.setRoles(Set.of(role)); // Sửa thành Set

        Method buildScopeMethod = AuthenticationService.class.getDeclaredMethod("buildScope", User.class);
        buildScopeMethod.setAccessible(true);

        String scope = (String) buildScopeMethod.invoke(authenticationService, user);

        assertTrue(scope.contains("ROLE_ADMIN"));
        assertTrue(scope.contains("READ_PRIVILEGES"));
    }

    @Test
    void verifyToken_validToken_success() throws Exception {
        SignedJWT signedJWT = generateTestSignedJWT(user); // Tạo token hợp lệ
        String token = signedJWT.serialize();

        Method verifyTokenMethod =
                AuthenticationService.class.getDeclaredMethod("verifyToken", String.class, boolean.class);
        verifyTokenMethod.setAccessible(true);

        SignedJWT result = (SignedJWT) verifyTokenMethod.invoke(authenticationService, token, false);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getJWTClaimsSet().getSubject());
    }

    @Test
    void verifyToken_invalidToken_throwsException() throws Exception {
        String invalidToken = "invalid.token.value";

        Method verifyTokenMethod =
                AuthenticationService.class.getDeclaredMethod("verifyToken", String.class, boolean.class);
        verifyTokenMethod.setAccessible(true);

        Exception thrown = assertThrows(Exception.class, () -> {
            verifyTokenMethod.invoke(authenticationService, invalidToken, false);
        });

        // Kiểm tra nếu nguyên nhân gốc của lỗi là ParseException hoặc AppException
        Throwable cause = thrown.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof ParseException || cause instanceof AppException);
    }
}
