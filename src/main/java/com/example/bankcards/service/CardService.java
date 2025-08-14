package com.example.bankcards.service;

import com.example.bankcards.dto.card.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CardService {
    CardResponseDto create(CardRequestDto cardRequestDto);
    Page<CardResponseDto> getAll(CardSearchCriteriaDto criteria, Pageable pageable);
    Page<CardResponseDto> getAllCardsByUserLogin(String login, CardSearchCriteriaDto criteria, Pageable pageable);
    CardResponseDto getById(Long id);
    CardResponseDto getUserCardById(String login, Long id);
    CardResponseDto updateExpiryDate(Long id, UpdateExpiryDateDto dto);
    CardResponseDto updateStatus(Long id, UpdateStatusDto dto);
    void transferBetweenCards(String login, CardTransferRequestDto dto);
    void delete(Long id);
}
