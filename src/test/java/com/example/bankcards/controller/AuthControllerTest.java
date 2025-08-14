package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthRequestDto;
import com.example.bankcards.dto.auth.AuthResponseDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    private UserResponseDto user;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        user = UserResponseDto.builder()
                .id(1L)
                .roleName("USER")
                .login("testUser")
                .isEnabled(true)
                .isNonLocked(true)
                .build();
    }

    @Test
    void testRegister() throws Exception {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setLogin("testUser");
        requestDto.setPassword("Passw0rd*");

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setUser(user);
        responseDto.setToken("jwt-token");

        when(authService.register(any(AuthRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.login").value("testUser"))
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(authService, times(1)).register(any(AuthRequestDto.class));
    }

    @Test
    void testLogin() throws Exception {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setLogin("testUser");
        requestDto.setPassword("Passw0rd*");

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setUser(user);
        responseDto.setToken("jwt-token");

        when(authService.login(any(AuthRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.login").value("testUser"))
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(authService, times(1)).login(any(AuthRequestDto.class));
    }

    @Test
    void testGetCurrentUser() throws Exception {
        String token = "Bearer jwt-token";
        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setUser(user);
        responseDto.setToken("jwt-token");

        when(authService.getCurrentUser("jwt-token")).thenReturn(responseDto);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.login").value("testUser"))
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(authService, times(1)).getCurrentUser("jwt-token");
    }
}
