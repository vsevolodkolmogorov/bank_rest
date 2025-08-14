package com.example.bankcards.service;

import com.example.bankcards.dto.user.UpdateUserRoleDto;
import com.example.bankcards.dto.user.UserRequestDto;
import com.example.bankcards.dto.user.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto create(UserRequestDto userRequestDto);
    Page<UserResponseDto> getAll(Pageable pageable);
    UserResponseDto getById(Long id);
    UserResponseDto updateRole(Long id, UpdateUserRoleDto dto);
    UserResponseDto toggleLockUser(Long id);
    UserResponseDto toggleDisableUser(Long id);
    void delete(Long id);
}
