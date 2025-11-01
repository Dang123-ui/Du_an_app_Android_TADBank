package com.example.tad_bank_t1.data.repository.ekyc;

import com.example.tad_bank_t1.data.adapterPattern.core.FirestoreClientProvider;
import com.example.tad_bank_t1.data.adapterPattern.ekyc.EkycAdapter;
import com.example.tad_bank_t1.data.model.Ekyc;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
public class FirebaseEkycRepository implements EkycRepository{
    private final EkycAdapter adapter = new EkycAdapter();
    @Override
    public Task<String> create(Ekyc ekyc) {
        return adapter.addAutoId(ekyc);
    }

    @Override
    public Task<Void> update(String id, Ekyc ekyc) {
        return adapter.set(id, ekyc);
    }

    @Override
    public Task<Void> delete(String id) {
        return adapter.delete(id);
    }

    @Override
    public Task<Ekyc> getById(String id) {
        return adapter.get(id, Ekyc.class);
    }
}
