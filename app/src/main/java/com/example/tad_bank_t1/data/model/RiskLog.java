package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.UserRisk;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RiskLog implements Serializable {

    @DocumentId
    public String id;

    // FK -> User.userId
    public String uid;

    public UserRisk fromRisk;
    public UserRisk toRisk;

    // nguyên nhân thay đổi
    public String reason;

    // thời gian thay đổi
    public Date createdAt;

    public RiskLog() {}

    public RiskLog(String uid, UserRisk fromRisk, UserRisk toRisk, String reason) {
        this.uid = uid;
        this.fromRisk = fromRisk;
        this.toRisk = toRisk;
        this.reason = reason;
        this.createdAt = new Date();
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        putIfNotNull(map, "uid", uid);
        putIfNotNull(map, "fromRisk", fromRisk != null ? fromRisk.name() : null);
        putIfNotNull(map, "toRisk", toRisk != null ? toRisk.name() : null);
        putIfNotNull(map, "reason", reason);
        putIfNotNull(map, "createdAt", createdAt);
        return map;
    }

    private static void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) map.put(key, value);
    }
}
