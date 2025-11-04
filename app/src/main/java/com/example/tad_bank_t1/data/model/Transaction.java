package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;

import java.util.Date;

public class Transaction {
    private String transactionId;      // PK
    private String accountId;          // FK -> Accounts.accountId
    private TxnType type;
    private TnxStatus status;
    private TxnChannel channel;
    private Double amount;
    private String currency;           // default VND
    private String description;
    // giữ chính tả "chanel" theo sơ đồ
    private Date createdAt;
    private String parentId;               // refund
    private String counterpartyAccount;
    private String counterpartyName;
    private String counterpartyBankCode;
    private String branchId;               // nếu rút tại CN/ATM
    private Double feeAmount;
    private String billId;                 // link Bill
    private String paymentId;              // link Payment
    private String scheduleId;             // link MortgagePaymentSchedule
    private boolean otpRequired;
    private Date otpVerifiedAt;

    public Transaction() {
    }

    public Transaction(String transactionId, String accountId, TxnType type, TnxStatus status,
                       TxnChannel channel, Double amount, String description, Date createAt,
                       String counterpartyAccount, String counterpartyName,
                       String counterpartyBankCode, Double feeAmount) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.status = status;
        this.channel = channel;
        this.amount = amount;
        this.description = description;
        this.createdAt = createAt;
        this.counterpartyAccount = counterpartyAccount;
        this.counterpartyName = counterpartyName;
        this.counterpartyBankCode = counterpartyBankCode;
        this.feeAmount = feeAmount;
    }

    public Transaction(String transactionId, String accountId, TxnType type,
                       TnxStatus status, TxnChannel channel, Double amount,
                       String currency, String description, Date createAt,
                       String parentId, String counterpartyAccount, String counterpartyName,
                       String counterpartyBankCode, String branchId, Double feeAmount,
                       String billId, String paymentId, String scheduleId, boolean otpRequired, Date otpVerifiedAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.status = status;
        this.channel = channel;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.createdAt = createAt;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
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

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
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
