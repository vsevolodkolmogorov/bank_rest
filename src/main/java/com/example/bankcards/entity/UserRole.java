package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatusCode;
import com.example.bankcards.entity.enums.UserRoleCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Роль пользователя (например, ADMIN, USER)")
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор роли", example = "1")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private UserRoleCode code;
}

