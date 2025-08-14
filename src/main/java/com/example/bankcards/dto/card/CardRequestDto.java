package com.example.bankcards.dto.card;

import com.example.bankcards.entity.enums.CardStatusCode;
import com.example.bankcards.exception.yearMonth.FutureOrPresentYearMonth;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Объект запроса для создания или обновления карты")
public class CardRequestDto {

    @Schema(description = "Идентификатор пользователя, связанного с картой",
            example = "123",
            required = true)
    @NotNull(message = "Идентификатор пользователя обязателен")
    @Positive(message = "Идентификатор пользователя должен быть положительным")
    private Long userId;

    @Schema(description = "Дата истечения срока действия карты в формате yyyy-MM",
            type = "string",
            pattern = "yyyy-MM",
            example = "2025-08",
            required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @NotNull(message = "Дата истечения срока действия не может быть пустой")
    @FutureOrPresentYearMonth
    private YearMonth expiryDate;

    @Schema(description = "Код статуса карты",
            example = "ACTIVE",
            required = true)
    @NotNull(message = "Статус обязателен")
    private CardStatusCode status;

    @Schema(description = "Баланс, доступный на карте",
            example = "1000.00",
            required = true)
    @NotNull(message = "Баланс обязателен")
    @PositiveOrZero(message = "Баланс должен быть положительным или нулевым")
    private BigDecimal balance;
}

