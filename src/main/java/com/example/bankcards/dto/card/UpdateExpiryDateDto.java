package com.example.bankcards.dto.card;

import com.example.bankcards.exception.yearMonth.FutureOrPresentYearMonth;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект запроса для обновления даты истечения срока действия карты")
public class UpdateExpiryDateDto {

    @Schema(description = "Дата истечения срока действия карты в формате yyyy-MM",
            type = "string",
            pattern = "yyyy-MM",
            example = "2025-08",
            required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @NotNull(message = "Дата истечения срока действия не может быть пустой")
    @FutureOrPresentYearMonth
    private YearMonth expiryDate;
}
