package com.example.tad_bank_t1.data.model;

public class SavingsAccount {
    public Long   accountId;    // PK == FK -> Accounts.accountId
    public Long   policyId;     // FK -> SavingRateProlicies.prolicyId
    public String startDate;    // yyyy-MM-dd
    public String maturityDate; // yyyy-MM-dd

    public SavingsAccount() {}
}
