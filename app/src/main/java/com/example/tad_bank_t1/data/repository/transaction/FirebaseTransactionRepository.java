package com.example.tad_bank_t1.data.repository.transaction;

import com.example.tad_bank_t1.data.adapterPattern.TransactionAdapter;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // --------------------------------
    // Tạo giao dịch với status: PENDING
    // --------------------------------
    @Override
    public void createTransaction(Transaction transaction, ResultCallback<Transaction> callback) {
        // transaction to Map
        if (transaction.getTransactionId() == null){
            transaction.setTransactionId(TransactionUtil.generateTransactionId());
        }
        if (transaction.getStatus() == null){
            transaction.setStatus(TnxStatus.PENDING);
        }

        transaction.setCreatedAt(new Date());

        Map<String, Object> txnMap = transaction.toMap();

        String id = transaction.getTransactionId();

        // upsert transaction
        adapter.col()
                .document(id)
                .set(txnMap)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(id).get();
                })
                .addOnSuccessListener(doc -> {
                    Transaction saved = doc.toObject(Transaction.class);
                    callback.onSucces(saved);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // --------------------------------
    // Cap nhat trang thai mot cho giao dich
    // --------------------------------
    public void updateTransactionStatus(String transactionId, TnxStatus newStatus, ResultCallback<Transaction> callback) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", newStatus);
        adapter.col()
                .document(transactionId)
                .update(updateData)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(transactionId).get();
                })
                .addOnSuccessListener(documentSnapshot -> {
                    Transaction obj = documentSnapshot.toObject(Transaction.class);
                    callback.onSucces(obj);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }

    // --------------------------------
    // Lay mot thong tin giao dich bang id
    // --------------------------------
    public void getTransactionById(String transactionId, ResultCallback<Transaction> callback) {
        adapter.col()
                .document(transactionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Transaction obj = documentSnapshot.toObject(Transaction.class);
                    callback.onSucces(obj);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }

}
