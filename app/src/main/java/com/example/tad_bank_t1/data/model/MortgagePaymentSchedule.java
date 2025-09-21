package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.MortgageInstallmentStatus;

public class MortgagePaymentSchedule {
    public Long   scheduleId;     // PK
    public Long   accountId;      // FK -> MortgageAccounts.accountId
    public String dueDate;        // yyyy-MM-dd
    public Double amountDue;
    public Double amountPaid;
    public MortgageInstallmentStatus status; // PENDING | PAID | OVERDUE

    public MortgagePaymentSchedule() {}
}
