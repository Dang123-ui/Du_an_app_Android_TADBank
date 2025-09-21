package com.example.tad_bank_t1;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
