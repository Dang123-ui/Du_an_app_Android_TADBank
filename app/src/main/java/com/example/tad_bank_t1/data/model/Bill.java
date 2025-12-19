package com.example.tad_bank_t1.data.model;

import com.example.tad_bank_t1.data.model.enums.BillStatus;
import com.example.tad_bank_t1.data.model.remote.Customer;

import java.util.Date;

public class Bill {
    public String      billId;       // PK
    public String    providerId;
//    public Customer
    public String    period;
    public String dueDate;
    public Long    amount;
    public BillStatus status;      // NEW | PAID | CANCELLED

    public Bill() {}
}
