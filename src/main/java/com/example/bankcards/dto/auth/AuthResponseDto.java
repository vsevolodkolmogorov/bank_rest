package com.example.bankcards.dto.auth;

import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.util.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Объект ответа аутентификации, содержащий JWT-токен и информацию о пользователе")
public class AuthResponseDto {
    @Schema(description = "JWT-токен для аутентификации",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2aWxhcnJlYWxpdHkiLCJpYXQiOjE3NTQ5ODg5MjYsImV4cCI6MTc1NTA3NTMyNn0.DnEr5Gx5xuaD0Kf0x_SFoYm6QGazn61UNNfJHCqkRB3",
            required = true)
    @JsonView(JsonViews.Auth.class)
    private String token;

    @Schema(description = "Информация о пользователе", required = true)
    @JsonView(JsonViews.Auth.class)
    private UserResponseDto user;
}
