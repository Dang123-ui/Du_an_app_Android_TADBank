package com.example.tad_bank_t1.data.repository.otp;

import com.example.tad_bank_t1.data.adapterPattern.otp.OtpCodeAdapter;
import com.example.tad_bank_t1.data.model.OtpCode;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseOtpCodeRepository implements OtpCodeRepository {

    private final OtpCodeAdapter adapter = new OtpCodeAdapter();
    private static final long DEFAULT_TTL_MILLIS = 5 * 60 * 1000;
    private static final SecureRandom RNG = new SecureRandom();
    @Override
    public Task<String> create(OtpCode otp) {
        DocumentReference ref = adapter.col().document();
        otp.setOtpId(ref.getId());
        return ref.set(otp)
                .continueWith(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getId();
                });
    }

    @Override
    public Task<Boolean> verifyAndConsume(String userId, String purpose, String code) {
        // 1) Tìm OTP phù hợp
        Query q = adapter.query()
                .whereEqualTo("userId", userId)
                .whereEqualTo("purpose", purpose)
                .whereEqualTo("code", code)
                .limit(1);

        return adapter.where(q).continueWithTask(t -> {
            QuerySnapshot qs = t.getResult();
            if (qs == null || qs.isEmpty()) {
                return Tasks.forResult(false);
            }

            DocumentSnapshot first = qs.getDocuments().get(0);
            final DocumentReference docRef = first.getReference();

            // 2) Xác thực & consume trong 1 transaction để an toàn
            FirebaseFirestore db = adapter.col().getFirestore();
            return db.runTransaction(transaction -> {
                DocumentSnapshot snap = transaction.get(docRef);

                // Đọc lại thông tin (phòng khi doc đã thay đổi giữa lúc query và transaction)
                String dbUserId   = snap.getString("userId");
                String dbPurpose  = snap.getString("purpose");
                String dbCode     = snap.getString("code");
                Timestamp usedAt  = snap.getTimestamp("usedAt");
                Timestamp expires = snap.getTimestamp("expiresAt");

                long nowMillis = System.currentTimeMillis();

                // Kiểm tra tính hợp lệ
                if (usedAt != null) return false;
                if (expires == null || expires.toDate().getTime() < nowMillis) return false;
                if (!userId.equals(dbUserId)) return false;
                if (!purpose.equals(dbPurpose)) return false;
                if (!code.equals(dbCode)) return false;

                // 3) Consume: đặt usedAt = server time, xoá code
                Map<String, Object> updates = new HashMap<>();
                updates.put("usedAt", FieldValue.serverTimestamp());
                updates.put("code", FieldValue.delete());
                transaction.update(docRef, updates);

                return true;
            });
        });
    }

    @Override
    public Task<OtpCode> getById(String otpId) {
        return adapter.get(otpId, OtpCode.class);
    }
    public Task<String> sendEmailOtp(String userId, String purpose, String email) {
        return sendEmailOtp(userId, email, purpose, DEFAULT_TTL_MILLIS);
    }
    public Task<String> sendEmailOtp(String userId, String email, String purpose, long ttlMillis) {
        String code = random6();
        long now = System.currentTimeMillis();
        Date createdAt = new Date(now);
        Date expiresAt = new Date(now + Math.max(10_000L, ttlMillis));
        OtpCode otp = new OtpCode();
        otp.setUserId(userId);
        otp.setPurpose(purpose);
        otp.setCode(code);
        otp.setCreatedAt(createdAt);
        otp.setExpiresAt(expiresAt);
        otp.setUsedAt(null);
        return create(otp);
    }
    private static String random6(){
        int n = RNG.nextInt(1_000_000);
        return String.format("%06d", n);
    }
}