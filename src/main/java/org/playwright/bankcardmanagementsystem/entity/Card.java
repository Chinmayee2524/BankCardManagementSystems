package org.playwright.bankcardmanagementsystem.entity;



import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "card_number", nullable = false, unique = true)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
   private CardType cardType;

    @Enumerated (EnumType.STRING)
    @Column (name = "status", nullable = false)
    private CardStatus status;
    @Column(name = "expiry_date",nullable = false)
    private LocalDate expiryDate;

    @Column(name= "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "available_limit", precision = 15, scale = 2)
    private BigDecimal availableLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

  public Card() {
    }

    public Card(Long id,
            String cardNumber,
            CardType cardType,
            CardStatus status,
            LocalDate expiryDate,
            BigDecimal creditLimit,
                BigDecimal availableLimit,
              Customer customer) {
          this.id = id;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.status = status;
        this.expiryDate = expiryDate;
        this.creditLimit = creditLimit;
        this.availableLimit = availableLimit;
        this.customer = customer;
    }

    public Long getId() {
       return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(BigDecimal availableLimit) {
        this.availableLimit = availableLimit;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}