package com.example.tad_bank_t1.data.model;

import java.util.Date;

public class TransferBeneficiary {
    public Long   beneficiaryId;     // PK (auto increment ở RDBMS)
    public Long   accountId;         // FK -> Accounts.accountId
    public String accountNumber;
    public String bankCode;
    public String beneficiaryName;
    public Date createdAt;

    public TransferBeneficiary() {}
}
