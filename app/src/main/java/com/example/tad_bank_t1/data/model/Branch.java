package com.example.tad_bank_t1.data.model;

public class Branch {
    public int brandId;     // PK (giữ nguyên tên "brandId" theo sơ đồ)
    public String name;
    public String type;        // BRANCH | ATM
    public String address;
    public String province;
    public Double latitude;
    public Double longitude;

    public Branch(int brandId, String name, String type, String address, String province, Double latitude, Double longitude, String phone, String email) {
        this.brandId = brandId;
        this.name = name;
        this.type = type;
        this.address = address;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.email = email;
    }

    public String phone;
    public String email;

    public Branch() {}

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
