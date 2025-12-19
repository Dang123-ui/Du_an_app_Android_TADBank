package com.example.tad_bank_t1.ui.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PaymentReturnViewModel extends ViewModel {
    private final MutableLiveData<Uri> returnUri = new MutableLiveData<>();
    public LiveData<Uri> getReturnUri() { return returnUri; }
    public void publish(Uri uri) { returnUri.setValue(uri); }
}
