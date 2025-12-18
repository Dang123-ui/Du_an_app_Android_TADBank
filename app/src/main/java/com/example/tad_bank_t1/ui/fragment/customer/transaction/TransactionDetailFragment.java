package com.example.tad_bank_t1.ui.fragment.customer.transaction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.databinding.FragmentTransactionDetailBinding;
import com.example.tad_bank_t1.ui.base.UiConfig;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.CurrencyUtil;
import com.example.tad_bank_t1.util.DateTimeUtil;

public class TransactionDetailFragment extends Fragment implements UiConfig {
    private TransactionViewModel transactionViewModel;
    private FragmentTransactionDetailBinding binding;


    public TransactionDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public String getAppBarTitle() {
        return getString(R.string.chi_tiet_gd);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTransactionDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        transactionViewModel.getSelectedTransaction().observe(getViewLifecycleOwner(), transaction -> {
            if (transaction != null) {
                initView(transaction);
            }
        });
    }

    private void initView(Transaction txn){
        binding.txtTxnDetailTxnCode.setText(txn.getTransactionReference());

        String dateTimeLocal = DateTimeUtil.localDateTimeToStr(txn.getCreatedAt());
        binding.txtTxnDetailTxnDate.setText(dateTimeLocal);

        binding.txtTxnDetailTxnAmount.setText(
                CurrencyUtil.formatAmount(txn.getAmount()) + " " + txn.getCurrency()
        );

        StringBuilder content = new StringBuilder();

        if (txn.getType() == TxnType.TRANSFER_INTERNAL || txn.getType() == TxnType.TRANSFER_EXTERNAL
            || txn.getType() == TxnType.MOBILE_TOPUP || txn.getType() == TxnType.BILL_PAYMENT
        ){
            content.append(
                    txn.getDescription() + ". "
                            + "CT tu " + txn.getAccountNumber() + " " + txn.getAccountName()
                            + " toi " + txn.getCounterpartyAccount()
            );

            if (txn.getCounterpartyName() != null) {
                content.append(" " + txn.getCounterpartyName());
            }
            if (txn.getCounterpartyBankCode() != null) {
                content.append(" " + txn.getCounterpartyBankCode());
            }
        } else if (txn.getType() == TxnType.TRANSFER_INTERNAL_INCOMING) {
            content.append(txn.getDescription());
        }


        binding.txtTxnDetailTxnContent.setText(content);
    }

    // destroyview
    @Override
    public void onDestroyView() {
        transactionViewModel.selectTransaction(null);
        binding = null;
        super.onDestroyView();
    }

}