package com.example.tad_bank_t1.data.model;

import java.util.Date;

public class OtpCode {
    public String otpId;           // PK
    public String userId;          // FK -> Users.userId
    public String code;
    public String purpose;         // LOGIN | TRANSFER
    public Date expiresAt;
    public Date   usedAt;
    public OtpCode() {}
}
