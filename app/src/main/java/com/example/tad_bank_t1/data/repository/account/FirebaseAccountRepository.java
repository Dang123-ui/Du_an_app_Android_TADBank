package com.example.tad_bank_t1.data.repository.account;

import android.app.DownloadManager;

import com.example.tad_bank_t1.data.adapterPattern.account.AccountAdapter;
import com.example.tad_bank_t1.data.model.Account;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseAccountRepository implements AccountRepository {
    private final AccountAdapter adapter = new AccountAdapter();

    @Override
    public Task<String> create(Account account) {
        return adapter.addAutoId(account);
    }

    @Override
    public Task<Void> update(String id, Account account) {
        return adapter.set(id, account);
    }

    @Override
    public Task<Void> delete(String id) {
        return adapter.delete(id);
    }

    @Override
    public Task<Account> getById(String id) {
        return adapter.get(id, Account.class);
    }

    @Override
    public Task<Account> getByUserId(String userId) {
        Query q = adapter.query()
                .whereEqualTo("userId", userId).limit(1);
        return adapter.where(q).continueWith(t -> {
            if (!t.isSuccessful() || t.getResult() == null || t.getResult().isEmpty()) {
                return null;
            }
            return t.getResult().getDocuments().get(0).toObject(Account.class);
        });
    }

    @Override
    public Task<Account> getByAccountNumber(String accountNumber) {
        Query q = adapter.query()
                .whereEqualTo("accountNumber", accountNumber)
                .limit(1);
        return adapter.where(q).continueWith(t -> {
            if (!t.isSuccessful() || t.getResult() == null || t.getResult().isEmpty()) {
                return null;
            }
            return t.getResult().getDocuments().get(0).toObject(Account.class);
        });
    }

    @Override
    public Task<List<Account>> getAccountsByUserId(String userId) {
        Query q = adapter.query()
                .whereEqualTo("userId", userId);

        return adapter.where(q).continueWith(t -> {
            if (!t.isSuccessful() || t.getResult() == null || t.getResult().isEmpty()) {
                return Collections.emptyList();
            }

            List<Account> accounts = new ArrayList<>();
            for (DocumentSnapshot doc : t.getResult().getDocuments()) {
                Account acc = doc.toObject(Account.class);
                if (acc != null) accounts.add(acc);
            }
            return accounts;
        });
    }
    @Override
    public Task<Boolean> isAccountNumberAvailable(String accountNumber) {
        Query query = adapter.query().whereEqualTo("accountNumber", accountNumber).limit(1);
        return adapter.where(query).continueWith(t -> {
            if (!t.isSuccessful() || t.getResult() == null) return false;
            return t.getResult().isEmpty();
        });
    }

    @Override
    public ListenerRegistration listenAccountsByUserId(String userId, OnAccountsChanged listener) {
        Query q = adapter.query().whereEqualTo("userId", userId);
        return q.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                if(listener != null) listener.onError(e);
                return;
            }
            List<Account> accounts = new ArrayList<>();
            if(snapshot != null) {
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    Account acc = doc.toObject(Account.class);
                    if (acc != null) accounts.add(acc);
                }
            }
            if (listener != null) listener.onChanged(accounts);
        });
    }
}
