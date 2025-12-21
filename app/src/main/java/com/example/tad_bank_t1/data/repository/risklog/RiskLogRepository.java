package com.example.tad_bank_t1.data.repository.risklog;

import com.example.tad_bank_t1.data.model.RiskLog;
import com.example.tad_bank_t1.data.model.enums.UserRisk;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public interface RiskLogRepository {
    Task<String> create(RiskLog log);

    Task<QuerySnapshot> getLatestByUid(String uid, int limit);

    ListenerRegistration listenLatestByUid(String uid, int limit, OnRiskLogsChanged listener);

    interface OnRiskLogsChanged {
        void onChanged(QuerySnapshot snap);
        void onError(Exception e);
    }
}
