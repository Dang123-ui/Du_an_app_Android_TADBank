package com.example.tad_bank_t1;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();

        Log.d("APP Debug", "Da chay App.java");
        FirebaseApp.initializeApp(this);
    }
}
