package com.example.tad_bank_t1.data.model;

public class Bank {
    private String bankId;
    private String bankName;
    private String bankLongName;
    private String bankImageUrl;


    public Bank(String bankImageUrl, String bankName, String bankId, String bankLongName) {
        this.bankImageUrl = bankImageUrl;
        this.bankName = bankName;
        this.bankId = bankId;
        this.bankLongName = bankLongName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankImageUrl() {
        return bankImageUrl;
    }

    public void setBankImageUrl(String bankImageUrl) {
        this.bankImageUrl = bankImageUrl;
    }

    public String getBankLongName() {
        return bankLongName;
    }

    public void setBankLongName(String bankLongName) {
        this.bankLongName = bankLongName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
