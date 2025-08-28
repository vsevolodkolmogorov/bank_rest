package com.example.bankcards.service;

import com.example.bankcards.dto.card.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CardService {
    CardResponseDto create(CardRequestDto cardRequestDto);
    Page<CardResponseDto> getAll(CardSearchCriteriaDto criteria, Pageable pageable);
    Page<CardResponseDto> getAllCardsByUserEmail(String email, CardSearchCriteriaDto criteria, Pageable pageable);
    CardResponseDto getById(Long id);
    CardResponseDto getUserCardById(String email, Long id);
    CardResponseDto updateExpiryDate(Long id, UpdateExpiryDateDto dto);
    CardResponseDto updateStatus(Long id, UpdateStatusDto dto);
    void transferBetweenCards(String email, CardTransferRequestDto dto);
    void delete(Long id);
}
