package com.example.bankcards.dto.user;

import com.example.bankcards.entity.enums.UserRoleCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Объект запроса для обновления роли пользователя")
public class UpdateUserRoleDto {

    @Schema(description = "Роль пользователя",
            example = "ADMIN",
            required = true)
    @NotNull(message = "Роль не может быть пустой")
    private UserRoleCode role;
}
