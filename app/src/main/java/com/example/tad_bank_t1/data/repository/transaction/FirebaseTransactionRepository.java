package com.example.tad_bank_t1.data.repository.transaction;

import com.example.tad_bank_t1.data.adapterPattern.TransactionAdapter;
import com.example.tad_bank_t1.data.model.Transaction;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseTransactionRepository implements TransactionRepository {
    private final TransactionAdapter adapter = new TransactionAdapter();

    @Override
    public Task<Void> upsert(Transaction txn) {
        return adapter.merge(txn.getTransactionId(), txn);
    }

    @Override
    public Task<Transaction> getById(String txnId) {
        return adapter.get(txnId, Transaction.class);
    }

    @Override
    public Task<List<Transaction>> getTransactionsByAccount(String accountId) {
        Query q = adapter.query()
                .whereEqualTo("accountId", accountId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(20);

        return adapter.where(q).continueWith(task -> {
            List<Transaction> result = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                // Lấy từng DocumentSnapshot trong QuerySnapshot
                for (var doc : task.getResult().getDocuments()) {
                    Transaction obj = doc.toObject(Transaction.class);
                    if (obj != null) result.add(obj);
                }
            }
            return result;
        });
    }

    @Override
    public Task<QuerySnapshot> searchByKeyword(String keyword, int limit) {
        return adapter.where(
                adapter.query().orderBy("fullName")
                        .startAt(keyword).endAt(keyword + "\uf8ff").limit(limit)
        );
    }

    @Override
    public void getTransactionsInRange(Date start, Date end, TransactionListCallback callback) {
        adapter.query()
                .whereGreaterThanOrEqualTo("createdAt", start)
                .whereLessThanOrEqualTo("createdAt", end)
                .get().addOnSuccessListener(snap -> {
                    List<Transaction> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Transaction tx = doc.toObject(Transaction.class);
                        if (tx != null) {
                            // Nếu cần PK:
                            // tx.setTransactionId(doc.getId());
                            list.add(tx);
                        }
                    }
                    callback.onSuccess(list);
                }).addOnFailureListener(callback::onFailure);
    }
}
