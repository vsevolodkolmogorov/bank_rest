package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatusCode;
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
@Schema(description = "Статус банковской карты (например, Активна, Заблокирована, Истек срок)")
@Table(name = "card_status")
public class CardStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор статуса", example = "1")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private CardStatusCode code;
}

