package org.playwright.bankcardmanagementsystem.repository;

import org.playwright.bankcardmanagementsystem.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import org.playwright.bankcardmanagementsystem.entity.CardStatus;


import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findByCustomerId(Long customerId);

    long countByCustomerId(Long customerId);

    long countByCustomerIdAndStatus(Long customerId, CardStatus status);
}