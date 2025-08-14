package com.example.bankcards.dto.user;

import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.util.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Объект ответа, содержащий информацию о пользователе")
public class UserResponseDto {

    @Schema(description = "Уникальный идентификатор пользователя",
            example = "10",
            required = true)
    @JsonView(JsonViews.Auth.class)
    private Long id;

    @Schema(description = "Логин пользователя",
            example = "user123",
            required = true)
    @JsonView(JsonViews.Auth.class)
    private String login;

    @Schema(description = "Название роли пользователя",
            example = "ADMIN",
            required = true)
    @JsonView(JsonViews.Auth.class)
    private String roleName;

    @Schema(description = "Флаг, указывающий, активен ли пользователь",
            example = "true",
            required = true)
    @JsonView(JsonViews.Auth.class)
    private boolean isEnabled;

    @Schema(description = "Флаг, указывающий, не заблокирован ли пользователь",
            example = "true",
            required = true)
    @JsonView(JsonViews.Auth.class)
    private boolean isNonLocked;

    @Schema(description = "Список карт пользователя",
            required = false)
    @Builder.Default
    @JsonView(JsonViews.Full.class)
    private List<CardResponseDto> cards = new ArrayList<>();
}

