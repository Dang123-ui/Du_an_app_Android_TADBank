package com.example.tad_bank_t1.data.adapterPattern;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.Account;
import com.google.android.gms.tasks.Task;

public class AccountAdapter extends AbstractFirestoreAdapter<Account> {
    public AccountAdapter() {
        super(FirestorePaths.ACCOUNTS);
    }
}
