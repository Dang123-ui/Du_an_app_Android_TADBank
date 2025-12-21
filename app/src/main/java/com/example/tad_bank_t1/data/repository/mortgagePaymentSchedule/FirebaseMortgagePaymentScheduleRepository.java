package com.example.tad_bank_t1.data.repository.mortgagePaymentSchedule;

import com.example.tad_bank_t1.data.adapterPattern.MortgagePaymentScheduleAdapter;
import com.example.tad_bank_t1.data.adapterPattern.SavingPolicyAdapter;
import com.example.tad_bank_t1.data.model.MortgagePaymentSchedule;
import com.example.tad_bank_t1.data.model.MortgagePaymentSchedule;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseMortgagePaymentScheduleRepository implements MortgagePaymentScheduleRepository {
    private final MortgagePaymentScheduleAdapter adapter = new MortgagePaymentScheduleAdapter();


    @Override
    public void create(MortgagePaymentSchedule mortgagePaymentSchedule, ResultCallback<MortgagePaymentSchedule> callback) {
        Map<String, Object> txnMap = mortgagePaymentSchedule.toMap();
        String colName = "MPS";

        String id = mortgagePaymentSchedule.getScheduleId() != null ?
                mortgagePaymentSchedule.getScheduleId()
                : colName + "-" + TransactionUtil.generateIdWithTime();


        // upsert transaction
        adapter.col()
                .document(id)
                .set(txnMap)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(id).get();
                })
                .addOnSuccessListener(doc -> {
                    MortgagePaymentSchedule saved = doc.toObject(MortgagePaymentSchedule.class);
                    callback.onSuccess(saved);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void getAll(ResultCallback<List<MortgagePaymentSchedule>> callback) {
        Query q = adapter.query().limit(100);

        q.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<MortgagePaymentSchedule> list = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        MortgagePaymentSchedule obj = doc.toObject(MortgagePaymentSchedule.class);
                        if (obj != null) list.add(obj);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void getById(String mortgagePaymentScheduleId, ResultCallback<MortgagePaymentSchedule> callback) {
        adapter.col()
                .document(mortgagePaymentScheduleId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    MortgagePaymentSchedule obj = documentSnapshot.toObject(MortgagePaymentSchedule.class);
                    callback.onSuccess(obj);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }

    @Override
    public void update(MortgagePaymentSchedule mortgagePaymentSchedule, ResultCallback<MortgagePaymentSchedule> callback) {
        Map<String, Object> updateData = mortgagePaymentSchedule.toMap();
        if (mortgagePaymentSchedule.getScheduleId() == null) {
            callback.onError("mortgagePaymentScheduleId is null");
            return;
        }
        updateData.remove("scheduleId");

        adapter.col()
                .document(mortgagePaymentSchedule.getScheduleId())
                .update(updateData)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(mortgagePaymentSchedule.getScheduleId()).get();
                })
                .addOnSuccessListener(documentSnapshot -> {
                    MortgagePaymentSchedule obj = documentSnapshot.toObject(MortgagePaymentSchedule.class);
                    callback.onSuccess(obj);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }
}
