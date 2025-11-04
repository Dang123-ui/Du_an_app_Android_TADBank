package com.example.tad_bank_t1.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.repository.transaction.FirebaseTransactionRepository;

import java.util.List;

public class TransactionViewModel extends ViewModel {
    private final FirebaseTransactionRepository repo = new FirebaseTransactionRepository();

    private final MutableLiveData<List<Transaction>> _transactions = new MutableLiveData<>();
    public LiveData<List<Transaction>> transactions = _transactions;
    private MutableLiveData<Transaction> _selectedTransaction = new MutableLiveData<>();
    private MutableLiveData<String> _selectedAccountId = new MutableLiveData<>();


    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    // transaction list
    public void loadTransactions(String accountId) {
        _isLoading.setValue(true);
        repo.getTransactionsByAccount(accountId)
                .addOnSuccessListener(list -> {
                    Log.d("TransactionViewModel", "Loaded " + list.size() + " transactions");
                    _transactions.setValue(list);
                    _isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    _isLoading.setValue(false);
                });
    }

    // transaction detail selected
    public void selectTransaction(Transaction transaction) {
        _selectedTransaction.setValue(transaction);
    }

    public LiveData<Transaction> getSelectedTransaction() {
        return _selectedTransaction;
    }

    public void setSelectedAccountId(String accountId) {
        _selectedAccountId.setValue(accountId);
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
        _selectedAccountId.setValue(null);
        _selectedTransaction.setValue(null);
        _transactions.setValue(null);
    }

}
