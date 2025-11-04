package com.example.tad_bank_t1.data.model;

public class Bank {
    private String bankId;
    private String bankName;
    private String bankCode;
    private String bankLongName;
    private String bankImageUrl;


    public Bank(String bankId, String bankImageUrl, String bankName, String bankCode, String bankLongName) {
        this.bankId = bankId;
        this.bankImageUrl = bankImageUrl;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.bankLongName = bankLongName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankLongName() {
        return bankLongName;
    }

    public void setBankLongName(String bankLongName) {
        this.bankLongName = bankLongName;
    }

    public String getBankImageUrl() {
        return bankImageUrl;
    }

    public void setBankImageUrl(String bankImageUrl) {
        this.bankImageUrl = bankImageUrl;
    }
}
