package com.example.tad_bank_t1.data.adapterPattern.otp;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;

public class OtpCodeAdapter extends AbstractFirestoreAdapter<com.example.tad_bank_t1.data.model.OtpCode> {
    public OtpCodeAdapter() {super(FirestorePaths.OTP_CODES);}
}
