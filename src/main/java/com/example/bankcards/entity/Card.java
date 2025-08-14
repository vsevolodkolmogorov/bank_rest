package com.example.bankcards.entity;

import com.example.bankcards.util.YearMonthAttributeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Информация о банковской карте")
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор карты", example = "123")
    private Long id;

    @Schema(description = "Зашифрованный номер карты", example = "EncryptedDataHere")
    private String cardNumberEncrypted;

    @Schema(description = "Последние 4 цифры исходного номера", example = "1234")
    @Column(name = "last_four_digits")
    private String lastFourDigits;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @Schema(description = "Владелец карты (пользователь)")
    private User user;

    @Convert(converter = YearMonthAttributeConverter.class)
    @Schema(description = "Срок действия карты в формате ГГГГ-ММ", example = "2025-08")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth expiryDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id")
    @Schema(description = "Статус карты")
    private CardStatus status;

    @Schema(description = "Баланс на карте", example = "1500.00")
    private BigDecimal balance;
}

