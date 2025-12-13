package com.example.tad_bank_t1.data.repository.notification;

import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.NotificationType;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public interface NotificationRepository {
    Task<Void> upsert(Notification Notification);
    Task<Notification> getById(String txnId);
    Task<List<Notification>> getNotificationsByUser(String userId);
    ListenerRegistration listenerGetNotificationsByUser(String userId, NotificationType type, OnNotificationsChanged listener);
//    Task<Void> delete(String txnId)
    ListenerRegistration listenerUnreadCount(String userId, OnUnreadCountNotificationChanged listener);
    Task<QuerySnapshot> searchByKeyword(String keyword, int limit);
    Task<Void> markNotificationsAsRead(List<Notification> notifications);

    // --------------------------------
    // Tạo thong bao chua doc khi xay ra giao dich
    // --------------------------------
    void createNotification(Notification notification, ResultCallback<Notification> callback);

    // --------------------------------
    // Lay mot thong tin thong bao bang id
    // --------------------------------
    void getNotificationById(String NotificationId, ResultCallback<Notification> callback);
}
