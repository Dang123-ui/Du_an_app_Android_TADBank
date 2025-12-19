package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.response.ResultWrapper;

public class AccountViewModel extends ViewModel {
    private final AccountRepository accountRepo = new FirebaseAccountRepository();

    // state result
    private MutableLiveData<ResultWrapper<Account>> _state = new MutableLiveData<>();

    // ============ state result ==============
    public LiveData<ResultWrapper<Account>> getResultState() {
        return _state;
    }

    // update Account
    public void updateAccount(Account account) {
        if (account == null) {
            _state.postValue(ResultWrapper.error("Account is null"));
            return;
        }

        _state.postValue(ResultWrapper.loading());

        // update tu repository
        accountRepo.updateAccount(account, new ResultCallback<Account>() {
            @Override
            public void onSuccess(Account data) {
                if (data == null) {
                    _state.postValue(ResultWrapper.error("Account not found"));
                } else {
                    _state.postValue(ResultWrapper.success(data));
                }
            }

            @Override
            public void onError(String error) {
                _state.postValue(ResultWrapper.error(error));
            }
        });
    }

    public void getAccountByAccountNumber(String accountNumber) {
        accountRepo.getAccountByAccountNumber(accountNumber, new ResultCallback<Account>() {
            @Override
            public void onSuccess(Account data) {
                if (data == null) {
                    _state.postValue(ResultWrapper.error("Account not found"));
                } else {
                    _state.postValue(ResultWrapper.success(data));
                }
            }

            @Override
            public void onError(String error) {
                _state.postValue(ResultWrapper.error(error));
            }
        });
    }

}
