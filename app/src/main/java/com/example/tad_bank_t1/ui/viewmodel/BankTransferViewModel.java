package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.ui.form.BankTransferForm;

public class BankTransferViewModel extends ViewModel {
    private MutableLiveData<BankTransferForm> _form = new MutableLiveData<>();

    public BankTransferViewModel() {
        _form.setValue(new BankTransferForm());
    }

    public MutableLiveData<BankTransferForm> getForm() {
        return _form;
    }

    public void setForm(BankTransferForm form) {
        _form.setValue(form);
    }

}
