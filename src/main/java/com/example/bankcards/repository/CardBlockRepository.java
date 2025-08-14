package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardBlockRepository extends JpaRepository<CardBlock, Long> {
    Optional<CardBlock> findCardBlockById(Long id);
    Optional<CardBlock> findCardBlockByIdAndRequestedByLogin(Long id, String login);
    Page<CardBlock> findAllByRequestedByLogin(String login, Pageable pageable);
}
