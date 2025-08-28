package com.example.bankcards.controller;

import com.example.bankcards.config.TestSecurityConfig;
import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.enums.CardStatusCode;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CardController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
@Import(TestSecurityConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    private CardResponseDto getSampleResponse() {
        CardResponseDto dto = new CardResponseDto();
        dto.setId(1L);
        dto.setMaskedCardNumber("**** **** **** 1234");
        dto.setOwnerEmail("user@example.com");
        dto.setExpiryDate(YearMonth.of(2025, 8));
        dto.setStatusName(CardStatusCode.ACTIVE.name());
        dto.setBalance(BigDecimal.valueOf(1500.75));
        return dto;
    }

    @Test
    void createCard_success() throws Exception {
        CardRequestDto request = new CardRequestDto();
        request.setUserId(1L);
        request.setExpiryDate(YearMonth.of(2025, 8));
        request.setStatus(CardStatusCode.ACTIVE);
        request.setBalance(BigDecimal.valueOf(1000));

        CardResponseDto response = getSampleResponse();

        Mockito.when(cardService.create(any(CardRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerEmail", is("user@example.com")))
                .andExpect(jsonPath("$.balance", is(1500.75)));
    }

    @Test
    void getCardById_success() throws Exception {
        Mockito.when(cardService.getById(1L))
                .thenReturn(getSampleResponse());

        mockMvc.perform(get("/api/card/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maskedCardNumber", is("**** **** **** 1234")));
    }

    @Test
    void searchCards_success() throws Exception {
        CardResponseDto dto = getSampleResponse();

        Page<CardResponseDto> pageResult = new PageImpl<>(
                List.of(dto),
                PageRequest.of(0, 10),
                1
        );

        Mockito.when(cardService.getAll(
                any(CardSearchCriteriaDto.class),
                any(Pageable.class))
        ).thenReturn(pageResult);

        mockMvc.perform(get("/api/card")
                        .param("lastFourDigits", "1234")
                        .param("status", "ACTIVE")
                        .param("minBalance", "100")
                        .param("maxBalance", "2000")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].statusName", is("ACTIVE")))
                .andExpect(jsonPath("$.content[0].maskedCardNumber", is("**** **** **** 1234")));
    }

    @Test
    void updateStatus_success() throws Exception {
        UpdateStatusDto request = new UpdateStatusDto();
        request.setStatus(CardStatusCode.BLOCKED);

        Mockito.when(cardService.updateStatus(eq(1L), any(UpdateStatusDto.class)))
                .thenReturn(getSampleResponse());

        mockMvc.perform(patch("/api/card/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName", is("ACTIVE")));
    }

    @Test
    void updateExpiryDate_success() throws Exception {
        UpdateExpiryDateDto request = new UpdateExpiryDateDto();
        request.setExpiryDate(YearMonth.of(2025, 8));

        Mockito.when(cardService.updateExpiryDate(eq(1L), any(UpdateExpiryDateDto.class)))
                .thenReturn(getSampleResponse());

        mockMvc.perform(patch("/api/card/{id}/expiry", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expiryDate", is("2025-08")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void transfer_success() throws Exception {
        CardTransferRequestDto request = new CardTransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(500));

        Mockito.doNothing().when(cardService)
                .transferBetweenCards(eq("testuser"), any(CardTransferRequestDto.class));

        mockMvc.perform(post("/api/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer completed successfully"));
    }


    @Test
    void deleteCard_success() throws Exception {
        Mockito.doNothing().when(cardService).delete(1L);

        mockMvc.perform(delete("/api/card/{id}", 1L))
                .andExpect(status().isOk());
    }
}
