package com.example.tad_bank_t1.data.repository.account;

import com.example.tad_bank_t1.data.model.Account;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public interface AccountRepository {
    // Task<Void> upsert(Account account);

    Task<String> create(Account account);

    Task<Void> update(String id, Account account);

    Task<Void> delete(String id);

    Task<Account> getById(String id);

    Task<Account> getByUserId(String userId);

    Task<Account> getById(String accountId);

    Task<List<Account>> getAccountsByUserId(String userId);

    Task<QuerySnapshot> searchByKeyword(String keyword, int limit);

    Task<Boolean> isAccountNumberAvailable(String accountNumber);
}
