package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.Transaction;

public class TransactionAdapter extends AbstractFirestoreAdapter<Transaction> {
    public TransactionAdapter() {
        super(FirestorePaths.TRANSACTIONS);
    }
}
