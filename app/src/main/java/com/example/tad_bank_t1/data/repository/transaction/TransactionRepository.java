package com.example.tad_bank_t1.data.repository.transaction;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

public interface TransactionRepository {
    Task<Void> upsert(Transaction transaction);
    Task<Transaction> getById(String txnId);
    Task<List<Transaction>> getTransactionsByAccount(String accountId);
    Task<QuerySnapshot> searchByKeyword(String keyword, int limit);
    interface TransactionListCallback {
        void onSuccess(List<Transaction> transactions);
        void onFailure(Exception e);
    }
    void getTransactionsInRange(Date start, Date end, TransactionListCallback callback);
}
