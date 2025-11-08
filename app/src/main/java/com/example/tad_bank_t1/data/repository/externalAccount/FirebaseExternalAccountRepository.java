package com.example.tad_bank_t1.data.repository.externalAccount;

import com.example.tad_bank_t1.data.adapterPattern.BankAdapter;
import com.example.tad_bank_t1.data.adapterPattern.ExternalAccountAdapter;
import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.ExternalAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseExternalAccountRepository implements ExternalAccountRepository {
    private final ExternalAccountAdapter adapter = new ExternalAccountAdapter();

    @Override
    public Task<ExternalAccount> getByAccountNumber(String accountNumber) {
        Query q = adapter.query()
                .whereEqualTo("accountNumber", accountNumber)
                .limit(1);

        return q.get().continueWith(task -> {
                    if (!task.isSuccessful() || task.getResult() == null || task.getResult().isEmpty()) {
                        return null;
                    }

                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    return doc.toObject(ExternalAccount.class);
                }
        );
    }

    @Override
    public Task<ExternalAccount> getByBankIdAndAccountNumber(String bankId, String accountNumber) {
        Query q = adapter.query()
                .whereEqualTo("bankId", bankId)
                .whereEqualTo("accountNumber", accountNumber)
                .limit(1);

        return q.get().continueWith(task -> {
                    if (!task.isSuccessful() || task.getResult() == null || task.getResult().isEmpty()) {
                        return null;
                    }

                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    return doc.toObject(ExternalAccount.class);
                }
        );
    }
}
