package com.example.bankcards.controller;

import com.example.bankcards.dto.cardBlock.CardBlockRequestDto;
import com.example.bankcards.dto.cardBlock.CardBlockResponseDto;
import com.example.bankcards.service.CardBlockService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cardBlock")
@RequiredArgsConstructor
@Tag(name = "Блокировка карт", description = "API для управления запросами на блокировку карт")
@Slf4j
public class CardBlockController {

    private final CardBlockService cardBlockService;

    @Operation(summary = "Получение запроса на блокировку по ID", description = "Возвращает информацию о запросе на блокировку карты по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardBlockResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Запрос не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/{id}")
    public CardBlockResponseDto getCardBlock(@Parameter(description = "Идентификатор запроса на блокировку", example = "1") @PathVariable Long id) {
        log.info("Get card block attempt by id: {}",  id);
        return cardBlockService.getById(id);
    }

    @Operation(summary = "Получение запроса на блокировку текущего пользователя", description = "Возвращает информацию о запросе на блокировку карты, связанного с текущим пользователем")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardBlockResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content),
            @ApiResponse(responseCode = "401", description = "Недействительный или отсутствующий токен", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Запрос не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/{id}/my")
    public CardBlockResponseDto getUserCardBlock(
            @Parameter(description = "Идентификатор запроса на блокировку", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        log.info("Get user {} card block attempt by id: {}", login,  id);
        return cardBlockService.getUserRequestById(login, id);
    }

    @Operation(summary = "Получение списка запросов на блокировку текущего пользователя", description = "Возвращает страницу запросов на блокировку карт, связанных с текущим пользователем")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка запросов",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content),
            @ApiResponse(responseCode = "401", description = "Недействительный или отсутствующий токен", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/my")
    public Page<CardBlockResponseDto> getUserCardBlocks(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Номер страницы (начинается с 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        String login = userDetails.getUsername();
        log.info("Get users {} card blocks pages attempt: {}", login,  pageable.getPageSize());
        return cardBlockService.getAllRequestByUserLogin(login, pageable);
    }

    @Operation(summary = "Получение всех запросов на блокировку", description = "Возвращает страницу всех запросов на блокировку карт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка запросов",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping
    public Page<CardBlockResponseDto> getAllCardBlocks(
            @Parameter(description = "Номер страницы (начинается с 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.info("Get all card blocks pages attempt: {}", pageable.getPageSize());
        return cardBlockService.getAll(pageable);
    }

    @Operation(summary = "Создание запроса на блокировку карты", description = "Создает новый запрос на блокировку карты для текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос успешно создан",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardBlockResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
            @ApiResponse(responseCode = "401", description = "Недействительный или отсутствующий токен", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping
    public CardBlockResponseDto createCardBlock(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CardBlockRequestDto cardBlockDto) {
        String login = userDetails.getUsername();
        log.info("Create card {} blocks attempt: {}", cardBlockDto.getCardId(), login);
        return cardBlockService.create(login, cardBlockDto);
    }

    @Operation(summary = "Одобрение запроса на блокировку", description = "Одобряет запрос на блокировку карты с указанным комментарием")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос успешно одобрен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardBlockResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Запрос не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PatchMapping("/{id}/approve")
    public CardBlockResponseDto approveRequest(
            @Parameter(description = "Идентификатор запроса на блокировку", example = "1") @PathVariable Long id,
            @Parameter(description = "Комментарий администратора", example = "Одобрено администратором") @RequestParam String comment) {
        log.info("Approve card {} block attempt, admin comment: {}", id , comment);
        return cardBlockService.approveRequest(id, comment);
    }

    @Operation(summary = "Отклонение запроса на блокировку", description = "Отклоняет запрос на блокировку карты с указанным комментарием")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос успешно отклонен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardBlockResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Запрос не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PatchMapping("/{id}/reject")
    public CardBlockResponseDto rejectRequest(
            @Parameter(description = "Идентификатор запроса на блокировку", example = "1") @PathVariable Long id,
            @Parameter(description = "Комментарий администратора", example = "Отклонено из-за недостаточной информации") @RequestParam String comment) {
        log.info("Reject card {} block attempt, admin comment: {}", id , comment);
        return cardBlockService.rejectRequest(id, comment);
    }

    @Operation(summary = "Удаление запроса на блокировку", description = "Удаляет запрос на блокировку карты по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Запрос успешно удален", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Запрос не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @DeleteMapping("/{id}")
    public void deleteCardBlock(@Parameter(description = "Идентификатор запроса на блокировку", example = "1") @PathVariable Long id) {
        log.info("Delete card {} block attempt", id);
        cardBlockService.delete(id);
    }
}
