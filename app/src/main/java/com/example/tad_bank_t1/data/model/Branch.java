package com.example.tad_bank_t1.data.model;

public class Branch {
    public Long   brandId;     // PK (giữ nguyên tên "brandId" theo sơ đồ)
    public String name;
    public String type;        // BRANCH | ATM
    public String address;
    public String province;
    public Double latitude;
    public Double longitude;
    public String phone;
    public String email;

    public Branch() {}
}
