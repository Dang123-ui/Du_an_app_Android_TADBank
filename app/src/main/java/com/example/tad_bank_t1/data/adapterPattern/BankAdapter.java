package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.Bank;

public class BankAdapter extends AbstractFirestoreAdapter<Bank> {
    public BankAdapter() {super(FirestorePaths.BANKS);}
}
