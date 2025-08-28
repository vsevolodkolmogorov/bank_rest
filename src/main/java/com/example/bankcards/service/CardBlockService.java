package com.example.bankcards.service;

import com.example.bankcards.dto.cardBlock.CardBlockRequestDto;
import com.example.bankcards.dto.cardBlock.CardBlockResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardBlockService {
    CardBlockResponseDto create(String email, CardBlockRequestDto cardBlockRequestDto);
    Page<CardBlockResponseDto> getAll(Pageable pageable);
    Page<CardBlockResponseDto> getAllRequestByUserEmail(String email, Pageable pageable);
    CardBlockResponseDto getById(Long id);
    CardBlockResponseDto getUserRequestById(String email, Long id);
    CardBlockResponseDto approveRequest(Long id, String comment);
    CardBlockResponseDto rejectRequest(Long id, String comment);
    void delete(Long id);
}
