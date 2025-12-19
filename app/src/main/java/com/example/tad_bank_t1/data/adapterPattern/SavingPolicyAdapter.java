package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.SavingsRatePolicy;

public class SavingPolicyAdapter extends AbstractFirestoreAdapter<SavingsRatePolicy> {
    public SavingPolicyAdapter() {super(FirestorePaths.SAVING_RATE_POLICIES);}
}
