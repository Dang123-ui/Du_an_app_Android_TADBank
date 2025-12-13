package com.example.tad_bank_t1.ui.form.payload.transactions;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TxnType;

public class TransferPayload extends BaseTransactionPayload{
    public Bank receiverBank;

    public TransferPayload(){}

    public TransferPayload(Account senderAccount, Transaction transaction, Bank receiverBank) {
        super(senderAccount, transaction);
        this.receiverBank = receiverBank;
    }

    @Override
    public boolean isValid() {
        return transaction != null && transaction.getAmount() > 0 && receiverBank != null;
    }
}
