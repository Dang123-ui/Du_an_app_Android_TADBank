package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.remote.Provider;
import com.example.tad_bank_t1.data.repository.provider.ProviderRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import java.util.List;

public class ProviderViewModel extends ViewModel {

    private ProviderRepository repo = new ProviderRepository();

    // state
    private MutableLiveData<ResultWrapper<Boolean>> _topupState = new MutableLiveData<>();


    public LiveData<ResultWrapper<List<Provider>>> providers;


    public LiveData<ResultWrapper<Boolean>> getTopupState() {
        return _topupState;
    }

    public void loadProviders(String type) {
        providers = repo.getProviders(type);
    }

    public LiveData<ResultWrapper<Boolean>> checkTopup(String phoneNumber) {
        return repo.checkTopup(phoneNumber);
    }
}

