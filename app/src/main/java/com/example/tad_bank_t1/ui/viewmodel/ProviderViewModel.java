package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.remote.Provider;
import com.example.tad_bank_t1.data.repository.provider.ProviderRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProviderViewModel extends ViewModel {

    private final ProviderRepository repo = new ProviderRepository();

    private final MediatorLiveData<ResultWrapper<List<Provider>>> _providersState = new MediatorLiveData<>();
    public LiveData<ResultWrapper<List<Provider>>> getProvidersState() { return _providersState; }

    private LiveData<ResultWrapper<List<Provider>>> source;
    private List<Provider> cacheProviders = Collections.emptyList();

    // selected
    private final MediatorLiveData<ResultWrapper<Provider>> selectedProviderState = new MediatorLiveData<>();
    public LiveData<ResultWrapper<Provider>> getSelectedProvider() { return selectedProviderState; }


    // --- top up -----
    private MutableLiveData<ResultWrapper<Boolean>> _topupState = new MutableLiveData<>();

    public LiveData<ResultWrapper<Boolean>> getTopupState() { return _topupState; }
    public LiveData<ResultWrapper<Boolean>> checkTopup(String phoneNumber) { return repo.checkTopup(phoneNumber); }

    public void setSelectedProvider(Provider provider) {
        selectedProviderState.setValue(ResultWrapper.success(provider));
    }

    public void clearSelectedProvider() {
        selectedProviderState.setValue(null);
    }

    public void loadProviders(String type) {
        if (source != null) _providersState.removeSource(source);

        source = repo.getProviders(type);
        _providersState.addSource(source, result -> {
            _providersState.setValue(result);

            // cache khi SUCCESS
            if (result != null && result.getData() != null) {
                List<Provider> safe = result.getData() != null ? result.getData() : Collections.emptyList();
                cacheProviders = safe;
            }
        });
    }

    public void searchCacheProviders(String query) {
        String key = query == null ? "" : query.trim().toLowerCase();

        if (key.isEmpty()) {
            _providersState.setValue(ResultWrapper.success(cacheProviders));
            return;
        }

        List<Provider> out = new ArrayList<>();
        for (Provider p : cacheProviders) {
            String name = (p.getName() != null) ? p.getName().toLowerCase() : "";
//            String code = (p.getCode() != null) ? p.getCode().toLowerCase() : "";
            if (name.contains(key)) out.add(p);
        }

        _providersState.setValue(ResultWrapper.success(out));
    }
}
