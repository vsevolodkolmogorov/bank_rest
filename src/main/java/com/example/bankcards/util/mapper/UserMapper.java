package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.auth.AuthRequestDto;
import com.example.bankcards.dto.card.CardResponseDto;
import com.example.bankcards.dto.user.UserRequestDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserMapper {

    private final CardMapper cardMapper;

    public User authToEntity(AuthRequestDto dto) {
        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
        log.info("Mapped authRequestDto {}, to entity {}", dto.getEmail(), user.getUsername());
        return user;
    }

    public User toEntity(UserRequestDto dto, UserRole role) {
        User user = User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(role)
                .build();

        log.info("Mapped userRequestDto {} with role {}, to entity {} ", dto.getEmail(), role.getCode().name(), user.getUsername());
        return user;
    }

    public UserResponseDto toResponseDto(User user) {
        List<CardResponseDto> cards = user.getCards().stream().map(cardMapper::toResponseDto).toList();

        UserResponseDto responseDto = UserResponseDto.builder()
                .id(user.getId())
                .roleName(user.getRole().getCode().name())
                .email(user.getEmail())
                .cards(cards)
                .isEnabled(user.isEnabled())
                .isNonLocked(user.isAccountNonLocked())
                .build();

        log.info("Mapped entity {} to userResponseDto {} ", user.getUsername(), responseDto.getEmail());
        return responseDto;
    }
}
