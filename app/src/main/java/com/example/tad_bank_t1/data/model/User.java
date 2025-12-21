package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.Gender;
import com.example.tad_bank_t1.data.model.enums.Role;
import com.example.tad_bank_t1.data.model.enums.UserRisk;
import com.example.tad_bank_t1.data.model.enums.UserSegment;
import com.example.tad_bank_t1.data.model.enums.UserStatus;
import com.example.tad_bank_t1.data.model.enums.UserStatusOnlOff;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    @DocumentId
    public String userId;
    public String username;
    public String email;
    public String phone;
    public String password;
    public Role role;
    public UserStatus status;
    public Date createdAt;

    private String idNumber;
    private String fullName;
    private String address;
    private Date dateOfBirth;
    private String avatar;
    private String avatarUser;
    private UserStatusOnlOff statusOnlOff;
    private Date lastActiveAt;

    // ====== ADDED ======
    // segment & risk dùng enum
    private UserSegment segment;
    private UserRisk risk;

    // gender mặc định NAM
    private Gender gender = Gender.MALE;

    public User() {}

    public User(String uid) {
        this.userId = uid;
        this.status = UserStatus.ACTIVE;
        this.role = Role.CUSTOMER;
        this.createdAt = new Date();

        // defaults
        this.gender = Gender.MALE;
        this.segment = UserSegment.MASS;
        this.risk = UserRisk.LOW;
    }

    public User(String id, String username, String email, String phone, String password,
                Role role, UserStatus status, Date createdAt,
                String idNumber, String fullName, String address,
                Date dateOfBirth, String avatar,
                UserStatusOnlOff statusOnlOff, Date lastActiveAt,
                UserSegment segment, UserRisk risk, Gender gender) {

        this.userId = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;

        this.idNumber = idNumber;
        this.fullName = fullName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.avatar = avatar;
        this.statusOnlOff = statusOnlOff;
        this.lastActiveAt = lastActiveAt;

        this.segment = segment;
        this.risk = risk;
        this.gender = (gender == null) ? Gender.MALE : gender;
    }

    // Giữ lại constructor cũ để không phá code cũ
    public User(String id, String username, String email, String phone, String password, Role role, UserStatus status,
                Date createdAt, String idNumber, String fullName, String address,
                Date dateOfBirth, String avatar) {

        this.userId = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.idNumber = idNumber;
        this.fullName = fullName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.avatar = avatar;
        // defaults cho field mới
        this.gender = Gender.MALE;
        this.segment = UserSegment.MASS;
        this.risk = UserRisk.LOW;
        this.avatarUser = null;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("username", username);
        m.put("email", email);
        m.put("phone", phone);
        m.put("password", password);
        m.put("role", role);
        m.put("status", status);
        m.put("createdAt", createdAt);
        m.put("idNumber", idNumber);
        m.put("fullName", fullName);
        m.put("address", address);
        m.put("dateOfBirth", dateOfBirth);
        m.put("avatar", avatar);
        m.put("statusOnlOff", statusOnlOff);
        m.put("lastActiveAt", lastActiveAt);

        // ====== ADDED ======
        m.put("segment", segment);
        m.put("risk", risk);
        m.put("gender", gender);
        m.put("avatarUser", avatarUser);
        return m;
    }

    @Exclude
    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }

    public UserStatus getStatus() { return status; }

    public void setStatus(UserStatus status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getIdNumber() { return idNumber; }

    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public Date getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAvatar() { return avatar; }

    public void setAvatar(String avatar) { this.avatar = avatar; }

    public UserStatusOnlOff getStatusOnlOff() { return statusOnlOff; }

    public void setStatusOnlOff(UserStatusOnlOff statusOnlOff) { this.statusOnlOff = statusOnlOff; }

    public Date getLastActiveAt() { return lastActiveAt; }

    public void setLastActiveAt(Date lastActiveAt) { this.lastActiveAt = lastActiveAt; }

    // ====== ADDED getters/setters ======
    public UserSegment getSegment() { return segment; }

    public void setSegment(UserSegment segment) { this.segment = segment; }

    public UserRisk getRisk() { return risk; }

    public void setRisk(UserRisk risk) { this.risk = risk; }

    public Gender getGender() { return gender; }

    public void setGender(Gender gender) {
        this.gender = (gender == null) ? Gender.MALE : gender;
    }

    public String getAvatarUser() {
        return avatarUser;
    }

    public void setAvatarUser(String avatarUser) {
        this.avatarUser = avatarUser;
    }
}
