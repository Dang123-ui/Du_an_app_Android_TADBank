package com.example.tad_bank_t1.saving.model;

public enum CapitalizationMethod {
    MONTHLY,    // Nhận lãi hàng tháng
    QUARTERLY,  // Nhận lãi hàng quý
    MATURITY;   // Nhận lãi cuối kỳ

    public String toDisplayString() {
        switch (this) {
            case MONTHLY:
                return "Nhận lãi hàng tháng";
            case QUARTERLY:
                return "Nhận lãi hàng quý";
            case MATURITY:
                return "Nhận lãi cuối kỳ";
            default:
                return "";
        }
    }
}

