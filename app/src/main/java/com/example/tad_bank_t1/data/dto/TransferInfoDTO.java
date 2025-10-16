package com.example.tad_bank_t1.data.dto;

public class TransferInfoDTO {
    private String debitAccount;
    private  String beneficiaryAccount;
    private String beneficiaryName;
    private  String beneficiaryBank;
    private  String contentTransfer;
    private  double amount;
    private  double feeTransfer;
    private String methodTransfer;

    public TransferInfoDTO(){}

    public TransferInfoDTO(String debitAccount, String beneficiaryAccount, String beneficiaryName, String beneficiaryBank,
                           String contentTransfer, double amount, double feeTransfer, String methodTransfer) {
        this.beneficiaryAccount = beneficiaryAccount;
        this.beneficiaryName = beneficiaryName;
        this.debitAccount = debitAccount;
        this.beneficiaryBank = beneficiaryBank;
        this.contentTransfer = contentTransfer;
        this.amount = amount;
        this.feeTransfer = feeTransfer;
        this.methodTransfer = methodTransfer;
    }

    public String getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(String debitAccount) {
        this.debitAccount = debitAccount;
    }

    public String getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public void setBeneficiaryAccount(String beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
    }

    public String getContentTransfer() {
        return contentTransfer;
    }

    public void setContentTransfer(String contentTransfer) {
        this.contentTransfer = contentTransfer;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getFeeTransfer() {
        return feeTransfer;
    }

    public void setFeeTransfer(double feeTransfer) {
        this.feeTransfer = feeTransfer;
    }

    public String getMethodTransfer() {
        return methodTransfer;
    }

    public void setMethodTransfer(String methodTransfer) {
        this.methodTransfer = methodTransfer;
    }
}
