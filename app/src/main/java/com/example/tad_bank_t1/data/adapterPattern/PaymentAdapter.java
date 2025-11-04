package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.Payment;

public class PaymentAdapter extends AbstractFirestoreAdapter<Payment> {
    public PaymentAdapter() {super(FirestorePaths.PAYMENTS);}
}
