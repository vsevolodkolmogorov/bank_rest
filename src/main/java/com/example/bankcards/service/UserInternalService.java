package com.example.bankcards.service;

import com.example.bankcards.entity.User;

public interface UserInternalService {
    User getUserEntityById(Long id);
    User getUserEntityByEmail(String email);
    User createUserEntity(User user);
    boolean existByUserEmail(String email);
}
