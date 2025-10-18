package com.example.tad_bank_t1.data.adapterPattern.users;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;

public class UserAdapter extends AbstractFirestoreAdapter<com.example.tad_bank_t1.data.model.User>{
    public UserAdapter() {
        super(FirestorePaths.USERS);
    }

}
