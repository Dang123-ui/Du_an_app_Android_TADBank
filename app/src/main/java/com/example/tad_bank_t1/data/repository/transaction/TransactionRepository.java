package com.example.tad_bank_t1.data.repository.transaction;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TransactionRepository {
    Task<Void> upsert(Transaction transaction);
    Task<Transaction> getById(String txnId);
    Task<List<Transaction>> getTransactionsByAccount(String accountId);
    Task<QuerySnapshot> searchByKeyword(String keyword, int limit);

    // --------------------------------
    // Tạo giao dịch với status: PENDING
    // --------------------------------
    public void createTransaction(Transaction transaction, ResultCallback<Transaction> callback);

    // --------------------------------
    // Cap nhat trang thai mot cho giao dich
    // --------------------------------
    public void updateTransactionStatus(String transactionId, TnxStatus newStatus, ResultCallback<Transaction> callback);

    // --------------------------------
    // Lay mot thong tin giao dich bang id
    // --------------------------------
    public void getTransactionById(String transactionId, ResultCallback<Transaction> callback);

}
