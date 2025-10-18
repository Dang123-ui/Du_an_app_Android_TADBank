package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;

import java.util.Date;

public class Transaction {
    public Long       transactionId;      // PK
    public Long       accountId;          // FK -> Accounts.accountId
    public TxnType type;
    public TnxStatus status;
    public TxnChannel channel;
    public Double     amount;
    public String     currency;           // default VND
    public String     description;
        // giữ chính tả "chanel" theo sơ đồ
    public Date createAt;

    public Long   parentId;               // refund
    public String counterpartyAccount;
    public String counterpartyName;
    public String counterpartyBankCode;
    public Long   branchId;               // nếu rút tại CN/ATM
    public Double feeAmount;

    public Long   billId;                 // link Bill
    public Long   paymentId;              // link Payment
    public Long   scheduleId;             // link MortgagePaymentSchedule

    public boolean otpRequired;
    public Date    otpVerifiedAt;

    public Transaction() {}

    public Transaction(Long transactionId, Long accountId, TxnType type, TnxStatus status, TxnChannel channel, Double amount, String description, Date createAt, String counterpartyAccount, String counterpartyName, String counterpartyBankCode, Double feeAmount) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.status = status;
        this.channel = channel;
        this.amount = amount;
        this.description = description;
        this.createAt = createAt;
        this.counterpartyAccount = counterpartyAccount;
        this.counterpartyName = counterpartyName;
        this.counterpartyBankCode = counterpartyBankCode;
        this.feeAmount = feeAmount;
    }

    public Transaction(Long transactionId, Long accountId, TxnType type, TnxStatus status, TxnChannel channel, Double amount, String currency, String description, Date createAt, Long parentId, String counterpartyAccount, String counterpartyName, String counterpartyBankCode, Long branchId, Double feeAmount, Long billId, Long paymentId, Long scheduleId, boolean otpRequired, Date otpVerifiedAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.status = status;
        this.channel = channel;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.createAt = createAt;
        this.parentId = parentId;
        this.counterpartyAccount = counterpartyAccount;
        this.counterpartyName = counterpartyName;
        this.counterpartyBankCode = counterpartyBankCode;
        this.branchId = branchId;
        this.feeAmount = feeAmount;
        this.billId = billId;
        this.paymentId = paymentId;
        this.scheduleId = scheduleId;
        this.otpRequired = otpRequired;
        this.otpVerifiedAt = otpVerifiedAt;
    }



    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public TxnType getType() {
        return type;
    }

    public void setType(TxnType type) {
        this.type = type;
    }

    public TnxStatus getStatus() {
        return status;
    }

    public void setStatus(TnxStatus status) {
        this.status = status;
    }

    public TxnChannel getChannel() {
        return channel;
    }

    public void setChannel(TxnChannel channel) {
        this.channel = channel;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCounterpartyAccount() {
        return counterpartyAccount;
    }

    public void setCounterpartyAccount(String counterpartyAccount) {
        this.counterpartyAccount = counterpartyAccount;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public String getCounterpartyBankCode() {
        return counterpartyBankCode;
    }

    public void setCounterpartyBankCode(String counterpartyBankCode) {
        this.counterpartyBankCode = counterpartyBankCode;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public boolean isOtpRequired() {
        return otpRequired;
    }

    public void setOtpRequired(boolean otpRequired) {
        this.otpRequired = otpRequired;
    }

    public Date getOtpVerifiedAt() {
        return otpVerifiedAt;
    }

    public void setOtpVerifiedAt(Date otpVerifiedAt) {
        this.otpVerifiedAt = otpVerifiedAt;
    }
}
