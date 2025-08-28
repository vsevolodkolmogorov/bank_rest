package com.example.bankcards.controller;

import com.example.bankcards.config.TestSecurityConfig;
import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.dto.card.CardSearchCriteriaDto;
import com.example.bankcards.dto.user.UpdateUserRoleDto;
import com.example.bankcards.dto.user.UserRequestDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.entity.enums.UserRoleCode;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .email("user123@gmail.com")
                .roleName("ADMIN")
                .isEnabled(true)
                .isNonLocked(true)
                .cards(Collections.emptyList())
                .build();
    }

    @Test
    void getUser_ReturnsUser() throws Exception {
        when(userService.getById(1L)).thenReturn(userResponseDto);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user123@gmail.com"));
    }

    @Test
    void createUser_ReturnsCreatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("user123@gmail.com");
        requestDto.setPassword("Passw0rd*");
        requestDto.setRole(UserRoleCode.ADMIN);

        when(userService.create(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user123@gmail.com"));
    }

    @Test
    void updateRole_ReturnsUpdatedUser() throws Exception {
        UpdateUserRoleDto dto = new UpdateUserRoleDto();
        dto.setRole(UserRoleCode.USER);

        when(userService.updateRole(1L, dto)).thenReturn(userResponseDto);

        mockMvc.perform(patch("/api/user/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user123@gmail.com"));
    }

    @Test
    void toggleLockUser_ReturnsUpdatedUser() throws Exception {
        when(userService.toggleLockUser(1L)).thenReturn(userResponseDto);

        mockMvc.perform(patch("/api/user/1/toggleLock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nonLocked").value(true));
    }

    @Test
    void toggleDisableUser_ReturnsUpdatedUser() throws Exception {
        when(userService.toggleDisableUser(1L)).thenReturn(userResponseDto);

        mockMvc.perform(patch("/api/user/1/toggleEnable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void deleteUser_CallsService() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }
}
