package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.NotificationType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notification {
    private String notificationId;      // ID (PK)
    private String userId;              // FK -> Users.userId
    private String accountId;           // optional: nếu thông báo liên quan account cụ thể
    private String title;               // tiêu đề ngắn
    private String message;             // nội dung chi tiết
    private NotificationType type;      // SYSTEM, TRANSACTION, PROMOTION, WARNING, etc.
    private boolean isRead;               // đã đọc chưa
    private Date createdAt, updatedAt;             // thời gian tạo
    private String relatedId;           // ID liên quan (transactionId / loanId / billId / etc.)
    private String deepLink;            // optional: link mở ra màn hình chi tiết trong app

    public Notification (){}

    // Constructor with builder
    public Notification(Builder builder) {
        this.notificationId = builder.notificationId;
        this.userId = builder.userId;
        this.accountId = builder.accountId;
        this.title = builder.title;
        this.message = builder.message;
        this.type = builder.type;
        this.isRead = builder.isRead;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : new Date();
        this.relatedId = builder.relatedId;
        this.deepLink = builder.deepLink;
    }

    // get set

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

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean read) {
        this.isRead = read;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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

    // end get set



    // ==============
    // Builder pattern
    // ==============
    public static Builder builder(){
        return new Builder();
    }
    public static class Builder{
        private String notificationId;
        private String userId;

        private String accountId;
        private String title;
        private String message;
        private NotificationType type;
        private boolean isRead;
        private String relatedId;
        private String deepLink;
        private Date createdAt, updatedAt;


        public Builder notificationId(String notificationId){
            this.notificationId = notificationId;
            return this;
        }

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder accountId(String accountId){
            this.accountId = accountId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder isRead(boolean read) {
            this.isRead = read;
            return this;
        }

        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }


        public Builder relatedId(String relatedId) {
            this.relatedId = relatedId;
            return this;
        }

        public Builder deepLink(String deepLink) {
            this.deepLink = deepLink;
            return this;
        }

        public Notification build(){
            return new Notification(this);
        }
    }



    // Map de luu vao fire base
    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("notificationId", notificationId);
        map.put("userId", userId);
        map.put("accountId", accountId);
        map.put("title", title);
        map.put("message", message);
        map.put("type", type != null ? type.name() : "");
        map.put("isRead", isRead);
        map.put("createdAt", createdAt != null ? createdAt : new Date());
        map.put("updatedAt", updatedAt != null ? updatedAt : new Date());
        map.put("relatedId", relatedId);
        map.put("deepLink", deepLink);
        return map;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", userId='" + userId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                ", relatedId='" + relatedId + '\'' +
                ", deepLink='" + deepLink + '\'' +
                '}';
    }
}
