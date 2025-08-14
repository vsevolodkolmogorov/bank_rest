package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    Optional<Card> findCardById(Long id);
    Optional<Card> findCardByCardNumberEncrypted(String cardNumber);
    Optional<Card> findCardByIdAndUserLogin(Long id, String login);
    Page<Card> findAllByUserLogin(String login,Pageable pageable);
}
