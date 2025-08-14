package com.example.bankcards.service.impl;

import com.example.bankcards.dto.card.UpdateStatusDto;
import com.example.bankcards.dto.cardBlock.CardBlockRequestDto;
import com.example.bankcards.dto.cardBlock.CardBlockResponseDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.entity.enums.CardStatusCode;
import com.example.bankcards.entity.enums.RequestStatusCode;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardBlockRepository;
import com.example.bankcards.repository.RequestStatusRepository;
import com.example.bankcards.service.CardBlockService;
import com.example.bankcards.service.CardInternalService;
import com.example.bankcards.service.UserInternalService;
import com.example.bankcards.util.mapper.CardBlockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardBlockImpl implements CardBlockService {

    private final CardBlockMapper mapper;
    private final CardInternalService cardService;
    private final UserInternalService userService;
    private final RequestStatusRepository requestRepository;
    private final CardBlockRepository cardBlockRepository;

    @Override
    public CardBlockResponseDto create(String login, CardBlockRequestDto cardBlockRequestDto) {
        User user = userService.getUserEntityByLogin(login);
        Card card = cardService.findCardById(cardBlockRequestDto.getCardId());
        cardService.validateUserOwnsCard(card, user);
        RequestStatus status = findRequestStatus(RequestStatusCode.PENDING);

        CardBlock mappedCardBlock = mapper.toEntity(LocalDateTime.now(), user, card, status);
        CardBlock cardBlock = cardBlockRepository.save(mappedCardBlock);

        CardBlockResponseDto response = mapper.toResponseDto(cardBlock);
        log.info("Created card block for cardId {} by user {}, status: {}", cardBlockRequestDto.getCardId(), login, response.getStatus());
        return response;
    }

    @Override
    public Page<CardBlockResponseDto> getAll(Pageable pageable) {
        Page<CardBlockResponseDto> result = cardBlockRepository.findAll(pageable).map(mapper::toResponseDto);
        log.info("Get all card block requests, total pages: {}", result.getTotalPages());
        return result;
    }

    @Override
    public Page<CardBlockResponseDto> getAllRequestByUserLogin(String login, Pageable pageable) {
        Page<CardBlockResponseDto> result = cardBlockRepository.findAllByRequestedByLogin(login, pageable).map(mapper::toResponseDto);
        log.info("Get all block requests for user {}, total pages: {}", login, result.getTotalPages());
        return result;
    }

    @Override
    public CardBlockResponseDto getById(Long id) {
        CardBlock cardBlock = findCardBlockById(id);
        CardBlockResponseDto responseDto = mapper.toResponseDto(cardBlock);
        log.info("Get block request by id {}, status: {}", id, responseDto.getStatus());
        return responseDto;
    }

    @Override
    public CardBlockResponseDto getUserRequestById(String login, Long id) {
        CardBlock cardBlock = cardBlockRepository.findCardBlockByIdAndRequestedByLogin(id, login)
                .orElseThrow(() -> new CardBlockRequestNotFoundException(String.format("Запрос на блокировку карты с идентификатором %s не найден", id)));
        CardBlockResponseDto responseDto = mapper.toResponseDto(cardBlock);
        log.info("Get block request by id {} for user {}, status: {}", id, login, responseDto.getStatus());
        return responseDto;
    }

    @Override
    public CardBlockResponseDto approveRequest(Long id, String comment) {
        CardBlock cardBlock = findCardBlockById(id);
        CardBlockResponseDto response = makeRequest(cardBlock, comment, RequestStatusCode.APPROVED);
        updateCardStatus(cardBlock.getCard().getId());
        log.info("Approve block request id {}, admin comment: {}, result status: {}", id, comment, response.getStatus());
        return response;
    }

    @Override
    public CardBlockResponseDto rejectRequest(Long id, String comment) {
        CardBlock cardBlock = findCardBlockById(id);
        CardBlockResponseDto response = makeRequest(cardBlock, comment, RequestStatusCode.REJECTED);
        log.info("Reject block request id {}, admin comment: {}, result status: {}", id, comment, response.getStatus());
        return response;
    }

    @Override
    public void delete(Long id) {
        CardBlock cardBlock = findCardBlockById(id);
        cardBlockRepository.delete(cardBlock);
        log.info("Deleted card block id {}, status was: {}", id, cardBlock.getStatus());
    }

    private CardBlockResponseDto makeRequest(CardBlock cardBlock, String comment, RequestStatusCode statusCode) {
        validationStatusRequest(cardBlock.getStatus().getCode());
        RequestStatus status = findRequestStatus(statusCode);
        cardBlock.setStatus(status);
        cardBlock.setAdminComment(comment);

        CardBlockResponseDto responseDto = mapper.toResponseDto(cardBlock);
        log.info("Processed block request id {} to status {} with admin comment: {}", cardBlock.getId(), responseDto.getStatus(), comment);
        return responseDto;
    }

    private CardBlock findCardBlockById(Long id) {
        return cardBlockRepository.findCardBlockById(id)
                .orElseThrow(() -> new CardNotFoundException(String.format("Карта с идентификатором %s не найдена", id)));
    }

    private RequestStatus findRequestStatus(RequestStatusCode code) {
        return requestRepository.findByCode(code)
                .orElseThrow(() -> new StatusCodeNotFoundException(String.format("Статус %s не найден", code.name())));
    }

    private void updateCardStatus(Long cardId) {
        UpdateStatusDto updateStatusDto = new UpdateStatusDto(CardStatusCode.BLOCKED);
        cardService.updateStatus(cardId, updateStatusDto);
    }

    private void validationStatusRequest(RequestStatusCode code) {
        if (code != RequestStatusCode.PENDING) {
            throw new BlockRequestAlreadyProcessedException(String.format("Статус запроса не PENDING, а %s", code));
        }
    }
}

