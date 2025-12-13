package com.example.tad_bank_t1.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.data.model.enums.NotificationType;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.repository.notification.FirebaseNotificationRepository;
import com.example.tad_bank_t1.data.repository.notification.NotificationRepository;
import com.example.tad_bank_t1.data.repository.notification.OnNotificationsChanged;
import com.example.tad_bank_t1.data.repository.notification.OnUnreadCountNotificationChanged;
import com.example.tad_bank_t1.data.response.ResultWrapper;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationViewModel extends ViewModel {
    NotificationRepository notificationRepository = new FirebaseNotificationRepository();

    private final MutableLiveData<List<Notification>> _systemNotifications = new MutableLiveData<>();
    public LiveData<List<Notification>> systemNotifications = _systemNotifications;

    private final MutableLiveData<List<Notification>> _transactionNotifications = new MutableLiveData<>();
    public LiveData<List<Notification>> transactionNotifications = _transactionNotifications;

    private final MutableLiveData<Integer> _unreadCount = new MutableLiveData<>();
    public LiveData<Integer> unreadCount = _unreadCount;

    // result state
    private final MutableLiveData<ResultWrapper<Notification>> _resultState = new MutableLiveData<>();


    private ListenerRegistration listenerSystem;
    private ListenerRegistration listenerTransaction;
    private ListenerRegistration  unreadListener;

    // listener remote
    public void startListeningSystem(String userId){
        listenerSystem = notificationRepository.listenerGetNotificationsByUser(userId, NotificationType.SYSTEM, new OnNotificationsChanged(){
            @Override
            public void onChanged(List<Notification> notifications) {
                _systemNotifications.postValue(notifications);
            }

            @Override
            public void onError(Exception e) {
                Log.d("NotificationViewModel", "Error getting documents: " + e.getMessage());
            }

        });

    }



    public void startListeningTransaction(String userId){
        listenerTransaction = notificationRepository.listenerGetNotificationsByUser(userId, NotificationType.TRANSACTION, new OnNotificationsChanged(){
            @Override
            public void onChanged(List<Notification> notifications) {
                _transactionNotifications.postValue(notifications);
            }

            @Override
            public void onError(Exception e) {
                Log.d("NotificationViewModel", "Error getting documents: " + e.getMessage());
            }

        });

    }

    public void startListeningUnreadCount(String userId){
        unreadListener = notificationRepository.listenerUnreadCount(userId, new OnUnreadCountNotificationChanged() {
            @Override
            public void onUnreadChanged(Integer count) {
                _unreadCount.postValue(count);
            }
        });
    }

    public void markAllRead(NotificationType type) {
        List<Notification> list;
        if (type == NotificationType.SYSTEM) list = _systemNotifications.getValue();
        else if (type == NotificationType.TRANSACTION) list = _transactionNotifications.getValue();
        else return;

        if (list == null) return;
        notificationRepository.markNotificationsAsRead(list);
    }

    public void stopListening() {
        if (listenerSystem != null) listenerSystem.remove();
        if (listenerTransaction != null) listenerTransaction.remove();
        if (unreadListener != null) unreadListener.remove();
    }

    // Tao thong bao khi giao dich thanh cong
    public void createNotification(Notification notification){
        if (notification == null) return;
        _resultState.postValue(ResultWrapper.loading());

        notificationRepository.createNotification(notification, new ResultCallback<Notification>() {
            @Override
            public void onSucces(Notification data) {
                if (data == null) {
                    _resultState.postValue(ResultWrapper.error("Tạo Notification bị lỗi"));
                } else {
                    _resultState.postValue(ResultWrapper.success(data));
                }
            }

            @Override
            public void onError(String error) {
                _resultState.postValue(ResultWrapper.error(error));
            }
        });
    }

}
