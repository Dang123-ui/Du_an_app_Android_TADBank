package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.Notification;

public class NotificationAdapter extends AbstractFirestoreAdapter<Notification> {
    public NotificationAdapter() {super(FirestorePaths.NOTIFICATIONS);}
}
