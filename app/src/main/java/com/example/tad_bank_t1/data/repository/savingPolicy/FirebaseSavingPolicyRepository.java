package com.example.tad_bank_t1.data.repository.savingPolicy;

import com.example.tad_bank_t1.data.adapterPattern.SavingPolicyAdapter;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FirebaseSavingPolicyRepository implements SavingPolicyRepository {
    private final SavingPolicyAdapter adapter = new SavingPolicyAdapter();


    @Override
    public void create(SavingsRatePolicy savingPolicy, ResultCallback<SavingsRatePolicy> callback) {
        Map<String, Object> txnMap = savingPolicy.toMap();
        String colName = "SP";

        String id = savingPolicy.getSavingPolicyId() != null ? savingPolicy.getSavingPolicyId() : colName + "-" + TransactionUtil.generateIdWithTime();


        // upsert transaction
        adapter.col()
                .document(id)
                .set(txnMap)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(id).get();
                })
                .addOnSuccessListener(doc -> {
                    SavingsRatePolicy saved = doc.toObject(SavingsRatePolicy.class);
                    callback.onSuccess(saved);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void getAll(ResultCallback<List<SavingsRatePolicy>> callback) {
        Query q = adapter.query()
                .orderBy("createdAt", Query.Direction.DESCENDING);

        q.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SavingsRatePolicy> list = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        SavingsRatePolicy obj = doc.toObject(SavingsRatePolicy.class);
                        if (obj != null) list.add(obj);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void getById(String savingPolicyId, ResultCallback<SavingsRatePolicy> callback) {
        adapter.col()
                .document(savingPolicyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    SavingsRatePolicy obj = documentSnapshot.toObject(SavingsRatePolicy.class);
                    callback.onSuccess(obj);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }

    @Override
    public void update(SavingsRatePolicy savingPolicy, ResultCallback<SavingsRatePolicy> callback) {
        Map<String, Object> updateData = savingPolicy.toMap();
        if (savingPolicy.getSavingPolicyId() == null) {
            callback.onError("savingPolicyId is null");
            return;
        }
        updateData.remove("savingPolicyId");

        adapter.col()
                .document(savingPolicy.getSavingPolicyId())
                .update(updateData)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(savingPolicy.getSavingPolicyId()).get();
                })
                .addOnSuccessListener(documentSnapshot -> {
                    SavingsRatePolicy obj = documentSnapshot.toObject(SavingsRatePolicy.class);
                    callback.onSuccess(obj);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }
}
