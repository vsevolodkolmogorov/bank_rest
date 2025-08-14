package com.example.bankcards.dto.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Объект ответа, содержащий информацию о карте")
public class CardResponseDto {

    @Schema(description = "Уникальный идентификатор карты",
            example = "10",
            required = true)
    private Long id;

    @Schema(description = "Маскированный номер карты, отображающий только последние 4 цифры",
            example = "**** **** **** 1234",
            required = true)
    private String maskedCardNumber;

    @Schema(description = "Логин владельца карты",
            example = "user123",
            required = true)
    private String ownerLogin;

    @Schema(description = "Дата истечения срока действия карты в формате yyyy-MM",
            type = "string",
            pattern = "yyyy-MM",
            example = "2025-08",
            required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth expiryDate;

    @Schema(description = "Название статуса карты",
            example = "ACTIVE",
            required = true)
    private String statusName;

    @Schema(description = "Текущий баланс на карте",
            example = "1500.75",
            required = true)
    private BigDecimal balance;
}
