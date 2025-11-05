package com.example.tad_bank_t1.data.adapterPattern.users;

import androidx.annotation.NonNull;

import com.example.tad_bank_t1.data.adapterPattern.core.AbstractFirestoreAdapter;
import com.example.tad_bank_t1.data.adapterPattern.core.FirestorePaths;
import com.example.tad_bank_t1.data.model.User;
import com.google.android.gms.tasks.Task;

public class UserAdapter extends AbstractFirestoreAdapter<com.example.tad_bank_t1.data.model.User>{
    public UserAdapter() {
        super(FirestorePaths.USERS);
    }
    public Task<Void> addWithId(@NonNull User user) {
        return super.addWithId(user.getUserId(), user);
    }

}
