package com.example.tad_bank_t1.data.model.enums;

public enum TxnType {
    // Incoming
    TRANSFER_RECEIVE,
    INTEREST,
    SAVING_DEPOSIT,
    REFUND,
    SALARY,

    // Outgoing
    TRANSFER_INTERNAL,
    TRANSFER_EXTERNAL,
    ATM_WITHDRAWAL,
    BILL_PAYMENT,
    MOBILE_TOPUP,
    MERCHANT_PAYMENT,

    // Other
    LOAN_PAYMENT,
    FEE
}
