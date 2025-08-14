package com.example.bankcards.service;

import com.example.bankcards.entity.User;

public interface UserInternalService {
    User getUserEntityById(Long id);
    User getUserEntityByLogin(String login);
    User createUserEntity(User user);
    boolean existByUserLogin(String login);
}
