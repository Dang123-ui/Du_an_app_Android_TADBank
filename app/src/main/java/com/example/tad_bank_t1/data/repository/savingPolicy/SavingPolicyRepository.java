package com.example.tad_bank_t1.data.repository.savingPolicy;

import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.model.SavingsRatePolicy;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface SavingPolicyRepository {
    // create
    void create(SavingsRatePolicy savingPolicy, ResultCallback<SavingsRatePolicy> callback);

    // get all
    void getAll(ResultCallback<List<SavingsRatePolicy>> callback);

    // get by id
    void getById(String savingPolicyId, ResultCallback<SavingsRatePolicy> callback);

    // update
    void update(SavingsRatePolicy savingPolicy, ResultCallback<SavingsRatePolicy> callback);

    // // delete
    // void delete(int id);
}
