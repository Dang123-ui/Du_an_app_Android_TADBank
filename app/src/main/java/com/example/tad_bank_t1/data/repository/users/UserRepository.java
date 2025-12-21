package com.example.tad_bank_t1.data.repository.users;

import com.example.tad_bank_t1.data.model.Ekyc;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.UserStatus;
import com.example.tad_bank_t1.data.model.enums.UserStatusOnlOff;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface UserRepository {
    Task<String> create(User user);
    Task<Void> update(String id, User user);
    Task<User> getById(String userId);
    Task<Boolean> phoneExists(String phone);
    Task<Boolean> emailExists(String email);
    Task<Boolean> usernameExists(String username);
    Task<Void> lock(String userId);
    Task<Void> activate(String userId);
    Task<QuerySnapshot> searchByKeyword(String keyword, int limit);
    Task<User> findByEmail(String email);
    Task<User> findByPhone(String phone);
    Task<Void> updateStatusOnlOff(String userId, UserStatusOnlOff statusOnlOff);
    ListenerRegistration listenUserOnlineOffline(UserOnlineOfflineListener listener);
    interface ActiveUsersCallback {
        void onSuccess(Set<String> userIds);
        void onFailure(Exception e);
    }
    void getActiveUserIdsInRange(Date start, Date end, ActiveUsersCallback callback);
    Task<List<User>> getCustomerActive();
}
