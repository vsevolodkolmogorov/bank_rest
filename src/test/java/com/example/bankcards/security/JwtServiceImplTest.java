package com.example.bankcards.security;

import com.example.bankcards.exception.ParseTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private UserDetails userDetails;
    private String secret = "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIHNlY3JldCBmb3IgdGVzdGluZyE="; // Base64

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtServiceImpl();

        // Установим секрет через рефлексию
        Field secretField = JwtServiceImpl.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, secret);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void isTokenValid_returnsTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);
        boolean valid = jwtService.isTokenValid(token, userDetails);
        assertTrue(valid);
    }

    @Test
    void isTokenValid_returnsFalseForInvalidUsername() {
        String token = jwtService.generateToken(userDetails);
        UserDetails anotherUser = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("anotherUser");
        boolean valid = jwtService.isTokenValid(token, anotherUser);
        assertFalse(valid);
    }

    @Test
    void isTokenExpired_returnsFalseForFreshToken() {
        String token = jwtService.generateToken(userDetails);
        boolean expired = jwtService.isTokenExpired(token);
        assertFalse(expired);
    }

    @Test
    void extractUsername_throwsExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(ParseTokenException.class, () -> jwtService.extractUsername(invalidToken));
    }

    @Test
    void isTokenExpired_throwsExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(ParseTokenException.class, () -> jwtService.isTokenExpired(invalidToken));
    }
}
