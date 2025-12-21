package com.example.tad_bank_t1.data.repository.account;

import com.example.tad_bank_t1.data.model.Account;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AccountRepository {
    Task<String> create(Account account);
    Task<Void> update(String id, Account account);
    Task<Void> delete(String id);
    Task<Account> getById(String id);
    Task<Account> getByUserId(String userId);
    Task<Boolean> isAccountNumberAvailable(String accountNumber);
    Task<List<Account>> getAccountsByUserId(String userId);
    ListenerRegistration listenAccountsByUserId(String userId, OnAccountsChanged listener);
    Task<Void> freezeAccountsByUserId(String userId);
    Task<Void> unfreezeAccountsByUserId(String userId);
    interface OnAccountsChanged {
        void onChanged(List<Account> accounts);
        void onError(Exception e);
    }
    interface AccountUserMapCallback {
        void onSuccess(Map<String, String> accountIdToUserId);
        void onFailure(Exception e);
    }
    void getUserIdsByAccountIds(Set<String> accountIds, AccountUserMapCallback callback);

}
