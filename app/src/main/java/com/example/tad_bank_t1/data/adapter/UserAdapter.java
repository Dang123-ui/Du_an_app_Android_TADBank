package com.example.tad_bank_t1.data.adapter;

import com.example.tad_bank_t1.data.model.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class UserAdapter {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private UserAdapter() {}
    public static Map<String, Object> toFirestoreMap(
            User user,
            String uid,
            String address,
            String fullName,
            java.util.Date dateOfBirth,
            String avatar
    ){
        Map<String, Object> data = new HashMap<>();
        data.put("username", user.username);
        data.put("phone", user.phone);
        data.put("email", user.email);
        data.put("role", user.role.name());
        data.put("status", user.status.name());
        data.put("createdAt", new Timestamp(user.createdAt.getTime()));
        data.put("uid", uid);
        data.put("address", address);
        data.put("fullName", fullName);
        if (dateOfBirth != null) data.put("dateOfBirth", dateOfBirth);
        data.put("avatar", avatar == null ? "" : avatar);

        return data;
    }
}

