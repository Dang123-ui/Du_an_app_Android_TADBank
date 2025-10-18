package com.example.tad_bank_t1.data.repository.ekyc;

import com.example.tad_bank_t1.data.model.Ekyc;
import com.google.android.gms.tasks.Task;

public interface EkycRepository {
    Task<String> create(Ekyc ekyc);
    Task<Void> update(String id, Ekyc ekyc);
    Task<Void> delete(String id);
    Task<Ekyc> getById(String id);
}
