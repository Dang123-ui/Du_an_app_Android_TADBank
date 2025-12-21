package com.example.tad_bank_t1.data.repository.account;

import android.app.DownloadManager;

import com.example.tad_bank_t1.data.adapterPattern.account.AccountAdapter;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.model.enums.AccountStatus;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


    // --------------------------------
    // Cap nhat thong tin tai khoan
    // --------------------------------
    @Override
    public void updateAccount(Account account, ResultCallback<Account> callback) {
        account.setUpdatedAt(new Date());

        adapter.col()
                .document(account.getAccountId())
                .update(account.toMap())
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return adapter.col().document(account.getAccountId()).get();
                })
                .addOnSuccessListener(documentSnapshot -> {
                    Account obj = documentSnapshot.toObject(Account.class);
                    callback.onSuccess(obj);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }

    @Override
    public void getAccountByAccountNumber(String accountNumber, ResultCallback<Account> callback) {

        adapter.col()
                .whereEqualTo("accountNumber", accountNumber)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap == null || snap.isEmpty()) {
                        callback.onError("Không tìm thấy tài khoản"); // không tìm thấy
                        return;
                    }

                    DocumentSnapshot doc = snap.getDocuments().get(0);
                    Account account = doc.toObject(Account.class);
                    callback.onSuccess(account);  // trả về account
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }

    public void updateBalanceAccount(String accountNumber, Long amount, ResultCallback<Account> callback) {
        adapter.col()
                .whereEqualTo("accountNumber", accountNumber)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap == null || snap.isEmpty()) {
                        return; // không tìm thấy
                    }
                    DocumentSnapshot doc = snap.getDocuments().get(0);
                    Account account = doc.toObject(Account.class);

                    Long newBalance = account.getBalance() + amount;
                    account.setBalance(newBalance);
                    account.setUpdatedAt(new Date());


                    adapter.col()
                            .document(account.getAccountId())
                            .update("balance", newBalance)
                            .continueWithTask(task -> {
                                if (!task.isSuccessful()) throw task.getException();
                                return adapter.col().document(account.getAccountId()).get();
                            })
                            .addOnSuccessListener(documentSnapshot -> {
                                Account obj = documentSnapshot.toObject(Account.class);
                                callback.onSuccess(obj);
                            })
                            .addOnFailureListener(e -> {
                                // cập nhật thất bại
                                callback.onError(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    // không tìm thấy tài khoản
                    callback.onError(e.getMessage());
                });
    }


    // --------------------------------
    // start saving and mortgage
    // --------------------------------
    @Override
    public Task<List<Account>> getAccountsByUserIdAndType(String userId, String accountType) {
        Query query = adapter.query()
                .whereEqualTo("userId", userId);

        if (accountType != null){
            query = query.whereEqualTo("type", accountType);
        }

        return adapter.where(query).continueWith(task -> {
            if (!task.isSuccessful() || task.getResult() == null || task.getResult().isEmpty()) {
                return Collections.emptyList();
            }

            List<Account> accounts = new ArrayList<>();
            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                Account account = doc.toObject(Account.class);
                if (account != null) accounts.add(account);
            }
            return accounts;
        });
    }
    // --------------------------------
    // end saving and mortgage
    // --------------------------------
    @Override
    public void getUserIdsByAccountIds(Set<String> accountIds, AccountUserMapCallback callback) {
        if (accountIds == null || accountIds.isEmpty()) {
            callback.onSuccess(Collections.emptyMap());
            return;
        }

        final int BATCH_SIZE = 10;
        List<String> ids = new ArrayList<>(accountIds);

        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
            batches.add(ids.subList(i, Math.min(i + BATCH_SIZE, ids.size())));
        }

        Map<String, String> merged = new HashMap<>();
        java.util.concurrent.atomic.AtomicInteger done = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicBoolean failed = new java.util.concurrent.atomic.AtomicBoolean(false);

        for (List<String> batch : batches) {
            adapter.query()
                    .whereIn(FieldPath.documentId(), batch)  // <= luôn <= 10 phần tử
                    .get()
                    .addOnSuccessListener(snap -> {
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            String accId = doc.getString("accountId");
                            String userId = doc.getString("userId");
                            if (accId != null && userId != null) {
                                merged.put(accId, userId);
                            }
                        }

                        if (done.incrementAndGet() == batches.size() && !failed.get()) {
                            callback.onSuccess(merged);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (failed.compareAndSet(false, true)) {
                            callback.onFailure(e);
                        }
                    });
        }
    }

    @Override
    public Task<Void> freezeAccountsByUserId(String userId) {
        return updateAccountsStatusByUserId(userId, AccountStatus.FROZEN, false);
    }

    @Override
    public Task<Void> unfreezeAccountsByUserId(String userId) {
        return updateAccountsStatusByUserId(userId, AccountStatus.OPEN, true);
    }
    private Task<Void> updateAccountsStatusByUserId(String userId,
                                                    AccountStatus newStatus,
                                                    boolean skipClosed) {

        return adapter.query()
                .whereEqualTo("userId", userId)
                .get()
                .continueWithTask(t -> {
                    if (!t.isSuccessful() || t.getResult() == null) {
                        Exception e = t.getException() != null ? t.getException()
                                : new Exception("Query accounts failed");
                        return Tasks.forException(e);
                    }

                    List<DocumentSnapshot> docs = t.getResult().getDocuments();
                    if (docs.isEmpty()) return Tasks.forResult(null);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final int LIMIT = 450;

                    Task<Void> chain = Tasks.forResult(null);

                    for (int i = 0; i < docs.size(); i += LIMIT) {
                        final int start = i; // ✅ phải final để dùng trong lambda
                        final int end = Math.min(i + LIMIT, docs.size());

                        chain = chain.continueWithTask(x -> {
                            WriteBatch batch = db.batch();

                            for (int j = start; j < end; j++) {
                                DocumentSnapshot doc = docs.get(j);

                                if (skipClosed) {
                                    String cur = doc.getString("status");
                                    if (AccountStatus.CLOSED.name().equalsIgnoreCase(cur)) continue;
                                }

                                batch.update(
                                        doc.getReference(),
                                        "status", newStatus.name(),
                                        "updatedAt", FieldValue.serverTimestamp()
                                );
                            }

                            return batch.commit();
                        });
                    }

                    return chain;
                });
    }
}
