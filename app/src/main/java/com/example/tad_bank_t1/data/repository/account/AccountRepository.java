package com.example.tad_bank_t1.data.repository.account;

import com.example.tad_bank_t1.data.model.Account;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public interface AccountRepository {
    Task<Void> upsert(Account account);
    Task<Account> getById(String accountId);
    Task<List<Account>> getAccountsByUserId(String userId);
    Task<QuerySnapshot> searchByKeyword(String keyword, int limit);
}
