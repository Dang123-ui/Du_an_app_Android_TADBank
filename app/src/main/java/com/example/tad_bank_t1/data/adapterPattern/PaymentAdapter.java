package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;

public class PaymentAdapter extends AbstractFirestoreAdapter<com.example.tad_bank_t1.data.model.OtpCode> {
    public PaymentAdapter() {super(FirestorePaths.PAYMENTS);}
}
