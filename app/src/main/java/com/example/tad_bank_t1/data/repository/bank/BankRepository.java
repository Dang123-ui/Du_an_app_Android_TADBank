package com.example.tad_bank_t1.data.repository.bank;

import com.example.tad_bank_t1.data.model.Bank;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface BankRepository {

    Task<Bank> getById(String bankId);
    Task<List<Bank>> getAll();
    Task<List<Bank>> search(String key);
}
