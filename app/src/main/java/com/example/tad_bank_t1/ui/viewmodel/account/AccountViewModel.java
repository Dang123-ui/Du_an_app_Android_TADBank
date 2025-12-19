package com.example.tad_bank_t1.ui.viewmodel.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.repository.account.AccountRepository;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import java.util.List;

/** Quản lý account trong ViewModel CRUD Account */
public class AccountViewModel extends ViewModel {
    private final AccountRepository accountRepo = new FirebaseAccountRepository();

    // state result
    private MutableLiveData<ResultWrapper<Account>> _state = new MutableLiveData<>();

    private MutableLiveData<ResultWrapper<Account>> _createState = new MutableLiveData<>();
    private MutableLiveData<ResultWrapper<List<Account>>> _listState = new MutableLiveData<>();

    private MutableLiveData<ResultWrapper<Account>> _accountState = new MutableLiveData<>();

    public void resetState(){
        _state.postValue(null);
        _createState.postValue(null);
        _listState.postValue(null);
        _accountState.postValue(null);
    }

    // getter
    public LiveData<ResultWrapper<Account>> getAccountState() {
        return _accountState;
    }
    public LiveData<ResultWrapper<List<Account>>> getListState() {
        return _listState;
    }

    public LiveData<ResultWrapper<Account>> getCreateState() {
        return _createState;
    }

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


    // Tạo account
    public void createAccount(Account account) {
        if (account == null) {
            _createState.postValue(ResultWrapper.error("Account is null"));
            return;
        }

        _createState.postValue(ResultWrapper.loading());
        accountRepo.create(account)
                .addOnSuccessListener(id -> {
                    account.setAccountId(id);
                    _createState.postValue(ResultWrapper.success(account));
                })
                .addOnFailureListener(e -> {
                    _createState.postValue(ResultWrapper.error(e.getMessage()));
                });
    }


    // xóa Account

    // lấy account theo id
    public void getAccountById(String accountId) {
        resetState();
        _accountState.postValue(ResultWrapper.loading());

        accountRepo.getById(accountId)
                .addOnSuccessListener(account -> {
                    if (account == null) {
                        _accountState.postValue(ResultWrapper.error("No account found"));
                    } else {
                        _accountState.postValue(ResultWrapper.success(account));
                    }
                })
                .addOnFailureListener(e -> {
                    _accountState.postValue(ResultWrapper.error(e.getMessage()));
                });
    }

    // lấy danh sách Account by user Id
    public void getAccountsByUserId(String userId) {
        getAccountsByUserIdAndType(userId, null);
    }

    // lấy account theo user id và type
    public void getAccountsByUserIdAndType(String userId, String accountType) {
        resetState();
        _listState.postValue(ResultWrapper.loading());

        accountRepo.getAccountsByUserIdAndType(userId, accountType)
                .addOnSuccessListener(list -> {
                    if (list == null || list.isEmpty()) {
                        _listState.postValue(ResultWrapper.error("No accounts found"));
                    } else {
                        _listState.postValue(ResultWrapper.success(list));
                    }
                })
                .addOnFailureListener(e -> {
                    _listState.postValue(ResultWrapper.error(e.getMessage()));
                });
    }

    // Lấy account theo accountNumber
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


    // Lấy danh sách Account theo trạng thái

}
