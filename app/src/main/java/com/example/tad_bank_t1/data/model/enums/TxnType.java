package com.example.tad_bank_t1.data.model.enums;

public enum TxnType {
    // Incoming
    TRANSFER_INTERNAL_INCOMING,
    SAVING_INTEREST,
    REFUND,
    SALARY,

    // Outgoing
    TRANSFER_INTERNAL,
    TRANSFER_EXTERNAL,
    SAVING_WITHDRAW,
    SAVING_DEPOSIT,
    MORTGAGE_PAYMENT,
    ATM_WITHDRAWAL,
    BILL_PAYMENT,
    MOBILE_TOPUP,
    MERCHANT_PAYMENT,
    // Other
    LOAN_PAYMENT,
    FEE,
    TRANSFER_INTERNAL_OUTGOING,

}
