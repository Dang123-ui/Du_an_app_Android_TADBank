package com.example.tad_bank_t1.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Bank;
import com.example.tad_bank_t1.data.repository.bank.BankRepository;
import com.example.tad_bank_t1.data.repository.bank.FirebaseBankRepository;

import java.util.ArrayList;
import java.util.List;

public class BankViewModel extends ViewModel {
    private BankRepository repo = new FirebaseBankRepository();

    private MutableLiveData<List<Bank>> _banks = new MutableLiveData<>(List.of());
    private MutableLiveData<Bank> _selectedBank = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    private MutableLiveData<String> _error = new MutableLiveData<>(null);
    private List<Bank> cacheBank = new ArrayList<>();

    public BankViewModel(){
        loadAll();
    }

    public LiveData<List<Bank>> getBanks(){
        return _banks;
    }

    private void loadAll(){
        _loading.postValue(true);
        _error.postValue(null);
        repo.getAll()
                .addOnSuccessListener(banks -> {
                    Log.d("TAG BANK", "loadAll: success" + banks.size());
                    if (banks != null){
                        cacheBank = banks;
                    }
                    _banks.postValue(banks);
                    _loading.postValue(false);
                })
                .addOnFailureListener(e -> {
                    _error.postValue(e.getMessage());
                    _loading.postValue(false);
                    Log.e("TAG BANK", "loadAll: fail" + e.getMessage());
                });
    }

    public void searchCacheBanks(String key){
        if (key.isEmpty()){
            _banks.postValue(new ArrayList<>(cacheBank));
            return;
        }
        String lowerKey = key.toLowerCase();
        Log.d("TAG BANK", "Lenth cached bank: " + cacheBank.size());

        List<Bank> result = new ArrayList<>();
        for (Bank bank : cacheBank){
            String code = bank.getBankCode() == null ? "" : bank.getBankCode();
            String name = bank.getBankName() == null ? "" : bank.getBankName();
            String longName = bank.getBankLongName() == null ? "" : bank.getBankLongName();

            if (code.toLowerCase().contains(lowerKey)
                    || name.toLowerCase().contains(lowerKey)
                    || longName.toLowerCase().contains(lowerKey)) {

                result.add(bank);
            }
        }
        _banks.postValue(result);
    }

    public void searchBanks(String key){
        _loading.postValue(true);
        _error.postValue(null);
        repo.searchRealtime(key)
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
