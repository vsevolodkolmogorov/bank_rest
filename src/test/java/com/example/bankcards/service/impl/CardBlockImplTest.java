package com.example.bankcards.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.bankcards.dto.cardBlock.CardBlockRequestDto;
import com.example.bankcards.dto.cardBlock.CardBlockResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlock;
import com.example.bankcards.entity.RequestStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.RequestStatusCode;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardBlockRepository;
import com.example.bankcards.repository.RequestStatusRepository;
import com.example.bankcards.service.CardInternalService;
import com.example.bankcards.service.UserInternalService;
import com.example.bankcards.util.mapper.CardBlockMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

class CardBlockImplTest {

    @InjectMocks
    private CardBlockImpl cardBlockService;

    @Mock
    private CardBlockMapper mapper;

    @Mock
    private CardInternalService cardService;

    @Mock
    private UserInternalService userService;

    @Mock
    private RequestStatusRepository requestRepository;

    @Mock
    private CardBlockRepository cardBlockRepository;

    private User user;
    private Card card;
    private CardBlock cardBlock;
    private RequestStatus pendingStatus;
    private CardBlockRequestDto requestDto;
    private CardBlockResponseDto responseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setLogin("testUser");

        card = new Card();
        card.setId(1L);

        pendingStatus = new RequestStatus();
        pendingStatus.setCode(RequestStatusCode.PENDING);

        cardBlock = new CardBlock();
        cardBlock.setId(1L);
        cardBlock.setCard(card);
        cardBlock.setRequestedBy(user);
        cardBlock.setStatus(pendingStatus);

        requestDto = new CardBlockRequestDto();
        requestDto.setCardId(1L);

        responseDto = new CardBlockResponseDto();
        responseDto.setStatus(RequestStatusCode.PENDING);
    }

    @Test
    void testCreateCardBlock() {
        when(userService.getUserEntityByLogin("testUser")).thenReturn(user);
        when(cardService.findCardById(1L)).thenReturn(card);
        doNothing().when(cardService).validateUserOwnsCard(card, user);
        when(requestRepository.findByCode(RequestStatusCode.PENDING)).thenReturn(Optional.of(pendingStatus));
        when(mapper.toEntity(any(LocalDateTime.class), eq(user), eq(card), eq(pendingStatus))).thenReturn(cardBlock);
        when(cardBlockRepository.save(cardBlock)).thenReturn(cardBlock);
        when(mapper.toResponseDto(cardBlock)).thenReturn(responseDto);

        CardBlockResponseDto result = cardBlockService.create("testUser", requestDto);

        assertEquals(RequestStatusCode.PENDING, result.getStatus());
        verify(cardBlockRepository).save(cardBlock);
    }

    @Test
    void testGetAllCardBlocks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardBlock> page = new PageImpl<>(Collections.singletonList(cardBlock));
        when(cardBlockRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponseDto(cardBlock)).thenReturn(responseDto);

        Page<CardBlockResponseDto> result = cardBlockService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(RequestStatusCode.PENDING, result.getContent().get(0).getStatus());
    }

    @Test
    void testGetAllRequestByUserLogin() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardBlock> page = new PageImpl<>(Collections.singletonList(cardBlock));
        when(cardBlockRepository.findAllByRequestedByLogin("testUser", pageable)).thenReturn(page);
        when(mapper.toResponseDto(cardBlock)).thenReturn(responseDto);

        Page<CardBlockResponseDto> result = cardBlockService.getAllRequestByUserLogin("testUser", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(RequestStatusCode.PENDING, result.getContent().get(0).getStatus());
    }

    @Test
    void testGetByIdCardBlockFound() {
        when(cardBlockRepository.findCardBlockById(1L)).thenReturn(Optional.of(cardBlock));
        when(mapper.toResponseDto(cardBlock)).thenReturn(responseDto);

        CardBlockResponseDto result = cardBlockService.getById(1L);

        assertEquals(RequestStatusCode.PENDING, result.getStatus());
    }

    @Test
    void testGetByIdCardBlockNotFound() {
        when(cardBlockRepository.findCardBlockById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardBlockService.getById(1L));
    }

    @Test
    void testApproveRequest() {
        RequestStatus approvedStatus = new RequestStatus();
        approvedStatus.setCode(RequestStatusCode.APPROVED);

        when(cardBlockRepository.findCardBlockById(1L)).thenReturn(Optional.of(cardBlock));
        when(requestRepository.findByCode(RequestStatusCode.APPROVED)).thenReturn(Optional.of(approvedStatus));
        when(mapper.toResponseDto(any(CardBlock.class))).thenAnswer(invocation -> {
            CardBlock cb = invocation.getArgument(0);
            CardBlockResponseDto dto = new CardBlockResponseDto();
            dto.setStatus(cb.getStatus().getCode());
            return dto;
        });
        when(cardService.updateStatus(eq(1L), any())).thenReturn(null);

        CardBlockResponseDto result = cardBlockService.approveRequest(1L, "ok");

        verify(cardService).updateStatus(eq(1L), any());
        assertEquals(RequestStatusCode.APPROVED, result.getStatus());
    }

    @Test
    void testRejectRequest() {
        RequestStatus rejectedStatus = new RequestStatus();
        rejectedStatus.setCode(RequestStatusCode.REJECTED);

        when(cardBlockRepository.findCardBlockById(1L)).thenReturn(Optional.of(cardBlock));
        when(requestRepository.findByCode(RequestStatusCode.REJECTED)).thenReturn(Optional.of(rejectedStatus));
        when(mapper.toResponseDto(any(CardBlock.class))).thenAnswer(invocation -> {
            CardBlock cb = invocation.getArgument(0);
            CardBlockResponseDto dto = new CardBlockResponseDto();
            dto.setStatus(cb.getStatus().getCode());
            return dto;
        });

        CardBlockResponseDto result = cardBlockService.rejectRequest(1L, "reject");

        assertEquals(RequestStatusCode.REJECTED, result.getStatus());
    }

    @Test
    void testDeleteCardBlock() {
        when(cardBlockRepository.findCardBlockById(1L)).thenReturn(Optional.of(cardBlock));

        cardBlockService.delete(1L);

        verify(cardBlockRepository).delete(cardBlock);
    }
}
