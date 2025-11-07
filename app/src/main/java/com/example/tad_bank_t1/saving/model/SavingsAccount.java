package com.example.tad_bank_t1.saving.model;

public class SavingsAccount {
    private String id;
    private String accountNumber;
    private long balance;              // VNĐ
    private String currency;           // "VND"
    private String startDate;          // "yyyy-MM-dd"
    private String maturityDate;       // "yyyy-MM-dd"
    private double interestRate;       // 6.5
    private PaymentMethod paymentMethod;
    private AccountStatus status;

    public SavingsAccount() {
    }

    public SavingsAccount(String id, String accountNumber, long balance, String currency,
                         String startDate, String maturityDate, double interestRate,
                         PaymentMethod paymentMethod, AccountStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.interestRate = interestRate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}

