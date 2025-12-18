package com.example.tad_bank_t1.util;

import com.example.tad_bank_t1.data.model.OtpCode;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class OtpUtil {
    // otp 6 chu so
    public static OtpCode generateOtp(String userId, String purpose){
        OtpCode otp = new OtpCode();
        otp.setUserId(userId);
        otp.setPurpose(purpose);
        otp.setCode(generateRandomOtp6());

        Date createdAt = new Date();
        otp.setCreatedAt(createdAt);
        otp.setExpiresAt(new Date(createdAt.getTime() + TadConstants.OTP_TTL_MS));
        otp.setUsedAt(null);
        return otp;
    }

    public static String generateRandomOtp6() {
        int val = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format(Locale.getDefault(), "%06d", val);
    }
}
