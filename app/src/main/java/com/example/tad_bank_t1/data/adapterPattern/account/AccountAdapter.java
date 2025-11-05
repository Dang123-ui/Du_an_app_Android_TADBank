package com.example.tad_bank_t1.data.adapterPattern.account;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.Account;

public class AccountAdapter extends AbstractFirestoreAdapter<Account> {
    public AccountAdapter() {
        super(FirestorePaths.ACCOUNTS);
    }
}
