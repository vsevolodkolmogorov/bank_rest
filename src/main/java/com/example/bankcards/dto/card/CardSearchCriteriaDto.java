package com.example.bankcards.dto.card;

import com.example.bankcards.entity.enums.CardStatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Объект критериев поиска для фильтрации карт")
public class CardSearchCriteriaDto {

    @Schema(description = "Последние четыре цифры номера карты",
            example = "1234")
    private String lastFourDigits;

    @Schema(description = "Статус карты",
            example = "ACTIVE")
    private CardStatusCode status;

    @Schema(description = "Минимальный баланс на карте",
            example = "100.00")
    private BigDecimal minBalance;

    @Schema(description = "Максимальный баланс на карте",
            example = "1000.00")
    private BigDecimal maxBalance;
}
