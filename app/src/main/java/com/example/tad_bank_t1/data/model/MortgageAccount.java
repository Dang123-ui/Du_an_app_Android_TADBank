package com.example.tad_bank_t1.data.model;

public class MortgageAccount {
    public Long   accountId;        // PK == FK -> Accounts.accountId
    public Double principalAmount;
    public Double interestRate;
    public Integer termMonths;
    public String  startDate;       // yyyy-MM-dd
    public String  dueDate;         // yyyy-MM-dd
    public Long    officerId;       // Users.userId (OFFICER)

    public MortgageAccount() {}
}
