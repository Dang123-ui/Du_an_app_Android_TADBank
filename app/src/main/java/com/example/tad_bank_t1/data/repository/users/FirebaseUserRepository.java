package com.example.tad_bank_t1.data.repository.users;

import com.example.tad_bank_t1.data.adapterPattern.users.UserAdapter;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.UserStatus;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseUserRepository implements UserRepository {
    private final UserAdapter adapter = new UserAdapter();

    @Override public Task<Void> upsert(User u) { return adapter.merge(u.getUserId(), u); }
    @Override public Task<User> getById(String uid) { return adapter.get(uid, User.class); }

    @Override public Task<Boolean> usernameExists(String username) {
        Query q = adapter.query().whereEqualTo("username", username).limit(1);
        return adapter.where(q).continueWith(t -> t.getResult()!=null && !t.getResult().isEmpty());
    }
    @Override public Task<Boolean> emailExists(String email) {
        Query q = adapter.query().whereEqualTo("email", email).limit(1);
        return adapter.where(q).continueWith(t -> t.getResult()!=null && !t.getResult().isEmpty());
    }
    @Override public Task<Boolean> phoneExists(String phone) {
        Query q = adapter.query().whereEqualTo("phone", phone).limit(1);
        return adapter.where(q).continueWith(t -> t.getResult()!=null && !t.getResult().isEmpty());
    }

    @Override
    public Task<Void> lock(String userId) {
        User patch = new User();
        patch.setStatus(UserStatus.LOCKED);
        return adapter.doc(userId).update("status",UserStatus.LOCKED,"updatedAt", FieldValue.serverTimestamp());
    }

    @Override
    public Task<Void> activate(String userId) {
        User patch = new User();
        patch.setStatus(UserStatus.ACTIVE);
        return adapter.doc(userId).update("status",UserStatus.ACTIVE,"updatedAt", FieldValue.serverTimestamp());
    }

    @Override public Task<QuerySnapshot> searchByKeyword(String keyword, int limit) {
        return adapter.where(
                adapter.query().orderBy("fullName")
                        .startAt(keyword).endAt(keyword+"\uf8ff").limit(limit)
        );
    }

}
