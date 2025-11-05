package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Account implements Serializable {
    @DocumentId
    public String accountId;
    public String userId;
    public String pinCode;
    public String accountName;
    public String accountNumber;

    public String branchId;
    public AccountType type;
    public String currency;
    public  long balance;
    public AccountStatus status;
    public Date createdAt;
    private Date closedAt;
    private Date updatedAt;

    public Account() {}
    public Account(String id,
                   String userId,
                   String pinCode,
                   String accountName,
                   String accountNumber,
                   String branchId,
                   AccountType type,
                   String currency,
                   Long balance,
                   AccountStatus status,
                   Date createdAt,
                   Date closedAt,
                   Date updatedAt) {
        this.accountId = id;
        this.userId = userId;
        this.pinCode = pinCode;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.branchId = branchId;
        this.type = type;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.updatedAt = updatedAt;
    }
    @Exclude
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
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

    public Date getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Date closedAt) {
        this.closedAt = closedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("pinCode", pinCode);
        map.put("accountName", accountName);
        map.put("accountNumber", accountNumber);
        map.put("branchId", branchId);
        map.put("type", type);
        map.put("currency", currency);
        map.put("balance", balance);
        map.put("status", status);
        map.put("createdAt", createdAt);
        map.put("closedAt", closedAt);
        map.put("updatedAt", updatedAt);
        return map;
    }
}
