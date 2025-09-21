package com.example.tad_bank_t1.data.model;

public class Profile {
    public Long   profileId;     // PK
    public Long   userId;        // unique FK -> Users.userId
    public String idNumber;      // unique
    public String fullName;
    public String phone;
    public String address;
    public String dataOfBirth;   // yyyy-MM-dd (đúng theo tên trong sơ đồ)
    public String avatar;

    public Profile() {}
}
