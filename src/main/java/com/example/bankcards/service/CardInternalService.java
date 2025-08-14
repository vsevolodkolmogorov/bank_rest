package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.dto.card.UpdateStatusDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;

public interface CardInternalService {
    Card findCardById(Long id);
    CardResponseDto updateStatus(Long id, UpdateStatusDto dto);
    void validateUserOwnsCard(Card card, User user);
    boolean existsByCardNumberEncrypted(String encryptedCardNumber);
}
