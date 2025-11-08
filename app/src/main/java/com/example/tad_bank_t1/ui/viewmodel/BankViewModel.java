package com.example.tad_bank_t1.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.repository.bank.BankRepository;
import com.example.tad_bank_t1.data.repository.bank.FirebaseBankRepository;

import java.util.List;

public class BankViewModel extends ViewModel {
    private BankRepository repo = new FirebaseBankRepository();

    private MutableLiveData<List<Bank>> _banks = new MutableLiveData<>(List.of());
    private MutableLiveData<Bank> _selectedBank = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    private MutableLiveData<String> _error = new MutableLiveData<>(null);

    public BankViewModel(){
        loadAll();
    }

    public LiveData<List<Bank>> getBanks(){
        return _banks;
    }

    public void loadAll(){
        _loading.postValue(true);
        _error.postValue(null);
        repo.getAll()
                .addOnSuccessListener(banks -> {
                    Log.d("TAG BANK", "loadAll: success" + banks.size());
                    _banks.postValue(banks);
                    _loading.postValue(false);
                })
                .addOnFailureListener(e -> {
                    _error.postValue(e.getMessage());
                    _loading.postValue(false);
                    Log.e("TAG BANK", "loadAll: fail" + e.getMessage());
                });
    }

    public void searchBanks(String key){
        _loading.postValue(true);
        _error.postValue(null);
        repo.search(key)
                .addOnSuccessListener(banks -> {
                    _banks.postValue(banks);
                    _loading.postValue(false);
                })
                .addOnFailureListener(e -> {
                    _error.postValue(e.getMessage());
                    _loading.postValue(false);
                });
    }

    public void setSelectedBank(Bank bank){
        _selectedBank.postValue(bank);
    }
    public LiveData<Bank> getSelectedBank(){
        return _selectedBank;
    }

    public LiveData<Boolean> getLoading(){
        return _loading;
    }

    public LiveData<String> getError(){
        return _error;
    }
}
