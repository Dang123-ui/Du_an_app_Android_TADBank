package com.example.tad_bank_t1.data.repository.savingPolicy;

import com.example.tad_bank_t1.data.adapterPattern.SavingPolicyAdapter;
import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FirebaseSavingPolicyRepository implements SavingPolicyRepository {
    private final SavingPolicyAdapter adapter = new SavingPolicyAdapter();

    @Override
    public void create(SavingsRatePolicy savingPolicy, ResultCallback<SavingsRatePolicy> callback) {
        String id = (savingPolicy.getSavingPolicyId() == null) ? "" : savingPolicy.getSavingPolicyId().trim();
        if (id.isEmpty()) {
            id = generateSavingPolicyId();          // tự sinh id
            savingPolicy.setSavingPolicyId(id);     // gán lại vào object
        }
        final String finalId = id;
        Map<String, Object> map = savingPolicy.toMap();

        adapter.col()
                .document(id)
                .set(map)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(finalId).get();
                })
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onError("Create failed (doc missing)");
                        return;
                    }
                    SavingsRatePolicy saved = doc.toObject(SavingsRatePolicy.class);
                    if (saved != null) {
                        // đảm bảo object lấy docId (và docId == field savingPolicyId)
                        saved.setSavingPolicyId(doc.getId());
                    }
                    callback.onSuccess(saved);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void getAll(ResultCallback<List<SavingsRatePolicy>> callback) {
        Query q = adapter.query()
                .orderBy("createdAt", Query.Direction.DESCENDING);

        q.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SavingsRatePolicy> list = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        if (!doc.exists()) continue;

                        SavingsRatePolicy obj = doc.toObject(SavingsRatePolicy.class);
                        if (obj == null) continue;
                        obj.setSavingPolicyId(doc.getId());

                        list.add(obj);
                    }

                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void getById(String savingPolicyId, ResultCallback<SavingsRatePolicy> callback) {
        String id = (savingPolicyId == null) ? "" : savingPolicyId.trim();
        if (id.isEmpty()) {
            callback.onError("savingPolicyId is empty");
            return;
        }

        adapter.col()
                .document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onError("Not found");
                        return;
                    }

                    SavingsRatePolicy obj = doc.toObject(SavingsRatePolicy.class);
                    if (obj == null) {
                        callback.onError("Parse failed");
                        return;
                    }
                    if (obj.getSavingPolicyId() == null || obj.getSavingPolicyId().trim().isEmpty()) {
                        obj.setSavingPolicyId(doc.getId());
                    }

                    callback.onSuccess(obj);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void update(SavingsRatePolicy savingPolicy, ResultCallback<SavingsRatePolicy> callback) {
        if (savingPolicy == null) {
            callback.onError("savingPolicy is null");
            return;
        }

        String id = (savingPolicy.getSavingPolicyId() == null) ? "" : savingPolicy.getSavingPolicyId().trim();
        if (id.isEmpty()) {
            callback.onError("savingPolicyId is empty");
            return;
        }
        final String finalId = id;
        Map<String, Object> updateData = savingPolicy.toMap();
        updateData.put("savingPolicyId", finalId);

        adapter.col()
                .document(finalId)
                .update(updateData)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(finalId).get();
                })
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onError("Not found");
                        return;
                    }
                    SavingsRatePolicy obj = doc.toObject(SavingsRatePolicy.class);
                    if (obj == null) {
                        callback.onError("Parse failed");
                        return;
                    }
                    if (obj.getSavingPolicyId() == null || obj.getSavingPolicyId().trim().isEmpty()) {
                        obj.setSavingPolicyId(doc.getId());
                    }

                    callback.onSuccess(obj);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public ListenerRegistration listenAll(ResultCallback<List<SavingsRatePolicy>> callback) {
        Query q = adapter.query().orderBy("createdAt", Query.Direction.DESCENDING);
        return q.addSnapshotListener((snap, e) -> {
            if (e != null) {
                callback.onError(e.getMessage());
                return;
            }
            List<SavingsRatePolicy> list = new ArrayList<>();
            if (snap != null) {
                for (DocumentSnapshot doc : snap.getDocuments()) {
                    try {
                        if (!doc.exists()) continue;
                        SavingsRatePolicy obj = doc.toObject(SavingsRatePolicy.class);
                        if (obj == null) continue;
                        if (obj.getSavingPolicyId() == null || obj.getSavingPolicyId().trim().isEmpty()) {
                            obj.setSavingPolicyId(doc.getId());
                        }
                        list.add(obj);
                    } catch (Exception ex) {

                    }
                }
            }

            callback.onSuccess(list);
        });
    }

    private String generateSavingPolicyId() {
        return "POLICY_" + System.currentTimeMillis();
    }
}
