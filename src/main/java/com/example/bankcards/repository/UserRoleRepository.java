package com.example.bankcards.repository;

import com.example.bankcards.entity.UserRole;
import com.example.bankcards.entity.enums.UserRoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByCode(UserRoleCode code);
}
