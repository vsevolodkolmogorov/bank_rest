package com.example.bankcards.service.impl;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatusCode;
import com.example.bankcards.entity.enums.UserRoleCode;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardStatusRepository;
import com.example.bankcards.service.CardInternalService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.EncryptionCardService;
import com.example.bankcards.service.UserInternalService;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.CardSpecifications;
import com.example.bankcards.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService, CardInternalService {

    private final CardMapper mapper;
    private final CardRepository cardRepository;
    private final CardStatusRepository cardStatusRepository;
    private final UserInternalService userService;
    private final CardNumberGenerator cardNumberGenerator;
    private final EncryptionCardService encryptionService;

    @Override
    @Transactional
    public CardResponseDto create(CardRequestDto cardRequestDto) {
        User user = userService.getUserEntityById(cardRequestDto.getUserId());
        CardStatus cardStatus = findCardStatus(cardRequestDto.getStatus());

        Card mappedCard = mapper.toEntity(cardRequestDto, user, cardStatus);
        addCardNumberAndLastFourDigits(mappedCard);

        Card card = cardRepository.save(mappedCard);
        CardResponseDto result = mapper.toResponseDto(card);
        log.info("Create card for user {} with number {}", user.getEmail(), result.getMaskedCardNumber());
        return result;
    }

    @Transactional
    public void initiationThreeCards(Long userId) {
        CardRequestDto cardActive1000 = CardRequestDto.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000L))
                .status(CardStatusCode.ACTIVE)
                .expiryDate(YearMonth.now().plusYears(3))
                .build();

        CardRequestDto cardActiveZero = CardRequestDto.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(0L))
                .status(CardStatusCode.ACTIVE)
                .expiryDate(YearMonth.now().plusYears(3))
                .build();

        CardRequestDto cardEXPIRED = CardRequestDto.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(0L))
                .status(CardStatusCode.EXPIRED)
                .expiryDate(YearMonth.now())
                .build();

        List<CardRequestDto> list = List.of(cardActive1000, cardActiveZero, cardEXPIRED);

        for (CardRequestDto card: list) {
            create(card);
        }
    }

    @Override
    public Page<CardResponseDto> getAll(CardSearchCriteriaDto criteria, Pageable pageable) {
        Specification<Card> spec = Specification
                .where(CardSpecifications.withLastFourDigits(criteria.getLastFourDigits()))
                .and(CardSpecifications.withStatus(criteria.getStatus() != null ? findCardStatus(criteria.getStatus()) : null))
                .and(CardSpecifications.withBalanceRange(criteria.getMinBalance(), criteria.getMaxBalance()));

        Page<CardResponseDto> result = cardRepository.findAll(spec, pageable).map(mapper::toResponseDto);
        log.info("Get all cards, total pages: {}", result.getTotalPages());
        return result;
    }

    @Override
    public Page<CardResponseDto> getAllCardsByUserEmail(String email, CardSearchCriteriaDto criteria, Pageable pageable) {
        Specification<Card> spec = Specification
                .where(CardSpecifications.belongsToUser(email))
                .and(CardSpecifications.withLastFourDigits(criteria.getLastFourDigits()))
                .and(CardSpecifications.withStatus(criteria.getStatus() != null ? findCardStatus(criteria.getStatus()) : null))
                .and(CardSpecifications.withBalanceRange(criteria.getMinBalance(), criteria.getMaxBalance()));

        Page<CardResponseDto> result = cardRepository.findAll(spec, pageable).map(mapper::toResponseDto);
        log.info("Get all cards for user {}, total pages: {}", email, result.getTotalPages());
        return result;
    }

    @Override
    public CardResponseDto getById(Long id) {
        CardResponseDto result = mapper.toResponseDto(findCardById(id));
        log.info("Get card by id {} -> {}", id, result.getMaskedCardNumber());
        return result;
    }

    @Override
    public CardResponseDto getUserCardById(String email, Long id) {
        Card card = findCardByIdAndUserEmail(id, email);
        CardResponseDto result = mapper.toResponseDto(card);
        log.info("Get card for user {} by id {} -> {}", email, id, result.getMaskedCardNumber());
        return result;
    }

    @Override
    public CardResponseDto updateExpiryDate(Long id, UpdateExpiryDateDto dto) {
        Card card = findCardById(id);
        validateExpiryDateChange(card, dto.getExpiryDate());
        card.setExpiryDate(dto.getExpiryDate());

        Card updatedCard = cardRepository.save(card);
        CardResponseDto result = mapper.toResponseDto(updatedCard);
        log.info("Update expiry date for card {} to {}", result.getMaskedCardNumber(), result.getExpiryDate());
        return result;
    }

    @Override
    public CardResponseDto updateStatus(Long id, UpdateStatusDto dto) {
        Card card = findCardById(id);
        validateStatusChange(card, dto.getStatus());
        card.setStatus(findCardStatus(dto.getStatus()));

        Card updatedCard = cardRepository.save(card);
        CardResponseDto result = mapper.toResponseDto(updatedCard);
        log.info("Update status for card {} to {}", result.getMaskedCardNumber(), result.getStatusName());
        return result;
    }

    @Override
    @Transactional
    public void transferBetweenCards(String email, CardTransferRequestDto dto) {
        Card fromCard = findCardById(dto.getFromCardId());
        Card toCard = findCardById(dto.getToCardId());

        validateTransfer(fromCard, toCard, email, dto.getAmount());

        fromCard.setBalance(fromCard.getBalance().subtract(dto.getAmount()));
        toCard.setBalance(toCard.getBalance().add(dto.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        log.info("Transfer {} from card {} to card {} by user {} completed",
                dto.getAmount(), fromCard.getLastFourDigits(), toCard.getLastFourDigits(), email);
    }

    @Override
    public void delete(Long id) {
        Card card = findCardById(id);
        cardRepository.delete(card);
        log.info("Delete card {} by id {}", card.getLastFourDigits(), id);
    }

    @Override
    public Card findCardById(Long id) {
        return cardRepository.findCardById(id)
                .orElseThrow(() -> new CardNotFoundException(String.format("Карта с идентификатором %s не найдена", id)));
    }

    @Override
    public boolean existsByCardNumberEncrypted(String encryptedCardNumber) {
        return cardRepository.findCardByCardNumberEncrypted(encryptedCardNumber).isPresent();
    }

    private Card findCardByIdAndUserEmail(Long id, String email) {
        return cardRepository.findCardByIdAndUserEmail(id, email)
                .orElseThrow(() -> new CardNotFoundException(String.format("Карта с идентификатором %s не найдена", id)));
    }

    private void validateTransfer(Card fromCard, Card toCard, String email, BigDecimal amount) {
        User user = userService.getUserEntityByEmail(email);
        validateUserOwnsCard(fromCard, user);
        validateUserOwnsCard(toCard, user);
        validateCardBalance(fromCard.getBalance(), amount);
        validateCardStatus(fromCard);
        validateCardStatus(toCard);
    }

    private CardStatus findCardStatus(CardStatusCode code) {
        return cardStatusRepository.findByCode(code)
                .orElseThrow(() -> new StatusCodeNotFoundException(String.format("Статус %s не найден", code.name())));
    }

    private void validateExpiryDateChange(Card card, YearMonth newDate) {
        if (card.getExpiryDate().equals(newDate)) {
            throw new ExpiryDateSameException("Срок действия совпадает с текущим");
        }
    }

    private void validateStatusChange(Card card, CardStatusCode cardStatusCode) {
        if (card.getStatus().getCode().equals(cardStatusCode)) {
            throw new StatusCodeSameException("Статус такой же, как и текущий");
        }
    }

    private void validateCardBalance(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Недостаточный баланс");
        }
    }

    private void validateCardStatus(Card card) {
        CardStatus cardStatusActive = findCardStatus(CardStatusCode.ACTIVE);
        if (card.getStatus() != cardStatusActive) {
            throw new IllegalStateException(String.format("Card %s is not active", card.getLastFourDigits()));
        }
    }

    @Override
    public void validateUserOwnsCard(Card card, User user) {
        if (user.getRole().getCode() == UserRoleCode.ADMIN) {
            return;
        }
        if (!card.getUser().getEmail().equals(user.getEmail())) {
            throw new AccessDeniedException(
                    String.format("Пользователь %s не является владельцем карты %s",
                            user.getEmail(),
                            card.getLastFourDigits())
            );
        }
    }

    private void addCardNumberAndLastFourDigits(Card card) {
        String cardNumber;
        String cardNumberEncrypted;
        String lastFourDigits;
        do {
            cardNumber = cardNumberGenerator.generateCardNumber();
            cardNumberEncrypted = encryptionService.encrypt(cardNumber);
            lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
        } while (existsByCardNumberEncrypted(cardNumberEncrypted));
        card.setCardNumberEncrypted(cardNumberEncrypted);
        card.setLastFourDigits(lastFourDigits);
    }
}
