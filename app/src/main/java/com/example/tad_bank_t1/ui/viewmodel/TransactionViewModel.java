package com.example.tad_bank_t1.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.repository.notification.FirebaseNotificationRepository;
import com.example.tad_bank_t1.data.repository.transaction.FirebaseTransactionRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;
import com.example.tad_bank_t1.util.NotificationUtil;
import com.example.tad_bank_t1.util.TransactionUtil;

import java.util.List;

public class TransactionViewModel extends ViewModel {
    private final FirebaseTransactionRepository repo = new FirebaseTransactionRepository();
    private final FirebaseAccountRepository accountRepository = new FirebaseAccountRepository();
    private final FirebaseNotificationRepository notificationRepository = new FirebaseNotificationRepository();

    private final MutableLiveData<List<Transaction>> _transactions = new MutableLiveData<>();
    public LiveData<List<Transaction>> transactions = _transactions;
    private MutableLiveData<Transaction> _selectedTransaction = new MutableLiveData<>();
    private MutableLiveData<String> _selectedAccountId = new MutableLiveData<>();


    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    // state voi ResultWrapper
    private MutableLiveData<ResultWrapper<Transaction>> _state = new MutableLiveData<>();

    // state idempotency key
    private String idempotencyKey = null;

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }


    // transaction list
    public void loadTransactions(String accountId) {
        Log.d("TransactionViewModel", "Loading transactions for account " + accountId + "...");

        _isLoading.postValue(true);
        repo.getTransactionsByAccount(accountId)
                .addOnSuccessListener(list -> {
                    Log.d("TransactionViewModel", "Loaded " + list.size() + " transactions");
                    _transactions.postValue(list);
                    _isLoading.postValue(false);
                })
                .addOnFailureListener(e -> {
                    _isLoading.postValue(false);
                    Log.d("TransactionViewModel", "Error loading transactions:" + e.getMessage());
                });
    }

    // transaction detail selected
    public void selectTransaction(Transaction transaction) {
        _selectedTransaction.postValue(transaction);
    }

    public LiveData<Transaction> getSelectedTransaction() {
        return _selectedTransaction;
    }

    public void setSelectedAccountId(String accountId) {
        _selectedAccountId.postValue(accountId);
    }

    public LiveData<String> getSelectedAccountId() {
        return _selectedAccountId;
    }


    // check data
    public boolean hasData() {
        return transactions.getValue() != null && !transactions.getValue().isEmpty();
    }

    // clear selected
    public void clearSelection() {
        _selectedAccountId.postValue(null);
        _selectedTransaction.postValue(null);
        _transactions.postValue(null);
    }


    // ================================================
    // lay state cua viewmodel vói result wrapper
    // ================================================
    public LiveData<ResultWrapper<Transaction>> getResultState() {
        return _state;
    }

    // ================================================
    // clear state cua viewmodel vói result wrapper
    // ================================================
    public void clearResultState() {
        _state.postValue(null);
    }

    // ================================================
    // Tạo transaction mới
    // ================================================
    public void createTransaction(Transaction transaction) {
        if (transaction.getAccountId() == null){
            _state.postValue(ResultWrapper.error("Tài khoản chưa có ID"));
            return;
        }
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            _state.postValue(ResultWrapper.error("Số tiền không hợp lệ"));
            return;
        }

        // loading
        _state.postValue(ResultWrapper.loading());
        repo.createTransaction(transaction, new ResultCallback<Transaction>() {
            @Override
            public void onSucces(Transaction data) {
                if (data == null) {
                    _state.postValue(ResultWrapper.error("Tạo Transaction bị lỗi"));
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

    // ================================================
    // lay transaction theo id bang viec quan ly 1 result state
    // ================================================
    public void getTransactionById(String transactionId) {
        // loading
        _state.postValue(ResultWrapper.loading());
        // lay tu repository
        repo.getTransactionById(transactionId, new ResultCallback<Transaction>() {
            @Override
            public void onSucces(Transaction data) {
                if (data == null) {
                    _state.postValue(ResultWrapper.error("Transaction not found"));
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

    // ================
    // update transaction
    // ================
    public void updateTransactionStatus(String transactionId, TnxStatus newStatus) {
        if (transactionId == null) {
            _state.postValue(ResultWrapper.error("Transaction ID is null"));
            return;
        }
        // loading
        _state.postValue(ResultWrapper.loading());

        // update tu repository
        repo.updateTransactionStatus(transactionId, newStatus, new ResultCallback<Transaction>() {
            @Override
            public void onSucces(Transaction data) {
                if (data == null) {
                    _state.postValue(ResultWrapper.error("Transaction not found"));
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

    // ================
    // execute transaction
    // ================
    public void executeTransaction(Transaction transaction, Account sender, User user) {
        if (transaction.getAccountId() == null){
            _state.postValue(ResultWrapper.error("Tài khoản chưa có ID"));
            return;
        }

        if (transaction.getStatus() == TnxStatus.COMPLETED) return;

        // loading
        _state.postValue(ResultWrapper.loading());

        // update tu repository
        repo.updateTransactionStatus(transaction.getTransactionId(), TnxStatus.COMPLETED, new ResultCallback<Transaction>() {
            @Override
            public void onSucces(Transaction data) {
                if (data == null) {
                    _state.postValue(ResultWrapper.error("Transaction not found"));
                } else {
                    // update balance
                    boolean isComing = TransactionUtil.isIncoming(transaction);
                    Long finalAmount = isComing ? transaction.getAmount() : -transaction.getAmount();
                    accountRepository.updateBalanceAccount(transaction.getAccountNumber(), finalAmount);
                    sender.setBalance(sender.getBalance() + finalAmount);


                    if (transaction.getType() == TxnType.TRANSFER_INTERNAL){
                        accountRepository.updateBalanceAccount(transaction.getCounterpartyAccount(), transaction.getAmount());
                    }

                    // thong bao
                    // gui thong bao qua email va app mobile
                    Notification noti = NotificationUtil.createNotificationTxn(user, sender, transaction);
                    notificationRepository.createNotification(noti, new ResultCallback<Notification>() {
                        @Override
                        public void onSucces(Notification data) {
                            Log.d("TransactionViewModel", "onSucces: " + data.toString());
                        }

                        @Override
                        public void onError(String error) {
                            Log.d("TransactionViewModel", "onError: " + error);
                        }
                    });


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
