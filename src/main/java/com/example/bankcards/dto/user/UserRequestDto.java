package com.example.bankcards.dto.user;

import com.example.bankcards.dto.BaseUserDto;
import com.example.bankcards.entity.enums.UserRoleCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект запроса для создания или обновления пользователя")
public class UserRequestDto extends BaseUserDto {

    @Schema(description = "Роль пользователя",
            example = "ADMIN",
            required = true)
    @NotNull(message = "Роль не может быть пустой")
    private UserRoleCode role;
}
