package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.BillStatus;

import java.util.Date;

public class Bill {
    public Long      billId;       // PK
    public Long      ownerBill;    // Users.userId
    public String    billType;
    public String    provider;
    public Double    amount;
    public Date dueDate;
    public BillStatus status;      // NEW | PAID | CANCELLED

    public Bill() {}
}
