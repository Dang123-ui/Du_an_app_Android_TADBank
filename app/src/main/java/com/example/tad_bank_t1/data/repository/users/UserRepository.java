package com.example.tad_bank_t1.data.repository.users;

import com.example.tad_bank_t1.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public interface UserRepository {
    Task<Void> upsert(User user);
    Task<User> getById(String userId);
    Task<Boolean> phoneExists(String phone);
    Task<Boolean> emailExists(String email);
    Task<Boolean> usernameExists(String username);
    Task<Void> lock(String userId);
    Task<Void> activate(String userId);
    Task<QuerySnapshot> searchByKeyword(String keyword, int limit);
}
