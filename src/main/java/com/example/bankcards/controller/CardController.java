package com.example.bankcards.controller;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.enums.CardStatusCode;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
@Tag(name = "Управление картами", description = "API для управления картами пользователей")
@Slf4j
public class CardController {

    private final CardService cardService;

    @GetMapping("/{id}")
    public CardResponseDto getCard(@PathVariable Long id) {
        log.info("Get card by id {}", id);
        return cardService.getById(id);
    }

    @GetMapping("/{id}/my")
    public CardResponseDto getUserCard(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get user card {} for login {}", id, userDetails.getUsername());
        return cardService.getUserCardById(userDetails.getUsername(), id);
    }

    @GetMapping("/my")
    public Page<CardResponseDto> getUserCards(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String lastFourDigits,
            @RequestParam(required = false) CardStatusCode status,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance) {

        log.info("Get cards for user {} with filters lastFourDigits={}, status={}, minBalance={}, maxBalance={}, page={}, size={}",
                userDetails.getUsername(), lastFourDigits, status, minBalance, maxBalance, page, size);

        Pageable pageable = PageRequest.of(page, size);
        CardSearchCriteriaDto criteria = CardSearchCriteriaDto.builder()
                .lastFourDigits(lastFourDigits)
                .status(status)
                .minBalance(minBalance)
                .maxBalance(maxBalance)
                .build();
        return cardService.getAllCardsByUserLogin(userDetails.getUsername(), criteria, pageable);
    }

    @GetMapping
    public Page<CardResponseDto> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String lastFourDigits,
            @RequestParam(required = false) CardStatusCode status,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance) {

        log.info("Get all cards with filters lastFourDigits={}, status={}, minBalance={}, maxBalance={}, page={}, size={}",
                lastFourDigits, status, minBalance, maxBalance, page, size);

        Pageable pageable = PageRequest.of(page, size);
        CardSearchCriteriaDto criteria = CardSearchCriteriaDto.builder()
                .lastFourDigits(lastFourDigits)
                .status(status)
                .minBalance(minBalance)
                .maxBalance(maxBalance)
                .build();
        return cardService.getAll(criteria, pageable);
    }

    @PostMapping
    public CardResponseDto createCard(@Valid @RequestBody CardRequestDto cardDto) {
        log.info("Create card for userId={} with params {}", cardDto.getUserId(), cardDto);
        return cardService.create(cardDto);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CardTransferRequestDto dto) {
        log.info("Transfer attempt by user {}: {}", userDetails.getUsername(), dto);
        cardService.transferBetweenCards(userDetails.getUsername(), dto);
        return ResponseEntity.ok("Transfer completed successfully");
    }

    @PatchMapping("/{id}/expiry")
    public CardResponseDto updateExpiryDate(@PathVariable Long id, @Valid @RequestBody UpdateExpiryDateDto dto) {
        log.info("Update expiry date for card {} with data {}", id, dto);
        return cardService.updateExpiryDate(id, dto);
    }

    @PatchMapping("/{id}/status")
    public CardResponseDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusDto dto) {
        log.info("Update status for card {} with data {}", id, dto);
        return cardService.updateStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        log.info("Delete card with id {}", id);
        cardService.delete(id);
    }
}
