package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.Bill;

public class BillAdapter extends AbstractFirestoreAdapter<Bill> {
    public BillAdapter() {super(FirestorePaths.BILLS);}
}
