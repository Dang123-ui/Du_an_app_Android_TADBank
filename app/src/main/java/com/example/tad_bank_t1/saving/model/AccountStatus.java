package com.example.tad_bank_t1.saving.model;

public enum AccountStatus {
    ACTIVE,     // "Đang hoạt động"
    CLOSED;     // "Đã đóng"

    public String toDisplayString() {
        switch (this) {
            case ACTIVE:
                return "Đang hoạt động";
            case CLOSED:
                return "Đã đóng";
            default:
                return "";
        }
    }
}

