package com.example.tad_bank_t1.data.repository.otp;

import com.example.tad_bank_t1.data.model.OtpCode;
import com.google.android.gms.tasks.Task;

public interface OtpCodeRepository {
    Task<String> create(OtpCode otp);
    Task<Boolean> verifyAndConsume(String userId, String purpose, String code);
    Task<OtpCode> getById(String otpId);
}
