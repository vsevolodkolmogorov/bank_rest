package com.example.bankcards.dto.card;

import com.example.bankcards.entity.enums.CardStatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект запроса для обновления статуса карты")
public class UpdateStatusDto {

    @Schema(description = "Код статуса карты",
            example = "ACTIVE",
            required = true)
    @NotNull(message = "Статус обязателен")
    private CardStatusCode status;
}
