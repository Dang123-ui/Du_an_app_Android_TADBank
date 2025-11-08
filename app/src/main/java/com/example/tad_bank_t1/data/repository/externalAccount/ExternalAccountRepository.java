package com.example.tad_bank_t1.data.repository.externalAccount;

import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.ExternalAccount;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface ExternalAccountRepository {

    Task<ExternalAccount> getByAccountNumber(String accountNumber);
    Task<ExternalAccount> getByBankIdAndAccountNumber(String bankId, String accountNumber);
//    Task<List<ExternalAccount>> getAll();
//    Task<List<ExternalAccount>> search(String key);
}
