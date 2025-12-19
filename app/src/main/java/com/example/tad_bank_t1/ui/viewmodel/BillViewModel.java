package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.remote.Bill;
import com.example.tad_bank_t1.data.repository.bill.BillRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;

public class BillViewModel extends ViewModel {
    private BillRepository repo = new BillRepository();

    public LiveData<ResultWrapper<Bill>> getBillByProviderAndCustomer(String providerId, String customerCode) {
        return repo.getBillByProviderAndCustomer(providerId, customerCode);
    }
}
