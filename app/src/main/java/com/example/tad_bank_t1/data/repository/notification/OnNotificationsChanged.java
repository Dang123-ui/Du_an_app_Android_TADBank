package com.example.tad_bank_t1.data.repository.notification;

import com.example.tad_bank_t1.data.model.Notification;

import java.util.List;

public interface OnNotificationsChanged {
    void onChanged(List<Notification> notifications);
    void onError(Exception e);
}
