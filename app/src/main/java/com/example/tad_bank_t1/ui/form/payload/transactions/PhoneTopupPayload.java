package com.example.tad_bank_t1.ui.form.payload.transactions;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TxnType;

public class PhoneTopupPayload extends BaseTransactionPayload{
    public String phoneNumber;
//    public String carrier;

    public PhoneTopupPayload(){}

    public PhoneTopupPayload(Account senderAccount, Transaction transaction, String phoneNumber) {
        super(senderAccount, transaction);
        this.phoneNumber = phoneNumber;
    }

    public PhoneTopupPayload(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean isValid(){
        return phoneNumber != null && transaction != null && transaction.getAmount() > 0;
    }
}

