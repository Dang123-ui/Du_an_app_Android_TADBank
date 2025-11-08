package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.ExternalAccount;

public class ExternalAccountAdapter extends AbstractFirestoreAdapter<ExternalAccount> {
    public ExternalAccountAdapter(){
        super(FirestorePaths.EXTERNAL_ACCOUNTS);
    }
}