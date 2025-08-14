package com.example.bankcards.service.impl;

import com.example.bankcards.dto.auth.AuthRequestDto;
import com.example.bankcards.dto.auth.AuthResponseDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AuthenticationProcessException;
import com.example.bankcards.exception.LoginAlreadyRegisteredException;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.AuthService;
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

    @Override
    public AuthResponseDto register(AuthRequestDto authRequestDto) {
        validateUserExistence(authRequestDto.getLogin());

        User user = createUserFromDto(authRequestDto);
        UserResponseDto responseDto = userMapper.toResponseDto(user);
        String token = jwtService.generateToken(user);

        AuthResponseDto result = new AuthResponseDto(token, responseDto);
        log.info("Register user {} success with token: {}, result: {}", responseDto.getLogin(), token, result);
        return result;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        User user = userInternalService.getUserEntityByLogin(authRequestDto.getLogin());
        UserResponseDto responseDto = userMapper.toResponseDto(user);
        authenticateUser(authRequestDto);
        String token = jwtService.generateToken(user);

        AuthResponseDto result = new AuthResponseDto(token, responseDto);
        log.info("Login user {} success with token: {}, result: {}", responseDto.getLogin(), token, result);
        return result;
    }

    @Override
    public AuthResponseDto getCurrentUser(String token) {
        String login = jwtService.extractUsername(token);
        User user = userInternalService.getUserEntityByLogin(login);
        UserResponseDto responseDto = userMapper.toResponseDto(user);

        AuthResponseDto result = new AuthResponseDto(token, responseDto);
        log.info("Get current user {} success, token: {}, result: {}", responseDto.getLogin(), token, result);
        return result;
    }

    private User createUserFromDto(AuthRequestDto authRequestDto) {
        User userMapped = userMapper.authToEntity(authRequestDto);
        User user = userInternalService.createUserEntity(userMapped);

        log.info("Created user {} from authDto login {}", user.getUsername(), authRequestDto.getLogin());
        return user;
    }

    private void authenticateUser(AuthRequestDto authRequestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.getLogin(), authRequestDto.getPassword())
            );
            log.info("Authenticated user {}", authRequestDto.getLogin());
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", authRequestDto.getLogin(), e.getMessage());
            throw new AuthenticationProcessException(e.getMessage());
        }
    }

    private void validateUserExistence(String login) {
        boolean exists = userInternalService.existByUserLogin(login);
        log.info("Validation user {} existence: {}", login, exists);

        if (exists) {
            log.error("LoginAlreadyRegisteredException: login {} is already registered", login);
            throw new LoginAlreadyRegisteredException("Логин уже зарегистрирован: " + login);
        }
    }
}

