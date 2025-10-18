package com.example.tad_bank_t1.data.model.enums;

public enum TxnType {
    // Incoming
    RECEIVE_TRANSFER,
    INTEREST,
    REFUND,
    SALARY,

    // Outgoing
    TRANSFER_INTERNAL,
    TRANSFER_EXTERNAL,
    WITHDRAW,
    BILL_PAYMENT,
    TOPUP,
    MERCHANT_PAYMENT,

    // Other
    LOAN_PAYMENT,
    FEE
}
