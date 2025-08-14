package com.example.bankcards.service.impl;

import com.example.bankcards.dto.user.UserRequestDto;
import com.example.bankcards.dto.user.UserResponseDto;
import com.example.bankcards.dto.user.UpdateUserRoleDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.entity.enums.UserRoleCode;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UserRoleNotFoundException;
import com.example.bankcards.exception.UserRoleSameException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.UserRoleRepository;
import com.example.bankcards.service.UserInternalService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserInternalService {

    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Override
    public UserResponseDto create(UserRequestDto userRequestDto) {
        User mappedUser = mapper.toEntity(userRequestDto, getRole(userRequestDto.getRole()));
        User user = createUserEntity(mappedUser);
        UserResponseDto responseDto = mapper.toResponseDto(user);
        log.info("Created user {} from userRequestDto", responseDto.getLogin());
        return responseDto;
    }

    @Override
    public Page<UserResponseDto> getAll(Pageable pageable) {
        Page<UserResponseDto> allUsers = userRepository.findAll(pageable).map(mapper::toResponseDto);
        log.info("Get all users pages: {}", allUsers.getTotalPages());
        return allUsers;
    }

    @Override
    public UserResponseDto getById(Long id) {
        User user = getUserEntityById(id);
        UserResponseDto responseDto = mapper.toResponseDto(user);
        log.info("Get userResponse {} by id: {}", user.getUsername(), id);
        return responseDto;
    }

    @Override
    public UserResponseDto updateRole(Long id, UpdateUserRoleDto dto) {
        User user = getUserEntityById(id);
        validateUserRole(user.getRole().getCode(), dto.getRole());
        user.setRole(getUserRole(dto.getRole()));
        User updatedUser = userRepository.save(user);
        UserResponseDto responseDto = mapper.toResponseDto(updatedUser);
        log.info("Update user {} role: {}", user.getUsername(), responseDto.getRoleName());
        return responseDto;
    }

    @Override
    public UserResponseDto toggleLockUser(Long id) {
        User user = getUserEntityById(id);
        user.setAccountNonLocked(!user.isAccountNonLocked());
        User updatedUser = userRepository.save(user);
        UserResponseDto responseDto = mapper.toResponseDto(updatedUser);
        log.info("Toggle user {} lock  {}", user.getUsername(), user.isAccountNonLocked());
        return responseDto;
    }

    @Override
    public UserResponseDto toggleDisableUser(Long id) {
        User user = getUserEntityById(id);
        user.setEnabled(!user.isEnabled());
        User updatedUser = userRepository.save(user);
        UserResponseDto responseDto = mapper.toResponseDto(updatedUser);
        log.info("User {} set enable {}", user.getUsername(), user.isEnabled());
        return responseDto;
    }

    @Override
    public void delete(Long id) {
        User user = getUserEntityById(id);
        userRepository.delete(user);
    }

    @Override
    public User getUserEntityById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с идентификатором %s не найден", id)));
        log.info("Get userEntity {} by id: {}", user.getUsername(), id);
        return user;
    }

    @Override
    public User getUserEntityByLogin(String login) {
        User user = userRepository.findByLoginWithRole(login)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с логином %s не найден", login)));
        log.info("Get user {} by login: {}", user.getUsername(), login);
        return user;
    }

    public UserRole getUserRole(UserRoleCode code) {
        UserRole role = roleRepository.findByCode(code)
                .orElseThrow(() -> new UserRoleNotFoundException(String.format("Роль пользователя по коду %s не найдена", code)));
        log.info("Get user role {} by code: {}", role.getCode(), code);
        return role;
    }

    @Override
    public User createUserEntity(User rawUser) {
        User preparedUser = prepareNewUserForSave(rawUser, rawUser.getRole());
        User user = userRepository.save(preparedUser);
        log.info("Created user {} from raw", user.getUsername());
        return user;
    }

    @Override
    public boolean existByUserLogin(String login) {
        boolean isExist = userRepository.findByLoginWithRole(login).isPresent();
        log.info("Check is user {} exist by login: {}", login, isExist);
        return isExist;
    }

    private User prepareNewUserForSave(User user, UserRole userRole) {
        if (userRole == null) user.setRole(getRole(UserRoleCode.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Prepared user {}, encoded password, and check role {}", user.getUsername(), user.getRole().getCode().name());
        return user;
    }

    private void validateUserRole(UserRoleCode code, UserRoleCode codeChange) {
        if (code.equals(codeChange)) {
            throw new UserRoleSameException(String.format("Пользователи имеют одинаковый код %s", code));
        }
    }

    private UserRole getRole(UserRoleCode roleCode) {
        UserRole userRole = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new UserRoleNotFoundException(String.format("Роль пользователя по коду %s не найдена", roleCode.name())));
        log.info("Get role {} by role code {}}", userRole.getCode(), roleCode.name());
        return userRole;
    }
}
