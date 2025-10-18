package com.example.tad_bank_t1.data.adapterPattern.core;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public interface FirestoreAdapter<T> {
    CollectionReference col();
    DocumentReference doc(String id);
    Task<String> addAutoId(T entity);
    Task<Void> set(String id, T entity);
    Task<Void> merge(String id, T partial);
    Task<T> get(String id, Class<T> clazz);
    Task<QuerySnapshot> where(Query q);
    Query query();
    Task<Void> delete(String id);
}
