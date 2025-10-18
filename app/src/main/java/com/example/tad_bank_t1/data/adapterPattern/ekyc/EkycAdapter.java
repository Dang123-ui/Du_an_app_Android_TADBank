package com.example.tad_bank_t1.data.adapterPattern.ekyc;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;

public class EkycAdapter extends AbstractFirestoreAdapter<com.example.tad_bank_t1.data.model.Ekyc> {
    public EkycAdapter() {
        super(FirestorePaths.EKYC);
    }
}
