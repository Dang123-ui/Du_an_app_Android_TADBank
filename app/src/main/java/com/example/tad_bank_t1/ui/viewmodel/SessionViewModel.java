package com.example.tad_bank_t1.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.users.FirebaseUserRepository;
import com.example.tad_bank_t1.data.repository.users.UserRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class SessionViewModel extends ViewModel {
    private final UserRepository userRepo = new FirebaseUserRepository();
    private final AccountRepository accountRepo = new FirebaseAccountRepository();

    private final MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> user = _user;

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

    public void loadCurrentUserAndAccounts(String userId) {
        _isLoading.setValue(true);
        userRepo.getById(userId)
                .addOnSuccessListener(user -> {
                    _user.setValue(user);
                    Log.d("SessionViewModel", "User loaded:" + user.getFullName());
                    accountRepo.getAccountsByUserId(user.getUserId())
                            .addOnSuccessListener(list -> {
                                _accounts.setValue(list);
                                if (list != null) {
                                    for (Account acc : list) {
                                        if (acc.isDefault()) {
                                            _defaultAccount.setValue(acc);
                                            break;
                                        }
                                    }
                                }
                                _isLoading.setValue(false);
                            })
                            .addOnFailureListener(e -> {
                                _error.setValue(e.getMessage());
                                _isLoading.setValue(false);
                            });
                })
                .addOnFailureListener(e -> {
                    _error.setValue(e.getMessage());
                    Log.d("SessionViewModel", "Error loading user:" + e.getMessage());
                    _isLoading.setValue(false);
                });
    }


    public void observeUserAndAccountsRealtime(String userId) {
        _isLoading.setValue(true);
        userRepo.getById(userId)
                .addOnSuccessListener(user -> {
                    _user.setValue(user);
                    Log.d("SessionViewModel", "User loaded:" + user.getFullName());


                    // Bắt đầu lắng nghe realtime account
                    accountListener = accountRepo.listenAccountsByUserId(userId, new AccountRepository.OnAccountsChanged() {
                        @Override
                        public void onChanged(List<Account> accounts) {
                            _accounts.setValue(accounts);
                            for (Account a : accounts) {
                                if (a.isDefault()) _defaultAccount.setValue(a);
                                Log.d("SessionViewModel", "Account loaded:" + a.getAccountName());
                            }
                            _isLoading.setValue(false);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("SessionViewModel", "Error account:" + e.getMessage());
                            _error.setValue(e.getMessage());
                            _isLoading.setValue(false);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.d("SessionViewModel", "Error loading user:" + e.getMessage());
                    _error.setValue(e.getMessage());
                    _isLoading.setValue(false);
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
        _selectedAccount.setValue(account);
    }

    public void clearSession() {
        _user.setValue(null);
        _accounts.setValue(null);
        _defaultAccount.setValue(null);
        _selectedAccount.setValue(null);
    }

    public void clearSelectedAccount() {
        _selectedAccount.setValue(null);
    }
}
