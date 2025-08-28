package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Базовый объект запроса для данных пользователя")
public class BaseUserDto {

    @Schema(description = "Почта пользователя",
            example = "user@example.com",
            required = true,
            minLength = 5,
            maxLength = 20)
    @Email
    @NotBlank(message = "Почта не может быть пустым")
    @Size(min = 5, max = 50, message = "Почта должен содержать от 3 до 20 символов")
    private String email;

    @Schema(description = "Пароль пользователя",
            example = "Passw0rd*",
            required = true,
            minLength = 8,
            pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).*$")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).*$",
            message = "Пароль должен содержать цифру, строчную букву, заглавную букву и специальный символ"
    )
    private String password;
}

