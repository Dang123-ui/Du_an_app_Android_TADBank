package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.ExternalAccount;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.externalAccount.ExternalAccountRepository;
import com.example.tad_bank_t1.data.repository.externalAccount.FirebaseExternalAccountRepository;


public class ExternalAccountViewModel extends ViewModel {
    private AccountRepository repoInternal;
    private ExternalAccountRepository repoExternal;
    private MutableLiveData<ExternalAccount> _externalAccount = new MutableLiveData<>(null);
    private MutableLiveData<Account> _internalAccount = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    private MutableLiveData<String> _error = new MutableLiveData<>(null);

    public ExternalAccountViewModel() {
        repoExternal = new FirebaseExternalAccountRepository();
        repoInternal = new FirebaseAccountRepository();
    }

    public void searchAccounts(boolean isTadBank, String bankId, String accountNumber) {
        _loading.postValue(true);
        _error.postValue(null);
        if (isTadBank) {
            repoInternal.getByAccountNumber(accountNumber)
                    .addOnSuccessListener(account -> {
                        _loading.postValue(false);
                        _internalAccount.postValue(null);
                        if (account == null) {
                            // KHÔNG TÌM THẤY
                            _internalAccount.postValue(null);
                            _error.postValue("NOT_FOUND");
                        } else {
                            _internalAccount.postValue(account);
                            _error.postValue(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        _error.postValue(e.getMessage());
                        _internalAccount.postValue(null);
                        _externalAccount.postValue(null);
                        _loading.postValue(false);
                    });
        } else{
            repoExternal.getByBankIdAndAccountNumber(bankId, accountNumber)
                    .addOnSuccessListener(externalAccount -> {
                        _loading.postValue(false);
                        _internalAccount.postValue(null);
                        if (externalAccount == null) {
                            // KHÔNG TÌM THẤY
                            _externalAccount.postValue(null);
                            _error.postValue("NOT_FOUND");
                        } else {
                            _externalAccount.postValue(externalAccount);
                            _error.postValue(null);
                        }

                    })
                    .addOnFailureListener(e -> {
                        _error.postValue(e.getMessage());
                        _internalAccount.postValue(null);
                        _externalAccount.postValue(null);
                        _loading.postValue(false);
                    });
        }
    }

    public void clearError() {
        _error.postValue(null);
    }
    public void clearAccounts() {
        _externalAccount.setValue(null);
        _internalAccount.setValue(null);
    }


    public MutableLiveData<ExternalAccount> getExternalAccount() {
        return _externalAccount;
    }
    public MutableLiveData<Account> getInternalAccount() {
        return _internalAccount;
    }


    public MutableLiveData<Boolean> getLoading() {
        return _loading;
    }

    public MutableLiveData<String> getError() {
        return _error;
    }

}



