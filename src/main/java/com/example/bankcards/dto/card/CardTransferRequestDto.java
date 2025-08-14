package com.example.bankcards.dto.card;

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
@Schema(description = "Объект запроса для перевода средств между картами")
public class CardTransferRequestDto {

    @Schema(description = "Идентификатор карты, с которой осуществляется перевод",
            example = "10",
            required = true)
    private Long fromCardId;

    @Schema(description = "Идентификатор карты, на которую осуществляется перевод",
            example = "20",
            required = true)
    private Long toCardId;

    @Schema(description = "Сумма перевода",
            example = "500.00",
            required = true)
    private BigDecimal amount;
}
