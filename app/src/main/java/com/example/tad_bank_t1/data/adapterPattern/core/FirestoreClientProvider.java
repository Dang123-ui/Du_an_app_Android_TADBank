package com.example.tad_bank_t1.data.adapterPattern.core;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public final class FirestoreClientProvider {
    private static volatile FirebaseFirestore db;
    private static volatile FirebaseAuth auth;
    private static volatile FirebaseStorage storage;
    public static FirebaseFirestore db() {
        if (db == null) {
            synchronized (FirestoreClientProvider.class) {
                if (db == null) db = FirebaseFirestore.getInstance();
            }
        }
        return db;
    }
    public static FirebaseAuth auth() {
        if (auth == null) {
            synchronized (FirestoreClientProvider.class) {
                if (auth == null) auth = FirebaseAuth.getInstance();
            }
        }
        return auth;
    }
    public static FirebaseStorage storage() {
        if (storage == null) {
            synchronized (FirestoreClientProvider.class) {
                if (storage == null) storage = FirebaseStorage.getInstance();
            }
        }
        return storage;
    }
}
