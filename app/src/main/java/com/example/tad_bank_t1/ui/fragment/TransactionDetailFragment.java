package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;
import com.example.tad_bank_t1.util.DateTimeUtil;

public class TransactionDetailFragment extends Fragment {
    private TransactionViewModel transactionViewModel;


    public TransactionDetailFragment() {
        // Required empty public constructor
    }
    public static TransactionDetailFragment newInstance(String param1, String param2) {
        TransactionDetailFragment fragment = new TransactionDetailFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_transaction_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        transactionViewModel.getSelectedTransaction().observe(getViewLifecycleOwner(), transaction -> {
            if (transaction != null) {
                initView(view, transaction);
            }
        });
    }

    private void initView(View view, Transaction txn){
        TextView txtTxnDetailTxnCode = view.findViewById(R.id.txtTxnDetailTxnCode);
        TextView txtTxnDetailTxnDate = view.findViewById(R.id.txtTxnDetailTxnDate);
        TextView txtTxnDetailTxnAmount = view.findViewById(R.id.txtTxnDetailTxnAmount);
        TextView txtTxnDetailTxnContent = view.findViewById(R.id.txtTxnDetailTxnContent);

        txtTxnDetailTxnCode.setText(txn.getTransactionId());
        String dateTimeLocal = DateTimeUtil.localDateTimeToStr(txn.getCreatedAt());
        txtTxnDetailTxnDate.setText(dateTimeLocal);
        txtTxnDetailTxnAmount.setText(txn.getAmount() + " " + txn.getCurrency());
        txtTxnDetailTxnContent.setText(txn.getDescription());
        String content = txn.getDescription() + ". " + "CT tu " + txn.getAccountId()
                + " toi " + txn.getCounterpartyAccount() + " " + txn.getCounterpartyName() + " " + txn.getCounterpartyBankCode();
        txtTxnDetailTxnContent.setText(content);
    }

    // destroyview
    @Override
    public void onDestroyView() {
        transactionViewModel.selectTransaction(null);
        super.onDestroyView();
    }

}