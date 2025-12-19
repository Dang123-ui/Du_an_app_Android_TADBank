package com.example.tad_bank_t1.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class SessionViewModel extends ViewModel {
    private final UserRepository userRepo = new FirebaseUserRepository();
    private final AccountRepository accountRepo = new FirebaseAccountRepository();

    private final MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> user = _user;

    // user id
    private MutableLiveData<String> userId = new MutableLiveData<>();

    private final MutableLiveData<List<Account>> _accounts = new MutableLiveData<>();
    public LiveData<List<Account>> accounts = _accounts;

    private final MutableLiveData<Account> _defaultAccount = new MutableLiveData<>();
    public LiveData<Account> defaultAccount = _defaultAccount;

    private final MutableLiveData<Account> _selectedAccount = new MutableLiveData<>();
    public LiveData<Account> selectedAccount = _selectedAccount;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private ListenerRegistration accountListener;

    // thanh toan chon account
    private final MediatorLiveData<Account> _payAccount = new MediatorLiveData<>();
    public LiveData<Account> payAccount = _payAccount;

    public SessionViewModel() {
        // chọn theo selected -> default
        _payAccount.addSource(_selectedAccount, acc -> recomputePayAccount());
        _payAccount.addSource(_defaultAccount, acc -> recomputePayAccount());
        _payAccount.addSource(_accounts, list -> recomputePayAccount());
    }

    private void recomputePayAccount() {
        Account sel = _selectedAccount.getValue();
        if (sel != null) {
            _payAccount.postValue(sel);
            return;
        }

        Account def = _defaultAccount.getValue();
        if (def != null) {
            _payAccount.postValue(def);
            return;
        }

        List<Account> list = _accounts.getValue();
        if (list != null && !list.isEmpty()) {
            _payAccount.postValue(list.get(0));
        } else {
            _payAccount.postValue(null);
        }
    }


    public void setUserId(String id) {
        userId.setValue(id);
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public void loadCurrentUserAndAccounts(String userId) {
        _isLoading.postValue(true);
        userRepo.getById(userId)
                .addOnSuccessListener(user -> {
                    _user.postValue(user);
                    Log.d("SessionViewModel", "User loaded:" + user.getFullName());
                    accountRepo.getAccountsByUserId(user.getUserId())
                            .addOnSuccessListener(list -> {
                                _accounts.postValue(list);
                                if (list != null) {
                                    for (Account acc : list) {
                                        if (acc.isDefault()) {
                                            _defaultAccount.postValue(acc);
                                            break;
                                        }
                                    }
                                }
                                _isLoading.postValue(false);
                            })
                            .addOnFailureListener(e -> {
                                _error.postValue(e.getMessage());
                                _isLoading.postValue(false);
                            });
                })
                .addOnFailureListener(e -> {
                    _error.postValue(e.getMessage());
                    Log.d("SessionViewModel", "Error loading user:" + e.getMessage());
                    _isLoading.postValue(false);
                });
    }


    // lang nghe realtime tai khoan
    public void observeUserAndAccountsRealtime(String userId) {
        _isLoading.postValue(true);
        userRepo.getById(userId)
                .addOnSuccessListener(user -> {
                    _user.postValue(user);
                    Log.d("SessionViewModel", "User loaded:" + user.getFullName());


                    // Bắt đầu lắng nghe realtime account
                    accountListener = accountRepo.listenAccountsByUserId(userId, new AccountRepository.OnAccountsChanged() {
                        @Override
                        public void onChanged(List<Account> accounts) {
                            _accounts.postValue(accounts);
                            for (Account a : accounts) {
                                if (a.isDefault()) {
                                    _defaultAccount.postValue(a);
                                    Log.d("SessionViewModel", "Account loaded:" + a.getAccountName());
                                }
                            }
                            _isLoading.postValue(false);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("SessionViewModel", "Error account:" + e.getMessage());
                            _error.postValue(e.getMessage());
                            _isLoading.postValue(false);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.d("SessionViewModel", "Error loading user:" + e.getMessage());
                    _error.postValue(e.getMessage());
                    _isLoading.postValue(false);
                });
    }

    @Override
    protected void onCleared() {
        if (accountListener != null) {
            accountListener.remove();
            accountListener = null;
        }
        super.onCleared();
    }

    public void setSelectedAccount(Account account) {
        _selectedAccount.postValue(account);
    }

    public void clearSession() {
        _user.postValue(null);
        _accounts.postValue(null);
        _defaultAccount.postValue(null);
        _selectedAccount.postValue(null);
    }


    public void clearSelectedAccount() {
        _selectedAccount.postValue(null);
    }

}
