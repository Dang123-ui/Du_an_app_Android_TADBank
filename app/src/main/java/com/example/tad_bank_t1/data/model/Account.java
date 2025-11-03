package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;

import java.util.Date;

public class Account {
    private String accountId;     // PK
    private String userId;        // FK -> Users.userId
    private String branchId;      // FK -> Branches.brandId
    private AccountType type;          // CHECKING | SAVING | MORTGAGE
    private String accountName;
    private String accountNumber; // unique
    private boolean isDefault;
    private String currency;      // default VND
    private Double balance;       // decimal(10,3)
    private AccountStatus status;       // OPEN | FROZEN | CLOSE
    private Date createdAt;
    private Date updateAt;

    public Account() {
    }

    public Account(String accountId, String userId, String branchId, AccountType type, String accountName, String accountNumber, boolean isDefault, String currency, Double balance, AccountStatus status, Date createdAt, Date updateAt) {
        this.accountId = accountId;
        this.userId = userId;
        this.branchId = branchId;
        this.type = type;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.isDefault = isDefault;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}
