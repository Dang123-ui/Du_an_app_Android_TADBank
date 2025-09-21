package com.example.tad_bank_t1.data.model;

import java.util.Date;

public class Notification {
    public String notificationId; // doc id hoặc PK
    public String userId;         // FK -> Users.userId
    public String title;          // tiêu đề
    public String message;        // nội dung
    public String type;           // SYSTEM, TRANSACTION, PROMOTION...
    public boolean read;          // đã đọc hay chưa
    public Date createdAt;        // thời gian tạo

    public Notification() {} // cần constructor rỗng cho Firestore

    public Notification(String userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = false;
        this.createdAt = new Date();
    }
}
