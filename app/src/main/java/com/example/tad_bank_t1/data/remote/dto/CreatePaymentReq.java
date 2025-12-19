package com.example.tad_bank_t1.data.remote.dto;

import java.util.Map;

public class CreatePaymentReq {

    // ===== Identity =====
    public String transactionId;    // required
    public String paymentId;        // optional: null => backend gen
    public String idempotencyKey;   // optional nhưng rất nên có (chống bấm 2 lần)

    // ===== Who pays =====
    public String userId;              // user hiện tại
    public String payerAccountId;      // tài khoản trừ tiền (internal account)
    public String payerAccountNumber;  // để hiển thị/trace (backend vẫn nên verify lại)
    public String payerAccountName;

    // ===== What are we paying for =====
    public String purpose;   // "BILL" | "TOPUP" | "TICKET" | "HOTEL" | "ECOMMERCE"
    public String billId;    // nếu là hóa đơn điện/nước/học phí
    public String orderId;   // nếu là vé/ecommerce/hotel (1 trong billId/orderId có thể null)

    // ===== Money =====
    public long amount;        // VND (đồng) - backend nên override theo DB (bill/order)
    public String currency;    // default "VND"
    public Long feeAmount;     // optional

    // ===== VNPay options =====
    public String orderInfo;   // mô tả hiển thị trên VNPay
    public String locale;      // "vn"/"en"
    public String bankCode;    // optional

    // ===== Extra info (tuỳ dịch vụ) =====
    // VD: customerCode điện/nước, providerCode, phoneNumber topup, seat/movieId...
    public Map<String, String> metadata;
}