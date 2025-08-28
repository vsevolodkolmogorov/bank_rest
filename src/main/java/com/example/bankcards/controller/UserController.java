package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UpdateUserRoleDto;
import com.example.bankcards.dto.user.UserRequestDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Управление пользователями", description = "API для управления данными пользователей")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получение пользователя по ID", description = "Возвращает информацию о пользователе по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение пользователя",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    @GetMapping("/{id}")
    @JsonView(JsonViews.Auth.class)
    public UserResponseDto getUser(
            @Parameter(description = "Идентификатор пользователя", example = "10") @PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        return userService.getById(id);
    }

    @Operation(summary = "Получение всех пользователей", description = "Возвращает страницу всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка пользователей",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content)
    })

    @GetMapping
    @JsonView(JsonViews.Auth.class)
    public Page<UserResponseDto> getAllUsers(
            @Parameter(description = "Номер страницы (начинается с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all users, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return userService.getAll(pageable);
    }

    @Operation(summary = "Создание нового пользователя", description = "Создает нового пользователя на основе предоставленных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким логином уже существует", content = @Content)
    })
    @PostMapping
    @JsonView(JsonViews.Auth.class)
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto userDto) {
        log.info("Creating new user with email: {}", userDto.getEmail());
        return userService.create(userDto);
    }

    @Operation(summary = "Обновление роли пользователя", description = "Обновляет роль пользователя по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль успешно обновлена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    @PatchMapping("/{id}/role")
    @JsonView(JsonViews.Auth.class)
    public UserResponseDto updateRole(
            @Parameter(description = "Идентификатор пользователя", example = "10") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleDto dto) {
        log.info("Updating role for user ID: {} to {}", id, dto.getRole());
        return userService.updateRole(id, dto);
    }

    @Operation(summary = "Переключение статуса блокировки пользователя", description = "Переключает статус блокировки пользователя (заблокирован/не заблокирован)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус блокировки успешно изменен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    @PatchMapping("/{id}/toggleLock")
    @JsonView(JsonViews.Auth.class)
    public UserResponseDto toggleLockUser(
            @Parameter(description = "Идентификатор пользователя", example = "10") @PathVariable Long id) {
        log.info("Toggling lock status for user ID: {}", id);
        return userService.toggleLockUser(id);
    }

    @Operation(summary = "Переключение статуса активности пользователя", description = "Переключает статус активности пользователя (активен/не активен)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус активности успешно изменен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    @PatchMapping("/{id}/toggleEnable")
    @JsonView(JsonViews.Auth.class)
    public UserResponseDto toggleDisableUser(
            @Parameter(description = "Идентификатор пользователя", example = "10") @PathVariable Long id) {
        log.info("Toggling active status for user ID: {}", id);
        return userService.toggleDisableUser(id);
    }

    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    @DeleteMapping("/{id}")
    public void deleteUser(
            @Parameter(description = "Идентификатор пользователя", example = "10") @PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        userService.delete(id);
    }
}
