package org.playwright.bankcardmanagementsystem.dto;


import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private String customerName;

    private long totalCards;

    private long activeCards;

    private long blockedCards;

    private BigDecimal availableCredit;

    private List<TransactionSummaryDto> recentTransactions;

    public DashboardResponse() {
    }

    public DashboardResponse(String customerName,
                             long totalCards,
                             long activeCards,
                             long blockedCards,
                             BigDecimal availableCredit,
                             List<TransactionSummaryDto> recentTransactions) {
        this.customerName = customerName;
        this.totalCards = totalCards;
        this.activeCards = activeCards;
        this.blockedCards = blockedCards;
        this.availableCredit = availableCredit;
        this.recentTransactions = recentTransactions;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(long totalCards) {
        this.totalCards = totalCards;
    }

    public long getActiveCards() {
        return activeCards;
    }

    public void setActiveCards(long activeCards) {
        this.activeCards = activeCards;
    }

    public long getBlockedCards() {
        return blockedCards;
    }

    public void setBlockedCards(long blockedCards) {
        this.blockedCards = blockedCards;
    }

    public BigDecimal getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(BigDecimal availableCredit) {
        this.availableCredit = availableCredit;
    }

    public List<TransactionSummaryDto> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionSummaryDto> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}