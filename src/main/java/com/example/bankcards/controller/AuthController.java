package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthRequestDto;
import com.example.bankcards.dto.auth.AuthResponseDto;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.util.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Аутентификация", description = "API для регистрации, входа и получения информации о текущем пользователе")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация нового пользователя", description = "Создает нового пользователя и возвращает JWT-токен и информацию о пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная регистрация",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса или ошибка разбора токена",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким логином уже существует", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping("/register")
    @JsonView(JsonViews.Auth.class)
    public AuthResponseDto register(@Valid @RequestBody AuthRequestDto authRequestDto) {
        log.info("Register request received for email: {}", authRequestDto.getLogin());
        return authService.register(authRequestDto);
    }

    @Operation(summary = "Вход пользователя", description = "Аутентифицирует пользователя и возвращает JWT-токен и информацию о пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса или ошибка разбора токена",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль / ошибка аутентификации", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping("/login")
    @JsonView(JsonViews.Auth.class)
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        log.info("Login attempt for email: {}", authRequestDto.getLogin());
        return authService.login(authRequestDto);
    }

    @Operation(summary = "Получение информации о текущем пользователе", description = "Возвращает информацию о текущем пользователе на основе JWT-токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение информации о пользователе",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка разбора токена", content = @Content),
            @ApiResponse(responseCode = "401", description = "Недействительный или отсутствующий токен / ошибка аутентификации", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/me")
    @JsonView(JsonViews.Full.class)
    public AuthResponseDto getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        log.info("Get current user attempt for token: {}", token);
        return authService.getCurrentUser(token);
    }
}