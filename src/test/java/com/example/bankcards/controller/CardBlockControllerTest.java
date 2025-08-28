package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.cardBlock.CardBlockRequestDto;
import com.example.bankcards.dto.cardBlock.CardBlockResponseDto;
import com.example.bankcards.entity.enums.RequestStatusCode;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.service.CardBlockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CardBlockController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class CardBlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardBlockService cardBlockService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private CardBlockResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new CardBlockResponseDto();
        responseDto.setId(1L);
        responseDto.setCardId(123L);
        responseDto.setStatus(RequestStatusCode.PENDING);
    }

    @Test
    @DisplayName("GET /api/cardBlock/{id} — успешно")
    @WithMockUser
    void getCardBlock_success() throws Exception {
        Mockito.when(cardBlockService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/cardBlock/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cardId").value(123L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/cardBlock/{id}/my — успешно")
    @WithMockUser(username = "testUser")
    void getUserCardBlock_success() throws Exception {
        Mockito.when(cardBlockService.getUserRequestById("testUser", 1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/cardBlock/{id}/my", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /api/cardBlock/my — успешно")
    @WithMockUser(username = "testUser")
    void getUserCardBlocks_success() throws Exception {
        Mockito.when(cardBlockService.getAllRequestByUserEmail(eq("testUser@example.com"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(responseDto)));

        mockMvc.perform(get("/api/cardBlock/my")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("GET /api/cardBlock — успешно")
    @WithMockUser
    void getAllCardBlocks_success() throws Exception {
        Mockito.when(cardBlockService.getAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(responseDto)));

        mockMvc.perform(get("/api/cardBlock")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("POST /api/cardBlock — успешно")
    @WithMockUser(username = "testUser")
    void createCardBlock_success() throws Exception {
        CardBlockRequestDto requestDto = new CardBlockRequestDto();
        requestDto.setCardId(123L);

        Mockito.when(cardBlockService.create(eq("testUser@example.com"), any(CardBlockRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/cardBlock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PATCH /api/cardBlock/{id}/approve — успешно")
    @WithMockUser
    void approveRequest_success() throws Exception {
        Mockito.when(cardBlockService.approveRequest(1L, "Approved")).thenReturn(responseDto);

        mockMvc.perform(patch("/api/cardBlock/{id}/approve", 1L)
                        .param("comment", "Approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PATCH /api/cardBlock/{id}/reject — успешно")
    @WithMockUser
    void rejectRequest_success() throws Exception {
        Mockito.when(cardBlockService.rejectRequest(1L, "Rejected")).thenReturn(responseDto);

        mockMvc.perform(patch("/api/cardBlock/{id}/reject", 1L)
                        .param("comment", "Rejected"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("DELETE /api/cardBlock/{id} — успешно")
    @WithMockUser
    void deleteCardBlock_success() throws Exception {
        mockMvc.perform(delete("/api/cardBlock/{id}", 1L))
                .andExpect(status().isOk());
    }
}
