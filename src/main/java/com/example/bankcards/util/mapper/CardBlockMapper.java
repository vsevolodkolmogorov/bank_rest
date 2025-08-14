package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.cardBlock.CardBlockResponseDto;
import com.example.bankcards.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardBlockMapper {

    public CardBlock toEntity(LocalDateTime dateTime, User user, Card card, RequestStatus status) {
        CardBlock cardBlock = CardBlock.builder()
                .requestedBy(user)
                .card(card)
                .status(status)
                .adminComment(null)
                .requestDate(dateTime)
                .build();

        log.info("Mapped CardBlock entity: cardId={}, userId={}, status={}",
                card.getId(), user.getId(), status.getCode());
        return cardBlock;
    }

    public CardBlockResponseDto toResponseDto(CardBlock cardBlock) {
        CardBlockResponseDto dto = CardBlockResponseDto.builder()
                .id(cardBlock.getId())
                .adminComment(cardBlock.getAdminComment())
                .cardId(cardBlock.getCard().getId())
                .requestDate(cardBlock.getRequestDate())
                .status(cardBlock.getStatus().getCode())
                .userId(cardBlock.getRequestedBy().getId())
                .build();

        log.info("Mapped CardBlockResponseDto: id={}, cardId={}, userId={}, status={}",
                dto.getId(), dto.getCardId(), dto.getUserId(), dto.getStatus());
        return dto;
    }
}

