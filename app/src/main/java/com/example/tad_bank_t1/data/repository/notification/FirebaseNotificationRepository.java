package com.example.tad_bank_t1.data.repository.notification;

import android.util.Log;

import com.example.tad_bank_t1.data.adapterPattern.NotificationAdapter;
import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.NotificationType;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FirebaseNotificationRepository implements NotificationRepository {
    private final NotificationAdapter adapter = new NotificationAdapter();

    @Override
    public Task<Void> upsert(Notification txn) {
        return adapter.merge(txn.getNotificationId(), txn);
    }

    @Override
    public Task<Notification> getById(String txnId) {
        return adapter.get(txnId, Notification.class);
    }

    @Override
    public Task<List<Notification>> getNotificationsByUser(String userId) {
        Query q = adapter.query()
                .whereIn("userId", Arrays.asList(userId, "ALL"))
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(30);

        return adapter.where(q).continueWith(task -> {
            List<Notification> result = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                // Lấy từng DocumentSnapshot trong QuerySnapshot
                for (var doc : task.getResult().getDocuments()) {
                    Notification obj = doc.toObject(Notification.class);

                    if (obj != null){
                        obj.setNotificationId(doc.getId());
                        result.add(obj);
                    }
                }
            }
            return result;
        });
    }

    @Override
    public ListenerRegistration listenerGetNotificationsByUser(String userId, NotificationType type, OnNotificationsChanged listener){
        Query q = adapter.query()
                .whereIn("userId", Arrays.asList(userId, "ALL"))
                .whereEqualTo("type", type.name())
                .orderBy("createdAt", Query.Direction.DESCENDING);

        return q.addSnapshotListener((notifications, error) -> {
            if (error != null) {
                listener.onError(error);
                return;
            }
            List<Notification> result = new ArrayList<>();
            if (notifications != null && !notifications.isEmpty()){
                for (var doc : notifications.getDocuments()){
                    Notification obj = doc.toObject(Notification.class);
                    if (obj != null) {
                        obj.setNotificationId(doc.getId());
                        result.add(obj);
                    }
                }
            }

            listener.onChanged(result);
        });
    }

    // realtime count notifications
    @Override
    public ListenerRegistration listenerUnreadCount(String userId, OnUnreadCountNotificationChanged listener) {
        Query q = adapter.query()
                .whereIn("userId", Arrays.asList(userId, "ALL"))
                .whereEqualTo("isRead", false);
        return q.addSnapshotListener((count, error) -> {
            if (error != null) return;
            Log.d("FirebaseNotificationRepository", "listenerUnreadCount: " + count.size() + "");
            listener.onUnreadChanged(count.size());
        });
    }

    @Override
    public Task<QuerySnapshot> searchByKeyword(String keyword, int limit) {
        Query q = adapter.query()
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .startAt(keyword)
                .endAt(keyword + "\uf8ff")
                .limit(limit);
        return adapter.where(q);
    }

    @Override
    public Task<Void> markNotificationsAsRead(List<Notification> notifications) {
        for (Notification n : notifications){
            if (!n.getIsRead()){
                adapter.doc(n.getNotificationId())
                        .update("isRead", true);
            }
        }
        return null;
    }


    // --------------------------------
    // Tạo thong bao chua doc khi xay ra giao dich
    // --------------------------------
    @Override
    public void createNotification(Notification notification, ResultCallback<Notification> callback) {
        notification.setIsRead(false);
        Map<String, Object> txnMap = notification.toMap();

        String id = notification.getNotificationId() == null
                ? TransactionUtil.generateTransactionId()
                : notification.getNotificationId();

        adapter.col()
                .document(id)
                .set(txnMap)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(id).get();
                })
                .addOnSuccessListener(doc -> {
                    Notification saved = doc.toObject(Notification.class);
                    callback.onSucces(saved);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // --------------------------------
    // Lay mot thong tin thong bao bang id
    // --------------------------------
    @Override
    public void getNotificationById(String NotificationId, ResultCallback<Notification> callback) {

    }
}
