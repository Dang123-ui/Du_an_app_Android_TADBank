package com.example.tad_bank_t1.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.data.model.Account;
import com.example.tad_bank_t1.data.session.SessionManager;
import com.example.tad_bank_t1.ui.activity.MainActivity;
import com.example.tad_bank_t1.ui.viewmodel.SessionViewModel;
import com.example.tad_bank_t1.ui.viewmodel.TransactionViewModel;


public class CardTransactionHistoryFragment extends Fragment {
    private TextView txtTxnHistoryAccName, txtTxnHistoryAccNumber, txtTxnHistoryAccBalance;
    private ImageButton imbtOpenAccDetail;
    private SessionViewModel sessionViewModel;

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
        return inflater.inflate(R.layout.fragment_card_transaction_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // findId
        txtTxnHistoryAccName = view.findViewById(R.id.txtTxnHistoryAccName);
        txtTxnHistoryAccNumber = view.findViewById(R.id.txtTxnHistoryAccNumber);
        txtTxnHistoryAccBalance = view.findViewById(R.id.txtTxnHistoryBalance);
        imbtOpenAccDetail = view.findViewById(R.id.imbtOpenAccDetail);

        // onclick open detail
        imbtOpenAccDetail.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openFeatureFragment(new AccountDetailFragment(), getString(R.string.chi_tiet_tai_khoan));
        });

        // setting data
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
        // Quan sát selectedAccount riêng
        sessionViewModel.selectedAccount.observe(getViewLifecycleOwner(), selectedAccount -> {
            if (selectedAccount != null) {
                bindingCard(selectedAccount);
            }
        });

        // Quan sát defaultAccount riêng
        sessionViewModel.defaultAccount.observe(getViewLifecycleOwner(), defaultAccount -> {
            // Chỉ cập nhật khi chưa có selectedAccount
            if (sessionViewModel.selectedAccount.getValue() == null && defaultAccount != null) {
                bindingCard(defaultAccount);
            }
        });

    }

    private void bindingCard(Account account) {
        txtTxnHistoryAccName.setText(account.getAccountName());
        txtTxnHistoryAccNumber.setText(account.getAccountNumber());
        txtTxnHistoryAccBalance.setText(account.getBalance() + " " + account.getCurrency());
    }
}