package com.example.tad_bank_t1.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tad_bank_t1.app.notification.AppNotificationHelper;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.model.Notification;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.data.repository.account.FirebaseAccountRepository;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.repository.email.EmailRepository;
import com.example.tad_bank_t1.data.repository.notification.FirebaseNotificationRepository;
import com.example.tad_bank_t1.data.repository.transaction.FirebaseTransactionRepository;
import com.example.tad_bank_t1.data.repository.transaction.TransactionRepository;
import com.example.tad_bank_t1.data.response.ResultWrapper;
import com.example.tad_bank_t1.util.NotificationUtil;
import com.example.tad_bank_t1.util.TransactionUtil;
import com.example.tad_bank_t1.util.email.SmtpEmailSender;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;
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

    // listener transaction
    private ListenerRegistration transactionListener;
    private MutableLiveData<ResultWrapper<Transaction>> _listenerState = new MutableLiveData<>();

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

        transaction.setCreatedAt(new Date());

        // loading
        _state.postValue(ResultWrapper.loading());
        repo.createTransaction(transaction, new ResultCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction data) {
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
            public void onSuccess(Transaction data) {
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
            public void onSuccess(Transaction data) {
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
            public void onSuccess(Transaction data) {
                if (data == null) { _state.postValue(ResultWrapper.error("Transaction not found")); return; }

                boolean isComing = TransactionUtil.isIncoming(transaction);
                long finalAmount = isComing ? transaction.getAmount() : -transaction.getAmount();


                // rut tien tu saving account thi reser về 0
                if (transaction.getType() == TxnType.SAVING_WITHDRAW){
                    finalAmount = -sender.getBalance();
                }

                accountRepository.updateBalanceAccount(transaction.getAccountNumber(), finalAmount, new ResultCallback<Account>() {
                    @Override public void onSuccess(Account accountUpdated) {
                        sender.setBalance(accountUpdated.getBalance());

                        // nếu nội bộ thì cộng người nhận: INTERNAL || SAVING_DEPOSIT
                        if (TransactionUtil.isInternal(data.getType())) {
                            accountRepository.updateBalanceAccount(transaction.getCounterpartyAccount(), transaction.getAmount(), new ResultCallback<Account>() {
                                @Override public void onSuccess(Account receiverUpdate) {
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
                        // ======================
                        // gui thong bao cho nguoi chuyen tien
                        // ======================
                        Notification noti = NotificationUtil.createNotificationTxn(user, sender, transaction);
                        notificationRepository.createNotification(noti, new ResultCallback<Notification>() {
                            @Override public void onSuccess(Notification n) {
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

                        // ======================
                        // tao giao dich nhan tien neu la chuyen noi bo
                        // ======================
                        if (TransactionUtil.isInternal(completedTxn.getType())){
                            // tao giao dich moi
                            String newRef = TransactionUtil.generateRef();
                            String newIdemp = TransactionUtil.generateIdempotencyKey(
                                    transaction.getAmount(),
                                    transaction.getCounterpartyAccount(),
                                    transaction.getAccountNumber(),
                                    TxnType.TRANSFER_INTERNAL_INCOMING
                            );
                            String newDesc = "REF " + transaction.getTransactionReference() + ". "
                                    + transaction.getDescription() + ". "
                                    + "CT tu " + transaction.getAccountNumber() + " " + transaction.getAccountName()
                                    + " toi " + transaction.getCounterpartyAccount()
                                    + " " + transaction.getCounterpartyName()
                                    + " " + transaction.getCounterpartyBankCode();


                            Transaction transactionReceive = Transaction.builder()
                                    .transactionId(TransactionUtil.generateTransactionId())
                                    .accountNumber(transaction.getCounterpartyAccount())
                                    .accountName(transaction.getCounterpartyName())
                                    .counterpartyAccount(sender.getAccountNumber())
                                    .counterpartyName(sender.getAccountName())
                                    .counterpartyBankCode(transaction.getCounterpartyBankCode())
                                    .counterpartyBankName(transaction.getCounterpartyBankName())
                                    .counterpartyBankLogo(transaction.getCounterpartyBankLogo())
                                    .description(newDesc)
                                    .amount(transaction.getAmount())
                                    .feeAmount(transaction.getFeeAmount())
                                    .status(TnxStatus.COMPLETED)
                                    .type(TxnType.TRANSFER_INTERNAL_INCOMING)
                                    .channel(TxnChannel.MOBILE_APP)
                                    .currency("VND")
                                    .idempotencyKey(newIdemp)
                                    .transactionReference(newRef)
                                    .createdAt(new Date())
                                    .updatedAt(new Date())
                                    .build();

                            // tao thong bao cho nguoi nhan

                            // tim account
                            accountRepository.getAccountByAccountNumber(transaction.getCounterpartyAccount(), new ResultCallback<Account>() {
                                @Override
                                public void onSuccess(Account data) {
                                    transactionReceive.setAccountId(data.getAccountId());
                                    User userReceiver = new User();
                                    userReceiver.setUserId(data.getUserId());

                                    // Tao giao dich database
                                    repo.createTransaction(transactionReceive, new ResultCallback<Transaction>() {
                                        @Override
                                        public void onSuccess(Transaction transactionCreated) {
                                            Log.d(
                                                    "MIRROR_TXN",
                                                    "Transaction created: " + transactionCreated.toString()
                                            );

                                            // Xong thi tao thong bao cho nguoi nhan
                                            Notification notiReceive = NotificationUtil.createNotificationTxn(userReceiver, data, transactionCreated);

                                            notificationRepository.createNotification(notiReceive, new ResultCallback<Notification>() {
                                                @Override
                                                public void onSuccess(Notification data) {
                                                    Log.d("MIRROR_TXN", "Notification created");
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    Log.e("MIRROR_TXN", "Failed: " + error);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.e("MIRROR_TXN", "Failed: " + error);
                                        }
                                    });

                                }

                                @Override
                                public void onError(String error) {
                                    Log.e("MIRROR_TXN", "Failed: " + error);
                                }
                            });
                        }

                    }
                });
            }

            @Override
            public void onError(String error) {
                _state.postValue(ResultWrapper.error(error));
            }
        });
    }


    // ============
    // listener transaction
    // =============
    // ==========================
    // ✅ LISTEN TRANSACTION STATUS (VNPAY / async update)
    // ==========================
    public void listenTransactionStatus(@NonNull String transactionId) {
        stopListenTransactionStatus(); // tránh listen chồng

        // set loading state (tuỳ ResultWrapper của bạn)
        _listenerState.setValue(ResultWrapper.loading());

        transactionListener = repo.listenerTransactionById(transactionId, new TransactionRepository.OnTransactionChanged() {
            @Override
            public void onChanged(Transaction transaction) {
                _listenerState.setValue(ResultWrapper.success(transaction));
            }

            @Override
            public void onError(Exception e) {
                _listenerState.setValue(ResultWrapper.error(e.getMessage()));
            }
        });
    }

    private boolean postVnpayDone = false;

    public void runPostSuccessActionsOnce(Transaction txn, Account sender, User user) {
        if (postVnpayDone) return;
        postVnpayDone = true;

        // tạo notification (local + firestore)
        Notification noti = NotificationUtil.createNotificationTxn(user, sender, txn);
        notificationRepository.createNotification(noti, new ResultCallback<Notification>() {
            @Override public void onSuccess(Notification n) {
                AppNotificationHelper.showTransactionNoti(app(), noti);
            }
            @Override public void onError(String error) {
                // vẫn show local notification
                AppNotificationHelper.showTransactionNoti(app(), noti);
            }
        });

        // gửi email receipt (nếu bạn đang dùng SMTP từ app)
        emailRepository.sendTxnReceipt(user, sender, txn, new SmtpEmailSender.Callback() {
            @Override public void onSuccess() { /* ok */ }
            @Override public void onError(String error) {
                Log.e("Email", "Send email failed: " + error);
            }
        });
    }

    public void resetPostSuccessFlag() {
        postVnpayDone = false;
    }


    public LiveData<ResultWrapper<Transaction>> getListenerState() {
        return _listenerState;
    }

    public void stopListenTransactionStatus() {
        if (transactionListener != null) {
            transactionListener.remove();
            transactionListener = null;
        }
    }

    @Override
    protected void onCleared() {
        stopListenTransactionStatus();
        super.onCleared();
    }
}
