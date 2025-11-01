package com.example.tad_bank_t1.data.adapterPattern.core;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public abstract class AbstractFirestoreAdapter<T> implements FirestoreAdapter<T>{
    protected final FirebaseFirestore db = FirestoreClientProvider.db();
    protected final String collection;
    protected AbstractFirestoreAdapter(String collection) {
        this.collection = collection;
    }
    @Override
    public CollectionReference col() {
        return db.collection(collection);
    }
    @Override
    public DocumentReference doc(String id) {
        return col().document(id);
    }
    @Override
    public Task<String> addAutoId(T entity) {
        DocumentReference ref = col().document();
        return ref.set(entity, SetOptions.merge()).continueWith(t -> ref.getId());
    }
    @Override
    public Task<Void> set(String id, T entity) {
        return doc(id).set(entity, SetOptions.merge());
    }
    @Override
    public Task<Void> merge(String id, T partial) {
        return doc(id).set(partial, SetOptions.merge());
    }
    @Override
    public Task<T> get(String id, Class<T> clazz) {
        return doc(id).get().continueWith(t -> {
            DocumentSnapshot d = t.getResult();
            return (d != null && d.exists()) ? d.toObject(clazz) : null;
        });
    }
    @Override
    public Task<Void> delete(String id) {
        return doc(id).delete();
    }

    @Override
    public Task<QuerySnapshot> where(Query q) {
        return q.get();
    }

    @Override
    public Query query() {
        return col();
    }
}
