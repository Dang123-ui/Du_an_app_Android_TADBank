package com.example.tad_bank_t1.saving.model;

public class SavingsAccountDetail {
    private String id;
    private String accountNumber;
    private String branch;
    private long balance;
    private String currency;
    private String startDate;
    private String maturityDate;
    private double interestRate;
    private String policyName;
    private String termLength;
    private String interestPaymentMethod;
    private long estimatedInterest;
    private long totalAtMaturity;
    private AccountStatus status;

    public SavingsAccountDetail() {
    }

    public SavingsAccountDetail(String id, String accountNumber, String branch, long balance,
                                String currency, String startDate, String maturityDate,
                                double interestRate, String policyName, String termLength,
                                String interestPaymentMethod, long estimatedInterest,
                                long totalAtMaturity, AccountStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.balance = balance;
        this.currency = currency;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.interestRate = interestRate;
        this.policyName = policyName;
        this.termLength = termLength;
        this.interestPaymentMethod = interestPaymentMethod;
        this.estimatedInterest = estimatedInterest;
        this.totalAtMaturity = totalAtMaturity;
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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
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

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getTermLength() {
        return termLength;
    }

    public void setTermLength(String termLength) {
        this.termLength = termLength;
    }

    public String getInterestPaymentMethod() {
        return interestPaymentMethod;
    }

    public void setInterestPaymentMethod(String interestPaymentMethod) {
        this.interestPaymentMethod = interestPaymentMethod;
    }

    public long getEstimatedInterest() {
        return estimatedInterest;
    }

    public void setEstimatedInterest(long estimatedInterest) {
        this.estimatedInterest = estimatedInterest;
    }

    public long getTotalAtMaturity() {
        return totalAtMaturity;
    }

    public void setTotalAtMaturity(long totalAtMaturity) {
        this.totalAtMaturity = totalAtMaturity;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}

