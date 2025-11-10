package com.example.tad_bank_t1.ui.form;

import com.example.tad_bank_t1.data.model.Bank;

public class BankTransferForm {
    private String sourceAccountId;
    private String sourceAccountNumber;
    private String sourceAccountName;
    private String destinationAccountNumber;
    private String destinationAccountName;
    private Bank destinationBank;
    private Double amount;
    private String description;

    public BankTransferForm(){}

    public BankTransferForm(String sourceAccountId, String sourceAccountNumber, String sourceAccountName, String destinationAccountNumber, String destinationAccountName, Bank destinationBank, Double amount, String description) {
        this.sourceAccountId = sourceAccountId;
        this.sourceAccountNumber = sourceAccountNumber;
        this.sourceAccountName = sourceAccountName;
        this.destinationAccountNumber = destinationAccountNumber;
        this.destinationAccountName = destinationAccountName;
        this.destinationBank = destinationBank;
        this.amount = amount;
        this.description = description;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getSourceAccountName() {
        return sourceAccountName;
    }

    public void setSourceAccountName(String sourceAccountName) {
        this.sourceAccountName = sourceAccountName;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public String getDestinationAccountName() {
        return destinationAccountName;
    }

    public void setDestinationAccountName(String destinationAccountName) {
        this.destinationAccountName = destinationAccountName;
    }

    public Bank getDestinationBank() {
        return destinationBank;
    }

    public void setDestinationBank(Bank destinationBank) {
        this.destinationBank = destinationBank;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
