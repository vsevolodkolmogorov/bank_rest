package com.example.bankcards.service.impl;

import com.example.bankcards.dto.auth.AuthRequestDto;
import com.example.bankcards.dto.auth.AuthResponseDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthenticationProcessException;
import com.example.bankcards.exception.EmailAlreadyRegisteredException;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.CardInternalService;
import com.example.bankcards.service.UserInternalService;
import com.example.bankcards.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;
    private final UserInternalService userInternalService;
    private final CardInternalService cardInternalService;

    @Override
    public AuthResponseDto register(AuthRequestDto authRequestDto) {
        validateUserExistence(authRequestDto.getEmail());

        User user = createUserFromDto(authRequestDto);
        UserResponseDto responseDto = userMapper.toResponseDto(user);
        String token = jwtService.generateToken(user);
        AuthResponseDto result = new AuthResponseDto(token, responseDto);
        log.info("Register user {} success with token: {}, result: {}", responseDto.getEmail(), token, result);
        cardInternalService.initiationThreeCards(user.getId());
        return result;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        User user = userInternalService.getUserEntityByEmail(authRequestDto.getEmail());
        UserResponseDto responseDto = userMapper.toResponseDto(user);
        authenticateUser(authRequestDto);
        String token = jwtService.generateToken(user);

        AuthResponseDto result = new AuthResponseDto(token, responseDto);
        log.info("User email {} success with token: {}, result: {}", responseDto.getEmail(), token, result);
        return result;
    }

    @Override
    public AuthResponseDto getCurrentUser(String token) {
        String email = jwtService.extractUsername(token);
        User user = userInternalService.getUserEntityByEmail(email);
        UserResponseDto responseDto = userMapper.toResponseDto(user);

        AuthResponseDto result = new AuthResponseDto(token, responseDto);
        log.info("Get current user {} success, token: {}, result: {}", responseDto.getEmail(), token, result);
        return result;
    }

    private User createUserFromDto(AuthRequestDto authRequestDto) {
        User userMapped = userMapper.authToEntity(authRequestDto);
        User user = userInternalService.createUserEntity(userMapped);

        log.info("Created user {} from authDto email {}", user.getUsername(), authRequestDto.getEmail());
        return user;
    }

    private void authenticateUser(AuthRequestDto authRequestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(), authRequestDto.getPassword())
            );
            log.info("Authenticated user {}", authRequestDto.getEmail());
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", authRequestDto.getEmail(), e.getMessage());
            throw new AuthenticationProcessException("Ошибка при аутентификации: " + e.getMessage());
        }
    }

    private void validateUserExistence(String email) {
        boolean exists = userInternalService.existByUserEmail(email);
        log.info("Validation user {} existence: {}", email, exists);

        if (exists) {
            log.error("EmailAlreadyRegisteredException: email {} is already registered", email);
            throw new EmailAlreadyRegisteredException("Почта уже зарегистрирован: " + email);
        }
    }
}

