package com.example.tad_bank_t1.data.repository.account;

import com.example.tad_bank_t1.data.model.Account;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public interface AccountRepository {
    Task<String> create(Account account);
    Task<Void> update(String id, Account account);
    Task<Void> delete(String id);
    Task<Account> getById(String id);
    Task<Account> getByUserId(String userId);
    Task<Account> getByAccountNumber(String accountNumber);
    Task<Boolean> isAccountNumberAvailable(String accountNumber);
    Task<List<Account>> getAccountsByUserId(String userId);
    ListenerRegistration listenAccountsByUserId(String userId, OnAccountsChanged listener);
    interface OnAccountsChanged {
        void onChanged(List<Account> accounts);
        void onError(Exception e);
    }
}
