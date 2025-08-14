package com.example.bankcards.dto.cardBlock;

import com.example.bankcards.entity.enums.RequestStatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Объект ответа для операции блокировки карты")
public class CardBlockResponseDto {

    @Schema(description = "Уникальный идентификатор запроса на блокировку",
            example = "1",
            required = true)
    private Long id;

    @Schema(description = "Дата и время создания запроса на блокировку",
            example = "2025-08-13T20:37:00",
            required = true)
    private LocalDateTime requestDate;

    @Schema(description = "Идентификатор карты, подлежащей блокировке",
            example = "10",
            required = true)
    private Long cardId;

    @Schema(description = "Идентификатор пользователя, связанного с картой",
            example = "100",
            required = true)
    private Long userId;

    @Schema(description = "Статус запроса на блокировку",
            example = "APPROVED",
            required = true)
    private RequestStatusCode status;

    @Schema(description = "Комментарий администратора к запросу на блокировку",
            example = "Карта заблокирована по запросу пользователя",
            required = false)
    private String adminComment;
}
