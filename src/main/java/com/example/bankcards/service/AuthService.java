package com.example.bankcards.service;

import com.example.bankcards.dto.auth.AuthRequestDto;
import com.example.bankcards.dto.auth.AuthResponseDto;

public interface AuthService {
    AuthResponseDto register(AuthRequestDto authRequestDto);
    AuthResponseDto login(AuthRequestDto authRequestDto);
    AuthResponseDto getCurrentUser(String token);
}
