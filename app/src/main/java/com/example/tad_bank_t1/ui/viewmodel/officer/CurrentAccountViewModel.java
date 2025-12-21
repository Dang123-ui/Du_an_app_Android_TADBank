package com.example.tad_bank_t1.ui.viewmodel.officer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;

import java.util.ArrayList;
import java.util.List;

public class CurrentAccountViewModel extends ViewModel {
    public enum Filter { ALL, CHECKING, SAVING, MORTGAGE }

    private final MutableLiveData<List<Account>> allAccounts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Filter> selectedFilter = new MutableLiveData<>(Filter.ALL);
    private final MutableLiveData<Integer> badgeChecking = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> badgeSaving = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> badgeMortgage = new MutableLiveData<>(0);
}
