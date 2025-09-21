package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.Role;
import com.example.tad_bank_t1.data.model.enums.UserStatus;

import java.util.Date;

public class User {
    public Long   userId;
    public String username;
    public String email;
    public String phone;
    public String password;
    public Role role;
    public UserStatus status;
    public Date createdAt;
    public User() {}
}
