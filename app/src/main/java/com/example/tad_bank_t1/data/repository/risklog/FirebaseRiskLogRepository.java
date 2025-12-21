package com.example.tad_bank_t1.data.repository.risklog;

import androidx.annotation.NonNull;

import com.example.tad_bank_t1.data.model.RiskLog;
import com.example.tad_bank_t1.data.model.enums.UserRisk;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRiskLogRepository implements RiskLogRepository {
    private static final String COL_RISK_LOGS = "risk_logs";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public Task<String> create(RiskLog log) {
        DocumentReference ref = db.collection(COL_RISK_LOGS).document();

        Map<String, Object> data = (log != null) ? log.toMap() : new HashMap<>();

        // Chuẩn hoá enum để lưu ổn định (String)
        if (log != null) {
            if (log.fromRisk != null) data.put("fromRisk", log.fromRisk.name());
            if (log.toRisk != null) data.put("toRisk", log.toRisk.name());
        }

        // Luôn lấy time server nếu tạo mới
        data.put("createdAt", FieldValue.serverTimestamp());
        return ref.set(data).continueWith(t -> ref.getId());
    }
    @Override
    public Task<QuerySnapshot> getLatestByUid(String uid, int limit) {
        Query q = db.collection(COL_RISK_LOGS)
                .whereEqualTo("uid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);

        return q.get();
    }

    @Override
    public ListenerRegistration listenLatestByUid(@NonNull String uid,
                                                  int limit,
                                                  @NonNull OnRiskLogsChanged listener) {
        Query q = db.collection(COL_RISK_LOGS)
                .whereEqualTo("uid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);

        return q.addSnapshotListener((snap, e) -> {
            if (e != null) {
                listener.onError(e);
                return;
            }
            if (snap != null) listener.onChanged(snap);
        });
    }
}
