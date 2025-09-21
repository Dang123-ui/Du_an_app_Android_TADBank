package com.example.tad_bank_t1.data.model;

import java.util.Date;

public class Payment {
    public Long   paymentId;    // PK
    public Long   billId;       // FK -> Bills.billId
    public String gateway;
    public String gatewayRef;
    public Double paidAmount;
    public Date createAt;

    public Payment() {}
}
