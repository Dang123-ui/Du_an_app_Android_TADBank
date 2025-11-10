package com.example.tad_bank_t1.data.repository.bank;

import com.example.tad_bank_t1.data.adapterPattern.BankAdapter;
import com.example.tad_bank_t1.data.adapterPattern.account.AccountAdapter;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Bank;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseBankRepository implements BankRepository {
    private final BankAdapter adapter = new BankAdapter();

    @Override
    public Task<Bank> getById(String id) {
        return adapter.get(id, Bank.class);
    }

    @Override
    public Task<List<Bank>> getAll() {
        return adapter.query().get().continueWith(task -> {
                if (!task.isSuccessful() || task.getResult() == null || task.getResult().isEmpty()) {
                    return Collections.emptyList();
                }

                List<Bank> banks = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    Bank obj = doc.toObject(Bank.class);
                    if (obj != null) {
                        banks.add(obj);
                    }
                }
                return banks;
            }
        );
    }

    @Override
    public Task<List<Bank>> searchRealtime(String key) {
        Query q = adapter.query()
                .where(
                       Filter.or(
                               Filter.equalTo("bankName", key),
                               Filter.equalTo("bankCode", key),
                               Filter.equalTo("bankLongName", key)
                       )
                );

        return adapter.where(q).continueWith(task -> {
                    if (!task.isSuccessful() || task.getResult() == null || task.getResult().isEmpty()) {
                        return Collections.emptyList();
                    }

                    List<Bank> banks = new ArrayList<>();
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        Bank obj = doc.toObject(Bank.class);
                        if (obj != null) {
                            banks.add(obj);
                        }
                    }
                    return banks;
                }
        );
    }
}
