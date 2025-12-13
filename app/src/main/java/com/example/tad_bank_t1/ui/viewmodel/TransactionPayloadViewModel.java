package com.example.tad_bank_t1.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.response.ResultWrapper;
import com.example.tad_bank_t1.ui.form.payload.transactions.BaseTransactionPayload;

public class TransactionPayloadViewModel extends ViewModel {
    private MutableLiveData<BaseTransactionPayload> txnPayload = new MutableLiveData<>();

    public void setTxnPayload(BaseTransactionPayload txnPayload){
        this.txnPayload.setValue(txnPayload);
    }

    public BaseTransactionPayload getTxnPayload(){
        return txnPayload.getValue();
    }

    // ================================================
    // lay payload cua viewmodel
    // ================================================
    public void clearPayload(){
        txnPayload.postValue(null);
    }
}
