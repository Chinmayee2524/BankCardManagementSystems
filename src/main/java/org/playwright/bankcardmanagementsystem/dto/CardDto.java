package org.playwright.bankcardmanagementsystem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardDto {

    private Long id;

    private String cardNumber;

    private String cardType;

    private String cardholder;

    private String status;

    private LocalDate expiryDate;

    private BigDecimal creditLimit;

    private BigDecimal availableLimit;
}