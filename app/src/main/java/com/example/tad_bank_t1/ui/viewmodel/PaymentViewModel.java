package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.User;
import com.example.tad_bank_t1.data.remote.dto.CreatePaymentReq;
import com.example.tad_bank_t1.data.remote.dto.CreatePaymentRes;
import com.example.tad_bank_t1.data.repository.callbacks.ResultCallback;
import com.example.tad_bank_t1.data.repository.payment.PaymentRepository;
import com.example.tad_bank_t1.data.repository.payment.PaymentRepositoryImpl;
import com.example.tad_bank_t1.data.response.ResultWrapper;

import java.util.Map;
public class PaymentViewModel extends ViewModel {

    private final PaymentRepository repo = new PaymentRepositoryImpl();

    private final MutableLiveData<ResultWrapper<CreatePaymentRes>> _createState = new MutableLiveData<>();


    public LiveData<ResultWrapper<CreatePaymentRes>> getCreateState() {
        return _createState;
    }

    public void createVnpayPayment(Transaction txn, User user, Map<String, String> metadata) {
        CreatePaymentReq req = new CreatePaymentReq();
        req.transactionId = txn.getTransactionId();
        req.userId = user.getUserId();
        req.payerAccountId = txn.getAccountId();
        req.payerAccountNumber = txn.getAccountNumber();
        req.payerAccountName = txn.getAccountName();
        req.purpose = txn.getType().name();
        req.currency = "VND";
        req.feeAmount = txn.getFeeAmount();
        req.amount = txn.getAmount();
        req.billId = txn.getBillId();
        req.orderId = txn.getOrderId();
        req.orderInfo = txn.getDescription();
        req.locale = "vn";
        req.metadata = metadata;

        repo.createPaymentVnpay(req, new ResultCallback<>() {
            public void onLoading(){ _createState.setValue(ResultWrapper.loading()); }
            public void onSuccess(CreatePaymentRes d){ _createState.setValue(ResultWrapper.success(d)); }
            public void onError(String e){ _createState.setValue(ResultWrapper.error(e)); }
        });
    }

    public void clearCreateState() {
        _createState.setValue(null);
    }
}

