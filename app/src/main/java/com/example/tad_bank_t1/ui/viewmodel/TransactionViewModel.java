package com.example.tad_bank_t1.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tad_bank_t1.app.notification.AppNotificationHelper;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.repository.email.EmailRepository;
import com.example.tad_bank_t1.data.repository.notification.FirebaseNotificationRepository;
import com.example.tad_bank_t1.data.repository.transaction.FirebaseTransactionRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;
import com.example.tad_bank_t1.util.NotificationUtil;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.example.tad_bank_t1.util.email.SmtpEmailSender;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private final FirebaseTransactionRepository repo = new FirebaseTransactionRepository();
    private final FirebaseAccountRepository accountRepository = new FirebaseAccountRepository();
    private final FirebaseNotificationRepository notificationRepository = new FirebaseNotificationRepository();
    private final EmailRepository emailRepository = new EmailRepository();


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

    public TransactionViewModel(Application application) {
        super(application);
    }

    private Application app() {
        return getApplication();
    }


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
        if (transaction.getAccountId() == null) { _state.postValue(ResultWrapper.error("Tài khoản chưa có ID")); return; }
        if (transaction.getStatus() == TnxStatus.COMPLETED) return;

        _state.postValue(ResultWrapper.loading());

        repo.updateTransactionStatus(transaction.getTransactionId(), TnxStatus.COMPLETED, new ResultCallback<Transaction>() {
            @Override
            public void onSucces(Transaction data) {
                if (data == null) { _state.postValue(ResultWrapper.error("Transaction not found")); return; }

                boolean isComing = TransactionUtil.isIncoming(transaction);
                long finalAmount = isComing ? transaction.getAmount() : -transaction.getAmount();

                accountRepository.updateBalanceAccount(transaction.getAccountNumber(), finalAmount, new ResultCallback<Void>() {
                    @Override public void onSucces(Void ignored) {
                        sender.setBalance(sender.getBalance() + finalAmount);

                        // nếu nội bộ thì cộng người nhận
                        if (transaction.getType() == TxnType.TRANSFER_INTERNAL) {
                            accountRepository.updateBalanceAccount(transaction.getCounterpartyAccount(), transaction.getAmount(), new ResultCallback<Void>() {
                                @Override public void onSucces(Void ignored2) {
                                    afterBalanceDone(data);
                                }
                                @Override public void onError(String error) {
                                    _state.postValue(ResultWrapper.error("Update receiver balance failed: " + error));
                                }
                            });
                        } else {
                            afterBalanceDone(data);
                        }
                    }

                    @Override public void onError(String error) {
                        _state.postValue(ResultWrapper.error("Update sender balance failed: " + error));
                    }

                    private void afterBalanceDone(Transaction completedTxn) {
                        Notification noti = NotificationUtil.createNotificationTxn(user, sender, transaction);

                        notificationRepository.createNotification(noti, new ResultCallback<Notification>() {
                            @Override public void onSucces(Notification n) {
                                // thông báo trong app
                                Log.d("TRANSACTION NOTI", noti.toString());
                                AppNotificationHelper.showTransactionNoti(app(), noti);

                                // ✅ GỬI EMAIL Ở ĐÂY (sau khi đã thành công)
                                emailRepository.sendTxnReceipt(user, sender, transaction, new SmtpEmailSender.Callback() {
                                    @Override public void onSuccess() {
                                        _state.postValue(ResultWrapper.success(completedTxn));
                                    }

                                    @Override public void onError(String error) {
                                        // giao dịch vẫn success, chỉ email fail
                                        Log.e("Email", "Send email failed: " + error);
                                        _state.postValue(ResultWrapper.success(completedTxn));
                                    }
                                });
                            }

                            @Override public void onError(String error) {
                                // thông báo trong app
                                AppNotificationHelper.showTransactionNoti(app(), noti);

                                // notification fail cũng không làm fail transaction
                                Log.e("Noti", "Create noti failed: " + error);

                                // vẫn gửi email (tuỳ bạn). Nếu muốn chắc chắn vẫn gửi:
                                emailRepository.sendTxnReceipt(user, sender, transaction, new SmtpEmailSender.Callback() {
                                    @Override public void onSuccess() { _state.postValue(ResultWrapper.success(completedTxn)); }
                                    @Override public void onError(String e) {
                                        Log.e("Email", "Send email failed: " + e);
                                        _state.postValue(ResultWrapper.success(completedTxn));
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(String error) {
                _state.postValue(ResultWrapper.error(error));
            }
        });
    }


}
