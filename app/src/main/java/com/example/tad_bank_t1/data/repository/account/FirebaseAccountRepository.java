package com.example.tad_bank_t1.data.repository.account;

import com.example.tad_bank_t1.data.adapterPattern.AccountAdapter;
import com.example.tad_bank_t1.data.model.Account;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseAccountRepository implements AccountRepository {
    private final AccountAdapter adapter = new AccountAdapter();

    @Override
    public Task<Void> upsert(Account account) {
        return adapter.merge(account.getAccountId(), account);
    }

    @Override
    public Task<Account> getById(String accountId) {
        return adapter.get(accountId, Account.class);
    }

    @Override
    public Task<List<Account>> getAccountsByUserId(String userId) {
        Query q = adapter.query().whereEqualTo("userId", userId);

        return adapter.where(q).continueWith(task -> {
            List<Account> accountList = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                // Lấy từng DocumentSnapshot trong QuerySnapshot
                for (var doc : task.getResult().getDocuments()) {
                    Account acc = doc.toObject(Account.class);
                    if (acc != null) accountList.add(acc);
                }
            }
            return accountList;
        });
    }

    // real time
    public ListenerRegistration listenAccountsByUserId(String userId, OnAccountsChanged listener) {
        Query q = adapter.query().whereEqualTo("userId", userId);
        return q.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                listener.onError(e);
                return;
            }
            List<Account> list = new ArrayList<>();
            if (snapshot != null && !snapshot.isEmpty()) {
                for (var doc : snapshot.getDocuments()) {
                    Account acc = doc.toObject(Account.class);
                    if (acc != null) list.add(acc);
                }
            }
            listener.onChanged(list);
        });
    }

    public interface OnAccountsChanged {
        void onChanged(List<Account> accounts);
        void onError(Exception e);
    }


    @Override
    public Task<QuerySnapshot> searchByKeyword(String keyword, int limit) {
        return adapter.where(
                adapter.query().orderBy("fullName")
                        .startAt(keyword).endAt(keyword + "\uf8ff").limit(limit)
        );
    }
}
