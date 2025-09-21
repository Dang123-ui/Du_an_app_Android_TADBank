package com.example.tad_bank_t1.data.model;

import java.util.Date;

public class SavingsRatePolicy {
    public Long   prolicyId;     // PK (giữ tên trong sơ đồ)
    public String policyName;
    public Double interestRate;  // 5,2 %
    public Integer termMonths;
    public Long   updatedBy;     // userId OFFICER
    public Date updatedAt;

    public SavingsRatePolicy() {}
}
