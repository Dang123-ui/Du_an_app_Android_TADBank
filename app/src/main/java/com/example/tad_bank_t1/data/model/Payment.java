package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.paymentProvider.PaymentProvider;
import com.example.tad_bank_t1.data.model.enums.paymentProvider.PaymentPurpose;
import com.example.tad_bank_t1.data.model.enums.paymentProvider.PaymentStatus;

import java.util.Date;

public class Payment {

    // ====== Identity & Link ======
    private String paymentId;        // PK internal
    private String userId;           // optional
    private String accountId;        // account nguồn (nếu pay từ bank account)
    private String accountNumber;
    private String accountName;
    private String billId;           // hóa đơn điện/nước... (nullable)
    private String orderId;          // đơn hàng vé/hotel/ecom... (nullable)
    private String transactionId;    // link Transaction (set khi SUCCESS)
    private String idempotencyKey;   // chống tạo payment trùng

    // ====== Payment Info ======
    private PaymentProvider provider;  // VNPAY / STRIPE
    private PaymentPurpose purpose;    // BILL/TOPUP/TICKET/HOTEL/ECOMMERCE/OTHER
    private PaymentStatus status;      // PENDING/SUCCESS/FAILED/CANCELED/EXPIRED

    private Long amount;              // đơn vị đồng (VND) hoặc theo currency
    private Long feeAmount;           // phí (nullable)
    private String currency;          // "VND" default
    private String description;       // mô tả hiển thị cho user

    // ====== Provider References (quan trọng) ======
    private String providerOrderRef;      // "orderId" gửi sang cổng (VD: vnp_TxnRef hoặc stripe_payment_intent_id)
    private String providerTransactionNo; // mã giao dịch do cổng trả về (VD: vnp_TransactionNo)
    private String providerResponseCode;  // code kết quả (VD: vnp_ResponseCode)
    private String providerStatus;        // status text (optional: vnp_TransactionStatus, stripe status)

    // ====== Security ======
    private boolean otpRequired;
    private Date otpVerifiedAt;

    // ====== Audit ======
    private Date createdAt;
    private Date updatedAt;
    private Date expiredAt;

    public Payment() {
        // required empty constructor for Firebase/Firestore
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public PaymentPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(PaymentPurpose purpose) {
        this.purpose = purpose;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Long feeAmount) {
        this.feeAmount = feeAmount;
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

    public String getProviderOrderRef() {
        return providerOrderRef;
    }

    public void setProviderOrderRef(String providerOrderRef) {
        this.providerOrderRef = providerOrderRef;
    }

    public String getProviderTransactionNo() {
        return providerTransactionNo;
    }

    public void setProviderTransactionNo(String providerTransactionNo) {
        this.providerTransactionNo = providerTransactionNo;
    }

    public String getProviderResponseCode() {
        return providerResponseCode;
    }

    public void setProviderResponseCode(String providerResponseCode) {
        this.providerResponseCode = providerResponseCode;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    public void setProviderStatus(String providerStatus) {
        this.providerStatus = providerStatus;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", userId='" + userId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountName='" + accountName + '\'' +
                ", billId='" + billId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", idempotencyKey='" + idempotencyKey + '\'' +
                ", provider=" + provider +
                ", purpose=" + purpose +
                ", status=" + status +
                ", amount=" + amount +
                ", feeAmount=" + feeAmount +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                ", providerOrderRef='" + providerOrderRef + '\'' +
                ", providerTransactionNo='" + providerTransactionNo + '\'' +
                ", providerResponseCode='" + providerResponseCode + '\'' +
                ", providerStatus='" + providerStatus + '\'' +
                ", otpRequired=" + otpRequired +
                ", otpVerifiedAt=" + otpVerifiedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
