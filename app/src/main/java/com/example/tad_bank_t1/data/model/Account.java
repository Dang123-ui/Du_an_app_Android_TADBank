package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.example.tad_bank_t1.data.model.enums.AccountType;

import java.util.Date;

public class Account {
    public Long         accountId;     // PK
    public Long         userId;        // FK -> Users.userId
    public Long         branchId;      // FK -> Branches.brandId
    public AccountType type;          // CHECKING | SAVING | MORTAGE
    public String       accountNumber; // unique
    public String       currency;      // default VND
    public Double       balance;       // decimal(10,3)
    public AccountStatus status;       // OPEN | FROZEN | CLOSE
    public Date created_at;

    public Account() {}
}
