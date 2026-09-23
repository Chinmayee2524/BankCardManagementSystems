package org.playwright.bankcardmanagementsystem.dto;



import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSummaryDto {

    private Long transactionId;
    private String merchant;
    private BigDecimal amount;
    private LocalDateTime transactionDate;

    public TransactionSummaryDto() {
    }

    public TransactionSummaryDto(Long transactionId,
                                 String merchant,
                                 BigDecimal amount,
                                 LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.merchant = merchant;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
