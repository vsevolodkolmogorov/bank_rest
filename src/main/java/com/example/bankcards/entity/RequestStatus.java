package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.RequestStatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "request_status")
public class RequestStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор статуса", example = "1")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private RequestStatusCode code;
}