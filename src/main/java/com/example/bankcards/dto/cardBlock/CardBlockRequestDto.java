package com.example.bankcards.dto.cardBlock;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Объект запроса для блокировки карты")
public class CardBlockRequestDto {

    @Schema(description = "Идентификатор карты для блокировки",
            example = "10",
            required = true)
    @NotNull(message = "Идентификатор карты обязателен")
    @Positive(message = "Идентификатор карты должен быть положительным")
    private Long cardId;
}
