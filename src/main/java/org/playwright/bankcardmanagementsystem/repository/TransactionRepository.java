package org.playwright.bankcardmanagementsystem.repository;



import org.playwright.bankcardmanagementsystem.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop5ByCardCustomerIdOrderByTransactionDateDesc(Long customerId);
}
