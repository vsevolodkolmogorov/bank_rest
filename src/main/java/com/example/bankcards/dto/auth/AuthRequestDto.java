package com.example.bankcards.dto.auth;

import com.example.bankcards.dto.BaseUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Schema(description = "Запрос для авторизации пользователя")
public class AuthRequestDto extends BaseUserDto { }
