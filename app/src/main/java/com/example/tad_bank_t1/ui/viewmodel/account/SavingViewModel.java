package com.example.tad_bank_t1.ui.viewmodel.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;

public class SavingViewModel extends ViewModel {
    private final AccountRepository accountRepo = new FirebaseAccountRepository();

    private MutableLiveData<ResultWrapper<Account>> _payoutAccState = new MutableLiveData<>();

    public void resetState(){
        _payoutAccState.postValue(null);
    }

    // get state
    // getter
    public LiveData<ResultWrapper<Account>> getPayoutAccState() {
        return _payoutAccState;
    }


    // CRUD
    // lấy account theo id
    public void getPayoutAccountById(String accountId) {
        resetState();
        _payoutAccState.postValue(ResultWrapper.loading());

        accountRepo.getById(accountId)
                .addOnSuccessListener(account -> {
                    if (account == null) {
                        _payoutAccState.postValue(ResultWrapper.error("No account found"));
                    } else {
                        _payoutAccState.postValue(ResultWrapper.success(account));
                    }
                })
                .addOnFailureListener(e -> {
                    _payoutAccState.postValue(ResultWrapper.error(e.getMessage()));
                });
    }

}
