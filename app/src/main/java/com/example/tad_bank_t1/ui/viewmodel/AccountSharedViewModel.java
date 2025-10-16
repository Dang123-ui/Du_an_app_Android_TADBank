package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountSharedViewModel extends ViewModel {
    private final MutableLiveData<String> debitAccountNumber = new MutableLiveData<>();

    public void setDebitAccountNumberCurrent(String accountNumberCurrent) {
        debitAccountNumber.setValue(accountNumberCurrent);
    }

    public LiveData<String> getAccountNumberCurrent() {
        return debitAccountNumber;
    }
}