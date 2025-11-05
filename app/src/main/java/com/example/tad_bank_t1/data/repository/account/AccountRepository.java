package com.example.tad_bank_t1.data.repository.account;

import com.example.tad_bank_t1.data.model.Account;
import com.google.android.gms.tasks.Task;

public interface AccountRepository {
    Task<String> create(Account account);
    Task<Void> update(String id, Account account);
    Task<Void> delete(String id);
    Task<Account> getById(String id);
    Task<Account> getByUserId(String userId);
    Task<Boolean> isAccountNumberAvailable(String accountNumber);
}
