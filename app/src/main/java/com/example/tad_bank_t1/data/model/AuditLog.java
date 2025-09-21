package com.example.tad_bank_t1.data.model;

import java.util.Date;

public class AuditLog {
    public Long   logId;      // PK
    public Long   userId;     // Users.userId (CUS|OFFICER)
    public String action;
    public String entity;     // "Users" | "Accounts" | "Transactions"...
    public Long   entityId;
    public String oldValue;   // JSON/text
    public String newValue;   // JSON/text
    public Date createdAt;

    public AuditLog() {}
}
