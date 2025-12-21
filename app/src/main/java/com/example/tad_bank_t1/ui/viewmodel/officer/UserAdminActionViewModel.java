package com.example.tad_bank_t1.ui.viewmodel.officer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.enums.UserStatus;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserAdminActionViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final AccountRepository accountRepo = new FirebaseAccountRepository();

    private final MutableLiveData<String> message = new MutableLiveData<>();
    public LiveData<String> getMessage() { return message; }

    // ===== ONLY 2 STATUS: active / locked =====
    public void lockUser(String uid) {
        setUserStatus(uid, UserStatus.LOCKED)
                .addOnSuccessListener(v -> {
                    // user locked => freeze all accounts
                    accountRepo.freezeAccountsByUserId(uid)
                            .addOnSuccessListener(v2 -> message.setValue("Đã khóa user + FROZEN toàn bộ accounts"))
                            .addOnFailureListener(e -> message.setValue("Khóa user OK, nhưng freeze accounts lỗi: " + e.getMessage()));
                });
    }

    public void unlockUser(String uid) {
        setUserStatus(uid, UserStatus.ACTIVE)
                .addOnSuccessListener(v -> {
                    // user active => unfreeze accounts (OPEN), bỏ qua account CLOSED
                    accountRepo.unfreezeAccountsByUserId(uid)
                            .addOnSuccessListener(v2 -> message.setValue("Đã mở khóa user + OPEN lại accounts (trừ CLOSED)"))
                            .addOnFailureListener(e -> message.setValue("Mở khóa user OK, nhưng unfreeze accounts lỗi: " + e.getMessage()));
                });
    }

    // update user.status
    private Task<Void> setUserStatus(String uid, UserStatus status) {
        Map<String, Object> up = new HashMap<>();
        up.put("status", status);
        up.put("updatedAt", FieldValue.serverTimestamp());

        return db.collection("users").document(uid)
                .update(up)
                .addOnSuccessListener(v -> message.setValue("Đã cập nhật user status: " + status))
                .addOnFailureListener(e -> message.setValue("Update user thất bại: " + e.getMessage()));
    }
}
