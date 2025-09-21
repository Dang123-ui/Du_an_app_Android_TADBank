package com.example.tad_bank_t1.data.model;

public class MfaFactor {
    public String factorID;        // PK
    public String userId;          // FK -> Users.userId
    public String type;            // "TOTP" | "SM"
    public String secretOrToken;
    public boolean enabled;

    public MfaFactor() {}
}
