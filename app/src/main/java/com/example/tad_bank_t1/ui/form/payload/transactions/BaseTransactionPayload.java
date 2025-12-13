package com.example.tad_bank_t1.ui.form.payload.transactions;

import android.util.Log;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TxnType;

public abstract class BaseTransactionPayload {
    protected Transaction transaction;
    protected Account senderAccount;

    public BaseTransactionPayload() {
        this.transaction = new Transaction();
    }

    public BaseTransactionPayload(Account senderAccount, Transaction transaction) {
        Log.d("TAG", "BaseTransactionPayload: " + senderAccount.toString());
        this.senderAccount = senderAccount;
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Account senderAccount) {
        this.senderAccount = senderAccount;
    }


    public abstract boolean isValid();
}
