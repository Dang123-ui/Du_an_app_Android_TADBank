package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.NotificationType;

import java.util.Date;

public class Notification {
    private String notificationId;      // ID (PK)
    private String userId;              // FK -> Users.userId
    private String accountId;           // optional: nếu thông báo liên quan account cụ thể
    private String title;               // tiêu đề ngắn
    private String message;             // nội dung chi tiết
    private NotificationType type;      // SYSTEM, TRANSACTION, PROMOTION, WARNING, etc.
    private boolean read;               // đã đọc chưa
    private Date createdAt;             // thời gian tạo
    private String relatedId;           // ID liên quan (transactionId / loanId / billId / etc.)
    private String deepLink;            // optional: link mở ra màn hình chi tiết trong app

    public Notification (){}

    public Notification(String notificationId, String userId, String accountId, String title, String message, NotificationType type, boolean read, Date createdAt, String relatedId, String deepLink) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.accountId = accountId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
        this.relatedId = relatedId;
        this.deepLink = deepLink;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
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

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }
}
