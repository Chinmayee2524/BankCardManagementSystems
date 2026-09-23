package org.playwright.bankcardmanagementsystem.service;



import lombok.RequiredArgsConstructor;
import org.playwright.bankcardmanagementsystem.dto.DashboardResponse;
import org.playwright.bankcardmanagementsystem.dto.TransactionSummaryDto;
import org.playwright.bankcardmanagementsystem.entity.Card;
import org.playwright.bankcardmanagementsystem.entity.CardStatus;
import org.playwright.bankcardmanagementsystem.entity.Customer;
import org.playwright.bankcardmanagementsystem.entity.Transaction;
import org.playwright.bankcardmanagementsystem.repository.CardRepository;
import org.playwright.bankcardmanagementsystem.repository.CustomerRepository;
import org.playwright.bankcardmanagementsystem.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    public DashboardResponse getDashboard(Long userId)
            throws InterruptedException {

        Thread.sleep(500);

        Customer customer = customerRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        long totalCards =
                cardRepository.countByCustomerId(customer.getId());

        long activeCards =
                cardRepository.countByCustomerIdAndStatus(
                        customer.getId(),
                        CardStatus.ACTIVE);

        long blockedCards =
                cardRepository.countByCustomerIdAndStatus(
                        customer.getId(),
                        CardStatus.BLOCKED);

        List<Card> cards =
                cardRepository.findByCustomerId(customer.getId());

        BigDecimal availableCredit =
                cards.stream()
                        .map(Card::getAvailableLimit)
                        .reduce(BigDecimal.ZERO,
                                BigDecimal::add);

        List<TransactionSummaryDto> transactions =
                transactionRepository
                        .findTop5ByCardCustomerIdOrderByTransactionDateDesc(
                                customer.getId())
                        .stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());

        return new DashboardResponse(
                customer.getFullName(),
                totalCards,
                activeCards,
                blockedCards,
                availableCredit,
                transactions
        );
    }

    private TransactionSummaryDto convertToDto(
            Transaction transaction) {

        return new TransactionSummaryDto(
                transaction.getId(),
                transaction.getMerchant(),
                transaction.getAmount(),
                transaction.getTransactionDate()
        );
    }
}