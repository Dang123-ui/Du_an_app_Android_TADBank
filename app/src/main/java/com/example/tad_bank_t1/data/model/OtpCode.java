package com.example.tad_bank_t1.data.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class OtpCode {

    private String otpId;
    private String userId;
    private String code;
    private String purpose;
    private Date expiresAt;
    private Date usedAt;

    @ServerTimestamp
    private Date createdAt;

    public OtpCode() {}

    public OtpCode(String otpId, String userId, String code, String purpose,
                   Date expiresAt, Date usedAt) {
        this.otpId = otpId;
        this.userId = userId;
        this.code = code;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
    }

    public String getOtpId() { return otpId; }
    public void setOtpId(String otpId) { this.otpId = otpId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }

    public Date getUsedAt() { return usedAt; }
    public void setUsedAt(Date usedAt) { this.usedAt = usedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
