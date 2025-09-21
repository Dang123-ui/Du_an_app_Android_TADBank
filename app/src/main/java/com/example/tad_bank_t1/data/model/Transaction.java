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
}
