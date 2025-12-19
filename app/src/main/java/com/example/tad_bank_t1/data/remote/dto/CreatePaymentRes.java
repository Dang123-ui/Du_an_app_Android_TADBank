package com.example.tad_bank_t1.data.remote.dto;

public class CreatePaymentRes {
    public String paymentId;          // internal id
    public String transactionId;      // id của giao dịch trong hệ thống
    public String providerOrderRef;          // vnp_TxnRef (orderId gửi VNPay) - thường = paymentId
    public String paymentUrl;         // URL mở WebView/CustomTabs
    public long expiredAt;            // optional (epoch millis) để app biết timeout
}

