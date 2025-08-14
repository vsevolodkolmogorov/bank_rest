package com.example.bankcards.repository;

import com.example.bankcards.entity.RequestStatus;
import com.example.bankcards.entity.enums.RequestStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestStatusRepository extends JpaRepository<RequestStatus, Long> {
    Optional<RequestStatus> findByCode(RequestStatusCode code);
}
