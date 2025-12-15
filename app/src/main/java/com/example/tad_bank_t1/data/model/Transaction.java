package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private String transactionId;      // PK
    private String accountId;          // FK -> Accounts.accountId
    private String accountNumber;
    private String accountName;
    private TxnType type;
    private TnxStatus status;
    private TxnChannel channel;
    private Long amount;
    private String currency;           // default VND
    private String description;
    // giữ chính tả "chanel" theo sơ đồ
    private String parentId;               // refund
    private String counterpartyAccount;
    private String counterpartyName;
    private String counterpartyBankCode;
    private String counterpartyBankName;
    private String counterpartyBankLogo;
    private String branchId;               // nếu rút tại CN/ATM
    private Long feeAmount;
    private String billId;                 // link Bill
    private String paymentId;              // link Payment
    private String scheduleId;             // link MortgagePaymentSchedule
    private boolean otpRequired;
    private Date otpVerifiedAt;
    private String idempotencyKey;
    private String transactionReference;
    private Date createdAt, updatedAt;

    public Transaction() {
    }

    // ======= Builder-based private constructor =======
    private Transaction(Builder builder) {
        this.transactionId = builder.transactionId;
        this.parentId = builder.parentId;

        this.accountId = builder.accountId;
        this.accountNumber = builder.accountNumber;
        this.accountName = builder.accountName;

        this.status = builder.status;
        this.type = builder.type;
        this.channel = builder.channel;

        this.branchId = builder.branchId;

        this.amount = builder.amount;
        this.feeAmount = builder.feeAmount;
        this.currency = builder.currency;

        this.description = builder.description;

        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updateAt != null ? builder.updateAt : new Date();

        this.counterpartyAccount = builder.counterpartyAccount;
        this.counterpartyName = builder.counterpartyName;
        this.counterpartyBankCode = builder.counterpartyBankCode;
        this.counterpartyBankName = builder.counterpartyBankName;
        this.counterpartyBankLogo = builder.counterpartyBankLogo;

        this.billId = builder.billId;

        this.paymentId = builder.paymentId;

        this.scheduleId = builder.scheduleId;

        this.otpRequired = builder.otpRequired;
        this.otpVerifiedAt = builder.otpVerifiedAt;

        this.idempotencyKey = builder.idempotencyKey;
        this.transactionReference = builder.transactionReference;
    }

    // Builder pattern
    public static  Builder builder(){
        return new Builder();
    }

    // ===== get set =====

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public TxnType getType() {
        return type;
    }

    public TnxStatus getStatus() {
        return status;
    }

    public TxnChannel getChannel() {
        return channel;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getParentId() {
        return parentId;
    }

    public String getCounterpartyAccount() {
        return counterpartyAccount;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public String getCounterpartyBankCode() {
        return counterpartyBankCode;
    }

    public String getCounterpartyBankName() {
        return counterpartyBankName;
    }

    public String getCounterpartyBankLogo() {
        return counterpartyBankLogo;
    }

    public String getBranchId() {
        return branchId;
    }

    public Long getFeeAmount() {
        return feeAmount;
    }

    public String getBillId() {
        return billId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public boolean isOtpRequired() {
        return otpRequired;
    }

    public Date getOtpVerifiedAt() {
        return otpVerifiedAt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setType(TxnType type) {
        this.type = type;
    }

    public void setStatus(TnxStatus status) {
        this.status = status;
    }

    public void setChannel(TxnChannel channel) {
        this.channel = channel;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setCounterpartyAccount(String counterpartyAccount) {
        this.counterpartyAccount = counterpartyAccount;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public void setCounterpartyBankCode(String counterpartyBankCode) {
        this.counterpartyBankCode = counterpartyBankCode;
    }

    public void setCounterpartyBankName(String counterpartyBankName) {
        this.counterpartyBankName = counterpartyBankName;
    }

    public void setCounterpartyBankLogo(String counterpartyBankLogo) {
        this.counterpartyBankLogo = counterpartyBankLogo;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public void setFeeAmount(Long feeAmount) {
        this.feeAmount = feeAmount;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setOtpRequired(boolean otpRequired) {
        this.otpRequired = otpRequired;
    }

    public void setOtpVerifiedAt(Date otpVerifiedAt) {
        this.otpVerifiedAt = otpVerifiedAt;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ===== end get set =====

    // ======= Builder class =======
    public static class Builder {
        private String transactionId;
        private String accountId;
        private String accountNumber, accountName;
        private TxnType type;
        private TnxStatus status;
        private TxnChannel channel;
        private Long amount;
        private String currency = "VND";   // default
        private String description;
        private Date createdAt, updateAt;
        private String parentId;
        private String counterpartyAccount;
        private String counterpartyName;
        private String counterpartyBankCode, counterpartyBankName, counterpartyBankLogo;
        private String branchId;
        private Long feeAmount;
        private String billId;
        private String paymentId;
        private String scheduleId;
        private boolean otpRequired;
        private Date otpVerifiedAt;
        private String idempotencyKey;
        private String transactionReference;


        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder accountName(String accountName) {
            this.accountName = accountName;
            return this;
        }

        public Builder type(TxnType type) {
            this.type = type;
            return this;
        }

        public Builder status(TnxStatus status) {
            this.status = status;
            return this;
        }

        public Builder channel(TxnChannel channel) {
            this.channel = channel;
            return this;
        }

        public Builder amount(Long amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updateAt(Date updateAt) {
            this.updateAt = updateAt;
            return this;
        }


        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder counterpartyAccount(String counterpartyAccount) {
            this.counterpartyAccount = counterpartyAccount;
            return this;
        }

        public Builder counterpartyName(String counterpartyName) {
            this.counterpartyName = counterpartyName;
            return this;
        }

        public Builder counterpartyBankCode(String counterpartyBankCode) {
            this.counterpartyBankCode = counterpartyBankCode;
            return this;
        }

        public Builder counterpartyBankName(String counterpartyBankName) {
            this.counterpartyBankName = counterpartyBankName;
            return this;
        }

        public Builder counterpartyBankLogo(String counterpartyBankLogo) {
            this.counterpartyBankLogo = counterpartyBankLogo;
            return this;
        }

        public Builder branchId(String branchId) {
            this.branchId = branchId;
            return this;
        }

        public Builder feeAmount(Long feeAmount) {
            this.feeAmount = feeAmount;
            return this;
        }

        public Builder billId(String billId) {
            this.billId = billId;
            return this;
        }

        public Builder paymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder scheduleId(String scheduleId) {
            this.scheduleId = scheduleId;
            return this;
        }

        public Builder otpRequired(boolean otpRequired) {
            this.otpRequired = otpRequired;
            return this;
        }

        public Builder otpVerifiedAt(Date otpVerifiedAt) {
            this.otpVerifiedAt = otpVerifiedAt;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder transactionReference(String transactionReference) {
            this.transactionReference = transactionReference;
            return this;
        }

        public Transaction build() {
            if (this.createdAt == null) {
                this.createdAt = new Date();
                this.updateAt = this.createdAt;
            }
            return new Transaction(this);
        }
    }


    // to Map
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("transactionId", transactionId);
        result.put("accountId", accountId);
        result.put("accountNumber", accountNumber);
        result.put("accountName", accountName);
        result.put("type", type);
        result.put("status", status);
        result.put("channel", channel);
        result.put("amount", amount);
        result.put("currency", currency);
        result.put("description", description);
        result.put("createdAt", createdAt);
        result.put("parentId", parentId);
        result.put("counterpartyAccount", counterpartyAccount);
        result.put("counterpartyName", counterpartyName);
        result.put("counterpartyBankCode", counterpartyBankCode);
        result.put("counterpartyBankName", counterpartyBankName);
        result.put("counterpartyBankLogo", counterpartyBankLogo);
        result.put("branchId", branchId);
        result.put("feeAmount", feeAmount);
        result.put("billId", billId);
        result.put("paymentId", paymentId);
        result.put("scheduleId", scheduleId);
        result.put("otpRequired", otpRequired);
        result.put("otpVerifiedAt", otpVerifiedAt);
        result.put("idempotencyKey", idempotencyKey);
        result.put("transactionReference", transactionReference);
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountName='" + accountName + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", channel=" + channel +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", parentId='" + parentId + '\'' +
                ", counterpartyAccount='" + counterpartyAccount + '\'' +
                ", counterpartyName='" + counterpartyName + '\'' +
                ", counterpartyBankCode='" + counterpartyBankCode + '\'' +
                ", branchId='" + branchId + '\'' +
                ", feeAmount=" + feeAmount +
                ", billId='" + billId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", scheduleId='" + scheduleId + '\'' +
                ", otpRequired=" + otpRequired +
                ", otpVerifiedAt=" + otpVerifiedAt +
                ", idempotencyKey='" + idempotencyKey + '\'' +
                ", transactionReference='" + transactionReference + '\'' +
                '}';
    }
}
