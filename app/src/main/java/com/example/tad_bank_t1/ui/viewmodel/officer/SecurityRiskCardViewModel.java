package com.example.tad_bank_t1.ui.viewmodel.officer;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.RiskLog;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.UserRisk;
import com.example.tad_bank_t1.data.repository.risklog.FirebaseRiskLogRepository;
import com.example.tad_bank_t1.data.repository.risklog.RiskLogRepository;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SecurityRiskCardViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final RiskLogRepository riskLogRepo = new FirebaseRiskLogRepository();

    private ListenerRegistration userReg;
    private ListenerRegistration logsReg;

    // ====== ADDED: last login text ======
    private final MutableLiveData<String> lastLoginText = new MutableLiveData<>("—");
    public LiveData<String> getLastLoginText() { return lastLoginText; }
    private final MutableLiveData<String> riskText = new MutableLiveData<>("Low");
    private final MutableLiveData<Integer> riskColor = new MutableLiveData<>(0xFF1B8A3B);
    public LiveData<String> getRiskText() { return riskText; }
    public LiveData<Integer> getRiskColor() { return riskColor; }
    private final MutableLiveData<UserRisk> currentRisk = new MutableLiveData<>(UserRisk.LOW);
    private final MutableLiveData<List<RiskLog>> logs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    private final MutableLiveData<Boolean> enableLow = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> enableMedium = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> enableHigh = new MutableLiveData<>(true);

    public LiveData<UserRisk> getCurrentRisk() { return currentRisk; }
    public LiveData<List<RiskLog>> getLogs() { return logs; }
    public LiveData<String> getError() { return error; }

    public LiveData<Boolean> getEnableLow() { return enableLow; }
    public LiveData<Boolean> getEnableMedium() { return enableMedium; }
    public LiveData<Boolean> getEnableHigh() { return enableHigh; }

    public void start(@NonNull String uid) {
        stop();

        DocumentReference userRef = db.collection("users").document(uid);
        userReg = userRef.addSnapshotListener((snap, e) -> {
            if (e != null) {
                error.postValue(e.getMessage());
                return;
            }
            if (snap == null || !snap.exists()) return;

            User u = snap.toObject(User.class);
            if (u == null) return;

            // ====== ADDED: update Last Login from lastActiveAt ======
            Date last = u.getLastActiveAt(); // field bạn đang có trong User
            if (last != null) {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                lastLoginText.postValue(df.format(last));
            } else {
                lastLoginText.postValue("—");
            }

            // Risk current
            UserRisk r = (u.getRisk() == null) ? UserRisk.LOW : u.getRisk();
            currentRisk.postValue(r);
            riskText.postValue(cap(r.name()));
            riskColor.postValue(colorForRisk(r));

            // disable nút trùng trạng thái
            enableLow.postValue(r != UserRisk.LOW);
            enableMedium.postValue(r != UserRisk.MEDIUM);
            enableHigh.postValue(r != UserRisk.HIGH);
        });

        logsReg = riskLogRepo.listenLatestByUid(uid, 10, new RiskLogRepository.OnRiskLogsChanged() {
            @Override
            public void onChanged(com.google.firebase.firestore.QuerySnapshot snap) {
                List<RiskLog> list = snap.toObjects(RiskLog.class);
                logs.postValue(list);
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e.getMessage());
            }
        });
    }

    public void setRiskLow(@NonNull String uid, @NonNull String reason) {
        setRisk(uid, UserRisk.LOW, reason);
    }

    public void setRiskMedium(@NonNull String uid, @NonNull String reason) {
        setRisk(uid, UserRisk.MEDIUM, reason);
    }

    public void setRiskHigh(@NonNull String uid, @NonNull String reason) {
        setRisk(uid, UserRisk.HIGH, reason);
    }

    private void setRisk(@NonNull String uid, @NonNull UserRisk target, @NonNull String reason) {
        UserRisk cur = currentRisk.getValue() == null ? UserRisk.LOW : currentRisk.getValue();
        if (cur == target) return;

        DocumentReference userRef = db.collection("users").document(uid);

        userRef.update("risk", target)
                .addOnSuccessListener(v -> {
                    RiskLog log = new RiskLog(uid, cur, target, reason);
                    riskLogRepo.create(log)
                            .addOnFailureListener(e -> error.postValue(e.getMessage()));
                })
                .addOnFailureListener(e -> error.postValue(e.getMessage()));
    }

    public void stop() {
        if (userReg != null) { userReg.remove(); userReg = null; }
        if (logsReg != null) { logsReg.remove(); logsReg = null; }
    }

    @Override
    protected void onCleared() {
        stop();
        super.onCleared();
    }
    private static String cap(String s) {
        String l = s.toLowerCase(Locale.getDefault());
        return Character.toUpperCase(l.charAt(0)) + l.substring(1);
    }

    private static int colorForRisk(UserRisk r) {
        switch (r) {
            case LOW: return 0xFF1B8A3B;
            case MEDIUM: return 0xFFFF8A00;
            case HIGH: return 0xFFC32248;
            default: return 0xFF171213;
        }
    }

}
