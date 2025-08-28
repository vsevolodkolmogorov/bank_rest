package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.card.CardRequestDto;
import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.MaskCardNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardMapper {

    private final MaskCardNumber maskCardNumber;

    public Card toEntity(CardRequestDto dto, User user, CardStatus status) {
        Card card = Card.builder()
                .balance(dto.getBalance())
                .expiryDate(dto.getExpiryDate())
                .user(user)
                .status(status)
                .build();

        log.info("Mapped Card entity: userId={}, balance={}, expiryDate={}, status={}",
                user.getId(), dto.getBalance(), dto.getExpiryDate(), status.getCode());
        return card;
    }

    public CardResponseDto toResponseDto(Card card) {
        CardResponseDto dto = CardResponseDto.builder()
                .id(card.getId())
                .balance(card.getBalance())
                .expiryDate(card.getExpiryDate())
                .maskedCardNumber(maskCardNumber.makeMask(card.getLastFourDigits()))
                .ownerEmail(card.getUser().getEmail())
                .statusName(card.getStatus().getCode().name())
                .build();

        log.info("Mapped CardResponseDto: id={}, ownerLogin={}, maskedCardNumber={}, status={}",
                dto.getId(), dto.getOwnerEmail(), dto.getMaskedCardNumber(), dto.getStatusName());
        return dto;
    }
}

