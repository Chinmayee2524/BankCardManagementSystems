package org.playwright.bankcardmanagementsystem.seed;

import lombok.RequiredArgsConstructor;
import org.playwright.bankcardmanagementsystem.entity.*;
import org.playwright.bankcardmanagementsystem.repository.CardRepository;
import org.playwright.bankcardmanagementsystem.repository.CustomerRepository;
import org.playwright.bankcardmanagementsystem.repository.TransactionRepository;
import org.playwright.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.count() > 0) {
            return;
        }

        // ADMIN USER
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);

        userRepository.save(admin);

        // SUPPORT USER
        User support = new User();
        support.setEmail("support@test.com");
        support.setPassword(passwordEncoder.encode("Support@123"));
        support.setRole(Role.SUPPORT_AGENT);
        support.setEnabled(true);

        userRepository.save(support);

        // CUSTOMER USER
        User customerUser = new User();
        customerUser.setEmail("customer@test.com");
        customerUser.setPassword(passwordEncoder.encode("Customer@123"));
        customerUser.setRole(Role.CUSTOMER);
        customerUser.setEnabled(true);

        customerUser = userRepository.save(customerUser);

        // CUSTOMER PROFILE
        Customer customer = new Customer();
        customer.setFullName("John Doe");
        customer.setPhone("9876543210");
        customer.setAddress("Baner");
        customer.setCity("Pune");
        customer.setPincode("411045");
        customer.setUser(customerUser);

        customer = customerRepository.save(customer);

        // CARD 1
        Card card1 = new Card();
        card1.setCardNumber("XXXX-1234");
        card1.setCardType(CardType.CREDIT);
        card1.setStatus(CardStatus.ACTIVE);
        card1.setExpiryDate(LocalDate.of(2028, 12, 31));
        card1.setCreditLimit(new BigDecimal("100000"));
        card1.setAvailableLimit(new BigDecimal("75000"));
        card1.setCustomer(customer);

        card1 = cardRepository.save(card1);

        // CARD 2
        Card card2 = new Card();
        card2.setCardNumber("XXXX-5678");
        card2.setCardType(CardType.DEBIT);
        card2.setStatus(CardStatus.BLOCKED);
        card2.setExpiryDate(LocalDate.of(2027, 8, 31));
        card2.setCreditLimit(new BigDecimal("50000"));
        card2.setAvailableLimit(new BigDecimal("50000"));
        card2.setCustomer(customer);

        cardRepository.save(card2);

        // TRANSACTION 1
        Transaction transaction1 = new Transaction();
        transaction1.setMerchant("Amazon");
        transaction1.setAmount(new BigDecimal("2500"));
        transaction1.setTransactionDate(
                LocalDateTime.now().minusDays(1));
        transaction1.setCard(card1);

        transactionRepository.save(transaction1);

        // TRANSACTION 2
        Transaction transaction2 = new Transaction();
        transaction2.setMerchant("Flipkart");
        transaction2.setAmount(new BigDecimal("1200"));
        transaction2.setTransactionDate(
                LocalDateTime.now().minusDays(2));
        transaction2.setCard(card1);

        transactionRepository.save(transaction2);

        // TRANSACTION 3
        Transaction transaction3 = new Transaction();
        transaction3.setMerchant("Swiggy");
        transaction3.setAmount(new BigDecimal("800"));
        transaction3.setTransactionDate(
                LocalDateTime.now().minusDays(3));
        transaction3.setCard(card1);
    }
}
