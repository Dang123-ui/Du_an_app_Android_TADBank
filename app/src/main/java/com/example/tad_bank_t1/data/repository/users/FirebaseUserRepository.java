package com.example.tad_bank_t1.data.repository.users;

import com.example.tad_bank_t1.data.adapterPattern.users.UserAdapter;
import com.example.tad_bank_t1.data.model.Ekyc;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.Role;
import com.example.tad_bank_t1.data.model.enums.UserStatus;
import com.example.tad_bank_t1.data.model.enums.UserStatusOnlOff;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebaseUserRepository implements UserRepository {
    private final UserAdapter adapter = new UserAdapter();

    @Override
    public Task<String> create(User user) {
        if (user.getUserId() != null) {
            return adapter.addWithId(user).continueWith(task -> user.getUserId());
        } else {
            return adapter.addAutoId(user);
        }
    }
    @Override
    public Task<Void> update(String id, User user) {
        return adapter.set(id, user);
    }

    @Override
    public Task<User> getById(String uid) {
        return adapter.get(uid, User.class);
    }

    @Override
    public Task<Boolean> usernameExists(String username) {
        Query q = adapter.query().whereEqualTo("username", username).limit(1);
        return adapter.where(q).continueWith(t -> t.getResult() != null && !t.getResult().isEmpty());
    }

    @Override
    public Task<Boolean> emailExists(String email) {
        Query q = adapter.query().whereEqualTo("email", email).limit(1);
        return adapter.where(q).continueWith(t -> t.getResult() != null && !t.getResult().isEmpty());
    }

    @Override
    public Task<Boolean> phoneExists(String phone) {
        String normalized = normalizePhoneForFirebase(phone);

        Query q = adapter.query()
                .whereEqualTo("phone", normalized)
                .limit(1);

        return adapter.where(q).continueWith(t -> {
            if (!t.isSuccessful()) return false; // hoặc throw t.getException();

            return t.getResult() != null && !t.getResult().isEmpty();
        });
    }


    @Override
    public Task<Void> lock(String userId) {
        User patch = new User();
        patch.setStatus(UserStatus.LOCKED);
        return adapter.doc(userId).update("status", UserStatus.LOCKED, "updatedAt", FieldValue.serverTimestamp());
    }

    @Override
    public Task<Void> activate(String userId) {
        User patch = new User();
        patch.setStatus(UserStatus.ACTIVE);
        return adapter.doc(userId).update("status", UserStatus.ACTIVE, "updatedAt", FieldValue.serverTimestamp());
    }

    @Override
    public Task<QuerySnapshot> searchByKeyword(String keyword, int limit) {
        return adapter.where(
                adapter.query().orderBy("fullName")
                        .startAt(keyword).endAt(keyword + "\uf8ff").limit(limit));
    }
    @Override
    public Task<User> findByEmail(String email) {
        Query q = adapter.query()
            .whereEqualTo("email", email)
            .limit(1);

        return adapter.where(q).continueWith(task -> {
            if (!task.isSuccessful() || task.getResult() == null) return null;
            QuerySnapshot snap = task.getResult();
            if (snap.isEmpty()) return null;
            DocumentSnapshot doc = snap.getDocuments().get(0);
            return doc.toObject(User.class);
        });
    }
    private static String normalizePhoneForFirebase(String input) {
        if (input == null) return null;
        String s = input.replaceAll("[^0-9+]", "");
        if (s.startsWith("+84")) {
            String rest = s.substring(3).replaceFirst("^0+", "");
            return "+84" + rest;
        }
        if (s.startsWith("+")) {
            return s;
        }
        if (s.startsWith("0")) {
            return "+84" + s.substring(1);
        }
        if (s.startsWith("84")) {
            return "+" + s;
        }
        return "+84" + s;
    }


    @Override
    public Task<User> findByPhone(String phone) {
        String e164 = normalizePhoneForFirebase(phone);
        Query q = adapter.query()
                .whereEqualTo("phone", e164)
                .limit(1);
        return adapter.where(q).continueWith(task -> {
            if (!task.isSuccessful() || task.getResult() == null) return null;
            QuerySnapshot snap = task.getResult();
            if (snap.isEmpty()) return null;
            DocumentSnapshot doc = snap.getDocuments().get(0);
            return doc.toObject(User.class);
        });
    }

    @Override
    public Task<Void> updateStatusOnlOff(String userId, UserStatusOnlOff statusOnlOff) {
        Map<String, Object> data = new HashMap<>();
        data.put("statusOnlOff", statusOnlOff != null ? statusOnlOff.name() : null);
        data.put("lastActiveAt", FieldValue.serverTimestamp());
        // adapter.doc(userId) là DocumentReference; nếu tên khác thì đổi cho đúng
        return adapter.doc(userId).update(data);
    }

    @Override
    public ListenerRegistration listenUserOnlineOffline(UserOnlineOfflineListener listener) {
        return adapter.query().addSnapshotListener((snap, e) -> {
            if(e != null || snap == null) return;
            int online = 0;
            int offline = 0;
            for (DocumentSnapshot doc : snap.getDocuments()) {
                String statusStr = doc.getString("statusOnlOff");
                if ("ONLINE".equals(statusStr)) {
                    online++;
                } else if ("OFFLINE".equals(statusStr)) {
                    offline++;
                }
            }
            listener.onChanged(online, offline);
        });
    }

    @Override
    public void getActiveUserIdsInRange(Date start, Date end, ActiveUsersCallback callback) {
        adapter.query().whereGreaterThanOrEqualTo("lastActiveAt", start).whereLessThanOrEqualTo("lastActiveAt", end).get().addOnSuccessListener(snap ->{
                    Set<String> ids = new HashSet<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        ids.add(doc.getId());
                    }
                    callback.onSuccess(ids);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public Task<List<User>> getCustomerActive() {
        Query q = adapter.query().whereEqualTo("role", Role.CUSTOMER).whereEqualTo("status", UserStatus.ACTIVE);
        return adapter.where(q).continueWith(t -> {
            if(!t.isSuccessful() || t.getResult() == null || t.getResult().isEmpty()) return Collections.emptyList();
            List<User> users = new ArrayList<>();
            for(DocumentSnapshot doc : t.getResult().getDocuments()){
                User u = doc.toObject(User.class);
                if(u != null) users.add(u);
            }
            return users;
        });
    }
}
