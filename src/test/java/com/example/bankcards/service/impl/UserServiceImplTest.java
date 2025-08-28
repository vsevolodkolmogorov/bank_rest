package com.example.bankcards.service.impl;

import com.example.bankcards.dto.user.UpdateUserRoleDto;
import com.example.bankcards.dto.user.UserRequestDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.entity.enums.UserRoleCode;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.UserRoleRepository;
import com.example.bankcards.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper mapper;

    private UserRole userRole;
    private User user;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRole = new UserRole();
        userRole.setCode(UserRoleCode.USER);

        user = new User();
        user.setId(1L);
        user.setEmail("testUser@example.com");
        user.setPassword("pass");
        user.setRole(userRole);
        user.setAccountNonLocked(true);
        user.setEnabled(true);

        userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("testUser@example.com");
        userRequestDto.setPassword("pass");
        userRequestDto.setRole(UserRoleCode.USER);

        userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("testUser@example.com");
        userResponseDto.setRoleName("USER");
    }

    @Test
    void testCreateUser() {
        when(roleRepository.findByCode(UserRoleCode.USER)).thenReturn(Optional.of(userRole));
        when(mapper.toEntity(userRequestDto, userRole)).thenReturn(user);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.create(userRequestDto);

        assertEquals("test", result.getEmail());
        assertEquals("USER", result.getRoleName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testGetAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Collections.singletonList(user);
        Page<User> page = new PageImpl<>(users, pageable, users.size());
        when(userRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponseDto(user)).thenReturn(userResponseDto);

        Page<UserResponseDto> result = userService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("test", result.getContent().get(0).getEmail());
    }

    @Test
    void testGetByIdUserFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getById(1L);

        assertEquals("test", result.getEmail());
    }

    @Test
    void testGetByIdUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void testUpdateRoleSuccess() {
        UpdateUserRoleDto dto = new UpdateUserRoleDto();
        dto.setRole(UserRoleCode.ADMIN);

        UserRole newRole = new UserRole();
        newRole.setCode(UserRoleCode.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByCode(UserRoleCode.ADMIN)).thenReturn(Optional.of(newRole));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateRole(1L, dto);

        assertEquals("USER", result.getRoleName());
        verify(userRepository).save(user);
    }

    @Test
    void testToggleLockUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.toggleLockUser(1L);

        assertEquals("test", result.getEmail());
        assertFalse(user.isAccountNonLocked());
        verify(userRepository).save(user);
    }

    @Test
    void testToggleDisableUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.toggleDisableUser(1L);

        assertEquals("test", result.getEmail());
        assertFalse(user.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).delete(user);
    }
}
