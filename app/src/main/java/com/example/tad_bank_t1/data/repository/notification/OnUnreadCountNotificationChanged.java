package com.example.tad_bank_t1.data.repository.notification;

import com.google.firebase.firestore.QuerySnapshot;

public interface OnUnreadCountNotificationChanged {
    void onUnreadChanged(Integer count);
}
