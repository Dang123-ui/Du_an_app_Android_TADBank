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

    public Notification(String userId, String title, String message, String type, Date createdAt) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = false;
        this.createdAt = createdAt;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
