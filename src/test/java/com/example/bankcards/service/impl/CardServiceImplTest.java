package com.example.bankcards.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.bankcards.dto.card.CardRequestDto;
import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.dto.card.CardTransferRequestDto;
import com.example.bankcards.dto.card.UpdateStatusDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.entity.enums.CardStatusCode;
import com.example.bankcards.entity.enums.UserRoleCode;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.StatusCodeSameException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.service.EncryptionCardService;
import com.example.bankcards.service.UserInternalService;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.mapper.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardMapper mapper;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardStatusRepository cardStatusRepository;

    @Mock
    private UserInternalService userService;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @Mock
    private EncryptionCardService encryptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCardSuccess() {
        User user = new User();
        user.setLogin("user1");

        CardStatus status = new CardStatus();
        status.setCode(CardStatusCode.ACTIVE);

        CardRequestDto request = new CardRequestDto();
        request.setUserId(1L);
        request.setStatus(CardStatusCode.ACTIVE);

        Card mappedCard = new Card();
        mappedCard.setUser(user);

        Card savedCard = new Card();
        savedCard.setUser(user);
        savedCard.setLastFourDigits("1234");

        CardResponseDto responseDto = new CardResponseDto();
        responseDto.setMaskedCardNumber("****1234");

        when(userService.getUserEntityById(1L)).thenReturn(user);
        when(cardStatusRepository.findByCode(CardStatusCode.ACTIVE)).thenReturn(Optional.of(status));
        when(mapper.toEntity(request, user, status)).thenReturn(mappedCard);
        when(cardNumberGenerator.generateCardNumber()).thenReturn("1111222233331234");
        when(encryptionService.encrypt("1111222233331234")).thenReturn("encrypted");
        when(cardRepository.findCardByCardNumberEncrypted("encrypted")).thenReturn(Optional.empty());
        when(cardRepository.save(mappedCard)).thenReturn(savedCard);
        when(mapper.toResponseDto(savedCard)).thenReturn(responseDto);

        CardResponseDto result = cardService.create(request);

        assertEquals("****1234", result.getMaskedCardNumber());
        verify(cardRepository).save(mappedCard);
        verify(encryptionService).encrypt("1111222233331234");
    }

    @Test
    void testUpdateStatusThrowsIfSame() {
        Card card = new Card();
        CardStatus status = new CardStatus();
        status.setCode(CardStatusCode.ACTIVE);
        card.setStatus(status);

        when(cardRepository.findCardById(1L)).thenReturn(Optional.of(card));

        UpdateStatusDto dto = new UpdateStatusDto(CardStatusCode.ACTIVE);

        assertThrows(StatusCodeSameException.class, () -> cardService.updateStatus(1L, dto));
    }

    @Test
    void testTransferBetweenCardsSuccess() {
        User user = new User();
        user.setLogin("user1");
        user.setRole(new UserRole(1L, UserRoleCode.USER));

        CardStatus cardStatus = new CardStatus(1L, CardStatusCode.ACTIVE);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(user);
        fromCard.setBalance(BigDecimal.valueOf(100));
        fromCard.setStatus(cardStatus);
        fromCard.setLastFourDigits("1111");

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(user);
        toCard.setBalance(BigDecimal.valueOf(50));
        toCard.setStatus(cardStatus);
        toCard.setLastFourDigits("2222");

        when(cardRepository.findCardById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findCardById(2L)).thenReturn(Optional.of(toCard));
        when(userService.getUserEntityByLogin("user1")).thenReturn(user);
        when(cardStatusRepository.findByCode(CardStatusCode.ACTIVE)).thenReturn(Optional.of(cardStatus));

        CardTransferRequestDto dto = new CardTransferRequestDto();
        dto.setFromCardId(1L);
        dto.setToCardId(2L);
        dto.setAmount(BigDecimal.valueOf(60));

        cardService.transferBetweenCards("user1", dto);

        assertEquals(BigDecimal.valueOf(40), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(110), toCard.getBalance());

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    void testValidateUserOwnsCardThrowsIfNotOwner() {
        User user = new User();
        user.setLogin("user1");
        user.setRole(new UserRole(1L, UserRoleCode.USER));

        User otherUser = new User();
        otherUser.setLogin("user2");

        Card card = new Card();
        card.setUser(otherUser);
        card.setLastFourDigits("1234");

        assertThrows(AccessDeniedException.class, () -> cardService.validateUserOwnsCard(card, user));
    }
}
